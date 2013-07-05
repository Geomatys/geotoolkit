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

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.acquisition.OperationType;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Progress;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Designations for the operation used to acquire the dataset.
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
@XmlType(name = "MI_Operation_Type", propOrder={
    "description",
    "citation",
    "identifier",
    "status",
    "type",
    "childOperations",
    "objectives",
    "parentOperation",
    "plan",
    "platforms",
    "significantEvents"
})
@XmlRootElement(name = "MI_Operation")
public class DefaultOperation extends MetadataEntity implements Operation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4247450339144267883L;

    /**
     * Description of the mission on which the platform observations are made and the
     * objectives of that mission.
     */
    private InternationalString description;

    /**
     * Identification of the mission.
     */
    private Citation citation;

    /**
     * Status of the data acquisition.
     */
    private Progress status;

    /**
     * Collection technique for the operation.
     */
    private OperationType type;

    /**
     * Sub-missions that make up part of a larger mission.
     */
    private Collection<Operation> childOperations;

    /**
     * Object(s) or area(s) of interest to be sensed.
     */
    private Collection<Objective> objectives;

    /**
     * Heritage of the operation.
     */
    private Operation parentOperation;

    /**
     * Plan satisfied by the operation.
     */
    private Plan plan;

    /**
     * Platform (or platforms) used in the operation.
     */
    private Collection<Platform> platforms;

    /**
     * Record of an event occurring during an operation.
     */
    private Collection<Event> significantEvents;

    /**
     * Constructs an initially empty operation.
     */
    public DefaultOperation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultOperation(final Operation source) {
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
    public static DefaultOperation castOrCopy(final Operation object) {
        return (object == null) || (object instanceof DefaultOperation)
                ? (DefaultOperation) object : new DefaultOperation(object);
    }

    /**
     * Returns the description of the mission on which the platform observations are made and the
     * objectives of that mission. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the description of the mission on which the platform observations are made and the
     * objectives of that mission.
     *
     * @param newValue The new description value.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the identification of the mission. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "citation")
    public synchronized Citation getCitation() {
        return citation;
    }

    /**
     * Sets the identification of the mission.
     *
     * @param newValue The new citation value.
     */
    public synchronized void setCitation(final Citation newValue) {
        checkWritePermission();
        citation = newValue;
    }

    /**
     * Returns the unique identification of the operation.
     */
    @Override
    @XmlElement(name = "identifier", required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the unique identification of the operation.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Returns the status of the data acquisition.
     */
    @Override
    @XmlElement(name = "status", required = true)
    public synchronized Progress getStatus() {
        return status;
    }

    /**
     * Sets the status of the data acquisition.
     *
     * @param newValue The new status value.
     */
    public synchronized void setStatus(final Progress newValue) {
        checkWritePermission();
        status = newValue;
    }

    /**
     * Returns the collection technique for the operation.
     */
    @Override
    @XmlElement(name = "type")
    public synchronized OperationType getType() {
        return type;
    }

    /**
     * Sets the collection technique for the operation.
     *
     * @param newValue The new type value.
     */
    public synchronized void setType(final OperationType newValue) {
        checkWritePermission();
        type = newValue;
    }

    /**
     * Returns the sub-missions that make up part of a larger mission.
     */
    @Override
    @XmlElement(name = "childOperation")
    public synchronized Collection<Operation> getChildOperations() {
        return childOperations = nonNullCollection(childOperations, Operation.class);
    }

    /**
     * Sets the sub-missions that make up part of a larger mission.
     *
     * @param newValues The new child operations values.
     */
    public synchronized void setChildOperations(final Collection<? extends Operation> newValues) {
        childOperations = copyCollection(newValues, childOperations, Operation.class);
    }

    /**
     * Returns object(s) or area(s) of interest to be sensed.
     */
    @Override
    @XmlElement(name = "objective")
    public synchronized Collection<Objective> getObjectives() {
        return objectives = nonNullCollection(objectives, Objective.class);
    }

    /**
     * Sets Object(s) or area(s) of interest to be sensed.
     *
     * @param newValues The new objectives values.
     */
    public synchronized void setObjectives(final Collection<? extends Objective> newValues) {
        objectives = copyCollection(newValues, objectives, Objective.class);
    }

    /**
     * Returns the heritage of the operation.
     */
    @Override
    @XmlElement(name = "parentOperation", required = true)
    public synchronized Operation getParentOperation() {
        return parentOperation;
    }

    /**
     * Sets the heritage of the operation.
     *
     * @param newValue The new parent operation value.
     */
    public synchronized void setParentOperation(final Operation newValue) {
        checkWritePermission();
        parentOperation = newValue;
    }

    /**
     * Returns the plan satisfied by the operation.
     */
    @Override
    @XmlElement(name = "plan")
    public synchronized Plan getPlan() {
        return plan;
    }

    /**
     * Sets the plan satisfied by the operation.
     *
     * @param newValue The new plan value.
     */
    public synchronized void setPlan(final Plan newValue) {
        checkWritePermission();
        plan = newValue;
    }

    /**
     * Returns the platform (or platforms) used in the operation.
     */
    @Override
    @XmlElement(name = "platform")
    public synchronized Collection<Platform> getPlatforms() {
        return platforms = nonNullCollection(platforms, Platform.class);
    }

    /**
     * Sets the platform (or platforms) used in the operation.
     *
     * @param newValues The new platforms values.
     */
    public synchronized void setPlatforms(final Collection<? extends Platform> newValues) {
        platforms = copyCollection(newValues, platforms, Platform.class);
    }

    /**
     * Returns the record of an event occurring during an operation.
     */
    @Override
    @XmlElement(name = "significantEvent")
    public synchronized Collection<Event> getSignificantEvents() {
        return significantEvents = nonNullCollection(significantEvents, Event.class);
    }

    /**
     * Sets the record of an event occurring during an operation.
     *
     * @param newValues The new significant events value.
     */
    public synchronized void setSignificantEvents(final Collection<? extends Event> newValues) {
        significantEvents = copyCollection(newValues, significantEvents, Event.class);
    }
}
