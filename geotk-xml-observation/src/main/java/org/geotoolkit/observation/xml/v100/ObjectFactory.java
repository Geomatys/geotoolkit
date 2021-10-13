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
package org.geotoolkit.observation.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Observation_QNAME   = new QName("http://www.opengis.net/om/1.0", "Observation");
    private static final QName _Measurement_QNAME   = new QName("http://www.opengis.net/om/1.0", "Measurement");
    private static final QName _Result_QNAME        = new QName("http://www.opengis.net/om/1.0", "result");

    /**
     *
     */
    public ObjectFactory() {
    }

     /**
     * Create an instance of {@link ObservationType }
     *
     */
    public ObservationType createObservationType() {
        return new ObservationType();
    }

    /**
     * Create an instance of {@link ObservationCollectionType }
     *
     */
    public ObservationCollectionType createObservationCollectionType() {
        return new ObservationCollectionType();
    }

      /**
     * Create an instance of {@link MeasurementType }
     *
     */
    public MeasurementType createMeasurementType() {
        return new MeasurementType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "Observation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<ObservationType> createObservation(final ObservationType value) {
        return new JAXBElement<ObservationType>(_Observation_QNAME, ObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "Measurement", substitutionHeadNamespace = "http://www.opengis.net/om/1.0", substitutionHeadName = "Observation")
    public JAXBElement<MeasurementType> createMeasurement(final MeasurementType value) {
        return new JAXBElement<MeasurementType>(_Measurement_QNAME, MeasurementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "result")
    public JAXBElement<Object> createResult(final Object value) {
        return new JAXBElement<Object>(_Result_QNAME, Object.class, null, value);
    }
}
