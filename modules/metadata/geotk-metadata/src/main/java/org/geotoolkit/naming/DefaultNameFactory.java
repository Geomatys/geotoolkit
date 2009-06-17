/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.naming;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.DefaultInternationalString;
import static org.geotoolkit.naming.AbstractName.ensureNonNull;
import static org.geotoolkit.naming.DefaultNameSpace.DEFAULT_SEPARATOR_STRING;


/**
 * A factory for {@link AbstractName} objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @see org.geotoolkit.factory.FactoryFinder#getNameFactory
 *
 * @since 2.1
 * @module
 */
public class DefaultNameFactory extends Factory implements NameFactory {
    /**
     * Creates a new factory. Users should not invoke this constructor directly.
     * Use {@link org.geotoolkit.factory.FactoryFinder#getNameFactory} instead.
     */
    public DefaultNameFactory() {
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
     *
     * @param name
     *          The name of the namespace to be returned. This argument can be created using
     *          <code>{@linkplain #createGenericName createGenericName}(null, parsedNames)</code>.
     * @param properties
     *          An optional map of properties to be assigned to the namespace. Recognized entries
     *          are:
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Purpose</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@code "separator"}&nbsp;</td>
     *     <td nowrap>&nbsp;The separator to insert between {@linkplain GenericName#getParsedNames
     *     parsed names} in that namespace. For HTTP namespace, it is {@code "."}. For URN namespace,
     *     it is typically {@code ":"}.</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@code "separator.head"}&nbsp;</td>
     *     <td nowrap>&nbsp;The separator to insert between the namespace and the
     *     {@linkplain GenericName#head head}. For HTTP namespace, it is {@code "://"}.
     *     For URN namespace, it is typically {@code ":"}. If this entry is omitted, then
     *     the default is the same value than the {@code "separator"} entry.</td>
     *   </tr>
     * </table>
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
        final boolean isEmpty = (separator.length() == 0);
        if (isEmpty || headSeparator.length() == 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$1, isEmpty ? "separator" : "separator.head"));
        }
        return DefaultNameSpace.forName(name, headSeparator, separator);
    }

    /**
     * @deprecated Replaced by {@link #createNameSpace(GenericName, Map)}.
     *
     * @param name
     *          The name of the namespace to be returned. This argument can be created using
     *          <code>{@linkplain #createGenericName createGenericName}(null, parsedNames)</code>.
     * @param headSeparator
     *          The separator to insert between the namespace and the {@linkplain AbstractName#head
     *          head}. For HTTP namespace, it is {@code "://"}. For URN namespace, it is typically
     *          {@code ":"}.
     * @param separator
     *          The separator to insert between {@linkplain AbstractName#getParsedNames parsed names}
     *          in that namespace. For HTTP namespace, it is {@code "."}. For URN namespace, it is
     *          typically {@code ":"}.
     * @return A namespace having the given name and separator.
     *
     * @since 3.00
     */
    @Override
    @Deprecated
    public NameSpace createNameSpace(final GenericName name,
            final String headSeparator, final String separator)
    {
        ensureNonNull("separator",     separator);
        ensureNonNull("headSeparator", headSeparator);
        final Map<String,String> properties = new HashMap<String,String>(4);
        properties.put("separator", separator);
        properties.put("separator.head", headSeparator);
        return createNameSpace(name, properties);
    }

    /**
     * Creates a namespace having the given name and using the
     * {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR default separator}.
     *
     * @param name The name of the namespace to be returned. This argument can be created using
     *        <code>{@linkplain #createGenericName createGenericName}(null, parsedNames)</code>.
     * @return A namespace having the given name and separator.
     *
     * @since 3.00
     */
    public NameSpace createNameSpace(final GenericName name) {
        return createNameSpace(name, null);
    }

    /**
     * Creates a local name from the given character sequence. The default implementation
     * returns a new {@linkplain DefaultLocalName} instance.
     *
     * @param  scope The {@linkplain GenericName#scope scope} of the local
     *         name to be created, or {@code null} for a global namespace.
     * @param  name The local name as a string or an international string.
     * @return The local name for the given character sequence.
     * @throws IllegalArgumentException If the {@code name} argument is null.
     *
     * @since 3.00
     */
    @Override
    public LocalName createLocalName(NameSpace scope, CharSequence name)
            throws IllegalArgumentException
    {
        if (scope instanceof DefaultNameSpace) {
            // Following may return a cached instance.
            return ((DefaultNameSpace) scope).local(name, null);
        }
        return new DefaultLocalName(scope, name);
    }

    /**
     * Creates a local or scoped name from an array of parsed names. The default implementation
     * returns an instance of {@link DefaultLocalName} if the length of the {@code parsedNames}
     * array is 1, or an instance of {@link DefaultScopedName} if the length of the array is 2
     * or more.
     *
     * @param  scope The {@linkplain AbstractName#scope scope} of the generic name to
     *         be created, or {@code null} for a global namespace.
     * @param  parsedNames The local names as an array of {@linkplain String strings} or
     *         {@linkplain InternationalString international strings}. This array must
     *         contains at least one element.
     * @return The generic name for the given parsed names.
     * @throws IllegalArgumentException If the given array is empty.
     *
     * @since 3.00
     */
    @Override
    public GenericName createGenericName(NameSpace scope, CharSequence[] parsedNames)
            throws IllegalArgumentException
    {
        ensureNonNull("parsedNames", parsedNames);
        switch (parsedNames.length) {
            default: return new DefaultScopedName(scope, Arrays.asList(parsedNames));
            case 1:  return createLocalName(scope, parsedNames[0]); // User may override.
            case 0:  throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
        }
    }

    /**
     * Creates a local name from a {@linkplain DefaultLocalName#scope scope} and a
     * {@linkplain DefaultLocalName#toString name}. The {@code scope} argument identifies the
     * {@linkplain DefaultNameSpace name space} in which the local name will be created. The
     * {@code name} argument is taken verbatism as the string representation of the local name.
     *
     * @param scope The scope, or {@code null} for the global one.
     * @param name  The unlocalized name. May be {@code null} if {@code localizedName} is non-null.
     * @param localizedName A localized version of the name, or {@code null} if none.
     * @return The local name.
     *
     * @deprecated Replaced by {@link #createNameSpace createNameSpace} for the scope argument,
     *             and {@link #createGenericName createGenericName} for the name and localized
     *             name arguments.
     */
    @Override
    @Deprecated
    public LocalName createLocalName(GenericName scope, String name, InternationalString localizedName) {
        final CharSequence n = merge(name, localizedName);
        ensureNonNull("name", n);
        return new DefaultLocalName(DefaultNameSpace.forName(scope,
                DEFAULT_SEPARATOR_STRING, DEFAULT_SEPARATOR_STRING), n);
    }

    /**
     * @deprecated Not implemented.
     */
    @Override
    @Deprecated
    public ScopedName createScopedName(GenericName scope, String name, InternationalString localizedName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns a {@linkplain String string} or an {@linkplain InternationalString international
     * string} for the given argument. If both arguments are non-null, returns an international
     * string where {@code toString(null)} returns the unlocalized name.
     *
     * @deprecated To be deleted after we removed the deprecated methods from {@link NameFactory}.
     */
    @Deprecated
    private CharSequence merge(final String name, final InternationalString localizedName) {
        if (localizedName == null) {
            return name;
        }
        if (name == null || name.equals(localizedName.toString(null))) {
            return localizedName;
        }
        if (localizedName.getClass().equals(DefaultInternationalString.class)) {
            final DefaultInternationalString def = (DefaultInternationalString) localizedName;
            final Map<Locale,String> names = new HashMap<Locale,String>();
            synchronized (def) {
                for (final Locale locale : def.getLocales()) {
                    names.put(locale, def.toString(locale));
                }
            }
            names.put(null, name);
            return new DefaultInternationalString(names);
        }
        return new InternationalName(name, localizedName);
    }

    /**
     * Constructs a generic name from a qualified name. This method splits the given name around a
     * separator inferred from the given scope, or the {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR
     * default separator} if the given scope is null.
     *
     * @param  scope The {@linkplain AbstractName#scope scope} of the generic name to
     *         be created, or {@code null} for a global namespace.
     * @param  name The qualified name, as a sequence of names separated by a scope-dependant
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
        final List<String> names = new ArrayList<String>();
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
     *   <li>{@link Identifier}, its {@linkplain Identifier#getCode code} to be parsed as a generic
     *       name using the {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR default separator}.</li>
     * </ul>
     *
     * @param  value The object to cast into an array of generic names.
     * @return The generic names. May be a direct reference to {@code value}.
     * @throws IllegalArgumentException if {@code value} is null.
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
            if (value instanceof Collection) {
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
        throw new ClassCastException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, value.getClass()));
    }
}
