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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.wms package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 * @author Guilhem Legal
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Name_QNAME = new QName("http://www.opengis.net/wms", "Name");
    private static final QName _MinScaleDenominator_QNAME = new QName("http://www.opengis.net/wms", "MinScaleDenominator");
    private static final QName _PostCode_QNAME = new QName("http://www.opengis.net/wms", "PostCode");
    private static final QName _MaxHeight_QNAME = new QName("http://www.opengis.net/wms", "MaxHeight");
    private static final QName _Address_QNAME = new QName("http://www.opengis.net/wms", "Address");
    private static final QName _ContactFacsimileTelephone_QNAME = new QName("http://www.opengis.net/wms", "ContactFacsimileTelephone");
    private static final QName _AddressType_QNAME = new QName("http://www.opengis.net/wms", "AddressType");
    private static final QName _ContactVoiceTelephone_QNAME = new QName("http://www.opengis.net/wms", "ContactVoiceTelephone");
    private static final QName _Abstract_QNAME = new QName("http://www.opengis.net/wms", "Abstract");
    private static final QName _MaxWidth_QNAME = new QName("http://www.opengis.net/wms", "MaxWidth");
    private static final QName _AccessConstraints_QNAME = new QName("http://www.opengis.net/wms", "AccessConstraints");
    private static final QName _ExtendedCapabilities_QNAME = new QName("http://www.opengis.net/wms", "_ExtendedCapabilities");
    private static final QName _ContactPerson_QNAME = new QName("http://www.opengis.net/wms", "ContactPerson");
    private static final QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/wms", "GetCapabilities");
    private static final QName _LayerLimit_QNAME = new QName("http://www.opengis.net/wms", "LayerLimit");
    private static final QName _ContactOrganization_QNAME = new QName("http://www.opengis.net/wms", "ContactOrganization");
    private static final QName _ExtendedOperation_QNAME = new QName("http://www.opengis.net/wms", "_ExtendedOperation");
    private static final QName _Country_QNAME = new QName("http://www.opengis.net/wms", "Country");
    private static final QName _City_QNAME = new QName("http://www.opengis.net/wms", "City");
    private static final QName _Title_QNAME = new QName("http://www.opengis.net/wms", "Title");
    private static final QName _Fees_QNAME = new QName("http://www.opengis.net/wms", "Fees");
    private static final QName _GetMap_QNAME = new QName("http://www.opengis.net/wms", "GetMap");
    private static final QName _GetFeatureInfo_QNAME = new QName("http://www.opengis.net/wms", "GetFeatureInfo");
    private static final QName _StateOrProvince_QNAME = new QName("http://www.opengis.net/wms", "StateOrProvince");
    private static final QName _Format_QNAME = new QName("http://www.opengis.net/wms", "Format");
    private static final QName _CRS_QNAME = new QName("http://www.opengis.net/wms", "CRS");
    private static final QName _MaxScaleDenominator_QNAME = new QName("http://www.opengis.net/wms", "MaxScaleDenominator");
    private static final QName _ContactElectronicMailAddress_QNAME = new QName("http://www.opengis.net/wms", "ContactElectronicMailAddress");
    private static final QName _ContactPosition_QNAME = new QName("http://www.opengis.net/wms", "ContactPosition");

    private static final QName _GetLegendGraphic_QNAME = new QName("http://www.opengis.net/sld", "GetLegendGraphic");
    private static final QName _DescribeLayer_QNAME = new QName("http://www.opengis.net/sld", "DescribeLayer");
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wms
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EXGeographicBoundingBox }
     *
     */
    public EXGeographicBoundingBox createEXGeographicBoundingBox() {
        return new EXGeographicBoundingBox();
    }

    /**
     * Create an instance of {@link LogoURL }
     *
     */
    public LogoURL createLogoURL() {
        return new LogoURL();
    }

    /**
     * Create an instance of {@link Get }
     *
     */
    public Get createGet() {
        return new Get();
    }

    /**
     * Create an instance of {@link OperationType }
     *
     */
    public OperationType createOperationType() {
        return new OperationType();
    }

    /**
     * Create an instance of {@link LegendURL }
     *
     */
    public LegendURL createLegendURL() {
        return new LegendURL();
    }

    /**
     * Create an instance of {@link WMSCapabilities }
     *
     */
    public WMSCapabilities createWMSCapabilities() {
        return new WMSCapabilities();
    }

    /**
     * Create an instance of {@link FeatureListURL }
     *
     */
    public FeatureListURL createFeatureListURL() {
        return new FeatureListURL();
    }

    /**
     * Create an instance of {@link OnlineResource }
     *
     */
    public OnlineResource createOnlineResource() {
        return new OnlineResource();
    }

    /**
     * Create an instance of {@link ContactAddress }
     *
     */
    public ContactAddress createContactAddress() {
        return new ContactAddress();
    }

    /**
     * Create an instance of {@link HTTP }
     *
     */
    public HTTP createHTTP() {
        return new HTTP();
    }

    /**
     * Create an instance of {@link DataURL }
     *
     */
    public DataURL createDataURL() {
        return new DataURL();
    }

    /**
     * Create an instance of {@link ContactInformation }
     *
     */
    public ContactInformation createContactInformation() {
        return new ContactInformation();
    }

    /**
     * Create an instance of {@link Dimension }
     *
     */
    public Dimension createDimension() {
        return new Dimension();
    }

    /**
     * Create an instance of {@link Request }
     *
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link ContactPersonPrimary }
     *
     */
    public ContactPersonPrimary createContactPersonPrimary() {
        return new ContactPersonPrimary();
    }

    /**
     * Create an instance of {@link StyleSheetURL }
     *
     */
    public StyleSheetURL createStyleSheetURL() {
        return new StyleSheetURL();
    }

    /**
     * Create an instance of {@link Layer }
     *
     */
    public Layer createLayer() {
        return new Layer();
    }

    /**
     * Create an instance of {@link KeywordList }
     *
     */
    public KeywordList createKeywordList() {
        return new KeywordList();
    }

    /**
     * Create an instance of {@link Service }
     *
     */
    public Service createService() {
        return new Service();
    }

    /**
     * Create an instance of {@link MetadataURL }
     *
     */
    public MetadataURL createMetadataURL() {
        return new MetadataURL();
    }

    /**
     * Create an instance of {@link Post }
     *
     */
    public Post createPost() {
        return new Post();
    }

    /**
     * Create an instance of {@link BoundingBox }
     *
     */
    public BoundingBox createBoundingBox() {
        return new BoundingBox();
    }

    /**
     * Create an instance of {@link StyleURL }
     *
     */
    public StyleURL createStyleURL() {
        return new StyleURL();
    }

    /**
     * Create an instance of {@link Exception }
     *
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link Keyword }
     *
     */
    public Keyword createKeyword() {
        return new Keyword();
    }

    /**
     * Create an instance of {@link Identifier }
     *
     */
    public Identifier createIdentifier() {
        return new Identifier();
    }

    /**
     * Create an instance of {@link Attribution }
     *
     */
    public Attribution createAttribution() {
        return new Attribution();
    }

    /**
     * Create an instance of {@link Style }
     *
     */
    public Style createStyle() {
        return new Style();
    }

    /**
     * Create an instance of {@link DCPType }
     *
     */
    public DCPType createDCPType() {
        return new DCPType();
    }

    /**
     * Create an instance of {@link AuthorityURL }
     *
     */
    public AuthorityURL createAuthorityURL() {
        return new AuthorityURL();
    }

    /**
     * Create an instance of {@link Capability }
     *
     */
    public Capability createCapability() {
        return new Capability();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Name")
    public JAXBElement<String> createName(final String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "MinScaleDenominator")
    public JAXBElement<Double> createMinScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MinScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "PostCode")
    public JAXBElement<String> createPostCode(final String value) {
        return new JAXBElement<String>(_PostCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "MaxHeight")
    public JAXBElement<Integer> createMaxHeight(final Integer value) {
        return new JAXBElement<Integer>(_MaxHeight_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Address")
    public JAXBElement<String> createAddress(final String value) {
        return new JAXBElement<String>(_Address_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactFacsimileTelephone")
    public JAXBElement<String> createContactFacsimileTelephone(final String value) {
        return new JAXBElement<String>(_ContactFacsimileTelephone_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "AddressType")
    public JAXBElement<String> createAddressType(final String value) {
        return new JAXBElement<String>(_AddressType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactVoiceTelephone")
    public JAXBElement<String> createContactVoiceTelephone(final String value) {
        return new JAXBElement<String>(_ContactVoiceTelephone_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Abstract")
    public JAXBElement<String> createAbstract(final String value) {
        return new JAXBElement<String>(_Abstract_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "MaxWidth")
    public JAXBElement<Integer> createMaxWidth(final Integer value) {
        return new JAXBElement<Integer>(_MaxWidth_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "AccessConstraints")
    public JAXBElement<String> createAccessConstraints(final String value) {
        return new JAXBElement<String>(_AccessConstraints_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "_ExtendedCapabilities")
    public JAXBElement<Object> createExtendedCapabilities(final Object value) {
        return new JAXBElement<Object>(_ExtendedCapabilities_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactPerson")
    public JAXBElement<String> createContactPerson(final String value) {
        return new JAXBElement<String>(_ContactPerson_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "GetCapabilities")
    public JAXBElement<OperationType> createGetCapabilities(final OperationType value) {
        return new JAXBElement<OperationType>(_GetCapabilities_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "LayerLimit")
    public JAXBElement<Integer> createLayerLimit(final Integer value) {
        return new JAXBElement<Integer>(_LayerLimit_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactOrganization")
    public JAXBElement<String> createContactOrganization(final String value) {
        return new JAXBElement<String>(_ContactOrganization_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "_ExtendedOperation")
    public JAXBElement<OperationType> createExtendedOperation(final OperationType value) {
        return new JAXBElement<OperationType>(_ExtendedOperation_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Country")
    public JAXBElement<String> createCountry(final String value) {
        return new JAXBElement<String>(_Country_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "City")
    public JAXBElement<String> createCity(final String value) {
        return new JAXBElement<String>(_City_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Title")
    public JAXBElement<String> createTitle(final String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Fees")
    public JAXBElement<String> createFees(final String value) {
        return new JAXBElement<String>(_Fees_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "GetMap")
    public JAXBElement<OperationType> createGetMap(final OperationType value) {
        return new JAXBElement<OperationType>(_GetMap_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "GetFeatureInfo")
    public JAXBElement<OperationType> createGetFeatureInfo(final OperationType value) {
        return new JAXBElement<OperationType>(_GetFeatureInfo_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "StateOrProvince")
    public JAXBElement<String> createStateOrProvince(final String value) {
        return new JAXBElement<String>(_StateOrProvince_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "Format")
    public JAXBElement<String> createFormat(final String value) {
        return new JAXBElement<String>(_Format_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "CRS")
    public JAXBElement<String> createCRS(final String value) {
        return new JAXBElement<String>(_CRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "MaxScaleDenominator")
    public JAXBElement<Double> createMaxScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MaxScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactElectronicMailAddress")
    public JAXBElement<String> createContactElectronicMailAddress(final String value) {
        return new JAXBElement<String>(_ContactElectronicMailAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wms", name = "ContactPosition")
    public JAXBElement<String> createContactPosition(final String value) {
        return new JAXBElement<String>(_ContactPosition_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "GetLegendGraphic", substitutionHeadNamespace = "http://www.opengis.net/wms", substitutionHeadName = "_ExtendedOperation")
    public JAXBElement<OperationType> createGetLegendGraphic(final OperationType value) {
        return new JAXBElement<OperationType>(_GetLegendGraphic_QNAME, OperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "DescribeLayer", substitutionHeadNamespace = "http://www.opengis.net/wms", substitutionHeadName = "_ExtendedOperation")
    public JAXBElement<OperationType> createDescribeLayer(final OperationType value) {
        return new JAXBElement<OperationType>(_DescribeLayer_QNAME, OperationType.class, null, value);
    }
}
