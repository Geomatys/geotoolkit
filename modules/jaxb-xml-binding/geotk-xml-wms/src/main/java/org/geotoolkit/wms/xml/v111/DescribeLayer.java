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
package org.geotoolkit.wms.xml.v111;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractOperation;


/**
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "dcpType"
})
@XmlRootElement(name = "DescribeLayer")
public class DescribeLayer extends AbstractOperation {

    @XmlElement(name = "Format", required = true)
    private List<Format> format;
    @XmlElement(name = "DCPType", required = true)
    private List<DCPType> dcpType;

    public DescribeLayer() {

    }

    public DescribeLayer(List<String> formats, DCPType... dcpList) {
        if (formats != null) {
            this.format = new ArrayList<Format>();
            for (String f : formats) {
                this.format.add(new Format(f));
            }
        }
        if (dcpList != null) {
            this.dcpType = new ArrayList<DCPType>();
            this.dcpType.addAll(Arrays.asList(dcpList));
        }
    }
    
    /**
     * Gets the value of the format property.
     */
    public List<Format> getFormat() {
        if (format == null) {
            format = new ArrayList<Format>();
        }
        return this.format;
    }

    /**
     * Gets the value of the dcpType property.
     * 
     */
    public List<DCPType> getDCPType() {
        if (dcpType == null) {
            dcpType = new ArrayList<DCPType>();
        }
        return this.dcpType;
    }

    public void updateURL(String url) {
        for (DCPType dcp : dcpType) {
            final HTTP http = dcp.getHTTP();
            if (http != null) {
                final Get get = http.getGet();
                if (get != null) {
                    OnlineResource or = get.getOnlineResource();
                    if (or != null) {
                        or.setHref(url);
                    }
                }
                final Post post = http.getPost();
                if (post != null) {
                    OnlineResource or = post.getOnlineResource();
                    if (or != null) {
                        or.setHref(url);
                    }
                }
            }
        }
    }
}
