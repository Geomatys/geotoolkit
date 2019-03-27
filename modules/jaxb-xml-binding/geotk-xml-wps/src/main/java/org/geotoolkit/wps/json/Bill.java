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
 * Bill
 */
public class Bill implements WPSJSONResponse {

    private String id = null;

    private String title = null;

    private String description = null;

    private Double price = null;

    private String currency = null;

    private Date created = null;

    private String userId = null;

    private String quotationId = null;

    public Bill() {

    }

    public Bill(org.geotoolkit.wps.xml.v200.Bill xml) {
        if (xml != null) {
            this.id = xml.getId();
            this.title = xml.getTitle();
            this.description = xml.getDescription();
            this.price = xml.getPrice();
            this.currency = xml.getCurrency();
            this.created = xml.getCreated();
            this.userId = xml.getUserId();
            this.quotationId = xml.getQuotationId();
        }
    }

    public Bill id(String id) {
        this.id = id;
        return this;
    }

    /**
     * The id of the bill
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

    public Bill title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The name of the bill
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

    public Bill description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A description of what is charged
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

    public Bill price(Double price) {
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

    public Bill currency(String currency) {
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

    public Bill created(Date created) {
        this.created = created;
        return this;
    }

    /**
     * The date and time (ISO 8601 format) when the bill was created
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

    public Bill userId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * User id that is charged for this bill
     *
     * @return userId
  *
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Bill quotationId(String quotationId) {
        this.quotationId = quotationId;
        return this;
    }

    /**
     * Reference to the quotation id corresponding to this bill
     *
     * @return quotationId
  *
     */
    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bill bill = (Bill) o;
        return Objects.equals(this.id, bill.id)
                && Objects.equals(this.title, bill.title)
                && Objects.equals(this.description, bill.description)
                && Objects.equals(this.price, bill.price)
                && Objects.equals(this.currency, bill.currency)
                && Objects.equals(this.created, bill.created)
                && Objects.equals(this.userId, bill.userId)
                && Objects.equals(this.quotationId, bill.quotationId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, price, currency, created, userId, quotationId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Bill {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    created: ").append(toIndentedString(created)).append("\n");
        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
        sb.append("    quotationId: ").append(toIndentedString(quotationId)).append("\n");
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
