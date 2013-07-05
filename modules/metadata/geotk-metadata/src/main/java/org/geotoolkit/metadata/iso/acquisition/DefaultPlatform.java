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
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Designation of the platform used to acquire the dataset.
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
@XmlType(name = "MI_Platform_Type", propOrder={
    "citation",
    "identifier",
    "description",
    "sponsors",
    "instruments"
})
@XmlRootElement(name = "MI_Platform")
public class DefaultPlatform extends MetadataEntity implements Platform {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -6870357428019309408L;

    /**
     * Source where information about the platform is described.
     */
    private Citation citation;

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
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultPlatform(final Platform source) {
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
    public static DefaultPlatform castOrCopy(final Platform object) {
        return (object == null) || (object instanceof DefaultPlatform)
                ? (DefaultPlatform) object : new DefaultPlatform(object);
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
    @XmlElement(name = "identifier", required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the unique identification of the platform.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Gets the narrative description of the platform supporting the instrument.
     */
    @Override
    @XmlElement(name = "description", required = true)
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
    @XmlElement(name = "sponsor")
    public synchronized Collection<ResponsibleParty> getSponsors() {
        return sponsors = nonNullCollection(sponsors, ResponsibleParty.class);
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
    @XmlElement(name = "instrument", required = true)
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
