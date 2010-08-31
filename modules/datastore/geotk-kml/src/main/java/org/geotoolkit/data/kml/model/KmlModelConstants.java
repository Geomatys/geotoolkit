/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.net.URI;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Samuel Andrés
 */
public class KmlModelConstants {
//
//    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
//            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public static final CoordinateReferenceSystem KML_CRS = DefaultGeographicCRS.WGS84;
    public static final String KML_NAMESPACE = "http://www.opengis.net/kml";

    public static final FeatureType TYPE_KML_ENTITY;
    public static final AttributeDescriptor ATT_ID_ATTRIBUTES;
    public static final AttributeDescriptor ATT_NAME;
    public static final AttributeDescriptor ATT_VISIBILITY;
    public static final AttributeDescriptor ATT_OPEN;
    public static final AttributeDescriptor ATT_AUTHOR;
    public static final AttributeDescriptor ATT_LINK;
    public static final AttributeDescriptor ATT_ADDRESS;
    public static final AttributeDescriptor ATT_ADDRESS_DETAILS;
    public static final AttributeDescriptor ATT_PHONE_NUMBER;
    public static final AttributeDescriptor ATT_SNIPPET;
    public static final AttributeDescriptor ATT_DESCRIPTION;
    public static final AttributeDescriptor ATT_VIEW;
    public static final AttributeDescriptor ATT_TIME_PRIMITIVE;
    public static final AttributeDescriptor ATT_STYLE_URL;
    public static final AttributeDescriptor ATT_STYLE_SELECTOR;
    public static final AttributeDescriptor ATT_REGION;
    public static final AttributeDescriptor ATT_EXTENDED_DATA;
    public static final AttributeDescriptor ATT_EXTENSIONS;

    public static final FeatureType TYPE_PLACEMARK;
//    public static final GeometryDescriptor ATT_PLACEMARK_GEOMETRY;
    public static final AttributeDescriptor ATT_PLACEMARK_GEOMETRY;

    public static final FeatureType TYPE_NETWORK_LINK;
    public static final AttributeDescriptor ATT_NETWORK_LINK_REFRESH_VISIBILITY;
    public static final AttributeDescriptor ATT_NETWORK_LINK_FLY_TO_VIEW;
    public static final AttributeDescriptor ATT_NETWORK_LINK_LINK;

    public static final FeatureType TYPE_CONTAINER;

    public static final FeatureType TYPE_FOLDER;
    public static final AttributeDescriptor ATT_FOLDER_FEATURES;

    public static final FeatureType TYPE_DOCUMENT;
    public static final AttributeDescriptor ATT_DOCUMENT_FEATURES;
    public static final AttributeDescriptor ATT_DOCUMENT_SCHEMAS;

    public static final FeatureType TYPE_OVERLAY;
    public static final AttributeDescriptor ATT_OVERLAY_COLOR;
    public static final AttributeDescriptor ATT_OVERLAY_DRAW_ORDER;
    public static final AttributeDescriptor ATT_OVERLAY_ICON;

    public static final FeatureType TYPE_GROUND_OVERLAY;
    public static final AttributeDescriptor ATT_GROUND_OVERLAY_ALTITUDE;
    public static final AttributeDescriptor ATT_GROUND_OVERLAY_ALTITUDE_MODE;
    public static final AttributeDescriptor ATT_GROUND_OVERLAY_LAT_LON_BOX;

    public static final FeatureType TYPE_SCREEN_OVERLAY;
    public static final AttributeDescriptor ATT_SCREEN_OVERLAY_OVERLAYXY;
    public static final AttributeDescriptor ATT_SCREEN_OVERLAY_SCREENXY;
    public static final AttributeDescriptor ATT_SCREEN_OVERLAY_ROTATIONXY;
    public static final AttributeDescriptor ATT_SCREEN_OVERLAY_SIZE;
    public static final AttributeDescriptor ATT_SCREEN_OVERLAY_ROTATION;

