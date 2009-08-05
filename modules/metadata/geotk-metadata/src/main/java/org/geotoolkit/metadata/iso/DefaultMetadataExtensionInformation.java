/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso;

import java.util.Collection;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.citation.OnLineResource;
import org.opengis.metadata.ExtendedElementInformation;
import org.opengis.metadata.MetadataExtensionInformation;


/**
 * Information describing metadata extensions.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "extensionOnLineResource",
    "extendedElementInformation"
})
@XmlRootElement(name = "MD_MetadataExtensionInformation")
public class DefaultMetadataExtensionInformation extends MetadataEntity
        implements MetadataExtensionInformation
{
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 573866936088674519L;

    /**
     * Information about on-line sources containing the community profile name and
     * the extended metadata elements. Information for all new metadata elements.
     */
    private OnLineResource extensionOnLineResource;

    /**
     * Provides information about a new metadata element, not found in ISO 19115, which is
     * required to describe geographic data.
     */
    private Collection<ExtendedElementInformation> extendedElementInformation;

    /**
     * Construct an initially empty metadata extension information.
     */
    public DefaultMetadataExtensionInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultMetadataExtensionInformation(final MetadataExtensionInformation source) {
        super(source);
    }

    /**
     * Information about on-line sources containing the community profile name and
     * the extended metadata elements. Information for all new metadata elements.
     */
    @Override
    @XmlElement(name = "extensionOnLineResource")
    public synchronized OnLineResource getExtensionOnLineResource() {
        return extensionOnLineResource;
    }

    /**
     * Sets information about on-line sources.
     *
     * @param newValue The new extension online resource.
     */
    public synchronized void setExtensionOnLineResource(final OnLineResource newValue) {
        checkWritePermission();
        this.extensionOnLineResource = newValue;
    }

    /**
     * Provides information about a new metadata element, not found in ISO 19115, which is
     * required to describe geographic data.
     */
    @Override
    @XmlElement(name = "extendedElementInformation")
    public synchronized Collection<ExtendedElementInformation> getExtendedElementInformation() {
        return xmlOptional(extendedElementInformation = nonNullCollection(extendedElementInformation,
                ExtendedElementInformation.class));
    }

    /**
     * Sets information about a new metadata element.
     *
     * @param newValues The new extented element information.
     */
    public synchronized void setExtendedElementInformation(
            final Collection<? extends ExtendedElementInformation> newValues) {
        extendedElementInformation = copyCollection(newValues, extendedElementInformation,
                                                    ExtendedElementInformation.class);
    }
}
