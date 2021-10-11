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


package org.geotoolkit.opt.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the _int.esa.earth.opt package.
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

    private final static QName _EarthObservation_QNAME = new QName("http://earth.esa.int/opt", "EarthObservation");
    private final static QName _EarthObservationResult_QNAME = new QName("http://earth.esa.int/opt", "EarthObservationResult");
    private final static QName _Acquisition_QNAME = new QName("http://earth.esa.int/opt", "Acquisition");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.earth.opt
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
     * Create an instance of {@link AcquisitionType }
     *
     */
    public AcquisitionType createAcquisitionType() {
        return new AcquisitionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/opt", name = "EarthObservation", substitutionHeadNamespace = "http://earth.esa.int/eop", substitutionHeadName = "EarthObservation")
    public JAXBElement<EarthObservationType> createEarthObservation(EarthObservationType value) {
        return new JAXBElement<EarthObservationType>(_EarthObservation_QNAME, EarthObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/opt", name = "EarthObservationResult", substitutionHeadNamespace = "http://earth.esa.int/eop", substitutionHeadName = "EarthObservationResult")
    public JAXBElement<EarthObservationResultType> createEarthObservationResult(EarthObservationResultType value) {
        return new JAXBElement<EarthObservationResultType>(_EarthObservationResult_QNAME, EarthObservationResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcquisitionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/opt", name = "Acquisition", substitutionHeadNamespace = "http://earth.esa.int/eop", substitutionHeadName = "Acquisition")
    public JAXBElement<AcquisitionType> createAcquisition(AcquisitionType value) {
        return new JAXBElement<AcquisitionType>(_Acquisition_QNAME, AcquisitionType.class, null, value);
    }

}
