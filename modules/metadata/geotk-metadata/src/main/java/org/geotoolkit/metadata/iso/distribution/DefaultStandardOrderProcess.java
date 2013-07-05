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
package org.geotoolkit.metadata.iso.distribution;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.distribution.StandardOrderProcess;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Common ways in which the resource may be obtained or received, and related instructions
 * and fee information.
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
@XmlType(name = "MD_StandardOrderProcess_Type", propOrder={
    "fees",
    "plannedAvailableDateTime",
    "orderingInstructions",
    "turnaround"
})
@XmlRootElement(name = "MD_StandardOrderProcess")
public class DefaultStandardOrderProcess extends MetadataEntity implements StandardOrderProcess {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -6503378937452728631L;

    /**
     * Fees and terms for retrieving the resource.
     * Include monetary units (as specified in ISO 4217).
     */
    private InternationalString fees;

    /**
     * Date and time when the dataset will be available,
     * in milliseconds elapsed since January 1st, 1970.
     */
    private long plannedAvailableDateTime;

    /**
     * General instructions, terms and services provided by the distributor.
     */
    private InternationalString orderingInstructions;

    /**
     * Typical turnaround time for the filling of an order.
     */
    private InternationalString turnaround;

    /**
     * Constructs an initially empty standard order process.
     */
    public DefaultStandardOrderProcess() {
        plannedAvailableDateTime = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultStandardOrderProcess(final StandardOrderProcess source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (plannedAvailableDateTime == 0 && source.getPlannedAvailableDateTime() == null) {
                plannedAvailableDateTime = Long.MIN_VALUE;
            }
        }
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
    public static DefaultStandardOrderProcess castOrCopy(final StandardOrderProcess object) {
        return (object == null) || (object instanceof DefaultStandardOrderProcess)
                ? (DefaultStandardOrderProcess) object : new DefaultStandardOrderProcess(object);
    }

    /**
     * Returns fees and terms for retrieving the resource.
     * Include monetary units (as specified in ISO 4217).
     */
    @Override
    @XmlElement(name = "fees")
    public synchronized InternationalString getFees() {
        return fees;
    }

    /**
     * Sets fees and terms for retrieving the resource.
     * Include monetary units (as specified in ISO 4217).
     *
     * @param newValue The new fees.
     */
    public synchronized void setFees(final InternationalString newValue) {
        checkWritePermission();
        fees = newValue;
    }

    /**
     * Returns the date and time when the dataset will be available.
     */
    @Override
    @XmlElement(name = "plannedAvailableDateTime")
    public synchronized Date getPlannedAvailableDateTime() {
        return (plannedAvailableDateTime != Long.MIN_VALUE) ?
                new Date(plannedAvailableDateTime) : null;
    }

    /**
     * Sets the date and time when the dataset will be available.
     *
     * @param newValue The new planned available time.
     */
    public synchronized void setPlannedAvailableDateTime(final Date newValue) {
        checkWritePermission();
        plannedAvailableDateTime = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns general instructions, terms and services provided by the distributor.
     */
    @Override
    @XmlElement(name = "orderingInstructions")
    public synchronized InternationalString getOrderingInstructions() {
        return orderingInstructions;
    }

    /**
     * Sets general instructions, terms and services provided by the distributor.
     *
     * @param newValue The new ordering instructions.
     */
    public synchronized void setOrderingInstructions(final InternationalString newValue) {
        checkWritePermission();
        orderingInstructions = newValue;
    }

    /**
     * Returns typical turnaround time for the filling of an order.
     */
    @Override
    @XmlElement(name = "turnaround")
    public synchronized InternationalString getTurnaround() {
        return turnaround;
    }

    /**
     * Sets typical turnaround time for the filling of an order.
     *
     * @param newValue The new turnaround.
     */
    public synchronized void setTurnaround(final InternationalString newValue) {
        checkWritePermission();
        turnaround = newValue;
    }
}
