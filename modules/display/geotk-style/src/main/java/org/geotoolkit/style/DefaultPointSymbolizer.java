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

import org.opengis.style.Description;
import org.opengis.style.Graphic;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of GeoAPI point symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPointSymbolizer extends AbstractSymbolizer implements PointSymbolizer{

    private final Graphic graphic;
        
    /**
     * Create a default immutable Point symbolizer.
     * 
     * @param graphic : if null will be replaced by default value.
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    public DefaultPointSymbolizer(Graphic graphic, Unit uom, String geom, String name, Description desc){
        super(uom,geom,name,desc);
        this.graphic = (graphic == null) ? DEFAULT_GRAPHIC : graphic ;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Graphic getGraphic() {
        return graphic;
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

        DefaultPointSymbolizer other = (DefaultPointSymbolizer) obj;

        return this.graphic.equals(other.graphic)
                && this.desc.equals(other.desc)
                && this.uom.equals(other.uom)
                && Utilities.equals(this.geom, other.geom)
                && Utilities.equals(this.name, other.name);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = graphic.hashCode();
        hash *= uom.hashCode();
        hash *= desc.hashCode();
        if(geom != null) hash *= geom.hashCode();
        if(name != null) hash *= name.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Point Symbolizer : Graphic=");
        if(graphic != null){
            builder.append(graphic);
        }
        if(uom != null){
            builder.append(" Unit=");
            builder.append(uom);
        }
        if(geom != null){
            builder.append(" Geometry=");
            builder.append(geom);
        }
        builder.append(']');
        return builder.toString();
    }
}
