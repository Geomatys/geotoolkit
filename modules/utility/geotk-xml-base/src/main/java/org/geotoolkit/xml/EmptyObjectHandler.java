/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.xml;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.resources.Errors;


/**
 * The handler for an object where all methods returns null or empty collections, except
 * a few methods related to object identity. This handler is used only when no concrete
 * definition were found for a XML element identified by {@code xlink} or {@code uuidref}
 * attributes.
 *
 * NOTE: the same handler could be used for every proxy having the same XLink. For now,
 *       it doesn't seem worth to cache the handlers.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
final class EmptyObjectHandler implements InvocationHandler {
    /**
     * The {@code xlink} attributes.
     */
    private final XLink xlink;

    /**
     * Creates a new handler for an object identified by the given {@code xlink} attributes.
     */
    EmptyObjectHandler(final XLink xlink) {
        this.xlink = xlink;
    }

    /**
     * Returns {@code true} if the given type is one of the interfaces ignored by
     * {@link #getInterface(Object)}.
     */
    static boolean isIgnoredInterface(final Class<?> type) {
        return IdentifiedObject.class.isAssignableFrom(type) ||
               EmptyObject.class.isAssignableFrom(type) ||
               LenientComparable.class.isAssignableFrom(type);
    }

    /**
     * Returns the interface implemented by the given proxy.
     */
    private static Class<?> getInterface(final Object proxy) {
        for (final Class<?> type : proxy.getClass().getInterfaces()) {
            if (!isIgnoredInterface(type)) {
                return type;
            }
        }
        throw new AssertionError(proxy); // Should not happen.
    }

    /**
     * Processes a method invocation. For any invocation of a getter method, then there is
     * a choice:
     * <p>
     * <ul>
     *   <li>If the invoked method is {@code getXLink()}, returns the {@link #xlink} attribute.</li>
     *   <li>If the invoked method is any other kind of getter, returns null except if the return
     *       type is a collection, in which case an empty collection is returned.</li>
     *   <li>If the invoked method is a setter method, throw a {@link UnsupportedOperationException}
     *       since the proxy instance is assumed unmodifiable.</li>
     *   <li>If the invoked method is one of the {@link Object} method, delegate to the
     *       {@link XLink}.</li>
     * </ul>
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String name = method.getName();
        if (args == null) {
            // TODO: Strings in switch with JDK 7.
            if (name.equals("getXLink")) {
                return xlink;
            }
            if (name.equals("toString")) {
                return getInterface(proxy).getSimpleName() + '[' + xlink.toString() + ']';
            }
            if (name.equals("hashCode")) {
                return ~xlink.hashCode();
            }
            if (name.startsWith("get")) {
                final Class<?> resultType = method.getReturnType();
                if (List.class.isAssignableFrom(resultType)) {
                    return Collections.EMPTY_LIST;
                }
                if (Set.class.isAssignableFrom(resultType)) {
                    return Collections.EMPTY_SET;
                }
                if (Map.class.isAssignableFrom(resultType)) {
                    return Collections.EMPTY_MAP;
                }
            }
        } else switch (args.length) {
            case 1: {
                // TODO: Strings in switch with JDK 7.
                if (name.equals("equals")) {
                    return equals(proxy, args[0], ComparisonMode.STRICT);
                }
                if (name.startsWith("set")) {
                    throw new UnsupportedOperationException(Errors.format(
                            Errors.Keys.UNMODIFIABLE_OBJECT_$1, getInterface(proxy)));
                }
                break;
            }
            case 2: {
                if (name.equals("equals")) {
                    return equals(proxy, args[0], (ComparisonMode) args[1]);
                }
                break;
            }
        }
        return null;
    }

    /**
     * Compares the given objects to the given level of strictness. The first object shall
     * be the proxy, and the second object an arbitrary implementation which should be
     * empty in order to consider the two objects as equal.
     */
    private boolean equals(final Object proxy, final Object other, final ComparisonMode mode) throws Throwable {
        if (other == proxy) return true;
        if (other == null) return false;
        if (proxy.getClass().equals(other.getClass())) {
            final EmptyObjectHandler h = (EmptyObjectHandler) Proxy.getInvocationHandler(other);
            return xlink.equals(h.xlink);
        }
        switch (mode) {
            case STRICT: return false; // The above test is the only relevant one for this mode.
            case IGNORE_METADATA: break; // Do not compare the xlink below.
            default: {
                XLink ox = null;
                if (other instanceof IdentifiedObject) {
                    final IdentifiedObject id = (IdentifiedObject) other;
                    ox = id.getXLink();
                }
                if (!Utilities.equals(xlink, ox)) {
                    return false;
                }
                break;
            }
        }
        /*
         * Having two objects declaring the same xlink and implementing the same interface,
         * ensures that all properties in the other objects are null or empty collections.
         */
        final Class<?> type = getInterface(proxy);
        if (!type.isInstance(other)) {
            return false;
        }
        for (final Method getter : type.getMethods()) {
            // Note: Class.getMethods() on interface does return Object methods.
            if (!Void.TYPE.equals(getter.getReturnType()) && getter.getParameterTypes().length == 0) {
                final Object value;
                try {
                    value = getter.invoke(other, (Object[]) null);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
                if (value != null) {
                    if ((value instanceof Collection<?>) && ((Collection<?>) value).isEmpty()) {
                        continue; // Empty collection, which is consistent with this proxy behavior.
                    }
                    if ((value instanceof Map<?,?>) && ((Map<?,?>) value).isEmpty()) {
                        continue; // Empty collection, which is consistent with this proxy behavior.
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
