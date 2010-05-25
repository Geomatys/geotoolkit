package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Angle180Default implements Angle180{

    private double angle;

    public Angle180Default(double angle)throws KmlException {
        if(angle >= -180 && angle <= 180){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -180 and 180 degrees. You've intented an initialization with "+angle+" degree(s)");
        }
    }

    @Override
    public double getAngle(){return this.angle;}
}
