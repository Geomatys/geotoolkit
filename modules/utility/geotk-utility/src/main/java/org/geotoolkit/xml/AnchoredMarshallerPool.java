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
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.resources.Errors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.xml.XML;
import org.apache.sis.xml.XLink;
import org.apache.sis.xml.MarshalContext;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.ReferenceResolver;
import org.apache.sis.internal.jaxb.TypeRegistration;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AnchoredMarshallerPool extends MarshallerPool {

    public AnchoredMarshallerPool() throws JAXBException {
        this(TypeRegistration.getSharedContext());
    }

    private final String schemaLocation;

    /**
     * Binds string labels with URNs or anchors.
     *
     * @see #addAnchor(String, URI)
     */
    private final Map<String,URI> anchors;

    /**
     * Creates a new factory for the given JAXB context, with a default empty namespace.
     *
     * @param  context       The JAXB context.
     * @throws JAXBException If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final JAXBContext context) throws JAXBException {
        this(null, context, null, new HashMap<String,URI>(), null);
    }

    public AnchoredMarshallerPool(final JAXBContext context, final Map<String, Object> properties) throws JAXBException {
        this(null, context, null, new HashMap<String,URI>(), properties);
    }

    /**
     * Creates a new factory for the given class to be bound, with a default empty namespace.
     *
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final Class<?>... classesToBeBound) throws JAXBException {
        this(null, classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final Class<?>... classesToBeBound) throws JAXBException {
        this(rootNamespace, null, classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String schemaLocation, final Class<?>... classesToBeBound) throws JAXBException {
        this(rootNamespace, JAXBContext.newInstance(classesToBeBound), schemaLocation, new HashMap<String,URI>(), null);
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
        this(null, packages);
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
        this(rootNamespace, packages, (String) null);
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
        this(rootNamespace, JAXBContext.newInstance(packages), schemaLocation, new HashMap<String,URI>(), null);
    }

    private AnchoredMarshallerPool(final String rootNamespace, final JAXBContext context, final String schemaLocation,
            final Map<String,URI> anchors, final Map<String, Object> properties) throws JAXBException
    {
        super(context, getProperties(rootNamespace, anchors, properties));
        this.schemaLocation = schemaLocation;
        this.anchors = anchors;
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

    /**
     * Return a Map of Marshaller properties.
     *
     * @param rootNamespace The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param anchors Map of anchors to be stored as the {@link #anchors} field.
     */
    private static Map<String, Object> getProperties(final String rootNamespace, final Map<String,URI> anchors, final Map<String, Object> previousProperties) {
        final Map<String, Object> properties;
        if (previousProperties != null) {
            properties = previousProperties;
        } else {
            properties = new HashMap<>();
        }

        if (rootNamespace != null) {
            properties.put(XML.DEFAULT_NAMESPACE, rootNamespace);
        }
        properties.put(XML.DEFAULT_NAMESPACE, rootNamespace);
        properties.put(XML.RESOLVER, new ReferenceResolver() {
            @Override
            public XLink anchor(final MarshalContext context, final Object value, final CharSequence text) {
                final URI linkage;
                synchronized (anchors) {
                    linkage = anchors.get(value);
                }
                if (linkage != null) {
                    final XLink xlink = new XLink();
                    xlink.setHRef(linkage);
                    return xlink;
                }
                return super.anchor(context, value, text);
            }
        });
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
        synchronized (anchors) {
            final URI old = anchors.put(label, linkage);
            if (old != null) {
                anchors.put(label, old);
                throw new IllegalStateException(Errors.format(Errors.Keys.ValueAlreadyDefined_1, label));
            }
        }
    }
}
