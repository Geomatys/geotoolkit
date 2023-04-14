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

import javax.xml.namespace.QName;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.ResponseMode;
import org.geotoolkit.observation.model.ResultMode;
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationQuery extends AbstractObservationQuery {

    /**
     * Result model of the output.
     * Can be {@link org.geotoolkit.observation.OMUtils#OBSERVATION_QNAME} for complex observation output,
     * or {@link org.geotoolkit.observation.OMUtils#MEASUREMENT_QNAME} for single measurement observation output.
     */
    private final QName resultModel;

    /**
     * Response mode (inspired by SOS standard).
     * - INLINE: list of complete observation.
     * - ATTACHED: mostly not implemented but specify that we want the observation in a separed attachment.
     * - OUT_OF_BAND: used for other format export like netcdf for example.
     * - RESULT_TEMPLATE: return only the observation template for each sensor matching the query.
     */
    private final ResponseMode responseMode;

    /**
     * special format reponse to change the result values.
     * example:
     * - 'resultArray': values will not be transmitted in a string datablock, but as an Object array.
     * - 'text/csv': default behavior values will be transmitted in a string datablock.
     * - 'count': special case to count the number of values.
     */
    private final String responseFormat;

    private boolean includeTimeInTemplate = false;

    private boolean includeFoiInTemplate = true;

    private boolean includeIdInDataBlock = false;

    /**
     * profile values in observation results does not include by default.
     * A time field with the date of each profile will be added to complex result if set.
     */
    private boolean includeTimeForProfile = false;

    private boolean includeQualityFields = true;

    /**
     * if set to true, each measure will be separated in its own observation.
     */
    private boolean separatedMeasure = false;

    /**
     * if set to false, each profile of a procedure will be merged in one observation.
     */
    private boolean separatedProfileObservation = true;

    private Integer decimationSize;

    private ResultMode resultMode;

    public ObservationQuery(QName resultModel, ResponseMode responseMode, String responseFormat) {
        this(null, resultModel, responseMode, responseFormat);
    }

    public ObservationQuery(Filter selection, QName resultModel, ResponseMode responseMode, String responseFormat) {
        super(OMEntity.OBSERVATION, selection);
        this.resultModel    = resultModel;
        this.responseMode   = responseMode;
        this.responseFormat = responseFormat;
    }

    public QName getResultModel() {
        return resultModel;
    }

    public ResponseMode getResponseMode() {
        return responseMode;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public boolean isIncludeTimeInTemplate() {
        return includeTimeInTemplate;
    }

    public void setIncludeTimeInTemplate(boolean includeTimeInTemplate) {
        this.includeTimeInTemplate = includeTimeInTemplate;
    }

    public boolean isIncludeFoiInTemplate() {
        return includeFoiInTemplate;
    }

    public void setIncludeFoiInTemplate(boolean includeFoiInTemplate) {
        this.includeFoiInTemplate = includeFoiInTemplate;
    }

    public boolean isIncludeIdInDataBlock() {
        return includeIdInDataBlock;
    }

    public void setIncludeIdInDataBlock(boolean includeIdInDataBlock) {
        this.includeIdInDataBlock = includeIdInDataBlock;
    }

    public boolean isIncludeTimeForProfile() {
        return includeTimeForProfile;
    }

    public void setIncludeTimeForProfile(boolean includeTimeForProfile) {
        this.includeTimeForProfile = includeTimeForProfile;
    }

    public boolean isSeparatedMeasure() {
        return separatedMeasure;
    }

    public void setSeparatedMeasure(boolean separatedMeasure) {
        this.separatedMeasure = separatedMeasure;
    }

    public boolean isSeparatedProfileObservation() {
        return separatedProfileObservation;
    }

    public void setSeparatedProfileObservation(boolean separatedProfileObservation) {
        this.separatedProfileObservation = separatedProfileObservation;
    }

    public Integer getDecimationSize() {
        return decimationSize;
    }

    public void setDecimationSize(Integer decimationSize) {
        this.decimationSize = decimationSize;
    }

    public ResultMode getResultMode() {
        return resultMode;
    }

    public void setResultMode(ResultMode resultMode) {
        this.resultMode = resultMode;
    }

    public boolean isIncludeQualityFields() {
        return includeQualityFields;
    }

    public void setIncludeQualityFields(boolean includeQualityFields) {
        this.includeQualityFields = includeQualityFields;
    }

    @Override
    public ObservationQuery noPaging() {
        ObservationQuery query = new ObservationQuery(resultModel, responseMode, responseFormat);
        query.setDecimationSize(decimationSize);
        query.setIncludeFoiInTemplate(includeFoiInTemplate);
        query.setIncludeIdInDataBlock(includeIdInDataBlock);
        query.setIncludeQualityFields(includeQualityFields);
        query.setIncludeTimeForProfile(includeTimeForProfile);
        query.setIncludeTimeInTemplate(includeTimeInTemplate);
        query.setSeparatedMeasure(separatedMeasure);
        query.setResultMode(resultMode);
        applyFeatureAttributes(query);
        return query;
    }
}
