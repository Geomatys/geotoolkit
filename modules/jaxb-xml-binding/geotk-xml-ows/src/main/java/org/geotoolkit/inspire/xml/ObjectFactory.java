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
package org.geotoolkit.inspire.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.inspire package. 
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

    private final static QName _MultiligualCapabilities_QNAME = new QName("http://www.inspire.org", "MultiligualCapabilities");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.inspire
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LanguagesType }
     * 
     */
    public LanguagesType createLanguagesType() {
        return new LanguagesType();
    }

    /**
     * Create an instance of {@link DocumentType }
     * 
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link InspireCapabilitiesType }
     * 
     */
    public InspireCapabilitiesType createInspireCapabilitiesType() {
        return new InspireCapabilitiesType();
    }

    /**
     * Create an instance of {@link TranslatedCapabilitiesType }
     * 
     */
    public TranslatedCapabilitiesType createTranslatedCapabilitiesType() {
        return new TranslatedCapabilitiesType();
    }

    /**
     * Create an instance of {@link TranslatedCapabilitiesType }
     *
     */
    public MultiLingualCapabilities createMultiLingualCapabilities() {
        return new MultiLingualCapabilities();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InspireCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.inspire.org", name = "MultiligualCapabilities")
    public JAXBElement<InspireCapabilitiesType> createMultiligualCapabilities(InspireCapabilitiesType value) {
        return new JAXBElement<InspireCapabilitiesType>(_MultiligualCapabilities_QNAME, InspireCapabilitiesType.class, null, value);
    }

}
