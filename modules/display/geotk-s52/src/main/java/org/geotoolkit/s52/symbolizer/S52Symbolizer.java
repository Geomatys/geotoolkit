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
package org.geotoolkit.s52.symbolizer;

import java.util.Collections;
import java.util.Map;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
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
@XmlType(name = "S52SymbolizerType")
@XmlRootElement(name="S52Symbolizer",namespace="http://geotoolkit.org")
public class S52Symbolizer extends SymbolizerType implements ExtensionSymbolizer{

    @XmlTransient
    public static final String NAME = "S-52";
    @XmlTransient
    private static final Expression ALL = GO2Utilities.FILTER_FACTORY.property("*");
    @XmlTransient
    private final boolean differed;

    public S52Symbolizer() {
        this(true);
    }

    /**
     *
     * @param differed indicate is layer is painted now or waits for other possible S-52 layers.
     *                 default value is True.
     */
    public S52Symbolizer(boolean differed) {
        this.differed = differed;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    public boolean isDiffered() {
        return differed;
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.singletonMap("prop", ALL);
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return NonSI.PIXEL;
    }

    @Override
    public String getGeometryPropertyName() {
        return null;
    }

}
