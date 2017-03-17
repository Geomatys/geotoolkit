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
        public static final short AbridgedMolodenskyTransform = 5;

        /**
         * Add
         */
        public static final short Add = 6;

        /**
         * Administrator
         */
        public static final short Administrator = 7;

        /**
         * Albers Equal Area projection
         */
        public static final short AlbersEqualAreaProjection = 8;

        /**
         * All
         */
        public static final short All = 9;

        /**
         * All users
         */
        public static final short AllUsers = 10;

        /**
         * Altitude
         */
        public static final short Altitude = 11;

        /**
         * Altitudes
         */
        public static final short Altitudes = 12;

        /**
         * Apply
         */
        public static final short Apply = 13;

        /**
         * Authority
         */
        public static final short Authority = 14;

        /**
         * Automatic
         */
        public static final short Automatic = 15;

        /**
         * Axis changes
         */
        public static final short AxisChanges = 16;

        /**
         * {0} axis
         */
        public static final short Axis_1 = 17;

        /**
         * Azimuth
         */
        public static final short Azimuth = 18;

        /**
         * Band
         */
        public static final short Band = 19;

        /**
         * Band {0}
         */
        public static final short Band_1 = 20;

        /**
         * Cancel
         */
        public static final short Cancel = 21;

        /**
         * Cartesian
         */
        public static final short Cartesian = 22;

        /**
         * Cartesian 2D
         */
        public static final short Cartesian2d = 23;

        /**
         * Cartesian 3D
         */
        public static final short Cartesian3d = 24;

        /**
         * Cassini-Soldner projection
         */
        public static final short CassiniSoldnerProjection = 25;

        /**
         * Category
         */
        public static final short Category = 26;

        /**
         * Choose
         */
        public static final short Choose = 27;

        /**
         * Class
         */
        public static final short Class = 28;

        /**
         * Close
         */
        public static final short Close = 29;

        /**
         * Code
         */
        public static final short Code = 30;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final short ColorCount_1 = 31;

        /**
         * Color model
         */
        public static final short ColorModel = 32;

        /**
         * Color space
         */
        public static final short ColorSpace = 33;

        /**
         * Colors
         */
        public static final short Colors = 34;

        /**
         * Column
         */
        public static final short Column = 35;

        /**
         * Columns
         */
        public static final short Columns = 36;

        /**
         * Commands
         */
        public static final short Commands = 37;

        /**
         * Compare with
         */
        public static final short CompareWith = 38;

        /**
         * Completed
         */
        public static final short Completed = 39;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final short ComponentCount_1 = 40;

        /**
         * Configure
         */
        public static final short Configure = 41;

        /**
         * Confirm
         */
        public static final short Confirm = 42;

        /**
         * Connection parameters
         */
        public static final short ConnectionParameters = 43;

        /**
         * Coordinate Reference System
         */
        public static final short CoordinateReferenceSystem = 44;

        /**
         * Coordinates selection
         */
        public static final short CoordinatesSelection = 45;

        /**
         * Current user
         */
        public static final short CurrentUser = 46;

        /**
         * Data
         */
        public static final short Data = 47;

        /**
         * Data are present
         */
        public static final short DataArePresent = 48;

        /**
         * {0} data base
         */
        public static final short DataBase_1 = 49;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final short DataBase_3 = 50;

        /**
         * Data type
         */
        public static final short DataType = 51;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final short DataType_2 = 52;

        /**
         * {0} data
         */
        public static final short Data_1 = 53;

        /**
         * Database
         */
        public static final short Database = 54;

        /**
         * Database engine
         */
        public static final short DatabaseEngine = 55;

        /**
         * Database URL
         */
        public static final short DatabaseUrl = 56;

        /**
         * Datum
         */
        public static final short Datum = 57;

        /**
         * Datum shift
         */
        public static final short DatumShift = 58;

        /**
         * day
         */
        public static final short Day = 59;

        /**
         * days
         */
        public static final short Days = 60;

        /**
         * Debug
         */
        public static final short Debug = 61;

        /**
         * Decoders
         */
        public static final short Decoders = 62;

        /**
         * Default
         */
        public static final short Default = 63;

        /**
         * Define
         */
        public static final short Define = 64;

        /**
         * Delete
         */
        public static final short Delete = 65;

        /**
         * Depth
         */
        public static final short Depth = 66;

        /**
         * Derived from {0}
         */
        public static final short DerivedFrom_1 = 67;

        /**
         * Description
         */
        public static final short Description = 68;

        /**
         * Directories
         */
        public static final short Directories = 69;

        /**
         * Display
         */
        public static final short Display = 70;

        /**
         * Distance
         */
        public static final short Distance = 71;

        /**
         * Domain
         */
        public static final short Domain = 72;

        /**
         * Down
         */
        public static final short Down = 73;

        /**
         * Download
         */
        public static final short Download = 74;

        /**
         * Downloading
         */
        public static final short Downloading = 75;

        /**
         * Duplicated value
         */
        public static final short DuplicatedValue = 76;

        /**
         * Duration
         */
        public static final short Duration = 77;

        /**
         * Easting
         */
        public static final short Easting = 78;

        /**
         * Edit
         */
        public static final short Edit = 79;

        /**
         * Efficiency
         */
        public static final short Efficiency = 80;

        /**
         * Ellipsoid
         */
        public static final short Ellipsoid = 81;

        /**
         * Ellipsoid shift
         */
        public static final short EllipsoidShift = 82;

        /**
         * Ellipsoidal height
         */
        public static final short EllipsoidalHeight = 83;

        /**
         * Empty
         */
        public static final short Empty = 84;

        /**
         * Encoders
         */
        public static final short Encoders = 85;

        /**
         * End time
         */
        public static final short EndTime = 86;

        /**
         * Error
         */
        public static final short Error = 87;

        /**
         * Error filters
         */
        public static final short ErrorFilters = 88;

        /**
         * Error - {0}
         */
        public static final short Error_1 = 89;

        /**
         * Event logger
         */
        public static final short EventLogger = 90;

        /**
         * Examples
         */
        public static final short Examples = 91;

        /**
         * Exception
         */
        public static final short Exception = 92;

        /**
         * Exponential
         */
        public static final short Exponential = 93;

        /**
         * False
         */
        public static final short False = 94;

        /**
         * File
         */
        public static final short File = 95;

        /**
         * Line {1} in file {0}
         */
        public static final short FilePosition_2 = 96;

        /**
         * File {0}
         */
        public static final short File_1 = 97;

        /**
         * {0} files
         */
        public static final short Files_1 = 98;

        /**
         * Format
         */
        public static final short Format = 99;

        /**
         * General
         */
        public static final short General = 100;

        /**
         * Generic Cartesian 2D
         */
        public static final short GenericCartesian2d = 101;

        /**
         * Generic Cartesian 3D
         */
        public static final short GenericCartesian3d = 102;

        /**
         * Geocentric
         */
        public static final short Geocentric = 103;

        /**
         * Geocentric radius
         */
        public static final short GeocentricRadius = 104;

        /**
         * Geocentric transform
         */
        public static final short GeocentricTransform = 105;

        /**
         * Geocentric X
         */
        public static final short GeocentricX = 106;

        /**
         * Geocentric Y
         */
        public static final short GeocentricY = 107;

        /**
         * Geocentric Z
         */
        public static final short GeocentricZ = 108;

        /**
         * Geodetic 2D
         */
        public static final short Geodetic2d = 109;

        /**
         * Geodetic 3D
         */
        public static final short Geodetic3d = 110;

        /**
         * Geodetic latitude
         */
        public static final short GeodeticLatitude = 111;

        /**
         * Geodetic longitude
         */
        public static final short GeodeticLongitude = 112;

        /**
         * Geographic coordinates
         */
        public static final short GeographicCoordinates = 113;

        /**
         * Geospatial
         */
        public static final short Geospatial = 114;

        /**
         * Gradient masks
         */
        public static final short GradientMasks = 115;

        /**
         * Gravity-related height
         */
        public static final short GravityRelatedHeight = 116;

        /**
         * Gray scale
         */
        public static final short GrayScale = 117;

        /**
         * Grid
         */
        public static final short Grid = 118;

        /**
         * Gridded data
         */
        public static final short GriddedData = 119;

        /**
         * Height
         */
        public static final short Height = 120;

        /**
         * Help
         */
        public static final short Help = 121;

        /**
         * Hide
         */
        public static final short Hide = 122;

        /**
         * Horizontal
         */
        public static final short Horizontal = 123;

        /**
         * Horizontal component
         */
        public static final short HorizontalComponent = 124;

        /**
         * {0}-{1}
         */
        public static final short Hyphen_2 = 125;

        /**
         * Identification
         */
        public static final short Identification = 126;

        /**
         * Identifier
         */
        public static final short Identifier = 127;

        /**
         * Identity
         */
        public static final short Identity = 128;

        /**
         * Image of class {0}
         */
        public static final short ImageClass_1 = 129;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final short ImageCodecWithWorldFile_2 = 130;

        /**
         * Image list
         */
        public static final short ImageList = 131;

        /**
         * Image root directory
         */
        public static final short ImageRootDirectory = 132;

        /**
         * Image size
         */
        public static final short ImageSize = 133;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final short ImageSize_3 = 134;

        /**
         * Image {0}
         */
        public static final short Image_1 = 135;

        /**
         * Images
         */
        public static final short Images = 136;

        /**
         * Implementations
         */
        public static final short Implementations = 137;

        /**
         * Index
         */
        public static final short Index = 138;

        /**
         * Informations
         */
        public static final short Informations = 139;

        /**
         * Install
         */
        public static final short Install = 140;

        /**
         * {0} installation
         */
        public static final short Installation_1 = 141;

        /**
         * Inverse operation
         */
        public static final short InverseOperation = 142;

        /**
         * Inverse transform
         */
        public static final short InverseTransform = 143;

        /**
         * {0}
         */
        public static final short JavaVendor_1 = 144;

        /**
         * Java version {0}
         */
        public static final short JavaVersion_1 = 145;

        /**
         * Kernel
         */
        public static final short Kernel = 146;

        /**
         * Latitude
         */
        public static final short Latitude = 147;

        /**
         * Layers
         */
        public static final short Layers = 148;

        /**
         * Left
         */
        public static final short Left = 149;

        /**
         * Level
         */
        public static final short Level = 150;

        /**
         * Line {0}
         */
        public static final short Line_1 = 151;

        /**
         * Lines
         */
        public static final short Lines = 152;

        /**
         * Loading…
         */
        public static final short Loading = 153;

        /**
         * Loading headers
         */
        public static final short LoadingHeaders = 154;

        /**
         * Loading image {0}
         */
        public static final short LoadingImage_1 = 155;

        /**
         * Loading images {0} and {1}
         */
        public static final short LoadingImages_2 = 156;

        /**
         * Loading {0}…
         */
        public static final short Loading_1 = 157;

        /**
         * Logarithmic
         */
        public static final short Logarithmic = 158;

        /**
         * Logger
         */
        public static final short Logger = 159;

        /**
         * Longitude
         */
        public static final short Longitude = 160;

        /**
         * Magnifier
         */
        public static final short Magnifier = 161;

        /**
         * Mandatory
         */
        public static final short Mandatory = 162;

        /**
         * Math transform
         */
        public static final short MathTransform = 163;

        /**
         * Maximum
         */
        public static final short Maximum = 164;

        /**
         * Allocated memory: {0} MB
         */
        public static final short MemoryHeapSize_1 = 165;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final short MemoryHeapUsage_1 = 166;

        /**
         * Message
         */
        public static final short Message = 167;

        /**
         * Metadata
         */
        public static final short Metadata = 168;

        /**
         * Method
         */
        public static final short Method = 169;

        /**
         * Minimum
         */
        public static final short Minimum = 170;

        /**
         * Molodensky transform
         */
        public static final short MolodenskyTransform = 171;

        /**
         * … {0} more…
         */
        public static final short More_1 = 172;

        /**
         * NADCON transform
         */
        public static final short NadconTransform = 173;

        /**
         * Name
         */
        public static final short Name = 174;

        /**
         * Navigate
         */
        public static final short Navigate = 175;

        /**
         * New format
         */
        public static final short NewFormat = 176;

        /**
         * New layer
         */
        public static final short NewLayer = 177;

        /**
         * {0} (no details)
         */
        public static final short NoDetails_1 = 178;

        /**
         * No duplicated value found.
         */
        public static final short NoDuplicationFound = 179;

        /**
         * No data
         */
        public static final short Nodata = 180;

        /**
         * None
         */
        public static final short None = 181;

        /**
         * Northing
         */
        public static final short Northing = 182;

        /**
         * Not installed
         */
        public static final short NotInstalled = 183;

        /**
         * Note
         */
        public static final short Note = 184;

        /**
         * Oblique Mercator projection
         */
        public static final short ObliqueMercatorProjection = 185;

        /**
         * Occurrence
         */
        public static final short Occurrence = 186;

        /**
         * Offset
         */
        public static final short Offset = 187;

        /**
         * Ok
         */
        public static final short Ok = 188;

        /**
         * {0} operation
         */
        public static final short Operation_1 = 189;

        /**
         * Operations
         */
        public static final short Operations = 190;

        /**
         * Options
         */
        public static final short Options = 191;

        /**
         * Order
         */
        public static final short Order = 192;

        /**
         * Orthodromic distance
         */
        public static final short OrthodromicDistance = 193;

        /**
         * Orthographic projection
         */
        public static final short OrthographicProjection = 194;

        /**
         * {0} system
         */
        public static final short OsName_1 = 195;

        /**
         * Version {0} for {1}
         */
        public static final short OsVersion_2 = 196;

        /**
         * Others
         */
        public static final short Others = 197;

        /**
         * Output directory
         */
        public static final short OutputDirectory = 198;

        /**
         * Parameter {0}
         */
        public static final short Parameter_1 = 199;

        /**
         * Password
         */
        public static final short Password = 200;

        /**
         * Paused
         */
        public static final short Paused = 201;

        /**
         * Personalized
         */
        public static final short Personalized = 202;

        /**
         * Pixel size
         */
        public static final short PixelSize = 203;

        /**
         * Pixels
         */
        public static final short Pixels = 204;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final short PointCountInGrid_3 = 205;

        /**
         * Port
         */
        public static final short Port = 206;

        /**
         * Predefined kernels
         */
        public static final short PredefinedKernels = 207;

        /**
         * Preferences
         */
        public static final short Preferences = 208;

        /**
         * Preferred resolution
         */
        public static final short PreferredResolution = 209;

        /**
         * Preview
         */
        public static final short Preview = 210;

        /**
         * Progression
         */
        public static final short Progression = 211;

        /**
         * Projected
         */
        public static final short Projected = 212;

        /**
         * Properties
         */
        public static final short Properties = 213;

        /**
         * Properties of {0}
         */
        public static final short PropertiesOf_1 = 214;

        /**
         * Quit
         */
        public static final short Quit = 215;

        /**
         * Range
         */
        public static final short Range = 216;

        /**
         * {0} bits real number
         */
        public static final short RealNumber_1 = 217;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final short Rectangle_4 = 218;

        /**
         * Refresh
         */
        public static final short Refresh = 219;

        /**
         * Remarks
         */
        public static final short Remarks = 220;

        /**
         * Remove
         */
        public static final short Remove = 221;

        /**
         * Reset
         */
        public static final short Reset = 222;

        /**
         * Resolution
         */
        public static final short Resolution = 223;

        /**
         * Resumed
         */
        public static final short Resumed = 224;

        /**
         * Right
         */
        public static final short Right = 225;

        /**
         * Root directory
         */
        public static final short RootDirectory = 226;

        /**
         * Rotate left
         */
        public static final short RotateLeft = 2;

        /**
         * Rotate right
         */
        public static final short RotateRight = 3;

        /**
         * Row
         */
        public static final short Row = 227;

        /**
         * Running tasks
         */
        public static final short RunningTasks = 4;

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
