package de.fuchsmod.features.calculator;

public enum MathSupplier implements MathOperation {
    E,
    PI,
    RANDOM;

    @Override
    public int getPriority() {
        return 5;
    }
}
