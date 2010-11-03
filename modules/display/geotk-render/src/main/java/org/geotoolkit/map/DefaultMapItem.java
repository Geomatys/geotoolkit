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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.NotifiedCheckedList;

/**
 * Sample map item. not a layer or a mapcontext.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultMapItem extends AbstractMapItem implements ItemListener{

    protected final List<MapItem> items = new NotifiedCheckedList<MapItem>(MapItem.class) {

        @Override
        protected void notifyAdd(MapItem item, int index) {
            registerListenerSource(item);
            fireItemChange(CollectionChangeEvent.ITEM_ADDED,
                    Collections.singleton(item), NumberRange.create(index, index));
        }

        @Override
        protected void notifyAdd(Collection<? extends MapItem> items, NumberRange<Integer> range) {
            for(MapItem item : items){
                registerListenerSource(item);
            }
            fireItemChange(CollectionChangeEvent.ITEM_ADDED,items, range);
        }

        @Override
        protected void notifyRemove(MapItem item, int index) {
            unregisterListenerSource(item);
            fireItemChange(CollectionChangeEvent.ITEM_REMOVED,
                    Collections.singleton(item), NumberRange.create(index, index));
        }

        @Override
        protected void notifyRemove(Collection<? extends MapItem> items, NumberRange<Integer> range) {
            for(MapItem item : items){
                unregisterListenerSource(item);
            }
            fireItemChange(CollectionChangeEvent.ITEM_REMOVED,items, range);
        }
    };

    private final ItemListener.Weak itemListener = new ItemListener.Weak(this);

    @Override
    public List<MapItem> items() {
        return items;
    }

    //--------------------------------------------------------------------------
    // item listener------------------------------------------------------------
    //--------------------------------------------------------------------------

    protected void registerListenerSource(MapItem item){
        itemListener.registerSource(item);
    }

    protected void unregisterListenerSource(MapItem item){
        itemListener.unregisterSource(item);
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> evt) {
        final MapItem source = (MapItem) evt.getSource();
        final int index = items.indexOf(source);
        fireItemChange(CollectionChangeEvent.ITEM_CHANGED,source,
                       NumberRange.create(index, index),evt);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final MapItem source = (MapItem) evt.getSource();
        final int index = items.indexOf(source);
        fireItemChange(CollectionChangeEvent.ITEM_CHANGED,source,
                       NumberRange.create(index, index),evt);
    }

}
