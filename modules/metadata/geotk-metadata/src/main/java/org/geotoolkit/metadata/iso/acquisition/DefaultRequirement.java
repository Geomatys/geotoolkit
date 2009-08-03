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
import java.util.Date;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Plan;
import org.opengis.metadata.acquisition.Priority;
import org.opengis.metadata.acquisition.RequestedDate;
import org.opengis.metadata.acquisition.Requirement;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Requirement to be satisfied by the planned data acquisition.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "citation",
    "identifier",
    "requestors",
    "recipients",
    "priority",
    "requestedDate",
    "expiryDate",
    "satisifedPlans"
})
@XmlRootElement(name = "MI_Requirement")
public class DefaultRequirement extends MetadataEntity implements Requirement {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 7305276418007196949L;

    /**
     * Identification of reference or guidance material for the requirement.
     */
    private Citation citation;

    /**
     * Unique name, or code, for the requirement.
     */
    private Identifier identifier;

    /**
     * Origin of requirement.
     */
    private Collection<ResponsibleParty> requestors;

    /**
     * Person(s), or body(ies), to receive results of requirement.
     */
    private Collection<ResponsibleParty> recipients;

    /**
     * Relative ordered importance, or urgency, of the requirement.
     */
    private Priority priority;

    /**
     * Required or preferred acquisition date and time.
     */
    private RequestedDate requestedDate;

    /**
     * Date and time after which collection is no longer valid.
     */
    private Date expiryDate;

    /**
     * Plan that identifies solution to satisfy the requirement.
     */
    private Collection<Plan> satisifedPlans;

    /**
     * Constructs an initially empty requirement.
     */
    public DefaultRequirement() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultRequirement(final Requirement source) {
        super(source);
    }

    /**
     * Returns the identification of reference or guidance material for the requirement.
     * {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "citation")
    public Citation getCitation() {
        return citation;
    }

    /**
     * Sets the identification of reference or guidance material for the requirement.
     *
     * @param newValue The new citation value.
     */
    public synchronized void setCitation(final Citation newValue) {
        checkWritePermission();
        citation = newValue;
    }

    /**
     * Returns the unique name, or code, for the requirement.
     */
    @Override
    @XmlElement(name = "identifier")
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique name, or code, for the requirement.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
    }

    /**
     * Returns the origin of requirement.
     */
    @Override
    @XmlElement(name = "requestor")
    public synchronized Collection<ResponsibleParty> getRequestors() {
        return requestors = nonNullCollection(requestors, ResponsibleParty.class);
    }

    /**
     * Sets the origin of requirement.
     *
     * @param newValues The new requestors values.
     */
    public synchronized void setRequestors(final Collection<? extends ResponsibleParty> newValues) {
        requestors = copyCollection(newValues, requestors, ResponsibleParty.class);
    }

    /**
     * Returns the person(s), or body(ies), to receive results of requirement.
     */
    @Override
    @XmlElement(name = "recipient")
    public synchronized Collection<ResponsibleParty> getRecipients() {
        return recipients = nonNullCollection(recipients, ResponsibleParty.class);
    }

    /**
     * Sets the Person(s), or body(ies), to receive results of requirement.
     *
     * @param newValues The new recipients values.
     */
    public synchronized void setRecipients(final Collection<? extends ResponsibleParty> newValues) {
        recipients = copyCollection(newValues, recipients, ResponsibleParty.class);
    }

    /**
     * Returns the relative ordered importance, or urgency, of the requirement.
     */
    @Override
    @XmlElement(name = "priority")
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the relative ordered importance, or urgency, of the requirement.
     *
     * @param newValue The new priority value.
     */
    public synchronized void setPriority(final Priority newValue) {
        checkWritePermission();
        priority = newValue;
    }

    /**
     * Returns the required or preferred acquisition date and time.
     */
    @Override
    @XmlElement(name = "requestedDate")
    public RequestedDate getRequestedDate() {
        return requestedDate;
    }

    /**
     * Sets the required or preferred acquisition date and time.
     *
     * @param newValue The new requested date value.
     */
    public synchronized void setRequestedDate(final RequestedDate newValue) {
        checkWritePermission();
        requestedDate = newValue;
    }

    /**
     * Returns the date and time after which collection is no longer valid.
     */
    @Override
    @XmlElement(name = "expiryDate")
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the date and time after which collection is no longer valid.
     *
     * @param newValue The new expiry date.
     */
    public synchronized void setExpiryDate(final Date newValue) {
        checkWritePermission();
        expiryDate = newValue;
    }

    /**
     * Returns the plan that identifies solution to satisfy the requirement.
     */
    @Override
    @XmlElement(name = "satisifedPlan")
    public synchronized Collection<Plan> getSatisifedPlans() {
        return xmlOptional(satisifedPlans = nonNullCollection(satisifedPlans, Plan.class));
    }

    /**
     * Sets the plan that identifies solution to satisfy the requirement.
     *
     * @param newValues The new satisfied plans values.
     */
    public synchronized void setSatisifedPlans(final Collection<? extends Plan> newValues) {
        satisifedPlans = copyCollection(newValues, satisifedPlans, Plan.class);
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
