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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SubFieldDescription {
    
    private String tag;
    private FieldValueType type;
    private Integer length;

    public SubFieldDescription() {
    }

    public SubFieldDescription(FieldValueType type, Integer length) {
        this.type = type;
        this.length = length;
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
    public FieldValueType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(FieldValueType type) {
        this.type = type;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(" [").append(type);
        if(length!=null){
            sb.append("(").append(length).append(")");
        }
        sb.append("]");
        return sb.toString();
    }
    
}
