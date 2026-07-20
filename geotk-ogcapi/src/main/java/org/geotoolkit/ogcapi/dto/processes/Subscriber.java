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
 * Callback URIs for asynchronous job execution (non-Core "Callback"
 * conformance class), attached to an {@link Execute} request.
 */
@JsonPropertyOrder({
    Subscriber.JSON_PROPERTY_SUCCESS_URI,
    Subscriber.JSON_PROPERTY_IN_PROGRESS_URI,
    Subscriber.JSON_PROPERTY_FAILED_URI
})
@XmlRootElement(name = "Subscriber")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Subscriber")
public final class Subscriber extends DataTransferObject {

    public static final String JSON_PROPERTY_SUCCESS_URI = "successUri";
    @XmlElement(name = "successUri")
    @jakarta.annotation.Nonnull
    private String successUri;

    public static final String JSON_PROPERTY_IN_PROGRESS_URI = "inProgressUri";
    @XmlElement(name = "inProgressUri")
    @jakarta.annotation.Nullable
    private String inProgressUri;

    public static final String JSON_PROPERTY_FAILED_URI = "failedUri";
    @XmlElement(name = "failedUri")
    @jakarta.annotation.Nullable
    private String failedUri;

    public Subscriber() {
    }

    public Subscriber successUri(@jakarta.annotation.Nonnull String successUri) {
        this.successUri = successUri;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SUCCESS_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "successUri")
    public String getSuccessUri() {
        return successUri;
    }

    @JsonProperty(JSON_PROPERTY_SUCCESS_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "successUri")
    public void setSuccessUri(@jakarta.annotation.Nonnull String successUri) {
        this.successUri = successUri;
    }

    public Subscriber inProgressUri(@jakarta.annotation.Nullable String inProgressUri) {
        this.inProgressUri = inProgressUri;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_IN_PROGRESS_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "inProgressUri")
    public String getInProgressUri() {
        return inProgressUri;
    }

    @JsonProperty(JSON_PROPERTY_IN_PROGRESS_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "inProgressUri")
    public void setInProgressUri(@jakarta.annotation.Nullable String inProgressUri) {
        this.inProgressUri = inProgressUri;
    }

    public Subscriber failedUri(@jakarta.annotation.Nullable String failedUri) {
        this.failedUri = failedUri;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FAILED_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "failedUri")
    public String getFailedUri() {
        return failedUri;
    }

    @JsonProperty(JSON_PROPERTY_FAILED_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "failedUri")
    public void setFailedUri(@jakarta.annotation.Nullable String failedUri) {
        this.failedUri = failedUri;
    }
}
