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
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.wps.xml.WPSResponse;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement(name = "Quotation")
public class Quotation implements WPSResponse {

    private String id;

    private String title;

    private String description;

    private String processId;

    private Double price;

    private String currency;

    private Date expire;

    private Date created;

    private String userId;

    private String details;

    private String estimatedTime;

    private Execute processParameters;

    private List<AlternateQuotation> alternativeQuotations;

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
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
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
     * @return the expire
     */
    public Date getExpire() {
        return expire;
    }

    /**
     * @param expire the expire to set
     */
    public void setExpire(Date expire) {
        this.expire = expire;
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
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return the estimatedTime
     */
    public String getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * @param estimatedTime the estimatedTime to set
     */
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    /**
     * @return the processParameters
     */
    public Execute getProcessParameters() {
        return processParameters;
    }

    /**
     * @param processParameters the processParameters to set
     */
    public void setProcessParameters(Execute processParameters) {
        this.processParameters = processParameters;
    }

    /**
     * @return the alternativeQuotations
     */
    public List<AlternateQuotation> getAlternativeQuotations() {
        return alternativeQuotations;
    }

    /**
     * @param alternativeQuotations the alternativeQuotations to set
     */
    public void setAlternativeQuotations(List<AlternateQuotation> alternativeQuotations) {
        this.alternativeQuotations = alternativeQuotations;
    }


}
