package de.fuchsmod.features.calculator;

public enum MathBifunction implements MathOperation {
    LOG;

    @Override
    public int getPriority() {
        return 5;
    }
}
