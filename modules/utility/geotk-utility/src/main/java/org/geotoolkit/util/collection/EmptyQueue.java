/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;


/**
 * An immutable, serializable empty sorted set.
 *
 * @param  <E> Type of elements in the collection.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
final class EmptyQueue<E> extends AbstractQueue<E> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6147951199761870325L;

    /**
     * The singleton instance to be returned by {@link Utilities#emptyQueue()}.
     * This is not parameterized on intend.
     */
    @SuppressWarnings("rawtypes")
    static final Queue INSTANCE = new EmptyQueue<>();

    /**
     * Do not allow instantiation except for the singleton.
     */
    private EmptyQueue() {
    }

    @Override public void        clear()    {}
    @Override public boolean     isEmpty()  {return true;}
    @Override public int         size()     {return 0;}
    @Override public Iterator<E> iterator() {return Collections.<E>emptySet().iterator();}
    @Override public boolean     offer(E e) {return false;}
    @Override public E           poll()     {return null;}
    @Override public E           peek()     {return null;}

    /**
     * Returns the singleton instance of deserialization.
     */
    protected Object readResolve() {
        return INSTANCE;
    }
}
