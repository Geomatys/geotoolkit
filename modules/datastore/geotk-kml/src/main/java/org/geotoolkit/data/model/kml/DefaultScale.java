package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultScale extends DefaultAbstractObject implements Scale {

    private final double x;
    private final double y;
    private final double z;
    private final List<SimpleType> scaleSimpleExtensions;
    private final List<AbstractObject> scaleObjectExtensions;

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
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions){
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
    public double getX() {return this.x;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getY() {return this.y;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getZ() {return this.z;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getScaleSimpleExtensions() {return this.scaleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getScaleObjectExtensions() {return this.scaleObjectExtensions;}

}
