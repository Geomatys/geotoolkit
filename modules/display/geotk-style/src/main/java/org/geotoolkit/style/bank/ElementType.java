/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.bank;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public enum ElementType {
    GROUP(0),
    STYLE(1),
    FEATURE_TYPE_STYLE(2),
    RULE(3),
    SYMBOLIZER(4);

    private final int level;
    private ElementType(int level) {
        this.level = level;
    }

    public boolean isContainerFor(ElementType candidate){
        return this.level < candidate.level;
    }

    
}
