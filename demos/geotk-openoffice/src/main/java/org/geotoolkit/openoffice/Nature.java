/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
 */
package org.geotoolkit.openoffice;

import com.sun.star.beans.XPropertySet;

import org.geotoolkit.nature.Calendar;
import org.geotoolkit.nature.SeaWater;
import org.geotoolkit.nature.SunRelativePosition;


/**
 * Exports methods from the {@link org.geotoolkit.nature} package as
 * <A HREF="http://www.openoffice.org">OpenOffice</A> add-ins.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public final class Nature extends Formulas implements XNature {
    /**
     * The name for the registration of this component.
     * <strong>NOTE:</strong> OpenOffice expects a field with exactly that name; do not rename!
     */
    static final String __serviceName = "org.geotoolkit.openoffice.Nature";

    /**
     * The calculator for sun relative position. Will be created only when first needed.
     */
    private transient SunRelativePosition calculator;

    /**
     * Constructs a default implementation of {@code XNature} interface.
     */
    public Nature() {
        setTimeZone("GMT");
        methods.put("getNoonTime", new MethodInfo("Nature", "NOON.TIME",
            "Returns the noon time (in GMT) when the Sun reach its highest point.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "latitude",   "The latitude of observation point, in degrees.",
                "longitude",  "The longitude of observation point, in degrees.",
                "time",       "The observation date."
        }));
        methods.put("getElevation", new MethodInfo("Nature", "SUN.ELEVATION",
            "Returns the Sun's elevation angle in degrees.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "latitude",   "The latitude of observation point, in degrees.",
                "longitude",  "The longitude of observation point, in degrees.",
                "time",       "The observation date and time, in GMT."
        }));
        methods.put("getAzimuth", new MethodInfo("Nature", "SUN.AZIMUTH",
            "Returns the Sun's azimuth in degrees.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "latitude",   "The latitude of observation point, in degrees.",
                "longitude",  "The longitude of observation point, in degrees.",
                "time",       "The observation date and time, in GMT."
        }));
        methods.put("getTropicalYearLength", new MethodInfo("Nature", "TROPICAL.YEAR.LENGTH",
            "Returns the tropical year length in days.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "time",       "A date that contains the year."
        }));
        methods.put("getSynodicMonthLength", new MethodInfo("Nature", "SYNODIC.MONTH.LENGTH",
            "Returns the synodic month length in days.",
            new String[] {
                "xOptions",   "Provided by OpenOffice.",
                "time",       "A date that contains the month."
        }));
        methods.put("getSeaWaterDensity", new MethodInfo("Nature", "SEAWATER.DENSITY",
            "Computes sea water density (kg/m³) as a function of salinity, temperature and pressure.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "salinity",    "Salinity PSS-78.",
                "temperature", "Temperature ITS-68.",
                "pressure",    "Pressure in decibars, not including atmospheric pressure."
        }));
        methods.put("getSeaWaterMeltingPoint", new MethodInfo("Nature", "SEAWATER.MELTING.POINT",
            "Computes the sea water fusion temperature (melting point) as a function of salinity and pressure.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "salinity",    "Salinity PSS-78.",
                "pressure",    "Pressure in decibars, not including atmospheric pressure."
        }));
        methods.put("getSeaWaterSoundVelocity", new MethodInfo("Nature", "SEAWATER.SOUND.VELOCITY",
            "Computes the sound velocity in sea water as a function of salinity, temperature and pressure.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "salinity",    "Salinity PSS-78.",
                "temperature", "Temperature ITS-68.",
                "pressure",    "Pressure in decibars, not including atmospheric pressure."
        }));
        methods.put("getSeaWaterSaturationO2", new MethodInfo("Nature", "SEAWATER.OXYGEN.SATURATION",
            "Computes the saturation in disolved oxygen (µmol/kg) as a function of salinity and temperature.",
            new String[] {
                "xOptions",    "Provided by OpenOffice.",
                "salinity",    "Salinity PSS-78.",
                "temperature", "Temperature ITS-68."
        }));
    }

    /**
     * The service name that can be used to create such an object by a factory.
     */
    @Override
    public String getServiceName() {
        return __serviceName;
    }

    /**
     * Returns informations about sur relative position for the specified coordinates.
     *
     * @param xOptions  Provided by OpenOffice.
     * @param latitude  The latitude of observation point, in degrees.
     * @param longitude The longitude of observation point, in degrees.
     * @param time      The observation date and time, in GMT.
     */
    private SunRelativePosition getSunRelativePosition(final XPropertySet xOptions,
                                                       final double       latitude,
                                                       final double       longitude,
                                                       final double       time)
    {
        if (calculator == null) {
            calculator = new SunRelativePosition(Double.NaN);
        }
        calculator.setCoordinate(longitude, latitude);
        calculator.setDate(toDate(xOptions, time));
        return calculator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNoonTime(final XPropertySet xOptions,
                              final double       latitude,
                              final double       longitude,
                              final double       time)
    {
        return getSunRelativePosition(xOptions, latitude, longitude, time).getNoonTime() / (double) DAY_TO_MILLIS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getElevation(final XPropertySet xOptions,
                               final double       latitude,
                               final double       longitude,
                               final double       time)
    {
        return getSunRelativePosition(xOptions, latitude, longitude, time).getElevation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAzimuth(final XPropertySet xOptions,
                             final double       latitude,
                             final double       longitude,
                             final double       time)
    {
        return getSunRelativePosition(xOptions, latitude, longitude, time).getAzimuth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTropicalYearLength(final XPropertySet xOptions, final double time) {
        return Calendar.tropicalYearLength(toDate(xOptions, time));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSynodicMonthLength(final XPropertySet xOptions, final double time) {
        return Calendar.synodicMonthLength(toDate(xOptions, time));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSeaWaterDensity(final XPropertySet xOptions,
                                     final double       salinity,
                                     final double       temperature,
                                     final double       pressure)
    {
        return SeaWater.density(salinity, temperature, pressure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSeaWaterMeltingPoint(final XPropertySet xOptions,
                                          final double       salinity,
                                          final double       pressure)
    {
        return SeaWater.fusionTemperature(salinity, pressure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSeaWaterSoundVelocity(final XPropertySet xOptions,
                                           final double       salinity,
                                           final double       temperature,
                                           final double       pressure)
    {
        return SeaWater.soundVelocity(salinity, temperature, pressure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSeaWaterSaturationO2(final XPropertySet xOptions,
                                          final double       salinity,
                                          final double       temperature)
    {
        return SeaWater.saturationO2(salinity, temperature);
    }
}
