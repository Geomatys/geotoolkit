package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Anglepos90Default implements Anglepos90 {

    private double angle;

    public Anglepos90Default(double angle)throws KmlException {
        if(angle >= 0 && angle <= 90){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between 0 and 90 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    public double getAngle(){return this.angle;}
}
