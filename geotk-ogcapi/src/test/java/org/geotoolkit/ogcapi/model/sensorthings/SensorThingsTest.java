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
package org.geotoolkit.ogcapi.model.sensorthings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.sensorthings.Datastream;
import org.geotoolkit.ogcapi.dto.sensorthings.EntityList;
import org.geotoolkit.ogcapi.dto.sensorthings.Observation;
import org.geotoolkit.ogcapi.dto.sensorthings.Thing;
import org.geotoolkit.ogcapi.dto.sensorthings.UnitOfMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class SensorThingsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testThing() throws JsonProcessingException {
        final Thing thing = new Thing()
                .name("Weather station")
                .description("A rooftop weather station");

        final String json = mapper.writeValueAsString(thing);
        Assertions.assertFalse(json.contains("@iot.id"), "null id must not be serialized");
        Assertions.assertFalse(json.contains("properties"), "null properties must not be serialized");

        final Thing read = mapper.readValue(json, Thing.class);
        Assertions.assertEquals(thing, read);
    }

    @Test
    public void testDatastream() throws JsonProcessingException {
        final Datastream datastream = new Datastream()
                .name("Air temperature")
                .description("Air temperature readings")
                .unitOfMeasurement(new UnitOfMeasurement()
                        .name("Degree Celsius")
                        .symbol("degC")
                        .definition("http://qudt.org/vocab/unit/DEG_C"))
                .observationType("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement")
                .thing(new Thing().id(1));

        final String json = mapper.writeValueAsString(datastream);
        Assertions.assertTrue(json.contains("\"unitOfMeasurement\""));
        // linking by id only must not serialize the other Thing fields
        Assertions.assertFalse(json.contains("\"name\":null"));

        final Datastream read = mapper.readValue(json, Datastream.class);
        Assertions.assertEquals(datastream, read);
        Assertions.assertEquals(1, read.getThing().getId());
    }

    @Test
    public void testObservationPolymorphicResult() throws JsonProcessingException {
        final Observation observation = new Observation()
                .phenomenonTime("2026-07-23T10:00:00Z")
                .result(21.5)
                .datastream(new Datastream().id(1));

        final String json = mapper.writeValueAsString(observation);
        final Observation read = mapper.readValue(json, Observation.class);
        Assertions.assertEquals(observation, read);
        Assertions.assertEquals(21.5, ((Number) read.getResult()).doubleValue());

        // result may also be an arbitrary JSON object (complex observation type)
        final Observation complex = new Observation()
                .phenomenonTime("2026-07-23T10:00:00Z")
                .result(Map.of("value", 21.5, "flag", "ok"));
        final Observation complexRead = mapper.readValue(mapper.writeValueAsString(complex), Observation.class);
        Assertions.assertEquals(complex, complexRead);
    }

    @Test
    public void testEntityListPaging() throws JsonProcessingException {
        final EntityList<Thing> list = new EntityList<Thing>()
                .addValueItem(new Thing().name("A").description("First").id(1))
                .addValueItem(new Thing().name("B").description("Second").id(2))
                .count(2)
                .nextLink("http://example.org/v1.1/Things?$skip=2");

        final String json = mapper.writeValueAsString(list);
        Assertions.assertTrue(json.contains("\"@iot.count\":2"));

        final EntityList<Thing> read = mapper.readValue(json, new TypeReference<EntityList<Thing>>() {});
        Assertions.assertEquals(list, read);
        Assertions.assertEquals(2, read.getValue().size());
    }
}
