/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.io.PrintWriter;

import org.opengis.referencing.IdentifiedObject;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.InternalUtilities;


/**
 * Implementation of {@link CachingAuthorityFactory#printCacheContent()}.
 * Instance of this class represent a single record in the cache content
 * to be listed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see CachingAuthorityFactory#printCacheContent()
 *
 * @since 3.17
 * @module
 */
final class CacheRecord implements Comparable<CacheRecord> {
    /**
     * The key-value pair, and the identity string representation of the value.
     */
    private final String key, value, identity;

    /**
     * The key numeric value, using for sorting purpose only.
     */
    private final int code;

    /**
     * Creates a new record for the given key-value pair.
     */
    private CacheRecord(final Object key, Object value) {
        identity = InternalUtilities.identity(value);
        String text;
        if (value instanceof Collection<?>) {
            final Iterator<?> it = ((Collection<?>) value).iterator();
            value = it.hasNext() ? it.next() : null;
        }
        if (value instanceof IdentifiedObject) {
            text = String.valueOf(((IdentifiedObject) value).getName());
        } else {
            text = null;
        }
        this.value = text;
        this.key = text = String.valueOf(key);
        text = text.substring(text.indexOf('[') + 1);
        final int i = text.indexOf(' ');
        if (i >= 1) {
            text = text.substring(0, i);
        }
        int code;
        try {
            code = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            code = Integer.MAX_VALUE;
        }
        this.code = code;
    }

    /**
     * Compares with the given record for ordering.
     */
    @Override
    public int compareTo(final CacheRecord other) {
        if (code < other.code) return -1;
        if (code > other.code) return +1;
        return key.compareTo(other.key);
    }

    /**
     * Implementation of the public {@link CachingAuthorityFactory#printCacheContent()} method.
     *
     * @param cache The cache.
     * @param out The output writer, or {@code null} for the standard output stream.
     */
    public static void printCacheContent(final Map<?,?> cache, PrintWriter out) {
        final List<CacheRecord> list = new ArrayList<CacheRecord>(cache.size() + 10);
        int codeLength = 0;
        int identityLength = 0;
        for (final Map.Entry<?,?> entry : cache.entrySet()) {
            final CacheRecord record = new CacheRecord(entry.getKey(), entry.getValue());
            int length = record.key.length();
            if (length > codeLength) {
                codeLength = length;
            }
            length = record.identity.length();
            if (length > identityLength) {
                identityLength = length;
            }
            list.add(record);
        }
        codeLength += 2;
        identityLength += 2;
        final CacheRecord[] records = list.toArray(new CacheRecord[list.size()]);
        Arrays.sort(records);
        if (out == null) {
            out = IOUtilities.standardPrintWriter();
        }
        for (final CacheRecord record : records) {
            out.print(record.key);
            out.print(CharSequences.spaces(codeLength - record.key.length()));
            out.print(record.identity);
            if (record.value != null) {
                out.print(CharSequences.spaces(identityLength - record.identity.length()));
                out.println(record.value);
            } else {
                out.println();
            }
        }
        out.flush();
    }
}
