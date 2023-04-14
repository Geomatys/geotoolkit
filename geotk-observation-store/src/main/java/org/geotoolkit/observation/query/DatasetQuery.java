/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.observation.query;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.sis.storage.FeatureQuery;
import static org.geotoolkit.observation.OMUtils.OBSERVATION_QNAME;
import org.geotoolkit.observation.model.ResponseMode;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DatasetQuery extends FeatureQuery {

    /**
     * If supported the store will affect this id to a (single) extracted sensor.
     */
    private final String affectedSensorID;

    /**
     * Filter on the sensor to include int the dataset.
     */
    private final List<String> sensorIds;

    /**
     * Used in observation Extraction.
     * profile values in observation results does not include by default.
     * A time field with the date of each profile will be added to complex result if set.
     */
    private boolean includeTimeForProfile = false;

    /**
     * Used in observation Extraction.
     * if set to false, each profile of a procedure will be merged in one observation.
     * Be careful if includeTimeForProfile is not set as well,
     * it will not be possible to distinguish the original profile time.
     */
    private boolean separatedProfileObservation = true;

    /**
     * Used in observation Extraction.
     * Result model of the output.
     * Can be {@link org.geotoolkit.observation.OMUtils#OBSERVATION_QNAME} for complex observation output,
     * or {@link org.geotoolkit.observation.OMUtils#MEASUREMENT_QNAME} for single measurement observation output.
     */
    private QName resultModel = OBSERVATION_QNAME;

    /**
     * Used in observation Extraction.
     * Response mode (inspired by SOS standard).
     * - INLINE: list of complete observation.
     * - ATTACHED: mostly not implemented but specify that we want the observation in a separed attachment.
     * - OUT_OF_BAND: used for other format export like netcdf for example.
     * - RESULT_TEMPLATE: return only the observation template for each sensor matching the query.
     */
    private ResponseMode responseMode = ResponseMode.INLINE;

    /**
     * Used in observation Extraction.
     * special format reponse to change the result values.
     * example:
     * - 'resultArray': values will not be transmitted in a string datablock, but as an Object array.
     * - 'text/csv': default behavior values will be transmitted in a string datablock.
     * - 'count': special case to count the number of values.
     */
    private String responseFormat = null;

    public DatasetQuery() {
        this(null, null);
    }

    public DatasetQuery(String affectedSensorID) {
        this(affectedSensorID, null);
    }

    public DatasetQuery(List<String> sensorIds) {
        this(null, sensorIds);
    }

    private DatasetQuery(String affectedSensorID, List<String> sensorIds) {
        this.affectedSensorID = affectedSensorID;
        if (sensorIds != null) {
            this.sensorIds = sensorIds;
        } else {
            this.sensorIds = new ArrayList<>();
        }
    }

    public String getAffectedSensorID() {
        return affectedSensorID;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public boolean isIncludeTimeForProfile() {
        return includeTimeForProfile;
    }

    public void setIncludeTimeForProfile(boolean includeTimeForProfile) {
        this.includeTimeForProfile = includeTimeForProfile;
    }

    public boolean isSeparatedProfileObservation() {
        return separatedProfileObservation;
    }

    public void setSeparatedProfileObservation(boolean separatedProfileObservation) {
        this.separatedProfileObservation = separatedProfileObservation;
    }

    public QName getResultModel() {
        return resultModel;
    }

    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    public ResponseMode getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(ResponseMode responseMode) {
        this.responseMode = responseMode;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
}
