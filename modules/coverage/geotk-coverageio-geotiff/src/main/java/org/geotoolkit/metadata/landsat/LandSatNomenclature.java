/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.metadata.landsat;

import java.util.Date;
import org.geotoolkit.util.NumberRange;

/**
 * From a landsat file name, several information can be extracted.
 * This class organise information which can be extracted from it.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class LandSatNomenclature {
    
    public static enum Channel{
        B10("Visible Blue",         NumberRange.create(0.45,0.52),  30),
        B20("Visible Green",        NumberRange.create(0.52,0.60),  30),
        B30("Visible Red",          NumberRange.create(0.63,0.69),  30),
        B40("Near infrared",        NumberRange.create(0.77,0.90),  30),
        B50("Near infrared",        NumberRange.create(1.55,1.75),  30),
        B61("Thermique low gain",   NumberRange.create(10.40,12.50),60),
        B62("Thermique high gain",  NumberRange.create(10.40,12.50),60),
        B70("Medium infrared",      NumberRange.create(2.08,2.35),  30),
        B80("Panchromatic",         NumberRange.create(0.52,0.90),  15);
        
        private final String description;
        private final NumberRange spectrum;
        private final double resolution;

        private Channel(String description, NumberRange spectrum, double resolution) {
            this.description = description;
            this.spectrum = spectrum;
            this.resolution = resolution;
        }

        /**
         * General description of this channel.
         * @return String, not null
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sensor aquisition sensor range.
         * @return NumberRange in um (micro-meter)
         */
        public NumberRange getSpectrum() {
            return spectrum;
        }

        /**
         * Expect approximative image resolution in meter.
         * @return number in meter
         */
        public double getResolution() {
            return resolution;
        }
        
    }
    
    private final String sensor;
    private final int platform;
    private final int path;
    private final int startRow;
    private final int endRow;
    private final Date date;
    private final Channel channel;
    private final String station;

    public LandSatNomenclature(String sensor, int platform, int path, int startRow, int endRow, Date date, Channel channel, String station) {
        this.sensor = sensor;
        this.platform = platform;
        this.path = path;
        this.startRow = startRow;
        this.endRow = endRow;
        this.date = date;
        this.channel = channel;
        this.station = station;
    }

    /**
     * Sensor :
     * E : ETM+
     * T : TM:
     * M
     * 
     * @return String, non null
     */
    public String getSensor() {
        return sensor;
    }

    /**
     * @return platform number
     */
    public int getPlatform() {
        return platform;
    }

    /**
     * Satellite path number.
     * @return int 
     */
    public int getPath() {
        return path;
    }

    /**
     * Image start row position in satellite path.
     * @return int 
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Image end row position in satellite path.
     * @return int 
     */
    public int getEndRow() {
        return endRow;
    }

    /**
     * Image aquisition date.
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Current channel if name define a channel.
     * @return can be null
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return String station
     */
    public String getStation() {
        return station;
    }
        
}
