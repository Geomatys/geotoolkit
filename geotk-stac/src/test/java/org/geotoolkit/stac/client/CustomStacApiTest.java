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
package org.geotoolkit.stac.client;

import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.geotoolkit.stac.request.SearchItems;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Manual integration tests for {@link StacApi} against a real STAC server.
 * All tests are annotated {@link Ignore} — remove the annotation on the test
 * you want to run and configure the constants below to match your server.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CustomStacApiTest {

    // -------------------------------------------------------------------------
    // Configure these before running
    // -------------------------------------------------------------------------

    /** Base URL of the STAC API root (e.g. "https://my-stac-server.com/stac"). */
    private static final String STAC_URL = "https://stac-pg-api.ifremer.fr";

    /** Collection ID to search in, or null to search all collections. */
    private static final String COLLECTION = "AVHRR_SST_METOP_B_OSISAF_L2P_v1_0";

    /** Bounding box [minLon, minLat, maxLon, maxLat]. */
    private static final double[] BBOX = {-10, 10, -10, 10};

    /** RFC 3339 temporal extent interval bounds. */
    private static final String[] TEMPORAL = {"2024-06-01T00:00:00Z", "2024-06-05T00:00:00Z"};

    /** Expected number of results for the search test. */
    private static final int EXPECTED_ITEM_COUNT = 8;

    // -------------------------------------------------------------------------

    private StacApi stacApi;

    @Before
    public void setup() throws Exception {
        final HttpClient.Builder httpBuilder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL);

        final OpenApiConfiguration config = new OpenApiConfiguration.Builder()
                .updateBaseUri(STAC_URL)
                .setHttpClientBuilder(httpBuilder)
                .build();

        stacApi = new StacApi(config, new DefaultDownloadURIExtractor(httpBuilder.build()));
    }

    @Ignore
    @Test
    public void testSearchItemsWithBboxAndTemporal() throws Exception {
        final String datetime = TEMPORAL[0].concat("/").concat(TEMPORAL[1]);

            final List<Item> items = stacApi.searchItems(new SearchItems()
                .collections(COLLECTION != null ? List.of(COLLECTION) : null)
                .bbox(BBOX)
                .datetime(datetime));

        assertEquals(EXPECTED_ITEM_COUNT, items.size());
    }

    @Ignore
    @Test
    public void testGetDownloadUrl() throws Exception {
        final String datetime = TEMPORAL[0].concat("/").concat(TEMPORAL[1]);

        final List<Item> items = stacApi.searchItems(new SearchItems()
                .collections(List.of(COLLECTION))
                .bbox(BBOX)
                .datetime(datetime));

        assertEquals(EXPECTED_ITEM_COUNT, items.size());

        final List<URI> downloadURIs = new ArrayList<>();
        System.out.println("🚀 Initializing downloads...");
        for (Item item : items) {
            final URI downloadUri = stacApi.getDownloadURI(item);
            if (downloadUri != null) {
                downloadURIs.add(downloadUri);
                System.out.printf("   ✅ Found: %s%n", downloadUri);
            } else {
                System.err.printf("   ❌ No URI for item: %s%n", item.getId());
            }
        }

        assertEquals(EXPECTED_ITEM_COUNT, downloadURIs.size());
    }

    @Ignore
    @Test
    public void testGetCollections() throws Exception {
        final List<org.geotoolkit.stac.dto.Collection> collections = stacApi.getCollections();
        System.out.println("Collections found: " + collections.size());
        collections.forEach(c -> System.out.println("  - " + c.getId()));
    }
}
