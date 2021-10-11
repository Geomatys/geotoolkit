/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOperation;
import org.geotoolkit.ows.xml.Range;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetCapabilities">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DescribeCoverage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GetCoverage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCapabilities",
    "describeCoverage",
    "getCoverage"
})
public class Request {

    @XmlElement(name = "GetCapabilities", required = true)
    private Request.GetCapabilities getCapabilities;
    @XmlElement(name = "DescribeCoverage", required = true)
    private Request.DescribeCoverage describeCoverage;
    @XmlElement(name = "GetCoverage", required = true)
    private Request.GetCoverage getCoverage;

    public Request() {
    }

    public Request(Request that) {
        if (that != null) {
            if (that.getCapabilities != null) {
                this.getCapabilities = new GetCapabilities(that.getCapabilities);
            }
            if (that.describeCoverage != null) {
                this.describeCoverage = new DescribeCoverage(that.describeCoverage);
            }
            if (that.getCoverage != null) {
                this.getCoverage = new GetCoverage(that.getCoverage);
            }
        }

    }

    /**
     * Gets the value of the getCapabilities property.
     */
    public Request.GetCapabilities getGetCapabilities() {
        return getCapabilities;
    }

    /**
     * Sets the value of the getCapabilities property.
     */
    public void setGetCapabilities(final Request.GetCapabilities value) {
        this.getCapabilities = value;
    }

    /**
     * Gets the value of the describeCoverage property.
     */
    public Request.DescribeCoverage getDescribeCoverage() {
        return describeCoverage;
    }

    /**
     * Sets the value of the describeCoverage property.
     *
     */
    public void setDescribeCoverage(final Request.DescribeCoverage value) {
        this.describeCoverage = value;
    }

    /**
     * Gets the value of the getCoverage property.
     *
     */
    public Request.GetCoverage getGetCoverage() {
        return getCoverage;
    }

    /**
     * Sets the value of the getCoverage property.
     *
     */
    public void setGetCoverage(final Request.GetCoverage value) {
        this.getCoverage = value;
    }

    /**
     *
     * @param url
     */
    public void updateURL(final String url) {
        if (describeCoverage != null) {
            describeCoverage.updateURL(url);
        }
        if (getCapabilities != null) {
            getCapabilities.updateURL(url);
        }
        if (getCoverage != null) {
            getCoverage.updateURL(url);
        }
    }

    @Override
    public Request clone() {
        return new Request(this);
    }

