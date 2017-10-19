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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractLegendURL;
import org.geotoolkit.wms.xml.AbstractStyleSheetURL;
import org.geotoolkit.wms.xml.AbstractStyleURL;
import org.geotoolkit.wms.xml.v111.OnlineResource;
import org.geotoolkit.wms.xml.v111.StyleURL;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "styleURL"
})
@XmlRootElement(name = "Style")
public class Style implements org.geotoolkit.wms.xml.Style {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "StyleURL")
    protected String styleURL;

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
     * Obtient la valeur de la propriété styleURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public AbstractStyleURL getStyleURL() {
        return new StyleURL("", new OnlineResource(styleURL));
    }

    /**
     * Définit la valeur de la propriété styleURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStyleURL(String value) {
        this.styleURL = value;
    }

    @Override
    public AbstractStyleSheetURL getStyleSheetURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<? extends AbstractLegendURL> getLegendURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
