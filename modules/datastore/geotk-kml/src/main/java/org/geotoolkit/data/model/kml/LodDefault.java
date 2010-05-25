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

    @Override
    public double getMinLodPixels() {return this.minLodPixels;}

    @Override
    public double getMaxLodPixels() {return this.maxLodPixels;}

    @Override
    public double getMinFadeExtent() {return this.minFadeExtent;}

    @Override
    public double getMaxFadeExtent() {return this.maxFadeExtent;}

    @Override
    public List<SimpleType> getLodSimpleExtensions() {return this.lodSimpleExtentions;}

    @Override
    public List<AbstractObject> getLodObjectExtensions() {return this.lodObjectExtensions;}

}
