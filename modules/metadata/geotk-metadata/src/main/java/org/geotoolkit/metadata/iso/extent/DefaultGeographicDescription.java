/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.GeographicDescription;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Description of the geographic area using identifiers.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
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
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultGeographicDescription(final GeographicDescription source) {
        super(source);
    }

    /**
     * Creates a geographic description initialized to the specified value.
     *
     * @param geographicIdentifier The identifier used to represent a geographic area.
     */
     public DefaultGeographicDescription(final Identifier geographicIdentifier) {
         setGeographicIdentifier(geographicIdentifier);
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
