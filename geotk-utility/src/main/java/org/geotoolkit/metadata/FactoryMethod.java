/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.InvocationTargetException;

import org.opengis.util.FactoryException;



/**
 * The method used for creating an instance of some specific class using a factory.
 * This is used by {@link MetadataFactory} only.
 * <p>
 * Factory method starts with {@link #create}, and their first parameter type is {@code Map},
 * {@code Map<String,?>} or {@code Map<String,Object>}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
final class FactoryMethod {
    /**
     * A dummy {@code FactoryMethod} used by {@link MetadataFactory} as a sentinel
     * values when we have determined that there is no factory method available.
     */
    static final FactoryMethod NULL = new FactoryMethod(null, null);

    /**
     * The method to invoke on a factory.
     */
    private final Method method;

    /**
     * The factory instance on which to invoke the method.
     */
    private final Object factory;

    /**
     * Creates a new {@code FactoryMethod} object.
     *
     * @param method  The method to invoke on a factory.
     * @param factory The factory instance on which to invoke the method.
     */
    private FactoryMethod(final Method method, final Object factory) {
        this.method  = method;
        this.factory = factory;
    }

    /**
     * Creates a new {@code FactoryMethod} instance for creating an object of the given type
     * from the given factory. The first suitable factory is used. If no suitable factory is
     * found, then this method returns {@code null}.
     *
     * @param  type The type of the object to create.
     * @param  factories The factories to try.
     * @return A new {@code FactoryMethod}, or {@code null} if none.
     */
    static FactoryMethod find(final Class<?> type, final Object[] factories) {
        for (final Object factory : factories) {
            for (final Method method : factory.getClass().getMethods()) {
                if (method.isSynthetic() || method.isAnnotationPresent(Deprecated.class)) {
                    // Ignores synthetic and deprecated methods.
                    continue;
                }
                final String name = method.getName();
                if (!name.startsWith("create")) {
                    continue;
                }
                if (!type.isAssignableFrom(method.getReturnType())) {
                    continue;
                }
                Type[] types = method.getGenericParameterTypes();
                if (types.length == 0) {
                    continue;
                }
                /*
                 * We have found a "create" method with a suitable return type. Now check
                 * the parameters. The first one must be either the Map raw parameter type,
                 * the Map<String,?> type or the Map<String,Object> one.
                 */
                final Type mapType = types[0];
                if (mapType != Map.class) {
                    if (!(mapType instanceof ParameterizedType)) {
                        continue;
                    }
                    types = ((ParameterizedType) mapType).getActualTypeArguments();
                    if (types.length != 2 ||
                            !bounds(types[0]).isAssignableFrom(String.class) ||
                            !bounds(types[1]).isAssignableFrom(Object.class))
                    {
                        continue;
                    }
                    return new FactoryMethod(method, factory);
                }
            }
        }
        return null;
    }

    /**
     * Returns the upper bounds of the given type. The type must be either a (@link Class} or
     * a {@link WildcardType}. We assume that no other type can occur in this implementation.
     */
    private static Class<?> bounds(Type type) {
        while (type instanceof WildcardType) {
            final Type[] types = ((WildcardType) type).getUpperBounds();
            if (types.length == 1) {
                type = types[0];
            }
        }
        return (Class<?>) type;
    }

    /**
     * Invokes the {@code Factory.create(Map, ...)} method with the given properties.
     * The values in the given map that are assignable to the parameter type expected
     * by the factory method are extracted from the map and assigned to the parameter
     * array.
     * <p>
     * In the special case where this {@code FactoryMethod} is {@link #NULL}, this
     * method returns {@code null}.
     */
    Object create(Map<String,?> properties) throws FactoryException {
        if (method == null) {
            return null;
        }
        final Class<?>[] types = method.getParameterTypes();
        final Object[] parameters = new Object[types.length];
        if (types.length > 1) {
            final Map<String,Object> reduced = new LinkedHashMap<>(properties);
            properties = reduced;
            for (int i=1; i<types.length; i++) {
                final Class<?> expected = types[i];
                for (final Iterator<Object> it=reduced.values().iterator(); it.hasNext();) {
                    final Object value = it.next();
                    if (expected.isInstance(value)) {
                        parameters[i] = value;
                        it.remove();
                        break;
                    }
                }
            }
        }
        parameters[0] = properties;
        try {
            return method.invoke(factory, parameters);
        } catch (IllegalAccessException e) {
            // Should never happen, because we asked for public methods only.
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof FactoryException) {
                throw (FactoryException) cause;
            }
            throw new FactoryException(cause.getLocalizedMessage(), cause);
        }
    }

    /**
     * Returns {@code true} if the given object is equals to this factory method.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof FactoryMethod) {
            final FactoryMethod that = (FactoryMethod) other;
            return method.equals(that.method) && factory == that.factory;
        }
        return false;
    }

    /**
     * Defined for consistency with {@link #equals} but not used.
     */
    @Override
    public int hashCode() {
        return method.hashCode() ^ System.identityHashCode(factory);
    }
}