    public AbstractOperation getOperation(final String operationName) {
        if (operationName.equalsIgnoreCase("GetCapabilities")) {
            return getCapabilities;
        } else if (operationName.equalsIgnoreCase("GetCoverage")) {
            return getCoverage;
        } else if (operationName.equalsIgnoreCase("DescribeCoverage")) {
            return describeCoverage;
        }
        return null;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dcpType"
    })
    public static class DescribeCoverage implements AbstractOperation {

        @XmlElement(name = "DCPType", required = true)
        private List<DCPTypeType> dcpType;

        public DescribeCoverage() {
        }

        public DescribeCoverage(final DescribeCoverage that) {
            if (that != null && that.dcpType != null) {
                this.dcpType = new ArrayList<>();
                for (DCPTypeType t : that.dcpType) {
                    this.dcpType.add(new DCPTypeType(t));
                }
            }
        }

        public DescribeCoverage(final List<DCPTypeType> dcp) {
            this.dcpType = dcp;
        }

        /**
         * Gets the value of the dcpType property.
         *
         */
        @Override
        public List<DCPTypeType> getDCP() {
            if (dcpType == null) {
                dcpType = new ArrayList<>();
            }
            return this.dcpType;
        }

        public void updateURL(final String url) {
            if (this.dcpType != null) {
                for (DCPTypeType dcp : dcpType) {
                    dcp.updateURL(url);
                }
            }
        }

        @Override
        public List<? extends AbstractDomain> getParameter() {
            //no parameter
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getParameter(String name) {
            //no parameter
            return null;
        }

        @Override
        public AbstractDomain getParameterIgnoreCase(String name) {
            //no parameter
            return null;
        }

        @Override
        public List<? extends AbstractDomain> getConstraint() {
            //no constraint
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getConstraint(String name) {
            //no constraint
            return null;
        }

        @Override
        public AbstractDomain getConstraintIgnoreCase(String name) {
            //no constraint
            return null;
        }

        @Override
        public void updateParameter(String parameterName, Collection<String> values) {
            //no parameter
        }

        @Override
        public void updateParameter(String parameterName, Range range) {
            //no parameter
        }

        @Override
        public String getName() {
            return "DescribeCoverage";
        }
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dcpType"
    })
    public static class GetCapabilities implements AbstractOperation {

        @XmlElement(name = "DCPType", required = true)
        private List<DCPTypeType> dcpType;

        public GetCapabilities() {
        }

        public GetCapabilities(final GetCapabilities that) {
            if (that != null && that.dcpType != null) {
                this.dcpType = new ArrayList<>();
                for (DCPTypeType t : that.dcpType) {
                    this.dcpType.add(new DCPTypeType(t));
                }
            }
        }

        public GetCapabilities(final List<DCPTypeType> dcp) {
            this.dcpType = dcp;
        }

        /**
         * Gets the value of the dcpType property.
         *
         */
        @Override
        public List<DCPTypeType> getDCP() {
            if (dcpType == null) {
                dcpType = new ArrayList<>();
            }
            return this.dcpType;
        }

        public void updateURL(final String url) {
            if (this.dcpType != null) {
                for (DCPTypeType dcp : dcpType) {
                    dcp.updateURL(url);
                }
            }
        }

        @Override
        public List<? extends AbstractDomain> getParameter() {
            //no parameter
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getParameter(String name) {
            //no parameter
            return null;
        }

        @Override
        public AbstractDomain getParameterIgnoreCase(String name) {
            //no parameter
            return null;
        }

        @Override
        public List<? extends AbstractDomain> getConstraint() {
            //no constraint
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getConstraint(String name) {
            //no constraint
            return null;
        }

        @Override
        public AbstractDomain getConstraintIgnoreCase(String name) {
            //no constraint
            return null;
        }

        @Override
        public void updateParameter(String parameterName, Collection<String> values) {
            //no parameter
        }

        @Override
        public void updateParameter(String parameterName, Range range) {
            //no parameter
        }

        @Override
        public String getName() {
            return "GetCapabilities";
        }
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained
     * within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dcpType"
    })
    public static class GetCoverage implements AbstractOperation {

        @XmlElement(name = "DCPType", required = true)
        private List<DCPTypeType> dcpType;

        public GetCoverage() {
        }

        public GetCoverage(final GetCoverage that) {
            if (that != null && that.dcpType != null) {
                this.dcpType = new ArrayList<>();
                for (DCPTypeType t : that.dcpType) {
                    this.dcpType.add(new DCPTypeType(t));
                }
            }
        }

        public GetCoverage(final List<DCPTypeType> dcp) {
            this.dcpType = dcp;
        }

        /**
         * Gets the value of the dcpType property.
         *
         */
        @Override
        public List<DCPTypeType> getDCP() {
            if (dcpType == null) {
                dcpType = new ArrayList<>();
            }
            return this.dcpType;
        }

        public void updateURL(final String url) {
            if (this.dcpType != null) {
                for (DCPTypeType dcp : dcpType) {
                    dcp.updateURL(url);
                }
            }
        }

        @Override
        public List<? extends AbstractDomain> getParameter() {
            //no parameter
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getParameter(String name) {
            //no parameter
            return null;
        }

        @Override
        public AbstractDomain getParameterIgnoreCase(String name) {
            //no parameter
            return null;
        }

        @Override
        public List<? extends AbstractDomain> getConstraint() {
            //no constraint
            return new ArrayList<>();
        }

        @Override
        public AbstractDomain getConstraint(String name) {
            //no constraint
            return null;
        }

        @Override
        public AbstractDomain getConstraintIgnoreCase(String name) {
            //no constraint
            return null;
        }

        @Override
        public void updateParameter(String parameterName, Collection<String> values) {
            //no parameter
        }

        @Override
        public void updateParameter(String parameterName, Range range) {
            //no parameter
        }

        @Override
        public String getName() {
            return "GetCoverage";
        }
    }
}
