package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class CameraDefault extends AbstractViewDefault implements Camera {

    private Angle180 longitude;
    private Angle90 latitude;
    private double altitude;
    private Angle360 heading;
    private Anglepos180 tilt;
    private Angle180 roll;
    private List<SimpleType> cameraSimpleExtensions;
    private List<AbstractObject> cameraObjectExtensions;

    public CameraDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.heading = heading;
        this.tilt = tilt;
        this.roll = roll;
        this.cameraSimpleExtensions = cameraSimpleExtensions;
        this.cameraObjectExtensions = cameraObjectExtensions;
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
    public Angle180 getRoll() {return this.roll;}

    @Override
    public List<SimpleType> getCameraSimpleExtensions() {return this.cameraSimpleExtensions;}

    @Override
    public List<AbstractObject> getCameraObjectExtensions() {return this.cameraObjectExtensions;}

}
