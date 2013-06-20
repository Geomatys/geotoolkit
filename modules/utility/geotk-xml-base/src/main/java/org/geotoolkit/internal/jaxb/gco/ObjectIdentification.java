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
package org.geotoolkit.internal.jaxb.gco;

import java.util.UUID;
import org.geotoolkit.internal.jaxb.UUIDs;


/**
 * The {@code gco:ObjectIdentification} XML attribute group is included by all metadata types
 * defined in the {@link org.apache.sis.metadata.iso} packages. The attributes of interest
 * defined in this group are {@code id} and {@code uuid}.
 * <p>
 * This {@code gco:ObjectIdentification} group is complementary to {@code gco:ObjectReference},
 * which defines the {@code xlink} and {@code uuidref} attributes to be supported by all metadata
 * wrappers in the private {@link org.geotoolkit.internal.jaxb.metadata} package and sub-packages.
 *
 * {@section Difference between <code>gml:id</code> and <code>gmd:uuid</code>}
 * The <a href="https://www.seegrid.csiro.au/wiki/bin/view/AppSchemas/GmlIdentifiers">GML identifiers</a>
 * page said:
 * <p>
 * <ul>
 *   <li>{@code id} is a standard <strong>GML</strong> attribute available on every object-with-identity.
 *   It has type={@code "xs:ID"} - i.e. it is a fragment identifier, unique within document scope only,
 *   for internal cross-references. It is not useful by itself as a persistent unique identifier.</li>
 *
 *   <li>{@code uuid} is an optional attribute available on every object-with-identity, provided in
 *   the <strong>GMD</strong> schemas that implement ISO 19115 in XML. May be used as a persistent
 *   unique identifier, but only available within GMD context.</li>
 * </ul>
 * <p>
 * However according the <a href="http://schemas.opengis.net/iso/19139/20070417/gco/gcoBase.xsd">OGC
 * schema</a>, those identifiers seem to be defined in the GCO schema.</p>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see ObjectReference
 * @see <a href="http://schemas.opengis.net/iso/19139/20070417/gco/gcoBase.xsd">OGC schema</a>
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.19
 * @module
 */
public final class ObjectIdentification {
    /**
     * A system-wide map of UUIDs using {@link UUID}.
     */
    public static final UUIDs<UUID> UUIDs = new UUIDs.Standard();

    /**
     * No subclass allowed for now, but it may be allowed in a future version.
     */
    private ObjectIdentification() {
    }

    // If an implementation is needed in a future Geotk version,
    // org.geotoolkit.metadata.iso.MetadataEntity can be used as a model.
}
