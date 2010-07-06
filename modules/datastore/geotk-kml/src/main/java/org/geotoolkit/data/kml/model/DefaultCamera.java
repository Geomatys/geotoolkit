package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCamera extends DefaultAbstractView implements Camera {

    private double longitude;
    private double latitude;
    private double altitude;
    private double heading;
    private double tilt;
    private double roll;
    private AltitudeMode altitudeMode;

    /**
     *
     */
    public DefaultCamera() {
        this.longitude = DEF_LONGITUDE;
        this.latitude = DEF_LATITUDE;
        this.altitude = DEF_ALTITUDE;
        this.heading = DEF_HEADING;
        this.tilt = DEF_TILT;
        this.roll = DEF_ROLL;
        this.altitudeMode = DEF_ALTITUDE_MODE;
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
    public DefaultCamera(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions,
            List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions,
            List<AbstractObject> cameraObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions,
                abstractViewObjectExtensions);
        this.longitude = KmlUtilities.checkAngle180(longitude);
        this.latitude = KmlUtilities.checkAngle90(latitude);
        this.altitude = altitude;
        this.heading = KmlUtilities.checkAngle360(heading);
        this.tilt = KmlUtilities.checkAnglePos180(tilt);
        this.roll = KmlUtilities.checkAngle180(roll);
        this.altitudeMode = altitudeMode;
        if (cameraSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.CAMERA).addAll(cameraSimpleExtensions);
        }
        if (cameraObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.CAMERA).addAll(cameraObjectExtensions);
        }
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
    public double getHeading() {
        return this.heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTilt() {
        return this.tilt;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRoll() {
        return this.roll;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLongitude(double longitude) {
        this.longitude = KmlUtilities.checkAngle180(longitude);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatitude(double latitude) {
        this.latitude = KmlUtilities.checkAngle90(latitude);
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
    public void setHeading(double heading) {
        this.heading = KmlUtilities.checkAngle360(heading);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {
        this.tilt = KmlUtilities.checkAnglePos180(tilt);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRoll(double roll) {
        this.roll = KmlUtilities.checkAngle180(roll);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }
}
