/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sos.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.sos.xml.SOSResponse;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="RS" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetResultResponse", propOrder = {
    "result"
})
@XmlRootElement(name = "GetResultResponse")
public class GetResultResponse implements SOSResponse {

    @XmlElement(required = true)
    private GetResultResponse.Result result;

    /**
     * An empty constructor used by jaxB
     */
     GetResultResponse(){}
     
     /**
     * Build a new Response to a getResult request.
     */
     public GetResultResponse(GetResultResponse.Result result){
         this.result = result;
     }
     
    /**
     * Return the value of the result property.
     */
    public GetResultResponse.Result getResult() {
        return result;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetResultResponse) {
            final GetResultResponse that = (GetResultResponse) object;
            return Utilities.equals(this.result, that.result);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.result != null ? this.result.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        if (result != null)
            return "GetResultResponse: " + result.toString();
        else
            return "GetResultResponse: result is null";
    }

    /**
     * RS attribute points to the description of the reference system of the result.
     * The description will contain all information necessary to understand 
     * what is provided within the result response. 
     * The most simple case would be a single value.
     * 
     * @author Guilhem Legal
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Result {

        @XmlValue
        private String value;
        @XmlAttribute(name = "RS", required = true)
        @XmlSchemaType(name = "anyURI")
        private String rs;

        /**
         * An empty constructor used by jaxB
         */
        Result(){}
        
        /**
         * Build a new Result
         */
        public Result(String value, String rs){
            this.rs    = rs;
            this.value = value;
        }
     
        /**
         * Return the value of the value property.
         * 
         */
        public String getValue() {
            return value;
        }

        /**
         * Return the value of the rs property.
         * 
         */
        public String getRS() {
            return rs;
        }

        /**
         * Verify if this entry is identical to the specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Result) {
                final Result that = (Result) object;
                return Utilities.equals(this.rs,    that.rs) &&
                       Utilities.equals(this.value, that.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
            hash = 17 * hash + (this.rs != null ? this.rs.hashCode() : 0);
            return hash;
        }
        
        @Override
        public String toString() {
            return " rs=" + rs + " value=" + value;
        }
    }
}
