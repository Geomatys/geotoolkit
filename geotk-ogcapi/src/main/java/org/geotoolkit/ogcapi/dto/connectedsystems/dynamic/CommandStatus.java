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
 * A status report for a {@link Command}.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class CommandStatus extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_COMMAND_ID = "command@id";
    private String commandId;

    public static final String JSON_PROPERTY_REPORT_TIME = "reportTime";
    private String reportTime;

    public static final String JSON_PROPERTY_STATUS_CODE = "statusCode";
    private CommandStatusCode statusCode;

    public static final String JSON_PROPERTY_PERCENT_COMPLETION = "percentCompletion";
    private Double percentCompletion;

    public static final String JSON_PROPERTY_EXECUTION_TIME = "executionTime";
    private TimePeriod executionTime;

    public static final String JSON_PROPERTY_MESSAGE = "message";
    private String message;

    public static final String JSON_PROPERTY_RESULTS = "results";
    private List<CommandResult> results;

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

    public CommandStatus id(String id) {
        setId(id);
        return this;
    }

    /**
     * Identifier of the command this status report is about. (required)
     */
    @JsonProperty(JSON_PROPERTY_COMMAND_ID)
    public String getCommandId() {
        return commandId;
    }

    @JsonProperty(JSON_PROPERTY_COMMAND_ID)
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public CommandStatus commandId(String commandId) {
        setCommandId(commandId);
        return this;
    }

    /**
     * Time at which this status report was generated, as an ISO 8601 date-time. (required)
     */
    @JsonProperty(JSON_PROPERTY_REPORT_TIME)
    public String getReportTime() {
        return reportTime;
    }

    @JsonProperty(JSON_PROPERTY_REPORT_TIME)
    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public CommandStatus reportTime(String reportTime) {
        setReportTime(reportTime);
        return this;
    }

    /**
     * Status code of the command. (required)
     */
    @JsonProperty(JSON_PROPERTY_STATUS_CODE)
    public CommandStatusCode getStatusCode() {
        return statusCode;
    }

    @JsonProperty(JSON_PROPERTY_STATUS_CODE)
    public void setStatusCode(CommandStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public CommandStatus statusCode(CommandStatusCode statusCode) {
        setStatusCode(statusCode);
        return this;
    }

    /**
     * Estimated completion percentage, between 0 and 100. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PERCENT_COMPLETION)
    public Double getPercentCompletion() {
        return percentCompletion;
    }

    @JsonProperty(JSON_PROPERTY_PERCENT_COMPLETION)
    public void setPercentCompletion(Double percentCompletion) {
        this.percentCompletion = percentCompletion;
    }

    public CommandStatus percentCompletion(Double percentCompletion) {
        setPercentCompletion(percentCompletion);
        return this;
    }

    /**
     * Time period during which the command was/is expected to execute. (optional)
     */
    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public TimePeriod getExecutionTime() {
        return executionTime;
    }

    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public void setExecutionTime(TimePeriod executionTime) {
        this.executionTime = executionTime;
    }

    public CommandStatus executionTime(TimePeriod executionTime) {
        setExecutionTime(executionTime);
        return this;
    }

    /**
     * Human readable message providing more detail about this status report. (optional)
     */
    @JsonProperty(JSON_PROPERTY_MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(JSON_PROPERTY_MESSAGE)
    public void setMessage(String message) {
        this.message = message;
    }

    public CommandStatus message(String message) {
        setMessage(message);
        return this;
    }

    /**
     * Results produced so far for the command. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULTS)
    public List<CommandResult> getResults() {
        return results;
    }

    @JsonProperty(JSON_PROPERTY_RESULTS)
    public void setResults(List<CommandResult> results) {
        this.results = results;
    }

    public CommandStatus results(List<CommandResult> results) {
        setResults(results);
        return this;
    }

}
