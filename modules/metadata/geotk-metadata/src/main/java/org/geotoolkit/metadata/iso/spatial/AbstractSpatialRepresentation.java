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
package org.geotoolkit.metadata.iso.spatial;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.spatial.SpatialRepresentation;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Method used to represent geographic information in the dataset.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "AbstractMD_SpatialRepresentation_Type")
@XmlRootElement(name = "MD_SpatialRepresentation")
@XmlSeeAlso({
    DefaultGridSpatialRepresentation.class,
    DefaultVectorSpatialRepresentation.class
})
public class AbstractSpatialRepresentation extends MetadataEntity implements SpatialRepresentation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1443170876207840116L;

    /**
     * Constructs an initially empty spatial representation.
     */
    public AbstractSpatialRepresentation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public AbstractSpatialRepresentation(final SpatialRepresentation source) {
        super(source);
    }
}
