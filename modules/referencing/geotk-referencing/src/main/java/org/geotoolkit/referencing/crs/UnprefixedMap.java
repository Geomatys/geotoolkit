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
package org.geotoolkit.referencing.crs;

import java.util.Map;
import java.util.HashMap;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.InternationalString;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.collection.DerivedMap;

import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.IdentifiedObject.ALIAS_KEY;


/**
 * A map without the {@code "conversion."} prefix in front of property keys. This
 * implementation performs a special processing for the <code>{@linkplain #prefix}.name</code>
 * key: if it doesn't exists, then the plain {@code name} key is used. In other words,
 * this map inherits the {@code "name"} property from the {@linkplain #base} map.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
final class UnprefixedMap extends DerivedMap<String,String,Object> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5603681431606877770L;

    /**
     * The prefix to remove for this map.
     */
    private final String prefix;

    /**
     * {@code true} if the <code>{@linkplain #prefix}.name</code> property exists
     * in the {@linkplain #base base} map. This class will inherit the name and alias
     * from the {@linkplain #base base} map only if this field is set to {@code false}.
     */
    private final boolean hasName, hasAlias;

    /**
     * Creates a new unprefixed map from the specified base map and prefix.
     *
     * @param base   The base map.
     * @param prefix The prefix to remove from the keys in the base map.
     */
    @SuppressWarnings({"unchecked","rawtypes"}) // Okay because values are Object.
    public UnprefixedMap(final Map<String,?> base, final String prefix) {
        super((Map) base, String.class);
        this.prefix = prefix.trim();
        final String  nameKey = this.prefix + IdentifiedObject. NAME_KEY;
        final String aliasKey = this.prefix + IdentifiedObject.ALIAS_KEY;
        boolean hasName  = false;
        boolean hasAlias = false;
        for (final Object value : base.keySet()) {
            final String candidate = value.toString().trim();
            if (keyMatches(nameKey, candidate)) {
                hasName = true;
                if (hasAlias) break;
            } else
            if (keyMatches(aliasKey, candidate)) {
                hasAlias = true;
                if (hasName) break;
            }
        }
        this.hasName  = hasName;
        this.hasAlias = hasAlias;
    }

    /**
     * Removes the prefix from the specified key. If the key doesn't begins with
     * the prefix, then this method returns {@code null}.
     *
     * @param  key A key from the {@linkplain #base} map.
     * @return The key that this view should contains instead of {@code key}, or {@code null}.
     */
    @Override
    protected String baseToDerived(final String key) {
        final int length = prefix.length();
        final String textualKey = key.trim();
        if (textualKey.regionMatches(true, 0, prefix, 0, length)) {
            return textualKey.substring(length).trim();
        }
        if (isPlainKey(textualKey)) {
            return textualKey;
        }
        return null;
    }

    /**
     * Adds the prefix to the specified key.
     *
     * @param  key A key in this map.
     * @return The key stored in the {@linkplain #base} map.
     */
    @Override
    protected String derivedToBase(final String key) {
        final String textualKey = key.trim();
        if (isPlainKey(textualKey)) {
            return textualKey;
        }
        return prefix + textualKey;
    }

    /**
     * Returns {@code true} if the specified candidate is {@code "name"}
     * or {@code "alias"} without prefix. Key starting with {@code "name_"}
     * or {@code "alias_"} are accepted as well.
     */
    private boolean isPlainKey(final String key) {
        return (!hasName  && keyMatches(IdentifiedObject.NAME_KEY,  key)) ||
               (!hasAlias && keyMatches(IdentifiedObject.ALIAS_KEY, key));
    }

    /**
     * Returns {@code true} if the specified candidate matched
     * the specified key name.
     */
    private static boolean keyMatches(final String key, final String candidate) {
        final int length = key.length();
        return candidate.regionMatches(true, 0, key, 0, length) &&
               (candidate.length() == length || candidate.charAt(length) == '_');
    }

    /**
     * Creates a name for the predefined constants in subclasses. The name is a {@link String}
     * object in a fixed locale. In many case this fixed locale is the English one, but for this
     * particular method we take the system default. We do that way because this method is used
     * for the creation of convenience objects only, not for objects created from an official
     * database. Consequently the "unlocalized" name is actually chosen according the user's
     * locale at class initialization time.
     * <p>
     * The same name is also added in a localizable form as an alias. Since the {@link #nameMatches}
     * convenience method checks the alias, it still possible to consider two objects as equivalent
     * even if their names were formatted in different locales.
     */
    static Map<String,?> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString());
        properties.put(ALIAS_KEY, name);
        return properties;
    }
}
