/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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


/**
 * Kind of conversions which are allowed on sample values during the read process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @see SampleConverter
 * @see SpatialImageReadParam#setAllowedConversion(SampleConversionType, boolean)
 *
 * @since 3.11
 * @module
 */
public enum SampleConversionType {
    /**
     * Indicates that {@link SpatialImageReader} is allowed to apply an offset on signed integer
     * values in order to get unsigned integers. More specifically, if
     * {@link SpatialImageReader#getRawDataType(int)} returns {@link DataBuffer#TYPE_SHORT}
     * and this conversion type {@linkplain SpatialImageReadParam#isAllowedConversion is allowed},
     * then {@code SpatialImageReader} will process as if the {@code getRawDataType(int)} method
     * returned {@link DataBuffer#TYPE_USHORT}. <em>Consequently, an offset will be added to every
     * sample values</em> in order to avoid negative values.
     * <p>
     * See {@link SpatialImageReader#getRawDataType(int)} for more information and an example.
     *
     * @see SpatialImageReader#getRawDataType(int)
     */
    SHIFT_SIGNED_INTEGERS
}
