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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Per-output execution options, keyed by output id in
 * {@link Execute#getOutputs()}.
 */
@JsonPropertyOrder({
    OutputSelection.JSON_PROPERTY_FORMAT,
    OutputSelection.JSON_PROPERTY_TRANSMISSION_MODE
})
public final class OutputSelection extends DataTransferObject {

    public static final String JSON_PROPERTY_FORMAT = "format";
    private Format format;

    public static final String JSON_PROPERTY_TRANSMISSION_MODE = "transmissionMode";
    private TransmissionMode transmissionMode;

    public OutputSelection() {
    }

    public OutputSelection format(Format format) {
        this.format = format;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Format getFormat() {
        return format;
    }

    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFormat(Format format) {
        this.format = format;
    }

    public OutputSelection transmissionMode(TransmissionMode transmissionMode) {
        this.transmissionMode = transmissionMode;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TRANSMISSION_MODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TransmissionMode getTransmissionMode() {
        return transmissionMode;
    }

    @JsonProperty(JSON_PROPERTY_TRANSMISSION_MODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTransmissionMode(TransmissionMode transmissionMode) {
        this.transmissionMode = transmissionMode;
    }
}
