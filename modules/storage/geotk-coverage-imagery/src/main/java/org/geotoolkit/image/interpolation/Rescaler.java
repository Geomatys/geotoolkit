package org.geotoolkit.image.interpolation;

/**
 * An interpolation which stretch histogram into given interval.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 31/05/13
 */
public class Rescaler extends PipeLineInterpolation {

    protected final double min;
    protected final double max;

    protected final double[] translation;
    protected final double[] scale;

    public Rescaler(final Interpolation source, final double minValue, final double maxValue) {
        this(source, minValue, maxValue, null);
    }
    public Rescaler(final Interpolation source, final double minValue, final double maxValue, double[] minMax) {
        super(source);
        min = Math.min(minValue, maxValue);
        max = Math.max(minValue, maxValue);
        final double destSpan = max-min;

        if (minMax == null || minMax.length < numBands*6) {
            this.getMinMaxValue(null);
        } else {
            this.minMax = minMax;
        }

        translation = new double[numBands];
        scale = new double[numBands];
        for(int i = 0, j = 0 ; i < numBands ; i++, j+=6) {
            scale[i] = destSpan/(this.minMax[j+3] - this.minMax[j]);
            translation[i] = min-this.minMax[j];
        }
    }

    @Override
    public double interpolate(double x, double y, int band) {
        double tmp = super.interpolate(x, y, band);
        return (tmp + translation[band]) * scale[band];
    }
    
}
