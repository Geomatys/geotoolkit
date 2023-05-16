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

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import static org.geotoolkit.wps.xml.WPSMarshallerPool.WPS_2_0_NAMESPACE;

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

    private final static QName _ExpirationDate_QNAME = new QName(WPS_2_0_NAMESPACE, "ExpirationDate");
    private final static QName _Capabilities_QNAME = new QName(WPS_2_0_NAMESPACE, "Capabilities");
    private final static QName _BoundingBoxData_QNAME = new QName(WPS_2_0_NAMESPACE, "BoundingBoxData");
    private final static QName _Process_QNAME = new QName(WPS_2_0_NAMESPACE, "Process");
    private final static QName _ComplexData_QNAME = new QName(WPS_2_0_NAMESPACE, "ComplexData");
    private final static QName _DataDescription_QNAME = new QName(WPS_2_0_NAMESPACE, "DataDescription");
    private final static QName _Reference_QNAME = new QName(WPS_2_0_NAMESPACE, "Reference");
    private final static QName _GetCapabilities_QNAME = new QName(WPS_2_0_NAMESPACE, "GetCapabilities");
    private final static QName _JobID_QNAME = new QName(WPS_2_0_NAMESPACE, "JobID");
    private final static QName _Execute_QNAME = new QName(WPS_2_0_NAMESPACE, "Execute");
    private final static QName _LiteralData_QNAME = new QName(WPS_2_0_NAMESPACE, "LiteralData");
    private final static QName _GenericProcess_QNAME = new QName(WPS_2_0_NAMESPACE, "GenericProcess");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Reference }
     *
     */
    public Reference createReferenceType() {
        return new Reference();
    }

    /**
     * Create an instance of {@link LiteralData }
     *
     */
    public LiteralData createLiteralDataType() {
        return new LiteralData();
    }

    /**
     * Create an instance of {@link Execute }
     *
     */
    public Execute createExecuteRequestType() {
        return new Execute();
    }

    /**
     * Create an instance of {@link ProcessDescriptionType }
     *
     */
    public ProcessDescription createProcessDescription() {
        return new ProcessDescription();
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
     * Create an instance of {@link DataOutput }
     *
     */
    public DataOutput createDataOutputType() {
        return new DataOutput();
    }

    /**
     * Create an instance of {@link Format }
     *
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link ComplexData }
     *
     */
    public ComplexData createComplexDataType() {
        return new ComplexData();
    }

    /**
     * Create an instance of {@link GetCapabilities }
     *
     */
    public GetCapabilities createGetCapabilitiesType() {
        return new GetCapabilities();
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
     * Create an instance of {@link GenericProcess }
     *
     */
    public GenericProcess createGenericProcessType() {
        return new GenericProcess();
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
     * Create an instance of {@link ProcessSummary }
     *
     */
    public ProcessSummary createProcessSummaryType() {
        return new ProcessSummary();
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
     * Create an instance of {@link Description }
     *
     */
    public Description createDescriptionType() {
        return new Description();
    }

    /**
     * Create an instance of {@link GenericInput }
     *
     */
    public GenericInput createGenericInputType() {
        return new GenericInput();
    }

    /**
     * Create an instance of {@link GenericOutput }
     *
     */
    public GenericOutput createGenericOutputType() {
        return new GenericOutput();
    }

    /**
     * Create an instance of {@link InputDescription }
     *
     */
    public InputDescription createInputDescriptionType() {
        return new InputDescription();
    }

    /**
     * Create an instance of {@link OutputDescription }
     *
     */
    public OutputDescription createOutputDescriptionType() {
        return new OutputDescription();
    }

    /**
     * Create an instance of {@link OutputDefinition }
     *
     */
    public OutputDefinition createOutputDefinitionType() {
        return new OutputDefinition();
    }

    /**
     * Create an instance of {@link DataInput }
     *
     */
    public DataInput createDataInputType() {
        return new DataInput();
    }

    /**
     * Create an instance of {@link LiteralDataDomain }
     *
     */
    public LiteralDataDomain createLiteralDataDomainType() {
        return new LiteralDataDomain();
    }

    /**
     * Create an instance of {@link WPSCapabilitiesType.Extension }
     *
     */
    public Capabilities.Extension createWPSCapabilitiesTypeExtension() {
        return new Capabilities.Extension();
    }

    /**
     * Create an instance of {@link ReferenceType.BodyReference }
     *
     */
    public Reference.BodyReference createReferenceTypeBodyReference() {
        return new Reference.BodyReference();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "JobID")
    public JAXBElement<String> createJobID(String value) {
        return new JAXBElement<String>(_JobID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiteralData }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "LiteralData", substitutionHeadNamespace = WPS_2_0_NAMESPACE, substitutionHeadName = "DataDescription")
    public JAXBElement<LiteralData> createLiteralData(LiteralData value) {
        return new JAXBElement<LiteralData>(_LiteralData_QNAME, LiteralData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessDescription }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "Process")
    public JAXBElement<ProcessDescription> createProcess(ProcessDescription value) {
        return new JAXBElement<ProcessDescription>(_Process_QNAME, ProcessDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WPSCapabilities }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "Capabilities")
    public JAXBElement<Capabilities> createCapabilities(Capabilities value) {
        return new JAXBElement<Capabilities>(_Capabilities_QNAME, Capabilities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "ExpirationDate")
    public JAXBElement<XMLGregorianCalendar> createExpirationDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ExpirationDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Reference }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "ReferenceType")
    public JAXBElement<Reference> createReference(Reference value) {
        return new JAXBElement<Reference>(_Reference_QNAME, Reference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxData }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "BoundingBoxData", substitutionHeadNamespace = WPS_2_0_NAMESPACE, substitutionHeadName = "DataDescription")
    public JAXBElement<BoundingBoxData> createBoundingBoxData(BoundingBoxData value) {
        return new JAXBElement<BoundingBoxData>(_BoundingBoxData_QNAME, BoundingBoxData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericProcess }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "GenericProcess")
    public JAXBElement<GenericProcess> createGenericProcess(GenericProcess value) {
        return new JAXBElement<GenericProcess>(_GenericProcess_QNAME, GenericProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilities }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "GetCapabilities")
    public JAXBElement<GetCapabilities> createGetCapabilities(GetCapabilities value) {
        return new JAXBElement<GetCapabilities>(_GetCapabilities_QNAME, GetCapabilities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComplexData }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "ComplexData", substitutionHeadNamespace = WPS_2_0_NAMESPACE, substitutionHeadName = "DataDescription")
    public JAXBElement<ComplexData> createComplexData(ComplexData value) {
        return new JAXBElement<ComplexData>(_ComplexData_QNAME, ComplexData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataDescription }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "DataDescription")
    public JAXBElement<DataDescription> createDataDescription(DataDescription value) {
        return new JAXBElement<DataDescription>(_DataDescription_QNAME, DataDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Execute }{@code >}}
     *
     */
    @XmlElementDecl(namespace = WPS_2_0_NAMESPACE, name = "Execute")
    public JAXBElement<Execute> createExecute(Execute value) {
        return new JAXBElement<Execute>(_Execute_QNAME, Execute.class, null, value);
    }

}
