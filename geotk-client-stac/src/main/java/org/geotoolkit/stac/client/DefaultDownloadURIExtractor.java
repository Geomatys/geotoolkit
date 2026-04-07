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

import org.geotoolkit.stac.dto.Asset;
import org.geotoolkit.stac.dto.Item;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link DownloadURIExtractor} that probes candidate URIs via HTTP
 * before returning one. Results are cached per STAC collection so that HTTP probes are run
 * only once per collection per extractor lifetime.
 *
 * <p>The selection algorithm for the <em>first</em> item of a given collection is:</p>
 * <ol>
 *   <li>Collect assets with role {@code "data"} first, then all other assets.</li>
 *   <li>For each asset, try the primary {@code href}, then each alternate href (from
 *       {@link Asset#getAlternate()}) in iteration order.</li>
 *   <li>For each HTTP/HTTPS candidate, probe with a HEAD request (falling back to a
 *       range GET if HEAD is not supported).</li>
 *   <li>On success, record a {@link CollectionStrategy} for this collection and return
 *       the URI immediately.</li>
 *   <li>If nothing is reachable, return {@code null}.</li>
 * </ol>
 *
 * <p>For <em>subsequent items</em> of the same collection the cached {@link CollectionStrategy}
 * is applied directly — no HTTP probe is issued. The assumption is that within a collection
 * all items share the same access pattern (main href vs. a specific alternate).</p>
 *
 * <p>If an item has no collection ID (i.e. {@link Item#getCollection()} returns {@code null}),
 * the full probing algorithm is run without caching.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public class DefaultDownloadURIExtractor implements DownloadURIExtractor {

    private static final Logger LOGGER = Logger.getLogger(DefaultDownloadURIExtractor.class.getName());

    /** Timeout used when probing URIs for reachability. */
    private static final Duration PROBE_TIMEOUT = Duration.ofSeconds(15);

    // -------------------------------------------------------------------------
    // Inner type: CollectionStrategy
    // -------------------------------------------------------------------------

    /**
     * Describes which href slot was found to be reachable for a given STAC collection.
     * It stores two pieces of information:
     * <ul>
     *   <li>{@code dataRole} — whether the working asset had the {@code "data"} role.</li>
     *   <li>{@code alternateKey} — {@code null} means "use the primary href"; a non-null
     *       value is the key into {@link Asset#getAlternate()} to use instead.</li>
     * </ul>
     */
    static final class CollectionStrategy {

        /** {@code true} if the working asset was a data-role asset; {@code false} for others. */
        final boolean dataRole;

        /**
         * {@code null} → use the primary {@code href}.
         * Non-null → use {@code asset.getAlternate().get(alternateKey).getHref()}.
         */
        final String alternateKey;

        private CollectionStrategy(boolean dataRole, String alternateKey) {
            this.dataRole = dataRole;
            this.alternateKey = alternateKey;
        }

        static CollectionStrategy primary(boolean dataRole) {
            return new CollectionStrategy(dataRole, null);
        }

        static CollectionStrategy alternate(boolean dataRole, String key) {
            return new CollectionStrategy(dataRole, key);
        }

        @Override
        public String toString() {
            return "CollectionStrategy{dataRole=" + dataRole
                    + ", alternateKey=" + (alternateKey == null ? "<primary>" : "'" + alternateKey + "'") + "}";
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** HTTP client shared with the caller (e.g. {@link StacClient}). */
    private final HttpClient httpClient;

    /**
     * Per-collection strategy cache.
     * - Key: {@link Item#getCollection()} value.
     * - Value: the first href-slot that succeeded for any item in that collection.
     * This cache is valid for the current session / extractor.
     */
    private final ConcurrentHashMap<String, CollectionStrategy> collectionStrategyCache = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a new extractor with its own internal HTTP client (15s connect timeout,
     * follows redirects).
     */
    public DefaultDownloadURIExtractor() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    /**
     * Creates a new extractor that shares the given HTTP client.
     * Sharing the client is preferred when the caller (e.g. {@link StacClient}) already
     * manages one, to avoid creating redundant connection pools.
     *
     * @param httpClient the HTTP client to use for reachability probes
     */
    public DefaultDownloadURIExtractor(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // -------------------------------------------------------------------------
    // DownloadURIExtractor
    // -------------------------------------------------------------------------

    /**
     * Extracts a download URI from the given STAC item.
     * This method will proceed like this:
     * <p>We check first if we already cached correct link for the collection of item given.
     * If nothing cached, the selection algorithm run :</p>
     * <ol>
     *   <li>Collect assets with role {@code "data"} first, then all other assets.</li>
     *   <li>For each asset, try the primary {@code href}, then each alternate href (from
     *       {@link Asset#getAlternate()}) in iteration order.</li>
     *   <li>For each HTTP/HTTPS candidate, probe with a HEAD request (falling back to a
     *       range GET if HEAD is not supported).</li>
     *   <li>On success, record a {@link CollectionStrategy} for this collection and return
     *       the URI immediately.</li>
     *   <li>If nothing is reachable, return {@code null}.</li>
     * </ol>
     *
     * @param item the STAC item
     * @return the download URI, or null if none could be found
     */
    @Override
    public URI extract(Item item) {
        Map<String, Asset> assets = item.getAssets();
        if (assets == null || assets.isEmpty()) return null;

        // Get Collection of this item
        String collectionId = item.getCollection();

        // --- Fast path: collection strategy already known ---
        if (collectionId != null) {
            CollectionStrategy cached = collectionStrategyCache.get(collectionId);
            if (cached != null) {
                URI uri = applyStrategy(cached, assets);
                if (uri != null) {
                    LOGGER.fine(() -> "Cache hit for collection '" + collectionId + "': " + cached);
                    return uri;
                }
                // Cached strategy no longer works (e.g. server changed) → fall through to re-probe.
                LOGGER.fine(() -> "Cached strategy for collection '" + collectionId
                        + "' yielded no URI; re-probing.");
                collectionStrategyCache.remove(collectionId);
            }
        }

        // --- Slow path: probe and learn which slot works ---
        // Separate data-role assets from the rest; data assets are tried first.
        List<Asset> dataAssets = new ArrayList<>();
        List<Asset> otherAssets = new ArrayList<>();
        for (Asset asset : assets.values()) {
            if (hasDataRole(asset)) {
                dataAssets.add(asset);
            } else {
                otherAssets.add(asset);
            }
        }

        // Try data-role assets first, then fallback assets.
        URI result = findFirstReachable(dataAssets, true, collectionId);
        if (result != null) return result;

        /* /!\ Check for other assets without "data" role is deactivated as we consider
         * elements without the "data" flag are not the data we want to load (but thumbnails or something else) /!\
         * If you want to reactivate it, uncomment this line :
         */
        // return findFirstReachable(otherAssets, false, collectionId);

        return null; // No checking for "otherAssets" (assets without "data" role)
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Iterates a list of assets, probes each candidate href, and on the first reachable
     * one records the strategy in the cache (if a collection ID is available).
     *
     * @param assets       the assets to try
     * @param dataRole     whether these assets have the data role (for strategy recording)
     * @param collectionId the collection ID to cache the result under (may be null)
     * @return the first reachable URI, or {@code null}
     */
    private URI findFirstReachable(List<Asset> assets, boolean dataRole, String collectionId) {
        for (Asset asset : assets) {
            // Primary href
            String primary = asset.getHref();
            if (primary != null && !primary.isBlank() && isHttpScheme(primary) && probeUri(primary)) {
                if (collectionId != null) {
                    CollectionStrategy strategy = CollectionStrategy.primary(dataRole);
                    collectionStrategyCache.put(collectionId, strategy);
                    LOGGER.fine(() -> "Learned strategy for collection '" + collectionId + "': " + strategy);
                }
                return URI.create(primary);
            }

            // Alternate hrefs
            Map<String, Asset> alternates = asset.getAlternate();
            if (alternates != null) {
                for (Map.Entry<String, Asset> alt : alternates.entrySet()) {
                    String altHref = alt.getValue() != null ? alt.getValue().getHref() : null;
                    if (altHref != null && !altHref.isBlank() && isHttpScheme(altHref) && probeUri(altHref)) {
                        String altKey = alt.getKey();
                        if (collectionId != null) {
                            CollectionStrategy strategy = CollectionStrategy.alternate(dataRole, altKey);
                            collectionStrategyCache.put(collectionId, strategy);
                            LOGGER.fine(() -> "Learned strategy for collection '" + collectionId + "': " + strategy);
                        }
                        return URI.create(altHref);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Applies a previously learned {@link CollectionStrategy} to the current item's assets,
     * returning the corresponding URI without probing.
     *
     * @param strategy the strategy to apply
     * @param assets   the item's asset map
     * @return the URI, or {@code null} if the strategy cannot be applied (e.g. missing asset)
     */
    private URI applyStrategy(CollectionStrategy strategy, Map<String, Asset> assets) {
        for (Asset asset : assets.values()) {
            if (strategy.dataRole != hasDataRole(asset)) continue;

            if (strategy.alternateKey == null) {
                // Use primary href
                String href = asset.getHref();
                if (href != null && !href.isBlank()) return URI.create(href);
            } else {
                // Use the named alternate
                Map<String, Asset> alternates = asset.getAlternate();
                if (alternates != null) {
                    Asset alt = alternates.get(strategy.alternateKey);
                    if (alt != null && alt.getHref() != null && !alt.getHref().isBlank()) {
                        return URI.create(alt.getHref());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Probes the given href for HTTP reachability.
     * <ol>
     *   <li>Tries a HEAD request first (cheap, no body transfer).</li>
     *   <li>If HEAD is not supported (HTTP 405) or fails with an I/O error, retries with a
     *       GET request limited to the first byte (Range: bytes=0-0).</li>
     *   <li>Returns {@code true} if the final response status is &lt; 400.</li>
     * </ol>
     *
     * @param href the URL to probe
     * @return {@code true} if the URL is reachable
     */
    private boolean probeUri(String href) {
        try {
            HttpRequest headRequest = HttpRequest.newBuilder()
                    .uri(URI.create(href))
                    .timeout(PROBE_TIMEOUT)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> headResp = httpClient.send(headRequest, HttpResponse.BodyHandlers.discarding());
            int status = headResp.statusCode();

            if (status == 405) {
                return probeWithGet(href);
            }

            boolean reachable = status < 400;
            LOGGER.fine(() -> "HEAD probe " + href + " → " + status + " (reachable=" + reachable + ")");
            return reachable;

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "HEAD probe failed for " + href + ", retrying with GET", e);
            return probeWithGet(href);
        }
    }

    /**
     * Fallback probe using a range GET request (first byte only).
     *
     * @param href the URL to probe
     * @return {@code true} if the response status is &lt; 400
     */
    private boolean probeWithGet(String href) {
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(href))
                    .timeout(PROBE_TIMEOUT)
                    .header("Range", "bytes=0-0")
                    .GET()
                    .build();

            HttpResponse<Void> getResp = httpClient.send(getRequest, HttpResponse.BodyHandlers.discarding());
            int status = getResp.statusCode();
            boolean reachable = status < 400;
            LOGGER.fine(() -> "GET probe " + href + " → " + status + " (reachable=" + reachable + ")");
            return reachable;

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "GET probe also failed for " + href, e);
            return false;
        }
    }

    /**
     * Returns {@code true} if the asset has a role list containing {@code "data"}.
     */
    private static boolean hasDataRole(Asset asset) {
        List<String> roles = asset.getRoles();
        if (roles == null) return false;
        for (String r : roles) {
            if ("data".equals(r)) return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if the href uses the {@code http} or {@code https} scheme.
     * Non-HTTP hrefs (e.g. {@code s3://}, local paths) are skipped — they cannot be probed
     * with the built-in {@link HttpClient}.
     */
    private static boolean isHttpScheme(String href) {
        String lower = href.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
