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
import org.geotoolkit.feature.type.DefaultName;
import org.opengis.util.GenericName;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import java.util.regex.Pattern;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.expression.Expression;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class LabelLine implements MIFSymbolizer {

    public static final GenericName NAME = DefaultName.create("LABEL");
    public static final Pattern PATTERN = Pattern.compile(NAME.tip().toString(), Pattern.CASE_INSENSITIVE);

    private String type = "simple";
    private Coordinate point;

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
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public String getGeometryPropertyName() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Expression getGeometry() {
        return FactoryFinder.getFilterFactory(null).property(getGeometryPropertyName());
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
