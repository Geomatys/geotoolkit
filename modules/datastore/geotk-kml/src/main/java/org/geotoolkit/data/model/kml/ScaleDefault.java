package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class ScaleDefault extends AbstractObjectDefault implements Scale {

    private double x;
    private double y;
    private double z;
    private List<SimpleType> scaleSimpleExtensions;
    private List<AbstractObject> scaleObjectExtensions;

    public ScaleDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {return this.x;}

    @Override
    public double getY() {return this.y;}

    @Override
    public double getZ() {return this.z;}

    @Override
    public List<SimpleType> getScaleSimpleExtensions() {return this.scaleSimpleExtensions;}

    @Override
    public List<AbstractObject> getScaleObjectExtensions() {return this.scaleObjectExtensions;}

}
