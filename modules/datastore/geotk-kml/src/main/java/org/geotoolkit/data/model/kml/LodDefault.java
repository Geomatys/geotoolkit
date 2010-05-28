package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LodDefault extends AbstractObjectDefault implements Lod {

    private double minLodPixels;
    private double maxLodPixels;
    private double minFadeExtent;
    private double maxFadeExtent;
    private List<SimpleType> lodSimpleExtentions;
    private List<AbstractObject> lodObjectExtensions;

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
    public LodDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.minLodPixels = minLodPixels;
        this.maxLodPixels = maxLodPixels;
        this.minFadeExtent = minFadeExtent;
        this.maxFadeExtent = maxFadeExtent;
        this.lodSimpleExtentions = lodSimpleExtentions;
        this.lodObjectExtensions = lodObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinLodPixels() {return this.minLodPixels;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxLodPixels() {return this.maxLodPixels;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinFadeExtent() {return this.minFadeExtent;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxFadeExtent() {return this.maxFadeExtent;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLodSimpleExtensions() {return this.lodSimpleExtentions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLodObjectExtensions() {return this.lodObjectExtensions;}
}
