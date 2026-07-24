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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Phone numbers of a {@link Contact}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Phone extends DataTransferObject {

    public static final String JSON_PROPERTY_VOICE = "voice";
    private String voice;

    public static final String JSON_PROPERTY_FACSIMILE = "facsimile";
    private String facsimile;

    /**
     * Voice phone number. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VOICE)
    public String getVoice() {
        return voice;
    }

    @JsonProperty(JSON_PROPERTY_VOICE)
    public void setVoice(String voice) {
        this.voice = voice;
    }

    public Phone voice(String voice) {
        setVoice(voice);
        return this;
    }

    /**
     * Facsimile phone number. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FACSIMILE)
    public String getFacsimile() {
        return facsimile;
    }

    @JsonProperty(JSON_PROPERTY_FACSIMILE)
    public void setFacsimile(String facsimile) {
        this.facsimile = facsimile;
    }

    public Phone facsimile(String facsimile) {
        setFacsimile(facsimile);
        return this;
    }

}
