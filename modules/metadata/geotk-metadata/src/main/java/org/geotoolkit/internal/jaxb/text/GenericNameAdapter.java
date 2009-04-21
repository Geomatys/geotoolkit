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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.util.GenericName;

import org.geotoolkit.naming.AbstractName;
import org.geotoolkit.naming.DefaultTypeName;
import org.geotoolkit.naming.DefaultLocalName;
import org.geotoolkit.naming.DefaultScopedName;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.xml.Namespaces;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface.
 * See package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
public final class GenericNameAdapter extends XmlAdapter<GenericNameAdapter, GenericName> {
    /**
     * The generic name to be marshalled.
     */
    private AbstractName name;

    /**
     * Empty constructor for JAXB only.
     */
    public GenericNameAdapter() {
    }

    /**
     * Wraps an name at marshalling-time.
     *
     * @param name The metadata value to marshall.
     */
    private GenericNameAdapter(final AbstractName name) {
        this.name = name;
    }

    /**
     * Ensures that the {@linkplain #name} is not already defined.
     *
     * @throws IllegalStateException If a name is already defined.
     */
    private void ensureUndefined() throws IllegalStateException {
        if (name != null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, "name"));
        }
    }

    /**
     * Returns the {@code LocalName} generated from the metadata value.
     * This method is called at marshalling-time by JAXB.
     *
     * @return The current name, or {@code null} if none.
     */
    @XmlElement(name = "LocalName", namespace = Namespaces.GCO)
    public DefaultLocalName getLocalName() {
        final AbstractName name = this.name;
        return (name instanceof DefaultLocalName) ? (DefaultLocalName) name : null;
    }

    /**
     * Sets the value for the {@code LocalName}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param name The new name.
     * @throws IllegalStateException If a name is already defined.
     */
    public void setLocalName(final DefaultLocalName name) throws IllegalStateException {
        ensureUndefined();
        this.name = name;
    }

    /**
     * Returns the {@code ScopedName} generated from the metadata value.
     * This method is called at marshalling-time by JAXB.
     *
     * @return The current name, or {@code null} if none.
     */
    @XmlElement(name = "ScopedName", namespace = Namespaces.GCO)
    public DefaultScopedName getScopedName() {
        final AbstractName name = this.name;
        return (name instanceof DefaultScopedName) ? (DefaultScopedName) name : null;
    }

    /**
     * Sets the value for the {@code ScopedName}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param name The new name.
     * @throws IllegalStateException If a name is already defined.
     */
    public void setScopedName(final DefaultScopedName name) throws IllegalStateException {
        ensureUndefined();
        this.name = name;
    }

    /**
     * Returns the {@code TypeName} generated from the metadata value.
     * This method is called at marshalling-time by JAXB.
     *
     * @return The current name, or {@code null} if none.
     */
    @XmlElement(name = "TypeName", namespace = Namespaces.GCO)
    public DefaultTypeName getTypeName() {
        final AbstractName name = this.name;
        return (name instanceof DefaultTypeName) ? (DefaultTypeName) name : null;
    }

    /**
     * Sets the value for the {@code TypeName}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param name The new name.
     * @throws IllegalStateException If a name is already defined.
     */
    public void setTypeName(final DefaultTypeName name) throws IllegalStateException {
        ensureUndefined();
        this.name = name;
    }

    /**
     * Does the link between an {@link AbstractName} and the adapter associated.
     * JAXB calls automatically this method at marshalling-time.
     *
     * @param value The implementing class for this metadata value.
     * @return An adapter which represents the metadata value.
     */
    @Override
    public GenericNameAdapter marshal(final GenericName value) {
        if (value == null) {
            return null;
        }
        final AbstractName name;
        if (value instanceof AbstractName) {
            name = (AbstractName) value;
        } else {
            /*
             * Following cast should be safe because the getNameFactory() method asked specifically
             * for a DefaultNameFactory instance, which is known to create AbstractName instances.
             */
            name = (AbstractName) ScopedNameAdapter.wrap(value, LocalNameAdapter.getNameFactory());
        }
        return new GenericNameAdapter(name);
    }

    /**
     * Does the link between adapters and the way they will be unmarshalled.
     * JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter value.
     * @return The implementing class.
     */
    @Override
    public GenericName unmarshal(final GenericNameAdapter value) {
        return (value != null) ? value.name : null;
    }
}
