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
package org.geotoolkit.ogcwxs.request.observation;

import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.sos.xml.ResponseModeType;

/**
 * To retrieve an observation identified by its id.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetObservationById extends RequestParameters {

    private String observationId;
    private String srsName;
    private String responseFormat;
    private QName resultModel;
    private ResponseModeType responseMode;
    private SOSVersion version;

    /**
     * Identifier of the observation to retrieve. (required)
     *
     * @return the observationId
     */
    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public GetObservationById observationId(String observationId) {
        setObservationId(observationId);
        return this;
    }

    public String getSrsName() {
        return srsName;
    }

    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    public GetObservationById srsName(String srsName) {
        setSrsName(srsName);
        return this;
    }

    /**
     * Requested response format. (required)
     *
     * @return the responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public GetObservationById responseFormat(String responseFormat) {
        setResponseFormat(responseFormat);
        return this;
    }

    public QName getResultModel() {
        return resultModel;
    }

    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    public GetObservationById resultModel(QName resultModel) {
        setResultModel(resultModel);
        return this;
    }

    public ResponseModeType getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(ResponseModeType responseMode) {
        this.responseMode = responseMode;
    }

    public GetObservationById responseMode(ResponseModeType responseMode) {
        setResponseMode(responseMode);
        return this;
    }

    public SOSVersion getVersion() {
        return version;
    }

    public void setVersion(SOSVersion version) {
        this.version = version;
    }

    public GetObservationById version(SOSVersion version) {
        setVersion(version);
        return this;
    }

}
