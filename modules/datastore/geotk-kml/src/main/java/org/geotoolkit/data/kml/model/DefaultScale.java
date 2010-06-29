package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultScale extends DefaultAbstractObject implements Scale {

    private double x;
    private double y;
    private double z;
    private List<SimpleType> scaleSimpleExtensions;
    private List<AbstractObject> scaleObjectExtensions;

    /**
     * 
     */
    public DefaultScale() {
        this.x = DEF_X;
        this.y = DEF_Y;
        this.z = DEF_Z;
        this.scaleSimpleExtensions = EMPTY_LIST;
        this.scaleObjectExtensions = EMPTY_LIST;
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
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.x = x;
        this.y = y;
        this.z = z;
        this.scaleSimpleExtensions = (scaleSimpleExtensions == null) ? EMPTY_LIST : scaleSimpleExtensions;
        this.scaleObjectExtensions = (scaleObjectExtensions == null) ? EMPTY_LIST : scaleObjectExtensions;
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
    public List<SimpleType> getScaleSimpleExtensions() {
        return this.scaleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getScaleObjectExtensions() {
        return this.scaleObjectExtensions;
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScaleSimpleExtensions(List<SimpleType> scaleSimpleExtensions) {
        this.scaleSimpleExtensions = scaleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScaleObjectExtensions(List<AbstractObject> scaleObjectExtensions) {
        this.scaleObjectExtensions = scaleObjectExtensions;
    }
}
