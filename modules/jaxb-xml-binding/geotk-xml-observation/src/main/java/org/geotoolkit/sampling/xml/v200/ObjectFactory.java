/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sampling.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.sampling._2 package. 
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

    private final static QName _SFSamplingFeatureCollection_QNAME = new QName("http://www.opengis.net/sampling/2.0", "SF_SamplingFeatureCollection");
    private final static QName _SamplingFeatureComplex_QNAME = new QName("http://www.opengis.net/sampling/2.0", "SamplingFeatureComplex");
    private final static QName _SFSamplingFeature_QNAME = new QName("http://www.opengis.net/sampling/2.0", "SF_SamplingFeature");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sampling._2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SFSamplingFeatureCollectionPropertyType }
     * 
     */
    public SFSamplingFeatureCollectionPropertyType createSFSamplingFeatureCollectionPropertyType() {
        return new SFSamplingFeatureCollectionPropertyType();
    }

    /**
     * Create an instance of {@link SFSamplingFeatureCollectionType }
     * 
     */
    public SFSamplingFeatureCollectionType createSFSamplingFeatureCollectionType() {
        return new SFSamplingFeatureCollectionType();
    }

    /**
     * Create an instance of {@link SamplingFeatureComplexType }
     * 
     */
    public SamplingFeatureComplexType createSamplingFeatureComplexType() {
        return new SamplingFeatureComplexType();
    }

    /**
     * Create an instance of {@link SFSamplingFeaturePropertyType }
     * 
     */
    public SFSamplingFeaturePropertyType createSFSamplingFeaturePropertyType() {
        return new SFSamplingFeaturePropertyType();
    }

    /**
     * Create an instance of {@link SFSamplingFeatureType }
     * 
     */
    public SFSamplingFeatureType createSFSamplingFeatureType() {
        return new SFSamplingFeatureType();
    }

    /**
     * Create an instance of {@link SFProcessPropertyType }
     * 
     */
    public SFProcessPropertyType createSFProcessPropertyType() {
        return new SFProcessPropertyType();
    }

    /**
     * Create an instance of {@link SamplingFeatureComplexPropertyType }
     * 
     */
    public SamplingFeatureComplexPropertyType createSamplingFeatureComplexPropertyType() {
        return new SamplingFeatureComplexPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SFSamplingFeatureCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/2.0", name = "SF_SamplingFeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SFSamplingFeatureCollectionType> createSFSamplingFeatureCollection(SFSamplingFeatureCollectionType value) {
        return new JAXBElement<SFSamplingFeatureCollectionType>(_SFSamplingFeatureCollection_QNAME, SFSamplingFeatureCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingFeatureComplexType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/2.0", name = "SamplingFeatureComplex")
    public JAXBElement<SamplingFeatureComplexType> createSamplingFeatureComplex(SamplingFeatureComplexType value) {
        return new JAXBElement<SamplingFeatureComplexType>(_SamplingFeatureComplex_QNAME, SamplingFeatureComplexType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SFSamplingFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/2.0", name = "SF_SamplingFeature", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SFSamplingFeatureType> createSFSamplingFeature(SFSamplingFeatureType value) {
        return new JAXBElement<SFSamplingFeatureType>(_SFSamplingFeature_QNAME, SFSamplingFeatureType.class, null, value);
    }

}
