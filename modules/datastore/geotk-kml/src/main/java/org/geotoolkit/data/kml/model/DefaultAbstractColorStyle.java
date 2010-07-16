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
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractColorStyle extends DefaultAbstractSubStyle implements AbstractColorStyle {

    protected Color color;
    protected ColorMode colorMode;

    /**
     * 
     */
    protected DefaultAbstractColorStyle() {
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
     */
    protected DefaultAbstractColorStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions);
        this.color = color;
        this.colorMode = colorMode;
        if (colorStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.COLOR_STYLE).addAll(colorStyleSimpleExtensions);
        }
        if (colorStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.COLOR_STYLE).addAll(colorStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public ColorMode getColorMode() {
        return this.colorMode;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractColorStyleDefault : "
                + "\n\tcolor : " + this.color
                + "\n\tcolorMode : " + this.colorMode;
        return resultat;
    }
}
