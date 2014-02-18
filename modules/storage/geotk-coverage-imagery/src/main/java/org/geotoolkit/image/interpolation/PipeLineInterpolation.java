package org.geotoolkit.image.interpolation;

/**
 * A class which aim is to override a given interpolation. the aim is to apply multiple, successive treatments on images with a single shot.
 *
 * TODO : Class created for specific needs, in urgence. A better API should be created for image pipeline operations.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 30/05/13
 */
public class PipeLineInterpolation extends Interpolation {

    protected final Interpolation source;

    public PipeLineInterpolation(Interpolation source) {
        super(source);
        this.source = source;
    }

    @Override
    public double interpolate(double x, double y, int band) {
        return source.interpolate(x, y, band);
    }

}
