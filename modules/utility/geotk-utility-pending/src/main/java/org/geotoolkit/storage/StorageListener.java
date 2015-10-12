/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Geomatys
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

package org.geotoolkit.storage;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.apache.sis.util.Disposable;
import org.apache.sis.util.logging.Logging;

/**
 * Listener for storage objects.
 *
 * Events are divided in :
 * - structure changes (like a new table or a field change)
 * - content changes (like a new record)
 *
 * Used in : FeatureStore,Session,FeatureCollection,CoverageStore,CoverageReference,...
 *
 * @author Johann Sorel (Geomatys)
 */
public interface StorageListener<S extends StorageEvent,C extends StorageEvent> extends EventListener{

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage");

    /**
     * Fired when the internal structure of the storage has changed.
     * @param event
     */
    void structureChanged(S event);


    /**
     * Fired when datas have been added,changed or deleted.
     * @param event
     */
    void contentChanged(C event);

    /**
     * Weak style listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<StorageListener> implements StorageListener,Disposable{

        private static final String ERROR_MSG = "Potential memory leak in StorageListener, could "
                        + "not remove listener because source object does not have a removeStorageListener method. "
                        + "Source object is : {0}";

        private final Collection<Object> sources = new ArrayList<Object>(1);

        public Weak(final StorageListener ref) {
            this(null,ref);
        }

        public Weak(final Object source, final StorageListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final Object source){
            if(source != null && !sources.contains(source)){
                //register in the new source
                this.sources.add(source);
                try {
                    final Method method = source.getClass().getMethod("addStorageListener", StorageListener.class);
                    method.invoke(source, this);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (InvocationTargetException ex) {
                    LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (NoSuchMethodException ex) {
                    LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (SecurityException ex) {
                    LOGGER.log(Level.WARNING, ERROR_MSG, source);
                }
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final Object source){
            sources.remove(source);
            remove(source);
        }

        /**
         * Unregister this listener from all it's sources.
         */
        public synchronized void unregisterAll(){
            for(final Object mc : sources.toArray(new Object[sources.size()])){
                unregisterSource(mc);
            }
        }

        private synchronized void remove(final Object source){
            try {
                final Method method = source.getClass().getMethod("removeStorageListener", StorageListener.class);
                method.invoke(source, this);
            } catch (IllegalAccessException ex) {
                LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (InvocationTargetException ex) {
                LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (NoSuchMethodException ex) {
                LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (SecurityException ex) {
                LOGGER.log(Level.WARNING, ERROR_MSG, source);
            }
        }

        @Override
        public synchronized void dispose() {
            for(Object source : sources){
                remove(source);
            }
            sources.clear();
        }

        @Override
        public void structureChanged(final StorageEvent event) {
            final StorageListener listener = get();
            if (listener != null) {
                listener.structureChanged(event);
            }
        }

        @Override
        public void contentChanged(final StorageEvent event) {
            final StorageListener listener = get();
            if (listener != null) {
                listener.contentChanged(event);
            }
        }

    }

}
