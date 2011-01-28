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
package org.geotoolkit.metadata.iso.content;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.MemberName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.content.RangeDimension;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information on the range of each dimension of a cell measurement value.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.17
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_RangeDimension", propOrder={
    "sequenceIdentifier",
    "descriptor"
})
@XmlSeeAlso({DefaultBand.class})
@XmlRootElement(name = "MD_RangeDimension")
public class DefaultRangeDimension extends MetadataEntity implements RangeDimension {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4365956866782010460L;

    /**
     * Number that uniquely identifies instances of bands of wavelengths on which a sensor
     * operates.
     */
    private MemberName sequenceIdentifier;

    /**
     * Description of the range of a cell measurement value.
     */
    private InternationalString descriptor;

    /**
     * Constructs an initially empty range dimension.
     */
    public DefaultRangeDimension() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultRangeDimension(final RangeDimension source) {
        super(source);
    }

    /**
     * Returns the number that uniquely identifies instances of bands of wavelengths
     * on which a sensor operates.
     */
    @Override
    @XmlElement(name = "sequenceIdentifier")
    public synchronized MemberName getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    /**
     * Sets the number that uniquely identifies instances of bands of wavelengths
     * on which a sensor operates.
     *
     * @param newValue The new sequence identifier.
     */
    public synchronized void setSequenceIdentifier(final MemberName newValue) {
        checkWritePermission();
        sequenceIdentifier = newValue;
    }

    /**
     * Returns the description of the range of a cell measurement value.
     */
    @Override
    @XmlElement(name = "descriptor")
    public synchronized InternationalString getDescriptor() {
        return descriptor;
    }

    /**
     * Sets the description of the range of a cell measurement value.
     *
     * @param newValue The new descriptor.
     */
    public synchronized void setDescriptor(final InternationalString newValue) {
        checkWritePermission();
        descriptor = newValue;
    }
}
