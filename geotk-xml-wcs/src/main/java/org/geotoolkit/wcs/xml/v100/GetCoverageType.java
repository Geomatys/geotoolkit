/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml.v100;

import java.util.Collections;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.VerticalCRS;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.GeodeticObjectBuilder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.Version;

import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.GridEnvelopeType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.wcs.xml.GetCoverage;
import org.geotoolkit.wcs.xml.StringUtilities;
import org.geotoolkit.wcs.xml.DomainSubset;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;

/**
 * <p>An xml binding class for a getCoverage request.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sourceCoverage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="domainSubset" type="{http://www.opengis.net/wcs}DomainSubsetType"/>
 *         &lt;element name="rangeSubset" type="{http://www.opengis.net/wcs}RangeSubsetType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}interpolationMethod" minOccurs="0"/>
 *         &lt;element name="output" type="{http://www.opengis.net/wcs}OutputType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WCS" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @author Cédric Briançon (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sourceCoverage",
    "domainSubset",
    "rangeSubset",
    "interpolationMethod",
    "output"
})
@XmlRootElement(name = "GetCoverage")
public class GetCoverageType implements GetCoverage {

    @XmlElement(required = true)
    private String sourceCoverage;
    @XmlElement(required = true)
    private DomainSubsetType domainSubset;
    private RangeSubsetType rangeSubset;
    private String interpolationMethod;
    @XmlElement(required = true)
    private OutputType output;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.wcs.xml.v100");

    /**
     * Empty constructor used by JAXB.
     */
    GetCoverageType() {
    }

    /**
     * Build a new GetCoverage request (1.0.0)
     */
    public GetCoverageType(final String sourceCoverage, final DomainSubsetType domainSubset,
            final RangeSubsetType rangeSubset, final String interpolationMethod,
            final OutputType output) {

        this.domainSubset        = domainSubset;
        this.interpolationMethod = interpolationMethod;
        this.output              = output;
        this.rangeSubset         = rangeSubset;
        this.service             = "WCS";
        this.sourceCoverage      = sourceCoverage;
        this.version             = "1.0.0";

    }
    /**
     * Gets the value of the sourceCoverage property.
     */
    public String getSourceCoverage() {
        return sourceCoverage;
    }

    /**
     * Gets the value of the domainSubset property.
     */
    @Override
    public List<DomainSubset> getDomainSubset() {
        final List<DomainSubset> result = new ArrayList<>();
        if (domainSubset != null) {
            result.add(domainSubset);
        }
        return result;
    }

    /**
     * Gets the value of the rangeSubset property.
     */
    @Override
    public RangeSubsetType getRangeSubset() {
        return rangeSubset;
    }

    /**
     * Spatial interpolation method to be used in resampling data from its original
     * form to the requested CRS and/or grid size.
     * Method shall be among those listed for the requested coverage in the DescribeCoverage response.
     */
    @Override
    public InterpolationMethod getInterpolationMethod() {
        if (interpolationMethod != null) {
            return InterpolationMethod.fromValue(interpolationMethod);
        }
        return null;
    }

    /**
     * Gets the value of the output property.
     */
    public OutputType getOutput() {
        return output;
    }

    /**
     * Gets the value of the service property.
     */
    @Override
    public String getService() {
        if (service == null) {
            return "WCS";
        } else {
            return service;
        }
    }

    @Override
    public void setService(final String value) {
        this.service = value;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public Version getVersion() {
         if (version != null) {
            return new Version(version);
        }
        return null;
    }

    @Override
    public void setVersion(final String value) {
        this.version = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getCRS() throws FactoryException {
        if (domainSubset == null || domainSubset.getSpatialSubSet() == null ||
            domainSubset.getSpatialSubSet().getEnvelope() == null)
        {
            return null;
        }
        final CoordinateReferenceSystem objCrs = AbstractCRS.castOrCopy(CRS.forCode(
                domainSubset.getSpatialSubSet().getEnvelope().getSrsName())).forConvention(AxesConvention.RIGHT_HANDED);
        final List<DirectPositionType> positions = domainSubset.getSpatialSubSet().getEnvelope().getPos();

        /*
         * If the bounding box contains at least 3 dimensions and the CRS specified is just
         * a 2D one, then we have to add a VerticalCRS to the one gotten by the crs decoding step.
         * Otherwise the CRS decoded is already fine, and we just return it.
         */
        if (positions.get(0).getDimension() > 2 && objCrs.getCoordinateSystem().getDimension() < 3) {
            final VerticalCRS verticalCRS = CommonCRS.Vertical.ELLIPSOIDAL.crs();
            return new GeodeticObjectBuilder().addName(objCrs.getName().getCode() + " (3D)")
                                              .createCompoundCRS(objCrs, verticalCRS);
        } else {
            return objCrs;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCoverage() {
        return sourceCoverage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getEnvelope() throws FactoryException {
        if (domainSubset == null || domainSubset.getSpatialSubSet() == null ||
            domainSubset.getSpatialSubSet().getEnvelope() == null)
        {
            return null;
        }
        final EnvelopeType env = domainSubset.getSpatialSubSet().getEnvelope();
        final List<DirectPositionType> positions = env.getPos();
        if (positions == null || positions.isEmpty()) {
            return null;
        }
        final DirectPositionType lows = positions.get(0);
        final DirectPositionType highs = positions.get(1);
        final CoordinateReferenceSystem crs = getCRS();
        final GeneralEnvelope objEnv = new GeneralEnvelope(crs);
        objEnv.setRange(0, lows.getValue().get(0), highs.getValue().get(0));
        objEnv.setRange(1, lows.getValue().get(1), highs.getValue().get(1));

        // If the CRS has a vertical part, then the envelope to return should be a 3D one.
        if (CRS.getVerticalComponent(crs, true) != null) {
            objEnv.setRange(2, lows.getValue().get(2), highs.getValue().get(2));
        }
        return objEnv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat() {
        if (output == null || output.getFormat() == null) {
            return null;
        }
        return output.getFormat().getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Double> getResolutions() {
        if (output == null) {
            return null;
        }
        return output.getResolutions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getResponseCRS() throws FactoryException {
        if (output == null || output.getCrs() == null || output.getCrs().getValue() == null) {
            return null;
        }
        final CoordinateReferenceSystem objCrs = CRS.forCode(output.getCrs().getValue());
        final List<DirectPositionType> positions = domainSubset.getSpatialSubSet().getEnvelope().getPos();

        /*
         * If the bounding box contains at least 3 dimensions and the CRS specified is just
         * a 2D one, then we have to add a VerticalCRS to the one gotten by the crs decoding step.
         * Otherwise the CRS decoded is already fine, and we just return it.
         */
        if (positions.get(0).getDimension() > 2 && objCrs.getCoordinateSystem().getDimension() < 3) {
            final VerticalCRS verticalCRS = CommonCRS.Vertical.ELLIPSOIDAL.crs();
            return new GeodeticObjectBuilder().addName(objCrs.getName().getCode() + " (3D)")
                                              .createCompoundCRS(objCrs, verticalCRS);
        } else {
            return objCrs;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getSize() {
        if (domainSubset == null || domainSubset.getSpatialSubSet() == null                 ||
            domainSubset.getSpatialSubSet().getGrid() == null                               ||
            domainSubset.getSpatialSubSet().getGrid().getLimits() == null                   ||
            domainSubset.getSpatialSubSet().getGrid().getLimits().getGridEnvelope() == null)
        {
            return null;
        }
        final GridEnvelopeType gridEnv = domainSubset.getSpatialSubSet().getGrid().getLimits().getGridEnvelope();
        GridCoordinates high = gridEnv.getHigh();
        if (high == null) {
            return null;
        }
        if (high.getCoordinateValues().length < 2) {
            return null;
        }
        final int width  = Math.toIntExact(high.getCoordinateValue(0));
        final int height = Math.toIntExact(high.getCoordinateValue(1));
        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        if (domainSubset == null || domainSubset.getTemporalSubSet() == null ||
            domainSubset.getTemporalSubSet().getTimePositionOrTimePeriod() == null)
        {
            return null;
        }
        final List<Object> times = domainSubset.getTemporalSubSet().getTimePositionOrTimePeriod();
        final Object timeObj = times.get(0);
        if (timeObj instanceof TimePositionType) {
            return ((TimePositionType) timeObj).getValue();
        } else {
            return null;
        }
    }

    @Override
    public String toKvp() {
        String kvp;
        try {
            kvp = "request=GetCapabilities&service="+ getService() +"&version="+ getVersion() +"&coverage="+
                     getCoverage() +"&bbox="+ StringUtilities.toBboxValue(getEnvelope()) +"&crs="+
                     StringUtilities.toCrsCode(getEnvelope()) +"&format="+ StringUtilities.toFormat(getFormat()) +
                     "&width="+ getSize().getWidth() +"&height="+ getSize().getHeight();
            final String time = getTime();
            if (time != null) {
                kvp += "&time="+ time;
            }
        } catch (FactoryException ex) {
            LOGGER.log(Level.INFO, null, ex);
            return null;
        }
        return kvp;
    }

    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    @Override
    public Object getExtension() {
        return null;
    }

    @Override
    public String getMediaType() {
        return null;
    }
}
