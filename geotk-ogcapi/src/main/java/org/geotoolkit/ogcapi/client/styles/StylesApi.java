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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.toPairs;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.styles.SetDefaultStyle;
import org.geotoolkit.ogcapi.dto.styles.Styles;
import org.geotoolkit.ogcapi.request.styles.CreateStyle;
import org.geotoolkit.ogcapi.request.styles.DeleteStyle;
import org.geotoolkit.ogcapi.request.styles.GetStyle;
import org.geotoolkit.ogcapi.request.styles.GetStylesList;
import org.geotoolkit.ogcapi.request.styles.ReplaceStyle;
import org.geotoolkit.ogcapi.request.styles.UpdateDefaultStyle;

/**
 * OGCAPI Styles.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class StylesApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_DRY_RUN = "dry-run";
    public static final String PARAM_F       = "f";

    /**
     * Path fragments.
     */
    public static final String PATH_COLLECTIONS = "/collections";
    public static final String PATH_STYLES      = "/styles";

    public StylesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the list of styles that are currently available for the base resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Styles&gt;
     */
    public ServiceRequest<Supplier<Styles>> getStyles(GetStylesList parameters) {

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES);

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Styles.class);
    }

    /**
     * Retrieve the stylesheet of a style. The response {@code Content-Type} header indicates which
     * style encoding was returned, readable via {@link java.net.http.HttpResponse#headers()} on the
     * sent request.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<byte[]> getStyle(GetStyle parameters) throws IllegalArgumentException {
        if (parameters.getStyleId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'styleId' when calling getStyle");
        }

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES + '/').append(urlEncode(parameters.getStyleId()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/vnd.mapbox.style+json, application/vnd.ogc.sld+xml;version=1.0, application/vnd.ogc.sld+xml;version=1.1, application/vnd.ogc.cartosym+css, application/vnd.ogc.cartosym+json, */*");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Publish a new style.
     *
     * <p>
     * On a successful (non {@code dry-run}) creation, the response has status code {@code 201} with a
     * {@code Location} header carrying the URI of the new style, from which the caller can extract the
     * new {@code styleId}, readable via {@link java.net.http.HttpResponse#headers()} on the sent
     * request. If {@link CreateStyle#getDryRun()} is {@code true}, no style is created and the response
     * has status code {@code 204}.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createStyle(CreateStyle parameters) throws IllegalArgumentException {
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling createStyle");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling createStyle");
        }

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES);

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) request.header("Prefer", parameters.getHandling().headerValue);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Replace an existing style. All representations of that style are replaced, including any
     * automatically derived representation in other style encodings.
     *
     * <p>
     * If {@link ReplaceStyle#getDryRun()} is {@code true}, the style is not actually replaced and the
     * response has status code {@code 204}.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceStyle(ReplaceStyle parameters) throws IllegalArgumentException {
        if (parameters.getStyleId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'styleId' when calling replaceStyle");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling replaceStyle");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling replaceStyle");
        }

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES + '/').append(urlEncode(parameters.getStyleId()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) request.header("Prefer", parameters.getHandling().headerValue);
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a style. Deleting a style also deletes any sub-resources associated with the style.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteStyle(DeleteStyle parameters) throws IllegalArgumentException {
        if (parameters.getStyleId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'styleId' when calling deleteStyle");
        }

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES + '/').append(urlEncode(parameters.getStyleId()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Update the default style of the style collection, using a {@code application/merge-patch+json}
     * request (RFC 7396 JSON Merge Patch). Setting {@link UpdateDefaultStyle#getDefaultStyleId()} to
     * {@code null} clears the default style.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if the request body cannot be serialized
     */
    public ServiceRequest<Void> updateDefaultStyle(UpdateDefaultStyle parameters) throws IllegalArgumentException {

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append(PATH_COLLECTIONS + '/').append(urlEncode(parameters.getCollectionId()));
        pathBuilder.append(PATH_STYLES);

        final byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(new SetDefaultStyle()._default(parameters.getDefaultStyleId()));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to serialize default style update", ex);
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), null));
        request.header("Content-Type", "application/merge-patch+json");
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

}
