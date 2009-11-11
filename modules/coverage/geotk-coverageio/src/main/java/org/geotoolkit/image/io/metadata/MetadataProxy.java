/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.Date;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import javax.imageio.metadata.IIOMetadataFormat;

import org.geotoolkit.metadata.KeyNamePolicy;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of metadata interfaces. Calls to getter methods are converted into calls to the
 * metadata accessor extracting an attribute from the {@link javax.imageio.metadata.IIOMetadata}
 * object.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
final class MetadataProxy implements InvocationHandler {
    /**
     * The metadata accessor. This is used for fetching the value of an attribute. The name of
     * the attribute is inferred from the method name using the {@linkplain #namesMapping} map.
     */
    private final MetadataAccessor accessor;

    /**
     * The index of the child element, or -1 if none.
     */
    private final int index;

    /**
     * The mapping from method names to attribute names, or {@code null} if this mapping
     * is unknown.
     */
    private final Map<String, String> namesMapping;

    /**
     * Creates a new proxy for the given metadata accessor.
     */
    private MetadataProxy(final Class<?> type, final MetadataAccessor accessor, final int index) {
        this.accessor = accessor;
        this.index    = index;
        final IIOMetadataFormat format = accessor.metadata.format;
        if (format instanceof SpatialMetadataFormat) {
            final MetadataStandard standard = ((SpatialMetadataFormat) format).getElementStandard(accessor.name());
            if (standard != null) {
                namesMapping = standard.asNameMap(type, SpatialMetadataFormat.NAME_POLICY, KeyNamePolicy.METHOD_NAME);
                return;
            }
        }
        namesMapping = null;
    }

    /**
     * Returns a new instance of a proxy class for the specified metadata interface.
     *
     * @param type     The interface for which to create a proxy instance.
     * @param accessor The metadata accessor.
     * @param index    The index of the child element, or -1 if none.
     */
    static <T> T newProxyInstance(final Class<T> type, final MetadataAccessor accessor, final int index) {
        return type.cast(Proxy.newProxyInstance(MetadataProxy.class.getClassLoader(),
                new Class<?>[] {type}, new MetadataProxy(type, accessor, index)));
    }

    /**
     * Returns the attribute name for the given method name.
     */
    private final String toAttributeName(final String methodName) {
        if (namesMapping != null) {
            final String attribute = namesMapping.get(methodName);
            if (attribute != null) {
                return attribute;
            }
        }
        throw new IllegalArgumentException(methodName); // TODO: infer using JavaBeans conventions.
    }

    /**
     * Invoked when a method from the metadata interface has been invoked.
     *
     * @param  proxy  The proxy instance that the method was invoked on.
     * @param  method The method from the interface which have been invoked.
     * @param  args   The arguments, or {@code null} if the method takes no argument.
     * @return The value to return from the method invocation on the proxy instance.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        String name = method.getName();
        if (!name.startsWith("get")) {
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.UNKNOW_COMMAND_$1, name));
        }
        if (args != null && args.length != 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_$1, name));
        }
        /*
         * Gets the name of the attribute to fetch, and set the accessor
         * child index on the children represented by this proxy (if any).
         */
        final MetadataAccessor accessor = this.accessor;
        name = toAttributeName(name);
        if (index >= 0) {
            accessor.selectChild(index);
        } else {
            accessor.selectParent();
        }
        /*
         * First, process the cases that are handled in a special way.
         */
        final Class<?> targetType = Classes.primitiveToWrapper(method.getReturnType());
        if (Double  .class.equals(targetType)) return accessor.getAttributeAsDouble  (name);
        if (Integer .class.equals(targetType)) return accessor.getAttributeAsInteger (name);
        if (double[].class.equals(targetType)) return accessor.getAttributeAsDoubles (name, false);
        if (int[]   .class.equals(targetType)) return accessor.getAttributeAsIntegers(name, false);
        if (Date    .class.equals(targetType)) return accessor.getAttributeAsDate    (name);
        /*
         * Other type. (TODO)
         */
        final String value = accessor.getAttributeAsString(name);
        return null;
    }
}
