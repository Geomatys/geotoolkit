/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.FeatureNaming;
import org.apache.sis.storage.IllegalNameException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericNameIndex<T> extends FeatureNaming<T> {

    private final Map<GenericName,T> names = new HashMap<>();

    /**
     * Copy of index generic name keys.
     *
     * @return keys copy.
     */
    public synchronized Set<GenericName> getNames() {
        return new HashSet<>(names.keySet());
    }

    public synchronized Collection<T> getValues() {
        return new ArrayList<>(names.values());
    }

    public boolean contains(String name) {
        try {
            get(null, name);
            return true;
        } catch (IllegalNameException ex) {
            return false;
        }
    }

    @Override
    public synchronized void add(DataStore store, GenericName name, T value) throws IllegalNameException {
        super.add(store, name, value);
        names.put(name,value);
    }

    @Override
    public synchronized boolean remove(DataStore store, GenericName name) throws IllegalNameException {
        final boolean res = super.remove(store, name);
        if (res) names.remove(name);
        return res;
    }

    public GenericName getGenericName(String name) throws IllegalNameException {
        throw new UnsupportedOperationException("TODO");
    }

    public void clear() throws IllegalNameException {
        for (GenericName name : new ArrayList<>(names.keySet())) {
            remove(null, name);
        }
    }
}
