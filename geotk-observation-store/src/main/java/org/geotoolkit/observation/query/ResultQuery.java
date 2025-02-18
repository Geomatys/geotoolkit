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
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ResultQuery extends AbstractObservationQuery {

    private final QName resultModel;

    private final ResponseMode responseMode;

    private String responseFormat;

    private String procedure;

    private boolean includeTimeForProfile = false;

    private boolean includeIdInDataBlock = false;

    /**
     * Set to false if you don't want to include the quality fields in the
     * results.
     */
    private boolean includeQualityFields = true;

    /**
     * Set to false if you don't want to include the parameters fields in the
     * results.
     */
    private boolean includeParameterFields = true;

    private Integer decimationSize;

    public ResultQuery(QName resultModel, ResponseMode responseMode, String procedure, String responseFormat) {
        this(null, resultModel, responseMode, procedure, responseFormat);
    }

    public ResultQuery(Filter selection, QName resultModel, ResponseMode responseMode, String procedure, String responseFormat) {
        super(OMEntity.RESULT, selection);
        this.resultModel  = resultModel;
        this.responseMode = responseMode;
        this.procedure    = procedure;
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

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public boolean isIncludeTimeForProfile() {
        return includeTimeForProfile;
    }

    public void setIncludeTimeForProfile(boolean includeTimeForProfile) {
        this.includeTimeForProfile = includeTimeForProfile;
    }

    public boolean isIncludeIdInDataBlock() {
        return includeIdInDataBlock;
    }

    public void setIncludeIdInDataBlock(boolean includeIdInDataBlock) {
        this.includeIdInDataBlock = includeIdInDataBlock;
    }

    public Integer getDecimationSize() {
        return decimationSize;
    }

    public void setDecimationSize(Integer decimationSize) {
        this.decimationSize = decimationSize;
    }

    public boolean isIncludeQualityFields() {
        return includeQualityFields;
    }

    public void setIncludeQualityFields(boolean includeQualityFields) {
        this.includeQualityFields = includeQualityFields;
    }

    public boolean isIncludeParameterFields() {
        return includeParameterFields;
    }

    public void setIncludeParameterFields(boolean includeParameterFields) {
        this.includeParameterFields = includeParameterFields;
    }

    @Override
    public ResultQuery noPaging() {
        ResultQuery query = new ResultQuery(resultModel, responseMode, procedure, responseFormat);
        query.setDecimationSize(decimationSize);
        query.setIncludeIdInDataBlock(includeIdInDataBlock);
        query.setIncludeQualityFields(includeQualityFields);
        query.setIncludeParameterFields(includeParameterFields);
        query.setIncludeTimeForProfile(includeTimeForProfile);
        applyFeatureAttributes(query);
        return query;
    }
}
