package org.geotoolkit.data.model.kml;

/**
 * <p>This class implements an angle whose value
 * is included between 0 and 90 degrees.</p>
 *
 * @author Samuel Andrés
 */
public class DefaultAnglepos90 implements Anglepos90 {

    private final double angle;

    /**
     * @param angle The angle value.
     * @throws KmlException If the angle value is out of bound.
     */
    public DefaultAnglepos90(double angle)throws KmlException {
        if(angle >= 0 && angle <= 90){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between 0 and 90 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public double getAngle(){return this.angle;}
}
