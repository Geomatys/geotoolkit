/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.observation;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve a description (typically SensorML) of a sensor.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeSensor extends RequestParameters {

    private String sensorId;
    private String outputFormat;

    /**
     * Identifier of the sensor to describe. (required)
     *
     * @return the sensorId
     */
    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public DescribeSensor sensorId(String sensorId) {
        setSensorId(sensorId);
        return this;
    }

    /**
     * Requested output format, e.g. {@code text/xml;subtype="sensorML/1.0.1"}. (required)
     *
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public DescribeSensor outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
