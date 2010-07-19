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
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLabelStyle extends DefaultAbstractColorStyle implements LabelStyle {

    private double scale;

    /**
     *
     */
    public DefaultLabelStyle() {
        this.scale = DEF_SCALE;
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
     * @param scale
     * @param labelStyleSimpleExtensions
     * @param labelStyleObjectExtensions
     */
    public DefaultLabelStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions,
            List<AbstractObject> labelStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions,
                colorStyleObjectExtensions);
        this.scale = scale;
        if (labelStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.LABEL_STYLE).addAll(labelStyleSimpleExtensions);
        }
        if (labelStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.LABEL_STYLE).addAll(labelStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getScale() {
        return this.scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tLabelStyleDefault : "
                + "\n\tscale : " + this.scale;
        return resultat;
    }
}
