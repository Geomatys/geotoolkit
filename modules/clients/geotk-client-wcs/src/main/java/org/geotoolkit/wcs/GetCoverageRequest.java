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
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs;

import java.awt.Dimension;
import org.geotoolkit.client.Request;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetCoverageRequest extends Request {
    String getCoverage();

    void setCoverage(String coverage);

    Envelope getEnvelope();

    void setEnvelope(Envelope envelope);

    Dimension getDimension();

    void setDimension(Dimension dimension);

    String getFormat();

    void setFormat(String format);

    CoordinateReferenceSystem getResponseCRS();

    void setResponseCRS(CoordinateReferenceSystem responseCRS);

    String getTime();

    void setTime(String time);

    Double getResX();

    void setResX(Double resX);

    Double getResY();

    void setResY(Double resY);

    Double getResZ();

    void setResZ(Double resZ);

    Integer getDepth();

    void setDepth(Integer depth);

    String getExceptions();

    void setExceptions(String exceptions);
}
