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
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.quality.AccuracyOfATimeMeasurement;


/**
 * Correctness of the temporal references of an item (reporting of error in time measurement).
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.04
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "DQ_AccuracyOfATimeMeasurement_Type")
@XmlRootElement(name = "DQ_AccuracyOfATimeMeasurement")
public class DefaultAccuracyOfATimeMeasurement extends AbstractTemporalAccuracy
        implements AccuracyOfATimeMeasurement
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7934234071852119486L;

    /**
     * Constructs an initially empty accuracy of a time measurement.
     */
    public DefaultAccuracyOfATimeMeasurement() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultAccuracyOfATimeMeasurement(final AccuracyOfATimeMeasurement source) {
        super(source);
    }
}
