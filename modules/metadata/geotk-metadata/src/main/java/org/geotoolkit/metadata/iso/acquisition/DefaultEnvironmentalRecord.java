/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.acquisition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the environmental conditions during the acquisition.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "averageAirTemperature",
    "maxRelativeHumidity",
    "maxAltitude",
    "meteorologicalConditions"
})
@XmlRootElement(name = "MI_EnvironmentalRecord")
public class DefaultEnvironmentalRecord extends MetadataEntity implements EnvironmentalRecord {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     */
    public DefaultEnvironmentalRecord(final EnvironmentalRecord source) {
        super(source);
    }

    /**
     * Returns the average air temperature along the flight pass during the photo flight.
     */
    @Override
    @XmlElement(name = "averageAirTemperature")
    public synchronized Double getAverageAirTemperature() {
        return averageAirTemperature;
    }

    /**
     * Sets the average air temperature along the flight pass during the photo flight.
     *
     * @param newValue The new average air tempature value.
     */
    public synchronized void setAverageAirTemperature(final Double newValue) {
        checkWritePermission();
        averageAirTemperature = newValue;
    }

    /**
     * Returns the maximum relative humidity along the flight pass during the photo flight.
     */
    @Override
    @XmlElement(name = "maxRelativeHumidity")
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
    @XmlElement(name = "maxAltitude")
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
    @XmlElement(name = "meteorologicalConditions")
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
