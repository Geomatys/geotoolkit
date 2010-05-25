package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractLatLonBoxDefault extends AbstractObjectDefault implements AbstractLatLonBox {

    protected Angle180 north;
    protected Angle180 south;
    protected Angle180 east;
    protected Angle180 west;
    protected List<SimpleType> abstractLatLonBoxSimpleExtensions;
    protected List<AbstractObject> abstractLatLonBoxObjectExtensions;

    protected AbstractLatLonBoxDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.abstractLatLonBoxSimpleExtensions = abstractLatLonBoxSimpleExtensions;
        this.abstractLatLonBoxObjectExtensions = abstractLatLonBoxObjectExtensions;
    }

    @Override
    public Angle180 getNorth() {return this.north;}

    @Override
    public Angle180 getSouth() {return this.south;}

    @Override
    public Angle180 getEast() {return this.east;}

    @Override
    public Angle180 getWest() {return this.west;}

    @Override
    public List<SimpleType> getAbstractLatLonBoxSimpleExtensions() {return this.abstractLatLonBoxSimpleExtensions;}

    @Override
    public List<AbstractObject> getAbstractLatLonBoxObjectExtensions() {return this.abstractLatLonBoxObjectExtensions;}

}
