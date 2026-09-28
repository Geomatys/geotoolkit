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
package org.geotoolkit.ogcapi.storage;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.ogcapi.dto.processes.Execute;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.ogcapi.dto.processes.ResponseType;
import org.geotoolkit.ogcapi.dto.processes.TransmissionMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test the mapping between OGC API - Processes documents and the process API.
 * No request is sent to the server.
 *
 * @author Johann Sorel (Geomatys)
 */
public class OgcApiProcessingRegistryTest {

    private static final String DESCRIPTION = """
        {
          "id": "sample",
          "title": "Sample",
          "description": "Sample process",
          "version": "1.0.0",
          "jobControlOptions": ["sync-execute"],
          "inputs": {
            "mode":   { "title": "Mode", "schema": { "type": "string", "enum": ["fast", "slow"] } },
            "factor": { "schema": { "type": "number" }, "minOccurs": 0 },
            "counts": { "schema": { "type": "integer" }, "minOccurs": 0, "maxOccurs": "unbounded" },
            "flag":   { "schema": { "type": "boolean" }, "minOccurs": 0 },
            "area":   { "schema": { "type": "object", "format": "ogc-bbox" }, "minOccurs": 0 }
          },
          "outputs": {
            "result": { "schema": { "type": "number" } },
            "link":   { "schema": { } },
            "extent": { "schema": { "type": "object", "format": "ogc-bbox" } }
          }
        }
        """;

    private OgcApiProcessingRegistry registry;
    private OgcApiProcessDescriptor descriptor;

    @BeforeEach
    public void setUp() throws Exception {
        final OpenApiConfiguration config = OpenApiConfiguration.builder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(8080)
                .setBasePath("ogcapi")
                .build();
        registry = new OgcApiProcessingRegistry(config);
        final ProcessDescription description = OpenApiConfiguration.createDefaultObjectMapper()
                .readValue(DESCRIPTION, ProcessDescription.class);
        descriptor = OgcApiProcessDescriptor.create(registry, description);
    }

    @AfterEach
    public void tearDown() throws Exception {
        registry.close();
    }

    /**
     * Test JSON schemas are mapped to parameter descriptors.
     */
    @Test
    public void testDescriptor() {
        assertEquals("sample", descriptor.getIdentifier().getCode());
        assertEquals("Sample", descriptor.getDisplayName().toString());
        assertEquals("Sample process", descriptor.getProcedureDescription().toString());
        assertEquals("1.0.0", descriptor.getVersion());

        final ParameterDescriptor<?> mode = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("mode");
        assertEquals(String.class, mode.getValueClass());
        assertEquals(1, mode.getMinimumOccurs());
        assertEquals(1, mode.getMaximumOccurs());
        assertEquals(Set.of("fast", "slow"), mode.getValidValues());

        final ParameterDescriptor<?> factor = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("factor");
        assertEquals(Double.class, factor.getValueClass());
        assertEquals(0, factor.getMinimumOccurs());

        final ParameterDescriptor<?> counts = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("counts");
        assertEquals(Integer.class, counts.getValueClass());
        assertEquals(Integer.MAX_VALUE, counts.getMaximumOccurs());

        final ParameterDescriptor<?> flag = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("flag");
        assertEquals(Boolean.class, flag.getValueClass());

        final ParameterDescriptor<?> area = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("area");
        assertEquals(Envelope.class, area.getValueClass());

        assertEquals(3, descriptor.getOutputDescriptor().descriptors().size());
        final ParameterDescriptor<?> result = (ParameterDescriptor<?>) descriptor.getOutputDescriptor().descriptor("result");
        assertEquals(Double.class, result.getValueClass());
        final ParameterDescriptor<?> link = (ParameterDescriptor<?>) descriptor.getOutputDescriptor().descriptor("link");
        assertEquals(Object.class, link.getValueClass());
    }

    /**
     * Test the execute request body built from input parameters.
     */
    @Test
    public void testToExecute() throws Exception {
        final ParameterValueGroup input = descriptor.getInputDescriptor().createValue();
        input.parameter("mode").setValue("fast");
        final ParameterDescriptor<?> countsDesc = (ParameterDescriptor<?>) descriptor.getInputDescriptor().descriptor("counts");
        for (int i = 1; i <= 2; i++) {
            final ParameterValue<?> count = countsDesc.createValue();
            count.setValue(i);
            input.values().add(count);
        }
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, 0, 10);
        env.setRange(1, 1, 11);
        input.parameter("area").setValue(env);

        final OgcApiProcess process = new OgcApiProcess(registry, descriptor, input);
        final Execute execute = process.toExecute();

        final Map<String, Object> inputs = execute.getInputs();
        assertEquals("fast", inputs.get("mode"));
        assertEquals(List.of(1, 2), inputs.get("counts"));
        assertEquals(Map.of("bbox", List.of(0.0, 1.0, 10.0, 11.0)), inputs.get("area"));
        assertFalse(inputs.containsKey("factor"));
        assertFalse(inputs.containsKey("flag"));

        assertEquals(Set.of("result", "link", "extent"), execute.getOutputs().keySet());
        assertEquals(TransmissionMode.VALUE, execute.getOutputs().get("result").getTransmissionMode());
        assertEquals(ResponseType.DOCUMENT, execute.getResponse());
    }

    /**
     * Test output parameters are filled from a results document.
     */
    @Test
    public void testFillOutputs() throws Exception {
        final ParameterValueGroup input = descriptor.getInputDescriptor().createValue();
        input.parameter("mode").setValue("slow");
        final OgcApiProcess process = new OgcApiProcess(registry, descriptor, input);

        final ParameterValueGroup output = process.fillOutputs(Map.of(
                "result", Map.of("value", 3, "mediaType", "application/json"),
                "link", Map.of("href", "http://example.com/result.tiff"),
                "extent", Map.of("bbox", List.of(-10, -20, 10, 20))));

        assertEquals(3.0, output.parameter("result").getValue());
        assertEquals(URI.create("http://example.com/result.tiff"), output.parameter("link").getValue());

        final Envelope extent = assertInstanceOf(Envelope.class, output.parameter("extent").getValue());
        assertEquals(-10.0, extent.getMinimum(0));
        assertEquals(-20.0, extent.getMinimum(1));
        assertEquals(10.0, extent.getMaximum(0));
        assertEquals(20.0, extent.getMaximum(1));
    }

    /**
     * Test missing outputs are left unset.
     */
    @Test
    public void testFillPartialOutputs() throws Exception {
        final ParameterValueGroup input = descriptor.getInputDescriptor().createValue();
        input.parameter("mode").setValue("slow");
        final OgcApiProcess process = new OgcApiProcess(registry, descriptor, input);

        final ParameterValueGroup output = process.fillOutputs(Map.of("result", 1.5));
        assertEquals(1.5, output.parameter("result").getValue());
        assertNull(output.parameter("link").getValue());
    }
}
