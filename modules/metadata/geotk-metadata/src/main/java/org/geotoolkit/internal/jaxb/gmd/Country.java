/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.gmd;

import java.util.Locale;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.resources.Locales;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.code.CodeListProxy;


/**
 * JAXB adapter for {@link Locale}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 * <p>
 * This adapter formats the locale like below:
 *
 * {@preformat xml
 *   <gmd:country>
 *     <gmd:Country codeList="http://(...snip...)" codeListValue="FR">
 *       France
 *     </gmd:Country>
 *   </gmd:country>
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
public final class Country extends XmlAdapter<Country, Locale> {
    /**
     * A proxy form of the {@link CodeList}.
     */
    private CodeListProxy proxy;

    /**
     * Empty constructor for JAXB only.
     */
    public Country() {
    }

    /**
     * Creates a new adapter for the given locale.
     */
    Country(final Locale value, final Locale vl) {
        proxy = new CodeListProxy("ML_gmxCodelists.xml", "Country",
                value.getCountry(), value.getDisplayCountry(vl));
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @XmlElement(name = "Country")
    public CodeListProxy getElement() {
        return proxy;
    }

    /**
     * Invoked by JAXB on unmarshalling.
     *
     * @param proxy The unmarshalled value.
     */
    public void setElement(final CodeListProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Substitutes the locale by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The locale value.
     * @return The adapter for the locale value.
     */
    @Override
    public final Country marshal(final Locale value) {
        return (value != null && !value.getCountry().isEmpty())
                ? new Country(value, MarshalContext.getLocale()) : null;
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A locale which represents the metadata value.
     */
    @Override
    public final Locale unmarshal(final Country value) {
        if (value != null && value.proxy != null) {
            String code = value.proxy.codeListValue;
            if (code != null && !(code = code.trim()).isEmpty()) {
                return Locales.unique(new Locale("", code));
            }
        }
        return null;
    }
}
