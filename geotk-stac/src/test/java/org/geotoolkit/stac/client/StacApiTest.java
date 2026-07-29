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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Collection;
import org.geotoolkit.stac.dto.Collections;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.geotoolkit.stac.request.SearchItems;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link StacApi} using an embedded HTTP server.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class StacApiTest {

    private static HttpServer server;
    private static StacApi stacApi;
    private static String serverUrl;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeClass
    public static void setup() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();
        serverUrl = "http://localhost:" + port;

        // POST /stac/search — returns one item page
        server.createContext("/stac/search", exchange -> {
            final Item item = new Item();
            item.setId("test-item-1");
            item.setCollection("test-collection");
            final ItemCollection ic = new ItemCollection();
            ic.setFeatures(List.of(item));
            ic.setLinks(List.of());
            try (OutputStream os = exchange.getResponseBody()) {
                final byte[] bytes = MAPPER.writeValueAsBytes(ic);
                exchange.sendResponseHeaders(200, bytes.length);
                os.write(bytes);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // GET /stac/collections — returns two collections
        server.createContext("/stac/collections", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
                return;
            }
            // Route /stac/collections/coll-1 to specific handler (registered separately)
            if (exchange.getRequestURI().getPath().startsWith("/stac/collections/")) {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
                return;
            }
            final Collection c1 = new Collection();
            c1.setId("coll-1");
            final Collection c2 = new Collection();
            c2.setId("coll-2");
            final Collections cols = new Collections();
            cols.setCollections(List.of(c1, c2));
            try (OutputStream os = exchange.getResponseBody()) {
                final byte[] bytes = MAPPER.writeValueAsBytes(cols);
                exchange.sendResponseHeaders(200, bytes.length);
                os.write(bytes);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // GET /stac/collections/coll-1 — returns single collection
        server.createContext("/stac/collections/coll-1", exchange -> {
            final ObjectNode collObj = MAPPER.createObjectNode();
            collObj.put("id", "coll-1");
            collObj.put("type", "Collection");
            collObj.putArray("links");
            try (OutputStream os = exchange.getResponseBody()) {
                final byte[] bytes = MAPPER.writeValueAsBytes(collObj);
                exchange.sendResponseHeaders(200, bytes.length);
                os.write(bytes);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // GET /stac/collections/notfound — 404
        server.createContext("/stac/collections/notfound", exchange -> {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
        });

        // GET /data.nc — binary download
        server.createContext("/data.nc", exchange -> {
            final byte[] bytes = new byte[]{1, 2, 3, 4, 5};
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        // GET /stac/item-1 — Feature item
        server.createContext("/stac/item-1", exchange -> {
            final Item item = new Item();
            item.setId("item-1");
            item.setCollection("coll-1");
            try (OutputStream os = exchange.getResponseBody()) {
                final byte[] bytes = MAPPER.writeValueAsBytes(item);
                exchange.sendResponseHeaders(200, bytes.length);
                os.write(bytes);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        // GET /stac/collections/coll-1 already handled above; this is the type-only response
        // GET /stac/catalog — unknown type
        server.createContext("/stac/catalog", exchange -> {
            final ObjectNode node = MAPPER.createObjectNode();
            node.put("type", "Catalog");
            try (OutputStream os = exchange.getResponseBody()) {
                final byte[] bytes = MAPPER.writeValueAsBytes(node);
                exchange.sendResponseHeaders(200, bytes.length);
                os.write(bytes);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        });

        server.start();

        final OpenApiConfiguration config = new OpenApiConfiguration.Builder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(port)
                .setBasePath("/stac")
                .build();
        final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        stacApi = new StacApi(config, new DefaultDownloadURIExtractor(httpClient));
    }

    @AfterClass
    public static void teardown() {
        if (server != null) server.stop(0);
    }

    @Test
    public void testSearchItemsSuccess() throws Exception {
        final List<Item> items = stacApi.searchItems(
                new SearchItems().collections(List.of("test-collection")));
        assertEquals(1, items.size());
        assertEquals("test-item-1", items.get(0).getId());
    }

    /**
     * A capped {@code searchItems(params, maxTotal)} call must stop paginating as soon as enough
     * items were collected, never requesting pages beyond what's needed.
     */
    @Test
    public void testSearchItemsWithCapStopsPagination() throws Exception {
        final HttpServer capServer = HttpServer.create(new InetSocketAddress(0), 0);
        final int capPort = capServer.getAddress().getPort();
        final String capServerUrl = "http://localhost:" + capPort;
        final AtomicInteger page3Hits = new AtomicInteger();
        try {
            capServer.createContext("/stac/search/page2", exchange -> {
                writeItemPage(exchange, List.of(itemWithId("item-3"), itemWithId("item-4")),
                        List.of(new Link().rel("next").href(capServerUrl + "/stac/search/page3")));
            });
            capServer.createContext("/stac/search/page3", exchange -> {
                page3Hits.incrementAndGet();
                writeItemPage(exchange, List.of(itemWithId("item-5"), itemWithId("item-6")), List.of());
            });
            capServer.createContext("/stac/search", exchange -> {
                writeItemPage(exchange, List.of(itemWithId("item-1"), itemWithId("item-2")),
                        List.of(new Link().rel("next").href(capServerUrl + "/stac/search/page2")));
            });
            capServer.start();

            final OpenApiConfiguration config = new OpenApiConfiguration.Builder()
                    .setScheme("http")
                    .setHost("localhost")
                    .setPort(capPort)
                    .setBasePath("/stac")
                    .build();
            final StacApi cappedApi = new StacApi(config,
                    new DefaultDownloadURIExtractor(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()));

            final List<Item> items = cappedApi.searchItems(
                    new SearchItems().collections(List.of("test-collection")), 3);

            assertEquals(3, items.size());
            assertEquals(0, page3Hits.get());
        } finally {
            capServer.stop(0);
        }
    }

    private static Item itemWithId(String id) {
        final Item item = new Item();
        item.setId(id);
        item.setCollection("test-collection");
        return item;
    }

    private static void writeItemPage(com.sun.net.httpserver.HttpExchange exchange, List<Item> items, List<Link> links) throws java.io.IOException {
        final ItemCollection ic = new ItemCollection();
        ic.setFeatures(items);
        ic.setLinks(links);
        try (OutputStream os = exchange.getResponseBody()) {
            final byte[] bytes = MAPPER.writeValueAsBytes(ic);
            exchange.sendResponseHeaders(200, bytes.length);
            os.write(bytes);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }

    @Test
    public void testGetDownloadUrl() throws Exception {
        final Map<String, Asset> assets = new HashMap<>();
        final Asset asset = new Asset();
        asset.setHref(serverUrl + "/data.nc");
        asset.setRoles(List.of("data"));
        assets.put("data", asset);
        final Item item = new Item();
        item.setAssets(assets);

        final URI uri = stacApi.getDownloadURI(item);
        assertEquals(serverUrl + "/data.nc", uri.toString());
    }

    @Test
    public void testGetCollections() throws Exception {
        final List<Collection> collections = stacApi.getCollections();
        assertEquals(2, collections.size());
        assertEquals("coll-1", collections.get(0).getId());
    }

    @Test
    public void testGetCollectionIds() throws Exception {
        final List<String> ids = stacApi.getCollectionIds();
        assertEquals(2, ids.size());
        assertTrue(ids.contains("coll-1"));
        assertTrue(ids.contains("coll-2"));
    }

    @Test
    public void testGetCollection() throws Exception {
        final Collection col = stacApi.getCollection("coll-1");
        assertNotNull(col);
        assertEquals("coll-1", col.getId());
    }

    @Test
    public void testGetCollectionNotFound() throws Exception {
        final Collection col = stacApi.getCollection("notfound");
        assertNull(col);
    }

    @Test
    public void testCollectionExists() throws Exception {
        assertTrue(stacApi.collectionExists("coll-1"));
        assertFalse(stacApi.collectionExists("notfound"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        final URI uri = URI.create(serverUrl + "/data.nc");
        final Path tmpDir = Files.createTempDirectory("stac-test");
        final Path result = stacApi.downloadFile(uri, tmpDir);
        assertNotNull(result);
        assertTrue(Files.exists(result));
    }

    @Test
    public void testDownloadFileNonHttpSchemeUsesNio() throws Exception {
        // Non-HTTP schemes (e.g. s3://) route through NIO instead of the HTTP client.
        // file:// exercises that branch without requiring a real S3 filesystem provider.
        final Path sourceDir = Files.createTempDirectory("stac-nio-source");
        final Path source = sourceDir.resolve("asset.bin");
        Files.write(source, new byte[]{9, 8, 7});

        final Path tmpDir = Files.createTempDirectory("stac-nio-dest");
        final Path result = stacApi.downloadFile(source.toUri(), tmpDir);

        assertNotNull(result);
        assertArrayEquals(new byte[]{9, 8, 7}, Files.readAllBytes(result));
    }

    @Test
    public void testDetectStacTypeItem() throws Exception {
        assertEquals(StacResourceType.ITEM,
                stacApi.detectStacType(serverUrl + "/stac/item-1"));
    }

    @Test
    public void testDetectStacTypeCollection() throws Exception {
        assertEquals(StacResourceType.COLLECTION,
                stacApi.detectStacType(serverUrl + "/stac/collections/coll-1"));
    }

    @Test
    public void testDetectStacTypeUnknown() throws Exception {
        assertEquals(StacResourceType.UNKNOWN,
                stacApi.detectStacType(serverUrl + "/stac/catalog"));
    }

    @Test
    public void testIsItem() throws Exception {
        assertTrue(stacApi.isItem(serverUrl + "/stac/item-1"));
        assertFalse(stacApi.isItem(serverUrl + "/stac/collections/coll-1"));
    }

    @Test
    public void testIsCollection() throws Exception {
        assertTrue(stacApi.isCollection(serverUrl + "/stac/collections/coll-1"));
        assertFalse(stacApi.isCollection(serverUrl + "/stac/item-1"));
    }

    @Test
    public void testLoadItem() throws Exception {
        final Item item = stacApi.loadItem(serverUrl + "/stac/item-1");
        assertNotNull(item);
        assertEquals("item-1", item.getId());
        assertEquals("coll-1", item.getCollection());
    }

    @Test
    public void testLoadItemFromNonFeatureReturnsNull() throws Exception {
        // A Collection URL should return null (type != "Feature")
        final Item item = stacApi.loadItem(serverUrl + "/stac/collections/coll-1");
        assertNull(item);
    }
}
