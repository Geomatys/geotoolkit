package org.geotoolkit.stac.process;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.stac.client.DownloadURIExtractor;
import org.geotoolkit.stac.client.StacApi;
import org.opengis.geometry.Envelope;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class StacApiProcessingUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Utility method to convert a spatial extent represented as an Envelope into a bounding box array of doubles in the format [minX, minY, maxX, maxY].
     * @param spatialExtent an Envelope representing the spatial extent, which may be null. If null, the method will return null.
     * @return an array of doubles representing the bounding box in the format [minX, minY, maxX, maxY] if the input spatialExtent is not null, or null if the input is null.
     */
    static double[] getBbox(Envelope spatialExtent) {
        if (spatialExtent != null) {
            GeneralEnvelope env = new GeneralEnvelope(spatialExtent);
            return  new double[]{
                    env.getMinimum(0), env.getMinimum(1),
                    env.getMaximum(0), env.getMaximum(1)
            };
        }
        return null;
    }

    /**
     * Utility method to convert a temporal extent represented as an array of strings into a single string in the format "start/end".
     * A missing bound is rendered as the STAC open-interval marker "..", per the RFC 3339 datetime interval spec.
     * @param temporalExtentArray an array of strings representing the temporal extent,
     *                            where the first element is the start time and the second element is the end time.
     *                            If the array has only one element, it will be returned as is.
     * @return a string representing the temporal extent in the format "start/end" if the input array has
     * two or more elements, or the single element if the array has only one element. Returns null if the input array is null or empty.
     */
    static String getTemporalExtent(String[] temporalExtentArray) {
        if (temporalExtentArray == null || temporalExtentArray.length == 0) {
            return null;
        }

        if (temporalExtentArray.length == 1) {
            if (temporalExtentArray[0] == null) return null;
            return temporalExtentArray[0] + '/' + temporalExtentArray[0];
        }

        String start = temporalExtentArray[0];
        String end = temporalExtentArray[1];

        if (start == null && end == null) {
            return null;
        } else if (start == null) {
            return "../" + end;
        } else if (end == null) {
            return start + "/..";
        } else {
            return start + "/" + end;
        }
    }

    /**
     * Utility method to create a StacApi instance bound to the given base URL, optionally using a
     * custom DownloadURIExtractor if the class name is provided.
     * @param baseUrl the STAC API base URL the returned client will be bound to
     * @param extractorClassName the fully qualified class name of the custom DownloadURIExtractor to use, or null/empty to use the default extractor
     * @return a StacApi instance configured with the specified DownloadURIExtractor or the default one if no class name is provided
     * @throws ClassNotFoundException if the specified class name does not correspond to a valid class
     * @throws NoSuchMethodException if the specified class does not have a no-argument constructor
     * @throws InvocationTargetException if the constructor of the specified class throws an exception during instantiation
     * @throws InstantiationException if the specified class cannot be instantiated (e.g., if it's abstract or an interface)
     * @throws IllegalAccessException if the constructor of the specified class is not accessible (e.g., if it's private)
     */
    static StacApi buildStacApi(String baseUrl, String extractorClassName)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        final OpenApiConfiguration config = new OpenApiConfiguration.Builder()
                .updateBaseUri(baseUrl)
                .build();
        if (extractorClassName != null && !extractorClassName.trim().isEmpty()) {
            final Class<?> extractorClass = Class.forName(extractorClassName);
            final DownloadURIExtractor extractor = (DownloadURIExtractor)
                    extractorClass.getDeclaredConstructor().newInstance();
            return new StacApi(config, extractor);
        } else {
            return new StacApi(config);
        }
    }

    /**
     * The root STAC service URL and collection id resolved from a Collection resource URL.
     */
    record CollectionRoot(String rootUrl, String collectionId) {}

    /**
     * Resolves the STAC service root URL and collection id from a URL pointing to a Collection resource,
     * mirroring the rewrite the STAC search endpoint needs (Collection URLs are not searchable directly,
     * only {@code {root}/search} is).
     *
     * @param collectionUrl the URL of the Collection resource
     * @param fallbackCollectionId the collection id to use if the Collection JSON has none (e.g. the "collection" process parameter)
     * @return the resolved root URL and collection id
     * @throws IOException if an error occurs during the request or response parsing
     * @throws InterruptedException if an interruption occurs during the http request
     */
    static CollectionRoot resolveCollectionRoot(String collectionUrl, String fallbackCollectionId)
            throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(collectionUrl))
                .timeout(Duration.ofSeconds(60))
                .GET().build();
        final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("HTTP " + response.statusCode() + " from GET " + collectionUrl);
        }
        final JsonNode collNode = MAPPER.readTree(response.body());
        final String collectionId = collNode.path("id").asText(fallbackCollectionId);

        final JsonNode links = collNode.get("links");
        if (links != null && links.isArray()) {
            for (JsonNode link : links) {
                if ("root".equals(link.path("rel").asText()) && link.hasNonNull("href")) {
                    return new CollectionRoot(link.get("href").asText(), collectionId);
                }
            }
        }

        // Heuristic fallback if no explicit root link is present
        String rootUrl = collectionUrl;
        int idx = rootUrl.lastIndexOf("/collections/");
        if (idx != -1) {
            rootUrl = rootUrl.substring(0, idx);
        } else if (rootUrl.endsWith("/collections")) {
            rootUrl = rootUrl.substring(0, rootUrl.length() - "/collections".length());
        }
        return new CollectionRoot(rootUrl, collectionId);
    }
}
