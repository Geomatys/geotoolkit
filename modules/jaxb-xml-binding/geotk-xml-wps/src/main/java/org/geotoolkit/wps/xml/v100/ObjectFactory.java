/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.wps.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.wps._1_0 package. 
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

    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/wps/1.0.0", "Capabilities");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps._1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessStartedType }
     * 
     */
    public ProcessStartedType createProcessStartedType() {
        return new ProcessStartedType();
    }

    /**
     * Create an instance of {@link Execute }
     * 
     */
    public Execute createExecute() {
        return new Execute();
    }

    /**
     * Create an instance of {@link ComplexDataDescriptionType }
     * 
     */
    public ComplexDataDescriptionType createComplexDataDescriptionType() {
        return new ComplexDataDescriptionType();
    }

    /**
     * Create an instance of {@link ProcessDescriptionType.DataInputs }
     * 
     */
    public ProcessDescriptionType.DataInputs createProcessDescriptionTypeDataInputs() {
        return new ProcessDescriptionType.DataInputs();
    }

    /**
     * Create an instance of {@link ComplexDataCombinationType }
     * 
     */
    public ComplexDataCombinationType createComplexDataCombinationType() {
        return new ComplexDataCombinationType();
    }

    /**
     * Create an instance of {@link ProcessDescriptionType.ProcessOutputs }
     * 
     */
    public ProcessDescriptionType.ProcessOutputs createProcessDescriptionTypeProcessOutputs() {
        return new ProcessDescriptionType.ProcessOutputs();
    }

    /**
     * Create an instance of {@link ComplexDataCombinationsType }
     * 
     */
    public ComplexDataCombinationsType createComplexDataCombinationsType() {
        return new ComplexDataCombinationsType();
    }

    /**
     * Create an instance of {@link ResponseDocumentType }
     * 
     */
    public ResponseDocumentType createResponseDocumentType() {
        return new ResponseDocumentType();
    }

    /**
     * Create an instance of {@link LiteralDataType }
     * 
     */
    public LiteralDataType createLiteralDataType() {
        return new LiteralDataType();
    }

    /**
     * Create an instance of {@link ComplexDataType }
     * 
     */
    public ComplexDataType createComplexDataType() {
        return new ComplexDataType();
    }

    /**
     * Create an instance of {@link SupportedUOMsType }
     * 
     */
    public SupportedUOMsType createSupportedUOMsType() {
        return new SupportedUOMsType();
    }

    /**
     * Create an instance of {@link InputDescriptionType }
     * 
     */
    public InputDescriptionType createInputDescriptionType() {
        return new InputDescriptionType();
    }

    /**
     * Create an instance of {@link ResponseBaseType }
     * 
     */
    public ResponseBaseType createResponseBaseType() {
        return new ResponseBaseType();
    }

    /**
     * Create an instance of {@link WSDL }
     * 
     */
    public WSDL createWSDL() {
        return new WSDL();
    }

    /**
     * Create an instance of {@link ResponseFormType }
     * 
     */
    public ResponseFormType createResponseFormType() {
        return new ResponseFormType();
    }

    /**
     * Create an instance of {@link UOMsType }
     * 
     */
    public UOMsType createUOMsType() {
        return new UOMsType();
    }

    /**
     * Create an instance of {@link ProcessOfferings }
     * 
     */
    public ProcessOfferings createProcessOfferings() {
        return new ProcessOfferings();
    }

    /**
     * Create an instance of {@link OutputDefinitionsType }
     * 
     */
    public OutputDefinitionsType createOutputDefinitionsType() {
        return new OutputDefinitionsType();
    }

    /**
     * Create an instance of {@link RequestBaseType }
     * 
     */
    public RequestBaseType createRequestBaseType() {
        return new RequestBaseType();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link StatusType }
     * 
     */
    public StatusType createStatusType() {
        return new StatusType();
    }

    /**
     * Create an instance of {@link GetCapabilities }
     * 
     */
    public GetCapabilities createGetCapabilities() {
        return new GetCapabilities();
    }

    /**
     * Create an instance of {@link LiteralOutputType }
     * 
     */
    public LiteralOutputType createLiteralOutputType() {
        return new LiteralOutputType();
    }

    /**
     * Create an instance of {@link SupportedCRSsType }
     * 
     */
    public SupportedCRSsType createSupportedCRSsType() {
        return new SupportedCRSsType();
    }

    /**
     * Create an instance of {@link DocumentOutputDefinitionType }
     * 
     */
    public DocumentOutputDefinitionType createDocumentOutputDefinitionType() {
        return new DocumentOutputDefinitionType();
    }

    /**
     * Create an instance of {@link SupportedCRSsType.Default }
     * 
     */
    public SupportedCRSsType.Default createSupportedCRSsTypeDefault() {
        return new SupportedCRSsType.Default();
    }

    /**
     * Create an instance of {@link WPSCapabilitiesType }
     * 
     */
    public WPSCapabilitiesType createWPSCapabilitiesType() {
        return new WPSCapabilitiesType();
    }

    /**
     * Create an instance of {@link DescribeProcess }
     * 
     */
    public DescribeProcess createDescribeProcess() {
        return new DescribeProcess();
    }

    /**
     * Create an instance of {@link Languages }
     * 
     */
    public Languages createLanguages() {
        return new Languages();
    }

    /**
     * Create an instance of {@link InputReferenceType.BodyReference }
     * 
     */
    public InputReferenceType.BodyReference createInputReferenceTypeBodyReference() {
        return new InputReferenceType.BodyReference();
    }

    /**
     * Create an instance of {@link SupportedUOMsType.Default }
     * 
     */
    public SupportedUOMsType.Default createSupportedUOMsTypeDefault() {
        return new SupportedUOMsType.Default();
    }

    /**
     * Create an instance of {@link ProcessBriefType }
     * 
     */
    public ProcessBriefType createProcessBriefType() {
        return new ProcessBriefType();
    }

    /**
     * Create an instance of {@link InputReferenceType }
     * 
     */
    public InputReferenceType createInputReferenceType() {
        return new InputReferenceType();
    }

    /**
     * Create an instance of {@link SupportedComplexDataType }
     * 
     */
    public SupportedComplexDataType createSupportedComplexDataType() {
        return new SupportedComplexDataType();
    }

    /**
     * Create an instance of {@link InputType }
     * 
     */
    public InputType createInputType() {
        return new InputType();
    }

    /**
     * Create an instance of {@link OutputDefinitionType }
     * 
     */
    public OutputDefinitionType createOutputDefinitionType() {
        return new OutputDefinitionType();
    }

    /**
     * Create an instance of {@link OutputDataType }
     * 
     */
    public OutputDataType createOutputDataType() {
        return new OutputDataType();
    }

    /**
     * Create an instance of {@link ExecuteResponse.ProcessOutputs }
     * 
     */
    public ExecuteResponse.ProcessOutputs createExecuteResponseProcessOutputs() {
        return new ExecuteResponse.ProcessOutputs();
    }

    /**
     * Create an instance of {@link OutputReferenceType }
     * 
     */
    public OutputReferenceType createOutputReferenceType() {
        return new OutputReferenceType();
    }

    /**
     * Create an instance of {@link InputReferenceType.Header }
     * 
     */
    public InputReferenceType.Header createInputReferenceTypeHeader() {
        return new InputReferenceType.Header();
    }

    /**
     * Create an instance of {@link ProcessFailedType }
     * 
     */
    public ProcessFailedType createProcessFailedType() {
        return new ProcessFailedType();
    }

    /**
     * Create an instance of {@link ExecuteResponse }
     * 
     */
    public ExecuteResponse createExecuteResponse() {
        return new ExecuteResponse();
    }

    /**
     * Create an instance of {@link LiteralInputType }
     * 
     */
    public LiteralInputType createLiteralInputType() {
        return new LiteralInputType();
    }

    /**
     * Create an instance of {@link ProcessDescriptions }
     * 
     */
    public ProcessDescriptions createProcessDescriptions() {
        return new ProcessDescriptions();
    }

    /**
     * Create an instance of {@link SupportedComplexDataInputType }
     * 
     */
    public SupportedComplexDataInputType createSupportedComplexDataInputType() {
        return new SupportedComplexDataInputType();
    }

    /**
     * Create an instance of {@link ProcessDescriptionType }
     * 
     */
    public ProcessDescriptionType createProcessDescriptionType() {
        return new ProcessDescriptionType();
    }

    /**
     * Create an instance of {@link LanguagesType }
     * 
     */
    public LanguagesType createLanguagesType() {
        return new LanguagesType();
    }

    /**
     * Create an instance of {@link CRSsType }
     * 
     */
    public CRSsType createCRSsType() {
        return new CRSsType();
    }

    /**
     * Create an instance of {@link DescriptionType }
     * 
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link OutputDescriptionType }
     * 
     */
    public OutputDescriptionType createOutputDescriptionType() {
        return new OutputDescriptionType();
    }

    /**
     * Create an instance of {@link ValuesReferenceType }
     * 
     */
    public ValuesReferenceType createValuesReferenceType() {
        return new ValuesReferenceType();
    }

    /**
     * Create an instance of {@link DataInputsType }
     * 
     */
    public DataInputsType createDataInputsType() {
        return new DataInputsType();
    }

    /**
     * Create an instance of {@link Languages.Default }
     * 
     */
    public Languages.Default createLanguagesDefault() {
        return new Languages.Default();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WPSCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wps/1.0.0", name = "Capabilities")
    public JAXBElement<WPSCapabilitiesType> createCapabilities(WPSCapabilitiesType value) {
        return new JAXBElement<WPSCapabilitiesType>(_Capabilities_QNAME, WPSCapabilitiesType.class, null, value);
    }

}
