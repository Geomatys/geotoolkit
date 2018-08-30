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
package org.geotoolkit.wps.json;

import java.util.Date;
import java.util.Objects;

/**
 * AlternateQuotation
 */
public class AlternateQuotation {

    private String id = null;

    private String title = null;

    private String description = null;

    private Double price = null;

    private String currency = null;

    private Date expire = null;

    private Date created = null;

    private String details = null;

    private String estimatedTime = null;

    public AlternateQuotation() {

    }

    public AlternateQuotation(org.geotoolkit.wps.xml.v200.AlternateQuotation xml) {
        if (xml != null) {
            this.id = xml.getId();
            this.title = xml.getTitle();
            this.description = xml.getDescription();
            this.price = xml.getPrice();
            this.currency = xml.getCurrency();
            this.expire = xml.getExpire();
            this.created = xml.getCreated();
            this.details = xml.getDetails();
            this.estimatedTime = xml.getEstimatedTime();
        }
    }


    public AlternateQuotation id(String id) {
        this.id = id;
        return this;
    }

    /**
     * The id of the quotation
     *
     * @return id
  *
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AlternateQuotation title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The name of the quotation
     *
     * @return title
  *
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlternateQuotation description(String description) {
        this.description = description;
        return this;
    }

    /**
     * The description of what the quotation is related to
     *
     * @return description
  *
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlternateQuotation price(Double price) {
        this.price = price;
        return this;
    }

    /**
     * Get price
     *
     * @return price
  *
     */
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public AlternateQuotation currency(String currency) {
        this.currency = currency;
        return this;
    }

    /**
     * Currency code in ISO 4217 format
     *
     * @return currency
  *
     */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AlternateQuotation expire(Date expire) {
        this.expire = expire;
        return this;
    }

    /**
     * The date and time (ISO 8601 format) when the quotation will expire
     *
     * @return expire
  *
     */
    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public AlternateQuotation created(Date created) {
        this.created = created;
        return this;
    }

    /**
     * The date and time (ISO 8601 format) when the quotation was created
     *
     * @return created
  *
     */
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public AlternateQuotation details(String details) {
        this.details = details;
        return this;
    }

    /**
     * Get details
     *
     * @return details
  *
     */
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public AlternateQuotation estimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
        return this;
    }

    /**
     * The estimated duration for the process to be performed (in ISO 8601
     * duration format)
     *
     * @return estimatedTime
  *
     */
    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlternateQuotation alternateQuotation = (AlternateQuotation) o;
        return Objects.equals(this.id, alternateQuotation.id)
                && Objects.equals(this.title, alternateQuotation.title)
                && Objects.equals(this.description, alternateQuotation.description)
                && Objects.equals(this.price, alternateQuotation.price)
                && Objects.equals(this.currency, alternateQuotation.currency)
                && Objects.equals(this.expire, alternateQuotation.expire)
                && Objects.equals(this.created, alternateQuotation.created)
                && Objects.equals(this.details, alternateQuotation.details)
                && Objects.equals(this.estimatedTime, alternateQuotation.estimatedTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, price, currency, expire, created, details, estimatedTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlternateQuotation {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    expire: ").append(toIndentedString(expire)).append("\n");
        sb.append("    created: ").append(toIndentedString(created)).append("\n");
        sb.append("    details: ").append(toIndentedString(details)).append("\n");
        sb.append("    estimatedTime: ").append(toIndentedString(estimatedTime)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
