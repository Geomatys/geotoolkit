/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.thw.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Guilhem Legal
 * @since 2.4
 */
@XmlRootElement(name = "GetLinkedCswResponse", namespace = "http://ws.geotk.org/")
public class GetLinkedCswResponse {

    @XmlElement(name = "return")
    private List<String> response;

    public GetLinkedCswResponse() {

    }

    public GetLinkedCswResponse(List<String> response) {
        this.response = response;
    }

    /**
     * Gets the value of the return property.
     */
    public List<String> getReturn() {
        if (response == null) {
            response = new ArrayList<String>();
        }
        return this.response;
    }
}
