package org.geotoolkit.processing.coverage.statistics;

import static org.geotoolkit.processing.coverage.statistics.NumericHistogram.AddResult.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class NumericHistogram {

    private final int nbBins;
    private final long[] hist;
    private final double min;
    private final double max;
    private final double binSize;

    public NumericHistogram(double min, double max) {
        this(1000, min, max);
    }

    public NumericHistogram(int nbBins, double min, double max) {
        this.nbBins = nbBins;
        this.min = min;
        this.max = max;
        this.hist = new long[nbBins];
        this.binSize = (max - min) / (double)nbBins;
    }

    public AddResult addValue(double value) {
        return addValue(value, 1);
    }

    public AddResult addValue(double value, long occurs) {
        if (Double.isNaN(value)) return NAN;
        else if (Double.isInfinite(value)) return INFINITY;
        int bin = (int) ((value - min) / binSize);
        if (bin < 0) {
         /* this data is smaller than min */
            return TOO_LOW;
        } else if (bin >= nbBins) {
            if (value <= max) {
                hist[nbBins-1] += occurs;
            } else return TOO_HIGH;
        } else {
            hist[bin] += occurs;
        }

        return SUCCESS;
    }

    public long[] getHist() {
        return hist;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getNbBins() {
        return nbBins;
    }

    public enum AddResult {
        TOO_LOW,
        TOO_HIGH,
        INFINITY,
        NAN,
        SUCCESS
    }
}
