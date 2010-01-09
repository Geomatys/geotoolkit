/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.acquisition;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.acquisition.GeometryType;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Requirement;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Progress;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Designations for the planning information related to meeting the data acquisition requirements.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "type",
    "status",
    "citation",
    "operations",
    "satisfiedRequirements"
})
@XmlRootElement(name = "MI_Plan")
public class DefaultPlan extends MetadataEntity implements Plan {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     */
    public DefaultPlan(final Plan source) {
        super(source);
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
    @XmlElement(name = "status")
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
    @XmlElement(name = "citation")
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
        return xmlOptional(operations = nonNullCollection(operations, Operation.class));
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
        return xmlOptional(satisfiedRequirements = nonNullCollection(satisfiedRequirements, Requirement.class));
    }

    /**
     * Sets the requirement satisfied by the plan.
     *
     * @param newValues The new statisfied requirements.
     */
    public synchronized void setSatisfiedRequirements(final Collection<? extends Requirement> newValues) {
        satisfiedRequirements = copyCollection(newValues, satisfiedRequirements, Requirement.class);
    }
}
