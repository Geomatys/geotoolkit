/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Designation of the platform used to acquire the dataset.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "citation",
    "identifier",
    "description",
    "sponsors",
    "instruments"
})
@XmlRootElement(name = "MI_Platform")
public class DefaultPlatform extends MetadataEntity implements Platform {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -6870357428019309409L;

    /**
     * Source where information about the platform is described.
     */
    private Citation citation;

    /**
     * Unique identification of the platform.
     */
    private Identifier identifier;

    /**
     * Narrative description of the platform supporting the instrument.
     */
    private InternationalString description;

    /**
     * Organization responsible for building, launch, or operation of the platform.
     */
    private Collection<ResponsibleParty> sponsors;

    /**
     * Instrument(s) mounted on a platform.
     */
    private Collection<Instrument> instruments;

    /**
     * Constructs an initially empty platform.
     */
    public DefaultPlatform() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultPlatform(final Platform source) {
        super(source);
    }

    /**
     * Returns the source where information about the platform is described. {@code null}
     * if unspecified.
     */
    @Override
    @XmlElement(name = "citation")
    public synchronized Citation getCitation() {
        return citation;
    }

    /**
     * Sets the source where information about the platform is described.
     *
     * @param newValue The new citation value.
     */
    public synchronized void setCitation(final Citation newValue) {
        checkWritePermission();
        citation = newValue;
    }

    /**
     * Returns the unique identification of the platform.
     */
    @Override
    @XmlElement(name = "identifier")
    public synchronized Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identification of the platform.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
    }

    /**
     * Gets the narrative description of the platform supporting the instrument.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the narrative description of the platform supporting the instrument.
     *
     * @param newValue The new description value.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the organization responsible for building, launch, or operation of the platform.
     */
    @Override
    @XmlElement(name = "sponsors")
    public synchronized Collection<ResponsibleParty> getSponsors() {
        return xmlOptional(sponsors = nonNullCollection(sponsors, ResponsibleParty.class));
    }

    /**
     * Sets the organization responsible for building, launch, or operation of the platform.
     *
     * @param newValues The new sponsors values;
     */
    public synchronized void setSponsors(final Collection<? extends ResponsibleParty> newValues) {
        sponsors = copyCollection(newValues, sponsors, ResponsibleParty.class);
    }

    /**
     * Gets the instrument(s) mounted on a platform.
     */
    @Override
    @XmlElement(name = "instruments")
    public synchronized Collection<Instrument> getInstruments() {
        return instruments = nonNullCollection(instruments, Instrument.class);
    }

    /**
     * Sets the instrument(s) mounted on a platform.
     *
     * @param newValues The new instruments values.
     */
    public synchronized void setInstruments(final Collection<? extends Instrument> newValues) {
        instruments = copyCollection(newValues, instruments, Instrument.class);
    }
}
