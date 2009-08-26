/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.quality.Usability;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Degree of adherence of a dataset to a specific set of user requirements.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlRootElement(name = "QE_Usability")
public class DefaultUsability extends AbstractElement implements Usability {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -2834763269479082042L;

    /**
     * Constructs an initially empty usability.
     */
    public DefaultUsability() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultUsability(final Usability source) {
        super(source);
    }
}
