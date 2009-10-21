/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.style;

import javax.measure.unit.Unit;

import org.geotoolkit.util.Utilities;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.StyleVisitor;
import org.opengis.style.TextSymbolizer;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of GeoAPI text symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTextSymbolizer implements TextSymbolizer{

    private final Expression label;
    
    private final Font font;
    
    private final LabelPlacement placement;
    
    private final Halo halo;
    
    private final Fill fill;
    
    private final Unit uom;
    
    private final String geom;
    
    private final String name;
    
    private final Description desc;
    
    /**
     * Create a default immutable Text symbolizer.
     * 
     * @param label : can not be null
     * @param font : if null will be replaced by default value.
     * @param placement : if null will be replaced by default value.
     * @param halo : if null will be replaced by default value.
     * @param fill : if null will be replaced by default value.
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    public DefaultTextSymbolizer(Expression label, Font font, LabelPlacement placement,
            Halo halo, Fill fill, Unit uom, String geom, String name, Description desc){
        if(label == null){
            throw new NullPointerException("Label can not be null");
        }
        
        this.label = label;
        this.font = (font == null) ? DEFAULT_FONT : font;
        this.placement = (placement == null) ? DEFAULT_POINTPLACEMENT : placement;
        this.halo = (halo == null) ? DEFAULT_HALO : halo;
        this.fill = (fill == null) ? DEFAULT_FILL : fill;
        this.uom = (uom == null) ? DEFAULT_UOM : uom;
        this.geom = geom;
        this.name = name;
        this.desc = (desc == null) ? DEFAULT_DESCRIPTION : desc;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLabel() {
         return label;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Font getFont() {
        return font;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LabelPlacement getLabelPlacement() {
        return placement;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Halo getHalo() {
        return halo;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Fill getFill() {
        return fill;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Unit getUnitOfMeasure() {
        return uom;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getGeometryPropertyName() {
        return geom;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Description getDescription() {
        return desc;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultTextSymbolizer other = (DefaultTextSymbolizer) obj;

        return this.desc.equals(other.desc)
                && this.fill.equals(other.fill)
                && this.font.equals(other.font)
                && Utilities.equals(this.geom,other.geom)
                && this.halo.equals(other.halo)
                && this.label.equals(other.label)
                && Utilities.equals(this.name, other.name)
                && this.placement.equals(other.placement)
                && this.uom.equals(other.uom);
                        

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash *= label.hashCode();
        hash *= font.hashCode();
        hash *= placement.hashCode();
        hash *= desc.hashCode();
        hash *= fill.hashCode();
        if(geom != null) hash *= geom.hashCode();
        hash *= halo.hashCode();
        if(name != null) hash *= name.hashCode();
        hash *= uom.hashCode();
        return hash;
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[TextSymbolizer : Label=");
        builder.append(label.toString());
        builder.append(" Font=");
        builder.append(font.toString());
        builder.append(" Halo=");
        builder.append(halo.toString());
        builder.append(" Placement=");
        builder.append(placement.toString());
        builder.append(']');
        return builder.toString();
    }
}
