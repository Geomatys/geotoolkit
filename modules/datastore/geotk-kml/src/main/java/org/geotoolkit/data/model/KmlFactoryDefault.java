package org.geotoolkit.data.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomLinkDefault;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomPersonConstructDefault;
import org.geotoolkit.data.model.kml.AbstractContainer;
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
import org.geotoolkit.data.model.kml.Change;
import org.geotoolkit.data.model.kml.ChangeDefault;
import org.geotoolkit.data.model.kml.Color;
import org.geotoolkit.data.model.kml.ColorDefault;
import org.geotoolkit.data.model.kml.ColorMode;
import org.geotoolkit.data.model.kml.Coordinate;
import org.geotoolkit.data.model.kml.CoordinateDefault;
import org.geotoolkit.data.model.kml.Coordinates;
import org.geotoolkit.data.model.kml.CoordinatesDefault;
import org.geotoolkit.data.model.kml.Create;
import org.geotoolkit.data.model.kml.CreateDefault;
import org.geotoolkit.data.model.kml.Data;
import org.geotoolkit.data.model.kml.DataDefault;
import org.geotoolkit.data.model.kml.Delete;
import org.geotoolkit.data.model.kml.DeleteDefault;
import org.geotoolkit.data.model.kml.DisplayMode;
import org.geotoolkit.data.model.kml.Document;
import org.geotoolkit.data.model.kml.DocumentDefault;
import org.geotoolkit.data.model.kml.ExtendedData;
import org.geotoolkit.data.model.kml.ExtendedDataDefault;
import org.geotoolkit.data.model.kml.Folder;
import org.geotoolkit.data.model.kml.FolderDefault;
import org.geotoolkit.data.model.kml.GridOrigin;
import org.geotoolkit.data.model.kml.GroundOverlay;
import org.geotoolkit.data.model.kml.GroundOverlayDefault;
import org.geotoolkit.data.model.kml.Icon;
import org.geotoolkit.data.model.kml.IconDefault;
import org.geotoolkit.data.model.kml.IconStyle;
import org.geotoolkit.data.model.kml.IconStyleDefault;
import org.geotoolkit.data.model.kml.IdAttributes;
import org.geotoolkit.data.model.kml.IdAttributesDefault;
import org.geotoolkit.data.model.kml.ImagePyramid;
import org.geotoolkit.data.model.kml.ImagePyramidDefault;
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
import org.geotoolkit.data.model.kml.NetworkLink;
import org.geotoolkit.data.model.kml.NetworkLinkControl;
import org.geotoolkit.data.model.kml.NetworkLinkControlDefault;
import org.geotoolkit.data.model.kml.NetworkLinkDefault;
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
import org.geotoolkit.data.model.kml.Snippet;
import org.geotoolkit.data.model.kml.SnippetDefault;
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
import org.geotoolkit.data.model.kml.Update;
import org.geotoolkit.data.model.kml.UpdateDefault;
import org.geotoolkit.data.model.kml.Vec2;
import org.geotoolkit.data.model.kml.Vec2Default;
import org.geotoolkit.data.model.kml.ViewRefreshMode;
import org.geotoolkit.data.model.kml.ViewVolume;
import org.geotoolkit.data.model.kml.ViewVolumeDefault;
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
        return new KmlDefault(networkLinkControl, abstractFeature, 
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
        return new AliasDefault(objectSimpleExtensions, idAttributes,
                targetHref, sourceHref, aliasSimpleExtensions, aliasObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Angle180 createAngle180(double angle) throws KmlException{
        return new Angle180Default(angle);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Anglepos180 createAnglepos180(double angle) throws KmlException{
        return new Anglepos180Default(angle);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Angle360 createAngle360(double angle) throws KmlException{
        return new Angle360Default(angle);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Angle90 createAngle90(double angle) throws KmlException{
        return new Angle90Default(angle);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public AtomLink createAtomLinkDefault(String href, String rel, String type, String hreflang, String title, String length){
        return new AtomLinkDefault(href, rel, type, hreflang, title, length);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct createAtomPersonConstruct(List<String> names, List<String> uris, List<String> emails){
        return new AtomPersonConstructDefault(names, uris, emails);
    }

    /**
     * @{@inheritDoc }
     */
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

    /**
     * @{@inheritDoc }
     */
    @Override
    public BasicLink createBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions){
        return new BasicLinkDefault(objectSimpleExtensions, idAttributes, href, basicLinkSimpleExtensions, basicLinkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions){
        return new BoundaryDefault(linearRing, boundarySimpleExtensions, boundaryObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Camera createCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            Angle180 longitude, Angle90 latitude, double altitude,
            Angle360 heading, Anglepos180 tilt, Angle180 roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions){
        return new CameraDefault(objectSimpleExtensions, idAttributes,
                abstractViewSimpleExtensions, abstractViewObjectExtensions,
                longitude, latitude, altitude, heading, tilt, roll, altitudeMode,
                cameraSimpleExtensions, cameraObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Change createChange(List<AbstractObject> objects){
        return new ChangeDefault(objects);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Color createColor(String color) throws KmlException{
        return new ColorDefault(color);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(String listCoordinates) {
        return new CoordinateDefault(listCoordinates);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude) {
        return new CoordinateDefault(geodeticLongiude, geodeticLatitude, altitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude) {
        return new CoordinateDefault(geodeticLongiude, geodeticLatitude);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Coordinates createCoordinates(List<Coordinate> coordinates) {
        return new CoordinatesDefault(coordinates);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Create createCreate(List<AbstractContainer> containers){
        return new CreateDefault(containers);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Data createData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions){
        return new DataDefault(objectSimpleExtensions, idAttributes,
                displayName, value, dataExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Delete createDelete(List<AbstractFeature> features){
        return new DeleteDefault(features);
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
        return new DocumentDefault(objectSimpleExtensions, idAttributes,
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
        return new ExtendedDataDefault(datas, schemaDatas, anyOtherElements);
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
        return new FolderDefault(objectSimpleExtensions, idAttributes,
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

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public Icon createIcon(Link link){
        return new IconDefault(link);
    }

    /**
     * @{@inheritDoc }
     */
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

    /**
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes createIdAttributes(String id, String targetId){
        return new IdAttributesDefault(id, targetId);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ImagePyramid createImagePyramid(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions){
        return new ImagePyramidDefault(objectSimpleExtensions, idAttributes,
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
        return new ItemIconDefault(objectSimpleExtensions, idAttributes,
                states, href, itemIconSimpleExtensions, itemIconObjectExtensions);
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
        return new LabelStyleDefault(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions,
                scale, labelStyleSimpleExtensions, labelStyleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public LatLonBox createLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 north, Angle180 south, Angle180 east, Angle180 west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            Angle180 rotation, List<SimpleType> latLonBoxSimpleExtensions, List<AbstractObject> latLonBoxObjectExtensions){
        return new LatLonBoxDefault(objectSimpleExtensions, idAttributes,
                north, south, east, west, abstractLatLonBoxSimpleExtensions, abstractLatLonBoxObjectExtensions,
                rotation, latLonBoxSimpleExtensions, latLonBoxObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
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
        return new LinearRingDefault(objectSimpleExtensions, idAttributes,
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
        return new LineStringDefault(objectSimpleExtensions, idAttributes,
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
        return new LineStyleDefault(objectSimpleExtensions, idAttributes,
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
        return new LinkDefault(objectSimpleExtensions, idAttributes,
                href, basicLinkSimpleExtensions, basicLinkObjectExtensions, refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery, linkSimpleExtensions, linkObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
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

    /**
     * @{@inheritDoc }
     */
    @Override
    public Location createLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            Angle180 longitude, Angle90 latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions){
        return new LocationDefault(objectSimpleExtensions, idAttributes,
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
        return new LodDefault(objectSimpleExtensions, idAttributes,
                minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent,
                lodSimpleExtentions, lodObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
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
        return new ModelDefault(objectSimpleExtensions, idAttributes,
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
        return new MultiGeometryDefault(objectSimpleExtensions, idAttributes,
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
        return new NetworkLinkDefault(objectSimpleExtensions, idAttributes, name, visibility, open, author, atomLink,
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
        return new NetworkLinkControlDefault(minRefreshPeriod, maxSessionLength,
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
            Angle360 heading, Anglepos180 tilt, Angle180 roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions){
        return new OrientationDefault(objectSimpleExtensions, idAttributes,
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
        return new PairDefault(objectSimpleExtensions, idAttributes,
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
        return new PlacemarkDefault(objectSimpleExtensions, idAttributes, name, visibility,
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
        return new PointDefault(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions,
                extrude, altitudeMode, coordinates, pointSimpleExtensions, pointObjectExtensions);
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
        return new PolygonDefault(objectSimpleExtensions, idAttributes,
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
        return new PolyStyleDefault(objectSimpleExtensions, idAttributes,
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
        return new RegionDefault(objectSimpleExtensions, idAttributes,
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
        return new ResourceMapDefault(objectSimpleExtensions, idAttributes,
                aliases, resourceMapSimpleExtensions, resourceMapObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Scale createScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions){
        return new ScaleDefault(objectSimpleExtensions, idAttributes, x, y, z, scaleSimpleExtensions, scaleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id){
        return new SchemaDefault(simpleFields, name, id);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SchemaData createSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions){
        return new SchemaDataDefault(objectSimpleExtensions, idAttributes,
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

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleData createSimpleData(String name, String content){
        return new SimpleDataDefault(name, content);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public SimpleField createSimpleField(String displayName, String type, String name){
        return new SimpleFieldDefault(displayName, type, name);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Snippet createSnippet(int maxLines, String content){
        return new SnippetDefault(maxLines, content);
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
        return new StyleDefault(objectSimpleExtensions, idAttributes,
                abstractStyleSelectorSimpleExtensions, abstractStyleSelectorObjectExtensions,
                iconStyle, labelStyle, lineStyle, polyStyle, balloonStyle, listStyle,
                styleSimpleExtensions, styleObjectExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public StyleMap createStyleMap(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions){
        return new StyleMapDefault(objectSimpleExtensions, idAttributes,
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
        return new TimeSpanDefault(objectSimpleExtensions, idAttributes,
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
        return new TimeStampDefault(objectSimpleExtensions, idAttributes,
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
        return new UpdateDefault(creates, deletes, changes, updateOpExtensions, updateExtensions);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit){
        return new Vec2Default(x, y, xUnit, yUnit);
    }

    /**
     * @{@inheritDoc }
     */
    @Override
    public ViewVolume createViewVolume(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            Angle180 leftFov, Angle180 rightFov, Angle90 bottomFov, Angle90 topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions){
        return new ViewVolumeDefault(objectSimpleExtensions, idAttributes,
                leftFov, rightFov, bottomFov, topFov, near,
                viewVolumeSimpleExtensions, viewVolumeObjectExtensions);
    }

    
    /*
     * Utilities to initialize default values.
     */

    /**
     * <p>This method creates a default Color.</p>
     *
     * <p>Use the appropriate OPAQUE_BLACK or OPAQUE_BLACK
     * values to obtain a default color.</p>
     *
     * @param flag
     * @return
     */
    public static Color createColor(int flag){
        Color color = null;
        try {
            if (flag == 0) {
                color = new ColorDefault("ff000000");
            } else if (flag == 1) {
                color = new ColorDefault("ffffffff");
            }
        } catch (KmlException ex) {
                Logger.getLogger(KmlFactoryDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        return color;
    }

    /**
     * <p>This method creates a default Angle180.</p>
     *
     * <p>Use the appropriate ANGLE_MIN, ANGLE_ZERO or ANGLE_MAX
     * values to obtain a default angle.</p>
     *
     * @param flag
     * @return
     */
    public static Angle180 createAngle180(String flag){
        Angle180 angle = null;
        try {
            if ("max".equals(flag)) {
                angle = new Angle180Default(180.0);
            } else if ("zero".equals(flag)) {
                angle = new Angle180Default(0.0);
            } else if ("min".equals(flag)) {
                angle = new Angle180Default(-180.0);
            }

        } catch (KmlException ex) {
            Logger.getLogger(KmlFactoryDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        return angle;
    }

    /**
     * <p>This method creates a default Anglepos180.</p>
     *
     * <p>Use the appropriate ANGLE_MIN, ANGLE_ZERO or ANGLE_MAX
     * values to obtain a default angle.</p>
     *
     * @param flag
     * @return
     */
    public static Anglepos180 createAnglepos180(String flag){
        Anglepos180 angle = null;
        try {
            if ("zero".equals(flag)) {
                angle = new Anglepos180Default(0.0);
            }
        } catch (KmlException ex) {
            Logger.getLogger(KmlFactoryDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        return angle;
    }

    /**
     * <p>This method creates a default Anglepos180.</p>
     *
     * <p>Use the appropriate ANGLE_MIN, ANGLE_ZERO or ANGLE_MAX
     * values to obtain a default angle.</p>
     *
     * @param flag
     * @return
     */
    public static Angle360 createAngle360(String flag){
        Angle360 angle = null;
        try {
            if ("max".equals(flag)) {
                angle = new Angle360Default(360.0);
            } else if ("zero".equals(flag)) {
                angle = new Angle360Default(0.0);
            } else if ("min".equals(flag)) {
                angle = new Angle360Default(-360.0);
            }

        } catch (KmlException ex) {
            Logger.getLogger(KmlFactoryDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        return angle;
    }

    /**
     * <p>This method creates a default Anglepos180.</p>
     *
     * <p>Use the appropriate ANGLE_MIN, ANGLE_ZERO or ANGLE_MAX
     * values to obtain a default angle.</p>
     *
     * @param flag
     * @return
     */
    public static Angle90 createAngle90(String flag){
        Angle90 angle = null;
        try {
            if ("max".equals(flag)) {
                angle = new Angle90Default(90.0);
            } else if ("zero".equals(flag)) {
                angle = new Angle90Default(0.0);
            } else if ("min".equals(flag)) {
                angle = new Angle90Default(-90.0);
            }

        } catch (KmlException ex) {
            Logger.getLogger(KmlFactoryDefault.class.getName()).log(Level.SEVERE, null, ex);
        }
        return angle;
    }
}
