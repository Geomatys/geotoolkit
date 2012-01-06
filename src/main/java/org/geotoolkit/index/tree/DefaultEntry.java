/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public class DefaultEntry<B,V> implements Entry<B,V>{

    private final B boundary;
    private final V value;

    public DefaultEntry(final B boundary, final V value) {
        this.boundary = boundary;
        this.value = value;
    }
    
    public B getBoundary() {
        return boundary;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Classes.getShortClassName(this)+" : "+boundary+"  =>  "+value;
    }
    
}
