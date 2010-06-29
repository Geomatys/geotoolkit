package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
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
    private List<SimpleType> lodSimpleExtentions;
    private List<AbstractObject> lodObjectExtensions;

    public DefaultLod() {
        this.minLodPixels = DEF_MIN_LOD_PIXELS;
        this.maxLodPixels = DEF_MAX_LOD_PIXELS;
        this.minFadeExtent = DEF_MIN_FADE_EXTENT;
        this.maxFadeExtent = DEF_MAX_FADE_EXTENT;
        this.lodSimpleExtentions = EMPTY_LIST;
        this.lodObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param minLodPixels
     * @param maxLodPixels
     * @param minFadeExtent
     * @param maxFadeExtent
     * @param lodSimpleExtentions
     * @param lodObjectExtensions
     */
    public DefaultLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.minLodPixels = minLodPixels;
        this.maxLodPixels = maxLodPixels;
        this.minFadeExtent = minFadeExtent;
        this.maxFadeExtent = maxFadeExtent;
        this.lodSimpleExtentions = (lodSimpleExtentions == null) ? EMPTY_LIST : lodSimpleExtentions;
        this.lodObjectExtensions = (lodObjectExtensions == null) ? EMPTY_LIST : lodObjectExtensions;
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
    public List<SimpleType> getLodSimpleExtensions() {
        return this.lodSimpleExtentions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLodObjectExtensions() {
        return this.lodObjectExtensions;
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLodSimpleExtensions(List<SimpleType> lodSimpleExtensions) {
        this.lodSimpleExtentions = lodSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLodObjectExtensions(List<AbstractObject> lodObjectExtensions) {
        this.lodObjectExtensions = lodObjectExtensions;
    }
}
