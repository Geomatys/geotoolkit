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
        public static final short About = 1;

        /**
         * Abridged Molodensky transform
         */
        public static final short AbridgedMolodenskyTransform = 2;

        /**
         * Add
         */
        public static final short Add = 3;

        /**
         * Administrator
         */
        public static final short Administrator = 4;

        /**
         * Albers Equal Area projection
         */
        public static final short AlbersEqualAreaProjection = 5;

        /**
         * All
         */
        public static final short All = 6;

        /**
         * All users
         */
        public static final short AllUsers = 7;

        /**
         * Altitude
         */
        public static final short Altitude = 8;

        /**
         * Altitudes
         */
        public static final short Altitudes = 9;

        /**
         * Apply
         */
        public static final short Apply = 10;

        /**
         * Authority
         */
        public static final short Authority = 11;

        /**
         * Automatic
         */
        public static final short Automatic = 12;

        /**
         * Axis changes
         */
        public static final short AxisChanges = 13;

        /**
         * {0} axis
         */
        public static final short Axis_1 = 14;

        /**
         * Azimuth
         */
        public static final short Azimuth = 15;

        /**
         * Band
         */
        public static final short Band = 16;

        /**
         * Band {0}
         */
        public static final short Band_1 = 17;

        /**
         * Cancel
         */
        public static final short Cancel = 18;

        /**
         * Cartesian
         */
        public static final short Cartesian = 19;

        /**
         * Cartesian 2D
         */
        public static final short Cartesian2d = 20;

        /**
         * Cartesian 3D
         */
        public static final short Cartesian3d = 21;

        /**
         * Cassini-Soldner projection
         */
        public static final short CassiniSoldnerProjection = 22;

        /**
         * Category
         */
        public static final short Category = 23;

        /**
         * Choose
         */
        public static final short Choose = 24;

        /**
         * Class
         */
        public static final short Class = 25;

        /**
         * Close
         */
        public static final short Close = 26;

        /**
         * Code
         */
        public static final short Code = 27;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final short ColorCount_1 = 28;

        /**
         * Color model
         */
        public static final short ColorModel = 29;

        /**
         * Color space
         */
        public static final short ColorSpace = 30;

        /**
         * Colors
         */
        public static final short Colors = 31;

        /**
         * Column
         */
        public static final short Column = 32;

        /**
         * Columns
         */
        public static final short Columns = 33;

        /**
         * Commands
         */
        public static final short Commands = 34;

        /**
         * Compare with
         */
        public static final short CompareWith = 35;

        /**
         * Completed
         */
        public static final short Completed = 36;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final short ComponentCount_1 = 37;

        /**
         * Configure
         */
        public static final short Configure = 38;

        /**
         * Confirm
         */
        public static final short Confirm = 39;

        /**
         * Connection parameters
         */
        public static final short ConnectionParameters = 40;

        /**
         * Coordinate Reference System
         */
        public static final short CoordinateReferenceSystem = 41;

        /**
         * Coordinates selection
         */
        public static final short CoordinatesSelection = 42;

        /**
         * Current user
         */
        public static final short CurrentUser = 43;

        /**
         * Data
         */
        public static final short Data = 44;

        /**
         * Data are present
         */
        public static final short DataArePresent = 45;

        /**
         * {0} data base
         */
        public static final short DataBase_1 = 46;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final short DataBase_3 = 47;

        /**
         * Data type
         */
        public static final short DataType = 48;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final short DataType_2 = 49;

        /**
         * {0} data
         */
        public static final short Data_1 = 50;

        /**
         * Database
         */
        public static final short Database = 51;

        /**
         * Database engine
         */
        public static final short DatabaseEngine = 52;

        /**
         * Database URL
         */
        public static final short DatabaseUrl = 53;

        /**
         * Datum
         */
        public static final short Datum = 54;

        /**
         * Datum shift
         */
        public static final short DatumShift = 55;

        /**
         * day
         */
        public static final short Day = 56;

        /**
         * days
         */
        public static final short Days = 57;

        /**
         * Debug
         */
        public static final short Debug = 58;

        /**
         * Decoders
         */
        public static final short Decoders = 59;

        /**
         * Default
         */
        public static final short Default = 60;

        /**
         * Define
         */
        public static final short Define = 61;

        /**
         * Delete
         */
        public static final short Delete = 62;

        /**
         * Depth
         */
        public static final short Depth = 63;

        /**
         * Derived from {0}
         */
        public static final short DerivedFrom_1 = 64;

        /**
         * Description
         */
        public static final short Description = 65;

        /**
         * Directories
         */
        public static final short Directories = 66;

        /**
         * Display
         */
        public static final short Display = 67;

        /**
         * Distance
         */
        public static final short Distance = 68;

        /**
         * Domain
         */
        public static final short Domain = 69;

        /**
         * Down
         */
        public static final short Down = 70;

        /**
         * Download
         */
        public static final short Download = 71;

        /**
         * Downloading
         */
        public static final short Downloading = 72;

        /**
         * Duplicated value
         */
        public static final short DuplicatedValue = 73;

        /**
         * Duration
         */
        public static final short Duration = 74;

        /**
         * Easting
         */
        public static final short Easting = 75;

        /**
         * Edit
         */
        public static final short Edit = 76;

        /**
         * Efficiency
         */
        public static final short Efficiency = 77;

        /**
         * Ellipsoid
         */
        public static final short Ellipsoid = 78;

        /**
         * Ellipsoid shift
         */
        public static final short EllipsoidShift = 79;

        /**
         * Ellipsoidal height
         */
        public static final short EllipsoidalHeight = 80;

        /**
         * Empty
         */
        public static final short Empty = 81;

        /**
         * Encoders
         */
        public static final short Encoders = 82;

        /**
         * End time
         */
        public static final short EndTime = 83;

        /**
         * Error
         */
        public static final short Error = 84;

        /**
         * Error filters
         */
        public static final short ErrorFilters = 85;

        /**
         * Error - {0}
         */
        public static final short Error_1 = 86;

        /**
         * Event logger
         */
        public static final short EventLogger = 87;

        /**
         * Examples
         */
        public static final short Examples = 88;

        /**
         * Exception
         */
        public static final short Exception = 89;

        /**
         * Exponential
         */
        public static final short Exponential = 90;

        /**
         * False
         */
        public static final short False = 91;

        /**
         * File
         */
        public static final short File = 92;

        /**
         * Line {1} in file {0}
         */
        public static final short FilePosition_2 = 93;

        /**
         * File {0}
         */
        public static final short File_1 = 94;

        /**
         * {0} files
         */
        public static final short Files_1 = 95;

        /**
         * Format
         */
        public static final short Format = 96;

        /**
         * General
         */
        public static final short General = 97;

        /**
         * Generic Cartesian 2D
         */
        public static final short GenericCartesian2d = 98;

        /**
         * Generic Cartesian 3D
         */
        public static final short GenericCartesian3d = 99;

        /**
         * Geocentric
         */
        public static final short Geocentric = 100;

        /**
         * Geocentric radius
         */
        public static final short GeocentricRadius = 101;

        /**
         * Geocentric transform
         */
        public static final short GeocentricTransform = 102;

        /**
         * Geocentric X
         */
        public static final short GeocentricX = 103;

        /**
         * Geocentric Y
         */
        public static final short GeocentricY = 104;

        /**
         * Geocentric Z
         */
        public static final short GeocentricZ = 105;

        /**
         * Geodetic 2D
         */
        public static final short Geodetic2d = 106;

        /**
         * Geodetic 3D
         */
        public static final short Geodetic3d = 107;

        /**
         * Geodetic latitude
         */
        public static final short GeodeticLatitude = 108;

        /**
         * Geodetic longitude
         */
        public static final short GeodeticLongitude = 109;

        /**
         * Geographic coordinates
         */
        public static final short GeographicCoordinates = 110;

        /**
         * Geospatial
         */
        public static final short Geospatial = 111;

        /**
         * Gradient masks
         */
        public static final short GradientMasks = 112;

        /**
         * Gravity-related height
         */
        public static final short GravityRelatedHeight = 113;

        /**
         * Gray scale
         */
        public static final short GrayScale = 114;

        /**
         * Grid
         */
        public static final short Grid = 115;

        /**
         * Gridded data
         */
        public static final short GriddedData = 116;

        /**
         * Height
         */
        public static final short Height = 117;

        /**
         * Help
         */
        public static final short Help = 118;

        /**
         * Hide
         */
        public static final short Hide = 119;

        /**
         * Horizontal
         */
        public static final short Horizontal = 120;

        /**
         * Horizontal component
         */
        public static final short HorizontalComponent = 121;

        /**
         * {0}-{1}
         */
        public static final short Hyphen_2 = 122;

        /**
         * Identification
         */
        public static final short Identification = 123;

        /**
         * Identifier
         */
        public static final short Identifier = 124;

        /**
         * Identity
         */
        public static final short Identity = 125;

        /**
         * Image of class {0}
         */
        public static final short ImageClass_1 = 126;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final short ImageCodecWithWorldFile_2 = 127;

        /**
         * Image list
         */
        public static final short ImageList = 128;

        /**
         * Image root directory
         */
        public static final short ImageRootDirectory = 129;

        /**
         * Image size
         */
        public static final short ImageSize = 130;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final short ImageSize_3 = 131;

        /**
         * Image {0}
         */
        public static final short Image_1 = 132;

        /**
         * Images
         */
        public static final short Images = 133;

        /**
         * Implementations
         */
        public static final short Implementations = 134;

        /**
         * Index
         */
        public static final short Index = 135;

        /**
         * Informations
         */
        public static final short Informations = 136;

        /**
         * Install
         */
        public static final short Install = 137;

        /**
         * {0} installation
         */
        public static final short Installation_1 = 138;

        /**
         * Inverse operation
         */
        public static final short InverseOperation = 139;

        /**
         * Inverse transform
         */
        public static final short InverseTransform = 140;

        /**
         * {0}
         */
        public static final short JavaVendor_1 = 141;

        /**
         * Java version {0}
         */
        public static final short JavaVersion_1 = 142;

        /**
         * Kernel
         */
        public static final short Kernel = 143;

        /**
         * Latitude
         */
        public static final short Latitude = 144;

        /**
         * Layers
         */
        public static final short Layers = 145;

        /**
         * Left
         */
        public static final short Left = 146;

        /**
         * Level
         */
        public static final short Level = 147;

        /**
         * Line {0}
         */
        public static final short Line_1 = 148;

        /**
         * Lines
         */
        public static final short Lines = 149;

        /**
         * Loading…
         */
        public static final short Loading = 150;

        /**
         * Loading headers
         */
        public static final short LoadingHeaders = 151;

        /**
         * Loading image {0}
         */
        public static final short LoadingImage_1 = 152;

        /**
         * Loading images {0} and {1}
         */
        public static final short LoadingImages_2 = 153;

        /**
         * Loading {0}…
         */
        public static final short Loading_1 = 154;

        /**
         * Logarithmic
         */
        public static final short Logarithmic = 155;

        /**
         * Logger
         */
        public static final short Logger = 156;

        /**
         * Longitude
         */
        public static final short Longitude = 157;

        /**
         * Magnifier
         */
        public static final short Magnifier = 158;

        /**
         * Mandatory
         */
        public static final short Mandatory = 159;

        /**
         * Math transform
         */
        public static final short MathTransform = 160;

        /**
         * Maximum
         */
        public static final short Maximum = 161;

        /**
         * Allocated memory: {0} MB
         */
        public static final short MemoryHeapSize_1 = 162;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final short MemoryHeapUsage_1 = 163;

        /**
         * Message
         */
        public static final short Message = 164;

        /**
         * Metadata
         */
        public static final short Metadata = 165;

        /**
         * Method
         */
        public static final short Method = 166;

        /**
         * Minimum
         */
        public static final short Minimum = 167;

        /**
         * Molodensky transform
         */
        public static final short MolodenskyTransform = 168;

        /**
         * … {0} more…
         */
        public static final short More_1 = 169;

        /**
         * NADCON transform
         */
        public static final short NadconTransform = 170;

        /**
         * Name
         */
        public static final short Name = 171;

        /**
         * Navigate
         */
        public static final short Navigate = 172;

        /**
         * New format
         */
        public static final short NewFormat = 173;

        /**
         * New layer
         */
        public static final short NewLayer = 174;

        /**
         * {0} (no details)
         */
        public static final short NoDetails_1 = 175;

        /**
         * No duplicated value found.
         */
        public static final short NoDuplicationFound = 176;

        /**
         * No data
         */
        public static final short Nodata = 177;

        /**
         * None
         */
        public static final short None = 178;

        /**
         * Northing
         */
        public static final short Northing = 179;

        /**
         * Not installed
         */
        public static final short NotInstalled = 180;

        /**
         * Note
         */
        public static final short Note = 181;

        /**
         * Oblique Mercator projection
         */
        public static final short ObliqueMercatorProjection = 182;

        /**
         * Occurrence
         */
        public static final short Occurrence = 183;

        /**
         * Offset
         */
        public static final short Offset = 184;

        /**
         * Ok
         */
        public static final short Ok = 185;

        /**
         * {0} operation
         */
        public static final short Operation_1 = 186;

        /**
         * Operations
         */
        public static final short Operations = 187;

        /**
         * Options
         */
        public static final short Options = 188;

        /**
         * Order
         */
        public static final short Order = 189;

        /**
         * Orthodromic distance
         */
        public static final short OrthodromicDistance = 190;

        /**
         * Orthographic projection
         */
        public static final short OrthographicProjection = 191;

        /**
         * {0} system
         */
        public static final short OsName_1 = 192;

        /**
         * Version {0} for {1}
         */
        public static final short OsVersion_2 = 193;

        /**
         * Others
         */
        public static final short Others = 194;

        /**
         * Output directory
         */
        public static final short OutputDirectory = 195;

        /**
         * Parameter {0}
         */
        public static final short Parameter_1 = 196;

        /**
         * Password
         */
        public static final short Password = 197;

        /**
         * Paused
         */
        public static final short Paused = 198;

        /**
         * Personalized
         */
        public static final short Personalized = 199;

        /**
         * Pixel size
         */
        public static final short PixelSize = 200;

        /**
         * Pixels
         */
        public static final short Pixels = 201;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final short PointCountInGrid_3 = 202;

        /**
         * Port
         */
        public static final short Port = 203;

        /**
         * Predefined kernels
         */
        public static final short PredefinedKernels = 204;

        /**
         * Preferences
         */
        public static final short Preferences = 205;

        /**
         * Preferred resolution
         */
        public static final short PreferredResolution = 206;

        /**
         * Preview
         */
        public static final short Preview = 207;

        /**
         * Progression
         */
        public static final short Progression = 208;

        /**
         * Projected
         */
        public static final short Projected = 209;

        /**
         * Properties
         */
        public static final short Properties = 210;

        /**
         * Properties of {0}
         */
        public static final short PropertiesOf_1 = 211;

        /**
         * Quit
         */
        public static final short Quit = 212;

        /**
         * Range
         */
        public static final short Range = 213;

        /**
         * {0} bits real number
         */
        public static final short RealNumber_1 = 214;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final short Rectangle_4 = 215;

        /**
         * Refresh
         */
        public static final short Refresh = 216;

        /**
         * Remarks
         */
        public static final short Remarks = 217;

        /**
         * Remove
         */
        public static final short Remove = 218;

        /**
         * Reset
         */
        public static final short Reset = 219;

        /**
         * Resolution
         */
        public static final short Resolution = 220;

        /**
         * Resumed
         */
        public static final short Resumed = 221;

        /**
         * Right
         */
        public static final short Right = 222;

        /**
         * Root directory
         */
        public static final short RootDirectory = 223;

        /**
         * Rotate left
         */
        public static final short RotateLeft = 224;

        /**
         * Rotate right
         */
        public static final short RotateRight = 225;

        /**
         * Row
         */
        public static final short Row = 226;

        /**
         * Running tasks
         */
        public static final short RunningTasks = 227;

        /**
         * Sample dimensions
         */
        public static final short SampleDimensions = 228;

        /**
         * Sample model
         */
        public static final short SampleModel = 229;

        /**
         * Saving {0}…
         */
        public static final short Saving_1 = 230;

        /**
         * Scale
         */
        public static final short Scale = 231;

        /**
         * Schema
         */
        public static final short Schema = 232;

        /**
         * Search
         */
        public static final short Search = 233;

        /**
         * Selected colors
         */
        public static final short SelectedColors = 234;

        /**
         * Server
         */
        public static final short Server = 235;

        /**
         * Service
         */
        public static final short Service = 236;

        /**
         * Set preferred resolution
         */
        public static final short SetPreferredResolution = 237;

        /**
         * Show magnifier
         */
        public static final short ShowMagnifier = 238;

        /**
         * {0} bits signed integer
         */
        public static final short SignedInteger_1 = 239;

        /**
         * Size
         */
        public static final short Size = 240;

        /**
         * (in angle minutes)
         */
        public static final short SizeInMinutes = 241;

        /**
         * {0} × {1}
         */
        public static final short Size_2 = 242;

        /**
         * Source CRS
         */
        public static final short SourceCrs = 243;

        /**
         * Source point
         */
        public static final short SourcePoint = 244;

        /**
         * Southing
         */
        public static final short Southing = 245;

        /**
         * Spatial objects
         */
        public static final short SpatialObjects = 246;

        /**
         * Spherical
         */
        public static final short Spherical = 247;

        /**
         * Spherical latitude
         */
        public static final short SphericalLatitude = 248;

        /**
         * Spherical longitude
         */
        public static final short SphericalLongitude = 249;

        /**
         * Standard
         */
        public static final short Standard = 250;

        /**
         * Start time
         */
        public static final short StartTime = 251;

        /**
         * Started
         */
        public static final short Started = 252;

        /**
         * Stereographic projection
         */
        public static final short StereographicProjection = 253;

        /**
         * Subsampling
         */
        public static final short Subsampling = 254;

        /**
         * Superseded by {0}.
         */
        public static final short SupersededBy_1 = 255;

        /**
         * System
         */
        public static final short System = 256;

        /**
         * Target
         */
        public static final short Target = 257;

        /**
         * Target CRS
         */
        public static final short TargetCrs = 258;

        /**
         * Target point
         */
        public static final short TargetPoint = 259;

        /**
         * Tasks
         */
        public static final short Tasks = 260;

        /**
         * Tile cache capacity: {0} Mb
         */
        public static final short TileCacheCapacity_1 = 261;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final short TileSize_4 = 262;

        /**
         * Tiles size
         */
        public static final short TilesSize = 263;

        /**
         * Time
         */
        public static final short Time = 264;

        /**
         * Time
         */
        public static final short TimeOfDay = 265;

        /**
         * Time range
         */
        public static final short TimeRange = 266;

        /**
         * Time zone
         */
        public static final short TimeZone = 267;

        /**
         * Transformation accuracy
         */
        public static final short TransformationAccuracy = 268;

        /**
         * Transverse Mercator projection
         */
        public static final short TransverseMercatorProjection = 269;

        /**
         * True
         */
        public static final short True = 270;

        /**
         * Type
         */
        public static final short Type = 271;

        /**
         * Undefined
         */
        public static final short Undefined = 272;

        /**
         * Units
         */
        public static final short Units = 273;

        /**
         * Unknown
         */
        public static final short Unknown = 274;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final short UnsignedInteger_2 = 275;

        /**
         * Untitled
         */
        public static final short Untitled = 276;

        /**
         * Up
         */
        public static final short Up = 277;

        /**
         * URL
         */
        public static final short Url = 278;

        /**
         * Use best resolution
         */
        public static final short UseBestResolution = 279;

        /**
         * User
         */
        public static final short User = 280;

        /**
         * Valid values
         */
        public static final short ValidValues = 281;

        /**
         * Value
         */
        public static final short Value = 282;

        /**
         * Value range
         */
        public static final short ValueRange = 283;

        /**
         * Vendor
         */
        public static final short Vendor = 284;

        /**
         * Verifying
         */
        public static final short Verifying = 285;

        /**
         * “{0}” version
         */
        public static final short VersionOf_1 = 286;

        /**
         * Version {0}
         */
        public static final short Version_1 = 287;

        /**
         * Vertical
         */
        public static final short Vertical = 288;

        /**
         * Vertical component
         */
        public static final short VerticalComponent = 289;

        /**
         * Warning
         */
        public static final short Warning = 290;

        /**
         * Westing
         */
        public static final short Westing = 291;

        /**
         * Width
         */
        public static final short Width = 292;

        /**
         * Wizards
         */
        public static final short Wizards = 293;

        /**
         * Zoom in
         */
        public static final short ZoomIn = 294;

        /**
         * Close zoom
         */
        public static final short ZoomMax = 295;

        /**
         * Zoom out
         */
        public static final short ZoomOut = 296;
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
