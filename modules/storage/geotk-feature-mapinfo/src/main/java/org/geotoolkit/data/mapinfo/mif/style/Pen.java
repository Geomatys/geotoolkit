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

import org.geotoolkit.data.mapinfo.mif.geometry.MIFLineBuilder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.style.DefaultDescription;
import org.opengis.util.GenericName;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.Unit;
import java.awt.*;
import java.util.regex.Pattern;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public class Pen implements MIFSymbolizer, LineSymbolizer {

    public static final GenericName NAME = NamesExt.create("PEN");

    public static final Pattern PEN_PATTERN = Pattern.compile(NAME.tip().toString()+"(\\s*\\([^\\)]+\\))?", Pattern.CASE_INSENSITIVE);

    private int widthCode;
    private int pattern;
    private int colorCode;

    private transient Stroke stroke = null;
    private transient Expression perpendicularOffset = null;
    private transient Graphics2D graphic =null;
    private String geometryName= MIFLineBuilder.NAME.tip().toString();

    public Pen(int widthCode, int pattern, int colorCode) {
        this.widthCode = widthCode;
        this.pattern = pattern;
        this.colorCode = colorCode;
    }

    public int getWidthCode() {
        return widthCode;
    }

    public int getPattern() {
        return pattern;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setWidthCode(int widthCode) {
        this.widthCode = widthCode;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setPerpendicularOffset(Expression perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public void setGraphic(Graphics2D graphic) {
        this.graphic = graphic;
    }

    public void setGeometryName(String geometryName) {
        this.geometryName = geometryName;
    }

    @Override
    public Expression getGeometry() {
        return MIFUtils.FF.property(getGeometryPropertyName());
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public Expression getPerpendicularOffset() {
        return perpendicularOffset;
    }

    @Override
    public String toMIFText() {
        return NAME.tip().toString()+"("+widthCode+","+pattern+","+colorCode+")";
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return null;
    }

    @Override
    public String getGeometryPropertyName() {
        return geometryName;
    }

    @Override
    public String getName() {
        return NAME.tip().toString();
    }

    @Override
    public Description getDescription() {
        return new DefaultDescription(new SimpleInternationalString(NAME.tip().toString()),new SimpleInternationalString(toMIFText()));
    }

    @Override
    public Object accept(StyleVisitor styleVisitor, Object o) {
        return styleVisitor.visit(this, o);
    }

    @Override
    public String toString() {
        return toMIFText();
    }
}
