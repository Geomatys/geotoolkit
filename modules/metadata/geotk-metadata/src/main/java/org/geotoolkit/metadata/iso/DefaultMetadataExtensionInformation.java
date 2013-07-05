/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.ExtendedElementInformation;
import org.opengis.metadata.MetadataExtensionInformation;


/**
 * Information describing metadata extensions.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_MetadataExtensionInformation_Type", propOrder={
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
    private OnlineResource extensionOnLineResource;

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
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultMetadataExtensionInformation(final MetadataExtensionInformation source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultMetadataExtensionInformation castOrCopy(final MetadataExtensionInformation object) {
        return (object == null) || (object instanceof DefaultMetadataExtensionInformation)
                ? (DefaultMetadataExtensionInformation) object
                : new DefaultMetadataExtensionInformation(object);
    }

    /**
     * Information about on-line sources containing the community profile name and
     * the extended metadata elements. Information for all new metadata elements.
     */
    @Override
    @XmlElement(name = "extensionOnLineResource")
    public synchronized OnlineResource getExtensionOnLineResource() {
        return extensionOnLineResource;
    }

    /**
     * Sets information about on-line sources.
     *
     * @param newValue The new extension online resource.
     */
    public synchronized void setExtensionOnLineResource(final OnlineResource newValue) {
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
        return extendedElementInformation = nonNullCollection(extendedElementInformation, ExtendedElementInformation.class);
    }

    /**
     * Sets information about a new metadata element.
     *
     * @param newValues The new extended element information.
     */
    public synchronized void setExtendedElementInformation(final Collection<? extends ExtendedElementInformation> newValues) {
        extendedElementInformation = copyCollection(newValues, extendedElementInformation, ExtendedElementInformation.class);
    }
}
