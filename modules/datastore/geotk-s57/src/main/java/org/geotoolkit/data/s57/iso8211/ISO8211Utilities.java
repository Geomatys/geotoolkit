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
    
    public static char readChar(final byte[] buffer, int offset) {
        return (char) buffer[offset++];
    }
    
    public static short readUnsignedChar(final byte[] buffer, int offset){
        return ((short) (buffer[offset++] & 0xff));
    }

    public static short readUnsignedShort(final byte[] buffer, int offset){
        //LSBF
        short value = (short) (buffer[offset++] & 0xff);
        value += (buffer[offset++] & 0xff) << 8;
        return (value);
    }

    public static int readUnsignedInt(final byte[] buffer, int offset){
        //LSBF
        //VERIFIER QUE L'OPERATION & ENTRE DEUX OCTETS ENTRAINE
        //UN INTEGER.
        // & 0xff sert a rendre le signed byte (par defaut dans java)
        // en un unsigned byte.
        int value = (buffer[offset++] & 0xff);
        value += (buffer[offset++] & 0xff) << 8;
        value += (buffer[offset++] & 0xff) << 16;
        value += (buffer[offset++] & 0xff) << 24;
        return (value);
    }

    public static float readFloat(final byte[] buffer, int offset){
        //LSBF
        int value = readUnsignedInt(buffer, offset);
        return (Float.intBitsToFloat(value));
    }

    public static double readDouble(final byte[] buffer, int offset){
        //LSBF
        double value = buffer[offset++] & 0xff;
        value += (buffer[offset++] & 0xff) << 8;
        value += (buffer[offset++] & 0xff) << 16;
        value += (buffer[offset++] & 0xff) << 24;

        value += (buffer[offset++] & 0xff) << 32;
        value += (buffer[offset++] & 0xff) << 40;
        value += (buffer[offset++] & 0xff) << 48;
        value += (buffer[offset++] & 0xff) << 56;
        return (value);
    }
    
    //Lecture d'une chaine jusqu'au delimiter
    public static String readString(final byte[] buffer, int offset, char delimiter){
        int old_cursor = offset;

        //TraceLogger.println(offset);
        while ((char) (buffer[offset++]) != delimiter
                && offset < buffer.length);

        //TraceLogger.println(offset);
        String value;
        if ((offset - old_cursor - 1) > 0) {
            value = new String(buffer, old_cursor,
                    (offset - old_cursor - 1));
        } else {
            value = new String("");
        }

        return (value);
    }
    
    //Lecture d'une chaine jusqu'a la fin du tableau
    public static String readEndString(final byte[] buffer, int offset) {
        int old_cursor = offset;
        int taille = buffer.length;

        if (offset < taille) {
            while (offset < taille) {
                offset++;
            }
            String value =
                    new String(buffer, old_cursor, (offset - old_cursor));

            return (value);
        } else {
            return null;
        }
    }
    
    //Lecture d'une chaine de la taille demandee
    public static String readString(final byte[] buffer, int offset,int taille) {
        String value = new String(buffer, offset, taille);
        offset += taille;
        return (value);
    }

    //Lecture d'un double ascci jusqu'au delimiter
    public static double readStringDouble(final byte[] buffer, int offset,char delimiter) {
        return (Double.valueOf(readString(buffer,offset,delimiter)).doubleValue());
    }
    
    //Lecture d'un float ascci jusqu'au delimiter
    public static float readStringFloat(final byte[] buffer, int offset, char delimiter) {
        return (Float.valueOf(readString(buffer,offset,delimiter)).floatValue());
    }
    
    //Lecture d'un integer ascci jusqu'au delimiter
    public static int readStringInt(final byte[] buffer, int offset, char delimiter) {
        return (Integer.parseInt(readString(buffer,offset,delimiter).trim()));
    }

    //Lecture d'un float ascci sur une longueur donnee
    public static float readStringFloat(final byte[] buffer, int offset,int taille) {
        return (Float.valueOf(readString(buffer,offset,taille)).floatValue());
    }
    //Lecture d'un integer ascci sur une longueur donnee

    public static int readStringInt(final byte[] buffer, int offset,int taille) {
        return (Integer.parseInt(readString(buffer,offset,taille).trim()));
    }
    //Lecture d'un double ascci jusqu'au delimiter

    public static double readStringDouble(final byte[] buffer, int offset,int taille) {
        return (Double.valueOf(readString(buffer,offset,taille)).doubleValue());
    }
        
    
}
