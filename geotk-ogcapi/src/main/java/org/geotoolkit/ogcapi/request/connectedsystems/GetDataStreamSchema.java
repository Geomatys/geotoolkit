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
package org.geotoolkit.ogcapi.request.connectedsystems;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /datastreams/{dataStreamId}/schema}: retrieves the
 * result/parameter schema of a DataStream, encoded in the given observation format.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetDataStreamSchema extends RequestParameters {

    private String dataStreamId;
    private String obsFormat;

    /**
     * Local identifier of the data stream. (required)
     *
     * @return the dataStreamId
     */
    public String getDataStreamId() {
        return dataStreamId;
    }

    /**
     * @param dataStreamId the dataStreamId to set
     * @see #getDataStreamId()
     */
    public void setDataStreamId(String dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    /**
     * @param dataStreamId the dataStreamId to set
     * @see #getDataStreamId()
     */
    public GetDataStreamSchema dataStreamId(String dataStreamId) {
        setDataStreamId(dataStreamId);
        return this;
    }

    /**
     * The observation format (media type) for which the schema is requested. (required)
     *
     * @return the obsFormat
     */
    public String getObsFormat() {
        return obsFormat;
    }

    /**
     * @param obsFormat the obsFormat to set
     * @see #getObsFormat()
     */
    public void setObsFormat(String obsFormat) {
        this.obsFormat = obsFormat;
    }

    /**
     * @param obsFormat the obsFormat to set
     * @see #getObsFormat()
     */
    public GetDataStreamSchema obsFormat(String obsFormat) {
        setObsFormat(obsFormat);
        return this;
    }

}
