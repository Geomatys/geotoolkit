/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.geotoolkit.internal.ReferenceQueueConsumer;

/**
 * Weak PropertyChangeListener.
 * 
 * @author Johann sorel (Geomatys)
 */
public class WeakPropertyChangeListener extends WeakReference<PropertyChangeListener>
                                        implements PropertyChangeListener,Disposable{

    private Object source = null;

    public WeakPropertyChangeListener(final Object source, final PropertyChangeListener ref) {
        super(ref, ReferenceQueueConsumer.DEFAULT.queue);
        ArgumentChecks.ensureNonNull("source", source);
        registerSource(source);
    }

    /**
     * Register this listener on the given source.
     */
    public synchronized void registerSource(final Object source){
        this.source = source;
        //register in the new source
        try {
            final Method method = source.getClass().getMethod("addPropertyListener", PropertyChangeListener.class);
            method.invoke(source, this);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Object do not have property listener methods.",ex);
        } catch (SecurityException ex) {
            throw new RuntimeException("Object do not have property listener methods.",ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Object do not have property listener methods.",ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Object do not have property listener methods.",ex);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final PropertyChangeListener listener = get();
        if (listener != null) {
            listener.propertyChange(evt);
        }
    }

    @Override
    public void dispose() {
        if(source != null){
            try {
                final Method method = source.getClass().getMethod("removePropertyListener", PropertyChangeListener.class);
                method.invoke(source, this);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Object do not have property listener methods.",ex);
            } catch (SecurityException ex) {
                throw new RuntimeException("Object do not have property listener methods.",ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Object do not have property listener methods.",ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException("Object do not have property listener methods.",ex);
            }
        }
        source = null;
    }

}
