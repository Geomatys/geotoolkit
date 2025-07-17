/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.dggs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DggsTest {

    /**
     * Check null fields are not written.
     */
    @Test
    public void testNullValueAreNotWritten() throws JsonProcessingException {

        final DggrsZonesResponse response = new DggrsZonesResponse();
        final String json = new ObjectMapper().writeValueAsString(response);
        Assertions.assertEquals("{\"zones\":[],\"links\":[]}", json);

    }

}
