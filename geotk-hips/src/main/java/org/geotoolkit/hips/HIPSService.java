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
package org.geotoolkit.hips;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class HIPSService extends AbstractService {

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

    public HIPSService(ServiceConfiguration apiClient) {
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
     * @throws ServiceException if fails to make API call or parse the response
     */
    public ServiceResponse<HIPSList> getHipsList() throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", Collections.EMPTY_LIST));
        request.header("Accept", "text/plain");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        HttpResponse<InputStream> response = send(request);
        try (InputStream in = response.body()) {
            final HIPSList list = new HIPSList();
            list.read(in);
            return new ServiceResponse<>(
                    response.statusCode(),
                    response.headers().map(),
                    list);
        } catch (IOException ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * Get HIPS properties.
     *
     * @return ApiResponse&lt;HIPSList&gt;
     * @throws ServiceException if fails to make API call or parse the response
     */
    public ServiceResponse<HIPSProperties> getHipsProperties() throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/properties", Collections.EMPTY_LIST));
        request.header("Accept", "text/plain");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        HttpResponse<InputStream> response = send(request);
        try (InputStream in = response.body()) {
            final HIPSProperties properties = new HIPSProperties();
            properties.read(in);
            return new ServiceResponse<>(
                    response.statusCode(),
                    response.headers().map(),
                    properties);
        } catch (IOException ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * Get HIPS cell.
     *
     * @param path relative path to the hips list document
     * @return ApiResponse&lt;HIPSList&gt;
     * @throws ServiceException if fails to make API call or parse the response
     */
    public ServiceResponse<byte[]> getHipsCell(int order, long pixelIndex, String ext) throws ServiceException {
        final String path = resolve(order, pixelIndex, ext);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        URI uri = toUri("/"+path, Collections.EMPTY_LIST);
        request.uri(uri);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        HttpResponse<InputStream> response = send(request);
        try (InputStream in = response.body()) {
            byte[] data = in.readAllBytes();
            return new ServiceResponse<>(
                    response.statusCode(),
                    response.headers().map(),
                    data);
        } catch (IOException ex) {
            throw new ServiceException(ex);
        }
    }
}
