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

import org.geotoolkit.xml.XLink;
import org.geotoolkit.util.Utilities;


/**
 * The {@code gco:ObjectReference} XML attribute group is included by all metadata wrappers defined
 * in the {@link org.geotoolkit.internal.jaxb.metadata} package. The attributes of interest defined
 * in this group are {@code uuidref}, {@code type}, {@code xlink:href}, {@code xlink:role},
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
 * @since 3.18
 * @module
 */
final class ObjectReference extends XLink {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8626892072241872154L;

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
     * Creates a new object reference of kind {@code xlink:simpleLink}.
     */
    ObjectReference() {
        setType(Type.SIMPLE);
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
            return Utilities.equals(this.uuidref, that.uuidref);
        }
        return false;
    }

    /**
     * Returns a hash code value for this object reference.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(uuidref, super.hashCode());
    }
}
