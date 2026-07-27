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
 * Callback URIs for asynchronous job execution (non-Core "Callback"
 * conformance class), attached to an {@link Execute} request.
 */
@JsonPropertyOrder({
    Subscriber.JSON_PROPERTY_SUCCESS_URI,
    Subscriber.JSON_PROPERTY_IN_PROGRESS_URI,
    Subscriber.JSON_PROPERTY_FAILED_URI
})
public final class Subscriber extends DataTransferObject {

    public static final String JSON_PROPERTY_SUCCESS_URI = "successUri";
    private String successUri;

    public static final String JSON_PROPERTY_IN_PROGRESS_URI = "inProgressUri";
    private String inProgressUri;

    public static final String JSON_PROPERTY_FAILED_URI = "failedUri";
    private String failedUri;

    public Subscriber() {
    }

    public Subscriber successUri(String successUri) {
        this.successUri = successUri;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_SUCCESS_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getSuccessUri() {
        return successUri;
    }

    @JsonProperty(JSON_PROPERTY_SUCCESS_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSuccessUri(String successUri) {
        this.successUri = successUri;
    }

    public Subscriber inProgressUri(String inProgressUri) {
        this.inProgressUri = inProgressUri;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_IN_PROGRESS_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getInProgressUri() {
        return inProgressUri;
    }

    @JsonProperty(JSON_PROPERTY_IN_PROGRESS_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInProgressUri(String inProgressUri) {
        this.inProgressUri = inProgressUri;
    }

    public Subscriber failedUri(String failedUri) {
        this.failedUri = failedUri;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_FAILED_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getFailedUri() {
        return failedUri;
    }

    @JsonProperty(JSON_PROPERTY_FAILED_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFailedUri(String failedUri) {
        this.failedUri = failedUri;
    }
}
