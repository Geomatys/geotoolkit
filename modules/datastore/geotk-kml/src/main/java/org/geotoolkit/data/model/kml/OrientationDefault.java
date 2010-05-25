package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class OrientationDefault extends AbstractObjectDefault implements Orientation {

    private Angle360 heading;
    private Anglepos180 tilt;
    private Angle180 roll;
    private List<SimpleType> orientationSimpleExtensions;
    private List<AbstractObject> orientationObjectExtensions;

    public OrientationDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.heading = heading;
        this.tilt= tilt;
        this.roll = roll;
        this.orientationSimpleExtensions = orientationSimpleExtensions;
        this.orientationObjectExtensions = orientationObjectExtensions;
    }

    @Override
    public Angle360 getHeading() {return this.heading;}

    @Override
    public Anglepos180 getTilt() {return this.tilt;}

    @Override
    public Angle180 getRoll() {return this.roll;}

    @Override
    public List<SimpleType> getOrientationSimpleExtensions() {return this.orientationSimpleExtensions;}

    @Override
    public List<AbstractObject> getOrientationObjectExtensions() {return this.orientationObjectExtensions;}

}
