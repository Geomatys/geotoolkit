/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;

import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.sld.Constraint;

/**
 * Listener for user layer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface LayerListener extends PropertyChangeListener{

    /**
     * Called when a change occurs in the living style collection.
     */
    void styleChange(CollectionChangeEvent<MutableLayerStyle> event);
    
    /**
     * Called when a constraint change.
     */
    void constraintChange(CollectionChangeEvent<Constraint> event);
    
    /**
     * Weak SLD listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<LayerListener> implements LayerListener,Disposable{

        private final Collection<MutableLayer> sources = new ArrayList<MutableLayer>(1);

        public Weak(final LayerListener ref) {
            this(null,ref);
        }

        public Weak(final MutableLayer source, final LayerListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final MutableLayer source){
            if(source != null){
                //register in the new source
                source.addListener(this);
                this.sources.add(source);
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final MutableLayer source){
            sources.remove(source);
            source.removeListener(this);
        }

        @Override
        public synchronized void dispose() {
            for(MutableLayer source : sources){
                source.removeListener(this);
            }
            sources.clear();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            final LayerListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void styleChange(final CollectionChangeEvent<MutableLayerStyle> event) {
            final LayerListener listener = get();
            if (listener != null) {
                listener.styleChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void constraintChange(final CollectionChangeEvent<Constraint> event) {
            final LayerListener listener = get();
            if (listener != null) {
                listener.constraintChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }
    
}
