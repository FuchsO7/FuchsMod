package de.fuchsmod.features.calculator;

public enum Brackets implements MathOperation {
    OPENING,
    CLOSING;

    @Override
    public int getPriority() {
        return 0;
    }
}