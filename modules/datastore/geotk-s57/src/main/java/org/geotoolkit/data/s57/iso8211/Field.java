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
package org.geotoolkit.data.s57.iso8211;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Field {
    
    private final List<Field> subFields = new ArrayList<Field>();
    private final FieldDescription type;
    private final byte[] value;

    public Field(FieldDescription type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the type
     */
    public FieldDescription getType() {
        return type;
    }

    /**
     * @return the value
     */
    public byte[] getValue() {
        return value;
    }

    public List<Field> getSubFields() {
        return subFields;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Field");
        return sb.toString();
    }
    
}
