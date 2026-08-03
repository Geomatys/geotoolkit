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
package org.geotoolkit.ogcapi.client.styles;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.styles.CreateStyle;
import org.geotoolkit.ogcapi.request.styles.DeleteStyle;
import org.geotoolkit.ogcapi.request.styles.GetStyle;
import org.geotoolkit.ogcapi.request.styles.GetStylesList;
import org.geotoolkit.ogcapi.request.styles.ReplaceStyle;
import org.geotoolkit.ogcapi.request.styles.UpdateDefaultStyle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StylesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain StylesApi#getStyles(org.geotoolkit.ogcapi.request.styles.GetStylesList) };
     * Request the list of styles of collection `land use`, in html format.
     */
    @Test
    public void testGetStyles() {
        final var api = new StylesApi(newConfig());

        { //check valid request
            final var parameters = new GetStylesList()
                    .collectionId("land use")
                    .format("html");
            final var serviceRequest = api.getStyles(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/land%20use/styles"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain StylesApi#getStyle(org.geotoolkit.ogcapi.request.styles.GetStyle) };
     * Request the stylesheet of style `alpine style` of collection `parks`, in sld10 format.
     */
    @Test
    public void testGetStyle() {
        final var api = new StylesApi(newConfig());

        { //check incorrect request : missing style id
            final var parameters = new GetStyle()
                    .collectionId("parks");
            assertThrows(IllegalArgumentException.class, () -> api.getStyle(parameters));
        }

        { //check valid request
            final var parameters = new GetStyle()
                    .collectionId("parks")
                    .styleId("alpine style")
                    .format("sld10");
            final var serviceRequest = api.getStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/styles/alpine%20style"
                    + "?f=sld10", request.uri().toString());
            assertEquals(Optional.of("application/vnd.mapbox.style+json, application/vnd.ogc.sld+xml;version=1.0, application/vnd.ogc.sld+xml;version=1.1, application/vnd.ogc.cartosym+css, application/vnd.ogc.cartosym+json, */*"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain StylesApi#createStyle(org.geotoolkit.ogcapi.request.styles.CreateStyle) };
     * Publish a new mapbox style for collection `open data`, as a dry run, with strict handling.
     */
    @Test
    public void testCreateStyle() {
        final var api = new StylesApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateStyle()
                    .collectionId("open data")
                    .contentType("application/vnd.mapbox.style+json");
            assertThrows(IllegalArgumentException.class, () -> api.createStyle(parameters));
        }

        { //check valid request
            final byte[] content = "{\"version\":8,\"name\":\"night mode\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateStyle()
                    .collectionId("open data")
                    .content(content)
                    .contentType("application/vnd.mapbox.style+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/styles"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/vnd.mapbox.style+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain StylesApi#replaceStyle(org.geotoolkit.ogcapi.request.styles.ReplaceStyle) };
     * Replace style `night mode` of collection `open data`, with lenient handling.
     */
    @Test
    public void testReplaceStyle() {
        final var api = new StylesApi(newConfig());

        { //check incorrect request : missing style id
            final var parameters = new ReplaceStyle()
                    .collectionId("open data")
                    .content("{\"version\":8}".getBytes(StandardCharsets.UTF_8))
                    .contentType("application/vnd.mapbox.style+json");
            assertThrows(IllegalArgumentException.class, () -> api.replaceStyle(parameters));
        }

        { //check valid request
            final byte[] content = "{\"version\":8,\"name\":\"night mode\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceStyle()
                    .collectionId("open data")
                    .styleId("night mode")
                    .content(content)
                    .contentType("application/vnd.mapbox.style+json")
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/styles/night%20mode", request.uri().toString());
            assertEquals(Optional.of("application/vnd.mapbox.style+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain StylesApi#deleteStyle(org.geotoolkit.ogcapi.request.styles.DeleteStyle) };
     * Delete style `night mode` of collection `open data`.
     */
    @Test
    public void testDeleteStyle() {
        final var api = new StylesApi(newConfig());

        { //check incorrect request : missing style id
            final var parameters = new DeleteStyle()
                    .collectionId("open data");
            assertThrows(IllegalArgumentException.class, () -> api.deleteStyle(parameters));
        }

        { //check valid request
            final var parameters = new DeleteStyle()
                    .collectionId("open data")
                    .styleId("night mode");
            final var serviceRequest = api.deleteStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/styles/night%20mode", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain StylesApi#updateDefaultStyle(org.geotoolkit.ogcapi.request.styles.UpdateDefaultStyle) };
     * Set the default style of collection `open data` to `night mode`, then clear the default style
     * of the whole dataset.
     *
     * <p>
     * The request body is a {@code application/merge-patch+json} document generated internally via
     * Jackson serialization, so the exact body bytes cannot be asserted literally.</p>
     */
    @Test
    public void testUpdateDefaultStyle() {
        final var api = new StylesApi(newConfig());

        { //check valid request : with collectionId
            final var parameters = new UpdateDefaultStyle()
                    .collectionId("open data")
                    .defaultStyleId("night mode");
            final var serviceRequest = api.updateDefaultStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/styles", request.uri().toString());
            assertEquals(Optional.of("application/merge-patch+json"), request.headers().firstValue("Content-Type"));
        }

        { //check valid request : without collectionId
            final var parameters = new UpdateDefaultStyle()
                    .defaultStyleId(null);
            final var serviceRequest = api.updateDefaultStyle(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/styles", request.uri().toString());
            assertEquals(Optional.of("application/merge-patch+json"), request.headers().firstValue("Content-Type"));
        }
    }
}
