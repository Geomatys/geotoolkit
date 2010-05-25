package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class LookAtDefault extends AbstractViewDefault implements LookAt {

    private Angle180 longitude;
    private Angle90 latitude;
    private double altitude;
    private Angle360 heading;
    private Anglepos180 tilt;
    private double range;
    private List<SimpleType> lookAtSimpleExtensions;
    private List<AbstractObject> lookAtObjectExtensions;

    public LookAtDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.heading = heading;
        this.tilt = tilt;
        this.range = range;
        this.lookAtSimpleExtensions = lookAtSimpleExtensions;
        this.lookAtObjectExtensions = lookAtObjectExtensions;
    }

    @Override
    public Angle180 getLongitude() {return this.longitude;}

    @Override
    public Angle90 getLatitude() {return this.latitude;}

    @Override
    public double getAltitude() {return this.altitude;}

    @Override
    public Angle360 getHeading() {return this.heading;}

    @Override
    public Anglepos180 getTilt() {return this.tilt;}

    @Override
    public double getRange() {return this.range;}

    @Override
    public List<SimpleType> getLookAtSimpleExtensions() {return this.lookAtSimpleExtensions;}

    @Override
    public List<AbstractObject> getLookAtObjectExtensions() {return this.lookAtObjectExtensions;}

}
