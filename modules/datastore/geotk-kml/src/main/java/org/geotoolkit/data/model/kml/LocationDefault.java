package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LocationDefault extends AbstractObjectDefault implements Location {

    private Angle180 longitude;
    private Angle90 latitude;
    private double altitude;
    private List<SimpleType> locationSimpleExtensions;
    private List<AbstractObject> locationObjectExtensions;

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
    public LocationDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle180 longitude, Angle90 latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.locationSimpleExtensions = locationSimpleExtensions;
        this.locationObjectExtensions = locationObjectExtensions;
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
