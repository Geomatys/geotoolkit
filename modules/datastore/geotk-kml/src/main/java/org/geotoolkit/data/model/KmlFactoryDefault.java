package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomLinkDefault;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomPersonConstructDefault;
import org.geotoolkit.data.model.kml.AbstractFeature;
import org.geotoolkit.data.model.kml.AbstractGeometry;
import org.geotoolkit.data.model.kml.AbstractObject;
import org.geotoolkit.data.model.kml.AbstractStyleSelector;
import org.geotoolkit.data.model.kml.AbstractTimePrimitive;
import org.geotoolkit.data.model.kml.AbstractView;
import org.geotoolkit.data.model.kml.Alias;
import org.geotoolkit.data.model.kml.AliasDefault;
import org.geotoolkit.data.model.kml.AltitudeMode;
import org.geotoolkit.data.model.kml.Angle180;
import org.geotoolkit.data.model.kml.Angle180Default;
import org.geotoolkit.data.model.kml.Angle360;
import org.geotoolkit.data.model.kml.Angle360Default;
import org.geotoolkit.data.model.kml.Angle90;
import org.geotoolkit.data.model.kml.Angle90Default;
import org.geotoolkit.data.model.kml.Anglepos180;
import org.geotoolkit.data.model.kml.Anglepos180Default;
import org.geotoolkit.data.model.kml.BalloonStyle;
import org.geotoolkit.data.model.kml.BalloonStyleDefault;
import org.geotoolkit.data.model.kml.BasicLink;
import org.geotoolkit.data.model.kml.BasicLinkDefault;
import org.geotoolkit.data.model.kml.Boundary;
import org.geotoolkit.data.model.kml.BoundaryDefault;
import org.geotoolkit.data.model.kml.Camera;
import org.geotoolkit.data.model.kml.CameraDefault;
import org.geotoolkit.data.model.kml.Color;
import org.geotoolkit.data.model.kml.ColorDefault;
import org.geotoolkit.data.model.kml.ColorMode;
import org.geotoolkit.data.model.kml.Coordinate;
import org.geotoolkit.data.model.kml.CoordinateDefault;
import org.geotoolkit.data.model.kml.Coordinates;
import org.geotoolkit.data.model.kml.CoordinatesDefault;
import org.geotoolkit.data.model.kml.Data;
import org.geotoolkit.data.model.kml.DataDefault;
import org.geotoolkit.data.model.kml.DisplayMode;
import org.geotoolkit.data.model.kml.Document;
import org.geotoolkit.data.model.kml.DocumentDefault;
import org.geotoolkit.data.model.kml.ExtendedData;
import org.geotoolkit.data.model.kml.ExtendedDataDefault;
import org.geotoolkit.data.model.kml.Folder;
import org.geotoolkit.data.model.kml.FolderDefault;
import org.geotoolkit.data.model.kml.GroundOverlay;
import org.geotoolkit.data.model.kml.GroundOverlayDefault;
import org.geotoolkit.data.model.kml.IconStyle;
import org.geotoolkit.data.model.kml.IconStyleDefault;
import org.geotoolkit.data.model.kml.IdAttributes;
import org.geotoolkit.data.model.kml.IdAttributesDefault;
import org.geotoolkit.data.model.kml.ImagePyramid;
import org.geotoolkit.data.model.kml.ItemIcon;
import org.geotoolkit.data.model.kml.ItemIconDefault;
import org.geotoolkit.data.model.kml.ItemIconState;
import org.geotoolkit.data.model.kml.Kml;
import org.geotoolkit.data.model.kml.KmlDefault;
import org.geotoolkit.data.model.kml.KmlException;
import org.geotoolkit.data.model.kml.LabelStyle;
import org.geotoolkit.data.model.kml.LabelStyleDefault;
import org.geotoolkit.data.model.kml.LatLonAltBox;
import org.geotoolkit.data.model.kml.LatLonAltBoxDefault;
import org.geotoolkit.data.model.kml.LatLonBox;
import org.geotoolkit.data.model.kml.LatLonBoxDefault;
import org.geotoolkit.data.model.kml.LineString;
import org.geotoolkit.data.model.kml.LineStringDefault;
import org.geotoolkit.data.model.kml.LineStyle;
import org.geotoolkit.data.model.kml.LineStyleDefault;
import org.geotoolkit.data.model.kml.LinearRing;
import org.geotoolkit.data.model.kml.LinearRingDefault;
import org.geotoolkit.data.model.kml.Link;
import org.geotoolkit.data.model.kml.LinkDefault;
import org.geotoolkit.data.model.kml.ListItem;
import org.geotoolkit.data.model.kml.ListStyle;
import org.geotoolkit.data.model.kml.ListStyleDefault;
import org.geotoolkit.data.model.kml.Location;
import org.geotoolkit.data.model.kml.LocationDefault;
import org.geotoolkit.data.model.kml.Lod;
import org.geotoolkit.data.model.kml.LodDefault;
import org.geotoolkit.data.model.kml.LookAt;
import org.geotoolkit.data.model.kml.LookAtDefault;
import org.geotoolkit.data.model.kml.Model;
import org.geotoolkit.data.model.kml.ModelDefault;
import org.geotoolkit.data.model.kml.MultiGeometry;
import org.geotoolkit.data.model.kml.MultiGeometryDefault;
import org.geotoolkit.data.model.kml.NetworkLinkControl;
import org.geotoolkit.data.model.kml.Orientation;
import org.geotoolkit.data.model.kml.OrientationDefault;
import org.geotoolkit.data.model.kml.Pair;
import org.geotoolkit.data.model.kml.PairDefault;
import org.geotoolkit.data.model.kml.PhotoOverlay;
import org.geotoolkit.data.model.kml.PhotoOverlayDefault;
import org.geotoolkit.data.model.kml.Placemark;
import org.geotoolkit.data.model.kml.PlacemarkDefault;
import org.geotoolkit.data.model.kml.Point;
import org.geotoolkit.data.model.kml.PointDefault;
import org.geotoolkit.data.model.kml.PolyStyle;
import org.geotoolkit.data.model.kml.PolyStyleDefault;
import org.geotoolkit.data.model.kml.Polygon;
import org.geotoolkit.data.model.kml.PolygonDefault;
import org.geotoolkit.data.model.kml.RefreshMode;
import org.geotoolkit.data.model.kml.Region;
import org.geotoolkit.data.model.kml.RegionDefault;
import org.geotoolkit.data.model.kml.ResourceMap;
import org.geotoolkit.data.model.kml.ResourceMapDefault;
import org.geotoolkit.data.model.kml.Scale;
import org.geotoolkit.data.model.kml.ScaleDefault;
import org.geotoolkit.data.model.kml.Schema;
import org.geotoolkit.data.model.kml.SchemaData;
import org.geotoolkit.data.model.kml.SchemaDataDefault;
import org.geotoolkit.data.model.kml.SchemaDefault;
import org.geotoolkit.data.model.kml.ScreenOverlay;
import org.geotoolkit.data.model.kml.ScreenOverlayDefault;
import org.geotoolkit.data.model.kml.Shape;
import org.geotoolkit.data.model.kml.SimpleData;
import org.geotoolkit.data.model.kml.SimpleDataDefault;
import org.geotoolkit.data.model.kml.SimpleField;
import org.geotoolkit.data.model.kml.SimpleFieldDefault;
import org.geotoolkit.data.model.kml.Style;
import org.geotoolkit.data.model.kml.StyleDefault;
import org.geotoolkit.data.model.kml.StyleMap;
import org.geotoolkit.data.model.kml.StyleMapDefault;
import org.geotoolkit.data.model.kml.StyleState;
import org.geotoolkit.data.model.kml.TimeSpan;
import org.geotoolkit.data.model.kml.TimeSpanDefault;
import org.geotoolkit.data.model.kml.TimeStamp;
import org.geotoolkit.data.model.kml.TimeStampDefault;
import org.geotoolkit.data.model.kml.Units;
import org.geotoolkit.data.model.kml.Vec2;
import org.geotoolkit.data.model.kml.Vec2Default;
import org.geotoolkit.data.model.kml.ViewRefreshMode;
import org.geotoolkit.data.model.kml.ViewVolume;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class KmlFactoryDefault implements KmlFactory{

    @Override
    public Kml createKml(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtensions,
            List<AbstractObject> kmlObjectExtensions){
        return new KmlDefault(networkLinkControl, abstractFeature, 
                kmlSimpleExtensions, kmlObjectExtensions);
    }

    @Override
    public Alias createAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String targetHref, String sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions){
        return new AliasDefault(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, aliasSimpleExtensions, aliasObjectExtensions);
    }

    @Override
    public Angle180 createAngle180(double angle) throws KmlException{
        return new Angle180Default(angle);
    }

    @Override
    public Anglepos180 createAnglepos180(double angle) throws KmlException{
        return new Anglepos180Default(angle);
    }

    @Override
    public Angle360 createAngle360(double angle) throws KmlException{
        return new Angle360Default(angle);
    }

    @Override
    public Angle90 createAngle90(double angle) throws KmlException{
        return new Angle90Default(angle);
    }

    @Override
    public AtomLink createAtomLinkDefault(String href, String rel, String type, String hreflang, String title, String length){
        return new AtomLinkDefault(href, rel, type, hreflang, title, length);
    }

    @Override
    public AtomPersonConstruct createAtomPersonConstruct(List<String> names, List<String> uris, List<String> emails){
        return new AtomPersonConstructDefault(names, uris, emails);
    }

    @Override
    public BalloonStyle createBalloonStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions){
        return new BalloonStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                bgColor, textColor, text, displayMode,
                balloonStyleSimpleExtensions, balloonStyleObjectExtensions);
    }

    @Override
    public BasicLink createBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions){
        return new BasicLinkDefault(objectSimpleExtensions, idAttributes, href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    @Override
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions){
        return new BoundaryDefault(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    @Override
    public Camera createCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions){
        return new CameraDefault(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    @Override
    public Color createColor(String color) throws KmlException{
        return new ColorDefault(color);
    }

    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return new CoordinateDefault(listCoordinates);
    }

    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude) {
        return new CoordinateDefault(geodeticLongiude, geodeticLatitude, altitude);
    }

    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude) {
        return new CoordinateDefault(geodeticLongiude, geodeticLatitude);
    }

    @Override
    public Coordinates createCoordinates(List<Coordinate> coordinates) {
        return new CoordinatesDefault(coordinates);
    }

    @Override
    public Data createData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions){
        return new DataDefault(objectSimpleExtensions, idAttributes,
                displayName, value, dataExtensions);
    }

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
        return new DocumentDefault(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                schemas, features, documentSimpleExtensions, documentObjectExtensions);
    }

    @Override
    public ExtendedData createExtendedData(List<Data> datas, List<SchemaData> schemaDatas, List<Object> anyOtherElements){
        return new ExtendedDataDefault(datas, schemaDatas, anyOtherElements);
    }

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
        return new FolderDefault(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions,
                features, folderSimpleExtensions, folderObjectExtensions);
    }

    @Override
    public GroundOverlay createGroundOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Link icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            double altitude, AltitudeMode altitudeMode, LatLonBox latLonBox,
            List<SimpleType> groundOverlaySimpleExtensions, List<AbstractObject> groundOverlayObjectExtensions){
        return new GroundOverlayDefault(objectSimpleExtensions, idAttributes,
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

    @Override
    public IconStyle createIconStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, Angle360 heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions){
        return new IconStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode,
                colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, heading, icon, hotSpot,
                iconStyleSimpleExtensions, iconStyleObjectExtensions);
    }
    
    @Override
    public IdAttributes createIdAttributes(String id, String targetId){
        return new IdAttributesDefault(id, targetId);
    }

    @Override
    public ItemIcon createItemIcon(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions){
        return new ItemIconDefault(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
    }

    @Override
    public LabelStyle createLabelStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions, List<AbstractObject> labelStyleObjectExtensions){
        return new LabelStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    @Override
    public LatLonBox createLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            Angle180 rotation, List<SimpleType> latLonBoxSimpleExtensions, List<AbstractObject> latLonBoxObjectExtensions){
        return new LatLonBoxDefault(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    @Override
    public LatLonAltBox createLatLonAltBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, List<AbstractObject> latLonAltBoxObjectExtensions){
        return new LatLonAltBoxDefault(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                minAltitude, maxAltitude, altitudeMode, latLonAltBoxSimpleExtensions, latLonAltBoxObjectExtensions);
    }

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
        return new LinearRingDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate,
                altitudeMode, coordinates,
                linearRingSimpleExtensions, linearRingObjectExtensions);
    }

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
        return new LineStringDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, coordinates,
                lineStringSimpleExtensions, lineStringObjectExtensions);
    }

    @Override
    public LineStyle createLineStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions){
        return new LineStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                width, lineStyleSimpleExtensions, lineStyleObjectExtensions);
    }
    
    @Override
    public Link createLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval, ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, List<AbstractObject> linkObjectExtensions){
        return new LinkDefault(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions, refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery, linkSimpleExtensions, linkObjectExtensions);
    }

    @Override
    public ListStyle createListStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, List<AbstractObject> listStyleObjectExtensions){
        return new ListStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                listItem, bgColor, itemIcons, maxSnippetLines,
                listStyleSimpleExtensions, listStyleObjectExtensions);
    }

    @Override
    public Location createLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle180 longitude, Angle90 latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        return new LocationDefault(objectSimpleExtensions, idAttributes,
                longitude, latitude, altitude,
                locationSimpleExtensions, locationObjectExtensions);
    }

    @Override
    public Lod createLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions){
        return new LodDefault(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    @Override
    public LookAt createLookAt(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions){
        return new LookAtDefault(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, range,
                lookAtSimpleExtensions, lookAtObjectExtensions);
    }

    @Override
    public Model createModel(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions, List<AbstractObject> modelObjectExtensions){
        return new ModelDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                altitudeMode, location, orientation, scale, link, resourceMap,
                modelSimpleExtensions, modelObjectExtensions);
    }

    @Override
    public MultiGeometry createMultiGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions){
        return new MultiGeometryDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                geometries, multiGeometrySimpleExtensions, multiGeometryObjectExtensions);
    }

    @Override
    public Orientation createOrientation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions){
        return new OrientationDefault(objectSimpleExtensions, idAttributes,
                heading, tilt, roll,
                orientationSimpleExtensions, orientationObjectExtensions);
    }

    @Override
    public Pair createPair(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, String styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions){
        return new PairDefault(objectSimpleExtensions, idAttributes,
                key, styleUrl, styleSelector,
                pairSimpleExtensions, pairObjectExtensions);
    }

    @Override
    public PhotoOverlay createPhotoOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Link icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            Angle180 rotation, ViewVolume viewVolume, ImagePyramid imagePyramid, Point point, Shape shape,
            List<SimpleType> photoOverlaySimpleExtensions, List<AbstractObject> photoOverlayObjectExtensions){
        return new PhotoOverlayDefault(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address,
                addressDetails, phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                color, drawOrder, icon,
                abstractOveraySimpleExtensions, abstractOverlayObjectExtensions,
                rotation, viewVolume, imagePyramid, point, shape,
                photoOverlaySimpleExtensions, photoOverlayObjectExtensions);
    }

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
        return new PlacemarkDefault(objectSimpleExtensions, idAttributes, name, visibility,
                open, author, link, address, addressDetails, phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                abstractGeometry,
                placemarkSimpleExtensions, placemarkObjectExtension);
    }

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
        return new PointDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
    }

    @Override
    public Polygon createPolygon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundaryIs, List<Boundary> innerBoundariesAre,
            List<SimpleType> polygonSimpleExtensions, List<AbstractObject> polygonObjectExtensions){
        return new PolygonDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, tessellate, altitudeMode, outerBoundaryIs, innerBoundariesAre,
                polygonSimpleExtensions, polygonObjectExtensions);
    }

    @Override
    public PolyStyle createPolyStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions, List<AbstractObject> polyStyleObjectExtensions){
        return new PolyStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                fill, outline, polyStyleSimpleExtensions, polyStyleObjectExtensions);
    }

    @Override
    public Region createRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, List<SimpleType> regionSimpleExtensions, List<AbstractObject> regionObjectExtentions){
        return new RegionDefault(objectSimpleExtensions, idAttributes,
                latLonAltBox, lod, regionSimpleExtensions, regionObjectExtentions);
    }

    @Override
    public ResourceMap createResourceMap(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions, List<AbstractObject> resourceMapObjectExtensions){
        return new ResourceMapDefault(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    @Override
    public Scale createScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions){
        return new ScaleDefault(objectSimpleExtensions, idAttributes, x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
    }

    @Override
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id){
        return new SchemaDefault(simpleFields, name, id);
    }

    @Override
    public SchemaData createSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions){
        return new SchemaDataDefault(objectSimpleExtensions, idAttributes,
                simpleDatas, schemaDataExtensions);
    }

    @Override
    public ScreenOverlay createScreenOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Link icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions,
            Vec2 overlayXY, Vec2 screenXY, Vec2 rotationXY, Vec2 size, Angle180 rotation,
            List<SimpleType> screenOverlaySimpleExtensions, List<AbstractObject> screenOverlayObjectExtensions){
        return new ScreenOverlayDefault(objectSimpleExtensions, idAttributes,
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
    public SimpleData createSimpleData(String name, String content){
        return new SimpleDataDefault(name, content);
    }

    @Override
    public SimpleField createSimpleField(String displayName, String type, String name){
        return new SimpleFieldDefault(displayName, type, name);
    }

    @Override
    public Style createStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle, PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions){
        return new StyleDefault(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    @Override
    public StyleMap createStyleMap(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions){
        return new StyleMapDefault(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                pairs, styleMapSimpleExtensions, styleMapObjectExtensions);
    }

    @Override
    public TimeSpan createTimeSpan(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String begin, String end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions){
        return new TimeSpanDefault(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                begin, end, timeSpanSimpleExtensions, timeSpanObjectExtensions);
    }

    @Override
    public TimeStamp createTimeStamp(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions){
        return new TimeStampDefault(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions,
                when, timeStampSimpleExtensions, timeStampObjectExtensions);
    }

    @Override
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit){
        return new Vec2Default(x, y, xUnit, yUnit);
    }
}
