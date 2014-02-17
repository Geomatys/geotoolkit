/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wrs.xml.v090;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.cat.wrs package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Geometry_QNAME           = new QName("http://www.opengis.net/cat/wrs", "Geometry");
    private static final QName _ApplicationModule_QNAME  = new QName("http://www.opengis.net/cat/wrs", "ApplicationModule");
    private static final QName _WRSExtrinsicObject_QNAME = new QName("http://www.opengis.net/cat/wrs", "WRSExtrinsicObject");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.cat.wrs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SimpleLinkType }
     * 
     */
    public SimpleLinkType createSimpleLinkType() {
        return new SimpleLinkType();
    }

    /**
     * Create an instance of {@link WRSExtrinsicObjectType }
     * 
     */
    public WRSExtrinsicObjectType createWRSExtrinsicObjectType() {
        return new WRSExtrinsicObjectType();
    }

    /**
     * Create an instance of {@link ApplicationModuleType }
     * 
     */
    public ApplicationModuleType createApplicationModuleType() {
        return new ApplicationModuleType();
    }

    /**
     * Create an instance of {@link GeometryType }
     * 
     */
    public GeometryType createGeometryType() {
        return new GeometryType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs", name = "Geometry", substitutionHeadNamespace = "http://www.opengis.net/cat/wrs", substitutionHeadName = "WRSExtrinsicObject")
    public JAXBElement<GeometryType> createGeometry(final GeometryType value) {
        return new JAXBElement<GeometryType>(_Geometry_QNAME, GeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApplicationModuleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs", name = "ApplicationModule", substitutionHeadNamespace = "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", substitutionHeadName = "RegistryPackage")
    public JAXBElement<ApplicationModuleType> createApplicationModule(final ApplicationModuleType value) {
        return new JAXBElement<ApplicationModuleType>(_ApplicationModule_QNAME, ApplicationModuleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WRSExtrinsicObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs", name = "WRSExtrinsicObject", substitutionHeadNamespace = "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", substitutionHeadName = "ExtrinsicObject")
    public JAXBElement<WRSExtrinsicObjectType> createWRSExtrinsicObject(final WRSExtrinsicObjectType value) {
        return new JAXBElement<WRSExtrinsicObjectType>(_WRSExtrinsicObject_QNAME, WRSExtrinsicObjectType.class, null, value);
    }

}
