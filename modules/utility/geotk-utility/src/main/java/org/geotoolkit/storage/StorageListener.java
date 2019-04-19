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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
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
public final class StorageListener {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage");

    private StorageListener(){}

    /**
     * Weak style listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<ChangeListener> implements ChangeListener, Disposable {

        private final Collection<Object> sources = new ArrayList<>(1);

        public Weak(final ChangeListener ref) {
            this(null,ref);
        }

        public Weak(final Object source, final ChangeListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            registerSource(source);
        }

        /**
         * Register this listener on the given source.
         */
        public synchronized void registerSource(final Object source) {
            if (source != null && !sources.contains(source)) {
                // register in the new source
                this.sources.add(source);
                if (source instanceof Resource) {
                    Resource res = (Resource) source;
                    res.addListener(this, ChangeEvent.class);
                }
            }
        }

        /**
         * Unregister this listener on the given source.
         */
        public synchronized void unregisterSource(final Object source) {
            sources.remove(source);
            remove(source);
        }

        /**
         * Unregister this listener from all it's sources.
         */
        public synchronized void unregisterAll() {
            for (final Object mc : sources.toArray(new Object[sources.size()])) {
                unregisterSource(mc);
            }
        }

        private synchronized void remove(final Object source) {
            if (source instanceof Resource) {
                Resource res = (Resource) source;
                res.removeListener(this, ChangeEvent.class);
            }
        }

        @Override
        public synchronized void dispose() {
            for (Object source : sources) {
                remove(source);
            }
            sources.clear();
        }

        @Override
        public void changeOccured(final ChangeEvent event) {
            final ChangeListener listener = get();
            if (listener != null) {
                listener.changeOccured(event);
            }
        }
    }
}
