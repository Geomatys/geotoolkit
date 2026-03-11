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
package org.geotoolkit.processing.stac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.client.utils.URIUtils;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

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

import org.geotoolkit.stac.client.DownloadURIExtractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link StacClientItemsDownloadingProcess}.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacClientItemsProcessTest {

    /**
     * ObjectMapper for JSON serialization/deserialization.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Embedded HTTP server to mock STAC API responses.
     */
    private HttpServer server;

    /**
     * Base URL of the embedded HTTP server.
     */
    private String serverUrl;

    /**
     * Temporary directory for downloaded files during tests.
     */
    private Path tempDir;

    @Before
    public void setup() throws Exception {
        // Create a temporary directory for the test
        tempDir = Files.createTempDirectory("stac-process-test");

        // Start an embedded HTTP server
        server = HttpServer.create(new InetSocketAddress(0), 0);

        // Search endpoint
        server.createContext("/stac/search", exchange -> {
            try {
                int port = server.getAddress().getPort();
                String localUrl = "http://localhost:" + port;

                Item item = new Item();
                item.setId("test-item-1");

                Asset dataAsset = new Asset();
                dataAsset.setHref(localUrl + "/data.nc");
                dataAsset.setRoles(Collections.singletonList("data"));

                Map<String, Asset> assets = new HashMap<>();
                assets.put("data", dataAsset);
                item.setAssets(assets);

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
    public void teardown() throws Exception {
        if (server != null) {
            server.stop(0);
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                 .sorted((a, b) -> b.compareTo(a))
                 .forEach(p -> {
                     try {
                         Files.delete(p);
                     } catch (Exception e) {
                         // ignore
                     }
                 });
        }
    }

    /**
     * Test for the downloading process, which retrieves items from a STAC API and downloads associated assets.
     * @throws Exception
     */
    @Test
    public void testDownloadProcess() throws Exception {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                "geotoolkit", "stac.items.downloading");
        assertNotNull("STAC downloading descriptor not found", desc);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter(StacClientItemsDownloadingDescriptor.STAC_URL_NAME).setValue(serverUrl + "/stac");
        input.parameter(StacClientItemsDownloadingDescriptor.COLLECTION_NAME).setValue("test-collection");
        input.parameter(StacClientItemsDownloadingDescriptor.OUTPUT_DIRECTORY_NAME).setValue(tempDir);
        input.parameter(StacClientItemsDownloadingDescriptor.EXTRACTOR_CLASS_NAME).setValue(MockExtractor.class.getName());

        final Process process = desc.createProcess(input);
        assertNotNull("Failed to create STAC process", process);

        final ParameterValueGroup result = process.call();
        assertNotNull("STAC process execution result is null", result);

        @SuppressWarnings("unchecked")
        List<Path> downloadedFiles = (List<Path>) result.parameter(
                StacClientItemsDownloadingDescriptor.OUTPUT_NAME).getValue();
        
        assertNotNull(downloadedFiles);
        assertEquals(1, downloadedFiles.size());
        
        Path downloadedFile = downloadedFiles.get(0);
        assertTrue(Files.exists(downloadedFile));
        assertEquals("data.nc", downloadedFile.getFileName().toString());
        assertEquals("fake netcdf data", Files.readString(downloadedFile));

        // Remove the downloaded file after test
        Files.deleteIfExists(downloadedFile);
    }

    /**
     * Test for the getURIs process, which retrieves download URIs for items matching specified criteria.
     * @throws Exception
     */
    @Test
    public void testGetURIs() throws Exception {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                "geotoolkit", "stac.items.getURIs");
        assertNotNull("STAC getURIs descriptor not found", desc);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter(StacClientItemsGetURIsDescriptor.STAC_URL_NAME).setValue(serverUrl + "/stac");
        input.parameter(StacClientItemsGetURIsDescriptor.COLLECTION_NAME).setValue("test-collection");
        input.parameter(StacClientItemsGetURIsDescriptor.EXTRACTOR_CLASS_NAME).setValue(MockExtractor.class.getName());

        final Process process = desc.createProcess(input);
        assertNotNull("Failed to create STAC process", process);

        final ParameterValueGroup result = process.call();
        assertNotNull("STAC process execution result is null", result);

        @SuppressWarnings("unchecked")
        List<URI> assetsURIs = (List<URI>) result.parameter(
                StacClientItemsGetURIsDescriptor.OUTPUT_NAME).getValue();

        assertNotNull(assetsURIs);
        assertEquals(1, assetsURIs.size());

        URI assetURI = assetsURIs.get(0);
        String actual = assetURI.toString();
        assertTrue(actual.matches("http://localhost:\\d+/data\\.nc"));
    }

    /**
     * Mock extractor for testing dynamic class loading.
     */
    public static class MockExtractor implements DownloadURIExtractor {
        @Override
        public URI extract(final Item item) {
            final Map<String, Asset> assets = item.getAssets();
            if (assets != null && assets.containsKey("data")) {
                return URI.create(assets.get("data").getHref());
            }
            return null;
        }
    }
}
