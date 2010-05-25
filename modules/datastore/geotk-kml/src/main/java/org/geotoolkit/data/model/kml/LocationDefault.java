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



    @Override
    public Angle180 getLongitude() {return this.longitude;}

    @Override
    public Angle90 getLatitude() {return this.latitude;}

    @Override
    public double getAltitude() {return this.altitude;}

    @Override
    public List<SimpleType> getLocationSimpleExtensions() {return this.locationSimpleExtensions;}

    @Override
    public List<AbstractObject> getLocationObjectExtensions() {return this.locationObjectExtensions;}

}
