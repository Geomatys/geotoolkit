/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.text;

import java.util.List;
import java.util.ConcurrentModificationException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.geotoolkit.naming.DefaultScopedName;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface.
 * See package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class ScopedNameAdapter extends XmlAdapter<DefaultScopedName,ScopedName> {
    /**
     * The factory to use for creating names.
     * Will be created only if needed.
     */
    private transient NameFactory factory;

    /**
     * Empty constructor for JAXB only.
     */
    public ScopedNameAdapter() {
    }

    /**
     * Recreates a new name for the given name, using the given factory.
     * This is used in order to get a Geotoolkit implementation from an
     * arbitrary implementation.
     */
    static GenericName wrap(final GenericName value, final NameFactory factory) {
        final List<? extends LocalName> parsedNames = value.getParsedNames();
        final CharSequence[] names = new CharSequence[parsedNames.size()];
        int i=0;
        for (final LocalName name : parsedNames) {
            // Asks for the unlocalized name, since we are going to marshal that.
            names[i++] = name.toInternationalString().toString(null);
        }
        if (i != names.length) {
            throw new ConcurrentModificationException();
        }
        return factory.createGenericName(value.scope(), names);
    }

    /**
     * Does the link between a {@link ScopedName} and the string associated.
     * JAXB calls automatically this method at marshalling-time.
     *
     * @param value The implementing class for this metadata value.
     * @return A {@linkplain DefaultScopedName scoped name} which represents the metadata value.
     */
    @Override
    public DefaultScopedName marshal(final ScopedName value) {
        if (value == null) {
            return null;
        }
        if (value instanceof DefaultScopedName) {
            return (DefaultScopedName) value;
        }
        NameFactory factory = this.factory;
        if (factory == null) {
            // No need to synchronize. This is not a big deal if the factory is fetched twice.
            this.factory = factory = LocalNameAdapter.getNameFactory();
        }
        /*
         * The following cast should not fail  because we asked specifically for the
         * DefaultNameFactory instance (which is known to create DefaultLocalName or
         * DefaultScopedName instances),  and the names array should contains two or
         * more elements (otherwise the argument value should have been a LocalName).
         */
        return (DefaultScopedName) wrap(value, factory);
    }

    /**
     * Does the link between {@linkplain DefaultScopedName scoped names} and the way they
     * will be unmarshalled. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The {@linkplain DefaultScopedName scoped name} value.
     * @return The implementing class for this string.
     */
    @Override
    public ScopedName unmarshal(final DefaultScopedName value) {
        return value;
    }
}
