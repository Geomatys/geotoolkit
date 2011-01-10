/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.Disposable;


/**
 * Listener for MapContext. This listener is for PropertyChanges or layer list change.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ContextListener extends ItemListener {
        
    /**
     * Called when a change occurs in the living layer list.
     */
    void layerChange(CollectionChangeEvent<MapLayer> event);

    /**
     * Weak context listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<ContextListener> implements ContextListener,Disposable{

        private final Collection<MapContext> sources = new ArrayList<MapContext>(1);

        public Weak(final ContextListener ref) {
            this(null,ref);
        }

        public Weak(final MapContext source, final ContextListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final MapContext source){
            if(source != null){
                //register in the new source
                source.addContextListener(this);
                this.sources.add(source);
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final MapContext source){
            sources.remove(source);
            source.removeContextListener(this);
        }

        @Override
        public synchronized void dispose() {
            for(MapContext source : sources){
                source.removeContextListener(this);
            }
            sources.clear();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            final ContextListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void layerChange(final CollectionChangeEvent<MapLayer> event) {
            final ContextListener listener = get();
            if (listener != null) {
                listener.layerChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void itemChange(final CollectionChangeEvent<MapItem> event) {
            final ContextListener listener = get();
            if (listener != null) {
                listener.itemChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }

}
