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
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.NoSuchElementException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.opengis.util.CodeList;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.SimpleInternationalString;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.coverage.grid.GeneralGridCoordinates;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of metadata interfaces. Calls to getter methods are converted into calls to the
 * metadata accessor extracting an attribute from the {@link javax.imageio.metadata.IIOMetadata}
 * object.
 *
 * {@section Naming conventions for elements and attributes}
 * This class assumes that the elements and attributes are named according the same rules than
 * the ones used by the {@code SpatialMetadataFormat.addTree(...)} methods. More specifically:
 *
 * <ul>
 *   <li><p>Attribute names are inferred from the method names according the
 *       {@link SpatialMetadataFormat#NAME_POLICY}, which follow Java-Beans conventions.</p></li>
 *
 *   <li><p>Element names are inferred in the same way than attribute names (see above),
 *       except that the first character is converted to an upper-case character using
 *       the {@link SpatialMetadataFormat#toElementName(String)} method.</p></li>
 *
 *   <li><p><b>Special case for lists:</b> if an element is not found, then this class looks
 *       for an element having a name in the singular form, where the name is inferred from
 *       the UML identifier and {@link SpatialMetadataFormat#toComponentName}. The intend is
 *       to support the case where the XML tree has been simplified with the replacement of
 *       collections by singletons.</p></li>
 * </ul>
 *
 * @param <T> The metadata interface implemented by the proxy.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.06
 * @module
 */
final class MetadataProxy<T> implements InvocationHandler {
    /**
     * {@code true} for enabling the process of a few Geotk-specific special cases. This field
     * should always be {@code true}. It is defined mostly as a way to spot every places where
     * some special cases are defined.
     *
     * @see #getAttributeLength(String)
     * @see #getAttributeAsInteger(String, int)
     */
    private static final boolean SPECIAL_CASE = true;

    /**
     * The interface implemented by the proxy.
     */
    final Class<T> interfaceType;

    /**
     * The metadata accessor. This is used for fetching the value of an attribute. The name of
     * the attribute is inferred from the method name using the {@linkplain #namesMapping} map.
     */
    final MetadataNodeParser accessor;

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
     * The children created up to date. This is used only when the return type of some
     * invoked methods is a {@link java.util.Collection}, {@link java.util.List} or an
     * other metadata interface.
     * <p>
     * The keys are method names (instead than attribute names) because they are
     * usually internalized by the JVM, which is not the case of the attribute names.
     */
    private transient Map<String, Object> childs;

    /**
     * Creates a new proxy having the same properties than the given one except for the index.
     * This is used for creating elements in a list.
     */
    private MetadataProxy(final MetadataProxy<T> prototype, final int index) {
        interfaceType = prototype.interfaceType;
        namesMapping  = prototype.namesMapping;
        accessor      = prototype.accessor;
        this.index    = index;
    }

    /**
     * Creates a new proxy for the given metadata accessor.
     */
    MetadataProxy(final Class<T> type, final MetadataNodeParser accessor) {
        interfaceType = type;
        this.accessor = accessor;
        this.index    = -1;
        final IIOMetadataFormat format = accessor.format;
        if (format instanceof SpatialMetadataFormat) {
            namesMapping = ((SpatialMetadataFormat) format).getElementNames(accessor.name());
        } else {
            namesMapping = null;
        }
    }

    /**
     * Returns a new instance of a proxy class for the specified metadata interface.
     *
     * @param  type     The interface for which to create a proxy instance.
     * @param  accessor The metadata accessor.
     * @throws IllegalArgumentException If the given type is not a valid interface
     *         (see {@link Proxy} javadoc for a list of the conditions).
     */
    static <T> T newProxyInstance(final Class<T> type, final MetadataNodeParser accessor) {
        return type.cast(Proxy.newProxyInstance(MetadataProxy.class.getClassLoader(),
                new Class<?>[] {type}, new MetadataProxy<>(type, accessor)));
    }

    /**
     * Returns a new instance of a proxy class with the same properties than this instance
     * but a different index. This is used for creating elements in a list.
     */
    final T newProxyInstance(final int index) {
        final Class<T> type = interfaceType;
        return type.cast(Proxy.newProxyInstance(MetadataProxy.class.getClassLoader(),
                new Class<?>[] {type}, new MetadataProxy<>(this, index)));
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
        final int length = methodName.length();
        switch (length - offset) {
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
                return new StringBuilder(length - offset)
                        .append(Character.toLowerCase(methodName.charAt(offset)))
                        .append(methodName, offset+1, length).toString();
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
     * @return The type to use, which is guaranteed to be assignable to the method type.
     * @throws IllegalArgumentException If the named element does not exist or does not define objects.
     */
    private Class<?> getElementClass(final String name, final Class<?> methodType) throws IllegalArgumentException {
        final IIOMetadataFormat format = accessor.format;
        final int valueType = format.getObjectValueType(name);
        if (valueType != IIOMetadataFormat.VALUE_NONE) {
            Class<?> declaredType = format.getObjectClass(name); // Not allowed to be null.
            if (valueType == IIOMetadataFormat.VALUE_LIST) {
                declaredType = Classes.changeArrayDimension(declaredType, 1);
            }
            if (methodType == null || methodType.isAssignableFrom(declaredType)) {
                return declaredType;
            }
        }
        return methodType;
    }

    /**
     * Casts the {@code type} class to represent a subclass of the class represented by the
     * {@code sub} argument. Checks that the cast is valid, and returns {@code null} if it
     * is not.
     * <p>
     * This method performs the same work than
     * <code>type.{@linkplain Class#asSubclass(Class) asSubclass}(sub)</code>,
     * except that {@code null} is returned instead than throwing an exception
     * if the cast is not valid or if any of the argument is {@code null}.
     *
     * @param  <U>  The compile-time bounds of the {@code sub} argument.
     * @param  type The class to cast to a sub-class, or {@code null}.
     * @param  sub  The subclass to cast to, or {@code null}.
     * @return The {@code type} argument casted to a subclass of the {@code sub} argument,
     *         or {@code null} if this cast can not be performed.
     *
     * @see Class#asSubclass(Class)
     */
    @SuppressWarnings("unchecked")
    private static <U> Class<? extends U> asSubclassOrNull(final Class<?> type, final Class<U> sub) {
        // Design note: We are required to return null if 'sub' is null (not to return 'type'
        // unchanged), because if we returned 'type', we would have an unsafe cast if this
        // method is invoked indirectly from a parameterized method.
        return (type != null && sub != null && sub.isAssignableFrom(type)) ? (Class) type : null;
    }

    /**
     * For an attribute containing an array, return the length of that array.
     * This is used only for implementing a few {@link #SPECIAL_CASE special cases}.
     */
    private int getAttributeLength(final String name) {
        final int[] values = accessor.getAttributeAsIntegers(name, false);
        return (values != null) ? values.length : 0;
    }

    /**
     * For an attribute containing an array, return the attribute at the given index from that
     * array. This is used only for implementing a few {@link #SPECIAL_CASE special cases}.
     */
    private int getAttributeAsInteger(final String name, final int index) {
        final int[] values = accessor.getAttributeAsIntegers(name, false);
        return (values != null) ? values[index] : 0;
    }

    /**
     * Invoked when a method from the metadata interface has been invoked.
     *
     * @param  proxy  The proxy instance that the method was invoked on.
     * @param  method The method from the interface which have been invoked.
     * @param  args   The arguments, or {@code null} if the method takes no argument.
     * @return The value to return from the method invocation on the proxy instance.
     * @throws UnsupportedOperationException If the given method is not supported.
     * @throws IllegalArgumentException If {@code args} contains a value while none was expected.
     * @throws IllegalStateException If the attribute value can not be converted to the return type.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException
    {
        /*
         * We accept only calls to getter methods, except for a few non-final
         * methods defined in Object that we need to define for compliance.
         */
        final String methodName = method.getName();
        final int numArgs = (args != null) ? args.length : 0;
        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            switch (numArgs) {
                case 0: {
                    if (methodName.equals("toString")) {
                        return toProxyString();
                    }
                    if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                    break;
                }
                case 1: {
                    if (methodName.equals("equals")) {
                        return proxy == args[0];
                    }
                    break;
                }
            }
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.UNKNOWN_COMMAND_$1, methodName));
        }
        /*
         * If an argument is provided to the method, this is an error since we don't know
         * how to handle that, except for a few hard-coded special cases.
         */
        if (numArgs != 0) {
            if (SPECIAL_CASE && numArgs == 1) {
                final Object arg = args[0];
                if (arg instanceof Integer) {
                    final int dim = (Integer) arg;
                    if (proxy instanceof GridEnvelope) {
                        switch (methodName) {
                            case "getLow":  return getAttributeAsInteger("low",  dim);
                            case "getHigh": return getAttributeAsInteger("high", dim);
                            case "getSpan": return getAttributeAsInteger("high", dim) -
                                                   getAttributeAsInteger("low",  dim) + 1;
                        }
                    }
                }
            }
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_$1, methodName));
        }
        /*
         * Gets the name of the attribute to fetch, and set the accessor
         * child index on the children represented by this proxy (if any).
         */
        final MetadataNodeParser accessor = this.accessor;
        final String name = getAttributeName(methodName);
        if (index >= 0) {
            accessor.selectChild(index);
        } else {
            accessor.selectParent();
        }
        /*
         * First, process the cases that are handled in a special way. The order is significant:
         * if the target type is some generic type like java.lang.Object, then we want to select
         * the method performing the less transformation (String if the target type is Object,
         * Double rather than Integer if the target type is Number).
         */
        final Class<?> targetType = method.getReturnType();
        if (targetType.equals(Double.TYPE)) {
            Double value = accessor.getAttributeAsDouble(name);
            if (value == null) value = Double.NaN;
            return value;
        }
        if (targetType.equals(Float.TYPE)) {
            Float value = accessor.getAttributeAsFloat(name);
            if (value == null) value = Float.NaN;
            return value;
        }
        if (targetType.equals(Long.TYPE)) {
            Integer value = accessor.getAttributeAsInteger(name);
            return Long.valueOf((value != null) ? value.longValue() : 0L);
        }
        if (targetType.equals(Integer.TYPE)) {
            Integer value = accessor.getAttributeAsInteger(name);
            if (value == null) {
                value = 0;
                if (SPECIAL_CASE) {
                    if (methodName.equals("getDimension") && proxy instanceof GridEnvelope) {
                        value = Math.max(getAttributeLength("low"), getAttributeLength("high"));
                    }
                }
            }
            return value;
        }
        if (targetType.equals(Short.TYPE)) {
            Integer value = accessor.getAttributeAsInteger(name);
            return Short.valueOf((value != null) ? value.shortValue() : (short) 0);
        }
        if (targetType.equals(Byte.TYPE)) {
            Integer value = accessor.getAttributeAsInteger(name);
            return Byte.valueOf((value != null) ? value.byteValue() : (byte) 0);
        }
        if (targetType.equals(Boolean.TYPE)) {
            Boolean value = accessor.getAttributeAsBoolean(name);
            if (value == null) value = Boolean.FALSE;
            return value;
        }
        if (targetType.isAssignableFrom(String     .class)) return accessor.getAttribute          (name);
        if (targetType.isAssignableFrom(Double     .class)) return accessor.getAttributeAsDouble  (name);
        if (targetType.isAssignableFrom(Float      .class)) return accessor.getAttributeAsFloat   (name);
        if (targetType.isAssignableFrom(Integer    .class)) return accessor.getAttributeAsInteger (name);
        if (targetType.isAssignableFrom(Boolean    .class)) return accessor.getAttributeAsBoolean (name);
        if (targetType.isAssignableFrom(String[]   .class)) return accessor.getAttributeAsStrings (name, false);
        if (targetType.isAssignableFrom(double[]   .class)) return accessor.getAttributeAsDoubles (name, false);
        if (targetType.isAssignableFrom(float[]    .class)) return accessor.getAttributeAsFloats  (name, false);
        if (targetType.isAssignableFrom(int[]      .class)) return accessor.getAttributeAsIntegers(name, false);
        if (targetType.isAssignableFrom(Date       .class)) return accessor.getAttributeAsDate    (name);
        if (targetType.isAssignableFrom(NumberRange.class)) return accessor.getAttributeAsRange   (name);
        if (targetType.isAssignableFrom(Citation   .class)) return accessor.getAttributeAsCitation(name);
        if (targetType.isAssignableFrom(InternationalString.class)) {
            return SimpleInternationalString.wrap(accessor.getAttribute(name));
        }
        if (targetType.isAssignableFrom(Unit.class)) {
            final Class<?> bounds = Classes.boundOfParameterizedProperty(method);
            return accessor.getAttributeAsUnit(name, asSubclassOrNull(bounds, Quantity.class));
        }
        if (SPECIAL_CASE && Character.isLowerCase(name.charAt(0))) {
            /*
             * We have not yet defined a good public API for those cases. For the time being,
             * we use the case of the node name in order to detect if we have an attribute
             * instead than an element.
             */
            if (targetType.isAssignableFrom(GeneralDirectPosition.class)) {
                final double[] ordinates = accessor.getAttributeAsDoubles(name, false);
                return (ordinates != null) ? new GeneralDirectPosition(ordinates) : null;
            }
            if (targetType.isAssignableFrom(GeneralGridCoordinates.class)) {
                final int[] ordinates = accessor.getAttributeAsIntegers(name, false);
                return (ordinates != null) ? new GeneralGridCoordinates(ordinates) : null;
            }
        }
        if (targetType.isAssignableFrom(List.class)) {
            /*
             * The return type is a list or a collection. If the collection elements are some
             * simple types, then we assume that it still an attribute. We do not cache this
             * value because we want to recompute it on next method call (the returned list
             * is not "live").
             */
            Class<?> componentType = Classes.boundOfParameterizedProperty(method);
            if (componentType.isAssignableFrom(String.class)) {
                return UnmodifiableArrayList.wrap(accessor.getAttributeAsStrings(name, false));
            }
            if (componentType.isAssignableFrom(InternationalString.class)) {
                return UnmodifiableArrayList.wrap(SimpleInternationalString.wrap(
                        accessor.getAttributeAsStrings(name, false)));
            }
            if (componentType.isAssignableFrom(Citation.class)) {
                return Collections.singletonList(accessor.getAttributeAsCitation(name));
            }
            /*
             * Assume a nested element. We will create a "live" list and cache it for future
             * reuse. The type of list elements may be a subtype of 'componentType' because
             * IIOMetadataFormat may specify restrictions.
             */
            if (childs == null) {
                childs = new HashMap<>();
            }
            List<?> list = (List<?>) childs.get(methodName);
            if (list == null) {
                String elementName = SpatialMetadataFormat.toElementName(name);
                final MetadataNodeParser acc;
                try {
                    acc = new MetadataNodeParser(accessor, elementName, "#auto");
                } catch (IllegalArgumentException e) {
                    /*
                     * This exception happen when no node for 'elementName' is defined in the
                     * IIOMetadataFormat used by the accessor. For example, DiscoveryMetadata
                     * node in stream SpatialMetadataFormat omits the 'languages' attribute.
                     */
                    Logging.recoverableException(MetadataNodeParser.LOGGER, interfaceType, methodName, e);
                    return null;
                } catch (NoSuchElementException e) {
                    // There is no value for this node.
                    return Collections.emptyList();
                }
                /*
                 * At this point we have a MetadataNodeParser to a node which is known to exist.
                 * This node may have no children, in which case we need to wraps the singleton
                 * in a list.
                 */
                if (acc.allowsChildren()) {
                    componentType = getElementClass(acc.childPath, componentType);
                    list = acc.newProxyList(componentType);
                } else {
                    componentType = getElementClass(elementName, componentType);
                    list = Collections.singletonList(acc.newProxyInstance(componentType));
                }
                childs.put(methodName, list);
            }
            return list;
        }
        /*
         * Code lists case. Only existing instances are returned; no new instance is created.
         */
        if (CodeList.class.isAssignableFrom(targetType)) {
            @SuppressWarnings({"unchecked","rawtypes"})
            final CodeList code = accessor.getAttributeAsCode(name, (Class) targetType);
            return code;
        }
        /*
         * For all other types, assume a nested child element.
         * A new proxy will be created for the nested child.
         */
        if (childs == null) {
            childs = new HashMap<>();
        }
        Object child = childs.get(methodName);
        if (child == null) {
            try {
                // Each of the last 3 lines may throw, directly or indirectly, an IllegalArgumentException.
                final String   elementName = SpatialMetadataFormat.toElementName(name);
                final Class<?> elementType = getElementClass(elementName, targetType);
                final MetadataNodeParser acc = new MetadataNodeParser(accessor, elementName, "#auto");
                child = acc.isEmpty() ? Void.TYPE : acc.newProxyInstance(elementType);
            } catch (IllegalArgumentException e) {
                /*
                 * Report the warning and remember that we can not return a value for this
                 * element, so we don't try again next time.  We use a lower warning level
                 * since this exception can be considered normal (IIOMetadataFormat does
                 * not define every attributes).
                 */
                accessor.warning(Level.FINE, interfaceType, methodName, e);
                child = Void.TYPE;
            } catch (NoSuchElementException e) {
                // There is no value for this node.
                child = Void.TYPE;
            }
            childs.put(methodName, child);
        }
        return (child != Void.TYPE) ? child : null;
    }

    /**
     * Sets the warning level of all proxy instances in the given collection.
     *
     * @param instances The collections of proxy instances.
     * @param level The new warning level.
     */
    static void setWarningLevel(final Collection<?> instances, final Level level) {
        for (final Object proxy : instances) {
            if (proxy instanceof MetadataProxyList<?>) {
                ((MetadataProxyList<?>) proxy).setWarningLevel(level);
            } else if (proxy instanceof Collection<?>) {
                setWarningLevel((Collection<?>) proxy, level);
            } else if (proxy != null) {
                final MetadataProxy<?> handler = (MetadataProxy<?>) Proxy.getInvocationHandler(proxy);
                if (!level.equals(handler.accessor.setWarningLevel(level))) {
                    // Recursive calls only if the level changed. This
                    // is a precaution against infinite recursivity.
                    final Map<String, Object> childs = handler.childs;
                    if (childs != null) {
                        setWarningLevel(childs.values(), level);
                    }
                }
            }
        }
    }

    /**
     * Returns a string representation of the proxy. This is mostly for debugging
     * purpose and may change in any future version.
     */
    private String toProxyString() {
        if (index >= 0) {
            return Classes.getShortName(interfaceType) + '[' + index + ']';
        }
        return accessor.toString(interfaceType);
    }

    /**
     * Returns a string representation of the {@linkplain #accessor}, but declaring the
     * class as {@code MetadataProxy} instead than {@code MetadataNodeParser}.
     */
    @Override
    public String toString() {
        return accessor.toString(getClass());
    }
}
