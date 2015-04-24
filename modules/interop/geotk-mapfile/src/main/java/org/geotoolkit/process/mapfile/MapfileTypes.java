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
package org.geotoolkit.process.mapfile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.util.UnmodifiableArrayList;

import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

/**
 * Declares all mapfile types.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MapfileTypes {

    public static final String NAMESPACE = "http://mapserver.org";

    public static final FeatureType CLASS;
    public static final GenericName CLASS_BACKGROUNDCOLOR;
    public static final GenericName CLASS_COLOR;
    public static final GenericName CLASS_DEBUG;
    public static final GenericName CLASS_EXPRESSION;
    public static final GenericName CLASS_GROUP;
    public static final GenericName CLASS_KEYIMAGE;
    public static final GenericName CLASS_LABEL;
    public static final GenericName CLASS_MAXSCALEDENOM;
    public static final GenericName CLASS_MAXSCALE;
    public static final GenericName CLASS_MAXSIZE;
    public static final GenericName CLASS_MINSCALEDENOM;
    public static final GenericName CLASS_MINSCALE;
    public static final GenericName CLASS_MINSIZE;
    public static final GenericName CLASS_NAME;
    public static final GenericName CLASS_OUTLINECOLOR;
    public static final GenericName CLASS_SIZE;
    public static final GenericName CLASS_STATUS;
    public static final GenericName CLASS_STYLE;
    public static final GenericName CLASS_SYMBOL;
    public static final GenericName CLASS_TEMPLATE;
    /** Expression */
    public static final GenericName CLASS_TEXT;

    public static final FeatureType CLUSTER;
    public static final GenericName CLUSTER_MAXDISTANCE;
    public static final GenericName CLUSTER_REGION;
    public static final GenericName CLUSTER_GROUP;
    public static final GenericName CLUSTER_FILTER;

    public static final FeatureType FEATURE;
    public static final GenericName FEATURE_POINTS;
    public static final GenericName FEATURE_ITEMS;
    public static final GenericName FEATURE_TEXT;
    public static final GenericName FEATURE_WKT;

    public static final FeatureType GRID;
    public static final GenericName GRID_LABELFORMAT;
    public static final GenericName GRID_MINARCS;
    public static final GenericName GRID_MAXARCS;
    public static final GenericName GRID_MININTERVAL;
    public static final GenericName GRID_MAXINTERVAL;
    public static final GenericName GRID_MINSUBDIVIDE;
    public static final GenericName GRID_MAXSUBDIVIDE;

    public static final FeatureType JOIN;
    public static final GenericName JOIN_CONNECTION;
    public static final GenericName JOIN_CONNECTIONTYPE;
    public static final GenericName JOIN_FOOTER;
    public static final GenericName JOIN_FROM;
    public static final GenericName JOIN_HEADER;
    public static final GenericName JOIN_NAME;
    public static final GenericName JOIN_TABLE;
    public static final GenericName JOIN_TEMPLATE;
    public static final GenericName JOIN_TO;
    public static final GenericName JOIN_TYPE;

    public static final FeatureType LABEL;
    public static final GenericName LABEL_ALIGN;
    /** [double|auto|follow|attribute] */
    public static final GenericName LABEL_ANGLE;
    public static final GenericName LABEL_ANTIALIAS;
    public static final GenericName LABEL_BACKGROUNDCOLOR;
    public static final GenericName LABEL_BACKGROUNDSHADOWCOLOR;
    public static final GenericName LABEL_BACKGROUNDSHADOWSIZE;
    public static final GenericName LABEL_BUFFER;
    /** [r] [g] [b] | [attribute] */
    public static final GenericName LABEL_COLOR;
    public static final GenericName LABEL_ENCODING;
    public static final GenericName LABEL_FONT;
    public static final GenericName LABEL_FORCE;
    public static final GenericName LABEL_MAXLENGTH;
    public static final GenericName LABEL_MAXOVERLAPANGLE;
    public static final GenericName LABEL_MAXSIZE;
    public static final GenericName LABEL_MINDISTANCE;
    public static final GenericName LABEL_MINFEATURESIZE;
    public static final GenericName LABEL_MINSIZE;
    public static final GenericName LABEL_OFFSET;
    /** [r] [g] [b] | [attribute] */
    public static final GenericName LABEL_OUTLINECOLOR;
    /** [integer] */
    public static final GenericName LABEL_OUTLINEWIDTH;
    public static final GenericName LABEL_PARTIALS;
    /** [ul|uc|ur|cl|cc|cr|ll|lc|lr|auto] */
    public static final GenericName LABEL_POSITION;
    public static final GenericName LABEL_PRIORITY;
    public static final GenericName LABEL_REPEATDISTANCE;
    public static final GenericName LABEL_SHADOWCOLOR;
    public static final GenericName LABEL_SHADOWSIZE;
    /** [double]|[tiny|small|medium|large|giant]|[attribute] */
    public static final GenericName LABEL_SIZE;
    public static final GenericName LABEL_STYLE;
    public static final GenericName LABEL_TYPE;
    public static final GenericName LABEL_WRAP;

    public static final FeatureType LAYER;
    public static final GenericName LAYER_CLASS;
    public static final GenericName LAYER_CLASSGROUP;
    /** [attribute] */
    public static final GenericName LAYER_CLASSITEM;
    public static final GenericName LAYER_CLUSTER;
    public static final GenericName LAYER_CONNECTION;
    public static final GenericName LAYER_CONNECTIONTYPE;
    public static final GenericName LAYER_DATA;
    public static final GenericName LAYER_DEBUG;
    public static final GenericName LAYER_DUMP;
    public static final GenericName LAYER_EXTENT;
    public static final GenericName LAYER_FEATURE;
    public static final GenericName LAYER_FILTER;
    /** [attribute] */
    public static final GenericName LAYER_FILTERITEM;
    public static final GenericName LAYER_FOOTER;
    public static final GenericName LAYER_GRID;
    public static final GenericName LAYER_GROUP;
    public static final GenericName LAYER_HEADER;
    public static final GenericName LAYER_JOIN;
    /** [attribute] */
    public static final GenericName LAYER_LABELANGLEITEM;
    public static final GenericName LAYER_LABELCACHE;
    /** [attribute] */
    public static final GenericName LAYER_LABELITEM;
    public static final GenericName LAYER_LABELMAXSCALEDENOM;
    public static final GenericName LAYER_LABELMAXSCALE;
    public static final GenericName LAYER_LABELMINSCALEDENOM;
    public static final GenericName LAYER_LABELMINSCALE;
    public static final GenericName LAYER_LABELREQUIRES;
    /** [attribute] */
    public static final GenericName LAYER_LABELSIZEITEM;
    public static final GenericName LAYER_MAXFEATURES;
    public static final GenericName LAYER_MAXGEOWIDTH;
    public static final GenericName LAYER_MAXSCALEDENOM;
    public static final GenericName LAYER_MAXSCALE;
    public static final GenericName LAYER_METADATA;
    public static final GenericName LAYER_MINGEOWIDTH;
    public static final GenericName LAYER_MINSCALEDENOM;
    public static final GenericName LAYER_MINSCALE;
    public static final GenericName LAYER_NAME;
    public static final GenericName LAYER_OFFSITE;
    /** [integer|alpha] */
    public static final GenericName LAYER_OPACITY;
    public static final GenericName LAYER_PLUGIN;
    public static final GenericName LAYER_POSTLABELCACHE;
    public static final GenericName LAYER_PROCESSING;
    public static final GenericName LAYER_PROJECTION;
    public static final GenericName LAYER_REQUIRES;
    public static final GenericName LAYER_SIZEUNITS;
    public static final GenericName LAYER_STATUS;
    /** [attribute] */
    public static final GenericName LAYER_STYLEITEM;
    public static final GenericName LAYER_SYMBOLSCALEDENOM;
    public static final GenericName LAYER_SYMBOLSCALE;
    public static final GenericName LAYER_TEMPLATE;
    public static final GenericName LAYER_TILEINDEX;
    /** [attribute] */
    public static final GenericName LAYER_TILEITEM;
    public static final GenericName LAYER_TOLERANCE;
    public static final GenericName LAYER_TOLERANCEUNITS;
    public static final GenericName LAYER_TRANSPARENCY;
    public static final GenericName LAYER_TRANSFORM;
    public static final GenericName LAYER_TYPE;
    public static final GenericName LAYER_UNITS;

    public static final FeatureType LEGEND;
    public static final GenericName LEGEND_IMAGECOLOR;
    public static final GenericName LEGEND_INTERLACE;
    public static final GenericName LEGEND_KEYSIZE;
    public static final GenericName LEGEND_KEYSPACING;
    public static final GenericName LEGEND_LABEL;
    public static final GenericName LEGEND_OUTLINECOLOR;
    public static final GenericName LEGEND_POSITION;
    public static final GenericName LEGEND_POSTLABELCACHE;
    public static final GenericName LEGEND_STATUS;
    public static final GenericName LEGEND_TEMPLATE;
    public static final GenericName LEGEND_TRANSPARENT;

    public static final FeatureType MAP;
    public static final GenericName MAP_ANGLE;
    public static final GenericName MAP_CONFIG;
    public static final GenericName MAP_DATAPATTERN;
    public static final GenericName MAP_DEBUG;
    public static final GenericName MAP_DEFRESOLUTION;
    public static final GenericName MAP_EXTENT;
    public static final GenericName MAP_FONTSET;
    public static final GenericName MAP_IMAGECOLOR;
    public static final GenericName MAP_IMAGEQUALITY;
    public static final GenericName MAP_IMAGETYPE;
    public static final GenericName MAP_INTERLACE;
    public static final GenericName MAP_LAYER;
    public static final GenericName MAP_LEGEND;
    public static final GenericName MAP_MAXSIZE;
    public static final GenericName MAP_NAME;
    public static final GenericName MAP_PROJECTION;
    public static final GenericName MAP_QUERYMAP;
    public static final GenericName MAP_REFERENCE;
    public static final GenericName MAP_RESOLUTION;
    public static final GenericName MAP_SCALEDENOM;
    public static final GenericName MAP_SCALE;
    public static final GenericName MAP_SCALEBAR;
    public static final GenericName MAP_SHAPEPATH;
    public static final GenericName MAP_SIZE;
    public static final GenericName MAP_STATUS;
    public static final GenericName MAP_SYMBOLSET;
    public static final GenericName MAP_SYMBOL;
    public static final GenericName MAP_TEMPLATEPATTERN;
    public static final GenericName MAP_TRANSPARENT;
    public static final GenericName MAP_UNITS;
    public static final GenericName MAP_WEB;

    public static final FeatureType OUTPUTFORMAT;
    public static final GenericName OUTPUTFORMAT_DRIVER;
    public static final GenericName OUTPUTFORMAT_EXTENSION;
    public static final GenericName OUTPUTFORMAT_FORMATOPTION;
    public static final GenericName OUTPUTFORMAT_IMAGEMODE;
    public static final GenericName OUTPUTFORMAT_MIMETYPE;
    public static final GenericName OUTPUTFORMAT_NAME;
    public static final GenericName OUTPUTFORMAT_TRANSPARENT;

    public static final FeatureType QUERYMAP;
    public static final GenericName QUERYMAP_COLOR;
    public static final GenericName QUERYMAP_SIZE;
    public static final GenericName QUERYMAP_STATUS;
    public static final GenericName QUERYMAP_STYLE;

    public static final FeatureType REFERENCE;
    public static final GenericName REFERENCE_COLOR;
    public static final GenericName REFERENCE_EXTENT;
    public static final GenericName REFERENCE_IMAGE;
    public static final GenericName REFERENCE_MARKER;
    public static final GenericName REFERENCE_MARKERSIZE;
    public static final GenericName REFERENCE_MINBOXSIZE;
    public static final GenericName REFERENCE_MAXBOXSIZE;
    public static final GenericName REFERENCE_OUTLINECOLOR;
    public static final GenericName REFERENCE_SIZE;
    public static final GenericName REFERENCE_STATUS;

    public static final FeatureType SCALEBAR;
    public static final GenericName SCALEBAR_ALIGN;
    public static final GenericName SCALEBAR_BACKGROUNDCOLOR;
    public static final GenericName SCALEBAR_COLOR;
    public static final GenericName SCALEBAR_IMAGECOLOR;
    public static final GenericName SCALEBAR_INTERLACE;
    public static final GenericName SCALEBAR_INTERVALS;
    public static final GenericName SCALEBAR_LABEL;
    public static final GenericName SCALEBAR_OUTLINECOLOR;
    public static final GenericName SCALEBAR_POSITION;
    public static final GenericName SCALEBAR_POSTLABELCACHE;
    public static final GenericName SCALEBAR_SIZE;
    public static final GenericName SCALEBAR_STATUS;
    public static final GenericName SCALEBAR_STYLE;
    public static final GenericName SCALEBAR_TRANSPARENT;
    public static final GenericName SCALEBAR_UNITS;

    public static final FeatureType STYLE;
    public static final GenericName STYLE_ANGLE;
    public static final GenericName STYLE_ANGLEITEM;
    public static final GenericName STYLE_ANTIALIAS;
    public static final GenericName STYLE_BACKGROUNDCOLOR;
    /**  [r] [g] [b] | [attribute] */
    public static final GenericName STYLE_COLOR;
    public static final GenericName STYLE_GAP;
    public static final GenericName STYLE_GEOMTRANSFORM;
    /** [butt|round|square] */
    public static final GenericName STYLE_LINECAP;
    /** [round|miter|bevel] */
    public static final GenericName STYLE_LINEJOIN;
    public static final GenericName STYLE_LINEJOINMAXSIZE;
    public static final GenericName STYLE_MAXSIZE;
    public static final GenericName STYLE_MAXWIDTH;
    public static final GenericName STYLE_MINSIZE;
    public static final GenericName STYLE_MINWIDTH;
    public static final GenericName STYLE_OFFSET;
    /** [integer|attribute] */
    public static final GenericName STYLE_OPACITY;
    /** [r] [g] [b] | [attribute] */
    public static final GenericName STYLE_OUTLINECOLOR;
    /** no info, I guess it's an expression */
    public static final GenericName STYLE_OUTLINEWIDTH;
    /** [double on] [double off] [double on] [double off] ... END */
    public static final GenericName STYLE_PATTERN;
    /** [double|attribute] */
    public static final GenericName STYLE_SIZE;
    public static final GenericName STYLE_SIZEITEM;
    public static final GenericName STYLE_SYMBOL;
    /** [double|attribute] */
    public static final GenericName STYLE_WIDTH;

    public static final FeatureType SYMBOL;
    public static final GenericName SYMBOL_ANTIALIAS;
    public static final GenericName SYMBOL_CHARACTER;
    public static final GenericName SYMBOL_FILLED;
    public static final GenericName SYMBOL_FONT;
    public static final GenericName SYMBOL_GAP;
    public static final GenericName SYMBOL_IMAGE;
    public static final GenericName SYMBOL_NAME;
    public static final GenericName SYMBOL_LINECAP;
    public static final GenericName SYMBOL_LINEJOIN;
    public static final GenericName SYMBOL_LINEJOINMAXSIZE;
    public static final GenericName SYMBOL_PATTERN;
    public static final GenericName SYMBOL_POINTS;
    public static final GenericName SYMBOL_STYLE;
    public static final GenericName SYMBOL_TRANSPARENT;
    public static final GenericName SYMBOL_TYPE;

    public static final FeatureType WEB;
    public static final GenericName WEB_BROWSEFORMAT;
    public static final GenericName WEB_EMPTY;
    public static final GenericName WEB_ERROR;
    public static final GenericName WEB_FOOTER;
    public static final GenericName WEB_HEADER;
    public static final GenericName WEB_IMAGEPATH;
    public static final GenericName WEB_IMAGEURL;
    public static final GenericName WEB_LEGENDFORMAT;
    public static final GenericName WEB_LOG;
    public static final GenericName WEB_MAXSCALEDENOM;
    public static final GenericName WEB_MAXSCALE;
    public static final GenericName WEB_MAXTEMPLATE;
    public static final GenericName WEB_METADATA;
    public static final GenericName WEB_MINSCALEDENOM;
    public static final GenericName WEB_MINSCALE;
    public static final GenericName WEB_MINTEMPLATE;
    public static final GenericName WEB_QUERYFORMAT;
    public static final GenericName WEB_TEMPLATE;

    static {
        FeatureTypeBuilder ftb;


        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"STYLE");
        STYLE_ANGLE             = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANGLE"),            String.class);
        STYLE_ANGLEITEM         = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANGLEITEM"),        String.class); //deprecated
        STYLE_ANTIALIAS         = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANTIALIAS"),        Boolean.class);
        STYLE_BACKGROUNDCOLOR   = addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class);
        STYLE_COLOR             = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Expression.class);
        STYLE_GAP               = addAttribute(ftb,NamesExt.create(NAMESPACE, "GAP"),              Double.class);
        STYLE_GEOMTRANSFORM     = addAttribute(ftb,NamesExt.create(NAMESPACE, "GEOMTRANSFORM"),    String.class);
        STYLE_LINECAP           = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINECAP"),          Literal.class);
        STYLE_LINEJOIN          = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINEJOIN"),         Literal.class);
        STYLE_LINEJOINMAXSIZE   = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class);
        STYLE_MAXSIZE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSIZE"),          Double.class);
        STYLE_MAXWIDTH          = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXWIDTH"),         Double.class);
        STYLE_MINSIZE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSIZE"),          Double.class);
        STYLE_MINWIDTH          = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINWIDTH"),         Double.class);
        STYLE_OFFSET            = addAttribute(ftb,NamesExt.create(NAMESPACE, "OFFSET"),           Point2D.class);
        STYLE_OPACITY           = addAttribute(ftb,NamesExt.create(NAMESPACE, "OPACITY"),          Expression.class);
        STYLE_OUTLINECOLOR      = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Expression.class);
        STYLE_OUTLINEWIDTH      = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINEWIDTH"),     Expression.class);
        STYLE_PATTERN           = addAttribute(ftb,NamesExt.create(NAMESPACE, "PATTERN"),          float[].class);
        STYLE_SIZE              = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Expression.class);
        STYLE_SIZEITEM          = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZEITEM"),         String.class); //deprecated
        STYLE_SYMBOL            = addAttribute(ftb,NamesExt.create(NAMESPACE, "SYMBOL"),           String.class);
        STYLE_WIDTH             = addAttribute(ftb,NamesExt.create(NAMESPACE, "WIDTH"),            Expression.class);
        STYLE = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"LABEL");
        LABEL_ALIGN                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "ALIGN"),            String.class);
        LABEL_ANGLE                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANGLE"),            String.class);
        LABEL_ANTIALIAS             = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANTIALIAS"),        Boolean.class);
        LABEL_BACKGROUNDCOLOR       = addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class); //deprecated
        LABEL_BACKGROUNDSHADOWCOLOR = addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDSHADOWCOLOR"),Color.class); //deprecated
        LABEL_BACKGROUNDSHADOWSIZE  = addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDSHADOWSIZE"),Point2D.class); //deprecated
        LABEL_BUFFER                = addAttribute(ftb,NamesExt.create(NAMESPACE, "BUFFER"),           Integer.class);
        LABEL_COLOR                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Expression.class);
        LABEL_ENCODING              = addAttribute(ftb,NamesExt.create(NAMESPACE, "ENCODING"),         String.class);
        LABEL_FONT                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "FONT"),             String.class);
        LABEL_FORCE                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "FORCE"),            Boolean.class);
        LABEL_MAXLENGTH             = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXLENGTH"),        Integer.class);
        LABEL_MAXOVERLAPANGLE       = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXOVERLAPANGLE"),  Double.class);
        LABEL_MAXSIZE               = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSIZE"),          Double.class);
        LABEL_MINDISTANCE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINDISTANCE"),      Integer.class);
        LABEL_MINFEATURESIZE        = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINFEATURESIZE"),   String.class);
        LABEL_MINSIZE               = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSIZE"),          Double.class);
        LABEL_OFFSET                = addAttribute(ftb,NamesExt.create(NAMESPACE, "OFFSET"),           Point2D.class);
        LABEL_OUTLINECOLOR          = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Expression.class);
        LABEL_OUTLINEWIDTH          = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINEWIDTH"),     Integer.class);
        LABEL_PARTIALS              = addAttribute(ftb,NamesExt.create(NAMESPACE, "PARTIALS"),         Boolean.class);
        LABEL_POSITION              = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSITION"),         String.class);
        LABEL_PRIORITY              = addAttribute(ftb,NamesExt.create(NAMESPACE, "PRIORITY"),         String.class);
        LABEL_REPEATDISTANCE        = addAttribute(ftb,NamesExt.create(NAMESPACE, "REPEATDISTANCE"),   Integer.class);
        LABEL_SHADOWCOLOR           = addAttribute(ftb,NamesExt.create(NAMESPACE, "SHADOWCOLOR"),      Color.class);
        LABEL_SHADOWSIZE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "SHADOWSIZE"),       String.class);
        LABEL_SIZE                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Expression.class);
        LABEL_STYLE                 = STYLE.getName();
                                      ftb.addAssociation(STYLE).setName(STYLE.getName());
        LABEL_TYPE                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "TYPE"),             String.class);
        LABEL_WRAP                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "WRAP"),             String.class);
        LABEL = ftb.build();


        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"WEB");
        WEB_BROWSEFORMAT        = addAttribute(ftb,NamesExt.create(NAMESPACE, "BROWSEFORMAT"),     String.class);
        WEB_EMPTY               = addAttribute(ftb,NamesExt.create(NAMESPACE, "EMPTY"),            String.class);
        WEB_ERROR               = addAttribute(ftb,NamesExt.create(NAMESPACE, "ERROR"),            String.class);
        WEB_FOOTER              = addAttribute(ftb,NamesExt.create(NAMESPACE, "FOOTER"),           String.class);
        WEB_HEADER              = addAttribute(ftb,NamesExt.create(NAMESPACE, "HEADER"),           String.class);
        WEB_IMAGEPATH           = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGEPATH"),        String.class);
        WEB_IMAGEURL            = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGEURL"),         String.class);
        WEB_LEGENDFORMAT        = addAttribute(ftb,NamesExt.create(NAMESPACE, "LEGENDFORMAT"),     String.class);
        WEB_LOG                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "LOG"),              String.class); //deprecated
        WEB_MAXSCALEDENOM       = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALEDENOM"),    Double.class);
        WEB_MAXSCALE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALE"),         Double.class); //deprecated
        WEB_MAXTEMPLATE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXTEMPLATE"),      String.class);
        WEB_METADATA            = addAttribute(ftb,NamesExt.create(NAMESPACE, "METADATA"),         String.class);
        WEB_MINSCALEDENOM       = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALEDENOM"),    Double.class);
        WEB_MINSCALE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALE"),         Double.class); //deprecated
        WEB_MINTEMPLATE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINTEMPLATE"),      String.class);
        WEB_QUERYFORMAT         = addAttribute(ftb,NamesExt.create(NAMESPACE, "QUERYFORMAT"),      String.class);
        WEB_TEMPLATE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATE"),         String.class);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class);
        WEB = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"CLASS");
        CLASS_BACKGROUNDCOLOR   = addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class);
        CLASS_COLOR             = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Color.class);
        CLASS_DEBUG             = addAttribute(ftb,NamesExt.create(NAMESPACE, "DEBUG"),            Boolean.class);
        CLASS_EXPRESSION        = addAttribute(ftb,NamesExt.create(NAMESPACE, "EXPRESSION"),       String.class);
        CLASS_GROUP             = addAttribute(ftb,NamesExt.create(NAMESPACE, "GROUP"),            String.class);
        CLASS_KEYIMAGE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "KEYIMAGE"),         String.class);
        CLASS_LABEL             = LABEL.getName();
                                  ftb.addAssociation(LABEL).setName(LABEL.getName());
        CLASS_MAXSCALEDENOM     = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALEDENOM"),    Double.class);
        CLASS_MAXSCALE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALE"),         Double.class); //deprecated
        CLASS_MAXSIZE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSIZE"),          Integer.class);
        CLASS_MINSCALEDENOM     = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALEDENOM"),    Double.class);
        CLASS_MINSCALE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALE"),         Double.class); //deprecated
        CLASS_MINSIZE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSIZE"),          Integer.class);
        CLASS_NAME              = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        CLASS_OUTLINECOLOR      = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Color.class);
        CLASS_SIZE              = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Integer.class);
        CLASS_STATUS            = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           Boolean.class);
        CLASS_STYLE             = STYLE.getName();
                                  ftb.addAssociation(STYLE).setName(STYLE.getName());
        CLASS_SYMBOL            = addAttribute(ftb,NamesExt.create(NAMESPACE, "SYMBOL"),           String.class);
        CLASS_TEMPLATE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATE"),         String.class);
        CLASS_TEXT              = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEXT"),             Expression.class);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class);
        CLASS = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"CLUSTER");
        CLUSTER_MAXDISTANCE = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXDISTANCE"),      Double.class);
        CLUSTER_REGION      = addAttribute(ftb,NamesExt.create(NAMESPACE, "REGION"),           String.class);
        CLUSTER_GROUP       = addAttribute(ftb,NamesExt.create(NAMESPACE, "GROUP"),            String.class);
        CLUSTER_FILTER      = addAttribute(ftb,NamesExt.create(NAMESPACE, "FILTER"),           String.class);
        CLUSTER = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"FEATURE");
        FEATURE_POINTS  = addAttribute(ftb,NamesExt.create(NAMESPACE, "POINTS"),           String.class);
        FEATURE_ITEMS   = addAttribute(ftb,NamesExt.create(NAMESPACE, "ITEMS"),            String.class);
        FEATURE_TEXT    = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEXT"),             String.class);
        FEATURE_WKT     = addAttribute(ftb,NamesExt.create(NAMESPACE, "WKT"),              String.class);
        FEATURE = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"GRID");
        GRID_LABELFORMAT    = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELFORMAT"),      String.class);
        GRID_MINARCS        = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINARCS"),          Double.class);
        GRID_MAXARCS        = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXARCS"),          Double.class);
        GRID_MININTERVAL    = addAttribute(ftb,NamesExt.create(NAMESPACE, "MININTERVAL"),      Double.class);
        GRID_MAXINTERVAL    = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXINTERVAL"),      Double.class);
        GRID_MINSUBDIVIDE   = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSUBDIVIDE"),     Double.class);
        GRID_MAXSUBDIVIDE   = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSUBDIVIDE"),     Double.class);
        GRID = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"JOIN");
        JOIN_CONNECTION     = addAttribute(ftb,NamesExt.create(NAMESPACE, "CONNECTION"),       String.class);
        JOIN_CONNECTIONTYPE = addAttribute(ftb,NamesExt.create(NAMESPACE, "CONNECTIONTYPE"),   String.class);
        JOIN_FOOTER         = addAttribute(ftb,NamesExt.create(NAMESPACE, "FOOTER"),           String.class);
        JOIN_FROM           = addAttribute(ftb,NamesExt.create(NAMESPACE, "FROM"),             String.class);
        JOIN_HEADER         = addAttribute(ftb,NamesExt.create(NAMESPACE, "HEADER"),           String.class);
        JOIN_NAME           = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        JOIN_TABLE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "TABLE"),            String.class);
        JOIN_TEMPLATE       = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATE"),         String.class);
        JOIN_TO             = addAttribute(ftb,NamesExt.create(NAMESPACE, "TO"),               String.class);
        JOIN_TYPE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "TYPE"),             String.class);
        JOIN = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"LAYER");
        LAYER_CLASS                 = CLASS.getName();
                                      ftb.addAssociation(CLASS).setName(CLASS.getName()).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        LAYER_CLASSGROUP            = addAttribute(ftb,NamesExt.create(NAMESPACE, "CLASSGROUP"),       String.class);
        LAYER_CLASSITEM             = addAttribute(ftb,NamesExt.create(NAMESPACE, "CLASSITEM"),        PropertyName.class);
        LAYER_CLUSTER               = CLUSTER.getName();
                                      ftb.addAssociation(CLUSTER).setName(CLUSTER.getName());
        LAYER_CONNECTION            = addAttribute(ftb,NamesExt.create(NAMESPACE, "CONNECTION"),       String.class);
        LAYER_CONNECTIONTYPE        = addAttribute(ftb,NamesExt.create(NAMESPACE, "CONNECTIONTYPE"),   String.class);
        LAYER_DATA                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "DATA"),             String.class);
        LAYER_DEBUG                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "DEBUG"),            String.class);
        LAYER_DUMP                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "DUMP"),             Boolean.class);
        LAYER_EXTENT                = addAttribute(ftb,NamesExt.create(NAMESPACE, "EXTENT"),           String.class);
        LAYER_FEATURE               = FEATURE.getName();
                                      ftb.addAssociation(FEATURE).setName(FEATURE.getName());
        LAYER_FILTER                = addAttribute(ftb,NamesExt.create(NAMESPACE, "FILTER"),           String.class);
        LAYER_FILTERITEM            = addAttribute(ftb,NamesExt.create(NAMESPACE, "FILTERITEM"),       PropertyName.class);
        LAYER_FOOTER                = addAttribute(ftb,NamesExt.create(NAMESPACE, "FOOTER"),           String.class);
        LAYER_GRID                  = GRID.getName();
                                      ftb.addAssociation(GRID).setName(GRID.getName());
        LAYER_GROUP                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "GROUP"),            String.class);
        LAYER_HEADER                = addAttribute(ftb,NamesExt.create(NAMESPACE, "HEADER"),           String.class);
        LAYER_JOIN                  = JOIN.getName();
                                      ftb.addAssociation(JOIN).setName(JOIN.getName());
        LAYER_LABELANGLEITEM        = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELANGLEITEM"),   PropertyName.class);
        LAYER_LABELCACHE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELCACHE"),       Boolean.class);
        LAYER_LABELITEM             = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELITEM"),        PropertyName.class);
        LAYER_LABELMAXSCALEDENOM    = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELMAXSCALEDENOM"),Double.class);
        LAYER_LABELMAXSCALE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELMAXSCALE"),    Double.class); //deprecated
        LAYER_LABELMINSCALEDENOM    = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELMINSCALEDENOM"),Double.class);
        LAYER_LABELMINSCALE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELMINSCALE"),    Double.class); //deprecated
        LAYER_LABELREQUIRES         = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELREQUIRES"),    String.class);
        LAYER_LABELSIZEITEM         = addAttribute(ftb,NamesExt.create(NAMESPACE, "LABELSIZEITEM"),    PropertyName.class); //deprecated
        LAYER_MAXFEATURES           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXFEATURES"),      Integer.class);
        LAYER_MAXGEOWIDTH           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXGEOWIDTH"),      Double.class);
        LAYER_MAXSCALEDENOM         = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALEDENOM"),    Double.class);
        LAYER_MAXSCALE              = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSCALE"),         Double.class); //deprecated
        LAYER_METADATA              = addAttribute(ftb,NamesExt.create(NAMESPACE, "METADATA"),         String.class);
        LAYER_MINGEOWIDTH           = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINGEOWIDTH"),      Double.class);
        LAYER_MINSCALEDENOM         = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALEDENOM"),    Double.class);
        LAYER_MINSCALE              = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINSCALE"),         Double.class); //deprecated
        LAYER_NAME                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        LAYER_OFFSITE               = addAttribute(ftb,NamesExt.create(NAMESPACE, "OFFSITE"),          Color.class);
        LAYER_OPACITY               = addAttribute(ftb,NamesExt.create(NAMESPACE, "OPACITY"),          String.class);
        LAYER_PLUGIN                = addAttribute(ftb,NamesExt.create(NAMESPACE, "PLUGIN"),           String.class);
        LAYER_POSTLABELCACHE        = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSTLABELCACHE"),   Boolean.class);
        LAYER_PROCESSING            = addAttribute(ftb,NamesExt.create(NAMESPACE, "PROCESSING"),       String.class);
        LAYER_PROJECTION            = addAttribute(ftb,NamesExt.create(NAMESPACE, "PROJECTION"),       String.class);
        LAYER_REQUIRES              = addAttribute(ftb,NamesExt.create(NAMESPACE, "REQUIRES"),         String.class);
        LAYER_SIZEUNITS             = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZEUNITS"),        String.class);
        LAYER_STATUS                = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           String.class);
        LAYER_STYLEITEM             = addAttribute(ftb,NamesExt.create(NAMESPACE, "STYLEITEM"),        PropertyName.class);
        LAYER_SYMBOLSCALEDENOM      = addAttribute(ftb,NamesExt.create(NAMESPACE, "SYMBOLSCALEDENOM"), Double.class);
        LAYER_SYMBOLSCALE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "SYMBOLSCALE"),      Double.class); //deprecated
        LAYER_TEMPLATE              = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATE"),         String.class);
        LAYER_TILEINDEX             = addAttribute(ftb,NamesExt.create(NAMESPACE, "TILEINDEX"),        String.class);
        LAYER_TILEITEM              = addAttribute(ftb,NamesExt.create(NAMESPACE, "TILEITEM"),         PropertyName.class);
        LAYER_TOLERANCE             = addAttribute(ftb,NamesExt.create(NAMESPACE, "TOLERANCE"),        Double.class);
        LAYER_TOLERANCEUNITS        = addAttribute(ftb,NamesExt.create(NAMESPACE, "TOLERANCEUNITS"),   String.class);
        LAYER_TRANSPARENCY          = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENCY"),     String.class); //deprecated
        LAYER_TRANSFORM             = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSFORM"),        String.class);
        LAYER_TYPE                  = addAttribute(ftb,NamesExt.create(NAMESPACE, "TYPE"),             String.class);
        LAYER_UNITS                 = addAttribute(ftb,NamesExt.create(NAMESPACE, "UNITS"),            String.class);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class);
        LAYER = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"LEGEND");
        LEGEND_IMAGECOLOR       = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGECOLOR"),       Color.class);
        LEGEND_INTERLACE        = addAttribute(ftb,NamesExt.create(NAMESPACE, "INTERLACE"),        Boolean.class);
        LEGEND_KEYSIZE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "KEYSIZE"),          Point2D.class);
        LEGEND_KEYSPACING       = addAttribute(ftb,NamesExt.create(NAMESPACE, "KEYSPACING"),       Point2D.class);
        LEGEND_LABEL            = LABEL.getName();
                                  ftb.addAssociation(LABEL).setName(LABEL.getName()).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        LEGEND_OUTLINECOLOR     = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Color.class);
        LEGEND_POSITION         = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSITION"),         String.class);
        LEGEND_POSTLABELCACHE   = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSTLABELCACHE"),   Boolean.class);
        LEGEND_STATUS           = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           String.class);
        LEGEND_TEMPLATE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATE"),         String.class);
        LEGEND_TRANSPARENT      = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENT"),      Boolean.class); //deprecated
        LEGEND = ftb.build();


        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"OUTPUTFORMAT");
        OUTPUTFORMAT_DRIVER         = addAttribute(ftb,NamesExt.create(NAMESPACE, "DRIVER"),           String.class);
        OUTPUTFORMAT_EXTENSION      = addAttribute(ftb,NamesExt.create(NAMESPACE, "EXTENSION"),        String.class);
        OUTPUTFORMAT_FORMATOPTION   = NamesExt.create(NAMESPACE, "FORMATOPTION");
                                      ftb.addAttribute(String.class).setName(NamesExt.create(NAMESPACE, "FORMATOPTION")).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        OUTPUTFORMAT_IMAGEMODE      = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGEMODE"),        String.class);
        OUTPUTFORMAT_MIMETYPE       = addAttribute(ftb,NamesExt.create(NAMESPACE, "MIMETYPE"),         String.class);
        OUTPUTFORMAT_NAME           = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        OUTPUTFORMAT_TRANSPARENT    = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENT"),      Boolean.class);
        OUTPUTFORMAT = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"QUERYMAP");
        QUERYMAP_COLOR      = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Color.class);
        QUERYMAP_SIZE       = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Point2D.class);
        QUERYMAP_STATUS     = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           Boolean.class);
        QUERYMAP_STYLE      = addAttribute(ftb,NamesExt.create(NAMESPACE, "STYLE"),            String.class);
        QUERYMAP = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"REFERENCE");
        REFERENCE_COLOR         = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Color.class);
        REFERENCE_EXTENT        = addAttribute(ftb,NamesExt.create(NAMESPACE, "EXTENT"),           String.class);
        REFERENCE_IMAGE         = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGE"),            String.class);
        REFERENCE_MARKER        = addAttribute(ftb,NamesExt.create(NAMESPACE, "MARKER"),           String.class);
        REFERENCE_MARKERSIZE    = addAttribute(ftb,NamesExt.create(NAMESPACE, "MARKERSIZE"),       Integer.class);
        REFERENCE_MINBOXSIZE    = addAttribute(ftb,NamesExt.create(NAMESPACE, "MINBOXSIZE"),       Integer.class);
        REFERENCE_MAXBOXSIZE    = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXBOXSIZE"),       Integer.class);
        REFERENCE_OUTLINECOLOR  = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Color.class);
        REFERENCE_SIZE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Point2D.class);
        REFERENCE_STATUS        = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           Boolean.class);
        REFERENCE = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"SCALEBAR");
        SCALEBAR_ALIGN          = addAttribute(ftb,NamesExt.create(NAMESPACE, "ALIGN"),            String.class);
        SCALEBAR_BACKGROUNDCOLOR= addAttribute(ftb,NamesExt.create(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class);
        SCALEBAR_COLOR          = addAttribute(ftb,NamesExt.create(NAMESPACE, "COLOR"),            Color.class);
        SCALEBAR_IMAGECOLOR     = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGECOLOR"),       Color.class);
        SCALEBAR_INTERLACE      = addAttribute(ftb,NamesExt.create(NAMESPACE, "INTERLACE"),        Boolean.class); //deprecated
        SCALEBAR_INTERVALS      = addAttribute(ftb,NamesExt.create(NAMESPACE, "INTERVALS"),        Integer.class);
        SCALEBAR_LABEL          = LABEL.getName();
                                  ftb.addAssociation(LABEL).setName(LABEL.getName());
        SCALEBAR_OUTLINECOLOR   = addAttribute(ftb,NamesExt.create(NAMESPACE, "OUTLINECOLOR"),     Color.class);
        SCALEBAR_POSITION       = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSITION"),         String.class);
        SCALEBAR_POSTLABELCACHE = addAttribute(ftb,NamesExt.create(NAMESPACE, "POSTLABELCACHE"),   Boolean.class);
        SCALEBAR_SIZE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             Point2D.class);
        SCALEBAR_STATUS         = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           String.class);
        SCALEBAR_STYLE          = addAttribute(ftb,NamesExt.create(NAMESPACE, "STYLE"),            Integer.class);
        SCALEBAR_TRANSPARENT    = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENT"),      Boolean.class); //deprecated
        SCALEBAR_UNITS          = addAttribute(ftb,NamesExt.create(NAMESPACE, "UNITS"),            String.class);
        SCALEBAR = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"SYMBOL");
        SYMBOL_ANTIALIAS        = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANTIALIAS"),        Boolean.class);
        SYMBOL_CHARACTER        = addAttribute(ftb,NamesExt.create(NAMESPACE, "CHARACTER"),        String.class);
        SYMBOL_FILLED           = addAttribute(ftb,NamesExt.create(NAMESPACE, "FILLED"),           Boolean.class);
        SYMBOL_FONT             = addAttribute(ftb,NamesExt.create(NAMESPACE, "FONT"),             String.class);
        SYMBOL_GAP              = addAttribute(ftb,NamesExt.create(NAMESPACE, "GAP"),              Integer.class); //deprecated
        SYMBOL_IMAGE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGE"),            String.class);
        SYMBOL_NAME             = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        SYMBOL_LINECAP          = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINECAP"),          String.class); //deprecated
        SYMBOL_LINEJOIN         = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINEJOIN"),         String.class); //deprecated
        SYMBOL_LINEJOINMAXSIZE  = addAttribute(ftb,NamesExt.create(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class); //deprecated
        SYMBOL_PATTERN          = addAttribute(ftb,NamesExt.create(NAMESPACE, "PATTERN"),          String.class); //deprecated
        SYMBOL_POINTS           = addAttribute(ftb,NamesExt.create(NAMESPACE, "POINTS"),           String.class);
        SYMBOL_STYLE            = addAttribute(ftb,NamesExt.create(NAMESPACE, "STYLE"),            String.class); //deprecated
        SYMBOL_TRANSPARENT      = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENT"),      Integer.class);
        /** [ellipse|hatch|pixmap|simple|truetype|vector] */
        SYMBOL_TYPE             = addAttribute(ftb,NamesExt.create(NAMESPACE, "TYPE"),             String.class);
        SYMBOL = ftb.build();

        //----------------------------------------------------------------------
        ftb = new FeatureTypeBuilder();
        ftb.setName(NAMESPACE,"MAP");
        MAP_ANGLE               = addAttribute(ftb,NamesExt.create(NAMESPACE, "ANGLE"),            Double.class);
        MAP_CONFIG              = addAttribute(ftb,NamesExt.create(NAMESPACE, "CONFIG"),           String.class);
        MAP_DATAPATTERN         = addAttribute(ftb,NamesExt.create(NAMESPACE, "DATAPATTERN"),      String.class);
        MAP_DEBUG               = addAttribute(ftb,NamesExt.create(NAMESPACE, "DEBUG"),            String.class);
        MAP_DEFRESOLUTION       = addAttribute(ftb,NamesExt.create(NAMESPACE, "DEFRESOLUTION"),    Integer.class);
        MAP_EXTENT              = addAttribute(ftb,NamesExt.create(NAMESPACE, "EXTENT"),           String.class);
        MAP_FONTSET             = addAttribute(ftb,NamesExt.create(NAMESPACE, "FONTSET"),          String.class);
        MAP_IMAGECOLOR          = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGECOLOR"),       Color.class);
        MAP_IMAGEQUALITY        = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGEQUALITY"),     Integer.class); //deprecated
        MAP_IMAGETYPE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "IMAGETYPE"),        String.class);
        MAP_INTERLACE           = addAttribute(ftb,NamesExt.create(NAMESPACE, "INTERLACE"),        Boolean.class); //deprecated
        MAP_LAYER               = LAYER.getName();
                                  ftb.addAssociation(LAYER).setName(LAYER.getName()).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        MAP_LEGEND              = LEGEND.getName();
                                  ftb.addAssociation(LEGEND).setName(LEGEND.getName()).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        MAP_MAXSIZE             = addAttribute(ftb,NamesExt.create(NAMESPACE, "MAXSIZE"),          Integer.class);
        MAP_NAME                = addAttribute(ftb,NamesExt.create(NAMESPACE, "NAME"),             String.class);
        MAP_PROJECTION          = addAttribute(ftb,NamesExt.create(NAMESPACE, "PROJECTION"),       String.class);
        MAP_QUERYMAP            = QUERYMAP.getName();
                                  ftb.addAssociation(QUERYMAP).setName(QUERYMAP.getName());
        MAP_REFERENCE           = REFERENCE.getName();
                                  ftb.addAssociation(REFERENCE).setName(REFERENCE.getName());
        MAP_RESOLUTION          = addAttribute(ftb,NamesExt.create(NAMESPACE, "RESOLUTION"),       Integer.class);
        MAP_SCALEDENOM          = addAttribute(ftb,NamesExt.create(NAMESPACE, "SCALEDENOM"),       Double.class);
        MAP_SCALE               = addAttribute(ftb,NamesExt.create(NAMESPACE, "SCALE"),            Double.class); //deprecated
        MAP_SCALEBAR            = SCALEBAR.getName();
                                  ftb.addAssociation(SCALEBAR).setName(SCALEBAR.getName());
        MAP_SHAPEPATH           = addAttribute(ftb,NamesExt.create(NAMESPACE, "SHAPEPATH"),        String.class);
        MAP_SIZE                = addAttribute(ftb,NamesExt.create(NAMESPACE, "SIZE"),             String.class);
        MAP_STATUS              = addAttribute(ftb,NamesExt.create(NAMESPACE, "STATUS"),           Boolean.class);
        MAP_SYMBOLSET           = addAttribute(ftb,NamesExt.create(NAMESPACE, "SYMBOLSET"),        String.class);
        MAP_SYMBOL              = SYMBOL.getName();
                                  ftb.addAssociation(SYMBOL).setName(SYMBOL.getName()).setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        MAP_TEMPLATEPATTERN     = addAttribute(ftb,NamesExt.create(NAMESPACE, "TEMPLATEPATTERN"),  String.class);
        MAP_TRANSPARENT         = addAttribute(ftb,NamesExt.create(NAMESPACE, "TRANSPARENT"),      Boolean.class); //deprecated
        MAP_UNITS               = addAttribute(ftb,NamesExt.create(NAMESPACE, "UNITS"),            String.class);
        MAP_WEB                 = WEB.getName();
                                  ftb.addAssociation(WEB).setName(WEB.getName());
        MAP = ftb.build();

    }

    private static GenericName addAttribute(FeatureTypeBuilder ftb,GenericName name, Class valueClass){
        ftb.addAttribute(valueClass).setName(name);
        return name;
    }

    private static final List<FeatureType> ALL_TYPES = UnmodifiableArrayList.wrap(new FeatureType[] {
            CLASS,CLUSTER,FEATURE,GRID,JOIN,LABEL,LAYER,LEGEND,MAP,
            OUTPUTFORMAT,QUERYMAP,REFERENCE,SCALEBAR,STYLE,SYMBOL,WEB});

    public static FeatureType getType(final GenericName name){
        for(FeatureType ft : ALL_TYPES){
            if(ft.getName().equals(name)){
                return ft;
            }
        }
        return null;
    }

    public static FeatureType getType(final String name){
        for(FeatureType ft : ALL_TYPES){
            if(ft.getName().tip().toString().equalsIgnoreCase(name)){
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
