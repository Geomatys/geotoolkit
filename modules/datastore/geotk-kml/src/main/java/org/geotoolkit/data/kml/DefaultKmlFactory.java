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
package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.DefaultAlias;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.DefaultBalloonStyle;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.DefaultBasicLink;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.DefaultBoundary;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.DefaultCamera;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.DefaultChange;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.DefaultCoordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.DefaultCreate;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.DefaultData;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DefaultDelete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.DefaultExtendedData;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.DefaultIcon;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.DefaultIconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.DefaultIdAttributes;
import org.geotoolkit.data.kml.model.ImagePyramid;
import org.geotoolkit.data.kml.model.DefaultImagePyramid;
import org.geotoolkit.data.kml.model.ItemIcon;
import org.geotoolkit.data.kml.model.DefaultItemIcon;
import org.geotoolkit.data.kml.model.ItemIconState;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.DefaultKml;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.DefaultLabelStyle;
import org.geotoolkit.data.kml.model.LatLonAltBox;
import org.geotoolkit.data.kml.model.DefaultLatLonAltBox;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.DefaultLatLonBox;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.DefaultLineString;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.DefaultLineStyle;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.DefaultLinearRing;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.DefaultLink;
import org.geotoolkit.data.kml.model.ListItem;
import org.geotoolkit.data.kml.model.ListStyle;
import org.geotoolkit.data.kml.model.DefaultListStyle;
import org.geotoolkit.data.kml.model.Location;
import org.geotoolkit.data.kml.model.DefaultLocation;
import org.geotoolkit.data.kml.model.Lod;
import org.geotoolkit.data.kml.model.DefaultLod;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.DefaultLookAt;
import org.geotoolkit.data.kml.model.DefaultMetaData;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.DefaultModel;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.DefaultMultiGeometry;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.DefaultNetworkLinkControl;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.DefaultOrientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.DefaultPair;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.DefaultPoint;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.DefaultPolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.DefaultPolygon;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.DefaultRegion;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.DefaultResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.model.DefaultScale;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.DefaultSchemaData;
import org.geotoolkit.data.kml.model.DefaultSchema;
import org.geotoolkit.data.kml.model.Shape;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.model.DefaultSimpleData;
import org.geotoolkit.data.kml.model.SimpleField;
import org.geotoolkit.data.kml.model.DefaultSimpleField;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.model.DefaultSnippet;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.DefaultStyle;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.DefaultStyleMap;
import org.geotoolkit.data.kml.model.StyleState;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.DefaultTimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.model.DefaultTimeStamp;
import org.geotoolkit.data.kml.model.Units;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.DefaultUpdate;
import org.geotoolkit.data.kml.model.DefaultUrl;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.DefaultVec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.kml.model.DefaultViewVolume;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
import org.geotoolkit.data.kml.xsd.DefaultSimpleTypeContainer;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultKmlFactory implements KmlFactory{

    private static final KmlFactory KMLF = new DefaultKmlFactory();
    private static final GeometryFactory GF = new GeometryFactory();
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    private DefaultKmlFactory(){}

    public static KmlFactory getInstance(){
        return KMLF;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Kml createKml(NetworkLinkControl networkLinkControl,
            Feature abstractFeature,
            List<SimpleTypeContainer> kmlSimpleExtensions,
            List<Object> kmlObjectExtensions) {
        return new DefaultKml(networkLinkControl, abstractFeature,
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Kml createKml(){
        return new DefaultKml();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Alias createAlias(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleTypeContainer> aliasSimpleExtensions, List<Object> aliasObjectExtensions) {
        return new DefaultAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, aliasSimpleExtensions, aliasObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Alias createAlias(){
        return new DefaultAlias();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle createBalloonStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions, List<Object> subStyleObjectExtensions,
            Color bgColor, Color textColor, Object text, DisplayMode displayMode,
            List<SimpleTypeContainer> balloonStyleSimpleExtensions, List<Object> balloonStyleObjectExtensions) {
        return new DefaultBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle createBalloonStyle(){
        return new DefaultBalloonStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions) {
        return new DefaultBasicLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink() {
        return new DefaultBasicLink();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleTypeContainer> boundarySimpleExtensions,
            List<AbstractObject> boundaryObjectExtensions) {
        return new DefaultBoundary(linearRing, boundarySimpleExtensions,
                boundaryObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary() {
        return new DefaultBoundary();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractViewSimpleExtensions,
            List<Object> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> cameraSimpleExtensions,
            List<Object> cameraObjectExtensions) {
        return new DefaultCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera() {
        return new DefaultCamera();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(List<Object> objects) {
        return new DefaultChange(objects);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(){
        return new DefaultChange();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return KmlUtilities.toCoordinate(listCoordinates);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(
            double geodeticLongiude, double geodeticLatitude, double altitude) {
        return new Coordinate(geodeticLongiude, geodeticLatitude, altitude);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(
            double geodeticLongiude, double geodeticLatitude) {
        return new Coordinate(geodeticLongiude, geodeticLatitude);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinates createCoordinates(List<Coordinate> coordinates) {
        return new DefaultCoordinates(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate(List<Feature> containers) {
        return new DefaultCreate(containers);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate() {
        return new DefaultCreate();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Data createData(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, Object displayName, String value, List<Object> dataExtensions) {
        return new DefaultData(objectSimpleExtensions, idAttributes,
                name, displayName, value, dataExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Data createData(){
        return new DefaultData();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete(List<Feature> features) {
        return new DefaultDelete(features);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete() {
        return new DefaultDelete();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createDocument(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            List<SimpleTypeContainer> abstractContainerSimpleExtensions,
            List<Object> abstractContainerObjectExtensions,
            List<Schema> schemas, List<Feature> features,
            List<SimpleTypeContainer> documentSimpleExtensions,
            List<Object> documentObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (abstractContainerSimpleExtensions != null) {
            extensions.simples(Extensions.Names.CONTAINER).addAll(abstractContainerSimpleExtensions);
        }
        if (abstractContainerObjectExtensions != null) {
            extensions.complexes(Extensions.Names.CONTAINER).addAll(abstractContainerObjectExtensions);
        }

        if (documentSimpleExtensions != null) {
            extensions.simples(Extensions.Names.DOCUMENT).addAll(documentSimpleExtensions);
        }
        if (documentObjectExtensions != null) {
            extensions.complexes(Extensions.Names.DOCUMENT).addAll(documentObjectExtensions);
        }

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME,null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector) {
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        for (Schema schema : schemas) {
            properties.add(FF.createAttribute(schema, KmlModelConstants.ATT_DOCUMENT_SCHEMAS, null));
        }
        for (Feature feature : features){
            properties.add(FF.createAttribute(feature, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        }
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_DOCUMENT,"document");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createDocument() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_DOCUMENT,"document");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData createExtendedData(List<Data> datas, 
            List<SchemaData> schemaDatas, List<Object> anyOtherElements) {
        return new DefaultExtendedData(datas, schemaDatas, anyOtherElements);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData createExtendedData(){
        return new DefaultExtendedData();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createFolder(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            List<SimpleTypeContainer> abstractContainerSimpleExtensions,
            List<Object> abstractContainerObjectExtensions,
            List<Feature> features,
            List<SimpleTypeContainer> folderSimpleExtensions,
            List<Object> folderObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (abstractContainerSimpleExtensions != null) {
            extensions.simples(Extensions.Names.CONTAINER).addAll(abstractContainerSimpleExtensions);
        }
        if (abstractContainerObjectExtensions != null) {
            extensions.complexes(Extensions.Names.CONTAINER).addAll(abstractContainerObjectExtensions);
        }

        if (folderSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FOLDER).addAll(folderSimpleExtensions);
        }
        if (folderObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FOLDER).addAll(folderObjectExtensions);
        }

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        for (Feature feature : features){
            properties.add(FF.createAttribute(feature, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        }
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_FOLDER, "Folder");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createFolder() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_FOLDER, "Folder");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createGroundOverlay(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleTypeContainer> abstractOveraySimpleExtensions,
            List<Object> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleTypeContainer> groundOverlaySimpleExtensions,
            List<Object> groundOverlayObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (abstractOveraySimpleExtensions != null) {
            extensions.simples(Extensions.Names.OVERLAY).addAll(abstractOveraySimpleExtensions);
        }
        if (abstractOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.OVERLAY).addAll(abstractOverlayObjectExtensions);
        }

        if (groundOverlaySimpleExtensions != null) {
            extensions.simples(Extensions.Names.GROUND_OVERLAY).addAll(groundOverlaySimpleExtensions);
        }
        if (groundOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.GROUND_OVERLAY).addAll(groundOverlayObjectExtensions);
        }

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        properties.add(FF.createAttribute(color, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(drawOrder, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        properties.add(FF.createAttribute(altitude, KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE, null));
        properties.add(FF.createAttribute(altitudeMode, KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE_MODE, null));
        properties.add(FF.createAttribute(latLonBox, KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX, null));
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_GROUND_OVERLAY, "GroundOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createGroundOverlay() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_COLOR, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_DRAW_ORDER, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_ALTITUDE, KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_ALTITUDE_MODE, KmlModelConstants.ATT_GROUND_OVERLAY_ALTITUDE_MODE, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_GROUND_OVERLAY, "GroundOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Icon createIcon(Link link) {
        return new DefaultIcon(link);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    @Deprecated
    public Url createUrl(Link link) {
        return new DefaultUrl(link);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IconStyle createIconStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleTypeContainer> iconStyleSimpleExtensions,
            List<Object> iconStyleObjectExtensions) {
        return new DefaultIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IconStyle createIconStyle() {
        return new DefaultIconStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes(String id, String targetId) {
        return new DefaultIdAttributes(id, targetId);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes() {
        return new DefaultIdAttributes();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleTypeContainer> imagePyramidSimpleExtensions, 
            List<Object> imagePyramidObjectExtensions) {
        return new DefaultImagePyramid(objectSimpleExtensions, idAttributes,
                titleSize, maxWidth, maxHeight, gridOrigin,
                imagePyramidSimpleExtensions, imagePyramidObjectExtensions);
    }
    
    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(){
        return new DefaultImagePyramid();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleTypeContainer> itemIconSimpleExtensions,
            List<Object> itemIconObjectExtensions) {
        return new DefaultItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon() {
        return new DefaultItemIcon();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle createLabelStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            double scale,
            List<SimpleTypeContainer> labelStyleSimpleExtensions,
            List<Object> labelStyleObjectExtensions) {
        return new DefaultLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle createLabelStyle() {
        return new DefaultLabelStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions,
            List<Object> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleTypeContainer> latLonBoxSimpleExtensions,
            List<Object> latLonBoxObjectExtensions) {
        return new DefaultLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, 
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox() {
        return new DefaultLatLonBox();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox createLatLonAltBox(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions,
            List<Object> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, EnumAltitudeMode altitudeMode,
            List<SimpleTypeContainer> latLonAltBoxSimpleExtensions,
            List<Object> latLonAltBoxObjectExtensions) {
        return new DefaultLatLonAltBox(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode,
                latLonAltBoxSimpleExtensions,
                latLonAltBoxObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox createLatLonAltBox(){
        return new DefaultLatLonAltBox();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LinearRing createLinearRing(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            EnumAltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleTypeContainer> linearRingSimpleExtensions,
            List<Object> linearRingObjectExtensions) {
        return new DefaultLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate,
                altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LinearRing createLinearRing(Coordinates coordinates){
        return new DefaultLinearRing(coordinates, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineString createLineString(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleTypeContainer> lineStringSimpleExtensions,
            List<Object> lineStringObjectExtensions) {
        return new DefaultLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode,
                coordinates,
                lineStringSimpleExtensions,
                lineStringObjectExtensions, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineString createLineString(Coordinates coordinates) {
        return new DefaultLineString(coordinates,GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineStyle createLineStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            double width,
            List<SimpleTypeContainer> lineStyleSimpleExtensions,
            List<Object> lineStyleObjectExtensions) {
        return new DefaultLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineStyle createLineStyle(){
        return new DefaultLineStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link createLink(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String href, 
            List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval,
            ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleTypeContainer> linkSimpleExtensions,
            List<Object> linkObjectExtensions) {
        return new DefaultLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode,
                viewRefreshTime, viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link createLink() {
        return new DefaultLink();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleTypeContainer> listStyleSimpleExtensions,
            List<Object> listStyleObjectExtensions) {
        return new DefaultListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle() {
        return new DefaultListStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleTypeContainer> locationSimpleExtensions,
            List<Object> locationObjectExtensions) {
        return new DefaultLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude,
                locationSimpleExtensions, locationObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(){
        return new DefaultLocation();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Lod createLod(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels,
            double minFadeExtent, double maxFadeExtent,
            List<SimpleTypeContainer> lodSimpleExtentions,
            List<Object> lodObjectExtensions) {
        return new DefaultLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Lod createLod(){
        return new DefaultLod();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractViewSimpleExtensions,
            List<Object> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> lookAtSimpleExtensions,
            List<Object> lookAtObjectExtensions) {
        return new DefaultLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range, altitudeMode,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt() {
        return new DefaultLookAt();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Deprecated
    @Override
    public Metadata createMetadata(List<Object> content){
        return new DefaultMetaData(content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Deprecated
    @Override
    public Metadata createMetadata(){
        return new DefaultMetaData();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Model createModel(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            EnumAltitudeMode altitudeMode, Location location,
            Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleTypeContainer> modelSimpleExtensions,
            List<Object> modelObjectExtensions) {
        return new DefaultModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Model createModel(){
        return new DefaultModel();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MultiGeometry createMultiGeometry(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleTypeContainer> multiGeometrySimpleExtensions,
            List<Object> multiGeometryObjectExtensions) {
        return new DefaultMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MultiGeometry createMultiGeometry(){
        return new DefaultMultiGeometry();
    }
    
    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createNetworkLink(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, 
            Object snippet, Object description,
            AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleTypeContainer> networkLinkSimpleExtensions,
            List<Object> networkLinkObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (networkLinkSimpleExtensions != null) {
            extensions.simples(Extensions.Names.NETWORK_LINK).addAll(networkLinkSimpleExtensions);
        }
        if (networkLinkObjectExtensions != null) {
            extensions.complexes(Extensions.Names.NETWORK_LINK).addAll(networkLinkObjectExtensions);
        }
        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        properties.add(FF.createAttribute(refreshVisibility, KmlModelConstants.ATT_NETWORK_LINK_REFRESH_VISIBILITY, null));
        properties.add(FF.createAttribute(flyToView, KmlModelConstants.ATT_NETWORK_LINK_FLY_TO_VIEW, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_NETWORK_LINK_LINK, null));
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_NETWORK_LINK, "NetworkLink");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createNetworkLink() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_REFRESH_VISIBILITY, KmlModelConstants.ATT_NETWORK_LINK_REFRESH_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_FLY_TO_VIEW, KmlModelConstants.ATT_NETWORK_LINK_FLY_TO_VIEW, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_NETWORK_LINK, "NetworkLink");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl createNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName, 
            Object linkDescription, Snippet linkSnippet, Calendar expires,
            Update update, AbstractView view,
            List<SimpleTypeContainer> networkLinkControlSimpleExtensions,
            List<Object> networkLinkControlObjectExtensions) {
        return new DefaultNetworkLinkControl(minRefreshPeriod, maxSessionLength,
                cookie, message, linkName, linkDescription, linkSnippet,
                expires, update, view,
                networkLinkControlSimpleExtensions, networkLinkControlObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl createNetworkLinkControl(){
        return new DefaultNetworkLinkControl();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Orientation createOrientation(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleTypeContainer> orientationSimpleExtensions,
            List<Object> orientationObjectExtensions) {
        return new DefaultOrientation(objectSimpleExtensions, idAttributes,
                heading, tilt, roll,
                orientationSimpleExtensions, orientationObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Orientation createOrientation(){
        return new DefaultOrientation();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Pair createPair(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleTypeContainer> pairSimpleExtensions,
            List<Object> pairObjectExtensions) {
        return new DefaultPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector,
                pairSimpleExtensions, pairObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Pair createPair(){
        return new DefaultPair();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Feature createPhotoOverlay(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleTypeContainer> abstractOveraySimpleExtensions,
            List<Object> abstractOverlayObjectExtensions,
            double rotation, ViewVolume viewVolume, ImagePyramid imagePyramid,
            Point point, Shape shape,
            List<SimpleTypeContainer> photoOverlaySimpleExtensions,
            List<Object> photoOverlayObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (abstractOveraySimpleExtensions != null) {
            extensions.simples(Extensions.Names.OVERLAY).addAll(abstractOveraySimpleExtensions);
        }
        if (abstractOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.OVERLAY).addAll(abstractOverlayObjectExtensions);
        }

        if (photoOverlaySimpleExtensions != null) {
            extensions.simples(Extensions.Names.PHOTO_OVERLAY).addAll(photoOverlaySimpleExtensions);
        }
        if (photoOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.PHOTO_OVERLAY).addAll(photoOverlayObjectExtensions);
        }

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        properties.add(FF.createAttribute(color, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(drawOrder, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        properties.add(FF.createAttribute(rotation, KmlModelConstants.ATT_PHOTO_OVERLAY_ROTATION, null));
        properties.add(FF.createAttribute(viewVolume, KmlModelConstants.ATT_PHOTO_OVERLAY_VIEW_VOLUME, null));
        properties.add(FF.createAttribute(imagePyramid, KmlModelConstants.ATT_PHOTO_OVERLAY_IMAGE_PYRAMID, null));
        properties.add(FF.createAttribute(point, KmlModelConstants.ATT_PHOTO_OVERLAY_POINT, null));
        properties.add(FF.createAttribute(shape, KmlModelConstants.ATT_PHOTO_OVERLAY_SHAPE, null));
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_PHOTO_OVERLAY, "PhotoOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createPhotoOverlay() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_COLOR, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_DRAW_ORDER, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_ROTATION, KmlModelConstants.ATT_PHOTO_OVERLAY_ROTATION, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_PHOTO_OVERLAY, "PhotoOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createPlacemark(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility,
            boolean open,
            AtomPersonConstruct author,
            AtomLink link,
            String address,
            AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleTypeContainer> placemarkSimpleExtensions,
            List<Object> placemarkObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (placemarkSimpleExtensions != null) {
            extensions.simples(Extensions.Names.PLACEMARK).addAll(placemarkSimpleExtensions);
        }
        if (placemarkObjectExtensions != null) {
            extensions.complexes(Extensions.Names.PLACEMARK).addAll(placemarkObjectExtensions);
        }
        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        properties.add(FF.createAttribute(abstractGeometry, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_PLACEMARK, "Placemark");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createPlacemark() {
        List<Property> properties = new ArrayList<Property>();
        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_PLACEMARK, "Placemark");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Point createPoint(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude,
            EnumAltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleTypeContainer> pointSimpleExtensions,
            List<Object> pointObjectExtensions) {
        return new DefaultPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates,
                pointSimpleExtensions, pointObjectExtensions, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Point createPoint(Coordinates coordinates) {
        return new DefaultPoint(coordinates, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Polygon createPolygon(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundary, List<Boundary> innerBoundaries,
            List<SimpleTypeContainer> polygonSimpleExtensions,
            List<Object> polygonObjectExtensions) {
        return new DefaultPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode,
                outerBoundary, innerBoundaries, GF,
                polygonSimpleExtensions,
                polygonObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Polygon createPolygon(Boundary outerBoundary, List<Boundary> innerBoundaries) {
        return new DefaultPolygon(outerBoundary, innerBoundaries, GF);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle createPolyStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleTypeContainer> polyStyleSimpleExtensions,
            List<Object> polyStyleObjectExtensions) {
        return new DefaultPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle createPolyStyle(){
        return new DefaultPolyStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Region createRegion(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, 
            List<SimpleTypeContainer> regionSimpleExtensions,
            List<Object> regionObjectExtentions) {
        return new DefaultRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Region createRegion(){
        return new DefaultRegion();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap createResourceMap(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleTypeContainer> resourceMapSimpleExtensions,
            List<Object> resourceMapObjectExtensions) {
        return new DefaultResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap createResourceMap(){
        return new DefaultResourceMap();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double x, double y, double z,
            List<SimpleTypeContainer> scaleSimpleExtensions,
            List<Object> scaleObjectExtensions) {
        return new DefaultScale(objectSimpleExtensions, idAttributes, x, y, z,
                scaleSimpleExtensions, scaleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(){
        return new DefaultScale();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id, List<Object> schemaExtensions) {
        return new DefaultSchema(simpleFields, name, id, schemaExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(){
        return new DefaultSchema();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            URI schemaURL, List<SimpleData> simpleDatas, List<Object> schemaDataExtensions) {
        return new DefaultSchemaData(objectSimpleExtensions, idAttributes,
                schemaURL, simpleDatas, schemaDataExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(){
        return new DefaultSchemaData();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createScreenOverlay(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleTypeContainer> abstractOveraySimpleExtensions,
            List<Object> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleTypeContainer> screenOverlaySimpleExtensions,
            List<Object> screenOverlayObjectExtensions) {

        List<Property> properties = new ArrayList<Property>();

        Extensions extensions = new Extensions();
        if (objectSimpleExtensions != null) {
            extensions.simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractFeatureSimpleExtensions != null) {
            extensions.simples(Extensions.Names.FEATURE).addAll(abstractFeatureSimpleExtensions);
        }
        if (abstractFeatureObjectExtensions != null) {
            extensions.complexes(Extensions.Names.FEATURE).addAll(abstractFeatureObjectExtensions);
        }

        if (abstractOveraySimpleExtensions != null) {
            extensions.simples(Extensions.Names.OVERLAY).addAll(abstractOveraySimpleExtensions);
        }
        if (abstractOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.OVERLAY).addAll(abstractOverlayObjectExtensions);
        }

        if (screenOverlaySimpleExtensions != null) {
            extensions.simples(Extensions.Names.SCREEN_OVERLAY).addAll(screenOverlaySimpleExtensions);
        }
        if (screenOverlayObjectExtensions != null) {
            extensions.complexes(Extensions.Names.SCREEN_OVERLAY).addAll(screenOverlayObjectExtensions);
        }

        properties.add(FF.createAttribute(idAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        properties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        properties.add(FF.createAttribute(visibility, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(open, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(author, KmlModelConstants.ATT_AUTHOR, null));
        properties.add(FF.createAttribute(link, KmlModelConstants.ATT_LINK, null));
        properties.add(FF.createAttribute(address, KmlModelConstants.ATT_ADDRESS, null));
        properties.add(FF.createAttribute(addressDetails, KmlModelConstants.ATT_ADDRESS_DETAILS, null));
        properties.add(FF.createAttribute(phoneNumber, KmlModelConstants.ATT_PHONE_NUMBER, null));
        properties.add(FF.createAttribute(snippet, KmlModelConstants.ATT_SNIPPET, null));
        properties.add(FF.createAttribute(description, KmlModelConstants.ATT_DESCRIPTION, null));
        properties.add(FF.createAttribute(view, KmlModelConstants.ATT_VIEW, null));
        properties.add(FF.createAttribute(timePrimitive, KmlModelConstants.ATT_TIME_PRIMITIVE, null));
        properties.add(FF.createAttribute(styleUrl, KmlModelConstants.ATT_STYLE_URL, null));
        for (AbstractStyleSelector ass : styleSelector){
            properties.add(FF.createAttribute(ass, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        properties.add(FF.createAttribute(region, KmlModelConstants.ATT_REGION, null));
        properties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        properties.add(FF.createAttribute(color, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(drawOrder, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        properties.add(FF.createAttribute(rotation, KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION, null));
        properties.add(FF.createAttribute(overlayXY, KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY, null));
        properties.add(FF.createAttribute(screenXY, KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY, null));
        properties.add(FF.createAttribute(rotationXY, KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATIONXY, null));
        properties.add(FF.createAttribute(size, KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE, null));
        properties.add(FF.createAttribute(extensions, KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_SCREEN_OVERLAY, "ScreenOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Feature createScreenOverlay() {
        List<Property> properties = new ArrayList<Property>();

        properties.add(FF.createAttribute(KmlConstants.DEF_VISIBILITY, KmlModelConstants.ATT_VISIBILITY, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_OPEN, KmlModelConstants.ATT_OPEN, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_COLOR, KmlModelConstants.ATT_OVERLAY_COLOR, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_DRAW_ORDER, KmlModelConstants.ATT_OVERLAY_DRAW_ORDER, null));
        properties.add(FF.createAttribute(KmlConstants.DEF_ROTATION, KmlModelConstants.ATT_SCREEN_OVERLAY_ROTATION, null));
        properties.add(FF.createAttribute(new Extensions(), KmlModelConstants.ATT_EXTENSIONS, null));

        return FF.createFeature(
                properties, KmlModelConstants.TYPE_SCREEN_OVERLAY, "ScreenOverlay");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SimpleData createSimpleData(String name, String content) {
        return new DefaultSimpleData(name, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(Object displayName, String type,
            String name, List<Object> simpleFieldExtensions) {
        return new DefaultSimpleField(displayName, type, name, simpleFieldExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(){
        return new DefaultSimpleField();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Snippet createSnippet(int maxLines, Object content) {
        return new DefaultSnippet(maxLines, content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Style createStyle(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractStyleSelectorSimpleExtensions,
            List<Object> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle,
            PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleTypeContainer> styleSimpleExtensions,
            List<Object> styleObjectExtensions) {
        return new DefaultStyle(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Style createStyle() {
        return new DefaultStyle();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractStyleSelectorSimpleExtensions,
            List<Object> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleTypeContainer> styleMapSimpleExtensions,
            List<Object> styleMapObjectExtensions) {
        return new DefaultStyleMap(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(){
        return new DefaultStyleMap();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public TimeSpan createTimeSpan(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end, List<SimpleTypeContainer> timeSpanSimpleExtensions,
            List<Object> timeSpanObjectExtensions) {
        return new DefaultTimeSpan(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public TimeSpan createTimeSpan(){
        return new DefaultTimeSpan();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public TimeStamp createTimeStamp(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar when, List<SimpleTypeContainer> timeStampSimpleExtensions,
            List<Object> timeStampObjectExtensions) {
        return new DefaultTimeStamp(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public TimeStamp createTimeStamp(){
        return new DefaultTimeStamp();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Update createUpdate(URI targetHref, List<Object> updates,
            List<Object> updateOpExtensions, List<Object> updateExtensions) {
        return new DefaultUpdate(targetHref, updates,
                updateOpExtensions, updateExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Update createUpdate(){
        return new DefaultUpdate();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit) {
        return new DefaultVec2(x, y, xUnit, yUnit);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2() {
        return new DefaultVec2();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleTypeContainer> viewVolumeSimpleExtensions,
            List<Object> viewVolumeObjectExtensions) {
        return new DefaultViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume() {
        return new DefaultViewVolume();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Cdata createCdata(String content) {
        return new DefaultCdata(content);
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public SimpleTypeContainer createSimpleTypeContainer(String namespaceUri, String tagName, Object value) {
        return new DefaultSimpleTypeContainer(namespaceUri, tagName, value);
    }
}
