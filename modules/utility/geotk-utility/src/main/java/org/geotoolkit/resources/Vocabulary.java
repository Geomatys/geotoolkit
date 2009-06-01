/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.geotoolkit.util.ResourceInternationalString;


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
        public static final int ABOUT = 0;

        /**
         * Abridged Molodensky transform
         */
        public static final int ABRIDGED_MOLODENSKY_TRANSFORM = 1;

        /**
         * Add
         */
        public static final int ADD = 2;

        /**
         * Affine transform
         */
        public static final int AFFINE_TRANSFORM = 3;

        /**
         * Albers Equal Area projection
         */
        public static final int ALBERS_EQUAL_AREA_PROJECTION = 4;

        /**
         * Alias
         */
        public static final int ALIAS = 5;

        /**
         * All
         */
        public static final int ALL = 6;

        /**
         * All users
         */
        public static final int ALL_USERS = 7;

        /**
         * Altitude
         */
        public static final int ALTITUDE = 8;

        /**
         * Apply
         */
        public static final int APPLY = 291;

        /**
         * Authority
         */
        public static final int AUTHORITY = 9;

        /**
         * Automatic
         */
        public static final int AUTOMATIC = 10;

        /**
         * {0} axis
         */
        public static final int AXIS_$1 = 288;

        /**
         * Axis changes
         */
        public static final int AXIS_CHANGES = 11;

        /**
         * Azimuth
         */
        public static final int AZIMUTH = 12;

        /**
         * Band
         */
        public static final int BAND = 13;

        /**
         * Barometric altitude
         */
        public static final int BAROMETRIC_ALTITUDE = 14;

        /**
         * Black
         */
        public static final int BLACK = 15;

        /**
         * Blue
         */
        public static final int BLUE = 16;

        /**
         * Cancel
         */
        public static final int CANCEL = 17;

        /**
         * Cartesian
         */
        public static final int CARTESIAN = 18;

        /**
         * Cartesian 2D
         */
        public static final int CARTESIAN_2D = 19;

        /**
         * Cartesian 3D
         */
        public static final int CARTESIAN_3D = 20;

        /**
         * Cassini-Soldner projection
         */
        public static final int CASSINI_SOLDNER_PROJECTION = 21;

        /**
         * Category
         */
        public static final int CATEGORY = 22;

        /**
         * Choose
         */
        public static final int CHOOSE = 289;

        /**
         * Class
         */
        public static final int CLASS = 23;

        /**
         * Classic
         */
        public static final int CLASSIC = 24;

        /**
         * Close
         */
        public static final int CLOSE = 25;

        /**
         * Code
         */
        public static final int CODE = 26;

        /**
         * Colors
         */
        public static final int COLORS = 27;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final int COLOR_COUNT_$1 = 28;

        /**
         * Color model
         */
        public static final int COLOR_MODEL = 29;

        /**
         * Color space
         */
        public static final int COLOR_SPACE = 30;

        /**
         * Column
         */
        public static final int COLUMN = 31;

        /**
         * Columns
         */
        public static final int COLUMNS = 32;

        /**
         * Commands
         */
        public static final int COMMANDS = 33;

        /**
         * Compare with
         */
        public static final int COMPARE_WITH = 34;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final int COMPONENT_COUNT_$1 = 35;

        /**
         * Conversion
         */
        public static final int CONVERSION = 36;

        /**
         * Conversion and transformation
         */
        public static final int CONVERSION_AND_TRANSFORMATION = 37;

        /**
         * Coordinates selection
         */
        public static final int COORDINATES_SELECTION = 38;

        /**
         * Coordinate format
         */
        public static final int COORDINATE_FORMAT = 39;

        /**
         * Coordinate Reference System
         */
        public static final int COORDINATE_REFERENCE_SYSTEM = 40;

        /**
         * Current user
         */
        public static final int CURRENT_USER = 41;

        /**
         * Cyan
         */
        public static final int CYAN = 42;

        /**
         * Cylindrical Mercator projection
         */
        public static final int CYLINDRICAL_MERCATOR_PROJECTION = 43;

        /**
         * Data
         */
        public static final int DATA = 44;

        /**
         * Database engine
         */
        public static final int DATABASE_ENGINE = 45;

        /**
         * Database URL
         */
        public static final int DATABASE_URL = 46;

        /**
         * {0} data
         */
        public static final int DATA_$1 = 47;

        /**
         * Data are present
         */
        public static final int DATA_ARE_PRESENT = 48;

        /**
         * {0} data base
         */
        public static final int DATA_BASE_$1 = 49;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final int DATA_BASE_$3 = 50;

        /**
         * Data type
         */
        public static final int DATA_TYPE = 51;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final int DATA_TYPE_$2 = 52;

        /**
         * Datum
         */
        public static final int DATUM = 53;

        /**
         * Datum shift
         */
        public static final int DATUM_SHIFT = 54;

        /**
         * Debug
         */
        public static final int DEBUG = 55;

        /**
         * Decoders
         */
        public static final int DECODERS = 56;

        /**
         * Default
         */
        public static final int DEFAULT = 57;

        /**
         * Default value
         */
        public static final int DEFAULT_VALUE = 58;

        /**
         * Depth
         */
        public static final int DEPTH = 59;

        /**
         * Derived from {0}
         */
        public static final int DERIVED_FROM_$1 = 60;

        /**
         * Description
         */
        public static final int DESCRIPTION = 61;

        /**
         * Directories
         */
        public static final int DIRECTORIES = 62;

        /**
         * Discontinuous
         */
        public static final int DISCONTINUOUS = 63;

        /**
         * Display
         */
        public static final int DISPLAY = 64;

        /**
         * Distance
         */
        public static final int DISTANCE = 65;

        /**
         * Down
         */
        public static final int DOWN = 66;

        /**
         * Download
         */
        public static final int DOWNLOAD = 67;

        /**
         * Downloading
         */
        public static final int DOWNLOADING = 68;

        /**
         * Dublin Julian
         */
        public static final int DUBLIN_JULIAN = 69;

        /**
         * Duplicated value
         */
        public static final int DUPLICATED_VALUE = 70;

        /**
         * Earth gravitational model
         */
        public static final int EARTH_GRAVITATIONAL_MODEL = 71;

        /**
         * East
         */
        public static final int EAST = 72;

        /**
         * Easting
         */
        public static final int EASTING = 73;

        /**
         * Efficiency
         */
        public static final int EFFICIENCY = 296;

        /**
         * Ellipsoid
         */
        public static final int ELLIPSOID = 74;

        /**
         * Ellipsoidal
         */
        public static final int ELLIPSOIDAL = 75;

        /**
         * Ellipsoidal height
         */
        public static final int ELLIPSOIDAL_HEIGHT = 76;

        /**
         * Ellipsoid shift
         */
        public static final int ELLIPSOID_SHIFT = 77;

        /**
         * Empty
         */
        public static final int EMPTY = 78;

        /**
         * Encoders
         */
        public static final int ENCODERS = 79;

        /**
         * End time
         */
        public static final int END_TIME = 80;

        /**
         * Equidistant cylindrical projection
         */
        public static final int EQUIDISTANT_CYLINDRICAL_PROJECTION = 81;

        /**
         * Error
         */
        public static final int ERROR = 82;

        /**
         * Error - {0}
         */
        public static final int ERROR_$1 = 83;

        /**
         * Error filters
         */
        public static final int ERROR_FILTERS = 84;

        /**
         * Event logger
         */
        public static final int EVENT_LOGGER = 85;

        /**
         * Examples
         */
        public static final int EXAMPLES = 86;

        /**
         * Exception
         */
        public static final int EXCEPTION = 87;

        /**
         * Exponential
         */
        public static final int EXPONENTIAL = 88;

        /**
         * Factory
         */
        public static final int FACTORY = 89;

        /**
         * False
         */
        public static final int FALSE = 90;

        /**
         * File
         */
        public static final int FILE = 91;

        /**
         * File {0}
         */
        public static final int FILE_$1 = 92;

        /**
         * Line {1} in file {0}
         */
        public static final int FILE_POSITION_$2 = 93;

        /**
         * Format
         */
        public static final int FORMAT = 94;

        /**
         * Future
         */
        public static final int FUTURE = 95;

        /**
         * Generic cartesian 2D
         */
        public static final int GENERIC_CARTESIAN_2D = 96;

        /**
         * Generic cartesian 3D
         */
        public static final int GENERIC_CARTESIAN_3D = 97;

        /**
         * Geocentric
         */
        public static final int GEOCENTRIC = 98;

        /**
         * Geocentric radius
         */
        public static final int GEOCENTRIC_RADIUS = 99;

        /**
         * Geocentric transform
         */
        public static final int GEOCENTRIC_TRANSFORM = 100;

        /**
         * Geocentric X
         */
        public static final int GEOCENTRIC_X = 101;

        /**
         * Geocentric Y
         */
        public static final int GEOCENTRIC_Y = 102;

        /**
         * Geocentric Z
         */
        public static final int GEOCENTRIC_Z = 103;

        /**
         * Geodetic 2D
         */
        public static final int GEODETIC_2D = 104;

        /**
         * Geodetic 3D
         */
        public static final int GEODETIC_3D = 105;

        /**
         * Geodetic latitude
         */
        public static final int GEODETIC_LATITUDE = 106;

        /**
         * Geodetic longitude
         */
        public static final int GEODETIC_LONGITUDE = 107;

        /**
         * Geographic coordinates
         */
        public static final int GEOGRAPHIC_COORDINATES = 108;

        /**
         * Geoidal
         */
        public static final int GEOIDAL = 109;

        /**
         * Geoid model derived
         */
        public static final int GEOID_MODEL_DERIVED = 110;

        /**
         * Geophysics
         */
        public static final int GEOPHYSICS = 111;

        /**
         * Greenwich Mean Time (GMT)
         */
        public static final int GMT = 112;

        /**
         * Gradient masks
         */
        public static final int GRADIENT_MASKS = 113;

        /**
         * Gravity-related height
         */
        public static final int GRAVITY_RELATED_HEIGHT = 114;

        /**
         * Gray
         */
        public static final int GRAY = 115;

        /**
         * Gray scale
         */
        public static final int GRAY_SCALE = 116;

        /**
         * Green
         */
        public static final int GREEN = 117;

        /**
         * Grid
         */
        public static final int GRID = 118;

        /**
         * Height
         */
        public static final int HEIGHT = 119;

        /**
         * Hide
         */
        public static final int HIDE = 120;

        /**
         * Hiden
         */
        public static final int HIDEN = 121;

        /**
         * Horizontal
         */
        public static final int HORIZONTAL = 122;

        /**
         * Horizontal component
         */
        public static final int HORIZONTAL_COMPONENT = 123;

        /**
         * Hue
         */
        public static final int HUE = 124;

        /**
         * Identifier
         */
        public static final int IDENTIFIER = 125;

        /**
         * Identity
         */
        public static final int IDENTITY = 126;

        /**
         * Images
         */
        public static final int IMAGES = 127;

        /**
         * Image of class "{0}"
         */
        public static final int IMAGE_CLASS_$1 = 128;

        /**
         * Image list
         */
        public static final int IMAGE_LIST = 129;

        /**
         * Image size
         */
        public static final int IMAGE_SIZE = 130;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final int IMAGE_SIZE_$3 = 131;

        /**
         * Implementations
         */
        public static final int IMPLEMENTATIONS = 132;

        /**
         * Index
         */
        public static final int INDEX = 133;

        /**
         * Indexed
         */
        public static final int INDEXED = 134;

        /**
         * Informations
         */
        public static final int INFORMATIONS = 135;

        /**
         * Inside
         */
        public static final int INSIDE = 136;

        /**
         * {0} installation
         */
        public static final int INSTALLATION_$1 = 137;

        /**
         * Inverse {0}
         */
        public static final int INVERSE_$1 = 138;

        /**
         * Inverse operation
         */
        public static final int INVERSE_OPERATION = 139;

        /**
         * Inverse transform
         */
        public static final int INVERSE_TRANSFORM = 140;

        /**
         * {0}
         */
        public static final int JAVA_VENDOR_$1 = 141;

        /**
         * Java version {0}
         */
        public static final int JAVA_VERSION_$1 = 142;

        /**
         * Julian
         */
        public static final int JULIAN = 143;

        /**
         * Kernel
         */
        public static final int KERNEL = 144;

        /**
         * Lambert conformal conic projection
         */
        public static final int LAMBERT_CONFORMAL_PROJECTION = 145;

        /**
         * Latitude
         */
        public static final int LATITUDE = 146;

        /**
         * Left
         */
        public static final int LEFT = 147;

        /**
         * Level
         */
        public static final int LEVEL = 148;

        /**
         * Lightness
         */
        public static final int LIGHTNESS = 149;

        /**
         * Lines
         */
        public static final int LINES = 150;

        /**
         * Line {0}
         */
        public static final int LINE_$1 = 151;

        /**
         * Loading...
         */
        public static final int LOADING = 152;

        /**
         * Loading {0}...
         */
        public static final int LOADING_$1 = 153;

        /**
         * Loading headers
         */
        public static final int LOADING_HEADERS = 154;

        /**
         * Loading images {0} and {1}
         */
        public static final int LOADING_IMAGES_$2 = 155;

        /**
         * Loading image {0}
         */
        public static final int LOADING_IMAGE_$1 = 156;

        /**
         * Local
         */
        public static final int LOCAL = 157;

        /**
         * Logarithmic
         */
        public static final int LOGARITHMIC = 158;

        /**
         * Logger
         */
        public static final int LOGGER = 159;

        /**
         * Longitude
         */
        public static final int LONGITUDE = 160;

        /**
         * Magenta
         */
        public static final int MAGENTA = 161;

        /**
         * Magnifier
         */
        public static final int MAGNIFIER = 162;

        /**
         * Mandatory
         */
        public static final int MANDATORY = 163;

        /**
         * Math transform
         */
        public static final int MATH_TRANSFORM = 164;

        /**
         * Maximum
         */
        public static final int MAXIMUM = 165;

        /**
         * Allocated memory: {0} MB
         */
        public static final int MEMORY_HEAP_SIZE_$1 = 166;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final int MEMORY_HEAP_USAGE_$1 = 167;

        /**
         * Message
         */
        public static final int MESSAGE = 168;

        /**
         * Method
         */
        public static final int METHOD = 169;

        /**
         * Minimum
         */
        public static final int MINIMUM = 170;

        /**
         * Modified Julian
         */
        public static final int MODIFIED_JULIAN = 171;

        /**
         * Molodensky transform
         */
        public static final int MOLODENSKY_TRANSFORM = 172;

        /**
         * ... {0} more...
         */
        public static final int MORE_$1 = 173;

        /**
         * NADCON transform
         */
        public static final int NADCON_TRANSFORM = 174;

        /**
         * Name
         */
        public static final int NAME = 175;

        /**
         * No data
         */
        public static final int NODATA = 176;

        /**
         * None
         */
        public static final int NONE = 177;

        /**
         * Normal
         */
        public static final int NORMAL = 178;

        /**
         * North
         */
        public static final int NORTH = 179;

        /**
         * Northing
         */
        public static final int NORTHING = 180;

        /**
         * Note
         */
        public static final int NOTE = 181;

        /**
         * Not installed
         */
        public static final int NOT_INSTALLED = 182;

        /**
         * {0} (no details)
         */
        public static final int NO_DETAILS_$1 = 183;

        /**
         * No duplicated value found.
         */
        public static final int NO_DUPLICATION_FOUND = 184;

        /**
         * Oblique Mercator projection
         */
        public static final int OBLIQUE_MERCATOR_PROJECTION = 185;

        /**
         * Ok
         */
        public static final int OK = 186;

        /**
         * Operations
         */
        public static final int OPERATIONS = 187;

        /**
         * "{0}" operation
         */
        public static final int OPERATION_$1 = 188;

        /**
         * Options
         */
        public static final int OPTIONS = 189;

        /**
         * Order
         */
        public static final int ORDER = 190;

        /**
         * Orthodromic distance
         */
        public static final int ORTHODROMIC_DISTANCE = 191;

        /**
         * Orthographic projection
         */
        public static final int ORTHOGRAPHIC_PROJECTION = 192;

        /**
         * Orthometric
         */
        public static final int ORTHOMETRIC = 193;

        /**
         * {0} system
         */
        public static final int OS_NAME_$1 = 194;

        /**
         * Version {0} for {1}
         */
        public static final int OS_VERSION_$2 = 195;

        /**
         * Other
         */
        public static final int OTHER = 196;

        /**
         * Others
         */
        public static final int OTHERS = 197;

        /**
         * Output directory
         */
        public static final int OUTPUT_DIRECTORY = 290;

        /**
         * Outside
         */
        public static final int OUTSIDE = 198;

        /**
         * Palette
         */
        public static final int PALETTE = 199;

        /**
         * Parameter {0}
         */
        public static final int PARAMETER_$1 = 200;

        /**
         * Password
         */
        public static final int PASSWORD = 292;

        /**
         * Past
         */
        public static final int PAST = 201;

        /**
         * Personalized
         */
        public static final int PERSONALIZED = 202;

        /**
         * Pixels
         */
        public static final int PIXELS = 203;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final int POINT_COUNT_IN_GRID_$3 = 204;

        /**
         * Predefined kernels
         */
        public static final int PREDEFINED_KERNELS = 205;

        /**
         * Preferred resolution
         */
        public static final int PREFERRED_RESOLUTION = 206;

        /**
         * Preview
         */
        public static final int PREVIEW = 207;

        /**
         * Progression
         */
        public static final int PROGRESSION = 208;

        /**
         * Projected
         */
        public static final int PROJECTED = 209;

        /**
         * Properties
         */
        public static final int PROPERTIES = 210;

        /**
         * Range
         */
        public static final int RANGE = 211;

        /**
         * {0} bits real number
         */
        public static final int REAL_NUMBER_$1 = 212;

        /**
         * Area: x=[{0} .. {1}], y=[{2} .. {3}]
         */
        public static final int RECTANGLE_$4 = 213;

        /**
         * Red
         */
        public static final int RED = 214;

        /**
         * Remove
         */
        public static final int REMOVE = 215;

        /**
         * Reset
         */
        public static final int RESET = 216;

        /**
         * Right
         */
        public static final int RIGHT = 217;

        /**
         * Root directory
         */
        public static final int ROOT_DIRECTORY = 218;

        /**
         * Root mean squared error.
         */
        public static final int ROOT_MEAN_SQUARED_ERROR = 219;

        /**
         * Rotate left
         */
        public static final int ROTATE_LEFT = 220;

        /**
         * Rotate right
         */
        public static final int ROTATE_RIGHT = 221;

        /**
         * Row
         */
        public static final int ROW = 222;

        /**
         * Running tasks
         */
        public static final int RUNNING_TASKS = 223;

        /**
         * Sample model
         */
        public static final int SAMPLE_MODEL = 224;

        /**
         * Saturation
         */
        public static final int SATURATION = 225;

        /**
         * Saving {0}...
         */
        public static final int SAVING_$1 = 226;

        /**
         * Scale 1:{0} (approximative)
         */
        public static final int SCALE_$1 = 227;

        /**
         * Schema
         */
        public static final int SCHEMA = 293;

        /**
         * Search
         */
        public static final int SEARCH = 228;

        /**
         * Selected colors
         */
        public static final int SELECTED_COLORS = 297;

        /**
         * Service
         */
        public static final int SERVICE = 229;

        /**
         * Set preferred resolution
         */
        public static final int SET_PREFERRED_RESOLUTION = 230;

        /**
         * Show magnifier
         */
        public static final int SHOW_MAGNIFIER = 231;

        /**
         * {0} bits signed integer
         */
        public static final int SIGNED_INTEGER_$1 = 232;

        /**
         * Size
         */
        public static final int SIZE = 233;

        /**
         * {0} × {1}
         */
        public static final int SIZE_$2 = 234;

        /**
         * (in angle minutes)
         */
        public static final int SIZE_IN_MINUTES = 235;

        /**
         * Source CRS
         */
        public static final int SOURCE_CRS = 236;

        /**
         * Source point
         */
        public static final int SOURCE_POINT = 237;

        /**
         * South
         */
        public static final int SOUTH = 238;

        /**
         * Southing
         */
        public static final int SOUTHING = 239;

        /**
         * Spherical
         */
        public static final int SPHERICAL = 240;

        /**
         * Spherical latitude
         */
        public static final int SPHERICAL_LATITUDE = 241;

        /**
         * Spherical longitude
         */
        public static final int SPHERICAL_LONGITUDE = 242;

        /**
         * Start time
         */
        public static final int START_TIME = 243;

        /**
         * Stereographic projection
         */
        public static final int STEREOGRAPHIC_PROJECTION = 244;

        /**
         * Subsampling
         */
        public static final int SUBSAMPLING = 245;

        /**
         * System
         */
        public static final int SYSTEM = 246;

        /**
         * Target
         */
        public static final int TARGET = 247;

        /**
         * Target CRS
         */
        public static final int TARGET_CRS = 248;

        /**
         * Target point
         */
        public static final int TARGET_POINT = 249;

        /**
         * Tasks
         */
        public static final int TASKS = 250;

        /**
         * Temporal
         */
        public static final int TEMPORAL = 251;

        /**
         * Tiles size
         */
        public static final int TILES_SIZE = 252;

        /**
         * Tile cache capacity: {0} MB
         */
        public static final int TILE_CACHE_CAPACITY_$1 = 253;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final int TILE_SIZE_$4 = 254;

        /**
         * Time
         */
        public static final int TIME = 255;

        /**
         * Time
         */
        public static final int TIME_OF_DAY = 256;

        /**
         * Time range
         */
        public static final int TIME_RANGE = 257;

        /**
         * Time zone
         */
        public static final int TIME_ZONE = 258;

        /**
         * Transformation
         */
        public static final int TRANSFORMATION = 259;

        /**
         * Transformation accuracy
         */
        public static final int TRANSFORMATION_ACCURACY = 260;

        /**
         * Transparency
         */
        public static final int TRANSPARENCY = 261;

        /**
         * Transverse Mercator projection
         */
        public static final int TRANSVERSE_MERCATOR_PROJECTION = 262;

        /**
         * True
         */
        public static final int TRUE = 263;

        /**
         * Truncated Julian
         */
        public static final int TRUNCATED_JULIAN = 264;

        /**
         * Type
         */
        public static final int TYPE = 265;

        /**
         * Undefined
         */
        public static final int UNDEFINED = 266;

        /**
         * Units
         */
        public static final int UNITS = 267;

        /**
         * Unknow
         */
        public static final int UNKNOW = 268;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final int UNSIGNED_INTEGER_$2 = 269;

        /**
         * Untitled
         */
        public static final int UNTITLED = 270;

        /**
         * Up
         */
        public static final int UP = 271;

        /**
         * URL
         */
        public static final int URL = 294;

        /**
         * User
         */
        public static final int USER = 295;

        /**
         * Use best resolution
         */
        public static final int USE_BEST_RESOLUTION = 272;

        /**
         * Universal Time (UTC)
         */
        public static final int UTC = 273;

        /**
         * Value
         */
        public static final int VALUE = 274;

        /**
         * Vendor
         */
        public static final int VENDOR = 275;

        /**
         * Version {0}
         */
        public static final int VERSION_$1 = 276;

        /**
         * "{0}" version
         */
        public static final int VERSION_OF_$1 = 277;

        /**
         * Vertical
         */
        public static final int VERTICAL = 278;

        /**
         * Vertical component
         */
        public static final int VERTICAL_COMPONENT = 279;

        /**
         * Warning
         */
        public static final int WARNING = 280;

        /**
         * West
         */
        public static final int WEST = 281;

        /**
         * Westing
         */
        public static final int WESTING = 282;

        /**
         * Width
         */
        public static final int WIDTH = 283;

        /**
         * Yellow
         */
        public static final int YELLOW = 284;

        /**
         * Zoom in
         */
        public static final int ZOOM_IN = 285;

        /**
         * Close zoom
         */
        public static final int ZOOM_MAX = 286;

        /**
         * Zoom out
         */
        public static final int ZOOM_OUT = 287;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    Vocabulary(final String filename) {
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
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final int    key,
                                                          final Object arg0)
    {
        return new org.geotoolkit.util.SimpleInternationalString(format(key, arg0));
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final int    key,
                                                          final Object arg0,
                                                          final Object arg1)
    {
        return new org.geotoolkit.util.SimpleInternationalString(format(key, arg0, arg1));
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     */
    public static InternationalString formatInternational(final int    key,
                                                          final Object arg0,
                                                          final Object arg1,
                                                          final Object arg2)
    {
        return new org.geotoolkit.util.SimpleInternationalString(format(key, arg0, arg1, arg2));
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