    public static final FeatureType TYPE_PHOTO_OVERLAY;
    public static final AttributeDescriptor ATT_PHOTO_OVERLAY_ROTATION;
    public static final AttributeDescriptor ATT_PHOTO_OVERLAY_VIEW_VOLUME;
    public static final AttributeDescriptor ATT_PHOTO_OVERLAY_IMAGE_PYRAMID;
    public static final AttributeDescriptor ATT_PHOTO_OVERLAY_POINT;
    public static final AttributeDescriptor ATT_PHOTO_OVERLAY_SHAPE;


    static {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
//        final FeatureTypeFactory ftf = ftb.getFeatureTypeFactory();

        //-------------------- GENERIC KML ENTITY ------------------------------
        ATT_ID_ATTRIBUTES = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.ATT_ID), IdAttributes.class,0,1,false,null);
        ATT_NAME = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_NAME), String.class,0,1,false,null);
        ATT_VISIBILITY = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_VISIBILITY), Boolean.class,0,1,false,null);
        ATT_OPEN = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_OPEN), Boolean.class,0,1,false,null);
        ATT_AUTHOR = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ATOM_AUTHOR), AtomPersonConstruct.class,0,1,false,null);
        ATT_LINK = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ATOM_LINK), AtomLink.class,0,1,false,null);
        ATT_ADDRESS = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ADDRESS), String.class,0,1,false,null);
        ATT_ADDRESS_DETAILS = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_XAL_ADDRESS_DETAILS), AddressDetails.class,0,1,false,null);
        ATT_PHONE_NUMBER = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_PHONE_NUMBER), String.class,0,1,false,null);
        ATT_SNIPPET = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_SNIPPET), Object.class,0,1,false,null);
        ATT_DESCRIPTION = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_DESCRIPTION), Object.class,0,1,false,null);
        ATT_VIEW = adb.create(
                new DefaultName(KML_NAMESPACE, "View"), AbstractView.class,0,1,false,null);
        ATT_TIME_PRIMITIVE = adb.create(
                new DefaultName(KML_NAMESPACE, "TimePrimitive"), AbstractTimePrimitive.class,0,1,false,null);
        ATT_STYLE_URL = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_STYLE_URL), URI.class,0,1,false,null);
        ATT_STYLE_SELECTOR = adb.create(
                new DefaultName(KML_NAMESPACE, "StyleSelector"), AbstractStyleSelector.class,0,Integer.MAX_VALUE,false,null);
        ATT_REGION = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_REGION), Region.class,0,1,false,null);
        ATT_EXTENDED_DATA = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_EXTENDED_DATA), Object.class,0,Integer.MAX_VALUE,false,null);
        ATT_EXTENSIONS = adb.create(
                new DefaultName(KML_NAMESPACE, "Extensions"), Extensions.class,1,1,false,null);
        ftb.reset();
        ftb.setName(KML_NAMESPACE, "KMLEntity");
        ftb.setAbstract(true);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        TYPE_KML_ENTITY = ftb.buildFeatureType();

        //-------------------- PLACEMARK ------------------------------
