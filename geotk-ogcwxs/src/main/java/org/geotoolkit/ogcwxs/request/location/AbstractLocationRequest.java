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
package org.geotoolkit.ogcwxs.request.location;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * Fields shared by every OGC OpenLS (OLS) 1.2.0 operation: the optional
 * {@code <RequestHeader>} attributes and the {@code <Request>} envelope attributes.
 *
 * <blockquote><q>
 * Defines the header information for request messages.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public abstract class AbstractLocationRequest<T extends AbstractLocationRequest<T>> extends RequestParameters {

    private String clientName;
    private String clientPassword;
    private String sessionID;
    private String srsName;
    private String msid;
    private String requestID;
    private Integer maximumResponses;

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    /**
     * Client identification, sent in the request header. Optional.
     *
     * @see #clientName(String)
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * <blockquote><q>
     * The client name, used for authentication.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType/clientName</a></small></i>
     * </blockquote>
     */
    public T clientName(String clientName) {
        this.clientName = clientName;
        return self();
    }

    /**
     * Client authentication password, sent in the request header. Optional.
     *
     * @see #clientPassword(String)
     */
    public String getClientPassword() {
        return clientPassword;
    }

    /**
     * <blockquote><q>
     * The client password, used for authentication.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType/clientPassword</a></small></i>
     * </blockquote>
     */
    public T clientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
        return self();
    }

    /**
     * Session identifier tying together a sequence of requests. Optional.
     *
     * @see #sessionID(String)
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * <blockquote><q>
     * A client-defined unique session Iidentifier, which should be returned in
     * the response header.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType/sessionID</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Must be echoed back by the service in the response header.</li>
     * </ul>
     */
    public T sessionID(String sessionID) {
        this.sessionID = sessionID;
        return self();
    }

    /**
     * Spatial reference system used for coordinates in the request. Optional.
     *
     * @see #srsName(String)
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * <blockquote><q>
     * If no srsName attribute is given, the CRS must be specified as part of the
     * larger context this geometry element is part of, e.g. a geometric
     * aggregate. Default value is WGS84
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType/srsName</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Default value: WGS84.</li>
     * </ul>
     */
    public T srsName(String srsName) {
        this.srsName = srsName;
        return self();
    }

    /**
     * Mobile subscriber id, used by the Gateway service. Optional.
     *
     * @see #msid(String)
     */
    public String getMsid() {
        return msid;
    }

    /**
     * <blockquote><q>
     * A client-defined unique Iidentifier. Can be used for different purposes,
     * for example billing.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=97">; OGC 07-074 OpenLS Core, RequestHeaderType/MSID</a></small></i>
     * </blockquote>
     */
    public T msid(String msid) {
        this.msid = msid;
        return self();
    }

    /**
     * Client-assigned request identifier. Optional.
     *
     * @see #requestID(String)
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * <blockquote><q>
     * A client-defined request identifier, unique within the scope of the
     * session. The request identifier must be returned in the service response.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=98">; OGC 07-074 OpenLS Core, RequestType/requestID</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Required on the {@code <Request>} envelope.</li>
     *   <li>Must be returned unchanged in the service response.</li>
     * </ul>
     */
    public T requestID(String requestID) {
        this.requestID = requestID;
        return self();
    }

    /**
     * Maximum number of responses the client accepts. Optional.
     *
     * @see #maximumResponses(Integer)
     */
    public Integer getMaximumResponses() {
        return maximumResponses;
    }

    /**
     * <blockquote><q>
     * This parameter will allow the request to control the number of responses
     * generated. For example the POI request will use this as a contraint to
     * generate a certain number of POIs.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=98">; OGC 07-074 OpenLS Core, RequestType/maximumResponses</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Not applicable to operations whose single response can itself contain multiple values.</li>
     * </ul>
     */
    public T maximumResponses(Integer maximumResponses) {
        this.maximumResponses = maximumResponses;
        return self();
    }
}
