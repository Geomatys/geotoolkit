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
package org.geotoolkit.ols.xml;

import java.math.BigDecimal;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.ols.xml.v121.AbstractBodyType;
import org.geotoolkit.ols.xml.v121.AbstractRequestParametersType;
import org.geotoolkit.ols.xml.v121.AbstractResponseParametersType;
import org.geotoolkit.ols.xml.v121.ErrorListType;
import org.geotoolkit.ols.xml.v121.ErrorType;
import org.geotoolkit.ols.xml.v121.ObjectFactory;
import org.geotoolkit.ols.xml.v121.RequestHeaderType;
import org.geotoolkit.ols.xml.v121.RequestType;
import org.geotoolkit.ols.xml.v121.ResponseType;
import org.geotoolkit.ols.xml.v121.XLSType;

/**
 * Builds and reads the OGC OpenLS (OLS) 1.2.0 {@code <XLS>} request/response envelope.
 * OpenLS has a single version (1.2.0), so unlike {@code WFSXmlFactory}/{@code WMSXmlFactory} no
 * per-version dispatch is needed here.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class OLSXmlFactory {

    /** Value of the {@code XLS/@version} attribute, per OGC 07-074. */
    public static final BigDecimal VERSION = new BigDecimal("1.2");

    private static final ObjectFactory FACTORY = new ObjectFactory();
    private static final String DEFAULT_LANG = "en";

    private OLSXmlFactory() {}

    /**
     * Build a request header.
     *
     * @param clientName client identification (optional)
     * @param clientPassword client authentication password (optional)
     * @param sessionID session identifier tying together a sequence of requests (optional)
     * @param srsName spatial reference system to use for coordinates in the request (optional)
     * @param msid mobile subscriber id, used by the Gateway service (optional)
     * @return the request header
     */
    public static RequestHeaderType buildRequestHeader(String clientName, String clientPassword,
            String sessionID, String srsName, String msid) {
        final RequestHeaderType header = new RequestHeaderType();
        header.setClientName(clientName);
        header.setClientPassword(clientPassword);
        header.setSessionID(sessionID);
        header.setSrsName(srsName);
        header.setMSID(msid);
        return header;
    }

    /**
     * Wrap a single operation-specific request payload into a full {@code <XLS>} envelope.
     *
     * @param header request header, may be {@code null}
     * @param methodName OLS operation name (e.g. {@code "GeocodeRequest"})
     * @param requestID client-assigned request identifier, may be {@code null}
     * @param maximumResponses maximum number of responses the client accepts, may be {@code null}
     * @param requestParameters the operation-specific request payload, wrapped through the
     * matching {@link ObjectFactory#createGeocodeRequest(org.geotoolkit.ols.xml.v121.GeocodeRequestType)}
     * (or equivalent) factory method
     * @return the request envelope, ready to be marshalled by {@link OLSBindingUtilities#marshall(XLSType)}
     */
    public static XLSType buildRequestEnvelope(RequestHeaderType header, String methodName, String requestID,
            Integer maximumResponses, JAXBElement<? extends AbstractRequestParametersType> requestParameters) {
        final RequestType request = new RequestType();
        request.setMethodName(methodName);
        request.setVersion(VERSION.toPlainString());
        request.setRequestID(requestID);
        request.setMaximumResponses(maximumResponses);
        request.setRequestParameters(requestParameters);

        final XLSType xls = new XLSType();
        xls.setVersion(VERSION);
        xls.setLang(DEFAULT_LANG);
        if (header != null) {
            xls.setHeader(FACTORY.createRequestHeader(header));
        }
        xls.getBody().add(FACTORY.createRequest(request));
        return xls;
    }

    /**
     * Extract the {@code <Response>} body of an unmarshalled envelope.
     *
     * @param xls the unmarshalled response envelope
     * @return the response body
     * @throws JAXBException if the envelope does not contain a {@code <Response>} body
     */
    public static ResponseType extractResponse(XLSType xls) throws JAXBException {
        for (JAXBElement<? extends AbstractBodyType> element : xls.getBody()) {
            final AbstractBodyType body = element.getValue();
            if (body instanceof ResponseType response) {
                return response;
            }
        }
        throw new JAXBException("No <Response> body found in the OLS envelope");
    }

    /**
     * Extract and check the operation-specific response payload out of an unmarshalled envelope.
     * If the response carries an {@code <ErrorList>}, this throws instead of returning.
     *
     * @param <T> expected response parameters type, e.g. {@code GeocodeResponseListType}
     * @param xls the unmarshalled response envelope
     * @param type expected response parameters type
     * @return the operation-specific response payload
     * @throws JAXBException if the envelope carries an OLS error, or the payload type does not match
     */
    public static <T extends AbstractResponseParametersType> T extractResponseParameters(XLSType xls, Class<T> type)
            throws JAXBException {
        final ResponseType response = extractResponse(xls);
        checkError(response.getErrorList());
        final JAXBElement<? extends AbstractResponseParametersType> parameters = response.getResponseParameters();
        final AbstractResponseParametersType value = parameters == null ? null : parameters.getValue();
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new JAXBException(String.format(
                "Read object is not compatible with queried type.%nExpected: %s%nBut was: %s",
                type, value == null ? null : value.getClass()));
    }

    /**
     * Turn a non-empty {@code <ErrorList>} into an exception.
     *
     * @param errorList error list carried by a response header or body, may be {@code null}
     * @throws JAXBException if {@code errorList} carries at least one error
     */
    public static void checkError(ErrorListType errorList) throws JAXBException {
        if (errorList == null || errorList.getError() == null || errorList.getError().isEmpty()) {
            return;
        }
        final StringBuilder message = new StringBuilder("OLS request failed");
        if (errorList.getHighestSeverity() != null) {
            message.append(" (").append(errorList.getHighestSeverity()).append(')');
        }
        message.append(':');
        for (ErrorType error : errorList.getError()) {
            message.append(System.lineSeparator()).append(" - ").append(error.getMessage());
        }
        throw new JAXBException(message.toString());
    }
}
