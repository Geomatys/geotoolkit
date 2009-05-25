/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.math;

import java.io.Serializable;
import org.opengis.util.Cloneable;


/**
 * A simple class for the handling of complex numbers. This is not the purpose of this class
 * to provides a full-fledged library for complex number handling. This class exists mostly
 * for the limited needs of some transformation methods.
 * <p>
 * For performance reasons, the methods in this class never create new objects. They always
 * operate on an object specified in argument, and store the result in the object on which
 * the method was invoked.
 * <p>
 * This class is final for performance reason.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public final class Complex implements Cloneable, Serializable {
    /**
     * For compatibility with previous versions during deserialization.
     */
    private static final long serialVersionUID = -8143196508298758583L;

    /**
     * The real part of the complex number.
     */
    public double real;

    /**
     * The imaginary part of the complex number.
     */
    public double imag;

    /**
     * Creates a complex number initialized to (0,0).
     */
    public Complex() {
    }

    /**
     * Creates a complex number initialized to the same value than the specified one.
     *
     * @param c The complex number to copy.
     */
    public Complex(final Complex c) {
        real = c.real;
        imag = c.imag;
    }

    /**
     * Creates a complex number initialized to the specified real and imaginary parts.
     *
     * @param real The real part.
     * @param imag The imaginary part.
     */
    public Complex(final double real, final double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Set this complex number to the same value than the specified one.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c
     * }
     *
     * @param c The complex number to copy.
     */
    public void copy(final Complex c) {
        real = c.real;
        imag = c.imag;
    }

    /**
     * Multiplies a complex number by a scalar.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c * s
     * }
     *
     * @param c The complex number to multiply with the scalar.
     * @param s The scalar value to multiply.
     */
    public void multiply(final Complex c, final double s) {
        real = c.real * s;
        imag = c.imag * s;
    }

    /**
     * Multplies two complex numbers.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c1 * c2
     * }
     *
     * @param c1 The first complex number to multiply.
     * @param c2 The second complex number to multiply.
     */
    public void multiply(final Complex c1, final Complex c2) {
        final double x1 = c1.real;
        final double y1 = c1.imag;
        final double x2 = c2.real;
        final double y2 = c2.imag;
        real = (x1 * x2) - (y1 * y2);
        imag = (y1 * x2) + (x1 * y2);
    }

    /**
     * Divides one complex number by another.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c1 / c2
     * }
     *
     * @param c1 The complex number.
     * @param c2 The complex divisor.
     */
    public void divide(final Complex c1, final Complex c2) {
        final double x1 = c1.real;
        final double y1 = c1.imag;
        final double x2 = c2.real;
        final double y2 = c2.imag;
        final double denom = (x2 * x2) + (y2 * y2);
        real = ((x1 * x2) + (y1 * y2)) / denom;
        imag = ((y1 * x2) - (x1 * y2)) / denom;
    }

    /**
     * Adds to complex numbers.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c1 + c2
     * }
     *
     * @param c1 The first complex number to add.
     * @param c2 The second complex number to add.
     */
    public void add(final Complex c1, final Complex c2) {
        real = c1.real + c2.real;
        imag = c1.imag + c2.imag;
    }

    /**
     * Multplies two complex numbers, and add the result to a third one.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c0 + (c1 * c2)
     * }
     *
     * @param c1 The first complex number to multiply.
     * @param c2 The second complex number to multiply.
     * @param c0 The complex number to add to the product.
     */
    public void addMultiply(final Complex c0, final Complex c1, final Complex c2) {
        final double x1 = c1.real;
        final double y1 = c1.imag;
        final double x2 = c2.real;
        final double y2 = c2.imag;
        real = c0.real + ((x1 * x2) - (y1 * y2));
        imag = c0.imag + ((y1 * x2) + (x1 * y2));
    }

    /**
     * Computes the integer power of a complex number up to 6.
     * This method computes the following:
     *
     * {@preformat java
     *     this = c ^ power
     * }
     *
     * @param c The complex number to raise to a power.
     * @param power The power to raise the complex number.
     */
    public void power(final Complex c, final int power) {
        final double x = c.real;
        final double y = c.imag;
        switch (power) {
            case 0: {
                real = 1;
                imag = 0;
                break;
            }
            case 1: {
                real = x;
                imag = y;
                break;
            }
            case 2: {
                real = (x * x) - (y * y);
                imag = 2 * x * y;
                break;
            }
            case 3: {
                final double x2 = x * x;
                final double y2 = y * y;
                real = x * (x2 - 3*y2);
                imag = y * (3*x2 - y2);
                break;
            }
            case 4: {
                final double x2 = x * x;
                final double y2 = y * y;
                real = (x2 * x2) - (6 * x2 * y2) + (y2 * y2);
                imag = 4 * (x * y) * (x2 - y2);
                break;
            }
            case 5: {
                final double x2 = x  * x;
                final double y2 = y  * y;
                final double x4 = x2 * x2;
                final double y4 = y2 * y2;
                final double cr = (x2* y2) * 10;
                real = x * (x4 - cr + 5*y4);
                imag = y * (5*x4 - cr + y4);
                break;
            }
            case 6: {
                final double x2 = x  * x;
                final double y2 = y  * y;
                final double x4 = x2 * x2;
                final double y4 = y2 * y2;
                real = (x4 * (x2 - 15*y2) + (15*x2 * y4)) - (y4 * y2);
                imag = (x * y) * (6 * (x4 + y4) - (20 * x2 * y2));
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(power));
            }
        }
    }

    /**
     * Returns a copy of this complex number.
     */
    @Override
    public Complex clone() {
        return new Complex(this);
    }

    /**
     * Returns {@code true} if this complex number has the same value than the specified one.
     *
     * @param c The complex number to compare with this object.
     * @return {@code true} if both objects are equal.
     */
    public boolean equals(final Complex c) {
        return Double.doubleToLongBits(real) == Double.doubleToLongBits(c.real) &&
               Double.doubleToLongBits(imag) == Double.doubleToLongBits(c.imag);
    }

    /**
     * Compares this complex with the specified object for equality.
     *
     * @param c The complex number to compare with this object.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object c) {
        return (c instanceof Complex) && equals((Complex) c);
    }

    /**
     * Returns a hash value for this complex number.
     */
    @Override
    public int hashCode() {
        final long code = Double.doubleToLongBits(real) + 31*Double.doubleToLongBits(imag);
        return (int) code ^ (int) (code >>> 32);
    }

    /**
     * Returns a string representation of this complex number.
     */
    @Override
    public String toString() {
        return "Complex[" + real + ", " + imag + ']';
    }
}
