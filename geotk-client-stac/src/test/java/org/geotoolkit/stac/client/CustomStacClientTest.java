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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link StacClient} with your own data.
 */
public class CustomStacClientTest {

    private StacClient stacClient;

    @Before
    public void setup() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        stacClient = new StacClient(httpClient, new DefaultDownloadURIExtractor(httpClient));
    }

    /**
     * User testing
     */
    @Ignore
    @Test
    public void testGetDownloadUrl() throws Exception {
        final String SERVER_URL = "https://stac-pg-api.ifremer.fr";
        final double[] BBOX = new double[]{-10, 10,-10,10};
        final String[] TEMPORAL = new String[]{"2024-06-01T00:00:00Z", "2024-06-05T00:00:00Z"};

        List<Item> result = stacClient.searchItems(SERVER_URL, "AVHRR_SST_METOP_B_OSISAF_L2P_v1_0", BBOX, TEMPORAL[0].concat("/").concat(TEMPORAL[1]));
        assertEquals(8, result.size());

        List<URI> downloadURIs = new ArrayList<>();
        System.out.println("🚀 Initializing downloads...");
        for(Item i : result) {
            URI downloadURI = stacClient.getDownloadURI(i);
            if (downloadURI != null) {
                downloadURIs.add(downloadURI);
                System.out.printf("   ✅ Found: %s%n", downloadURI);
            } else {
                System.err.printf("   ❌ No URI for item: %s%n", i.getId());
            }
        }

        assertEquals(8, downloadURIs.size());
    }
}
