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
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.ComplexAttribute;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "geometry"),     Point.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "ele"),          Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "time"),         Date.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "magvar"),       Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "geoidheight"),  Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "name"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "cmt"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "desc"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "src"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "link"),         URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "sym"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "type"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "fix"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "sat"),          Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "hdop"),         Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "vdop"),         Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "pdop"),         Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "ageofdgpsdata"),Double.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "dgpsid"),       Integer.class,0,1,true,null);
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
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "geometry"),     LineString.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "name"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "cmt"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "desc"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "src"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "link"),         URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "number"),       Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "type"),         String.class,0,1,true,null);
        ftb.add(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, "rtept"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_ROUTE = ftb.buildFeatureType();


        //------------------- TRACK SEGMENT TYPE -------------------------------
        //<trkpt> wptType </trkpt> [0..*] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "TrackSegment");
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "geometry"),     LineString.class,GPX_CRS,1,1,false,null);
        ftb.add(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, "trkpt"),null,0,Integer.MAX_VALUE,true,null);
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
        ftb.add(new DefaultName(GPX_NAMESPACE, "index"),        Integer.class,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "geometry"),     MultiLineString.class,GPX_CRS,1,1,false,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "name"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "cmt"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "desc"),         String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "src"),          String.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "link"),         URI.class,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "number"),       Integer.class,0,1,true,null);
        ftb.add(new DefaultName(GPX_NAMESPACE, "type"),         String.class,0,1,true,null);
        ftb.add(TYPE_TRACK_SEGMENT,new DefaultName(GPX_NAMESPACE, "trkseg"),null,0,Integer.MAX_VALUE,true,null);
        TYPE_TRACK = ftb.buildFeatureType();

        DESC_WAYPOINT = ftf.createAttributeDescriptor( TYPE_WAYPOINT, TYPE_WAYPOINT.getName(), 1, 1, true, null);
        DESC_TRACK = ftf.createAttributeDescriptor( TYPE_TRACK, TYPE_TRACK.getName(), 1, 1, true, null);
        DESC_ROUTE = ftf.createAttributeDescriptor( TYPE_ROUTE, TYPE_ROUTE.getName(), 1, 1, true, null);
        DESC_TRACK_SEGMENT = ftf.createAttributeDescriptor( TYPE_TRACK_SEGMENT, TYPE_TRACK_SEGMENT.getName(), 1, 1, true, null);
    }

    private GPXModelConstants(){}

    public static Feature createWayPoint(int index, Point geometry, Double ele, Date time,
            Double magvar, Double geoidheight, String name, String cmt, String desc,
            String src, List<URI> links, String sym, String type, String fix,
            Integer sat, Double hdop, Double vdop, Double pdop, Double ageofdgpsdata,
            Integer dgpsid) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_WAYPOINT.getDescriptor("index")));
        properties.add(new DefaultProperty(geometry,    TYPE_WAYPOINT.getDescriptor("geometry")));

        if(ele != null)         properties.add(new DefaultProperty(ele,         TYPE_WAYPOINT.getDescriptor("ele")));if(ele != null)
        if(time != null)        properties.add(new DefaultProperty(time,        TYPE_WAYPOINT.getDescriptor("time")));
        if(magvar != null)      properties.add(new DefaultProperty(magvar,      TYPE_WAYPOINT.getDescriptor("magvar")));
        if(geoidheight != null) properties.add(new DefaultProperty(geoidheight, TYPE_WAYPOINT.getDescriptor("geoidheight")));
        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_WAYPOINT.getDescriptor("name")));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_WAYPOINT.getDescriptor("cmt")));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_WAYPOINT.getDescriptor("desc")));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_WAYPOINT.getDescriptor("src")));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_WAYPOINT.getDescriptor("link");
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(sym != null)         properties.add(new DefaultProperty(sym,         TYPE_WAYPOINT.getDescriptor("sym")));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_WAYPOINT.getDescriptor("type")));
        if(fix != null)         properties.add(new DefaultProperty(fix,         TYPE_WAYPOINT.getDescriptor("fix")));
        if(sat != null)         properties.add(new DefaultProperty(sat,         TYPE_WAYPOINT.getDescriptor("sat")));
        if(hdop != null)        properties.add(new DefaultProperty(hdop,        TYPE_WAYPOINT.getDescriptor("hdop")));
        if(vdop != null)        properties.add(new DefaultProperty(vdop,        TYPE_WAYPOINT.getDescriptor("vdop")));
        if(pdop != null)        properties.add(new DefaultProperty(pdop,        TYPE_WAYPOINT.getDescriptor("pdop")));
        if(ageofdgpsdata!=null) properties.add(new DefaultProperty(ageofdgpsdata,TYPE_WAYPOINT.getDescriptor("ageofdgpsdata")));
        if(dgpsid != null)      properties.add(new DefaultProperty(dgpsid,      TYPE_WAYPOINT.getDescriptor("dgpsid")));

        return DefaultFeature.create(properties, TYPE_WAYPOINT, new DefaultFeatureId(String.valueOf(index)));
    }

    public static Feature createRoute(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_ROUTE.getDescriptor("index")));

        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_ROUTE.getDescriptor("name")));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_ROUTE.getDescriptor("cmt")));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_ROUTE.getDescriptor("desc")));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_ROUTE.getDescriptor("src")));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_ROUTE.getDescriptor("link");
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(number != null)      properties.add(new DefaultProperty(number,      TYPE_ROUTE.getDescriptor("number")));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_ROUTE.getDescriptor("type")));

        final List<Coordinate> coords = new ArrayList<Coordinate>();
        if(wayPoints != null && !wayPoints.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_ROUTE.getDescriptor("rtept");
            for(Feature pt : wayPoints){
                properties.add(new DefaultProperty(pt, ptDesc));
                coords.add( ((Point)pt.getProperty("geometry").getValue()).getCoordinate());
            }
        }
        final LineString geom = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
        properties.add(new DefaultProperty(geom, TYPE_ROUTE.getDescriptor("geometry")));

        return DefaultFeature.create(properties, TYPE_ROUTE, new DefaultFeatureId(String.valueOf(index)));
    }

    public static ComplexAttribute createTrackSegment(int index, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index, TYPE_TRACK_SEGMENT.getDescriptor("index")));

        final List<Coordinate> coords = new ArrayList<Coordinate>();
        if(wayPoints != null && !wayPoints.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_TRACK_SEGMENT.getDescriptor("trkpt");
            for(Feature pt : wayPoints){
                properties.add(new DefaultProperty(pt, ptDesc));
                coords.add( ((Point)pt.getProperty("geometry").getValue()).getCoordinate());
            }
        }
        final LineString geom = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
        properties.add(new DefaultProperty(geom, TYPE_TRACK_SEGMENT.getDescriptor("geometry")));

        return new DefaultComplexAttribute(properties, DESC_TRACK_SEGMENT, new DefaultFeatureId(String.valueOf(index)));
    }

    public static Feature createTrack(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<ComplexAttribute> segments) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(new DefaultProperty(index,       TYPE_TRACK.getDescriptor("index")));

        if(name != null)        properties.add(new DefaultProperty(name,        TYPE_TRACK.getDescriptor("name")));
        if(cmt != null)         properties.add(new DefaultProperty(cmt,         TYPE_TRACK.getDescriptor("cmt")));
        if(desc != null)        properties.add(new DefaultProperty(desc,        TYPE_TRACK.getDescriptor("desc")));
        if(src != null)         properties.add(new DefaultProperty(src,         TYPE_TRACK.getDescriptor("src")));

        if(links != null && !links.isEmpty()){
            final PropertyDescriptor linkDesc = TYPE_TRACK.getDescriptor("link");
            for(URI uri : links){
                properties.add(new DefaultProperty(uri, linkDesc));
            }
        }

        if(number != null)      properties.add(new DefaultProperty(number,      TYPE_TRACK.getDescriptor("number")));
        if(type != null)        properties.add(new DefaultProperty(type,        TYPE_TRACK.getDescriptor("type")));

        final List<LineString> strings = new ArrayList<LineString>();
        if(segments != null && !segments.isEmpty()){
            final PropertyDescriptor ptDesc = TYPE_TRACK.getDescriptor("trkseg");
            for(ComplexAttribute seg : segments){
                properties.add(new DefaultProperty(seg, ptDesc));
                final Property prop = seg.getProperty("geometry");
                if(prop != null){
                    final Object obj = prop.getValue();
                    if(obj != null){
                        strings.add((LineString) obj);
                    }
                }
            }
        }
        final MultiLineString geom = GF.createMultiLineString(strings.toArray(new LineString[strings.size()]));
        properties.add(new DefaultProperty(geom, TYPE_TRACK.getDescriptor("geometry")));

        return DefaultFeature.create(properties, TYPE_TRACK, new DefaultFeatureId(String.valueOf(index)));
    }

}
