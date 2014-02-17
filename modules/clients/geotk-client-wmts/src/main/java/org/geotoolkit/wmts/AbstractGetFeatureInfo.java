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
package org.geotoolkit.wmts;

import org.geotoolkit.security.ClientSecurity;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeatureInfo extends AbstractGetTile implements GetFeatureInfoRequest {

    protected Integer columnIndex;
    protected Integer rawIndex;
    protected String infoFormat;

    protected AbstractGetFeatureInfo(String serverURL, String version, final ClientSecurity security) {
        super(serverURL, version,security);
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
    public void setInfoFormat(String format) {
        this.infoFormat = format;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (infoFormat == null) {
            throw new IllegalArgumentException("Info_Format is not defined");
        }
        requestParameters.put("INFO_FORMAT", infoFormat);
        if (columnIndex == null) {
            throw new IllegalArgumentException("I is not defined");
        }
        if (rawIndex == null) {
            throw new IllegalArgumentException("J is not defined");
        }
        requestParameters.put("I", String.valueOf(columnIndex));
        requestParameters.put("J", String.valueOf(rawIndex));
    }

}
