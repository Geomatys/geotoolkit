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
package org.geotoolkit.citygml.xml.v100.cityfurniture;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.citygml.cityfurniture._1 package. 
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

    private static final QName _CityFurniture_QNAME = new QName("http://www.opengis.net/citygml/cityfurniture/1.0", "CityFurniture");
    private static final QName _GenericApplicationPropertyOfCityFurniture_QNAME = new QName("http://www.opengis.net/citygml/cityfurniture/1.0", "_GenericApplicationPropertyOfCityFurniture");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.citygml.cityfurniture._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CityFurnitureType }
     * 
     */
    public CityFurnitureType createCityFurnitureType() {
        return new CityFurnitureType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CityFurnitureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/cityfurniture/1.0", name = "CityFurniture", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<CityFurnitureType> createCityFurniture(CityFurnitureType value) {
        return new JAXBElement<CityFurnitureType>(_CityFurniture_QNAME, CityFurnitureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/cityfurniture/1.0", name = "_GenericApplicationPropertyOfCityFurniture")
    public JAXBElement<Object> createGenericApplicationPropertyOfCityFurniture(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfCityFurniture_QNAME, Object.class, null, value);
    }

}
