/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.internal.jaxb.UUIDs;
import org.apache.sis.util.Version;
import org.apache.sis.xml.Namespaces;


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
     * A GML version suitable for calls to {@link #versionGML(Version)}.
     *
     * @since 3.21
     */
    public static final Version GML_3_0 = new Version("3.0"),
                                GML_3_2 = new Version("3.2");

    /**
     * The GML identifiers associated to the wrapped objects. This is different than the
     * map of UUIDs maintained by {@link UUIDs#DEFAULT}.
     *
     * @see <a href="https://www.seegrid.csiro.au/wiki/bin/view/AppSchemas/GmlIdentifiers">GML identifiers</a>
     */
    public static final UUIDs<String> IDs = new UUIDs.AsString();

    /**
     * The period identifier, or {@code null} if undefined.
     * This element is part of GML 3.1.1 specification.
     *
     * {@section Difference between <code>gmd:uuid</code> and <code>gml:id</code>}
     * <ul>
     *   <li>{@code id} is a standard <strong>GML</strong> attribute available on every
     *       object-with-identity. It has type={@code "xs:ID"} - i.e. it is a fragment
     *       identifier, unique within document scope only, for internal cross-references.
     *       It is not useful by itself as a persistent unique identifier.</li>
     *   <li>{@code uuid} is an optional attribute available on every object-with-identity,
     *       provided in the <strong>GMD</strong> schemas that implement ISO 19115 in XML.
     *       May be used as a persistent unique identifier, but only available within GMD
     *       context.</li>
     * </ul>
     *
     * @see <a href="https://www.seegrid.csiro.au/wiki/bin/view/AppSchemas/GmlIdentifiers">GML identifiers</a>
     * @see org.apache.sis.internal.jaxb.gco.PropertyType#uuid
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
        id = IDs.getOrCreateUUID(wrapped);
    }

    /**
     * Assign the {@link #id} value (if non-null) to the given object. This method
     * is typically invoked at unmarshalling time in order to assign the ID of this
     * temporary wrapper to the "real" GeoAPI implementation instance.
     *
     * @param wrapped The GeoAPI implementation for which to assign the ID.
     */
    public final void copyIdTo(final Object wrapped) {
        if (false && id != null) { // Temporarily disabled.
            IDs.setUUID(wrapped, id);
        }
    }
}
