/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.opengis.metadata.Identifier;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;


/**
 * The handler for an object where all methods returns null or empty collections, except
 * a few methods related to object identity. This handler is used only when no concrete
 * definition were found for a XML element identified by {@code xlink} or {@code uuidref}
 * attributes.
 *
 * {@note The same handler could be used for every proxy having the same XLink. For now,
 *        it doesn't seem worth to cache the handlers.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 * @module
 */
final class NilObjectHandler implements InvocationHandler {
    /**
     * The identifiers as an {@link IdentifierMapAdapter} object, or the {@code nilReason}
     * attribute as a {@link NilReason} object. We don't use separated fields because
     * those attributes are exclusive, and some operations like {@code toString()},
     * {@code hashCode()} and {@code equals(Object)} are the same for both types.
     */
    private final Object attribute;

    /**
     * Creates a new handler for an object identified by the given identifiers.
     * The identifiers are wrapped in a mutable list, so users can add, remove
     * or modify identifiers.
     */
    <T extends Identifier> NilObjectHandler(final Class<T> type, final T[] identifiers) {
        attribute = IdentifierMapAdapter.create(type, new ArrayList<T>(Arrays.asList(identifiers)));
    }

    /**
     * Creates a new handler for an object which is nil for the given reason.
     */
    NilObjectHandler(final NilReason nilReason) {
        attribute = nilReason;
    }

    /**
     * Returns {@code true} if the given type is one of the interfaces ignored by
     * {@link #getInterface(Object)}.
     */
    static boolean isIgnoredInterface(final Class<?> type) {
        return IdentifiedObject.class.isAssignableFrom(type) ||
               NilObject.class.isAssignableFrom(type) ||
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
     *   <li>If the invoked method is {@code getIdentifiers()}, returns the identifiers given at
     *       construction time.</li>
     *   <li>If the invoked method is {@code getIdentifierMap()}, returns a view over the
     *       identifiers given at construction time.</li>
     *   <li>If the invoked method is any other kind of getter, returns null except if:<ul>
     *       <li>the return type is a collection, in which case an empty collection is returned;</li>
     *       <li>the return type is a primitive, in which case the nil value for that primitive
     *           type is returned.</li></ul></li>
     *   <li>If the invoked method is a setter method, throw a {@link UnsupportedOperationException}
     *       since the proxy instance is assumed unmodifiable.</li>
     *   <li>If the invoked method is one of the {@link Object} method, delegate to the
     *       {@link #reference}.</li>
     * </ul>
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String name = method.getName();
        if (args == null) {
            // TODO: Strings in switch with JDK 7.
            if (name.equals("getNilReason")) {
                return (attribute instanceof NilReason) ? (NilReason) attribute : null;
            }
            if (name.equals("getIdentifierMap")) {
                return (attribute instanceof IdentifierMap) ? (IdentifierMap) attribute : null;
            }
            if (name.equals("getIdentifiers")) {
                return (attribute instanceof IdentifierMapAdapter) ?
                        ((IdentifierMapAdapter) attribute).identifiers : null;
            }
            if (name.equals("toString")) {
                return getInterface(proxy).getSimpleName() + '[' + attribute + ']';
            }
            if (name.equals("hashCode")) {
                return ~attribute.hashCode();
            }
            if (name.startsWith("get") || name.startsWith("is")) {
                return Numbers.valueOfNil(method.getReturnType());
            }
        } else switch (args.length) {
            case 1: {
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
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UNSUPPORTED_OPERATION_$1,
                getInterface(proxy).getSimpleName() + '.' + name));
    }

    /**
     * Compares the given objects to the given level of strictness. The first object shall
     * be the proxy, and the second object an arbitrary implementation which should be
     * empty in order to consider the two objects as equal.
     */
    private boolean equals(final Object proxy, final Object other, final ComparisonMode mode) throws Throwable {
        if (other == proxy) return true;
        if (other == null) return false;
        if (proxy.getClass() == other.getClass()) {
            if (mode.ordinal() >= ComparisonMode.IGNORE_METADATA.ordinal()) {
                return true;
            }
            final NilObjectHandler h = (NilObjectHandler) Proxy.getInvocationHandler(other);
            return attribute.equals(h.attribute);
        }
        switch (mode) {
            case STRICT: return false; // The above test is the only relevant one for this mode.
            case BY_CONTRACT: {
                Object tx = attribute, ox = null;
                if (tx instanceof IdentifierMapAdapter) {
                    tx = ((IdentifierMapAdapter) tx).identifiers;
                    if (other instanceof IdentifiedObject) {
                        ox = ((IdentifiedObject) other).getIdentifiers();
                    }
                } else {
                    if (other instanceof NilObject) {
                        ox = ((NilObject) other).getNilReason();
                    }
                }
                if (!Utilities.equals(tx, ox)) {
                    return false;
                }
                break;
            }
        }
        /*
         * Having two objects declaring the same identifiers and implementing the same interface,
         * ensures that all properties in the other objects are null or empty collections.
         */
        final Class<?> type = getInterface(proxy);
        if (!type.isInstance(other)) {
            return false;
        }
        for (final Method getter : type.getMethods()) {
            if (Classes.isPossibleGetter(getter)) {
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
