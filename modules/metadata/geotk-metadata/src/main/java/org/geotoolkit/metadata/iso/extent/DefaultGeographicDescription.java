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
package org.geotoolkit.metadata.iso.extent;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.GeographicDescription;

import org.geotoolkit.metadata.iso.DefaultIdentifier;


/**
 * Description of the geographic area using identifiers.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "EX_GeographicDescription_Type")
@XmlRootElement(name = "EX_GeographicDescription")
public class DefaultGeographicDescription extends AbstractGeographicExtent
        implements GeographicDescription
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7250161161099782176L;

    /**
     * The identifier used to represent a geographic area.
     */
    private Identifier geographicIdentifier;

    /**
     * Constructs an initially empty geographic description.
     */
    public DefaultGeographicDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultGeographicDescription(final GeographicDescription source) {
        super(source);
    }

    /**
     * Creates a geographic description initialized to the specified value.
     *
     * @param geographicIdentifier The identifier used to represent a geographic area,
     *        or {@code null} if none.
     */
    public DefaultGeographicDescription(final Identifier geographicIdentifier) {
        if (geographicIdentifier != null) {
            setGeographicIdentifier(geographicIdentifier);
        }
    }

    /**
     * Creates a geographic description initialized to the specified value.
     *
     * @param geographicIdentifier The identifier used to represent a geographic area,
     *        or {@code null} if none.
     *
     * @since 3.20
     */
    public DefaultGeographicDescription(final String geographicIdentifier) {
        if (geographicIdentifier != null) {
            setGeographicIdentifier(new DefaultIdentifier(geographicIdentifier));
        }
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
    public static DefaultGeographicDescription castOrCopy(final GeographicDescription object) {
        return (object == null) || (object instanceof DefaultGeographicDescription)
                ? (DefaultGeographicDescription) object : new DefaultGeographicDescription(object);
    }

    /**
     * Returns the identifier used to represent a geographic area.
     */
    @Override
    @XmlElement(name = "geographicIdentifier", required = true)
    public synchronized Identifier getGeographicIdentifier() {
        return geographicIdentifier;
    }

    /**
     * Sets the identifier used to represent a geographic area.
     *
     * @param newValue The new geographic identifier.
     */
    public synchronized void setGeographicIdentifier(final Identifier newValue) {
        checkWritePermission();
        geographicIdentifier = newValue;
    }
}
