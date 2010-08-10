/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.opengis.util.CodeList;


/**
 * Provides the details for an exception to be included in a {@link ServiceExceptionReport}.
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceExceptionType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string"&gt;
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;attribute name="locator" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @author Guilhem Legal
 * @author Martin Desruisseaux
 * @author Cédric Briançon
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceExceptionType", propOrder = {"message"})
public final class ServiceExceptionType {
    /**
     * The exception message.
     */
    @XmlValue
    private String message;

    /**
     * The exception code. Must be one of {@link ExceptionCode} constants.
     */
    @XmlAttribute
    private String code;

    /**
     * The method where the error occured.
     */
    @XmlAttribute
    private String locator;

    /**
     * Empty constructor used by JAXB.
     */
    ServiceExceptionType() {
    }

    /**
     * Builds a new exception with the specified message and code.
     *
     * @param message The message of the exception.
     * @param code A standard code for exception (OWS).
     */
    public ServiceExceptionType(final String message, final CodeList code) {
        this(message, code.name());
    }

    /**
     * Build a new exception with the specified message, code and locator.
     *
     * @param message The message of the exception.
     * @param code A standard code for exception (OWS).
     * @param locator The method where the error occured.
     */
    public ServiceExceptionType(final String message, final CodeList code, final String locator) {
        this(message, code.name(), locator);
    }

    /**
     * Builds a new exception with the specified message and code.
     *
     * @param message The message of the exception.
     * @param code A standard code for exception (OWS).
     */
    public ServiceExceptionType(final String message, final String code) {
        this.message = message;
        this.code    = code;
    }

    /**
     * Build a new exception with the specified message, code and locator.
     *
     * @param message The message of the exception.
     * @param code A standard code for exception (OWS).
     * @param locator The method where the error occured.
     */
    public ServiceExceptionType(final String message, final String code, final String locator) {
        this.message = message;
        this.code    = code;
        this.locator = locator;
    }

    /**
     * Returns the message of the exception, or {@code null} if none.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the exception code, or {@code null} if none.
     */
    public CodeList getCode() {
        return CodeList.valueOf(CodeList.class, code);
    }

    /**
     * Returns the locator, or {@code null} if none.
     * @return
     */
    public String getLocator() {
        return locator;
    }
}
