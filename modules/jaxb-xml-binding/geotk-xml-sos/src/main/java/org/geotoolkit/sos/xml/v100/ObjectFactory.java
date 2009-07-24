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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.CodeType;

/**
 *
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.constellation.sos package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 *
 * @author legal
 */
@XmlRegistry
public class ObjectFactory {
    
    private final static QName _SrsName_QNAME = new QName("http://www.opengis.net/sos/1.0", "srsName");
    private final static QName _SupportedSensorDescription_QNAME = new QName("http://www.opengis.net/sos/1.0", "supportedSensorDescription");
    private final static QName _SupportedSRS_QNAME = new QName("http://www.opengis.net/sos/1.0", "supportedSRS");
    
    /**
     * Create an instance of {@link ObservationOfferingType }
     * 
     */
    public ObservationOfferingEntry createObservationOfferingType() {
        return new ObservationOfferingEntry();
    }
    
    /**
     * Create an instance of {@link DescribeSensor }
     * 
     */
    public DescribeSensor createDescribeSensor() {
        return new DescribeSensor();
    }

    /**
     * Create an instance of {@link GetObservation }
     * 
     */
    public GetObservation createGetObservation() {
        return new GetObservation();
    }

    /**
     * Create an instance of {@link GetResult }
     * 
     */
    public GetResult createGetResult() {
        return new GetResult();
    }

    /**
     * Create an instance of {@link GetObservation.FeatureOfInterest }
     * 
     */
    public GetObservation.FeatureOfInterest createGetObservationFeatureOfInterest() {
        return new GetObservation.FeatureOfInterest();
    }

    /**
     * Create an instance of {@link InsertObservation }
     * 
     */
    public InsertObservation createInsertObservation() {
        return new InsertObservation();
    }

    /**
     * Create an instance of {@link GetObservationById }
     * 
     */
    public GetObservationById createGetObservationById() {
        return new GetObservationById();
    }

    /**
     * Create an instance of {@link GetResultResponse }
     * 
     */
    public GetResultResponse createGetResultResponse() {
        return new GetResultResponse();
    }

    /**
     * Create an instance of {@link ObservationOfferingType }
     * 
     */
    public ObservationOfferingEntry createObservationOfferingEntry() {
        return new ObservationOfferingEntry();
    }

    /**
     * Create an instance of {@link RegisterSensor }
     * 
     */
    public RegisterSensor createRegisterSensor() {
        return new RegisterSensor();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterest.Location }
     * 
     */
    public GetFeatureOfInterest.Location createGetFeatureOfInterestLocation() {
        return new GetFeatureOfInterest.Location();
    }

    /**
     * Create an instance of {@link ObservationTemplate }
     * 
     */
    public ObservationTemplate createObservationTemplate() {
        return new ObservationTemplate();
    }

    /**
     * Create an instance of {@link GetCapabilities }
     * 
     */
    public GetCapabilities createGetCapabilities() {
        return new GetCapabilities();
    }

    /**
     * Create an instance of {@link Capabilities }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterestTime }
     * 
     */
    public GetFeatureOfInterestTime createGetFeatureOfInterestTime() {
        return new GetFeatureOfInterestTime();
    }

    /**
     * Create an instance of {@link DescribeFeatureType }
     * 
     */
    public DescribeFeatureType createDescribeFeatureType() {
        return new DescribeFeatureType();
    }

    /**
     * Create an instance of {@link InsertObservationResponse }
     * 
     */
    public InsertObservationResponse createInsertObservationResponse() {
        return new InsertObservationResponse();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterest.EventTime }
     * 
     */
    public EventTime createEventTime() {
        return new EventTime();
    }

    /**
     * Create an instance of {@link GetResultResponse.Result }
     * 
     */
    public GetResultResponse.Result createGetResultResponseResult() {
        return new GetResultResponse.Result();
    }

    /**
     * Create an instance of {@link Contents.ObservationOfferingList }
     * 
     */
    public Contents.ObservationOfferingList createContentsObservationOfferingList() {
        return new Contents.ObservationOfferingList();
    }

    /**
     * Create an instance of {@link RegisterSensor.SensorDescription }
     * 
     */
    public RegisterSensor.SensorDescription createRegisterSensorSensorDescription() {
        return new RegisterSensor.SensorDescription();
    }

    /**
     * Create an instance of {@link Contents }
     * 
     */
    public Contents createContents() {
        return new Contents();
    }

    /**
     * Create an instance of {@link DescribeObservationType }
     * 
     */
    public DescribeObservationType createDescribeObservationType() {
        return new DescribeObservationType();
    }

    /**
     * Create an instance of {@link FilterCapabilities }
     * 
     */
    public FilterCapabilities createFilterCapabilities() {
        return new FilterCapabilities();
    }

    /**
     * Create an instance of {@link GetObservation.Result }
     * 
     */
    public GetObservation.Result createGetObservationResult() {
        return new GetObservation.Result();
    }

    /**
     * Create an instance of {@link RequestBaseType }
     * 
     */
    public RequestBaseType createRequestBaseType() {
        return new RequestBaseType();
    }

    /**
     * Create an instance of {@link DescribeResultModel }
     * 
     */
    public DescribeResultModel createDescribeResultModel() {
        return new DescribeResultModel();
    }

    /**
     * Create an instance of {@link RegisterSensorResponse }
     * 
     */
    public RegisterSensorResponse createRegisterSensorResponse() {
        return new RegisterSensorResponse();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterest }
     * 
     */
    public GetFeatureOfInterest createGetFeatureOfInterest() {
        return new GetFeatureOfInterest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/1.0", name = "srsName", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "AbstractMetaData")
    public JAXBElement<CodeType> createSrsName(CodeType value) {
        return new JAXBElement<CodeType>(_SrsName_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/1.0", name = "supportedSensorDescription", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "AbstractMetaData")
    public JAXBElement<QName> createSupportedSensorDescription(QName value) {
        return new JAXBElement<QName>(_SupportedSensorDescription_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/1.0", name = "supportedSRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createSupportedSRS(CodeType value) {
        return new JAXBElement<CodeType>(_SupportedSRS_QNAME, CodeType.class, null, value);
    }
}
