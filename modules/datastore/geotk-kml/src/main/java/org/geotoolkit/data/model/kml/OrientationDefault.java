package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class OrientationDefault extends AbstractObjectDefault implements Orientation {

    private final Angle360 heading;
    private final Anglepos180 tilt;
    private final Angle180 roll;
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
    public OrientationDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.heading = heading;
        this.tilt= tilt;
        this.roll = roll;
        this.orientationSimpleExtensions = (orientationSimpleExtensions == null) ? EMPTY_LIST : orientationSimpleExtensions;
        this.orientationObjectExtensions = (orientationObjectExtensions == null) ? EMPTY_LIST : orientationObjectExtensions;
    }

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
    public List<SimpleType> getOrientationSimpleExtensions() {return this.orientationSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getOrientationObjectExtensions() {return this.orientationObjectExtensions;}

}
