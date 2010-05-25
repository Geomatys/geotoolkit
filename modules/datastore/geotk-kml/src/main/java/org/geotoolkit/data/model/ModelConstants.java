package org.geotoolkit.data.model;

import org.geotoolkit.data.model.kml.Angle360;
import org.geotoolkit.data.model.kml.ColorMode;

/**
 *
 * @author Samuel Andrés
 */
public final class ModelConstants {

    // NAMESPACES
    public static final String URI_KML = "http://www.opengis.net/kml/2.2";
    public static final String URI_XAL = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0";
    public static final String URI_ATOM = "http://www.w3.org/2005/Atom";
    public static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String PREFIX_XAL = "xal";
    public static final String PREFIX_ATOM = "atom";
    public static final String PREFIX_XSI = "xsi";

    public static final String TAG_KML = "kml";

    // TAGS NETWORK LINK CONTROL
    public static final String TAG_NETWORK_LINK_CONTROL = "NetworkLinkControl";

    // TAGS ABSTRACT FEATURE
    public static final String TAG_NETWORK_LINK = "NetworkLink";
    public static final String TAG_PLACEMARK = "Placemark";

    // TAGS ABSTRACT CONTAINER
    public static final String TAG_FOLDER = "Folder";
    public static final String TAG_DOCUMENT = "Document";

    // TAGS ABSTRACT OVERLAY
    public static final String TAG_GROUND_OVERLAY = "GroundOverlay";
    public static final String TAG_PHOTO_OVERLAY = "PhotoOverlay";
    public static final String TAG_SCREEN_OVERLAY = "ScreenOverlay";

    // TAGS ABSTRACT VIEW
    public static final String TAG_LOOK_AT = "LookAt";
    public static final String TAG_CAMERA = "Camera";

    // TAGS ABSTRACT TIME PRIMITIVE
    public static final String TAG_TIME_STAMP = "TimeStamp";
    public static final String TAG_TIME_SPAN = "TimeSpan";

    // TAGS ABSTRACT STYLE SELECTOR
    public static final String TAG_STYLE = "Style";
    public static final String TAG_STYLE_MAP = "StyleMap";

    // TAGS ABSTRACT GEOMETRY
    public static final String TAG_MULTI_GEOMETRY = "MultiGeometry";
    public static final String TAG_LINE_STRING = "LineString";
    public static final String TAG_POLYGON = "Polygon";
    public static final String TAG_POINT = "Point";
    public static final String TAG_LINEAR_RING = "LinearRing";
    public static final String TAG_MODEL = "Model";

    // TAGS ABSTRACT COLOR STYLE
    public static final String TAG_BALLOON_STYLE = "BalloonStyle";
    public static final String TAG_COLOR_MODE = "colorMode";
    public static final String TAG_DISPLAY_MODE = "displayMode";
    public static final String TAG_ICON_STYLE = "IconStyle";
    public static final String TAG_LABEL_STYLE = "LabelStyle";
    public static final String TAG_LINE_STYLE = "LineStyle";
    public static final String TAG_LIST_STYLE = "ListStyle";
    public static final String TAG_POLY_STYLE = "PolyStyle";

