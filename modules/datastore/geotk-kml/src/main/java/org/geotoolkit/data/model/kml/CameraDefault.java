package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class CameraDefault extends AbstractViewDefault implements Camera {

    private final Angle180 longitude;
    private final Angle90 latitude;
    private final double altitude;
    private final Angle360 heading;
    private final Anglepos180 tilt;
    private final Angle180 roll;
    private final AltitudeMode altitudeMode;
    private final List<SimpleType> cameraSimpleExtensions;
    private final List<AbstractObject> cameraObjectExtensions;

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
    public CameraDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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

}
