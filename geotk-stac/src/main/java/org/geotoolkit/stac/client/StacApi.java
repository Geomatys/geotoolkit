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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.stac.dto.Collection;
import org.geotoolkit.stac.dto.Collections;
import org.geotoolkit.stac.dto.Item;
import org.geotoolkit.stac.dto.ItemCollection;
import org.geotoolkit.stac.request.GetCollection;
import org.geotoolkit.stac.request.GetCollections;
import org.geotoolkit.stac.request.GetItem;
import org.geotoolkit.stac.request.GetItems;
import org.geotoolkit.stac.request.SearchItems;

/**
 * STAC API client.
 * Provides both typed {@link ServiceRequest} methods for individual endpoints
 * and high-level convenience methods matching the legacy StacClient API.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on: <a href="https://api.stacspec.org/v1.0.0/">STAC API Specification</a>
 */
public class StacApi extends AbstractOpenApi {

    private static final Logger LOGGER = Logger.getLogger(StacApi.class.getName());

    private final DownloadURIExtractor downloadURIExtractor;

    public StacApi(OpenApiConfiguration config) {
        super(config);
        this.downloadURIExtractor = new DefaultDownloadURIExtractor(httpClient);
    }

    public StacApi(OpenApiConfiguration config, DownloadURIExtractor extractor) {
        super(config);
        this.downloadURIExtractor = extractor;
    }

    // =========================================================================
    // ServiceRequest methods (AbstractOpenApi pattern)
    // =========================================================================

