package org.geotoolkit.data.model.kml;

/**
 * <p>This class implements an angle whose value
 * is included between -360 and 360 degrees.</p>
 *
 * @author Samuel Andrés
 */
public class Angle360Default implements Angle360{

    private final double angle;

    /**
     * @param angle The angle value.
     * @throws KmlException If the angle value is out of bound.
     */
    public Angle360Default(double angle)throws KmlException {
        if(angle >= -360 && angle <= 360){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -360 and 360 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public double getAngle(){return this.angle;}

    @Override
    public String toString(){
        return "Angle360default : "+this.angle;
    }
}
