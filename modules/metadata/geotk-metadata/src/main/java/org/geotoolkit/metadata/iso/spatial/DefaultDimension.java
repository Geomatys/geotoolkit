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
package org.geotoolkit.metadata.iso.spatial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.DimensionNameType;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.gco.GO_Measure;

import org.geotoolkit.lang.ValueRange;


/**
 * Axis properties.
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
@XmlType(name = "MD_Dimension_Type", propOrder={
    "dimensionName", "dimensionSize", "resolution"
})
@XmlRootElement(name = "MD_Dimension")
public class DefaultDimension extends MetadataEntity implements Dimension {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2572515000574007266L;

    /**
     * Name of the axis.
     */
    private DimensionNameType dimensionName;

    /**
     * Number of elements along the axis.
     */
    private Integer dimensionSize;

    /**
     * Degree of detail in the grid dataset.
     */
    private Double resolution;

    /**
     * Constructs an initially empty dimension.
     */
    public DefaultDimension() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultDimension(final Dimension source) {
        super(source);
    }

    /**
     * Creates a dimension initialized to the given type and size.
     *
     * @param dimensionName The name of the axis, or {@code null} if none.
     * @param dimensionSize The number of elements along the axis.
     */
    public DefaultDimension(final DimensionNameType dimensionName, final int dimensionSize) {
        setDimensionName(dimensionName);
        setDimensionSize(dimensionSize);
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
    public static DefaultDimension castOrCopy(final Dimension object) {
        return (object == null) || (object instanceof DefaultDimension)
                ? (DefaultDimension) object : new DefaultDimension(object);
    }

    /**
     * Returns the name of the axis.
     */
    @Override
    @XmlElement(name = "dimensionName", required = true)
    public synchronized DimensionNameType getDimensionName() {
        return dimensionName;
    }

    /**
     * Sets the name of the axis.
     *
     * @param newValue The new dimension name.
     */
    public synchronized void setDimensionName(final DimensionNameType newValue) {
        checkWritePermission();
        dimensionName = newValue;
    }

    /**
     * Returns the number of elements along the axis.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "dimensionSize", required = true)
    public synchronized Integer getDimensionSize() {
        return dimensionSize;
    }

    /**
     * Sets the number of elements along the axis.
     *
     * @param newValue The new dimension size.
     */
    public synchronized void setDimensionSize(final Integer newValue) {
        checkWritePermission();
        dimensionSize = newValue;
    }

    /**
     * Returns the degree of detail in the grid dataset.
     */
    @Override
    @ValueRange(minimum=0, isMinIncluded=false)
    @XmlJavaTypeAdapter(GO_Measure.class)
    @XmlElement(name = "resolution")
    public synchronized Double getResolution() {
        return resolution;
    }

    /**
     * Sets the degree of detail in the grid dataset.
     *
     * @param newValue The new resolution.
     */
    public synchronized void setResolution(final Double newValue) {
        checkWritePermission();
        resolution = newValue;
    }
}
