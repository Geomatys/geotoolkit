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
package org.geotoolkit.style;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import javax.swing.event.EventListenerList;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.geotoolkit.util.collection.NotifiedCheckedSet;

import org.opengis.feature.type.Name;
import org.opengis.filter.Id;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.Description;
import org.opengis.style.SemanticType;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

/**
 * Mutable implementation of GeoAPI FeatureTypeStyle.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultMutableFeatureTypeStyle implements MutableFeatureTypeStyle{
    
    private final List<MutableRule> rules = new NotifiedCheckedList<MutableRule>(MutableRule.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableFeatureTypeStyle.this;
            }

            @Override
            protected void notifyAdd(final MutableRule item, final int index) {
                item.addListener(ruleListener);
                fireRuleChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableRule> items, final NumberRange<Integer> range) {
                for(final MutableRule item : items){
                    item.addListener(ruleListener);
                }
                fireRuleChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableRule item, int index) {
                item.removeListener(ruleListener);
                fireRuleChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyRemove(final Collection<? extends MutableRule> items, final NumberRange<Integer> range) {
                for(final MutableRule rule : items){
                    rule.removeListener(ruleListener);
                }
                fireRuleChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            }
            
        };
        
    private final Set<Name> names = new NotifiedCheckedSet<Name>(Name.class){

        @Override
        protected Object getLock() {
            return DefaultMutableFeatureTypeStyle.this;
        }

        @Override
        protected void notifyAdd(Name item, NumberRange<Integer> range) {
            fireNameChange(CollectionChangeEvent.ITEM_ADDED, item, range);
        }

        @Override
        protected void notifyAdd(Collection<? extends Name> items, NumberRange<Integer> range) {
            fireNameChange(CollectionChangeEvent.ITEM_ADDED, items, range);
        }

        @Override
        protected void notifyRemove(Name item, NumberRange<Integer> range) {
            fireNameChange(CollectionChangeEvent.ITEM_REMOVED, item, range);
        }
    };
    
    private final Set<SemanticType> semantics = new NotifiedCheckedSet<SemanticType>(SemanticType.class){

        @Override
        protected Object getLock() {
            return DefaultMutableFeatureTypeStyle.this;
        }

        @Override
        protected void notifyAdd(SemanticType item, NumberRange<Integer> range) {
            fireSemanticChange(CollectionChangeEvent.ITEM_ADDED, item, range);
        }

        @Override
        protected void notifyAdd(Collection<? extends SemanticType> items, NumberRange<Integer> range) {
            fireSemanticChange(CollectionChangeEvent.ITEM_ADDED, items, range);
        }

        @Override
        protected void notifyRemove(SemanticType item, NumberRange<Integer> range) {
            fireSemanticChange(CollectionChangeEvent.ITEM_REMOVED, item, range);
        }
    };
    
    private final RuleListener ruleListener = new RuleListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            fireRuleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableRule)event.getSource(), null, event);
        }

        @Override
        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
            fireRuleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableRule)event.getSource(), null, event);
        }
    };
        
    private final EventListenerList listeners = new EventListenerList();
    
    private String name = null;
    
    private Description desc = StyleConstants.DEFAULT_DESCRIPTION;
    
    private Id ids = null;
    
    private OnlineResource online = null;
    
    /**
     * Create a default mutable feature type style.
     */
    public DefaultMutableFeatureTypeStyle(){}
        
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of the feature type style.
     * This method is thread safe.
     */
    @Override
    public void setName(String name) {
        final String oldName;
        synchronized (this) {
            oldName = this.name;
            if (Utilities.equals(oldName,name)) {
                return;
            }
            this.name = name;
        }
        firePropertyChange(NAME_PROPERTY, oldName, this.name);
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public Description getDescription() {
        return desc;
    }

    /**
     * Set the Description of the feature type style.
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(Description desc){
        if (desc == null) {
            throw new NullPointerException("description can't be null");
        }
        
        final Description oldDesc;
        synchronized (this) {
            oldDesc = this.desc;
            if(oldDesc.equals(desc)){
                return;
            }
            this.desc = desc;
        }
        firePropertyChange(DESCRIPTION_PROPERTY, oldDesc, this.desc);
    }

    /**
     * {@inheritDoc }
     * @return Id : This is the "living" Id collection.
     */
    @Override
    public Id getFeatureInstanceIDs() {
        return ids;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setFeatureInstanceIDs(Id id){
        final Id oldIds;
        synchronized (this) {
            oldIds = this.ids;
            if(Utilities.equals(oldIds, id)){
                return;
            }
            this.ids = id;
        }
        firePropertyChange(IDS_PROPERTY, oldIds, this.ids);
    }
    
    /**
     * {@inheritDoc }
     * @return Set<Name> : This is the "living" Set.
     */
    @Override
    public Set<Name> featureTypeNames() {
        return names;
    }

    /**
     * {@inheritDoc }
     * @return Set<SemanticType> : This is the "living" Set.
     */
    @Override
    public Set<SemanticType> semanticTypeIdentifiers() {
        return semantics;
    }

    /**
     * {@inheritDoc }
     * @return List<Rule> : This is the "living" List.
     */
    @Override
    public List<MutableRule> rules() {
        return rules;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public OnlineResource getOnlineResource() {
        return online;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setOnlineResource(OnlineResource online) {
        final OnlineResource oldOnline;
        synchronized (this) {
            oldOnline = this.online;
            if(Utilities.equals(oldOnline, online)){
                return;
            }
            this.online = online;
        }
        firePropertyChange(ONLINE_PROPERTY, oldOnline, this.online);
    }
    
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
    }

    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);
        
        for(FeatureTypeStyleListener listener : lists){
            listener.propertyChange(event);
        }
        
    }
    
    protected void fireRuleChange(int type, MutableRule symbol, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableRule> event = new CollectionChangeEvent<MutableRule>(this, symbol, type, range,null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);

        for (FeatureTypeStyleListener listener : lists) {
            listener.ruleChange(event);
        }

    }
    
    protected void fireRuleChange(int type, MutableRule symbol, NumberRange<Integer> range, EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableRule> event = new CollectionChangeEvent<MutableRule>(this, symbol, type, range,subEvent);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);

        for (FeatureTypeStyleListener listener : lists) {
            listener.ruleChange(event);
        }

    }
    
    protected void fireRuleChange(int type, Collection<? extends MutableRule> symbol, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<MutableRule> event = new CollectionChangeEvent<MutableRule>(this,symbol,type,range,null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);
        
        for(FeatureTypeStyleListener listener : lists){
            listener.ruleChange(event);
        }
        
    }
    
    protected void fireNameChange(int type, Name ftsName, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<Name> event = new CollectionChangeEvent<Name>(this, ftsName, type, range, null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);

        for (FeatureTypeStyleListener listener : lists) {
            listener.featureTypeNameChange(event);
        }

    }
    
    protected void fireNameChange(int type, Collection<? extends Name> ftsNames, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<Name> event = new CollectionChangeEvent<Name>(this,ftsNames,type,range, null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);
        
        for(FeatureTypeStyleListener listener : lists){
            listener.featureTypeNameChange(event);
        }
        
    }
    
    protected void fireSemanticChange(int type, SemanticType semantic, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<SemanticType> event = new CollectionChangeEvent<SemanticType>(this, semantic, type, range, null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);

        for (FeatureTypeStyleListener listener : lists) {
            listener.semanticTypeChange(event);
        }

    }
    
    protected void fireSemanticChange(int type, Collection<? extends SemanticType> semantics, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<SemanticType> event = new CollectionChangeEvent<SemanticType>(this,semantics,type,range,null);
        final FeatureTypeStyleListener[] lists = listeners.getListeners(FeatureTypeStyleListener.class);
        
        for(FeatureTypeStyleListener listener : lists){
            listener.semanticTypeChange(event);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(FeatureTypeStyleListener listener){
        listeners.add(FeatureTypeStyleListener.class, listener);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(FeatureTypeStyleListener listener){
        listeners.remove(FeatureTypeStyleListener.class, listener);
    }
    
}
