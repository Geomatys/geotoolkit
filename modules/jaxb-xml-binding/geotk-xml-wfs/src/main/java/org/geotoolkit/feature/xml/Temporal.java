/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.feature.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Rohan FERRE (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Temporal {

    private String description;
    private List<Date> interval;
    private List<String> trs;

    public Temporal() {
        interval = new ArrayList<>();
        trs = new ArrayList<>();
        trs.add("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
    }

    public Temporal(String description, List<Date> interval, List<String> trs) {
        this.description = description;
        this.interval = interval;
        this.trs = trs;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return interval : an array list of an array list of string
     */
    public List<Date> getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(List<Date> interval) {
        this.interval = interval;
    }

    /**
     * @return trs : an array list of string
     */
    public List<String> getTrs() {
        return trs;
    }

    /**
     * @param trs the trs to set
     */
    public void setTrs(List<String> trs) {
        this.trs = trs;
    }

    /**
     * @param interval an array list of string to add to interval
     */
    public void addInterval(Date interval) {
        this.interval.add(interval);
    }
}
