/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.hips.client;

import org.geotoolkit.hips.dto.HIPSProperties;
import org.geotoolkit.hips.dto.HIPSList;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.function.Supplier;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HIPSApi extends AbstractService {

    /*
    https://www.ivoa.net/documents/HiPS/20170519/REC-HIPS-1.0-20170519.pdf part 4.4
    */
    /**
     * Required
     */
    public static final String FILE_PROPERTIES = "properties";
    /**
     * Recommended under condition
     */
    public static final String FILE_MOC = "Moc.fits";
    /**
     * Recommended under condition
     */
    public static final String FILE_METADATA = "metadata";
    /**
     * Optional
     */
    public static final String FILE_PREVIEW = "preview.jpg";
    /**
     * Optional
     */
    public static final String FILE_INDEX = "index.html";

    public HIPSApi(ServiceConfiguration apiClient) {
        super(apiClient);
    }

    /**
     * Get cell path for given order,index and extension.
     *
     * @param order healpix order, starting at zero
     * @param cellIndex cell index on nested curve order
     * @param ext extension including leading dot
     * @return relative cell path
     * @see https://www.ivoa.net/documents/HiPS/20170519/REC-HIPS-1.0-20170519.pdf part 4.1
     */
    public static String resolve(int order, long cellIndex, String ext) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Norder").append(order).append('/');
        final long D = (cellIndex / 10000) * 10000;
        sb.append("Dir").append(D).append('/');
        sb.append("Npix").append(cellIndex).append(ext);
        return sb.toString();
    }

    /**
     * Get HIPS list.
     *
     * @return ApiResponse&lt;HIPSList&gt;
     */
    public ServiceRequest<Supplier<HIPSList>> getHipsList() {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", Collections.EMPTY_LIST));
        request.header("Accept", "text/plain");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                final HIPSList list = new HIPSList();
                try {
                    list.read(in);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
                return list;
            });
        });
    }

    /**
     * Get HIPS properties.
     *
     * @return ApiResponse&lt;HIPSList&gt;
     */
    public ServiceRequest<Supplier<HIPSProperties>> getHipsProperties() {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/properties", Collections.EMPTY_LIST));
        request.header("Accept", "text/plain");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                final HIPSProperties properties = new HIPSProperties();
                try {
                    properties.read(in);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
                return properties;
            });
        });
    }

    /**
     * Get HIPS cell.
     *
     * @param path relative path to the hips list document
     * @return ApiResponse&lt;HIPSList&gt;
     */
    public ServiceRequest<byte[]> getHipsCell(int order, long pixelIndex, String ext) {
        final String path = resolve(order, pixelIndex, ext);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        URI uri = toUri("/"+path, Collections.EMPTY_LIST);
        request.uri(uri);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
