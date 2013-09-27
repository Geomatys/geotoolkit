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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.io.TableAppender;
import org.geotoolkit.s52.dai.ColorDefinitionCIE;

/**
 * Color palette for S-52 symbology.
 *
 * S-52 Annex A Part I p.25 (3.1 The Colour Coding Scheme)
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52Palette {

    private final String name;
    private final Map<String, ColorDefinitionCIE> colorMap = new HashMap<>();

    public S52Palette(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getColorNames(){
        return colorMap.keySet();
    }

    public void addColor(ColorDefinitionCIE ccie) {
        colorMap.put(ccie.getTokenName(), ccie);
    }

    public ColorDefinitionCIE getColorDef(final String colorName){
        return colorMap.get(colorName);
    }

    public String getColorHexa(final String colorName) {
        final ColorDefinitionCIE ccie = colorMap.get(colorName);
        if(ccie == null) return null;
        return ccie.getColorHexa();
    }

    public Color getColor(final String colorName) {
        final ColorDefinitionCIE cie = colorMap.get(colorName);
        if(cie == null){
            S52Context.LOGGER.log(Level.WARNING, "no color for name : "+colorName);
            return new Color(0, 0, 0, 0);
        }
        return cie.getColor();
    }

    @Override
    public String toString() {
        final TableAppender writer = new TableAppender();
        writer.writeHorizontalSeparator();
        writer.append("key");
        writer.nextColumn();
        writer.append("color");
        writer.writeHorizontalSeparator();
        for(Entry<String,ColorDefinitionCIE> entry : colorMap.entrySet()){
            writer.nextLine();
            writer.append(entry.getKey());
            writer.nextColumn();
            writer.append(entry.getValue().getColorHexa());
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
