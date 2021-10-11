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
package org.geotoolkit.image;

import java.awt.image.*;
import org.geotoolkit.lang.Static;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;


/**
 * A set of static methods working on {@link SampleModel}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public final class SampleModels extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private SampleModels() {
    }

    /**
     * Returns the size (in bits) of the data type for the given sample model.
     * This is a convenience method for:
     *
     * {@preformat java
     *     return DataBuffer.getDataTypeSize(model.getDataType());
     * }
     *
     * @param  model The sample model for which to get the size of data type.
     * @return The size of data type, in bits.
     *
     * @see DataBuffer#getDataTypeSize(int)
     */
    public static int getDataTypeSize(final SampleModel model) {
        return DataBuffer.getDataTypeSize(model.getDataType());
    }

    /**
     * Returns the <cite>scan line stride</cite> of the given sample model. The scanline stride is
     * the number of data array elements between a given sample and the corresponding sample in the
     * same column of the next scanline.
     *
     * @param  model The sample model from which to get the scan line stride.
     * @return The scan line stride.
     * @throws IllegalArgumentException If the given model is not of a known type.
     */
    public static int getScanlineStride(final SampleModel model) throws IllegalArgumentException {
        if (model instanceof ComponentSampleModel) {
            return ((ComponentSampleModel) model).getScanlineStride();
        }
        if (model instanceof SinglePixelPackedSampleModel) {
            return ((SinglePixelPackedSampleModel) model).getScanlineStride();
        }
        if (model instanceof MultiPixelPackedSampleModel) {
            return ((MultiPixelPackedSampleModel) model).getScanlineStride();
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.UnknownType_1, Classes.getShortClassName(model)));
    }

    /**
     * Returns the <cite>pixel stride</cite> of the given sample model. The pixel stride is the
     * number of data array elements between two samples for the same band on the same scanline.
     * <p>
     * If the given model is an instance of {@link MultiPixelPackedSampleModel}, then the stride
     * can be fractional. Note that the returned value still exact since the ratio of an integer
     * with a power of 2 has exact representations in IEEE 754.
     *
     * @param  model The sample model from which to get the pixel stride.
     * @return The pixel stride, rounded toward positive infinity if necessary.
     * @throws IllegalArgumentException If the given model is not of a known type.
     */
    public static float getPixelStride(final SampleModel model) throws IllegalArgumentException {
        if (model instanceof ComponentSampleModel) {
            return ((ComponentSampleModel) model).getPixelStride();
        }
        if (model instanceof SinglePixelPackedSampleModel) {
            return 1;
        }
        if (model instanceof MultiPixelPackedSampleModel) {
            return ((MultiPixelPackedSampleModel) model).getPixelBitStride() / (float) getDataTypeSize(model);
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.UnknownType_1, Classes.getShortClassName(model)));
    }

    /**
     * Returns the <cite>pixel stride</cite> of the given sample model <strong>in bits</strong>.
     * This is the number of bits between two samples for the same band on the same scanline.
     *
     * @param  model The sample model from which to get the pixel stride.
     * @return The pixel stride in <strong>bits</strong>.
     * @throws IllegalArgumentException If the given model is not of a known type.
     */
    public static int getPixelBitStride(final SampleModel model) throws IllegalArgumentException {
        if (model instanceof ComponentSampleModel) {
            return ((ComponentSampleModel) model).getPixelStride() * getDataTypeSize(model);
        }
        if (model instanceof SinglePixelPackedSampleModel) {
            return getDataTypeSize(model);
        }
        if (model instanceof MultiPixelPackedSampleModel) {
            return ((MultiPixelPackedSampleModel) model).getPixelBitStride();
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.UnknownType_1, Classes.getShortClassName(model)));
    }
}
