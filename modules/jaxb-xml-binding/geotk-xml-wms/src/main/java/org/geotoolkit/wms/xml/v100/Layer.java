/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywords",
    "srs",
    "latLonBoundingBox",
    "boundingBox",
    "dataURL",
    "style",
    "scaleHint",
    "layer"
})
@XmlRootElement(name = "Layer")
public class Layer {

    @XmlAttribute(name = "queryable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String queryable;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "Keywords")
    protected String keywords;
    @XmlElement(name = "SRS")
    protected String srs;
    @XmlElement(name = "LatLonBoundingBox")
    protected LatLonBoundingBox latLonBoundingBox;
    @XmlElement(name = "BoundingBox")
    protected List<BoundingBox> boundingBox;
    @XmlElement(name = "DataURL")
    protected String dataURL;
    @XmlElement(name = "Style")
    protected List<Style> style;
    @XmlElement(name = "ScaleHint")
    protected ScaleHint scaleHint;
    @XmlElement(name = "Layer")
    protected List<Layer> layer;

    /**
     * Obtient la valeur de la propriété queryable.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQueryable() {
        if (queryable == null) {
            return "0";
        } else {
            return queryable;
        }
    }

    /**
     * Définit la valeur de la propriété queryable.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQueryable(String value) {
        this.queryable = value;
    }

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété abstract.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Définit la valeur de la propriété abstract.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Obtient la valeur de la propriété keywords.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Définit la valeur de la propriété keywords.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeywords(String value) {
        this.keywords = value;
    }

    /**
     * Obtient la valeur de la propriété srs.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Définit la valeur de la propriété srs.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSRS(String value) {
        this.srs = value;
    }

    /**
     * Obtient la valeur de la propriété latLonBoundingBox.
     *
     * @return
     *     possible object is
     *     {@link LatLonBoundingBox }
     *
     */
    public LatLonBoundingBox getLatLonBoundingBox() {
        return latLonBoundingBox;
    }

    /**
     * Définit la valeur de la propriété latLonBoundingBox.
     *
     * @param value
     *     allowed object is
     *     {@link LatLonBoundingBox }
     *
     */
    public void setLatLonBoundingBox(LatLonBoundingBox value) {
        this.latLonBoundingBox = value;
    }

    /**
     * Gets the value of the boundingBox property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boundingBox property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoundingBox().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BoundingBox }
     *
     *
     */
    public List<BoundingBox> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<BoundingBox>();
        }
        return this.boundingBox;
    }

    /**
     * Obtient la valeur de la propriété dataURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataURL() {
        return dataURL;
    }

    /**
     * Définit la valeur de la propriété dataURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataURL(String value) {
        this.dataURL = value;
    }

    /**
     * Gets the value of the style property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the style property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Style }
     *
     *
     */
    public List<Style> getStyle() {
        if (style == null) {
            style = new ArrayList<Style>();
        }
        return this.style;
    }

    /**
     * Obtient la valeur de la propriété scaleHint.
     *
     * @return
     *     possible object is
     *     {@link ScaleHint }
     *
     */
    public ScaleHint getScaleHint() {
        return scaleHint;
    }

    /**
     * Définit la valeur de la propriété scaleHint.
     *
     * @param value
     *     allowed object is
     *     {@link ScaleHint }
     *
     */
    public void setScaleHint(ScaleHint value) {
        this.scaleHint = value;
    }

    /**
     * Gets the value of the layer property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer }
     *
     *
     */
    public List<Layer> getLayer() {
        if (layer == null) {
            layer = new ArrayList<Layer>();
        }
        return this.layer;
    }

}
