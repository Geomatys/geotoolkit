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

import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.wps.xml.WPSResponse;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement(name = "QuotationList")
public class QuotationList implements WPSResponse{

    private List<String> quotations;

    public QuotationList() {

    }

    public QuotationList(List<String> quotations) {
        this.quotations = quotations;
    }

    /**
     * @return the quotations
     */
    public List<String> getQuotations() {
        return quotations;
    }

    /**
     * @param quotations the quotations to set
     */
    public void setQuotations(List<String> quotations) {
        this.quotations = quotations;
    }
}
