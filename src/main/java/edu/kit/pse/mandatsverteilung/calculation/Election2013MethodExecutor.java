package edu.kit.pse.mandatsverteilung.calculation;

/**
 * Models the calculation executor for the calculation method of the election in 2013.
 */
class Election2013MethodExecutor extends MethodExecutor {

    /**
     * The constructor of the MethodExecutor used for the election in 2013.
     * It declares the method steps used by this calculation method.
     *
     * @param parameter The parameter for calculation.
     */
    Election2013MethodExecutor(MethodParameter parameter) {
        super(parameter);
        super.addStep(new FindWardWinnersParallelStep(parameter, super.getResult(), super.executorService))
             .addStep(new FilterPartiesThresholdStep(parameter, super.getResult()))
             .addStep(new DivideSeatsToStatesStep(parameter, super.getResult()))
             .addStep(new DivideSeatsPerStateToPartiesParallelStep(parameter, super.getResult(),
                     super.executorService));
        // if leveling seats should be used add @DivideSeatsToPartiesLoopStep
        // else perform the appropriate single steps
        if (parameter.isUseLevelingSeats()) {
            super.addStep(new DivideSeatsToPartiesLoopStep(parameter, super.getResult()))
                 .addStep(new DivideSeatsPerPartyToStatesParallelStep(parameter, super.getResult(),
                         super.executorService))
                 .addStep(new FindLevelingSeatsStep(parameter, super.getResult()))
                 .addStep(new CheckHalfVoteCountStep(parameter, super.getResult()));
        } else {
            super.addStep(new DivideSeatsToPartiesStep(parameter, super.getResult()))
                 .addStep(new DivideSeatsPerPartyToStatesParallelStep(parameter, super.getResult(),
                         super.executorService))
                 .addStep(new FindOverhangSeatsStep(parameter, super.getResult()))
                 .addStep(new CheckHalfVoteCountStep(parameter, super.getResult()));
        }
    }
}
