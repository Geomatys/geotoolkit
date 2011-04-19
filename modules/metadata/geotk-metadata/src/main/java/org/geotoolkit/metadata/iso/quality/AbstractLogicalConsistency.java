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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.quality.LogicalConsistency;


/**
 * Degree of adherence to logical rules of data structure, attribution and relationships (data
 * structure can be conceptual, logical or physical).
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.04
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "AbstractDQ_LogicalConsistency_Type")
@XmlRootElement(name = "DQ_LogicalConsistency")
@XmlSeeAlso({
    DefaultConceptualConsistency.class,
    DefaultDomainConsistency.class,
    DefaultDomainConsistency.class,
    DefaultFormatConsistency.class,
    DefaultTopologicalConsistency.class
})
public class AbstractLogicalConsistency extends AbstractElement implements LogicalConsistency {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2470752876057569947L;

    /**
     * Constructs an initially empty logical consistency.
     */
    public AbstractLogicalConsistency() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public AbstractLogicalConsistency(final LogicalConsistency source) {
        super(source);
    }
}
