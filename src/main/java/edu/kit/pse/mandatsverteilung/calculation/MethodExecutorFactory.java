package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * This Factory creates MethodExecutor instances for implemented calculation methods with the given parameters.
 */
public class MethodExecutorFactory {

    public static final int DEFAULT_THRESHOLD = 500;
    
    public static final int DEFAULT_DIRECT_THRESHOLD = 3;
    
    public static final int DEFAULT_INITIAL_SEATS = 598;
    
    /**
     * This method is the public interface to create a new MethodExecutor instance for the election in 2009.
     * @return The newly created MethodExecutor instance to start the calculation with.
     */
    public static MethodExecutor createElection2009MethodExecutor(VoteDistrRepublic voteDistrRepublic,
            CandidateManager cManager) {
        return createElection2009MethodExecutor(voteDistrRepublic, cManager, new CalculationUiAdapterProvider());
    }

    protected static MethodExecutor createElection2009MethodExecutor(VoteDistrRepublic voteDistrRepublic,
            CandidateManager cManager, CalculationUiAdapterProvider calculationUiAdapterProvider) {
        return new Election2009MethodExecutor(new MethodParameter(MethodExecutorType.ELECTION_2009, voteDistrRepublic,
                DEFAULT_THRESHOLD, DEFAULT_DIRECT_THRESHOLD, false, DEFAULT_INITIAL_SEATS, cManager,
                DivisorMethod.SainteLagueSchepers(), DivisorMethod.SainteLagueSchepers(),
                calculationUiAdapterProvider));
    }

    /**
     * This method is the public interface to create a new MethodExecutor instance for the election in 2013.
     * @return The newly created MethodExecutor instance to start the calculation with.
     */
    public static MethodExecutor createElection2013MethodExecutor(VoteDistrRepublic voteDistrRepublic,
            CandidateManager cManager) {
        return createElection2013MethodExecutor(voteDistrRepublic, cManager, new CalculationUiAdapterProvider());
    }

    protected static MethodExecutor createElection2013MethodExecutor(VoteDistrRepublic voteDistrRepublic,
            CandidateManager cManager, CalculationUiAdapterProvider calculationUiAdapterProvider) {
        return new Election2013MethodExecutor(new MethodParameter(MethodExecutorType.ELECTION_2013, voteDistrRepublic,
                DEFAULT_THRESHOLD, DEFAULT_DIRECT_THRESHOLD, true, DEFAULT_INITIAL_SEATS, cManager,
                DivisorMethod.SainteLagueSchepers(), DivisorMethod.SainteLagueSchepers(),
                calculationUiAdapterProvider));
    }

    /**
     * This method is the public interface to create a new MethodExecutor instance for a calculation method based on 
     * the election in 2009 or 2013 with custom parameters.
     * @param threshold The threshold value to use during calculation.
     * @param useLevelingSeats Whether to calculate with leveling seats or not.
     * @return The newly created MethodExecutor instance to start the calculation with.
     */
    public static MethodExecutor createElectionCustomExecutor(VoteDistrRepublic voteDistrRepublic,
            MethodExecutorType methodExecutorType, int threshold, int directThreshold, boolean useLevelingSeats,
            int initialCountSeatsRepublic, CandidateManager cManager) {
        return createElectionCustomExecutor(voteDistrRepublic, methodExecutorType, threshold, directThreshold,
                useLevelingSeats, initialCountSeatsRepublic, cManager, new CalculationUiAdapterProvider());
    }

    protected static MethodExecutor createElectionCustomExecutor(VoteDistrRepublic voteDistrRepublic,
            MethodExecutorType methodExecutorType, int threshold, int directThreshold, boolean useLevelingSeats,
            int initialCountSeatsRepublic, CandidateManager cManager,
            CalculationUiAdapterProvider calculationUiAdapterProvider) {
        switch (methodExecutorType) {
            case ELECTION_2009:
                return new Election2009MethodExecutor(new MethodParameter(methodExecutorType, voteDistrRepublic,
                        threshold, directThreshold, useLevelingSeats, initialCountSeatsRepublic, cManager,
                        DivisorMethod.SainteLagueSchepers(), DivisorMethod.SainteLagueSchepers(),
                        calculationUiAdapterProvider));
            case ELECTION_2013:
                return new Election2013MethodExecutor(new MethodParameter(methodExecutorType, voteDistrRepublic,
                        threshold, directThreshold, useLevelingSeats, initialCountSeatsRepublic, cManager,
                        DivisorMethod.SainteLagueSchepers(), DivisorMethod.SainteLagueSchepers(),
                        calculationUiAdapterProvider));
            default:
                throw new IllegalArgumentException("No MethodExecutor defined for this methodExecutorType");
        }
    }
}