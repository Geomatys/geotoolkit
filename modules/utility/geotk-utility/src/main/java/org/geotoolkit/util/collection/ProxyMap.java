package org.geotoolkit.util.collection;
/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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
import java.util.*;

/**
 * Readable map which delegate to a collection of child maps.
 * If a key appears in multiple maps then only the first map value will be returned.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProxyMap<K,V> implements Map<K,V> {

    private final Map[] subs;

    public ProxyMap(final Map... subs){
        this.subs = subs;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        for(Map m : subs){
            if(!m.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        for(Map m : subs){
            if(m.containsKey(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for(Map m : subs){
            if(m.containsValue(value)){
                return false;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for(Map m : subs){
            Object obj = m.get(key);
            if(obj != null){
                return (V)obj;
            }
        }
        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException("This map implementation is not writable");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("This map implementation is not writable");
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("This map implementation is not writable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This map implementation is not writable");
    }

    @Override
    public Set<K> keySet() {
        final Set<K> keys = new HashSet<K>();
        for(Map m : subs){
            keys.addAll(m.keySet());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        final Collection<V> values = new ArrayList<V>();
        for(Map m : subs){
            values.addAll(m.values());
        }
        return values;
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        final Set<Entry<K,V>> entries = new HashSet<Entry<K,V>>();
        for(Map m : subs){
            entries.addAll(m.entrySet());
        }
        return entries;
    }
}
