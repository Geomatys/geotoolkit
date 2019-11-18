/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.feature;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.memory.WrapFeatureIterator;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.session.DefaultSession;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;

/**
 *
 * @author guilhem
 */
public class FeatureSetWrapper  extends AbstractCollection<Feature> implements FeatureCollection {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data");

    private final FeatureSet featureSet;
    private final DataStore store;

    public FeatureSetWrapper(FeatureSet featureSet, DataStore store) {
        this.featureSet = featureSet;
        this.store = store;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        try {
            return featureSet.getIdentifier();
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public FeatureType getType() {
        try {
            return featureSet.getType();
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return featureSet.getEnvelope();
    }

    @Override
    public FeatureCollection subset(Query query) throws DataStoreException {
        return new FeatureSetWrapper(featureSet.subset(query), store) {

            // override because if we call getIdentifier on a subset, it return empty
            @Override
            public Optional<GenericName> getIdentifier() {
                try {
                    return featureSet.getIdentifier();
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }

        };
    }

    @Override
    public FeatureIterator iterator() throws FeatureStoreRuntimeException {
        try {
            return new WrapFeatureIterator(featureSet.features(false).iterator()){};
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public FeatureIterator iterator(Hints hints) throws FeatureStoreRuntimeException {
        // what to do with hints ?
        try {
            return new WrapFeatureIterator(featureSet.features(false).iterator()){};
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        featureSet.addListener(eventType, listener);
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        featureSet.removeListener(eventType, listener);
    }

    @Override
    public boolean isWritable() {
        return featureSet instanceof WritableFeatureSet;
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            wfs.updateType(newType);
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            wfs.add(features);
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    @Override
    public boolean add(Feature e) {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            try {
                wfs.add(Collections.singleton(e).iterator());
                return true;
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        } else {
            throw new FeatureStoreRuntimeException("The feature set is not writable.");
        }
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            wfs.replaceIf(filter, updater);
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    @Override
    public void update(Feature feature) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            Object name = feature.getPropertyValue(AttributeConvention.IDENTIFIER);
            if (name != null) {
                wfs.replaceIf((f)-> name.equals(f.getProperty(AttributeConvention.IDENTIFIER)), UnaryOperator.identity());
            } else {
                throw new DataStoreException("The feature has no identifier.");
            }
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    @Override
    public void update(Filter filter, Map<String, ?> values) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            wfs.replaceIf((f)-> filter.evaluate(f), (f) -> updateFeatureProperty(f, values));
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    private static Feature updateFeatureProperty(Feature feat, Map<String, ?> values) {
        for (Entry<String, ?> entry : values.entrySet()) {
            feat.setPropertyValue(entry.getKey(), entry.getValue());
        }
        return feat;
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            wfs.removeIf((f)-> filter.evaluate(f));
        } else {
            throw new DataStoreException("The feature set is not writable.");
        }
    }

    @Override
    public boolean remove(Object o) {
        if (featureSet instanceof WritableFeatureSet) {
            WritableFeatureSet wfs = (WritableFeatureSet) featureSet;
            if(o instanceof Feature){
                Object name = ((Feature)o).getPropertyValue(AttributeConvention.IDENTIFIER);
                if (name != null) {
                    try {
                        wfs.removeIf((f)-> name.equals(f.getProperty(AttributeConvention.IDENTIFIER)));
                    } catch (DataStoreException ex) {
                        throw new FeatureStoreRuntimeException(ex);
                    }
                    return true;
                } else {
                    throw new FeatureStoreRuntimeException("Unable to remove a feature with no identifier.");
                }
            } else {
                //trying to remove an object which is not a feature
                //it has no effect
                //should we be strict and raise an error ? or log it ?
            }
        } else {
            throw new FeatureStoreRuntimeException("The feature set is not writable.");
        }
        return false;
    }

    @Override
    public Session getSession() {
        return new DefaultSession(new FeatureStoreWrapper(store.getOpenParameters().orElse(null), featureSet), false);
    }

    @Override
    public int size() {
        return (int) FeatureStoreUtilities.calculateCount(iterator());
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        final FeatureIterator e = iterator();
        try{
            if (o==null) {
                while (e.hasNext())
                    if (e.next()==null)
                        return true;
            } else {
                while (e.hasNext())
                    if (o.equals(e.next()))
                        return true;
            }
            return false;
        }finally{
            e.close();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        final Iterator<?> e = c.iterator();
        try{
            while (e.hasNext())
                if (!contains(e.next()))
                    return false;
            return true;
        }finally{
            if(e instanceof CloseableIterator){
                ((CloseableIterator)e).close();
            }
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
    final FeatureIterator e = iterator();
        try{
            while (e.hasNext()) {
                if (!c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }finally{
            e.close();
        }
    }

    @Override
    public void clear() {
        FeatureIterator e = iterator();
        try{
            while (e.hasNext()) {
                e.next();
                e.remove();
            }
        }finally{
            e.close();
        }

    }

    @Override
    public Object[] toArray() {
        final List<Object> datas = new ArrayList<>();

        final Hints hints = new Hints();
        final FeatureIterator ite = iterator(hints);
        try{
            while(ite.hasNext()){
                datas.add(ite.next());
            }
        }finally{
            ite.close();
        }

        return datas.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        final List<Object> datas = new ArrayList<>();

        final Hints hints = new Hints();
        final FeatureIterator ite = iterator(hints);
        try{
            while(ite.hasNext()){
                datas.add(ite.next());
            }
        }finally{
            ite.close();
        }

        return datas.toArray(a);
    }


    @Override
    public boolean addAll(Collection<? extends Feature> clctn) {
        try {
            this.add(clctn.iterator());
            return true;
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        clctn.stream().forEach((o) -> remove(o));
        return true;
    }
}
