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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractGeographicBoundingBox;
import org.geotoolkit.wms.xml.AbstractLayer;


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
    private Integer queryable;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger cascaded;
    @XmlAttribute
    private Integer opaque;
    @XmlAttribute
    private Integer noSubsets;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger fixedWidth;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger fixedHeight;
    
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
             final EXGeographicBoundingBox exGeographicBoundingBox, final BoundingBox boundingBox, final Integer queryable,
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
            final Double maxScaleDenominator, final List<Layer> layer, final Integer queryable, final BigInteger cascaded,
            final Integer opaque, final Integer noSubsets, final BigInteger fixedWidth,  final BigInteger fixedHeight) {
        
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
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the abstract property.
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Gets the value of the keywordList property.
     * 
     */
    public KeywordList getKeywordList() {
        return keywordList;
    }

    /**
     * Gets the value of the crs property.
     * 
     */
    public List<String> getCRS() {
        return Collections.unmodifiableList(crs);
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
        return Collections.unmodifiableList(boundingBox);
    }

    /**
     * Gets the value of the dimension property.
     * 
     */
    public List<Dimension> getDimension() {
        return Collections.unmodifiableList(dimension);
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
        return Collections.unmodifiableList(authorityURL);
    }

    /**
     * Gets the value of the identifier property.
     */
    public List<Identifier> getIdentifier() {
        return Collections.unmodifiableList(identifier);
    }

    /**
     * Gets the value of the metadataURL property.
     */
    public List<MetadataURL> getMetadataURL() {
        return Collections.unmodifiableList(metadataURL);
    }

    /**
     * Gets the value of the dataURL property.
      */
    public List<DataURL> getDataURL() {
        return Collections.unmodifiableList(dataURL);
    }

    /**
     * Gets the value of the featureListURL property.
     * 
     */
    public List<FeatureListURL> getFeatureListURL() {
        return Collections.unmodifiableList(featureListURL);
    }

    /**
     * Gets the value of the style property.
     */
    public List<Style> getStyle() {
        return Collections.unmodifiableList(style);
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
     * Gets the value of the layer property. 
     */
    public List<Layer> getLayer() {
        return Collections.unmodifiableList(layer);
    }

    /**
     * Gets the value of the queryable property.
     */
    public boolean isQueryable() {
        if (queryable == null) {
            return false;
        } else {
            return queryable == 1;
        }
    }

    /**
     * Gets the value of the cascaded property.
     */
    public BigInteger getCascaded() {
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
    public BigInteger getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Gets the value of the fixedHeight property.
     */
    public BigInteger getFixedHeight() {
        return fixedHeight;
    }
    
    /**
     * Gets all values of each dimension property.
     * 
     */
    public List<AbstractDimension> getAbstractDimension() {
        List<AbstractDimension> list = new ArrayList<AbstractDimension>();
        /*Transform a  List<Dimension> to an  List<AbstractDimension>*/
        for( Dimension dim : getDimension() ){
            list.add((AbstractDimension) dim);
        }    
        return list;
    }
}
