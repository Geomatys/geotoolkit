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

import com.sun.net.httpserver.HttpServer;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link DefaultDownloadURIExtractor}.
 * Uses an embedded {@link HttpServer} to control how each candidate href responds.
 */
public class DefaultDownloadURIExtractorTest {

    private HttpServer server;
    private String serverUrl;
    private HttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // /ok  → 200
        server.createContext("/ok", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        // /notfound → 404
        server.createContext("/notfound", exchange -> {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
        });

        server.start();
        serverUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @After
    public void tearDown() {
        if (server != null) server.stop(0);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Item buildItem(String collectionId, Asset... assets) {
        Item item = new Item();
        item.setId("test-item");
        item.setCollection(collectionId);
        Map<String, Asset> assetMap = new HashMap<>();
        for (int i = 0; i < assets.length; i++) assetMap.put("asset-" + i, assets[i]);
        item.setAssets(assetMap);
        return item;
    }

    private Asset dataAsset(String href) {
        Asset a = new Asset(href);
        a.setRoles(List.of("data"));
        return a;
    }

    private Asset plainAsset(String href) {
        return new Asset(href);
    }

    /** Attaches a typed alternate link to an asset using the new Asset#alternate field. */
    private void addAlternate(Asset asset, String key, String href) {
        Asset alt = new Asset(href);
        asset.getAlternate().put(key, alt);
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    /**
     * A data-role asset with a reachable primary href should be returned.
     */
    @Test
    public void testExtractDataRoleReachable() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);
        Item item = buildItem("col-A", dataAsset(serverUrl + "/ok"));

        URI result = extractor.extract(item);

        assertNotNull(result);
        assertEquals(serverUrl + "/ok", result.toString());
    }

    /**
     * The second item of the same collection must use the cached strategy —
     * the server should be probed only once across both extract() calls.
     */
    @Test
    public void testCollectionCacheHit() throws Exception {
        AtomicInteger probeCount = new AtomicInteger();
        server.createContext("/probed", exchange -> {
            probeCount.incrementAndGet();
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        // First item → probe runs
        Item item1 = buildItem("col-cached", dataAsset(serverUrl + "/probed"));
        extractor.extract(item1);

        // Second item in the same collection → cache hit, no new probe
        Item item2 = buildItem("col-cached", dataAsset(serverUrl + "/probed"));
        extractor.extract(item2);

        assertEquals("Server should be probed exactly once (cache used for second item)", 1, probeCount.get());
    }

    /**
     * Different collections must be cached independently.
     */
    @Test
    public void testSeparateCachePerCollection() throws Exception {
        AtomicInteger colACount = new AtomicInteger();
        AtomicInteger colBCount = new AtomicInteger();

        server.createContext("/col-a", exchange -> {
            colACount.incrementAndGet();
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });
        server.createContext("/col-b", exchange -> {
            colBCount.incrementAndGet();
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        // Two items for collection A
        extractor.extract(buildItem("col-A", dataAsset(serverUrl + "/col-a")));
        extractor.extract(buildItem("col-A", dataAsset(serverUrl + "/col-a")));

        // Two items for collection B
        extractor.extract(buildItem("col-B", dataAsset(serverUrl + "/col-b")));
        extractor.extract(buildItem("col-B", dataAsset(serverUrl + "/col-b")));

        assertEquals("Collection A: 1 probe for 2 items", 1, colACount.get());
        assertEquals("Collection B: 1 probe for 2 items", 1, colBCount.get());
    }

    /**
     * When the primary href is unreachable, the extractor must fall back to the typed
     * alternate link and cache the alternate strategy for the collection.
     */
    @Test
    public void testExtractFallbackToAlternate() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        Asset asset = dataAsset(serverUrl + "/notfound");  // primary unreachable
        addAlternate(asset, "s3", serverUrl + "/ok");       // alternate reachable

        URI result = extractor.extract(buildItem("col-alt", asset));

        assertNotNull(result);
        assertEquals(serverUrl + "/ok", result.toString());
    }

    /**
     * The cached alternate strategy must be applied to the second item correctly.
     */
    @Test
    public void testAlternateStrategyCached() throws Exception {
        AtomicInteger altCount = new AtomicInteger();
        server.createContext("/alt-cached", exchange -> {
            altCount.incrementAndGet();
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        });

        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        // First item: primary → 404, alternate → 200 (probe fires once)
        Asset asset1 = dataAsset(serverUrl + "/notfound");
        addAlternate(asset1, "s3", serverUrl + "/alt-cached");
        extractor.extract(buildItem("col-alt-cache", asset1));

        // Second item: cached strategy should resolve to the "s3" alternate directly
        Asset asset2 = dataAsset(serverUrl + "/notfound");
        addAlternate(asset2, "s3", serverUrl + "/alt-cached");
        URI result = extractor.extract(buildItem("col-alt-cache", asset2));

        assertNotNull(result);
        assertEquals(serverUrl + "/alt-cached", result.toString());
        assertEquals("Alternate endpoint should have been probed only once", 1, altCount.get());
    }

    /**
     * /!\ Deactivated as we consider assets without "data" role are not the data we want to download
     * Reactivate this test if we change paradigm (and uncomment this feature in Default Extractor code) /!\
     *
     * When all data-role hrefs are unreachable, the extractor must fall back to
     * non-data-role assets.
     */
    @Ignore
    @Test
    public void testExtractFallbackToOtherAsset() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        Asset data = dataAsset(serverUrl + "/notfound");
        Asset other = plainAsset(serverUrl + "/ok");

        URI result = extractor.extract(buildItem("col-fallback", data, other));

        assertNotNull(result);
        assertEquals(serverUrl + "/ok", result.toString());
    }

    /**
     * When all candidates across all assets are unreachable, null must be returned.
     */
    @Test
    public void testExtractAllUnreachable() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        URI result = extractor.extract(buildItem("col-dead",
                dataAsset(serverUrl + "/notfound"),
                plainAsset(serverUrl + "/notfound")));

        assertNull(result);
    }

    /**
     * Items with no collection ID must still work (no caching, just probing).
     */
    @Test
    public void testExtractNoCollectionId() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);
        Item item = buildItem(null, dataAsset(serverUrl + "/ok"));

        URI result = extractor.extract(item);

        assertNotNull(result);
        assertEquals(serverUrl + "/ok", result.toString());
    }

    /**
     * An empty asset map must return null without errors.
     */
    @Test
    public void testExtractEmptyAssets() {
        DefaultDownloadURIExtractor extractor = new DefaultDownloadURIExtractor(httpClient);

        Item item = new Item();
        item.setId("empty");
        item.setAssets(Collections.emptyMap());

        assertNull(extractor.extract(item));
    }
}
