/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.*;
import java.util.HashMap;
import org.apache.sis.util.Version;
import org.geotoolkit.wfs.xml.GetFeature;
import org.geotoolkit.wfs.xml.ResolveValueType;
import org.geotoolkit.wfs.xml.ResultTypeType;
import org.geotoolkit.wfs.xml.StoredQuery;


/**
 * A GetFeature element contains one or more Query elements
 * that describe a query operation on one feature type.
 * In response to a GetFeature request, a Web Feature Service
 * must be able to generate a GML2 response that validates
 * using a schema generated by the DescribeFeatureType request.
 * A Web Feature Service may support other possibly non-XML
 * (and even binary) output formats as long as those formats
 * are advertised in the capabilities document.
 *
 *
 * <p>Java class for GetFeatureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetFeatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}Query" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WFS" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="GML2" />
 *       &lt;attribute name="maxFeatures" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureType", propOrder = {
    "query"
})
public class GetFeatureType implements GetFeature {

    @XmlElement(name = "Query", required = true)
    private List<QueryType> query;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    private String handle;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxFeatures;


    @XmlTransient
    private Map<String, String> prefixMapping;

    public GetFeatureType() {

    }

    public GetFeatureType(final String service, final String version, final String handle, final Integer maxFeatures,
            final List<QueryType> query, final String outputformat) {
        this.service = service;
        this.version = version;
        this.handle = handle;
        this.maxFeatures  = maxFeatures;
        this.query        = query;
        this.outputFormat = outputformat;
    }

    /**
     * Gets the value of the query property.
     */
    @Override
    public List<QueryType> getQuery() {
        if (query == null) {
            query = new ArrayList<QueryType>();
        }
        return this.query;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public Version getVersion() {
        if (version == null) {
            return new Version("1.0.0");
        } else {
            return new Version(version);
        }
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getService() {
        if (service == null) {
            return "WFS";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the handle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setHandle(String value) {
        this.handle = value;
    }

    /**
     * Gets the value of the outputFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "GML2";
        } else {
            return outputFormat;
        }
    }

    /**
     * Sets the value of the outputFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the maxFeatures property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    /**
     * Sets the value of the maxFeatures property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxFeatures(Integer value) {
        this.maxFeatures = value;
    }

    @Override
    public int getCount() {
        if (maxFeatures != null) {
            return maxFeatures;
        }
        return 0;
    }

    @Override
    public int getStartIndex() {
        return 0;
    }

    /**
     * @return the prefixMapping
     */
    @Override
    public Map<String, String> getPrefixMapping() {
        if (prefixMapping == null) {
            prefixMapping = new HashMap<>();
        }
        return prefixMapping;
    }

    /**
     * @param prefixMapping the prefixMapping to set
     */
    @Override
    public void setPrefixMapping(Map<String, String> prefixMapping) {
        this.prefixMapping = prefixMapping;
    }

    /**
     * Gets the value of the resultType property.
     *
     * @return
     *     possible object is
     *     {@link ResultTypeType }
     *
     */
    @Override
    public ResultTypeType getResultType() {
        return ResultTypeType.RESULTS;
    }

    @Override
    public List<? extends StoredQuery> getStoredQuery() {
        throw new UnsupportedOperationException("Not supported in V1.0.0");
    }

    @Override
    public ResolveValueType getResolve() {
        return null;
    }

    @Override
    public String getResolveDepth() {
        return null;
    }

    @Override
    public int getResolveTimeout() {
        return -1;
    }
}
