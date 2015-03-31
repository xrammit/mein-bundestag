package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrState;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;
import org.apache.log4j.Logger;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Determines the ward winners in parallel for all wards.
 */
class FindWardWinnersParallelStep extends ParallelMethodStep<WardWinnersResult> {
    private final Logger logger = Logger.getLogger(FindWardWinnersParallelStep.class);

    FindWardWinnersParallelStep(MethodParameter methodParameter, MethodResult methodResult,
                                       ExecutorService executorService) {
        super(methodParameter, methodResult, executorService);
    }

    @Override
    void initializeSubsteps() {
        logger.info("-- initializing substeps --");
        for (State state : methodParameter.getVoteDistrRepublic().getStates()) {
            this.callables.add(new WardWinnersSubStep(state, methodParameter));
        }
    }

    @Override
    void mergeSubResults() throws MethodExecutionException {
        logger.info("-- started merging subresults --");
        
        WardWinnersResult finalResult = new WardWinnersResult();
        try { 
            for (Future<WardWinnersResult> future : futures) {
                // wait for join and merge the subresults into one @WardWinnersResult
                finalResult.merge(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("caught exception while waiting for callables to join", e);
            throw new MethodExecutionException("caught exception while waiting for callables to join", this, e);
        }
        
        // decide the draw candidates and thereby add the processed wards to @super.result.DirectLotterySeats
        Map<Ward, Party> finalWinners = decideDraws(finalResult);
        
        // set DirectMandats
        methodResult.setDirectMandats(finalWinners);
        
        // compute the amount of directmandates for any party by iterating over the states in VoteDistrRepublic
        for (State state : methodParameter.getVoteDistrRepublic().getStates()) {
            // temporarily store the parties mandates in this map
            HashMap <Party, Integer> tmpAmount = new HashMap<Party,Integer>();
            
            // iterate over the wards in this state and set the amount appropriately
            for (Ward ward : methodParameter.getVoteDistrRepublic().get(state).getKeySet()) {
                tmpAmount.merge(finalWinners.get(ward), 1, (oldVal,defVal) -> oldVal + 1);
            }
            
            // add the values in tmpAmount to super.result.DirectMandatAmount
            for (Map.Entry<Party, Integer> entry : tmpAmount.entrySet()) {
                // updates DirectMandatAmount in the following way:
                // if there is no entry associated with the party in entry.getKey(), add a new Map consisting 
                // simply of the mapping state -> entry.getValue().
                // if there already is a mapping (i.e. an entry), update this one as follows:
                // as state iterates over a set there must not be a value associated to state in the mapping,
                // hence the mapping can be updated using put().
                if (methodResult.getDirectMandatAmount().containsKey(entry.getKey())) {
                    methodResult.getDirectMandatAmount().get(entry.getKey()).put(state, entry.getValue());
                } else {
                    //new map to store the mapping state -> entry.getValue()
                    HashMap<State, Integer> tmpMap = new HashMap<State,Integer>();
                    tmpMap.put(state, entry.getValue());
                    methodResult.getDirectMandatAmount().put(entry.getKey(), tmpMap);
                }
            }
        }
        logger.info("-- finished merging subresults --");
    }
    
    /**
     * Find a decision for any element in @result.drawWards, add the winner to @super.result.DirectLotterySeats
     * and update @result.wardWinners
     * @param result the @WardWinnerResult to perform the decision procedure on
     * @return the updated map @wardWinners
     * @throws MethodExecutionException
     */
    private Map<Ward, Party> decideDraws(WardWinnersResult result) throws MethodExecutionException {
        if (result.getDrawWards().isEmpty()) {
            logger.info("-- no draws available --");
            return result.getWardWinners();
        }
        logger.info("-- draw process started --");
        for (Entry<Ward, Set<Party>> entry : result.getDrawWards().entrySet()) {
            Set<Party> winner = new HashSet<>();
            boolean random = methodResult.isDecideDrawRandom();
            // check whether the user has previously selected to pick random options for all future draws
            // if not pass the draw options to the user
            if (!random) {
                // use CalculationUiAdapter to pass the draw entities to the user interface
                String message = "Bitte wählen Sie die Partei aus, deren Kandidat in " + entry.getKey()
                        + " das Direktmandat erhalten soll.";
                CalculationUiAdapter<Party>.Result drawResult
                        = methodParameter.getCalculationUiAdapterProvider().<Party>getAdapterInstance()
                        .decideDraw(entry.getValue(), 1 , message);
                // check if the user decided to automatically pick random options for this draw 
                // or even for all future draws as well
                random = drawResult.isRandom() || drawResult.isRandomAll();
                if (!random) {
                    // user has selected options manually
                    winner = drawResult.getChosenOptions();
                }
                if (drawResult.isRandomAll()) {
                    // if user has selected to decide all future draws random set flag in result object
                    methodResult.setDecideDrawRandom(true);
                    logger.info("user selected to decide this and future draws randomly");
                }
            }
            if (random) {
                // decide draw random
                List<Party> optionsList = entry.getValue().stream().collect(Collectors.toList());
                Collections.shuffle(optionsList);
                winner = optionsList.stream().limit(1).collect(Collectors.toSet());
                logger.info("draw was decided randomly");
            }
            // should never happen, as the above call should return a set of size 1
            if (winner.size() != 1) {
                logger.error("The draw process returned an unexpected result, as there are "
                        + winner.size() + " winners in ward " + entry.getKey().getName());
                throw new MethodExecutionException("The draw process returned an unexpected result, as there are "
                        + winner.size() + " winners in ward " + entry.getKey().getName(), this);
            } else {
                // add the single winner to @wardWinners and mark this ward as lottery seat
                for (Party party : winner) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(party.getName() + " won the draw in ward " + entry.getKey().getName());
                    }
                    result.getWardWinners().put(entry.getKey(), party);
                    methodResult.getDirectLotterySeats().add(entry.getKey());
                }
            }
        }
        logger.info("-- draw process finished --");
        return result.getWardWinners();
    }
}

