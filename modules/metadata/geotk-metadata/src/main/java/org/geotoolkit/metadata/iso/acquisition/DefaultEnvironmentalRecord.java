/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.acquisition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the environmental conditions during the acquisition.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MI_EnvironmentalRecord_Type", propOrder={
    "averageAirTemperature",
    "maxRelativeHumidity",
    "maxAltitude",
    "meteorologicalConditions"
})
@XmlRootElement(name = "MI_EnvironmentalRecord")
public class DefaultEnvironmentalRecord extends MetadataEntity implements EnvironmentalRecord {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8553651678708627947L;

    /**
     * Average air temperature along the flight pass during the photo flight.
     */
    private Double averageAirTemperature;

    /**
     * Maximum relative humidity along the flight pass during the photo flight.
     */
    private Double maxRelativeHumidity;

    /**
     * Maximum altitude during the photo flight.
     */
    private Double maxAltitude;

    /**
     * Meteorological conditions in the photo flight area, in particular clouds, snow and wind.
     */
    private InternationalString meteorologicalConditions;

    /**
     * Constructs an initially empty environmental record.
     */
    public DefaultEnvironmentalRecord() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultEnvironmentalRecord(final EnvironmentalRecord source) {
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
    public static DefaultEnvironmentalRecord castOrCopy(final EnvironmentalRecord object) {
        return (object == null) || (object instanceof DefaultEnvironmentalRecord)
                ? (DefaultEnvironmentalRecord) object : new DefaultEnvironmentalRecord(object);
    }

    /**
     * Returns the average air temperature along the flight pass during the photo flight.
     */
    @Override
    @XmlElement(name = "averageAirTemperature", required = true)
    public synchronized Double getAverageAirTemperature() {
        return averageAirTemperature;
    }

    /**
     * Sets the average air temperature along the flight pass during the photo flight.
     *
     * @param newValue The new average air temperature value.
     */
    public synchronized void setAverageAirTemperature(final Double newValue) {
        checkWritePermission();
        averageAirTemperature = newValue;
    }

    /**
     * Returns the maximum relative humidity along the flight pass during the photo flight.
     */
    @Override
    @ValueRange(minimum=0, maximum=100)
    @XmlElement(name = "maxRelativeHumidity", required = true)
    public synchronized Double getMaxRelativeHumidity() {
        return maxRelativeHumidity;
    }

    /**
     * Sets the maximum relative humidity along the flight pass during the photo flight.
     *
     * @param newValue The new maximum relative humidity.
     */
    public synchronized void setMaxRelativeHumidity(final Double newValue) {
        checkWritePermission();
        maxRelativeHumidity = newValue;
    }

    /**
     * Returns the maximum altitude during the photo flight.
     */
    @Override
    @XmlElement(name = "maxAltitude", required = true)
    public synchronized Double getMaxAltitude() {
        return maxAltitude;
    }

    /**
     * Sets the maximum altitude value.
     *
     * @param newValue The new maximum altitude value.
     */
    public synchronized void setMaxAltitude(final Double newValue) {
        checkWritePermission();
        maxAltitude = newValue;
    }

    /**
     * Returns the meteorological conditions in the photo flight area, in particular clouds,
     * snow and wind.
     */
    @Override
    @XmlElement(name = "meteorologicalConditions", required = true)
    public synchronized InternationalString getMeteorologicalConditions() {
        return meteorologicalConditions;
    }

    /**
     * Sets the meteorological conditions in the photo flight area, in particular clouds,
     * snow and wind.
     *
     * @param newValue The meteorological conditions value.
     */
    public synchronized void setMeteorologicalConditions(final InternationalString newValue) {
        checkWritePermission();
        meteorologicalConditions = newValue;
    }
}
