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
         * Administrator
         */
        public static final int ADMINISTRATOR = 3;

        /**
         * Affine transform
         */
        public static final int AFFINE_TRANSFORM = 4;

        /**
         * Albers Equal Area projection
         */
        public static final int ALBERS_EQUAL_AREA_PROJECTION = 5;

        /**
         * Alias
         */
        public static final int ALIAS = 6;

        /**
         * All
         */
        public static final int ALL = 7;

        /**
         * All users
         */
        public static final int ALL_USERS = 8;

        /**
         * Altitude
         */
        public static final int ALTITUDE = 9;

        /**
         * Altitudes
         */
        public static final int ALTITUDES = 336;

        /**
         * Apply
         */
        public static final int APPLY = 10;

        /**
         * Attribute
         */
        public static final int ATTRIBUTE = 11;

        /**
         * Authority
         */
        public static final int AUTHORITY = 12;

        /**
         * Automatic
         */
        public static final int AUTOMATIC = 13;

        /**
         * {0} axis
         */
        public static final int AXIS_1 = 14;

        /**
         * Axis changes
         */
        public static final int AXIS_CHANGES = 15;

        /**
         * Azimuth
         */
        public static final int AZIMUTH = 16;

        /**
         * Band
         */
        public static final int BAND = 17;

        /**
         * Band {0}
         */
        public static final int BAND_1 = 352;

        /**
         * Barometric altitude
         */
        public static final int BAROMETRIC_ALTITUDE = 18;

        /**
         * Black
         */
        public static final int BLACK = 19;

        /**
         * Blue
         */
        public static final int BLUE = 20;

        /**
         * Cancel
         */
        public static final int CANCEL = 21;

        /**
         * Cartesian
         */
        public static final int CARTESIAN = 22;

        /**
         * Cartesian 2D
         */
        public static final int CARTESIAN_2D = 23;

        /**
         * Cartesian 3D
         */
        public static final int CARTESIAN_3D = 24;

        /**
         * Cassini-Soldner projection
         */
        public static final int CASSINI_SOLDNER_PROJECTION = 25;

        /**
         * Category
         */
        public static final int CATEGORY = 26;

        /**
         * Choose
         */
        public static final int CHOOSE = 27;

        /**
         * Class
         */
        public static final int CLASS = 28;

        /**
         * Classic
         */
        public static final int CLASSIC = 29;

        /**
         * Close
         */
        public static final int CLOSE = 30;

        /**
         * Code
         */
        public static final int CODE = 31;

        /**
         * Colors
         */
        public static final int COLORS = 32;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final int COLOR_COUNT_1 = 33;

        /**
         * Color model
         */
        public static final int COLOR_MODEL = 34;

        /**
         * Color space
         */
        public static final int COLOR_SPACE = 35;

        /**
         * Column
         */
        public static final int COLUMN = 36;

        /**
         * Columns
         */
        public static final int COLUMNS = 37;

        /**
         * Commands
         */
        public static final int COMMANDS = 38;

        /**
         * Compare with
         */
        public static final int COMPARE_WITH = 39;

        /**
         * Completed
         */
        public static final int COMPLETED = 40;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final int COMPONENT_COUNT_1 = 41;

        /**
         * Configure
         */
        public static final int CONFIGURE = 42;

        /**
         * Confirm
         */
        public static final int CONFIRM = 43;

        /**
         * Connection parameters
         */
        public static final int CONNECTION_PARAMETERS = 44;

        /**
         * Conversion
         */
        public static final int CONVERSION = 45;

        /**
         * Conversion and transformation
         */
        public static final int CONVERSION_AND_TRANSFORMATION = 46;

        /**
         * Coordinates selection
         */
        public static final int COORDINATES_SELECTION = 47;

        /**
         * Coordinate format
         */
        public static final int COORDINATE_FORMAT = 48;

        /**
         * Coordinate Reference System
         */
        public static final int COORDINATE_REFERENCE_SYSTEM = 49;

        /**
         * Current user
         */
        public static final int CURRENT_USER = 50;

        /**
         * Cyan
         */
        public static final int CYAN = 51;

        /**
         * Cylindrical Mercator projection
         */
        public static final int CYLINDRICAL_MERCATOR_PROJECTION = 52;

        /**
         * Data
         */
        public static final int DATA = 53;

        /**
         * Database
         */
        public static final int DATABASE = 54;

        /**
         * Database engine
         */
        public static final int DATABASE_ENGINE = 55;

        /**
         * Database URL
         */
        public static final int DATABASE_URL = 56;

        /**
         * {0} data
         */
        public static final int DATA_1 = 57;

        /**
         * Data are present
         */
        public static final int DATA_ARE_PRESENT = 58;

        /**
         * {0} data base
         */
        public static final int DATA_BASE_1 = 59;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final int DATA_BASE_3 = 60;

        /**
         * Data type
         */
        public static final int DATA_TYPE = 61;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final int DATA_TYPE_2 = 62;

        /**
         * Datum
         */
        public static final int DATUM = 63;

        /**
         * Datum shift
         */
        public static final int DATUM_SHIFT = 64;

        /**
         * day
         */
        public static final int DAY = 65;

        /**
         * days
         */
        public static final int DAYS = 66;

        /**
         * Debug
         */
        public static final int DEBUG = 67;

        /**
         * Decoders
         */
        public static final int DECODERS = 68;

        /**
         * Default
         */
        public static final int DEFAULT = 69;

        /**
         * Default value
         */
        public static final int DEFAULT_VALUE = 70;

        /**
         * Define
         */
        public static final int DEFINE = 71;

        /**
         * Delete
         */
        public static final int DELETE = 72;

        /**
         * Depth
         */
        public static final int DEPTH = 73;

        /**
         * Derived from {0}
         */
        public static final int DERIVED_FROM_1 = 74;

        /**
         * Description
         */
        public static final int DESCRIPTION = 75;

        /**
         * Directories
         */
        public static final int DIRECTORIES = 76;

        /**
         * Discontinuous
         */
        public static final int DISCONTINUOUS = 77;

        /**
         * Display
         */
        public static final int DISPLAY = 78;

        /**
         * Distance
         */
        public static final int DISTANCE = 79;

        /**
         * Domain
         */
        public static final int DOMAIN = 337;

        /**
         * Down
         */
        public static final int DOWN = 80;

        /**
         * Download
         */
        public static final int DOWNLOAD = 81;

        /**
         * Downloading
         */
        public static final int DOWNLOADING = 82;

        /**
         * Dublin Julian
         */
        public static final int DUBLIN_JULIAN = 83;

        /**
         * Duplicated value
         */
        public static final int DUPLICATED_VALUE = 84;

        /**
         * Duration
         */
        public static final int DURATION = 85;

        /**
         * Earth gravitational model
         */
        public static final int EARTH_GRAVITATIONAL_MODEL = 86;

        /**
         * East
         */
        public static final int EAST = 87;

        /**
         * Easting
         */
        public static final int EASTING = 88;

        /**
         * Edit
         */
        public static final int EDIT = 346;

        /**
         * Efficiency
         */
        public static final int EFFICIENCY = 89;

        /**
         * Ellipsoid
         */
        public static final int ELLIPSOID = 90;

        /**
         * Ellipsoidal
         */
        public static final int ELLIPSOIDAL = 91;

        /**
         * Ellipsoidal height
         */
        public static final int ELLIPSOIDAL_HEIGHT = 92;

        /**
         * Ellipsoid shift
         */
        public static final int ELLIPSOID_SHIFT = 93;

        /**
         * Empty
         */
        public static final int EMPTY = 94;

        /**
         * Encoders
         */
        public static final int ENCODERS = 95;

        /**
         * End time
         */
        public static final int END_TIME = 96;

        /**
         * Equidistant cylindrical projection
         */
        public static final int EQUIDISTANT_CYLINDRICAL_PROJECTION = 97;

        /**
         * Error
         */
        public static final int ERROR = 98;

        /**
         * Error - {0}
         */
        public static final int ERROR_1 = 99;

        /**
         * Error filters
         */
        public static final int ERROR_FILTERS = 100;

        /**
         * Event logger
         */
        public static final int EVENT_LOGGER = 101;

        /**
         * Examples
         */
        public static final int EXAMPLES = 102;

        /**
         * Exception
         */
        public static final int EXCEPTION = 103;

        /**
         * Exponential
         */
        public static final int EXPONENTIAL = 104;

        /**
         * Factory
         */
        public static final int FACTORY = 105;

        /**
         * False
         */
        public static final int FALSE = 106;

        /**
         * File
         */
        public static final int FILE = 107;

        /**
         * {0} files
         */
        public static final int FILES_1 = 108;

        /**
         * File {0}
         */
        public static final int FILE_1 = 109;

        /**
         * Line {1} in file {0}
         */
        public static final int FILE_POSITION_2 = 110;

        /**
         * Format
         */
        public static final int FORMAT = 111;

        /**
         * Future
         */
        public static final int FUTURE = 112;

        /**
         * General
         */
        public static final int GENERAL = 344;

        /**
         * Generic Cartesian 2D
         */
        public static final int GENERIC_CARTESIAN_2D = 113;

        /**
         * Generic Cartesian 3D
         */
        public static final int GENERIC_CARTESIAN_3D = 114;

        /**
         * Geocentric
         */
        public static final int GEOCENTRIC = 115;

        /**
         * Geocentric radius
         */
        public static final int GEOCENTRIC_RADIUS = 116;

        /**
         * Geocentric transform
         */
        public static final int GEOCENTRIC_TRANSFORM = 117;

        /**
         * Geocentric X
         */
        public static final int GEOCENTRIC_X = 118;

        /**
         * Geocentric Y
         */
        public static final int GEOCENTRIC_Y = 119;

        /**
         * Geocentric Z
         */
        public static final int GEOCENTRIC_Z = 120;

        /**
         * Geodetic 2D
         */
        public static final int GEODETIC_2D = 121;

        /**
         * Geodetic 3D
         */
        public static final int GEODETIC_3D = 122;

        /**
         * Geodetic latitude
         */
        public static final int GEODETIC_LATITUDE = 123;

        /**
         * Geodetic longitude
         */
        public static final int GEODETIC_LONGITUDE = 124;

        /**
         * Geographic coordinates
         */
        public static final int GEOGRAPHIC_COORDINATES = 125;

        /**
         * Geoidal
         */
        public static final int GEOIDAL = 126;

        /**
         * Geoid model derived
         */
        public static final int GEOID_MODEL_DERIVED = 127;

        /**
         * Geophysics
         */
        public static final int GEOPHYSICS = 128;

        /**
         * Geospatial
         */
        public static final int GEOSPATIAL = 129;

        /**
         * Greenwich Mean Time (GMT)
         */
        public static final int GMT = 130;

        /**
         * Gradient masks
         */
        public static final int GRADIENT_MASKS = 131;

        /**
         * Gravity-related height
         */
        public static final int GRAVITY_RELATED_HEIGHT = 132;

        /**
         * Gray
         */
        public static final int GRAY = 133;

        /**
         * Gray scale
         */
        public static final int GRAY_SCALE = 134;

        /**
         * Green
         */
        public static final int GREEN = 135;

        /**
         * Grid
         */
        public static final int GRID = 136;

        /**
         * Gridded data
         */
        public static final int GRIDDED_DATA = 137;

        /**
         * Height
         */
        public static final int HEIGHT = 138;

        /**
         * Help
         */
        public static final int HELP = 139;

        /**
         * Hidden
         */
        public static final int HIDDEN = 141;

        /**
         * Hide
         */
        public static final int HIDE = 140;

        /**
         * Horizontal
         */
        public static final int HORIZONTAL = 142;

        /**
         * Horizontal component
         */
        public static final int HORIZONTAL_COMPONENT = 143;

        /**
         * Hue
         */
        public static final int HUE = 144;

        /**
         * {0}-{1}
         */
        public static final int HYPHEN_2 = 354;

        /**
         * Identification
         */
        public static final int IDENTIFICATION = 343;

        /**
         * Identifier
         */
        public static final int IDENTIFIER = 145;

        /**
         * Identity
         */
        public static final int IDENTITY = 146;

        /**
         * Images
         */
        public static final int IMAGES = 147;

        /**
         * Image {0}
         */
        public static final int IMAGE_1 = 148;

        /**
         * Image of class {0}
         */
        public static final int IMAGE_CLASS_1 = 149;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final int IMAGE_CODEC_WITH_WORLD_FILE_2 = 150;

        /**
         * Image list
         */
        public static final int IMAGE_LIST = 151;

        /**
         * Image root directory
         */
        public static final int IMAGE_ROOT_DIRECTORY = 152;

        /**
         * Image size
         */
        public static final int IMAGE_SIZE = 153;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final int IMAGE_SIZE_3 = 154;

        /**
         * Implementations
         */
        public static final int IMPLEMENTATIONS = 155;

        /**
         * Index
         */
        public static final int INDEX = 156;

        /**
         * Indexed
         */
        public static final int INDEXED = 157;

        /**
         * Informations
         */
        public static final int INFORMATIONS = 158;

        /**
         * Inside
         */
        public static final int INSIDE = 159;

        /**
         * Install
         */
        public static final int INSTALL = 160;

        /**
         * {0} installation
         */
        public static final int INSTALLATION_1 = 161;

        /**
         * Inverse {0}
         */
        public static final int INVERSE_1 = 162;

        /**
         * Inverse operation
         */
        public static final int INVERSE_OPERATION = 163;

        /**
         * Inverse transform
         */
        public static final int INVERSE_TRANSFORM = 164;

        /**
         * {0}
         */
        public static final int JAVA_VENDOR_1 = 165;

        /**
         * Java version {0}
         */
        public static final int JAVA_VERSION_1 = 166;

        /**
         * Julian
         */
        public static final int JULIAN = 167;

        /**
         * Kernel
         */
        public static final int KERNEL = 168;

        /**
         * Lambert conformal conic projection
         */
        public static final int LAMBERT_CONFORMAL_PROJECTION = 169;

        /**
         * Latitude
         */
        public static final int LATITUDE = 170;

        /**
         * Layers
         */
        public static final int LAYERS = 339;

        /**
         * Left
         */
        public static final int LEFT = 171;

        /**
         * Level
         */
        public static final int LEVEL = 172;

        /**
         * Lightness
         */
        public static final int LIGHTNESS = 173;

        /**
         * Lines
         */
        public static final int LINES = 174;

        /**
         * Line {0}
         */
        public static final int LINE_1 = 175;

        /**
         * Loading…
         */
        public static final int LOADING = 176;

        /**
         * Loading {0}…
         */
        public static final int LOADING_1 = 177;

        /**
         * Loading headers
         */
        public static final int LOADING_HEADERS = 178;

        /**
         * Loading images {0} and {1}
         */
        public static final int LOADING_IMAGES_2 = 179;

        /**
         * Loading image {0}
         */
        public static final int LOADING_IMAGE_1 = 180;

        /**
         * Local
         */
        public static final int LOCAL = 181;

        /**
         * Logarithmic
         */
        public static final int LOGARITHMIC = 182;

        /**
         * Logger
         */
        public static final int LOGGER = 183;

        /**
         * Longitude
         */
        public static final int LONGITUDE = 184;

        /**
         * Magenta
         */
        public static final int MAGENTA = 185;

        /**
         * Magnifier
         */
        public static final int MAGNIFIER = 186;

        /**
         * Mandatory
         */
        public static final int MANDATORY = 187;

        /**
         * Math transform
         */
        public static final int MATH_TRANSFORM = 188;

        /**
         * Maximum
         */
        public static final int MAXIMUM = 189;

        /**
         * Allocated memory: {0} MB
         */
        public static final int MEMORY_HEAP_SIZE_1 = 190;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final int MEMORY_HEAP_USAGE_1 = 191;

        /**
         * Message
         */
        public static final int MESSAGE = 192;

        /**
         * Metadata
         */
        public static final int METADATA = 193;

        /**
         * Method
         */
        public static final int METHOD = 194;

        /**
         * Minimum
         */
        public static final int MINIMUM = 195;

        /**
         * Modified Julian
         */
        public static final int MODIFIED_JULIAN = 196;

        /**
         * Molodensky transform
         */
        public static final int MOLODENSKY_TRANSFORM = 197;

        /**
         * … {0} more…
         */
        public static final int MORE_1 = 198;

        /**
         * NADCON transform
         */
        public static final int NADCON_TRANSFORM = 199;

        /**
         * Name
         */
        public static final int NAME = 200;

        /**
         * Navigate
         */
        public static final int NAVIGATE = 201;

        /**
         * New format
         */
        public static final int NEW_FORMAT = 353;

        /**
         * New layer
         */
        public static final int NEW_LAYER = 341;

        /**
         * No data
         */
        public static final int NODATA = 202;

        /**
         * None
         */
        public static final int NONE = 203;

        /**
         * Normal
         */
        public static final int NORMAL = 204;

        /**
         * North
         */
        public static final int NORTH = 205;

        /**
         * Northing
         */
        public static final int NORTHING = 206;

        /**
         * Note
         */
        public static final int NOTE = 207;

        /**
         * Not installed
         */
        public static final int NOT_INSTALLED = 208;

        /**
         * {0} (no details)
         */
        public static final int NO_DETAILS_1 = 209;

        /**
         * No duplicated value found.
         */
        public static final int NO_DUPLICATION_FOUND = 210;

        /**
         * Oblique Mercator projection
         */
        public static final int OBLIQUE_MERCATOR_PROJECTION = 211;

        /**
         * Occurrence
         */
        public static final int OCCURRENCE = 212;

        /**
         * Offset
         */
        public static final int OFFSET = 349;

        /**
         * Ok
         */
        public static final int OK = 213;

        /**
         * Operations
         */
        public static final int OPERATIONS = 214;

        /**
         * {0} operation
         */
        public static final int OPERATION_1 = 215;

        /**
         * Options
         */
        public static final int OPTIONS = 216;

        /**
         * Order
         */
        public static final int ORDER = 217;

        /**
         * Orthodromic distance
         */
        public static final int ORTHODROMIC_DISTANCE = 218;

        /**
         * Orthographic projection
         */
        public static final int ORTHOGRAPHIC_PROJECTION = 219;

        /**
         * Orthometric
         */
        public static final int ORTHOMETRIC = 220;

        /**
         * {0} system
         */
        public static final int OS_NAME_1 = 221;

        /**
         * Version {0} for {1}
         */
        public static final int OS_VERSION_2 = 222;

        /**
         * Other
         */
        public static final int OTHER = 223;

        /**
         * Others
         */
        public static final int OTHERS = 224;

        /**
         * Output directory
         */
        public static final int OUTPUT_DIRECTORY = 225;

        /**
         * Outside
         */
        public static final int OUTSIDE = 226;

        /**
         * Palette
         */
        public static final int PALETTE = 227;

        /**
         * Parameter {0}
         */
        public static final int PARAMETER_1 = 228;

        /**
         * Part
         */
        public static final int PART = 229;

        /**
         * Password
         */
        public static final int PASSWORD = 230;

        /**
         * Past
         */
        public static final int PAST = 231;

        /**
         * Paused
         */
        public static final int PAUSED = 355;

        /**
         * Personalized
         */
        public static final int PERSONALIZED = 232;

        /**
         * Pixels
         */
        public static final int PIXELS = 233;

        /**
         * Pixel size
         */
        public static final int PIXEL_SIZE = 234;

        public static final int PIXEL_VALUES = 348;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final int POINT_COUNT_IN_GRID_3 = 235;

        /**
         * Port
         */
        public static final int PORT = 236;

        /**
         * Predefined kernels
         */
        public static final int PREDEFINED_KERNELS = 237;

        /**
         * Preferences
         */
        public static final int PREFERENCES = 238;

        /**
         * Preferred resolution
         */
        public static final int PREFERRED_RESOLUTION = 239;

        /**
         * Preview
         */
        public static final int PREVIEW = 240;

        /**
         * Progression
         */
        public static final int PROGRESSION = 241;

        /**
         * Projected
         */
        public static final int PROJECTED = 242;

        /**
         * Properties
         */
        public static final int PROPERTIES = 243;

        /**
         * Properties of {0}
         */
        public static final int PROPERTIES_OF_1 = 338;

        /**
         * Quit
         */
        public static final int QUIT = 244;

        /**
         * Range
         */
        public static final int RANGE = 245;

        /**
         * {0} bits real number
         */
        public static final int REAL_NUMBER_1 = 246;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final int RECTANGLE_4 = 247;

        /**
         * Red
         */
        public static final int RED = 248;

        /**
         * Refresh
         */
        public static final int REFRESH = 335;

        /**
         * Remarks
         */
        public static final int REMARKS = 345;

        /**
         * Remove
         */
        public static final int REMOVE = 249;

        /**
         * Reset
         */
        public static final int RESET = 250;

        /**
         * Resolution
         */
        public static final int RESOLUTION = 340;

        /**
         * Resumed
         */
        public static final int RESUMED = 356;

        /**
         * RGF93 transform
         */
        public static final int RGF93_TRANSFORM = 342;

        /**
         * Right
         */
        public static final int RIGHT = 251;

        /**
         * Root directory
         */
        public static final int ROOT_DIRECTORY = 252;

        /**
         * Root mean squared error.
         */
        public static final int ROOT_MEAN_SQUARED_ERROR = 253;

        /**
         * Rotate left
         */
        public static final int ROTATE_LEFT = 254;

        /**
         * Rotate right
         */
        public static final int ROTATE_RIGHT = 255;

        /**
         * Row
         */
        public static final int ROW = 256;

        /**
         * Running tasks
         */
        public static final int RUNNING_TASKS = 257;

        /**
         * Sample dimensions
         */
        public static final int SAMPLE_DIMENSIONS = 351;

        /**
         * Sample model
         */
        public static final int SAMPLE_MODEL = 258;

        /**
         * Saturation
         */
        public static final int SATURATION = 259;

        /**
         * Saving {0}…
         */
        public static final int SAVING_1 = 260;

        /**
         * Scale
         */
        public static final int SCALE = 350;

        /**
         * Scale 1:{0} (approximative)
         */
        public static final int SCALE_1 = 261;

        /**
         * Schema
         */
        public static final int SCHEMA = 262;

        /**
         * Search
         */
        public static final int SEARCH = 263;

        /**
         * Selected colors
         */
        public static final int SELECTED_COLORS = 264;

        /**
         * Server
         */
        public static final int SERVER = 265;

        /**
         * Service
         */
        public static final int SERVICE = 266;

        /**
         * Set preferred resolution
         */
        public static final int SET_PREFERRED_RESOLUTION = 267;

        /**
         * Show magnifier
         */
        public static final int SHOW_MAGNIFIER = 268;

        /**
         * {0} bits signed integer
         */
        public static final int SIGNED_INTEGER_1 = 269;

        /**
         * Size
         */
        public static final int SIZE = 270;

        /**
         * {0} × {1}
         */
        public static final int SIZE_2 = 271;

        /**
         * (in angle minutes)
         */
        public static final int SIZE_IN_MINUTES = 272;

        /**
         * Source CRS
         */
        public static final int SOURCE_CRS = 273;

        /**
         * Source point
         */
        public static final int SOURCE_POINT = 274;

        /**
         * South
         */
        public static final int SOUTH = 275;

        /**
         * Southing
         */
        public static final int SOUTHING = 276;

        /**
         * Spatial objects
         */
        public static final int SPATIAL_OBJECTS = 277;

        /**
         * Spherical
         */
        public static final int SPHERICAL = 278;

        /**
         * Spherical latitude
         */
        public static final int SPHERICAL_LATITUDE = 279;

        /**
         * Spherical longitude
         */
        public static final int SPHERICAL_LONGITUDE = 280;

        /**
         * Standard
         */
        public static final int STANDARD = 281;

        /**
         * Started
         */
        public static final int STARTED = 282;

        /**
         * Start time
         */
        public static final int START_TIME = 283;

        /**
         * Stereographic projection
         */
        public static final int STEREOGRAPHIC_PROJECTION = 284;

        /**
         * Subsampling
         */
        public static final int SUBSAMPLING = 285;

        /**
         * Superseded by {0}.
         */
        public static final int SUPERSEDED_BY_1 = 286;

        /**
         * System
         */
        public static final int SYSTEM = 287;

        /**
         * Target
         */
        public static final int TARGET = 288;

        /**
         * Target CRS
         */
        public static final int TARGET_CRS = 289;

        /**
         * Target point
         */
        public static final int TARGET_POINT = 290;

        /**
         * Tasks
         */
        public static final int TASKS = 291;

        /**
         * Temporal
         */
        public static final int TEMPORAL = 292;

        /**
         * Tiles size
         */
        public static final int TILES_SIZE = 293;

        /**
         * Tile cache capacity: {0} Mb
         */
        public static final int TILE_CACHE_CAPACITY_1 = 294;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final int TILE_SIZE_4 = 295;

        /**
         * Time
         */
        public static final int TIME = 296;

        /**
         * Time
         */
        public static final int TIME_OF_DAY = 297;

        /**
         * Time range
         */
        public static final int TIME_RANGE = 298;

        /**
         * Time zone
         */
        public static final int TIME_ZONE = 299;

        /**
         * Transfert function
         */
        public static final int TRANSFERT_FUNCTION = 347;

        /**
         * Transformation
         */
        public static final int TRANSFORMATION = 300;

        /**
         * Transformation accuracy
         */
        public static final int TRANSFORMATION_ACCURACY = 301;

        /**
         * Transparency
         */
        public static final int TRANSPARENCY = 302;

        /**
         * Transverse Mercator projection
         */
        public static final int TRANSVERSE_MERCATOR_PROJECTION = 303;

        /**
         * True
         */
        public static final int TRUE = 304;

        /**
         * Truncated Julian
         */
        public static final int TRUNCATED_JULIAN = 305;

        /**
         * Type
         */
        public static final int TYPE = 306;

        /**
         * Undefined
         */
        public static final int UNDEFINED = 307;

        /**
         * Units
         */
        public static final int UNITS = 308;

        /**
         * Unknown
         */
        public static final int UNKNOWN = 309;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final int UNSIGNED_INTEGER_2 = 310;

        /**
         * Untitled
         */
        public static final int UNTITLED = 311;

        /**
         * Up
         */
        public static final int UP = 312;

        /**
         * URL
         */
        public static final int URL = 313;

        /**
         * User
         */
        public static final int USER = 314;

        /**
         * Use best resolution
         */
        public static final int USE_BEST_RESOLUTION = 315;

        /**
         * Universal Time (UTC)
         */
        public static final int UTC = 316;

        /**
         * Valid values
         */
        public static final int VALID_VALUES = 317;

        /**
         * Value
         */
        public static final int VALUE = 318;

        /**
         * Value range
         */
        public static final int VALUE_RANGE = 319;

        /**
         * Vendor
         */
        public static final int VENDOR = 320;

        /**
         * Verifying
         */
        public static final int VERIFYING = 321;

        /**
         * Version {0}
         */
        public static final int VERSION_1 = 322;

        /**
         * “{0}” version
         */
        public static final int VERSION_OF_1 = 323;

        /**
         * Vertical
         */
        public static final int VERTICAL = 324;

        /**
         * Vertical component
         */
        public static final int VERTICAL_COMPONENT = 325;

        /**
         * Warning
         */
        public static final int WARNING = 326;

        /**
         * West
         */
        public static final int WEST = 327;

        /**
         * Westing
         */
        public static final int WESTING = 328;

        /**
         * Width
         */
        public static final int WIDTH = 329;

        /**
         * Wizards
         */
        public static final int WIZARDS = 330;

        /**
         * Yellow
         */
        public static final int YELLOW = 331;

        /**
         * Zoom in
         */
        public static final int ZOOM_IN = 332;

        /**
         * Close zoom
         */
        public static final int ZOOM_MAX = 333;

        /**
         * Zoom out
         */
        public static final int ZOOM_OUT = 334;
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
     * Gets a string for the given key are replace all occurrence of "{0}"
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
     * Gets a string for the given key are replace all occurrence of "{0}",
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
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
