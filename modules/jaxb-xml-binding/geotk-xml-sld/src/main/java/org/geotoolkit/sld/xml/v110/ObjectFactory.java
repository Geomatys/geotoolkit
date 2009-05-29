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
package org.geotoolkit.sld.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.sld package. 
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

    private final static QName _Service_QNAME = new QName("http://www.opengis.net/sld", "Service");
    private final static QName _Value_QNAME = new QName("http://www.opengis.net/sld", "Value");
    private final static QName _IsDefault_QNAME = new QName("http://www.opengis.net/sld", "IsDefault");
    private final static QName _TimePeriod_QNAME = new QName("http://www.opengis.net/sld", "TimePeriod");
    private final static QName _UserDefinedSymbolization_QNAME = new QName("http://www.opengis.net/sld", "UserDefinedSymbolization");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sld
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LayerFeatureConstraints }
     * 
     */
    public LayerFeatureConstraints createLayerFeatureConstraints() {
        return new LayerFeatureConstraints();
    }

    /**
     * Create an instance of {@link StyledLayerDescriptor }
     * 
     */
    public StyledLayerDescriptor createStyledLayerDescriptor() {
        return new StyledLayerDescriptor();
    }

    /**
     * Create an instance of {@link UserStyle }
     * 
     */
    public UserStyle createUserStyle() {
        return new UserStyle();
    }

    /**
     * Create an instance of {@link RangeAxis }
     * 
     */
    public RangeAxis createRangeAxis() {
        return new RangeAxis();
    }

    /**
     * Create an instance of {@link InlineFeature }
     * 
     */
    public InlineFeature createInlineFeature() {
        return new InlineFeature();
    }

    /**
     * Create an instance of {@link FeatureTypeConstraint }
     * 
     */
    public FeatureTypeConstraint createFeatureTypeConstraint() {
        return new FeatureTypeConstraint();
    }

    /**
     * Create an instance of {@link Extent }
     * 
     */
    public Extent createExtent() {
        return new Extent();
    }

    /**
     * Create an instance of {@link NamedLayer }
     * 
     */
    public NamedLayer createNamedLayer() {
        return new NamedLayer();
    }

    /**
     * Create an instance of {@link RemoteOWS }
     * 
     */
    public RemoteOWS createRemoteOWS() {
        return new RemoteOWS();
    }

    /**
     * Create an instance of {@link NamedStyle }
     * 
     */
    public NamedStyle createNamedStyle() {
        return new NamedStyle();
    }

    /**
     * Create an instance of {@link LayerCoverageConstraints }
     * 
     */
    public LayerCoverageConstraints createLayerCoverageConstraints() {
        return new LayerCoverageConstraints();
    }

    /**
     * Create an instance of {@link CoverageConstraint }
     * 
     */
    public CoverageConstraint createCoverageConstraint() {
        return new CoverageConstraint();
    }

    /**
     * Create an instance of {@link CoverageExtent }
     * 
     */
    public CoverageExtent createCoverageExtent() {
        return new CoverageExtent();
    }

    /**
     * Create an instance of {@link UserLayer }
     * 
     */
    public UserLayer createUserLayer() {
        return new UserLayer();
    }

    /**
     * Create an instance of {@link UseSLDLibrary }
     * 
     */
    public UseSLDLibrary createUseSLDLibrary() {
        return new UseSLDLibrary();
    }

    /**
     * Create an instance of {@link UserDefinedSymbolization }
     *
     */
    public UserDefinedSymbolization createUserDefinedSymbolization() {
        return new UserDefinedSymbolization();
    }

    /**
     * Create an instance of {@link DescribeLayerResponseType }
     *
     */
    public DescribeLayerResponseType  createDescribeLayerResponseType() {
        return new DescribeLayerResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Service")
    public JAXBElement<String> createService(String value) {
        return new JAXBElement<String>(_Service_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Value")
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "IsDefault")
    public JAXBElement<Boolean> createIsDefault(Boolean value) {
        return new JAXBElement<Boolean>(_IsDefault_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "TimePeriod")
    public JAXBElement<String> createTimePeriod(String value) {
        return new JAXBElement<String>(_TimePeriod_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserDefinedSymbolization }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "UserDefinedSymbolization", substitutionHeadNamespace = "http://www.opengis.net/wms", substitutionHeadName = "_ExtendedCapabilities")
    public JAXBElement<UserDefinedSymbolization> createUserDefinedSymbolization(UserDefinedSymbolization value) {
        return new JAXBElement<UserDefinedSymbolization>(_UserDefinedSymbolization_QNAME, UserDefinedSymbolization.class, null, value);
    }

}
