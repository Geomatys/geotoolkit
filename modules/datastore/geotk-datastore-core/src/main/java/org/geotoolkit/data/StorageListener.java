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

package org.geotoolkit.data;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.logging.Level;

import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;

/**
 * Listener for datastore, session and feature collection.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface StorageListener extends EventListener{

    /**
     * Fired when a feature type has been created, modified or deleted.
     * @param event
     */
    void structureChanged(StorageManagementEvent event);

    /**
     * Fired when some features has been added, modified or deleted.
     * @param event
     */
    void contentChanged(StorageContentEvent event);

    /**
     * Weak style listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<StorageListener> implements StorageListener,Disposable{

        private static final String ERROR_MSG = "Potential memory leak in StorageListener, could "
                        + "not remove listener because source object does not have a removeStorageListener method. "
                        + "Source object is : {0}";

        private final Collection<Object> sources = new ArrayList<Object>(1);

        public Weak(StorageListener ref) {
            this(null,ref);
        }

        public Weak(Object source, StorageListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }
        
        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(Object source){
            if(source != null){
                //register in the new source
                this.sources.add(source);
                try {
                    final Method method = source.getClass().getMethod("addStorageListener", StorageListener.class);
                    method.invoke(source, this);
                } catch (IllegalAccessException ex) {
                    DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (IllegalArgumentException ex) {
                    DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (InvocationTargetException ex) {
                    DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (NoSuchMethodException ex) {
                    DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
                } catch (SecurityException ex) {
                    DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
                }
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(Object source){
            sources.remove(source);
            remove(source);
        }

        private synchronized void remove(Object source){
            try {
                final Method method = source.getClass().getMethod("removeStorageListener", StorageListener.class);
                method.invoke(source, this);
            } catch (IllegalAccessException ex) {
                DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (IllegalArgumentException ex) {
                DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (InvocationTargetException ex) {
                DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (NoSuchMethodException ex) {
                DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
            } catch (SecurityException ex) {
                DataUtilities.LOGGER.log(Level.WARNING, ERROR_MSG, source);
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
        public void structureChanged(StorageManagementEvent event) {
            final StorageListener listener = get();
            if (listener != null) {
                listener.structureChanged(event);
            }
        }

        @Override
        public void contentChanged(StorageContentEvent event) {
            final StorageListener listener = get();
            if (listener != null) {
                listener.contentChanged(event);
            }
        }

    }

}
