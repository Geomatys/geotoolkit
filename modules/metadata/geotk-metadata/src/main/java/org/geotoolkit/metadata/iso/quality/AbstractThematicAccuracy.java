/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.metadata.iso.quality;

import org.opengis.metadata.quality.ThematicAccuracy;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Accuracy of quantitative attributes and the correctness of non-quantitative attributes
 * and of the classifications of features and their relationships.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
public class AbstractThematicAccuracy extends AbstractElement implements ThematicAccuracy {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -781072634778767137L;

    /**
     * Constructs an initially empty thematic accuracy.
     */
    public AbstractThematicAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public AbstractThematicAccuracy(final ThematicAccuracy source) {
        super(source);
    }
}
