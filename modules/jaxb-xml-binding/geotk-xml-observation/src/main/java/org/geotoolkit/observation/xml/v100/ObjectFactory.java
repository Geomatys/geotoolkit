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
 * @module pending
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
     * Create an instance of {@link ObservationEntry }
     * 
     */
    public ObservationEntry createObservationEntry() {
        return new ObservationEntry();
    }
    
    /**
     * Create an instance of {@link ObservationCollectionEntry }
     * 
     */
    public ObservationCollectionEntry createObservationCollectionEntry() {
        return new ObservationCollectionEntry();
    }
    
      /**
     * Create an instance of {@link MeasurementEntry }
     * 
     */
    public MeasurementEntry createMeasurementEntry() {
        return new MeasurementEntry();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "Observation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<ObservationEntry> createObservation(final ObservationEntry value) {
        return new JAXBElement<ObservationEntry>(_Observation_QNAME, ObservationEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationEntry }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "Measurement", substitutionHeadNamespace = "http://www.opengis.net/om/1.0", substitutionHeadName = "Observation")
    public JAXBElement<MeasurementEntry> createMeasurement(final MeasurementEntry value) {
        return new JAXBElement<MeasurementEntry>(_Measurement_QNAME, MeasurementEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationEntry }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "result")
    public JAXBElement<Object> createResult(final Object value) {
        return new JAXBElement<Object>(_Result_QNAME, Object.class, null, value);
    }
}
