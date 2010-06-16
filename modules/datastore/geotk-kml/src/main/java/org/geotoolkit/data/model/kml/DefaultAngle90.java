package org.geotoolkit.data.model.kml;

/**
 * <p>This class implements an angle whose value
 * is included between -90 and 90 degrees.</p>
 * 
 * @author Samuel Andrés
 */
public class DefaultAngle90 implements Angle90 {

    private final double angle;

    /**
     * @param angle The angle value.
     * @throws KmlException If the angle value is out of bound.
     */
    public DefaultAngle90(double angle) throws KmlException {
        if(angle >= -90 && angle <= 90){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -90 and 90 degrees.");
        }
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public double getAngle(){return this.angle;}
}
