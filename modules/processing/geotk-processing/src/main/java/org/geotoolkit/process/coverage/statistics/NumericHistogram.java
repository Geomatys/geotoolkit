package org.geotoolkit.process.coverage.statistics;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class NumericHistogram {

    private int nbBins;
    private long[] hist;
    private double min;
    private double max;
    private double binSize;

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

    public void addValue(double value) {
        addValue(value, 1);
    }

    public void addValue(double value, long occurs) {
        int bin = (int) ((value - min) / binSize);
        if (bin < 0) {
         /* this data is smaller than min */
        } else if (bin >= nbBins) {
            if (value <= max) {
                hist[nbBins-1] += occurs;
            }
        } else {
            hist[bin] += occurs;
        }
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
}
