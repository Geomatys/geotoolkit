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
package org.geotoolkit.internal.jaxb.gco;

import java.util.UUID;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.XLink;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.internal.jaxb.IdentifierAdapter;
import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * The {@code gco:ObjectReference} XML attribute group is included by all metadata wrappers defined
 * in the {@link org.geotoolkit.internal.jaxb.metadata} package. The attributes of interest defined
 * in this group are {@code idref}, {@code uuidref}, {@code xlink:href}, {@code xlink:role},
 * {@code xlink:arcrole}, {@code xlink:title}, {@code xlink:show} and {@code xlink:actuate}.
 * <p>
 * This {@code gco:ObjectReference} group is complementary to {@code gco:ObjectIdentification},
 * which defines the {@code id} and {@code uuid} attributes to be supported by all metadata
 * implementations in the public {@link org.geotoolkit.metadata.iso} package and sub-packages.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see PropertyType
 * @see <a href="http://schemas.opengis.net/iso/19139/20070417/gco/gcoBase.xsd">OGC schema</a>
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.19
 * @module
 */
final class ObjectReference {
    /**
     * The identifier, or {@code null} if undefined.
     */
    String id;

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @see <a href="http://www.schemacentral.com/sc/niem21/a-uuidref-1.html">Usage of uuidref</a>
     */
    String uuid;

    /**
     * The {@code xlink} attributes, or {@code null} if none.
     */
    XLink xlink;

    /**
     * Creates an initially empty object reference.
     */
    ObjectReference() {
    }

    /**
     * Creates an object reference initialized to the given value.
     */
    ObjectReference(final String id, final String uuid, final XLink link) {
        this.id    = id;
        this.uuid  = uuid;
        this.xlink = link;
    }

    /**
     * Returns the field values as an array of identifiers.
     * Those identifiers will be assigned to the unmarshalled metadata object.
     *
     * @throws IllegalArgumentException If the UUID can not be parsed.
     */
    Identifier[] getIdentifiers() throws IllegalArgumentException {
        final Identifier[] identifiers = new Identifier[3];
        int count = 0;
        if (id != null) {
            identifiers[count++] = new IdentifierAdapter<String>(IdentifierSpace.ID, id);
        }
        if (uuid != null) {
            final UUID parsed = MarshalContext.converters().toUUID(uuid);
            if (parsed != null) {
                identifiers[count++] = new IdentifierAdapter<UUID>(IdentifierSpace.UUID, parsed);
            }
        }
        if (xlink != null) {
            identifiers[count++] = new IdentifierAdapter<XLink>(IdentifierSpace.XLINK, xlink);
        }
        return XArrays.resize(identifiers, count);
    }
}
