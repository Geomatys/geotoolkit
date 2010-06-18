package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultOrientation extends DefaultAbstractObject implements Orientation {

    private final double heading;
    private final double tilt;
    private final double roll;
    private final List<SimpleType> orientationSimpleExtensions;
    private final List<AbstractObject> orientationObjectExtensions;

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
            List<AbstractObject> orientationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.heading = KmlUtilities.checkAngle360(heading);
        this.tilt= KmlUtilities.checkAnglePos180(tilt);
        this.roll = KmlUtilities.checkAngle180(roll);
        this.orientationSimpleExtensions = (orientationSimpleExtensions == null) ? EMPTY_LIST : orientationSimpleExtensions;
        this.orientationObjectExtensions = (orientationObjectExtensions == null) ? EMPTY_LIST : orientationObjectExtensions;
    }

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
    public double getRoll() {return this.roll;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getOrientationSimpleExtensions() {return this.orientationSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getOrientationObjectExtensions() {return this.orientationObjectExtensions;}

}
