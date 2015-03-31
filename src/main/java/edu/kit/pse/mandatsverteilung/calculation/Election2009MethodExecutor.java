package edu.kit.pse.mandatsverteilung.calculation;

/**
 * Models the calculation executor for the calculation method of the election in 2009.
 */
class Election2009MethodExecutor extends MethodExecutor {

    /**
     * The constructor of the MethodExecutor used for the election in 2009.
     * It declares the method steps used by this calculation method.
     *
     * @param parameter The parameter for calculation.
     */
    Election2009MethodExecutor(MethodParameter parameter) {
        super(parameter);
        super.addStep(new FindWardWinnersParallelStep(parameter, super.getResult(), super.executorService))
             .addStep(new FilterPartiesThresholdStep(parameter, super.getResult()));
        
        // if leveling seats should be used calculate the minimal seat amount for any party, add a loop step to 
        // perform division in the parties to states respecting the minimal amount of seats 
        // else add the two single steps
        if (parameter.isUseLevelingSeats()) {
            super.addStep(new CalculateMinimalSeatsPerPartyStep(parameter, super.getResult(), super.executorService))
                 .addStep(new DivideSeatsToPartiesLoopStep(parameter, super.getResult()))
                 .addStep(new DivideSeatsPerPartyToStatesParallelStep(parameter, super.getResult(),
                         super.executorService))
                 .addStep(new FindLevelingSeatsStep(parameter, super.getResult()))
                 .addStep(new CheckHalfVoteCountStep(parameter, super.getResult()));
        } else {
            super.addStep(new DivideSeatsToPartiesStep(parameter, super.getResult()))
                 .addStep(new DivideSeatsPerPartyToStatesParallelStep(parameter, super.getResult(),
                         super.executorService))
                 .addStep(new CheckHalfVoteCountStep(parameter, super.getResult()))
                 .addStep(new FindOverhangSeatsStep(parameter, super.getResult()));
        }
    }
}
