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
import java.util.Locale;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.NoSuchElementException;
import java.util.MissingResourceException;

import org.opengis.annotation.UML;

import org.apache.sis.util.Localized;
import org.apache.sis.util.logging.Logging;


/**
 * Map of property descriptions for a given implementation class. This map is read-only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
final class DescriptionMap extends MetadataMap<String> implements Localized {
    /**
     * The resources bundle for the descriptions in the local given at construction time.
     */
    private final ResourceBundle descriptions;

    /**
     * The UML identifier of the class.
     */
    final String classname;

    /**
     * The number of items in the list, or -1 if not yet computed.
     */
    private int size = -1;

    /**
     * Creates a description map for the specified accessor.
     *
     * @param  accessor    The accessor to use for the metadata.
     * @param  packageBase The base package of interfaces. Must end with {@code '.'}.
     * @param  locale      Determines the locale of values in this map.
     * @param  keyNames    Determines the string representation of keys in the map.
     * @throws MissingResourceException If no resource bundle is found.
     */
    DescriptionMap(final PropertyAccessor accessor, final String packageBase,
            final Locale locale, final org.apache.sis.metadata.KeyNamePolicy keyNames) throws MissingResourceException
    {
        super(accessor, keyNames);
        final Class<?> type = accessor.type;
        final UML uml = type.getAnnotation(UML.class);
        classname = (uml != null) ? uml.identifier() : type.getSimpleName();
        descriptions = ResourceBundle.getBundle(packageBase + "Descriptions", locale);
    }

    /**
     * Returns the locale of the resource bundle which is backing this map.
     * This is not necessarily the same than the locale argument provided at
     * construction time.
     *
     * @return The locale of the resource bundle.
     */
    @Override
    public Locale getLocale() {
        return descriptions.getLocale();
    }

    /**
     * Returns {@code true} if this map is empty. This is a cheaper test than
     * {@link #size()} if the number of items has not yet been determined.
     */
    @Override
    public boolean isEmpty() {
        if (size < 0) {
            if (getString(classname) != null) {
                return false;
            }
            final int c = accessor.count();
            for (int i=0; i<c; i++) {
                if (getString(key(i)) != null) {
                    return false;
                }
            }
            size = 0;
        }
        return size == 0;
    }

    /**
     * Returns the number of entries in this map.
     */
    @Override
    public int size() {
        if (size < 0) {
            int count = 0;
            if (getString(classname) != null) {
                count++;
            }
            final int c = accessor.count();
            for (int i=0; i<c; i++) {
                if (getString(key(i)) != null) {
                    count++;
                }
            }
            size = count;
        }
        return size;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(final Object key) {
        return get(key) != null;
    }

    /**
     * Returns the resource key for the given property index.
     */
    final String key(final int index) {
        String base = classname;
        final Class<?> decl = accessor.getDeclaringClass(index);
        if (decl != null) {
            final UML uml = decl.getAnnotation(UML.class);
            base = (uml != null) ? uml.identifier() : decl.getSimpleName();
        }
        return base + '.' + accessor.name(index, org.apache.sis.metadata.KeyNamePolicy.UML_IDENTIFIER);
    }

    /**
     * Returns the resources for the given key, or {@code null} if none.
     * Missing resources are logged at the {@code FINE} level.
     */
    final String getString(final String key) {
        try {
            return descriptions.getString(key);
        } catch (MissingResourceException e) {
            Logging.recoverableException(DescriptionMap.class, "get", e);
            return null;
        }
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     */
    @Override
    public String get(final Object key) {
        if (key instanceof String) {
            if (((String) key).equalsIgnoreCase("class")) {
                return getString(classname);
            }
            final int index = accessor.indexOf((String) key);
            if (index >= 0) {
                return getString(key(index));
            }
        }
        return null;
    }

    /**
     * Returns an iterator over the entries contained in this map.
     */
    @Override
    final Iterator<Map.Entry<String,String>> iterator() {
        return new Iter();
    }

    /**
     * The iterator over the entries contained in a {@link Entries} set.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     */
    private final class Iter extends MetadataMap<String>.Iter {
        /**
         * The next description to return, or {@code null} if the termination is over.
         */
        private String nextDescription;

        /**
         * Index of {@link #nextDescription}.
         */
        private int nextIndex;

        /**
         * Creates en iterator.
         */
        Iter() {
            nextIndex = -1;
            if ((nextDescription = getString(classname)) == null) {
                // No class description. Move to the first property.
                move();
            }
        }

        /**
         * Moves to the next element.
         */
        private void move() {
            while (++nextIndex < accessor.count()) {
                if ((nextDescription = getString(key(nextIndex))) != null) {
                    return;
                }
            }
            nextDescription = null;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         */
        @Override
        public boolean hasNext() {
            return nextDescription != null;
        }

        /**
         * Returns the next element in the iteration.
         */
        @Override
        public Map.Entry<String,String> next() {
            final String description = nextDescription;
            if (description == null) {
                throw new NoSuchElementException();
            }
            final String key = (nextIndex >= 0) ? accessor.name(nextIndex, keyNames) : "class";
            move();
            return new SimpleEntry<>(key, description);
        }
    }
}
