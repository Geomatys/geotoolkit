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
 * Schema describing the format and structure of a {@link ControlStream}'s commands, results and
 * feasibility results.
 *
 * <p>Same flat-class approach as {@link ObservationSchema}: every field is nullable and no custom
 * (de)serializer is used. {@link #getCommandFormat()} is the discriminator, and only the fields
 * relevant to its value are expected to be populated at any given time:</p>
 * <ul>
 *   <li>{@code application/json}: {@link #getParametersSchema()}, {@link #getResultSchema()},
 *       {@link #getFeasibilityResultSchema()}.</li>
 *   <li>{@code application/swe+json}, {@code application/swe+text}, {@code application/swe+csv},
 *       {@code application/swe+binary}: {@link #getRecordSchema()}, {@link #getEncoding()}.</li>
 *   <li>{@code application/x-protobuf}: {@link #getMessageSchema()}.</li>
 *   <li>any other value: {@link #getAny()}.</li>
 * </ul>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class CommandSchema extends DataTransferObject {

    public static final String JSON_PROPERTY_COMMAND_FORMAT = "commandFormat";
    private String commandFormat;

    public static final String JSON_PROPERTY_PARAMETERS_SCHEMA = "parametersSchema";
    private AbstractDataComponent parametersSchema;

    public static final String JSON_PROPERTY_RESULT_SCHEMA = "resultSchema";
    private AbstractDataComponent resultSchema;

    public static final String JSON_PROPERTY_FEASIBILITY_RESULT_SCHEMA = "feasibilityResultSchema";
    private AbstractDataComponent feasibilityResultSchema;

    public static final String JSON_PROPERTY_RECORD_SCHEMA = "recordSchema";
    private AbstractDataComponent recordSchema;

    public static final String JSON_PROPERTY_ENCODING = "encoding";
    private AbstractEncoding encoding;

    public static final String JSON_PROPERTY_MESSAGE_SCHEMA = "messageSchema";
    private JsonNode messageSchema;

    public static final String JSON_PROPERTY_ANY = "any";
    private JsonNode any;

    /**
     * Media type discriminating the format of this stream's commands, e.g. {@code
     * application/json}, {@code application/swe+json}, {@code application/swe+text}, {@code
     * application/swe+csv}, {@code application/swe+binary}, {@code application/x-protobuf}, or
     * other implementation-defined values. (required)
     */
    @JsonProperty(JSON_PROPERTY_COMMAND_FORMAT)
    public String getCommandFormat() {
        return commandFormat;
    }

    @JsonProperty(JSON_PROPERTY_COMMAND_FORMAT)
    public void setCommandFormat(String commandFormat) {
        this.commandFormat = commandFormat;
    }

    public CommandSchema commandFormat(String commandFormat) {
        setCommandFormat(commandFormat);
        return this;
    }

    /**
     * Schema of the command parameters. Used when {@link #getCommandFormat()} is {@code
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

    public CommandSchema parametersSchema(AbstractDataComponent parametersSchema) {
        setParametersSchema(parametersSchema);
        return this;
    }

    /**
     * Optional inline schema of the command result. Used when {@link #getCommandFormat()} is
     * {@code application/json}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_SCHEMA)
    public AbstractDataComponent getResultSchema() {
        return resultSchema;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_SCHEMA)
    public void setResultSchema(AbstractDataComponent resultSchema) {
        this.resultSchema = resultSchema;
    }

    public CommandSchema resultSchema(AbstractDataComponent resultSchema) {
        setResultSchema(resultSchema);
        return this;
    }

    /**
     * Schema of the command feasibility result. Used when {@link #getCommandFormat()} is {@code
     * application/json}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FEASIBILITY_RESULT_SCHEMA)
    public AbstractDataComponent getFeasibilityResultSchema() {
        return feasibilityResultSchema;
    }

    @JsonProperty(JSON_PROPERTY_FEASIBILITY_RESULT_SCHEMA)
    public void setFeasibilityResultSchema(AbstractDataComponent feasibilityResultSchema) {
        this.feasibilityResultSchema = feasibilityResultSchema;
    }

    public CommandSchema feasibilityResultSchema(AbstractDataComponent feasibilityResultSchema) {
        setFeasibilityResultSchema(feasibilityResultSchema);
        return this;
    }

    /**
     * Schema of the command record. Used when {@link #getCommandFormat()} is one of the {@code
     * application/swe+*} variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RECORD_SCHEMA)
    public AbstractDataComponent getRecordSchema() {
        return recordSchema;
    }

    @JsonProperty(JSON_PROPERTY_RECORD_SCHEMA)
    public void setRecordSchema(AbstractDataComponent recordSchema) {
        this.recordSchema = recordSchema;
    }

    public CommandSchema recordSchema(AbstractDataComponent recordSchema) {
        setRecordSchema(recordSchema);
        return this;
    }

    /**
     * Encoding used to serialize {@link #getRecordSchema()}. Used when {@link
     * #getCommandFormat()} is one of the {@code application/swe+*} variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ENCODING)
    public AbstractEncoding getEncoding() {
        return encoding;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    public void setEncoding(AbstractEncoding encoding) {
        this.encoding = encoding;
    }

    public CommandSchema encoding(AbstractEncoding encoding) {
        setEncoding(encoding);
        return this;
    }

    /**
     * Schema of the encoded message, either a media type string or a link to an external schema
     * document. Used when {@link #getCommandFormat()} is {@code application/x-protobuf}.
     * (optional)
     */
    @JsonProperty(JSON_PROPERTY_MESSAGE_SCHEMA)
    public JsonNode getMessageSchema() {
        return messageSchema;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE_SCHEMA)
    public void setMessageSchema(JsonNode messageSchema) {
        this.messageSchema = messageSchema;
    }

    public CommandSchema messageSchema(JsonNode messageSchema) {
        setMessageSchema(messageSchema);
        return this;
    }

    /**
     * Catch-all for schema content associated with a {@link #getCommandFormat()} value not
     * otherwise modeled above. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ANY)
    public JsonNode getAny() {
        return any;
    }

    @JsonProperty(JSON_PROPERTY_ANY)
    public void setAny(JsonNode any) {
        this.any = any;
    }

    public CommandSchema any(JsonNode any) {
        setAny(any);
        return this;
    }

}
