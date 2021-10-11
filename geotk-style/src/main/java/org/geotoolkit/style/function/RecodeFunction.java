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
package org.geotoolkit.style.function;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.filter.AbstractExpression;

import org.geotoolkit.filter.capability.FunctionName;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.util.ScopedName;

/**
 * Implementation of "Recode" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a colour map
 * <li>Literal: lookup value
 * <li>Literal: MapItem : data 1
 * <li>Literal: MapItem : value 1
 * <li>Literal: MapItem : data 2
 * <li>Literal: MapItem : value 2
 * </ol>
 * In reality any expression will do.
 *
 * @author Johann Sorel (Geomatys)
 */
public class RecodeFunction extends AbstractExpression implements Recode {

    /**
     * Use as a PropertyName when defining a color map.
     * The "Raterdata" is expected to apply to only a single band;
     */
    public static final String RASTER_DATA = "Rasterdata";

    private final List<MapItem> items;
    private final Literal fallback;
    private final Expression lookup;

    /**
     * Make the instance of FunctionName available in
     * a consistent spot.
     */
    public static final FunctionName NAME = new FunctionName("Recode", Arrays.asList(
            "LookupValue",
            "Data 1", "Value 1",
            "Data 2", "Value 2"),
            -2);    // indicating unbounded, 2 minimum

    public RecodeFunction(final List<MapItem> items, final Expression lookup, final Literal fallback){
        this.items = items;
        this.lookup = lookup;
        this.fallback = fallback;
    }

    @Override
    public Expression getLookupValue() {
        return lookup;
    }

    @Override
    public List<MapItem> getMapItems() {
        return items;
    }

    @Override
    public ScopedName getFunctionName() {
        return createName("Recode");
    }

    @Override
    public List<Expression<Object,?>> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public Object apply(final Object object) {
        final Expression lookupExp = lookup;

        if(object instanceof Image){
            //todo handle recode with lookup
            return object;
        }else{
            throw new IllegalArgumentException("unexpected type : " + object + ", need Image.");
        }
    }

    public Literal getFallbackValue() {
        return fallback;
    }
}
