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
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Designations for the measuring instruments.
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
@XmlType(name = "MI_Instrument_Type", propOrder={
    "citations",
    "identifier",
    "type",
    "description",
    "mountedOn"
})
@XmlRootElement(name = "MI_Instrument")
public class DefaultInstrument extends MetadataEntity implements Instrument {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6356044176200794578L;

    /**
     * Complete citation of the instrument.
     */
    private Collection<Citation> citations;

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
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultInstrument(final Instrument source) {
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
    public static DefaultInstrument castOrCopy(final Instrument object) {
        return (object == null) || (object instanceof DefaultInstrument)
                ? (DefaultInstrument) object : new DefaultInstrument(object);
    }

    /**
     * Returns the complete citation of the instrument.
     */
    @Override
    @XmlElement(name = "citation")
    public synchronized Collection<Citation> getCitations() {
        return citations = nonNullCollection(citations, Citation.class);
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
    @XmlElement(name = "identifier", required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the unique identification of the instrument.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Returns the name of the type of instrument. Examples: framing, line-scan, push-broom, pan-frame.
     */
    @Override
    @XmlElement(name = "type", required = true)
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
