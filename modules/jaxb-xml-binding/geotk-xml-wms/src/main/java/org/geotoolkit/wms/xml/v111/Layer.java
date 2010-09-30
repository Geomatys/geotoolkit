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
package org.geotoolkit.wms.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
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
    "srs",
    "latLonBoundingBox",
    "boundingBox",
    "dimension",
    "extent",
    "attribution",
    "authorityURL",
    "identifier",
    "metadataURL",
    "dataURL",
    "featureListURL",
    "style",
    "scaleHint",
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
    @XmlElement(name = "Dimension")
    private List<Dimension> dimension = new ArrayList<Dimension>();
    @XmlElement(name = "SRS")
    private List<String> srs = new ArrayList<String>();
    @XmlElement(name = "Extent")
    private List<Extent> extent = new ArrayList<Extent>();
    @XmlElement(name = "LatLonBoundingBox")
    private LatLonBoundingBox latLonBoundingBox;
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
    @XmlElement(name = "ScaleHint")
    protected ScaleHint scaleHint;
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
             final LatLonBoundingBox latLonBoundingBox, List<AbstractLayer> layers) {
         this.title                   = title;
         this._abstract               = _abstract;
         this.layer                   = new ArrayList<Layer>();
         for (AbstractLayer l: layers) {
            if (l instanceof Layer) {
                this.layer.add((Layer)l);
            } else {
                throw new IllegalArgumentException("not good version of layer. expected 1.1.1");
            }
         }
         
         this.srs                     = crs;
         this.latLonBoundingBox       = latLonBoundingBox;
            
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
             final LatLonBoundingBox latLonBoundingBox, List<AbstractLayer> layers) {
         this(title,_abstract,crs,latLonBoundingBox,layers);
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
             final LatLonBoundingBox latLonBoundingBox, final BoundingBox boundingBox, final String queryable,
             final List<AbstractDimension> dimensions, final List<Style> styles) {
         this.name                    = name;
         this.title                   = name;
         this._abstract               = _abstract;
         this.keywordList             = new KeywordList(new Keyword(keyword));
         this.boundingBox.add(boundingBox);
         this.queryable = queryable;
         this.style = new ArrayList<Style>();
         if (styles != null) {
            for (Style s: styles) {
                this.style.add(s);
            }
         }
         
         this.srs = crs;
         
         this.dimension               = new ArrayList<Dimension>();
         for (AbstractDimension d: dimensions) {
             if (d instanceof Dimension) {
                 Extent ext = new Extent(d.getName(), d.getDefault(), d.getValue());
                 this.extent.add(ext);
                 d.setValue(null);
                 d.setDefault(null);
                 this.dimension.add((Dimension)d);
             } else {
                throw new IllegalArgumentException("not good version of layer. expected 1.1.1");
             }
         }
         
         this.latLonBoundingBox = latLonBoundingBox; 
     }
     
     
     
     
     
    /**
     * Build a full Layer object.
     */
    public Layer(final String name, final String title, final String _abstract,
            final KeywordList keywordList, final List<String> crs,
            final List<BoundingBox> boundingBox, final List<Dimension> dimension, final Attribution attribution,
            final List<AuthorityURL> authorityURL, final List<Identifier> identifier, final List<MetadataURL> metadataURL,
            final List<DataURL> dataURL, final List<FeatureListURL> featureListURL, final List<Style> style, final ScaleHint scaleHint,
            final Double maxScaleDenominator, final List<Layer> layer, final String queryable, final Integer cascaded,
            final Integer opaque, final Integer noSubsets, final Integer fixedWidth,  final Integer fixedHeight) {
        
        this._abstract               = _abstract;
        this.attribution             = attribution;
        this.authorityURL            = authorityURL;
        this.boundingBox             = boundingBox;
        this.cascaded                = cascaded;
        this.dataURL                 = dataURL;
        this.dimension               = dimension;
        this.featureListURL          = featureListURL;
        this.fixedHeight             = fixedHeight;
        this.fixedWidth              = fixedWidth;
        this.identifier              = identifier;
        this.keywordList             = keywordList;
        this.layer                   = layer;
        this.metadataURL             = metadataURL;
        this.name                    = name;
        this.noSubsets               = noSubsets;
        this.opaque                  = opaque;
        this.queryable               = queryable;
        this.style                   = style;
        this.title                   = title;
        this.scaleHint               = scaleHint;
        this.srs                     = crs;
        
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
     * Gets the value of the srs property.
     * 
     */
    public List<String> getSRS() {
        return Collections.unmodifiableList(srs);
    }

    
    /**
     * Gets the value of the LatLonBoundingBox property.
     */
    public AbstractGeographicBoundingBox getLatLonBoundingBox() {
        return latLonBoundingBox;
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
    @Override
    public List<Dimension> getDimension() {
        return Collections.unmodifiableList(dimension);
    }
    
    /**
     * Gets the value of the extent property.
     * 
     */
    public List<Extent> getExtent() {
        return Collections.unmodifiableList(extent);
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
    @Override
    public List<Style> getStyle() {
        return Collections.unmodifiableList(style);
    }

    /**
     * Gets the value of the maxScaleDenominator property.
     */
    public ScaleHint getScaleInt() {
        return scaleHint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Layer> getLayer() {
        return Collections.unmodifiableList(layer);
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
        
        /*Transform a  List<Dimension> in an  List<AbstractDimension>*/
        for( Dimension dim : getDimension() ){
            list.add((AbstractDimension) dim);
        }
    
        /*Set the AbstractDimension value from the corresponding Extent 
         *<Dimension name="time" ..../>
         *<Extent name="time" ...>value1,value2,....</Extent> 
         */
        List<Extent> listExt =  getExtent();      
        for (int i=0;i<listExt.size();i++){  
            AbstractDimension dimTmp = list.get(i);
            Extent extTmp = listExt.get(i);
            if(dimTmp.getDefault() == null)
                dimTmp.setDefault(extTmp.getDefault());
            dimTmp.setValue(extTmp.getvalue());
        }
   
        return list;
    }

    @Override
    public List<String> getCRS() {
        return getSRS();
    }

    @Override
    public Envelope getEnvelope() {
        final AbstractGeographicBoundingBox bbox = getLatLonBoundingBox();
        if(bbox != null){
            return new GeneralEnvelope(bbox);
        }
        return null;
    }
    
}
