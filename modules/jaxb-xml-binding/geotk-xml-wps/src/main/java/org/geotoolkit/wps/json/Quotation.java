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

import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Quotation
 */
public class Quotation implements WPSJSONResponse {

    private String id = null;

    private String title = null;

    private String description = null;

    private String processId = null;

    private Double price = null;

    private String currency = null;

    private Date expire = null;

    private Date created = null;

    private String userId = null;

    private String details = null;

    private String estimatedTime = null;

    private Execute processParameters = null;

    private List<AlternateQuotation> alternativeQuotations = null;

    public Quotation() {

    }
    
    public Quotation(org.geotoolkit.wps.xml.v200.Quotation xml) {
        if (xml != null) {
            this.id = xml.getId();
            this.title = xml.getTitle();
            this.description = xml.getDescription();
            this.processId = xml.getProcessId();
            this.price = xml.getPrice();
            this.currency = xml.getCurrency();
            this.expire = xml.getExpire();
            this.created = xml.getCreated();
            this.userId = xml.getUserId();
            this.details = xml.getDetails();
            this.estimatedTime = xml.getEstimatedTime();
            //this.processParameters = xml.getProcessParameters();
            if (xml.getAlternativeQuotations() != null) {
                this.alternativeQuotations = new ArrayList<>();
                for (org.geotoolkit.wps.xml.v200.AlternateQuotation alt : xml.getAlternativeQuotations()) {
                    this.alternativeQuotations.add(new AlternateQuotation(alt));
                }
            }
        }

    }

    public Quotation id(String id) {
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

    public Quotation title(String title) {
        this.title = title;
        return this;
    }

    /**
     * A name of the quotation
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

    public Quotation description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A description of what the quotation is related
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

    public Quotation processId(String processId) {
        this.processId = processId;
        return this;
    }

    /**
     * The id of the parent process
     *
     * @return processId
  *
     */
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Quotation price(Double price) {
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

    public Quotation currency(String currency) {
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

    public Quotation expire(Date expire) {
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

    public Quotation created(Date created) {
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

    public Quotation userId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * User id that requested this quotation
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

    public Quotation details(String details) {
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

    public Quotation estimatedTime(String estimatedTime) {
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

    public Quotation processParameters(Execute processParameters) {
        this.processParameters = processParameters;
        return this;
    }

    /**
     * Get processParameters
     *
     * @return processParameters
  *
     */
    public Execute getProcessParameters() {
        return processParameters;
    }

    public void setProcessParameters(Execute processParameters) {
        this.processParameters = processParameters;
    }

    public Quotation alternativeQuotations(List<AlternateQuotation> alternativeQuotations) {
        this.alternativeQuotations = alternativeQuotations;
        return this;
    }

    public Quotation addAlternativeQuotationsItem(AlternateQuotation alternativeQuotationsItem) {

        if (this.alternativeQuotations == null) {
            this.alternativeQuotations = new ArrayList<>();
        }

        this.alternativeQuotations.add(alternativeQuotationsItem);
        return this;
    }

    /**
     * Get alternativeQuotations
     *
     * @return alternativeQuotations
  *
     */
    public List<AlternateQuotation> getAlternativeQuotations() {
        return alternativeQuotations;
    }

    public void setAlternativeQuotations(List<AlternateQuotation> alternativeQuotations) {
        this.alternativeQuotations = alternativeQuotations;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quotation quotation = (Quotation) o;
        return Objects.equals(this.id, quotation.id)
                && Objects.equals(this.title, quotation.title)
                && Objects.equals(this.description, quotation.description)
                && Objects.equals(this.processId, quotation.processId)
                && Objects.equals(this.price, quotation.price)
                && Objects.equals(this.currency, quotation.currency)
                && Objects.equals(this.expire, quotation.expire)
                && Objects.equals(this.created, quotation.created)
                && Objects.equals(this.userId, quotation.userId)
                && Objects.equals(this.details, quotation.details)
                && Objects.equals(this.estimatedTime, quotation.estimatedTime)
                && Objects.equals(this.processParameters, quotation.processParameters)
                && Objects.equals(this.alternativeQuotations, quotation.alternativeQuotations);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, processId, price, currency, expire, created, userId, details, estimatedTime, processParameters, alternativeQuotations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Quotation {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    expire: ").append(toIndentedString(expire)).append("\n");
        sb.append("    created: ").append(toIndentedString(created)).append("\n");
        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
        sb.append("    details: ").append(toIndentedString(details)).append("\n");
        sb.append("    estimatedTime: ").append(toIndentedString(estimatedTime)).append("\n");
        sb.append("    processParameters: ").append(toIndentedString(processParameters)).append("\n");
        sb.append("    alternativeQuotations: ").append(toIndentedString(alternativeQuotations)).append("\n");
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
