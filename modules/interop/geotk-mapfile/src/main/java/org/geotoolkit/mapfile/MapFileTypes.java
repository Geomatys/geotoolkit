/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.mapfile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;

/**
 * Declares all mapfile types.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class MapfileTypes {
    
    public static final String NAMESPACE = "http://mapserver.org";
    
    public static final FeatureType CLASS;    
    public static final AttributeDescriptor CLASS_BACKGROUNDCOLOR;
    public static final AttributeDescriptor CLASS_COLOR;
    public static final AttributeDescriptor CLASS_DEBUG;
    public static final AttributeDescriptor CLASS_EXPRESSION;
    public static final AttributeDescriptor CLASS_GROUP;
    public static final AttributeDescriptor CLASS_KEYIMAGE;
    public static final AttributeDescriptor CLASS_LABEL;
    public static final AttributeDescriptor CLASS_MAXSCALEDENOM;
    public static final AttributeDescriptor CLASS_MAXSCALE;
    public static final AttributeDescriptor CLASS_MAXSIZE;
    public static final AttributeDescriptor CLASS_MINSCALEDENOM;
    public static final AttributeDescriptor CLASS_MINSCALE;
    public static final AttributeDescriptor CLASS_MINSIZE;
    public static final AttributeDescriptor CLASS_NAME;
    public static final AttributeDescriptor CLASS_OUTLINECOLOR;
    public static final AttributeDescriptor CLASS_SIZE;
    public static final AttributeDescriptor CLASS_STATUS;
    public static final AttributeDescriptor CLASS_STYLE;
    public static final AttributeDescriptor CLASS_SYMBOL;
    public static final AttributeDescriptor CLASS_TEMPLATE;
    /** Expression */
    public static final AttributeDescriptor CLASS_TEXT;
    
    public static final FeatureType CLUSTER;
    public static final AttributeDescriptor CLUSTER_MAXDISTANCE;
    public static final AttributeDescriptor CLUSTER_REGION;
    public static final AttributeDescriptor CLUSTER_GROUP;
    public static final AttributeDescriptor CLUSTER_FILTER;
                
    public static final FeatureType FEATURE;
    public static final AttributeDescriptor FEATURE_POINTS;
    public static final AttributeDescriptor FEATURE_ITEMS;
    public static final AttributeDescriptor FEATURE_TEXT;
    public static final AttributeDescriptor FEATURE_WKT;
    
    public static final FeatureType GRID;
    public static final AttributeDescriptor GRID_LABELFORMAT;
    public static final AttributeDescriptor GRID_MINARCS;
    public static final AttributeDescriptor GRID_MAXARCS;
    public static final AttributeDescriptor GRID_MININTERVAL;
    public static final AttributeDescriptor GRID_MAXINTERVAL;
    public static final AttributeDescriptor GRID_MINSUBDIVIDE;
    public static final AttributeDescriptor GRID_MAXSUBDIVIDE;
    
    public static final FeatureType JOIN;
    public static final AttributeDescriptor JOIN_CONNECTION;
    public static final AttributeDescriptor JOIN_CONNECTIONTYPE;
    public static final AttributeDescriptor JOIN_FOOTER;
    public static final AttributeDescriptor JOIN_FROM;
    public static final AttributeDescriptor JOIN_HEADER;
    public static final AttributeDescriptor JOIN_NAME;
    public static final AttributeDescriptor JOIN_TABLE;
    public static final AttributeDescriptor JOIN_TEMPLATE;
    public static final AttributeDescriptor JOIN_TO;
    public static final AttributeDescriptor JOIN_TYPE;
    
    public static final FeatureType LABEL;
    public static final AttributeDescriptor LABEL_ALIGN;
    /** [double|auto|follow|attribute] */
    public static final AttributeDescriptor LABEL_ANGLE;
    public static final AttributeDescriptor LABEL_ANTIALIAS;
    public static final AttributeDescriptor LABEL_BACKGROUNDCOLOR;
    public static final AttributeDescriptor LABEL_BACKGROUNDSHADOWCOLOR;
    public static final AttributeDescriptor LABEL_BACKGROUNDSHADOWSIZE;
    public static final AttributeDescriptor LABEL_BUFFER;
    /** [r] [g] [b] | [attribute] */
    public static final AttributeDescriptor LABEL_COLOR;
    public static final AttributeDescriptor LABEL_ENCODING;
    public static final AttributeDescriptor LABEL_FONT;
    public static final AttributeDescriptor LABEL_FORCE;
    public static final AttributeDescriptor LABEL_MAXLENGTH;
    public static final AttributeDescriptor LABEL_MAXOVERLAPANGLE;
    public static final AttributeDescriptor LABEL_MAXSIZE;
    public static final AttributeDescriptor LABEL_MINDISTANCE;
    public static final AttributeDescriptor LABEL_MINFEATURESIZE;
    public static final AttributeDescriptor LABEL_MINSIZE;
    public static final AttributeDescriptor LABEL_OFFSET;
    /** [r] [g] [b] | [attribute] */
    public static final AttributeDescriptor LABEL_OUTLINECOLOR;
    /** [integer] */
    public static final AttributeDescriptor LABEL_OUTLINEWIDTH;
    public static final AttributeDescriptor LABEL_PARTIALS;
    /** [ul|uc|ur|cl|cc|cr|ll|lc|lr|auto] */
    public static final AttributeDescriptor LABEL_POSITION;
    public static final AttributeDescriptor LABEL_PRIORITY;
    public static final AttributeDescriptor LABEL_REPEATDISTANCE;
    public static final AttributeDescriptor LABEL_SHADOWCOLOR;
    public static final AttributeDescriptor LABEL_SHADOWSIZE;
    /** [double]|[tiny|small|medium|large|giant]|[attribute] */
    public static final AttributeDescriptor LABEL_SIZE;
    public static final AttributeDescriptor LABEL_STYLE;
    public static final AttributeDescriptor LABEL_TYPE;
    public static final AttributeDescriptor LABEL_WRAP;
                
    public static final FeatureType LAYER;
    public static final AttributeDescriptor LAYER_CLASS;
    public static final AttributeDescriptor LAYER_CLASSGROUP;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_CLASSITEM;
    public static final AttributeDescriptor LAYER_CLUSTER;
    public static final AttributeDescriptor LAYER_CONNECTION;
    public static final AttributeDescriptor LAYER_CONNECTIONTYPE;
    public static final AttributeDescriptor LAYER_DATA;
    public static final AttributeDescriptor LAYER_DEBUG;
    public static final AttributeDescriptor LAYER_DUMP;
    public static final AttributeDescriptor LAYER_EXTENT;
    public static final AttributeDescriptor LAYER_FEATURE;
    public static final AttributeDescriptor LAYER_FILTER;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_FILTERITEM;
    public static final AttributeDescriptor LAYER_FOOTER;
    public static final AttributeDescriptor LAYER_GRID;
    public static final AttributeDescriptor LAYER_GROUP;
    public static final AttributeDescriptor LAYER_HEADER;
    public static final AttributeDescriptor LAYER_JOIN;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_LABELANGLEITEM;
    public static final AttributeDescriptor LAYER_LABELCACHE;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_LABELITEM;
    public static final AttributeDescriptor LAYER_LABELMAXSCALEDENOM;
    public static final AttributeDescriptor LAYER_LABELMAXSCALE;
    public static final AttributeDescriptor LAYER_LABELMINSCALEDENOM;
    public static final AttributeDescriptor LAYER_LABELMINSCALE;
    public static final AttributeDescriptor LAYER_LABELREQUIRES;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_LABELSIZEITEM;
    public static final AttributeDescriptor LAYER_MAXFEATURES;
    public static final AttributeDescriptor LAYER_MAXGEOWIDTH;
    public static final AttributeDescriptor LAYER_MAXSCALEDENOM;
    public static final AttributeDescriptor LAYER_MAXSCALE;
    public static final AttributeDescriptor LAYER_METADATA;
    public static final AttributeDescriptor LAYER_MINGEOWIDTH;
    public static final AttributeDescriptor LAYER_MINSCALEDENOM;
    public static final AttributeDescriptor LAYER_MINSCALE;
    public static final AttributeDescriptor LAYER_NAME;
    public static final AttributeDescriptor LAYER_OFFSITE;
    /** [integer|alpha] */
    public static final AttributeDescriptor LAYER_OPACITY;
    public static final AttributeDescriptor LAYER_PLUGIN;
    public static final AttributeDescriptor LAYER_POSTLABELCACHE;
    public static final AttributeDescriptor LAYER_PROCESSING;
    public static final AttributeDescriptor LAYER_PROJECTION;
    public static final AttributeDescriptor LAYER_REQUIRES;
    public static final AttributeDescriptor LAYER_SIZEUNITS;
    public static final AttributeDescriptor LAYER_STATUS;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_STYLEITEM;
    public static final AttributeDescriptor LAYER_SYMBOLSCALEDENOM;
    public static final AttributeDescriptor LAYER_SYMBOLSCALE;
    public static final AttributeDescriptor LAYER_TEMPLATE;
    public static final AttributeDescriptor LAYER_TILEINDEX;
    /** [attribute] */
    public static final AttributeDescriptor LAYER_TILEITEM;
    public static final AttributeDescriptor LAYER_TOLERANCE;
    public static final AttributeDescriptor LAYER_TOLERANCEUNITS;
    public static final AttributeDescriptor LAYER_TRANSPARENCY;
    public static final AttributeDescriptor LAYER_TRANSFORM;
    public static final AttributeDescriptor LAYER_TYPE;
    public static final AttributeDescriptor LAYER_UNITS;
    
    public static final FeatureType LEGEND;
    public static final AttributeDescriptor LEGEND_IMAGECOLOR;
    public static final AttributeDescriptor LEGEND_INTERLACE;
    public static final AttributeDescriptor LEGEND_KEYSIZE;
    public static final AttributeDescriptor LEGEND_KEYSPACING;
    public static final AttributeDescriptor LEGEND_LABEL;
    public static final AttributeDescriptor LEGEND_OUTLINECOLOR;
    public static final AttributeDescriptor LEGEND_POSITION;
    public static final AttributeDescriptor LEGEND_POSTLABELCACHE;
    public static final AttributeDescriptor LEGEND_STATUS;
    public static final AttributeDescriptor LEGEND_TEMPLATE;
    public static final AttributeDescriptor LEGEND_TRANSPARENT;
    
    public static final FeatureType MAP;
    public static final AttributeDescriptor MAP_ANGLE;
    public static final AttributeDescriptor MAP_CONFIG;
    public static final AttributeDescriptor MAP_DATAPATTERN;
    public static final AttributeDescriptor MAP_DEBUG;
    public static final AttributeDescriptor MAP_DEFRESOLUTION;
    public static final AttributeDescriptor MAP_EXTENT;
    public static final AttributeDescriptor MAP_FONTSET;
    public static final AttributeDescriptor MAP_IMAGECOLOR;
    public static final AttributeDescriptor MAP_IMAGEQUALITY;
    public static final AttributeDescriptor MAP_IMAGETYPE;
    public static final AttributeDescriptor MAP_INTERLACE;
    public static final AttributeDescriptor MAP_LAYER;
    public static final AttributeDescriptor MAP_LEGEND;
    public static final AttributeDescriptor MAP_MAXSIZE;
    public static final AttributeDescriptor MAP_NAME;
    public static final AttributeDescriptor MAP_PROJECTION;
    public static final AttributeDescriptor MAP_QUERYMAP;
    public static final AttributeDescriptor MAP_REFERENCE;
    public static final AttributeDescriptor MAP_RESOLUTION;
    public static final AttributeDescriptor MAP_SCALEDENOM;
    public static final AttributeDescriptor MAP_SCALE;
    public static final AttributeDescriptor MAP_SCALEBAR;
    public static final AttributeDescriptor MAP_SHAPEPATH;
    public static final AttributeDescriptor MAP_SIZE;
    public static final AttributeDescriptor MAP_STATUS;
    public static final AttributeDescriptor MAP_SYMBOLSET;
    public static final AttributeDescriptor MAP_SYMBOL;
    public static final AttributeDescriptor MAP_TEMPLATEPATTERN;
    public static final AttributeDescriptor MAP_TRANSPARENT;
    public static final AttributeDescriptor MAP_UNITS;
    public static final AttributeDescriptor MAP_WEB;
    
    public static final FeatureType OUTPUTFORMAT;
    public static final AttributeDescriptor OUTPUTFORMAT_DRIVER;
    public static final AttributeDescriptor OUTPUTFORMAT_EXTENSION;
    public static final AttributeDescriptor OUTPUTFORMAT_FORMATOPTION;
    public static final AttributeDescriptor OUTPUTFORMAT_IMAGEMODE;
    public static final AttributeDescriptor OUTPUTFORMAT_MIMETYPE;
    public static final AttributeDescriptor OUTPUTFORMAT_NAME;
    public static final AttributeDescriptor OUTPUTFORMAT_TRANSPARENT;
                
    public static final FeatureType QUERYMAP;
    public static final AttributeDescriptor QUERYMAP_COLOR;
    public static final AttributeDescriptor QUERYMAP_SIZE;
    public static final AttributeDescriptor QUERYMAP_STATUS;
    public static final AttributeDescriptor QUERYMAP_STYLE;
                
    public static final FeatureType REFERENCE;
    public static final AttributeDescriptor REFERENCE_COLOR;
    public static final AttributeDescriptor REFERENCE_EXTENT;
    public static final AttributeDescriptor REFERENCE_IMAGE;
    public static final AttributeDescriptor REFERENCE_MARKER;
    public static final AttributeDescriptor REFERENCE_MARKERSIZE;
    public static final AttributeDescriptor REFERENCE_MINBOXSIZE;
    public static final AttributeDescriptor REFERENCE_MAXBOXSIZE;
    public static final AttributeDescriptor REFERENCE_OUTLINECOLOR;
    public static final AttributeDescriptor REFERENCE_SIZE;
    public static final AttributeDescriptor REFERENCE_STATUS;
    
    public static final FeatureType SCALEBAR;
    public static final AttributeDescriptor SCALEBAR_ALIGN;
    public static final AttributeDescriptor SCALEBAR_BACKGROUNDCOLOR;
    public static final AttributeDescriptor SCALEBAR_COLOR;
    public static final AttributeDescriptor SCALEBAR_IMAGECOLOR;
    public static final AttributeDescriptor SCALEBAR_INTERLACE;
    public static final AttributeDescriptor SCALEBAR_INTERVALS;
    public static final AttributeDescriptor SCALEBAR_LABEL;
    public static final AttributeDescriptor SCALEBAR_OUTLINECOLOR;
    public static final AttributeDescriptor SCALEBAR_POSITION;
    public static final AttributeDescriptor SCALEBAR_POSTLABELCACHE;
    public static final AttributeDescriptor SCALEBAR_SIZE;
    public static final AttributeDescriptor SCALEBAR_STATUS;
    public static final AttributeDescriptor SCALEBAR_STYLE;
    public static final AttributeDescriptor SCALEBAR_TRANSPARENT;
    public static final AttributeDescriptor SCALEBAR_UNITS;
    
    public static final FeatureType STYLE;
    public static final AttributeDescriptor STYLE_ANGLE;
    public static final AttributeDescriptor STYLE_ANGLEITEM;
    public static final AttributeDescriptor STYLE_ANTIALIAS;
    public static final AttributeDescriptor STYLE_BACKGROUNDCOLOR;
    /**  [r] [g] [b] | [attribute] */
    public static final AttributeDescriptor STYLE_COLOR;
    public static final AttributeDescriptor STYLE_GAP;
    public static final AttributeDescriptor STYLE_GEOMTRANSFORM;
    public static final AttributeDescriptor STYLE_LINECAP;
    public static final AttributeDescriptor STYLE_LINEJOIN;
    public static final AttributeDescriptor STYLE_LINEJOINMAXSIZE;
    public static final AttributeDescriptor STYLE_MAXSIZE;
    public static final AttributeDescriptor STYLE_MAXWIDTH;
    public static final AttributeDescriptor STYLE_MINSIZE;
    public static final AttributeDescriptor STYLE_MINWIDTH;
    public static final AttributeDescriptor STYLE_OFFSET;
    /** [integer|attribute] */
    public static final AttributeDescriptor STYLE_OPACITY;
    /** [r] [g] [b] | [attribute] */
    public static final AttributeDescriptor STYLE_OUTLINECOLOR;
    public static final AttributeDescriptor STYLE_OUTLINEWIDTH;
    public static final AttributeDescriptor STYLE_PATTERN;
    /** [double|attribute] */
    public static final AttributeDescriptor STYLE_SIZE;
    public static final AttributeDescriptor STYLE_SIZEITEM;
    public static final AttributeDescriptor STYLE_SYMBOL;
    /** [double|attribute] */
    public static final AttributeDescriptor STYLE_WIDTH;
    
    public static final FeatureType SYMBOL;
    public static final AttributeDescriptor SYMBOL_ANTIALIAS;
    public static final AttributeDescriptor SYMBOL_CHARACTER;
    public static final AttributeDescriptor SYMBOL_FILLED;
    public static final AttributeDescriptor SYMBOL_FONT;
    public static final AttributeDescriptor SYMBOL_GAP;
    public static final AttributeDescriptor SYMBOL_IMAGE;
    public static final AttributeDescriptor SYMBOL_NAME;
    public static final AttributeDescriptor SYMBOL_LINECAP;
    public static final AttributeDescriptor SYMBOL_LINEJOIN;
    public static final AttributeDescriptor SYMBOL_LINEJOINMAXSIZE;
    public static final AttributeDescriptor SYMBOL_PATTERN;
    public static final AttributeDescriptor SYMBOL_POINTS;
    public static final AttributeDescriptor SYMBOL_STYLE;
    public static final AttributeDescriptor SYMBOL_TRANSPARENT;
    public static final AttributeDescriptor SYMBOL_TYPE;
    
    public static final FeatureType WEB;
    public static final AttributeDescriptor WEB_BROWSEFORMAT;
    public static final AttributeDescriptor WEB_EMPTY;
    public static final AttributeDescriptor WEB_ERROR;
    public static final AttributeDescriptor WEB_FOOTER;
    public static final AttributeDescriptor WEB_HEADER;
    public static final AttributeDescriptor WEB_IMAGEPATH;
    public static final AttributeDescriptor WEB_IMAGEURL;
    public static final AttributeDescriptor WEB_LEGENDFORMAT;
    public static final AttributeDescriptor WEB_LOG;
    public static final AttributeDescriptor WEB_MAXSCALEDENOM;
    public static final AttributeDescriptor WEB_MAXSCALE;
    public static final AttributeDescriptor WEB_MAXTEMPLATE;
    public static final AttributeDescriptor WEB_METADATA;
    public static final AttributeDescriptor WEB_MINSCALEDENOM;
    public static final AttributeDescriptor WEB_MINSCALE;
    public static final AttributeDescriptor WEB_MINTEMPLATE;
    public static final AttributeDescriptor WEB_QUERYFORMAT;
    public static final AttributeDescriptor WEB_TEMPLATE;
    
    static {
        final AttributeTypeBuilder atb          = new AttributeTypeBuilder();
        final AttributeDescriptorBuilder adb    = new AttributeDescriptorBuilder();
        final FeatureTypeBuilder ftb            = new FeatureTypeBuilder();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"STYLE");
        STYLE_ANGLE             = ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            String.class,0,1,false,null);
        STYLE_ANGLEITEM         = ftb.add(new DefaultName(NAMESPACE, "ANGLEITEM"),        String.class,0,1,false,deprecated());
        STYLE_ANTIALIAS         = ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        STYLE_BACKGROUNDCOLOR   = ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        STYLE_COLOR             = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Expression.class,0,1,false,null);
        STYLE_GAP               = ftb.add(new DefaultName(NAMESPACE, "GAP"),              Double.class,0,1,false,null);
        STYLE_GEOMTRANSFORM     = ftb.add(new DefaultName(NAMESPACE, "GEOMTRANSFORM"),    String.class,0,1,false,null);
        STYLE_LINECAP           = ftb.add(new DefaultName(NAMESPACE, "LINECAP"),          String.class,0,1,false,null);
        STYLE_LINEJOIN          = ftb.add(new DefaultName(NAMESPACE, "LINEJOIN"),         String.class,0,1,false,null);
        STYLE_LINEJOINMAXSIZE   = ftb.add(new DefaultName(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class,0,1,false,null);
        STYLE_MAXSIZE           = ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Double.class,0,1,false,null);
        STYLE_MAXWIDTH          = ftb.add(new DefaultName(NAMESPACE, "MAXWIDTH"),         Double.class,0,1,false,null);
        STYLE_MINSIZE           = ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Double.class,0,1,false,null);
        STYLE_MINWIDTH          = ftb.add(new DefaultName(NAMESPACE, "MINWIDTH"),         Double.class,0,1,false,null);
        STYLE_OFFSET            = ftb.add(new DefaultName(NAMESPACE, "OFFSET"),           Point2D.class,0,1,false,null);
        STYLE_OPACITY           = ftb.add(new DefaultName(NAMESPACE, "OPACITY"),          Expression.class,0,1,false,null);
        STYLE_OUTLINECOLOR      = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Expression.class,0,1,false,null);
        STYLE_OUTLINEWIDTH      = ftb.add(new DefaultName(NAMESPACE, "OUTLINEWIDTH"),     Double.class,0,1,false,null);
        STYLE_PATTERN           = ftb.add(new DefaultName(NAMESPACE, "PATTERN"),          String.class,0,1,false,null);
        STYLE_SIZE              = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Expression.class,0,1,false,null);
        STYLE_SIZEITEM          = ftb.add(new DefaultName(NAMESPACE, "SIZEITEM"),         String.class,0,1,false,deprecated());
        STYLE_SYMBOL            = ftb.add(new DefaultName(NAMESPACE, "SYMBOL"),           String.class,0,1,false,null);
        STYLE_WIDTH             = ftb.add(new DefaultName(NAMESPACE, "WIDTH"),            Expression.class,0,1,false,null);
        STYLE = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LABEL");
        LABEL_ALIGN                 = ftb.add(new DefaultName(NAMESPACE, "ALIGN"),            String.class,0,1,false,null);
        LABEL_ANGLE                 = ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            String.class,0,1,false,null);
        LABEL_ANTIALIAS             = ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        LABEL_BACKGROUNDCOLOR       = ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,deprecated());
        LABEL_BACKGROUNDSHADOWCOLOR = ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDSHADOWCOLOR"),Color.class,0,1,false,deprecated());
        LABEL_BACKGROUNDSHADOWSIZE  = ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDSHADOWSIZE"),Point2D.class,0,1,false,deprecated());
        LABEL_BUFFER                = ftb.add(new DefaultName(NAMESPACE, "BUFFER"),           Integer.class,0,1,false,null);
        LABEL_COLOR                 = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Expression.class,0,1,false,null);
        LABEL_ENCODING              = ftb.add(new DefaultName(NAMESPACE, "ENCODING"),         String.class,0,1,false,null);
        LABEL_FONT                  = ftb.add(new DefaultName(NAMESPACE, "FONT"),             String.class,0,1,false,null);
        LABEL_FORCE                 = ftb.add(new DefaultName(NAMESPACE, "FORCE"),            Boolean.class,0,1,false,null);
        LABEL_MAXLENGTH             = ftb.add(new DefaultName(NAMESPACE, "MAXLENGTH"),        Integer.class,0,1,false,null);
        LABEL_MAXOVERLAPANGLE       = ftb.add(new DefaultName(NAMESPACE, "MAXOVERLAPANGLE"),  Double.class,0,1,false,null);
        LABEL_MAXSIZE               = ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Double.class,0,1,false,null);
        LABEL_MINDISTANCE           = ftb.add(new DefaultName(NAMESPACE, "MINDISTANCE"),      Integer.class,0,1,false,null);
        LABEL_MINFEATURESIZE        = ftb.add(new DefaultName(NAMESPACE, "MINFEATURESIZE"),   String.class,0,1,false,null);
        LABEL_MINSIZE               = ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Double.class,0,1,false,null);
        LABEL_OFFSET                = ftb.add(new DefaultName(NAMESPACE, "OFFSET"),           Point2D.class,0,1,false,null);
        LABEL_OUTLINECOLOR          = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Expression.class,0,1,false,null);
        LABEL_OUTLINEWIDTH          = ftb.add(new DefaultName(NAMESPACE, "OUTLINEWIDTH"),     Integer.class,0,1,false,null);
        LABEL_PARTIALS              = ftb.add(new DefaultName(NAMESPACE, "PARTIALS"),         Boolean.class,0,1,false,null);
        LABEL_POSITION              = ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        LABEL_PRIORITY              = ftb.add(new DefaultName(NAMESPACE, "PRIORITY"),         String.class,0,1,false,null);
        LABEL_REPEATDISTANCE        = ftb.add(new DefaultName(NAMESPACE, "REPEATDISTANCE"),   Integer.class,0,1,false,null);
        LABEL_SHADOWCOLOR           = ftb.add(new DefaultName(NAMESPACE, "SHADOWCOLOR"),      Color.class,0,1,false,null);
        LABEL_SHADOWSIZE            = ftb.add(new DefaultName(NAMESPACE, "SHADOWSIZE"),       String.class,0,1,false,null);
        LABEL_SIZE                  = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Expression.class,0,1,false,null);
        LABEL_STYLE                 = ftb.add(STYLE,STYLE.getName(),null,0,1,false,null);
        LABEL_TYPE                  = ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        LABEL_WRAP                  = ftb.add(new DefaultName(NAMESPACE, "WRAP"),             String.class,0,1,false,null);
        LABEL = ftb.buildFeatureType();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"WEB");
        WEB_BROWSEFORMAT        = ftb.add(new DefaultName(NAMESPACE, "BROWSEFORMAT"),     String.class,0,1,false,null);
        WEB_EMPTY               = ftb.add(new DefaultName(NAMESPACE, "EMPTY"),            String.class,0,1,false,null);
        WEB_ERROR               = ftb.add(new DefaultName(NAMESPACE, "ERROR"),            String.class,0,1,false,null);
        WEB_FOOTER              = ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        WEB_HEADER              = ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        WEB_IMAGEPATH           = ftb.add(new DefaultName(NAMESPACE, "IMAGEPATH"),        String.class,0,1,false,null);
        WEB_IMAGEURL            = ftb.add(new DefaultName(NAMESPACE, "IMAGEURL"),         String.class,0,1,false,null);
        WEB_LEGENDFORMAT        = ftb.add(new DefaultName(NAMESPACE, "LEGENDFORMAT"),     String.class,0,1,false,null);
        WEB_LOG                 = ftb.add(new DefaultName(NAMESPACE, "LOG"),              String.class,0,1,false,deprecated());
        WEB_MAXSCALEDENOM       = ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        WEB_MAXSCALE            = ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        WEB_MAXTEMPLATE         = ftb.add(new DefaultName(NAMESPACE, "MAXTEMPLATE"),      String.class,0,1,false,null);
        WEB_METADATA            = ftb.add(new DefaultName(NAMESPACE, "METADATA"),         String.class,0,1,false,null);
        WEB_MINSCALEDENOM       = ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        WEB_MINSCALE            = ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        WEB_MINTEMPLATE         = ftb.add(new DefaultName(NAMESPACE, "MINTEMPLATE"),      String.class,0,1,false,null);
        WEB_QUERYFORMAT         = ftb.add(new DefaultName(NAMESPACE, "QUERYFORMAT"),      String.class,0,1,false,null);
        WEB_TEMPLATE            = ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);        
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        WEB = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"CLASS");
        CLASS_BACKGROUNDCOLOR   = ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        CLASS_COLOR             = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        CLASS_DEBUG             = ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            Boolean.class,0,1,false,null);
        CLASS_EXPRESSION        = ftb.add(new DefaultName(NAMESPACE, "EXPRESSION"),       String.class,0,1,false,null);
        CLASS_GROUP             = ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        CLASS_KEYIMAGE          = ftb.add(new DefaultName(NAMESPACE, "KEYIMAGE"),         String.class,0,1,false,null);
        CLASS_LABEL             = ftb.add(LABEL,LABEL.getName(),null,0,1,false,null);
        CLASS_MAXSCALEDENOM     = ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        CLASS_MAXSCALE          = ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        CLASS_MAXSIZE           = ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Integer.class,0,1,false,null);
        CLASS_MINSCALEDENOM     = ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        CLASS_MINSCALE          = ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        CLASS_MINSIZE           = ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Integer.class,0,1,false,null);
        CLASS_NAME              = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        CLASS_OUTLINECOLOR      = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        CLASS_SIZE              = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Integer.class,0,1,false,null);
        CLASS_STATUS            = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        CLASS_STYLE             = ftb.add(STYLE,STYLE.getName(),null,0,1,false,null);
        CLASS_SYMBOL            = ftb.add(new DefaultName(NAMESPACE, "SYMBOL"),           String.class,0,1,false,null);
        CLASS_TEMPLATE          = ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        CLASS_TEXT              = ftb.add(new DefaultName(NAMESPACE, "TEXT"),             Expression.class,0,1,false,null);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        CLASS = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"CLUSTER");
        CLUSTER_MAXDISTANCE = ftb.add(new DefaultName(NAMESPACE, "MAXDISTANCE"),      Double.class,0,1,false,null);
        CLUSTER_REGION      = ftb.add(new DefaultName(NAMESPACE, "REGION"),           String.class,0,1,false,null);
        CLUSTER_GROUP       = ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        CLUSTER_FILTER      = ftb.add(new DefaultName(NAMESPACE, "FILTER"),           String.class,0,1,false,null);
        CLUSTER = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"FEATURE");
        FEATURE_POINTS  = ftb.add(new DefaultName(NAMESPACE, "POINTS"),           String.class,0,1,false,null);
        FEATURE_ITEMS   = ftb.add(new DefaultName(NAMESPACE, "ITEMS"),            String.class,0,1,false,null);
        FEATURE_TEXT    = ftb.add(new DefaultName(NAMESPACE, "TEXT"),             String.class,0,1,false,null);
        FEATURE_WKT     = ftb.add(new DefaultName(NAMESPACE, "WKT"),              String.class,0,1,false,null);
        FEATURE = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"GRID");
        GRID_LABELFORMAT    = ftb.add(new DefaultName(NAMESPACE, "LABELFORMAT"),      String.class,0,1,false,null);
        GRID_MINARCS        = ftb.add(new DefaultName(NAMESPACE, "MINARCS"),          Double.class,0,1,false,null);
        GRID_MAXARCS        = ftb.add(new DefaultName(NAMESPACE, "MAXARCS"),          Double.class,0,1,false,null);
        GRID_MININTERVAL    = ftb.add(new DefaultName(NAMESPACE, "MININTERVAL"),      Double.class,0,1,false,null);
        GRID_MAXINTERVAL    = ftb.add(new DefaultName(NAMESPACE, "MAXINTERVAL"),      Double.class,0,1,false,null);
        GRID_MINSUBDIVIDE   = ftb.add(new DefaultName(NAMESPACE, "MINSUBDIVIDE"),     Double.class,0,1,false,null);
        GRID_MAXSUBDIVIDE   = ftb.add(new DefaultName(NAMESPACE, "MAXSUBDIVIDE"),     Double.class,0,1,false,null);
        GRID = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"JOIN");
        JOIN_CONNECTION     = ftb.add(new DefaultName(NAMESPACE, "CONNECTION"),       String.class,0,1,false,null);
        JOIN_CONNECTIONTYPE = ftb.add(new DefaultName(NAMESPACE, "CONNECTIONTYPE"),   String.class,0,1,false,null);
        JOIN_FOOTER         = ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        JOIN_FROM           = ftb.add(new DefaultName(NAMESPACE, "FROM"),             String.class,0,1,false,null);
        JOIN_HEADER         = ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        JOIN_NAME           = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        JOIN_TABLE          = ftb.add(new DefaultName(NAMESPACE, "TABLE"),            String.class,0,1,false,null);
        JOIN_TEMPLATE       = ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        JOIN_TO             = ftb.add(new DefaultName(NAMESPACE, "TO"),               String.class,0,1,false,null);
        JOIN_TYPE           = ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        JOIN = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LAYER");
        LAYER_CLASS                 = ftb.add(CLASS,CLASS.getName(),null,0,Integer.MAX_VALUE,false,null);
        LAYER_CLASSGROUP            = ftb.add(new DefaultName(NAMESPACE, "CLASSGROUP"),       String.class,0,1,false,null);
        LAYER_CLASSITEM             = ftb.add(new DefaultName(NAMESPACE, "CLASSITEM"),        PropertyName.class,0,1,false,null);
        LAYER_CLUSTER               = ftb.add(CLUSTER,CLUSTER.getName(),null,0,1,false,null);
        LAYER_CONNECTION            = ftb.add(new DefaultName(NAMESPACE, "CONNECTION"),       String.class,0,1,false,null);
        LAYER_CONNECTIONTYPE        = ftb.add(new DefaultName(NAMESPACE, "CONNECTIONTYPE"),   String.class,0,1,false,null);
        LAYER_DATA                  = ftb.add(new DefaultName(NAMESPACE, "DATA"),             String.class,0,1,false,null);
        LAYER_DEBUG                 = ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            String.class,0,1,false,null);
        LAYER_DUMP                  = ftb.add(new DefaultName(NAMESPACE, "DUMP"),             Boolean.class,0,1,false,null);
        LAYER_EXTENT                = ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        LAYER_FEATURE               = ftb.add(FEATURE,FEATURE.getName(),null,0,1,false,null);
        LAYER_FILTER                = ftb.add(new DefaultName(NAMESPACE, "FILTER"),           String.class,0,1,false,null);
        LAYER_FILTERITEM            = ftb.add(new DefaultName(NAMESPACE, "FILTERITEM"),       PropertyName.class,0,1,false,null);
        LAYER_FOOTER                = ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        LAYER_GRID                  = ftb.add(GRID,GRID.getName(),null,0,1,false,null);
        LAYER_GROUP                 = ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        LAYER_HEADER                = ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        LAYER_JOIN                  = ftb.add(JOIN,JOIN.getName(),null,0,1,false,null);
        LAYER_LABELANGLEITEM        = ftb.add(new DefaultName(NAMESPACE, "LABELANGLEITEM"),   PropertyName.class,0,1,false,null);
        LAYER_LABELCACHE            = ftb.add(new DefaultName(NAMESPACE, "LABELCACHE"),       Boolean.class,0,1,false,null);
        LAYER_LABELITEM             = ftb.add(new DefaultName(NAMESPACE, "LABELITEM"),        PropertyName.class,0,1,false,null);
        LAYER_LABELMAXSCALEDENOM    = ftb.add(new DefaultName(NAMESPACE, "LABELMAXSCALEDENOM"),Double.class,0,1,false,null);
        LAYER_LABELMAXSCALE         = ftb.add(new DefaultName(NAMESPACE, "LABELMAXSCALE"),    Double.class,0,1,false,deprecated());
        LAYER_LABELMINSCALEDENOM    = ftb.add(new DefaultName(NAMESPACE, "LABELMINSCALEDENOM"),Double.class,0,1,false,null);
        LAYER_LABELMINSCALE         = ftb.add(new DefaultName(NAMESPACE, "LABELMINSCALE"),    Double.class,0,1,false,deprecated());
        LAYER_LABELREQUIRES         = ftb.add(new DefaultName(NAMESPACE, "LABELREQUIRES"),    String.class,0,1,false,null);
        LAYER_LABELSIZEITEM         = ftb.add(new DefaultName(NAMESPACE, "LABELSIZEITEM"),    PropertyName.class,0,1,false,deprecated());
        LAYER_MAXFEATURES           = ftb.add(new DefaultName(NAMESPACE, "MAXFEATURES"),      Integer.class,0,1,false,null);
        LAYER_MAXGEOWIDTH           = ftb.add(new DefaultName(NAMESPACE, "MAXGEOWIDTH"),      Double.class,0,1,false,null);
        LAYER_MAXSCALEDENOM         = ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        LAYER_MAXSCALE              = ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        LAYER_METADATA              = ftb.add(new DefaultName(NAMESPACE, "METADATA"),         String.class,0,1,false,null);
        LAYER_MINGEOWIDTH           = ftb.add(new DefaultName(NAMESPACE, "MINGEOWIDTH"),      Double.class,0,1,false,null);
        LAYER_MINSCALEDENOM         = ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        LAYER_MINSCALE              = ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        LAYER_NAME                  = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        LAYER_OFFSITE               = ftb.add(new DefaultName(NAMESPACE, "OFFSITE"),          Color.class,0,1,false,null);
        LAYER_OPACITY               = ftb.add(new DefaultName(NAMESPACE, "OPACITY"),          String.class,0,1,false,null);
        LAYER_PLUGIN                = ftb.add(new DefaultName(NAMESPACE, "PLUGIN"),           String.class,0,1,false,null);
        LAYER_POSTLABELCACHE        = ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        LAYER_PROCESSING            = ftb.add(new DefaultName(NAMESPACE, "PROCESSING"),       String.class,0,1,false,null);
        LAYER_PROJECTION            = ftb.add(new DefaultName(NAMESPACE, "PROJECTION"),       String.class,0,1,false,null);
        LAYER_REQUIRES              = ftb.add(new DefaultName(NAMESPACE, "REQUIRES"),         String.class,0,1,false,null);
        LAYER_SIZEUNITS             = ftb.add(new DefaultName(NAMESPACE, "SIZEUNITS"),        String.class,0,1,false,null);
        LAYER_STATUS                = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        LAYER_STYLEITEM             = ftb.add(new DefaultName(NAMESPACE, "STYLEITEM"),        PropertyName.class,0,1,false,null);
        LAYER_SYMBOLSCALEDENOM      = ftb.add(new DefaultName(NAMESPACE, "SYMBOLSCALEDENOM"), Double.class,0,1,false,null);
        LAYER_SYMBOLSCALE           = ftb.add(new DefaultName(NAMESPACE, "SYMBOLSCALE"),      Double.class,0,1,false,deprecated());
        LAYER_TEMPLATE              = ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        LAYER_TILEINDEX             = ftb.add(new DefaultName(NAMESPACE, "TILEINDEX"),        String.class,0,1,false,null);
        LAYER_TILEITEM              = ftb.add(new DefaultName(NAMESPACE, "TILEITEM"),         PropertyName.class,0,1,false,null);
        LAYER_TOLERANCE             = ftb.add(new DefaultName(NAMESPACE, "TOLERANCE"),        Double.class,0,1,false,null);
        LAYER_TOLERANCEUNITS        = ftb.add(new DefaultName(NAMESPACE, "TOLERANCEUNITS"),   String.class,0,1,false,null);
        LAYER_TRANSPARENCY          = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENCY"),     String.class,0,1,false,deprecated());
        LAYER_TRANSFORM             = ftb.add(new DefaultName(NAMESPACE, "TRANSFORM"),        String.class,0,1,false,null);
        LAYER_TYPE                  = ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        LAYER_UNITS                 = ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        LAYER = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LEGEND");
        LEGEND_IMAGECOLOR       = ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        LEGEND_INTERLACE        = ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,null);
        LEGEND_KEYSIZE          = ftb.add(new DefaultName(NAMESPACE, "KEYSIZE"),          Point2D.class,0,1,false,null);
        LEGEND_KEYSPACING       = ftb.add(new DefaultName(NAMESPACE, "KEYSPACING"),       Point2D.class,0,1,false,null);
        LEGEND_LABEL            = ftb.add(LABEL,LABEL.getName(),null,0,Integer.MAX_VALUE,false,null);
        LEGEND_OUTLINECOLOR     = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        LEGEND_POSITION         = ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        LEGEND_POSTLABELCACHE   = ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        LEGEND_STATUS           = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        LEGEND_TEMPLATE         = ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        LEGEND_TRANSPARENT      = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        LEGEND = ftb.buildFeatureType();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"OUTPUTFORMAT");
        OUTPUTFORMAT_DRIVER         = ftb.add(new DefaultName(NAMESPACE, "DRIVER"),           String.class,0,1,false,null);
        OUTPUTFORMAT_EXTENSION      = ftb.add(new DefaultName(NAMESPACE, "EXTENSION"),        String.class,0,1,false,null);
        OUTPUTFORMAT_FORMATOPTION   = ftb.add(new DefaultName(NAMESPACE, "FORMATOPTION"),     String.class,0,Integer.MAX_VALUE,false,null);
        OUTPUTFORMAT_IMAGEMODE      = ftb.add(new DefaultName(NAMESPACE, "IMAGEMODE"),        String.class,0,1,false,null);
        OUTPUTFORMAT_MIMETYPE       = ftb.add(new DefaultName(NAMESPACE, "MIMETYPE"),         String.class,0,1,false,null);
        OUTPUTFORMAT_NAME           = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        OUTPUTFORMAT_TRANSPARENT    = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,null);
        OUTPUTFORMAT = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"QUERYMAP");
        QUERYMAP_COLOR      = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        QUERYMAP_SIZE       = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        QUERYMAP_STATUS     = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        QUERYMAP_STYLE      = ftb.add(new DefaultName(NAMESPACE, "STYLE"),            String.class,0,1,false,null);
        QUERYMAP = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"REFERENCE");
        REFERENCE_COLOR         = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        REFERENCE_EXTENT        = ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        REFERENCE_IMAGE         = ftb.add(new DefaultName(NAMESPACE, "IMAGE"),            String.class,0,1,false,null);
        REFERENCE_MARKER        = ftb.add(new DefaultName(NAMESPACE, "MARKER"),           String.class,0,1,false,null);
        REFERENCE_MARKERSIZE    = ftb.add(new DefaultName(NAMESPACE, "MARKERSIZE"),       Integer.class,0,1,false,null);
        REFERENCE_MINBOXSIZE    = ftb.add(new DefaultName(NAMESPACE, "MINBOXSIZE"),       Integer.class,0,1,false,null);
        REFERENCE_MAXBOXSIZE    = ftb.add(new DefaultName(NAMESPACE, "MAXBOXSIZE"),       Integer.class,0,1,false,null);
        REFERENCE_OUTLINECOLOR  = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        REFERENCE_SIZE          = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        REFERENCE_STATUS        = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        REFERENCE = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"SCALEBAR");
        SCALEBAR_ALIGN          = ftb.add(new DefaultName(NAMESPACE, "ALIGN"),            String.class,0,1,false,null);
        SCALEBAR_BACKGROUNDCOLOR= ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        SCALEBAR_COLOR          = ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        SCALEBAR_IMAGECOLOR     = ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        SCALEBAR_INTERLACE      = ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,deprecated());
        SCALEBAR_INTERVALS      = ftb.add(new DefaultName(NAMESPACE, "INTERVALS"),        Integer.class,0,1,false,null);
        SCALEBAR_LABEL          = ftb.add(LABEL,LABEL.getName(),null,0,1,false,null);
        SCALEBAR_OUTLINECOLOR   = ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        SCALEBAR_POSITION       = ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        SCALEBAR_POSTLABELCACHE = ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        SCALEBAR_SIZE           = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        SCALEBAR_STATUS         = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        SCALEBAR_STYLE          = ftb.add(new DefaultName(NAMESPACE, "STYLE"),            Integer.class,0,1,false,null);
        SCALEBAR_TRANSPARENT    = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        SCALEBAR_UNITS          = ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        SCALEBAR = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"SYMBOL");
        SYMBOL_ANTIALIAS        = ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        SYMBOL_CHARACTER        = ftb.add(new DefaultName(NAMESPACE, "CHARACTER"),        String.class,0,1,false,null);
        SYMBOL_FILLED           = ftb.add(new DefaultName(NAMESPACE, "FILLED"),           Boolean.class,0,1,false,null);
        SYMBOL_FONT             = ftb.add(new DefaultName(NAMESPACE, "FONT"),             String.class,0,1,false,null);
        SYMBOL_GAP              = ftb.add(new DefaultName(NAMESPACE, "GAP"),              Integer.class,0,1,false,deprecated());
        SYMBOL_IMAGE            = ftb.add(new DefaultName(NAMESPACE, "IMAGE"),            String.class,0,1,false,null);
        SYMBOL_NAME             = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        SYMBOL_LINECAP          = ftb.add(new DefaultName(NAMESPACE, "LINECAP"),          String.class,0,1,false,deprecated());
        SYMBOL_LINEJOIN         = ftb.add(new DefaultName(NAMESPACE, "LINEJOIN"),         String.class,0,1,false,deprecated());
        SYMBOL_LINEJOINMAXSIZE  = ftb.add(new DefaultName(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class,0,1,false,deprecated());
        SYMBOL_PATTERN          = ftb.add(new DefaultName(NAMESPACE, "PATTERN"),          String.class,0,1,false,deprecated());
        SYMBOL_POINTS           = ftb.add(new DefaultName(NAMESPACE, "POINTS"),           String.class,0,1,false,null);
        SYMBOL_STYLE            = ftb.add(new DefaultName(NAMESPACE, "STYLE"),            String.class,0,1,false,deprecated());
        SYMBOL_TRANSPARENT      = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Integer.class,0,1,false,null);
        /** [ellipse|hatch|pixmap|simple|truetype|vector] */
        SYMBOL_TYPE             = ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        SYMBOL = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"MAP");
        MAP_ANGLE               = ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            Double.class,0,1,false,null);
        MAP_CONFIG              = ftb.add(new DefaultName(NAMESPACE, "CONFIG"),           String.class,0,1,false,null);
        MAP_DATAPATTERN         = ftb.add(new DefaultName(NAMESPACE, "DATAPATTERN"),      String.class,0,1,false,null);
        MAP_DEBUG               = ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            String.class,0,1,false,null);
        MAP_DEFRESOLUTION       = ftb.add(new DefaultName(NAMESPACE, "DEFRESOLUTION"),    Integer.class,0,1,false,null);
        MAP_EXTENT              = ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        MAP_FONTSET             = ftb.add(new DefaultName(NAMESPACE, "FONTSET"),          String.class,0,1,false,null);
        MAP_IMAGECOLOR          = ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        MAP_IMAGEQUALITY        = ftb.add(new DefaultName(NAMESPACE, "IMAGEQUALITY"),     Integer.class,0,1,false,deprecated());
        MAP_IMAGETYPE           = ftb.add(new DefaultName(NAMESPACE, "IMAGETYPE"),        String.class,0,1,false,null);
        MAP_INTERLACE           = ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,deprecated());
        MAP_LAYER               = ftb.add(LAYER,LAYER.getName(),null,0,Integer.MAX_VALUE,false,null);
        MAP_LEGEND              = ftb.add(LEGEND,LEGEND.getName(),null,0,Integer.MAX_VALUE,false,null);
        MAP_MAXSIZE             = ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Integer.class,0,1,false,null);
        MAP_NAME                = ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        MAP_PROJECTION          = ftb.add(new DefaultName(NAMESPACE, "PROJECTION"),       String.class,0,1,false,null);
        MAP_QUERYMAP            = ftb.add(QUERYMAP,QUERYMAP.getName(),null,0,1,false,null);
        MAP_REFERENCE           = ftb.add(REFERENCE,REFERENCE.getName(),null,0,1,false,null);
        MAP_RESOLUTION          = ftb.add(new DefaultName(NAMESPACE, "RESOLUTION"),       Integer.class,0,1,false,null);
        MAP_SCALEDENOM          = ftb.add(new DefaultName(NAMESPACE, "SCALEDENOM"),       Double.class,0,1,false,null);
        MAP_SCALE               = ftb.add(new DefaultName(NAMESPACE, "SCALE"),            Double.class,0,1,false,deprecated());
        MAP_SCALEBAR            = ftb.add(SCALEBAR,SCALEBAR.getName(),null,0,1,false,null);
        MAP_SHAPEPATH           = ftb.add(new DefaultName(NAMESPACE, "SHAPEPATH"),        String.class,0,1,false,null);
        MAP_SIZE                = ftb.add(new DefaultName(NAMESPACE, "SIZE"),             String.class,0,1,false,null);
        MAP_STATUS              = ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        MAP_SYMBOLSET           = ftb.add(new DefaultName(NAMESPACE, "SYMBOLSET"),        String.class,0,1,false,null);
        MAP_SYMBOL              = ftb.add(SYMBOL,SYMBOL.getName(),null,0,Integer.MAX_VALUE,false,null);
        MAP_TEMPLATEPATTERN     = ftb.add(new DefaultName(NAMESPACE, "TEMPLATEPATTERN"),  String.class,0,1,false,null);
        MAP_TRANSPARENT         = ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        MAP_UNITS               = ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        MAP_WEB                 = ftb.add(WEB,WEB.getName(),null,0,1,false,null);
        MAP = ftb.buildFeatureType();
        
    }
    
    private static final List<FeatureType> ALL_TYPES = UnmodifiableArrayList.wrap(
            CLASS,CLUSTER,FEATURE,GRID,JOIN,LABEL,LAYER,LEGEND,MAP,
            OUTPUTFORMAT,QUERYMAP,REFERENCE,SCALEBAR,STYLE,SYMBOL,WEB);
    
    public static FeatureType getType(final Name name){
        for(FeatureType ft : ALL_TYPES){
            if(ft.getName().equals(name)){
                return ft;
            }
        }
        return null;
    }
    
    public static FeatureType getType(final String name){
        for(FeatureType ft : ALL_TYPES){
            if(ft.getName().getLocalPart().equalsIgnoreCase(name)){
                return ft;
            }
        }
        return null;
    }
    
    private static Map<Object,Object> deprecated(){
        return Collections.singletonMap((Object)"Deprecated", (Object)Boolean.TRUE);
    }
    
    private MapfileTypes(){
    }
    
}
