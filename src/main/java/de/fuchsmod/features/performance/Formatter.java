package de.fuchsmod.features.performance;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;

import java.util.function.BiFunction;

public class Formatter {
    private final int precision;
    private final double[] discreteThresholds;
    private final BiFunction<Double, Double, Boolean> discreteComparator;
    private final double continuousMin;
    private final double continuousMid;
    private final double continuousMax;

    public Formatter(int precision, double[] discreteThresholds, BiFunction<Double, Double, Boolean> discreteComparator, double continuousMin, double continuousMid, double continuousMax) {
        this.precision = precision;
        this.discreteThresholds = discreteThresholds;
        this.discreteComparator = discreteComparator;
        this.continuousMin = continuousMin;
        this.continuousMid = continuousMid;
        this.continuousMax = continuousMax;
    }

    private TextColor getDiscreteColor(double value) {
        final TextColor[] textColors = {
                TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN),
                TextColor.fromLegacyFormat(ChatFormatting.GREEN),
                TextColor.fromLegacyFormat(ChatFormatting.YELLOW),
                TextColor.fromLegacyFormat(ChatFormatting.RED),
                TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)
        };
        for (int i = 0; i < textColors.length; i++) {
            if (discreteComparator.apply(value, discreteThresholds[i]))
                return textColors[i];
        }
        return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
    }

    private TextColor getContinuousColor(double value) {
        int color;
        if (this.discreteComparator.apply(value, this.continuousMid)) {
            float alpha = (float) Math.clamp((value - this.continuousMid) / (this.continuousMax - this.continuousMid), 0.0, 1.0);
            color = ARGB.linearLerp(alpha, TextColor.fromLegacyFormat(ChatFormatting.YELLOW).getValue(), TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN).getValue());
        } else {
            float alpha = (float) Math.clamp((value - this.continuousMin) / (this.continuousMid - this.continuousMin), 0.0, 1.0);
            color = ARGB.linearLerp(alpha, TextColor.fromLegacyFormat(ChatFormatting.DARK_RED).getValue(), TextColor.fromLegacyFormat(ChatFormatting.YELLOW).getValue());
        }
        return TextColor.fromRgb(color);
    }

    public TextColor getColor(double value, boolean useContinuousColor) {
        return useContinuousColor ? getContinuousColor(value) : getDiscreteColor(value);
    }

    public Component formatValue(double value, boolean useContinuousColor) {
        return Component.literal(String.format("%." + this.precision + "f", value)).withColor(getColor(value, useContinuousColor).getValue());
    }
}
