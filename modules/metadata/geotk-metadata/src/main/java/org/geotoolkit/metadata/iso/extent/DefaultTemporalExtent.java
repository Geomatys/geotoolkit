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
package org.geotoolkit.metadata.iso.extent;

import java.util.Date;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.temporal.TemporalPrimitive;
import org.opengis.metadata.extent.TemporalExtent;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Boundary enclosing the dataset, expressed as the closed set of
 * (<var>x</var>,<var>y</var>) coordinates of the polygon. The last
 * point replicates first point.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "EX_TemporalExtent")
@XmlSeeAlso({DefaultSpatialTemporalExtent.class})
@XmlRootElement(name = "EX_TemporalExtent")
public class DefaultTemporalExtent extends MetadataEntity implements TemporalExtent {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3668140516657118045L;

    /**
     * The start date and time for the content of the dataset,
     * in milliseconds elapsed since January 1st, 1970. A value
     * of {@link Long#MIN_VALUE} means that this attribute is not set.
     */
    private long startTime = Long.MIN_VALUE;

    /**
     * The end date and time for the content of the dataset,
     * in milliseconds elapsed since January 1st, 1970. A value
     * of {@link Long#MIN_VALUE} means that this attribute is not set.
     */
    private long endTime = Long.MIN_VALUE;

    /**
     * The date and time for the content of the dataset.
     */
    private TemporalPrimitive extent;

    /**
     * Constructs an initially empty temporal extent.
     */
    public DefaultTemporalExtent() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultTemporalExtent(final TemporalExtent source) {
        super(source);
    }

    /**
     * Creates a temporal extent initialized to the specified values.
     *
     * @param startTime The start date and time for the content of the dataset.
     * @param endTime   The end date and time for the content of the dataset.
     */
    public DefaultTemporalExtent(final Date startTime, final Date endTime) {
        setStartTime(startTime);
        setEndTime  (endTime);
    }

    /**
     * The start date and time for the content of the dataset.
     *
     * @return The start time, or {@code null} if none.
     */
    public synchronized Date getStartTime() {
        final long time = startTime;
        return (time != Long.MIN_VALUE) ? new Date(time) : null;
    }

    /**
     * Sets the start date and time for the content of the dataset.
     *
     * @param newValue The new start time.
     */
    public synchronized void setStartTime(final Date newValue) {
        checkWritePermission();
        startTime = (newValue!=null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the end date and time for the content of the dataset.
     *
     * @return The end time, or {@code null} if none.
     */
    public synchronized Date getEndTime() {
        final long time = endTime;
        return (time != Long.MIN_VALUE) ? new Date(time) : null;
    }

    /**
     * Sets the end date and time for the content of the dataset.
     *
     * @param newValue The new end time.
     */
    public synchronized void setEndTime(final Date newValue) {
        checkWritePermission();
        endTime = (newValue!=null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the date and time for the content of the dataset.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "extent", required = true)
    public synchronized TemporalPrimitive getExtent() {
        return extent;
    }

    /**
     * Sets the date and time for the content of the dataset.
     *
     * @param newValue The new extent.
     *
     * @since 2.4
     */
    public synchronized void setExtent(final TemporalPrimitive newValue) {
        checkWritePermission();
        extent = newValue;
    }
}
