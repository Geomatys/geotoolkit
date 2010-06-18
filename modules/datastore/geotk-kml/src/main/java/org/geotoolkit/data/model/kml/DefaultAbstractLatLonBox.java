package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractLatLonBox extends DefaultAbstractObject implements AbstractLatLonBox {

    protected final double north;
    protected final double south;
    protected final double east;
    protected final double west;
    protected final List<SimpleType> abstractLatLonBoxSimpleExtensions;
    protected final List<AbstractObject> abstractLatLonBoxObjectExtensions;

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

}
