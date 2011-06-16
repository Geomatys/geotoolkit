/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * Abstract get coverage request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCoverage extends AbstractRequest implements GetCoverageRequest {
    /**
     * Default logger for all GetCoverage requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetCoverage.class);

    protected final String version;

    private String coverage = null;
    private Dimension dimension = null;
    private Envelope envelope = null;
    private String format = null;
    private String exceptions = null;
    private Double resX = null;
    private Double resY = null;
    private Double resZ = null;
    private Integer depth = null;
    private CoordinateReferenceSystem responseCRS = null;
    private String time = null;

    protected AbstractGetCoverage(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    @Override
    public String getCoverage() {
        return coverage;
    }

    @Override
    public Integer getDepth() {
        return depth;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public Envelope getEnvelope() {
        return envelope;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getExceptions() {
        return exceptions;
    }

    @Override
    public Double getResX() {
        return resX;
    }

    @Override
    public Double getResY() {
        return resY;
    }

    @Override
    public Double getResZ() {
        return resZ;
    }

    @Override
    public CoordinateReferenceSystem getResponseCRS() {
        return responseCRS;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void setCoverage(final String coverage) {
        this.coverage = coverage;
    }

    @Override
    public void setDepth(final Integer depth) {
        this.depth = depth;
    }

    @Override
    public void setDimension(final Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public void setEnvelope(final Envelope envelope) {
        this.envelope = envelope;
    }

    @Override
    public void setExceptions(final String exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public void setFormat(final String format) {
        this.format = format;
    }

    @Override
    public void setResX(final Double resX) {
        this.resX = resX;
    }

    @Override
    public void setResY(final Double resY) {
        this.resY = resY;
    }

    @Override
    public void setResZ(final Double resZ) {
        this.resZ = resZ;
    }

    @Override
    public void setResponseCRS(final CoordinateReferenceSystem responseCRS) {
        this.responseCRS = responseCRS;
    }

    @Override
    public void setTime(final String time) {
        this.time = time;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (coverage == null) {
            throw new IllegalArgumentException("Coverage is not defined");
        }
        if (envelope == null) {
            throw new IllegalArgumentException("An envelope must be defined");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format is not defined");
        }
        requestParameters.put("SERVICE",  "WCS");
        requestParameters.put("REQUEST",  "GetCoverage");
        requestParameters.put("VERSION",  version);
        requestParameters.put("COVERAGE", coverage);
        requestParameters.putAll(toString(envelope));
        requestParameters.put("FORMAT",   format);
        if (time != null) {
            requestParameters.put("TIME", time);
        }
        if (dimension != null) {
            requestParameters.put("WIDTH", String.valueOf(dimension.width));
            requestParameters.put("HEIGHT", String.valueOf(dimension.height));
            if (depth != null) {
                requestParameters.put("DEPTH", String.valueOf(depth));
            }
        }
        if (resX != null && resY != null) {
            requestParameters.put("RESX", String.valueOf(resX));
            requestParameters.put("RESY", String.valueOf(resY));
            if (resZ != null) {
                requestParameters.put("RESZ", String.valueOf(resZ));
            }
        }
        if (exceptions != null) {
            requestParameters.put("EXCEPTIONS", exceptions);
        }
        if (responseCRS != null) {
            try {
                requestParameters.put("RESPONSECRS", IdentifiedObjects.lookupIdentifier(responseCRS, false));
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return super.getURL();
    }

    private Map<String,String> toString(final Envelope envelope) {
        final Map<String,String> params = new HashMap<String,String>();
        final StringBuilder sb = new StringBuilder();
        final double minx = envelope.getMinimum(0);
        final double maxx = envelope.getMaximum(0);
        final double miny = envelope.getMinimum(1);
        final double maxy = envelope.getMaximum(1);
        sb.append(minx).append(',').append(miny).append(',').append(maxx).append(',').append(maxy);
        if (envelope.getDimension() > 2) {
            sb.append(',').append(envelope.getMinimum(2)).append(',').append(envelope.getMaximum(2));
        }
        params.put("BBOX", sb.toString());
        try {
            CoordinateReferenceSystem crs2d = CRSUtilities.getCRS2D(envelope.getCoordinateReferenceSystem());
            params.put("CRS", IdentifiedObjects.lookupIdentifier(crs2d, true));
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return params;
    }
}
