/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import java.util.Date;
import org.opengis.temporal.Period;


/**
 * Temporary interface while we wait for the inclusion of a {@code TemporalFactory}
 * interface in GeoAPI.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public interface TemporalFactory {
    /**
     * Creates a period from a begin and end date.
     *
     * @param  begin The start time.
     * @param  end The end tile.
     * @return The given begin and end time in a period object.
     */
    Period getPeriod(Date begin, Date end);
}
