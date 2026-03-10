package org.geotoolkit.stac.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link StacClient}.
 */
public class StacClientTest {

    private StacClient stacClient;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private String serverUrl;

    @Before
    public void setup() throws IOException {
        stacClient = new StacClient();
        
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
        
        // Specific collection endpoint
        server.createContext("/stac/collections/coll-1", exchange -> {
            try {
                org.geotoolkit.stac.dto.Collection c1 = new org.geotoolkit.stac.dto.Collection();
                c1.setId("coll-1");
                
                String jsonResponse = mapper.writeValueAsString(c1);
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
        
        Asset dataAsset = new Asset();
        dataAsset.setHref("https://example.com/data.nc");
        dataAsset.setRoles(Collections.singletonList("data"));
        
        Map<String, Asset> assets = new HashMap<>();
        assets.put("data", dataAsset);
        item.setAssets(assets);
        
        URI downloadUrl = stacClient.getDownloadURI(item);
        assertNotNull(downloadUrl);
        assertEquals("https://example.com/data.nc", downloadUrl.toString());
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
}
