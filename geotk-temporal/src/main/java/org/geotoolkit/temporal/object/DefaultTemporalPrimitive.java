/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.opengis.temporal.TemporalOrder;
import org.opengis.temporal.TemporalPrimitive;

/**
 * An abstract class that represents a non-decomposed element of geometry or topology of time.
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
public abstract class DefaultTemporalPrimitive extends AbstractIdentifiedObject implements TemporalPrimitive, TemporalOrder, Temporal {
    public DefaultTemporalPrimitive(Map<String, ?> properties) throws IllegalArgumentException {
        super(properties);
    }

    protected DefaultTemporalPrimitive() {
        super(org.apache.sis.referencing.privy.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalGeometricPrimitive)
     */
    protected DefaultTemporalPrimitive(final TemporalPrimitive object) {
        super(object);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return false;   // TODO
    }

    @Override
    public boolean isSupported(TemporalField field) {
        return false;   // TODO
    }

    @Override
    public long getLong(TemporalField field) {
         throw new UnsupportedTemporalTypeException("Not yet implemented.");
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
         throw new UnsupportedTemporalTypeException("Not yet implemented.");
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
         throw new UnsupportedTemporalTypeException("Not yet implemented.");
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
         throw new UnsupportedTemporalTypeException("Not yet implemented.");
    }
}
