/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.wms;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.StringUtilities;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeatureInfo extends AbstractGetMap implements GetFeatureInfoRequest {

    protected Integer columnIndex;
    protected Integer rawIndex;
    protected Integer featureCount;
    protected String infoFormat;
    protected String[] queryLayers;

    protected AbstractGetFeatureInfo(final String serverURL, final String version, final ClientSecurity security) {
        super(serverURL, version, security);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRawIndex() {
        return rawIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfoFormat() {
        return infoFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getQueryLayers() {
        return queryLayers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnIndex(final Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRawIndex(final Integer rawIndex) {
        this.rawIndex = rawIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQueryLayers(final String... layers) {
        this.queryLayers = layers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInfoFormat(final String format) {
        this.infoFormat = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFeatureCount() {
        return featureCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFeatureCount(final Integer featureCount) {
        this.featureCount = featureCount;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();

        requestParameters.put("REQUEST", "GetFeatureInfo");
        if (infoFormat == null) {
            throw new IllegalArgumentException("Info_Format is not defined");
        }

        if (queryLayers == null) {
            throw new IllegalArgumentException("Query_Layers is not defined");
        }

        requestParameters.put("INFO_FORMAT", infoFormat);
        requestParameters.put("QUERY_LAYERS", StringUtilities.toCommaSeparatedValues((Object[])queryLayers));

        if (featureCount != null && featureCount > 0)
            requestParameters.put("FEATURE_COUNT", String.valueOf(featureCount));
    }
}
