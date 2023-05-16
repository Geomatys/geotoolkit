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

import java.util.Date;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.wps.xml.WPSResponse;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement(name = "Bill")
public class Bill implements WPSResponse {

    private String id;

    private String title;

    private String description;

    private Double price;

    private String currency;

    private Date created;

    private String userId;

    private String quotationId;

    public Bill() {

    }

    public Bill(String id, Quotation quote) {
        if (quote != null) {
            this.id = id;
            this.title = quote.getTitle();
            this.description = quote.getDescription();
            this.price = quote.getPrice();
            this.currency = quote.getCurrency();
            this.userId = quote.getUserId();
            this.quotationId = quote.getId();
        }
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the quotationId
     */
    public String getQuotationId() {
        return quotationId;
    }

    /**
     * @param quotationId the quotationId to set
     */
    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

}
