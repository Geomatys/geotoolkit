/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.CodeType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlType(name = "", propOrder = {
    "identifier"
})
@XmlRootElement(name = "Undeploy")
public class Undeploy extends RequestBase {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/2.0", required = true)
    protected CodeType identifier;

    public Undeploy() {}

    public Undeploy(String service, String version, String language, CodeType identifier) {
        super(service, version, language);
        this.identifier = identifier;
    }

    /**
     *
     * One identifier for which the process description shall be undeployed.
     *
     */
    public CodeType getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(CodeType id) {
        this.identifier = id;
    }

    @Override
    public Map<String, String> toKVP() throws UnsupportedOperationException {
        final Map<String, String> kvp = new HashMap<>();
        kvp.put("SERVICE",getService());
        kvp.put("REQUEST","Undeploy");
        kvp.put("VERSION",getVersion().toString());
        kvp.put("IDENTIFIER", identifier.toString());
        return kvp;
    }

}
