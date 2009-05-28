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
package org.geotoolkit.wmc.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.context package. 
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

    private final static QName _ViewContext_QNAME = new QName("http://www.opengis.net/context", "ViewContext");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.context
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StyleType }
     * 
     */
    public StyleType createStyleType() {
        return new StyleType();
    }

    /**
     * Create an instance of {@link URLType }
     * 
     */
    public URLType createURLType() {
        return new URLType();
    }

    /**
     * Create an instance of {@link LayerListType }
     * 
     */
    public LayerListType createLayerListType() {
        return new LayerListType();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     * 
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link WindowType }
     * 
     */
    public WindowType createWindowType() {
        return new WindowType();
    }

    /**
     * Create an instance of {@link LayerType }
     * 
     */
    public LayerType createLayerType() {
        return new LayerType();
    }

    /**
     * Create an instance of {@link KeywordListType }
     * 
     */
    public KeywordListType createKeywordListType() {
        return new KeywordListType();
    }

    /**
     * Create an instance of {@link SLDType }
     * 
     */
    public SLDType createSLDType() {
        return new SLDType();
    }

    /**
     * Create an instance of {@link FormatType }
     * 
     */
    public FormatType createFormatType() {
        return new FormatType();
    }

    /**
     * Create an instance of {@link FormatListType }
     * 
     */
    public FormatListType createFormatListType() {
        return new FormatListType();
    }

    /**
     * Create an instance of {@link DimensionListType }
     * 
     */
    public DimensionListType createDimensionListType() {
        return new DimensionListType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link StyleListType }
     * 
     */
    public StyleListType createStyleListType() {
        return new StyleListType();
    }

    /**
     * Create an instance of {@link ServerType }
     * 
     */
    public ServerType createServerType() {
        return new ServerType();
    }

    /**
     * Create an instance of {@link BoundingBoxType }
     * 
     */
    public BoundingBoxType createBoundingBoxType() {
        return new BoundingBoxType();
    }

    /**
     * Create an instance of {@link ContactInformationType }
     * 
     */
    public ContactInformationType createContactInformationType() {
        return new ContactInformationType();
    }

    /**
     * Create an instance of {@link GeneralType }
     * 
     */
    public GeneralType createGeneralType() {
        return new GeneralType();
    }

    /**
     * Create an instance of {@link ContactPersonPrimaryType }
     * 
     */
    public ContactPersonPrimaryType createContactPersonPrimaryType() {
        return new ContactPersonPrimaryType();
    }

    /**
     * Create an instance of {@link DimensionType }
     * 
     */
    public DimensionType createDimensionType() {
        return new DimensionType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link ViewContextType }
     * 
     */
    public ViewContextType createViewContextType() {
        return new ViewContextType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ViewContextType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/context", name = "ViewContext")
    public JAXBElement<ViewContextType> createViewContext(ViewContextType value) {
        return new JAXBElement<ViewContextType>(_ViewContext_QNAME, ViewContextType.class, null, value);
    }

}
