/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gml.xml;

import java.time.Instant;
import java.util.Date;
import org.geotoolkit.temporal.object.InstantWrapper;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author rmarechal
 */
public interface GMLInstant extends TemporalGeometricPrimitive, InstantWrapper {

    public String getId();

    public default Date getDate() {
        AbstractTimePosition timePosition = getTimePosition();
        return (timePosition != null) ? timePosition.getDate() : null;
    }

    @Override
    public default Instant getInstant() {
        Date date = getDate();
        return (date != null) ? date.toInstant() : null;
    }

    public AbstractTimePosition getTimePosition();
}
