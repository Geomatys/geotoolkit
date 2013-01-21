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
package org.geotoolkit.metadata.iso.maintenance;

import java.util.Date;
import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.opengis.metadata.maintenance.MaintenanceFrequency;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.maintenance.ScopeDescription;
import org.opengis.temporal.PeriodDuration;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the scope and frequency of updating.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.21
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_MaintenanceInformation_Type", propOrder={
    "maintenanceAndUpdateFrequency", "dateOfNextUpdate", "userDefinedMaintenanceFrequency",
    "updateScopes", "updateScopeDescriptions", "maintenanceNotes", "contacts"
})
@XmlRootElement(name = "MD_MaintenanceInformation")
public class DefaultMaintenanceInformation extends MetadataEntity implements MaintenanceInformation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8523463344581266776L;

    /**
     * Frequency with which changes and additions are made to the resource after the
     * initial resource is completed.
     */
    private MaintenanceFrequency maintenanceAndUpdateFrequency;

    /**
     * Scheduled revision date for resource, in milliseconds elapsed
     * since January 1st, 1970. If there is no such date, then this field
     * is set to the special value {@link Long#MIN_VALUE}.
     */
    private long dateOfNextUpdate;

    /**
     * Maintenance period other than those defined, in milliseconds.
     */
    private PeriodDuration userDefinedMaintenanceFrequency;

    /**
     * Scope of data to which maintenance is applied.
     */
    private Collection<ScopeCode> updateScopes;

    /**
     * Additional information about the range or extent of the resource.
     */
    private Collection<ScopeDescription> updateScopeDescriptions;

    /**
     * Information regarding specific requirements for maintaining the resource.
     */
    private Collection<InternationalString> maintenanceNotes;

    /**
     * Identification of, and means of communicating with,
     * person(s) and organization(s) with responsibility for maintaining the metadata
     */
    private Collection<ResponsibleParty> contacts;

    /**
     * Creates a an initially empty maintenance information.
     */
    public DefaultMaintenanceInformation() {
        dateOfNextUpdate = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultMaintenanceInformation(final MaintenanceInformation source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (dateOfNextUpdate == 0 && source.getDateOfNextUpdate() == null) {
                dateOfNextUpdate = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Creates a maintenance information.
     *
     * @param maintenanceAndUpdateFrequency The frequency with which changes and additions
     *          are made to the resource after the initial resource is completed.
     */
    public DefaultMaintenanceInformation(final MaintenanceFrequency maintenanceAndUpdateFrequency) {
        this(); // Initialize the date field.
        setMaintenanceAndUpdateFrequency(maintenanceAndUpdateFrequency);
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
    public static DefaultMaintenanceInformation castOrCopy(final MaintenanceInformation object) {
        return (object == null) || (object instanceof DefaultMaintenanceInformation)
                ? (DefaultMaintenanceInformation) object : new DefaultMaintenanceInformation(object);
    }

    /**
     * Returns the frequency with which changes and additions are made to the resource
     * after the initial resource is completed.
     */
    @Override
    @XmlElement(name = "maintenanceAndUpdateFrequency", required = true)
    public synchronized MaintenanceFrequency getMaintenanceAndUpdateFrequency() {
        return maintenanceAndUpdateFrequency;
    }

    /**
     * Sets the frequency with which changes and additions are made to the resource
     * after the initial resource is completed.
     *
     * @param newValue The new maintenance frequency.
     */
    public synchronized void setMaintenanceAndUpdateFrequency(final MaintenanceFrequency newValue) {
        checkWritePermission();
        maintenanceAndUpdateFrequency = newValue;
    }

    /**
     * Returns the scheduled revision date for resource.
     */
    @Override
    @XmlElement(name = "dateOfNextUpdate")
    public synchronized Date getDateOfNextUpdate() {
        final long date = dateOfNextUpdate;
        return (date != Long.MIN_VALUE) ? new Date(date) : null;
    }

    /**
     * Sets the scheduled revision date for resource.
     *
     * @param newValue The new date of next update.
     */
    public synchronized void setDateOfNextUpdate(final Date newValue) {
        checkWritePermission();
        dateOfNextUpdate = (newValue!=null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the maintenance period other than those defined.
     *
     * @todo needs an implementation of org.opengis.temporal modules to anntote this parameter.
     */
    @Override
    @XmlElement(name = "userDefinedMaintenanceFrequency")
    public synchronized PeriodDuration getUserDefinedMaintenanceFrequency() {
        return userDefinedMaintenanceFrequency;
    }

    /**
     * Sets the maintenance period other than those defined.
     *
     * @param newValue The new user defined maintenance frequency.
     */
    public synchronized void setUserDefinedMaintenanceFrequency(final PeriodDuration newValue) {
        checkWritePermission();
        userDefinedMaintenanceFrequency = newValue;
    }

    /**
     * Returns the scope of data to which maintenance is applied.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "updateScope")
    public synchronized Collection<ScopeCode> getUpdateScopes() {
        return updateScopes = nonNullCollection(updateScopes, ScopeCode.class);
    }

    /**
     * Sets the scope of data to which maintenance is applied.
     *
     * @param newValues The new update scopes.
     *
     * @since 2.4
     */
    public synchronized void setUpdateScopes(final Collection<? extends ScopeCode> newValues) {
        updateScopes = copyCollection(newValues, updateScopes, ScopeCode.class);
    }

    /**
     * Returns additional information about the range or extent of the resource.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "updateScopeDescription")
    public synchronized Collection<ScopeDescription> getUpdateScopeDescriptions() {
        return updateScopeDescriptions = nonNullCollection(updateScopeDescriptions, ScopeDescription.class);
    }

    /**
     * Sets additional information about the range or extent of the resource.
     *
     * @param newValues The new update scope descriptions.
     *
     * @since 2.4
     */
    public synchronized void setUpdateScopeDescriptions(final Collection<? extends ScopeDescription> newValues) {
        updateScopeDescriptions = copyCollection(newValues, updateScopeDescriptions, ScopeDescription.class);
    }

    /**
     * Returns information regarding specific requirements for maintaining the resource.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "maintenanceNote")
    public synchronized Collection<InternationalString> getMaintenanceNotes() {
        return maintenanceNotes = nonNullCollection(maintenanceNotes, InternationalString.class);
    }

    /**
     * Sets information regarding specific requirements for maintaining the resource.
     *
     * @param newValues The new maintenance notes.
     *
     * @since 2.4
     */
    public synchronized void setMaintenanceNotes(final Collection<? extends InternationalString> newValues) {
        maintenanceNotes = copyCollection(newValues, maintenanceNotes, InternationalString.class);
    }

    /**
     * Returns identification of, and means of communicating with,
     * person(s) and organization(s) with responsibility for maintaining the metadata.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "contact")
    public synchronized Collection<ResponsibleParty> getContacts() {
        return contacts = nonNullCollection(contacts, ResponsibleParty.class);
    }

    /**
     * Sets identification of, and means of communicating with,
     * person(s) and organization(s) with responsibility for maintaining the metadata.
     *
     * @param newValues The new contacts
     *
     * @since 2.4
     */
    public synchronized void setContacts(final Collection<? extends ResponsibleParty> newValues) {
        contacts = copyCollection(newValues, contacts, ResponsibleParty.class);
    }
}
