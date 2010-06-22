package org.geotoolkit.data.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.kml.AbstractContainer;
import org.geotoolkit.data.model.kml.AbstractFeature;
import org.geotoolkit.data.model.kml.AbstractGeometry;
import org.geotoolkit.data.model.kml.AbstractObject;
import org.geotoolkit.data.model.kml.AbstractStyleSelector;
import org.geotoolkit.data.model.kml.AbstractTimePrimitive;
import org.geotoolkit.data.model.kml.AbstractView;
import org.geotoolkit.data.model.kml.Alias;
import org.geotoolkit.data.model.kml.DefaultAlias;
import org.geotoolkit.data.model.kml.AltitudeMode;
import org.geotoolkit.data.model.kml.BalloonStyle;
import org.geotoolkit.data.model.kml.DefaultBalloonStyle;
import org.geotoolkit.data.model.kml.BasicLink;
import org.geotoolkit.data.model.kml.DefaultBasicLink;
import org.geotoolkit.data.model.kml.Boundary;
import org.geotoolkit.data.model.kml.DefaultBoundary;
import org.geotoolkit.data.model.kml.Camera;
import org.geotoolkit.data.model.kml.DefaultCamera;
import org.geotoolkit.data.model.kml.Change;
import org.geotoolkit.data.model.kml.DefaultChange;
import org.geotoolkit.data.model.kml.ColorMode;
import org.geotoolkit.data.model.kml.Coordinate;
import org.geotoolkit.data.model.kml.DefaultCoordinate;
import org.geotoolkit.data.model.kml.Coordinates;
import org.geotoolkit.data.model.kml.DefaultCoordinates;
import org.geotoolkit.data.model.kml.Create;
import org.geotoolkit.data.model.kml.DefaultCreate;
import org.geotoolkit.data.model.kml.Data;
import org.geotoolkit.data.model.kml.DefaultData;
import org.geotoolkit.data.model.kml.Delete;
import org.geotoolkit.data.model.kml.DefaultDelete;
import org.geotoolkit.data.model.kml.DisplayMode;
import org.geotoolkit.data.model.kml.Document;
import org.geotoolkit.data.model.kml.DefaultDocument;
import org.geotoolkit.data.model.kml.ExtendedData;
import org.geotoolkit.data.model.kml.DefaultExtendedData;
import org.geotoolkit.data.model.kml.Folder;
import org.geotoolkit.data.model.kml.DefaultFolder;
import org.geotoolkit.data.model.kml.GridOrigin;
import org.geotoolkit.data.model.kml.GroundOverlay;
import org.geotoolkit.data.model.kml.DefaultGroundOverlay;
import org.geotoolkit.data.model.kml.Icon;
import org.geotoolkit.data.model.kml.DefaultIcon;
import org.geotoolkit.data.model.kml.IconStyle;
import org.geotoolkit.data.model.kml.DefaultIconStyle;
import org.geotoolkit.data.model.kml.IdAttributes;
import org.geotoolkit.data.model.kml.DefaultIdAttributes;
import org.geotoolkit.data.model.kml.ImagePyramid;
import org.geotoolkit.data.model.kml.DefaultImagePyramid;
import org.geotoolkit.data.model.kml.ItemIcon;
import org.geotoolkit.data.model.kml.DefaultItemIcon;
import org.geotoolkit.data.model.kml.ItemIconState;
import org.geotoolkit.data.model.kml.Kml;
import org.geotoolkit.data.model.kml.DefaultKml;
import org.geotoolkit.data.model.kml.LabelStyle;
import org.geotoolkit.data.model.kml.DefaultLabelStyle;
import org.geotoolkit.data.model.kml.LatLonAltBox;
import org.geotoolkit.data.model.kml.DefaultLatLonAltBox;
import org.geotoolkit.data.model.kml.LatLonBox;
import org.geotoolkit.data.model.kml.DefaultLatLonBox;
import org.geotoolkit.data.model.kml.LineString;
import org.geotoolkit.data.model.kml.DefaultLineString;
import org.geotoolkit.data.model.kml.LineStyle;
import org.geotoolkit.data.model.kml.DefaultLineStyle;
import org.geotoolkit.data.model.kml.LinearRing;
import org.geotoolkit.data.model.kml.DefaultLinearRing;
import org.geotoolkit.data.model.kml.Link;
import org.geotoolkit.data.model.kml.DefaultLink;
import org.geotoolkit.data.model.kml.ListItem;
import org.geotoolkit.data.model.kml.ListStyle;
import org.geotoolkit.data.model.kml.DefaultListStyle;
import org.geotoolkit.data.model.kml.Location;
import org.geotoolkit.data.model.kml.DefaultLocation;
import org.geotoolkit.data.model.kml.Lod;
import org.geotoolkit.data.model.kml.DefaultLod;
import org.geotoolkit.data.model.kml.LookAt;
import org.geotoolkit.data.model.kml.DefaultLookAt;
import org.geotoolkit.data.model.kml.Model;
import org.geotoolkit.data.model.kml.DefaultModel;
import org.geotoolkit.data.model.kml.MultiGeometry;
import org.geotoolkit.data.model.kml.DefaultMultiGeometry;
import org.geotoolkit.data.model.kml.NetworkLink;
import org.geotoolkit.data.model.kml.NetworkLinkControl;
import org.geotoolkit.data.model.kml.DefaultNetworkLinkControl;
import org.geotoolkit.data.model.kml.DefaultNetworkLink;
import org.geotoolkit.data.model.kml.Orientation;
import org.geotoolkit.data.model.kml.DefaultOrientation;
import org.geotoolkit.data.model.kml.Pair;
import org.geotoolkit.data.model.kml.DefaultPair;
import org.geotoolkit.data.model.kml.PhotoOverlay;
import org.geotoolkit.data.model.kml.DefaultPhotoOverlay;
import org.geotoolkit.data.model.kml.Placemark;
import org.geotoolkit.data.model.kml.DefaultPlacemark;
import org.geotoolkit.data.model.kml.Point;
import org.geotoolkit.data.model.kml.DefaultPoint;
import org.geotoolkit.data.model.kml.PolyStyle;
import org.geotoolkit.data.model.kml.DefaultPolyStyle;
import org.geotoolkit.data.model.kml.Polygon;
import org.geotoolkit.data.model.kml.DefaultPolygon;
import org.geotoolkit.data.model.kml.RefreshMode;
import org.geotoolkit.data.model.kml.Region;
import org.geotoolkit.data.model.kml.DefaultRegion;
import org.geotoolkit.data.model.kml.ResourceMap;
import org.geotoolkit.data.model.kml.DefaultResourceMap;
import org.geotoolkit.data.model.kml.Scale;
import org.geotoolkit.data.model.kml.DefaultScale;
import org.geotoolkit.data.model.kml.Schema;
import org.geotoolkit.data.model.kml.SchemaData;
import org.geotoolkit.data.model.kml.DefaultSchemaData;
import org.geotoolkit.data.model.kml.DefaultSchema;
import org.geotoolkit.data.model.kml.ScreenOverlay;
import org.geotoolkit.data.model.kml.DefaultScreenOverlay;
import org.geotoolkit.data.model.kml.Shape;
import org.geotoolkit.data.model.kml.SimpleData;
import org.geotoolkit.data.model.kml.DefaultSimpleData;
import org.geotoolkit.data.model.kml.SimpleField;
import org.geotoolkit.data.model.kml.DefaultSimpleField;
import org.geotoolkit.data.model.kml.Snippet;
import org.geotoolkit.data.model.kml.DefaultSnippet;
import org.geotoolkit.data.model.kml.Style;
import org.geotoolkit.data.model.kml.DefaultStyle;
import org.geotoolkit.data.model.kml.StyleMap;
import org.geotoolkit.data.model.kml.DefaultStyleMap;
import org.geotoolkit.data.model.kml.StyleState;
import org.geotoolkit.data.model.kml.TimeSpan;
import org.geotoolkit.data.model.kml.DefaultTimeSpan;
import org.geotoolkit.data.model.kml.TimeStamp;
import org.geotoolkit.data.model.kml.DefaultTimeStamp;
import org.geotoolkit.data.model.kml.Units;
import org.geotoolkit.data.model.kml.Update;
import org.geotoolkit.data.model.kml.DefaultUpdate;
import org.geotoolkit.data.model.kml.Vec2;
import org.geotoolkit.data.model.kml.DefaultVec2;
import org.geotoolkit.data.model.kml.ViewRefreshMode;
import org.geotoolkit.data.model.kml.ViewVolume;
import org.geotoolkit.data.model.kml.DefaultViewVolume;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class KmlFactoryDefault implements KmlFactory{

    /**
     * @{@inheritDoc }
     */
    @Override
    public Kml createKml(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtensions,
            List<AbstractObject> kmlObjectExtensions){
        return new DefaultKml(networkLinkControl, abstractFeature,
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Alias createAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String targetHref, String sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions){
        return new DefaultAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, aliasSimpleExtensions, aliasObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle createBalloonStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions){
        return new DefaultBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions){
        return new DefaultBasicLink(objectSimpleExtensions, idAttributes, href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink(){
        return new DefaultBasicLink();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions){
        return new DefaultBoundary(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions){
        return new DefaultCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera(){return new DefaultCamera();}

    /**
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(List<AbstractObject> objects){
        return new DefaultChange(objects);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return new DefaultCoordinate(listCoordinates);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude) {
        return new DefaultCoordinate(geodeticLongiude, geodeticLatitude, altitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude) {
        return new DefaultCoordinate(geodeticLongiude, geodeticLatitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinates createCoordinates(List<Coordinate> coordinates) {
        return new DefaultCoordinates(coordinates);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate(List<AbstractContainer> containers){
        return new DefaultCreate(containers);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Data createData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions){
        return new DefaultData(objectSimpleExtensions, idAttributes,
                displayName, value, dataExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete(List<AbstractFeature> features){
        return new DefaultDelete(features);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Document createDocument(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Schema> schemas, List<AbstractFeature> features,
            List<SimpleType> documentSimpleExtensions,
            List<AbstractObject> documentObjectExtensions){
        return new DefaultDocument(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData createExtendedData(List<Data> datas, List<SchemaData> schemaDatas, List<Object> anyOtherElements){
        return new DefaultExtendedData(datas, schemaDatas, anyOtherElements);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Folder createFolder(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions){
        return new DefaultFolder(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                features, folderSimpleExtensions, folderObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Folder createFolder(){
        return new DefaultFolder();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public GroundOverlay createGroundOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions, List<AbstractObject> groundOverlayObjectExtensions){
        return new DefaultGroundOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link,
                address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive,
                styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOveraySimpleExtensions, abstractOverlayObjectExtensions,
                altitude, altitudeMode, latLonBox,
                groundOverlaySimpleExtensions, groundOverlayObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public GroundOverlay createGroundOverlay(){
        return new DefaultGroundOverlay();
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public Icon createIcon(Link link){
        return new DefaultIcon(link);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public IconStyle createIconStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions){
        return new DefaultIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

    @Override
    public IconStyle createIconStyle(){
        return new DefaultIconStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes(String id, String targetId){
        return new DefaultIdAttributes(id, targetId);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions){
        return new DefaultImagePyramid(objectSimpleExtensions, idAttributes,
                titleSize, maxWidth, maxHeight, gridOrigin,
                imagePyramidSimpleExtensions, imagePyramidObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions){
        return new DefaultItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon(){
        return new DefaultItemIcon();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle createLabelStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions, List<AbstractObject> labelStyleObjectExtensions){
        return new DefaultLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleType> latLonBoxSimpleExtensions, List<AbstractObject> latLonBoxObjectExtensions){
        return new DefaultLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox(){
        return new DefaultLatLonBox();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox createLatLonAltBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, List<AbstractObject> latLonAltBoxObjectExtensions){
        return new DefaultLatLonAltBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode, latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LinearRing createLinearRing(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions){
        return new DefaultLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate,
                altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LineString createLineString(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> lineStringSimpleExtensions,
            List<AbstractObject> lineStringObjectExtensions){
        return new DefaultLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                lineStringSimpleExtensions, lineStringObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LineStyle createLineStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions){
        return new DefaultLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Link createLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval, ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, List<AbstractObject> linkObjectExtensions){
        return new DefaultLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions, refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery, linkSimpleExtensions, linkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Link createLink(){
        return new DefaultLink();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, List<AbstractObject> listStyleObjectExtensions){
        return new DefaultListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle(){
        return new DefaultListStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        return new DefaultLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude,
                locationSimpleExtensions, locationObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Lod createLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions){
        return new DefaultLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions){
        return new DefaultLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt(){
        return new DefaultLookAt();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Model createModel(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions, List<AbstractObject> modelObjectExtensions){
        return new DefaultModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public MultiGeometry createMultiGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions){
        return new DefaultMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public NetworkLink createNetworkLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions, List<AbstractObject> networkLinkObjectExtensions){
        return new DefaultNetworkLink(objectSimpleExtensions, idAttributes, name, visibility, open, author, atomLink,
                address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                refreshVisibility, flyToView, link,
                networkLinkSimpleExtensions, networkLinkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl createNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName, String linkDescription,
            Snippet linkSnippet, String expires, Update update, AbstractView view,
            List<SimpleType> networkLinkControlSimpleExtensions, List<AbstractObject> networkLinkControlObjectExtensions){
        return new DefaultNetworkLinkControl(minRefreshPeriod, maxSessionLength,
                cookie, message, linkName, linkDescription, linkSnippet,
                expires, update, view,
                networkLinkControlSimpleExtensions, networkLinkControlObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Orientation createOrientation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions){
        return new DefaultOrientation(objectSimpleExtensions, idAttributes,
                heading, tilt, roll,
                orientationSimpleExtensions, orientationObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Pair createPair(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, String styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions){
        return new DefaultPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector,
                pairSimpleExtensions, pairObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public PhotoOverlay createPhotoOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            double rotation, ViewVolume viewVolume, ImagePyramid imagePyramid, Point point, Shape shape,
            List<SimpleType> photoOverlaySimpleExtensions, List<AbstractObject> photoOverlayObjectExtensions){
        return new DefaultPhotoOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address,
                addressDetails, phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOveraySimpleExtensions, abstractOverlayObjectExtensions,
                rotation, viewVolume, imagePyramid, point, shape,
                photoOverlaySimpleExtensions, photoOverlayObjectExtensions);
    }
    
    /**
     * @{@inheritDoc }
     */
    @Override
    public PhotoOverlay createPhotoOverlay(){
        return new DefaultPhotoOverlay();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Placemark createPlacemark(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility,
            boolean open,
            AtomPersonConstruct author,
            AtomLink link,
            String address,
            AddressDetails addressDetails,
            String phoneNumber, String snippet,
            String description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtension){
        return new DefaultPlacemark(objectSimpleExtensions, idAttributes, name, visibility,
                open, author, link, address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                abstractGeometry,
                placemarkSimpleExtensions, placemarkObjectExtension);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Point createPoint(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions){
        return new DefaultPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Point createPoint(){
        return new DefaultPoint();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Polygon createPolygon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundaryIs, List<Boundary> innerBoundariesAre,
            List<SimpleType> polygonSimpleExtensions, List<AbstractObject> polygonObjectExtensions){
        return new DefaultPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, outerBoundaryIs, innerBoundariesAre,
                polygonSimpleExtensions, polygonObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle createPolyStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions, List<AbstractObject> polyStyleObjectExtensions){
        return new DefaultPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Region createRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, List<SimpleType> regionSimpleExtensions, List<AbstractObject> regionObjectExtentions){
        return new DefaultRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap createResourceMap(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions, List<AbstractObject> resourceMapObjectExtensions){
        return new DefaultResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions){
        return new DefaultScale(objectSimpleExtensions, idAttributes, x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id){
        return new DefaultSchema(simpleFields, name, id);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions){
        return new DefaultSchemaData(objectSimpleExtensions, idAttributes,
                simpleDatas, schemaDataExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ScreenOverlay createScreenOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleType> screenOverlaySimpleExtensions, List<AbstractObject> screenOverlayObjectExtensions){
        return new DefaultScreenOverlay(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address,
                addressDetails, phoneNumber, snippet,
                description, view, timePrimitive, styleUrl,
                styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOveraySimpleExtensions, abstractOverlayObjectExtensions,
                overlayXY, screenXY, rotationXY, size, rotation,
                screenOverlaySimpleExtensions, screenOverlayObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleData createSimpleData(String name, String content){
        return new DefaultSimpleData(name, content);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(String displayName, String type, String name){
        return new DefaultSimpleField(displayName, type, name);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Snippet createSnippet(int maxLines, String content){
        return new DefaultSnippet(maxLines, content);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Style createStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle, PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions){
        return new DefaultStyle(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Style createStyle(){
        return new DefaultStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions){
        return new DefaultStyleMap(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeSpan createTimeSpan(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String begin, String end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions){
        return new DefaultTimeSpan(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeStamp createTimeStamp(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions){
        return new DefaultTimeStamp(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Update createUpdate(List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<Object> updateOpExtensions, List<Object> updateExtensions){
        return new DefaultUpdate(creates, deletes, changes, updateOpExtensions, updateExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit){
        return new DefaultVec2(x, y, xUnit, yUnit);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions){
        return new DefaultViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume(){
        return new DefaultViewVolume();
    }
}
