/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * A dataset row.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RowType", propOrder = {"k", "v"})
public class RowType {

    @XmlElement(name = "K", required = true)
    private List<KType> k;

    @XmlElement(name = "V", required = true)
    private List<VType> v;

    public List<KType> getK() {
        if (k == null) {
            k = new ArrayList<>();
        }
        return k;
    }

    public void setK(List<KType> k) {
        this.k = k;
    }

    public List<VType> getV() {
        if (v == null) {
            v = new ArrayList<>();
        }
        return v;
    }

    public void setV(List<VType> v) {
        this.v = v;
    }
}
