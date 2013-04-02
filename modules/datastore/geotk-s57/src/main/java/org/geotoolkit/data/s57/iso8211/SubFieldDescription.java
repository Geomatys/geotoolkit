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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SubFieldDescription {
    
    private String tag;
    private FieldDataType type;
    private int length;
    private boolean mandatory;

    public SubFieldDescription() {
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the type
     */
    public FieldDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(FieldDataType type) {
        this.type = type;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(" [").append(type);
        sb.append("(").append(length);
        sb.append("), mandatory:").append(mandatory);
        sb.append("]");
        return sb.toString();
    }
    
}
