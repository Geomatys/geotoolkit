/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Vector extends Tuple {

    double length();

    double lengthSquare();

    /**
     * Normalize this vector.
     */
    void normalize();

    /**
     * Add other vector values to this vector.
     * @param other
     */
    void add(Tuple other);

    /**
     * Subtract other vector values to this vector.
     * @param other
     */
    void subtract(Tuple other);

    /**
     * Scale vector by given value.
     *
     * @param scale scaling factor
     */
    void scale(double scale);

    /**
     * Cross product.
     *
     * @param other
     * @return
     */
    default Vector cross(Tuple other) {
        double[] v1 = toArrayDouble();
        double[] v2 = other.toArrayDouble();
        double[] buffer = new double[v1.length];
        Vectors.cross(v1, v2, buffer);
        Vector res = Vectors.createDouble(buffer.length);
        res.set(buffer);
        return res;
    }

    /**
     * Dot product.
     *
     * @param other
     * @return
     */
    default double dot(Tuple other) {
        double dot = 0;
        for (int i=0,n=getDimension();i<n;i++){
            dot += get(i) * other.get(i);
        }
        return dot;
    }

}