    /**
     * List available collections.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Collections&gt;&gt;
     */
    public ServiceRequest<Supplier<Collections>> getCollections(GetCollections parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        if (parameters.getBbox() != null) {
            queryParams.addAll(toPairs("csv", "bbox", parameters.getBbox()));
        }
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Collections.class);
    }

    /**
     * Describe a specific collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Collection&gt;&gt;
     */
    public ServiceRequest<Supplier<Collection>> getCollection(GetCollection parameters) {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing required parameter 'collectionId' when calling getCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Collection.class);
    }

    /**
     * List items in a collection (single page).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ItemCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<ItemCollection>> getItems(GetItems parameters) {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing required parameter 'collectionId' when calling getItems");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        if (parameters.getBbox() != null) {
            queryParams.addAll(toPairs("csv", "bbox", parameters.getBbox()));
        }
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ItemCollection.class);
    }

    /**
     * Get a single item by collection and feature ID.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Item&gt;&gt;
     */
    public ServiceRequest<Supplier<Item>> getItem(GetItem parameters) {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing required parameter 'collectionId' when calling getItem");
        }
        if (parameters.getFeatureId() == null) {
            throw new IllegalArgumentException("Missing required parameter 'featureId' when calling getItem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId())
                + "/items/" + urlEncode(parameters.getFeatureId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Item.class);
    }

    /**
     * Search items via POST /search (single page).
     * For paginated results over all items use {@link #searchItems(SearchItems)}.
     *
     * @param parameters search body (also serialized as POST body)
     * @return ServiceRequest&lt;Supplier&lt;ItemCollection&gt;&gt;
     * @throws IllegalArgumentException if body serialization fails
     */
    public ServiceRequest<Supplier<ItemCollection>> searchItemsPage(SearchItems parameters) throws IllegalArgumentException {
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize search body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/search", List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return requestAsJson(request, ItemCollection.class);
    }

    // =========================================================================
    // High-level convenience methods
    // =========================================================================

    /**
     * Search items with automatic pagination and fallback to OGC API /items endpoint.
     *
     * <p>Attempts POST /search first. If the server does not support it and there is exactly
     * one collection in {@code params}, falls back to GET /collections/{id}/items.</p>
     *
     * @param params search parameters; must not be null
     * @return all matching items across all pages
     * @throws IOException          on network error or HTTP failure
     * @throws InterruptedException if the thread is interrupted
     */
    public List<Item> searchItems(SearchItems params) throws IOException, InterruptedException {
        return searchItems(params, null);
    }

    /**
     * Search items with automatic pagination and fallback to OGC API /items endpoint,
     * stopping early once {@code maxTotal} items have been collected.
     *
     * @param params   search parameters; must not be null
     * @param maxTotal maximum total number of items to return, or null for no limit
     * @return matching items across as many pages as needed to reach {@code maxTotal} (or all pages if null)
     * @throws IOException          on network error or HTTP failure
     * @throws InterruptedException if the thread is interrupted
     */
    public List<Item> searchItems(SearchItems params, Integer maxTotal) throws IOException, InterruptedException {
        if (params == null) throw new IllegalArgumentException("params must not be null");

        final int effectiveLimit = params.getLimit() != null ? params.getLimit() : 100;
        final SearchItems effectiveParams = params.getLimit() != null ? params
                : new SearchItems()
                        .collections(params.getCollections())
                        .bbox(params.getBbox())
                        .datetime(params.getDatetime())
                        .limit(effectiveLimit);

        final List<Item> allItems = new ArrayList<>();
        try {
            ItemCollection page = searchItemsPage(effectiveParams).send().body().get();
            collectFeatures(page, allItems);
            String nextHref;
            while (!capReached(allItems, maxTotal) && (nextHref = findNextHref(page)) != null) {
                try {
                    page = objectMapper.treeToValue(rawGet(nextHref), ItemCollection.class);
                    collectFeatures(page, allItems);
                } catch (IOException e) {
                    LOGGER.warning("Pagination failed after " + allItems.size() + " items: " + e.getMessage());
                    break;
                }
            }
        } catch (ServiceException e) {
            LOGGER.fine("POST /search failed (" + e.getMessage() + "), trying OGC /items fallback");
            final List<String> collections = effectiveParams.getCollections();
            if (collections == null || collections.size() != 1) {
                throw new IOException("POST /search failed and no single-collection fallback available: " + e.getMessage(), e);
            }
            try {
                ItemCollection page = getItems(new GetItems()
                        .collectionId(collections.get(0))
                        .bbox(toDoubleList(effectiveParams.getBbox()))
                        .datetime(effectiveParams.getDatetime())
                        .limit(effectiveLimit))
                        .send().body().get();
                collectFeatures(page, allItems);
                String nextHref;
                while (!capReached(allItems, maxTotal) && (nextHref = findNextHref(page)) != null) {
                    try {
                        page = objectMapper.treeToValue(rawGet(nextHref), ItemCollection.class);
                        collectFeatures(page, allItems);
                    } catch (IOException ex) {
                        LOGGER.warning("Fallback pagination failed after " + allItems.size() + " items: " + ex.getMessage());
                        break;
                    }
                }
            } catch (ServiceException e2) {
                throw new IOException("Both POST /search and GET /items failed: " + e2.getMessage(), e2);
            }
        }
        LOGGER.info("Total items found: " + allItems.size());
        return capReached(allItems, maxTotal) ? allItems.subList(0, maxTotal) : allItems;
    }

    private static boolean capReached(List<Item> items, Integer maxTotal) {
        return maxTotal != null && items.size() >= maxTotal;
    }

    /**
     * Retrieve all available collections (first page only).
     *
     * @return list of collections
     * @throws ServiceException on HTTP or network error
     */
    public List<Collection> getCollections() throws ServiceException {
        final Collections result = getCollections(new GetCollections()).send().body().get();
        return (result != null && result.getCollections() != null) ? result.getCollections() : List.of();
    }

    /**
     * Retrieve only collection IDs (first page only).
     *
     * @return list of collection identifiers
     * @throws ServiceException on HTTP or network error
     */
    public List<String> getCollectionIds() throws ServiceException {
        return getCollections().stream()
                .map(Collection::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a single collection by ID, or null if not found.
     *
     * @param collectionId collection identifier (required)
     * @return the collection, or null if HTTP 404
     * @throws ServiceException on HTTP or network error other than 404
     */
    public Collection getCollection(String collectionId) throws ServiceException {
        try {
            return getCollection(new GetCollection().collectionId(collectionId)).send().body().get();
        } catch (ServiceException e) {
            if (e.getCode() == 404) return null;
            throw e;
        }
    }

    /**
     * Check whether a collection exists on this server.
     *
     * @param collectionId collection identifier
     * @return true if the collection exists
     */
    public boolean collectionExists(String collectionId) {
        try {
            return getCollection(collectionId) != null;
        } catch (ServiceException e) {
            LOGGER.log(Level.WARNING, "Collection check failed: " + collectionId, e);
            return false;
        }
    }

    /**
     * Detect the STAC resource type at the given URL (ITEM, COLLECTION, or UNKNOWN).
     *
     * @param url resource URL
     * @return detected type
     */
    public StacResourceType detectStacType(String url) throws IOException, InterruptedException {
        final JsonNode data = rawGet(url);
        final String type = data.path("type").asText(null);
        if ("Feature".equals(type)) return StacResourceType.ITEM;
        if ("Collection".equals(type)) return StacResourceType.COLLECTION;
        return StacResourceType.UNKNOWN;
    }

    /**
     * Return true if the resource at {@code url} is a STAC Item.
     */
    public boolean isItem(String url) {
        try {
            return detectStacType(url) == StacResourceType.ITEM;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return true if the resource at {@code url} is a STAC Collection.
     */
    public boolean isCollection(String url) {
        try {
            return detectStacType(url) == StacResourceType.COLLECTION;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Load a STAC Item from a direct URL. Returns null if the resource is not an Item.
     *
     * @param url item URL
     * @return parsed Item, or null if type mismatch or not found
     */
    public Item loadItem(String url) throws IOException, InterruptedException {
        final JsonNode data = rawGet(url);
        final String type = data.path("type").asText(null);
        if (!"Feature".equals(type)) return null;
        return objectMapper.treeToValue(data, Item.class);
    }

    /**
     * Extract the download URI from a STAC Item using the configured {@link DownloadURIExtractor}.
     *
     * @param item STAC item
     * @return download URI, or null if none found
     */
    public URI getDownloadURI(Item item) {
        return downloadURIExtractor.extract(item);
    }

    /**
     * Download a file from a URI to a local directory (atomic move, skips if file exists).
     * Timeout: 5 minutes.
     *
     * @param uri       resource URI
     * @param outputDir target directory
     * @return path to the downloaded file
     */
    public Path downloadFile(URI uri, Path outputDir) throws IOException, InterruptedException {
        final String filename = extractFilename(uri);
        final Path target = outputDir.resolve(filename);
        if (Files.exists(target)) {
            LOGGER.fine("File already exists, skipping download: " + target);
            return target;
        }
        if (!isHttpScheme(uri)) {
            // Non-HTTP scheme (e.g. s3://): route through NIO instead of the HTTP client,
            // which only accepts http/https. Caller is responsible for registering any
            // FileSystemProvider (and credentials) needed to resolve this URI.
            Files.copy(Path.of(uri), target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Downloaded: " + target);
            return target;
        }
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMinutes(5))
                .GET()
                .build();
        final HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() >= 400) {
            throw new IOException("HTTP " + response.statusCode() + " downloading " + uri);
        }
        final Path temp = outputDir.resolve(filename + ".tmp");
        try (final InputStream in = response.body()) {
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("Downloaded: " + target);
        return target;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private String formatTemporalExtent(String temporalExtent) {
        if (temporalExtent == null) return null;
        final String[] parts = temporalExtent.split("/", 2);
        if (parts.length != 2) return temporalExtent;
        final String start = "null".equalsIgnoreCase(parts[0]) ? ".." : parts[0];
        final String end   = "null".equalsIgnoreCase(parts[1]) ? ".." : parts[1];
        if ("..".equals(start) && "..".equals(end)) return null;
        return start + "/" + end;
    }

    private JsonNode rawGet(String url) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        final HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new IOException("HTTP " + resp.statusCode() + " GET " + url);
        }
        return objectMapper.readTree(resp.body());
    }

    private String findNextHref(ItemCollection page) {
        if (page == null || page.getLinks() == null) return null;
        return page.getLinks().stream()
                .filter(l -> "next".equals(l.getRel()))
                .map(Link::getHref)
                .findFirst().orElse(null);
    }

    private static List<Double> toDoubleList(double[] arr) {
        if (arr == null) return null;
        final List<Double> list = new ArrayList<>(arr.length);
        for (final double v : arr) list.add(v);
        return list;
    }

    private static void collectFeatures(ItemCollection page, List<Item> target) {
        if (page != null && page.getFeatures() != null) {
            target.addAll(page.getFeatures());
        }
    }

    private static boolean isHttpScheme(URI uri) {
        final String scheme = uri.getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

    private String extractFilename(URI uri) {
        final String path = uri.getPath();
        if (path == null || path.equals("/")) {
            return Integer.toHexString(uri.hashCode());
        }
        final String name = path.substring(path.lastIndexOf('/') + 1);
        return name.isEmpty() ? Integer.toHexString(uri.hashCode()) : name;
    }
}
