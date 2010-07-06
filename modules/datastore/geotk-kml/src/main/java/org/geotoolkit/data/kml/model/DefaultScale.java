package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultScale extends DefaultAbstractObject implements Scale {

    private double x;
    private double y;
    private double z;

    /**
     * 
     */
    public DefaultScale() {
        this.x = DEF_X;
        this.y = DEF_Y;
        this.z = DEF_Z;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param x
     * @param y
     * @param z
     * @param scaleSimpleExtensions
     * @param scaleObjectExtensions
     */
    public DefaultScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions,
            List<AbstractObject> scaleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.x = x;
        this.y = y;
        this.z = z;
        if(scaleSimpleExtensions != null)
        this.extensions().simples(Extensions.Names.SCALE)
                .addAll(scaleSimpleExtensions);
        if(scaleObjectExtensions != null)
        this.extensions().complexes(Extensions.Names.SCALE)
                .addAll(scaleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getZ() {
        return this.z;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public Extensions extensions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
