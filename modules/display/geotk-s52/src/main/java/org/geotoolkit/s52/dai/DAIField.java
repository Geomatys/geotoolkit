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
package org.geotoolkit.s52.dai;

import java.io.IOException;
import java.util.Map;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.XInteger;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DAIField {

    protected static final int DELIM_1F = 0x1F;

    private final String code;
    private int subfieldLength;

    public DAIField(String code) {
        this.code = code;
    }

    /**
     * All fields have a unique code.
     * @return String, never null
     */
    public String getCode() {
        return code;
    }

    public abstract Map<String,Object> getSubFields();

    /**
     * Read subfield from given string.
     * @param str
     */
    public final void read(String str) throws IOException{
        if(str.startsWith(code)){
            str = str.substring(code.length());
            //subfields length = 5chars
            subfieldLength = Integer.valueOf(str.substring(0, 5).trim());
            str = str.substring(5);
        }else{
            throw new IOException("Uncorrect field string, does not start by field code : "+code);
        }
        readSubFields(str);
    }

    protected abstract void readSubFields(String str) throws IOException;

    protected int readIntBySize(String str, int[] offset, int size){
        final int i = XInteger.parseIntSigned(str, offset[0], offset[0]+size);
        offset[0]+=size;
        return i;
    }

    protected double readDoubleBySize(String str, int[] offset, int size){
        str = str.substring(offset[0], offset[0]+size);
        offset[0]+=size;
        return Double.parseDouble(str);
    }

    protected double readDoubleByDelim(String str, int[] offset, int delim){
        str = readStringByDelim(str, offset, delim);
        return Double.parseDouble(str);
    }

    protected String readStringBySize(String str, int[] offset, int size){
        str = str.substring(offset[0], offset[0]+size);
        offset[0] += size;
        return str;
    }

    protected String readStringByDelim(String str, int[] offset, int delim){
        return readStringByDelim(str, offset, delim, false);
    }

    /**
     * HACK : tolerance, boolean to allow no delimiter.
     */
    protected String readStringByDelim(String str, int[] offset, int delim, boolean allowNoDelim){
        int index = str.indexOf(delim, offset[0]);
        if(index==-1 && allowNoDelim){
            index = str.length();
        }
        str = str.substring(offset[0], index);
        offset[0] += index-offset[0]+1;
        return str;
    }

    /**
     * Create a new instance of this type of field.
     * @return DAIField
     */
    public DAIField newInstance(){
        try {
            return (DAIField)this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create a new instance of : "+this.getClass().getName());
        }
    }

    @Override
    public String toString() {
        return Trees.toString(getCode(), getSubFields().entrySet());
    }

}
