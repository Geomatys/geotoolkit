package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Angle360Default implements Angle360{

    private double angle;
    
    public Angle360Default(double angle)throws KmlException {
        if(angle >= -360 && angle <= 360){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -360 and 360 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    @Override
    public double getAngle(){return this.angle;}

    @Override
    public String toString(){
        return "Angle360default : "+this.angle;
    }
}
