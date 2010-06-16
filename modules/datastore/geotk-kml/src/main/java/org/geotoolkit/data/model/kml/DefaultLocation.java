package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLocation extends DefaultAbstractObject implements Location {

    private final Angle180 longitude;
    private final Angle90 latitude;
    private final double altitude;
    private final List<SimpleType> locationSimpleExtensions;
    private final List<AbstractObject> locationObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param longitude
     * @param latitude
     * @param altitude
     * @param locationSimpleExtensions
     * @param locationObjectExtensions
     */
    public DefaultLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle180 longitude, Angle90 latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.locationSimpleExtensions = (locationSimpleExtensions == null) ? EMPTY_LIST : locationSimpleExtensions;
        this.locationObjectExtensions = (locationObjectExtensions == null) ? EMPTY_LIST : locationObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle180 getLongitude() {return this.longitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle90 getLatitude() {return this.latitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {return this.altitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLocationSimpleExtensions() {return this.locationSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLocationObjectExtensions() {return this.locationObjectExtensions;}

}
