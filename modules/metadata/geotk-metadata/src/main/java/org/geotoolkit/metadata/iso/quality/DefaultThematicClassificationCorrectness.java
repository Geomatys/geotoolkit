/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.quality.ThematicClassificationCorrectness;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Comparison of the classes assigned to features or their attributes to a universe of discourse.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.04
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "DQ_ThematicClassificationCorrectness_Type")
@XmlRootElement(name = "DQ_ThematicClassificationCorrectness")
public class DefaultThematicClassificationCorrectness extends AbstractThematicAccuracy
        implements ThematicClassificationCorrectness
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5484398738783800915L;

    /**
     * Constructs an initially empty thematic classification correctness.
     */
    public DefaultThematicClassificationCorrectness() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultThematicClassificationCorrectness(final ThematicClassificationCorrectness source) {
        super(source);
    }
}
