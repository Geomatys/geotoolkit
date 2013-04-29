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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDCP;
import org.geotoolkit.ows.xml.AbstractHTTP;


/**
 * 
 *       Connect point URLs for the HTTP Distributed Computing Platform (DCP).
 *       Normally, only one Get and/or one Post is included in this element. 
 *       More than one Get and/or Post is allowed to support including alternative URLs for uses such as load balancing or backup. 
 *     
 * 
 * <p>Java class for DCPTypeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DCPTypeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HTTP">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element name="Get">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Post">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DCPTypeType", propOrder = {
    "http"
})
public class DCPTypeType implements AbstractDCP {

    @XmlElement(name = "HTTP", required = true)
    private DCPTypeType.HTTP http;

    public DCPTypeType() {

    }

    public DCPTypeType(final DCPTypeType that) {
        if (that != null && that.http != null) {
            this.http = new HTTP(that.http);
        }
    }
    
    public DCPTypeType(final HTTP http) {
        this.http = http;
    }

    /**
     * Gets the value of the http property.
     */
    @Override
    public DCPTypeType.HTTP getHTTP() {
        return http;
    }

    public void updateURL(final String url) {
        if (http != null) {
            http.updateURL(url);
        }
    }

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
     *         &lt;element name="Get">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Post">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "getOrPost"
    })
    public static class HTTP implements AbstractHTTP {

        @XmlElements({
            @XmlElement(name = "Post", type = DCPTypeType.HTTP.Post.class),
            @XmlElement(name = "Get", type = DCPTypeType.HTTP.Get.class)
        })
        private List<Object> getOrPost;

        public HTTP() {

        }
        
        public HTTP(final HTTP that) {
            if (that != null && that.getOrPost != null) {
                this.getOrPost = new ArrayList<Object>();
                for (Object o : that.getOrPost) {
                    if (o instanceof Post) {
                        this.getOrPost.add(new Post((Post)o));
                    } else if (o instanceof Get) {
                        this.getOrPost.add(new Get((Get)o));
                    } else {
                        // should not happen
                        throw new IllegalArgumentException("DCPtype must only contains GET or POST object");
                    }
                }
            }
        }

        public HTTP(final Get get, final Post post) {
            this.getOrPost = new ArrayList<Object>();
            if (get != null) {
                this.getOrPost.add(get);
            }
            if (post != null) {
                this.getOrPost.add(post);
            }
        }

        /**
         * Gets the value of the getOrPost property.
         * 
         */
        public List<Object> getRealGetOrPost() {
            if (getOrPost == null) {
                getOrPost = new ArrayList<Object>();
            }
            return this.getOrPost;
        }
        
        @Override
        public List<OnlineResourceType> getGetOrPost() {
            final List<OnlineResourceType> result = new ArrayList<OnlineResourceType>();
            if (getOrPost != null) {
                for (Object o : getOrPost) {
                    if (o instanceof Get) {
                        result.add(((Get)o).onlineResource);
                    } else if (o instanceof Post) {
                        result.add(((Post)o).onlineResource);
                    }
                }
            }
            return result;
        }

        public void updateURL(final String url) {
            if (this.getOrPost != null) {
                for (Object prot : getOrPost) {
                    if (prot instanceof Get) {
                        Get get = (Get) prot;
                        get.onlineResource.setHref(url);
                    } else if (prot instanceof Post) {
                        Post post = (Post) prot;
                        post.onlineResource.setHref(url);
                    }
                }
            }
        }


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
         *         &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "onlineResource"
        })
        public static class Get {

            @XmlElement(name = "OnlineResource", required = true)
            private OnlineResourceType onlineResource;

            public Get() {

            }

            public Get(final Get that) {
                if (that != null && that.onlineResource != null)
                this.onlineResource = new OnlineResourceType(that.onlineResource);
            }
            
            public Get(final OnlineResourceType or) {
                this.onlineResource = or;
            }

            /**
             * Gets the value of the onlineResource property.
             */
            public OnlineResourceType getOnlineResource() {
                return onlineResource;
            }
        }


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
         *         &lt;element name="OnlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "onlineResource"
        })
        public static class Post {

            public Post() {

            }
            
            public Post(final Post that) {
                if (that != null && that.onlineResource != null) {
                    this.onlineResource = new OnlineResourceType(that.onlineResource);
                }
            }

            public Post(final OnlineResourceType or) {
                this.onlineResource = or;
            }

            @XmlElement(name = "OnlineResource", required = true)
            private OnlineResourceType onlineResource;

            /**
             * Gets the value of the onlineResource property.
             */
            public OnlineResourceType getOnlineResource() {
                return onlineResource;
            }
        }
    }
}
