/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.internal.referencing;

import java.util.List;
import java.util.Arrays;
import org.opengis.util.Record;
import org.opengis.util.RecordType;
import org.apache.sis.metadata.iso.quality.DefaultQuantitativeResult;


/**
 * Temporary record implementation will we wait for a real one.
 *
 * @deprecated To be replaced by a better implementation as soon as we can.
 */
@Deprecated
@SuppressWarnings("serial")
public final class SimpleRecord implements Record, java.io.Serializable {
    /**
     * Sets the quantitative value or values, content determined by the evaluation procedure used.
     *
     * @param result Where to store the values.
     * @param newValues The new values.
     */
    public static void setValues(final DefaultQuantitativeResult result, final double... newValues) {
        final List<Record> records;
        if (newValues == null) {
            records = null;
        } else {
            final Record[] data = new Record[newValues.length];
            for (int i=0; i<newValues.length; i++) {
                data[i] = new SimpleRecord(newValues[i]);
            }
            records = Arrays.asList(data);
        }
        result.setValues(records);
    }

    private final java.util.Map<org.opengis.util.MemberName, Object> map;

    public SimpleRecord(final double value) {
        map = java.util.Collections.singletonMap((org.opengis.util.MemberName) null, (Object) value);
    }

    @Override public RecordType getRecordType() {
        throw new UnsupportedOperationException();
    }

    @Override public java.util.Map<org.opengis.util.MemberName, Object> getAttributes() {
        return map;
    }

    @Override public Object locate(org.opengis.util.MemberName name) {
        throw new UnsupportedOperationException();
    }

    @Override public void set(org.opengis.util.MemberName name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean equals(final Object other) {
        if (other instanceof SimpleRecord) {
            return map.equals(((SimpleRecord) other).map);
        }
        return false;
    }

    @Override public int hashCode() {
        return map.hashCode();
    }
}
