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

import org.apache.sis.xml.XLink;
import org.geotoolkit.xml.ObjectLinker;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.xml.IdentifiedObject;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.internal.jaxb.SpecializedIdentifier;
import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * The {@code gco:ObjectReference} XML attribute group is included by all metadata wrappers defined
 * in the {@link org.geotoolkit.internal.jaxb.metadata} package. The attributes of interest defined
 * in this group are {@code uuidref}, {@code xlink:href}, {@code xlink:role}, {@code xlink:arcrole},
 * {@code xlink:title}, {@code xlink:show} and {@code xlink:actuate}.
 * <p>
 * This {@code gco:ObjectReference} group is complementary to {@code gco:ObjectIdentification},
 * which define the {@code id} and {@code uuid} attributes to be supported by all metadata
 * implementations in the public {@link org.geotoolkit.metadata.iso} package and sub-packages.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see PropertyType
 * @see ObjectIdentification
 * @see <a href="http://schemas.opengis.net/iso/19139/20070417/gco/gcoBase.xsd">OGC schema</a>
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.18
 * @module
 */
final class ObjectReference {
    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @see <a href="http://www.schemacentral.com/sc/niem21/a-uuidref-1.html">Usage of uuidref</a>
     */
    String anyUUID;

    /**
     * The {@code xlink} attributes, or {@code null} if none.
     */
    XLink xlink;

    /**
     * The parsed value of {@link #anyUUID}, computed when first needed.
     */
    private transient UUID uuid;

    /**
     * Creates an initially empty object reference.
     */
    ObjectReference() {
    }

    /**
     * Creates an object reference initialized to the given value.
     */
    ObjectReference(final UUID uuid, final String anyUUID, final XLink link) {
        this.uuid    = uuid;
        this.anyUUID = anyUUID;
        this.xlink   = link;
    }

    /**
     * Parses the given string as a UUID.
     *
     * @param  anyUUID The string to parse, or {@code null}.
     * @return The parsed UUID, or {@code null}.
     * @throws IllegalArgumentException If {@code anyUUID} can not be parsed.
     */
    static UUID toUUID(final String anyUUID) throws IllegalArgumentException {
        return (anyUUID != null) ? MarshalContext.converters().toUUID(anyUUID) : null;
    }

    /**
     * If the given metadata object is null, tries to get an instance from the identifiers
     * declared in this {@code ObjectReference}. If the given metadata object is non-null,
     * assigns to that object the identifiers declared in this {@code ObjectReference}.
     * <p>
     * This method is invoked at unmarshalling time.
     *
     * @param  <T>       The compile-time type of the {@code type} argument.
     * @param  type      The expected type of the metadata object.
     * @param  metadata  The metadata object, or {@code null}.
     * @return A metadata object for the identifiers, or {@code null}
     * @throws IllegalArgumentException If the {@link #anyUUID} field can not be parsed.
     */
    final <T> T resolve(final Class<T> type, T metadata) throws IllegalArgumentException {
        if (uuid == null) {
            uuid = toUUID(anyUUID);
        }
        if (metadata == null) {
            final ObjectLinker linker = MarshalContext.linker();
            if ((uuid  == null || (metadata = linker.resolve(type, uuid )) == null) &&
                (xlink == null || (metadata = linker.resolve(type, xlink)) == null))
            {
                // Failed to find an existing metadata instance.
                // Creates an empty instance with the identifiers.
                int count = 0;
                SpecializedIdentifier<?>[] identifiers  = new SpecializedIdentifier<?>[2];
                if (uuid  != null) identifiers[count++] = new SpecializedIdentifier<UUID> (IdentifierSpace.UUID,  uuid);
                if (xlink != null) identifiers[count++] = new SpecializedIdentifier<XLink>(IdentifierSpace.XLINK, xlink);
                identifiers = ArraysExt.resize(identifiers, count);
                metadata = linker.newIdentifiedObject(type, identifiers);
            }
        } else {
            // If principle, the XML should contain a full metadata object OR a uuidref attribute.
            // However if both are present, check if the metadata object referenced by uuidref is
            // equals to the object we unmarshalled. If it is, then take the existing instance.
            if (uuid != null) {
                final Object existing = ObjectIdentification.UUIDs.lookup(uuid);
                if (metadata.equals(existing)) {
                    return type.cast(existing);
                }
            }
            if (metadata instanceof IdentifiedObject) {
                final IdentifierMap map = ((IdentifiedObject) metadata).getIdentifierMap();
                if (uuid  != null) new SpecializedIdentifier<UUID> (IdentifierSpace.UUID,  uuid) .putInto(map);
                if (xlink != null) new SpecializedIdentifier<XLink>(IdentifierSpace.XLINK, xlink).putInto(map);
            }
        }
        return metadata;
    }
}
