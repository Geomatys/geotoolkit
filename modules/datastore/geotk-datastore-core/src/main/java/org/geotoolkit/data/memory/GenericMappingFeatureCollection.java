/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.data.memory;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DefaultSubFeatureCollection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.SchemaException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * Basic support for a FeatureCollection that moves attributs to a new type definition
 * using a mapping objet.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class GenericMappingFeatureCollection<F extends Feature> extends AbstractCollection<F> implements FeatureCollection<F> {

    private final FeatureCollection original;
    private final FeatureType type;
    private final Map<PropertyDescriptor,Object> defaults;
    private final Map<PropertyDescriptor,List<PropertyDescriptor>> mapping;

    private GenericMappingFeatureCollection(FeatureCollection original, FeatureType newType, 
            Map<PropertyDescriptor,List<PropertyDescriptor>> mapping,
            Map<PropertyDescriptor,Object> defaults){
        this.original = original;
        this.mapping = mapping;
        this.defaults = defaults;
        this.type = newType;
    }

    @Override
    public String getID() {
        return original.getID();
    }

    @Override
    public Session getSession() {
        return original.getSession();
    }

    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return original.getEnvelope();
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public FeatureCollection<F> subCollection(Query query) throws DataStoreException {
        try {
            return new DefaultSubFeatureCollection<F>(this, query);
        } catch (SchemaException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public FeatureIterator<F> iterator() throws DataStoreRuntimeException {
        return new GenericMappingFeatureIterator<F>(original.iterator(), original.getFeatureType(), type, mapping, defaults);
    }

    @Override
    public void addStorageListener(StorageListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeStorageListener(StorageListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    ////////////////////////////////////////////////////////////////////////////
    // not writable ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void update(Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public boolean add(F e) {
        throw new DataStoreRuntimeException("Not writable");
    }

    @Override
    public boolean remove(Object o) {
        throw new DataStoreRuntimeException("Not writable");
    }

    @Override
    public boolean addAll(Collection<? extends F> clctn) {
        throw new DataStoreRuntimeException("Not writable");
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        throw new DataStoreRuntimeException("Not writable");
    }


    @Override
    public void clear() {
        throw new DataStoreRuntimeException("Not writable");
    }


}
