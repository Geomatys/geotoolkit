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
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Designations for the measuring instruments.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "citations",
    "identifier",
    "type",
    "description",
    "mountedOn"
})
@XmlRootElement(name = "MI_Instrument")
public class DefaultInstrument extends MetadataEntity implements Instrument {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 6356044176200794577L;

    /**
     * Complete citation of the instrument.
     */
    private Collection<Citation> citations;

    /**
     * Unique identification of the instrument.
     */
    private Identifier identifier;

    /**
     * Name of the type of instrument. Examples: framing, line-scan, push-broom, pan-frame.
     */
    private InternationalString type;

    /**
     * Textual description of the instrument.
     */
    private InternationalString description;

    /**
     * Platform on which the instrument is mounted.
     */
    private Platform mountedOn;

    /**
     * Constructs an initially empty instrument.
     */
    public DefaultInstrument() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultInstrument(final Instrument source) {
        super(source);
    }

    /**
     * Returns the complete citation of the instrument.
     */
    @Override
    @XmlElement(name = "citation")
    public synchronized Collection<Citation> getCitations() {
        return xmlOptional(citations = nonNullCollection(citations, Citation.class));
    }

    /**
     * Sets the complete citation of the instrument.
     *
     * @param newValues The new citation values.
     */
    public synchronized void setCitations(final Collection<? extends Citation> newValues) {
        citations = copyCollection(newValues, citations, Citation.class);
    }

    /**
     * Returns the unique identification of the instrument.
     */
    @Override
    @XmlElement(name = "identifier")
    public synchronized Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identification of the instrument.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
    }

    /**
     * Returns the name of the type of instrument. Examples: framing, line-scan, push-broom, pan-frame.
     */
    @Override
    @XmlElement(name = "type")
    public synchronized InternationalString getType() {
        return type;
    }

    /**
     * Sets the name of the type of instrument. Examples: framing, line-scan, push-broom, pan-frame.
     *
     * @param newValue The new type value.
     */
    public synchronized void setType(final InternationalString newValue) {
        checkWritePermission();
        type = newValue;
    }

    /**
     * Returns the textual description of the instrument. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the textual description of the instrument.
     *
     * @param newValue The new description value.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the platform on which the instrument is mounted. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "mountedOn")
    public synchronized Platform getMountedOn() {
        return mountedOn;
    }

    /**
     * Sets the platform on which the instrument is mounted.
     *
     * @param newValue The new platform value.
     */
    public synchronized void setMountedOn(final Platform newValue) {
        checkWritePermission();
        mountedOn = newValue;
    }
}
