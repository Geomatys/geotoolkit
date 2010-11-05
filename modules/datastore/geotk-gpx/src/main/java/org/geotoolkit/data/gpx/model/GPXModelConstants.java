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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.calculated.CalculatedLineStringAttribute;
import org.geotoolkit.feature.calculated.CalculatedMultiLineStringAttribute;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
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

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public static final CoordinateReferenceSystem GPX_CRS = DefaultGeographicCRS.WGS84;
    public static final String GPX_NAMESPACE = "http://www.topografix.com/GPX/1/1";
    public static final String GPX_GEOMETRY = "geometry";

    public static final FeatureType TYPE_GPX_ENTITY;
    private static final AttributeDescriptor ATT_INDEX;
    private static final GeometryDescriptor ATT_GEOMETRY;

    private static final AttributeDescriptor ATT_NAME;
    private static final AttributeDescriptor ATT_CMT;
    private static final AttributeDescriptor ATT_DESC;
    private static final AttributeDescriptor ATT_SRC;
    private static final AttributeDescriptor ATT_LINK;
    private static final AttributeDescriptor ATT_NUMBER;
    private static final AttributeDescriptor ATT_TYPE;

    public static final FeatureType TYPE_WAYPOINT;
    private static final GeometryDescriptor ATT_POINT_GEOMETRY;
    private static final AttributeDescriptor ATT_WPT_ELE;
    private static final AttributeDescriptor ATT_WPT_TIME;
    private static final AttributeDescriptor ATT_WPT_MAGVAR;
    private static final AttributeDescriptor ATT_WPT_GEOIHEIGHT;
    private static final AttributeDescriptor ATT_WPT_SYM;
    private static final AttributeDescriptor ATT_WPT_FIX;
    private static final AttributeDescriptor ATT_WPT_SAT;
    private static final AttributeDescriptor ATT_WPT_HDOP;
    private static final AttributeDescriptor ATT_WPT_VDOP;
    private static final AttributeDescriptor ATT_WPT_PDOP;
    private static final AttributeDescriptor ATT_WPT_AGEOFGPSDATA;
    private static final AttributeDescriptor ATT_WPT_DGPSID;


    public static final FeatureType TYPE_TRACK;
    private static final GeometryDescriptor ATT_TRACK_GEOMETRY;
    private static final AttributeDescriptor ATT_TRACKSEGMENTS;
    
    public static final FeatureType TYPE_ROUTE;
    private static final GeometryDescriptor ATT_ROUTE_GEOMETRY;
    private static final AttributeDescriptor ATT_WAYPOINTS;

    public static final ComplexType TYPE_TRACK_SEGMENT;
    private static final GeometryDescriptor ATT_TRACKSEG_GEOMETRY;
    private static final AttributeDescriptor ATT_TRACKPOINTS;


    static final AttributeDescriptor DESC_WAYPOINT;
    static final AttributeDescriptor DESC_TRACK;
    static final AttributeDescriptor DESC_ROUTE;
    static final AttributeDescriptor DESC_TRACK_SEGMENT;

    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final FeatureTypeFactory ftf = ftb.getFeatureTypeFactory();

        //-------------------- GENERIC GPX ENTITY ------------------------------
        ATT_INDEX = adb.create(new DefaultName(GPX_NAMESPACE, "index"),      Integer.class,1,1,false,null);
        ATT_GEOMETRY = (GeometryDescriptor) adb.create(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY), Geometry.class,GPX_CRS,1,1,false,null);

        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "GPXEntity");
        ftb.setAbstract(true);
        ftb.add(ATT_INDEX);
        ftb.add(ATT_GEOMETRY);
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
        ATT_POINT_GEOMETRY =    (GeometryDescriptor) adb.create(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     Point.class,GPX_CRS,1,1,false,null);
        ATT_WPT_ELE =           adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_ELE),    Double.class,0,1,true,null);
        ATT_WPT_TIME =          adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_TIME),   Date.class,0,1,true,null);
        ATT_WPT_MAGVAR =        adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_MAGVAR),Double.class,0,1,true,null);
        ATT_WPT_GEOIHEIGHT =    adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_GEOIHEIGHT),Double.class,0,1,true,null);
        ATT_NAME =              adb.create(new DefaultName(GPX_NAMESPACE, TAG_NAME),       String.class,0,1,true,null);
        ATT_CMT =               adb.create(new DefaultName(GPX_NAMESPACE, TAG_CMT),        String.class,0,1,true,null);
        ATT_DESC =              adb.create(new DefaultName(GPX_NAMESPACE, TAG_DESC),       String.class,0,1,true,null);
        ATT_SRC =               adb.create(new DefaultName(GPX_NAMESPACE, TAG_SRC),        String.class,0,1,true,null);
        ATT_LINK =              adb.create(new DefaultName(GPX_NAMESPACE, TAG_LINK),       URI.class,0,Integer.MAX_VALUE,true,null);
        ATT_WPT_SYM =           adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_SYM),   String.class,0,1,true,null);
        ATT_TYPE =              adb.create(new DefaultName(GPX_NAMESPACE, TAG_TYPE),       String.class,0,1,true,null);
        ATT_WPT_FIX =           adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_FIX),    String.class,0,1,true,null);
        ATT_WPT_SAT =           adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_SAT),    Integer.class,0,1,true,null);
        ATT_WPT_HDOP =          adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_HDOP),   Double.class,0,1,true,null);
        ATT_WPT_VDOP =          adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_VDOP),   Double.class,0,1,true,null);
        ATT_WPT_PDOP =          adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_PDOP),   Double.class,0,1,true,null);
        ATT_WPT_AGEOFGPSDATA =  adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_AGEOFGPSDATA),Double.class,0,1,true,null);
        ATT_WPT_DGPSID =        adb.create(new DefaultName(GPX_NAMESPACE, TAG_WPT_DGPSID), Integer.class,0,1,true,null);

        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "WayPoint");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(ATT_INDEX);
        ftb.add(ATT_POINT_GEOMETRY);
        ftb.add(ATT_WPT_ELE);
        ftb.add(ATT_WPT_TIME);
        ftb.add(ATT_WPT_MAGVAR);
        ftb.add(ATT_WPT_GEOIHEIGHT);
        ftb.add(ATT_NAME);
        ftb.add(ATT_CMT);
        ftb.add(ATT_DESC);
        ftb.add(ATT_SRC);
        ftb.add(ATT_LINK);
        ftb.add(ATT_WPT_SYM);
        ftb.add(ATT_TYPE);
        ftb.add(ATT_WPT_FIX);
        ftb.add(ATT_WPT_SAT);
        ftb.add(ATT_WPT_HDOP);
        ftb.add(ATT_WPT_VDOP);
        ftb.add(ATT_WPT_PDOP);
        ftb.add(ATT_WPT_AGEOFGPSDATA);
        ftb.add(ATT_WPT_DGPSID);
        ftb.setDefaultGeometry(ATT_POINT_GEOMETRY.getName());
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
        ATT_ROUTE_GEOMETRY  = (GeometryDescriptor) adb.create(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     LineString.class,GPX_CRS,1,1,false,null);
        ATT_NUMBER          = adb.create(new DefaultName(GPX_NAMESPACE, TAG_NUMBER),    Integer.class,0,1,true,null);
        ATT_WAYPOINTS       = adb.create(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, TAG_RTE_RTEPT),null,0,Integer.MAX_VALUE,true,null);

        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "Route");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(ATT_INDEX);
        ftb.add(ATT_ROUTE_GEOMETRY);
        ftb.add(ATT_NAME);
        ftb.add(ATT_CMT);
        ftb.add(ATT_DESC);
        ftb.add(ATT_SRC);
        ftb.add(ATT_LINK);
        ftb.add(ATT_NUMBER);
        ftb.add(ATT_TYPE);
        ftb.add(ATT_WAYPOINTS);
        ftb.setDefaultGeometry(ATT_ROUTE_GEOMETRY.getName());
        TYPE_ROUTE = ftb.buildFeatureType();


        //------------------- TRACK SEGMENT TYPE -------------------------------
        //<trkpt> wptType </trkpt> [0..*] ?
        //<extensions> extensionsType </extensions> [0..1] ?
        ATT_TRACKSEG_GEOMETRY  = (GeometryDescriptor) adb.create(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     LineString.class,GPX_CRS,1,1,false,null);
        ATT_TRACKPOINTS     = adb.create(TYPE_WAYPOINT,new DefaultName(GPX_NAMESPACE, TAG_TRK_SEG_PT),null,0,Integer.MAX_VALUE,true,null);

        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "TrackSegment");
        ftb.add(ATT_INDEX);
        ftb.add(ATT_TRACKSEG_GEOMETRY);
        ftb.add(ATT_TRACKPOINTS);
        ftb.setDefaultGeometry(ATT_TRACKSEG_GEOMETRY.getName());
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
        ATT_TRACK_GEOMETRY  = (GeometryDescriptor) adb.create(new DefaultName(GPX_NAMESPACE, GPX_GEOMETRY),     MultiLineString.class,GPX_CRS,1,1,false,null);
        ATT_TRACKSEGMENTS   = adb.create(TYPE_TRACK_SEGMENT,new DefaultName(GPX_NAMESPACE, TAG_TRK_SEG),null,0,Integer.MAX_VALUE,true,null);

        ftb.reset();
        ftb.setName(GPX_NAMESPACE, "Track");
        ftb.setSuperType(TYPE_GPX_ENTITY);
        ftb.add(ATT_INDEX);
        ftb.add(ATT_TRACK_GEOMETRY);
        ftb.add(ATT_NAME);
        ftb.add(ATT_CMT);
        ftb.add(ATT_DESC);
        ftb.add(ATT_SRC);
        ftb.add(ATT_LINK);
        ftb.add(ATT_NUMBER);
        ftb.add(ATT_TYPE);
        ftb.add(ATT_TRACKSEGMENTS);
        ftb.setDefaultGeometry(ATT_TRACK_GEOMETRY.getName());
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

        properties.add(FF.createAttribute(index, ATT_INDEX, null));
        properties.add(FF.createGeometryAttribute(geometry, ATT_POINT_GEOMETRY, null, null));

        if(ele != null)         properties.add(FF.createAttribute(ele,         ATT_WPT_ELE, null));
        if(time != null)        properties.add(FF.createAttribute(time,        ATT_WPT_TIME, null));
        if(magvar != null)      properties.add(FF.createAttribute(magvar,      ATT_WPT_MAGVAR, null));
        if(geoidheight != null) properties.add(FF.createAttribute(geoidheight, ATT_WPT_GEOIHEIGHT, null));
        if(name != null)        properties.add(FF.createAttribute(name,        ATT_NAME, null));
        if(cmt != null)         properties.add(FF.createAttribute(cmt,         ATT_CMT, null));
        if(desc != null)        properties.add(FF.createAttribute(desc,        ATT_DESC, null));
        if(src != null)         properties.add(FF.createAttribute(src,         ATT_SRC, null));

        if(links != null && !links.isEmpty()){
            for(URI uri : links){
                properties.add(FF.createAttribute(uri, ATT_LINK,null));
            }
        }

        if(sym != null)         properties.add(FF.createAttribute(sym,         ATT_WPT_SYM, null));
        if(type != null)        properties.add(FF.createAttribute(type,        ATT_TYPE, null));
        if(fix != null)         properties.add(FF.createAttribute(fix,         ATT_WPT_FIX, null));
        if(sat != null)         properties.add(FF.createAttribute(sat,         ATT_WPT_SAT, null));
        if(hdop != null)        properties.add(FF.createAttribute(hdop,        ATT_WPT_HDOP, null));
        if(vdop != null)        properties.add(FF.createAttribute(vdop,        ATT_WPT_VDOP, null));
        if(pdop != null)        properties.add(FF.createAttribute(pdop,        ATT_WPT_PDOP, null));
        if(ageofdgpsdata!=null) properties.add(FF.createAttribute(ageofdgpsdata,ATT_WPT_AGEOFGPSDATA, null));
        if(dgpsid != null)      properties.add(FF.createAttribute(dgpsid,      ATT_WPT_DGPSID, null));

        return FF.createFeature(properties, TYPE_WAYPOINT, Integer.toString(index));
    }

    public static Feature createRoute(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(index, ATT_INDEX, null));

        if(name != null)        properties.add(FF.createAttribute(name, ATT_NAME, null));
        if(cmt != null)         properties.add(FF.createAttribute(cmt, ATT_CMT, null));
        if(desc != null)        properties.add(FF.createAttribute(desc, ATT_DESC, null));
        if(src != null)         properties.add(FF.createAttribute(src, ATT_SRC, null));

        if(links != null && !links.isEmpty()){
            for(URI uri : links){
                properties.add(FF.createAttribute(uri, ATT_LINK,null));
            }
        }

        if(number != null)      properties.add(FF.createAttribute(number, ATT_NUMBER, null));
        if(type != null)        properties.add(FF.createAttribute(type, ATT_TYPE, null));

        if(wayPoints != null && !wayPoints.isEmpty()){
            for(Feature pt : wayPoints){
                properties.add(FF.createFeature(pt.getProperties(), ATT_WAYPOINTS,pt.getIdentifier().getID()));
            }
        }

        final CalculatedLineStringAttribute geomAtt = new CalculatedLineStringAttribute(
                ATT_ROUTE_GEOMETRY,
                ATT_WAYPOINTS.getName(), ATT_POINT_GEOMETRY.getName());
        properties.add(geomAtt);

        final Feature feature = FF.createFeature(properties, TYPE_ROUTE, Integer.toString(index));
        geomAtt.setRelated(feature);
        return feature;
    }

    public static ComplexAttribute createTrackSegment(int index, List<Feature> wayPoints) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(index, ATT_INDEX, null));

        if(wayPoints != null && !wayPoints.isEmpty()){
            for(Feature pt : wayPoints){
                properties.add(FF.createFeature(pt.getProperties(), ATT_TRACKPOINTS, pt.getIdentifier().getID()));
            }
        }

        final CalculatedLineStringAttribute geomAtt = new CalculatedLineStringAttribute(
                ATT_TRACKSEG_GEOMETRY,
                ATT_TRACKPOINTS.getName(), ATT_POINT_GEOMETRY.getName());
        properties.add(geomAtt);

        final ComplexAttribute ca = FF.createComplexAttribute(properties, DESC_TRACK_SEGMENT, Integer.toString(index));
        geomAtt.setRelated(ca);
        return ca;
    }

    public static Feature createTrack(int index, String name, String cmt, String desc,
            String src, List<URI> links, Integer number, String type, List<ComplexAttribute> segments) {

        final Collection<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(index, ATT_INDEX, null));

        if(name != null)        properties.add(FF.createAttribute(name, ATT_NAME, null));
        if(cmt != null)         properties.add(FF.createAttribute(cmt, ATT_CMT, null));
        if(desc != null)        properties.add(FF.createAttribute(desc, ATT_DESC, null));
        if(src != null)         properties.add(FF.createAttribute(src, ATT_SRC, null));

        if(links != null && !links.isEmpty()){
            for(URI uri : links){
                properties.add(FF.createAttribute(uri, ATT_LINK,null));
            }
        }

        if(number != null)      properties.add(FF.createAttribute(number, ATT_NUMBER, null));
        if(type != null)        properties.add(FF.createAttribute(type, ATT_TYPE, null));

        if(segments != null && !segments.isEmpty()){
            for(ComplexAttribute seg : segments){
                properties.add(FF.createComplexAttribute(seg.getProperties(), ATT_TRACKSEGMENTS, seg.getIdentifier().toString()));
            }
        }

        final CalculatedMultiLineStringAttribute geomAtt = new CalculatedMultiLineStringAttribute(
                ATT_TRACK_GEOMETRY,
                ATT_TRACKSEGMENTS.getName(), ATT_TRACKSEG_GEOMETRY.getName());
        properties.add(geomAtt);

        final Feature feature = FF.createFeature(properties, TYPE_TRACK, Integer.toString(index));
        geomAtt.setRelated(feature);
        return feature;
    }

}
