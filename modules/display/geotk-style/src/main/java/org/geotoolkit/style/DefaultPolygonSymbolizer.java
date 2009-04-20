/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style;

import javax.measure.unit.Unit;

import org.geotoolkit.util.Utilities;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of GeoAPI Polygon symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultPolygonSymbolizer extends AbstractSymbolizer implements PolygonSymbolizer{

    private final Stroke stroke;
    
    private final Fill fill;
    
    private final Displacement disp;
    
    private final Expression offset;
        
    /**
     * Create a default immutable polygon symbolizer.
     * 
     * @param stroke : can be null
     * @param fill : if null will be replaced by default value.
     * @param disp : if null will be replaced by default value.
     * @param offset : if null or Expression.NIL will be replaced by default value.
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    public DefaultPolygonSymbolizer(Stroke stroke, Fill fill, Displacement disp,
            Expression offset, Unit uom, String geom, String name, Description desc){
        super(uom,geom,name,desc);
        this.stroke = stroke;
        this.fill = (fill == null) ? DEFAULT_FILL : fill;
        this.disp = (disp == null) ? DEFAULT_DISPLACEMENT : disp;
        this.offset = (offset == null) ? DEFAULT_POLYGON_OFFSET : offset;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Stroke getStroke() {
        return stroke;
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
    public Displacement getDisplacement() {
        return disp;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getPerpendicularOffset() {
        return offset;
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

        DefaultPolygonSymbolizer other = (DefaultPolygonSymbolizer) obj;

        return this.fill.equals(other.fill)
                && this.disp.equals(other.disp)
                && this.desc.equals(other.desc)
                && this.offset.equals(other.offset)
                && this.uom.equals(other.uom)
                && Utilities.equals(this.stroke, other.stroke)
                && Utilities.equals(this.geom, other.geom)
                && Utilities.equals(this.name, other.name);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = fill.hashCode();
        hash *= disp.hashCode();
        hash *= offset.hashCode();
        hash *= uom.hashCode();
        hash *= desc.hashCode();
        if(stroke != null) hash *= stroke.hashCode();
        if(geom != null) hash *= geom.hashCode();
        if(name != null) hash *= name.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Polygon Symbolizer : Fill=");
        builder.append(fill.toString());
        if(stroke != null){
            builder.append(" Stroke=");
            builder.append(stroke.toString());
        }
        builder.append(" Offset=");
        builder.append(offset.toString());
        builder.append(" Disp=");
        builder.append(disp.toString());
        builder.append(" Unit=");
        builder.append(uom.toString());
        
        if(geom != null){
            builder.append(" Geometry=");
            builder.append(geom.toString());
        }
        builder.append(']');
        return builder.toString();
    }
}