//        ATT_PLACEMARK_GEOMETRY = (GeometryDescriptor) adb.create(
//                new DefaultName(KML_NAMESPACE, "geometry"), Geometry.class,KML_CRS,0,1,false,null);
        ATT_PLACEMARK_GEOMETRY = adb.create(
                new DefaultName(KML_NAMESPACE, "geometry"), AbstractGeometry.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, KmlConstants.TAG_PLACEMARK);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_PLACEMARK_GEOMETRY);
        ftb.setSuperType(TYPE_KML_ENTITY);
        TYPE_PLACEMARK = ftb.buildFeatureType();

        //-------------------- NETWORK_LINK ------------------------------
        ATT_NETWORK_LINK_REFRESH_VISIBILITY = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_REFRESH_VISIBILITY), Boolean.class,0,1,false,null);
        ATT_NETWORK_LINK_FLY_TO_VIEW = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_FLY_TO_VIEW), Boolean.class,0,1,false,null);
        ATT_NETWORK_LINK_LINK = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_LINK), Link.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, KmlConstants.TAG_DOCUMENT);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_NETWORK_LINK_REFRESH_VISIBILITY);
        ftb.add(ATT_NETWORK_LINK_FLY_TO_VIEW);
        ftb.add(ATT_NETWORK_LINK_LINK);
        ftb.setSuperType(TYPE_KML_ENTITY);
        TYPE_NETWORK_LINK = ftb.buildFeatureType();


        //-------------------- GENERIC CONTAINER ------------------------------
        ftb.reset();
        ftb.setName(KML_NAMESPACE, "Container");
        ftb.setAbstract(true);
        ftb.setSuperType(TYPE_KML_ENTITY);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        TYPE_CONTAINER = ftb.buildFeatureType();


        //-------------------- FOLDER ------------------------------
        ATT_FOLDER_FEATURES = adb.create(
                TYPE_KML_ENTITY, new DefaultName(KML_NAMESPACE, "features"),0,Integer.MAX_VALUE,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, KmlConstants.TAG_FOLDER);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_FOLDER_FEATURES);
        ftb.setSuperType(TYPE_CONTAINER);
        TYPE_FOLDER = ftb.buildFeatureType();

        //-------------------- DOCUMENT ------------------------------
        ATT_DOCUMENT_FEATURES = adb.create(
                TYPE_KML_ENTITY, new DefaultName(KML_NAMESPACE, "features"),0,Integer.MAX_VALUE,false,null);
        ATT_DOCUMENT_SCHEMAS = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_SCHEMA), Schema.class,0,Integer.MAX_VALUE,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, KmlConstants.TAG_DOCUMENT);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_DOCUMENT_FEATURES);
        ftb.add(ATT_DOCUMENT_SCHEMAS);
        ftb.setSuperType(TYPE_CONTAINER);
        TYPE_DOCUMENT = ftb.buildFeatureType();


        //-------------------- GENERIC OVERLAY ------------------------------
        ATT_OVERLAY_COLOR = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_COLOR), Color.class,0,1,false,null);
        ATT_OVERLAY_DRAW_ORDER = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_DRAW_ORDER), Integer.class,0,1,false,null);
        ATT_OVERLAY_ICON = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ICON), Link.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, "Overlay");
        ftb.setAbstract(true);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_OVERLAY_COLOR);
        ftb.add(ATT_OVERLAY_DRAW_ORDER);
        ftb.add(ATT_OVERLAY_ICON);
        ftb.setSuperType(TYPE_KML_ENTITY);
        TYPE_OVERLAY = ftb.buildFeatureType();

        //-------------------- GROUND OVERLAY ------------------------------
        ATT_GROUND_OVERLAY_ALTITUDE = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ALTITUDE), Double.class,0,1,false,null);
        ATT_GROUND_OVERLAY_ALTITUDE_MODE = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ALTITUDE_MODE), EnumAltitudeMode.class,0,1,false,null);
        ATT_GROUND_OVERLAY_LAT_LON_BOX = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_LAT_LON_BOX), LatLonBox.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, "Overlay");
        ftb.setAbstract(true);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_OVERLAY_COLOR);
        ftb.add(ATT_OVERLAY_DRAW_ORDER);
        ftb.add(ATT_OVERLAY_ICON);
        ftb.add(ATT_GROUND_OVERLAY_ALTITUDE);
        ftb.add(ATT_GROUND_OVERLAY_ALTITUDE_MODE);
        ftb.add(ATT_GROUND_OVERLAY_LAT_LON_BOX);
        ftb.setSuperType(TYPE_OVERLAY);
        TYPE_GROUND_OVERLAY = ftb.buildFeatureType();

        //-------------------- SCREEN OVERLAY ------------------------------
        ATT_SCREEN_OVERLAY_ROTATIONXY = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ROTATION_XY), Vec2.class,0,1,false,null);
        ATT_SCREEN_OVERLAY_OVERLAYXY = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_OVERLAY_XY), Vec2.class,0,1,false,null);
        ATT_SCREEN_OVERLAY_SCREENXY = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_SCREEN_XY), Vec2.class,0,1,false,null);
        ATT_SCREEN_OVERLAY_SIZE = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_SIZE), Vec2.class,0,1,false,null);
        ATT_SCREEN_OVERLAY_ROTATION = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ROTATION), Double.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, "Overlay");
        ftb.setAbstract(true);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_OVERLAY_COLOR);
        ftb.add(ATT_OVERLAY_DRAW_ORDER);
        ftb.add(ATT_OVERLAY_ICON);
        ftb.add(ATT_SCREEN_OVERLAY_ROTATIONXY);
        ftb.add(ATT_SCREEN_OVERLAY_OVERLAYXY);
        ftb.add(ATT_SCREEN_OVERLAY_SCREENXY);
        ftb.add(ATT_SCREEN_OVERLAY_SIZE);
        ftb.add(ATT_SCREEN_OVERLAY_ROTATION);
        ftb.setSuperType(TYPE_OVERLAY);
        TYPE_SCREEN_OVERLAY = ftb.buildFeatureType();

        //-------------------- PHOTO OVERLAY ------------------------------
        ATT_PHOTO_OVERLAY_ROTATION = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_ROTATION), Double.class,0,1,false,null);
        ATT_PHOTO_OVERLAY_VIEW_VOLUME = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_VIEW_VOLUME), ViewVolume.class,0,1,false,null);
        ATT_PHOTO_OVERLAY_IMAGE_PYRAMID = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_IMAGE_PYRAMID), ImagePyramid.class,0,1,false,null);
        ATT_PHOTO_OVERLAY_POINT = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_POINT), Point.class,0,1,false,null);
        ATT_PHOTO_OVERLAY_SHAPE = adb.create(
                new DefaultName(KML_NAMESPACE, KmlConstants.TAG_SHAPE), Shape.class,0,1,false,null);

        ftb.reset();
        ftb.setName(KML_NAMESPACE, "Overlay");
        ftb.setAbstract(true);
        ftb.add(ATT_ID_ATTRIBUTES);
        ftb.add(ATT_NAME);
        ftb.add(ATT_VISIBILITY);
        ftb.add(ATT_OPEN);
        ftb.add(ATT_AUTHOR);
        ftb.add(ATT_LINK);
        ftb.add(ATT_ADDRESS);
        ftb.add(ATT_ADDRESS_DETAILS);
        ftb.add(ATT_PHONE_NUMBER);
        ftb.add(ATT_SNIPPET);
        ftb.add(ATT_DESCRIPTION);
        ftb.add(ATT_VIEW);
        ftb.add(ATT_TIME_PRIMITIVE);
        ftb.add(ATT_STYLE_URL);
        ftb.add(ATT_STYLE_SELECTOR);
        ftb.add(ATT_REGION);
        ftb.add(ATT_EXTENDED_DATA);
        ftb.add(ATT_EXTENSIONS);
        ftb.add(ATT_OVERLAY_COLOR);
        ftb.add(ATT_OVERLAY_DRAW_ORDER);
        ftb.add(ATT_OVERLAY_ICON);
        ftb.add(ATT_PHOTO_OVERLAY_ROTATION);
        ftb.add(ATT_PHOTO_OVERLAY_VIEW_VOLUME);
        ftb.add(ATT_PHOTO_OVERLAY_IMAGE_PYRAMID);
        ftb.add(ATT_PHOTO_OVERLAY_POINT);
        ftb.add(ATT_PHOTO_OVERLAY_SHAPE);
        ftb.setSuperType(TYPE_OVERLAY);
        TYPE_PHOTO_OVERLAY = ftb.buildFeatureType();

    }

    private KmlModelConstants(){}

}
