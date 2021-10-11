/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractOperation;
import org.geotoolkit.wms.xml.AbstractRequest;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "riri", "fifi", "loulou"
})
@XmlRootElement(name = "Request")
public class Request implements AbstractRequest {

    @XmlElement(name = "Map", required = true, type = Map.class)
    protected Map riri;

    @XmlElement(name = "Capabilities", required = true, type = Capabilities.class)
    protected Capabilities fifi;

    @XmlElement(name = "FeatureInfo", required = true, type = FeatureInfo.class)
    protected FeatureInfo loulou;

    @Override
    public AbstractOperation getGetMap() {
        return riri;
    }

    @Override
    public AbstractOperation getGetCapabilities() {
        return fifi;
    }

    @Override
    public AbstractOperation getGetFeatureInfo() {
        return loulou;
    }

    @Override
    public void updateURL(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractRequest clone() {
        final Request clone = new Request();
        clone.riri = riri;
        clone.fifi = fifi;
        clone.loulou = loulou;

        return clone;
    }

}
