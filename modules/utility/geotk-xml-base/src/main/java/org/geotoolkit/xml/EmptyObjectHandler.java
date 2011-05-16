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
import java.util.Collections;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import org.geotoolkit.resources.Errors;


/**
 * The handler for an object where all methods returns null or empty collections, except
 * a few methods related to object identity. This handler is used only when no concrete
 * definition were found for a XML element identified by {@code xlink} or {@code uuidref}
 * attributes.
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
        return IdentifiedObject.class.isAssignableFrom(type) || EmptyObject.class.isAssignableFrom(type);
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
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
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
        } else if (args.length == 1) {
            // TODO: Strings in switch with JDK 7.
            if (name.startsWith("set")) {
                throw new UnsupportedOperationException(Errors.format(
                        Errors.Keys.UNMODIFIABLE_OBJECT_$1, getInterface(proxy)));
            }
            if (name.equals("equals")) {
                final Object other = args[0];
                return (other instanceof IdentifiedObject) && (other instanceof EmptyObject) &&
                        xlink.equals(((IdentifiedObject) other).getXLink()) &&
                        getInterface(proxy).isAssignableFrom(other.getClass());
            }
        }
        return null;
    }
}
