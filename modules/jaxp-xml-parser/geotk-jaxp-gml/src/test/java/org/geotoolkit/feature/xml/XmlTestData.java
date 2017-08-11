/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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
package org.geotoolkit.feature.xml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureCollection;
import static org.geotoolkit.data.AbstractFeatureStore.GML_32_NAMESPACE;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import static org.geotoolkit.feature.xml.GMLConvention.*;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class XmlTestData {

    public static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static final FeatureType simpleTypeBasic;
    public static final FeatureType simpleTypeFull;
    public static final FeatureType typeWithAtts;
    public static final FeatureType typeWithObject;
    public static final FeatureType typeEmpty;
    public static final FeatureType typeEmpty2;
    public static final FeatureType typeWithNil;
    public static final Feature simpleFeatureFull;
    public static final Feature simpleFeature1;
    public static final Feature simpleFeature2;
    public static final Feature simpleFeature3;
    public static FeatureCollection collectionSimple;
    public static final Feature featureComplex;
    public static final Feature featureWithAttributes;
    public static final Feature featureWithObject;
    public static final Feature featureEmpty;
    public static final Feature featureNil;

    public static FeatureType multiGeomType;

    public static FeatureType complexType;

    public static String EPSG_VERSION;

    static {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.geographic();
        ////////////////////////////////////////////////////////////////////////
        // TYPES ///////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////


        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        //NOTE : store the xsd simple type name using characteristic : Utils.SIMPLETYPE_NAME_CHARACTERISTIC

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Integer.class)           .setName(GML_311_NAMESPACE,"ID")               .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)            .setName(GML_311_NAMESPACE,"attString")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Short.class)             .setName(GML_311_NAMESPACE,"attShort")         .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Integer.class)           .setName(GML_311_NAMESPACE,"attInteger")       .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Long.class)              .setName(GML_311_NAMESPACE,"attLong")          .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Double.class)            .setName(GML_311_NAMESPACE,"attDouble")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(BigDecimal.class)        .setName(GML_311_NAMESPACE,"attDecimal")       .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Date.class)              .setName(GML_311_NAMESPACE,"attDate")          .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Timestamp.class)         .setName(GML_311_NAMESPACE,"attDateTime")      .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Boolean.class)           .setName(GML_311_NAMESPACE,"attBoolean")       .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Point.class)             .setName(GML_311_NAMESPACE,"geomPoint")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(MultiPoint.class)        .setName(GML_311_NAMESPACE,"geomMultiPoint")   .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(LineString.class)        .setName(GML_311_NAMESPACE,"geomLine")         .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(MultiLineString.class)   .setName(GML_311_NAMESPACE,"geomMultiLine")    .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Polygon.class)           .setName(GML_311_NAMESPACE,"geomPolygon")      .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(GeometryCollection.class).setName(GML_311_NAMESPACE,"geomMultiPolygon") .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(GeometryCollection.class).setName(GML_311_NAMESPACE,"geomMultiGeometry").setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Geometry.class)          .setName(GML_311_NAMESPACE,"geomAnyGeometry")  .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        simpleTypeFull = ftb.build();


        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestMultiGeom");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Integer.class)           .setName(GML_311_NAMESPACE,"ID")               .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)            .setName(GML_311_NAMESPACE,"attString")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Short.class)             .setName(GML_311_NAMESPACE,"attShort")         .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Integer.class)           .setName(GML_311_NAMESPACE,"attInteger")       .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Long.class)              .setName(GML_311_NAMESPACE,"attLong")          .setMinimumOccurs(0).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Double.class)            .setName(GML_311_NAMESPACE,"attDouble")        .setMinimumOccurs(0).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(BigDecimal.class)        .setName(GML_311_NAMESPACE,"attDecimal")       .setMinimumOccurs(0).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Date.class)              .setName(GML_311_NAMESPACE,"attDate")          .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Timestamp.class)         .setName(GML_311_NAMESPACE,"attDateTime")      .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Boolean.class)           .setName(GML_311_NAMESPACE,"attBoolean")       .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Point.class)             .setName(GML_311_NAMESPACE,"geomPoint")        .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(MultiPoint.class)        .setName(GML_311_NAMESPACE,"geomMultiPoint")   .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(LineString.class)        .setName(GML_311_NAMESPACE,"geomLine")         .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(MultiLineString.class)   .setName(GML_311_NAMESPACE,"geomMultiLine")    .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Polygon.class)           .setName(GML_311_NAMESPACE,"geomPolygon")      .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(GeometryCollection.class).setName(GML_311_NAMESPACE,"geomMultiPolygon") .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(GeometryCollection.class).setName(GML_311_NAMESPACE,"geomMultiGeometry").setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Geometry.class)          .setName(GML_311_NAMESPACE,"geomAnyGeometry")  .setMinimumOccurs(1).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        multiGeomType = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimpleBasic");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"attString")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Double.class)           .setName(GML_311_NAMESPACE,"attDouble")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        simpleTypeBasic = ftb.build();


        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"AddressType");
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"streetName")   .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"streetNumber") .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"city")         .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"province")     .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"postalCode")   .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"country")      .setMinimumOccurs(0).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        final FeatureType adress = ftb.build();


        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"Person");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Integer.class)          .setName(GML_311_NAMESPACE,"insuranceNumber")  .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"lastName")         .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"firstName")        .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Integer.class)          .setName(GML_311_NAMESPACE,"age")              .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)           .setName(GML_311_NAMESPACE,"sex")              .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Point .class)           .setName(GML_311_NAMESPACE,"position")         .setMinimumOccurs(0).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAssociation(adress).setName(NamesExt.create(GML_311_NAMESPACE, "mailAddress"))   .setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAttribute(String .class)          .setName(GML_311_NAMESPACE,"phone")            .setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        complexType = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_32);
        ftb.addAttribute(String.class)          .setName(GML_32_NAMESPACE,"@attString")  .setMinimumOccurs(0).setMaximumOccurs(1).setDefaultValue("hello");
        ftb.addAttribute(Integer.class)         .setName(GML_32_NAMESPACE,"@attInteger") .setMinimumOccurs(0).setMaximumOccurs(1).setDefaultValue(23);
        ftb.addAttribute(Integer.class)         .setName(GML_32_NAMESPACE,"ID")          .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(String.class)          .setName(GML_32_NAMESPACE,"eleString")   .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ftb.addAttribute(Integer.class)         .setName(GML_32_NAMESPACE,"eleInteger")  .setMinimumOccurs(1).setMaximumOccurs(1).addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        typeWithAtts = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(Object.class).setName(NamesExt.create(GML_32_NAMESPACE, "value"));
        typeWithObject = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_32);
        typeEmpty = ftb.build();

        AttributeTypeBuilder ib = new FeatureTypeBuilder().addAttribute(String.class);
        ib.setName(GML_32_NAMESPACE,"identifier");
        ib.setMinimumOccurs(0).setMaximumOccurs(1);
        ib.addCharacteristic(NILLABLE_CHARACTERISTIC).setDefaultValue(true);
        ib.addCharacteristic(String.class).setName(NamesExt.create(GML_32_NAMESPACE, "@codeBase"));
        final AttributeType identifierType = ib.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_31);
        ftb.addAttribute(String.class).setName(GML_311_NAMESPACE,"@id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(identifierType);
        typeEmpty2 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"SubRecordType");
        ftb.addAttribute(String.class).setName(NamesExt.create("@nilReason")).setMinimumOccurs(0).setMaximumOccurs(1);
        ftb.addAttribute(String.class).setName(GML_32_NAMESPACE,"attString")  .setMinimumOccurs(1).setMaximumOccurs(1);
        ftb.addAttribute(GMLConvention.NILLABLE_CHARACTERISTIC);
        final FeatureType subRecordType = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.setSuperTypes(ABSTRACTFEATURETYPE_32);
        ftb.addAssociation(subRecordType).setName(NamesExt.create(GML_32_NAMESPACE, "record")).setMinimumOccurs(0).setMaximumOccurs(1);
        typeWithNil = ftb.build();

        ////////////////////////////////////////////////////////////////////////
        // FEATURES ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        final GeometryFactory GF = new GeometryFactory();

        final Point pt = GF.createPoint(new Coordinate(5, 10));
        JTS.setCRS(pt, crs);
        final MultiPoint mpt = GF.createMultiPoint(new Coordinate[]{new Coordinate(5, 10), new Coordinate(15, 20)});
        JTS.setCRS(mpt, crs);
        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(30, 30)});
        JTS.setCRS(line1, crs);
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(11, 11), new Coordinate(21, 21), new Coordinate(31, 31)});
        JTS.setCRS(line2, crs);
        final MultiLineString mline = GF.createMultiLineString(new LineString[]{line1,line2});
        JTS.setCRS(mline, crs);
        final LinearRing ring1 = GF.createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 10),new Coordinate(0, 0)});
        JTS.setCRS(ring1, crs);
        final Polygon poly1 = GF.createPolygon(ring1, new LinearRing[0]);
        JTS.setCRS(poly1, crs);
        final LinearRing ring2 = GF.createLinearRing(new Coordinate[]{new Coordinate(1, 1), new Coordinate(11, 1), new Coordinate(11, 11),new Coordinate(1, 1)});
        JTS.setCRS(ring2, crs);
        final Polygon poly2 = GF.createPolygon(ring2, new LinearRing[0]);
        JTS.setCRS(poly2, crs);
        final MultiPolygon mpoly = GF.createMultiPolygon(new Polygon[]{poly1,poly2});
        JTS.setCRS(mpoly, crs);

        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        calendar1.set(Calendar.YEAR, 2005);
        calendar1.set(Calendar.MONTH, 7);
        calendar1.set(Calendar.DAY_OF_MONTH, 21);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        final Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        calendar2.set(Calendar.YEAR, 2005);
        calendar2.set(Calendar.MONTH, 7);
        calendar2.set(Calendar.DAY_OF_MONTH, 21);
        calendar2.set(Calendar.HOUR_OF_DAY, 15);
        calendar2.set(Calendar.MINUTE, 43);
        calendar2.set(Calendar.SECOND, 12);
        calendar2.set(Calendar.MILLISECOND, 52);

        simpleFeatureFull = simpleTypeFull.newInstance();
        simpleFeatureFull.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-156");
        simpleFeatureFull.setPropertyValue("ID", 36);
        simpleFeatureFull.setPropertyValue("attString", "stringValue");
        simpleFeatureFull.setPropertyValue("attShort", (short)12);
        simpleFeatureFull.setPropertyValue("attInteger", 24);
        simpleFeatureFull.setPropertyValue("attLong", 48l);
        simpleFeatureFull.setPropertyValue("attDouble", 96.12);
        simpleFeatureFull.setPropertyValue("attDecimal", new BigDecimal(456789));
        simpleFeatureFull.setPropertyValue("attDate", calendar1.getTime());
        simpleFeatureFull.setPropertyValue("attDateTime", new Timestamp(calendar2.getTimeInMillis()));
        simpleFeatureFull.setPropertyValue("attBoolean", Boolean.TRUE);
        simpleFeatureFull.setPropertyValue("geomPoint", pt);
        simpleFeatureFull.setPropertyValue("geomMultiPoint", mpt);
        simpleFeatureFull.setPropertyValue("geomLine", line1);
        simpleFeatureFull.setPropertyValue("geomMultiLine", mline);
        simpleFeatureFull.setPropertyValue("geomPolygon", poly1);
        simpleFeatureFull.setPropertyValue("geomMultiPolygon", mpoly);
        simpleFeatureFull.setPropertyValue("geomMultiGeometry", mpt);
        simpleFeatureFull.setPropertyValue("geomAnyGeometry", pt);

        simpleFeature1 = simpleTypeBasic.newInstance();
        simpleFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-89");
        simpleFeature1.setPropertyValue("attString","some text with words.");
        simpleFeature1.setPropertyValue("attDouble",56.14d);

        simpleFeature2 = simpleTypeBasic.newInstance();
        simpleFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-36");
        simpleFeature2.setPropertyValue("attString","some words assembled in a text.");
        simpleFeature2.setPropertyValue("attDouble",39.45d);

        simpleFeature3 = simpleTypeBasic.newInstance();
        simpleFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"id-412");
        simpleFeature3.setPropertyValue("attString","a text composed of words.");
        simpleFeature3.setPropertyValue("attDouble",12.31d);

        collectionSimple = FeatureStoreUtilities.collection("one of a kind ID", simpleTypeBasic);
        collectionSimple.add(simpleFeature1);
        collectionSimple.add(simpleFeature2);
        collectionSimple.add(simpleFeature3);
        try {
            collectionSimple = collectionSimple.subset(
                    QueryBuilder.sorted(collectionSimple.getType().getName().toString(), FF.sort("attDouble", SortOrder.ASCENDING)));
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }

        ((AbstractFeatureCollection)collectionSimple).setId("one of a kind ID");


        featureComplex = complexType.newInstance();
        featureComplex.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-0");
        featureComplex.setPropertyValue("insuranceNumber",new Integer(345678345));
        featureComplex.setPropertyValue("lastName","Smith");
        featureComplex.setPropertyValue("firstName","John");

        //final Property age = FeatureUtilities.defaultProperty(complexType.getDescriptor("age"));
        //age.setValue(new Integer(35));
        featureComplex.setPropertyValue("age",new Integer(35));
        featureComplex.setPropertyValue("sex","male");

        final Point pt2 = GF.createPoint(new Coordinate(10, 10));
        JTS.setCRS(pt2, crs);
        featureComplex.setPropertyValue("position", pt2);


        final Feature address = adress.newInstance();
        address.setPropertyValue("streetName","Main");
        address.setPropertyValue("streetNumber","10");
        address.setPropertyValue("city","SomeTown");
        address.setPropertyValue("province","Ontario");
        address.setPropertyValue("postalCode","M1R1K9");
        address.setPropertyValue("country","Canada");

        featureComplex.setPropertyValue("mailAddress", address);
        featureComplex.setPropertyValue("phone", Arrays.asList("4161234567","4168901234"));

        EPSG_VERSION = org.geotoolkit.referencing.CRS.getVersion("EPSG").toString();


        //feature with attributes
        featureWithAttributes = typeWithAtts.newInstance();
        featureWithAttributes.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-156");
        featureWithAttributes.setPropertyValue("ID", 36);
        featureWithAttributes.setPropertyValue("eleString", "stringValue");
        featureWithAttributes.setPropertyValue("eleInteger", 23);
        featureWithAttributes.setPropertyValue("@attString", "some text");
        featureWithAttributes.setPropertyValue("@attInteger", 456);

        //feature with object
        final FeatureTypeBuilder ctb = new FeatureTypeBuilder();
        ctb.setName("http://www.opengis.net/gml/3.2","quantityType");
        ctb.addAttribute(Double.class).setName(NamesExt.create("http://www.opengis.net/gml/3.2", "scale"));
        final FeatureType qtType = ctb.build();
        //final AttributeDescriptor adesc = adb.create(qtType, NamesExt.create("http://www.opengis.net/gml/3.2", "quantity"), 1, 1, true, null);

        final Feature propQuantity = qtType.newInstance();
        propQuantity.setPropertyValue("scale",3.14);
        featureWithObject = typeWithObject.newInstance();
        featureWithObject.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-156");
        featureWithObject.setPropertyValue("value", propQuantity);


        //feature with gml identifier property
        featureEmpty = typeEmpty2.newInstance();
        featureEmpty.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-156");
        final AttributeType charType = (AttributeType) ((AttributeType)typeEmpty2.getProperty("http://www.opengis.net/gml/3.2:identifier")).characteristics().get("http://www.opengis.net/gml/3.2:@codeBase");
        final Attribute charac = charType.newInstance();
        charac.setValue("something");
        featureEmpty.setPropertyValue("http://www.opengis.net/gml/3.2:identifier", "some text");
        ((Attribute)featureEmpty.getProperty("http://www.opengis.net/gml/3.2:identifier")).characteristics().put("http://www.opengis.net/gml/3.2:@codeBase",charac);

        //feature with a nil complex property
        featureNil = typeWithNil.newInstance();
        featureNil.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-156");
        final Feature propnil = subRecordType.newInstance();
        propnil.setPropertyValue("@nil", true);
        propnil.setPropertyValue("@nilReason", "unknown");
        featureNil.setPropertyValue("record",propnil);

    }

    private static Object addRole(AttributeRole attributeRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
