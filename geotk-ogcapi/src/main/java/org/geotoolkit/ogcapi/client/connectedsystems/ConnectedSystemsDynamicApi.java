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
package org.geotoolkit.ogcapi.client.connectedsystems;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.Command;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandResult;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandResultCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandSchema;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandStatus;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.CommandStatusCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.ControlStream;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.ControlStreamCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.DataStream;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.DataStreamCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.Observation;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.ObservationCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.ObservationSchema;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.SystemEvent;
import org.geotoolkit.ogcapi.dto.connectedsystems.dynamic.SystemEventCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.System;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.SystemCollection;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemHistoryRevision;
import org.geotoolkit.ogcapi.request.connectedsystems.GetAllSystemEvents;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResults;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatuses;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommands;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreams;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreams;
import org.geotoolkit.ogcapi.request.connectedsystems.GetObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.GetObservations;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvents;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistory;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistoryRevision;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemHistoryRevision;

/**
 * OGC API - Connected Systems - Part 2: Dynamic Data (DataStreams, Observations, ControlStreams,
 * Commands, CommandStatus, CommandResult, SystemEvents, System History).
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ConnectedSystemsDynamicApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_CASCADE             = "cascade";
    public static final String PARAM_CMD_FORMAT          = "cmdFormat";
    public static final String PARAM_CONTROLLED_PROPERTY = "controlledProperty";
    public static final String PARAM_DATA_STREAM         = "dataStream";
    public static final String PARAM_DATETIME            = "datetime";
    public static final String PARAM_DRY_RUN             = "dry-run";
    public static final String PARAM_EVENT_TYPE          = "eventType";
    public static final String PARAM_EXECUTION_TIME      = "executionTime";
    public static final String PARAM_FOI                 = "foi";
    public static final String PARAM_ID                  = "id";
    public static final String PARAM_ISSUE_TIME          = "issueTime";
    public static final String PARAM_LIMIT               = "limit";
    public static final String PARAM_OBSERVED_PROPERTY   = "observedProperty";
    public static final String PARAM_OBS_FORMAT          = "obsFormat";
    public static final String PARAM_PHENOMENON_TIME     = "phenomenonTime";
    public static final String PARAM_Q                   = "q";
    public static final String PARAM_RESULT_TIME         = "resultTime";
    public static final String PARAM_SENDER              = "sender";
    public static final String PARAM_STATUS_CODE         = "statusCode";
    public static final String PARAM_SYSTEM              = "system";
    public static final String PARAM_VALID_TIME          = "validTime";

    /**
     * Path fragments.
     */
    public static final String PATH_COMMANDS       = "/commands";
    public static final String PATH_CONTROLSTREAMS = "/controlstreams";
    public static final String PATH_DATASTREAMS    = "/datastreams";
    public static final String PATH_EVENTS         = "/events";
    public static final String PATH_HISTORY        = "/history";
    public static final String PATH_OBSERVATIONS   = "/observations";
    public static final String PATH_RESULT         = "/result";
    public static final String PATH_SCHEMA         = "/schema";
    public static final String PATH_STATUS         = "/status";
    public static final String PATH_SYSTEMS        = "/systems";
    public static final String PATH_SYSTEM_EVENTS  = "/systemEvents";

    private static final String ACCEPT_JSON = "application/json, text/html";

    public ConnectedSystemsDynamicApi(OpenApiConfiguration config) {
        super(config);
    }

    // ------------------------------------------------------------------
    // DataStreams
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<DataStreamCollection>> getDataStreams(GetDataStreams parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getQ()));
        queryParams.addAll(toPairs(PARAM_PHENOMENON_TIME, parameters.getPhenomenonTime()));
        queryParams.addAll(toPairs(PARAM_RESULT_TIME, parameters.getResultTime()));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, parameters.getSystem()));
        queryParams.addAll(toPairs("csv", PARAM_FOI, parameters.getFoi()));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, parameters.getObservedProperty()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DataStreamCollection.class);
    }

    public ServiceRequest<Void> createDataStream(CreateDataStream parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling createDataStream");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createDataStream");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_DATASTREAMS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<DataStream>> getDataStream(GetDataStream parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling getDataStream");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DataStream.class);
    }

    public ServiceRequest<Void> replaceDataStream(ReplaceDataStream parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling replaceDataStream");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceDataStream");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteDataStream(DeleteDataStream parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling deleteDataStream");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()), toPairs(PARAM_CASCADE, parameters.getCascade())));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<ObservationSchema>> getDataStreamSchema(GetDataStreamSchema parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling getDataStreamSchema");
        }
        if (parameters.getObsFormat() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'obsFormat' when calling getDataStreamSchema");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()) + PATH_SCHEMA, toPairs(PARAM_OBS_FORMAT, parameters.getObsFormat())));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ObservationSchema.class);
    }

    public ServiceRequest<Void> replaceDataStreamSchema(ReplaceDataStreamSchema parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling replaceDataStreamSchema");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceDataStreamSchema");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()) + PATH_SCHEMA, null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Observations
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<ObservationCollection>> getObservations(GetObservations parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getQ()));
        queryParams.addAll(toPairs(PARAM_PHENOMENON_TIME, parameters.getPhenomenonTime()));
        queryParams.addAll(toPairs(PARAM_RESULT_TIME, parameters.getResultTime()));
        queryParams.addAll(toPairs("csv", PARAM_DATA_STREAM, parameters.getDataStream()));
        queryParams.addAll(toPairs("csv", PARAM_FOI, parameters.getFoi()));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, parameters.getObservedProperty()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_OBSERVATIONS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ObservationCollection.class);
    }

    public ServiceRequest<Void> createObservation(CreateObservation parameters) throws IllegalArgumentException {
        if (parameters.getDataStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dataStreamId' when calling createObservation");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createObservation");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DATASTREAMS + '/' + urlEncode(parameters.getDataStreamId()) + PATH_OBSERVATIONS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<Observation>> getObservation(GetObservation parameters) throws IllegalArgumentException {
        if (parameters.getObsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'obsId' when calling getObservation");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_OBSERVATIONS + '/' + urlEncode(parameters.getObsId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Observation.class);
    }

    public ServiceRequest<Void> replaceObservation(ReplaceObservation parameters) throws IllegalArgumentException {
        if (parameters.getObsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'obsId' when calling replaceObservation");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceObservation");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_OBSERVATIONS + '/' + urlEncode(parameters.getObsId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteObservation(DeleteObservation parameters) throws IllegalArgumentException {
        if (parameters.getObsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'obsId' when calling deleteObservation");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_OBSERVATIONS + '/' + urlEncode(parameters.getObsId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // ControlStreams
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<ControlStreamCollection>> getControlStreams(GetControlStreams parameters) {
        final List<Pair> queryParams = controlFilterPairs(parameters.getId(), parameters.getQ(), parameters.getIssueTime(),
                parameters.getExecutionTime(), parameters.getStatusCode(), parameters.getSender(), parameters.getSystem(),
                parameters.getFoi(), parameters.getControlledProperty(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ControlStreamCollection.class);
    }

    public ServiceRequest<Void> createControlStream(CreateControlStream parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling createControlStream");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createControlStream");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_CONTROLSTREAMS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<ControlStream>> getControlStream(GetControlStream parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling getControlStream");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ControlStream.class);
    }

    public ServiceRequest<Void> replaceControlStream(ReplaceControlStream parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling replaceControlStream");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceControlStream");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteControlStream(DeleteControlStream parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling deleteControlStream");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()), toPairs(PARAM_CASCADE, parameters.getCascade())));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<CommandSchema>> getControlStreamSchema(GetControlStreamSchema parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling getControlStreamSchema");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()) + PATH_SCHEMA, toPairs(PARAM_CMD_FORMAT, parameters.getCmdFormat())));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandSchema.class);
    }

    public ServiceRequest<Void> replaceControlStreamSchema(ReplaceControlStreamSchema parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling replaceControlStreamSchema");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceControlStreamSchema");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()) + PATH_SCHEMA, null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<CommandCollection>> getCommands(GetCommands parameters) {
        final List<Pair> queryParams = controlFilterPairs(parameters.getId(), parameters.getQ(), parameters.getIssueTime(),
                parameters.getExecutionTime(), parameters.getStatusCode(), parameters.getSender(), parameters.getSystem(),
                parameters.getFoi(), parameters.getControlledProperty(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandCollection.class);
    }

    public ServiceRequest<Void> createCommand(CreateCommand parameters) throws IllegalArgumentException {
        if (parameters.getControlStreamId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'controlStreamId' when calling createCommand");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createCommand");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONTROLSTREAMS + '/' + urlEncode(parameters.getControlStreamId()) + PATH_COMMANDS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<Command>> getCommand(GetCommand parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling getCommand");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Command.class);
    }

    public ServiceRequest<Void> replaceCommand(ReplaceCommand parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling replaceCommand");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceCommand");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteCommand(DeleteCommand parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling deleteCommand");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Command Status
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<CommandStatusCollection>> getCommandStatuses(GetCommandStatuses parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling getCommandStatuses");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_STATUS, null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandStatusCollection.class);
    }

    public ServiceRequest<Void> createCommandStatus(CreateCommandStatus parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling createCommandStatus");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createCommandStatus");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_STATUS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<CommandStatus>> getCommandStatus(GetCommandStatus parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling getCommandStatus");
        }
        if (parameters.getStatusId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'statusId' when calling getCommandStatus");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_STATUS + '/' + urlEncode(parameters.getStatusId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandStatus.class);
    }

    public ServiceRequest<Void> replaceCommandStatus(ReplaceCommandStatus parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling replaceCommandStatus");
        }
        if (parameters.getStatusId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'statusId' when calling replaceCommandStatus");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceCommandStatus");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_STATUS + '/' + urlEncode(parameters.getStatusId()), null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteCommandStatus(DeleteCommandStatus parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling deleteCommandStatus");
        }
        if (parameters.getStatusId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'statusId' when calling deleteCommandStatus");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_STATUS + '/' + urlEncode(parameters.getStatusId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Command Result
    // ------------------------------------------------------------------

    public ServiceRequest<Supplier<CommandResultCollection>> getCommandResults(GetCommandResults parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling getCommandResults");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_RESULT, null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandResultCollection.class);
    }

    public ServiceRequest<Void> createCommandResult(CreateCommandResult parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling createCommandResult");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createCommandResult");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_RESULT, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<CommandResult>> getCommandResult(GetCommandResult parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling getCommandResult");
        }
        if (parameters.getResultId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'resultId' when calling getCommandResult");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_RESULT + '/' + urlEncode(parameters.getResultId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CommandResult.class);
    }

    public ServiceRequest<Void> replaceCommandResult(ReplaceCommandResult parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling replaceCommandResult");
        }
        if (parameters.getResultId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'resultId' when calling replaceCommandResult");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceCommandResult");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_RESULT + '/' + urlEncode(parameters.getResultId()), null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteCommandResult(DeleteCommandResult parameters) throws IllegalArgumentException {
        if (parameters.getCmdId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'cmdId' when calling deleteCommandResult");
        }
        if (parameters.getResultId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'resultId' when calling deleteCommandResult");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COMMANDS + '/' + urlEncode(parameters.getCmdId()) + PATH_RESULT + '/' + urlEncode(parameters.getResultId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // System Events
    // ------------------------------------------------------------------

    /**
     * Fetch SystemEvent resources across all systems.
     */
    public ServiceRequest<Supplier<SystemEventCollection>> getAllSystemEvents(GetAllSystemEvents parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getEventTime()));
        queryParams.addAll(toPairs("csv", PARAM_EVENT_TYPE, parameters.getEventType()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getQ()));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, parameters.getSystem()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEM_EVENTS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemEventCollection.class);
    }

    public ServiceRequest<Supplier<SystemEventCollection>> getSystemEvents(GetSystemEvents parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemEvents");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getEventTime()));
        queryParams.addAll(toPairs("csv", PARAM_EVENT_TYPE, parameters.getEventType()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getQ()));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, parameters.getSystem()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_EVENTS, queryParams));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemEventCollection.class);
    }

    public ServiceRequest<Void> createSystemEvent(CreateSystemEvent parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling createSystemEvent");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createSystemEvent");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_EVENTS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Supplier<SystemEvent>> getSystemEvent(GetSystemEvent parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemEvent");
        }
        if (parameters.getEventId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'eventId' when calling getSystemEvent");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_EVENTS + '/' + urlEncode(parameters.getEventId()), null));
        request.header("Accept", ACCEPT_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemEvent.class);
    }

    public ServiceRequest<Void> replaceSystemEvent(ReplaceSystemEvent parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling replaceSystemEvent");
        }
        if (parameters.getEventId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'eventId' when calling replaceSystemEvent");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceSystemEvent");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_EVENTS + '/' + urlEncode(parameters.getEventId()), null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteSystemEvent(DeleteSystemEvent parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling deleteSystemEvent");
        }
        if (parameters.getEventId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'eventId' when calling deleteSystemEvent");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_EVENTS + '/' + urlEncode(parameters.getEventId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // System History
    // ------------------------------------------------------------------

    /**
     * Fetch the historical revisions of a System resource, as a collection of past System
     * representations.
     */
    public ServiceRequest<Supplier<SystemCollection>> getSystemHistory(GetSystemHistory parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemHistory");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_VALID_TIME, parameters.getValidTime()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getQ()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_HISTORY, queryParams));
        request.header("Accept", "application/geo+json, application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemCollection.class);
    }

    /**
     * Fetch a single historical revision of a System resource.
     */
    public ServiceRequest<Supplier<System>> getSystemHistoryRevision(GetSystemHistoryRevision parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemHistoryRevision");
        }
        if (parameters.getRevId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'revId' when calling getSystemHistoryRevision");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_HISTORY + '/' + urlEncode(parameters.getRevId()), null));
        request.header("Accept", "application/geo+json, application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, System.class);
    }

    public ServiceRequest<Void> replaceSystemHistoryRevision(ReplaceSystemHistoryRevision parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling replaceSystemHistoryRevision");
        }
        if (parameters.getRevId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'revId' when calling replaceSystemHistoryRevision");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceSystemHistoryRevision");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_HISTORY + '/' + urlEncode(parameters.getRevId()), null));
        request.header("Content-Type", parameters.getContentType());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    public ServiceRequest<Void> deleteSystemHistoryRevision(DeleteSystemHistoryRevision parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling deleteSystemHistoryRevision");
        }
        if (parameters.getRevId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'revId' when calling deleteSystemHistoryRevision");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_HISTORY + '/' + urlEncode(parameters.getRevId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private List<Pair> controlFilterPairs(List<String> id, List<String> q, String issueTime, String executionTime,
            List<String> statusCode, List<String> sender, List<String> system, List<String> foi,
            List<String> controlledProperty, Integer limit) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, id));
        queryParams.addAll(toPairs("csv", PARAM_Q, q));
        queryParams.addAll(toPairs(PARAM_ISSUE_TIME, issueTime));
        queryParams.addAll(toPairs(PARAM_EXECUTION_TIME, executionTime));
        queryParams.addAll(toPairs("csv", PARAM_STATUS_CODE, statusCode));
        queryParams.addAll(toPairs("csv", PARAM_SENDER, sender));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, system));
        queryParams.addAll(toPairs("csv", PARAM_FOI, foi));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, controlledProperty));
        queryParams.addAll(toPairs(PARAM_LIMIT, limit));
        return queryParams;
    }

    private static void requireContent(byte[] content, String contentType, String callingMethod) {
        if (content == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling " + callingMethod);
        }
        if (contentType == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling " + callingMethod);
        }
    }

    private static void setPrefer(HttpRequest.Builder request, org.geotoolkit.ogcapi.request.PreferHandling handling) {
        if (handling != null) {
            request.header("Prefer", handling.headerValue);
        }
    }

}