/**
 * Callable template for each sub step.
 */
class WardWinnersSubStep implements Callable<WardWinnersResult> {
    private final Logger logger = Logger.getLogger(WardWinnersSubStep.class);
    private final State state;
    private final MethodParameter param;

    WardWinnersSubStep(State state, MethodParameter param) {
        this.state = state;
        this.param = param;
    }

    @Override
    public WardWinnersResult call() throws MethodExecutionException {
        logger.debug("-- started calculation in state " + state.getName() + " --");
        VoteDistrState voteDistrState = param.getVoteDistrRepublic().get(this.state);
        WardWinnersResult result = new WardWinnersResult();

        //iterate over wards and find the party with the maximal amount of first votes
        for (Ward ward : voteDistrState.getKeySet()) {
            //priority queue to store the parties together with their amount of first votes,
            //sorted descending by votes. Note that the given comparator is NOT consistent with equals.
            PriorityQueue<SimpleEntry<Party, Integer>> votesByParty
                = new PriorityQueue<SimpleEntry<Party, Integer>>((e1,e2) -> - e1.getValue().compareTo(e2.getValue()));

            for (Party party : voteDistrState.get(ward).getKeySet()) {
                votesByParty.add(new SimpleEntry<Party, Integer>(party, voteDistrState.get(ward).getFirst(party)));
            }

            //retrieves head of votesByParty
            SimpleEntry<Party, Integer> current = votesByParty.poll();
            if (current == null) {
                logger.error("There is no valid first vote for the ward " + ward.getName());
                throw new MethodExecutionException("There is no valid first vote for the ward " + ward.getName() + ".");
            }
            Set<Party> currentDraws = new HashSet<Party>();
            currentDraws.add(current.getKey());

            // add all parties having equal amount of first votes as current to currentDraws
            logger.debug("-- current winner in ward " + ward.getName() + " is " + current.getKey().getName() + " --");
            while (!votesByParty.isEmpty() && votesByParty.peek().getValue() == current.getValue()) {
                logger.debug(votesByParty.peek().getKey().getName() + " has the same amount of first votes in ward " 
                        + ward.getName() + " --");
                currentDraws.add(votesByParty.poll().getKey());
            }

            // if there is a unique party winning this vote (hence its current...), add it to result.wardWinners
            // else add currentDraws to result.drawWards
            if (currentDraws.size() == 1) {
                result.getWardWinners().put(ward, current.getKey());
            } else {
                result.getDrawWards().put(ward, currentDraws);
            }
        }
        logger.debug("-- finished calculation in state " + state.getName() + " --");
        return result;
    }
}

/**
 * Result object of each sub step callable.
 */
class WardWinnersResult {
    private final Map<Ward, Party> wardWinners;
    private final Map<Ward, Set<Party>> drawWards;

    WardWinnersResult() {
        this.wardWinners = new HashMap<>();
        this.drawWards = new HashMap<>();
    }
    
    /**
     * Merges the given @WardWinnersResult into this one.
     * Note that the key sets must be different otherwise the merge overwrites entries in this @WardWinnersResult
     * @param result the given @WardWinnersResult to merge
     */
    void merge(WardWinnersResult result) {
        this.wardWinners.putAll(result.wardWinners);
        this.drawWards.putAll(result.drawWards);
    }

    Map<Ward, Party> getWardWinners() {
        return wardWinners;
    }

    Map<Ward, Set<Party>> getDrawWards() {
        return drawWards;
    }
}

