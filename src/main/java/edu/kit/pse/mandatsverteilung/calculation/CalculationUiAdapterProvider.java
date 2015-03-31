package edu.kit.pse.mandatsverteilung.calculation;

/**
 * This class provides an adapter for the calculation classes to access the GUI.
 * During testing you might want to mock the method getAdapterInstance to return a special
 * CalculationUiAdapter instance.
 *
 * @author Tim Marx
 */
class CalculationUiAdapterProvider {

    /**
     * Provide the caller a new instance of the adapter.
     * Mock this method during testing to provide your own implementation without user interaction for example.
     * @param <T>
     * @return
     */
    protected <T> CalculationUiAdapter<T> getAdapterInstance() {
        return new CalculationUiAdapterJavaFX<T>();
    }
}
