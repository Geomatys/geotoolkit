/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.samplingspatial.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.samplingspatial._2 package. 
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

    private final static QName _Shape_QNAME = new QName("http://www.opengis.net/samplingSpatial/2.0", "shape");
    private final static QName _SFSpatialSamplingFeature_QNAME = new QName("http://www.opengis.net/samplingSpatial/2.0", "SF_SpatialSamplingFeature");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.samplingspatial._2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SFSpatialSamplingFeaturePropertyType }
     * 
     */
    public SFSpatialSamplingFeaturePropertyType createSFSpatialSamplingFeaturePropertyType() {
        return new SFSpatialSamplingFeaturePropertyType();
    }

    /**
     * Create an instance of {@link SFSpatialSamplingFeatureType }
     * 
     */
    public SFSpatialSamplingFeatureType createSFSpatialSamplingFeatureType() {
        return new SFSpatialSamplingFeatureType();
    }

    /**
     * Create an instance of {@link ShapeType }
     * 
     */
    public ShapeType createShapeType() {
        return new ShapeType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShapeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/samplingSpatial/2.0", name = "shape")
    public JAXBElement<ShapeType> createShape(ShapeType value) {
        return new JAXBElement<ShapeType>(_Shape_QNAME, ShapeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SFSpatialSamplingFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/samplingSpatial/2.0", name = "SF_SpatialSamplingFeature", substitutionHeadNamespace = "http://www.opengis.net/sampling/2.0", substitutionHeadName = "SF_SamplingFeature")
    public JAXBElement<SFSpatialSamplingFeatureType> createSFSpatialSamplingFeature(SFSpatialSamplingFeatureType value) {
        return new JAXBElement<SFSpatialSamplingFeatureType>(_SFSpatialSamplingFeature_QNAME, SFSpatialSamplingFeatureType.class, null, value);
    }

}
