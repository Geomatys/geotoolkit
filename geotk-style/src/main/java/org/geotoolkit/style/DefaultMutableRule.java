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
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.swing.event.EventListenerList;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.apache.sis.util.Classes;

import org.opengis.filter.Filter;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.Description;
import org.opengis.style.GraphicLegend;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.util.StringUtilities;

/**
 * Mutable implementation of Types Rule.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultMutableRule implements MutableRule{

    private final List<Symbolizer> symbols = new NotifiedCheckedList<Symbolizer>(Symbolizer.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableRule.this;
            }

            @Override
            protected void notifyAdd(final Symbolizer item, final int index) {
                fireSymbolizerChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, true, index, true) );
            }

            @Override
            protected void notifyAdd(final Collection<? extends Symbolizer> items, final NumberRange<Integer> range) {
                fireSymbolizerChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final Symbolizer item, final int index) {
                fireSymbolizerChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, true, index, true) );
            }

            @Override
            protected void notifyRemove(final Collection<? extends Symbolizer> items, final NumberRange<Integer> range) {
                fireSymbolizerChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            }

            @Override
            protected void notifyChange(Symbolizer oldItem, Symbolizer newItem, int index) {
                fireSymbolizerChange(CollectionChangeEvent.ITEM_CHANGED, oldItem, NumberRange.create(index, true, index, true) );
            }

        };

    private final EventListenerList listeners = new EventListenerList();

    private String name = null;
    private Description desc = StyleConstants.DEFAULT_DESCRIPTION;
    private GraphicLegend legend = null;
    private Filter filter = null;
    private boolean isElse = false;
    private double minscale = 0;
    private double maxScale = Double.MAX_VALUE;
    private OnlineResource online = null;

    /**
     * Create a default mutable rule.
     */
    public DefaultMutableRule(){}

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of the rule.
     * This method is thread safe.
     */
    @Override
    public void setName(final String name) {
        final String oldName;
        synchronized (this) {
            oldName = this.name;
            if (Objects.equals(oldName, name)) {
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
     * Set the Description of the rule.
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(final Description desc){
        ensureNonNull("description", desc);

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
     * This method is thread safe.
     */
    @Override
    public GraphicLegend getLegend() {
        return legend;
    }

    /**
     * Set the graphic legend of the rule.
     * @param legend : can be null.
     */
    @Override
    public void setLegendGraphic(final GraphicLegend legend){
        final GraphicLegend oldLegend;
        synchronized (this) {
            oldLegend = this.legend;
            if(Objects.equals(oldLegend, legend)){
                return;
            }
            this.legend = legend;
        }
        firePropertyChange(LEGEND_PROPERTY, oldLegend, this.legend);
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * Set the feature filter of the rule.
     * The filter will limit the features that will be displayed
     * using the underneath symbolizers.
     *
     * @param filter : can be null.
     */
    @Override
    public void setFilter(final Filter filter){
        final Filter oldFilter;
        synchronized (this) {
            oldFilter = this.filter;
            if(Objects.equals(oldFilter, filter)){
                return;
            }
            this.filter = filter;
        }
        firePropertyChange(FILTER_PROPERTY, oldFilter, this.filter);
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public boolean isElseFilter() {
        return isElse;
    }

    /**
     * Set the "else" flag of the filter.
     * If a ruma has this flag then it will used only for the
     * feature that no other rule handle.
     *
     */
    @Override
    public void setElseFilter(final boolean isElse){
        final boolean oldIsElse;
        synchronized (this) {
            oldIsElse = this.isElse;
            if(oldIsElse == isElse){
                return;
            }
            this.isElse = isElse;
        }
        firePropertyChange(ISELSE_FILTER_PROPERTY, oldIsElse, this.isElse);
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public double getMinScaleDenominator() {
        return minscale;
    }

    /**
     * Set the minimum scale on wich this rul apply.
     * if the display device is under this scale then this rule
     * will not be tested.
     */
    @Override
    public void setMinScaleDenominator(final double minScale){
        final double oldMinScale;
        synchronized (this) {
            oldMinScale = this.minscale;
            if(oldMinScale == minScale){
                return;
            }
            this.minscale = minScale;
        }
        firePropertyChange(MINIMUM_SCALE_PROPERTY, oldMinScale, this.minscale);
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public double getMaxScaleDenominator() {
        return maxScale;
    }

    /**
     * Set the maximum scale on wich this rul apply.
     * if the display device is above this scale then this rule
     * will not be tested.
     */
    @Override
    public void setMaxScaleDenominator(final double maxScale){
        final double oldMaxScale;
        synchronized (this) {
            oldMaxScale = this.maxScale;
            if(oldMaxScale == maxScale){
                return;
            }
            this.maxScale = maxScale;
        }
        firePropertyChange(MAXIMUM_SCALE_PROPERTY, oldMaxScale, this.maxScale);
    }

    /**
     *
     * @return live list
     */
    @Override
    public List<Symbolizer> symbolizers() {
        return symbols;
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
    public void setOnlineResource(final OnlineResource online) {
        final OnlineResource oldOnline;
        synchronized (this) {
            oldOnline = this.online;
            if(Objects.equals(oldOnline, online)){
                return;
            }
            this.online = online;
        }
        firePropertyChange(ONLINE_PROPERTY, oldOnline, this.online);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final StyleVisitor visitor, final Object extraData) {
        return visitor.visit(this,extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Rule : ");
        builder.append(Classes.getShortClassName(this));
        builder.append(" [");
        builder.append(desc);
        builder.append(']');

        if(!symbols.isEmpty()){
            builder.append(StringUtilities.toStringTree("", symbols));
        }

        return builder.toString();
    }

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue){
        //TODO make fire property change thread safe, preserve fire order

        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final PropertyChangeListener[] lists = listeners.getListeners(PropertyChangeListener.class);

        for(PropertyChangeListener listener : lists){
            listener.propertyChange(event);
        }

    }

    protected void fireSymbolizerChange(final int type, final Symbolizer symbol, final NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<Symbolizer> event = new CollectionChangeEvent<Symbolizer>(this, symbol, type, range, null);
        final RuleListener[] lists = listeners.getListeners(RuleListener.class);

        for (RuleListener listener : lists) {
            listener.symbolizerChange(event);
        }

    }

    protected void fireSymbolizerChange(final int type, final Collection<? extends Symbolizer> symbol, final NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<Symbolizer> event = new CollectionChangeEvent<Symbolizer>(this,symbol,type,range, null);
        final RuleListener[] lists = listeners.getListeners(RuleListener.class);

        for(RuleListener listener : lists){
            listener.symbolizerChange(event);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(final RuleListener listener){
        addListener((PropertyChangeListener)listener);
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(PropertyChangeListener listener) {
        removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener){
        listeners.add(PropertyChangeListener.class, listener);
        if(listener instanceof RuleListener){
            listeners.add(RuleListener.class, (RuleListener)listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener){
        listeners.remove(PropertyChangeListener.class, listener);
        if(listener instanceof RuleListener){
            listeners.remove(RuleListener.class, (RuleListener)listener);
        }
    }

}
