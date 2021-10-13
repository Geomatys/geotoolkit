/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DCPType }
     *
     */
    public DCPType createDCPType() {
        return new DCPType();
    }

    /**
     * Create an instance of {@link HTTP }
     *
     */
    public HTTP createHTTP() {
        return new HTTP();
    }

    /**
     * Create an instance of {@link WMTMSCapabilities }
     *
     */
    public WMTMSCapabilities createWMTMSCapabilities() {
        return new WMTMSCapabilities();
    }

    /**
     * Create an instance of {@link Service }
     *
     */
    public Service createService() {
        return new Service();
    }

    /**
     * Create an instance of {@link Capability }
     *
     */
    public Capability createCapability() {
        return new Capability();
    }

    /**
     * Create an instance of {@link FeatureInfo }
     *
     */
    public FeatureInfo createFeatureInfo() {
        return new FeatureInfo();
    }

    /**
     * Create an instance of {@link Format }
     *
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link LatLonBoundingBox }
     *
     */
    public LatLonBoundingBox createLatLonBoundingBox() {
        return new LatLonBoundingBox();
    }

    /**
     * Create an instance of {@link BoundingBox }
     *
     */
    public BoundingBox createBoundingBox() {
        return new BoundingBox();
    }

    /**
     * Create an instance of {@link Style }
     *
     */
    public Style createStyle() {
        return new Style();
    }

    /**
     * Create an instance of {@link Request }
     *
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link Exception }
     *
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link VendorSpecificCapabilities }
     *
     */
    public VendorSpecificCapabilities createVendorSpecificCapabilities() {
        return new VendorSpecificCapabilities();
    }

    /**
     * Create an instance of {@link Layer }
     *
     */
    public Layer createLayer() {
        return new Layer();
    }

    /**
     * Create an instance of {@link Post }
     *
     */
    public Post createPost() {
        return new Post();
    }

    /**
     * Create an instance of {@link Map }
     *
     */
    public Map createMap() {
        return new Map();
    }

    /**
     * Create an instance of {@link Capabilities }
     *
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link Get }
     *
     */
    public Get createGet() {
        return new Get();
    }

    /**
     * Create an instance of {@link ScaleHint }
     *
     */
    public ScaleHint createScaleHint() {
        return new ScaleHint();
    }

}
