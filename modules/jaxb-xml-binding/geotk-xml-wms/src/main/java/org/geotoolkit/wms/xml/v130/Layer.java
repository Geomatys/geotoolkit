/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractGeographicBoundingBox;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.opengis.geometry.Envelope;


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
 *         &lt;element ref="{http://www.opengis.net/wms}Name" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Title"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}KeywordList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}CRS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}EX_GeographicBoundingBox" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Dimension" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Attribution" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}AuthorityURL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Identifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}MetadataURL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}DataURL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}FeatureListURL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Style" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}MinScaleDenominator" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}MaxScaleDenominator" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Layer" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="queryable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="cascaded" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="opaque" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="noSubsets" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="fixedWidth" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="fixedHeight" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywordList",
    "crs",
    "exGeographicBoundingBox",
    "boundingBox",
    "dimension",
    "attribution",
    "authorityURL",
    "identifier",
    "metadataURL",
    "dataURL",
    "featureListURL",
    "style",
    "minScaleDenominator",
    "maxScaleDenominator",
    "layer"
})
public class Layer extends AbstractLayer {

    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "Title", required = true)
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "KeywordList")
    private KeywordList keywordList;
    @XmlElement(name = "CRS")
    private List<String> crs = new ArrayList<String>();
    @XmlElement(name = "EX_GeographicBoundingBox")
    private EXGeographicBoundingBox exGeographicBoundingBox;
    @XmlElement(name = "Dimension")
    private List<Dimension> dimension = new ArrayList<Dimension>();
    @XmlElement(name = "BoundingBox")
    private List<BoundingBox> boundingBox = new ArrayList<BoundingBox>();
    @XmlElement(name = "Attribution")
    private Attribution attribution;
    @XmlElement(name = "AuthorityURL")
    private List<AuthorityURL> authorityURL = new ArrayList<AuthorityURL>();
    @XmlElement(name = "Identifier")
    private List<Identifier> identifier = new ArrayList<Identifier>();
    @XmlElement(name = "MetadataURL")
    private List<MetadataURL> metadataURL = new ArrayList<MetadataURL>();
    @XmlElement(name = "DataURL")
    private List<DataURL> dataURL = new ArrayList<DataURL>();
    @XmlElement(name = "FeatureListURL")
    private List<FeatureListURL> featureListURL = new ArrayList<FeatureListURL>();
    @XmlElement(name = "Style")
    private List<Style> style = new ArrayList<Style>();
    @XmlElement(name = "MinScaleDenominator")
    private Double minScaleDenominator;
    @XmlElement(name = "MaxScaleDenominator")
    private Double maxScaleDenominator;
    @XmlElement(name = "Layer")
    private List<Layer> layer = new ArrayList<Layer>();
    @XmlAttribute
    private String queryable;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer cascaded;
    @XmlAttribute
    private Integer opaque;
    @XmlAttribute
    private Integer noSubsets;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer fixedWidth;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer fixedHeight;
    
    /**
     * An empty constructor used by JAXB.
     */
     Layer() {
     }

     /**
      * Build a root layer with only few arguments
      * 
      * @param title                   The title of the layer.
      * @param _abstract               A description of the layer.
      * @param crs                     The list of supported CRS.
      * @param exGeographicBoundingBox A general bounding box including all the child map.
      */
     public Layer(final String title, final String _abstract, final List<String> crs, 
             final EXGeographicBoundingBox exGeographicBoundingBox, List<AbstractLayer> layers) {
         this.title                   = title;
         this._abstract               = _abstract;
         this.layer                   = new ArrayList<Layer>();
         for (AbstractLayer l: layers) {
            if (l instanceof Layer) {
                this.layer.add((Layer)l);
            } else {
                throw new IllegalArgumentException("not good version of layer. expected 1.3.0");
            }
         }
         
         this.crs                     = crs;
         this.exGeographicBoundingBox = exGeographicBoundingBox;
     }
     
     /**
      * Build a root layer with only few arguments
      * 
      * @param title                   The title of the layer.
      * @param _abstract               A description of the layer.
      * @param crs                     The list of supported CRS.
      * @param exGeographicBoundingBox A general bounding box including all the child map.
      */
     public Layer(final String name, final String title, final String _abstract, final List<String> crs, 
             final EXGeographicBoundingBox exGeographicBoundingBox, List<AbstractLayer> layers) {
         this(title,_abstract,crs,exGeographicBoundingBox,layers);
         this.name = name;
     }
     
     /**
      * Build a child layer for the specified version
      * 
      * @param name      The title of the layer.
      * @param _abstract A description of the layer.
      * @param keyword   A keyword on the layer.
      * @param crs       The list of supported CRS by this layer.
      * @param exGeographicBoundingBox A latitude/longitude boundingBox.
      * @param boundingBox             A normal boundingBox.
      * @param queryable  A boolean indicating if the layer is queryable
      * @param dimension  A list of Dimension block.
      * @param style      An object describing the style of the layer.
      * @param version    The version of the wms service.
      */
     public Layer(final String name, final String _abstract, final String keyword, final List<String> crs, 
             final EXGeographicBoundingBox exGeographicBoundingBox, final BoundingBox boundingBox, final String queryable,
             final List<AbstractDimension> dimensions, final List<Style> styles) {
         this.name                    = name;
         this.title                   = name;
         this._abstract               = _abstract;
         this.keywordList             = new KeywordList(new Keyword(keyword));
         this.boundingBox.add(boundingBox);
         this.queryable               = queryable;
         this.style                   = new ArrayList<Style>();
         for (Style s: styles) {
             style.add(s);
         }
         
         this.crs                     = crs;
         this.dimension               = new ArrayList<Dimension>();
         for (AbstractDimension d: dimensions) {
             if (d instanceof Dimension) {
                this.dimension.add((Dimension)d);
             } else {
                throw new IllegalArgumentException("not good version of layer. expected 1.3.0");
             }
         }
         this.exGeographicBoundingBox = exGeographicBoundingBox;
     }
     
     
     
     
     
    /**
     * Build a full Layer object.
     */
    public Layer(final String name, final String title, final String _abstract,
            final KeywordList keywordList, final List<String> crs, final EXGeographicBoundingBox exGeographicBoundingBox,
            final List<BoundingBox> boundingBox, final List<Dimension> dimension, final Attribution attribution,
            final List<AuthorityURL> authorityURL, final List<Identifier> identifier, final List<MetadataURL> metadataURL,
            final List<DataURL> dataURL, final List<FeatureListURL> featureListURL, final List<Style> style, final Double minScaleDenominator,
            final Double maxScaleDenominator, final List<Layer> layer, final String queryable, final Integer cascaded,
            final Integer opaque, final Integer noSubsets, final Integer fixedWidth,  final Integer fixedHeight) {
        
        this._abstract               = _abstract;
        this.attribution             = attribution;
        this.authorityURL            = authorityURL;
        this.boundingBox             = boundingBox;
        this.cascaded                = cascaded;
        this.crs                     = crs;
        this.dataURL                 = dataURL;
        this.dimension               = dimension;
        this.exGeographicBoundingBox = exGeographicBoundingBox;
        this.featureListURL          = featureListURL;
        this.fixedHeight             = fixedHeight;
        this.fixedWidth              = fixedWidth;
        this.identifier              = identifier;
        this.keywordList             = keywordList;
        this.layer                   = layer;
        this.maxScaleDenominator     = maxScaleDenominator;
        this.metadataURL             = metadataURL;
        this.minScaleDenominator     = minScaleDenominator;
        this.name                    = name;
        this.noSubsets               = noSubsets;
        this.opaque                  = opaque;
        this.queryable               = queryable;
        this.style                   = style;
        this.title                   = title;
        
    }

    /**
     * Gets the value of the name property.
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
     */
    @Override
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Gets the value of the keywordList property.
     * 
     */
    @Override
    public KeywordList getKeywordList() {
        return keywordList;
    }

    /**
     * Gets the value of the crs property.
     * 
     */
    @Override
    public List<String> getCRS() {
        return crs;
    }
    

    /**
     * Gets the value of the exGeographicBoundingBox property.
     */
    public AbstractGeographicBoundingBox getEXGeographicBoundingBox() {
        return exGeographicBoundingBox;
    }
    
    
    /**
     * Gets the value of the boundingBox property.
     * 
     */
    public List<BoundingBox> getBoundingBox() {
        return boundingBox;
    }

    /**
     * Gets the value of the dimension property.
     * 
     */
    @Override
    public List<Dimension> getDimension() {
        return dimension;
    }
    
    /**
     * Gets the value of the attribution property.
     * 
     */
    public Attribution getAttribution() {
        return attribution;
    }

    /**
     * Gets the value of the authorityURL property.
     * 
     */
    public List<AuthorityURL> getAuthorityURL() {
        return authorityURL;
    }

    /**
     * Gets the value of the identifier property.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * Gets the value of the metadataURL property.
     */
    public List<MetadataURL> getMetadataURL() {
        return metadataURL;
    }

    /**
     * Gets the value of the dataURL property.
      */
    public List<DataURL> getDataURL() {
        return dataURL;
    }

    /**
     * Gets the value of the featureListURL property.
     * 
     */
    public List<FeatureListURL> getFeatureListURL() {
        return featureListURL;
    }

    /**
     * Gets the value of the style property.
     */
    @Override
    public List<Style> getStyle() {
        return style;
    }

    /**
     * Gets the value of the minScaleDenominator property.
     */
    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    /**
     * Gets the value of the maxScaleDenominator property.
     */
    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Layer> getLayer() {
        return layer;
    }

    /**
     * Gets the value of the queryable property.
     */
    @Override
    public boolean isQueryable() {
        return "1".equals(queryable) || "true".equalsIgnoreCase(queryable);
    }

    /**
     * Gets the value of the cascaded property.
     */
    public Integer getCascaded() {
        return cascaded;
    }

    /**
     * Gets the value of the opaque property.
     */
    public boolean isOpaque() {
        if (opaque == null) {
            return false;
        } else {
            return opaque == 1;
        }
    }

    /**
     * Gets the value of the noSubsets property.
     */
    public boolean isNoSubsets() {
        if (noSubsets == null) {
            return false;
        } else {
            return noSubsets == 1;
        }
    }

    /**
     * Gets the value of the fixedWidth property.
     */
    public Integer getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Gets the value of the fixedHeight property.
     */
    public Integer getFixedHeight() {
        return fixedHeight;
    }
    
    /**
     * Gets all values of each dimension property.
     * 
     */
    @Override
    public List<AbstractDimension> getAbstractDimension() {
        List<AbstractDimension> list = new ArrayList<AbstractDimension>();
        /*Transform a  List<Dimension> to an  List<AbstractDimension>*/
        for( Dimension dim : getDimension() ){
            list.add((AbstractDimension) dim);
        }    
        return list;
    }

    @Override
    public Envelope getEnvelope() {
        final AbstractGeographicBoundingBox bbox = getEXGeographicBoundingBox();
        if(bbox != null){
            return new GeneralEnvelope(bbox);
        }
        return null;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param abstract the _abstract to set
     */
    public void setAbstract(String abstrac) {
        this._abstract = abstrac;
    }

    /**
     * @param keywordList the keywordList to set
     */
    public void setKeywordList(KeywordList keywordList) {
        this.keywordList = keywordList;
    }

    /**
     * @param crs the crs to set
     */
    public void setCrs(List<String> crs) {
        this.crs = crs;
    }

    /**
     * @param exGeographicBoundingBox the exGeographicBoundingBox to set
     */
    public void setExGeographicBoundingBox(EXGeographicBoundingBox exGeographicBoundingBox) {
        this.exGeographicBoundingBox = exGeographicBoundingBox;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(List<Dimension> dimension) {
        this.dimension = dimension;
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(List<BoundingBox> boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @param attribution the attribution to set
     */
    public void setAttribution(Attribution attribution) {
        this.attribution = attribution;
    }

    /**
     * @param authorityURL the authorityURL to set
     */
    public void setAuthorityURL(List<AuthorityURL> authorityURL) {
        this.authorityURL = authorityURL;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    /**
     * @param metadataURL the metadataURL to set
     */
    public void setMetadataURL(List<MetadataURL> metadataURL) {
        this.metadataURL = metadataURL;
    }

    /**
     * @param dataURL the dataURL to set
     */
    public void setDataURL(List<DataURL> dataURL) {
        this.dataURL = dataURL;
    }

    /**
     * @param featureListURL the featureListURL to set
     */
    public void setFeatureListURL(List<FeatureListURL> featureListURL) {
        this.featureListURL = featureListURL;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(List<Style> style) {
        this.style = style;
    }

    /**
     * @param minScaleDenominator the minScaleDenominator to set
     */
    public void setMinScaleDenominator(Double minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    /**
     * @param maxScaleDenominator the maxScaleDenominator to set
     */
    public void setMaxScaleDenominator(Double maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(List<Layer> layer) {
        this.layer = layer;
    }

    /**
     * @param queryable the queryable to set
     */
    public void setQueryable(String queryable) {
        this.queryable = queryable;
    }

    /**
     * @param cascaded the cascaded to set
     */
    public void setCascaded(Integer cascaded) {
        this.cascaded = cascaded;
    }

    /**
     * @param opaque the opaque to set
     */
    public void setOpaque(Integer opaque) {
        this.opaque = opaque;
    }

    /**
     * @param noSubsets the noSubsets to set
     */
    public void setNoSubsets(Integer noSubsets) {
        this.noSubsets = noSubsets;
    }

    /**
     * @param fixedWidth the fixedWidth to set
     */
    public void setFixedWidth(Integer fixedWidth) {
        this.fixedWidth = fixedWidth;
    }

    /**
     * @param fixedHeight the fixedHeight to set
     */
    public void setFixedHeight(Integer fixedHeight) {
        this.fixedHeight = fixedHeight;
    }
}
