/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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


package org.geotoolkit.ops.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.a9.opensearch package.
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

    private final static QName _Query_QNAME = new QName("http://a9.com/-/spec/opensearch/1.1/", "Query");
    public final static QName _ItemsPerPage_QNAME = new QName("http://a9.com/-/spec/opensearch/1.1/", "itemsPerPage");
    public final static QName _TotalResults_QNAME = new QName("http://a9.com/-/spec/opensearch/1.1/", "totalResults");
    public final static QName _StartIndex_QNAME = new QName("http://a9.com/-/spec/opensearch/1.1/", "startIndex");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.a9.opensearch
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InspireQueryType }
     *
     */
    public InspireQueryType createInspireQueryType() {
        return new InspireQueryType();
    }

    /**
     * Create an instance of {@link OpenSearchDescription }
     *
     */
    public OpenSearchDescription createOpenSearchDescription() {
        return new OpenSearchDescription();
    }

    /**
     * Create an instance of {@link Url }
     *
     */
    public Url createUrl() {
        return new Url();
    }

    /**
     * Create an instance of {@link ImageType }
     *
     */
    public ImageType createImageType() {
        return new ImageType();
    }

    /**
     * Create an instance of {@link QueryType }
     *
     */
    public QueryType createQueryType() {
        return new QueryType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InspireQueryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://a9.com/-/spec/opensearch/1.1/", name = "Query")
    public JAXBElement<CompleteQueryType> createQuery(CompleteQueryType value) {
        return new JAXBElement<CompleteQueryType>(_Query_QNAME, CompleteQueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://a9.com/-/spec/opensearch/1.1/", name = "itemsPerPage")
    public JAXBElement<Long> createItemsPerPage(Long value) {
        return new JAXBElement<Long>(_ItemsPerPage_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://a9.com/-/spec/opensearch/1.1/", name = "totalResults")
    public JAXBElement<Long> createTotalResults(Long value) {
        return new JAXBElement<Long>(_TotalResults_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://a9.com/-/spec/opensearch/1.1/", name = "startIndex")
    public JAXBElement<Long> createStartIndex(Long value) {
        return new JAXBElement<Long>(_StartIndex_QNAME, Long.class, null, value);
    }

}
