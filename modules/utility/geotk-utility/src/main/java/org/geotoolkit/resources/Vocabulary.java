/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
        public static final int APPLY = 9;

        /**
         * Attribut
         */
        public static final int ATTRIBUT = 301;

        /**
         * Authority
         */
        public static final int AUTHORITY = 10;

        /**
         * Automatic
         */
        public static final int AUTOMATIC = 11;

        /**
         * {0} axis
         */
        public static final int AXIS_$1 = 12;

        /**
         * Axis changes
         */
        public static final int AXIS_CHANGES = 13;

        /**
         * Azimuth
         */
        public static final int AZIMUTH = 14;

        /**
         * Band
         */
        public static final int BAND = 15;

        /**
         * Barometric altitude
         */
        public static final int BAROMETRIC_ALTITUDE = 16;

        /**
         * Black
         */
        public static final int BLACK = 17;

        /**
         * Blue
         */
        public static final int BLUE = 18;

        /**
         * Cancel
         */
        public static final int CANCEL = 19;

        /**
         * Cartesian
         */
        public static final int CARTESIAN = 20;

        /**
         * Cartesian 2D
         */
        public static final int CARTESIAN_2D = 21;

        /**
         * Cartesian 3D
         */
        public static final int CARTESIAN_3D = 22;

        /**
         * Cassini-Soldner projection
         */
        public static final int CASSINI_SOLDNER_PROJECTION = 23;

        /**
         * Category
         */
        public static final int CATEGORY = 24;

        /**
         * Choose
         */
        public static final int CHOOSE = 25;

        /**
         * Class
         */
        public static final int CLASS = 26;

        /**
         * Classic
         */
        public static final int CLASSIC = 27;

        /**
         * Close
         */
        public static final int CLOSE = 28;

        /**
         * Code
         */
        public static final int CODE = 29;

        /**
         * Colors
         */
        public static final int COLORS = 30;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final int COLOR_COUNT_$1 = 31;

        /**
         * Color model
         */
        public static final int COLOR_MODEL = 32;

        /**
         * Color space
         */
        public static final int COLOR_SPACE = 33;

        /**
         * Column
         */
        public static final int COLUMN = 34;

        /**
         * Columns
         */
        public static final int COLUMNS = 35;

        /**
         * Commands
         */
        public static final int COMMANDS = 36;

        /**
         * Compare with
         */
        public static final int COMPARE_WITH = 37;

        /**
         * Completed
         */
        public static final int COMPLETED = 38;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final int COMPONENT_COUNT_$1 = 39;

        /**
         * Connection parameters
         */
        public static final int CONNECTION_PARAMETERS = 324;

        /**
         * Conversion
         */
        public static final int CONVERSION = 40;

        /**
         * Conversion and transformation
         */
        public static final int CONVERSION_AND_TRANSFORMATION = 41;

        /**
         * Coordinates selection
         */
        public static final int COORDINATES_SELECTION = 42;

        /**
         * Coordinate format
         */
        public static final int COORDINATE_FORMAT = 43;

        /**
         * Coordinate Reference System
         */
        public static final int COORDINATE_REFERENCE_SYSTEM = 44;

        /**
         * Current user
         */
        public static final int CURRENT_USER = 45;

        /**
         * Cyan
         */
        public static final int CYAN = 46;

        /**
         * Cylindrical Mercator projection
         */
        public static final int CYLINDRICAL_MERCATOR_PROJECTION = 47;

        /**
         * Data
         */
        public static final int DATA = 48;

        /**
         * Database engine
         */
        public static final int DATABASE_ENGINE = 49;

        /**
         * Database URL
         */
        public static final int DATABASE_URL = 50;

        /**
         * {0} data
         */
        public static final int DATA_$1 = 51;

        /**
         * Data are present
         */
        public static final int DATA_ARE_PRESENT = 52;

        /**
         * {0} data base
         */
        public static final int DATA_BASE_$1 = 53;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final int DATA_BASE_$3 = 54;

        /**
         * Data type
         */
        public static final int DATA_TYPE = 55;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final int DATA_TYPE_$2 = 56;

        /**
         * Datum
         */
        public static final int DATUM = 57;

        /**
         * Datum shift
         */
        public static final int DATUM_SHIFT = 58;

        /**
         * day
         */
        public static final int DAY = 315;

        /**
         * days
         */
        public static final int DAYS = 316;

        /**
         * Debug
         */
        public static final int DEBUG = 59;

        /**
         * Decoders
         */
        public static final int DECODERS = 60;

        /**
         * Default
         */
        public static final int DEFAULT = 61;

        /**
         * Default value
         */
        public static final int DEFAULT_VALUE = 62;

        /**
         * Define
         */
        public static final int DEFINE = 317;

        /**
         * Delete
         */
        public static final int DELETE = 318;

        /**
         * Depth
         */
        public static final int DEPTH = 63;

        /**
         * Derived from {0}
         */
        public static final int DERIVED_FROM_$1 = 64;

        /**
         * Description
         */
        public static final int DESCRIPTION = 65;

        /**
         * Directories
         */
        public static final int DIRECTORIES = 66;

        /**
         * Discontinuous
         */
        public static final int DISCONTINUOUS = 67;

        /**
         * Display
         */
        public static final int DISPLAY = 68;

        /**
         * Distance
         */
        public static final int DISTANCE = 69;

        /**
         * Down
         */
        public static final int DOWN = 70;

        /**
         * Download
         */
        public static final int DOWNLOAD = 71;

        /**
         * Downloading
         */
        public static final int DOWNLOADING = 72;

        /**
         * Dublin Julian
         */
        public static final int DUBLIN_JULIAN = 73;

        /**
         * Duplicated value
         */
        public static final int DUPLICATED_VALUE = 74;

        /**
         * Duration
         */
        public static final int DURATION = 319;

        /**
         * Earth gravitational model
         */
        public static final int EARTH_GRAVITATIONAL_MODEL = 75;

        /**
         * East
         */
        public static final int EAST = 76;

        /**
         * Easting
         */
        public static final int EASTING = 77;

        /**
         * Efficiency
         */
        public static final int EFFICIENCY = 78;

        /**
         * Ellipsoid
         */
        public static final int ELLIPSOID = 79;

        /**
         * Ellipsoidal
         */
        public static final int ELLIPSOIDAL = 80;

        /**
         * Ellipsoidal height
         */
        public static final int ELLIPSOIDAL_HEIGHT = 81;

        /**
         * Ellipsoid shift
         */
        public static final int ELLIPSOID_SHIFT = 82;

        /**
         * Empty
         */
        public static final int EMPTY = 83;

        /**
         * Encoders
         */
        public static final int ENCODERS = 84;

        /**
         * End time
         */
        public static final int END_TIME = 85;

        /**
         * Equidistant cylindrical projection
         */
        public static final int EQUIDISTANT_CYLINDRICAL_PROJECTION = 86;

        /**
         * Error
         */
        public static final int ERROR = 87;

        /**
         * Error - {0}
         */
        public static final int ERROR_$1 = 88;

        /**
         * Error filters
         */
        public static final int ERROR_FILTERS = 89;

        /**
         * Event logger
         */
        public static final int EVENT_LOGGER = 90;

        /**
         * Examples
         */
        public static final int EXAMPLES = 91;

        /**
         * Exception
         */
        public static final int EXCEPTION = 92;

        /**
         * Exponential
         */
        public static final int EXPONENTIAL = 93;

        /**
         * Factory
         */
        public static final int FACTORY = 94;

        /**
         * False
         */
        public static final int FALSE = 95;

        /**
         * File
         */
        public static final int FILE = 96;

        /**
         * File {0}
         */
        public static final int FILE_$1 = 97;

        /**
         * Line {1} in file {0}
         */
        public static final int FILE_POSITION_$2 = 98;

        /**
         * Format
         */
        public static final int FORMAT = 99;

        /**
         * Future
         */
        public static final int FUTURE = 100;

        /**
         * Generic cartesian 2D
         */
        public static final int GENERIC_CARTESIAN_2D = 101;

        /**
         * Generic cartesian 3D
         */
        public static final int GENERIC_CARTESIAN_3D = 102;

        /**
         * Geocentric
         */
        public static final int GEOCENTRIC = 103;

        /**
         * Geocentric radius
         */
        public static final int GEOCENTRIC_RADIUS = 104;

        /**
         * Geocentric transform
         */
        public static final int GEOCENTRIC_TRANSFORM = 105;

        /**
         * Geocentric X
         */
        public static final int GEOCENTRIC_X = 106;

        /**
         * Geocentric Y
         */
        public static final int GEOCENTRIC_Y = 107;

        /**
         * Geocentric Z
         */
        public static final int GEOCENTRIC_Z = 108;

        /**
         * Geodetic 2D
         */
        public static final int GEODETIC_2D = 109;

        /**
         * Geodetic 3D
         */
        public static final int GEODETIC_3D = 110;

        /**
         * Geodetic latitude
         */
        public static final int GEODETIC_LATITUDE = 111;

        /**
         * Geodetic longitude
         */
        public static final int GEODETIC_LONGITUDE = 112;

        /**
         * Geographic coordinates
         */
        public static final int GEOGRAPHIC_COORDINATES = 113;

        /**
         * Geoidal
         */
        public static final int GEOIDAL = 114;

        /**
         * Geoid model derived
         */
        public static final int GEOID_MODEL_DERIVED = 115;

        /**
         * Geophysics
         */
        public static final int GEOPHYSICS = 116;

        /**
         * Geospatial
         */
        public static final int GEOSPATIAL = 310;

        /**
         * Greenwich Mean Time (GMT)
         */
        public static final int GMT = 117;

        /**
         * Gradient masks
         */
        public static final int GRADIENT_MASKS = 118;

        /**
         * Gravity-related height
         */
        public static final int GRAVITY_RELATED_HEIGHT = 119;

        /**
         * Gray
         */
        public static final int GRAY = 120;

        /**
         * Gray scale
         */
        public static final int GRAY_SCALE = 121;

        /**
         * Green
         */
        public static final int GREEN = 122;

        /**
         * Grid
         */
        public static final int GRID = 123;

        /**
         * Gridded data
         */
        public static final int GRIDDED_DATA = 323;

        /**
         * Height
         */
        public static final int HEIGHT = 124;

        /**
         * Help
         */
        public static final int HELP = 320;

        /**
         * Hide
         */
        public static final int HIDE = 125;

        /**
         * Hiden
         */
        public static final int HIDEN = 126;

        /**
         * Horizontal
         */
        public static final int HORIZONTAL = 127;

        /**
         * Horizontal component
         */
        public static final int HORIZONTAL_COMPONENT = 128;

        /**
         * Hue
         */
        public static final int HUE = 129;

        /**
         * Identifier
         */
        public static final int IDENTIFIER = 130;

        /**
         * Identity
         */
        public static final int IDENTITY = 131;

        /**
         * Images
         */
        public static final int IMAGES = 132;

        /**
         * Image {0}
         */
        public static final int IMAGE_$1 = 305;

        /**
         * Image of class {0}
         */
        public static final int IMAGE_CLASS_$1 = 133;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final int IMAGE_CODEC_WITH_WORLD_FILE_$2 = 309;

        /**
         * Image list
         */
        public static final int IMAGE_LIST = 134;

        /**
         * Image root directory
         */
        public static final int IMAGE_ROOT_DIRECTORY = 325;

        /**
         * Image size
         */
        public static final int IMAGE_SIZE = 135;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final int IMAGE_SIZE_$3 = 136;

        /**
         * Implementations
         */
        public static final int IMPLEMENTATIONS = 137;

        /**
         * Index
         */
        public static final int INDEX = 138;

        /**
         * Indexed
         */
        public static final int INDEXED = 139;

        /**
         * Informations
         */
        public static final int INFORMATIONS = 140;

        /**
         * Inside
         */
        public static final int INSIDE = 141;

        /**
         * Install
         */
        public static final int INSTALL = 307;

        /**
         * {0} installation
         */
        public static final int INSTALLATION_$1 = 142;

        /**
         * Inverse {0}
         */
        public static final int INVERSE_$1 = 143;

        /**
         * Inverse operation
         */
        public static final int INVERSE_OPERATION = 144;

        /**
         * Inverse transform
         */
        public static final int INVERSE_TRANSFORM = 145;

        /**
         * {0}
         */
        public static final int JAVA_VENDOR_$1 = 146;

        /**
         * Java version {0}
         */
        public static final int JAVA_VERSION_$1 = 147;

        /**
         * Julian
         */
        public static final int JULIAN = 148;

        /**
         * Kernel
         */
        public static final int KERNEL = 149;

        /**
         * Lambert conformal conic projection
         */
        public static final int LAMBERT_CONFORMAL_PROJECTION = 150;

        /**
         * Latitude
         */
        public static final int LATITUDE = 151;

        /**
         * Left
         */
        public static final int LEFT = 152;

        /**
         * Level
         */
        public static final int LEVEL = 153;

        /**
         * Lightness
         */
        public static final int LIGHTNESS = 154;

        /**
         * Lines
         */
        public static final int LINES = 155;

        /**
         * Line {0}
         */
        public static final int LINE_$1 = 156;

        /**
         * Loading…
         */
        public static final int LOADING = 157;

        /**
         * Loading {0}…
         */
        public static final int LOADING_$1 = 158;

        /**
         * Loading headers
         */
        public static final int LOADING_HEADERS = 159;

        /**
         * Loading images {0} and {1}
         */
        public static final int LOADING_IMAGES_$2 = 160;

        /**
         * Loading image {0}
         */
        public static final int LOADING_IMAGE_$1 = 161;

        /**
         * Local
         */
        public static final int LOCAL = 162;

        /**
         * Logarithmic
         */
        public static final int LOGARITHMIC = 163;

        /**
         * Logger
         */
        public static final int LOGGER = 164;

        /**
         * Longitude
         */
        public static final int LONGITUDE = 165;

        /**
         * Magenta
         */
        public static final int MAGENTA = 166;

        /**
         * Magnifier
         */
        public static final int MAGNIFIER = 167;

        /**
         * Mandatory
         */
        public static final int MANDATORY = 168;

        /**
         * Math transform
         */
        public static final int MATH_TRANSFORM = 169;

        /**
         * Maximum
         */
        public static final int MAXIMUM = 170;

        /**
         * Allocated memory: {0} MB
         */
        public static final int MEMORY_HEAP_SIZE_$1 = 171;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final int MEMORY_HEAP_USAGE_$1 = 172;

        /**
         * Message
         */
        public static final int MESSAGE = 173;

        /**
         * Metadata
         */
        public static final int METADATA = 302;

        /**
         * Method
         */
        public static final int METHOD = 174;

        /**
         * Minimum
         */
        public static final int MINIMUM = 175;

        /**
         * Modified Julian
         */
        public static final int MODIFIED_JULIAN = 176;

        /**
         * Molodensky transform
         */
        public static final int MOLODENSKY_TRANSFORM = 177;

        /**
         * … {0} more…
         */
        public static final int MORE_$1 = 178;

        /**
         * NADCON transform
         */
        public static final int NADCON_TRANSFORM = 179;

        /**
         * Name
         */
        public static final int NAME = 180;

        /**
         * No data
         */
        public static final int NODATA = 181;

        /**
         * None
         */
        public static final int NONE = 182;

        /**
         * Normal
         */
        public static final int NORMAL = 183;

        /**
         * North
         */
        public static final int NORTH = 184;

        /**
         * Northing
         */
        public static final int NORTHING = 185;

        /**
         * Note
         */
        public static final int NOTE = 186;

        /**
         * Not installed
         */
        public static final int NOT_INSTALLED = 187;

        /**
         * {0} (no details)
         */
        public static final int NO_DETAILS_$1 = 188;

        /**
         * No duplicated value found.
         */
        public static final int NO_DUPLICATION_FOUND = 189;

        /**
         * Oblique Mercator projection
         */
        public static final int OBLIQUE_MERCATOR_PROJECTION = 190;

        /**
         * Occurence
         */
        public static final int OCCURENCE = 303;

        /**
         * Ok
         */
        public static final int OK = 191;

        /**
         * Operations
         */
        public static final int OPERATIONS = 192;

        /**
         * {0} operation
         */
        public static final int OPERATION_$1 = 193;

        /**
         * Options
         */
        public static final int OPTIONS = 194;

        /**
         * Order
         */
        public static final int ORDER = 195;

        /**
         * Orthodromic distance
         */
        public static final int ORTHODROMIC_DISTANCE = 196;

        /**
         * Orthographic projection
         */
        public static final int ORTHOGRAPHIC_PROJECTION = 197;

        /**
         * Orthometric
         */
        public static final int ORTHOMETRIC = 198;

        /**
         * {0} system
         */
        public static final int OS_NAME_$1 = 199;

        /**
         * Version {0} for {1}
         */
        public static final int OS_VERSION_$2 = 200;

        /**
         * Other
         */
        public static final int OTHER = 201;

        /**
         * Others
         */
        public static final int OTHERS = 202;

        /**
         * Output directory
         */
        public static final int OUTPUT_DIRECTORY = 203;

        /**
         * Outside
         */
        public static final int OUTSIDE = 204;

        /**
         * Palette
         */
        public static final int PALETTE = 205;

        /**
         * Parameter {0}
         */
        public static final int PARAMETER_$1 = 206;

        /**
         * Part
         */
        public static final int PART = 306;

        /**
         * Password
         */
        public static final int PASSWORD = 207;

        /**
         * Past
         */
        public static final int PAST = 208;

        /**
         * Personalized
         */
        public static final int PERSONALIZED = 209;

        /**
         * Pixels
         */
        public static final int PIXELS = 210;

        /**
         * Pixel size
         */
        public static final int PIXEL_SIZE = 312;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final int POINT_COUNT_IN_GRID_$3 = 211;

        /**
         * Predefined kernels
         */
        public static final int PREDEFINED_KERNELS = 212;

        /**
         * Preferences
         */
        public static final int PREFERENCES = 314;

        /**
         * Preferred resolution
         */
        public static final int PREFERRED_RESOLUTION = 213;

        /**
         * Preview
         */
        public static final int PREVIEW = 214;

        /**
         * Progression
         */
        public static final int PROGRESSION = 215;

        /**
         * Projected
         */
        public static final int PROJECTED = 216;

        /**
         * Properties
         */
        public static final int PROPERTIES = 217;

        /**
         * Quit
         */
        public static final int QUIT = 321;

        /**
         * Range
         */
        public static final int RANGE = 218;

        /**
         * {0} bits real number
         */
        public static final int REAL_NUMBER_$1 = 219;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final int RECTANGLE_$4 = 220;

        /**
         * Red
         */
        public static final int RED = 221;

        /**
         * Remove
         */
        public static final int REMOVE = 222;

        /**
         * Reset
         */
        public static final int RESET = 223;

        /**
         * Right
         */
        public static final int RIGHT = 224;

        /**
         * Root directory
         */
        public static final int ROOT_DIRECTORY = 225;

        /**
         * Root mean squared error.
         */
        public static final int ROOT_MEAN_SQUARED_ERROR = 226;

        /**
         * Rotate left
         */
        public static final int ROTATE_LEFT = 227;

        /**
         * Rotate right
         */
        public static final int ROTATE_RIGHT = 228;

        /**
         * Row
         */
        public static final int ROW = 229;

        /**
         * Running tasks
         */
        public static final int RUNNING_TASKS = 230;

        /**
         * Sample model
         */
        public static final int SAMPLE_MODEL = 231;

        /**
         * Saturation
         */
        public static final int SATURATION = 232;

        /**
         * Saving {0}…
         */
        public static final int SAVING_$1 = 233;

        /**
         * Scale 1:{0} (approximative)
         */
        public static final int SCALE_$1 = 234;

        /**
         * Schema
         */
        public static final int SCHEMA = 235;

        /**
         * Search
         */
        public static final int SEARCH = 236;

        /**
         * Selected colors
         */
        public static final int SELECTED_COLORS = 237;

        /**
         * Service
         */
        public static final int SERVICE = 238;

        /**
         * Set preferred resolution
         */
        public static final int SET_PREFERRED_RESOLUTION = 239;

        /**
         * Show magnifier
         */
        public static final int SHOW_MAGNIFIER = 240;

        /**
         * {0} bits signed integer
         */
        public static final int SIGNED_INTEGER_$1 = 241;

        /**
         * Size
         */
        public static final int SIZE = 242;

        /**
         * {0} × {1}
         */
        public static final int SIZE_$2 = 243;

        /**
         * (in angle minutes)
         */
        public static final int SIZE_IN_MINUTES = 244;

        /**
         * Source CRS
         */
        public static final int SOURCE_CRS = 245;

        /**
         * Source point
         */
        public static final int SOURCE_POINT = 246;

        /**
         * South
         */
        public static final int SOUTH = 247;

        /**
         * Southing
         */
        public static final int SOUTHING = 248;

        /**
         * Spherical
         */
        public static final int SPHERICAL = 249;

        /**
         * Spherical latitude
         */
        public static final int SPHERICAL_LATITUDE = 250;

        /**
         * Spherical longitude
         */
        public static final int SPHERICAL_LONGITUDE = 251;

        /**
         * Standard
         */
        public static final int STANDARD = 311;

        /**
         * Started
         */
        public static final int STARTED = 252;

        /**
         * Start time
         */
        public static final int START_TIME = 253;

        /**
         * Stereographic projection
         */
        public static final int STEREOGRAPHIC_PROJECTION = 254;

        /**
         * Subsampling
         */
        public static final int SUBSAMPLING = 255;

        /**
         * Superseded by $1.
         */
        public static final int SUPERSEDED_BY_$1 = 300;

        /**
         * System
         */
        public static final int SYSTEM = 256;

        /**
         * Target
         */
        public static final int TARGET = 257;

        /**
         * Target CRS
         */
        public static final int TARGET_CRS = 258;

        /**
         * Target point
         */
        public static final int TARGET_POINT = 259;

        /**
         * Tasks
         */
        public static final int TASKS = 260;

        /**
         * Temporal
         */
        public static final int TEMPORAL = 261;

        /**
         * Tiles size
         */
        public static final int TILES_SIZE = 262;

        /**
         * Tile cache capacity: {0} MB
         */
        public static final int TILE_CACHE_CAPACITY_$1 = 263;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final int TILE_SIZE_$4 = 264;

        /**
         * Time
         */
        public static final int TIME = 265;

        /**
         * Time
         */
        public static final int TIME_OF_DAY = 266;

        /**
         * Time range
         */
        public static final int TIME_RANGE = 267;

        /**
         * Time zone
         */
        public static final int TIME_ZONE = 268;

        /**
         * Transformation
         */
        public static final int TRANSFORMATION = 269;

        /**
         * Transformation accuracy
         */
        public static final int TRANSFORMATION_ACCURACY = 270;

        /**
         * Transparency
         */
        public static final int TRANSPARENCY = 271;

        /**
         * Transverse Mercator projection
         */
        public static final int TRANSVERSE_MERCATOR_PROJECTION = 272;

        /**
         * True
         */
        public static final int TRUE = 273;

        /**
         * Truncated Julian
         */
        public static final int TRUNCATED_JULIAN = 274;

        /**
         * Type
         */
        public static final int TYPE = 275;

        /**
         * Undefined
         */
        public static final int UNDEFINED = 276;

        /**
         * Units
         */
        public static final int UNITS = 277;

        /**
         * Unknow
         */
        public static final int UNKNOW = 278;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final int UNSIGNED_INTEGER_$2 = 279;

        /**
         * Untitled
         */
        public static final int UNTITLED = 280;

        /**
         * Up
         */
        public static final int UP = 281;

        /**
         * URL
         */
        public static final int URL = 282;

        /**
         * User
         */
        public static final int USER = 283;

        /**
         * Use best resolution
         */
        public static final int USE_BEST_RESOLUTION = 284;

        /**
         * Universal Time (UTC)
         */
        public static final int UTC = 285;

        /**
         * Valid values
         */
        public static final int VALID_VALUES = 304;

        /**
         * Value
         */
        public static final int VALUE = 286;

        /**
         * Value range
         */
        public static final int VALUE_RANGE = 313;

        /**
         * Vendor
         */
        public static final int VENDOR = 287;

        /**
         * Verifying
         */
        public static final int VERIFYING = 308;

        /**
         * Version {0}
         */
        public static final int VERSION_$1 = 288;

        /**
         * "{0}" version
         */
        public static final int VERSION_OF_$1 = 289;

        /**
         * Vertical
         */
        public static final int VERTICAL = 290;

        /**
         * Vertical component
         */
        public static final int VERTICAL_COMPONENT = 291;

        /**
         * Warning
         */
        public static final int WARNING = 292;

        /**
         * West
         */
        public static final int WEST = 293;

        /**
         * Westing
         */
        public static final int WESTING = 294;

        /**
         * Width
         */
        public static final int WIDTH = 295;

        /**
         * Wizards
         */
        public static final int WIZARDS = 322;

        /**
         * Yellow
         */
        public static final int YELLOW = 296;

        /**
         * Zoom in
         */
        public static final int ZOOM_IN = 297;

        /**
         * Close zoom
         */
        public static final int ZOOM_MAX = 298;

        /**
         * Zoom out
         */
        public static final int ZOOM_OUT = 299;
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
    public static InternationalString formatInternational(final int key, final Object arg) {
        return new org.geotoolkit.util.SimpleInternationalString(format(key, arg));
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
    public static InternationalString formatInternational(final int key, final Object... args) {
        return new org.geotoolkit.util.SimpleInternationalString(format(key, args));
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
