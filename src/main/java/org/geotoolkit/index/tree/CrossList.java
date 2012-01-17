/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.Collection;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.CollectionChangeListener;
import org.geotoolkit.util.collection.NotifiedCheckedList;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class CrossList<T extends Node> extends NotifiedCheckedList<T> implements CollectionChangeListener<T> {

    public CrossList(final Class<T> type) {
        super(type);
    }
    
    @Override
    protected void notifyAdd(T e, int i) {
        e.addListener(this);
    }

    @Override
    protected void notifyAdd(Collection<? extends T> clctn, NumberRange<Integer> nr) {
        for(T n : clctn){
            n.addListener(this);
        }
    }

    @Override
    protected void notifyRemove(T e, int i) {
        e.removeListener(this);
    }

    @Override
    protected void notifyRemove(Collection<? extends T> clctn, NumberRange<Integer> nr) {
        for(T n : clctn){
            n.removeListener(this);
        }
    }
}
