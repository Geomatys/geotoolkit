/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.display3d.style;

import java.util.Collections;
import java.util.Map;
import javax.measure.unit.Unit;

import org.geotoolkit.style.AbstractSymbolizer;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ExtrudedSymbolizer3D extends AbstractSymbolizer implements Symbolizer3D{

    private static final String NAME = "Extruded3D";

    private final Expression height;

    public ExtrudedSymbolizer3D(Unit uom, String geom, String name, Description desc, Expression height) {
        super(uom, geom, name, desc);
        this.height = height;
    }

    public Expression getHeight() {
        return height;
    }

    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public String getExtensionName() {
        return name;
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.emptyMap();
    }

}
