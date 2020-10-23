/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.sis.util.Disposable;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 * Map item listener.<br/>
 * Listen to properties and children changes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface ItemListener extends PropertyChangeListener {

    /**
     * Called when a change occurs in the live item list.
     */
    void itemChange(CollectionChangeEvent<MapItem> event);

    /**
     * Weak map item listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<ItemListener> implements ItemListener,Disposable{

        private final Collection<MapContext> sources = new ArrayList<MapContext>(1);

        public Weak(final ItemListener ref) {
            this(null,ref);
        }

        public Weak(final MapContext source, final ItemListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final MapContext source){
            if(source != null && !sources.contains(source)){
                //register in the new source
                source.addItemListener(this);
                this.sources.add(source);
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final MapContext source){
            sources.remove(source);
            source.removeItemListener(this);
        }

        @Override
        public synchronized void dispose() {
            for(MapContext source : sources){
                source.removeItemListener(this);
            }
            sources.clear();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            final ItemListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void itemChange(final CollectionChangeEvent<MapItem> event) {
            final ItemListener listener = get();
            if (listener != null) {
                listener.itemChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }

}
