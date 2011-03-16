/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.metadata.quality.NonQuantitativeAttributeAccuracy;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Accuracy of non-quantitative attributes.
 *
 * @author Cory Horner (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.04
 *
 * @since 2.4
 * @module
 */
@ThreadSafe
@XmlType(name = "DQ_NonQuantitativeAttributeAccuracy_Type")
@XmlRootElement(name = "DQ_NonQuantitativeAttributeAccuracy")
public class DefaultNonQuantitativeAttributeAccuracy extends AbstractThematicAccuracy
        implements NonQuantitativeAttributeAccuracy
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 2659617465862554345L;

    /**
     * Constructs an initially empty non quantitative attribute correctness.
     */
    public DefaultNonQuantitativeAttributeAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultNonQuantitativeAttributeAccuracy(final NonQuantitativeAttributeAccuracy source) {
        super(source);
    }
}
