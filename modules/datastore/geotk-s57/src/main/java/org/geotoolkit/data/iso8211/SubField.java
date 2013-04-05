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
        
        switch(type.getType()){
            case TEXT:
                if(length!=null){
                    value = new String(Arrays.copyOfRange(buffer, offset, offset+length));
                    return length;
                }else{
                    //find the end
                    if(length == null){
                        for(length=0;length+offset<buffer.length;length++){
                            if(   ISO8211Constants.FEND == buffer[length+offset] 
                               || ISO8211Constants.SFEND == buffer[length+offset]){
                                break;
                            }
                        }
                    }
                    value = new String(Arrays.copyOfRange(buffer, offset, offset+length));
                    return length+1; //+1 for the delimiter
                }
                
            case INTEGER:
                value = ISO8211Utilities.readSignedInteger(buffer, offset, 4);
                return 4;
            case REAL_FIXED:
                value = ISO8211Utilities.readUnsignedInteger(buffer, offset, 2);
                return 2;
            case REAL_FLOAT:
                value = ISO8211Utilities.readReal(buffer, offset, 4);
                return 4;
            case LOGICAL:
                value = (buffer[offset]!=0);
                return 1;
            case LE_INTEGER_UNSIGNED:
                value = ISO8211Utilities.readUnsignedInteger(buffer, offset, length);
                return length;
            case LE_INTEGER_SIGNED:
                value = ISO8211Utilities.readSignedInteger(buffer, offset, length);
                return length;
            case LE_REAL:
                value = ISO8211Utilities.readReal(buffer, offset,length);
                return length;
            case BINARY:
                value = Arrays.copyOfRange(buffer, offset, offset+(length/8)); //length is in bits
                return length/8; //length is in bits
        }
        return 0;
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
