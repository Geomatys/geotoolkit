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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Per-output execution options, keyed by output id in
 * {@link Execute#getOutputs()}.
 */
@JsonPropertyOrder({
    OutputSelection.JSON_PROPERTY_FORMAT,
    OutputSelection.JSON_PROPERTY_TRANSMISSION_MODE
})
@XmlRootElement(name = "OutputSelection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "OutputSelection")
public final class OutputSelection extends DataTransferObject {

    public static final String JSON_PROPERTY_FORMAT = "format";
    @XmlElement(name = "format")
    @jakarta.annotation.Nullable
    private Format format;

    public static final String JSON_PROPERTY_TRANSMISSION_MODE = "transmissionMode";
    @XmlElement(name = "transmissionMode")
    @jakarta.annotation.Nullable
    private TransmissionMode transmissionMode;

    public OutputSelection() {
    }

    public OutputSelection format(@jakarta.annotation.Nullable Format format) {
        this.format = format;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "format")
    public Format getFormat() {
        return format;
    }

    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "format")
    public void setFormat(@jakarta.annotation.Nullable Format format) {
        this.format = format;
    }

    public OutputSelection transmissionMode(@jakarta.annotation.Nullable TransmissionMode transmissionMode) {
        this.transmissionMode = transmissionMode;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TRANSMISSION_MODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "transmissionMode")
    public TransmissionMode getTransmissionMode() {
        return transmissionMode;
    }

    @JsonProperty(JSON_PROPERTY_TRANSMISSION_MODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "transmissionMode")
    public void setTransmissionMode(@jakarta.annotation.Nullable TransmissionMode transmissionMode) {
        this.transmissionMode = transmissionMode;
    }
}
