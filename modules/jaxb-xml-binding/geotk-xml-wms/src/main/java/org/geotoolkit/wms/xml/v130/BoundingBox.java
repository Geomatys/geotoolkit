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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractBoundingBox;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="CRS" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minx" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="miny" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxx" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxy" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="resx" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="resy" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "BoundingBox")
public class BoundingBox extends AbstractBoundingBox{

   
    @XmlAttribute(name = "CRS")
    private String crs;
    @XmlAttribute(required = true)
    private double minx;
    @XmlAttribute(required = true)
    private double miny;
    @XmlAttribute(required = true)
    private double maxx;
    @XmlAttribute(required = true)
    private double maxy;
    @XmlAttribute
    private Double resx;
    @XmlAttribute
    private Double resy;

    /**
     * An empty constructor used by JAXB.
     */
    BoundingBox() {
    }

    /**
     * Build a new bounding box version 1.3.0.
     *
     * @param crs 
     * @param minx 
     * @param miny 
     * @param maxx
     * @param maxy
     * @param resx
     * @param resy
     * @param version
     */
    public BoundingBox(final String crs, final double minx, final double miny,
            final double maxx, final double maxy, final double resx, final double resy, final String version) {
        this.maxx = maxx;
        this.maxy = maxy;
        this.minx = minx;
        this.miny = miny;
        this.crs  = crs;
        this.resx = resx;
        this.resy = resy;
       
    }
    
    /**
     * Gets the value of the crs property.
     */
    public String getCRS() {
        return crs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMinx() {
        return minx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMiny() {
        return miny;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxx() {
        return maxx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxy() {
        return maxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getResx() {
        return resx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getResy() {
        return resy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCRSCode() {
        return getCRS();
    }


}
