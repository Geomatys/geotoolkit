/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.acquisition;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.acquisition.GeometryType;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Requirement;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Progress;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Designations for the planning information related to meeting the data acquisition requirements.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MI_Plan_Type", propOrder={
    "type",
    "status",
    "citation",
    "operations",
    "satisfiedRequirements"
})
@XmlRootElement(name = "MI_Plan")
public class DefaultPlan extends MetadataEntity implements Plan {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8457900515677160271L;

    /**
     * Manner of sampling geometry that the planner expects for collection of objective data.
     */
    private GeometryType type;

    /**
     * Current status of the plan (pending, completed, etc.)
     */
    private Progress status;

    /**
     * Identification of authority requesting target collection.
     */
    private Citation citation;

    /**
     * Identification of the activity or activities that satisfy a plan.
     */
    private Collection<Operation> operations;

    /**
     * Requirement satisfied by the plan.
     */
    private Collection<Requirement> satisfiedRequirements;

    /**
     * Constructs an initially empty plan.
     */
    public DefaultPlan() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultPlan(final Plan source) {
        super(source);
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
    public static DefaultPlan castOrCopy(final Plan object) {
        return (object == null) || (object instanceof DefaultPlan)
                ? (DefaultPlan) object : new DefaultPlan(object);
    }

    /**
     * Returns the manner of sampling geometry that the planner expects for collection of
     * objective data. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "type")
    public synchronized GeometryType getType() {
        return type;
    }

    /**
     * Sets the manner of sampling geometry that the planner expects for collection of
     * objective data.
     *
     * @param newValue The new type value.
     */
    public synchronized void setType(final GeometryType newValue) {
        checkWritePermission();
        type = newValue;
    }

    /**
     * Returns the current status of the plan (pending, completed, etc.)
     */
    @Override
    @XmlElement(name = "status", required = true)
    public synchronized Progress getStatus() {
        return status;
    }

    /**
     * Sets the current status of the plan (pending, completed, etc.)
     *
     * @param newValue The new status value.
     */
    public synchronized void setStatus(final Progress newValue) {
        checkWritePermission();
        status = newValue;
    }

    /**
     * Returns the identification of authority requesting target collection.
     */
    @Override
    @XmlElement(name = "citation", required = true)
    public synchronized Citation getCitation() {
        return citation;
    }

    /**
     * Sets the identification of authority requesting target collection.
     *
     * @param newValue The new citation value.
     */
    public synchronized void setCitation(final Citation newValue) {
        checkWritePermission();
        citation = newValue;
    }

    /**
     * Returns the identification of the activity or activities that satisfy a plan.
     */
    @Override
    @XmlElement(name = "operation")
    public synchronized Collection<Operation> getOperations() {
        return operations = nonNullCollection(operations, Operation.class);
    }

    /**
     * Sets the identification of the activity or activities that satisfy a plan.
     *
     * @param newValues The new identifications of the activity.
     */
    public synchronized void setOperations(final Collection<? extends Operation> newValues) {
        operations = copyCollection(newValues, operations, Operation.class);
    }

    /**
     * Returns the requirement satisfied by the plan.
     */
    @Override
    @XmlElement(name = "satisfiedRequirement")
    public synchronized Collection<Requirement> getSatisfiedRequirements() {
        return satisfiedRequirements = nonNullCollection(satisfiedRequirements, Requirement.class);
    }

    /**
     * Sets the requirement satisfied by the plan.
     *
     * @param newValues The new satisfied requirements.
     */
    public synchronized void setSatisfiedRequirements(final Collection<? extends Requirement> newValues) {
        satisfiedRequirements = copyCollection(newValues, satisfiedRequirements, Requirement.class);
    }
}
