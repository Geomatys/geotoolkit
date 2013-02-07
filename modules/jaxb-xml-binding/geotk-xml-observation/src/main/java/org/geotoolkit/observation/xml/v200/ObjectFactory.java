/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.observation.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.om._2 package. 
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

    private final static QName _NamedValue_QNAME = new QName("http://www.opengis.net/om/2.0", "NamedValue");
    private final static QName _Result_QNAME = new QName("http://www.opengis.net/om/2.0", "result");
    private final static QName _ObservationContext_QNAME = new QName("http://www.opengis.net/om/2.0", "ObservationContext");
    private final static QName _OMObservation_QNAME = new QName("http://www.opengis.net/om/2.0", "OM_Observation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.om._2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OMObservationType }
     * 
     */
    public OMObservationType createOMObservationType() {
        return new OMObservationType();
    }

    /**
     * Create an instance of {@link TimeObjectPropertyType }
     * 
     */
    public TimeObjectPropertyType createTimeObjectPropertyType() {
        return new TimeObjectPropertyType();
    }

    /**
     * Create an instance of {@link ObservationContextType }
     * 
     */
    public ObservationContextType createObservationContextType() {
        return new ObservationContextType();
    }

    /**
     * Create an instance of {@link NamedValueType }
     * 
     */
    public NamedValueType createNamedValueType() {
        return new NamedValueType();
    }

    /**
     * Create an instance of {@link NamedValuePropertyType }
     * 
     */
    public NamedValuePropertyType createNamedValuePropertyType() {
        return new NamedValuePropertyType();
    }

    /**
     * Create an instance of {@link OMObservationPropertyType }
     * 
     */
    public OMObservationPropertyType createOMObservationPropertyType() {
        return new OMObservationPropertyType();
    }

    /**
     * Create an instance of {@link ObservationContextPropertyType }
     * 
     */
    public ObservationContextPropertyType createObservationContextPropertyType() {
        return new ObservationContextPropertyType();
    }

    /**
     * Create an instance of {@link OMProcessPropertyType }
     * 
     */
    public OMProcessPropertyType createOMProcessPropertyType() {
        return new OMProcessPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/2.0", name = "NamedValue")
    public JAXBElement<NamedValueType> createNamedValue(NamedValueType value) {
        return new JAXBElement<NamedValueType>(_NamedValue_QNAME, NamedValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/2.0", name = "result")
    public JAXBElement<Object> createResult(Object value) {
        return new JAXBElement<Object>(_Result_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationContextType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/2.0", name = "ObservationContext")
    public JAXBElement<ObservationContextType> createObservationContext(ObservationContextType value) {
        return new JAXBElement<ObservationContextType>(_ObservationContext_QNAME, ObservationContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OMObservationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/2.0", name = "OM_Observation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<OMObservationType> createOMObservation(OMObservationType value) {
        return new JAXBElement<OMObservationType>(_OMObservation_QNAME, OMObservationType.class, null, value);
    }

}
