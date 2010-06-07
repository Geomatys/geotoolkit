package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LatLonBoxDefault extends AbstractLatLonBoxDefault implements LatLonBox{

    private final Angle180 rotation;
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
    public LatLonBoxDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            Angle180 rotation,
            List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes, north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions);
        this.rotation = rotation;
        this.latLonBoxSimpleExtensions = (latLonBoxSimpleExtensions == null) ? EMPTY_LIST : latLonBoxSimpleExtensions;
        this.latLonBoxObjectExtensions = (latLonBoxObjectExtensions == null) ? EMPTY_LIST : latLonBoxObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle180 getRotation() {return this.rotation;}

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
