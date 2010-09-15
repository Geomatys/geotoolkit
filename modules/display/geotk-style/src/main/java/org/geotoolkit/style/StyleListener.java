/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.style;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import org.geotoolkit.internal.ReferenceQueueConsumer;
import org.geotoolkit.util.Disposable;


/**
 * Listener for Style.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface StyleListener extends PropertyChangeListener{

    /**
     * Called when a change occures in the living feature type style collection.
     */
    void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event);

    /**
     * Weak style listener. Use it when you are not
     * sure that the listener will be correctly removed by your class.
     */
    public static final class Weak extends WeakReference<StyleListener> implements StyleListener,Disposable{

        private final MutableStyle source;

        public Weak(MutableStyle source, StyleListener ref) {
            super(ref, ReferenceQueueConsumer.DEFAULT.queue);
            this.source = source;
        }

        @Override
        public void dispose() {
            source.removeListener(this);
        }

        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
            final StyleListener listener = get();
            if (listener != null) {
                listener.featureTypeStyleChange(event);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final StyleListener listener = get();
            if (listener != null) {
                listener.propertyChange(evt);
            }
            //if the listener is null, that means we are in the reference queue and it will be disposed soon.
        }

    }

}
