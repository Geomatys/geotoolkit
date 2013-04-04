/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.iso8211;

import java.io.IOException;

/** 
 * Field controls data structure.
 * 
 * @author Johann Sorel (Geomatys)
 */
public enum FieldDataStructure {
    ELEMENTARY(0), 
    ARRAY(1), 
    VECTOR(2);
    
    private final byte value;

    private FieldDataStructure(int value) {
        this.value = (byte) value;
    }

    public static FieldDataStructure get(int value) throws IOException {
        switch (value) {
            case 0:
                return ELEMENTARY;
            case 1:
                return ARRAY;
            case 2:
                return VECTOR;
        }
        throw new IOException("Unknowned type : " + value);
    }

    public byte toValue() {
        return value;
    }
    
}
