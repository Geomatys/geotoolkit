/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * Object Factory for Common Response (OGC API Common)
 *
 * @author Quentin BIALOTA
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: net.opengis.wfs
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LandingPage}
     *
     * @return
     */
    public LandingPage createLandingPage() {
        return new LandingPage();
    }

    /**
     * Create an instance of {@link Conformance}
     *
     * @return
     */
    public Conformance createConformance() {
        return new Conformance();
    }

    /**
     * Create an instance of {@link Collections}
     *
     * @return
     */
    public Collections createCollections() {
        return new Collections();
    }

    /**
     * Create an instance of {@link CollectionDescription}
     *
     * @return
     */
    public CollectionDescription createCollection() {
        return new CollectionDescription();
    }

    /**
     * Create an instance of {@link Schema}
     *
     * @return
     */
    public Schema createSchema() {return new Schema();}
}