    // ELEMENTARY TAGS -- KML
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_ALIAS = "Alias";
    public static final String TAG_ALTITUDE = "altitude";
    public static final String TAG_ALTITUDE_MODE = "altitudeMode";
    public static final String TAG_BEGIN = "begin";
    public static final String TAG_BG_COLOR = "bgColor";
    public static final String TAG_COLOR = "color";
    public static final String TAG_COORDINATES = "coordinates";
    public static final String TAG_DATA = "Data";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_DISPLAY_NAME = "displayName";
    public static final String TAG_DRAW_ORDER = "drawOrder";
    public static final String TAG_EAST = "east";
    public static final String TAG_END = "end";
    public static final String TAG_EXTENDED_DATA = "ExtendedData";
    public static final String TAG_EXTRUDE = "extrude";
    public static final String TAG_FILL = "fill";
    public static final String TAG_HEADING = "heading";
    public static final String TAG_HOT_SPOT = "hotSpot";
    public static final String TAG_HREF = "href";
    public static final String TAG_HTTP_QUERY = "httpQuery";
    public static final String TAG_ICON = "Icon";
    public static final String TAG_INNER_BOUNDARY_IS = "innerBoundaryIs";
    public static final String TAG_ITEM_ICON = "ItemIcon";
    public static final String TAG_KEY = "key";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LAT_LON_BOX = "LatLonBox";
    public static final String TAG_LAT_LON_ALT_BOX = "LatLonAltBox";
    public static final String TAG_LINK = "Link";
    public static final String TAG_LIST_ITEM = "listItemType";
    public static final String TAG_LOCATION = "Location";
    public static final String TAG_LOD = "Lod";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_MAX_ALTITUDE = "maxAltitude";
    public static final String TAG_MAX_FADE_EXTENT = "maxFadeExtent";
    public static final String TAG_MAX_LOD_PIXELS = "maxLodPixels";
    public static final String TAG_MAX_SNIPPET_LINES = "maxSnippetLines";
    public static final String TAG_MIN_ALTITUDE = "minAltitude";
    public static final String TAG_MIN_FADE_EXTENT = "minFadeExtent";
    public static final String TAG_MIN_LOD_PIXELS = "minLodPixels";
    public static final String TAG_NAME = "name";
    public static final String TAG_NORTH = "north";
    public static final String TAG_OPEN = "open";
    public static final String TAG_ORIENTATION = "Orientation";
    public static final String TAG_OUTER_BOUNDARY_IS = "outerBoundaryIs";
    public static final String TAG_OUTLINE = "outline";
    public static final String TAG_OVERLAY_XY = "overlayXY";
    public static final String TAG_PAIR = "Pair";
    public static final String TAG_PHONE_NUMBER = "phoneNumber";
    public static final String TAG_RANGE = "range";
    public static final String TAG_REFRESH_INTERVAL = "refreshInterval";
    public static final String TAG_REFRESH_MODE = "refreshMode";
    public static final String TAG_REGION = "Region";
    public static final String TAG_RESOURCE_MAP = "ResourceMap";
    public static final String TAG_ROLL = "roll";
    public static final String TAG_ROTATION = "rotation";
    public static final String TAG_ROTATION_XY = "rotationXY";
    public static final String TAG_SCALE = "scale";
    public static final String TAG_SCALE_BIG = "Scale";
    public static final String TAG_SCHEMA = "Schema";
    public static final String TAG_SCREEN_XY = "screenXY";
    public static final String TAG_SIMPLE_FIELD = "SimpleField";
    public static final String TAG_SIZE = "size";
    public static final String TAG_SCHEMA_DATA = "SchemaData";
    public static final String TAG_SIMPLE_DATA = "SimpleData";
    public static final String TAG_SNIPPET = "snippet";
    public static final String TAG_SOURCE_HREF = "sourceHref";
    public static final String TAG_SOUTH = "south";
    public static final String TAG_STATE = "state";
    public static final String TAG_STYLE_URL = "styleUrl";
    public static final String TAG_TARGET_HREF = "targetHref";
    public static final String TAG_TESSELLATE = "tessellate";
    public static final String TAG_TEXT = "text";
    public static final String TAG_TEXT_COLOR = "textColor";
    public static final String TAG_TILT = "tilt";
    public static final String TAG_VALUE = "value";
    public static final String TAG_VIEW_FORMAT = "viewFormat";
    public static final String TAG_VIEW_BOUND_SCALE = "viewBoundScale";
    public static final String TAG_VIEW_REFRESH_MODE = "viewRefreshMode";
    public static final String TAG_VIEW_REFRESH_TIME = "viewRefreshTime";
    public static final String TAG_VISIBILITY = "visibility";
    public static final String TAG_WHEN = "when";
    public static final String TAG_WEST = "west";
    public static final String TAG_WIDTH = "width";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";

    // ELEMENTARY TAGS -- ATOM
    public static final String TAG_ATOM_LINK = "link";
    public static final String TAG_ATOM_PERSON_CONSTRUCT = "atomPersonConstruct";
    public static final String TAG_ATOM_NAME = "name";
    public static final String TAG_ATOM_URI = "uri";
    public static final String TAG_ATOM_EMAIL = "email";

    // ELEMENTARY TAGS -- ATOM
    public static final String TAG_XAL_ADDRESS_DETAILS = "AddressDetails";

    // ATTRIBUTES
    public static final String ATT_ID = "id";
    public static final String ATT_NAME = "name";
    public static final String ATT_TARGET_ID = "targetId";
    public static final String ATT_TYPE = "type";

    // ATTRIBUTES VEC2
    public static final String ATT_X = "x";
    public static final String ATT_Y = "y";
    public static final String ATT_XUNITS = "xunits";
    public static final String ATT_YUNITS = "yunits";
}
