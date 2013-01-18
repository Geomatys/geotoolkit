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

package org.geotoolkit.gts.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.isotc211._2005.gts package. 
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

    private final static QName _TMPeriodDuration_QNAME = new QName("http://www.isotc211.org/2005/gts", "TM_PeriodDuration");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.isotc211._2005.gts
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TMPeriodDurationPropertyType }
     * 
     */
    public PeriodDurationType createTMPeriodDurationPropertyType() {
        return new PeriodDurationType();
    }

    /**
     * Create an instance of {@link TMPrimitivePropertyType }
     * 
     */
    public TMPrimitivePropertyType createTMPrimitivePropertyType() {
        return new TMPrimitivePropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.isotc211.org/2005/gts", name = "TM_PeriodDuration")
    public JAXBElement<Duration> createTMPeriodDuration(Duration value) {
        return new JAXBElement<Duration>(_TMPeriodDuration_QNAME, Duration.class, null, value);
    }

}
