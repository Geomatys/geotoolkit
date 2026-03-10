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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic STAC API client supporting STAC item search and download.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class StacClient {

    /**
     * Logger for the StacClient class.
     */
    private static final Logger LOGGER = Logger.getLogger(StacClient.class.getName());

    /**
     * Shared ObjectMapper instance for JSON processing.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * HTTP client for making requests to the STAC API. Configured with reasonable timeouts and redirect handling.
     */
    private final HttpClient httpClient;

    /**
     * Download URL extractor to determine how to get the actual data URL from a STAC item. This allows for flexible logic
     */
    private final DownloadUrlExtractor downloadUrlExtractor;

    /**
     * Creates a StacClient with the default HTTP client and default download URL extractor.
     */
    public StacClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build(), new DefaultDownloadUrlExtractor());
    }

    /**
     * Creates a StacClient with a specific HTTP client and specific download URL extractor.
     *
     * @param httpClient the HTTP client to use
     * @param extractor the logic to extract download URLs from items
     */
    public StacClient(HttpClient httpClient, DownloadUrlExtractor extractor) {
        this.httpClient = httpClient;
        this.downloadUrlExtractor = extractor;
    }

    /**
     * Search for STAC items matching the given criteria.
     *
     * @param stacUrl the STAC API base URL
     * @param collection the collection ID
     * @param bbox the bounding box [minLon, minLat, maxLon, maxLat]
     * @param temporalExtent the temporal extent string (e.g., "start/end")
     * @return a list of STAC item nodes
     * @throws Exception if an error occurs during search
     */
    public List<JsonNode> searchItems(String stacUrl, String collection,
                                      double[] bbox, String temporalExtent) throws Exception {
        String searchUrl = stacUrl.replaceAll("/+$", "") + "/search";

        ObjectNode payload = MAPPER.createObjectNode();
        ArrayNode collections = payload.putArray("collections");
        collections.add(collection);
        if (bbox != null && bbox.length == 4) {
            ArrayNode bboxArray = payload.putArray("bbox");
            for (double v : bbox) bboxArray.add(v);
        }
        if (temporalExtent != null && !temporalExtent.isEmpty()) {
            payload.put("datetime", temporalExtent);
        }
        payload.put("limit", 100);

        List<JsonNode> allItems = new ArrayList<>();
        int page = 1;

        LOGGER.info(String.format("Searching STAC API: %s\n  Collection: %s\n  Datetime: %s", searchUrl, collection, temporalExtent));

        while (true) {
            LOGGER.fine(String.format("Fetching page %d...", page));

            JsonNode data;
            try {
                data = postJson(searchUrl, payload);
            } catch (Exception ex) {
                if (page == 1) {
                    // iF the initial POST search fails, we can try to fallback to the OGC API collections/items endpoint
                    LOGGER.info("POST /search failed, falling back to OGC API collections endpoint");
                    return searchViaOgcApi(stacUrl, collection, bbox, temporalExtent);
                }
                throw ex;
            }

            JsonNode features = data.get("features");
            if (features != null && features.isArray()) {
                for (JsonNode item : features) {
                    allItems.add(item);
                }
            }
            
            JsonNode nextLink = findLink(data, "next");
            if (nextLink == null) break;

            String nextHref = nextLink.has("href") ? nextLink.get("href").asText() : null;
            String nextMethod = nextLink.has("method") ? nextLink.get("method").asText("GET") : "GET";

            if ("POST".equalsIgnoreCase(nextMethod) && nextLink.has("body")) {
                payload = (ObjectNode) nextLink.get("body").deepCopy();
            } else if (nextHref != null) {
                try {
                    data = getJson(nextHref);
                    features = data.get("features");
                    if (features != null && features.isArray()) {
                        for (JsonNode item : features) allItems.add(item);
                    }
                    if (findLink(data, "next") == null) break;
                    continue;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to fetch next link: " + nextHref, e);
                    break;
                }
            } else {
                break;
            }
            page++;
        }

        LOGGER.info(String.format("Total items found: %d", allItems.size()));
        return allItems;
    }

    /**
     * Fallback search method using OGC API /collections/{collection}/items endpoint if POST /search is not supported.
     * @param stacUrl the base URL of the STAC API
     * @param collection the collection ID to search within
     * @param bbox the bounding box to filter items (minLon, minLat, maxLon, maxLat)
     * @param temporalExtent the temporal extent string (e.g., "start/end") to filter items
     * @return a list of STAC item nodes matching the search criteria
     * @throws Exception if an error occurs during the search process
     */
    private List<JsonNode> searchViaOgcApi(String stacUrl, String collection,
                                           double[] bbox, String temporalExtent) throws Exception {
        StringBuilder itemsUrlBuilder = new StringBuilder();
        itemsUrlBuilder.append(stacUrl.replaceAll("/+$", ""))
                .append("/collections/")
                .append(collection)
                .append("/items?limit=100");

        if (bbox != null && bbox.length == 4) {
            itemsUrlBuilder.append("&bbox=")
                    .append(bbox[0]).append(",").append(bbox[1]).append(",")
                    .append(bbox[2]).append(",").append(bbox[3]);
        }
        if (temporalExtent != null && !temporalExtent.isEmpty()) {
            itemsUrlBuilder.append("&datetime=").append(temporalExtent);
        }

        String itemsUrl = itemsUrlBuilder.toString();
        List<JsonNode> allItems = new ArrayList<>();
        int page = 1;

        while (itemsUrl != null) {
            LOGGER.fine(String.format("Fetching OGC API page %d...", page));
            JsonNode data = getJson(itemsUrl);

            JsonNode features = data.get("features");
            if (features != null && features.isArray()) {
                for (JsonNode item : features) allItems.add(item);
            }

            JsonNode nextLink = findLink(data, "next");
            itemsUrl = (nextLink != null && nextLink.has("href"))
                    ? nextLink.get("href").asText() : null;
            page++;
        }

        LOGGER.info(String.format("Total items found (OGC API): %d", allItems.size()));
        return allItems;
    }

    /**
     * Extract the download URL from a STAC item using the registered extractor.
     *
     * @param item the STAC item
     * @return the download URL, or null if none found
     */
    public String getDownloadUrl(JsonNode item) {
        if (downloadUrlExtractor != null) {
            return downloadUrlExtractor.extract(item);
        }
        return null;
    }

    /**
     * Download a file to outputDir.
     * Uses a temp file and moves atomically upon success.
     *
     * @param url the file URL
     * @param outputDir the destination directory
     * @return the path to the downloaded file
     * @throws Exception if an error occurs
     */
    public Path downloadFile(String url, Path outputDir) throws Exception {
        String filename = extractFilename(url);
        Path destPath = outputDir.resolve(filename);

        // Check if file already exists to avoid re-downloading
        if (Files.exists(destPath)) return destPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(5))
                .GET().build();

        HttpResponse<InputStream> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() >= 400) {
            throw new IOException("HTTP " + response.statusCode() + " downloading " + url);
        }

        Path tmpPath = outputDir.resolve(filename + ".tmp");
        try (InputStream in = response.body()) {
            Files.copy(in, tmpPath, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.move(tmpPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        return destPath;
    }

    /*
     * =====================
     * HELPER METHODS
     * =====================
     */

    /**
     * Helper method to perform a POST request with a JSON body and parse the response as JSON.
     * @param url the URL to POST to
     * @param body the JSON body to send
     * @return the parsed JSON response
     * @throws Exception if an error occurs during the request or response parsing
     */
    private JsonNode postJson(String url, ObjectNode body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body)))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new IOException("HTTP " + resp.statusCode() + " from POST " + url);
        }
        return MAPPER.readTree(resp.body());
    }

    /**
     * Helper method to perform a GET request and parse the response as JSON.
     * @param url the URL to GET
     * @return the parsed JSON response
     * @throws Exception if an error occurs during the request or response parsing
     */
    private JsonNode getJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET().build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new IOException("HTTP " + resp.statusCode() + " from GET " + url);
        }
        return MAPPER.readTree(resp.body());
    }

    /**
     * Helper method to find a link with a specific rel in a STAC response.
     * @param data the JSON node containing the "links" array
     * @param rel the rel value to search for (e.g., "next")
     * @return the link node with the matching rel, or null if not found
     */
    private static JsonNode findLink(JsonNode data, String rel) {
        JsonNode links = data.get("links");
        if (links == null || !links.isArray()) return null;
        for (JsonNode link : links) {
            if (rel.equals(link.path("rel").asText())) return link;
        }
        return null;
    }

    /**
     * Helper method to extract a filename from a URL. If the URL path is empty or does not contain a valid filename, returns a default name.
     * @param url the URL to extract the filename from
     * @return the extracted filename, or a generated name if no valid filename can be determined
     */
    private static String extractFilename(String url) {
        // The default name is based on the URL hash to ensure uniqueness if no filename can be extracted.
        // And it allows to use cache based on URL if the same URL is encountered again.
        String path = URI.create(url).getPath();
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "download_" + Integer.toHexString(url.hashCode());
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.isEmpty()) {
            return "download_" + Integer.toHexString(url.hashCode());
        }
        return name;
    }
}
