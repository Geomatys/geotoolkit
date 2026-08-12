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
package org.geotoolkit.ogcwxs.client.tile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.client.Request;

/**
 * Lightweight {@link Request} adapter bridging a resolved WMTS tile URL (either built from
 * KVP query parameters, or a RESTful resourceURL with placeholders already substituted) to
 * the lazy {@link Request} contract used by the tiling/pyramid model.
 *
 * @author Quentin Bialota (Geomatys)
 */
final class WMTSTileRequest implements Request {

    private final HttpClient httpClient;
    private final URL url;
    private final Map<String, String> headerMap = new HashMap<>();
    private long timeout = 20000L;

    WMTSTileRequest(HttpClient httpClient, URL url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        try {
            return httpClient.send(getRequestBuilder().build(), HttpResponse.BodyHandlers.ofInputStream()).body();
        } catch (URISyntaxException | InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public HttpRequest.Builder getRequestBuilder() throws MalformedURLException, URISyntaxException {
        final HttpRequest.Builder builder = HttpRequest.newBuilder(url.toURI());
        headerMap.forEach(builder::header);
        if (timeout > 0) {
            builder.timeout(Duration.ofMillis(timeout));
        }
        return builder;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
