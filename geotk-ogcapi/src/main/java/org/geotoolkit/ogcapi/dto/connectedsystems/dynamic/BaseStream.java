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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * Shared shape of the two Part 2 stream resources, {@link DataStream} and {@link ControlStream}.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public abstract class BaseStream extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private TimePeriod validTime;

    public static final String JSON_PROPERTY_FORMATS = "formats";
    private List<String> formats;

    /**
     * Local identifier, typically read-only/server-assigned. (required)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Human readable name of the stream. (required)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Description of the stream. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Time period during which the stream is/was valid. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public TimePeriod getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public void setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
    }

    /**
     * List of media types supported for reading/writing this stream's records. (required)
     */
    @JsonProperty(JSON_PROPERTY_FORMATS)
    public List<String> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_PROPERTY_FORMATS)
    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

}
