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
package org.geotoolkit.metadata.iso.distribution;

import java.util.Collection;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.distribution.Medium;
import org.opengis.metadata.distribution.MediumName;
import org.opengis.metadata.distribution.MediumFormat;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the media on which the resource can be distributed.
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
@ThreadSafe
@XmlType(name = "MD_Medium_Type", propOrder={
    "name",
    "densities",
    "densityUnits",
    "volumes",
    "mediumFormats",
    "mediumNote"
})
@XmlRootElement(name = "MD_Medium")
public class DefaultMedium extends MetadataEntity implements Medium {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2838122926367921673L;

    /**
     * Name of the medium on which the resource can be received.
     */
    private MediumName name;

    /**
     * Density at which the data is recorded.
     * Returns {@code null} if unknown.
     * If non-null, then the number should be greater than zero.
     */
    private Collection<Double> densities;

    /**
     * Units of measure for the recording density.
     */
    private Unit<?> densityUnits;

    /**
     * Number of items in the media identified.
     * Returns {@code null} if unknown.
     */
    private Integer volumes;

    /**
     * Methods used to write to the medium.
     */
    private Collection<MediumFormat> mediumFormats;

    /**
     * Description of other limitations or requirements for using the medium.
     */
    private InternationalString mediumNote;

    /**
     * Constructs an initially empty medium.
     */
    public DefaultMedium() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultMedium(final Medium source) {
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
    public static DefaultMedium castOrCopy(final Medium object) {
        return (object == null) || (object instanceof DefaultMedium)
                ? (DefaultMedium) object : new DefaultMedium(object);
    }

    /**
     * Returns the name of the medium on which the resource can be received.
     */
    @Override
    @XmlElement(name = "name")
    public synchronized MediumName getName() {
        return name;
    }

    /**
     * Sets the name of the medium on which the resource can be received.
     *
     * @param newValue The new name.
     */
    public synchronized void setName(final MediumName newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Returns the density at which the data is recorded.
     * The numbers should be greater than zero.
     */
    @Override
    @XmlElement(name = "density")
    public synchronized Collection<Double> getDensities() {
        return densities = nonNullCollection(densities, Double.class);
    }

    /**
     * Sets density at which the data is recorded.
     * The numbers should be greater than zero.
     *
     * @param newValues The new densities.
     */
    public synchronized void setDensities(final Collection<? extends Double> newValues) {
        densities = copyCollection(newValues, densities, Double.class);
    }

    /**
     * Returns the units of measure for the recording density.
     */
    @Override
    @XmlElement(name = "densityUnits")
    public synchronized Unit<?> getDensityUnits() {
        return densityUnits;
    }

    /**
     * Sets the units of measure for the recording density.
     *
     * @param newValue The new density units.
     */
    public synchronized void setDensityUnits(final Unit<?> newValue) {
        checkWritePermission();
        densityUnits = newValue;
    }

    /**
     * Returns the number of items in the media identified.
     * Returns {@code null} if unknown.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "volumes")
    public synchronized Integer getVolumes() {
        return volumes;
    }

    /**
     * Sets the number of items in the media identified.
     * Returns {@code null} if unknown.
     *
     * @param newValue The new volumes.
     */
    public synchronized void setVolumes(final Integer newValue) {
        checkWritePermission();
        volumes = newValue;
    }

    /**
     * Returns the method used to write to the medium.
     */
    @Override
    @XmlElement(name = "mediumFormat")
    public synchronized Collection<MediumFormat> getMediumFormats() {
        return mediumFormats = nonNullCollection(mediumFormats, MediumFormat.class);
    }

    /**
     * Sets the method used to write to the medium.
     *
     * @param newValues The new medium formats.
     */
    public synchronized void setMediumFormats(final Collection<? extends MediumFormat> newValues) {
        mediumFormats = copyCollection(newValues, mediumFormats, MediumFormat.class);
    }

    /**
     * Returns a description of other limitations or requirements for using the medium.
     */
    @Override
    @XmlElement(name = "mediumNote")
    public synchronized InternationalString getMediumNote() {
        return mediumNote;
    }

    /**
     * Sets a description of other limitations or requirements for using the medium.
     *
     * @param newValue The new medium note.
     */
    public synchronized void setMediumNote(final InternationalString newValue) {
        checkWritePermission();
        mediumNote = newValue;
    }
}
