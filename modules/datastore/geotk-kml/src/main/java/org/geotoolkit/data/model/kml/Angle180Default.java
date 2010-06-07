package org.geotoolkit.data.model.kml;

/**
 * <p>This class implements an angle whose value
 * is included between -180 and 180 degrees.</p>
 *
 * @author Samuel Andrés
 */
public class Angle180Default implements Angle180{

    private final double angle;

    /**
     * @param angle The angle value.
     * @throws KmlException If the angle value is out of bound.
     */
    public Angle180Default(double angle)throws KmlException {
        if(angle >= -180 && angle <= 180){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -180 and 180 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public double getAngle(){return this.angle;}
}
