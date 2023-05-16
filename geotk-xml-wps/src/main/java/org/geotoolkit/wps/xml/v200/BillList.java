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
 * @author guilhem
 */
@XmlRootElement(name = "BillList")
public class BillList implements WPSResponse{

    private List<String> bills;

    public BillList() {

    }

    public BillList(List<String> bills) {
        this.bills = bills;
    }

    /**
     * @return the bills
     */
    public List<String> getBills() {
        return bills;
    }

    /**
     * @param bills the bills to set
     */
    public void setBills(List<String> bills) {
        this.bills = bills;
    }
}
