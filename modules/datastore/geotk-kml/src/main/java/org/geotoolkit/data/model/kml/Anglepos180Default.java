package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Anglepos180Default implements Anglepos180{

    private double angle;

    public Anglepos180Default(double angle)throws KmlException {
        if(angle >=  0 && angle <= 180){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between 0 and 180 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    @Override
    public double getAngle() {return this.angle;}
}
