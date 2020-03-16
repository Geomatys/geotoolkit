/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020
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
package org.geotoolkit.ops.xml.v110;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * Manually created class adding the extension parameters geo/time
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CompleteQueryType extends InspireQueryType {

    @XmlAttribute(name = "start", namespace = "http://a9.com/-/opensearch/extensions/time/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String start;

    @XmlAttribute(name = "end", namespace = "http://a9.com/-/opensearch/extensions/time/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String end;

    @XmlAttribute(name = "relation", namespace = "http://a9.com/-/opensearch/extensions/time/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String tRelation;

    @XmlAttribute(name = "box", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String box;

    @XmlAttribute(name = "uid", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String uid;

    @XmlAttribute(name = "geometry", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String geometry;

    @XmlAttribute(name = "relation", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String gRelation;

    @XmlAttribute(name = "lat", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String lat;

    @XmlAttribute(name = "lon", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String lon;

    @XmlAttribute(name = "radius", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String radius;

    @XmlAttribute(name = "name", namespace = "http://a9.com/-/opensearch/extensions/geo/1.0/")
    @XmlSchemaType(name = "anyURI")
    private String name;

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * @return the tRelation
     */
    public String gettRelation() {
        return tRelation;
    }

    /**
     * @param tRelation the tRelation to set
     */
    public void settRelation(String tRelation) {
        this.tRelation = tRelation;
    }

    /**
     * @return the box
     */
    public String getBox() {
        return box;
    }

    /**
     * @param box the box to set
     */
    public void setBox(String box) {
        this.box = box;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the geometry
     */
    public String getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    /**
     * @return the gRelation
     */
    public String getgRelation() {
        return gRelation;
    }

    /**
     * @param gRelation the gRelation to set
     */
    public void setgRelation(String gRelation) {
        this.gRelation = gRelation;
    }

    /**
     * @return the lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public String getLon() {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * @return the radius
     */
    public String getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(String radius) {
        this.radius = radius;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
