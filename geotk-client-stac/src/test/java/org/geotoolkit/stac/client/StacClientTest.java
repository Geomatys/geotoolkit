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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.junit.After;
import org.junit.Before;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link StacClient}.
 */
public class StacClientTest {

    private StacClient stacClient;
    private HttpClient httpClient;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private String serverUrl;

    @Before
    public void setup() throws IOException {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        stacClient = new StacClient(httpClient, new DefaultDownloadURIExtractor(httpClient));
        
        server = HttpServer.create(new InetSocketAddress(0), 0);
        
        // Search endpoint
        server.createContext("/stac/search", exchange -> {
            try {
                Item item = new Item();
                item.setId("test-item-1");
                ItemCollection collection = new ItemCollection();
                List<Item> features = new ArrayList<>();
                features.add(item);
                collection.setFeatures(features);
                
                String jsonResponse = mapper.writeValueAsString(collection);
                byte[] bytes = jsonResponse.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });
        
        // Collections endpoint
        server.createContext("/stac/collections", exchange -> {
            try {
                org.geotoolkit.stac.dto.Collection c1 = new org.geotoolkit.stac.dto.Collection();
                c1.setId("coll-1");
                org.geotoolkit.stac.dto.Collection c2 = new org.geotoolkit.stac.dto.Collection();
                c2.setId("coll-2");
                
                List<org.geotoolkit.stac.dto.Collection> collList = new ArrayList<>();
                collList.add(c1);
                collList.add(c2);
                
                org.geotoolkit.stac.dto.Collections collections = new org.geotoolkit.stac.dto.Collections();
                collections.setCollections(collList);
                
                String jsonResponse = mapper.writeValueAsString(collections);
                byte[] bytes = jsonResponse.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });
        
        // Specific collection endpoint (expanded for the new Collection-URL detection)
        server.createContext("/stac/collections/coll-1", exchange -> {
            try {
                ObjectNode collObj = mapper.createObjectNode();
                collObj.put("id", "coll-1");
                collObj.put("type", "Collection");
                ArrayNode links = collObj.putArray("links");
                ObjectNode rootLink = mapper.createObjectNode();
                rootLink.put("rel", "root");
                rootLink.put("href", serverUrl + "/stac");
                links.add(rootLink);
                
                String jsonResponse = mapper.writeValueAsString(collObj);
                byte[] bytes = jsonResponse.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // Download data endpoint
        server.createContext("/data.nc", exchange -> {
            byte[] bytes = "fake netcdf data".getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        // STAC Item endpoint (type=Feature)
        server.createContext("/stac/item-1", exchange -> {
            try {
                Item item = new Item();
                item.setId("item-1");
                item.setCollection("coll-1");
                String json = mapper.writeValueAsString(item);
                byte[] bytes = json.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // Unknown STAC type endpoint (API root – no "type" field)
        server.createContext("/stac/catalog", exchange -> {
            byte[] bytes = "{\"links\":[]}".getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        });

        server.start();
        int port = server.getAddress().getPort();
        serverUrl = "http://localhost:" + port;
    }

    @After
    public void teardown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testSearchItemsSuccess() throws Exception {
        List<Item> result = stacClient.searchItems(serverUrl + "/stac", "test-collection", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-item-1", result.get(0).getId());
    }

    @Test
    public void testGetDownloadUrl() {
        Item item = new Item();
        item.setId("test-item-download");

        String localHref = serverUrl + "/data.nc";
        Asset dataAsset = new Asset();
        dataAsset.setHref(localHref);
        dataAsset.setRoles(Collections.singletonList("data"));

        Map<String, Asset> assets = new HashMap<>();
        assets.put("data", dataAsset);
        item.setAssets(assets);

        URI downloadUrl = stacClient.getDownloadURI(item);
        assertNotNull(downloadUrl);
        assertEquals(localHref, downloadUrl.toString());
    }

    @Test
    public void testGetCollections() throws Exception {
        List<org.geotoolkit.stac.dto.Collection> result = stacClient.getCollections(serverUrl + "/stac");
        assertEquals(2, result.size());
        assertEquals("coll-1", result.get(0).getId());
        
        List<String> ids = stacClient.getCollectionIds(serverUrl + "/stac");
        assertEquals(2, ids.size());
        assertTrue(ids.contains("coll-1"));
        assertTrue(ids.contains("coll-2"));
    }

    @Test
    public void testGetCollection() throws Exception {
        org.geotoolkit.stac.dto.Collection result = stacClient.getCollection(serverUrl + "/stac", "coll-1");
        assertNotNull(result);
        assertEquals("coll-1", result.getId());
        
        assertTrue(stacClient.collectionExists(serverUrl + "/stac", "coll-1"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        Path tempDir = Files.createTempDirectory("stac-test");
        URI fileUri = URI.create(serverUrl + "/data.nc");

        Path downloaded = stacClient.downloadFile(fileUri, tempDir);

        assertTrue(Files.exists(downloaded));
        assertEquals("data.nc", downloaded.getFileName().toString());
        assertEquals("fake netcdf data", Files.readString(downloaded));
        Files.delete(downloaded);
        Files.delete(tempDir);
    }

    @Test
    public void testDetectStacTypeItem() throws Exception {
        StacResourceType type = stacClient.detectStacType(serverUrl + "/stac/item-1");
        assertEquals(StacResourceType.ITEM, type);
    }

    @Test
    public void testDetectStacTypeCollection() throws Exception {
        StacResourceType type = stacClient.detectStacType(serverUrl + "/stac/collections/coll-1");
        assertEquals(StacResourceType.COLLECTION, type);
    }

    @Test
    public void testDetectStacTypeUnknown() throws Exception {
        StacResourceType type = stacClient.detectStacType(serverUrl + "/stac/catalog");
        assertEquals(StacResourceType.UNKNOWN, type);
    }

    @Test
    public void testIsItem() throws Exception {
        assertTrue(stacClient.isItem(serverUrl + "/stac/item-1"));
        assertFalse(stacClient.isItem(serverUrl + "/stac/collections/coll-1"));
    }

    @Test
    public void testIsCollection() throws Exception {
        assertTrue(stacClient.isCollection(serverUrl + "/stac/collections/coll-1"));
        assertFalse(stacClient.isCollection(serverUrl + "/stac/item-1"));
    }

    @Test
    public void testLoadItem() throws Exception {
        Item item = stacClient.loadItem(serverUrl + "/stac/item-1");
        assertNotNull(item);
        assertEquals("item-1", item.getId());
        assertEquals("coll-1", item.getCollection());
    }

    @Test
    public void testLoadItemFromCollectionUrlReturnsNull() throws Exception {
        // Passing a Collection URL to loadItem() should return null (type mismatch)
        Item item = stacClient.loadItem(serverUrl + "/stac/collections/coll-1");
        assertNull(item);
    }

    @Test
    public void testSearchItemsWithCollectionUrl() throws Exception {
        // The stacUrl points directly to a Collection endpoint.
        // It should detect the type, extract the "coll-1" id, find the root link "/stac", 
        // and issue the search on "/stac/search" with the correct collection.
        List<Item> result = stacClient.searchItems(serverUrl + "/stac/collections/coll-1", null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-item-1", result.get(0).getId());
    }

    @Test
    public void testFormatTemporalExtent() {
        // Standard start/end should remain unchanged
        assertEquals("2020-01-01T00:00:00Z/2020-01-02T00:00:00Z", 
                stacClient.formatTemporalExtent("2020-01-01T00:00:00Z/2020-01-02T00:00:00Z"));
                
        // start/null should become start/..
        assertEquals("2020-01-01T00:00:00Z/..", 
                stacClient.formatTemporalExtent("2020-01-01T00:00:00Z/null"));
                
        // null/end should become ../end
        assertEquals("../2020-01-02T00:00:00Z", 
                stacClient.formatTemporalExtent("null/2020-01-02T00:00:00Z"));
                
        // null/null should be null
        assertNull(stacClient.formatTemporalExtent("null/null"));
        assertNull(stacClient.formatTemporalExtent("null"));
        
        // Blank or null input should return null
        assertNull(stacClient.formatTemporalExtent(null));
        assertNull(stacClient.formatTemporalExtent(""));
        assertNull(stacClient.formatTemporalExtent("   "));
    }
}
