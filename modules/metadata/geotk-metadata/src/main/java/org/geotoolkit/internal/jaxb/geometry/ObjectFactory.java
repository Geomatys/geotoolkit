/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.geometry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.apache.sis.xml.Namespaces;


/**
 * A minimalist XML object factory for getting JAXB to work without throwing exceptions when
 * there is no GML module in the classpath. This factory is extended with more complete methods
 * if the GML module.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@XmlRegistry
public class ObjectFactory {
    /**
     * The qualified name of {@code <AbstractGeometry>}.
     */
    protected static final QName AbstractGeometry_QNAME = new QName(Namespaces.GML, "AbstractGeometry");

    /**
     * The qualified name of {@code <AbstractGML>}.
     */
    protected static final QName AbstractGML_QNAME = new QName(Namespaces.GML, "AbstractGML");

    /**
     * The qualified name of {@code <AbstractObject>}.
     */
    protected static final QName AbstractObject_QNAME = new QName(Namespaces.GML, "AbstractObject");

    /**
     * Creates an instance of {@code JAXBElement<Object>}}.
     *
     * @param  value The {@code Object} value to wrap.
     * @return The wrapped value.
     */
    @XmlElementDecl(name = "AbstractObject")
    public JAXBElement<Object> createObject(final Object value) {
        return new JAXBElement<>(AbstractObject_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@code JAXBElement<AbstractGMLType>}}.
     * The type declared in the method signature should be {@code AbstractGMLType}.
     * However it is declared here as {@code Object} in order to avoid a dependency
     * toward the GML module.
     *
     * @param  value The GML {@code AbstractGMLType} value to wrap.
     * @return The wrapped value.
     */
    @XmlElementDecl(name = "AbstractGML",
            substitutionHeadName = "AbstractObject",
            substitutionHeadNamespace = Namespaces.GML) // Not necessary according javadoc, but appears to be in practice (JAXB 2.1 bug?)
    public JAXBElement<Object> createAbstractGML(final Object value) {
        return new JAXBElement<>(AbstractGML_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@code JAXBElement<AbstractGeometryType>}}.
     * The type declared in the method signature should be {@code AbstractGeometryType}.
     * However it is declared here as {@code Object} in order to avoid a dependency
     * toward the GML module.
     *
     * @param  value The {@code AbstractGeometryType} value to wrap.
     * @return The wrapped value.
     */
    @XmlElementDecl(name = "AbstractGeometry",
            substitutionHeadName = "AbstractGML",
            substitutionHeadNamespace = Namespaces.GML) // Not necessary according javadoc, but appears to be in practice (JAXB 2.1 bug?)
    public JAXBElement<Object> createAbstractGeometry(final Object value) {
        return new JAXBElement<>(AbstractGeometry_QNAME, Object.class, null, value);
    }
}
