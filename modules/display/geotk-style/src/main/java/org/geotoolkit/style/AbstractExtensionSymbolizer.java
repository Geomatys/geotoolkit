/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style;

import java.util.Collections;
import java.util.Map;
import javax.measure.unit.Unit;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;

/**
 * Abstract implementation of GeoAPI extension symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractExtensionSymbolizer extends AbstractSymbolizer implements ExtensionSymbolizer{

    public AbstractExtensionSymbolizer(Unit uom, String geom, String name, Description desc){
        super(uom,geom,name,desc);
    }

    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.emptyMap();
    }

}
