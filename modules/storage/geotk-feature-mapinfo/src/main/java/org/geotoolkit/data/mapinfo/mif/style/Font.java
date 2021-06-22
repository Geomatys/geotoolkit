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

import org.opengis.util.GenericName;
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.Unit;
import java.util.regex.Pattern;
import org.apache.sis.measure.Units;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.filter.Expression;

/**
 * Java representation of MIF-MID font style.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class Font implements MIFSymbolizer {

    public static final GenericName NAME = Names.createLocalName("style-rule", ":", "FONT");

    public static final Pattern PATTERN = Pattern.compile(NAME.tip().toString()+"(\\s*\\([^\\)]+\\))?", Pattern.CASE_INSENSITIVE);

    private String fontName = null;
    private int fontStyle = 0;
    private int fontColorCode = 0;
    private int backColorCode = -1;

    public Font(String ftName, int ftStyle, int ftColor) {
        fontName = ftName;
        fontStyle = ftStyle;
        fontColorCode = ftColor;
    }

    public Font(String ftName, int ftStyle, int ftColor, int backColor) {
        fontName = ftName;
        fontStyle = ftStyle;
        fontColorCode = ftColor;
        backColorCode = backColor;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public int getFontColorCode() {
        return fontColorCode;
    }

    public int getBackColorCode() {
        return backColorCode;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setFontColorCode(int fontColorCode) {
        this.fontColorCode = fontColorCode;
    }

    public void setBackColorCode(int backColorCode) {
        this.backColorCode = backColorCode;
    }

    @Override
    public String toMIFText() {
        return NAME.tip().toString()+"("+fontName+","+fontStyle+","+fontColorCode+((backColorCode>=0)? ","+backColorCode : "")+")";
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
