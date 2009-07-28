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
 */
@XmlRegistry
public class ObjectFactory {
    
    private final static QName _Observation_QNAME   = new QName("http://www.opengis.net/om/1.0", "Observation");
    private final static QName _Result_QNAME        = new QName("http://www.opengis.net/om/1.0", "result");
     
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
    public JAXBElement<ObservationEntry> createObservation(ObservationEntry value) {
        return new JAXBElement<ObservationEntry>(_Observation_QNAME, ObservationEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationEntry }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/om/1.0", name = "result")
    public JAXBElement<Object> createResult(Object value) {
        return new JAXBElement<Object>(_Result_QNAME, Object.class, null, value);
    }
}
