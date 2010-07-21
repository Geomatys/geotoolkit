/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.index;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Field definition
 * 
 * @author Tommaso Nolli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataDefinition {

    private final Charset charset;
    private final List<Field> fields;

    public DataDefinition(String charset, Class ... fields) {

        if(charset == null){
            throw new IllegalArgumentException("Charset can not be null");
        }
        
        this.charset = Charset.forName(charset);

        if(fields == null){
            this.fields = new ArrayList<Field>();
        }else{
            this.fields = new ArrayList<Field>(fields.length);
            for(Class clazz : fields){
                addField(clazz);
            }

        }

    }

    public final boolean isValid() {
        return !this.fields.isEmpty();
    }

    public int getFieldsCount() {
        return this.fields.size();
    }

    public Field getField(int i) {
        return this.fields.get(i);
    }

    /**
     * Well known classes
     * 
     * <ul>
     * <li> Short </li>
     * <li> Integer </li>
     * <li> Long </li>
     * <li> Float </li>
     * <li> Double </li>
     * <li> Date </li>
     * </ul>
     * 
     * 
     * @param clazz
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public void addField(Class clazz) {
        if (clazz == Short.class) {
            this.fields.add(new Field(clazz, 2));
        } else if (clazz == Integer.class) {
            this.fields.add(new Field(clazz, 4));
        } else if (clazz == Long.class) {
            this.fields.add(new Field(clazz, 8));
        } else if (clazz == Float.class) {
            // TODO: Are you sure of 4 bytes?
            this.fields.add(new Field(clazz, 4));
        } else if (clazz == Double.class) {
            this.fields.add(new Field(clazz, 8));
        } else {
            throw new IllegalArgumentException("Unknow len of class " + clazz
                    + "use addField(int)");
        }
    }

    /**
     * For classes with unknown length; this values will be threated as
     * <code>String</code>s and truncated at the specified len
     * 
     * @param len
     */
    public void addField(int len) {
        this.fields.add(new Field(String.class, len));
    }

    /**
     * Character set values are encoded in.
     * 
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gets the max len of the data
     */
    public int getLen() {
        int len = 0;
        for(Field field : fields) {
            len += field.getLen();
        }
        return len;
    }

    /**
     * Gets the len of this field after the encoding, this method may be
     * different from getLen() only if exists strings in the definition
     * 
     */
    public int getEncodedLen() {
        int len = 0;
        for(Field field : fields) {
            len += field.getEncodedLen();
        }
        return len;
    }

    /**
     * Inner class for Data fields
     * 
     * @author Tommaso Nolli
     */
    public class Field {
        private final Class clazz;
        private final int len;

        public Field(Class clazz, int len) {
            this.clazz = clazz;
            this.len = len;
        }

        public Class getFieldClass() {
            return clazz;
        }

        public int getLen() {
            return len;
        }

        public int getEncodedLen() {
            int ret = this.len;

            if (this.clazz.equals(String.class)) {
                ret = (int) charset.newEncoder().maxBytesPerChar() * this.len;
            }

            return ret;
        }
    }
}
