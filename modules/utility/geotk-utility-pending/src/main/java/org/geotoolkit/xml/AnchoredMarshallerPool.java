/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.xml;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AnchoredMarshallerPool extends MarshallerPool {

    public AnchoredMarshallerPool() throws JAXBException {
        super();
    }

    private String schemaLocation = null;

    /**
     * Creates a new factory for the given class to be bound, with a default empty namespace.
     *
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final Class<?>... classesToBeBound) throws JAXBException {
        super(classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final Class<?>... classesToBeBound) throws JAXBException {
        super(Collections.singletonMap(ROOT_NAMESPACE_KEY, rootNamespace), classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String schemaLocation, final Class<?>... classesToBeBound) throws JAXBException {
        super(getProperties(rootNamespace), classesToBeBound);
        this.schemaLocation = schemaLocation;
    }

    /**
     * Creates a new factory for the given packages, with a default empty namespace.
     * The separator character for the packages is the colon.
     *
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String packages) throws JAXBException {
        super(packages);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages) throws JAXBException {
        super(Collections.singletonMap(ROOT_NAMESPACE_KEY, rootNamespace), packages);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @param schemaLocation    The main xsd schema location for all the returned xml.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages, final String schemaLocation) throws JAXBException {
        super(getProperties(rootNamespace), packages);
        this.schemaLocation = schemaLocation;

    }

    /**
     * Return a Map of Marshaller properties.
     *
     * @param rootNamespace The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param schemaLocation The main xsd schema location for all the returned xml.
     * @return
     */
    public static Map<String, String> getProperties(final String rootNamespace) {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put(ROOT_NAMESPACE_KEY, rootNamespace);
        return properties;
    }

    @Override
    protected Marshaller createMarshaller() throws JAXBException {
        final Marshaller marshaller = super.createMarshaller();
        if (schemaLocation != null) {
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
        }
        return marshaller;
    }

    @Override
    public void addAnchor(final String text, final URI uri) {
        super.addAnchor(text, uri);
    }
}
