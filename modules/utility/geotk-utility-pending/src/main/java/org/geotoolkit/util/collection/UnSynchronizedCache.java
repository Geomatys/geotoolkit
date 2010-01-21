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

package org.geotoolkit.util.collection;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A Cache map that is limited to the given number of elements.
 * This map is not synchronized and do not preserve older elements with
 * weak or soft reference.
 * It can be seen a fixed max size map where only the most recents elements
 * are preserved.
 *
 * @author Johann Sorel (Geomatys)
 */
public class UnSynchronizedCache<K extends Object,V extends Object> extends LinkedHashMap<K, V>{

    private final int maxElements;

    public UnSynchronizedCache(int maxElement) {
        super();
        this.maxElements = maxElement;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> entry) {
        return size() > maxElements;
    }




}
