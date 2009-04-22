/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml.v111;

import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wcs.xml.GetCoverage;

import org.geotoolkit.wcs.xml.StringUtilities;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;

import org.geotoolkit.util.Version;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.VerticalCRS;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs/1.1.1}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element name="DomainSubset" type="{http://www.opengis.net/wcs/1.1.1}DomainSubsetType"/>
 *         &lt;element name="RangeSubset" type="{http://www.opengis.net/wcs/1.1.1}RangeSubsetType" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wcs/1.1.1}OutputType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Cédric Briançon (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "domainSubset",
    "rangeSubset",
    "output"
})
@XmlRootElement(name = "GetCoverage")
public class GetCoverageType implements GetCoverage {

    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;
    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    private CodeType identifier;
    @XmlElement(name = "DomainSubset", required = true)
    private DomainSubsetType domainSubset;
    @XmlElement(name = "RangeSubset")
    private RangeSubsetType rangeSubset;
    @XmlElement(name = "Output", required = true)
    private OutputType output;

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.wcs.xml.v111");

     /**
     * Empty constructor used by JAXB.
     */
    GetCoverageType() {
    }
    
    /**
     * Build a new GetCoverage request (1.1.1)
     */
    public GetCoverageType(CodeType identifier, DomainSubsetType domainSubset,
            RangeSubsetType rangeSubset, OutputType output) {
        
        this.domainSubset        = domainSubset;
        this.output              = output;
        this.rangeSubset         = rangeSubset;
        this.service             = "WCS";
        this.identifier          = identifier;
        this.version             = "1.1.1";
        
    }
    
    /**
     * Identifier of the coverage that this GetCoverage operation request shall draw from. 
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Gets the value of the domainSubset property.
     */
    public DomainSubsetType getDomainSubset() {
        return domainSubset;
    }

    /**
     * Gets the value of the rangeSubset property.
     */
    public RangeSubsetType getRangeSubset() {
        return rangeSubset;
    }

    /**
     * Gets the value of the output property.
     */
    public OutputType getOutput() {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Version getVersion() {
        return new Version(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getCRS() throws FactoryException {
        if (domainSubset == null || domainSubset.getBoundingBox() == null) {
            return null;
        }
        final BoundingBoxType boundingBox = domainSubset.getBoundingBox().getValue();
        final CoordinateReferenceSystem objCrs = CRS.decode(boundingBox.getCrs(), true);

        //final List<DirectPositionType> positions = domainSubset.getSpatialSubSet().getEnvelope().getPos();

        /*
         * If the bounding box contains at least 3 dimensions and the CRS specified is just
         * a 2D one, then we have to add a VerticalCRS to the one gotten by the crs decoding step.
         * Otherwise the CRS decoded is already fine, and we just return it.
         */
        if (boundingBox.getUpperCorner().size() > 2 && objCrs.getCoordinateSystem().getDimension() < 3) {
            final VerticalCRS verticalCRS = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
            return new DefaultCompoundCRS(objCrs.getName().getCode() + " (3D)", objCrs, verticalCRS);
        } else {
            return objCrs;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCoverage() {
        if (identifier == null) {
            return null;
        }
        return identifier.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getEnvelope() throws FactoryException {
        if (domainSubset == null || domainSubset.getBoundingBox() == null) {
            return null;
        }
        final BoundingBoxType boundingBox = domainSubset.getBoundingBox().getValue();
        final List<Double> lowerCorner = boundingBox.getLowerCorner();
        final List<Double> upperCorner = boundingBox.getUpperCorner();
        final CoordinateReferenceSystem crs = getCRS();
        final GeneralEnvelope objEnv = new GeneralEnvelope(crs);
        objEnv.setRange(0, lowerCorner.get(0), upperCorner.get(0));
        objEnv.setRange(1, lowerCorner.get(1), upperCorner.get(1));

        // If the CRS has a vertical part, then the envelope to return should be a 3D one.
        if (CRS.getVerticalCRS(crs) != null) {
            objEnv.setRange(2, lowerCorner.get(2), upperCorner.get(2));
        }
        return objEnv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat() {
        if (output == null) {
            return null;
        }
        return output.getFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getResponseCRS() throws FactoryException {
        if (output == null || output.getGridCRS() == null) {
            return null;
        }
        final CoordinateReferenceSystem objCrs = CRS.decode(output.getGridCRS().getSrsName().getValue());
        final BoundingBoxType boundingBox = domainSubset.getBoundingBox().getValue();

        /*
         * If the bounding box contains at least 3 dimensions and the CRS specified is just
         * a 2D one, then we have to add a VerticalCRS to the one gotten by the crs decoding step.
         * Otherwise the CRS decoded is already fine, and we just return it.
         */
        if (boundingBox.getDimensions().intValue() > 2 && objCrs.getCoordinateSystem().getDimension() < 3) {
            final VerticalCRS verticalCRS = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
            return new DefaultCompoundCRS(objCrs.getName().getCode() + " (3D)", objCrs, verticalCRS);
        } else {
            return objCrs;
        }
    }

    /**
     * Gets the value of the service property.
     */
    public String getService() {
        if (service == null) {
            return "WCS";
        } else {
            return service;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getSize() {
        /* TODO: get the width and height parameter from the calculation using the grid origin, the size
         * of the envelope and the grid offsets.
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        if (domainSubset == null || domainSubset.getTemporalSubset() == null) {
            return null;
        }
        final List<Object> times = domainSubset.getTemporalSubset().getTimePositionOrTimePeriod();
        final Object obj = times.get(0);
        if (obj instanceof TimePositionType) {
            return ((TimePositionType) obj).getValue();
        } else {
            return null;
        }
    }

    @Override
    public String toKvp() {
        String kvp;
        try {
            kvp = "request=GetCapabilities&service="+ getService() +"&version="+ getVersion() +"&identifier="+
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
}
