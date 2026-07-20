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
package org.geotoolkit.ogcapi.model.processes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.processes.Execute;
import org.geotoolkit.ogcapi.dto.processes.InputDescription;
import org.geotoolkit.ogcapi.dto.processes.JobControlOption;
import org.geotoolkit.ogcapi.dto.processes.OutputDescription;
import org.geotoolkit.ogcapi.dto.processes.OutputSelection;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.ogcapi.dto.processes.ProcessSummary;
import org.geotoolkit.ogcapi.dto.processes.ResponseType;
import org.geotoolkit.ogcapi.dto.processes.StatusCode;
import org.geotoolkit.ogcapi.dto.processes.StatusInfo;
import org.geotoolkit.ogcapi.dto.processes.Subscriber;
import org.geotoolkit.ogcapi.dto.processes.TransmissionMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcessesTest {

    /**
     * Check null fields are not written.
     */
    @Test
    public void testNullValueAreNotWritten() throws JsonProcessingException {
        final ProcessSummary summary = new ProcessSummary();
        final String json = new ObjectMapper().writeValueAsString(summary);
        Assertions.assertEquals("{\"id\":null,\"version\":null,\"links\":[]}", json);
    }

    /**
     * Serialize then deserialize a {@link ProcessDescription} with nested inputs/outputs, check
     * round-trip equality.
     */
    @Test
    public void testProcessDescriptionRoundTrip() throws JsonProcessingException {
        final InputDescription input = new InputDescription()
                .title("Buffer distance")
                .minOccurs(1)
                .maxOccurs(1)
                .valuePassing(List.of("byValue"));
        final OutputDescription output = new OutputDescription()
                .title("Buffered geometry")
                .minOccurs(1)
                .maxOccurs(1);

        final ProcessDescription process = new ProcessDescription()
                .putInputsItem("distance", input)
                .putOutputsItem("result", output);
        process.setTitle("Buffer");
        process.setDescription("Buffers a geometry by a distance");
        process.setJobControlOptions(List.of(JobControlOption.SYNC_EXECUTE, JobControlOption.ASYNC_EXECUTE));
        process.setOutputTransmission(List.of(TransmissionMode.VALUE, TransmissionMode.REFERENCE));
        process.setId("buffer");
        process.setVersion("1.0.0");

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(process);
        final ProcessDescription roundTripped = mapper.readValue(json, ProcessDescription.class);
        Assertions.assertEquals(process, roundTripped);
    }

    /**
     * Serialize then deserialize an {@link Execute} request body with per-output selection and a
     * subscriber, check round-trip equality.
     */
    @Test
    public void testExecuteRoundTrip() throws JsonProcessingException {
        final OutputSelection selection = new OutputSelection().transmissionMode(TransmissionMode.REFERENCE);
        final Subscriber subscriber = new Subscriber()
                .successUri("https://example.com/success")
                .failedUri("https://example.com/failed");

        final Execute execute = new Execute()
                .putInputsItem("distance", 10.0)
                .putOutputsItem("result", selection)
                .response(ResponseType.DOCUMENT)
                .subscriber(subscriber);

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(execute);
        final Execute roundTripped = mapper.readValue(json, Execute.class);
        Assertions.assertEquals(execute, roundTripped);
    }

    /**
     * Serialize then deserialize a {@link StatusInfo}, check round-trip equality and that the
     * standard's {@code jobID}/{@code processID}/{@code type} property names are used on the
     * wire.
     */
    @Test
    public void testStatusInfoRoundTrip() throws JsonProcessingException {
        final StatusInfo status = new StatusInfo()
                .processID("buffer")
                .jobID("123e4567")
                .status(StatusCode.RUNNING)
                .progress(42);

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(status);
        Assertions.assertTrue(json.contains("\"jobID\":\"123e4567\""));
        Assertions.assertTrue(json.contains("\"processID\":\"buffer\""));
        Assertions.assertTrue(json.contains("\"type\":\"process\""));

        final StatusInfo roundTripped = mapper.readValue(json, StatusInfo.class);
        Assertions.assertEquals(status, roundTripped);
    }

}
