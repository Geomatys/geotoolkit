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
package org.geotoolkit.ogcapi.model.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotoolkit.ogcapi.dto.records.Facet;
import org.geotoolkit.ogcapi.dto.records.FacetBucket;
import org.geotoolkit.ogcapi.dto.records.Facets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class RecordsTest {

    /**
     * Check null fields are not written.
     */
    @Test
    public void testNullValueAreNotWritten() throws JsonProcessingException {

        final Facets response = new Facets();
        final String json = new ObjectMapper().writeValueAsString(response);
        Assertions.assertEquals("{}", json);

    }

    /**
     * Round-trip a facets response through Jackson.
     */
    @Test
    public void testRoundTrip() throws JsonProcessingException {

        final FacetBucket bucket = new FacetBucket();
        bucket.setValue("dataset");
        bucket.setCount(12);

        final Facet facet = new Facet();
        facet.setName("type");
        facet.getBuckets().add(bucket);

        final Facets facets = new Facets();
        facets.getFacets().add(facet);

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(facets);
        final Facets read = mapper.readValue(json, Facets.class);

        Assertions.assertEquals(facets, read);

    }

}
