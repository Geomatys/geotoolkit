/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
    BYTE,

    /**
     * Data type {@link Short}.
     */
    SHORT,

    /**
     * Data type {@link Short}.
     */
    USHORT,

    /**
     * Data type {@link Integer}.
     */
    INTEGER,

    /**
     * Data type {@link Float}.
     */
    FLOAT,

    /**
     * Data type {@link Double}.
     */
    DOUBLE;

    /**
     * Mapping between {@link java.awt.image.SampleModel#getDataType()} and
     * {@link org.geotoolkit.image.internal.SampleType} enum.
     *
     * @param dataType integer from {@link java.awt.image.DataBuffer} constants
     * @return {@link org.geotoolkit.image.internal.SampleType} or null if type undefined.
     */
    public static SampleType valueOf(int dataType) {
        switch (dataType) {
            case DataBuffer.TYPE_BYTE     : return BYTE;
            case DataBuffer.TYPE_SHORT    : return SHORT;
            case DataBuffer.TYPE_USHORT   : return USHORT;
            case DataBuffer.TYPE_INT      : return INTEGER;
            case DataBuffer.TYPE_FLOAT    : return FLOAT;
            case DataBuffer.TYPE_DOUBLE   : return DOUBLE;
            case DataBuffer.TYPE_UNDEFINED: //fall through
            default: return null;
        }
    }

    /**
     * Mapping between {@link org.geotoolkit.image.internal.SampleType} enum and
     * {@link java.awt.image.SampleModel#getDataType()}.
     *
     * @param sampleType enum from {@link SampleType} constantes.
     * @return {@link DataBuffer#*} or {@link DataBuffer#TYPE_UNDEFINED} if unknow {@link SampleType}.
     */
    public static int valueOf(final SampleType sampleType) {
        switch (sampleType) {
            case BYTE    : return DataBuffer.TYPE_BYTE;
            case SHORT   : return DataBuffer.TYPE_SHORT;
            case USHORT  : return DataBuffer.TYPE_USHORT;
            case INTEGER : return DataBuffer.TYPE_INT;
            case FLOAT   : return DataBuffer.TYPE_FLOAT;
            case DOUBLE  : return DataBuffer.TYPE_DOUBLE;
            default      : return DataBuffer.TYPE_UNDEFINED;
        }
    }

    /**
     * Mapping between bits per sample and sample format with {@link SampleType}.<br><br>
     * expected bitPerSamples values : <br>
     * - 8(Byte)<br>
     * - 16(Short or UShort)<br>
     * - 32 (Int or Float)<br>
     * - 64 (Double)<br><br>
     *
     * expected sampleFormat values : <br>
     * - 1 for unsigned integer datas<br>
     * - 2 for signed integer datas<br>
     * - 3 for IEEE floating point<br><br>
     *
     * Example : <br>
     * UShort data : bitspersample = 16, sampleFormat = 1<br>
     * Float data  : bitspersample = 32, sampleformat = 3<br>
     *
     * Note : for bitpersample = 8, and bitspersample = 64, sampleFormat is ignored
     * the only available {@link SampleType} are respectively {@link SampleType#BYTE} and {@link SampleType#DOUBLE}.
     * Moreover for each unknow combination bitPerSample, sampleFormat this method return {@code null}.
     *
     *
     * @param bitPerSample bitPerSample bit number by sample.
     * @param sampleFormat integer to define floating, signed or unsigned type data.
     * @return {@link org.geotoolkit.image.internal.SampleType} or null if type undefined.
     */
    public static SampleType valueOf(final int bitPerSample, final int sampleFormat) {
        switch (bitPerSample) {
            case 8 : return BYTE;
            case 16 : {
                switch (sampleFormat) {
                    case 1 : return USHORT;
                    case 2 : return SHORT;
                    default : return null;
                }
            }
            case 32 : {
                return (sampleFormat == 3) ? FLOAT : INTEGER;
            }
            case 64: return DOUBLE;
            default: return null;
        }
    }
}
