/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.acquisition.Requirement;


/**
 * Designations for the measuring instruments, the platform carrying them, and the mission to
 * which the data contributes.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
@XmlType(propOrder = {
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
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     */
    public DefaultAcquisitionInformation(final AcquisitionInformation source) {
        super(source);
    }

    /**
     * Returns the plan as implemented by the acquisition.
     */
    @Override
    @XmlElement(name = "acquisitionPlan")
    public synchronized Collection<Plan> getAcquisitionPlans() {
        return xmlOptional(acquisitionPlans = nonNullCollection(acquisitionPlans, Plan.class));
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
        return xmlOptional(acquisitionRequirements = nonNullCollection(acquisitionRequirements, Requirement.class));
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
    public EnvironmentalRecord getEnvironmentalConditions() {
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
        return xmlOptional(instruments = nonNullCollection(instruments, Instrument.class));
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
        return xmlOptional(objectives = nonNullCollection(objectives, Objective.class));
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
        return xmlOptional(operations = nonNullCollection(operations, Operation.class));
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
        return xmlOptional(platforms = nonNullCollection(platforms, Platform.class));
    }

    /**
     * Sets the general information about the platform from which the data were taken.
     *
     * @param newValues The new platforms values.
     */
    public synchronized void setPlatforms(final Collection<? extends Platform> newValues) {
        platforms = copyCollection(newValues, platforms, Platform.class);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB when
     * the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
