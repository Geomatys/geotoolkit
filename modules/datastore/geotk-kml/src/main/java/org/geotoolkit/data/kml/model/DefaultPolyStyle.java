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
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPolyStyle extends DefaultAbstractColorStyle implements PolyStyle {

    private boolean fill;
    private boolean outline;

    /**
     * 
     */
    public DefaultPolyStyle() {
        this.fill = DEF_FILL;
        this.outline = DEF_OUTLINE;
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
     * @param fill
     * @param outline
     * @param polyStyleSimpleExtensions
     * @param polyStyleObjectExtensions
     */
    public DefaultPolyStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions,
            List<AbstractObject> polyStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions,
                colorStyleObjectExtensions);
        this.fill = fill;
        this.outline = outline;
        if (polyStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.POLY_STYLE).addAll(polyStyleSimpleExtensions);
        }
        if (polyStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.POLY_STYLE).addAll(polyStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getFill() {
        return this.fill;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getOutline() {
        return this.outline;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tPolyStyleDefault : "
                + "\n\tfill : " + this.fill
                + "\n\toutline : " + this.outline;
        return resultat;
    }
}
