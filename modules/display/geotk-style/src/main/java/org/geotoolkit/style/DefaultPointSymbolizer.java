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


import java.util.Objects;
import org.apache.sis.util.Classes;
import javax.measure.Unit;

import org.opengis.style.Description;
import org.opengis.style.Graphic;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.filter.Expression;

/**
 * Immutable implementation of Types point symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
    public DefaultPointSymbolizer(final Graphic graphic, final Unit uom, final Expression geom, final String name, final Description desc){
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
    public Object accept(final StyleVisitor visitor, final Object extraData) {
        return visitor.visit(this,extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {

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
                && Objects.equals(this.geom, other.geom)
                && Objects.equals(this.name, other.name);

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
        builder.append("Point Symbolizer : ");
        builder.append(Classes.getShortClassName(this));
        builder.append(" [");

        if(uom != null){
            builder.append(" Unit=");
            builder.append(uom);
        }
        if(geom != null){
            builder.append(" Geometry=");
            builder.append(geom);
        }
        builder.append(']');

        if(graphic != null){
            builder.append('\n');
            String sub = "\u2514\u2500\u2500" + graphic.toString(); //move text to the right
            sub = sub.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
            builder.append(sub);
        }

        return builder.toString();
    }
}
