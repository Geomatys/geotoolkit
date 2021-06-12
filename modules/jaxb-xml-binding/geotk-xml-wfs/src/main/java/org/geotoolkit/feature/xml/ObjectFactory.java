/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.feature.xml;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the net.opengis.wfs package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 *
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
     * Create an instance of {@link collections}
     *
     * @return
     */
    public collections createCollections() {
        return new collections();
    }

    /**
     * Create an instance of {@link Collection}
     *
     * @return
     */
    public Collection createCollection() {
        return new Collection();
    }
}
