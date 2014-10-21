/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
package org.geotoolkit.temporal.object;

import java.util.Map;
import org.opengis.temporal.TemporalTopologicalPrimitive;

/**
 * 
 * @author Remi Marechal (Geomatys).
 */
public class DefaultTemporalTopologicalPrimitive extends DefaultTemporalPrimitive implements TemporalTopologicalPrimitive {
    
    protected DefaultTemporalTopologicalPrimitive(final Map<String, ?> properties) {
        super(properties);
    }
    
    protected DefaultTemporalTopologicalPrimitive() {
        
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalTopologicalPrimitive)
     */
    protected DefaultTemporalTopologicalPrimitive(final TemporalTopologicalPrimitive object) {
        super(object);
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalTopologicalPrimitive}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalTopologicalPrimitive} instance is created using the
     *       {@linkplain #DefaultTemporalTopologicalPrimitive(TemporalTopologicalPrimitive) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalTopologicalPrimitive castOrCopy(final TemporalTopologicalPrimitive object) {
        if (object == null || object instanceof DefaultTemporalNode) {
            return (DefaultTemporalNode) object;
        }
        return new DefaultTemporalTopologicalPrimitive(object);
    }
}
