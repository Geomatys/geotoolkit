/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAttribute;

import org.geotoolkit.xml.Namespaces;


/**
 * Base class for GML objects that are wrappers around a GeoAPI implementation.
 * Every GML object to be marshalled have an ID attribute, which is mandatory.
 * If no ID is explicitely set, a default one will be created from the wrapped object.
 * <p>
 * <b>NOTE</b> This class is somewhat temporary. It assign the ID to the <em>wrapped</em>
 * object. In a future Geotk version, we should assign the ID to the object itself.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public abstract class GMLAdapter {
    /**
     * The period identifier, or {@code null} if undefined.
     * This element is part of GML 3.1.1 specification.
     */
    @XmlID
    @XmlAttribute(required = true, namespace = Namespaces.GML)
    private String id;

    /**
     * Creates a new GML object with no ID.
     * <p>
     * This constructor is typically invoked at unmarshalling time.
     * The {@link #id} value will then be set by JAXB.
     *
     * @see #copyIdTo(Object)
     */
    protected GMLAdapter() {
    }

    /**
     * Creates a new GML object wrapping the given GeoAPI implementation.
     * The ID will be determined from the given object.
     * <p>
     * This constructor is typically invoked at marshalling time. The {@link #id}
     * value set by this constructor will be used by JAXB for producing the XML.
     *
     * @param wrapped An instance of a GeoAPI interface to be wrapped.
     */
    protected GMLAdapter(final Object wrapped) {
        id = UUIDs.DEFAULT.getOrCreateUUID(wrapped);
    }

    /**
     * Assign the {@link #id} value (if non-null) to the given object. This method
     * is typically invoked at unmarshalling time in order to assign the ID of this
     * temporary wrapper to the "real" GeoAPI implementation instance.
     *
     * @param wrapped The GeoAPI implementation for which to assign the ID.
     */
    public final void copyIdTo(final Object wrapped) {
        if (id != null) {
            UUIDs.DEFAULT.setUUID(wrapped, id);
        }
    }
}
