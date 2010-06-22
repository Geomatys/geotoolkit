package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.model.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractLatLonBox extends DefaultAbstractObject implements AbstractLatLonBox {

    protected double north;
    protected double south;
    protected double east;
    protected double west;
    protected List<SimpleType> abstractLatLonBoxSimpleExtensions;
    protected List<AbstractObject> abstractLatLonBoxObjectExtensions;

    /**
     * 
     */
    protected DefaultAbstractLatLonBox(){
        this.north = DEF_NORTH;
        this.south = DEF_SOUTH;
        this.east = DEF_EAST;
        this.west = DEF_WEST;
        this.abstractLatLonBoxSimpleExtensions = EMPTY_LIST;
        this.abstractLatLonBoxObjectExtensions = EMPTY_LIST;
    }

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
     */
    protected DefaultAbstractLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.north = KmlUtilities.checkAngle180(north);
        this.south = KmlUtilities.checkAngle180(south);
        this.east = KmlUtilities.checkAngle180(east);
        this.west = KmlUtilities.checkAngle180(west);
        this.abstractLatLonBoxSimpleExtensions = (abstractLatLonBoxSimpleExtensions == null) ? EMPTY_LIST : abstractLatLonBoxSimpleExtensions;
        this.abstractLatLonBoxObjectExtensions = (abstractLatLonBoxObjectExtensions == null) ? EMPTY_LIST : abstractLatLonBoxObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getNorth() {return this.north;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getSouth() {return this.south;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getEast() {return this.east;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getWest() {return this.west;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractLatLonBoxSimpleExtensions() {return this.abstractLatLonBoxSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractLatLonBoxObjectExtensions() {return this.abstractLatLonBoxObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNorth(double north){this.north = north;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSouth(double south){this.south = south;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setEast(double east){this.east = east;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWest(double west){this.west = west;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractLatLonBoxSimpleExtensions(List<SimpleType> abstractLatLonBoxSimpleExtensions){
        this.abstractLatLonBoxSimpleExtensions = abstractLatLonBoxSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractLatLonBoxObjectExtensions(List<AbstractObject> abstractLatLonBoxObjectExtensions){
        this.abstractLatLonBoxObjectExtensions = abstractLatLonBoxObjectExtensions;
    }
}
