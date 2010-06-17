package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static org.geotoolkit.data.model.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCamera extends DefaultAbstractView implements Camera {

    private Angle180 longitude;
    private Angle90 latitude;
    private double altitude;
    private Angle360 heading;
    private Anglepos180 tilt;
    private Angle180 roll;
    private AltitudeMode altitudeMode;
    private List<SimpleType> cameraSimpleExtensions;
    private List<AbstractObject> cameraObjectExtensions;

    public DefaultCamera(){
        super();
        this.longitude = null;
        this.latitude = null;
        this.heading = null;
        this.tilt = null;
        this.roll = null;
        this.altitudeMode = null;
        this.cameraSimpleExtensions = EMPTY_LIST;
        this.cameraObjectExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     * @param longitude
     * @param latitude
     * @param altitude
     * @param heading
     * @param tilt
     * @param roll
     * @param altitudeMode
     * @param cameraSimpleExtensions
     * @param cameraObjectExtensions
     */
    public DefaultCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, Angle180 roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.heading = heading;
        this.tilt = tilt;
        this.roll = roll;
        this.altitudeMode = altitudeMode;
        this.cameraSimpleExtensions = (cameraSimpleExtensions == null) ? EMPTY_LIST : cameraSimpleExtensions;
        this.cameraObjectExtensions = (cameraObjectExtensions == null) ? EMPTY_LIST : cameraObjectExtensions;
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
    public Angle360 getHeading() {return this.heading;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Anglepos180 getTilt() {return this.tilt;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle180 getRoll() {return this.roll;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getCameraSimpleExtensions() {return this.cameraSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getCameraObjectExtensions() {return this.cameraObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLongitude(Angle180 longitude) {this.longitude = longitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatitude(Angle90 latitude) {this.latitude = latitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitude(double altitude) {this.altitude = altitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(Angle360 heading) {this.heading = heading;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(Anglepos180 tilt) {this.tilt = tilt;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRoll(Angle180 roll) {this.roll = roll;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {this.altitudeMode = altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCameraSimpleExtensions(List<SimpleType> cameraSimpleExtensions) {
        this.cameraSimpleExtensions = cameraSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCameraObjectExtensions(List<AbstractObject> cameraObjectExtensions) {
        this.cameraObjectExtensions = cameraObjectExtensions;
    }

}
