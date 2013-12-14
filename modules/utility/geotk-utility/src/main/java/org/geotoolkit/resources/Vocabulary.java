/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.resources.IndexedResourceBundle;


/**
 * Locale-dependent resources for words or simple sentences.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public final class Vocabulary extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.2
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * About
         */
        public static final short ABOUT = 0;

        /**
         * Abridged Molodensky transform
         */
        public static final short ABRIDGED_MOLODENSKY_TRANSFORM = 1;

        /**
         * Add
         */
        public static final short ADD = 2;

        /**
         * Administrator
         */
        public static final short ADMINISTRATOR = 3;

        /**
         * Affine transform
         */
        public static final short AFFINE_TRANSFORM = 4;

        /**
         * Albers Equal Area projection
         */
        public static final short ALBERS_EQUAL_AREA_PROJECTION = 5;

        /**
         * Alias
         */
        public static final short ALIAS = 6;

        /**
         * All
         */
        public static final short ALL = 7;

        /**
         * All users
         */
        public static final short ALL_USERS = 8;

        /**
         * Altitude
         */
        public static final short ALTITUDE = 9;

        /**
         * Altitudes
         */
        public static final short ALTITUDES = 10;

        /**
         * Apply
         */
        public static final short APPLY = 11;

        /**
         * Attribute
         */
        public static final short ATTRIBUTE = 12;

        /**
         * Authority
         */
        public static final short AUTHORITY = 13;

        /**
         * Automatic
         */
        public static final short AUTOMATIC = 14;

        /**
         * {0} axis
         */
        public static final short AXIS_1 = 15;

        /**
         * Axis changes
         */
        public static final short AXIS_CHANGES = 16;

        /**
         * Azimuth
         */
        public static final short AZIMUTH = 17;

        /**
         * Band
         */
        public static final short BAND = 18;

        /**
         * Band {0}
         */
        public static final short BAND_1 = 19;

        /**
         * Barometric altitude
         */
        public static final short BAROMETRIC_ALTITUDE = 20;

        /**
         * Black
         */
        public static final short BLACK = 21;

        /**
         * Blue
         */
        public static final short BLUE = 22;

        /**
         * Cancel
         */
        public static final short CANCEL = 23;

        /**
         * Cartesian
         */
        public static final short CARTESIAN = 24;

        /**
         * Cartesian 2D
         */
        public static final short CARTESIAN_2D = 25;

        /**
         * Cartesian 3D
         */
        public static final short CARTESIAN_3D = 26;

        /**
         * Cassini-Soldner projection
         */
        public static final short CASSINI_SOLDNER_PROJECTION = 27;

        /**
         * Category
         */
        public static final short CATEGORY = 28;

        /**
         * Choose
         */
        public static final short CHOOSE = 29;

        /**
         * Class
         */
        public static final short CLASS = 30;

        /**
         * Classic
         */
        public static final short CLASSIC = 31;

        /**
         * Close
         */
        public static final short CLOSE = 32;

        /**
         * Code
         */
        public static final short CODE = 33;

        /**
         * Colors
         */
        public static final short COLORS = 34;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final short COLOR_COUNT_1 = 35;

        /**
         * Color model
         */
        public static final short COLOR_MODEL = 36;

        /**
         * Color space
         */
        public static final short COLOR_SPACE = 37;

        /**
         * Column
         */
        public static final short COLUMN = 38;

        /**
         * Columns
         */
        public static final short COLUMNS = 39;

        /**
         * Commands
         */
        public static final short COMMANDS = 40;

        /**
         * Compare with
         */
        public static final short COMPARE_WITH = 41;

        /**
         * Completed
         */
        public static final short COMPLETED = 42;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final short COMPONENT_COUNT_1 = 43;

        /**
         * Configure
         */
        public static final short CONFIGURE = 44;

        /**
         * Confirm
         */
        public static final short CONFIRM = 45;

        /**
         * Connection parameters
         */
        public static final short CONNECTION_PARAMETERS = 46;

        /**
         * Conversion
         */
        public static final short CONVERSION = 47;

        /**
         * Conversion and transformation
         */
        public static final short CONVERSION_AND_TRANSFORMATION = 48;

        /**
         * Coordinates selection
         */
        public static final short COORDINATES_SELECTION = 49;

        /**
         * Coordinate format
         */
        public static final short COORDINATE_FORMAT = 50;

        /**
         * Coordinate Reference System
         */
        public static final short COORDINATE_REFERENCE_SYSTEM = 51;

        /**
         * Current user
         */
        public static final short CURRENT_USER = 52;

        /**
         * Cyan
         */
        public static final short CYAN = 53;

        /**
         * Cylindrical Mercator projection
         */
        public static final short CYLINDRICAL_MERCATOR_PROJECTION = 54;

        /**
         * Data
         */
        public static final short DATA = 55;

        /**
         * Database
         */
        public static final short DATABASE = 56;

        /**
         * Database engine
         */
        public static final short DATABASE_ENGINE = 57;

        /**
         * Database URL
         */
        public static final short DATABASE_URL = 58;

        /**
         * {0} data
         */
        public static final short DATA_1 = 59;

        /**
         * Data are present
         */
        public static final short DATA_ARE_PRESENT = 60;

        /**
         * {0} data base
         */
        public static final short DATA_BASE_1 = 61;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final short DATA_BASE_3 = 62;

        /**
         * Data type
         */
        public static final short DATA_TYPE = 63;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final short DATA_TYPE_2 = 64;

        /**
         * Datum
         */
        public static final short DATUM = 65;

        /**
         * Datum shift
         */
        public static final short DATUM_SHIFT = 66;

        /**
         * day
         */
        public static final short DAY = 67;

        /**
         * days
         */
        public static final short DAYS = 68;

        /**
         * Debug
         */
        public static final short DEBUG = 69;

        /**
         * Decoders
         */
        public static final short DECODERS = 70;

        /**
         * Default
         */
        public static final short DEFAULT = 71;

        /**
         * Default value
         */
        public static final short DEFAULT_VALUE = 72;

        /**
         * Define
         */
        public static final short DEFINE = 73;

        /**
         * Delete
         */
        public static final short DELETE = 74;

        /**
         * Depth
         */
        public static final short DEPTH = 75;

        /**
         * Derived from {0}
         */
        public static final short DERIVED_FROM_1 = 76;

        /**
         * Description
         */
        public static final short DESCRIPTION = 77;

        /**
         * Directories
         */
        public static final short DIRECTORIES = 78;

        /**
         * Discontinuous
         */
        public static final short DISCONTINUOUS = 79;

        /**
         * Display
         */
        public static final short DISPLAY = 80;

        /**
         * Distance
         */
        public static final short DISTANCE = 81;

        /**
         * Domain
         */
        public static final short DOMAIN = 82;

        /**
         * Down
         */
        public static final short DOWN = 83;

        /**
         * Download
         */
        public static final short DOWNLOAD = 84;

        /**
         * Downloading
         */
        public static final short DOWNLOADING = 85;

        /**
         * Dublin Julian
         */
        public static final short DUBLIN_JULIAN = 86;

        /**
         * Duplicated value
         */
        public static final short DUPLICATED_VALUE = 87;

        /**
         * Duration
         */
        public static final short DURATION = 88;

        /**
         * Earth gravitational model
         */
        public static final short EARTH_GRAVITATIONAL_MODEL = 89;

        /**
         * East
         */
        public static final short EAST = 90;

        /**
         * Easting
         */
        public static final short EASTING = 91;

        /**
         * Edit
         */
        public static final short EDIT = 92;

        /**
         * Efficiency
         */
        public static final short EFFICIENCY = 93;

        /**
         * Ellipsoid
         */
        public static final short ELLIPSOID = 94;

        /**
         * Ellipsoidal
         */
        public static final short ELLIPSOIDAL = 95;

        /**
         * Ellipsoidal height
         */
        public static final short ELLIPSOIDAL_HEIGHT = 96;

        /**
         * Ellipsoid shift
         */
        public static final short ELLIPSOID_SHIFT = 97;

        /**
         * Empty
         */
        public static final short EMPTY = 98;

        /**
         * Encoders
         */
        public static final short ENCODERS = 99;

        /**
         * End time
         */
        public static final short END_TIME = 100;

        /**
         * Equidistant cylindrical projection
         */
        public static final short EQUIDISTANT_CYLINDRICAL_PROJECTION = 101;

        /**
         * Error
         */
        public static final short ERROR = 102;

        /**
         * Error - {0}
         */
        public static final short ERROR_1 = 103;

        /**
         * Error filters
         */
        public static final short ERROR_FILTERS = 104;

        /**
         * Event logger
         */
        public static final short EVENT_LOGGER = 105;

        /**
         * Examples
         */
        public static final short EXAMPLES = 106;

        /**
         * Exception
         */
        public static final short EXCEPTION = 107;

        /**
         * Exponential
         */
        public static final short EXPONENTIAL = 108;

        /**
         * Factory
         */
        public static final short FACTORY = 109;

        /**
         * False
         */
        public static final short FALSE = 110;

        /**
         * File
         */
        public static final short FILE = 111;

        /**
         * {0} files
         */
        public static final short FILES_1 = 112;

        /**
         * File {0}
         */
        public static final short FILE_1 = 113;

        /**
         * Line {1} in file {0}
         */
        public static final short FILE_POSITION_2 = 114;

        /**
         * Format
         */
        public static final short FORMAT = 115;

        /**
         * Future
         */
        public static final short FUTURE = 116;

        /**
         * General
         */
        public static final short GENERAL = 117;

        /**
         * Generic Cartesian 2D
         */
        public static final short GENERIC_CARTESIAN_2D = 118;

        /**
         * Generic Cartesian 3D
         */
        public static final short GENERIC_CARTESIAN_3D = 119;

        /**
         * Geocentric
         */
        public static final short GEOCENTRIC = 120;

        /**
         * Geocentric radius
         */
        public static final short GEOCENTRIC_RADIUS = 121;

        /**
         * Geocentric transform
         */
        public static final short GEOCENTRIC_TRANSFORM = 122;

        /**
         * Geocentric X
         */
        public static final short GEOCENTRIC_X = 123;

        /**
         * Geocentric Y
         */
        public static final short GEOCENTRIC_Y = 124;

        /**
         * Geocentric Z
         */
        public static final short GEOCENTRIC_Z = 125;

        /**
         * Geodetic 2D
         */
        public static final short GEODETIC_2D = 126;

        /**
         * Geodetic 3D
         */
        public static final short GEODETIC_3D = 127;

        /**
         * Geodetic latitude
         */
        public static final short GEODETIC_LATITUDE = 128;

        /**
         * Geodetic longitude
         */
        public static final short GEODETIC_LONGITUDE = 129;

        /**
         * Geographic coordinates
         */
        public static final short GEOGRAPHIC_COORDINATES = 130;

        /**
         * Geoidal
         */
        public static final short GEOIDAL = 131;

        /**
         * Geoid model derived
         */
        public static final short GEOID_MODEL_DERIVED = 132;

        /**
         * Geophysics
         */
        public static final short GEOPHYSICS = 133;

        /**
         * Geospatial
         */
        public static final short GEOSPATIAL = 134;

        /**
         * Greenwich Mean Time (GMT)
         */
        public static final short GMT = 135;

        /**
         * Gradient masks
         */
        public static final short GRADIENT_MASKS = 136;

        /**
         * Gravity-related height
         */
        public static final short GRAVITY_RELATED_HEIGHT = 137;

        /**
         * Gray
         */
        public static final short GRAY = 138;

        /**
         * Gray scale
         */
        public static final short GRAY_SCALE = 139;

        /**
         * Green
         */
        public static final short GREEN = 140;

        /**
         * Grid
         */
        public static final short GRID = 141;

        /**
         * Gridded data
         */
        public static final short GRIDDED_DATA = 142;

        /**
         * Height
         */
        public static final short HEIGHT = 143;

        /**
         * Help
         */
        public static final short HELP = 144;

        /**
         * Hidden
         */
        public static final short HIDDEN = 145;

        /**
         * Hide
         */
        public static final short HIDE = 146;

        /**
         * Horizontal
         */
        public static final short HORIZONTAL = 147;

        /**
         * Horizontal component
         */
        public static final short HORIZONTAL_COMPONENT = 148;

        /**
         * Hue
         */
        public static final short HUE = 149;

        /**
         * {0}-{1}
         */
        public static final short HYPHEN_2 = 150;

        /**
         * Identification
         */
        public static final short IDENTIFICATION = 151;

        /**
         * Identifier
         */
        public static final short IDENTIFIER = 152;

        /**
         * Identity
         */
        public static final short IDENTITY = 153;

        /**
         * Images
         */
        public static final short IMAGES = 154;

        /**
         * Image {0}
         */
        public static final short IMAGE_1 = 155;

        /**
         * Image of class {0}
         */
        public static final short IMAGE_CLASS_1 = 156;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final short IMAGE_CODEC_WITH_WORLD_FILE_2 = 157;

        /**
         * Image list
         */
        public static final short IMAGE_LIST = 158;

        /**
         * Image root directory
         */
        public static final short IMAGE_ROOT_DIRECTORY = 159;

        /**
         * Image size
         */
        public static final short IMAGE_SIZE = 160;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final short IMAGE_SIZE_3 = 161;

        /**
         * Implementations
         */
        public static final short IMPLEMENTATIONS = 162;

        /**
         * Index
         */
        public static final short INDEX = 163;

        /**
         * Indexed
         */
        public static final short INDEXED = 164;

        /**
         * Informations
         */
        public static final short INFORMATIONS = 165;

        /**
         * Inside
         */
        public static final short INSIDE = 166;

        /**
         * Install
         */
        public static final short INSTALL = 167;

        /**
         * {0} installation
         */
        public static final short INSTALLATION_1 = 168;

        /**
         * Inverse {0}
         */
        public static final short INVERSE_1 = 169;

        /**
         * Inverse operation
         */
        public static final short INVERSE_OPERATION = 170;

        /**
         * Inverse transform
         */
        public static final short INVERSE_TRANSFORM = 171;

        /**
         * {0}
         */
        public static final short JAVA_VENDOR_1 = 172;

        /**
         * Java version {0}
         */
        public static final short JAVA_VERSION_1 = 173;

        /**
         * Julian
         */
        public static final short JULIAN = 174;

        /**
         * Kernel
         */
        public static final short KERNEL = 175;

        /**
         * Lambert conformal conic projection
         */
        public static final short LAMBERT_CONFORMAL_PROJECTION = 176;

        /**
         * Latitude
         */
        public static final short LATITUDE = 177;

        /**
         * Layers
         */
        public static final short LAYERS = 178;

        /**
         * Left
         */
        public static final short LEFT = 179;

        /**
         * Level
         */
        public static final short LEVEL = 180;

        /**
         * Lightness
         */
        public static final short LIGHTNESS = 181;

        /**
         * Lines
         */
        public static final short LINES = 182;

        /**
         * Line {0}
         */
        public static final short LINE_1 = 183;

        /**
         * Loading…
         */
        public static final short LOADING = 184;

        /**
         * Loading {0}…
         */
        public static final short LOADING_1 = 185;

        /**
         * Loading headers
         */
        public static final short LOADING_HEADERS = 186;

        /**
         * Loading images {0} and {1}
         */
        public static final short LOADING_IMAGES_2 = 187;

        /**
         * Loading image {0}
         */
        public static final short LOADING_IMAGE_1 = 188;

        /**
         * Local
         */
        public static final short LOCAL = 189;

        /**
         * Logarithmic
         */
        public static final short LOGARITHMIC = 190;

        /**
         * Logger
         */
        public static final short LOGGER = 191;

        /**
         * Longitude
         */
        public static final short LONGITUDE = 192;

        /**
         * Magenta
         */
        public static final short MAGENTA = 193;

        /**
         * Magnifier
         */
        public static final short MAGNIFIER = 194;

        /**
         * Mandatory
         */
        public static final short MANDATORY = 195;

        /**
         * Math transform
         */
        public static final short MATH_TRANSFORM = 196;

        /**
         * Maximum
         */
        public static final short MAXIMUM = 197;

        /**
         * Allocated memory: {0} MB
         */
        public static final short MEMORY_HEAP_SIZE_1 = 198;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final short MEMORY_HEAP_USAGE_1 = 199;

        /**
         * Message
         */
        public static final short MESSAGE = 200;

        /**
         * Metadata
         */
        public static final short METADATA = 201;

        /**
         * Method
         */
        public static final short METHOD = 202;

        /**
         * Minimum
         */
        public static final short MINIMUM = 203;

        /**
         * Modified Julian
         */
        public static final short MODIFIED_JULIAN = 204;

        /**
         * Molodensky transform
         */
        public static final short MOLODENSKY_TRANSFORM = 205;

        /**
         * … {0} more…
         */
        public static final short MORE_1 = 206;

        /**
         * NADCON transform
         */
        public static final short NADCON_TRANSFORM = 207;

        /**
         * Name
         */
        public static final short NAME = 208;

        /**
         * Navigate
         */
        public static final short NAVIGATE = 209;

        /**
         * New format
         */
        public static final short NEW_FORMAT = 210;

        /**
         * New layer
         */
        public static final short NEW_LAYER = 211;

        /**
         * No data
         */
        public static final short NODATA = 212;

        /**
         * None
         */
        public static final short NONE = 213;

        /**
         * Normal
         */
        public static final short NORMAL = 214;

        /**
         * North
         */
        public static final short NORTH = 215;

        /**
         * Northing
         */
        public static final short NORTHING = 216;

        /**
         * Note
         */
        public static final short NOTE = 217;

        /**
         * Not installed
         */
        public static final short NOT_INSTALLED = 218;

        /**
         * {0} (no details)
         */
        public static final short NO_DETAILS_1 = 219;

        /**
         * No duplicated value found.
         */
        public static final short NO_DUPLICATION_FOUND = 220;

        /**
         * Oblique Mercator projection
         */
        public static final short OBLIQUE_MERCATOR_PROJECTION = 221;

        /**
         * Occurrence
         */
        public static final short OCCURRENCE = 222;

        /**
         * Offset
         */
        public static final short OFFSET = 223;

        /**
         * Ok
         */
        public static final short OK = 224;

        /**
         * Operations
         */
        public static final short OPERATIONS = 225;

        /**
         * {0} operation
         */
        public static final short OPERATION_1 = 226;

        /**
         * Options
         */
        public static final short OPTIONS = 227;

        /**
         * Order
         */
        public static final short ORDER = 228;

        /**
         * Orthodromic distance
         */
        public static final short ORTHODROMIC_DISTANCE = 229;

        /**
         * Orthographic projection
         */
        public static final short ORTHOGRAPHIC_PROJECTION = 230;

        /**
         * Orthometric
         */
        public static final short ORTHOMETRIC = 231;

        /**
         * {0} system
         */
        public static final short OS_NAME_1 = 232;

        /**
         * Version {0} for {1}
         */
        public static final short OS_VERSION_2 = 233;

        /**
         * Other
         */
        public static final short OTHER = 234;

        /**
         * Others
         */
        public static final short OTHERS = 235;

        /**
         * Output directory
         */
        public static final short OUTPUT_DIRECTORY = 236;

        /**
         * Outside
         */
        public static final short OUTSIDE = 237;

        /**
         * Palette
         */
        public static final short PALETTE = 238;

        /**
         * Parameter {0}
         */
        public static final short PARAMETER_1 = 239;

        /**
         * Part
         */
        public static final short PART = 240;

        /**
         * Password
         */
        public static final short PASSWORD = 241;

        /**
         * Past
         */
        public static final short PAST = 242;

        /**
         * Paused
         */
        public static final short PAUSED = 243;

        /**
         * Personalized
         */
        public static final short PERSONALIZED = 244;

        /**
         * Pixels
         */
        public static final short PIXELS = 245;

        /**
         * Pixel size
         */
        public static final short PIXEL_SIZE = 246;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final short POINT_COUNT_IN_GRID_3 = 247;

        /**
         * Port
         */
        public static final short PORT = 248;

        /**
         * Predefined kernels
         */
        public static final short PREDEFINED_KERNELS = 249;

        /**
         * Preferences
         */
        public static final short PREFERENCES = 250;

        /**
         * Preferred resolution
         */
        public static final short PREFERRED_RESOLUTION = 251;

        /**
         * Preview
         */
        public static final short PREVIEW = 252;

        /**
         * Progression
         */
        public static final short PROGRESSION = 253;

        /**
         * Projected
         */
        public static final short PROJECTED = 254;

        /**
         * Properties
         */
        public static final short PROPERTIES = 255;

        /**
         * Properties of {0}
         */
        public static final short PROPERTIES_OF_1 = 256;

        /**
         * Quit
         */
        public static final short QUIT = 257;

        /**
         * Range
         */
        public static final short RANGE = 258;

        /**
         * {0} bits real number
         */
        public static final short REAL_NUMBER_1 = 259;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final short RECTANGLE_4 = 260;

        /**
         * Red
         */
        public static final short RED = 261;

        /**
         * Refresh
         */
        public static final short REFRESH = 262;

        /**
         * Remarks
         */
        public static final short REMARKS = 263;

        /**
         * Remove
         */
        public static final short REMOVE = 264;

        /**
         * Reset
         */
        public static final short RESET = 265;

        /**
         * Resolution
         */
        public static final short RESOLUTION = 266;

        /**
         * Resumed
         */
        public static final short RESUMED = 267;

        /**
         * RGF93 transform
         */
        public static final short RGF93_TRANSFORM = 268;

        /**
         * Right
         */
        public static final short RIGHT = 269;

        /**
         * Root directory
         */
        public static final short ROOT_DIRECTORY = 270;

        /**
         * Root mean squared error.
         */
        public static final short ROOT_MEAN_SQUARED_ERROR = 271;

        /**
         * Rotate left
         */
        public static final short ROTATE_LEFT = 272;

        /**
         * Rotate right
         */
        public static final short ROTATE_RIGHT = 273;

        /**
         * Row
         */
        public static final short ROW = 274;

        /**
         * Running tasks
         */
        public static final short RUNNING_TASKS = 275;

        /**
         * Sample dimensions
         */
        public static final short SAMPLE_DIMENSIONS = 276;

        /**
         * Sample model
         */
        public static final short SAMPLE_MODEL = 277;

        /**
         * Saturation
         */
        public static final short SATURATION = 278;

        /**
         * Saving {0}…
         */
        public static final short SAVING_1 = 279;

        /**
         * Scale
         */
        public static final short SCALE = 280;

        /**
         * Scale 1:{0} (approximative)
         */
        public static final short SCALE_1 = 281;

        /**
         * Schema
         */
        public static final short SCHEMA = 282;

        /**
         * Search
         */
        public static final short SEARCH = 283;

        /**
         * Selected colors
         */
        public static final short SELECTED_COLORS = 284;

        /**
         * Server
         */
        public static final short SERVER = 285;

        /**
         * Service
         */
        public static final short SERVICE = 286;

        /**
         * Set preferred resolution
         */
        public static final short SET_PREFERRED_RESOLUTION = 287;

        /**
         * Show magnifier
         */
        public static final short SHOW_MAGNIFIER = 288;

        /**
         * {0} bits signed integer
         */
        public static final short SIGNED_INTEGER_1 = 289;

        /**
         * Size
         */
        public static final short SIZE = 290;

        /**
         * {0} × {1}
         */
        public static final short SIZE_2 = 291;

        /**
         * (in angle minutes)
         */
        public static final short SIZE_IN_MINUTES = 292;

        /**
         * Source CRS
         */
        public static final short SOURCE_CRS = 293;

        /**
         * Source point
         */
        public static final short SOURCE_POINT = 294;

        /**
         * South
         */
        public static final short SOUTH = 295;

        /**
         * Southing
         */
        public static final short SOUTHING = 296;

        /**
         * Spatial objects
         */
        public static final short SPATIAL_OBJECTS = 297;

        /**
         * Spherical
         */
        public static final short SPHERICAL = 298;

        /**
         * Spherical latitude
         */
        public static final short SPHERICAL_LATITUDE = 299;

        /**
         * Spherical longitude
         */
        public static final short SPHERICAL_LONGITUDE = 300;

        /**
         * Standard
         */
        public static final short STANDARD = 301;

        /**
         * Started
         */
        public static final short STARTED = 302;

        /**
         * Start time
         */
        public static final short START_TIME = 303;

        /**
         * Stereographic projection
         */
        public static final short STEREOGRAPHIC_PROJECTION = 304;

        /**
         * Subsampling
         */
        public static final short SUBSAMPLING = 305;

        /**
         * Superseded by {0}.
         */
        public static final short SUPERSEDED_BY_1 = 306;

        /**
         * System
         */
        public static final short SYSTEM = 307;

        /**
         * Target
         */
        public static final short TARGET = 308;

        /**
         * Target CRS
         */
        public static final short TARGET_CRS = 309;

        /**
         * Target point
         */
        public static final short TARGET_POINT = 310;

        /**
         * Tasks
         */
        public static final short TASKS = 311;

        /**
         * Temporal
         */
        public static final short TEMPORAL = 312;

        /**
         * Tiles size
         */
        public static final short TILES_SIZE = 313;

        /**
         * Tile cache capacity: {0} Mb
         */
        public static final short TILE_CACHE_CAPACITY_1 = 314;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final short TILE_SIZE_4 = 315;

        /**
         * Time
         */
        public static final short TIME = 316;

        /**
         * Time
         */
        public static final short TIME_OF_DAY = 317;

        /**
         * Time range
         */
        public static final short TIME_RANGE = 318;

        /**
         * Time zone
         */
        public static final short TIME_ZONE = 319;

        /**
         * Transfert function
         */
        public static final short TRANSFERT_FUNCTION = 320;

        /**
         * Transformation
         */
        public static final short TRANSFORMATION = 321;

        /**
         * Transformation accuracy
         */
        public static final short TRANSFORMATION_ACCURACY = 322;

        /**
         * Transparency
         */
        public static final short TRANSPARENCY = 323;

        /**
         * Transverse Mercator projection
         */
        public static final short TRANSVERSE_MERCATOR_PROJECTION = 324;

        /**
         * True
         */
        public static final short TRUE = 325;

        /**
         * Truncated Julian
         */
        public static final short TRUNCATED_JULIAN = 326;

        /**
         * Type
         */
        public static final short TYPE = 327;

        /**
         * Undefined
         */
        public static final short UNDEFINED = 328;

        /**
         * Units
         */
        public static final short UNITS = 329;

        /**
         * Unknown
         */
        public static final short UNKNOWN = 330;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final short UNSIGNED_INTEGER_2 = 331;

        /**
         * Untitled
         */
        public static final short UNTITLED = 332;

        /**
         * Up
         */
        public static final short UP = 333;

        /**
         * URL
         */
        public static final short URL = 334;

        /**
         * User
         */
        public static final short USER = 335;

        /**
         * Use best resolution
         */
        public static final short USE_BEST_RESOLUTION = 336;

        /**
         * Universal Time (UTC)
         */
        public static final short UTC = 337;

        /**
         * Valid values
         */
        public static final short VALID_VALUES = 338;

        /**
         * Value
         */
        public static final short VALUE = 339;

        /**
         * Value range
         */
        public static final short VALUE_RANGE = 340;

        /**
         * Vendor
         */
        public static final short VENDOR = 341;

        /**
         * Verifying
         */
        public static final short VERIFYING = 342;

        /**
         * Version {0}
         */
        public static final short VERSION_1 = 343;

        /**
         * “{0}” version
         */
        public static final short VERSION_OF_1 = 344;

        /**
         * Vertical
         */
        public static final short VERTICAL = 345;

        /**
         * Vertical component
         */
        public static final short VERTICAL_COMPONENT = 346;

        /**
         * Warning
         */
        public static final short WARNING = 347;

        /**
         * West
         */
        public static final short WEST = 348;

        /**
         * Westing
         */
        public static final short WESTING = 349;

        /**
         * Width
         */
        public static final short WIDTH = 350;

        /**
         * Wizards
         */
        public static final short WIZARDS = 351;

        /**
         * Yellow
         */
        public static final short YELLOW = 352;

        /**
         * Zoom in
         */
        public static final short ZOOM_IN = 353;

        /**
         * Close zoom
         */
        public static final short ZOOM_MAX = 354;

        /**
         * Zoom out
         */
        public static final short ZOOM_OUT = 355;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Vocabulary(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Vocabulary getResources(Locale locale) throws MissingResourceException {
        return getBundle(Vocabulary.class, locale);
    }

    /**
     * The international string to be returned by {@link formatInternational}.
     */
    private static final class International extends ResourceInternationalString {
        private static final long serialVersionUID = -9199238559657784488L;

        International(final int key) {
            super(Vocabulary.class.getName(), String.valueOf(key));
        }

        @Override
        protected ResourceBundle getBundle(final Locale locale) {
            return getResources(locale);
        }
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @return An international string for the given key.
     */
    public static InternationalString formatInternational(final int key) {
        return new International(key);
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * {@note This method is redundant with the one expecting <code>Object...</code>, but is
     *        provided for binary compatibility with previous Geotk versions. It also avoid the
     *        creation of a temporary array. There is no risk of confusion since the two methods
     *        delegate their work to the same <code>format</code> method anyway.}
     *
     * @param  key The key for the desired string.
     * @param  arg Values to substitute to "{0}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object arg) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, arg));
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @param  args Values to substitute to "{0}", "{1}", <i>etc</i>.
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final short key, final Object... args) {
        return new org.apache.sis.util.iso.SimpleInternationalString(format(key, args));
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1);
    }

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
