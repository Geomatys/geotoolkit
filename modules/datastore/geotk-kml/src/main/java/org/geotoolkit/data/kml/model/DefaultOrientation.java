package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultOrientation extends DefaultAbstractObject implements Orientation {

    private double heading;
    private double tilt;
    private double roll;

    /**
     *
     */
    public DefaultOrientation() {
        this.heading = DEF_HEADING;
        this.tilt = DEF_TILT;
        this.roll = DEF_ROLL;
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
        this.tilt = KmlUtilities.checkAngle360(tilt);
        this.roll = KmlUtilities.checkAngle180(roll);
        if (orientationSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ORIENTATION).addAll(orientationSimpleExtensions);
        }
        if (orientationObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ORIENTATION).addAll(orientationObjectExtensions);
        }
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
    public void setHeading(double heading) {
        this.heading = KmlUtilities.checkAngle360(heading);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {
        this.tilt = KmlUtilities.checkAngle360(tilt);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRoll(double roll) {
        this.roll = KmlUtilities.checkAngle180(roll);
    }
}
