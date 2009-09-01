/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.code;

import java.util.Locale;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.resources.Locales;


/**
 * JAXB adapter for {@link Locale}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 *
 * @param <ValueType> The subclass implementing this adapter.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public abstract class CodeListLocaleAdapter<ValueType extends CodeListLocaleAdapter<ValueType>>
        extends XmlAdapter<ValueType, Locale>
{
    /**
     * A proxy form of the {@link CodeList}.
     */
    protected CodeListProxy proxy;

    /**
     * Empty constructor for subclasses only.
     */
    protected CodeListLocaleAdapter() {
    }

    /**
     * Creates a wrapper for a {@link CodeList}, in order to handle the format specified
     * in ISO-19139.
     *
     * @param proxy The proxy version of {@link CodeList} to be marshalled.
     */
    protected CodeListLocaleAdapter(final CodeListProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Wraps the proxy value into an adapter.
     *
     * @param proxy The proxy version of {@link CodeList}, to be marshalled.
     * @return The adapter that wraps the proxy value.
     */
    protected abstract ValueType wrap(final CodeListProxy proxy);

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A locale which represents the metadata value.
     */
    @Override
    public final Locale unmarshal(final ValueType value) {
        if (value == null) {
            return null;
        }
        return Locales.parse(value.proxy.codeListValue);
    }

    /**
     * Substitutes the locale by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The locale value.
     * @return The adapter for the locale value.
     */
    @Override
    public final ValueType marshal(final Locale value) {
        if (value == null) {
            return null;
        }
        return wrap(new CodeListProxy("LanguageCode", Locales.getLanguage(value)));
    }
}
