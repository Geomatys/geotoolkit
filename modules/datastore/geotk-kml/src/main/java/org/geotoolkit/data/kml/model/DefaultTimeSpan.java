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

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTimeSpan extends DefaultAbstractTimePrimitive implements TimeSpan {

    private Calendar begin;
    private Calendar end;

    /**
     * 
     */
    public DefaultTimeSpan() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param begin
     * @param end
     * @param timeSpanSimpleExtensions
     * @param timeSpanObjectExtensions
     */
    public DefaultTimeSpan(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end,
            List<SimpleTypeContainer> timeSpanSimpleExtensions,
            List<Object> timeSpanObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions,
                abstractTimePrimitiveObjectExtensions);
        this.begin = begin;
        this.end = end;
        if (timeSpanSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_SPAN).addAll(timeSpanSimpleExtensions);
        }
        if (timeSpanObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_SPAN).addAll(timeSpanObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getBegin() {
        return this.begin;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getEnd() {
        return this.end;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBegin(Calendar begin) {
        this.begin = begin;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setEnd(Calendar end) {
        this.end = end;
    }
}
