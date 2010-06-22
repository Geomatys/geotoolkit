package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLookAt extends DefaultAbstractView implements LookAt {

    private double longitude;
    private double latitude;
    private double altitude;
    private double heading;
    private double tilt;
    private double range;
    private List<SimpleType> lookAtSimpleExtensions;
    private List<AbstractObject> lookAtObjectExtensions;

    /**
     * 
     */
    public DefaultLookAt(){
        this.longitude = DEF_LONGITUDE;
        this.latitude = DEF_LATITUDE;
        this.altitude = DEF_ALTITUDE;
        this.heading = DEF_HEADING;
        this.tilt = DEF_TILT;
        this.range = DEF_RANGE;
        this.lookAtSimpleExtensions = EMPTY_LIST;
        this.lookAtObjectExtensions = EMPTY_LIST;
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
     * @param range
     * @param lookAtSimpleExtensions
     * @param lookAtObjectExtensions
     */
    public DefaultLookAt(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions);
        this.longitude = KmlUtilities.checkAngle180(longitude);
        this.latitude = KmlUtilities.checkAngle90(latitude);
        this.altitude = altitude;
        this.heading = KmlUtilities.checkAngle360(heading);
        this.tilt = KmlUtilities.checkAnglePos180(tilt);
        this.range = range;
        this.lookAtSimpleExtensions = (lookAtSimpleExtensions == null) ? EMPTY_LIST : lookAtSimpleExtensions;
        this.lookAtObjectExtensions = (lookAtObjectExtensions == null) ? EMPTY_LIST : lookAtObjectExtensions;
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
    public double getHeading() {return this.heading;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTilt() {return this.tilt;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRange() {return this.range;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLookAtSimpleExtensions() {return this.lookAtSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLookAtObjectExtensions() {return this.lookAtObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLongitude(double longitude) {this.longitude = KmlUtilities.checkAngle180(longitude);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLatitude(double latitude) {this.latitude = KmlUtilities.checkAngle90(latitude);}

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
    public void setHeading(double heading) {this.heading = KmlUtilities.checkAngle360(heading);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {this.tilt = KmlUtilities.checkAnglePos180(tilt);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRange(double range) {this.range = range;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLookAtSimpleExtensions(List<SimpleType> lookAtSimpleExtensions) {
        this.lookAtSimpleExtensions = lookAtSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLookAtObjectExtensions(List<AbstractObject> lookAtObjectExtensions) {
        this.lookAtObjectExtensions = lookAtObjectExtensions;
    }

}
