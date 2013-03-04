/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.jai;

import java.util.Arrays;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

import org.apache.sis.util.ArraysExt;


/**
 * A sample value suitable for use in a {@link java.util.HashSet}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 */
abstract class SampleValues {
    /**
     * Creates a {@code SampleValues}.
     */
    SampleValues() {
    }

    /**
     * Returns a new instance for the given number of bands.
     * The transfer type must be one of the {@link DataBuffer} constants.
     */
    public static SampleValues getInstance(final int transferType, final int numBands) {
        switch (transferType) {
            case DataBuffer.TYPE_DOUBLE: return new Double (numBands);
            case DataBuffer.TYPE_FLOAT:  return new Float  (numBands);
            default:                     return new Integer(numBands);
        }
    }

    /**
     * Returns a new instance for the given type wrapping the given sample values.
     * The transfer type must be one of the {@link DataBuffer} constants.
     */
    public static SampleValues getInstance(final int transferType, final double[] values) {
        switch (transferType) {
            case DataBuffer.TYPE_DOUBLE: return new Double (values);
            case DataBuffer.TYPE_FLOAT:  return new Float  (values);
            default:                     return new Integer(values);
        }
    }

    /**
     * Returns a new instance of the same type than this {@code SampleValues} object.
     * This new instance is initialized to zero sample values.
     */
    public abstract SampleValues instance();

    /**
     * Gets the pixel from the given raster and stores the values in this object.
     *
     * @param  source The raster from which to get the pixel value.
     * @param  x The <var>x</var> pixel ordinate.
     * @param  y The <var>y</var> pixel ordinate.
     * @return Always {@code this}.
     */
    public abstract SampleValues getPixel(final Raster source, final int x, final int y);

    /**
     * Sets the pixel of the given raster to the values in this object.
     *
     * @param  dest The raster in which to set the pixel value.
     * @param  x The <var>x</var> pixel ordinate.
     * @param  y The <var>y</var> pixel ordinate.
     */
    public abstract void setPixel(final WritableRaster dest, final int x, final int y);

    /**
     * Implementation backed by {@code double} values.
     */
    private static final class Double extends SampleValues {
        /** The values. */
        final double[] samples;

        /** Creates a new instance for the given number of bands. */
        Double(final int numBands) {
            samples = new double[numBands];
        }

        /** Creates a new instance initialized to the given value. */
        Double(final double[] samples) {
            this.samples = samples.clone();
        }

        /** {@inheritDoc} */
        @Override public SampleValues instance() {
            return new Double(samples.length);
        }

        /** {@inheritDoc} */
        @Override public SampleValues getPixel(final Raster source, final int x, final int y) {
            source.getPixel(x, y, samples);
            return this;
        }

        /** {@inheritDoc} */
        @Override public void setPixel(final WritableRaster dest, final int x, final int y) {
            dest.setPixel(x, y, samples);
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return Arrays.hashCode(samples);
        }

        /** {@inheritDoc} */
        @Override public boolean equals(final Object other) {
            return (other instanceof Double) && Arrays.equals(samples, ((Double) other).samples);
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return Arrays.toString(samples);
        }
    }

    /**
     * Implementation backed by {@code float} values.
     */
    private static final class Float extends SampleValues {
        /** The values. */
        final float[] samples;

        /** Creates a new instance for the given number of bands. */
        Float(final int numBands) {
            samples = new float[numBands];
        }

        /** Creates a new instance initialized to the given value. */
        Float(final double[] samples) {
            this.samples = ArraysExt.copyAsFloats(samples);
        }

        /** {@inheritDoc} */
        @Override public SampleValues instance() {
            return new Float(samples.length);
        }

        /** {@inheritDoc} */
        @Override public SampleValues getPixel(final Raster source, final int x, final int y) {
            source.getPixel(x, y, samples);
            return this;
        }

        /** {@inheritDoc} */
        @Override public void setPixel(final WritableRaster dest, final int x, final int y) {
            dest.setPixel(x, y, samples);
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return Arrays.hashCode(samples);
        }

        /** {@inheritDoc} */
        @Override public boolean equals(final Object other) {
            return (other instanceof Float) && Arrays.equals(samples, ((Float) other).samples);
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return Arrays.toString(samples);
        }
    }

    /**
     * Implementation backed by {@code int} values.
     */
    private static final class Integer extends SampleValues {
        /** The values. */
        final int[] samples;

        /** Creates a new instance for the given number of bands. */
        Integer(final int numBands) {
            samples = new int[numBands];
        }

        /** Creates a new instance initialized to the given value. */
        Integer(final double[] samples) {
            this.samples = ArraysExt.copyAsInts(samples);
        }

        /** {@inheritDoc} */
        @Override public SampleValues instance() {
            return new Integer(samples.length);
        }

        /** {@inheritDoc} */
        @Override public SampleValues getPixel(final Raster source, final int x, final int y) {
            source.getPixel(x, y, samples);
            return this;
        }

        /** {@inheritDoc} */
        @Override public void setPixel(final WritableRaster dest, final int x, final int y) {
            dest.setPixel(x, y, samples);
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return Arrays.hashCode(samples);
        }

        /** {@inheritDoc} */
        @Override public boolean equals(final Object other) {
            return (other instanceof Integer) && Arrays.equals(samples, ((Integer) other).samples);
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return Arrays.toString(samples);
        }
    }
}
