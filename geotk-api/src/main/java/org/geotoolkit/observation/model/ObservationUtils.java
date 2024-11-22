/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.observation.model;

import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierSpace;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author glegal
 */
public class ObservationUtils {

    public static void setIdentifiers(Period p, String id) {
        setIdentifier(p.getBeginning(), id + "-st-time");
        setIdentifier(p.getEnding(), id + "-en-time");
        setIdentifier(p, id + "-time");
    }

    public static void setIdentifier(TemporalPrimitive t, String id) {
        ((IdentifiedObject) t).getIdentifierMap().putSpecialized(IdentifierSpace.ID, id);
    }
}
