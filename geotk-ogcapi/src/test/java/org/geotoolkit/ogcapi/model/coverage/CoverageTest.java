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
package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.geotoolkit.ogcapi.dto.coverage.DomainSet;
import org.geotoolkit.ogcapi.dto.coverage.GeneralGrid;
import org.geotoolkit.ogcapi.dto.coverage.GridLimits;
import org.geotoolkit.ogcapi.dto.coverage.IndexAxis;
import org.geotoolkit.ogcapi.dto.coverage.RegularAxis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class CoverageTest {

    @Test
    public void testDomainSetRoundTrip() throws Exception {

        final GeneralGrid grid = new GeneralGrid("http://www.opengis.net/def/crs/EPSG/0/4326",
                List.of(new RegularAxis("Lat", 40.0, 50.0, "deg", 0.1),
                        new RegularAxis("Lon", 0.0, 10.0, "deg", 0.1)),
                new GridLimits(List.of(new IndexAxis("Lat", 0, 99), new IndexAxis("Lon", 0, 99))));
        final DomainSet domainSet = new DomainSet(grid);

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(domainSet);
        final DomainSet read = mapper.readValue(json, DomainSet.class);

        final GeneralGrid readGrid = read.getGeneralGrid();
        Assertions.assertEquals(grid.getSrsName(), readGrid.getSrsName());
        Assertions.assertEquals(grid.getAxisLabels(), readGrid.getAxisLabels());
        Assertions.assertEquals(grid.getGridLimits().getAxis().size(), readGrid.getGridLimits().getAxis().size());
    }

}
