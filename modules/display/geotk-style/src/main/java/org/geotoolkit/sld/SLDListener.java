/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld;

import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;

import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;
import org.geotoolkit.util.collection.CollectionChangeEvent;

import org.opengis.sld.SLDLibrary;

/**
 * Listener for style layer descriptor.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface SLDListener extends EventListener{

    /**
     * Called when a property change.
     * Same as a bean property change.
     */
    void propertyChange(PropertyChangeEvent event);
    
    /**
     * Called when a change occurs in the living layer collection.
     */
    void layerChange(CollectionChangeEvent<MutableLayer> event);
    
    /**
     * Called when a change occurs in the living SLDLibrary collection.
     */
    void libraryChange(CollectionChangeEvent<SLDLibrary> event);
    
    /**
     * Weak SLD listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<SLDListener> implements SLDListener,Disposable{

        private final Collection<MutableStyledLayerDescriptor> sources = new ArrayList<MutableStyledLayerDescriptor>(1);

        public Weak(final SLDListener ref) {
            this(null,ref);
        }

        public Weak(final MutableStyledLayerDescriptor source, final SLDListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final MutableStyledLayerDescriptor source){
            if(source != null){
                //register in the new source
                source.addListener(this);
                this.sources.add(source);
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final MutableStyledLayerDescriptor source){
            sources.remove(source);
            source.removeListener(this);
        }

        @Override
        public synchronized void dispose() {
            for(MutableStyledLayerDescriptor source : sources){
                source.removeListener(this);
            }
            sources.clear();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            final SLDListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void layerChange(final CollectionChangeEvent<MutableLayer> event) {
            final SLDListener listener = get();
            if (listener != null) {
                listener.layerChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void libraryChange(final CollectionChangeEvent<SLDLibrary> event) {
            final SLDListener listener = get();
            if (listener != null) {
                listener.libraryChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }
    
}
