/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
import java.lang.reflect.Method;
import java.util.EventListener;

import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.FeatureTypeStyleListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.style.StyleListener;

import org.opengis.feature.type.Name;
import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WeakStyleListener extends WeakListener<PropertyChangeListener>
        implements StyleListener, FeatureTypeStyleListener, RuleListener {


    public WeakStyleListener(PropertyChangeListener listener, Object src) {
        super(listener,src);
    }

    private <T extends PropertyChangeListener> T getRef(Class<T> clazz){
        final EventListener listener = listenerRef.get();

        if (listener != null) {
            if(clazz.isInstance(listener)){
                return (T) listener;
            }else{
                throw new IllegalStateException("Listener should have been of class : "+ clazz +" but was : " +listener);
            }
        }else{
            removeListener();
            return null;
        }
    }

    @Override
    protected void removeListener() {
        try {
            Method method = src.getClass().getMethod("removeListener", PropertyChangeListener.class);
            method.invoke(src, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final PropertyChangeListener listener = getRef(PropertyChangeListener.class);
        if (listener != null) {
            listener.propertyChange(evt);
        }
    }

    @Override
    public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> evt) {
        final StyleListener listener = getRef(StyleListener.class);
        if (listener != null) {
            listener.featureTypeStyleChange(evt);
        }
    }


    //feature type style listener ----------------------------------------------

    @Override
    public void ruleChange(CollectionChangeEvent<MutableRule> event) {
        final FeatureTypeStyleListener listener = getRef(FeatureTypeStyleListener.class);
        if (listener != null) {
            listener.ruleChange(event);
        }
    }

    @Override
    public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
        final FeatureTypeStyleListener listener = getRef(FeatureTypeStyleListener.class);
        if (listener != null) {
            listener.featureTypeNameChange(event);
        }
    }

    @Override
    public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
        final FeatureTypeStyleListener listener = getRef(FeatureTypeStyleListener.class);
        if (listener != null) {
            listener.semanticTypeChange(event);
        }
    }

    //rule listener methods ----------------------------------------------------

    @Override
    public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
        final RuleListener listener = getRef(RuleListener.class);
        if (listener != null) {
            listener.symbolizerChange(event);
        }
    }

}
