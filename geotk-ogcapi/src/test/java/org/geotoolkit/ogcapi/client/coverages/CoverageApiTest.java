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
package org.geotoolkit.ogcapi.client.coverages;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.coverage.GetCoverage;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageDomainSet;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageRangeType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain CoverageApi#getCoverageDomainSet(org.geotoolkit.ogcapi.request.coverage.GetCoverageDomainSet) };
     * Request the domain set of collection `elevation dataset` in json format.
     */
    @Test
    public void testGetCoverageDomainSet() {
        final var api = new CoverageApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCoverageDomainSet();
            assertThrows(IllegalArgumentException.class, () -> api.getCoverageDomainSet(parameters));
        }

        { //check valid request
            final var parameters = new GetCoverageDomainSet()
                    .collectionId("elevation dataset")
                    .format("json");
            final var serviceRequest = api.getCoverageDomainSet(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/elevation%20dataset/coverage/domainset"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain CoverageApi#getCoverageRangeType(org.geotoolkit.ogcapi.request.coverage.GetCoverageRangeType) };
     * Request the range type of collection `temperature-2m` in html format.
     */
    @Test
    public void testGetCoverageRangeType() {
        final var api = new CoverageApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCoverageRangeType();
            assertThrows(IllegalArgumentException.class, () -> api.getCoverageRangeType(parameters));
        }

        { //check valid request
            final var parameters = new GetCoverageRangeType()
                    .collectionId("temperature-2m")
                    .format("html");
            final var serviceRequest = api.getCoverageRangeType(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/temperature-2m/coverage/rangetype"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain CoverageApi#getCoverage(org.geotoolkit.ogcapi.request.coverage.GetCoverage) };
     * Request the raw coverage data of collection `elevation` restricted by a bounding box, a bbox CRS,
     * a sub-set of properties and a datetime, in tiff format.
     */
    @Test
    public void testGetCoverage() {
        final var api = new CoverageApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCoverage();
            assertThrows(IllegalArgumentException.class, () -> api.getCoverage(parameters));
        }

        { //check valid request
            final var parameters = new GetCoverage()
                    .collectionId("elevation")
                    .bbox(List.of(10.0, 20.0, 30.0, 40.5))
                    .bboxCrs("http://www.opengis.net/def/crs/EPSG/0/4326")
                    .properties(List.of("elevation", "slope"))
                    .datetime("2020-01-01T00:00:00Z")
                    .format("tiff");
            final ServiceRequest<InputStream> serviceRequest = api.getCoverage(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/elevation/coverage"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&bbox-crs=http%3A%2F%2Fwww.opengis.net%2Fdef%2Fcrs%2FEPSG%2F0%2F4326"
                    + "&properties=elevation%2Cslope"
                    + "&datetime=2020-01-01T00%3A00%3A00Z"
                    + "&f=tiff", request.uri().toString());
            assertEquals(Optional.of("image/tiff; application=geotiff, application/x-netcdf"), request.headers().firstValue("Accept"));
        }
    }
}
