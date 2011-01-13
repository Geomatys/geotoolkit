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
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.metadata.Obligation;
import org.geotoolkit.internal.CodeLists;


/**
 * JAXB adapter for {@link Obligation}, in order to wraps the value in an element
 * complying with ISO-19139 standard. See package documentation for more information
 * about the handling of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 2.5
 * @module
 */
public final class ObligationAdapter extends XmlAdapter<String, Obligation> {
    /**
     * Returns the obligation enumeration for the given name.
     *
     * @param value The obligation name.
     * @return The obligation enumeration for the given name.
     */
    @Override
    public Obligation unmarshal(String value) {
        return CodeLists.valueOf(Obligation.class, value);
    }

    /**
     * Returns the name of the given obligation.
     *
     * @param value The obligation enumeration.
     * @return The name of the given obligation.
     */
    @Override
    public String marshal(final Obligation value) {
        if (value == null) {
            return null;
        }
        return value.name();
    }
}
