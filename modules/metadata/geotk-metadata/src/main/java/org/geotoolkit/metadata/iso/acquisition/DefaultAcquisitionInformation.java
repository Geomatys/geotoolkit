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

import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.acquisition.Requirement;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Designations for the measuring instruments, the platform carrying them, and the mission to
 * which the data contributes.
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
@Deprecated
@ThreadSafe
@XmlType(name = "MI_AcquisitionInformation_Type", propOrder={
    "acquisitionPlans",
    "acquisitionRequirements",
    "environmentalConditions",
    "instruments",
    "objectives",
    "operations",
    "platforms"
})
@XmlRootElement(name = "MI_AcquisitionInformation")
public class DefaultAcquisitionInformation extends MetadataEntity implements AcquisitionInformation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1232071263806560246L;

    /**
     * Identifies the plan as implemented by the acquisition.
     */
    private Collection<Plan> acquisitionPlans;

    /**
     * Identifies the requirement the data acquisition intends to satisfy.
     */
    private Collection<Requirement> acquisitionRequirements;

    /**
     * A record of the environmental circumstances during the data acquisition.
     */
    private EnvironmentalRecord environmentalConditions;

    /**
     * General information about the instrument used in data acquisition.
     */
    private Collection<Instrument> instruments;

    /**
     * Identification of the area or object to be sensed.
     */
    private Collection<Objective> objectives;

    /**
     * General information about an identifiable activity which provided the data.
     */
    private Collection<Operation> operations;

    /**
     * General information about the platform from which the data were taken.
     */
    private Collection<Platform> platforms;

    /**
     * Constructs an initially empty acquisition information.
     */
    public DefaultAcquisitionInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultAcquisitionInformation(final AcquisitionInformation source) {
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
    public static DefaultAcquisitionInformation castOrCopy(final AcquisitionInformation object) {
        return (object == null) || (object instanceof DefaultAcquisitionInformation)
                ? (DefaultAcquisitionInformation) object : new DefaultAcquisitionInformation(object);
    }

    /**
     * Returns the plan as implemented by the acquisition.
     */
    @Override
    @XmlElement(name = "acquisitionPlan")
    public synchronized Collection<Plan> getAcquisitionPlans() {
        return acquisitionPlans = nonNullCollection(acquisitionPlans, Plan.class);
    }

    /**
     * Sets the plan as implemented by the acquisition.
     *
     * @param newValues The new plan values.
     */
    public synchronized void setAcquisitionPlans(final Collection<? extends Plan> newValues) {
        acquisitionPlans = copyCollection(newValues, acquisitionPlans, Plan.class);
    }

    /**
     * Returns the requirement the data acquisition intends to satisfy.
     */
    @Override
    @XmlElement(name = "acquisitionRequirement")
    public synchronized Collection<Requirement> getAcquisitionRequirements() {
        return acquisitionRequirements = nonNullCollection(acquisitionRequirements, Requirement.class);
    }

    /**
     * Sets the requirement the data acquisition intends to satisfy.
     *
     * @param newValues The new acquisition requirements values.
     */
    public synchronized void setAcquisitionRequirements(final Collection<? extends Requirement> newValues) {
        acquisitionRequirements = copyCollection(newValues, acquisitionRequirements, Requirement.class);
    }

    /**
     * Returns a record of the environmental circumstances during the data acquisition.
     * {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "environmentalConditions")
    public synchronized EnvironmentalRecord getEnvironmentalConditions() {
        return environmentalConditions;
    }

    /**
     * Sets the record of the environmental circumstances during the data acquisition.
     *
     * @param newValue The new environmental record value.
     */
    public synchronized void setEnvironmentalConditions(final EnvironmentalRecord newValue) {
        checkWritePermission();
        environmentalConditions = newValue;
    }

    /**
     * Returns the general information about the instrument used in data acquisition.
     */
    @Override
    @XmlElement(name = "instrument")
    public synchronized Collection<Instrument> getInstruments() {
        return instruments = nonNullCollection(instruments, Instrument.class);
    }

    /**
     * Sets the general information about the instrument used in data acquisition.
     *
     * @param newValues The new instruments values.
     */
    public synchronized void setInstruments(final Collection<? extends Instrument> newValues) {
        instruments = copyCollection(newValues, instruments, Instrument.class);
    }

    /**
     * Returns the area or object to be sensed.
     */
    @Override
    @XmlElement(name = "objective")
    public synchronized Collection<Objective> getObjectives() {
        return objectives = nonNullCollection(objectives, Objective.class);
    }

    /**
     * Sets the area or object to be sensed.
     *
     * @param newValues The new objectives values.
     */
    public synchronized void setObjectives(final Collection<? extends Objective> newValues) {
        objectives = copyCollection(newValues, objectives, Objective.class);
    }

    /**
     * Returns the general information about an identifiable activity which provided the data.
     */
    @Override
    @XmlElement(name = "operation")
    public synchronized Collection<Operation> getOperations() {
        return operations = nonNullCollection(operations, Operation.class);
    }

    /**
     * Sets the general information about an identifiable activity which provided the data.
     *
     * @param newValues The new operations values.
     */
    public synchronized void setOperations(final Collection<? extends Operation> newValues) {
        operations = copyCollection(newValues, operations, Operation.class);
    }

    /**
     * Returns the general information about the platform from which the data were taken.
     */
    @Override
    @XmlElement(name = "platform")
    public synchronized Collection<Platform> getPlatforms() {
        return platforms = nonNullCollection(platforms, Platform.class);
    }

    /**
     * Sets the general information about the platform from which the data were taken.
     *
     * @param newValues The new platforms values.
     */
    public synchronized void setPlatforms(final Collection<? extends Platform> newValues) {
        platforms = copyCollection(newValues, platforms, Platform.class);
    }
}
