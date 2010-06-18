package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static org.geotoolkit.data.model.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLookAt extends DefaultAbstractView implements LookAt {

    private Angle180 longitude;
    private Angle90 latitude;
    private double altitude;
    private Angle360 heading;
    private double tilt;
    private double range;
    private List<SimpleType> lookAtSimpleExtensions;
    private List<AbstractObject> lookAtObjectExtensions;

    /**
     * 
     */
    public DefaultLookAt(){
        this.altitude = DEF_ALTITUDE;
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
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.heading = heading;
        this.tilt = tilt;
        this.range = range;
        this.lookAtSimpleExtensions = (lookAtSimpleExtensions == null) ? EMPTY_LIST : lookAtSimpleExtensions;
        this.lookAtObjectExtensions = (lookAtObjectExtensions == null) ? EMPTY_LIST : lookAtObjectExtensions;
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
    public void setTilt(double tilt) {this.tilt = tilt;}

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
