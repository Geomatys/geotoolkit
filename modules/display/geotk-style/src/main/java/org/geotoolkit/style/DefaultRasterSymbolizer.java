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

import org.opengis.filter.expression.Expression;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI raster symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultRasterSymbolizer extends AbstractSymbolizer implements RasterSymbolizer{

    private final Expression opacity;
    
    private final ChannelSelection selection;
    
    private final OverlapBehavior overlap;
    
    private final ColorMap colorMap;
    
    private final ContrastEnhancement enhance;
    
    private final ShadedRelief relief;
    
    private final Symbolizer outline;
        
    /**
     * Create a default immutable Line symbolizer.
     * 
     * @param opacity : if null will be replaced by default value.
     * @param selection : if null will be replaced by default value.
     * @param overlap : if null will be replaced by default value.
     * @param colorMap : can be null
     * @param enchance : if null will be replaced by default value.
     * @param relief : if null will be replaced by default value.
     * @param outline : can be null
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    public DefaultRasterSymbolizer(Expression opacity,
            ChannelSelection selection, 
            OverlapBehavior overlap, 
            ColorMap colorMap, 
            ContrastEnhancement enhance,
            ShadedRelief relief,
            Symbolizer outline,
            Unit uom,
            String geom,
            String name,
            Description desc){
        super(uom,geom,name,desc);
        this.opacity = (opacity == null || opacity == NIL) ? DEFAULT_RASTER_OPACITY : opacity;
        this.selection = (selection == null) ? DEFAULT_RASTER_CHANNEL_GRAY : selection;
        this.overlap = (overlap == null) ? DEFAULT_RASTER_OVERLAP : overlap;
        this.colorMap = colorMap;
        this.enhance = (enhance == null) ? DEFAULT_CONTRAST_ENHANCEMENT : enhance;
        this.relief = (relief == null) ? DEFAULT_SHADED_RELIEF : relief;
        this.outline = outline;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getOpacity() {
        return opacity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ChannelSelection getChannelSelection() {
        return selection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OverlapBehavior getOverlapBehavior() {
        return overlap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ColorMap getColorMap() {
        return colorMap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ContrastEnhancement getContrastEnhancement() {
        return enhance;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ShadedRelief getShadedRelief() {
        return relief;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Symbolizer getImageOutline() {
        return outline;
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

        DefaultRasterSymbolizer other = (DefaultRasterSymbolizer) obj;

        return true;
//        return Utilities.equals(this.colorMap ,other.colorMap)
//                && this.desc.equals(other.desc)
//                && this.enhance.equals(other.enhance)
//                && Utilities.equals(this.geom,other.geom)
//                && Utilities.equals(this.name,other.name)
//                && this.opacity.equals(other.opacity)
//                && Utilities.equals(this.outline,other.outline)
//                && Utilities.equals(this.outline,other.outline)
//                && this.overlap.equals(other.overlap)
//                && Utilities.equals(this.relief,other.relief)
//                && Utilities.equals(this.selection,other.selection)
//                && this.uom.equals(other.uom);
                        

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(colorMap != null) hash *= colorMap.hashCode();
        hash *= desc.hashCode();
        if(enhance != null) hash *= enhance.hashCode();
        if(geom != null) hash *= geom.hashCode();
        if(name != null) hash *= name.hashCode();
        hash *= opacity.hashCode();
        if(outline != null) hash *= outline.hashCode();
        hash *= overlap.hashCode();
        if(relief != null) hash *= relief.hashCode();
        if(selection != null) hash *= selection.hashCode();
        hash *= uom.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[RasterSymbolizer : Opacity=");
        builder.append(opacity.toString());
        if(selection != null){
            builder.append(" Channels=");
            builder.append(selection.toString());
        }
        builder.append(']');
        return builder.toString();
    }
}
