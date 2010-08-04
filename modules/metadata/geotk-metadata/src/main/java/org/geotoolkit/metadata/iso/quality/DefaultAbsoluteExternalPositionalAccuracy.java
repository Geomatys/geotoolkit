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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.AbsoluteExternalPositionalAccuracy;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Closeness of reported coordinate values to values accepted as or being true.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.04
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlRootElement(name = "DQ_AbsoluteExternalPositionalAccuracy")
public class DefaultAbsoluteExternalPositionalAccuracy extends AbstractPositionalAccuracy
       implements AbsoluteExternalPositionalAccuracy
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4116627805950579738L;

    /**
     * Constructs an initially empty absolute external positional accuracy.
     */
    public DefaultAbsoluteExternalPositionalAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultAbsoluteExternalPositionalAccuracy(final AbsoluteExternalPositionalAccuracy source) {
        super(source);
    }

    /**
     * Creates an positional accuracy initialized to the given result.
     *
     * @param result The value obtained from applying a data quality measure against a specified
     *               acceptable conformance quality level.
     */
    public DefaultAbsoluteExternalPositionalAccuracy(final Result result) {
        super(result);
    }
}
