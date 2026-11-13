package de.fuchsmod.features.performance;

import net.minecraft.util.Util;

import java.util.ArrayList;

public abstract class AbstractMeasurement<T extends Number> {
    protected final ArrayList<Sample<T>> samples = new ArrayList<>();
    protected long MAX_SAMPLE_TIME_MILLIS = 10_000L;
    protected int MAXIMUM_SAMPLE_AMOUNT = 240;
    protected int AVERAGING_SAMPLE_AMOUNT = 5;

    protected void addSample(T value) {
        addSample(value, Util.getMillis());
    }

    protected void addSample(T value, long timestamp) {
        addSample(new Sample<T>(value, timestamp));
    }

    protected void addSample(Sample<T> sample) {
        samples.addFirst(sample);
        if (samples.size() >= MAXIMUM_SAMPLE_AMOUNT || samples.getLast().timestamp() + MAX_SAMPLE_TIME_MILLIS < Util.getMillis())
            samples.removeLast();
    }

    public ArrayList<Sample<T>> getSamples() {
        return samples;
    }

    protected void clearSamples() {
        samples.clear();
    }

    protected boolean hasEnoughSamplesForSignificantAverage() {
        return samples.size() >= AVERAGING_SAMPLE_AMOUNT;
    }

    public T getCurrent() {
        return samples.getFirst().value();
    }

    public double getAverage() {
        return getAverage(AVERAGING_SAMPLE_AMOUNT);
    }

    public double getAverage(int sampleAmount) {
        double sum = 0.0;
        int summands = 0;
        for (int i = 0; i < samples.size() && i < sampleAmount; i++) {
            sum += samples.get(i).value().doubleValue();
            summands++;
        }
        return sum / summands;
    }

    public double getMinimum() {
        double min = Double.POSITIVE_INFINITY;
        for (Sample<T> sample : samples) {
            min = Math.min(sample.value().doubleValue(), min);
        }
        return min;
    }

    public double getMaximum() {
        double max = Double.NEGATIVE_INFINITY;
        for (Sample<T> sample : samples) {
            max = Math.max(sample.value().doubleValue(), max);
        }
        return max;
    }

    public record Sample<T>(T value, double timestamp) {

    }
}
