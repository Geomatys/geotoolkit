package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLatLonBox extends DefaultAbstractLatLonBox implements LatLonBox{

    private final double rotation;
    private final List<SimpleType> latLonBoxSimpleExtensions;
    private final List<AbstractObject> latLonBoxObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param north
     * @param south
     * @param east
     * @param west
     * @param abstractLatLonBoxSimpleExtensions
     * @param abstractLatLonBoxObjectExtensions
     * @param rotation
     * @param latLonBoxSimpleExtensions
     * @param latLonBoxObjectExtensions
     */
    public DefaultLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation,
            List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes, north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions);
        this.rotation = KmlUtilities.checkAngle180(rotation);
        this.latLonBoxSimpleExtensions = (latLonBoxSimpleExtensions == null) ? EMPTY_LIST : latLonBoxSimpleExtensions;
        this.latLonBoxObjectExtensions = (latLonBoxObjectExtensions == null) ? EMPTY_LIST : latLonBoxObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRotation() {return this.rotation;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLatLonBoxSimpleExtensions() {return this.latLonBoxSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLatLonBoxObjectExtensions() {return this.latLonBoxObjectExtensions;}
}
