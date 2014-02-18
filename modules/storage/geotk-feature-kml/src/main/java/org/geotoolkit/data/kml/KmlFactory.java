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
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.awt.Color;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.ImagePyramid;
import org.geotoolkit.data.kml.model.ItemIcon;
import org.geotoolkit.data.kml.model.ItemIconState;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LatLonAltBox;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.ListItem;
import org.geotoolkit.data.kml.model.ListStyle;
import org.geotoolkit.data.kml.model.Location;
import org.geotoolkit.data.kml.model.Lod;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.Shape;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.model.SimpleField;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.StyleState;
import org.geotoolkit.data.kml.model.TimeSpan;
import org.geotoolkit.data.kml.model.TimeStamp;
import org.geotoolkit.data.kml.model.Units;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

import org.opengis.feature.Feature;

/**
 * <p>This interface recapitulates the methods allowing
 * to create objects mapping kml 2.2 /2.1 elements.</p>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface KmlFactory {

    /**
     *
     * @param networkLinkControl
     * @param abstractFeature
     * @param kmlSimpleExtension
     * @param kmlObjectExtensions
     * @return
     */
    Kml createKml(NetworkLinkControl networkLinkControl,
            Feature abstractFeature,
            List<SimpleTypeContainer> kmlSimpleExtension,
            List<Object> kmlObjectExtensions);

    /**
     *
     * @return
     */
    Kml createKml();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param targetHref
     * @param sourceHref
     * @param aliasSimpleExtensions
     * @param aliasObjectExtensions
     * @return
     */
    Alias createAlias(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleTypeContainer> aliasSimpleExtensions, List<Object> aliasObjectExtensions);

    /**
     *
     * @return
     */
    Alias createAlias();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param bgColor
     * @param textColor
     * @param text
     * @param displayMode
     * @param balloonStyleSimpleExtensions
     * @param balloonStyleObjectExtensions
     * @return
     */
    BalloonStyle createBalloonStyle(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions, List<Object> subStyleObjectExtensions,
            Color bgColor, Color textColor, Object text, DisplayMode displayMode,
            List<SimpleTypeContainer> balloonStyleSimpleExtensions, List<Object> balloonStyleObjectExtensions);

    /**
     *
     * @return
     */
    BalloonStyle createBalloonStyle();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     * @return
     */
    BasicLink createBasicLink(List<SimpleTypeContainer> objectSimpleExtensions, 
            IdAttributes idAttributes, String href,
            List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions);

    /**
     *
     * @return
     */
    BasicLink createBasicLink();

    /**
     *
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     * @return
     */
    Boundary createBoundary(LinearRing linearRing,
            List<SimpleTypeContainer> boundarySimpleExtensions, List<Object> boundaryObjectExtensions);

    /**
     *
     * @return
     */
    Boundary createBoundary();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     * @param longitude
     * @param latitude
     * @param altitude
     * @param heading
     * @param tilt
     * @param roll
     * @param altitudeMode
     * @param cameraSimpleExtensions
     * @param cameraObjectExtensions
     * @return
     */
    Camera createCamera(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractViewSimpleExtensions, List<Object> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> cameraSimpleExtensions, List<Object> cameraObjectExtensions);

    /**
     *
     * @return
     */
    Camera createCamera();

    /**
     *
     * @param objects
     * @return
     */
    Change createChange(List<Object> objects);

    /**
     *
     * @return
     */
    Change createChange();

    /**
     *
     * @param listCoordinates
     * @return
     */
    Coordinate createCoordinate(String listCoordinates);

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     * @param altitude
     * @return
     */
    Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude);

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     * @return
     */
    Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude);

    /**
     *
     * @param coordinates
     * @return
     */
    CoordinateSequence createCoordinates(List<Coordinate> coordinates);

    /**
     *
     * @param containers
     * @return
     */
    Create createCreate(List<Feature> containers);

    /**
     *
     * @return
     */
    Create createCreate();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param displayName
     * @param value
     * @param dataExtensions
     * @return
     */
    Data createData(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, Object displayName, String value, List<Object> dataExtensions);

    /**
     *
     * @return
     */
    Data createData();

    /**
     *
     * @param features
     * @return
     */
    Delete createDelete(List<Feature> features);

    /**
     *
     * @return
     */
    Delete createDelete();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     * @param schemas
     * @param features
     * @param documentSimpleExtensions
     * @param documentObjectExtensions
     * @return
     */
    Feature createDocument(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
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
            List<Object> documentObjectExtensions);

    /**
     *
     * @return
     */
    Feature createDocument();

    /**
     *
     * @param datas
     * @param schemaDatas
     * @param anyOtherElements
     * @return
     */
    ExtendedData createExtendedData(List<Data> datas, 
            List<SchemaData> schemaDatas, List<Object> anyOtherElements);

    /**
     *
     * @return
     */
    ExtendedData createExtendedData();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     * @param features
     * @param folderSimpleExtensions
     * @param folderObjectExtensions
     * @return
     */
    Feature createFolder(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
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
            List<Object> folderObjectExtensions);

    /**
     *
     * @return
     */
    Feature createFolder();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOveraySimpleExtensions
     * @param abstractOverlayObjectExtensions
     * @param altitude
     * @param altitudeMode
     * @param latLonBox
     * @param groundOverlaySimpleExtensions
     * @param groundOverlayObjectExtensions
     * @return
     */
    Feature createGroundOverlay(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
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
            List<SimpleTypeContainer> abstractOveraySimpleExtensions, List<Object> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleTypeContainer> groundOverlaySimpleExtensions, List<Object> groundOverlayObjectExtensions);

    /**
     *
     * @return
     */
    Feature createGroundOverlay();

    /**
     *
     * @param link
     * @return
     */
    Icon createIcon(Link link);

    /**
     *
     * @param link
     * @return
     * @deprecated
     */
    @Deprecated
    Url createUrl(Link link);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param scale
     * @param heading
     * @param icon
     * @param hotSpot
     * @param iconStyleSimpleExtensions
     * @param iconStyleObjectExtensions
     * @return
     */
    IconStyle createIconStyle(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions, List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions, List<Object> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleTypeContainer> iconStyleSimpleExtensions, List<Object> iconStyleObjectExtensions);

    /**
     *
     * @return
     */
    IconStyle createIconStyle();

    /**
     *
     * @param id
     * @param targetId
     * @return
     */
    IdAttributes createIdAttributes(String id, String targetId);

    /**
     *
     * @return
     */
    IdAttributes createIdAttributes();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param titleSize
     * @param maxWidth
     * @param maxHeight
     * @param gridOrigin
     * @param imagePyramidSimpleExtensions
     * @param imagePyramidObjectExtensions
     * @return
     */
    ImagePyramid createImagePyramid(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleTypeContainer> imagePyramidSimpleExtensions, 
            List<Object> imagePyramidObjectExtensions);

    /**
     *
     * @return
     */
    ImagePyramid createImagePyramid();
    
    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param states
     * @param href
     * @param itemIconSimpleExtensions
     * @param itemIconObjectExtensions
     * @return
     */
    ItemIcon createItemIcon(List<SimpleTypeContainer> objectSimpleExtensions, 
            IdAttributes idAttributes, List<ItemIconState> states, String href,
            List<SimpleTypeContainer> itemIconSimpleExtensions, 
            List<Object> itemIconObjectExtensions);

    /**
     *
     * @return
     */
    ItemIcon createItemIcon();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param scale
     * @param labelStyleSimpleExtensions
     * @param labelStyleObjectExtensions
     * @return
     */
    LabelStyle createLabelStyle(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions, List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions, List<Object> colorStyleObjectExtensions,
            double scale,
            List<SimpleTypeContainer> labelStyleSimpleExtensions, List<Object> labelStyleObjectExtensions);

    /**
     *
     * @return
     */
    LabelStyle createLabelStyle();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param north
     * @param south
     * @param east
     * @param west
     * @param abstractLatLonBoxSimpleExtensions
     * @param abstractLatLonBoxObjectExtensions
     * @param rotation
     * @param latLonBoxSimpleExtensions
     * @param latLonBoxObjectExtensions
     * @return
     */
    LatLonBox createLatLonBox(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions,
            List<Object> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleTypeContainer> latLonBoxSimpleExtensions,
            List<Object> latLonBoxObjectExtensions);

    /**
     *
     * @return
     */
    LatLonBox createLatLonBox();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param north
     * @param south
     * @param east
     * @param west
     * @param abstractLatLonBoxSimpleExtensions
     * @param abstractLatLonBoxObjectExtensions
     * @param minAltitude
     * @param maxAltitude
     * @param altitudeMode
     * @param latLonAltBoxSimpleExtensions
     * @param latLonAltBoxObjectExtensions
     * @return
     */
    LatLonAltBox createLatLonAltBox(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleTypeContainer> abstractLatLonBoxSimpleExtensions,
            List<Object> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> latLonAltBoxSimpleExtensions,
            List<Object> latLonAltBoxObjectExtensions);

    /**
     *
     * @return
     */
    LatLonAltBox createLatLonAltBox();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param coordinates
     * @param linearRingSimpleExtensions
     * @param linearRingObjectExtensions
     * @return
     */
    LinearRing createLinearRing(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            CoordinateSequence coordinates,
            List<SimpleTypeContainer> linearRingSimpleExtensions,
            List<Object> linearRingObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    LinearRing createLinearRing(CoordinateSequence coordinates);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param coordinates
     * @param lineStringSimpleExtensions
     * @param lineStringObjectExtensions
     * @return
     */
    LineString createLineString(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            CoordinateSequence coordinates,
            List<SimpleTypeContainer> lineStringSimpleExtensions,
            List<Object> lineStringObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    LineString createLineString(CoordinateSequence coordinates);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param width
     * @param lineStyleSimpleExtensions
     * @param lineStyleObjectExtensions
     * @return
     */
    LineStyle createLineStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            double width,
            List<SimpleTypeContainer> lineStyleSimpleExtensions,
            List<Object> lineStyleObjectExtensions);

    /**
     *
     * @return
     */
    LineStyle createLineStyle();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     * @param refreshMode
     * @param refreshInterval
     * @param viewRefreshMode
     * @param viewRefreshTime
     * @param viewBoundScale
     * @param viewFormat
     * @param httpQuery
     * @param linkSimpleExtensions
     * @param linkObjectExtensions
     * @return
     */
    Link createLink(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval,
            ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleTypeContainer> linkSimpleExtensions,
            List<Object> linkObjectExtensions);

    /**
     *
     * @return
     */
    Link createLink();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param listItem
     * @param bgColor
     * @param itemIcons
     * @param maxSnippetLines
     * @param listStyleSimpleExtensions
     * @param listStyleObjectExtensions
     * @return
     */
    ListStyle createListStyle(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions, List<Object> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleTypeContainer> listStyleSimpleExtensions, List<Object> listStyleObjectExtensions);

    /**
     *
     * @return
     */
    ListStyle createListStyle();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param longitude
     * @param latitude
     * @param altitude
     * @param locationSimpleExtensions
     * @param locationObjectExtensions
     * @return
     */
    Location createLocation(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleTypeContainer> locationSimpleExtensions, List<Object> locationObjectExtensions);

    /**
     *
     * @return
     */
    Location createLocation();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param minLodPixels
     * @param maxLodPixels
     * @param minFadeExtent
     * @param maxFadeExtent
     * @param lodSimpleExtentions
     * @param lodObjectExtensions
     * @return
     */
    Lod createLod(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleTypeContainer> lodSimpleExtentions, List<Object> lodObjectExtensions);

    /**
     *
     * @return
     */
    Lod createLod();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     * @param longitude
     * @param latitude
     * @param altitude
     * @param heading
     * @param tilt
     * @param range
     * @param lookAtSimpleExtensions
     * @param lookAtObjectExtensions
     * @return
     */
    LookAt createLookAt(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractViewSimpleExtensions, List<Object> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range, AltitudeMode altitudeMode,
            List<SimpleTypeContainer> lookAtSimpleExtensions, List<Object> lookAtObjectExtensions);

    /**
     *
     * @return
     */
    LookAt createLookAt();

    /**
     *
     * @param content
     * @return
     * @deprecated
     */
    @Deprecated
    Metadata createMetadata(List<Object> content);

    /**
     *
     * @return
     * @deprecated
     */
    @Deprecated
    Metadata createMetadata();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param altitudeMode
     * @param location
     * @param orientation
     * @param scale
     * @param link
     * @param resourceMap
     * @param modelSimpleExtensions
     * @param modelObjectExtensions
     * @return
     */
    Model createModel(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation,
            Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleTypeContainer> modelSimpleExtensions, List<Object> modelObjectExtensions);

    /**
     *
     * @return
     */
    Model createModel();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param geometries
     * @param multiGeometrySimpleExtensions
     * @param multiGeometryObjectExtensions
     * @return
     */
    MultiGeometry createMultiGeometry(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleTypeContainer> multiGeometrySimpleExtensions,
            List<Object> multiGeometryObjectExtensions);

    /**
     *
     * @return
     */
    MultiGeometry createMultiGeometry();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param refreshVisibility
     * @param flyToView
     * @param link
     * @param networkLinkSimpleExtensions
     * @param networkLinkObjectExtensions
     * @return
     */
     Feature createNetworkLink(List<SimpleTypeContainer> objectSimpleExtensions,
             IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleTypeContainer> abstractFeatureSimpleExtensions,
            List<Object> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleTypeContainer> networkLinkSimpleExtensions, 
            List<Object> networkLinkObjectExtensions);

     /**
      *
      * @return
      */
     Feature createNetworkLink();

    /**
     * 
     * @param minRefreshPeriod
     * @param maxSessionLength
     * @param cookie
     * @param message
     * @param linkName
     * @param linkDescription
     * @param linkSnippet
     * @param expire
     * @param update
     * @param view
     * @param networkLinkControlSimpleExtensions
     * @param networkLinkControlObjectExtensions
     * @return
     */
    NetworkLinkControl createNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName,
            Object linkDescription,  Snippet linkSnippet, Calendar expire,
            Update update, AbstractView view,
            List<SimpleTypeContainer> networkLinkControlSimpleExtensions,
            List<Object> networkLinkControlObjectExtensions);

    /**
     *
     * @return
     */
    NetworkLinkControl createNetworkLinkControl();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param heading
     * @param tilt
     * @param roll
     * @param orientationSimpleExtensions
     * @param orientationObjectExtensions
     * @return
     */
    Orientation createOrientation(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleTypeContainer> orientationSimpleExtensions,
            List<Object> orientationObjectExtensions);

    /**
     *
     * @return
     */
    Orientation createOrientation();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param key
     * @param styleUrl
     * @param styleSelector
     * @param pairSimpleExtensions
     * @param pairObjectExtensions
     * @return
     */
    Pair createPair(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleTypeContainer> pairSimpleExtensions,
            List<Object> pairObjectExtensions);

    /**
     *
     * @return
     */
    Pair createPair();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOveraySimpleExtensions
     * @param abstractOverlayObjectExtensions
     * @param rotation
     * @param viewVolume
     * @param imagePyramid
     * @param point
     * @param shape
     * @param photoOverlaySimpleExtensions
     * @param photoOverlayObjectExtensions
     * @return
     */
    Feature createPhotoOverlay(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, 
            Object snippet, Object description, AbstractView view,
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
            List<Object> photoOverlayObjectExtensions);

    /**
     *
     * @return
     */
    Feature createPhotoOverlay();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractGeometry
     * @param placemarkSimpleExtensions
     * @param placemarkObjectExtension
     * @return
     */
    Feature createPlacemark(List<SimpleTypeContainer> objectSimpleExtensions,
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
            List<Object> placemarkObjectExtension);

    /**
     *
     * @return
     */
    Feature createPlacemark();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param altitudeMode
     * @param coordinates
     * @param pointSimpleExtensions
     * @param pointObjectExtensions
     * @return
     */
    Point createPoint(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            CoordinateSequence coordinates,
            List<SimpleTypeContainer> pointSimpleExtensions,
            List<Object> pointObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    Point createPoint(CoordinateSequence coordinates);

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param outerBoundary
     * @param innerBoundaries
     * @param polygonSimpleExtensions
     * @param polygonObjectExtensions
     * @return
     */
    Polygon createPolygon(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundary, List<Boundary> innerBoundaries,
            List<SimpleTypeContainer> polygonSimpleExtensions,
            List<Object> polygonObjectExtensions);

    /**
     *
     * @param outerBoundary
     * @param innerBoundaries
     * @return
     */
    Polygon createPolygon(Boundary outerBoundary, List<Boundary> innerBoundaries);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param fill
     * @param outline
     * @param polyStyleSimpleExtensions
     * @param polyStyleObjectExtensions
     * @return
     */
    PolyStyle createPolyStyle(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleTypeContainer> subStyleSimpleExtensions,
            List<Object> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleTypeContainer> colorStyleSimpleExtensions,
            List<Object> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleTypeContainer> polyStyleSimpleExtensions,
            List<Object> polyStyleObjectExtensions);

    /**
     *
     * @return
     */
    PolyStyle createPolyStyle();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param latLonAltBox
     * @param lod
     * @param regionSimpleExtensions
     * @param regionObjectExtentions
     * @return
     */
    Region createRegion(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, 
            List<SimpleTypeContainer> regionSimpleExtensions,
            List<Object> regionObjectExtentions);

    /**
     *
     * @return
     */
    Region createRegion();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param aliases
     * @param resourceMapSimpleExtensions
     * @param resourceMapObjectExtensions
     * @return
     */
    ResourceMap createResourceMap(
            List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleTypeContainer> resourceMapSimpleExtensions,
            List<Object> resourceMapObjectExtensions);

    /**
     *
     * @return
     */
    ResourceMap createResourceMap();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param x
     * @param y
     * @param z
     * @param scaleSimpleExtensions
     * @param scaleObjectExtensions
     * @return
     */
    Scale createScale(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleTypeContainer> scaleSimpleExtensions,
            List<Object> scaleObjectExtensions);

    /**
     *
     * @return
     */
    Scale createScale();

    /**
     *
     * @param simpleFields
     * @param name
     * @param id
     * @param schemaExtensions
     * @return
     */
    Schema createSchema(List<SimpleField> simpleFields,
            String name, String id, List<Object> schemaExtensions);

    /**
     *
     * @return
     */
    Schema createSchema();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param schemaURL
     * @param simpleDatas
     * @param schemaDataExtensions
     * @return
     */
    SchemaData createSchemaData(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            URI schemaURL, List<SimpleData> simpleDatas, List<Object> schemaDataExtensions);

    /**
     *
     * @return
     */
    SchemaData createSchemaData();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOveraySimpleExtensions
     * @param abstractOverlayObjectExtensions
     * @param overlayXY
     * @param screenXY
     * @param rotationXY
     * @param size
     * @param rotation
     * @param screenOverlaySimpleExtensions
     * @param screenOverlayObjectExtensions
     * @return
     */
    Feature createScreenOverlay(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
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
            List<Object> screenOverlayObjectExtensions);

    /**
     *
     * @return
     */
    Feature createScreenOverlay();

    /**
     *
     * @param name
     * @param content
     * @return
     */
    SimpleData createSimpleData(String name, String content);

    /**
     *
     * @param displayName
     * @param type
     * @param name
     * @param simpleFieldExtensions
     * @return
     */
    SimpleField createSimpleField(Object displayName, String type,
            String name, List<Object> simpleFieldExtensions);

    /**
     *
     * @return
     */
    SimpleField createSimpleField();

    /**
     *
     * @param maxLines
     * @param content
     * @return
     */
    Snippet createSnippet(int maxLines, Object content);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     * @param iconStyle
     * @param labelStyle
     * @param lineStyle
     * @param polyStyle
     * @param balloonStyle
     * @param listStyle
     * @param styleSimpleExtensions
     * @param styleObjectExtensions
     * @return
     */
    Style createStyle(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractStyleSelectorSimpleExtensions,
            List<Object> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle,
            PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleTypeContainer> styleSimpleExtensions,
            List<Object> styleObjectExtensions);

    /**
     *
     * @return
     */
    Style createStyle();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     * @param pairs
     * @param styleMapSimpleExtensions
     * @param styleMapObjectExtensions
     * @return
     */
    StyleMap createStyleMap(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractStyleSelectorSimpleExtensions,
            List<Object> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleTypeContainer> styleMapSimpleExtensions,
            List<Object> styleMapObjectExtensions);

    /**
     *
     * @return
     */
    StyleMap createStyleMap();
    
    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param begin
     * @param end
     * @param timeSpanSimpleExtensions
     * @param timeSpanObjectExtensions
     * @return
     */
    TimeSpan createTimeSpan(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end, 
            List<SimpleTypeContainer> timeSpanSimpleExtensions,
            List<Object> timeSpanObjectExtensions);

    /**
     *
     * @return
     */
    TimeSpan createTimeSpan();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param when
     * @param timeStampSimpleExtensions
     * @param timeStampObjectExtensions
     * @return
     */
    TimeStamp createTimeStamp(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions, 
            List<Object> abstractTimePrimitiveObjectExtensions,
            Calendar when,
            List<SimpleTypeContainer> timeStampSimpleExtensions,
            List<Object> timeStampObjectExtensions);

    /**
     *
     * @return
     */
    TimeStamp createTimeStamp();
    
    /**
     *
     * @param targetHref
     * @param updates
     * @param updateOpExtensions
     * @param updateExtensions
     * @return
     */
    Update createUpdate(URI targetHref, List<Object> updates,
            List<Object> updateOpExtensions, List<Object> updateExtensions);

    /**
     *
     * @return
     */
    Update createUpdate();

    /**
     *
     * @param x
     * @param y
     * @param xUnit
     * @param yUnit
     * @return
     */
    Vec2 createVec2(double x, double y, Units xUnit, Units yUnit);

    /**
     *
     * @return
     */
    Vec2 createVec2();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param leftFov
     * @param rightFov
     * @param bottomFov
     * @param topFov
     * @param near
     * @param viewVolumeSimpleExtensions
     * @param viewVolumeObjectExtensions
     * @return
     */
     ViewVolume createViewVolume(List<SimpleTypeContainer> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleTypeContainer> viewVolumeSimpleExtensions, List<Object> viewVolumeObjectExtensions);

    /**
     *
     * @return
     */
    ViewVolume createViewVolume();

    /**
     *
     * @param content
     * @return
     */
   Cdata createCdata(String content);

    /**
     * 
     * @param namespaceUri
     * @param tagName
     * @param value
     * @return
     */
    SimpleTypeContainer createSimpleTypeContainer(String namespaceUri, String tagName, Object value);
}
