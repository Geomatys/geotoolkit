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
package org.geotoolkit.data.mapinfo.mif.style;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.Collections;
import org.opengis.util.GenericName;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.Unit;
import java.util.regex.Pattern;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.measure.Units;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.filter.expression.Expression;

/**
 * Java representation of MIF-MID label style.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class LabelLine implements MIFSymbolizer {

    public static final GenericName NAME = Names.createLocalName("style-rule", ":", "LABEL");
    public static final AttributeType LABEL = new DefaultAttributeType(Collections.singletonMap("name", LabelLine.NAME), LabelLine.class, 1, 1, null);

    public static final Pattern PATTERN = Pattern.compile(NAME.tip().toString(), Pattern.CASE_INSENSITIVE);

    private final String type;
    private final Coordinate point;

    public LabelLine(String lineType, Coordinate pt) {
        type = lineType;
        point = pt;
    }

    @Override
    public String toMIFText() {
        return NAME.tip().toString()+' '+type+' '+point.x+' '+point.y;
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return Units.POINT;
    }

    @Override
    public String getGeometryPropertyName() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Expression getGeometry() {
        return MIFUtils.FF.property(getGeometryPropertyName());
    }

    @Override
    public String getName() {
        return NAME.tip().toString();
    }

    @Override
    public Description getDescription() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Object accept(StyleVisitor styleVisitor, Object o) {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String toString() {
        return toMIFText();
    }
}
