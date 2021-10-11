/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescription">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}SupportedCRS" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "", propOrder = {
    "supportedCRSV2",
    "default",
    "supported"
})
@XmlRootElement(name = "BoundingBoxData")
public class BoundingBoxData extends DataDescription {

    protected List<SupportedCRS> supportedCRS;

    public BoundingBoxData() {

    }

    public BoundingBoxData(List<Format> formats, List<SupportedCRS> supportedCRS) {
        super(formats);
        this.supportedCRS = supportedCRS;
    }

    public List<SupportedCRS> getSupportedCRS() {
        if (supportedCRS == null) {
            supportedCRS = new ArrayList<>();
        }
        return this.supportedCRS;
    }

    /**
     * Gets the value of the supportedCRS property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportedCRS }
     *
     *
     */
    @XmlElement(name = "SupportedCRS", required = true)
    private List<SupportedCRS> getSupportedCRSV2() {
        if (FilterByVersion.isV2()) {
            if (supportedCRS == null) {
                supportedCRS = new ArrayList<>();
            }
            return this.supportedCRS;
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    @XmlElement(name="Default")
    private DefaultCRS getDefault() {
        if (FilterByVersion.isV1()) {
            for (SupportedCRS crs : getSupportedCRS()) {
                if (crs.isDefault()) {
                    return new DefaultCRS(Arrays.asList(crs.getValue()));
                }
            }
        }

        return null;
    }

    private void setDefault(DefaultCRS def) {
        if (def != null && def.crs != null && def.crs.size() == 1) {
            if (this.supportedCRS == null) {
                this.supportedCRS = new ArrayList<>();
            }
            this.supportedCRS.add(new SupportedCRS(def.crs.get(0), Boolean.TRUE));
        }
    }

    @XmlElement(name="Supported")
    private DefaultCRS getSupported() {
        if (FilterByVersion.isV1()) {
            List<String> supported = new ArrayList<>();
            for (SupportedCRS crs : getSupportedCRS()) {
                supported.add(crs.getValue());
            }
            return new DefaultCRS(supported);
        }

        return null;
    }

    private void setSupported(DefaultCRS supported) {
        if (supported != null && supported.crs != null && !supported.crs.isEmpty()) {
            if (this.supportedCRS == null) {
                this.supportedCRS = new ArrayList<>();
            }
            for (String sup : supported.crs) {
                boolean _default = (getDefault() != null  &&  getDefault().crs != null && getDefault().crs.size() == 1 && getDefault().crs.get(0).equals(sup));
                this.supportedCRS.add(new SupportedCRS(sup, _default));
            }
        }
    }


    private static class DefaultCRS {

        @XmlElement(name="CRS")
        List<String> crs;

        DefaultCRS() {}

        DefaultCRS(final List<String> f) {
            this.crs = f;
        }

        List<String> getCrs() {
            return crs;
        }

        void setCrs(final List<String> f) {
            this.crs = f;
        }
    }

}
