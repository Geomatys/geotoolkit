package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLod extends DefaultAbstractObject implements Lod {

    private double minLodPixels;
    private double maxLodPixels;
    private double minFadeExtent;
    private double maxFadeExtent;

    public DefaultLod() {
        this.minLodPixels = DEF_MIN_LOD_PIXELS;
        this.maxLodPixels = DEF_MAX_LOD_PIXELS;
        this.minFadeExtent = DEF_MIN_FADE_EXTENT;
        this.maxFadeExtent = DEF_MAX_FADE_EXTENT;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param minLodPixels
     * @param maxLodPixels
     * @param minFadeExtent
     * @param maxFadeExtent
     * @param lodSimpleExtensions
     * @param lodObjectExtensions
     */
    public DefaultLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels,
            double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtensions,
            List<AbstractObject> lodObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.minLodPixels = minLodPixels;
        this.maxLodPixels = maxLodPixels;
        this.minFadeExtent = minFadeExtent;
        this.maxFadeExtent = maxFadeExtent;
        if (lodSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LOD).addAll(lodSimpleExtensions);
        }
        if (lodObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LOD).addAll(lodObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinLodPixels() {
        return this.minLodPixels;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxLodPixels() {
        return this.maxLodPixels;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinFadeExtent() {
        return this.minFadeExtent;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxFadeExtent() {
        return this.maxFadeExtent;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMinLodPixels(double minLodPixels) {
        this.minLodPixels = minLodPixels;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxLodPixels(double maxLodPixels) {
        this.maxLodPixels = maxLodPixels;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMinFadeExtent(double minFadeExtent) {
        this.minFadeExtent = minFadeExtent;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxFadeExtent(double maxFadeExtent) {
        this.maxFadeExtent = maxFadeExtent;
    }
}
