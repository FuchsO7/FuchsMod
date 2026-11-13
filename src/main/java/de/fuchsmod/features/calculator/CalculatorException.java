package de.fuchsmod.features.calculator;

import com.mojang.brigadier.StringReader;

public class CalculatorException extends Exception {
    public final StringReader reader;

    public CalculatorException(String message) {
        this(message, null);
    }

    public CalculatorException(String message, StringReader reader) {
        super(message);
        this.reader = reader;
    }
}