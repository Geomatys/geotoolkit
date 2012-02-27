/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage;

import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.WritableRectIter;
import org.geotoolkit.image.TransfertRectIter;


/**
 * Transforms the value returned by the wrapped {@link RectIter} from unsigned integer to
 * signed integer.  This wrapper is used when the data buffer type is {@code TYPE_USHORT}
 * (typically because the color model is an {@link java.awt.image.IndexColorModel}) while
 * the range of sample value is rather the range of {@code TYPE_SHORT}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
final class SignedRectIter extends TransfertRectIter {
    /**
     * Creates a new iterator wrapping given ones.
     */
    SignedRectIter(final RectIter src, final WritableRectIter dst) {
        super(src, dst);
    }

    /**
     * Casts the sample value to short, then let the widening conversion happen back to
     * {@code int}. This is sufficient for fixing the sign. We apply the same strategy
     * for floating point values too, on the assumption that the underlying data type
     * is always {@code TYPE_USHORT} when this class is used.
     */
    @Override public int    getSample()            {return (short) super.getSample( );}
    @Override public int    getSample(int b)       {return (short) super.getSample(b);}
    @Override public float  getSampleFloat()       {return (short) super.getSample( );}
    @Override public float  getSampleFloat(int b)  {return (short) super.getSample(b);}
    @Override public double getSampleDouble()      {return (short) super.getSample( );}
    @Override public double getSampleDouble(int b) {return (short) super.getSample(b);}

    /**
     * Delegates to the source iterator, then fix the sign assuming that the underlying
     * data type is {@code TYPE_USHORT}.
     */
    @Override
    public int[] getPixel(int[] samples) {
        samples = super.getPixel(samples);
        for (int i=0; i<samples.length; i++) {
            samples[i] = (short) samples[i];
        }
        return samples;
    }

    /**
     * Delegates to the source iterator, then fix the sign assuming that the underlying
     * data type is {@code TYPE_USHORT}.
     */
    @Override
    public float[] getPixel(float[] samples) {
        samples = super.getPixel(samples);
        for (int i=0; i<samples.length; i++) {
            samples[i] = (short) samples[i];
        }
        return samples;
    }

    /**
     * Delegates to the source iterator, then fix the sign assuming that the underlying
     * data type is {@code TYPE_USHORT}.
     */
    @Override
    public double[] getPixel(double[] samples) {
        samples = super.getPixel(samples);
        for (int i=0; i<samples.length; i++) {
            samples[i] = (short) samples[i];
        }
        return samples;
    }
}
