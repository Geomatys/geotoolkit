/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.wcs.xml.v200.crs;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the net.opengis.wcs_service_extension_crs._1
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CrsMetadata_QNAME = new QName("http://www.opengis.net/wcs_service-extension_crs/1.0", "CrsMetadata");
    private final static QName _Crs_QNAME = new QName("http://www.opengis.net/wcs_service-extension_crs/1.0", "Crs");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package:
     * net.opengis.wcs_service_extension_crs._1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CrsType }
     *
     */
    public CrsType createCrsType() {
        return new CrsType();
    }

    /**
     * Create an instance of {@link CrsMetadataType }
     *
     */
    public CrsMetadataType createCrsMetadataType() {
        return new CrsMetadataType();
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link CrsMetadataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs_service-extension_crs/1.0", name = "CrsMetadata")
    public JAXBElement<CrsMetadataType> createCrsMetadata(CrsMetadataType value) {
        return new JAXBElement<>(_CrsMetadata_QNAME, CrsMetadataType.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link CrsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs_service-extension_crs/1.0", name = "Crs")
    public JAXBElement<CrsType> createCrs(CrsType value) {
        return new JAXBElement<>(_Crs_QNAME, CrsType.class, null, value);
    }

}
