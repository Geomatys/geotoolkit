/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.citygml.xml.v100.landuse;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.citygml.landuse._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LandUse_QNAME = new QName("http://www.opengis.net/citygml/landuse/1.0", "LandUse");
    private final static QName _GenericApplicationPropertyOfLandUse_QNAME = new QName("http://www.opengis.net/citygml/landuse/1.0", "_GenericApplicationPropertyOfLandUse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.citygml.landuse._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LandUseType }
     * 
     */
    public LandUseType createLandUseType() {
        return new LandUseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LandUseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/landuse/1.0", name = "LandUse", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<LandUseType> createLandUse(LandUseType value) {
        return new JAXBElement<LandUseType>(_LandUse_QNAME, LandUseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/landuse/1.0", name = "_GenericApplicationPropertyOfLandUse")
    public JAXBElement<Object> createGenericApplicationPropertyOfLandUse(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfLandUse_QNAME, Object.class, null, value);
    }

}
