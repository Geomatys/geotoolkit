/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ubjson;

/**
 * Constants from UBJSON specification.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://ubjson.org/
 */
final class UBJson {

    public static final byte NULL = 'Z';
    public static final byte NO_OP = 'N';
    public static final byte TRUE = 'T';
    public static final byte FALSE = 'F';
    public static final byte INT8 = 'i';
    public static final byte UINT8 = 'U';
    public static final byte INT16 = 'I';
    public static final byte INT32 = 'l';
    public static final byte INT64 = 'L';
    public static final byte FLOAT32 = 'd';
    public static final byte FLOAT64 = 'D';
    public static final byte HIGH_PRECISION_NUMBER = 'H';
    public static final byte CHAR = 'C';
    public static final byte STRING = 'S';

    public static final byte ARRAY_START = '[';
    public static final byte ARRAY_END = ']';
    public static final byte OBJECT_START = '{';
    public static final byte OBJECT_END = '}';

    public static final byte OPTIMIZED_TYPE = '$';
    public static final byte OPTIMIZED_SIZE = '#';

    private UBJson(){}
}
