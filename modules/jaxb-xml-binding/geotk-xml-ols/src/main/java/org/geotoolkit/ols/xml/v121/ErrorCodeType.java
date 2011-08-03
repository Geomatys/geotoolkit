/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RequestVersionMismatch"/>
 *     &lt;enumeration value="ValueNotRecognized"/>
 *     &lt;enumeration value="NotSupported"/>
 *     &lt;enumeration value="Inconsistent"/>
 *     &lt;enumeration value="DeliveryFailure"/>
 *     &lt;enumeration value="SecurityFailure"/>
 *     &lt;enumeration value="NoResultsReturned"/>
 *     &lt;enumeration value="TimedOut"/>
 *     &lt;enumeration value="InternalServerError"/>
 *     &lt;enumeration value="DataNotAvailable"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ErrorCodeType")
@XmlEnum
public enum ErrorCodeType {


    /**
     * Version of Request Schema not supported. Should echo the invliad version number used in the request
     * 
     */
    @XmlEnumValue("RequestVersionMismatch")
    REQUEST_VERSION_MISMATCH("RequestVersionMismatch"),

    /**
     * Element content or attribute value not recognized.  Although the document is well formed and valid, the element/attribute contains a value that could not be recognized and therefore could not be used by the service processing the message. The error should include the value that was not recognized
     * 
     */
    @XmlEnumValue("ValueNotRecognized")
    VALUE_NOT_RECOGNIZED("ValueNotRecognized"),

    /**
     * Element or attribute not supported. Although the document is well formed and valid, an element or attribute is present that is consistent with the rules and constraints contained in the OpenLS specification, but is not supported by the service processing the message.
     * 
     */
    @XmlEnumValue("NotSupported")
    NOT_SUPPORTED("NotSupported"),

    /**
     * Element content or attribute value inconsistent with other elements or attributes. Although the document is well formed and valid, according to the rules and constraints contained in the OpenLS specification the content of an element or attribute is inconsistent with the content of other elements or their attributes.
     * 
     */
    @XmlEnumValue("Inconsistent")
    INCONSISTENT("Inconsistent"),

    /**
     * Message Delivery Failure. A message has been received that either probably or definitely could not be sent to its next destination. Note: if severity is set to Warning then there is a small probability that the message was delivered.
     * 
     */
    @XmlEnumValue("DeliveryFailure")
    DELIVERY_FAILURE("DeliveryFailure"),

    /**
     * Message Security Checks Failed. Validation of signatures or checks on the authenticity or authority of the sender of the message have failed.
     * 
     */
    @XmlEnumValue("SecurityFailure")
    SECURITY_FAILURE("SecurityFailure"),

    /**
     * The inputs were correct but didn’t produce a result.
     * 
     */
    @XmlEnumValue("NoResultsReturned")
    NO_RESULTS_RETURNED("NoResultsReturned"),

    /**
     * The operation timed out on the server side.
     * 
     */
    @XmlEnumValue("TimedOut")
    TIMED_OUT("TimedOut"),

    /**
     * An error has occurred inside the server.
     * 
     */
    @XmlEnumValue("InternalServerError")
    INTERNAL_SERVER_ERROR("InternalServerError"),

    /**
     * The server does not have data coverage.
     * 
     */
    @XmlEnumValue("DataNotAvailable")
    DATA_NOT_AVAILABLE("DataNotAvailable"),

    /**
     * Unknown Error. Indicates that an error has occurred that is not covered explicitly by any of the other errors. The Error message attribute should be used to indicate the nature of the problem.
     * 
     */
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    ErrorCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ErrorCodeType fromValue(String v) {
        for (ErrorCodeType c: ErrorCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
