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

import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.util.Utilities;


/**
 * The {@code gco:ObjectReference} construct, which is inherited (indirectly) by all metadata
 * wrappers defined {@link org.geotoolkit.internal.jaxb.metadata} package. The attributes defined
 * by this class are {@code uuidref}, {@code type}, {@code xlink:href}, {@code xlink:role},
 * {@code xlink:arcrole}, {@code xlink:title}, {@code xlink:show} and {@code xlink:actuate}.
 * <p>
 * This object is complementary to the {@code gco:ObjectIdentification}, which defines the
 * {@code id} and {@code uuid} attributes to be supported by all metadata implementation in
 * the public {@link org.geotoolkit.metadata.iso} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.18
 * @module
 */
final class ObjectReference extends XLink {
    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @see <a href="http://www.schemacentral.com/sc/niem21/a-uuidref-1.html">Usage of uuidref</a>
     */
    @XmlAttribute
    String uuidref;

    /**
     * The reason why a mandatory attribute if left unspecified. This attribute is actually
     * not a {@code gco:ObjectReference} attribute, but it still declared here because it
     * appears in every ISO 19139 metadata.
     */
    @XmlAttribute
    String nilReason;

    /**
     * Creates a new object reference.
     */
    ObjectReference() {
    }

    /**
     * Compares this {@code ObjectReference} with the given object for equality.
     *
     * @param object The object to compare with this object reference.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final ObjectReference that = (ObjectReference) object;
            return Utilities.equals(this.uuidref,   that.uuidref) &&
                   Utilities.equals(this.nilReason, that.nilReason);
        }
        return false;
    }

    /**
     * Returns a hash code value for this object reference.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(uuidref, Utilities.hash(nilReason, super.hashCode()));
    }
}
