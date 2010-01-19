/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.Schema;


/**
 * Implementation of Schema.
 *
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public class DefaultSchema implements Schema {

    private final HashMap<Name, AttributeType> contents = new HashMap();
    private final String uri;

    /** Schema constructed w/ respect to provided URI */
    public DefaultSchema(final String uri) {
        super();
        this.uri = uri;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> keySet() {
        return contents.keySet();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        return contents.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean containsKey(final Object key) {
        return contents.containsKey(key);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean containsValue(final Object value) {
        return contents.containsValue(value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType get(final Object key) {
        return contents.get(key);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType put(final Name name, final AttributeType type) {
        if (!(name instanceof Name)) {
            throw new IllegalArgumentException("Please use a Name");
        }
        final Name n = (Name) name;
        if (!(n.getURI().startsWith(uri.toString()))) {
            throw new IllegalArgumentException("Provided name was not in schema:" + uri);
        }
        if (!(type instanceof AttributeType)) {
            throw new IllegalArgumentException("Please use an AttributeType");
        }
        final AttributeType t = (AttributeType) type;

        return contents.put(n, t);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType remove(final Object key) {
        return contents.remove(key);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void putAll(final Map<? extends Name, ? extends AttributeType> t) {
        contents.putAll(t);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        contents.clear();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<AttributeType> values() {
        return contents.values();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Entry<Name, AttributeType>> entrySet() {
        return contents.entrySet();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return contents.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        return contents.equals(obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return contents.toString();
    }

    @Override
    public String getURI() {
        return uri;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void add(final AttributeType type) {
        put(type.getName(), type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema profile(final Set<Name> profile) {
        return new DefaultProfile(this, profile);
    }
}
