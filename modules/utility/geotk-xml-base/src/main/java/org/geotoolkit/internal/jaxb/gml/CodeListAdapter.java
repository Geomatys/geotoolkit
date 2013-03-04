/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.gml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.util.CodeList;
import org.apache.sis.util.iso.Types;


/**
 * JAXB adapter for GML code lists, in order to integrate the value in an element
 * complying with GML standard. A subclass must exist for each code list.
 *
 * @param <BoundType> The code list being adapted.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.00)
 * @module
 */
public abstract class CodeListAdapter<BoundType extends CodeList<BoundType>> extends XmlAdapter<CodeListProxy,BoundType> {
    /**
     * Empty constructor for subclasses only.
     */
    protected CodeListAdapter() {
    }

    /**
     * Forces the initialization of the given code list class, since some
     * calls to {@link CodeList#valueOf} are done whereas the constructor
     * has not already been called.
     *
     * @param <T>  The code list type.
     * @param type The code list class to initialize.
     */
    protected static <T extends CodeList<T>> void ensureClassLoaded(final Class<T> type) {
        final String name = type.getName();
        try {
            Class.forName(name, true, type.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new TypeNotPresentException(name, ex); // Should never happen.
        }
    }

    /**
     * Returns the class of code list wrapped by this adapter.
     *
     * @return The code list class.
     */
    protected abstract Class<BoundType> getCodeListClass();

    /**
     * Returns the default code space for the wrapped code list.
     * The default implementation returns {@code null}.
     *
     * @return The default code space, or {@code null}.
     */
    protected String getCodeSpace() {
        return null;
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  proxy The proxy for the GML value.
     * @return A code list which represents the GML value.
     */
    @Override
    public final BoundType unmarshal(final CodeListProxy proxy) {
        return (proxy != null) ? Types.forCodeName(getCodeListClass(), proxy.identifier, true) : null;
    }

    /**
     * Substitutes the code list by the proxy to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The code list value.
     * @return The proxy for the given code list.
     */
    @Override
    public final CodeListProxy marshal(final BoundType value) {
        return (value != null) ? new CodeListProxy(getCodeSpace(), value) : null;
    }
}
