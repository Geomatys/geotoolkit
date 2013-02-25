/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.Map;
import java.util.Collections;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.util.Cloneable;


/**
 * A {@linkplain Collections#checkedMap checked} and {@linkplain Collections#synchronizedMap
 * synchronized} {@link java.util.Map}. Type checks are performed at run-time in addition of
 * compile-time checks. The synchronization lock can be modified at runtime by overriding the
 * {@link #getLock} method.
 * <p>
 * This class is similar to using the wrappers provided in {@link Collections}, minus the cost
 * of indirection levels and with the addition of overrideable methods.
 *
 * @todo Current implementation do not synchronize the {@linkplain #entrySet entry set},
 *       {@linkplain #keySet key set} and {@linkplain #values values} collection.
 *
 * @param <K> The type of keys in the map.
 * @param <V> The type of values in the map.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Collections#checkedMap
 * @see Collections#synchronizedMap
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.collection.CheckedHashMap}.
 */
@ThreadSafe
@Deprecated
public class CheckedHashMap<K,V> extends org.apache.sis.util.collection.CheckedHashMap<K,V> implements Cloneable {
    /**
     * Serial version UID for compatibility with different versions.
     */
    private static final long serialVersionUID = -7777695267921872848L;

    /**
     * Constructs a map of the specified type.
     *
     * @param keyType   The key type (should not be null).
     * @param valueType The value type (should not be null).
     */
    public CheckedHashMap(final Class<K> keyType, final Class<V> valueType) {
        super(keyType, valueType);
    }

    /**
     * Returns a shallow copy of this map.
     *
     * @return A shallow copy of this map.
     */
    @Override
    @SuppressWarnings("unchecked")
    public CheckedHashMap<K,V> clone() {
        return (CheckedHashMap<K,V>) super.clone();
    }
}
