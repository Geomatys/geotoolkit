package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLocation extends DefaultAbstractObject implements Location {

    private final double longitude;
    private final double latitude;
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
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.longitude = KmlUtilities.checkAngle180(longitude);
        this.latitude = KmlUtilities.checkAngle90(latitude);
        this.altitude = altitude;
        this.locationSimpleExtensions = (locationSimpleExtensions == null) ? EMPTY_LIST : locationSimpleExtensions;
        this.locationObjectExtensions = (locationObjectExtensions == null) ? EMPTY_LIST : locationObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getLongitude() {return this.longitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getLatitude() {return this.latitude;}

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
