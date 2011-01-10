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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wms.xml.AbstractRequest;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}GetCapabilities"/>
 *         &lt;element ref="{http://www.opengis.net/wms}GetMap"/>
 *         &lt;element ref="{http://www.opengis.net/wms}GetFeatureInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}_ExtendedOperation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCapabilities",
    "getMap",
    "getFeatureInfo",
    "extendedOperation"
})
@XmlRootElement(name = "Request")
public class Request extends AbstractRequest {

    @XmlElement(name = "GetCapabilities", required = true)
    private OperationType getCapabilities;
    @XmlElement(name = "GetMap", required = true)
    private OperationType getMap;
    @XmlElement(name = "GetFeatureInfo")
    private OperationType getFeatureInfo;
    @XmlElementRef(name = "_ExtendedOperation", namespace = "http://www.opengis.net/wms", type = JAXBElement.class)
    protected List<JAXBElement<OperationType>> extendedOperation = new ArrayList<JAXBElement<OperationType>>();

    /**
     * An empty constructor used by JAXB.
     */
     Request() {
     }

    /**
     * Build a new Request.
     */
    public Request(final OperationType getCapabilities, final OperationType getMap,
            final OperationType getFeatureInfo, final JAXBElement<OperationType>... extendedOperations) {
        this.getCapabilities = getCapabilities;
        this.getFeatureInfo  = getFeatureInfo;
        this.getMap          = getMap;
        for (final JAXBElement<OperationType> element : extendedOperations) {
            this.extendedOperation.add(element);
        }
    }
    /**
     * Gets the value of the getCapabilities property.
     * 
     */
    public OperationType getGetCapabilities() {
        return getCapabilities;
    }

    /**
     * Gets the value of the getMap property.
     * 
     */
    public OperationType getGetMap() {
        return getMap;
    }

    /**
     * Gets the value of the getFeatureInfo property.
     * 
     */
    public OperationType getGetFeatureInfo() {
        return getFeatureInfo;
    }

    /**
     * Gets the value of the extendedOperation property.
     */
    public List<JAXBElement<OperationType>> getExtendedOperation() {
        return extendedOperation;
    }

    /**
     * update all the dcp ur with the specified one.
     */
    public void updateURL(final String url) {
        if (getCapabilities != null) {
            getCapabilities.updateURL(url);
        }
        if (getFeatureInfo != null) {
            getFeatureInfo.updateURL(url);
        }
        if (getMap != null) {
            getMap.updateURL(url);
        }
        for (JAXBElement<OperationType> jbOp : extendedOperation) {
            if (jbOp.getValue() != null) {
                jbOp.getValue().updateURL(url);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        if (getCapabilities!= null) {
            s.append("getCapabilities:").append(getCapabilities).append('\n');
        }
        if (getFeatureInfo != null) {
            s.append("getFeatureInfo:").append(getFeatureInfo).append('\n');
        }
        if (getMap != null) {
            s.append("getMap:").append(getMap).append('\n');
        }
        if (extendedOperation != null) {
            for (JAXBElement<OperationType> ext: extendedOperation) {
                s.append("extension operation:").append(ext.getValue()).append('\n');
            }
            s.append('\n');
        }
        return s.toString();
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Request) {
            final Request that = (Request) object;

            boolean ext = true;
            if (this.extendedOperation != null && that.extendedOperation != null &&
                this.extendedOperation.size() == extendedOperation.size()) {
                for (int i = 0; i < this.extendedOperation.size(); i++) {
                    if (!Utilities.equals(this.extendedOperation.get(i), that.extendedOperation.get(i))) {
                        ext = false;
                        break;
                    }
                }

            } else if (this.extendedOperation == null && that.extendedOperation == null) {
                ext = true;
            } else  {
                ext = false;
            }
            return Utilities.equals(this.getCapabilities, that.getCapabilities) &&
                   Utilities.equals(this.getFeatureInfo,  that.getFeatureInfo)     &&
                   Utilities.equals(this.getMap,          that.getMap)   &&
                   ext;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.getCapabilities != null ? this.getCapabilities.hashCode() : 0);
        hash = 73 * hash + (this.getMap != null ? this.getMap.hashCode() : 0);
        hash = 73 * hash + (this.getFeatureInfo != null ? this.getFeatureInfo.hashCode() : 0);
        hash = 73 * hash + (this.extendedOperation != null ? this.extendedOperation.hashCode() : 0);
        return hash;
    }

}
