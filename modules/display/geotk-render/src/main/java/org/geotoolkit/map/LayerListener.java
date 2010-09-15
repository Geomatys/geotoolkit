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
import java.util.EventListener;
import java.util.EventObject;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;

/**
 * Listener for MapLayer. This listener is for PropertyChanges or style change.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface LayerListener extends EventListener {

    /**
     * Called when a property change.
     * Same as a bean property change.
     */
    void propertyChange(PropertyChangeEvent event);
    
    /**
     * Called when a change occurs in the layer style.
     */
    void styleChange(MapLayer source,EventObject event);

    /**
     * Weak layer listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<LayerListener> implements LayerListener,Disposable{

        private final Collection<MapLayer> sources = new ArrayList<MapLayer>(1);

        public Weak(LayerListener ref) {
            this(null,ref);
        }

        public Weak(MapLayer source, LayerListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(MapLayer source){
            if(source != null){
                //register in the new source
                source.addLayerListener(this);
                this.sources.add(source);
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(MapLayer source){
            sources.remove(source);
            source.removeLayerListener(this);
        }

        @Override
        public synchronized void dispose() {
            for(MapLayer source : sources){
                source.removeLayerListener(this);
            }
            sources.clear();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final LayerListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void styleChange(MapLayer source, EventObject event) {
            final LayerListener listener = get();
            if (listener != null) {
                listener.styleChange(source,event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }

}
