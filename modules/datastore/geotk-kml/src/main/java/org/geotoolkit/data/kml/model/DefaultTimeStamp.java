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
 * @module pending
 */
public class DefaultTimeStamp extends DefaultAbstractTimePrimitive implements TimeStamp {

    private Calendar when;

    /**
     * 
     */
    public DefaultTimeStamp() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param when
     * @param timeStampSimpleExtensions
     * @param timeStampObjectExtensions
     */
    public DefaultTimeStamp(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar when,
            List<SimpleTypeContainer> timeStampSimpleExtensions,
            List<Object> timeStampObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions,
                abstractTimePrimitiveObjectExtensions);
        this.when = when;
        if (timeStampSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_STAMP).addAll(timeStampSimpleExtensions);
        }
        if (timeStampObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_STAMP).addAll(timeStampObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getWhen() {
        return this.when;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWhen(Calendar when) {
        this.when = when;
    }
}
