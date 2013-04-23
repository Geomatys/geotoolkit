/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;


/**
 * The handler for metadata proxy that implement (indirectly) metadata interfaces like
 * {@link org.opengis.metadata.Metadata}, {@link org.opengis.metadata.citation.Citation},
 * <i>etc</i>.
 *
 * Any call to a method in a metadata interface is redirected toward the {@link #invoke} method.
 * This method uses reflection in order to find the caller's method and class name. The class
 * name is translated into a table name, and the method name is translated into a column name.
 * Then the information is fetch in the underlying metadata database.
 *
 * @author Touraïvane (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.1)
 * @module
 */
@ThreadSafe
final class MetadataHandler implements InvocationHandler {
    /**
     * The identifier used in order to locate the record for
     * this metadata entity in the database. This is usually
     * the primary key in the table which contains this entity.
     */
    private final String identifier;

    /**
     * The connection to the database. All metadata handlers
     * created from a single database should share the same source.
     */
    private final MetadataSource source;

    /**
     * Creates a new metadata handler.
     *
     * @param identifier The identifier used in order to locate the record for
     *                   this metadata entity in the database. This is usually
     *                   the primary key in the table which contains this entity.
     * @param source     The connection to the table which contains this entity.
     */
    public MetadataHandler(final String identifier, final MetadataSource source) {
        this.identifier = identifier;
        this.source     = source;
    }

    /**
     * Ensures that the given argument array has the expected length.
     */
    private static void checkArgumentCount(final Object[] args, final int expected) {
        final int count = (args != null) ? args.length : 0;
        if (count != expected) {
            final int key;
            final Object value;
            if (count == 0) {
                key = Errors.Keys.NO_PARAMETER_1;
                value = "arg";
            } else {
                key = Errors.Keys.UNEXPECTED_PARAMETER_1;
                value = args[0];
            }
            throw new MetadataException(Errors.format(key, value));
        }
    }

    /**
     * Invoked when any method from a metadata interface is invoked.
     *
     * @param proxy  The object on which the method is invoked.
     * @param method The method invoked.
     * @param args   The argument given to the method.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        final Class<?> type = method.getDeclaringClass();
        if (source.standard.isMetadata(type)) {
            checkArgumentCount(args, 0);
            /*
             * The method invoked is a method from the metadata interface. Consequently,
             * the information should exists in the underlying database.
             */
            try {
                return source.getValue(type, method, identifier);
            } catch (SQLException e) {
                Class<?> rt = method.getReturnType();
                if (Collection.class.isAssignableFrom(rt)) {
                    final Class<?> elementType = Classes.boundOfParameterizedProperty(method);
                    if (elementType != null) {
                        rt = elementType;
                    }
                }
                throw new MetadataException(Errors.format(Errors.Keys.DATABASE_FAILURE_2, rt, identifier), e);
            }
        }
        /*
         * The method invoked is a method inherited from a parent class, like Object.toString()
         * or Object.hashCode(). This information is not expected to exists in the database.
         * Do not delegate to the proxy since this result in never-ending loop.
         */
        final String name = method.getName();
        switch (name.hashCode()) {
            case -1618432855: {
                if (name.equals("identifier")) {
                    checkArgumentCount(args, 1);
                    return (args[0] == source) ? identifier : null;
                }
                break;
            }
            case -1295482945: {
                if (name.equals("equals")) {
                    checkArgumentCount(args, 1);
                    return proxy == args[0];
                }
                break;
            }
            case -1776922004: {
                if (name.equals("toString")) {
                    checkArgumentCount(args, 0);
                    return toString(type);
                }
                break;
            }
            case 147696667: {
                if (name.equals("hashCode")) {
                    checkArgumentCount(args, 0);
                    return System.identityHashCode(proxy);
                }
                break;
            }
        }
        throw new MetadataException(Errors.format(Errors.Keys.ILLEGAL_INSTRUCTION_1, name));
    }

    /**
     * Returns a string representation of a metadata of the given type.
     */
    private String toString(final Class<?> type) {
        return Classes.getShortName(type) + "[id=\"" + identifier + "\"]";
    }

    /**
     * Returns a string representation of this handler.
     * This is mostly for debugging purpose.
     */
    @Override
    public String toString() {
        return toString(getClass());
    }

    /**
     * Compares this method handler with the given object for equality.
     * Note that the same handler could be used for different metadata
     * objects if they share the same identifier. The {@code equals}
     * method is defined in order to allow to put {@code MetadataHandler}
     * in a hash map for this purpose.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof MetadataHandler) {
            final MetadataHandler that = (MetadataHandler) other;
            return source == that.source && identifier.equals(that.identifier);
        }
        return false;
    }

    /**
     * Returns a hash code value for this method handler.
     */
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
