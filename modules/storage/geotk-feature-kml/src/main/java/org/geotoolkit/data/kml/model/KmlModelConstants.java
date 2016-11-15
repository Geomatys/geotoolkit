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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.xal.model.AddressDetails;
import org.apache.sis.referencing.CommonCRS;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public final class KmlModelConstants {

    public static final CoordinateReferenceSystem KML_CRS = CommonCRS.WGS84.normalizedGeographic();
    public static final String KML_NAMESPACE = "http://www.opengis.net/kml";

    public static final FeatureType TYPE_KML_ENTITY;
    public static final AttributeType<IdAttributes> ATT_ID_ATTRIBUTES;
    public static final AttributeType<String> ATT_NAME;
    public static final AttributeType<Boolean> ATT_VISIBILITY;
    public static final AttributeType<Boolean> ATT_OPEN;
    public static final AttributeType<AtomPersonConstruct> ATT_AUTHOR;
    public static final AttributeType<AtomLink> ATT_LINK;
    public static final AttributeType<String> ATT_ADDRESS;
    public static final AttributeType<AddressDetails> ATT_ADDRESS_DETAILS;
    public static final AttributeType<String> ATT_PHONE_NUMBER;
    public static final AttributeType<Object> ATT_SNIPPET;
    public static final AttributeType<Object> ATT_DESCRIPTION;
    public static final AttributeType<AbstractView> ATT_VIEW;
    public static final AttributeType<AbstractTimePrimitive> ATT_TIME_PRIMITIVE;
    public static final AttributeType<URI> ATT_STYLE_URL;
    public static final AttributeType<AbstractStyleSelector> ATT_STYLE_SELECTOR;
    public static final AttributeType<Region> ATT_REGION;
    public static final AttributeType<Object> ATT_EXTENDED_DATA;
    public static final AttributeType<Extensions> ATT_EXTENSIONS;

    public static final FeatureType TYPE_PLACEMARK;
    public static final AttributeType<AbstractGeometry> ATT_PLACEMARK_GEOMETRY;

    public static final FeatureType TYPE_NETWORK_LINK;
    public static final AttributeType<Boolean> ATT_NETWORK_LINK_REFRESH_VISIBILITY;
    public static final AttributeType<Boolean> ATT_NETWORK_LINK_FLY_TO_VIEW;
    public static final AttributeType<Link> ATT_NETWORK_LINK_LINK;

    public static final FeatureType TYPE_CONTAINER;

    public static final FeatureType TYPE_FOLDER;
    public static final FeatureAssociationRole ATT_FOLDER_FEATURES;

    public static final FeatureType TYPE_DOCUMENT;
    public static final FeatureAssociationRole ATT_DOCUMENT_FEATURES;
    public static final AttributeType<Schema> ATT_DOCUMENT_SCHEMAS;

    public static final FeatureType TYPE_OVERLAY;
    public static final AttributeType<Color> ATT_OVERLAY_COLOR;
    public static final AttributeType<Integer> ATT_OVERLAY_DRAW_ORDER;
    public static final AttributeType<Link> ATT_OVERLAY_ICON;

    public static final FeatureType TYPE_GROUND_OVERLAY;
    public static final AttributeType<Double> ATT_GROUND_OVERLAY_ALTITUDE;
    public static final AttributeType<EnumAltitudeMode> ATT_GROUND_OVERLAY_ALTITUDE_MODE;
    public static final AttributeType<LatLonBox> ATT_GROUND_OVERLAY_LAT_LON_BOX;

    public static final FeatureType TYPE_SCREEN_OVERLAY;
    public static final AttributeType<Vec2> ATT_SCREEN_OVERLAY_OVERLAYXY;
    public static final AttributeType<Vec2> ATT_SCREEN_OVERLAY_SCREENXY;
    public static final AttributeType<Vec2> ATT_SCREEN_OVERLAY_ROTATIONXY;
    public static final AttributeType<Vec2> ATT_SCREEN_OVERLAY_SIZE;
    public static final AttributeType<Double> ATT_SCREEN_OVERLAY_ROTATION;

    public static final FeatureType TYPE_PHOTO_OVERLAY;
    public static final AttributeType<Double> ATT_PHOTO_OVERLAY_ROTATION;
    public static final AttributeType<ViewVolume> ATT_PHOTO_OVERLAY_VIEW_VOLUME;
    public static final AttributeType<ImagePyramid> ATT_PHOTO_OVERLAY_IMAGE_PYRAMID;
    public static final AttributeType<Point> ATT_PHOTO_OVERLAY_POINT;
    public static final AttributeType<Shape> ATT_PHOTO_OVERLAY_SHAPE;


    static {
        FeatureTypeBuilder ftb;

        //-------------------- GENERIC KML ENTITY ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_ID_ATTRIBUTES   = ftb.addAttribute(IdAttributes.class).setName(KmlConstants.ATT_ID).build();
        ATT_NAME            = ftb.addAttribute(String.class).setName(KmlConstants.TAG_NAME).build();
        ATT_VISIBILITY      = ftb.addAttribute(Boolean.class).setName(KmlConstants.TAG_VISIBILITY).setDefaultValue(KmlConstants.DEF_VISIBILITY).build();
        ATT_OPEN            = ftb.addAttribute(Boolean.class).setName(KmlConstants.TAG_OPEN).setDefaultValue(KmlConstants.DEF_OPEN).build();
        ATT_AUTHOR          = ftb.addAttribute(AtomPersonConstruct.class).setName(KmlConstants.TAG_ATOM_AUTHOR).build();
        ATT_LINK            = ftb.addAttribute(AtomLink.class).setName(KmlConstants.TAG_ATOM_LINK).build();
        ATT_ADDRESS         = ftb.addAttribute(String.class).setName(KmlConstants.TAG_ADDRESS).build();
        ATT_ADDRESS_DETAILS = ftb.addAttribute(AddressDetails.class).setName(KmlConstants.TAG_XAL_ADDRESS_DETAILS).build();
        ATT_PHONE_NUMBER    = ftb.addAttribute(String.class).setName(KmlConstants.TAG_PHONE_NUMBER).build();
        ATT_SNIPPET         = ftb.addAttribute(Object.class).setName(KmlConstants.TAG_SNIPPET).build();
        ATT_DESCRIPTION     = ftb.addAttribute(Object.class).setName(KmlConstants.TAG_DESCRIPTION).build();
        ATT_VIEW            = ftb.addAttribute(AbstractView.class).setName(KmlConstants.TAG_VIEW).build();
        ATT_TIME_PRIMITIVE  = ftb.addAttribute(AbstractTimePrimitive.class).setName(KmlConstants.TAG_TIME_PRIMITIVE).build();
        ATT_STYLE_URL       = ftb.addAttribute(URI.class).setName(KmlConstants.TAG_STYLE_URL).build();
        ATT_STYLE_SELECTOR  = ftb.addAttribute(AbstractStyleSelector.class).setName(KmlConstants.TAG_STYLE_SELECTOR).setMaximumOccurs(Integer.MAX_VALUE).build();
        ATT_REGION          = ftb.addAttribute(Region.class).setName(KmlConstants.TAG_REGION).build();
        ATT_EXTENDED_DATA   = ftb.addAttribute(Object.class).setName(KmlConstants.TAG_EXTENDED_DATA).setMaximumOccurs(Integer.MAX_VALUE).build();
        ATT_EXTENSIONS      = ftb.addAttribute(Extensions.class).setName(KmlConstants.TAG_EXTENSIONS).setMinimumOccurs(1).build();
        TYPE_KML_ENTITY     = ftb.setName("KMLEntity").setAbstract(true).build();

        //-------------------- PLACEMARK ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_PLACEMARK_GEOMETRY = ftb.addAttribute(AbstractGeometry.class).setName(KmlConstants.TAG_GEOMETRY).build();
        TYPE_PLACEMARK         = ftb.setSuperTypes(TYPE_KML_ENTITY).setName(KmlConstants.TAG_PLACEMARK).build();

        //-------------------- NETWORK_LINK ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_NETWORK_LINK_REFRESH_VISIBILITY = ftb.addAttribute(Boolean.class).setName(KmlConstants.TAG_REFRESH_VISIBILITY).setDefaultValue(KmlConstants.DEF_REFRESH_VISIBILITY).build();
        ATT_NETWORK_LINK_FLY_TO_VIEW        = ftb.addAttribute(Boolean.class).setName(KmlConstants.TAG_FLY_TO_VIEW).setDefaultValue(KmlConstants.DEF_FLY_TO_VIEW).build();
        ATT_NETWORK_LINK_LINK               = ftb.addAttribute(Link.class).setName(KmlConstants.TAG_LINK).build();
        TYPE_NETWORK_LINK                   = ftb.setSuperTypes(TYPE_KML_ENTITY).setName(KmlConstants.TAG_DOCUMENT).build();

        //-------------------- GENERIC CONTAINER ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        TYPE_CONTAINER = ftb.setSuperTypes(TYPE_KML_ENTITY).setName("Container").setAbstract(true).build();

        //-------------------- FOLDER ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, Integer.MAX_VALUE);
        ATT_FOLDER_FEATURES = ftb.addAssociation(TYPE_KML_ENTITY).setName(KmlConstants.TAG_FEATURES).build();
        TYPE_FOLDER         = ftb.setSuperTypes(TYPE_CONTAINER).setName(KmlConstants.TAG_FOLDER).build();

        //-------------------- DOCUMENT ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, Integer.MAX_VALUE);
        ATT_DOCUMENT_FEATURES = ftb.addAssociation(TYPE_KML_ENTITY).setName(KmlConstants.TAG_FEATURES).build();
        ATT_DOCUMENT_SCHEMAS  = ftb.addAttribute(Schema.class).setName(KmlConstants.TAG_SCHEMA).build();
        TYPE_DOCUMENT         = ftb.setSuperTypes(TYPE_CONTAINER).setName(KmlConstants.TAG_DOCUMENT).build();

        //-------------------- GENERIC OVERLAY ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_OVERLAY_COLOR      = ftb.addAttribute(Color.class).setName(KmlConstants.TAG_COLOR).setDefaultValue(KmlConstants.DEF_COLOR).build();
        ATT_OVERLAY_DRAW_ORDER = ftb.addAttribute(Integer.class).setName(KmlConstants.TAG_DRAW_ORDER).setDefaultValue(KmlConstants.DEF_DRAW_ORDER).build();
        ATT_OVERLAY_ICON       = ftb.addAttribute(Link.class).setName(KmlConstants.TAG_ICON).build();
        TYPE_OVERLAY           = ftb.setSuperTypes(TYPE_KML_ENTITY).setName("Overlay").setAbstract(true).build();

        //-------------------- GROUND OVERLAY ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_GROUND_OVERLAY_ALTITUDE      = ftb.addAttribute(Double.class).setName(KmlConstants.TAG_ALTITUDE).setDefaultValue(KmlConstants.DEF_ALTITUDE).build();
        ATT_GROUND_OVERLAY_ALTITUDE_MODE = ftb.addAttribute(EnumAltitudeMode.class).setName(KmlConstants.TAG_ALTITUDE_MODE).setDefaultValue(KmlConstants.DEF_ALTITUDE_MODE).build();
        ATT_GROUND_OVERLAY_LAT_LON_BOX   = ftb.addAttribute(LatLonBox.class).setName(KmlConstants.TAG_LAT_LON_BOX).build();
        TYPE_GROUND_OVERLAY              = ftb.setSuperTypes(TYPE_OVERLAY).setName("GroundOverlay").build();

        //-------------------- SCREEN OVERLAY ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_SCREEN_OVERLAY_ROTATIONXY = ftb.addAttribute(Vec2.class).setName(KmlConstants.TAG_ROTATION_XY).build();
        ATT_SCREEN_OVERLAY_OVERLAYXY  = ftb.addAttribute(Vec2.class).setName(KmlConstants.TAG_OVERLAY_XY).build();
        ATT_SCREEN_OVERLAY_SCREENXY   = ftb.addAttribute(Vec2.class).setName(KmlConstants.TAG_SCREEN_XY).build();
        ATT_SCREEN_OVERLAY_SIZE       = ftb.addAttribute(Vec2.class).setName(KmlConstants.TAG_SIZE).build();
        ATT_SCREEN_OVERLAY_ROTATION   = ftb.addAttribute(Double.class).setName(KmlConstants.TAG_ROTATION).setDefaultValue(KmlConstants.DEF_ROTATION).build();
        TYPE_SCREEN_OVERLAY           = ftb.setSuperTypes(TYPE_OVERLAY).setName("ScreenOverlay").build();

        //-------------------- PHOTO OVERLAY ------------------------------
        ftb = new FeatureTypeBuilder().setDefaultScope(KML_NAMESPACE).setDefaultCardinality(0, 1);
        ATT_PHOTO_OVERLAY_ROTATION = ftb.addAttribute(Double.class).setName(KmlConstants.TAG_ROTATION).setDefaultValue(KmlConstants.DEF_ROTATION).build();
        ATT_PHOTO_OVERLAY_VIEW_VOLUME = ftb.addAttribute(ViewVolume.class).setName(KmlConstants.TAG_VIEW_VOLUME).build();
        ATT_PHOTO_OVERLAY_IMAGE_PYRAMID = ftb.addAttribute(ImagePyramid.class).setName(KmlConstants.TAG_IMAGE_PYRAMID).build();
        ATT_PHOTO_OVERLAY_POINT = ftb.addAttribute(Point.class).setName(KmlConstants.TAG_POINT).build();
        ATT_PHOTO_OVERLAY_SHAPE = ftb.addAttribute(Shape.class).setName(KmlConstants.TAG_SHAPE).build();
        TYPE_PHOTO_OVERLAY = ftb.setSuperTypes(TYPE_OVERLAY).setName("PhotoOverlay").build();
    }

    private KmlModelConstants() {
    }
}
