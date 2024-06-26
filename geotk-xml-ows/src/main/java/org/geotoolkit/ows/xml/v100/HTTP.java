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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractHTTP;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="Get" type="{http://www.opengis.net/ows}RequestMethodType"/>
 *         &lt;element name="Post" type="{http://www.opengis.net/ows}RequestMethodType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getOrPost"
})
@XmlRootElement(name = "HTTP")
public class HTTP implements AbstractHTTP {

    @XmlElementRefs({
        @XmlElementRef(name = "Get", namespace = "http://www.opengis.net/ows", type = JAXBElement.class),
        @XmlElementRef(name = "Post", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    })
    private List<JAXBElement<RequestMethodType>> getOrPost;

    /**
     * Empty constructor used by JAXB.
     */
    HTTP(){
    }

    public HTTP(final HTTP that){
        if (that != null && that.getOrPost != null) {
            this.getOrPost = new ArrayList<>();
            for (JAXBElement<RequestMethodType> j : that.getOrPost) {
                this.getOrPost.add(new JAXBElement<>(j.getName(), j.getDeclaredType(), new RequestMethodType(j.getValue())));
            }
        }
    }

    /**
     * build a new HTTP object.
     */
    public HTTP(final List<JAXBElement<RequestMethodType>> getOrPost){
        this.getOrPost = getOrPost;
    }

    /**
     * build a new HTTP object.
     */
    public HTTP(final RequestMethodType get, final RequestMethodType post){
        final ObjectFactory factory = new ObjectFactory();
        this.getOrPost = new ArrayList<>();
        if (get != null) {
            this.getOrPost.add(factory.createHTTPGet(get));
        }
        if (post != null) {
            this.getOrPost.add(factory.createHTTPPost(post));
        }
    }

    /**
     * build a new HTTP object.
     */
    public HTTP(final List<RequestMethodType> get, final List<RequestMethodType> post){
        final ObjectFactory factory = new ObjectFactory();
        this.getOrPost = new ArrayList<>();
        if (get != null) {
            for (RequestMethodType r : get) {
                this.getOrPost.add(factory.createHTTPGet(r));
            }
        }
        if (post != null) {
            for (RequestMethodType r : post) {
                this.getOrPost.add(factory.createHTTPPost(r));
            }
        }
    }

    /**
     * Gets the value of the getOrPost property.
     */
    public List<JAXBElement<RequestMethodType>> getRealGetOrPost() {
        if (getOrPost == null) {
            getOrPost = new ArrayList<>();
        }
        return getOrPost;
    }

    /**
     * Gets the value of the getOrPost property.
     */
    public List<String> getProtocolNames() {
        if (getOrPost == null) {
            getOrPost = new ArrayList<>();
        }
        List<String> protocols = new ArrayList<>();
        for (JAXBElement<RequestMethodType> jb : getOrPost) {
            protocols.add(jb.getName().getLocalPart());
        }
        return protocols;
    }

    /**
     * Gets the value of the getOrPost property.
     */
    public List<RequestMethodType> getGetOrPost() {

        List<RequestMethodType> result = new ArrayList<>();
        for (JAXBElement<RequestMethodType> jb: getOrPost) {
            if(jb != null && jb.getValue() != null){
                result.add(jb.getValue());
            }
        }
        return result;
    }

     /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof HTTP) {
            final HTTP that = (HTTP) object;
            int i=0;
            for (JAXBElement<RequestMethodType> j:getOrPost) {
                if (!Objects.equals(j.getValue(), that.getOrPost.get(i).getValue()))
                    return false;

                i++;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.getOrPost != null ? this.getOrPost.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class HTTP: getorPos=").append('\n');
        for (JAXBElement<RequestMethodType> j:getOrPost){
            s.append(j.getValue().toString()).append('\n');
        }
        return s.toString();
    }
}
