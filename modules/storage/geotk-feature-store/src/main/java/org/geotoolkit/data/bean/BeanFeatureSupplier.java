/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data.bean;

import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.opengis.filter.Id;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Definition of a bean mapped as a FeatureType.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BeanFeatureSupplier {
    private final Class beanClass;
    private final CoordinateReferenceSystem crs;
    final BeanFeature.Mapping mapping;
    final BeanStore.FeatureSupplier supplier;
    private final Set<StorageListener> listeners = new HashSet<>();

    public BeanFeatureSupplier(Class bleanClass, String idField, String defaultGeom, String namespace,
            CoordinateReferenceSystem crs, BeanStore.FeatureSupplier supplier) {
        this(bleanClass,idField,defaultGeom,new Predicate<java.beans.PropertyDescriptor>() {
            @Override
            public boolean test(java.beans.PropertyDescriptor t) {
                return true;
            }
        }, crs, supplier);
    }

    public BeanFeatureSupplier(Class bleanClass, String idField, String defaultGeom,
            Predicate<java.beans.PropertyDescriptor> propertyFilter,
            CoordinateReferenceSystem crs, BeanStore.FeatureSupplier supplier) {
        this.beanClass = bleanClass;
        this.crs = crs;
        this.mapping = new BeanFeature.Mapping(beanClass, crs, idField, defaultGeom, propertyFilter);
        this.supplier = supplier;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    public BeanFeature.Mapping getMapping() {
        return mapping;
    }

    public BeanStore.FeatureSupplier getSupplier() {
        return supplier;
    }

    /**
     * Fires a features add event.
     *
     * @param ids modified feature ids
     */
    protected void fireFeaturesAdded(final Id ids) {
        sendContentEvent(FeatureStoreContentEvent.createAddEvent(this, mapping.featureType.getName(), ids));
    }

    /**
     * Fires a features update event.
     *
     * @param ids modified feature ids
     */
    protected void fireFeaturesUpdated(final Id ids) {
        sendContentEvent(FeatureStoreContentEvent.createUpdateEvent(this, mapping.featureType.getName(), ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param ids modified feature ids
     */
    protected void fireFeaturesDeleted(final Id ids) {
        sendContentEvent(FeatureStoreContentEvent.createDeleteEvent(this, mapping.featureType.getName(), ids));
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendContentEvent(final StorageEvent event) {
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for (final StorageListener listener : lst) {
            listener.contentChanged(event);
        }
    }

    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

}
