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

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultStyle extends DefaultAbstractStyleSelector implements Style {

    private IconStyle iconStyle;
    private LabelStyle labelStyle;
    private LineStyle lineStyle;
    private PolyStyle polyStyle;
    private BalloonStyle balloonStyle;
    private ListStyle listStyle;

    /**
     * 
     */
    public DefaultStyle() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     * @param iconStyle
     * @param labelStyle
     * @param lineStyle
     * @param polyStyle
     * @param balloonStyle
     * @param listStyle
     * @param styleSimpleExtensions
     * @param styleObjectExtensions
     */
    public DefaultStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle,
            PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions,
                abstractStyleSelectorObjectExtensions);
        this.iconStyle = iconStyle;
        this.labelStyle = labelStyle;
        this.lineStyle = lineStyle;
        this.polyStyle = polyStyle;
        this.balloonStyle = balloonStyle;
        this.listStyle = listStyle;
        if (styleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.STYLE).addAll(styleSimpleExtensions);
        }
        if (styleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.STYLE).addAll(styleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IconStyle getIconStyle() {
        return this.iconStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle getLabelStyle() {
        return this.labelStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineStyle getLineStyle() {
        return this.lineStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle getPolyStyle() {
        return this.polyStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle getBalloonStyle() {
        return this.balloonStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListStyle getListStyle() {
        return this.listStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIconStyle(IconStyle iconStyle) {
        this.iconStyle = iconStyle;
    }

    ;

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLabelStyle(LabelStyle labelStyle) {
        this.labelStyle = labelStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolyStyle(PolyStyle polyStyle) {
        this.polyStyle = polyStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBalloonStyle(BalloonStyle baloonStyle) {
        this.balloonStyle = baloonStyle;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListStyle(ListStyle listStyle) {
        this.listStyle = listStyle;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tStyleDefault : "
                + "\n\ticonStyle : " + this.iconStyle
                + "\n\tlabelStyle : " + this.labelStyle
                + "\n\tlineStyle : " + this.lineStyle
                + "\n\tpolyStyle : " + this.polyStyle
                + "\n\tballoonStyle : " + this.balloonStyle
                + "\n\tlistStyle : " + this.listStyle;
        return resultat;
    }
}
