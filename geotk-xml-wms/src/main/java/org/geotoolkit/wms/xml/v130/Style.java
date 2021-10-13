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
package org.geotoolkit.wms.xml.v130;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractLegendURL;


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
 *         &lt;element ref="{http://www.opengis.net/wms}Name"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Title"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}LegendURL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}StyleSheetURL" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}StyleURL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "legendURL",
    "styleSheetURL",
    "styleURL"
})
@XmlRootElement(name = "Style")
public class Style implements org.geotoolkit.wms.xml.Style{

    @XmlElement(name = "Name", required = true)
    private String name;
    @XmlElement(name = "Title", required = true)
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "LegendURL")
    private List<LegendURL> legendURL = new ArrayList<LegendURL>();
    @XmlElement(name = "StyleSheetURL")
    private StyleSheetURL styleSheetURL;
    @XmlElement(name = "StyleURL")
    private StyleURL styleURL;

    /**
     * An empty constructor used by JAXB.
     */
     Style() {
     }

    /**
     * Build a new Contact person primary object.
     */
    public Style(final String name, final String title, final String _abstract,
            final StyleURL styleURL, final StyleSheetURL styleSheetURL,final LegendURL... legendURLs) {

        this._abstract     = _abstract;
        this.name          = name;
        this.styleSheetURL = styleSheetURL;
        this.styleURL      = styleURL;
        this.title         = title;
        for (final LegendURL element : legendURLs) {
            this.legendURL.add(element);
        }
    }

    public Style(final org.geotoolkit.wms.xml.Style that) {

        if (that != null) {
            this._abstract     = that.getAbstract();
            this.name          = that.getName();
            this.title         = that.getTitle();
            if (that.getStyleSheetURL() != null) {
                this.styleSheetURL = new StyleSheetURL(that.getStyleSheetURL());
            }
            if (that.getStyleURL() != null) {
                this.styleURL = new StyleURL(that.getStyleURL());
            }
            if (that.getLegendURL() != null) {
                this.legendURL = new ArrayList<LegendURL>();
                for (final AbstractLegendURL element : that.getLegendURL()) {
                    this.legendURL.add(new LegendURL(element));
                }
            }
        }
    }

    /**
     * Gets the value of the name property.
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the title property.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the abstract property.
     *
     */
    @Override
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Gets the value of the legendURL property.
     *
     */
    @Override
    public List<LegendURL> getLegendURL() {
        return Collections.unmodifiableList(legendURL);
    }

    /**
     * Gets the value of the styleSheetURL property.
     */
    @Override
    public StyleSheetURL getStyleSheetURL() {
        return styleSheetURL;
    }

    /**
     * Gets the value of the styleURL property.
     *
     */
    @Override
    public StyleURL getStyleURL() {
        return styleURL;
    }

    @Override
    public String toString() {
        return getName() +" : "+ getTitle() +", "+ getAbstract();
    }

}
