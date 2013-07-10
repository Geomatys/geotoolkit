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
import org.geotoolkit.resources.Errors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.xml.XML;
import org.apache.sis.xml.MarshallerPool;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AnchoredMarshallerPool extends MarshallerPool {

    public AnchoredMarshallerPool() throws JAXBException {
        super(null);
    }

    private String schemaLocation = null;

    /**
     * Binds string labels with URNs or anchors. Values can be either {@link URI} or
     * {@link Anchor} instances. The map is initially null and will be created
     * when first needed.
     *
     * @see #addLinkage(String, URI)
     */
    private Map<String,Object> anchors;

    /**
     * Creates a new factory for the given class to be bound, with a default empty namespace.
     *
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final Class<?>... classesToBeBound) throws JAXBException {
        super(JAXBContext.newInstance(classesToBeBound), null);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final Class<?>... classesToBeBound) throws JAXBException {
        super(JAXBContext.newInstance(classesToBeBound), Collections.singletonMap(XML.DEFAULT_NAMESPACE, rootNamespace));
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String schemaLocation, final Class<?>... classesToBeBound) throws JAXBException {
        super(JAXBContext.newInstance(classesToBeBound), getProperties(rootNamespace));
        this.schemaLocation = schemaLocation;
    }

    /**
     * Creates a new factory for the given packages, with a default empty namespace.
     * The separator character for the packages is the colon.
     *
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.apache.sis.metadata.iso:org.apache.sis.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String packages) throws JAXBException {
        super(JAXBContext.newInstance(packages), null);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.apache.sis.metadata.iso:org.apache.sis.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages) throws JAXBException {
        super(JAXBContext.newInstance(packages), Collections.singletonMap(XML.DEFAULT_NAMESPACE, rootNamespace));
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.apache.sis.metadata.iso:org.apache.sis.metadata.iso.citation"}.
     * @param schemaLocation    The main xsd schema location for all the returned xml.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages, final String schemaLocation) throws JAXBException {
        super(JAXBContext.newInstance(packages), getProperties(rootNamespace));
        this.schemaLocation = schemaLocation;

    }

    /**
     * Return a Map of Marshaller properties.
     *
     * @param rootNamespace The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     */
    public static Map<String, String> getProperties(final String rootNamespace) {
        final Map<String, String> properties = new HashMap<>();
        properties.put(XML.DEFAULT_NAMESPACE, rootNamespace);
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

    /**
     * Adds a label associated to an URN. For some methods expected to return a code as a
     * {@link String} object, the code will be completed by the given URN in an {@code AnchorType}
     * element.
     * <p>
     * This method should be invoked from subclasses constructor only. Anchors can be added
     * but can not be removed or modified.
     *
     * @param  label The label associated to the URN.
     * @param  linkage The URN.
     * @throws IllegalStateException If a URN is already associated to the given linkage.
     */
    public void addAnchor(final String label, final URI linkage) throws IllegalStateException {
        if (anchors == null) {
            anchors = new HashMap<>();
        }
        final Object old = anchors.put(label, linkage);
        if (old != null) {
            anchors.put(label, old);
            throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_1, label));
        }
    }
}
