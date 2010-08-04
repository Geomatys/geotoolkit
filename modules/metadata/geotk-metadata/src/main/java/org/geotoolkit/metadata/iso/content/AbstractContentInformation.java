/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.metadata.iso.content;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.content.ContentInformation;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of the content of a dataset.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_ContentInformation")
@XmlSeeAlso({DefaultCoverageDescription.class, DefaultFeatureCatalogueDescription.class})
@XmlRootElement(name = "MD_ContentInformation")
public class AbstractContentInformation extends MetadataEntity implements ContentInformation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1609535650982322560L;

    /**
     * Constructs an initially empty content information.
     */
    public AbstractContentInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the value from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public AbstractContentInformation(final ContentInformation source) {
        super(source);
    }
}
