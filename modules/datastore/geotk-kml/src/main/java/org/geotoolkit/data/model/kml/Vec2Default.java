package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class Vec2Default implements Vec2 {

    private final double x;
    private final double y;
    private final Units xUnit;
    private final Units yUnit;

    /**
     *
     * @param x
     * @param y
     * @param xUnit
     * @param yUnit
     */
    public Vec2Default(double x, double y, Units xUnit, Units yUnit){
        this.x = x;
        this.y = y;
        this.xUnit = xUnit;
        this.yUnit = yUnit;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getX() {return this.x;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getY() {return this.y;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Units getXUnits() {return this.xUnit;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Units getYUnits() {return this.yUnit;}

    @Override
    public String toString() {
        return "Vec2Default : " +
                "\n\tx : " +this.x+
                "\n\ty : " +this.y+
                "\n\txUnit : " +this.xUnit+
                "\n\tyUnit : "+this.yUnit;
    }

}
