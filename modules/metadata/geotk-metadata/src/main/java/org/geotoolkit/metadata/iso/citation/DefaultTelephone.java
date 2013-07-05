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
package org.geotoolkit.metadata.iso.citation;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.Telephone;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Telephone numbers for contacting the responsible individual or organization.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "CI_Telephone_Type", propOrder={
    "voices",
    "facsimiles"
})
@XmlRootElement(name = "CI_Telephone")
public class DefaultTelephone extends MetadataEntity implements Telephone {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4920157673337669241L;

    /**
     * Telephone numbers by which individuals can speak to the responsible organization or
     * individual.
     */
    private Collection<String> voices;

    /**
     * Telephone numbers of a facsimile machine for the responsible organization or individual.
     */
    private Collection<String> facsimiles;

    /**
     * Constructs a default telephone.
     */
    public DefaultTelephone() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultTelephone(final Telephone source) {
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
    public static DefaultTelephone castOrCopy(final Telephone object) {
        return (object == null) || (object instanceof DefaultTelephone)
                ? (DefaultTelephone) object : new DefaultTelephone(object);
    }

    /**
     * Returns the telephone numbers by which individuals can speak to the responsible
     * organization or individual.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "voice")
    public synchronized Collection<String> getVoices() {
        return voices = nonNullCollection(voices, String.class);
    }

    /**
     * Sets the telephone numbers by which individuals can speak to the responsible
     * organization or individual.
     *
     * @param newValues The new telephone numbers.
     *
     * @since 2.4
     */
    public synchronized void setVoices(final Collection<? extends String> newValues) {
        voices = copyCollection(newValues, voices, String.class);
    }

    /**
     * Returns the telephone numbers of a facsimile machine for the responsible organization
     * or individual.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "facsimile")
    public synchronized Collection<String> getFacsimiles() {
        return facsimiles = nonNullCollection(facsimiles, String.class);
    }

    /**
     * Sets the telephone number of a facsimile machine for the responsible organization
     * or individual.
     *
     * @param newValues The new telephone number.
     *
     * @since 2.4
     */
    public synchronized void setFacsimiles(final Collection<? extends String> newValues) {
        facsimiles = copyCollection(newValues, facsimiles, String.class);
    }
}
