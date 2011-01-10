/*
 *    Puzzle GIS - Desktop GIS Platform
 *    http://puzzle-gis.codehaus.org
 *
 *    (C) 2007-2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.shapefile;

/**
 * A single field for a shapefile creation.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
class Field {
    private String name = "name";
    private FieldType type = FieldType.STRING;

    String getName() {
        return name;
    }

    void setName(final String name) {
        this.name = name;
    }

    FieldType getType() {
        return type;
    }

    void setType(final FieldType type) {
        this.type = type;
    }
    
}
