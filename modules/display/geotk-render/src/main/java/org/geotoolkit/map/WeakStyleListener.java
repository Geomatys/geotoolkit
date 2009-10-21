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
import java.lang.reflect.Method;
import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WeakStyleListener implements StyleListener{

        WeakReference<StyleListener> listenerRef;
        Object src;

        public WeakStyleListener(StyleListener listener, Object src){
            listenerRef = new ListenerReference(listener, this);
            this.src = src;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt){
            StyleListener listener = listenerRef.get();
            if(listener!=null)
                listener.propertyChange(evt);
        }

        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> evt) {
            StyleListener listener = listenerRef.get();
            if(listener!=null)
                listener.featureTypeStyleChange(evt);
        }

        private void removeListener(){
            try{
                Method method = src.getClass().getMethod("removeListener"
                        , new Class[] {StyleListener.class});
                method.invoke(src, new Object[]{ this });
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        class ListenerReference extends WeakReference{
            WeakStyleListener listener;

            public ListenerReference(Object ref, WeakStyleListener listener){
                super(ref, ActiveReferenceQueue.getInstance());
                this.listener = listener;
            }

            public void run(){
                listener.removeListener();
                listener = null;
            }
        }
    }
