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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;
import org.geotoolkit.ows.xml.v200.AcceptFormatsType;
import org.geotoolkit.ows.xml.v200.AcceptVersionsType;
import org.geotoolkit.ows.xml.v200.SectionsType;
import org.geotoolkit.wps.xml.GetCapabilities;


/**
 * <p>Java class for GetCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}GetCapabilitiesType">
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/ows/2.0}ServiceType" fixed="WPS" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCapabilitiesType")
public class GetCapabilitiesType extends org.geotoolkit.ows.xml.v200.GetCapabilitiesType implements GetCapabilities {

    public GetCapabilitiesType() {
        
    }
    
    public GetCapabilitiesType(final AcceptVersionsType acceptVersions, final SectionsType sections,
            final AcceptFormatsType acceptFormats, final String updateSequence, final String service) {
        super(acceptVersions, sections, acceptFormats, updateSequence, service);
    }
    
    @Override
    public List<String> getLanguages() {
        final AcceptLanguages languages = super.getAcceptLanguages();
        if (languages != null) {
            return languages.getLanguage();
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, String> toKVP() throws UnsupportedOperationException {
        final Map<String,String> params = new HashMap<>();
        params.put("SERVICE",    "WPS");
        params.put("REQUEST",    "GetCapabilities");
        params.put("ACCEPTVERSIONS",    StringUtils.join(getAcceptVersions().getVersion(), ','));
        return params;
    }

}
