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
package org.geotoolkit.openeo.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A single log entry for a batch job.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/debug-job">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        LogEntry.JSON_PROPERTY_ID,
        LogEntry.JSON_PROPERTY_CODE,
        LogEntry.JSON_PROPERTY_LEVEL,
        LogEntry.JSON_PROPERTY_MESSAGE,
        LogEntry.JSON_PROPERTY_TIME,
        LogEntry.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "LogEntry")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "LogEntry")
public class LogEntry extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @JacksonXmlProperty(localName = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_CODE = "code";
    @XmlElement(name = "code")
    @JacksonXmlProperty(localName = "code")
    @jakarta.annotation.Nullable
    private String code;

    public static final String JSON_PROPERTY_LEVEL = "level";
    @XmlElement(name = "level")
    @JacksonXmlProperty(localName = "level")
    @jakarta.annotation.Nonnull
    private String level;

    public static final String JSON_PROPERTY_MESSAGE = "message";
    @XmlElement(name = "message")
    @JacksonXmlProperty(localName = "message")
    @jakarta.annotation.Nonnull
    private String message;

    public static final String JSON_PROPERTY_TIME = "time";
    @XmlElement(name = "time")
    @JacksonXmlProperty(localName = "time")
    @jakarta.annotation.Nullable
    private String time;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public LogEntry id(String id) { this.id = id; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public String getId() { return id; }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ID)
    public void setId(@jakarta.annotation.Nonnull String id) { this.id = id; }

    public LogEntry code(String code) { this.code = code; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CODE)
    public String getCode() { return code; }

    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CODE)
    public void setCode(@jakarta.annotation.Nullable String code) { this.code = code; }

    public LogEntry level(String level) { this.level = level; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LEVEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LEVEL)
    public String getLevel() { return level; }

    @JsonProperty(JSON_PROPERTY_LEVEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LEVEL)
    public void setLevel(@jakarta.annotation.Nonnull String level) { this.level = level; }

    public LogEntry message(String message) { this.message = message; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_MESSAGE)
    public String getMessage() { return message; }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_MESSAGE)
    public void setMessage(@jakarta.annotation.Nonnull String message) { this.message = message; }

    public LogEntry time(String time) { this.time = time; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TIME)
    public String getTime() { return time; }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TIME)
    public void setTime(@jakarta.annotation.Nullable String time) { this.time = time; }

    public LogEntry links(List<Link> links) { this.links = links; return this; }
    public LogEntry addLinksItem(Link link) { if (this.links == null) this.links = new ArrayList<>(); this.links.add(link); return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry that = (LogEntry) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.code, that.code)
                && Objects.equals(this.level, that.level)
                && Objects.equals(this.message, that.message)
                && Objects.equals(this.time, that.time)
                && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, level, message, time, links);
    }
}
