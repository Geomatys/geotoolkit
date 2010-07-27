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
package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLineStyle extends DefaultAbstractColorStyle implements LineStyle {

    private double width;

    /**
     * 
     */
    public DefaultLineStyle() {
        this.width = DEF_WIDTH;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param width
     * @param lineStyleSimpleExtensions
     * @param lineStyleObjectExtensions
     */
    public DefaultLineStyle(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractSubStyleSimpleExtensions,
            List<Object> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            double width,
            List<SimpleTypeContainer> lineStyleSimpleExtensions,
            List<Object> lineStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions,
                colorStyleObjectExtensions);
        this.width = width;
        if (lineStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LINE_STYLE).addAll(lineStyleSimpleExtensions);
        }
        if (lineStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LINE_STYLE).addAll(lineStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getWidth() {
        return this.width;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tLineStyleDefault : "
                + "\n\twidth : " + this.width;
        return resultat;
    }
}
