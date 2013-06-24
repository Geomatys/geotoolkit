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
package org.geotoolkit.naming;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collection;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.TypeName;
import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.MemberName;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.metadata.iso.citation.Citations;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.naming.DefaultNameSpace.DEFAULT_SEPARATOR_STRING;


/**
 * A factory for {@link AbstractName} objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see org.geotoolkit.factory.FactoryFinder#getNameFactory(Hints)
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.util.iso} package.
 */
@Deprecated
@ThreadSafe
public class DefaultNameFactory extends Factory implements NameFactory {
    /**
     * Weak references to the name created by this factory.
     */
    private final WeakHashSet<GenericName> pool;

    /**
     * Creates a new factory. Users should not invoke this constructor directly.
     * Use {@link org.geotoolkit.factory.FactoryFinder#getNameFactory(Hints)} instead.
     */
    public DefaultNameFactory() {
        pool = new WeakHashSet<>(GenericName.class);
    }

    /**
     * Returns the implementor of this factory, which is {@link Citations#GEOTOOLKIT GEOTOOLKIT}.
     *
     * @since 3.19
     */
    @Override
    public Citation getVendor() {
        return Citations.GEOTOOLKIT;
    }

    /**
     * Creates an international string from a set of strings in different locales.
     */
    @Override
    public InternationalString createInternationalString(final Map<Locale, String> strings) {
        ensureNonNull("strings", strings);
        switch (strings.size()) {
            case 0:  throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_DICTIONARY));
            case 1:  return new SimpleInternationalString(strings.values().iterator().next());
            default: return new DefaultInternationalString(strings);
        }
        // Do not cache in the pool, because not all instances are immutable.
    }

    /**
     * Returns the value for the given key in the given properties map, or {@code null} if none.
     */
    private static String getString(final Map<String,?> properties, final String key) {
        if (properties != null) {
            final Object value = properties.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    /**
     * Creates a namespace having the given name. Despite the "create" name, this method tries
     * to returns an existing instance when possible.
     * <p>
     * This method can receive an optional map of properties. Recognized entries are:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Purpose</th>
     *   </tr>
     *   <tr>
     *     <td valign="top" nowrap>&nbsp;{@code "separator"}&nbsp;</td>
     *     <td>The separator to insert between {@linkplain GenericName#getParsedNames() parsed names}
     *     in that namespace. For HTTP namespace, it is {@code "."}. For URN namespace,
     *     it is typically {@code ":"}.</td>
     *   </tr>
     *   <tr>
     *     <td valign="top" nowrap>&nbsp;{@code "separator.head"}&nbsp;</td>
     *     <td>The separator to insert between the namespace and the
     *     {@linkplain GenericName#head() head}. For HTTP namespace, it is {@code "://"}.
     *     For URN namespace, it is typically {@code ":"}. If this entry is omitted, then
     *     the default is the same value than the {@code "separator"} entry.</td>
     *   </tr>
     * </table>
     *
     * @param name
     *          The name of the namespace to be returned. This argument can be created using
     *          <code>{@linkplain #createGenericName createGenericName}(null, parsedNames)</code>.
     * @param properties
     *          An optional map of properties to be assigned to the namespace, or {@code null} if none.
     *
     * @return A namespace having the given name and separator.
     *
     * @since 3.01
     */
    @Override
    public NameSpace createNameSpace(final GenericName name, final Map<String,?> properties) {
        ensureNonNull("name", name);
        String separator = getString(properties, "separator");
        if (separator == null) {
            separator = DefaultNameSpace.DEFAULT_SEPARATOR_STRING;
        }
        String headSeparator = getString(properties, "separator.head");
        if (headSeparator == null) {
            headSeparator = separator;
        }
        final boolean isEmpty = (separator.isEmpty());
        if (isEmpty || headSeparator.isEmpty()) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_1, isEmpty ? "separator" : "separator.head"));
        }
        return DefaultNameSpace.forName(name.toFullyQualifiedName(), headSeparator, separator);
    }

    /**
     * Creates a type name from the given character sequence. The default implementation
     * returns a new or an existing {@link DefaultTypeName} instance.
     *
     * @param  scope The {@linkplain GenericName#scope() scope} of the type
     *         name to be created, or {@code null} for a global namespace.
     * @param  name The type name as a string or an international string.
     * @return The type name for the given character sequence.
     * @throws NullArgumentException If the {@code name} argument is null.
     *
     * @since 3.04
     */
    @Override
    public TypeName createTypeName(final NameSpace scope, final CharSequence name) {
        return pool.unique(new DefaultTypeName(scope, name));
    }

    /**
     * Creates a member name from the given character sequence and attribute type.
     * The default implementation returns a new or an existing {@link DefaultMemberName}
     * instance.
     *
     * @param  scope The {@linkplain GenericName#scope() scope} of the member
     *         name to be created, or {@code null} for a global namespace.
     * @param  name The member name as a string or an international string.
     * @param  attributeType The type of the data associated with the record member.
     * @return The member name for the given character sequence.
     * @throws NullArgumentException If the {@code name} or {@code attributeType} argument is null.
     *
     * @since 3.17
     */
    @Override
    public MemberName createMemberName(final NameSpace scope, final CharSequence name, final TypeName attributeType) {
        return pool.unique(new DefaultMemberName(scope, name, attributeType));
    }

    /**
     * Creates a local name from the given character sequence. The default implementation
     * returns a new or an existing {@link DefaultLocalName} instance.
     *
     * @param  scope The {@linkplain GenericName#scope() scope} of the local
     *         name to be created, or {@code null} for a global namespace.
     * @param  name The local name as a string or an international string.
     * @return The local name for the given character sequence.
     * @throws NullArgumentException If the {@code name} argument is null.
     *
     * @since 3.00
     */
    @Override
    public LocalName createLocalName(final NameSpace scope, final CharSequence name) {
        if (scope instanceof DefaultNameSpace) {
            // Following may return a cached instance.
            return ((DefaultNameSpace) scope).local(name, null);
        }
        return pool.unique(new DefaultLocalName(scope, name));
    }

    /**
     * Creates a local or scoped name from an array of parsed names. The default implementation
     * returns an instance of {@link DefaultLocalName} if the length of the {@code parsedNames}
     * array is 1, or an instance of {@link DefaultScopedName} if the length of the array is 2
     * or more.
     *
     * @param  scope The {@linkplain AbstractName#scope() scope} of the generic name to
     *         be created, or {@code null} for a global namespace.
     * @param  parsedNames The local names as an array of {@linkplain String strings} or
     *         {@linkplain InternationalString international strings}. This array must
     *         contains at least one element.
     * @return The generic name for the given parsed names.
     * @throws NullArgumentException If the given array is empty.
     *
     * @since 3.00
     */
    @Override
    public GenericName createGenericName(final NameSpace scope, final CharSequence... parsedNames) {
        ensureNonNull("parsedNames", parsedNames);
        switch (parsedNames.length) {
            default: return pool.unique(new DefaultScopedName(scope, Arrays.asList(parsedNames)));
            case 1:  return createLocalName(scope, parsedNames[0]); // User may override.
            case 0:  throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
        }
    }

    /**
     * Constructs a generic name from a qualified name. This method splits the given name around a
     * separator inferred from the given scope, or the {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR
     * default separator} if the given scope is null.
     *
     * @param  scope The {@linkplain AbstractName#scope() scope} of the generic name to
     *         be created, or {@code null} for a global namespace.
     * @param  name The qualified name, as a sequence of names separated by a scope-dependent
     *         separator.
     * @return A name parsed from the given string.
     */
    @Override
    public GenericName parseGenericName(final NameSpace scope, final CharSequence name) {
        final String separator;
        if (scope instanceof DefaultNameSpace) {
            separator = ((DefaultNameSpace) scope).separator;
        } else {
            separator = DEFAULT_SEPARATOR_STRING;
        }
        final int s = separator.length();
        final List<String> names = new ArrayList<>();
        int lower = 0;
        final String string = name.toString();
        while (true) {
            final int upper = string.indexOf(separator, lower);
            if (upper >= 0) {
                names.add(string.substring(lower, upper));
                lower = upper + s;
            } else {
                names.add(string.substring(lower));
                break;
            }
        }
        if (names.size() == 1) {
            // Preserves the InternationalString (current implementation of
            // the parsing code above has lost the internationalization).
            return createLocalName(scope, name);
        }
        return createGenericName(scope, names.toArray(new String[names.size()]));
    }

    /**
     * Creates a generic name from the given value. The value may be an instance of
     * {@link GenericName}, {@link Identifier} or {@link CharSequence}. If the given
     * object is not recognized, then this method returns {@code null}.
     *
     * @param  value The object to convert.
     * @return The converted object, or {@code null} if {@code value} is not convertible.
     */
    private GenericName createFromObject(final Object value) {
        ensureNonNull("value", value);
        if (value instanceof GenericName) {
            return (GenericName) value;
        }
        if (value instanceof Identifier) {
            return parseGenericName(null, ((Identifier) value).getCode());
        }
        if (value instanceof CharSequence) {
            return parseGenericName(null, (CharSequence) value);
        }
        return null;
    }

    /**
     * Converts the given value to an array of generic names. If the given value is an instance of
     * {@link GenericName}, {@link String} or any other type enumerated below, then it is converted
     * and returned in an array of length 1. If the given value is an array or a collection, then an
     * array of same length is returned where each element has been converted. Allowed types or
     * element types are:
     * <p>
     * <ul>
     *   <li>{@link GenericName}, to be casted and returned as-is.</li>
     *   <li>{@link CharSequence} (usually a {@link String} or an {@link InternationalString}), to
     *       be parsed as a generic name using the {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR
     *       default separator}.</li>
     *   <li>{@link Identifier}, its {@linkplain Identifier#getCode() code} to be parsed as a generic
     *       name using the {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR default separator}.</li>
     * </ul>
     *
     * @param  value The object to cast into an array of generic names.
     * @return The generic names. May be a direct reference to {@code value}.
     * @throws NullArgumentException if {@code value} is null.
     * @throws ClassCastException if {@code value} can't be casted.
     */
    public GenericName[] toArray(Object value) throws ClassCastException {
        GenericName name = createFromObject(value);
        if (name != null) {
            return new GenericName[] {
                name
            };
        }
        /*
         * Above code checked for a singleton. Now check for a collection or an array.
         * The "jump" loop is just a trick for jumping to the throw clause in case of
         * failure.
         */
jump:   while (true) {
            final Object[] values;
            if (value instanceof Collection<?>) {
                values = ((Collection<?>) value).toArray();
            } else if (value instanceof Object[]) {
                values = (Object[]) value;
            } else {
                break jump;
            }
            if (values instanceof GenericName[]) {
                return (GenericName[]) values;
            }
            final GenericName[] names = new GenericName[values.length];
            for (int i=0; i<values.length; i++) {
                value = values[i];
                name = createFromObject(value);
                if (name == null) {
                    break jump;
                }
                names[i] = name;
            }
            return names;
        }
        throw new ClassCastException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, value.getClass()));
    }
}
