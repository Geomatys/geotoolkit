package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLocation extends DefaultAbstractObject implements Location {

    private double longitude;
    private double latitude;
    private double altitude;
    private List<SimpleType> locationSimpleExtensions;
    private List<AbstractObject> locationObjectExtensions;

    public DefaultLocation(){
        this.longitude = DEF_LONGITUDE;
        this.latitude = DEF_LATITUDE;
        this.altitude = DEF_ALTITUDE;
        this.locationSimpleExtensions = EMPTY_LIST;
        this.locationObjectExtensions = EMPTY_LIST;
    }

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
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions) {
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
    public double getLongitude() {
        return this.longitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getLatitude() {
        return this.latitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {
        return this.altitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLocationSimpleExtensions() {
        return this.locationSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLocationObjectExtensions() {
        return this.locationObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLocationSimpleExtensions(List<SimpleType> locationSimpleExtensions) {
        this.locationSimpleExtensions = locationSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLocationObjectExtensions(List<AbstractObject> locationObjectExtensions) {
        this.locationObjectExtensions = locationObjectExtensions;
    }
}
