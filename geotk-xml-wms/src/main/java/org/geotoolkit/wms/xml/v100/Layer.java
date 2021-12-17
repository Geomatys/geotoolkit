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
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.EstimatedGridGeometry;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractKeywordList;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractLogoURL;
import org.geotoolkit.wms.xml.AbstractOnlineResource;
import org.geotoolkit.wms.xml.AbstractURL;
import org.geotoolkit.wms.xml.v111.KeywordList;
import org.geotoolkit.wms.xml.v111.OnlineResource;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;


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
public class Layer implements AbstractLayer {

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
    @Override
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
    public List<AbstractURL> getDataURL() {
        return Collections.singletonList(new AbstractURL() {
            @Override
            public String getFormat() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AbstractOnlineResource getOnlineResource() {
                return new OnlineResource(dataURL);
            }
        });
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
    @Override
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
    @Override
    public List<Layer> getLayer() {
        if (layer == null) {
            layer = new ArrayList<>();
        }
        return this.layer;
    }

    @Override
    public void setCrs(final List<String> srs) {
        this.srs = srs.stream().findAny().orElse(null);
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(final List<BoundingBox> boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public void setAttribution(final String title, final String href, final AbstractLogoURL logo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthorityURL(final String format, final String href) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param identifier the identifier to set
     */
    @Override
    public void setIdentifier(final String authority, final String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMetadataURL(final String format, final String href, final String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDataURL(final String format, final String href) {
        this.dataURL = href;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(final List<Style> style) {
        this.style = style;
    }

    @Override
    public void updateStyle(final List<org.geotoolkit.wms.xml.Style> styles) {
        if (styles != null) {
            this.style = new ArrayList<>();
            for (org.geotoolkit.wms.xml.Style s : styles) {
                if (s instanceof Style) {
                    this.style.add((Style)s);
                }
            }
        }
    }

    /**
     * @param opaque the opaque to set
     */
    @Override
    public void setOpaque(final Integer opaque) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AbstractDimension> getAbstractDimension() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<? extends AbstractDimension> getDimension() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public AbstractKeywordList getKeywordList() {
        return new KeywordList(keywords);
    }

    @Override
    public void setKeywordList(List<String> kewords) {
        keywords = kewords.stream().findAny().orElse(null);
    }

    @Override
    public List<String> getCRS() {
        return srs == null? Collections.EMPTY_LIST : Collections.singletonList(srs);
    }

    @Override
    public Envelope getEnvelope() {
        final GridGeometry grid = getGridGeometry2D();
        if (grid == null) return null;
        return grid.getEnvelope();
    }

    @Override
    public GridGeometry getGridGeometry2D() {
        if (getBoundingBox().isEmpty()) {
            final LatLonBoundingBox bbox = getLatLonBoundingBox();
            if (bbox != null) {
                GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
                env.setRange(0, bbox.getWestBoundLongitude(), bbox.getEastBoundLongitude());
                env.setRange(1, bbox.getSouthBoundLatitude(), bbox.getNorthBoundLatitude());
                return new GridGeometry(null, env, GridOrientation.HOMOTHETY);
            }
            return null;
        }

        final BoundingBox bbox = getBoundingBox().get(0);
        try {
            GeneralEnvelope env = new GeneralEnvelope(CRS.forCode(bbox.getCRSCode()));
            env.setRange(0, bbox.getMinx(), bbox.getMaxx());
            env.setRange(1, bbox.getMiny(), bbox.getMaxy());
            Double resx = bbox.getResx();
            Double resy = bbox.getResy();

            if (resx != null && resy != null) {
                return new EstimatedGridGeometry(env, new double[]{resx, resy});
            } else {
                return new GridGeometry(null, env, GridOrientation.HOMOTHETY);
            }
        } catch (FactoryException e) {
            Logging.getLogger("org.geotoolkit.wms.xml.v100").warning(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean isQueryable() {
        if (queryable == null || queryable.trim().isEmpty()) {
            // TODO : we should get the value in parent layer
            return false;
        }

        try {
            return Integer.parseInt(queryable) > 0;
        } catch (NumberFormatException e) {
            // Well... it should not happen, but just in case, we test if we've
            // got 'true' value...
            return Boolean.parseBoolean(queryable);
        }
    }

    @Override
    public List<? extends AbstractURL> getMetadataURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Double getMinScaleDenominator() {
        return scaleHint == null? null : scaleHint.getMin();
    }

    @Override
    public Double getMaxScaleDenominator() {
        return scaleHint == null? null : scaleHint.getMax();
    }
}
