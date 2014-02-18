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

import org.geotoolkit.data.mapinfo.mif.geometry.MIFPointBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.style.DefaultDescription;
import org.opengis.feature.type.Name;
import org.opengis.style.Description;
import org.opengis.style.Graphic;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.StyleVisitor;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import java.awt.*;
import java.util.regex.Pattern;
import org.apache.sis.util.iso.SimpleInternationalString;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public class Symbol implements MIFSymbolizer, PointSymbolizer {

    public static final Name NAME = new DefaultName("SYMBOL");

    public final static Pattern SYMBOL_PATTERN = Pattern.compile(NAME.getLocalPart()+"(\\s*\\([^\\)]+\\))?", Pattern.CASE_INSENSITIVE);

    private int shape  = 0;
    private int colorCode = 0;
    private int size = 0;

    private String geometryName= MIFPointBuilder.NAME.getLocalPart();
    private transient Graphics2D graphic;

    public Symbol() { }

    public Symbol(int shape, int colorCode, int size, String geometryName) {
        this.shape = shape;
        this.colorCode = colorCode;
        this.size = size;
        if(geometryName != null) {
            this.geometryName = geometryName;
        }
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setGraphic(Graphics2D graphic) {
        this.graphic = graphic;
    }

    public void setGeometryName(String geometryName) {
        this.geometryName = geometryName;
    }

    @Override
    public String toMIFText() {
        return NAME.getLocalPart()+"("+shape+","+colorCode+","+size+")";
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
        return NAME.getLocalPart();
    }

    @Override
    public Description getDescription() {
        return new DefaultDescription(new SimpleInternationalString(NAME.getLocalPart()),new SimpleInternationalString(toMIFText()));
    }

    @Override
    public Graphic getGraphic() {
        return null;
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
