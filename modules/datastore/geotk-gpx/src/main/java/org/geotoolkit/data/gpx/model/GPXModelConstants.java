/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.gpx.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.geotoolkit.feature.DefaultComplexAttribute;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.DefaultProperty;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.gpx.xml.GPXConstants.*;

/**
 * Global GPX constants, defines the namespace and Feature types.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GPXModelConstants {

    private static final GeometryFactory GF = new GeometryFactory();

    public static final CoordinateReferenceSystem GPX_CRS = DefaultGeographicCRS.WGS84;
    public static final String GPX_NAMESPACE = "http://www.topografix.com/GPX/1/1";
    public static final String GPX_GEOMETRY = "geometry";

    public static final FeatureType TYPE_GPX_ENTITY;
    public static final FeatureType TYPE_WAYPOINT;
    public static final FeatureType TYPE_TRACK;
    public static final FeatureType TYPE_ROUTE;
    public static final ComplexType TYPE_TRACK_SEGMENT;

    static final AttributeDescriptor DESC_WAYPOINT;
    static final AttributeDescriptor DESC_TRACK;
    static final AttributeDescriptor DESC_ROUTE;
    static final AttributeDescriptor DESC_TRACK_SEGMENT;

    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final FeatureTypeFactory ftf = ftb.getFeatureTypeFactory();

        //-------------------- GENERIC GPX ENTITY ------------------------------
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "GPXEntity");
        ftb.setAbstract(true);
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),      Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY), Point.class,GPX_CRS,1,1,false,null);
        TYPE_GPX_ENTITY = ftb.buildFeatureType();

        //------------------- WAY POINT TYPE -----------------------------------
        //lat="latitudeType [1] ?"
        //lon="longitudeType [1] ?">
        //<ele> xsd:decimal </ele> [0..1] ?
        //<time> xsd:dateTime </time> [0..1] ?
        //<magvar> degreesType </magvar> [0..1] ?
        //<geoidheight> xsd:decimal </geoidheight> [0..1] ?
        //<name> xsd:string </name> [0..1] ?
        //<cmt> xsd:string </cmt> [0..1] ?
        //<desc> xsd:string </desc> [0..1] ?
        //<src> xsd:string </src> [0..1] ?
        //<link> linkType </link> [0..*] ?
        //<sym> xsd:string </sym> [0..1] ?
        //<type> xsd:string </type> [0..1] ?
        //<fix> fixType </fix> [0..1] ?
        //<sat> xsd:nonNegativeInteger </sat> [0..1] ?
        //<hdop> xsd:decimal </hdop> [0..1] ?
        //<vdop> xsd:decimal </vdop> [0..1] ?
        //<pdop> xsd:decimal </pdop> [0..1] ?
        //<ageofdgpsdata> xsd:decimal </ageofdgpsdata> [0..1] ?
        //<dgpsid> dgpsStationType </dgpsid> [0..1] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "WayPoint");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     Point.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_ELE),    Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_TIME),   Date.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_MAGVAR),Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_GEOIHEIGHT),Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_NAME),       String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_CMT),        String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_DESC),       String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_SRC),        String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_LINK),       URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_SYM),   String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_TYPE),       String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_FIX),    String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_SAT),    Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_HDOP),   Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_VDOP),   Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_PDOP),   Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_AGEOFGPSDATA),Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_WPT_DGPSID), Integer.class,0,1,true,null);
        TYPE_WAYPOINT = ftb.buildFeatureType();


        //------------------- ROUTE TYPE ---------------------------------------
        //<name> xsd:string </name> [0..1] ?
        //<cmt> xsd:string </cmt> [0..1] ?
        //<desc> xsd:string </desc> [0..1] ?
        //<src> xsd:string </src> [0..1] ?
        //<link> linkType </link> [0..*] ?
        //<number> xsd:nonNegativeInteger </number> [0..1] ?
        //<type> xsd:string </type> [0..1] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        //<rtept> wptType </rtept> [0..*] ?
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "Route");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     LineString.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_NAME),      String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_CMT),       String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_DESC),      String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_SRC),       String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_LINK),      URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_NUMBER),    Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_TYPE),       String.class,0,1,true,null);
        ftb.add(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, TAG_RTE_RTEPT),null,0,Integer.MAX_VALUE,true,null);
        TYPE_ROUTE = ftb.buildFeatureType();


        //------------------- TRACK SEGMENT TYPE -------------------------------
        //<trkpt> wptType </trkpt> [0..*] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "TrackSegment");
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     LineString.class,GPX_CRS,1,1,false,null);
        ftb.add(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, TAG_TRK_SEG_PT),null,0,Integer.MAX_VALUE,true,null);
        TYPE_TRACK_SEGMENT = ftb.buildType();

        //------------------- TRACK TYPE ---------------------------------------
        //<name> xsd:string </name> [0..1] ?
        //<cmt> xsd:string </cmt> [0..1] ?
        //<desc> xsd:string </desc> [0..1] ?
        //<src> xsd:string </src> [0..1] ?
        //<link> linkType </link> [0..*] ?
        //<number> xsd:nonNegativeInteger </number> [0..1] ?
        //<type> xsd:string </type> [0..1] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        //<trkseg> trksegType </trkseg> [0..*] ?
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "Track");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     MultiLineString.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_NAME),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_CMT),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_DESC),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_SRC),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_LINK),         URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_NUMBER),       Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, TAG_TYPE),         String.class,0,1,true,null);
        ftb.add(TYPE_TRACK_SEGMENT,new DefaultName(GPX_NAMESPACE, TAG_TRK_SEG),null,0,Integer.MAX_VALUE,true,null);
        TYPE_TRACK = ftb.buildFeatureType();

        DESC_WAYPOINT = ftf.createAttributeDescriptor( TYPE_WAYPOINT, TYPE_WAYPOINT.getName(), 1, 1, true, null);
        DESC_TRACK = ftf.createAttributeDescriptor( TYPE_TRACK, TYPE_TRACK.getName(), 1, 1, true, null);
        DESC_ROUTE = ftf.createAttributeDescriptor( TYPE_ROUTE, TYPE_ROUTE.getName(), 1, 1, true, null);
        DESC_TRACK_SEGMENT = ftf.createAttributeDescriptor( TYPE_TRACK_SEGMENT, TYPE_TRACK_SEGMENT.getName(), 1, 1, true, null);
    }

    private GPXModelConstants(){}

    /**
     *
     * @param xmin : minimum longitude
     * @param xmax : maximum longitude
     * @param ymin : minimum latitude
     * @param ymax : maximum latitude
     * @return Immutable envelope in WGS84 with the given extents.
     */
    public static Envelope createEnvelope(double xmin, double xmax, double ymin, double ymax){
        return new ImmutableEnvelope(DefaultGeographicCRS.WGS84, xmin, xmax, ymin, ymax);
    }

    public static Feature createWayPoint(int index, Point geometry, Double ele, Date time,
            Double magvar, Double geoidheight, String name, String cmt, String desc,
            String src, List<URI> links, String sym, String type, String fix,
            Integer sat, Double hdop, Double vdop, Double pdop, Double ageofdgpsdata,
            Integer dgpsid) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_WAYPOINT.getDescriptor("index")));
        properties.add(new DefaultProperty(geometry,    TYPE_WAYPOINT.getDescriptor(GPX_GEOMETRY)));

        if(ele != null)         properties.add(new DefaultProperty(ele,         TYPE_WAYPOINT.getDescriptor(TAG_WPT_ELE)));
        if(time != null)        properties.add(new DefaultProperty(time,        TYPE_WAYPOINT.getDescriptor(TAG_WPT_TIME)));
        if(magvar != null)      properties.add(new DefaultProperty(magvar,      TYPE_WAYPOINT.getDescriptor(TAG_WPT_MAGVAR)));
        if(geoidheight != null) properties.add(new DefaultProperty(geoidheight, TYPE_WAYPOINT.getDescriptor(TAG_WPT_GEOIHEIGHT)));
        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_WAYPOINT.getDescriptor(TAG_NAME)));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_WAYPOINT.getDescriptor(TAG_CMT)));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_WAYPOINT.getDescriptor(TAG_DESC)));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_WAYPOINT.getDescriptor(TAG_SRC)));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_WAYPOINT.getDescriptor(TAG_LINK);
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(sym != null)         properties.add(new DefaultProperty(sym,         TYPE_WAYPOINT.getDescriptor(TAG_WPT_SYM)));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_WAYPOINT.getDescriptor(TAG_TYPE)));
        if(fix != null)         properties.add(new DefaultProperty(fix,         TYPE_WAYPOINT.getDescriptor(TAG_WPT_FIX)));
        if(sat != null)         properties.add(new DefaultProperty(sat,         TYPE_WAYPOINT.getDescriptor(TAG_WPT_SAT)));
        if(hdop != null)        properties.add(new DefaultProperty(hdop,        TYPE_WAYPOINT.getDescriptor(TAG_WPT_HDOP)));
        if(vdop != null)        properties.add(new DefaultProperty(vdop,        TYPE_WAYPOINT.getDescriptor(TAG_WPT_VDOP)));
        if(pdop != null)        properties.add(new DefaultProperty(pdop,        TYPE_WAYPOINT.getDescriptor(TAG_WPT_PDOP)));
        if(ageofdgpsdata!=null) properties.add(new DefaultProperty(ageofdgpsdata,TYPE_WAYPOINT.getDescriptor(TAG_WPT_AGEOFGPSDATA)));
        if(dgpsid != null)      properties.add(new DefaultProperty(dgpsid,      TYPE_WAYPOINT.getDescriptor(TAG_WPT_DGPSID)));

        return DefaultFeature.create(properties, TYPE_WAYPOINT, new DefaultFeatureId(String.valueOf(index)));
    }

    public static Feature createRoute(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_ROUTE.getDescriptor("index")));

        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_ROUTE.getDescriptor(TAG_NAME)));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_ROUTE.getDescriptor(TAG_CMT)));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_ROUTE.getDescriptor(TAG_DESC)));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_ROUTE.getDescriptor(TAG_SRC)));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_ROUTE.getDescriptor(TAG_LINK);
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(number != null)      properties.add(new DefaultProperty(number,      TYPE_ROUTE.getDescriptor(TAG_NUMBER)));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_ROUTE.getDescriptor(TAG_TYPE)));

        final List<Coordinate> coords = new ArrayList<Coordinate>();
        if(wayPoints != null && !wayPoints.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_ROUTE.getDescriptor(TAG_RTE_RTEPT);
            for(Feature pt : wayPoints){
                properties.add(new DefaultProperty(pt, ptDesc));
                coords.add( ((Point)pt.getProperty(GPX_GEOMETRY).getValue()).getCoordinate());
            }
        }
        final LineString geom = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
        properties.add(new DefaultProperty(geom, TYPE_ROUTE.getDescriptor(GPX_GEOMETRY)));

        return DefaultFeature.create(properties, TYPE_ROUTE, new DefaultFeatureId(String.valueOf(index)));
    }

    public static ComplexAttribute createTrackSegment(int index, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index, TYPE_TRACK_SEGMENT.getDescriptor("index")));

        final List<Coordinate> coords = new ArrayList<Coordinate>();
        if(wayPoints != null && !wayPoints.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_TRACK_SEGMENT.getDescriptor(TAG_TRK_SEG_PT);
            for(Feature pt : wayPoints){
                properties.add(new DefaultProperty(pt, ptDesc));
                coords.add( ((Point)pt.getProperty(GPX_GEOMETRY).getValue()).getCoordinate());
            }
        }
        final LineString geom = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
        properties.add(new DefaultProperty(geom, TYPE_TRACK_SEGMENT.getDescriptor(GPX_GEOMETRY)));

        return new DefaultComplexAttribute(properties, DESC_TRACK_SEGMENT, new DefaultFeatureId(String.valueOf(index)));
    }

    public static Feature createTrack(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<ComplexAttribute> segments) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_TRACK.getDescriptor("index")));

        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_TRACK.getDescriptor(TAG_NAME)));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_TRACK.getDescriptor(TAG_CMT)));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_TRACK.getDescriptor(TAG_DESC)));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_TRACK.getDescriptor(TAG_SRC)));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_TRACK.getDescriptor(TAG_LINK);
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(number != null)      properties.add(new DefaultProperty(number,      TYPE_TRACK.getDescriptor(TAG_NUMBER)));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_TRACK.getDescriptor(TAG_TYPE)));

        final List<LineString> strings = new ArrayList<LineString>();
        if(segments != null && !segments.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_TRACK.getDescriptor(TAG_TRK_SEG);
            for(ComplexAttribute seg : segments){
                properties.add(new DefaultProperty(seg, ptDesc));
                final Property prop = seg.getProperty(GPX_GEOMETRY);
                if(prop != null){
                    final Object obj = prop.getValue();
                    if(obj != null){
                        strings.add((LineString) obj);
                    }
                }
            }
        }
        final MultiLineString geom = GF.createMultiLineString(strings.toArray(new LineString[strings.size()]));
        properties.add(new DefaultProperty(geom, TYPE_TRACK.getDescriptor(GPX_GEOMETRY)));

        return DefaultFeature.create(properties, TYPE_TRACK, new DefaultFeatureId(String.valueOf(index)));
    }

}
