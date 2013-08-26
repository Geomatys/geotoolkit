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
package org.geotoolkit.s52;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.sis.io.TableAppender;

/**
 * Color palette for S-52 symbology.
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52Palette {

    private final String name;
    private final Map<String,String> colorMap = new HashMap<>();

    public S52Palette(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addColor(final String name, final String value) {
        colorMap.put(name, value);
    }

    public String getColor(final String colorName) {
        return colorMap.get(colorName);
    }

    @Override
    public String toString() {
        final TableAppender writer = new TableAppender();
        writer.writeHorizontalSeparator();
        writer.append("key");
        writer.nextColumn();
        writer.append("color");
        writer.writeHorizontalSeparator();
        for(Entry<String,String> entry : colorMap.entrySet()){
            writer.nextLine();
            writer.append(entry.getKey());
            writer.nextColumn();
            writer.append(entry.getValue());
        }

        writer.writeHorizontalSeparator();
        return name+"\n"+writer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final S52Palette other = (S52Palette) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }

}
