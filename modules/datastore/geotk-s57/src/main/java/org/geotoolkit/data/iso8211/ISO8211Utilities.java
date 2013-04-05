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

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.io.LEDataInputStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ISO8211Utilities {
    
    private ISO8211Utilities(){}
    
    public static String trimZeros(byte[] buffer, int start, int end){
        for(;start<end-1;start++){
            if('0' != buffer[start]) break;
        }
        return new String(Arrays.copyOfRange(buffer, start, end));
    }
    
    public static void expect(final DataInput ds, final char val) throws IOException{
        char c = (char)ds.readByte();
        if(val!=c)throw new IOException("Unexpected value : "+c+" was expecting : "+val);
    }
    
    public static void expect(final DataInput ds, final byte[] val) throws IOException{
        final byte[] buffer = new byte[val.length];        
        ds.readFully(buffer);
        if(!Arrays.equals(val, buffer))throw new IOException("Unexpected value : "+buffer+" was expecting : "+val);
    }
    
    //--------------------decodage du buffer---------------------------
    public static long readUnsignedInteger(final byte[] buffer, int offset, int length){
        if(length==1){
            return buffer[offset] & 0xFF;
        }else if(length==2){
            return LEDataInputStream.readUnsignedShort(buffer, offset);            
        }else if(length==4){
            return LEDataInputStream.readUnsignedInt(buffer, offset);            
        }else if(length==8){
            return LEDataInputStream.readLong(buffer, offset);            
        }else{
            throw new RuntimeException("size not in : 1,2,4,8");
        }
    }
    
    public static long readSignedInteger(final byte[] buffer, int offset, int length){
        if(length==1){
            return buffer[offset];
        }else if(length==2){
            return LEDataInputStream.readShort(buffer, offset);            
        }else if(length==4){
            return LEDataInputStream.readInt(buffer, offset);            
        }else if(length==8){
            return LEDataInputStream.readLong(buffer, offset);            
        }else{
            throw new RuntimeException("size not in : 1,2,4,8");
        }
    }
    
    public static double readReal(final byte[] buffer, int offset, int length){
         if(length==4){
            return LEDataInputStream.readFloat(buffer, offset);
        }else if(length==8){
            return LEDataInputStream.readDouble(buffer, offset); 
        }else{
            throw new RuntimeException("size not in : 4,8");
        }
    }
    
}
