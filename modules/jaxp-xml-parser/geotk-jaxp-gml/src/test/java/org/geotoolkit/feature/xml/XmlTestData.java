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
import java.util.ArrayList;
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
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.FeatureType;
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

    public static SimpleFeatureType simpleTypeBasic;
    public static SimpleFeatureType simpleTypeFull;
    public static FeatureType typeWithAtts;
    public static FeatureType typeWithObject;
    public static FeatureType typeEmpty;
    public static FeatureType typeEmpty2;
    public static FeatureType typeWithNil;
    public static SimpleFeature simpleFeatureFull;
    public static SimpleFeature simpleFeature1;
    public static SimpleFeature simpleFeature2;
    public static SimpleFeature simpleFeature3;
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
        ftb.add(new DefaultName(GML_311_NAMESPACE,"ID"),                   Integer.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attString"),            String.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attShort"),             Short.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attInteger"),           Integer.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attLong"),              Long.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDouble"),            Double.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDecimal"),           BigDecimal.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDate"),              Date.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDateTime"),          Timestamp.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attBoolean"),           Boolean.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomPoint"),            Point.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiPoint"),       MultiPoint.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomLine"),             LineString.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiLine"),        MultiLineString.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomPolygon"),          Polygon.class, crs);
        //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class, crs);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomAnyGeometry"),      Geometry.class, crs);
        simpleTypeFull = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"TestMultiGeom");
        ftb.add(new DefaultName(GML_311_NAMESPACE,"ID"),                   Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attString"),            String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attShort"),             Short.class,             1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attInteger"),           Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attLong"),              Long.class,              0,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDouble"),            Double.class,            0,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDecimal"),           BigDecimal.class,        0,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDate"),              Date.class,              1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDateTime"),          Timestamp.class,         1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attBoolean"),           Boolean.class,           1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomPoint"),            Point.class,             crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiPoint"),       MultiPoint.class,        crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomLine"),             LineString.class,        crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiLine"),        MultiLineString.class,   crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomPolygon"),          Polygon.class,           crs,1,Integer.MAX_VALUE,true,null);
        //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class,crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class,crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"geomAnyGeometry"),      Geometry.class,          crs,1,Integer.MAX_VALUE,true,null);
        multiGeomType = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"TestSimpleBasic");
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attString"),            String.class);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"attDouble"),            Double.class);
        simpleTypeBasic = ftb.buildSimpleFeatureType();


        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"AddressType");
        ftb.add(new DefaultName(GML_311_NAMESPACE,"streetName"),           String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"streetNumber"),         String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"city"),                 String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"province"),             String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"postalCode"),           String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"country"),              String.class,            0,1,true,null);
        final ComplexType adress = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"AddressPropertyType");
        ftb.add(adress, new DefaultName(GML_311_NAMESPACE,"Address"),  null,                    1,1,false,null);
        final ComplexType mailadress = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_311_NAMESPACE,"Person");
        ftb.add(new DefaultName(GML_311_NAMESPACE,"insuranceNumber"),      Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"lastName"),             String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"firstName"),            String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"age"),                  Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"sex"),                  String.class,            1,1,true,null);
        //ftb.add(new DefaultName(GML_NAMESPACE,"spouse"),               Person.class,            0,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"position"),             Point.class,        crs, 0,1,true,null);
        ftb.add(mailadress, new DefaultName(GML_311_NAMESPACE,"mailAddress"),  null,                    0,1,true,null);
        ftb.add(new DefaultName(GML_311_NAMESPACE,"phone"),                String.class,            0,Integer.MAX_VALUE,true,null);

        complexType = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(new DefaultName(GML_32_NAMESPACE,"@attString"),           String.class,0,1,false,"hello",null);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"@attInteger"),          Integer.class,0,1,false,23,null);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"ID"),                   Integer.class);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"eleString"),            String.class);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"eleInteger"),           Integer.class);
        typeWithAtts = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(new DefaultName(GML_32_NAMESPACE,"value"),                Object.class);
        typeWithObject = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        typeEmpty = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"identifier");
        ftb.add(new DefaultName(GML_32_NAMESPACE,Utils.VALUE_PROPERTY_NAME), String.class,1,1,true,null);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"@codeBase"), String.class,1,1,true,null);
        final ComplexType identifierType = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(identifierType, new DefaultName(GML_32_NAMESPACE,"identifier"), null, 0, 1, true, null);
        typeEmpty2 = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"SubRecordType");
        ftb.add(new DefaultName(GML_32_NAMESPACE,"@nilReason"), String.class,0,1,false,null,null);
        ftb.add(new DefaultName(GML_32_NAMESPACE,"attString"), String.class,1,1,false,null,null);
        final ComplexType subRecordType = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_32_NAMESPACE,"TestSimple");
        ftb.add(subRecordType, new DefaultName(GML_32_NAMESPACE,"record"), null, 0, 1, true, null);
        typeWithNil = ftb.buildFeatureType();

        ////////////////////////////////////////////////////////////////////////
        // FEATURES ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        final GeometryFactory GF = new GeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(simpleTypeFull);

        final Point pt = GF.createPoint(new Coordinate(5, 10));
        final MultiPoint mpt = GF.createMultiPoint(new Coordinate[]{new Coordinate(5, 10), new Coordinate(15, 20)});
        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(30, 30)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(11, 11), new Coordinate(21, 21), new Coordinate(31, 31)});
        final MultiLineString mline = GF.createMultiLineString(new LineString[]{line1,line2});
        final LinearRing ring1 = GF.createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 10),new Coordinate(0, 0)});
        final Polygon poly1 = GF.createPolygon(ring1, new LinearRing[0]);
        final LinearRing ring2 = GF.createLinearRing(new Coordinate[]{new Coordinate(1, 1), new Coordinate(11, 1), new Coordinate(11, 11),new Coordinate(1, 1)});
        final Polygon poly2 = GF.createPolygon(ring2, new LinearRing[0]);
        final MultiPolygon mpoly = GF.createMultiPolygon(new Polygon[]{poly1,poly2});

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
        sfb.set(i++, 36);
        sfb.set(i++, "stringValue");
        sfb.set(i++, 12);
        sfb.set(i++, 24);
        sfb.set(i++, 48);
        sfb.set(i++, 96.12);
        sfb.set(i++, new BigDecimal(456789));
        sfb.set(i++, calendar1.getTime());
        sfb.set(i++, new Timestamp(calendar2.getTimeInMillis()));
        sfb.set(i++, Boolean.TRUE);
        sfb.set(i++, pt);
        sfb.set(i++, mpt);
        sfb.set(i++, line1);
        sfb.set(i++, mline);
        sfb.set(i++, poly1);
        sfb.set(i++, mpoly);
        sfb.set(i++, mpt);
        sfb.set(i++, pt);
        simpleFeatureFull = sfb.buildFeature("id-156");

        sfb = new SimpleFeatureBuilder(simpleTypeBasic);
        sfb.set(0,"some text with words.");
        sfb.set(1,56.14d);
        simpleFeature1 = sfb.buildFeature("id-89");
        sfb.set(0,"some words assembled in a text.");
        sfb.set(1,39.45d);
        simpleFeature2 = sfb.buildFeature("id-36");
        sfb.set(0,"a text composed of words.");
        sfb.set(1,12.31d);
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
        location.setValue(GF.createPoint(new Coordinate(10, 10)));
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
        ctb.add(new DefaultName("http://www.opengis.net/gml/3.2", "scale"), double.class);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeDescriptor adesc = adb.create(ctb.buildType(), new DefaultName("http://www.opengis.net/gml/3.2", "quantity"), 1, 1, true, null);

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
