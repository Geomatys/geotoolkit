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
package org.geotoolkit.openeo.collection.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.openeo.dto.collection.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link Collection}, now extending the OGC API - Common
 * {@code CollectionDescription}, still deserializes a real OpenEO/STAC collection document
 * into both its inherited fields (id, title, extent, ...) and its STAC-specific fields
 * (stac_version, summaries, assets).
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CollectionTest {

    private static final String SAMPLE_JSON = """
        {
          "stac_version": "1.0.0",
          "id": "SENTINEL2_L2A",
          "title": "Sentinel-2 L2A",
          "description": "Sentinel-2 Level 2A data",
          "keywords": ["sentinel", "optical"],
          "links": [
            {"rel": "self", "href": "https://example.org/collections/SENTINEL2_L2A"}
          ],
          "extent": {
            "spatial": {"bbox": [[-180, -56, 180, 83]]},
            "temporal": {"interval": [["2015-06-23T00:00:00Z", null]]}
          },
          "summaries": {"eo:bands": ["B01", "B02"]},
          "assets": {"thumbnail": {"href": "https://example.org/thumb.png"}}
        }
        """;

    @Test
    public void deserializesInheritedAndOwnFields() throws Exception {
        final ObjectMapper mapper = OpenApiConfiguration.createDefaultObjectMapper();
        final Collection collection = mapper.readValue(SAMPLE_JSON, Collection.class);

        Assertions.assertEquals("SENTINEL2_L2A", collection.getId());
        Assertions.assertEquals("Sentinel-2 L2A", collection.getTitle());
        Assertions.assertEquals("Sentinel-2 Level 2A data", collection.getDescription());
        Assertions.assertEquals(1, collection.getLinks().size());
        Assertions.assertEquals("self", collection.getLinks().get(0).getRel());

        Assertions.assertNotNull(collection.getExtent());
        Assertions.assertNotNull(collection.getExtent().getSpatial());
        Assertions.assertArrayEquals(new double[]{-180, -56, 180, 83}, collection.getExtent().getSpatial().getBbox()[0]);
        Assertions.assertNotNull(collection.getExtent().getTemporal());

        Assertions.assertEquals("1.0.0", collection.getStacVersion());
        Assertions.assertEquals(java.util.List.of("B01", "B02"), collection.getSummaries().get("eo:bands"));
        Assertions.assertNotNull(collection.getAssets());
    }
}
