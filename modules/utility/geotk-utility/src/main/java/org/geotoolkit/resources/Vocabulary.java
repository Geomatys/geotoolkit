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
        public static final short About = 0;

        /**
         * Abridged Molodensky transform
         */
        public static final short AbridgedMolodenskyTransform = 1;

        /**
         * Add
         */
        public static final short Add = 2;

        /**
         * Administrator
         */
        public static final short Administrator = 3;

        /**
         * Albers Equal Area projection
         */
        public static final short AlbersEqualAreaProjection = 4;

        /**
         * All
         */
        public static final short All = 5;

        /**
         * All users
         */
        public static final short AllUsers = 6;

        /**
         * Altitude
         */
        public static final short Altitude = 7;

        /**
         * Altitudes
         */
        public static final short Altitudes = 8;

        /**
         * Apply
         */
        public static final short Apply = 9;

        /**
         * Authority
         */
        public static final short Authority = 10;

        /**
         * Automatic
         */
        public static final short Automatic = 11;

        /**
         * Axis changes
         */
        public static final short AxisChanges = 12;

        /**
         * {0} axis
         */
        public static final short Axis_1 = 13;

        /**
         * Azimuth
         */
        public static final short Azimuth = 14;

        /**
         * Band
         */
        public static final short Band = 15;

        /**
         * Band {0}
         */
        public static final short Band_1 = 16;

        /**
         * Cancel
         */
        public static final short Cancel = 17;

        /**
         * Cartesian
         */
        public static final short Cartesian = 18;

        /**
         * Cartesian 2D
         */
        public static final short Cartesian2d = 19;

        /**
         * Cartesian 3D
         */
        public static final short Cartesian3d = 20;

        /**
         * Cassini-Soldner projection
         */
        public static final short CassiniSoldnerProjection = 21;

        /**
         * Category
         */
        public static final short Category = 22;

        /**
         * Choose
         */
        public static final short Choose = 23;

        /**
         * Class
         */
        public static final short Class = 24;

        /**
         * Close
         */
        public static final short Close = 25;

        /**
         * Code
         */
        public static final short Code = 26;

        /**
         * {0} color{0,choice,1#|2#s}
         */
        public static final short ColorCount_1 = 27;

        /**
         * Color model
         */
        public static final short ColorModel = 28;

        /**
         * Color space
         */
        public static final short ColorSpace = 29;

        /**
         * Colors
         */
        public static final short Colors = 30;

        /**
         * Column
         */
        public static final short Column = 31;

        /**
         * Columns
         */
        public static final short Columns = 32;

        /**
         * Commands
         */
        public static final short Commands = 33;

        /**
         * Compare with
         */
        public static final short CompareWith = 34;

        /**
         * Completed
         */
        public static final short Completed = 35;

        /**
         * {0} component{0,choice,1#|2#s}
         */
        public static final short ComponentCount_1 = 36;

        /**
         * Configure
         */
        public static final short Configure = 37;

        /**
         * Confirm
         */
        public static final short Confirm = 38;

        /**
         * Connection parameters
         */
        public static final short ConnectionParameters = 39;

        /**
         * Coordinate Reference System
         */
        public static final short CoordinateReferenceSystem = 40;

        /**
         * Coordinates selection
         */
        public static final short CoordinatesSelection = 41;

        /**
         * Current user
         */
        public static final short CurrentUser = 42;

        /**
         * Data
         */
        public static final short Data = 43;

        /**
         * Data are present
         */
        public static final short DataArePresent = 44;

        /**
         * {0} data base
         */
        public static final short DataBase_1 = 45;

        /**
         * {0} data base version {1} on {2} engine.
         */
        public static final short DataBase_3 = 46;

        /**
         * Data type
         */
        public static final short DataType = 47;

        /**
         * {1} bits {0,choice,0#unsigned integer|1#signed integer|2#real number}
         */
        public static final short DataType_2 = 48;

        /**
         * {0} data
         */
        public static final short Data_1 = 49;

        /**
         * Database
         */
        public static final short Database = 50;

        /**
         * Database engine
         */
        public static final short DatabaseEngine = 51;

        /**
         * Database URL
         */
        public static final short DatabaseUrl = 52;

        /**
         * Datum
         */
        public static final short Datum = 53;

        /**
         * Datum shift
         */
        public static final short DatumShift = 54;

        /**
         * day
         */
        public static final short Day = 55;

        /**
         * days
         */
        public static final short Days = 56;

        /**
         * Debug
         */
        public static final short Debug = 57;

        /**
         * Decoders
         */
        public static final short Decoders = 58;

        /**
         * Default
         */
        public static final short Default = 59;

        /**
         * Define
         */
        public static final short Define = 60;

        /**
         * Delete
         */
        public static final short Delete = 61;

        /**
         * Depth
         */
        public static final short Depth = 62;

        /**
         * Derived from {0}
         */
        public static final short DerivedFrom_1 = 63;

        /**
         * Description
         */
        public static final short Description = 64;

        /**
         * Directories
         */
        public static final short Directories = 65;

        /**
         * Display
         */
        public static final short Display = 66;

        /**
         * Distance
         */
        public static final short Distance = 67;

        /**
         * Domain
         */
        public static final short Domain = 68;

        /**
         * Down
         */
        public static final short Down = 69;

        /**
         * Download
         */
        public static final short Download = 70;

        /**
         * Downloading
         */
        public static final short Downloading = 71;

        /**
         * Duplicated value
         */
        public static final short DuplicatedValue = 72;

        /**
         * Duration
         */
        public static final short Duration = 73;

        /**
         * Easting
         */
        public static final short Easting = 74;

        /**
         * Edit
         */
        public static final short Edit = 75;

        /**
         * Efficiency
         */
        public static final short Efficiency = 76;

        /**
         * Ellipsoid
         */
        public static final short Ellipsoid = 77;

        /**
         * Ellipsoid shift
         */
        public static final short EllipsoidShift = 78;

        /**
         * Ellipsoidal height
         */
        public static final short EllipsoidalHeight = 79;

        /**
         * Empty
         */
        public static final short Empty = 80;

        /**
         * Encoders
         */
        public static final short Encoders = 81;

        /**
         * End time
         */
        public static final short EndTime = 82;

        /**
         * Error
         */
        public static final short Error = 83;

        /**
         * Error filters
         */
        public static final short ErrorFilters = 84;

        /**
         * Error - {0}
         */
        public static final short Error_1 = 85;

        /**
         * Event logger
         */
        public static final short EventLogger = 86;

        /**
         * Examples
         */
        public static final short Examples = 87;

        /**
         * Exception
         */
        public static final short Exception = 88;

        /**
         * Exponential
         */
        public static final short Exponential = 89;

        /**
         * False
         */
        public static final short False = 90;

        /**
         * File
         */
        public static final short File = 91;

        /**
         * Line {1} in file {0}
         */
        public static final short FilePosition_2 = 92;

        /**
         * File {0}
         */
        public static final short File_1 = 93;

        /**
         * {0} files
         */
        public static final short Files_1 = 94;

        /**
         * Format
         */
        public static final short Format = 95;

        /**
         * General
         */
        public static final short General = 96;

        /**
         * Generic Cartesian 2D
         */
        public static final short GenericCartesian2d = 97;

        /**
         * Generic Cartesian 3D
         */
        public static final short GenericCartesian3d = 98;

        /**
         * Geocentric
         */
        public static final short Geocentric = 99;

        /**
         * Geocentric radius
         */
        public static final short GeocentricRadius = 100;

        /**
         * Geocentric transform
         */
        public static final short GeocentricTransform = 101;

        /**
         * Geocentric X
         */
        public static final short GeocentricX = 102;

        /**
         * Geocentric Y
         */
        public static final short GeocentricY = 103;

        /**
         * Geocentric Z
         */
        public static final short GeocentricZ = 104;

        /**
         * Geodetic 2D
         */
        public static final short Geodetic2d = 105;

        /**
         * Geodetic 3D
         */
        public static final short Geodetic3d = 106;

        /**
         * Geodetic latitude
         */
        public static final short GeodeticLatitude = 107;

        /**
         * Geodetic longitude
         */
        public static final short GeodeticLongitude = 108;

        /**
         * Geographic coordinates
         */
        public static final short GeographicCoordinates = 109;

        /**
         * Geospatial
         */
        public static final short Geospatial = 110;

        /**
         * Gradient masks
         */
        public static final short GradientMasks = 111;

        /**
         * Gravity-related height
         */
        public static final short GravityRelatedHeight = 112;

        /**
         * Gray scale
         */
        public static final short GrayScale = 113;

        /**
         * Grid
         */
        public static final short Grid = 114;

        /**
         * Gridded data
         */
        public static final short GriddedData = 115;

        /**
         * Height
         */
        public static final short Height = 116;

        /**
         * Help
         */
        public static final short Help = 117;

        /**
         * Hide
         */
        public static final short Hide = 118;

        /**
         * Horizontal
         */
        public static final short Horizontal = 119;

        /**
         * Horizontal component
         */
        public static final short HorizontalComponent = 120;

        /**
         * {0}-{1}
         */
        public static final short Hyphen_2 = 121;

        /**
         * Identification
         */
        public static final short Identification = 122;

        /**
         * Identifier
         */
        public static final short Identifier = 123;

        /**
         * Identity
         */
        public static final short Identity = 124;

        /**
         * Image of class {0}
         */
        public static final short ImageClass_1 = 125;

        /**
         * {1} Image {0,choice,0#Reader|1#Writer} with World File
         */
        public static final short ImageCodecWithWorldFile_2 = 126;

        /**
         * Image list
         */
        public static final short ImageList = 127;

        /**
         * Image root directory
         */
        public static final short ImageRootDirectory = 128;

        /**
         * Image size
         */
        public static final short ImageSize = 129;

        /**
         * {0} × {1} pixels × {2} bands
         */
        public static final short ImageSize_3 = 130;

        /**
         * Image {0}
         */
        public static final short Image_1 = 131;

        /**
         * Images
         */
        public static final short Images = 132;

        /**
         * Implementations
         */
        public static final short Implementations = 133;

        /**
         * Index
         */
        public static final short Index = 134;

        /**
         * Informations
         */
        public static final short Informations = 135;

        /**
         * Install
         */
        public static final short Install = 136;

        /**
         * {0} installation
         */
        public static final short Installation_1 = 137;

        /**
         * Inverse operation
         */
        public static final short InverseOperation = 138;

        /**
         * Inverse transform
         */
        public static final short InverseTransform = 139;

        /**
         * {0}
         */
        public static final short JavaVendor_1 = 140;

        /**
         * Java version {0}
         */
        public static final short JavaVersion_1 = 141;

        /**
         * Kernel
         */
        public static final short Kernel = 142;

        /**
         * Latitude
         */
        public static final short Latitude = 143;

        /**
         * Layers
         */
        public static final short Layers = 144;

        /**
         * Left
         */
        public static final short Left = 145;

        /**
         * Level
         */
        public static final short Level = 146;

        /**
         * Line {0}
         */
        public static final short Line_1 = 147;

        /**
         * Lines
         */
        public static final short Lines = 148;

        /**
         * Loading…
         */
        public static final short Loading = 149;

        /**
         * Loading headers
         */
        public static final short LoadingHeaders = 150;

        /**
         * Loading image {0}
         */
        public static final short LoadingImage_1 = 151;

        /**
         * Loading images {0} and {1}
         */
        public static final short LoadingImages_2 = 152;

        /**
         * Loading {0}…
         */
        public static final short Loading_1 = 153;

        /**
         * Logarithmic
         */
        public static final short Logarithmic = 154;

        /**
         * Logger
         */
        public static final short Logger = 155;

        /**
         * Longitude
         */
        public static final short Longitude = 156;

        /**
         * Magnifier
         */
        public static final short Magnifier = 157;

        /**
         * Mandatory
         */
        public static final short Mandatory = 158;

        /**
         * Math transform
         */
        public static final short MathTransform = 159;

        /**
         * Maximum
         */
        public static final short Maximum = 160;

        /**
         * Allocated memory: {0} MB
         */
        public static final short MemoryHeapSize_1 = 161;

        /**
         * Allocation used: {0,number,percent}
         */
        public static final short MemoryHeapUsage_1 = 162;

        /**
         * Message
         */
        public static final short Message = 163;

        /**
         * Metadata
         */
        public static final short Metadata = 164;

        /**
         * Method
         */
        public static final short Method = 165;

        /**
         * Minimum
         */
        public static final short Minimum = 166;

        /**
         * Molodensky transform
         */
        public static final short MolodenskyTransform = 167;

        /**
         * … {0} more…
         */
        public static final short More_1 = 168;

        /**
         * NADCON transform
         */
        public static final short NadconTransform = 169;

        /**
         * Name
         */
        public static final short Name = 170;

        /**
         * Navigate
         */
        public static final short Navigate = 171;

        /**
         * New format
         */
        public static final short NewFormat = 172;

        /**
         * New layer
         */
        public static final short NewLayer = 173;

        /**
         * {0} (no details)
         */
        public static final short NoDetails_1 = 174;

        /**
         * No duplicated value found.
         */
        public static final short NoDuplicationFound = 175;

        /**
         * No data
         */
        public static final short Nodata = 176;

        /**
         * None
         */
        public static final short None = 177;

        /**
         * Northing
         */
        public static final short Northing = 178;

        /**
         * Not installed
         */
        public static final short NotInstalled = 179;

        /**
         * Note
         */
        public static final short Note = 180;

        /**
         * Oblique Mercator projection
         */
        public static final short ObliqueMercatorProjection = 181;

        /**
         * Occurrence
         */
        public static final short Occurrence = 182;

        /**
         * Offset
         */
        public static final short Offset = 183;

        /**
         * Ok
         */
        public static final short Ok = 184;

        /**
         * {0} operation
         */
        public static final short Operation_1 = 185;

        /**
         * Operations
         */
        public static final short Operations = 186;

        /**
         * Options
         */
        public static final short Options = 187;

        /**
         * Order
         */
        public static final short Order = 188;

        /**
         * Orthodromic distance
         */
        public static final short OrthodromicDistance = 189;

        /**
         * Orthographic projection
         */
        public static final short OrthographicProjection = 190;

        /**
         * {0} system
         */
        public static final short OsName_1 = 191;

        /**
         * Version {0} for {1}
         */
        public static final short OsVersion_2 = 192;

        /**
         * Others
         */
        public static final short Others = 193;

        /**
         * Output directory
         */
        public static final short OutputDirectory = 194;

        /**
         * Parameter {0}
         */
        public static final short Parameter_1 = 195;

        /**
         * Password
         */
        public static final short Password = 196;

        /**
         * Paused
         */
        public static final short Paused = 197;

        /**
         * Personalized
         */
        public static final short Personalized = 198;

        /**
         * Pixel size
         */
        public static final short PixelSize = 199;

        /**
         * Pixels
         */
        public static final short Pixels = 200;

        /**
         * {0} points on a {1} × {2} grid.
         */
        public static final short PointCountInGrid_3 = 201;

        /**
         * Port
         */
        public static final short Port = 202;

        /**
         * Predefined kernels
         */
        public static final short PredefinedKernels = 203;

        /**
         * Preferences
         */
        public static final short Preferences = 204;

        /**
         * Preferred resolution
         */
        public static final short PreferredResolution = 205;

        /**
         * Preview
         */
        public static final short Preview = 206;

        /**
         * Progression
         */
        public static final short Progression = 207;

        /**
         * Projected
         */
        public static final short Projected = 208;

        /**
         * Properties
         */
        public static final short Properties = 209;

        /**
         * Properties of {0}
         */
        public static final short PropertiesOf_1 = 210;

        /**
         * Quit
         */
        public static final short Quit = 211;

        /**
         * Range
         */
        public static final short Range = 212;

        /**
         * {0} bits real number
         */
        public static final short RealNumber_1 = 213;

        /**
         * Area: x=[{0} … {1}], y=[{2} … {3}]
         */
        public static final short Rectangle_4 = 214;

        /**
         * Refresh
         */
        public static final short Refresh = 215;

        /**
         * Remarks
         */
        public static final short Remarks = 216;

        /**
         * Remove
         */
        public static final short Remove = 217;

        /**
         * Reset
         */
        public static final short Reset = 218;

        /**
         * Resolution
         */
        public static final short Resolution = 219;

        /**
         * Resumed
         */
        public static final short Resumed = 220;

        /**
         * Right
         */
        public static final short Right = 221;

        /**
         * Root directory
         */
        public static final short RootDirectory = 222;

        /**
         * Rotate left
         */
        public static final short RotateLeft = 223;

        /**
         * Rotate right
         */
        public static final short RotateRight = 224;

        /**
         * Row
         */
        public static final short Row = 225;

        /**
         * Running tasks
         */
        public static final short RunningTasks = 226;

        /**
         * Sample dimensions
         */
        public static final short SampleDimensions = 227;

        /**
         * Sample model
         */
        public static final short SampleModel = 228;

        /**
         * Saving {0}…
         */
        public static final short Saving_1 = 229;

        /**
         * Scale
         */
        public static final short Scale = 230;

        /**
         * Schema
         */
        public static final short Schema = 231;

        /**
         * Search
         */
        public static final short Search = 232;

        /**
         * Selected colors
         */
        public static final short SelectedColors = 233;

        /**
         * Server
         */
        public static final short Server = 234;

        /**
         * Service
         */
        public static final short Service = 235;

        /**
         * Set preferred resolution
         */
        public static final short SetPreferredResolution = 236;

        /**
         * Show magnifier
         */
        public static final short ShowMagnifier = 237;

        /**
         * {0} bits signed integer
         */
        public static final short SignedInteger_1 = 238;

        /**
         * Size
         */
        public static final short Size = 239;

        /**
         * (in angle minutes)
         */
        public static final short SizeInMinutes = 240;

        /**
         * {0} × {1}
         */
        public static final short Size_2 = 241;

        /**
         * Source CRS
         */
        public static final short SourceCrs = 242;

        /**
         * Source point
         */
        public static final short SourcePoint = 243;

        /**
         * Southing
         */
        public static final short Southing = 244;

        /**
         * Spatial objects
         */
        public static final short SpatialObjects = 245;

        /**
         * Spherical
         */
        public static final short Spherical = 246;

        /**
         * Spherical latitude
         */
        public static final short SphericalLatitude = 247;

        /**
         * Spherical longitude
         */
        public static final short SphericalLongitude = 248;

        /**
         * Standard
         */
        public static final short Standard = 249;

        /**
         * Start time
         */
        public static final short StartTime = 250;

        /**
         * Started
         */
        public static final short Started = 251;

        /**
         * Stereographic projection
         */
        public static final short StereographicProjection = 252;

        /**
         * Subsampling
         */
        public static final short Subsampling = 253;

        /**
         * Superseded by {0}.
         */
        public static final short SupersededBy_1 = 254;

        /**
         * System
         */
        public static final short System = 255;

        /**
         * Target
         */
        public static final short Target = 256;

        /**
         * Target CRS
         */
        public static final short TargetCrs = 257;

        /**
         * Target point
         */
        public static final short TargetPoint = 258;

        /**
         * Tasks
         */
        public static final short Tasks = 259;

        /**
         * Tile cache capacity: {0} Mb
         */
        public static final short TileCacheCapacity_1 = 260;

        /**
         * {0}×{1} tiles of {2} × {3} pixels
         */
        public static final short TileSize_4 = 261;

        /**
         * Tiles size
         */
        public static final short TilesSize = 262;

        /**
         * Time
         */
        public static final short Time = 263;

        /**
         * Time
         */
        public static final short TimeOfDay = 264;

        /**
         * Time range
         */
        public static final short TimeRange = 265;

        /**
         * Time zone
         */
        public static final short TimeZone = 266;

        /**
         * Transformation accuracy
         */
        public static final short TransformationAccuracy = 267;

        /**
         * Transverse Mercator projection
         */
        public static final short TransverseMercatorProjection = 268;

        /**
         * True
         */
        public static final short True = 269;

        /**
         * Type
         */
        public static final short Type = 270;

        /**
         * Undefined
         */
        public static final short Undefined = 271;

        /**
         * Units
         */
        public static final short Units = 272;

        /**
         * Unknown
         */
        public static final short Unknown = 273;

        /**
         * {0} bits unsigned integer ({1} bits/pixel)
         */
        public static final short UnsignedInteger_2 = 274;

        /**
         * Untitled
         */
        public static final short Untitled = 275;

        /**
         * Up
         */
        public static final short Up = 276;

        /**
         * URL
         */
        public static final short Url = 277;

        /**
         * Use best resolution
         */
        public static final short UseBestResolution = 278;

        /**
         * User
         */
        public static final short User = 279;

        /**
         * Valid values
         */
        public static final short ValidValues = 280;

        /**
         * Value
         */
        public static final short Value = 281;

        /**
         * Value range
         */
        public static final short ValueRange = 282;

        /**
         * Vendor
         */
        public static final short Vendor = 283;

        /**
         * Verifying
         */
        public static final short Verifying = 284;

        /**
         * “{0}” version
         */
        public static final short VersionOf_1 = 285;

        /**
         * Version {0}
         */
        public static final short Version_1 = 286;

        /**
         * Vertical
         */
        public static final short Vertical = 287;

        /**
         * Vertical component
         */
        public static final short VerticalComponent = 288;

        /**
         * Warning
         */
        public static final short Warning = 289;

        /**
         * Westing
         */
        public static final short Westing = 290;

        /**
         * Width
         */
        public static final short Width = 291;

        /**
         * Wizards
         */
        public static final short Wizards = 292;

        /**
         * Zoom in
         */
        public static final short ZoomIn = 293;

        /**
         * Close zoom
         */
        public static final short ZoomMax = 294;

        /**
         * Zoom out
         */
        public static final short ZoomOut = 295;
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
