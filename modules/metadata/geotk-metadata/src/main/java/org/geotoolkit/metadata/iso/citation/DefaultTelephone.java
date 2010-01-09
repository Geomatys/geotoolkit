/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.metadata.citation.Telephone;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Telephone numbers for contacting the responsible individual or organization.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "voices",
    "facsimiles"
})
@XmlRootElement(name = "CI_Telephone")
public class DefaultTelephone extends MetadataEntity implements Telephone {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultTelephone(final Telephone source) {
        super(source);
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
        return xmlOptional(voices = nonNullCollection(voices, String.class));
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
        return xmlOptional(facsimiles = nonNullCollection(facsimiles, String.class));
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
