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
package org.geotoolkit.image.io;

import java.awt.image.DataBuffer;
import org.geotoolkit.image.io.metadata.SampleDomain;
import org.geotoolkit.coverage.io.ImageCoverageReader;


/**
 * Kind of conversions which are allowed on sample values during the read process. This enum is
 * given to {@link SpatialImageReadParam} in order to give to the reading process some flexibility
 * about the values to be stored in the {@link java.awt.image.Raster} objects.
 * <p>
 * By default, the reading process performed by {@link SpatialImageReader} is strict and will store
 * the same values than the ones read from the stream. However more efficient storage can sometime
 * be achieved if some conversions are allowed, for example replacing fill values by 0 and applying
 * an offset for avoiding negative numbers. The {@link ImageCoverageReader} class in particular
 * allows some changes based on the additional knowledge inferred from image metadata.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see SampleConverter
 * @see SpatialImageReadParam#setSampleConversionAllowed(SampleConversionType, boolean)
 *
 * @since 3.11
 * @module
 */
public enum SampleConversionType {
    /**
     * Indicates that {@link SpatialImageReader} is allowed to apply an offset on signed integer
     * values in order to get unsigned integers. More specifically, if the {@code SpatialImageReader}
     * {@link SpatialImageReader#getRawDataType(int) getRawDataType(int)} method returns
     * {@link DataBuffer#TYPE_SHORT} and this conversion type
     * {@linkplain SpatialImageReadParam#isSampleConversionAllowed is allowed},
     * then {@code SpatialImageReader} will process as if the {@code getRawDataType(int)} method
     * returned {@link DataBuffer#TYPE_USHORT TYPE_USHORT}. <em>Consequently, an offset may be
     * added to every sample values</em> in order to avoid negative values.
     * <p>
     * See {@link SpatialImageReader#getRawDataType(int)} for more information and an example.
     *
     * @see SampleConverter#createOffset(double, double)
     */
    SHIFT_SIGNED_INTEGERS,

    /**
     * Indicates that {@link SpatialImageReader} is allowed to replace
     * {@linkplain SampleDomain#getFillSampleValues() fill values} by {@link Float#NaN NaN}.
     * This replacement is possible only if the {@linkplain SpatialImageReader#getRawDataType(int)
     * raw data type} is {@link DataBuffer#TYPE_FLOAT} or {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE}.
     *
     * @see SampleConverter#createPadValueMask(double)
     *
     * @since 3.12
     */
    REPLACE_FILL_VALUES,

    /**
     * Indicates that {@link SpatialImageReader} is allowed to store samples as floating point
     * values instead than integer values. When provided, this enum ensures that
     * {@linkplain SampleDomain#getFillSampleValues() fill values} can be replaced by
     * {@link Float#NaN NaN} if the {@link #REPLACE_FILL_VALUES} enum is also provided.
     *
     * @since 3.12
     */
    STORE_AS_FLOATS
}
