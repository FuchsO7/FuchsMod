package de.fuchsmod.features.calculator;

public enum MathFunction implements MathOperation {
    SQRT,
    ROUND,
    FACT,
    ABS,
    SIGN,
    LN,
    LG,
    SIN,
    COS,
    TAN,
    ASIN,
    ACOS,
    ATAN;

    @Override
    public int getPriority() {
        return 5;
    }
}