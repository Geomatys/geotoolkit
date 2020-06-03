/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.ext.tiledebug;

import java.util.Collections;
import java.util.Map;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.measure.Units;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TileDebugSymbolizerType")
@XmlRootElement(name="TileDebugSymbolizer",namespace="http://geotoolkit.org")
public class TileDebugSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String NAME = "TileDebug";
    @XmlElement(name = "Geometry")
    protected String geometry;

    public void setGeometry(final String value) {
        this.geometry = value;
    }

    @Override
    public Expression getGeometry() {
        return geometry==null ? null : GO2Utilities.FILTER_FACTORY.property(geometry);
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return Units.POINT;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    @Override
    public String getGeometryPropertyName() {
        return geometry;
    }

}
