package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LatLonBoxDefault extends AbstractLatLonBoxDefault implements LatLonBox{

    private Angle180 rotation;
    private List<SimpleType> latLonBoxSimpleExtensions;
    private List<AbstractObject> latLonBoxObjectExtensions;

    public LatLonBoxDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            Angle180 rotation,
            List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes, north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions);
        this.rotation = rotation;
        this.latLonBoxSimpleExtensions = latLonBoxSimpleExtensions;
        this.latLonBoxObjectExtensions = latLonBoxObjectExtensions;
    }

    @Override
    public Angle180 getRotation() {return this.rotation;}

    @Override
    public List<SimpleType> getLatLonBoxSimpleExtensions() {return this.latLonBoxSimpleExtensions;}

    @Override
    public List<AbstractObject> getLatLonBoxObjectExtensions() {return this.latLonBoxObjectExtensions;}
}
