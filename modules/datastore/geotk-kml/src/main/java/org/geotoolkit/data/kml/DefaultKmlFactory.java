package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.kml.model.AbstractContainer;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.AbstractTimePrimitive;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.DefaultAlias;
import org.geotoolkit.data.kml.model.AltitudeMode;
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
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.DefaultDocument;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.DefaultExtendedData;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.DefaultFolder;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.GroundOverlay;
import org.geotoolkit.data.kml.model.DefaultGroundOverlay;
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
import org.geotoolkit.data.kml.model.NetworkLink;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.DefaultNetworkLinkControl;
import org.geotoolkit.data.kml.model.DefaultNetworkLink;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.DefaultOrientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.DefaultPair;
import org.geotoolkit.data.kml.model.PhotoOverlay;
import org.geotoolkit.data.kml.model.DefaultPhotoOverlay;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.DefaultPlacemark;
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
import org.geotoolkit.data.kml.model.ScreenOverlay;
import org.geotoolkit.data.kml.model.DefaultScreenOverlay;
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
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultKmlFactory implements KmlFactory{

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * @{@inheritDoc }
     */
    @Override
    public Kml createKml(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtensions,
            List<AbstractObject> kmlObjectExtensions) {
        return new DefaultKml(networkLinkControl, abstractFeature,
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Kml createKml(){
        return new DefaultKml();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Alias createAlias(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions) {
        return new DefaultAlias(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, aliasSimpleExtensions, aliasObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Alias createAlias(){
        return new DefaultAlias();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle createBalloonStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, Object text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions) {
        return new DefaultBalloonStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle createBalloonStyle(){
        return new DefaultBalloonStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, 
            List<AbstractObject> basicLinkObjectExtensions) {
        return new DefaultBasicLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink() {
        return new DefaultBasicLink();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions, 
            List<AbstractObject> boundaryObjectExtensions) {
        return new DefaultBoundary(linearRing, boundarySimpleExtensions,
                boundaryObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary() {
        return new DefaultBoundary();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions,
            List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, 
            List<AbstractObject> cameraObjectExtensions) {
        return new DefaultCamera(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera() {
        return new DefaultCamera();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(List<AbstractObject> objects) {
        return new DefaultChange(objects);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(){
        return new DefaultChange();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return KmlUtilities.toCoordinate(listCoordinates);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(
            double geodeticLongiude, double geodeticLatitude, double altitude) {
        return new Coordinate(geodeticLongiude, geodeticLatitude, altitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(
            double geodeticLongiude, double geodeticLatitude) {
        return new Coordinate(geodeticLongiude, geodeticLatitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinates createCoordinates(List<Coordinate> coordinates) {
        return new DefaultCoordinates(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate(List<AbstractContainer> containers) {
        return new DefaultCreate(containers);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate() {
        return new DefaultCreate();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Data createData(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, Object displayName, String value, List<Object> dataExtensions) {
        return new DefaultData(objectSimpleExtensions, idAttributes,
                name, displayName, value, dataExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Data createData(){
        return new DefaultData();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete(List<AbstractFeature> features) {
        return new DefaultDelete(features);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete() {
        return new DefaultDelete();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Document createDocument(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
            List<Schema> schemas, List<AbstractFeature> features,
            List<SimpleType> documentSimpleExtensions,
            List<AbstractObject> documentObjectExtensions) {
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
    public Document createDocument() {
        return new DefaultDocument();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData createExtendedData(List<Data> datas, 
            List<SchemaData> schemaDatas, List<Object> anyOtherElements) {
        return new DefaultExtendedData(datas, schemaDatas, anyOtherElements);
    }


    /**
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData createExtendedData(){
        return new DefaultExtendedData();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Folder createFolder(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions) {
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
    public Folder createFolder() {
        return new DefaultFolder();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public GroundOverlay createGroundOverlay(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
            List<SimpleType> abstractOveraySimpleExtensions,
            List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions, 
            List<AbstractObject> groundOverlayObjectExtensions) {
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
    public GroundOverlay createGroundOverlay() {
        return new DefaultGroundOverlay();
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
     * @{@inheritDoc }
     */
    @Override
    public IconStyle createIconStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, 
            List<AbstractObject> iconStyleObjectExtensions) {
        return new DefaultIconStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }

    @Override
    public IconStyle createIconStyle() {
        return new DefaultIconStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes(String id, String targetId) {
        return new DefaultIdAttributes(id, targetId);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes() {
        return new DefaultIdAttributes();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions) {
        return new DefaultImagePyramid(objectSimpleExtensions, idAttributes,
                titleSize, maxWidth, maxHeight, gridOrigin,
                imagePyramidSimpleExtensions, imagePyramidObjectExtensions);
    }
    
    /**
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(){
        return new DefaultImagePyramid();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, 
            List<AbstractObject> itemIconObjectExtensions) {
        return new DefaultItemIcon(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ItemIcon createItemIcon() {
        return new DefaultItemIcon();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle createLabelStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions, 
            List<AbstractObject> labelStyleObjectExtensions) {
        return new DefaultLabelStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    @Override
    public LabelStyle createLabelStyle() {
        return new DefaultLabelStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleType> latLonBoxSimpleExtensions, 
            List<AbstractObject> latLonBoxObjectExtensions) {
        return new DefaultLatLonBox(objectSimpleExtensions, idAttributes,
                north, south, east, west, 
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox() {
        return new DefaultLatLonBox();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox createLatLonAltBox(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions,
            List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, 
            List<AbstractObject> latLonAltBoxObjectExtensions) {
        return new DefaultLatLonAltBox(objectSimpleExtensions, idAttributes,
                north, south, east, west,
                abstractLatLonBoxSimpleExtensions,
                abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode,
                latLonAltBoxSimpleExtensions,
                latLonAltBoxObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonAltBox createLatLonAltBox(){
        return new DefaultLatLonAltBox();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LinearRing createLinearRing(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions) {
        return new DefaultLinearRing(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate,
                altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions, GF);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LinearRing createLinearRing(Coordinates coordinates){
        return new DefaultLinearRing(coordinates, GF);
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
            List<AbstractObject> lineStringObjectExtensions) {
        return new DefaultLineString(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode,
                coordinates,
                lineStringSimpleExtensions,
                lineStringObjectExtensions, GF);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LineString createLineString(Coordinates coordinates) {
        return new DefaultLineString(coordinates,GF);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LineStyle createLineStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions,
            List<AbstractObject> lineStyleObjectExtensions) {
        return new DefaultLineStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LineStyle createLineStyle(){
        return new DefaultLineStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Link createLink(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, 
            List<SimpleType> basicLinkSimpleExtensions,
            List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval,
            ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, 
            List<AbstractObject> linkObjectExtensions) {
        return new DefaultLink(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions,
                refreshMode, refreshInterval, viewRefreshMode,
                viewRefreshTime, viewBoundScale, viewFormat, httpQuery,
                linkSimpleExtensions, linkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Link createLink() {
        return new DefaultLink();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, 
            List<AbstractObject> listStyleObjectExtensions) {
        return new DefaultListStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ListStyle createListStyle() {
        return new DefaultListStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions,
            List<AbstractObject> locationObjectExtensions) {
        return new DefaultLocation(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude,
                locationSimpleExtensions, locationObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(){
        return new DefaultLocation();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Lod createLod(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels,
            double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions,
            List<AbstractObject> lodObjectExtensions) {
        return new DefaultLod(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Lod createLod(){
        return new DefaultLod();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions,
            List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions,
            List<AbstractObject> lookAtObjectExtensions) {
        return new DefaultLookAt(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LookAt createLookAt() {
        return new DefaultLookAt();
    }

    /**
     *
     * @param content
     * @return
     * @deprecated
     */
    @Deprecated
    @Override
    public Metadata createMetadata(List<Object> content){
        return new DefaultMetaData(content);
    }

    /**
     *
     * @return
     * @deprecated
     */
    @Deprecated
    @Override
    public Metadata createMetadata(){
        return new DefaultMetaData();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Model createModel(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location,
            Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions,
            List<AbstractObject> modelObjectExtensions) {
        return new DefaultModel(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Model createModel(){
        return new DefaultModel();
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
            List<AbstractObject> multiGeometryObjectExtensions) {
        return new DefaultMultiGeometry(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public MultiGeometry createMultiGeometry(){
        return new DefaultMultiGeometry();
    }
    
    /**
     * @{@inheritDoc }
     */
    @Override
    public NetworkLink createNetworkLink(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, 
            Object snippet, Object description,
            AbstractView view, AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions,
            List<AbstractObject> networkLinkObjectExtensions) {
        return new DefaultNetworkLink(objectSimpleExtensions, idAttributes, name,
                visibility, open, author, atomLink,
                address, addressDetails, phoneNumber, snippet,
                description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                refreshVisibility, flyToView, link,
                networkLinkSimpleExtensions, networkLinkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public NetworkLink createNetworkLink() {
        return new DefaultNetworkLink();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl createNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName, 
            Object linkDescription, Snippet linkSnippet, Calendar expires,
            Update update, AbstractView view,
            List<SimpleType> networkLinkControlSimpleExtensions,
            List<AbstractObject> networkLinkControlObjectExtensions) {
        return new DefaultNetworkLinkControl(minRefreshPeriod, maxSessionLength,
                cookie, message, linkName, linkDescription, linkSnippet,
                expires, update, view,
                networkLinkControlSimpleExtensions, networkLinkControlObjectExtensions);
    }

    /**
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
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions) {
        return new DefaultOrientation(objectSimpleExtensions, idAttributes,
                heading, tilt, roll,
                orientationSimpleExtensions, orientationObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Orientation createOrientation(){
        return new DefaultOrientation();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Pair createPair(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions) {
        return new DefaultPair(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector,
                pairSimpleExtensions, pairObjectExtensions);
    }

    /**
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
    public PhotoOverlay createPhotoOverlay(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
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
            List<AbstractObject> photoOverlayObjectExtensions) {
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
    public PhotoOverlay createPhotoOverlay() {
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
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtension) {
        return new DefaultPlacemark(objectSimpleExtensions, idAttributes, name, 
                visibility, open, author, link, address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive,
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
    public Placemark createPlacemark() {
        return new DefaultPlacemark();
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
            List<AbstractObject> pointObjectExtensions) {
        return new DefaultPoint(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates,
                pointSimpleExtensions, pointObjectExtensions, GF);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Point createPoint(Coordinates coordinates) {
        return new DefaultPoint(coordinates, GF);
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
            Boundary outerBoundary, List<Boundary> innerBoundaries,
            List<SimpleType> polygonSimpleExtensions,
            List<AbstractObject> polygonObjectExtensions) {
        return new DefaultPolygon(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions,
                abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode,
                outerBoundary, innerBoundaries, GF,
                polygonSimpleExtensions,
                polygonObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Polygon createPolygon(Boundary outerBoundary, List<Boundary> innerBoundaries) {
        return new DefaultPolygon(outerBoundary, innerBoundaries, GF);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle createPolyStyle(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions,
            List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions,
            List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions,
            List<AbstractObject> polyStyleObjectExtensions) {
        return new DefaultPolyStyle(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle createPolyStyle(){
        return new DefaultPolyStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Region createRegion(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, 
            List<SimpleType> regionSimpleExtensions,
            List<AbstractObject> regionObjectExtentions) {
        return new DefaultRegion(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Region createRegion(){
        return new DefaultRegion();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap createResourceMap(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions,
            List<AbstractObject> resourceMapObjectExtensions) {
        return new DefaultResourceMap(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ResourceMap createResourceMap(){
        return new DefaultResourceMap();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions,
            List<AbstractObject> scaleObjectExtensions) {
        return new DefaultScale(objectSimpleExtensions, idAttributes, x, y, z,
                scaleSimpleExtensions, scaleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(){
        return new DefaultScale();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id, List<Object> schemaExtensions) {
        return new DefaultSchema(simpleFields, name, id, schemaExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(){
        return new DefaultSchema();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            URI schemaURL, List<SimpleData> simpleDatas, List<Object> schemaDataExtensions) {
        return new DefaultSchemaData(objectSimpleExtensions, idAttributes,
                schemaURL, simpleDatas, schemaDataExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(){
        return new DefaultSchemaData();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ScreenOverlay createScreenOverlay(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
            List<SimpleType> abstractOveraySimpleExtensions,
            List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, double rotation,
            List<SimpleType> screenOverlaySimpleExtensions,
            List<AbstractObject> screenOverlayObjectExtensions) {
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

    @Override
    public ScreenOverlay createScreenOverlay() {
        return new DefaultScreenOverlay();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleData createSimpleData(String name, String content) {
        return new DefaultSimpleData(name, content);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(Object displayName, String type,
            String name, List<Object> simpleFieldExtensions) {
        return new DefaultSimpleField(displayName, type, name, simpleFieldExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(){
        return new DefaultSimpleField();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Snippet createSnippet(int maxLines, Object content) {
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
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle,
            PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions) {
        return new DefaultStyle(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Style createStyle() {
        return new DefaultStyle();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions,
            List<AbstractObject> styleMapObjectExtensions) {
        return new DefaultStyleMap(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(){
        return new DefaultStyleMap();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeSpan createTimeSpan(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end, List<SimpleType> timeSpanSimpleExtensions,
            List<AbstractObject> timeSpanObjectExtensions) {
        return new DefaultTimeSpan(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeSpan createTimeSpan(){
        return new DefaultTimeSpan();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeStamp createTimeStamp(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar when, List<SimpleType> timeStampSimpleExtensions,
            List<AbstractObject> timeStampObjectExtensions) {
        return new DefaultTimeStamp(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public TimeStamp createTimeStamp(){
        return new DefaultTimeStamp();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Update createUpdate(URI targetHref, List<Object> updates,
            List<Object> updateOpExtensions, List<Object> updateExtensions) {
        return new DefaultUpdate(targetHref, updates,
                updateOpExtensions, updateExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Update createUpdate(){
        return new DefaultUpdate();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit) {
        return new DefaultVec2(x, y, xUnit, yUnit);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2() {
        return new DefaultVec2();
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume(
            List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions,
            List<AbstractObject> viewVolumeObjectExtensions) {
        return new DefaultViewVolume(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume() {
        return new DefaultViewVolume();
    }
}
