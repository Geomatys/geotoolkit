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


package org.geotoolkit.opt.xml.v201;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.opt._2 package.
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

    private final static QName _EarthObservationResult_QNAME = new QName("http://www.opengis.net/opt/2.1", "EarthObservationResult");
    private final static QName _EarthObservation_QNAME = new QName("http://www.opengis.net/opt/2.1", "EarthObservation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.opt._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EarthObservationType }
     *
     */
    public EarthObservationType createEarthObservationType() {
        return new EarthObservationType();
    }

    /**
     * Create an instance of {@link EarthObservationResultType }
     *
     */
    public EarthObservationResultType createEarthObservationResultType() {
        return new EarthObservationResultType();
    }

    /**
     * Create an instance of {@link EarthObservationResultPropertyType }
     *
     */
    public EarthObservationResultPropertyType createEarthObservationResultPropertyType() {
        return new EarthObservationResultPropertyType();
    }

    /**
     * Create an instance of {@link EarthObservationPropertyType }
     *
     */
    public EarthObservationPropertyType createEarthObservationPropertyType() {
        return new EarthObservationPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/opt/2.1", name = "EarthObservationResult", substitutionHeadNamespace = "http://www.opengis.net/eop/2.1", substitutionHeadName = "EarthObservationResult")
    public JAXBElement<EarthObservationResultType> createEarthObservationResult(EarthObservationResultType value) {
        return new JAXBElement<EarthObservationResultType>(_EarthObservationResult_QNAME, EarthObservationResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/opt/2.1", name = "EarthObservation", substitutionHeadNamespace = "http://www.opengis.net/eop/2.1", substitutionHeadName = "EarthObservation")
    public JAXBElement<EarthObservationType> createEarthObservation(EarthObservationType value) {
        return new JAXBElement<EarthObservationType>(_EarthObservation_QNAME, EarthObservationType.class, null, value);
    }

}
