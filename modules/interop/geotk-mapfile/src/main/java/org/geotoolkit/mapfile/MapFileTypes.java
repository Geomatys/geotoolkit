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

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 * Declares all mapfile types.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class MapFileTypes {
    
    public static final String NAMESPACE = "http://mapserver.org";
    
    public static final FeatureType CLASS;
    public static final FeatureType CLUSTER;
    public static final FeatureType FEATURE;
    public static final FeatureType GRID;
    public static final FeatureType JOIN;
    public static final FeatureType LABEL;
    public static final FeatureType LAYER;
    public static final FeatureType LEGEND;
    public static final FeatureType MAP;
    public static final FeatureType OUTPUTFORMAT;
    public static final FeatureType QUERYMAP;
    public static final FeatureType REFERENCE;
    public static final FeatureType SCALEBAR;
    public static final FeatureType STYLE;
    public static final FeatureType SYMBOL;
    public static final FeatureType WEB;
    
    static {
        final AttributeTypeBuilder atb          = new AttributeTypeBuilder();
        final AttributeDescriptorBuilder adb    = new AttributeDescriptorBuilder();
        final FeatureTypeBuilder ftb            = new FeatureTypeBuilder();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"STYLE");
        ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ANGLEITEM"),        String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GAP"),              Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GEOMTRANSFORM"),    String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LINECAP"),          String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LINEJOIN"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXWIDTH"),         Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINWIDTH"),         Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OFFSET"),           Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OPACITY"),          String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PATTERN"),          String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZEITEM"),         String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "SYMBOL"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "WIDTH"),            String.class,0,1,false,null);
        STYLE = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LABEL");
        ftb.add(new DefaultName(NAMESPACE, "ALIGN"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDSHADOWCOLOR"),Color.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDSHADOWSIZE"),Point2D.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "BUFFER"),           Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ENCODING"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FONT"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FORCE"),            Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXLENGTH"),        Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXOVERLAPANGLE"),  Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINDISTANCE"),      Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINFEATURESIZE"),   String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OFFSET"),           Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINEWIDTH"),     Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PARTIALS"),         Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PRIORITY"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "REPEATDISTANCE"),   Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SHADOWCOLOR"),      Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SHADOWSIZE"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             String.class,0,1,false,null);
        ftb.add(STYLE,STYLE.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "WRAP"),             String.class,0,1,false,null);
        LABEL = ftb.buildFeatureType();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"WEB");
        ftb.add(new DefaultName(NAMESPACE, "BROWSEFORMAT"),     String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EMPTY"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ERROR"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGEPATH"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGEURL"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LEGENDFORMAT"),     String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LOG"),              String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MAXTEMPLATE"),      String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "METADATA"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MINTEMPLATE"),      String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "QUERYFORMAT"),      String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);        
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        WEB = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"CLASS");
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EXPRESSION"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "KEYIMAGE"),         String.class,0,1,false,null);
        ftb.add(LABEL,LABEL.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MINSIZE"),          Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        ftb.add(STYLE,STYLE.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SYMBOL"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEXT"),             String.class,0,1,false,null);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        CLASS = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"CLUSTER");
        ftb.add(new DefaultName(NAMESPACE, "MAXDISTANCE"),      Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "REGION"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FILTER"),           String.class,0,1,false,null);
        CLUSTER = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"FEATURE");
        ftb.add(new DefaultName(NAMESPACE, "POINTS"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "ITEMS"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEXT"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "WKT"),              String.class,0,1,false,null);
        FEATURE = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"GRID");
        ftb.add(new DefaultName(NAMESPACE, "LABELFORMAT"),      String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINARCS"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXARCS"),          Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MININTERVAL"),      Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXINTERVAL"),      Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSUBDIVIDE"),     Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSUBDIVIDE"),     Double.class,0,1,false,null);
        GRID = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();        
        ftb.setName(NAMESPACE,"JOIN");
        ftb.add(new DefaultName(NAMESPACE, "CONNECTION"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CONNECTIONTYPE"),   String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FROM"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TABLE"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TO"),               String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        JOIN = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LAYER");
        ftb.add(CLASS,CLASS.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CLASSGROUP"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CLASSITEM"),        String.class,0,1,false,null);
        ftb.add(CLUSTER,CLUSTER.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CONNECTION"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CONNECTIONTYPE"),   String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DATA"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DUMP"),             Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        ftb.add(FEATURE,FEATURE.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FILTER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FILTERITEM"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FOOTER"),           String.class,0,1,false,null);
        ftb.add(GRID,GRID.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GROUP"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "HEADER"),           String.class,0,1,false,null);
        ftb.add(JOIN,JOIN.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELANGLEITEM"),   String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELCACHE"),       Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELITEM"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELMAXSCALEDENOM"),Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELMAXSCALE"),    Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "LABELMINSCALEDENOM"),Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELMINSCALE"),    Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "LABELREQUIRES"),    String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LABELSIZEITEM"),    String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "MAXFEATURES"),      Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXGEOWIDTH"),      Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "METADATA"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINGEOWIDTH"),      Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALEDENOM"),    Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINSCALE"),         Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OFFSITE"),          Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OPACITY"),          String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PLUGIN"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PROCESSING"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PROJECTION"),       String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "REQUIRES"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZEUNITS"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STYLEITEM"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SYMBOLSCALEDENOM"), Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SYMBOLSCALE"),      Double.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TILEINDEX"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TILEITEM"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TOLERANCE"),        Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TOLERANCEUNITS"),   String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENCY"),     String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "TRANSFORM"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        //should exist, yet the mapserver doesn't explain what it contain
        //ftb.add(new DefaultName(NAMESPACE, "VALIDATION"),       String.class,0,1,false,null);
        LAYER = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"LEGEND");
        ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "KEYSIZE"),          Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "KEYSPACING"),       Point2D.class,0,1,false,null);
        ftb.add(LABEL,LABEL.getName(),null,0,Integer.MAX_VALUE,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATE"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        LEGEND = ftb.buildFeatureType();
        
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"OUTPUTFORMAT");
        ftb.add(new DefaultName(NAMESPACE, "DRIVER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EXTENSION"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FORMATOPTION"),     String.class,0,Integer.MAX_VALUE,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGEMODE"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MIMETYPE"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,null);
        OUTPUTFORMAT = ftb.buildFeatureType();
                
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"QUERYMAP");
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STYLE"),            String.class,0,1,false,null);
        QUERYMAP = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"REFERENCE");
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGE"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MARKER"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MARKERSIZE"),       Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MINBOXSIZE"),       Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXBOXSIZE"),       Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        REFERENCE = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"SCALEBAR");
        ftb.add(new DefaultName(NAMESPACE, "ALIGN"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "BACKGROUNDCOLOR"),  Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "COLOR"),            Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "INTERVALS"),        Integer.class,0,1,false,null);
        ftb.add(LABEL,LABEL.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "OUTLINECOLOR"),     Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSITION"),         String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "POSTLABELCACHE"),   Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             Point2D.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STYLE"),            Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        SCALEBAR = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"SYMBOL");
        ftb.add(new DefaultName(NAMESPACE, "ANTIALIAS"),        Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CHARACTER"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FILLED"),           Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FONT"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "GAP"),              Integer.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "IMAGE"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "LINECAP"),          String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "LINEJOIN"),         String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "LINEJOINMAXSIZE"),  Integer.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "PATTERN"),          String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "POINTS"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STYLE"),            String.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TYPE"),             String.class,0,1,false,null);
        SYMBOL = ftb.buildFeatureType();
        
        //----------------------------------------------------------------------
        atb.reset(); adb.reset(); ftb.reset();
        ftb.setName(NAMESPACE,"MAP");
        ftb.add(new DefaultName(NAMESPACE, "ANGLE"),            Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "CONFIG"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DATAPATTERN"),      String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DEBUG"),            String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "DEFRESOLUTION"),    Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "EXTENT"),           String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "FONTSET"),          String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGECOLOR"),       Color.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "IMAGEQUALITY"),     Integer.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "IMAGETYPE"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "INTERLACE"),        Boolean.class,0,1,false,deprecated());
        ftb.add(LAYER,LAYER.getName(),null,0,Integer.MAX_VALUE,false,null);
        ftb.add(LEGEND,LEGEND.getName(),null,0,Integer.MAX_VALUE,false,null);
        ftb.add(new DefaultName(NAMESPACE, "MAXSIZE"),          Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "NAME"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "PROJECTION"),       String.class,0,1,false,null);
        ftb.add(QUERYMAP,QUERYMAP.getName(),null,0,1,false,null);
        ftb.add(REFERENCE,REFERENCE.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "RESOLUTION"),       Integer.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SCALEDENOM"),       Double.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SCALE"),            Double.class,0,1,false,deprecated());
        ftb.add(SCALEBAR,SCALEBAR.getName(),null,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SHAPEPATH"),        String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SIZE"),             String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "STATUS"),           Boolean.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "SYMBOLSET"),        String.class,0,1,false,null);
        ftb.add(SYMBOL,SYMBOL.getName(),null,0,Integer.MAX_VALUE,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TEMPLATEPATTERN"),  String.class,0,1,false,null);
        ftb.add(new DefaultName(NAMESPACE, "TRANSPARENT"),      Boolean.class,0,1,false,deprecated());
        ftb.add(new DefaultName(NAMESPACE, "UNITS"),            String.class,0,1,false,null);
        ftb.add(WEB,WEB.getName(),null,0,1,false,null);
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
            if(ft.getName().getLocalPart().equals(name)){
                return ft;
            }
        }
        return null;
    }
    
    private static Map<Object,Object> deprecated(){
        return Collections.singletonMap((Object)"Deprecated", (Object)Boolean.TRUE);
    }
    
    private MapFileTypes(){
    }
    
}
