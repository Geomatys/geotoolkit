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

import java.io.DataOutput;
import java.util.Arrays;
import static org.geotoolkit.data.iso8211.FieldValueType.BINARY;
import static org.geotoolkit.data.iso8211.FieldValueType.INTEGER;
import static org.geotoolkit.data.iso8211.FieldValueType.LE_INTEGER_SIGNED;
import static org.geotoolkit.data.iso8211.FieldValueType.LE_INTEGER_UNSIGNED;
import static org.geotoolkit.data.iso8211.FieldValueType.LOGICAL;
import static org.geotoolkit.data.iso8211.FieldValueType.LE_REAL;
import static org.geotoolkit.data.iso8211.FieldValueType.REAL_FIXED;
import static org.geotoolkit.data.iso8211.FieldValueType.REAL_FLOAT;
import static org.geotoolkit.data.iso8211.FieldValueType.TEXT;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SubField {
    
    private final SubFieldDescription type;
    private Object value;

    public SubField(SubFieldDescription type) {
        this.type = type;
    }

    public SubFieldDescription getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /** only the value to string */
    public String toStringPlain() {
        return String.valueOf(value);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(type.getTag()).append(" : ").append(value);
        return sb.toString();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // IO operations ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Read subfield value from buffer.
     * @param buffer
     * @param offset
     * @return number of bytes read
     */
    public int readValue(byte[] buffer, int offset){
        Integer length = type.getLength();
        boolean endMarked = false;
        if(length==null){
            endMarked=true;
            //find the end
            length = 0;
            for(int i=offset;i<buffer.length;i++){
                if(   ISO8211Constants.FEND == buffer[i] 
                   || ISO8211Constants.SFEND == buffer[i]){
                    break;
                }
                length++;
            }
        }
        
        //clip size
        if(type.getType() == BINARY){
            length = length/8;
        }
        
        switch(type.getType()){
            case TEXT:
                value = new String(Arrays.copyOfRange(buffer, offset, offset+length));
                break;
            case INTEGER:
                value = ISO8211Utilities.readSignedInteger(buffer, offset, length);
                break;
            case REAL_FIXED:
                value = ISO8211Utilities.readUnsignedInteger(buffer, offset, length);
                break;
            case REAL_FLOAT:
                value = ISO8211Utilities.readReal(buffer, offset, length);
                break;
            case LOGICAL:
                value = (buffer[offset]!=0);
                break;
            case LE_INTEGER_UNSIGNED:
                value = ISO8211Utilities.readUnsignedInteger(buffer, offset, length);
                break;
            case LE_INTEGER_SIGNED:
                value = ISO8211Utilities.readSignedInteger(buffer, offset, length);
                break;
            case LE_REAL:
                value = ISO8211Utilities.readReal(buffer, offset,length);
                break;
            case BINARY:
                value = Arrays.copyOfRange(buffer, offset, offset+length);
                break;
        }
        
        if(endMarked)length++;
        return length;
    }
    
    /**
     * 
     * @param out 
     * @return int : number of bytes written
     */
    public int writeValue(final DataOutput out){
        throw new RuntimeException("No supported yet.");
    }
    
}
