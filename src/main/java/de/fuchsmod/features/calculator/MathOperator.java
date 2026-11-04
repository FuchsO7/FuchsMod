package de.fuchsmod.features.calculator;

public enum MathOperator implements MathOperation{
    ADD(1),
    SUB(1),
    NEG(4),
    MUL(2),
    DIV(2),
    MOD(2),
    POW(3);

    private final int priority;

    MathOperator(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }
}
