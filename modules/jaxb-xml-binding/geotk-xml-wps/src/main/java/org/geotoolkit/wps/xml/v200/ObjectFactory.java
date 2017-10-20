/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.wps._2 package.
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

    private final static QName _ExpirationDate_QNAME = new QName("http://www.opengis.net/wps/2.0", "ExpirationDate");
    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/wps/2.0", "Capabilities");
    private final static QName _BoundingBoxData_QNAME = new QName("http://www.opengis.net/wps/2.0", "BoundingBoxData");
    private final static QName _Process_QNAME = new QName("http://www.opengis.net/wps/2.0", "Process");
    private final static QName _ComplexData_QNAME = new QName("http://www.opengis.net/wps/2.0", "ComplexData");
    private final static QName _DataDescription_QNAME = new QName("http://www.opengis.net/wps/2.0", "DataDescription");
    private final static QName _Reference_QNAME = new QName("http://www.opengis.net/wps/2.0", "Reference");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/wps/2.0", "GetCapabilities");
    private final static QName _JobID_QNAME = new QName("http://www.opengis.net/wps/2.0", "JobID");
    private final static QName _Execute_QNAME = new QName("http://www.opengis.net/wps/2.0", "Execute");
    private final static QName _LiteralData_QNAME = new QName("http://www.opengis.net/wps/2.0", "LiteralData");
    private final static QName _GenericProcess_QNAME = new QName("http://www.opengis.net/wps/2.0", "GenericProcess");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WPSCapabilitiesType }
     *
     */
    public WPSCapabilitiesType createWPSCapabilitiesType() {
        return new WPSCapabilitiesType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     *
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link LiteralDataType }
     *
     */
    public LiteralDataType createLiteralDataType() {
        return new LiteralDataType();
    }

    /**
     * Create an instance of {@link ExecuteRequestType }
     *
     */
    public ExecuteRequestType createExecuteRequestType() {
        return new ExecuteRequestType();
    }

    /**
     * Create an instance of {@link ProcessDescriptionType }
     *
     */
    public ProcessDescriptionType createProcessDescriptionType() {
        return new ProcessDescriptionType();
    }

    /**
     * Create an instance of {@link ProcessOfferings }
     *
     */
    public ProcessOfferings createProcessOfferings() {
        return new ProcessOfferings();
    }

    /**
     * Create an instance of {@link ProcessOffering }
     *
     */
    public ProcessOffering createProcessOffering() {
        return new ProcessOffering();
    }

    /**
     * Create an instance of {@link Result }
     *
     */
    public Result createResult() {
        return new Result();
    }

    /**
     * Create an instance of {@link DataOutputType }
     *
     */
    public DataOutputType createDataOutputType() {
        return new DataOutputType();
    }

    /**
     * Create an instance of {@link Format }
     *
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link ComplexDataType }
     *
     */
    public ComplexDataType createComplexDataType() {
        return new ComplexDataType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     *
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link BoundingBoxData }
     *
     */
    public BoundingBoxData createBoundingBoxData() {
        return new BoundingBoxData();
    }

    /**
     * Create an instance of {@link DescribeProcess }
     *
     */
    public DescribeProcess createDescribeProcess() {
        return new DescribeProcess();
    }

    /**
     * Create an instance of {@link GenericProcessType }
     *
     */
    public GenericProcessType createGenericProcessType() {
        return new GenericProcessType();
    }

    /**
     * Create an instance of {@link GetStatus }
     *
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link GetResult }
     *
     */
    public GetResult createGetResult() {
        return new GetResult();
    }

    /**
     * Create an instance of {@link LiteralValue }
     *
     */
    public LiteralValue createLiteralValue() {
        return new LiteralValue();
    }

    /**
     * Create an instance of {@link Data }
     *
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link Contents }
     *
     */
    public Contents createContents() {
        return new Contents();
    }

    /**
     * Create an instance of {@link ProcessSummaryType }
     *
     */
    public ProcessSummaryType createProcessSummaryType() {
        return new ProcessSummaryType();
    }

    /**
     * Create an instance of {@link StatusInfo }
     *
     */
    public StatusInfo createStatusInfo() {
        return new StatusInfo();
    }

    /**
     * Create an instance of {@link Dismiss }
     *
     */
    public Dismiss createDismiss() {
        return new Dismiss();
    }

    /**
     * Create an instance of {@link SupportedCRS }
     *
     */
    public SupportedCRS createSupportedCRS() {
        return new SupportedCRS();
    }

    /**
     * Create an instance of {@link DescriptionType }
     *
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link GenericInputType }
     *
     */
    public GenericInputType createGenericInputType() {
        return new GenericInputType();
    }

    /**
     * Create an instance of {@link GenericOutputType }
     *
     */
    public GenericOutputType createGenericOutputType() {
        return new GenericOutputType();
    }

    /**
     * Create an instance of {@link InputDescriptionType }
     *
     */
    public InputDescriptionType createInputDescriptionType() {
        return new InputDescriptionType();
    }

    /**
     * Create an instance of {@link OutputDescriptionType }
     *
     */
    public OutputDescriptionType createOutputDescriptionType() {
        return new OutputDescriptionType();
    }

    /**
     * Create an instance of {@link OutputDefinitionType }
     *
     */
    public OutputDefinitionType createOutputDefinitionType() {
        return new OutputDefinitionType();
    }

    /**
     * Create an instance of {@link DataInputType }
     *
     */
    public DataInputType createDataInputType() {
        return new DataInputType();
    }

    /**
     * Create an instance of {@link LiteralDataDomainType }
     *
     */
    public LiteralDataDomainType createLiteralDataDomainType() {
        return new LiteralDataDomainType();
    }

    /**
     * Create an instance of {@link WPSCapabilitiesType.Extension }
     *
     */
    public WPSCapabilitiesType.Extension createWPSCapabilitiesTypeExtension() {
        return new WPSCapabilitiesType.Extension();
    }

    /**
     * Create an instance of {@link ReferenceType.BodyReference }
     *
     */
    public ReferenceType.BodyReference createReferenceTypeBodyReference() {
        return new ReferenceType.BodyReference();
    }

    /**
     * Create an instance of {@link LiteralDataType.LiteralDataDomain }
     *
     */
    public LiteralDataType.LiteralDataDomain createLiteralDataTypeLiteralDataDomain() {
        return new LiteralDataType.LiteralDataDomain();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "JobID")
    public JAXBElement<String> createJobID(String value) {
        return new JAXBElement<String>(_JobID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiteralDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "LiteralData", substitutionHeadNamespace = "http://www.opengis.net/wps/2.0", substitutionHeadName = "DataDescription")
    public JAXBElement<LiteralDataType> createLiteralData(LiteralDataType value) {
        return new JAXBElement<LiteralDataType>(_LiteralData_QNAME, LiteralDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "Process")
    public JAXBElement<ProcessDescriptionType> createProcess(ProcessDescriptionType value) {
        return new JAXBElement<ProcessDescriptionType>(_Process_QNAME, ProcessDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WPSCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "Capabilities")
    public JAXBElement<WPSCapabilitiesType> createCapabilities(WPSCapabilitiesType value) {
        return new JAXBElement<WPSCapabilitiesType>(_Capabilities_QNAME, WPSCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "ExpirationDate")
    public JAXBElement<XMLGregorianCalendar> createExpirationDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ExpirationDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<ReferenceType>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxData }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "BoundingBoxData", substitutionHeadNamespace = "http://www.opengis.net/wps/2.0", substitutionHeadName = "DataDescription")
    public JAXBElement<BoundingBoxData> createBoundingBoxData(BoundingBoxData value) {
        return new JAXBElement<BoundingBoxData>(_BoundingBoxData_QNAME, BoundingBoxData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericProcessType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "GenericProcess")
    public JAXBElement<GenericProcessType> createGenericProcess(GenericProcessType value) {
        return new JAXBElement<GenericProcessType>(_GenericProcess_QNAME, GenericProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComplexDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "ComplexData", substitutionHeadNamespace = "http://www.opengis.net/wps/2.0", substitutionHeadName = "DataDescription")
    public JAXBElement<ComplexDataType> createComplexData(ComplexDataType value) {
        return new JAXBElement<ComplexDataType>(_ComplexData_QNAME, ComplexDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "DataDescription")
    public JAXBElement<DataDescriptionType> createDataDescription(DataDescriptionType value) {
        return new JAXBElement<DataDescriptionType>(_DataDescription_QNAME, DataDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/2.0", name = "Execute")
    public JAXBElement<ExecuteRequestType> createExecute(ExecuteRequestType value) {
        return new JAXBElement<ExecuteRequestType>(_Execute_QNAME, ExecuteRequestType.class, null, value);
    }

}
