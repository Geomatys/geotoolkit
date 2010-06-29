package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultOrientation extends DefaultAbstractObject implements Orientation {

    private double heading;
    private double tilt;
    private double roll;
    private List<SimpleType> orientationSimpleExtensions;
    private List<AbstractObject> orientationObjectExtensions;

    /**
     *
     */
    public DefaultOrientation() {
        this.heading = DEF_HEADING;
        this.tilt = DEF_TILT;
        this.roll = DEF_ROLL;
        this.orientationSimpleExtensions = EMPTY_LIST;
        this.orientationObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param heading
     * @param tilt
     * @param roll
     * @param orientationSimpleExtensions
     * @param orientationObjectExtensions
     */
    public DefaultOrientation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.heading = KmlUtilities.checkAngle360(heading);
        this.tilt = KmlUtilities.checkAnglePos180(tilt);
        this.roll = KmlUtilities.checkAngle180(roll);
        this.orientationSimpleExtensions = (orientationSimpleExtensions == null) ? EMPTY_LIST : orientationSimpleExtensions;
        this.orientationObjectExtensions = (orientationObjectExtensions == null) ? EMPTY_LIST : orientationObjectExtensions;
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
    public List<SimpleType> getOrientationSimpleExtensions() {
        return this.orientationSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getOrientationObjectExtensions() {
        return this.orientationObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(double heading) {
        this.heading = heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRoll(double roll) {
        this.roll = roll;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOrientationSimpleExtensions(List<SimpleType> orientationSimpleExtensions) {
        this.orientationSimpleExtensions = orientationSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOrientationObjectExtensions(List<AbstractObject> orientationObjectExtensions) {
        this.orientationObjectExtensions = orientationObjectExtensions;
    }
}
