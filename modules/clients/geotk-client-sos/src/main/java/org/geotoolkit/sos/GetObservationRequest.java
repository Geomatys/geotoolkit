/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General License for more details.
 */
package org.geotoolkit.sos;

import javax.xml.namespace.QName;
import org.geotoolkit.client.Request;
import org.geotoolkit.sos.xml.v100.EventTime;
import org.geotoolkit.sos.xml.v100.GetObservation;
import org.geotoolkit.sos.xml.v100.ResponseModeType;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetObservationRequest extends Request {

    EventTime[] getEventTimes();

    GetObservation.FeatureOfInterest getFeatureOfInterest();

    String[] getObservedProperties();

    String getOffering();

    String[] getProcedures();

    String getResponseFormat();

    ResponseModeType getResponseMode();

    GetObservation.Result getResult();

    QName getResultModel();

    String getSrsName();

    void setEventTimes(EventTime... eventTimes);

    void setFeatureOfInterest(GetObservation.FeatureOfInterest featureOfInterest);

    void setObservedProperties(String... observedProperties);

    void setOffering(String offering);

    void setProcedures(String... procedures);

    void setResponseFormat(String responseFormat);

    void setResponseMode(ResponseModeType responseMode);

    void setResult(GetObservation.Result result);

    void setResultModel(QName resultModel);

    void setSrsName(String srsName);

}
