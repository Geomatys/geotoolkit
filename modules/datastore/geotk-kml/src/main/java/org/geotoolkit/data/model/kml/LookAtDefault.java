package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LookAtDefault extends AbstractViewDefault implements LookAt {

    private final Angle180 longitude;
    private final Angle90 latitude;
    private final double altitude;
    private final Angle360 heading;
    private final Anglepos180 tilt;
    private final double range;
    private final List<SimpleType> lookAtSimpleExtensions;
    private final List<AbstractObject> lookAtObjectExtensions;

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
    public Anglepos180 getTilt() {return this.tilt;}

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

}
