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
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.encoding.AbstractEncoding;

/**
 * Schema describing the format and structure of a {@link DataStream}'s observation results and
 * parameters.
 *
 * <p>This is a flat class with every field nullable and no custom (de)serializer: {@link
 * #getObsFormat()} is the discriminator, and only the fields relevant to its value are expected
 * to be populated at any given time. There is no real ambiguity to resolve since {@code
 * obsFormat} itself already tells the caller which fields are meaningful:</p>
 * <ul>
 *   <li>{@code application/json}: {@link #getParametersSchema()}, {@link #getResultSchema()},
 *       {@link #getResultLink()}.</li>
 *   <li>{@code application/swe+json}, {@code application/swe+text}, {@code application/swe+csv},
 *       {@code application/swe+binary}: {@link #getRecordSchema()}, {@link #getEncoding()}.</li>
 *   <li>{@code application/x-protobuf}: {@link #getMessageSchema()}.</li>
 *   <li>any other value: {@link #getAny()}.</li>
 * </ul>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class ObservationSchema extends DataTransferObject {

    public static final String JSON_PROPERTY_OBS_FORMAT = "obsFormat";
    private String obsFormat;

    public static final String JSON_PROPERTY_PARAMETERS_SCHEMA = "parametersSchema";
    private AbstractDataComponent parametersSchema;

    public static final String JSON_PROPERTY_RESULT_SCHEMA = "resultSchema";
    private AbstractDataComponent resultSchema;

    public static final String JSON_PROPERTY_RECORD_SCHEMA = "recordSchema";
    private AbstractDataComponent recordSchema;

    public static final String JSON_PROPERTY_ENCODING = "encoding";
    private AbstractEncoding encoding;

    public static final String JSON_PROPERTY_RESULT_LINK = "resultLink";
    private ResultLink resultLink;

    public static final String JSON_PROPERTY_MESSAGE_SCHEMA = "messageSchema";
    private JsonNode messageSchema;

    public static final String JSON_PROPERTY_ANY = "any";
    private JsonNode any;

    /**
     * Media type discriminating the format of this stream's observations, e.g. {@code
     * application/json}, {@code application/swe+json}, {@code application/swe+text}, {@code
     * application/swe+csv}, {@code application/swe+binary}, {@code application/x-protobuf}, or
     * other implementation-defined values. (required)
     */
    @JsonProperty(JSON_PROPERTY_OBS_FORMAT)
    public String getObsFormat() {
        return obsFormat;
    }

    @JsonProperty(JSON_PROPERTY_OBS_FORMAT)
    public void setObsFormat(String obsFormat) {
        this.obsFormat = obsFormat;
    }

    public ObservationSchema obsFormat(String obsFormat) {
        setObsFormat(obsFormat);
        return this;
    }

    /**
     * Schema of the observation parameters. Used when {@link #getObsFormat()} is {@code
     * application/json}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS_SCHEMA)
    public AbstractDataComponent getParametersSchema() {
        return parametersSchema;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS_SCHEMA)
    public void setParametersSchema(AbstractDataComponent parametersSchema) {
        this.parametersSchema = parametersSchema;
    }

    public ObservationSchema parametersSchema(AbstractDataComponent parametersSchema) {
        setParametersSchema(parametersSchema);
        return this;
    }

    /**
     * Schema of the observation result. Used when {@link #getObsFormat()} is {@code
     * application/json}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_SCHEMA)
    public AbstractDataComponent getResultSchema() {
        return resultSchema;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_SCHEMA)
    public void setResultSchema(AbstractDataComponent resultSchema) {
        this.resultSchema = resultSchema;
    }

    public ObservationSchema resultSchema(AbstractDataComponent resultSchema) {
        setResultSchema(resultSchema);
        return this;
    }

    /**
     * Schema of the observation record (what the spec calls {@code recordSchema} for the SWE
     * variants). Used when {@link #getObsFormat()} is one of the {@code application/swe+*}
     * variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RECORD_SCHEMA)
    public AbstractDataComponent getRecordSchema() {
        return recordSchema;
    }

    @JsonProperty(JSON_PROPERTY_RECORD_SCHEMA)
    public void setRecordSchema(AbstractDataComponent recordSchema) {
        this.recordSchema = recordSchema;
    }

    public ObservationSchema recordSchema(AbstractDataComponent recordSchema) {
        setRecordSchema(recordSchema);
        return this;
    }

    /**
     * Encoding used to serialize {@link #getRecordSchema()}. Used when {@link #getObsFormat()} is
     * one of the {@code application/swe+*} variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ENCODING)
    public AbstractEncoding getEncoding() {
        return encoding;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    public void setEncoding(AbstractEncoding encoding) {
        this.encoding = encoding;
    }

    public ObservationSchema encoding(AbstractEncoding encoding) {
        setEncoding(encoding);
        return this;
    }

    /**
     * Media type of an out-of-band observation result. Used when {@link #getObsFormat()} is
     * {@code application/json} and observations link to an external result rather than embedding
     * it inline. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_LINK)
    public ResultLink getResultLink() {
        return resultLink;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_LINK)
    public void setResultLink(ResultLink resultLink) {
        this.resultLink = resultLink;
    }

    public ObservationSchema resultLink(ResultLink resultLink) {
        setResultLink(resultLink);
        return this;
    }

    /**
     * Schema of the encoded message, either a media type string or a link to an external schema
     * document. Used when {@link #getObsFormat()} is {@code application/x-protobuf}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_MESSAGE_SCHEMA)
    public JsonNode getMessageSchema() {
        return messageSchema;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE_SCHEMA)
    public void setMessageSchema(JsonNode messageSchema) {
        this.messageSchema = messageSchema;
    }

    public ObservationSchema messageSchema(JsonNode messageSchema) {
        setMessageSchema(messageSchema);
        return this;
    }

    /**
     * Catch-all for schema content associated with an {@link #getObsFormat()} value not otherwise
     * modeled above. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ANY)
    public JsonNode getAny() {
        return any;
    }

    @JsonProperty(JSON_PROPERTY_ANY)
    public void setAny(JsonNode any) {
        this.any = any;
    }

    public ObservationSchema any(JsonNode any) {
        setAny(any);
        return this;
    }

    /**
     * Descriptor of an out-of-band observation result, as referenced by {@link
     * ObservationSchema#getResultLink()}.
     */
    public static class ResultLink extends DataTransferObject {

        public static final String JSON_PROPERTY_MEDIA_TYPE = "mediaType";
        private String mediaType;

        /**
         * Media type of the linked result. (optional)
         */
        @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
        public String getMediaType() {
            return mediaType;
        }

        @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public ResultLink mediaType(String mediaType) {
            setMediaType(mediaType);
            return this;
        }

    }

}
