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
package org.geotoolkit.metadata.iso.content;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.MemberName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.Band;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information on the range of each dimension of a cell measurement value.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MD_RangeDimension_Type", propOrder={
    "sequenceIdentifier",
    "descriptor"
})
@XmlRootElement(name = "MD_RangeDimension")
@XmlSeeAlso(DefaultBand.class)
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
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultRangeDimension(final RangeDimension source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link Band} sub-interface. If that interface is found,
     * then this method delegates to the corresponding {@code castOrCopy} static method.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultRangeDimension castOrCopy(final RangeDimension object) {
        if (object instanceof Band) {
            return DefaultBand.castOrCopy((Band) object);
        }
        return (object == null) || (object instanceof DefaultRangeDimension) ?
                (DefaultRangeDimension) object : new DefaultRangeDimension(object);
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
