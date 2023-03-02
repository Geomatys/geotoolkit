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

    private final QName resultModel;

    private final ResponseMode responseMode;

    private final String responseFormat;

    private boolean includeTimeInTemplate = false;

    private boolean includeFoiInTemplate = true;

    private boolean includeIdInDataBlock = false;

    private boolean includeTimeForProfile = false;

    private boolean includeQualityFields = true;

    private boolean separatedObservation = false;

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

    public boolean isSeparatedObservation() {
        return separatedObservation;
    }

    public void setSeparatedObservation(boolean separatedObservation) {
        this.separatedObservation = separatedObservation;
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
        query.setSeparatedObservation(separatedObservation);
        query.setResultMode(resultMode);
        applyFeatureAttributes(query);
        return query;
    }
}
