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
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;

import org.opengis.metadata.content.Band;

import org.geotoolkit.metadata.KeyNamePolicy;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.converter.AnyConverter;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.NumberRange;
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
     * {@code true} for enabling the process of a few Geotk-specific special cases. This field
     * should always be {@code true}. It is defined mostly as a way to spot every places where
     * some special cases are defined.
     */
    private static final boolean SPECIAL_CASE = true;

    /**
     * The implemented interface. This is used mostly for {@code toString()} implementation.
     */
    private final Class<?> interfaceType;

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
     * is unknown. Keys are method names, and values are the attribute name as determined
     * by {@link SpatialMetadataFormat#NAME_POLICY}.
     */
    private final Map<String, String> namesMapping;

    /**
     * The lists created up to date. This is used only when the return type of some
     * invoked methods is a {@link java.util.Collection} or {@link java.util.List}.
     * <p>
     * The keys are method names (instead than attribute names) because they are
     * usually internalized by the JVM, which is not the case of the attribute names.
     */
    private transient Map<String, List<?>> lists;

    /**
     * The converter from {@link String} to target type.
     * Will be created when first needed.
     */
    private transient AnyConverter converter;

    /**
     * Creates a new proxy for the given metadata accessor.
     */
    private MetadataProxy(Class<?> type, final MetadataAccessor accessor, final int index) {
        interfaceType = type;
        this.accessor = accessor;
        this.index    = index;
        final IIOMetadataFormat format = accessor.format;
        if (format instanceof SpatialMetadataFormat) {
            final MetadataStandard standard = ((SpatialMetadataFormat) format).getElementStandard(accessor.name());
            if (standard != null) {
                if (SPECIAL_CASE) {
                    /*
                     * If the metadata standard is ISO 19115, then we must process SampleDimension
                     * especially because this interface is not defined by ISO 19115. It is a Geotk
                     * interface designed as a sub-set of the ISO Band interface, plus a few additions.
                     */
                    if (MetadataStandard.ISO_19115.equals(standard)) {
                        if (SampleDimension.class.equals(type)) {
                            type = Band.class;
                        }
                    }
                }
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
     * Returns the attribute name for the given method name. The caller must have verified
     * that the method name starts with either {@code "get"}, {@code "set"} or {@code "is"}.
     *
     * @param  methodName The value of {@link Method#getName()}.
     * @return The name of the attribute to search in the {@code IIOMetadataNode}
     *         wrapped by the {@linkplain #accessor}.
     */
    @SuppressWarnings("fallthrough")
    private String getAttributeName(final String methodName) {
        if (namesMapping != null) {
            final String attribute = namesMapping.get(methodName);
            if (attribute != null) {
                return attribute;
            }
        }
        /*
         * If no mapping is explicitly declared for the given method name, apply JavaBeans
         * conventions. If the prefix is not "is", the code below assumes "get" or "set".
         */
        final int offset = methodName.startsWith("is") ? 2 : 3; // Prefix length
        switch (methodName.length() - offset) {
            default: {
                /*
                 * If there is at least 2 characters after the prefix, assume that
                 * we have an acronym if the two first character are upper case.
                 */
                if (Character.isUpperCase(methodName.charAt(offset)) &&
                    Character.isUpperCase(methodName.charAt(offset+1)))
                {
                    return methodName.substring(offset);
                }
                // Fall through
            }
            case 1: {
                /*
                 * If we have at least one character, make the first character lower-case.
                 */
                return Character.toLowerCase(methodName.charAt(offset)) + methodName.substring(offset + 1);
            }
            case 0: {
                /*
                 * If we have only the prefix, return it unchanged.
                 */
                return methodName;
            }
        }
    }

    /**
     * Returns the type of user object for the given element. This typically equals to the
     * {@linkplain Method#getReturnType() method return type}, but is some occasion the
     * {@link IIOMetadataFormat} forces a sub-type.
     *
     * @param  name The element name.
     * @param  methodType The type inferred from the method signature, or {@code null} if unknown.
     * @return The type to use, which is garanteed to be assignable to the method type.
     */
    private Class<?> getElementClass(final String name, final Class<?> methodType) {
        final Class<?> declaredType = accessor.format.getObjectClass(name); // Not allowed to be null.
        return (methodType == null || methodType.isAssignableFrom(declaredType)) ? declaredType : methodType;
    }

    /**
     * Invoked when a method from the metadata interface has been invoked.
     *
     * @param  proxy  The proxy instance that the method was invoked on.
     * @param  method The method from the interface which have been invoked.
     * @param  args   The arguments, or {@code null} if the method takes no argument.
     * @return The value to return from the method invocation on the proxy instance.
     * @throws IllegalStateException If the attribute value can not be converted to the return type.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws IllegalStateException
    {
        final String methodName = method.getName();
        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            if (methodName.equals("toString") && args == null) {
                return accessor.toString(interfaceType);
            }
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.UNKNOW_COMMAND_$1, methodName));
        }
        if (args != null && args.length != 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_$1, methodName));
        }
        /*
         * Gets the name of the attribute to fetch, and set the accessor
         * child index on the children represented by this proxy (if any).
         */
        final MetadataAccessor accessor = this.accessor;
        final String name = getAttributeName(methodName);
        if (index >= 0) {
            accessor.selectChild(index);
        } else {
            accessor.selectParent();
        }
        /*
         * First, process the cases that are handled in a special way.
         */
        final Class<?> targetType = Classes.primitiveToWrapper(method.getReturnType());
        final boolean canReturnString = targetType.isAssignableFrom(String.class);
        if (!canReturnString) {
            if (targetType.isAssignableFrom(Double     .class)) return accessor.getAttributeAsDouble  (name);
            if (targetType.isAssignableFrom(Integer    .class)) return accessor.getAttributeAsInteger (name);
            if (targetType.isAssignableFrom(double[]   .class)) return accessor.getAttributeAsDoubles (name, false);
            if (targetType.isAssignableFrom(int[]      .class)) return accessor.getAttributeAsIntegers(name, false);
            if (targetType.isAssignableFrom(Date       .class)) return accessor.getAttributeAsDate    (name);
            if (targetType.isAssignableFrom(NumberRange.class)) return accessor.getAttributeAsRange   (name);
            if (targetType.isAssignableFrom(List.class)) {
                /*
                 * TODO: process after the line below the cases that are not collection of
                 *       metadata. For example it could be a collection of dates.
                 */
                Class<?> componentType = Classes.boundOfParameterizedAttribute(method);
                /*
                 * For lists, we instantiate MetadataProxyList only when first needed and cache
                 * the result for reuse. The type of elements are garanteed to be compatible
                 * with the type declared in the method signature (inferred from generic types).
                 * However it can also be restricted to a subtype, if the metadata format makes
                 * such restriction.
                 */
                if (lists == null) {
                    lists = new HashMap<String, List<?>>();
                }
                List<?> list = lists.get(methodName);
                if (list == null) {
                    final String[] childs;
                    final IIOMetadataFormat format = accessor.format;
                    String elementName = SpatialMetadataFormat.toElementName(name);
                    if (format.getChildPolicy(elementName) != IIOMetadataFormat.CHILD_POLICY_REPEAT ||
                            (childs = format.getChildNames(elementName)) == null || childs.length != 1)
                    {
                        /*
                         * The return type is a collection, but it doesn't seem
                         * to be compatible with what the metadata format saids.
                         */
                        throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, targetType));
                    }
                    elementName = childs[0];
                    componentType = getElementClass(elementName, componentType);
                    list = MetadataProxyList.create(componentType, accessor);
                    lists.put(methodName, list);
                }
                return list;
            }
        }
        /*
         * String, CharSequence and Object can accepts directly the attribute value.
         * For all other types, we need to apply a type conversion.
         */
        final String value = accessor.getAttributeAsString(name);
        if (value == null || canReturnString) {
            return value;
        }
        if (converter == null) {
            converter = new AnyConverter();
        }
        try {
            return converter.convert(value, targetType);
        } catch (NonconvertibleObjectException e) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.CANT_PROCESS_PROPERTY_$2, methodName, value), e);
        }
    }

    /**
     * Returns a string representation of the {@linkplain #accessor}, but declaring the
     * class as {@code MetadataProxy} instead than {@code MetadataAccessor}.
     */
    @Override
    public String toString() {
        return accessor.toString(getClass());
    }
}
