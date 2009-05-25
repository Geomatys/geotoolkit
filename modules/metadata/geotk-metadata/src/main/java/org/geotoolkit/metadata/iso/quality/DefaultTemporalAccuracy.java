/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.metadata.quality.TemporalAccuracy;


/**
 * Accuracy of the temporal attributes and temporal relationships of features.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class DefaultTemporalAccuracy extends AbstractElement implements TemporalAccuracy {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 4525353962603986621L;

    /**
     * Constructs an initially empty temporal accuracy.
     */
    public DefaultTemporalAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultTemporalAccuracy(final TemporalAccuracy source) {
        super(source);
    }
}
