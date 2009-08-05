/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.metadata.extent.VerticalExtent;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Vertical domain of dataset.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "minimumValue",
    "maximumValue",
    "verticalCRS"
})
@XmlRootElement(name = "EX_VerticalExtent")
public class DefaultVerticalExtent extends MetadataEntity implements VerticalExtent {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3214554246909844079L;

    /**
     * The lowest vertical extent contained in the dataset.
     */
    private Double minimumValue;

    /**
     * The highest vertical extent contained in the dataset.
     */
    private Double maximumValue;

    /**
     * Provides information about the vertical coordinate reference system to
     * which the maximum and minimum elevation values are measured. The CRS
     * identification includes unit of measure.
     */
    private VerticalCRS verticalCRS;

    /**
     * Constructs an initially empty vertical extent.
     */
    public DefaultVerticalExtent() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultVerticalExtent(final VerticalExtent source) {
        super(source);
    }

    /**
     * Creates a vertical extent initialized to the specified values.
     *
     * @param minimumValue The lowest vertical extent contained in the dataset.
     * @param maximumValue The highest vertical extent contained in the dataset.
     * @param verticalCRS  The information about the vertical coordinate reference system.
     *
     * @since 2.4
     */
    public DefaultVerticalExtent(final Double minimumValue,
                                 final Double maximumValue,
                                 final VerticalCRS verticalCRS)
    {
        setMinimumValue(minimumValue);
        setMaximumValue(maximumValue);
        setVerticalCRS (verticalCRS );
    }

    /**
     * Returns the lowest vertical extent contained in the dataset.
     */
    @Override
    @XmlElement(name = "minimumValue", required = true)
    public synchronized Double getMinimumValue() {
        return minimumValue;
    }

    /**
     * Sets the lowest vertical extent contained in the dataset.
     *
     * @param newValue The new minimum value.
     */
    public synchronized void setMinimumValue(final Double newValue) {
        checkWritePermission();
        minimumValue = newValue;
    }

    /**
     * Returns the highest vertical extent contained in the dataset.
     */
    @Override
    @XmlElement(name = "maximumValue", required = true)
    public synchronized Double getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the highest vertical extent contained in the dataset.
     *
     * @param newValue The new maximum value.
     */
    public synchronized void setMaximumValue(final Double newValue) {
        checkWritePermission();
        maximumValue = newValue;
    }

    /**
     * Provides information about the vertical coordinate reference system to
     * which the maximum and minimum elevation values are measured. The CRS
     * identification includes unit of measure.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "verticalCRS", required = true)
    public synchronized VerticalCRS getVerticalCRS() {
        return verticalCRS;
    }

    /**
     * Sets the information about the vertical coordinate reference system to
     * which the maximum and minimum elevation values are measured.
     *
     * @param newValue The new vertical CRS.
     *
     * @since 2.4
     */
    public synchronized void setVerticalCRS(final VerticalCRS newValue) {
        checkWritePermission();
        verticalCRS = newValue;
    }
}
