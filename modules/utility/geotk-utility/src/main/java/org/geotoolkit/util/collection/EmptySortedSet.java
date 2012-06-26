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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;


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
 *
 * @todo This class will be removed on the JDK8 branch.
 */
final class EmptySortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4684832991264788298L;

    /**
     * The unique instance of this set.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    static final SortedSet INSTANCE = new EmptySortedSet<>();

    /**
     * Do not allow instantiation except for the unique instance.
     */
    private EmptySortedSet() {
    }

    @Override public void          clear()                      {}
    @Override public Comparator<E> comparator()                 {return null;}
    @Override public Iterator<E>   iterator()                   {return Collections.<E>emptySet().iterator();}
    @Override public int           size()                       {return 0;}
    @Override public boolean       isEmpty()                    {return true;}
    @Override public boolean       contains(Object obj)         {return false;}
    @Override public boolean       containsAll(Collection<?> c) {return c.isEmpty();}
    @Override public E             first()                      {throw new NoSuchElementException();}
    @Override public E             last()                       {throw new NoSuchElementException();}
    @Override public SortedSet<E>  subSet(E from, E to)         {return this;}
    @Override public SortedSet<E>  headSet(E toElement)         {return this;}
    @Override public SortedSet<E>  tailSet(E fromElement)       {return this;}

    /**
     * Returns the unique instance of deserialization.
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
