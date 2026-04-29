/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Error object returned by an OGC-API.
 * The properties of this object may greatly vary from one server/service to another.
 *
 * Properties are set as an example in the guideline document :
 * https://github.com/opengeospatial/OGC-Web-API-Guidelines#principle-7--error-handling-and-use-of-http-status-codes
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    ErrorMessage.JSON_PROPERTY_DEVELOPER_MESSAGE,
    ErrorMessage.JSON_PROPERTY_USER_MESSAGE,
    ErrorMessage.JSON_PROPERTY_ERROR_CODE,
    ErrorMessage.JSON_PROPERTY_ERROR_STACKTRACE,
    ErrorMessage.JSON_PROPERTY_CONTACT_DETAILS
})
@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Error")
public class ErrorMessage extends DataTransferObject {

    public static final String JSON_PROPERTY_DEVELOPER_MESSAGE = "developer_message";
    @XmlElement(name = "developer_message")
    private String developerMessage;

    public static final String JSON_PROPERTY_USER_MESSAGE = "user_message";
    @XmlElement(name = "user_message")
    private String userMessage;

    public static final String JSON_PROPERTY_ERROR_CODE = "error_code";
    @XmlElement(name = "error_code")
    private String errorCode;

    /**
     * Not defined in the guildeline, but seems a crucial debugging property.
     */
    public static final String JSON_PROPERTY_ERROR_STACKTRACE = "error_stacktrace";
    @XmlElement(name = "error_stacktrace")
    private String errorStacktrace;

    public static final String JSON_PROPERTY_CONTACT_DETAILS = "contact_details";
    @XmlElement(name = "contact_details")
    private String contactDetails;

    public ErrorMessage() {
    }

    @JsonProperty(JSON_PROPERTY_DEVELOPER_MESSAGE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DEVELOPER_MESSAGE)
    public String getDeveloperMessage() {
        return developerMessage;
    }

    @JsonProperty(JSON_PROPERTY_DEVELOPER_MESSAGE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_DEVELOPER_MESSAGE)
    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    @JsonProperty(JSON_PROPERTY_USER_MESSAGE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_USER_MESSAGE)
    public String getUserMessage() {
        return userMessage;
    }

    @JsonProperty(JSON_PROPERTY_USER_MESSAGE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_USER_MESSAGE)
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    @JsonProperty(JSON_PROPERTY_ERROR_CODE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ERROR_CODE)
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty(JSON_PROPERTY_ERROR_CODE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ERROR_CODE)
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty(JSON_PROPERTY_ERROR_STACKTRACE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ERROR_STACKTRACE)
    public String getErrorStacktrace() {
        return errorStacktrace;
    }

    @JsonProperty(JSON_PROPERTY_ERROR_STACKTRACE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ERROR_STACKTRACE)
    public void setErrorStacktrace(String errorStacktrace) {
        this.errorStacktrace = errorStacktrace;
    }

    @JsonProperty(JSON_PROPERTY_CONTACT_DETAILS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONTACT_DETAILS)
    public String getContactDetails() {
        return contactDetails;
    }

    @JsonProperty(JSON_PROPERTY_CONTACT_DETAILS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONTACT_DETAILS)
    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

}
