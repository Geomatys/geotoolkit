package org.geotoolkit.data.kml;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.kml.model.AbstractContainer;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractObject;
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
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.DisplayMode;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.GridOrigin;
import org.geotoolkit.data.kml.model.GroundOverlay;
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
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.NetworkLink;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.PhotoOverlay;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.ScreenOverlay;
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
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface recapitulates the methods allowing
 * to create objects mapping kml 2.2 elements.</p>
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
     * @return The Kml object.
     */
    public Kml createKml(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtension,
            List<AbstractObject> kmlObjectExtensions);

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
    public Alias createAlias(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String targetHref, String sourceHref,
            List<SimpleType> aliasSimpleExtensions, List<AbstractObject> aliasObjectExtensions);

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
    public BalloonStyle createBalloonStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     * @return
     */
    public BasicLink createBasicLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions);

    /**
     * 
     * @return
     */
    public BasicLink createBasicLink();

    /**
     *
     * @param linearRing
     * @param boundarySimpleExtensions
     * @param boundaryObjectExtensions
     * @return
     */
    public Boundary createBoundary(LinearRing linearRing,
            List<SimpleType> boundarySimpleExtensions, List<AbstractObject> boundaryObjectExtensions);

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
    public Camera createCamera(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double roll, AltitudeMode altitudeMode,
            List<SimpleType> cameraSimpleExtensions, List<AbstractObject> cameraObjectExtensions);

    /**
     * 
     * @return
     */
    public Camera createCamera();

    /**
     *
     * @param objects
     * @return
     */
    public Change createChange(List<AbstractObject> objects);

    /**
     *
     * @param listCoordinates
     * @return
     */
    public Coordinate createCoordinate(String listCoordinates);

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     * @param altitude
     * @return
     */
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude);

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     * @return
     */
    public Coordinate createCoordinate(double geodeticLongiude, double geodeticLatitude);

    /**
     *
     * @param coordinates
     * @return
     */
    public Coordinates createCoordinates(List<Coordinate> coordinates);

    /**
     *
     * @param containers
     * @return
     */
    public Create createCreate(List<AbstractContainer> containers);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param displayName
     * @param value
     * @param dataExtensions
     * @return
     */
    public Data createData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions);

    /**
     *
     * @param features
     * @return
     */
    public Delete createDelete(List<AbstractFeature> features);

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
            List<AbstractObject> documentObjectExtensions);

    /**
     *
     * @param datas
     * @param schemaDatas
     * @param anyOtherElements
     * @return
     */
    public ExtendedData createExtendedData(List<Data> datas, List<SchemaData> schemaDatas, List<Object> anyOtherElements);

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
            List<AbstractObject> folderObjectExtensions);

    /**
     * 
     * @return
     */
    public Folder createFolder();

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
            List<SimpleType> groundOverlaySimpleExtensions, List<AbstractObject> groundOverlayObjectExtensions);

    /**
     * 
     * @return
     */
    public GroundOverlay createGroundOverlay();

    /**
     * 
     * @param link
     * @return
     */
    public Icon createIcon(Link link);

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
    public IconStyle createIconStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions);

    /**
     * 
     * @return
     */
    public IconStyle createIconStyle();

    /**
     *
     * @param id
     * @param targetId
     * @return
     */
    public IdAttributes createIdAttributes(String id, String targetId);

    /**
     * 
     * @return
     */
    public IdAttributes createIdAttributes();

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
    public ImagePyramid createImagePyramid(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            int titleSize, int maxWidth, int maxHeight, GridOrigin gridOrigin,
            List<SimpleType> imagePyramidSimpleExtensions, List<AbstractObject> imagePyramidObjectExtensions);

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
    public ItemIcon createItemIcon(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<ItemIconState> states, String href,
            List<SimpleType> itemIconSimpleExtensions, List<AbstractObject> itemIconObjectExtensions);

    /**
     * 
     * @return
     */
    public ItemIcon createItemIcon();

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
    public LabelStyle createLabelStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale,
            List<SimpleType> labelStyleSimpleExtensions, List<AbstractObject> labelStyleObjectExtensions);

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
    public LatLonBox createLatLonBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double rotation, List<SimpleType> latLonBoxSimpleExtensions, List<AbstractObject> latLonBoxObjectExtensions);

    /**
     * 
     * @return
     */
    public LatLonBox createLatLonBox();

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
    public LatLonAltBox createLatLonAltBox(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double north, double south, double east, double west,
            List<SimpleType> abstractLatLonBoxSimpleExtensions, List<AbstractObject> abstractLatLonBoxObjectExtensions,
            double minAltitude, double maxAltitude, AltitudeMode altitudeMode,
            List<SimpleType> latLonAltBoxSimpleExtensions, List<AbstractObject> latLonAltBoxObjectExtensions);

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
    public LinearRing createLinearRing(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> linearRingSimpleExtensions,
            List<AbstractObject> linearRingObjectExtensions);

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
    public LineString createLineString(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> lineStringSimpleExtensions,
            List<AbstractObject> lineStringObjectExtensions);

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
    public LineStyle createLineStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions);

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
    public Link createLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String href, List<SimpleType> basicLinkSimpleExtensions, List<AbstractObject> basicLinkObjectExtensions,
            RefreshMode refreshMode, double refreshInterval, ViewRefreshMode viewRefreshMode, double viewRefreshTime,
            double viewBoundScale, String viewFormat, String httpQuery,
            List<SimpleType> linkSimpleExtensions, List<AbstractObject> linkObjectExtensions);

    /**
     * 
     * @return
     */
    public Link createLink();

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
    public ListStyle createListStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, List<AbstractObject> listStyleObjectExtensions);

    /**
     * 
     * @return
     */
    public ListStyle createListStyle();

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
    public Location createLocation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double longitude, double latitude, double altitude,
            List<SimpleType> locationSimpleExtensions, List<AbstractObject> locationObjectExtensions);

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
    public Lod createLod(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double minLodPixels, double maxLodPixels, double minFadeExtent, double maxFadeExtent,
            List<SimpleType> lodSimpleExtentions, List<AbstractObject> lodObjectExtensions);

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
    public LookAt createLookAt(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions, List<AbstractObject> abstractViewObjectExtensions,
            double longitude, double latitude, double altitude,
            double heading, double tilt, double range,
            List<SimpleType> lookAtSimpleExtensions, List<AbstractObject> lookAtObjectExtensions);

    /**
     * 
     * @return
     */
    public LookAt createLookAt();

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
    public Model createModel(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            AltitudeMode altitudeMode, Location location, Orientation orientation, Scale scale, Link link, ResourceMap resourceMap,
            List<SimpleType> modelSimpleExtensions, List<AbstractObject> modelObjectExtensions);

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
    public MultiGeometry createMultiGeometry(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            List<AbstractGeometry> geometries,
            List<SimpleType> multiGeometrySimpleExtensions,
            List<AbstractObject> multiGeometryObjectExtensions);

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
     public NetworkLink createNetworkLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions, List<AbstractObject> networkLinkObjectExtensions);

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
    public NetworkLinkControl createNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName, String linkDescription,
            Snippet linkSnippet, String expire, Update update, AbstractView view,
            List<SimpleType> networkLinkControlSimpleExtensions, List<AbstractObject> networkLinkControlObjectExtensions);

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
    public Orientation createOrientation(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            double heading, double tilt, double roll,
            List<SimpleType> orientationSimpleExtensions,
            List<AbstractObject> orientationObjectExtensions);

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
    public Pair createPair(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            StyleState key, String styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleType> pairSimpleExtensions,
            List<AbstractObject> pairObjectExtensions);

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
            List<SimpleType> photoOverlaySimpleExtensions, List<AbstractObject> photoOverlayObjectExtensions);

    /**
     * 
     * @return
     */
    public PhotoOverlay createPhotoOverlay();

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
            List<AbstractObject> placemarkObjectExtension);

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
    public Point createPoint(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions);

    /**
     * 
     * @return
     */
    public Point createPoint();

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param outerBoundaryIs
     * @param innerBoundariesAre
     * @param polygonSimpleExtensions
     * @param polygonObjectExtensions
     * @return
     */
    public Polygon createPolygon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundaryIs, List<Boundary> innerBoundariesAre,
            List<SimpleType> polygonSimpleExtensions, List<AbstractObject> polygonObjectExtensions);

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
    public PolyStyle createPolyStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            boolean fill, boolean outline,
            List<SimpleType> polyStyleSimpleExtensions, List<AbstractObject> polyStyleObjectExtensions);

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
    public Region createRegion(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            LatLonAltBox latLonAltBox, Lod lod, List<SimpleType> regionSimpleExtensions, List<AbstractObject> regionObjectExtentions);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param aliases
     * @param resourceMapSimpleExtensions
     * @param resourceMapObjectExtensions
     * @return
     */
    public ResourceMap createResourceMap(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleType> resourceMapSimpleExtensions, List<AbstractObject> resourceMapObjectExtensions);

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
    public Scale createScale(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, double x, double y, double z,
            List<SimpleType> scaleSimpleExtensions, List<AbstractObject> scaleObjectExtensions);

    /**
     *
     * @param simpleFields
     * @param name
     * @param id
     * @return
     */
    public Schema createSchema(List<SimpleField> simpleFields,
            String name, String id);

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param simpleDatas
     * @param schemaDataExtensions
     * @return
     */
    public SchemaData createSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions);

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
            List<SimpleType> screenOverlaySimpleExtensions, List<AbstractObject> screenOverlayObjectExtensions);

    /**
     *
     * @return
     */
    public ScreenOverlay createScreenOverlay();
    /**
     *
     * @param name
     * @param content
     * @return
     */
    public SimpleData createSimpleData(String name, String content);

    /**
     *
     * @param displayName
     * @param type
     * @param name
     * @return
     */
    public SimpleField createSimpleField(String displayName, String type, String name);

    /**
     *
     * @param maxLines
     * @param content
     * @return
     */
    public Snippet createSnippet(int maxLines, String content);

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
    public Style createStyle(List<SimpleType> objectSimpleExtensions,
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
    public Style createStyle();

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
    public StyleMap createStyleMap(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            List<Pair> pairs, List<SimpleType> styleMapSimpleExtensions, List<AbstractObject> styleMapObjectExtensions);

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
    public TimeSpan createTimeSpan(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String begin, String end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions);

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
    public TimeStamp createTimeStamp(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions);

    /**
     *
     * @param creates
     * @param deletes
     * @param changes
     * @param updateOpExtensions
     * @param updateExtensions
     * @return
     */
    public Update createUpdate(List<Create> creates,
            List<Delete> deletes, List<Change> changes,
            List<Object> updateOpExtensions, List<Object> updateExtensions);

    /**
     *
     * @param x
     * @param y
     * @param xUnit
     * @param yUnit
     * @return
     */
    public Vec2 createVec2(double x, double y, Units xUnit, Units yUnit);

    /**
     * 
     * @return
     */
    public Vec2 createVec2();

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param leftFov
     * @param rightFov
     * @param bottomFov
     * @param topFov
     * @param viewVolumeSimpleExtensions
     * @param viewVolumeObjectExtensions
     * @return
     */
    public ViewVolume createViewVolume(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            double leftFov, double rightFov, double bottomFov, double topFov, double near,
            List<SimpleType> viewVolumeSimpleExtensions, List<AbstractObject> viewVolumeObjectExtensions);

    /**
     * 
     * @return
     */
    public ViewVolume createViewVolume();
}
