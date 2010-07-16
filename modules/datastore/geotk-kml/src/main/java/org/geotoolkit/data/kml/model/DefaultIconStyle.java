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
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIconStyle extends DefaultAbstractColorStyle implements IconStyle {

    private double scale;
    private double heading;
    private BasicLink icon;
    private Vec2 hotSpot;

    /**
     * 
     */
    public DefaultIconStyle() {
        this.scale = DEF_SCALE;
        this.heading = DEF_HEADING;
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
     * @param heading
     * @param icon
     * @param hotSpot
     * @param iconStyleSimpleExtensions
     * @param iconStyleObjectExtensions
     */
    public DefaultIconStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions,
            List<AbstractObject> iconStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions,
                colorStyleObjectExtensions);
        this.scale = scale;
        this.heading = KmlUtilities.checkAngle360(heading);
        this.icon = icon;
        this.hotSpot = hotSpot;
        if (iconStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ICON_STYLE).addAll(iconStyleSimpleExtensions);
        }
        if (iconStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ICON_STYLE).addAll(iconStyleObjectExtensions);
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
    public double getHeading() {
        return this.heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BasicLink getIcon() {
        return this.icon;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getHotSpot() {
        return this.hotSpot;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(double heading) {
        this.heading = KmlUtilities.checkAngle360(heading);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIcon(BasicLink icon) {
        this.icon = icon;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHotSpot(Vec2 hotSpot) {
        this.hotSpot = hotSpot;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tIconStyleDefault : "
                + "\n\tscale : " + this.scale
                + "\n\theading : " + this.heading
                + "\n\ticon : " + this.icon
                + "\n\thotSpot : " + this.hotSpot;
        return resultat;
    }
}
