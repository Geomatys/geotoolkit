package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Angle90Default implements Angle90 {

    private double angle;

    public Angle90Default(double angle) throws KmlException {
        if(angle > -90 && angle < 90){
            this.angle = angle;
        } else {
            throw new KmlException("This angle type requires a value between -90 and 90 degrees.");
        }
    }

    @Override
    public double getAngle(){return this.angle;}
}
