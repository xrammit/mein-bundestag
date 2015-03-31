package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tim Marx
 * @version 1.0
 */
class CalculationUiAdapterTesting<E> extends CalculationUiAdapter<E> {
    private final boolean chooseDefinedOptions;
    private final Set<E> optionsToChoose;
    private boolean attributeRandom = false;
    private boolean attributeAlwaysRandom = false;

    CalculationUiAdapterTesting(Set<E> optionsToChoose) {
        chooseDefinedOptions = true;
        this.optionsToChoose = optionsToChoose;
    }

    CalculationUiAdapterTesting() {
        chooseDefinedOptions = false;
        optionsToChoose = Collections.<E>emptySet();
    }

    @Override
    Result decideDraw(Set<E> options, int numOptionsToChoose, String message) {
        if (chooseDefinedOptions) {
            if (numOptionsToChoose != optionsToChoose.size()) {
                throw new IllegalArgumentException("You must pass exactly as much options as are requested by decideDraws!");
            }
            Set<E> optionsChosen = new HashSet<E>();
            for (E option : options) {
                if (optionsToChoose.contains(option)) {
                    optionsChosen.add(option);
                }
            }
            if (numOptionsToChoose != optionsChosen.size()) {
                throw new IllegalArgumentException("You've passed options that are not selectable!");
            }
            return new Result(optionsChosen, attributeAlwaysRandom, attributeRandom);
        } else {
            // take the numOptionsToChoose-first entities
            return new Result(options.stream().limit(numOptionsToChoose).collect(Collectors.toSet()),
                    attributeAlwaysRandom, attributeRandom);
        }
    }

    void setAttributeRandom(boolean attributeRandom) {
        this.attributeRandom = attributeRandom;
    }

    void setAttributeAlwaysRandom(boolean attributeAlwaysRandom) {
        this.attributeAlwaysRandom = attributeAlwaysRandom;
    }
}
