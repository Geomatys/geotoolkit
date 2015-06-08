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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureCollection;
import static org.geotoolkit.data.AbstractFeatureStore.GML_311_NAMESPACE;
import static org.geotoolkit.data.AbstractFeatureStore.GML_32_NAMESPACE;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.Attribute;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class XmlTestData {

    public static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static FeatureType simpleTypeBasic;
    public static FeatureType simpleTypeFull;
    public static FeatureType typeWithAtts;
    public static FeatureType typeWithObject;
    public static FeatureType typeEmpty;
    public static FeatureType typeEmpty2;
    public static FeatureType typeWithNil;
    public static Feature simpleFeatureFull;
    public static Feature simpleFeature1;
    public static Feature simpleFeature2;
    public static Feature simpleFeature3;
    public static FeatureCollection collectionSimple;
    public static Feature featureComplex;
    public static Feature featureWithAttributes;
    public static Feature featureWithObject;
    public static Feature featureEmpty;
    public static Feature featureNil;

    public static FeatureType multiGeomType;

    public static FeatureType complexType;

    public static String EPSG_VERSION;

    static {

        final CoordinateReferenceSystem crs;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (FactoryException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        ////////////////////////////////////////////////////////////////////////
        // TYPES ///////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_311_NAMESPACE,"TestSimple");
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "ID"),         null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "attString"),  null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","short",                Short.class, NamesExt.create(GML_311_NAMESPACE, "attShort"),   null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "attInteger"), null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","long",                 Long.class, NamesExt.create(GML_311_NAMESPACE, "attLong"),    null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","double",               Double.class, NamesExt.create(GML_311_NAMESPACE, "attDouble"),  null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","decimal",              BigDecimal.class, NamesExt.create(GML_311_NAMESPACE, "attDecimal"), null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","date",                 Date.class, NamesExt.create(GML_311_NAMESPACE, "attDate"),    null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","dateTime",             Timestamp.class, NamesExt.create(GML_311_NAMESPACE, "attDateTime"),null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","boolean",              Boolean.class, NamesExt.create(GML_311_NAMESPACE, "attBoolean"), null, 1,1,true,null);
        ftb.add(GML_311_NAMESPACE, "Point",           Point.class, NamesExt.create(GML_311_NAMESPACE, "geomPoint"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "MultiPoint",      MultiPoint.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiPoint"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "Curve",           LineString.class, NamesExt.create(GML_311_NAMESPACE, "geomLine"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "CompositeCurve",  MultiLineString.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiLine"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "Polygon",         Polygon.class, NamesExt.create(GML_311_NAMESPACE, "geomPolygon"), null, 1, 1, true, null);
        //multipolygon does not exist in gml
        ftb.add(GML_311_NAMESPACE, "MultiGeometry",   GeometryCollection.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiPolygon"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "MultiGeometry",   GeometryCollection.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiGeometry"), null, 1, 1, true, null);
        ftb.add(GML_311_NAMESPACE, "GeometryPropertyType",Geometry.class, NamesExt.create(GML_311_NAMESPACE, "geomAnyGeometry"), null, 1, 1, true, null);
        simpleTypeFull = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"TestMultiGeom");
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "ID"),         null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "attString"),  null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","short",                Short.class, NamesExt.create(GML_311_NAMESPACE, "attShort"),   null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "attInteger"), null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","long",                 Long.class, NamesExt.create(GML_311_NAMESPACE, "attLong"),    null, 0,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","double",               Double.class, NamesExt.create(GML_311_NAMESPACE, "attDouble"),  null, 0,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","decimal",              BigDecimal.class, NamesExt.create(GML_311_NAMESPACE, "attDecimal"), null, 0,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","date",                 Date.class, NamesExt.create(GML_311_NAMESPACE, "attDate"),    null, 1,Integer.MAX_VALUE,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","dateTime",             Timestamp.class, NamesExt.create(GML_311_NAMESPACE, "attDateTime"),null, 1,Integer.MAX_VALUE,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","boolean",              Boolean.class, NamesExt.create(GML_311_NAMESPACE, "attBoolean"), null, 1,Integer.MAX_VALUE,true,null);
        ftb.add(GML_311_NAMESPACE, "Point",           Point.class, NamesExt.create(GML_311_NAMESPACE, "geomPoint"), null, 0, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "MultiPoint",      MultiPoint.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiPoint"), null, 0, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "Curve",           LineString.class, NamesExt.create(GML_311_NAMESPACE, "geomLine"), null, 0, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "CompositeCurve",  MultiLineString.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiLine"), null, 1, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "Polygon",         Polygon.class, NamesExt.create(GML_311_NAMESPACE, "geomPolygon"), null, 1, Integer.MAX_VALUE, true, null);
        //multipolygon does not exist in gml
        ftb.add(GML_311_NAMESPACE, "MultiGeometry",   GeometryCollection.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiPolygon"), null, 1, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "MultiGeometry",   GeometryCollection.class, NamesExt.create(GML_311_NAMESPACE, "geomMultiGeometry"), null, 1, Integer.MAX_VALUE, true, null);
        ftb.add(GML_311_NAMESPACE, "GeometryPropertyType",Geometry.class, NamesExt.create(GML_311_NAMESPACE, "geomAnyGeometry"), null, 1, Integer.MAX_VALUE, true, null);
        multiGeomType = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"TestSimpleBasic");
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "attString"),  null, 1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","double",               Double.class, NamesExt.create(GML_311_NAMESPACE, "attDouble"),  null, 1,1,true,null);
        simpleTypeBasic = ftb.buildSimpleFeatureType();


        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"AddressType");
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "streetName"),           null,            1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "streetNumber"),         null,            1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "city"),                 null,            1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "province"),             null,            1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "postalCode"),           null,            1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string", String.class, NamesExt.create(GML_311_NAMESPACE, "country"),              null,            0,1,true,null);
        final ComplexType adress = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"AddressPropertyType");
        ftb.add(adress, NamesExt.create(GML_311_NAMESPACE, "Address"),  null,                    1,1,false,null);
        final ComplexType mailadress = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"Person");
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "insuranceNumber"),      null,           1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "lastName"),             null,           1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "firstName"),            null,           1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_311_NAMESPACE, "age"),                  null,           1,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "sex"),                  null,           1,1,true,null);
        //ftb.add(new DefaultName(GML_NAMESPACE,"spouse"),               Person.class,            0,1,true,null);
        ftb.add(GML_311_NAMESPACE, "Point",                                Point.class, NamesExt.create(GML_311_NAMESPACE, "position"),             null,           0,1,true,null);
        ftb.add(mailadress, NamesExt.create(GML_311_NAMESPACE, "mailAddress"),  null,                    0,1,true,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_311_NAMESPACE, "phone"),                null,           0,Integer.MAX_VALUE,true,null);

        complexType = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(NamesExt.create(GML_32_NAMESPACE, "@attString"),           String.class,0,1,false,"hello",null);
        ftb.add(NamesExt.create(GML_32_NAMESPACE, "@attInteger"),          Integer.class,0,1,false,23,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_32_NAMESPACE, "ID"),  null, 1,1,true, null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_32_NAMESPACE, "eleString"),  null, 1,1,true, null);
        ftb.add("http://www.w3.org/2001/XMLSchema","integer",              Integer.class, NamesExt.create(GML_32_NAMESPACE, "eleInteger"),  null, 1,1,true, null);
        typeWithAtts = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(NamesExt.create(GML_32_NAMESPACE, "value"),                Object.class);
        typeWithObject = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        typeEmpty = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"identifier");
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_32_NAMESPACE, Utils.VALUE_PROPERTY_NAME),  null, 1,1,true, null);
        ftb.add(NamesExt.create(GML_32_NAMESPACE, "@codeBase"), String.class,1,1,true,null);
        final ComplexType identifierType = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(identifierType, NamesExt.create(GML_32_NAMESPACE, "identifier"), null, 0, 1, true, null);
        typeEmpty2 = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"SubRecordType");
        ftb.add(NamesExt.create(GML_32_NAMESPACE, "@nilReason"), String.class,0,1,false,null,null);
        ftb.add("http://www.w3.org/2001/XMLSchema","string",               String.class, NamesExt.create(GML_32_NAMESPACE, "attString"),  null, 1,1,false, null);
        final ComplexType subRecordType = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(subRecordType, NamesExt.create(GML_32_NAMESPACE, "record"), null, 0, 1, true, null);
        typeWithNil = ftb.buildFeatureType();

        ////////////////////////////////////////////////////////////////////////
        // FEATURES ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        final GeometryFactory GF = new GeometryFactory();
        FeatureBuilder sfb = new FeatureBuilder(simpleTypeFull);

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

        int i=0;
        sfb.reset();
        sfb.setPropertyValue(i++, 36);
        sfb.setPropertyValue(i++, "stringValue");
        sfb.setPropertyValue(i++, 12);
        sfb.setPropertyValue(i++, 24);
        sfb.setPropertyValue(i++, 48);
        sfb.setPropertyValue(i++, 96.12);
        sfb.setPropertyValue(i++, new BigDecimal(456789));
        sfb.setPropertyValue(i++, calendar1.getTime());
        sfb.setPropertyValue(i++, new Timestamp(calendar2.getTimeInMillis()));
        sfb.setPropertyValue(i++, Boolean.TRUE);
        sfb.setPropertyValue(i++, pt);
        sfb.setPropertyValue(i++, mpt);
        sfb.setPropertyValue(i++, line1);
        sfb.setPropertyValue(i++, mline);
        sfb.setPropertyValue(i++, poly1);
        sfb.setPropertyValue(i++, mpoly);
        sfb.setPropertyValue(i++, mpt);
        sfb.setPropertyValue(i++, pt);
        simpleFeatureFull = sfb.buildFeature("id-156");

        sfb = new FeatureBuilder(simpleTypeBasic);
        sfb.setPropertyValue(0,"some text with words.");
        sfb.setPropertyValue(1,56.14d);
        simpleFeature1 = sfb.buildFeature("id-89");
        sfb.setPropertyValue(0,"some words assembled in a text.");
        sfb.setPropertyValue(1,39.45d);
        simpleFeature2 = sfb.buildFeature("id-36");
        sfb.setPropertyValue(0,"a text composed of words.");
        sfb.setPropertyValue(1,12.31d);
        simpleFeature3 = sfb.buildFeature("id-412");

        collectionSimple = FeatureStoreUtilities.collection("one of a kind ID", simpleTypeBasic);
        collectionSimple.add(simpleFeature1);
        collectionSimple.add(simpleFeature2);
        collectionSimple.add(simpleFeature3);
        try {
            collectionSimple = collectionSimple.subCollection(
                    QueryBuilder.sorted(collectionSimple.getFeatureType().getName(), FF.sort("attDouble", SortOrder.ASCENDING)));
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }

        ((AbstractFeatureCollection)collectionSimple).setId("one of a kind ID");


        featureComplex = FeatureUtilities.defaultFeature(complexType, "id-0");
        featureComplex.getProperty("insuranceNumber").setValue(new Integer(345678345));
        featureComplex.getProperty("lastName").setValue("Smith");
        featureComplex.getProperty("firstName").setValue("John");

        //final Property age = FeatureUtilities.defaultProperty(complexType.getDescriptor("age"));
        //age.setValue(new Integer(35));
        featureComplex.getProperty("age").setValue(new Integer(35));
        featureComplex.getProperty("sex").setValue("male");

        final Property location = FeatureUtilities.defaultProperty(complexType.getDescriptor("position"));
        Point pt2 = GF.createPoint(new Coordinate(10, 10));
        JTS.setCRS(pt2, crs);
        location.setValue(pt2);
        featureComplex.getProperties().add(location);


        final ComplexAttribute address = (ComplexAttribute) FeatureUtilities.defaultProperty(
                mailadress.getDescriptor("Address"));
        address.getProperty("streetName").setValue("Main");
        address.getProperty("streetNumber").setValue("10");
        address.getProperty("city").setValue("SomeTown");
        address.getProperty("province").setValue("Ontario");
        address.getProperty("postalCode").setValue("M1R1K9");
        final Property country = FeatureUtilities.defaultProperty(adress.getDescriptor("country"));
        country.setValue("Canada");
        address.getProperties().add(country);


        final ComplexAttribute mailAddress = (ComplexAttribute) FeatureUtilities.defaultProperty(
                complexType.getDescriptor("mailAddress"));
        mailAddress.getProperties().clear();
        mailAddress.getProperties().add(address);

        featureComplex.getProperties().add(mailAddress);

        final Property phone = FeatureUtilities.defaultProperty(complexType.getDescriptor("phone"));
        phone.setValue(Arrays.asList("4161234567", "4168901234"));
        featureComplex.getProperties().add(phone);

        EPSG_VERSION = CRS.getVersion("EPSG").toString();
        
        
        //feature with attributes
        featureWithAttributes = FeatureUtilities.defaultFeature(typeWithAtts, "id-156");
        featureWithAttributes.setPropertyValue("ID", 36);
        featureWithAttributes.setPropertyValue("eleString", "stringValue");
        featureWithAttributes.setPropertyValue("eleInteger", 23);
        featureWithAttributes.setPropertyValue("@attString", "some text");
        featureWithAttributes.setPropertyValue("@attInteger", 456);

        //feature with object
        final FeatureTypeBuilder ctb = new FeatureTypeBuilder();
        ctb.setName("http://www.opengis.net/gml/3.2","quantityType");
        ctb.add(NamesExt.create("http://www.opengis.net/gml/3.2", "scale"), double.class);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeDescriptor adesc = adb.create(ctb.buildType(), NamesExt.create("http://www.opengis.net/gml/3.2", "quantity"), 1, 1, true, null);

        final ComplexAttribute propQuantity = (ComplexAttribute) FeatureUtilities.defaultProperty(adesc);
        propQuantity.getProperty("scale").setValue(3.14);
        featureWithObject = FeatureUtilities.defaultFeature(typeWithObject, "id-156");
        featureWithObject.setPropertyValue("value", propQuantity);


        //feature with gml identifier property
        featureEmpty = FeatureUtilities.defaultFeature(typeEmpty2, "id-156");
        final ComplexAttribute prop = (ComplexAttribute) FeatureUtilities.defaultProperty(typeEmpty2.getDescriptor("identifier"));
        prop.getProperty(Utils.VALUE_PROPERTY_NAME).setValue("some text");
        prop.getProperty("@codeBase").setValue("something");
        featureEmpty.getProperties().add(prop);

        //feature with a nil complex property
        featureNil = FeatureUtilities.defaultFeature(typeWithNil, "id-156");
        final ComplexAttribute propnil = (ComplexAttribute) FeatureUtilities.defaultProperty(typeWithNil.getDescriptor("record"));
        final Attribute att = (Attribute) FeatureUtilities.defaultProperty(propnil.getType().getDescriptor("@nilReason"));
        att.setValue("unknown");
        propnil.getProperties().add(att);
        featureNil.getProperties().add(propnil);


    }

}
