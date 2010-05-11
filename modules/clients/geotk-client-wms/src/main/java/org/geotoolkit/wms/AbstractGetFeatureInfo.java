/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    protected String infoFormat;
    protected String[] queryLayers;

    protected AbstractGetFeatureInfo(String serverURL, String version) {
        super(serverURL, version);
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

    @Override
    public String getInfoFormat() {
        return infoFormat;
    }

    @Override
    public String[] getQueryLayers() {
        return queryLayers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRawIndex(Integer rawIndex) {
        this.rawIndex = rawIndex;
    }

    @Override
    public void setQueryLayers(String... layers) {
        this.queryLayers = layers;
    }

    @Override
    public void setInfoFormat(String format) {
        this.infoFormat = format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (infoFormat == null) {
            throw new IllegalArgumentException("Info_Format is not defined");
        }
        if (queryLayers == null) {
            throw new IllegalArgumentException("Query_Layers is not defined");
        }
        requestParameters.put("INFO_FORMAT", infoFormat);
        requestParameters.put("QUERY_LAYERS", StringUtilities.toCommaSeparatedValues(queryLayers));

        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
