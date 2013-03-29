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
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Field {
    
    private String tag;
    private int length;
    private int position;
    private final List<Field> subFields = new ArrayList<Field>();

    public Field() {
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
     * @return the lenght
     */
    public int getLenght() {
        return length;
    }

    /**
     * @param length the lenght to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    public List<Field> getSubFields() {
        return subFields;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(tag);
        sb.append(" [length:").append(length);
        sb.append(",position:").append(position);
        sb.append(Trees.toString("]", subFields));
        return sb.toString();
    }
    
}
