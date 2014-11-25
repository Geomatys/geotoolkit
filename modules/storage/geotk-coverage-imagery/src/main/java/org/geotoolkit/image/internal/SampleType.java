/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.image.internal;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;

/**
 * Define internaly {@link ColorModel} data type.
 * 
 * @author Remi Marechal (Geomatys).
 */
public enum SampleType {
    /**
     * Data type {@link Byte}.
     */
    Byte, 
    
    /**
     * Data type {@link Short}.
     */
    Short,

    /**
     * Data type {@link Short}.
     */
    UShort,

    /**
     * Data type {@link Integer}.
     */
    Integer, 
    
    /**
     * Data type {@link Float}.
     */
    Float, 
    
    /**
     * Data type {@link Double}.
     */
    Double;

    /**
     * Mapping between {@link java.awt.image.SampleModel#getDataType()} and
     * {@link org.geotoolkit.image.internal.SampleType} enum.
     *
     * @param dataType integer from {@link java.awt.image.DataBuffer} constants
     * @return {@link org.geotoolkit.image.internal.SampleType} or null if type undefined.
     */
    public static SampleType valueOf(int dataType) {
        switch (dataType) {
            case DataBuffer.TYPE_BYTE : return Byte;
            case DataBuffer.TYPE_SHORT : return Short;
            case DataBuffer.TYPE_USHORT : return UShort;
            case DataBuffer.TYPE_INT: return Integer;
            case DataBuffer.TYPE_FLOAT : return Float;
            case DataBuffer.TYPE_DOUBLE : return Double;
            case DataBuffer.TYPE_UNDEFINED: //fall through
            default: return null;
        }
    }
}
