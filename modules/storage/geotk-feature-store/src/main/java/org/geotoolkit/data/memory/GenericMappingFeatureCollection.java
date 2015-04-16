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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.memory.mapping.DefaultFeatureMapper;
import org.geotoolkit.data.memory.mapping.FeatureMapper;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * Basic support for a FeatureCollection that moves attributs to a new type definition
 * using a mapping objet.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class GenericMappingFeatureCollection extends AbstractCollection<Feature> implements FeatureCollection {

    private final FeatureCollection original;
    private final FeatureType type;
    private final FeatureMapper mapper;

    public GenericMappingFeatureCollection(final FeatureCollection original, final FeatureType newType,
            final Map<PropertyDescriptor,List<PropertyDescriptor>> mapping,
            final Map<PropertyDescriptor,Object> defaults){
        this(original,
                new DefaultFeatureMapper(
                        original.getFeatureType(),
                        newType,
                        mapping,
                        defaults)
                );
    }

    public GenericMappingFeatureCollection(final FeatureCollection original, final FeatureMapper mapper){
        this.original = original;
        this.mapper = mapper;
        this.type = mapper.getTargetType();
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
    public Source getSource() {
        return original.getSource();
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
    public FeatureCollection subCollection(final Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Mapping feature collection is not made to allow sub query.");
    }

    @Override
    public FeatureIterator iterator() throws FeatureStoreRuntimeException {
        return iterator(null);
    }

    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return new GenericMappingFeatureIterator(original.iterator(), mapper);
    }

    @Override
    public void addStorageListener(final StorageListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeStorageListener(final StorageListener listener) {
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
    public void update(Feature feature) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public void update(final Filter filter, final AttributeDescriptor desc, final Object value) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public void update(final Filter filter, final Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public void remove(final Filter filter) throws DataStoreException {
        throw new DataStoreException("Not writable");
    }

    @Override
    public boolean add(final Feature e) {
        throw new FeatureStoreRuntimeException("Not writable");
    }

    @Override
    public boolean remove(final Object o) {
        throw new FeatureStoreRuntimeException("Not writable");
    }

    @Override
    public boolean addAll(final Collection<? extends Feature> clctn) {
        throw new FeatureStoreRuntimeException("Not writable");
    }

    @Override
    public boolean removeAll(final Collection<?> clctn) {
        throw new FeatureStoreRuntimeException("Not writable");
    }


    @Override
    public void clear() {
        throw new FeatureStoreRuntimeException("Not writable");
    }

}
