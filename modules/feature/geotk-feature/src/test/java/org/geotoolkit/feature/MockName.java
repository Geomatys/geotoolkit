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
package org.geotoolkit.feature;

import java.util.Objects;
import java.util.Collections;
import javax.xml.namespace.QName;
import org.opengis.feature.type.Name;
import org.apache.sis.util.iso.DefaultLocalName;
import org.apache.sis.internal.system.DefaultFactories;

/**
 * A simple implementation of interface Name we need for running tests.
 * @author Alexis MANIN
 *
 * @deprecated The GeoAPI {@code Name} interface is expected to be replaced by {@link org.opengis.util.GenericName}.
 */
@Deprecated
public class MockName extends DefaultLocalName implements Name {
    /**
     * Namespace / scope
     */
    private final String namespace;
    /**
     * Local part
     */
    private final String local;
    private final String separator;

    /**
     * Constructs an instance with the local part set. Namespace / scope is set
     * to null.
     *
     * @param local The local part of the name.
     */
    public MockName(final String local) {
        this(null, local);
    }

    public MockName(final QName qname) {
        this(qname.getNamespaceURI(), qname.getLocalPart());
    }

    /**
     * Constructs an instance with the local part and namespace set.
     *
     * @param namespace The namespace or scope of the name.
     * @param local The local part of the name.
     *
     */
    public MockName(final String namespace, final String local) {
        this(namespace, ":", local);
    }

    /**
     * Constructs an instance with the local part and namespace set.
     *
     * @param namespace The namespace or scope of the name.
     * @param local The local part of the name.
     *
     */
    public MockName(final String namespace, final String separator, final String local) {

        // WARNING: DefaultFactories.NAMES is not a public API and may change in any future SIS version.

        super(DefaultFactories.NAMES.createNameSpace(
                DefaultFactories.NAMES.createGenericName(null, namespace),
                Collections.singletonMap("separator.head", separator)), local);
        this.namespace = namespace;
        this.separator = separator;
        this.local = local;
    }

    @Override
    public boolean isGlobal() {
        return getNamespaceURI() == null;
    }

    @Override
    public String getSeparator() {
        return separator;
    }

    @Override
    public String getNamespaceURI() {
        return namespace;
    }

    @Override
    public String getLocalPart() {
        return local;
    }

    @Override
    public String getURI() {
        if ((namespace == null) && (local == null)) {
            return null;
        }
        if (namespace == null) {
            return local;
        }
        if (local == null) {
            return namespace;
        }
        return new StringBuilder(namespace).append(separator).append(local).toString();
    }

    /**
     * Returns a hash code value for this operand.
     */
    @Override
    public int hashCode() {
        return (namespace == null ? 0 : namespace.hashCode())
                + 37 * (local == null ? 0 : local.hashCode());
    }

    /**
     * value object with equality based on name and namespace.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Name) {
            final MockName other = (MockName) obj;
            if (!Objects.equals(this.namespace, other.getNamespaceURI())) {
                return false;
            }
            if (!Objects.equals(this.local, other.getLocalPart())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Name or namespace:name
     */
    @Override
    public String toString() {
        final String uri = this.getNamespaceURI();
        if (uri == null) {
            return this.getLocalPart();
        } else {
            return new StringBuilder("{").append(uri).append('}').append(this.getLocalPart()).toString();
        }
    }
}
