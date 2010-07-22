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
import java.awt.Color;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinates;
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
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.opengis.feature.Feature;

/**
 * <p>This interface recapitulates the methods allowing
 * to create objects mapping kml 2.2 /2.1 elements.</p>
 *
 * @author Samuel Andrés
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
            List<SimpleType> kmlSimpleExtension,
            List<AbstractObject> kmlObjectExtensions);

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
    Alias createAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions);

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
    BalloonStyle createBalloonStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, Object text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions);

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
    BasicLink createBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions);

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
            List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions);

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
    Camera createCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, EnumAltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions);

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
    Coordinates createCoordinates(List<Coordinate> coordinates);

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
    Data createData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
    Feature createDocument(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Schema> schemas, List<Feature> features,
            List<SimpleType> documentSimpleExtensions,
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
    Feature createFolder(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Feature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions);

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
    Feature createGroundOverlay(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, EnumAltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions, List<AbstractObject> groundOverlayObjectExtensions);

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
    IconStyle createIconStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions);

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
    ImagePyramid createImagePyramid(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions);

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
    ItemIcon createItemIcon(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions);

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
    LabelStyle createLabelStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions, List<AbstractObject> labelStyleObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleType> latLonBoxSimpleExtensions,
            List<AbstractObject> latLonBoxObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, EnumAltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions,
            List<AbstractObject> latLonAltBoxObjectExtensions);

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
    LinearRing createLinearRing(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            EnumAltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    LinearRing createLinearRing(Coordinates coordinates);

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
    LineString createLineString(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            EnumAltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> lineStringSimpleExtensions,
            List<AbstractObject> lineStringObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    LineString createLineString(Coordinates coordinates);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions,
            List<AbstractObject> lineStyleObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval,
            ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions,
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
    ListStyle createListStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, List<AbstractObject> listStyleObjectExtensions);

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
    Location createLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions);

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
    Lod createLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions);

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
    LookAt createLookAt(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions);

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
    Model createModel(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            EnumAltitudeMode altitudeMode, Location location, Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions, List<AbstractObject> modelObjectExtensions);

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
    MultiGeometry createMultiGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions);

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
     Feature createNetworkLink(List<SimpleType> objectSimpleExtensions,
             IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions, List<AbstractObject> networkLinkObjectExtensions);

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
            List<SimpleType> networkLinkControlSimpleExtensions,
            List<AbstractObject> networkLinkControlObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, 
            Object snippet, Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions,
            List<AbstractObject> abstractOverlayObjectExtensions,
            double rotation, ViewVolume viewVolume, ImagePyramid imagePyramid,
            Point point, Shape shape,
            List<SimpleType> photoOverlaySimpleExtensions,
            List<AbstractObject> photoOverlayObjectExtensions);

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
    Feature createPlacemark(List<SimpleType> objectSimpleExtensions,
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
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtension);

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
    Point createPoint(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            EnumAltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions);

    /**
     *
     * @param coordinates
     * @return
     */
    Point createPoint(Coordinates coordinates);

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
    Polygon createPolygon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, EnumAltitudeMode altitudeMode,
            Boundary outerBoundary, List<Boundary> innerBoundaries,
            List<SimpleType> polygonSimpleExtensions,
            List<AbstractObject> polygonObjectExtensions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions,
            List<AbstractObject> polyStyleObjectExtensions);

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
    Region createRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, 
            List<SimpleType> regionSimpleExtensions,
            List<AbstractObject> regionObjectExtentions);

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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions,
            List<AbstractObject> resourceMapObjectExtensions);

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
    Scale createScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions,
            List<AbstractObject> scaleObjectExtensions);

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
    SchemaData createSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
    Feature createScreenOverlay(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, 
            String phoneNumber, Object snippet,
            Object description, AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleType> screenOverlaySimpleExtensions, List<AbstractObject> screenOverlayObjectExtensions);

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
    Style createStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle, PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions);

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
    StyleMap createStyleMap(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions);

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
    TimeSpan createTimeSpan(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions);

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
    TimeStamp createTimeStamp(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions);

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
     ViewVolume createViewVolume(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions);

    /**
     * 
     * @return
     */
    public ViewVolume createViewVolume();
}
