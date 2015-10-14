/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.opengis.util.InternationalString;

/**
 * Internalization of all styling widgets.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public final class MessageBundle extends IndexedResourceBundle {

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
         * Accreditation
         */
        public static final short Accreditation = 0;

        /**
         * Abstract
         */
        public static final short abstrac = 1;

        /**
         * Add symbol
         */
        public static final short addSymbol = 2;

        /**
         * Add all values
         */
        public static final short add_all_values = 333;

        /**
         * Add dimension
         */
        public static final short add_dimension = 334;

        /**
         * Add value
         */
        public static final short add_value = 335;

        /**
         * Add vector file data ...
         */
        public static final short add_vector_file = 336;

        /**
         * Advanced
         */
        public static final short advanced = 3;

        /**
         * Aligned
         */
        public static final short aligned = 4;

        /**
         * All
         */
        public static final short all = 5;

        /**
         * All Geometries
         */
        public static final short allGeom = 6;

        /**
         * all status
         */
        public static final short allStatus = 7;

        /**
         * Analyze
         */
        public static final short analyze = 8;

        /**
         * Raster analyze
         */
        public static final short analyze_raster = 337;

        /**
         * Vector analyze
         */
        public static final short analyze_vector = 338;

        /**
         * Anchor
         */
        public static final short anchor = 9;

        /**
         * Animation
         */
        public static final short animation = 10;

        /**
         * Anti-Aliasing
         */
        public static final short antialiasing = 11;

        /**
         * Application clipboard
         */
        public static final short applicationclipboard = 12;

        /**
         * Apply
         */
        public static final short apply = 13;

        /**
         * Validity area
         */
        public static final short area_validity = 339;

        /**
         * Background
         */
        public static final short background = 14;

        /**
         * Backward
         */
        public static final short backward = 15;

        /**
         * Begin
         */
        public static final short begin = 16;

        /**
         * Blue channel
         */
        public static final short blue = 17;

        /**
         * Border
         */
        public static final short border = 18;

        /**
         * Brightness only
         */
        public static final short brightnessonly = 19;

        /**
         * Cancel
         */
        public static final short cancel = 20;

        /**
         * Category
         */
        public static final short category = 21;

        /**
         * Chain
         */
        public static final short chain = 22;

        /**
         * Chain editor
         */
        public static final short chainEditor = 23;

        /**
         * Input parameters
         */
        public static final short chainInputs = 24;

        /**
         * Output parameters
         */
        public static final short chainOutputs = 25;

        /**
         * Chains table
         */
        public static final short chainTable = 26;

        /**
         * Change
         */
        public static final short change = 27;

        /**
         * Channels
         */
        public static final short channels = 28;

        /**
         * Choose image :
         */
        public static final short chooseImage = 29;

        /**
         * Connect
         */
        public static final short choosercoveragestore_connect = 340;

        /**
         * Open coverage store
         */
        public static final short choosercoveragestore_label = 341;

        /**
         * New
         */
        public static final short choosercoveragestore_new = 342;

        /**
         * OK
         */
        public static final short choosercoveragestore_ok = 343;

        /**
         * Connect
         */
        public static final short chooserfeaturestore_connect = 344;

        /**
         * Open feature store
         */
        public static final short chooserfeaturestore_label = 345;

        /**
         * New
         */
        public static final short chooserfeaturestore_new = 346;

        /**
         * OK
         */
        public static final short chooserfeaturestore_ok = 347;

        /**
         * Connect
         */
        public static final short chooserserver_connect = 348;

        /**
         * Open serveur connection
         */
        public static final short chooserserver_label = 349;

        /**
         * OK
         */
        public static final short chooserserver_ok = 350;

        /**
         * Classes
         */
        public static final short classes = 30;

        /**
         * Classify
         */
        public static final short classify = 31;

        /**
         * Clear selection
         */
        public static final short clear_selection = 351;

        /**
         * Clipboard
         */
        public static final short clipboard = 32;

        /**
         * Code
         */
        public static final short code = 33;

        /**
         * Crs
         */
        public static final short colCrs = 34;

        /**
         * Delete
         */
        public static final short colDelete = 35;

        /**
         * Duplicate
         */
        public static final short colDuplicate = 36;

        /**
         * Edit
         */
        public static final short colEdit = 37;

        /**
         * Job
         */
        public static final short colJob = 38;

        /**
         * Log
         */
        public static final short colLog = 39;

        /**
         * Lower
         */
        public static final short colLower = 40;

        /**
         * Metadata
         */
        public static final short colMetadata = 41;

        /**
         * Pause
         */
        public static final short colPause = 42;

        /**
         * Play
         */
        public static final short colPlay = 43;

        /**
         * Priority
         */
        public static final short colPriority = 44;

        /**
         * Progress
         */
        public static final short colProgress = 45;

        /**
         * Remaining time
         */
        public static final short colRemain = 46;

        /**
         * Responsible
         */
        public static final short colResponsible = 47;

        /**
         * Start date
         */
        public static final short colStartDate = 48;

        /**
         * Status
         */
        public static final short colStatus = 49;

        /**
         * Stop
         */
        public static final short colStop = 50;

        /**
         * Upper
         */
        public static final short colUpper = 51;

        /**
         * Version
         */
        public static final short colVersion = 52;

        /**
         * View
         */
        public static final short colView = 53;

        /**
         * Color
         */
        public static final short color = 54;

        /**
         * Colors
         */
        public static final short colors = 55;

        /**
         * Commit
         */
        public static final short commit = 56;

        /**
         * Compare
         */
        public static final short compare = 57;

        /**
         * completed
         */
        public static final short completed = 58;

        /**
         * False
         */
        public static final short conditionalFailed = 59;

        /**
         * True
         */
        public static final short conditionalSuccess = 60;

        /**
         * Conditional
         */
        public static final short conditionalTitle = 61;

        /**
         * Configuration of process
         */
        public static final short configProcessTitle = 62;

        /**
         * Confirm deletion ?
         */
        public static final short confirm_delete = 352;

        /**
         * Constant
         */
        public static final short constantTitle = 63;

        /**
         * copy
         */
        public static final short contexttreetable_a_copy = 353;

        /**
         * Activated
         */
        public static final short contexttreetable_activated = 354;

        /**
         * Contexts
         */
        public static final short contexttreetable_contexts = 355;

        /**
         * Copy
         */
        public static final short contexttreetable_copy = 356;

        /**
         * Cut
         */
        public static final short contexttreetable_cut = 357;

        /**
         * Delete
         */
        public static final short contexttreetable_delete = 358;

        /**
         * Duplicate
         */
        public static final short contexttreetable_duplicate = 359;

        /**
         * Feature table
         */
        public static final short contexttreetable_feature_table = 360;

        /**
         * Layers
         */
        public static final short contexttreetable_layers = 361;

        /**
         * New layer group
         */
        public static final short contexttreetable_newgroup = 362;

        /**
         * Paste
         */
        public static final short contexttreetable_paste = 363;

        /**
         * Properties
         */
        public static final short contexttreetable_properties = 364;

        /**
         * Visible
         */
        public static final short contexttreetable_visible = 365;

        /**
         * Contrast
         */
        public static final short contrast = 64;

        /**
         * Coordinates :
         */
        public static final short coordinates_title = 366;

        /**
         * Copy
         */
        public static final short copy = 65;

        /**
         * Copy selection
         */
        public static final short copyselection = 66;

        /**
         * Copy selection (append)
         */
        public static final short copyselectionappend = 67;

        /**
         * Copy to application clipboard
         */
        public static final short copytoappclipboard = 68;

        /**
         * Copy to system clipboard
         */
        public static final short copytosysclipboard = 69;

        /**
         * Count
         */
        public static final short count = 70;

        /**
         * Advanced
         */
        public static final short cql_advanced = 367;

        /**
         * Simple
         */
        public static final short cql_simple = 368;

        /**
         * CQL filter (Ex : Attribute = 'text' or attribute > number)
         */
        public static final short cql_text_help = 369;

        /**
         * Create
         */
        public static final short create = 71;

        /**
         * Create hole
         */
        public static final short createHole = 72;

        /**
         * Create linestring
         */
        public static final short createLineString = 73;

        /**
         * Create multi-linestring
         */
        public static final short createMultiLineString = 74;

        /**
         * Create multi-point
         */
        public static final short createMultiPoint = 75;

        /**
         * Create multi-polygon
         */
        public static final short createMultiPolygon = 76;

        /**
         * Create new
         */
        public static final short createNew = 77;

        /**
         * Create geometry piece
         */
        public static final short createPart = 78;

        /**
         * Create point
         */
        public static final short createPoint = 79;

        /**
         * Create polygon
         */
        public static final short createPolygon = 80;

        /**
         * Creation date
         */
        public static final short creationDate = 81;

        /**
         * CRS
         */
        public static final short crs = 82;

        /**
         * Coordinate system :
         */
        public static final short crs_title = 370;

        /**
         * Apply
         */
        public static final short crschooser_apply = 371;

        /**
         * Cancel
         */
        public static final short crschooser_cancel = 372;

        /**
         * Coordinate Reference Systems
         */
        public static final short crschooser_crs = 373;

        /**
         * List
         */
        public static final short crschooser_list = 374;

        /**
         * CRS Chooser
         */
        public static final short crschooser_title = 375;

        /**
         * WKT
         */
        public static final short crschooser_wkt = 376;

        /**
         * Dashes
         */
        public static final short dashes = 83;

        /**
         * Data
         */
        public static final short data = 84;

        /**
         * Data structure
         */
        public static final short dataStructure = 85;

        /**
         * Datas
         */
        public static final short datas = 86;

        /**
         * Default value
         */
        public static final short defaultValue = 87;

        /**
         * Default style
         */
        public static final short defaut = 88;

        /**
         * Delete
         */
        public static final short delete = 89;

        /**
         * Deleting this chain will delete related jobs. Are you sure to delete it ?
         */
        public static final short deleteChainAndJobs = 90;

        /**
         * Delete chain
         */
        public static final short deleteChainTitle = 91;

        /**
         * Do you really want to delete this job?
         */
        public static final short deleteJob = 92;

        /**
         * Delete job
         */
        public static final short deleteJobTitle = 93;

        /**
         * Description
         */
        public static final short description = 94;

        /**
         * Deviation
         */
        public static final short deviation = 95;

        /**
         * Displacement
         */
        public static final short displacement = 96;

        /**
         * Displacement X
         */
        public static final short displacementX = 97;

        /**
         * Displacement Y
         */
        public static final short displacementY = 98;

        /**
         * Division
         */
        public static final short division = 99;

        /**
         * Douglas Peuker
         */
        public static final short douglaspeuker = 100;

        /**
         * Down
         */
        public static final short down = 101;

        /**
         * Failed to download metadata document.
         */
        public static final short downloadMetadataFail = 102;

        /**
         * Downloading chains list
         */
        public static final short downloadingChains = 103;

        /**
         * Downloading jobs list
         */
        public static final short downloadingJobs = 104;

        /**
         * Downloading status list
         */
        public static final short downloadingStatus = 105;

        /**
         * Edit
         */
        public static final short edit = 106;

        /**
         * Edit nodes
         */
        public static final short editNode = 107;

        /**
         * Edit symbol
         */
        public static final short editSymbol = 108;

        /**
         * Edition tool
         */
        public static final short editTool = 109;

        /**
         * All dimension of layer need to be selected before editing.style_twostate_simple=Simple
         */
        public static final short edition_coverage_warrningdimension = 377;

        /**
         * Groovy script
         */
        public static final short editor = 110;

        /**
         * Default value
         */
        public static final short editorHelpDefaultLabel = 111;

        /**
         * Description
         */
        public static final short editorHelpDescriptionLabel = 112;

        /**
         * mandatory
         */
        public static final short editorHelpMandatory = 113;

        /**
         * Mandatory
         */
        public static final short editorHelpMandatoryLabel = 114;

        /**
         * optional
         */
        public static final short editorHelpOptional = 115;

        /**
         * Data type
         */
        public static final short editorHelpTypeLabel = 116;

        /**
         * Unit
         */
        public static final short editorHelpUnitLabel = 117;

        /**
         * Valid values
         */
        public static final short editorHelpValidLabel = 118;

        /**
         * El
         */
        public static final short el = 119;

        /**
         * Elevation attributs
         */
        public static final short elevation_configuration = 378;

        /**
         * Is else filter
         */
        public static final short else_filter = 379;

        /**
         * Active
         */
        public static final short enabled = 120;

        /**
         * End
         */
        public static final short end = 121;

        /**
         * Error while publishing chain
         */
        public static final short errorPublish = 122;

        /**
         * Unable to reach the server
         */
        public static final short errorURL = 123;

        /**
         * Estimated time (min)
         */
        public static final short estimatedTime = 124;

        /**
         * Execute
         */
        public static final short execute = 125;

        /**
         * Test execution
         */
        public static final short executeTest = 126;

        /**
         * Export XML
         */
        public static final short exportXML = 127;

        /**
         * External
         */
        public static final short external = 128;

        /**
         * Factor
         */
        public static final short factor = 129;

        /**
         * failed
         */
        public static final short failed = 130;

        /**
         * Family
         */
        public static final short family = 131;

        /**
         * Feature type name
         */
        public static final short featuretypename = 132;

        /**
         * File trigger
         */
        public static final short fileTrigger = 133;

        /**
         * Fill
         */
        public static final short fill = 134;

        /**
         * Fill Graphic
         */
        public static final short fillgraphic = 135;

        /**
         * Filter
         */
        public static final short filter = 136;

        /**
         * Filter
         */
        public static final short filter2 = 137;

        /**
         * Apply filters
         */
        public static final short filterApply = 138;

        /**
         * Reset filters
         */
        public static final short filterReset = 139;

        /**
         * Access Database
         */
        public static final short filter_access = 380;

        /**
         * ESRI ASCII Grid
         */
        public static final short filter_asc = 381;

        /**
         * CSV
         */
        public static final short filter_csv = 382;

        /**
         * Dimap, Spot image
         */
        public static final short filter_dimap = 383;

        /**
         * AutoCAD DWG
         */
        public static final short filter_dwg = 384;

        /**
         * ERMapper Compressed Wavelets
         */
        public static final short filter_ecw = 385;

        /**
         * Erdas Imagine
         */
        public static final short filter_erdas_img = 386;

        /**
         * Folders
         */
        public static final short filter_folder = 387;

        /**
         * Georeferenced Tiff
         */
        public static final short filter_geotiff = 388;

        /**
         * GML
         */
        public static final short filter_gml = 389;

        /**
         * Joint Photographic Experts Group
         */
        public static final short filter_jpg = 390;

        /**
         * JPEG 2000
         */
        public static final short filter_jpg2 = 391;

        /**
         * Keyhole Markup Language - Google Earth/Map
         */
        public static final short filter_kml = 392;

        /**
         * Zipped Keyhole Markup Language - Google Earth/Map
         */
        public static final short filter_kmz = 393;

        /**
         * MicroStation
         */
        public static final short filter_microstation_dgn = 394;

        /**
         * MapInfo Exchange
         */
        public static final short filter_mif = 395;

        /**
         * NetCDF-CF, NcML, Grib 1-2
         */
        public static final short filter_netcdfgrib = 396;

        /**
         * GPS Measures (NMEA)
         */
        public static final short filter_nmea = 397;

        /**
         * Adobe PDF
         */
        public static final short filter_pdf = 398;

        /**
         * Portable Network Graphics
         */
        public static final short filter_png = 399;

        /**
         * ENC S-57
         */
        public static final short filter_s57 = 400;

        /**
         * ESRI Shapefile
         */
        public static final short filter_shapefile = 401;

        /**
         * Style Layer Descriptor
         */
        public static final short filter_sld = 402;

        /**
         * Scalable Vector Graphics
         */
        public static final short filter_svg = 403;

        /**
         * MapInfo
         */
        public static final short filter_tab = 404;

        /**
         * Tagged Image File Format
         */
        public static final short filter_tiff = 405;

        /**
         * VisualDEM
         */
        public static final short filter_vdem = 406;

        /**
         * Web Map Context
         */
        public static final short filter_wmc = 407;

        /**
         * World Image
         */
        public static final short filter_world_image = 408;

        /**
         * Filters
         */
        public static final short filters = 140;

        /**
         * Folder
         */
        public static final short folder = 141;

        /**
         * Fonts
         */
        public static final short fonts = 142;

        /**
         * Force longitude first
         */
        public static final short force_longitude_first = 409;

        /**
         * From file :
         */
        public static final short fromFile = 143;

        /**
         * Function
         */
        public static final short function = 144;

        /**
         * Gamma
         */
        public static final short gamma = 145;

        /**
         * Gap
         */
        public static final short gap = 146;

        /**
         * General
         */
        public static final short general = 147;

        /**
         * Generalized
         */
        public static final short generalize = 148;

        /**
         * Generate
         */
        public static final short generate = 149;

        /**
         * Geographic
         */
        public static final short geographic = 150;

        /**
         * Geometry
         */
        public static final short geometry = 151;

        /**
         * Drag view
         */
        public static final short gesture_drag = 410;

        /**
         * Add new geometry
         */
        public static final short gesture_geom_add = 411;

        /**
         * Delete geometry
         */
        public static final short gesture_geom_delete = 412;

        /**
         * Move geometry
         */
        public static final short gesture_geom_move = 413;

        /**
         * Select geometry
         */
        public static final short gesture_geom_select = 414;

        /**
         * Add node
         */
        public static final short gesture_node_add = 415;

        /**
         * Delete node
         */
        public static final short gesture_node_delete = 416;

        /**
         * Move node
         */
        public static final short gesture_node_move = 417;

        /**
         * Select node
         */
        public static final short gesture_node_select = 418;

        /**
         * Add sub-geometry
         */
        public static final short gesture_subgeom_add = 419;

        /**
         * Delete sub-geometry
         */
        public static final short gesture_subgeom_delete = 420;

        /**
         * Move sub-geometry
         */
        public static final short gesture_subgeom_move = 421;

        /**
         * Validate sub-geometry
         */
        public static final short gesture_subgeom_validate = 422;

        /**
         * Validate
         */
        public static final short gesture_validate = 423;

        /**
         * Zoom +/-
         */
        public static final short gesture_zoom = 424;

        /**
         * Graphics
         */
        public static final short graphic = 152;

        /**
         * Graphic color
         */
        public static final short graphic_color = 425;

        /**
         * Graphic fill
         */
        public static final short graphic_fill = 426;

        /**
         * Graphic stroke
         */
        public static final short graphic_stroke = 427;

        /**
         * Gray channel
         */
        public static final short gray = 153;

        /**
         * Green channel
         */
        public static final short green = 154;

        /**
         * Grid
         */
        public static final short grid = 155;

        /**
         * Groovy editor
         */
        public static final short groovyEditror = 156;

        /**
         * Groovy script
         */
        public static final short groovyScriptLbl = 157;

        /**
         * Advanced
         */
        public static final short guiToogleAdvancedView = 158;

        /**
         * Basic
         */
        public static final short guiToogleBasicView = 159;

        /**
         * Intermediate
         */
        public static final short guiToogleInterView = 160;

        /**
         * Halo
         */
        public static final short halo = 161;

        /**
         * Hide help
         */
        public static final short hideHelp = 162;

        /**
         * Identifier
         */
        public static final short identifier = 163;

        /**
         * Image
         */
        public static final short image = 164;

        /**
         * Import XML
         */
        public static final short importXML = 165;

        /**
         * Import/Export
         */
        public static final short import_export = 428;

        /**
         * Initial gap
         */
        public static final short initial_gap = 429;

        /**
         * Not supported input
         */
        public static final short inputNotSupported = 166;

        /**
         * Inputs
         */
        public static final short inputs = 167;

        /**
         * Interpolation
         */
        public static final short interpolation = 168;

        /**
         * Bicubic
         */
        public static final short interpolation_bicubic = 430;

        /**
         * Linear
         */
        public static final short interpolation_linear = 431;

        /**
         * None
         */
        public static final short interpolation_none = 432;

        /**
         * Invert palette
         */
        public static final short invert = 169;

        /**
         * Invert colors
         */
        public static final short invert_palette = 433;

        /**
         * Color map
         */
        public static final short isolineEditor_colormap = 434;

        /**
         * Isolines
         */
        public static final short isolineEditor_displayName = 435;

        /**
         * Show isolines only
         */
        public static final short isolineEditor_isolineOnly = 436;

        /**
         * Line
         */
        public static final short isolineEditor_line = 437;

        /**
         * Show isoline value
         */
        public static final short isolineEditor_showLabel = 438;

        /**
         * Text
         */
        public static final short isolineEditor_text = 439;

        /**
         * Dynamic method, no value/color association.
         */
        public static final short jenks_notable = 440;

        /**
         * Job
         */
        public static final short job = 170;

        /**
         * Job editor
         */
        public static final short jobEditor = 171;

        /**
         * The job has been launch on the server.
         */
        public static final short jobExecuting = 172;

        /**
         * Input parameters
         */
        public static final short jobInputs = 173;

        /**
         * Output parameters
         */
        public static final short jobOutputs = 174;

        /**
         * Jobs table
         */
        public static final short jobTable = 175;

        /**
         * Label
         */
        public static final short label = 176;

        /**
         * Layers
         */
        public static final short layers = 177;

        /**
         * Legend
         */
        public static final short legend = 178;

        /**
         * Lenght
         */
        public static final short lenght = 179;

        /**
         * Font and Style
         */
        public static final short libPoliceStyle = 180;

        /**
         * Line
         */
        public static final short line = 181;

        /**
         * Line shape and color
         */
        public static final short lineShapeAndColor = 182;

        /**
         * Line cap
         */
        public static final short linecap = 183;

        /**
         * Line join
         */
        public static final short linejoin = 184;

        /**
         * Line placement
         */
        public static final short lineplacement = 185;

        /**
         * Loading
         */
        public static final short loading = 186;

        /**
         * Mandatory
         */
        public static final short mandatory = 187;

        /**
         * Manual
         */
        public static final short manual = 188;

        /**
         * Enter a description for the manual intervention.
         */
        public static final short manualDesc = 189;

        /**
         * Manual intervention
         */
        public static final short manualInt = 190;

        /**
         * Map
         */
        public static final short map = 191;

        /**
         * Rendering parameters
         */
        public static final short map_config = 441;

        /**
         * Activate
         */
        public static final short map_control_activate = 442;

        /**
         * cannot calculate coordinate
         */
        public static final short map_control_coord_error = 443;

        /**
         * Coordinate :
         */
        public static final short map_control_mouse_coord = 444;

        /**
         * Unit
         */
        public static final short map_control_unit = 445;

        /**
         * Map Coordinate Reference System
         */
        public static final short map_crs = 446;

        /**
         * Rendering informations
         */
        public static final short map_debug = 447;

        /**
         * Edit mode
         */
        public static final short map_edit = 448;

        /**
         * Elevation slider
         */
        public static final short map_elevation_slider = 449;

        /**
         * Grab informations
         */
        public static final short map_information = 450;

        /**
         * Measure area
         */
        public static final short map_measure_area = 451;

        /**
         * Measure lenght
         */
        public static final short map_measure_lenght = 452;

        /**
         * Move center here
         */
        public static final short map_move_elevation_center = 453;

        /**
         * Move maximum limit here
         */
        public static final short map_move_elevation_maximum = 454;

        /**
         * Move minimum limit here
         */
        public static final short map_move_elevation_minimum = 455;

        /**
         * Move center here
         */
        public static final short map_move_temporal_center = 456;

        /**
         * Move left limit here
         */
        public static final short map_move_temporal_left = 457;

        /**
         * Move right limit here
         */
        public static final short map_move_temporal_right = 458;

        /**
         * Go to coordinate
         */
        public static final short map_nav_to = 459;

        /**
         * Painting
         */
        public static final short map_painting = 460;

        /**
         * Free move
         */
        public static final short map_pan = 461;

        /**
         * Repaint
         */
        public static final short map_refresh = 462;

        /**
         * Remove elevation range
         */
        public static final short map_remove_elevation = 463;

        /**
         * Remove maximum limit
         */
        public static final short map_remove_elevation_maximum = 464;

        /**
         * Remove minimum limit
         */
        public static final short map_remove_elevation_minimum = 465;

        /**
         * Remove time range
         */
        public static final short map_remove_temporal = 466;

        /**
         * Remove left limit
         */
        public static final short map_remove_temporal_left = 467;

        /**
         * Remove right limit
         */
        public static final short map_remove_temporal_right = 468;

        /**
         * Selection mode
         */
        public static final short map_select = 469;

        /**
         * Selection properties
         */
        public static final short map_select_config = 470;

        /**
         * Statefull rendering mode
         */
        public static final short map_statefull = 471;

        /**
         * Render by symbol order
         */
        public static final short map_style_order = 472;

        /**
         * Temporal slider
         */
        public static final short map_temporal_slider = 473;

        /**
         * Preserve X-Y ratio
         */
        public static final short map_xy_ratio = 474;

        /**
         * Zoom to extent
         */
        public static final short map_zoom_all = 475;

        /**
         * Zoom in
         */
        public static final short map_zoom_in = 476;

        /**
         * Next
         */
        public static final short map_zoom_next = 477;

        /**
         * Zoom out
         */
        public static final short map_zoom_out = 478;

        /**
         * Previous
         */
        public static final short map_zoom_previous = 479;

        /**
         * Zoom on layer
         */
        public static final short map_zoom_to_layer = 480;

        /**
         * Mark
         */
        public static final short mark = 192;

        /**
         * Maximum
         */
        public static final short maximum = 193;

        /**
         * Max scale
         */
        public static final short maxscale = 194;

        /**
         * Mean
         */
        public static final short mean = 195;

        /**
         * Median
         */
        public static final short median = 196;

        /**
         * Method
         */
        public static final short method = 197;

        /**
         * Histogram
         */
        public static final short method_histogram = 481;

        /**
         * None
         */
        public static final short method_none = 482;

        /**
         * Normalize
         */
        public static final short method_normalize = 483;

        /**
         * Mime type
         */
        public static final short mime = 198;

        /**
         * Mime type
         */
        public static final short mimeType = 199;

        /**
         * Minimum
         */
        public static final short minimum = 200;

        /**
         * Min scale
         */
        public static final short minscale = 201;

        /**
         * Model
         */
        public static final short model = 202;

        /**
         * Move
         */
        public static final short move = 203;

        /**
         * Move to position
         */
        public static final short movetoposition = 204;

        /**
         * Multithread
         */
        public static final short multithread = 205;

        /**
         * Name
         */
        public static final short name = 206;

        /**
         * NaN value
         */
        public static final short nanValue = 207;

        /**
         * New chain
         */
        public static final short newChain = 208;

        /**
         * New Job
         */
        public static final short newJob = 209;

        /**
         * New Groovy process
         */
        public static final short newProcess = 210;

        /**
         * New trigger
         */
        public static final short newTrigger = 211;

        /**
         * External
         */
        public static final short new_external = 484;

        /**
         * New
         */
        public static final short new_file = 485;

        /**
         * Mark
         */
        public static final short new_mark = 486;

        /**
         * No
         */
        public static final short no = 212;

        /**
         * No data values
         */
        public static final short noData = 213;

        /**
         * no name
         */
        public static final short noname = 214;

        /**
         * None
         */
        public static final short none = 215;

        /**
         * Normalize
         */
        public static final short normalize = 216;

        /**
         * Not supported
         */
        public static final short notSupported = 217;

        /**
         * not started
         */
        public static final short not_started = 487;

        /**
         * Offset
         */
        public static final short offset = 218;

        /**
         * Offset
         */
        public static final short offset2 = 219;

        /**
         * Ok
         */
        public static final short ok = 220;

        /**
         * Opacity
         */
        public static final short opacity = 221;

        /**
         * Open
         */
        public static final short open = 222;

        /**
         * Open
         */
        public static final short open_file = 488;

        /**
         * Operand
         */
        public static final short operand = 223;

        /**
         * Azimuth
         */
        public static final short org_geotoolkit_gui_swing_render3d_azimut = 489;

        /**
         * Layers
         */
        public static final short org_geotoolkit_gui_swing_render3d_layer = 490;

        /**
         * DEM
         */
        public static final short org_geotoolkit_gui_swing_render3d_mnt = 491;

        /**
         * Rotation
         */
        public static final short org_geotoolkit_gui_swing_render3d_rotation = 492;

        /**
         * Show shadows
         */
        public static final short org_geotoolkit_gui_swing_render3d_show = 493;

        /**
         * Others
         */
        public static final short other = 224;

        /**
         * Other rule
         */
        public static final short otherRule = 225;

        /**
         * Outline
         */
        public static final short outline = 226;

        /**
         * Outputs
         */
        public static final short outputs = 227;

        /**
         * Overlap
         */
        public static final short overlap = 228;

        /**
         * Palette
         */
        public static final short palette = 229;

        /**
         * Activate parameter
         */
        public static final short parameters_activateParam = 494;

        /**
         * Add a parameter group
         */
        public static final short parameters_addNewGroupParameter = 495;

        /**
         * Add new parameter
         */
        public static final short parameters_addNewSimpleParameter = 496;

        /**
         * Code
         */
        public static final short parameters_code = 497;

        /**
         * Collapse group
         */
        public static final short parameters_collapse = 498;

        /**
         * Default value
         */
        public static final short parameters_defaultValue = 499;

        /**
         * Description
         */
        public static final short parameters_description = 500;

        /**
         * Default value
         */
        public static final short parameters_editorHelpDefaultLabel = 501;

        /**
         * Description
         */
        public static final short parameters_editorHelpDescriptionLabel = 502;

        /**
         * mandatory
         */
        public static final short parameters_editorHelpMandatory = 503;

        /**
         * Mandatory
         */
        public static final short parameters_editorHelpMandatoryLabel = 504;

        /**
         * optional
         */
        public static final short parameters_editorHelpOptional = 505;

        /**
         * Data type
         */
        public static final short parameters_editorHelpTypeLabel = 506;

        /**
         * Unit
         */
        public static final short parameters_editorHelpUnitLabel = 507;

        /**
         * Valid values
         */
        public static final short parameters_editorHelpValidLabel = 508;

        /**
         * Error
         */
        public static final short parameters_editorHelpValidationErrorLabel = 509;

        /**
         * Parameter can't be null or empty :
         */
        public static final short parameters_errorNullEmptyParameterValue = 510;

        /**
         * Expend group
         */
        public static final short parameters_expend = 511;

        /**
         * Mandatory
         */
        public static final short parameters_mandatory = 512;

        /**
         * Max
         */
        public static final short parameters_max = 513;

        /**
         * Max. occurences
         */
        public static final short parameters_maxOccurs = 514;

        /**
         * Min
         */
        public static final short parameters_min = 515;

        /**
         * Min. occurences
         */
        public static final short parameters_minOccurs = 516;

        /**
         * Occurences :
         */
        public static final short parameters_occurences = 517;

        /**
         * Remove group value
         */
        public static final short parameters_removeGroup = 518;

        /**
         * Remove parameter
         */
        public static final short parameters_removeParameter = 519;

        /**
         * Type
         */
        public static final short parameters_type = 520;

        /**
         * Unactivate parameter
         */
        public static final short parameters_unactivateParam = 521;

        /**
         * Password
         */
        public static final short password = 230;

        /**
         * Paste
         */
        public static final short paste = 231;

        /**
         * paused
         */
        public static final short paused = 232;

        /**
         * Placement
         */
        public static final short placement = 233;

        /**
         * Bottom left
         */
        public static final short placement_bottomleft = 522;

        /**
         * Bottom
         */
        public static final short placement_bottommiddle = 523;

        /**
         * Bottom right
         */
        public static final short placement_bottomright = 524;

        /**
         * Left
         */
        public static final short placement_centerleft = 525;

        /**
         * Center
         */
        public static final short placement_centermiddle = 526;

        /**
         * Right
         */
        public static final short placement_centerright = 527;

        /**
         * Top left
         */
        public static final short placement_topleft = 528;

        /**
         * Top
         */
        public static final short placement_topmiddle = 529;

        /**
         * Top right
         */
        public static final short placement_topright = 530;

        /**
         * Plain color
         */
        public static final short plainColor = 234;

        /**
         * Planning table
         */
        public static final short planningTable = 235;

        /**
         * Point :
         */
        public static final short point = 236;

        /**
         * Point placement
         */
        public static final short pointplacement = 237;

        /**
         * Polygon
         */
        public static final short polygon = 238;

        /**
         * Position
         */
        public static final short position = 239;

        /**
         * Predefined shapes
         */
        public static final short predefinedShape = 240;

        /**
         * Priority
         */
        public static final short priority = 241;

        /**
         * Groovy process
         */
        public static final short processGroovy = 242;

        /**
         * Name
         */
        public static final short processIdLbl = 243;

        /**
         * Input parameters of process
         */
        public static final short processInputParameters = 244;

        /**
         * Editor
         */
        public static final short processTabEditor = 245;

        /**
         * Groovy process list
         */
        public static final short processTable = 246;

        /**
         * Processes
         */
        public static final short processes = 247;

        /**
         * processing
         */
        public static final short processing = 248;

        /**
         * Properties
         */
        public static final short properties = 249;

        /**
         * Property
         */
        public static final short property = 250;

        /**
         * Action
         */
        public static final short property_action = 531;

        /**
         * All
         */
        public static final short property_all = 532;

        /**
         * Apply
         */
        public static final short property_apply = 533;

        /**
         * Close
         */
        public static final short property_close = 534;

        /**
         * Geometric
         */
        public static final short property_cql_advance = 535;

        /**
         * Commun
         */
        public static final short property_cql_basic = 536;

        /**
         * Error in CQL query
         */
        public static final short property_cql_error = 537;

        /**
         * Fields
         */
        public static final short property_cql_field = 538;

        /**
         * CQL
         */
        public static final short property_cql_filter = 539;

        /**
         * Edit
         */
        public static final short property_edit = 540;

        /**
         * Editor
         */
        public static final short property_editor = 541;

        /**
         * Feature table
         */
        public static final short property_feature_table = 542;

        /**
         * Filter
         */
        public static final short property_filter = 543;

        /**
         * General
         */
        public static final short property_general_title = 544;

        /**
         * Description :
         */
        public static final short property_info_description = 545;

        /**
         * Keywords :
         */
        public static final short property_info_keyword = 546;

        /**
         * Publisher :
         */
        public static final short property_info_publisher = 547;

        /**
         * Schema :
         */
        public static final short property_info_schema = 548;

        /**
         * Source :
         */
        public static final short property_info_source = 549;

        /**
         * Info title :
         */
        public static final short property_info_title = 550;

        /**
         * Properties
         */
        public static final short property_properties = 551;

        /**
         * Revert
         */
        public static final short property_revert = 552;

        /**
         * Style
         */
        public static final short property_style = 553;

        /**
         * Advanced
         */
        public static final short property_style_advanced = 554;

        /**
         * Classification : interval
         */
        public static final short property_style_classification_interval = 555;

        /**
         * Classification : unique symbol
         */
        public static final short property_style_classification_unique = 556;

        /**
         * Color map
         */
        public static final short property_style_colormap = 557;

        /**
         * Jenks classification
         */
        public static final short property_style_jenks = 558;

        /**
         * Label
         */
        public static final short property_style_label = 559;

        /**
         * Enable Label
         */
        public static final short property_style_label_enable = 560;

        /**
         * Simple
         */
        public static final short property_style_simple = 561;

        /**
         * Style
         */
        public static final short property_style_style = 562;

        /**
         * Unknown Layer Style
         */
        public static final short property_style_unknown_simplestyle = 563;

        /**
         * Title :
         */
        public static final short property_title = 564;

        /**
         * Publish
         */
        public static final short publish = 251;

        /**
         * Publish chain
         */
        public static final short publishChain = 252;

        /**
         * Qantile
         */
        public static final short qantile = 253;

        /**
         * Quantity
         */
        public static final short quantity = 254;

        /**
         * Radius
         */
        public static final short radius = 255;

        /**
         * Raw data
         */
        public static final short rawData = 256;

        /**
         * Red channel
         */
        public static final short red = 257;

        /**
         * Refresh
         */
        public static final short refresh = 258;

        /**
         * Refresh table
         */
        public static final short refresh_table = 565;

        /**
         * Related to
         */
        public static final short relatedTo = 259;

        /**
         * Relief
         */
        public static final short relief = 260;

        /**
         * Remove hole
         */
        public static final short removeHole = 261;

        /**
         * Remove geometry piece
         */
        public static final short removePart = 262;

        /**
         * Remove all values
         */
        public static final short remove_all_values = 566;

        /**
         * Repeat interval (min)
         */
        public static final short repeatTime = 263;

        /**
         * Repeated
         */
        public static final short repeated = 264;

        /**
         * Responsible
         */
        public static final short responsible = 265;

        /**
         * RGB
         */
        public static final short rgb = 266;

        /**
         * RollBack
         */
        public static final short rollback = 267;

        /**
         * Rotation
         */
        public static final short rotation = 268;

        /**
         * Run
         */
        public static final short run = 269;

        /**
         * Area table
         */
        public static final short s52_areatable = 567;

        /**
         * Attributes filter
         */
        public static final short s52_attributefilter = 568;

        /**
         * Background
         */
        public static final short s52_background = 569;

        /**
         * Base
         */
        public static final short s52_base = 570;

        /**
         * Category
         */
        public static final short s52_category = 571;

        /**
         * Chart
         */
        public static final short s52_chart = 572;

        /**
         * Object class
         */
        public static final short s52_class = 573;

        /**
         * Code
         */
        public static final short s52_code = 574;

        /**
         * Contour labels
         */
        public static final short s52_contourlabel = 575;

        /**
         * Deep contour
         */
        public static final short s52_deepcontour = 576;

        /**
         * Description
         */
        public static final short s52_description = 577;

        /**
         * Details
         */
        public static final short s52_detail = 578;

        /**
         * Distance tag
         */
        public static final short s52_distancetag = 579;

        /**
         * Filter
         */
        public static final short s52_filter = 580;

        /**
         * Full sector
         */
        public static final short s52_fullsector = 581;

        /**
         * Global
         */
        public static final short s52_global = 582;

        /**
         * Show isolated danger (shallow water)
         */
        public static final short s52_isolateDanger = 583;

        /**
         * Light description
         */
        public static final short s52_lightdescription = 584;

        /**
         * Line table
         */
        public static final short s52_linetable = 585;

        /**
         * Low accuracy symbols
         */
        public static final short s52_lowaccsymbol = 586;

        /**
         * Mariner
         */
        public static final short s52_mariner = 587;

        /**
         * No text
         */
        public static final short s52_notext = 588;

        /**
         * Other
         */
        public static final short s52_other = 589;

        /**
         * Palette
         */
        public static final short s52_palette = 590;

        /**
         * Palettes
         */
        public static final short s52_palettes = 591;

        /**
         * Point table
         */
        public static final short s52_pointtable = 592;

        /**
         * Preview
         */
        public static final short s52_preview = 593;

        /**
         * Priority
         */
        public static final short s52_priority = 594;

        /**
         * Radar
         */
        public static final short s52_radar = 595;

        /**
         * null
         */
        public static final short s52_radarnull = 596;

        /**
         * Over
         */
        public static final short s52_radarover = 597;

        /**
         * Under
         */
        public static final short s52_radarunder = 598;

        /**
         * Rules
         */
        public static final short s52_rules = 599;

        /**
         * Safety contour
         */
        public static final short s52_safetycontour = 600;

        /**
         * Safety depth (meters)
         */
        public static final short s52_safetydepth = 601;

        /**
         * Scale filter (SCAMIN/SCAMAX)
         */
        public static final short s52_scalefilter = 602;

        /**
         * Shallow contour
         */
        public static final short s52_shallowcontour = 603;

        /**
         * Shallow pattern
         */
        public static final short s52_shallowpattern = 604;

        /**
         * Ship outline
         */
        public static final short s52_shipoutline = 605;

        /**
         * Standard
         */
        public static final short s52_standard = 606;

        /**
         * Symbol instructions
         */
        public static final short s52_symbolinstructions = 607;

        /**
         * Symbols
         */
        public static final short s52_symbols = 608;

        /**
         * Time tag
         */
        public static final short s52_timetag = 609;

        /**
         * Title
         */
        public static final short s52_title = 610;

        /**
         * Two shades
         */
        public static final short s52_twoshades = 611;

        /**
         * Viewing group
         */
        public static final short s52_viewinggroup = 612;

        /**
         * Visible
         */
        public static final short s52_visible = 613;

        /**
         * Save
         */
        public static final short save = 270;

        /**
         * Save as Draft
         */
        public static final short saveDraft = 271;

        /**
         * Save
         */
        public static final short save_file = 614;

        /**
         * Save as ...
         */
        public static final short saveas_file = 615;

        /**
         * Scale
         */
        public static final short scale = 272;

        /**
         * Scale :
         */
        public static final short scale_title = 616;

        /**
         * Edition area
         */
        public static final short select_area = 617;

        /**
         * Geographic
         */
        public static final short select_geographic = 618;

        /**
         * Intersect
         */
        public static final short select_intersect = 619;

        /**
         * Free form
         */
        public static final short select_lasso = 620;

        /**
         * Rectangle
         */
        public static final short select_square = 621;

        /**
         * Visual
         */
        public static final short select_visual = 622;

        /**
         * Within
         */
        public static final short select_within = 623;

        /**
         * Selected channels
         */
        public static final short selecteds = 273;

        /**
         * Any
         */
        public static final short semantic_any = 624;

        /**
         * Lines
         */
        public static final short semantic_line = 625;

        /**
         * Points
         */
        public static final short semantic_point = 626;

        /**
         * Polygons
         */
        public static final short semantic_polygon = 627;

        /**
         * Rasters
         */
        public static final short semantic_raster = 628;

        /**
         * Texts
         */
        public static final short semantic_text = 629;

        /**
         * server URL
         */
        public static final short serverURL = 274;

        /**
         * Commit
         */
        public static final short sessionCommit = 275;

        /**
         * Rollback
         */
        public static final short sessionRollback = 276;

        /**
         * Shape border
         */
        public static final short shapeBorder = 277;

        /**
         * Shape fill
         */
        public static final short shapeFill = 278;

        /**
         * Exp
         */
        public static final short shortexpression = 279;

        /**
         * Display help
         */
        public static final short showHelp = 280;

        /**
         * Show identifying properties
         */
        public static final short show_id = 630;

        /**
         * Shapefile creation
         */
        public static final short shp_Shapefile_creation = 632;

        /**
         * Add
         */
        public static final short shp_add = 633;

        /**
         * Attributs
         */
        public static final short shp_attributs = 634;

        /**
         * Create
         */
        public static final short shp_create = 635;

        /**
         * CRS
         */
        public static final short shp_crs = 636;

        /**
         * default
         */
        public static final short shp_default = 637;

        /**
         * Delete
         */
        public static final short shp_delete = 638;

        /**
         * Down
         */
        public static final short shp_down = 639;

        /**
         * File :
         */
        public static final short shp_file = 640;

        /**
         * Geometry
         */
        public static final short shp_geometry = 641;

        /**
         * List
         */
        public static final short shp_list = 642;

        /**
         * MultiLine
         */
        public static final short shp_multiline = 643;

        /**
         * MultiPoint
         */
        public static final short shp_multipoint = 644;

        /**
         * MultiPolygon
         */
        public static final short shp_multipolygon = 645;

        /**
         * Name
         */
        public static final short shp_name = 646;

        /**
         * Point
         */
        public static final short shp_point = 647;

        /**
         * ...
         */
        public static final short shp_ppp = 631;

        /**
         * Shapefile creation
         */
        public static final short shp_shapefile_creation = 648;

        /**
         * Type
         */
        public static final short shp_type = 649;

        /**
         * Up
         */
        public static final short shp_up = 650;

        /**
         * Simplification
         */
        public static final short simplification = 281;

        /**
         * Single
         */
        public static final short single = 282;

        /**
         * Size
         */
        public static final short size = 283;

        /**
         * SLD
         */
        public static final short sld = 284;

        /**
         * Editor
         */
        public static final short sldeditor = 285;

        /**
         * Spatial
         */
        public static final short spatial = 286;

        /**
         * Standard
         */
        public static final short standard = 287;

        /**
         * Start edition
         */
        public static final short start = 288;

        /**
         * Start time
         */
        public static final short startTime = 289;

        /**
         * started
         */
        public static final short started = 290;

        /**
         * Status table
         */
        public static final short statusTable = 291;

        /**
         * Stop
         */
        public static final short stop = 292;

        /**
         * Stroke
         */
        public static final short stroke = 293;

        /**
         * Stroke Graphic
         */
        public static final short strokegraphic = 294;

        /**
         * Style
         */
        public static final short style = 295;

        /**
         * Style bank
         */
        public static final short styleBank = 296;

        /**
         * X
         */
        public static final short style_anchorpoint_x = 651;

        /**
         * Y
         */
        public static final short style_anchorpoint_y = 652;

        /**
         * Cell size
         */
        public static final short style_cellsymbolizer_cellsize = 653;

        /**
         * Edit
         */
        public static final short style_cellsymbolizer_edit = 654;

        /**
         * Filter
         */
        public static final short style_cellsymbolizer_filter = 655;

        /**
         * Cell patches
         */
        public static final short style_cellsymbolizer_tooltip = 656;

        /**
         * Type
         */
        public static final short style_cellsymbolizer_type = 657;

        /**
         * Blue channel
         */
        public static final short style_channelSelection_bluechannel = 658;

        /**
         * Override grayscale
         */
        public static final short style_channelSelection_gray = 659;

        /**
         * Gray channel
         */
        public static final short style_channelSelection_graychannel = 660;

        /**
         * Green channel
         */
        public static final short style_channelSelection_greenchannel = 661;

        /**
         * Red channel
         */
        public static final short style_channelSelection_redchannel = 662;

        /**
         * Override RGB
         */
        public static final short style_channelSelection_rgb = 663;

        /**
         * Native colors
         */
        public static final short style_channelselection_native = 664;

        /**
         * Band
         */
        public static final short style_rastercolormappane_band = 665;

        /**
         * Fit to data
         */
        public static final short style_rastercolormappane_fittodata = 666;

        /**
         * Interpolate
         */
        public static final short style_rastercolormappane_interpolate = 667;

        /**
         * Invert
         */
        public static final short style_rastercolormappane_invert = 668;

        /**
         * NaN
         */
        public static final short style_rastercolormappane_nan = 669;

        /**
         * Palette
         */
        public static final short style_rastercolormappane_palette = 670;

        /**
         * Channels
         */
        public static final short style_rastersymbolizer_channels = 671;

        /**
         * ColorMap
         */
        public static final short style_rastersymbolizer_cm_colormap = 672;

        /**
         * Grayscale/RGB
         */
        public static final short style_rastersymbolizer_cm_rgb = 673;

        /**
         * Color
         */
        public static final short style_rastersymbolizer_color = 674;

        /**
         * Color model
         */
        public static final short style_rastersymbolizer_colormodel = 675;

        /**
         * Divisions
         */
        public static final short style_rastersymbolizer_divisions = 676;

        /**
         * Edit
         */
        public static final short style_rastersymbolizer_edit = 677;

        /**
         * General
         */
        public static final short style_rastersymbolizer_general = 678;

        /**
         * Line
         */
        public static final short style_rastersymbolizer_line = 679;

        /**
         * Lower
         */
        public static final short style_rastersymbolizer_lower = 680;

        /**
         * None
         */
        public static final short style_rastersymbolizer_none = 681;

        /**
         * Opacity
         */
        public static final short style_rastersymbolizer_opacity = 682;

        /**
         * Outline
         */
        public static final short style_rastersymbolizer_outline = 683;

        /**
         * Overlap
         */
        public static final short style_rastersymbolizer_overlap = 684;

        /**
         * Polygon
         */
        public static final short style_rastersymbolizer_polygon = 685;

        /**
         * Upper
         */
        public static final short style_rastersymbolizer_upper = 686;

        /**
         * Value
         */
        public static final short style_rastersymbolizer_value = 687;

        /**
         * Advanced
         */
        public static final short style_twostate_advanced = 688;

        /**
         * Simple
         */
        public static final short style_twostate_simple = 689;

        /**
         * Sum
         */
        public static final short sum = 297;

        /**
         * Symbol
         */
        public static final short symbol = 298;

        /**
         * Symbols layer
         */
        public static final short symbolLayer = 299;

        /**
         * Symbol preview
         */
        public static final short symbolPreview = 300;

        /**
         * Line symbol
         */
        public static final short symbol_line = 690;

        /**
         * Point symbol
         */
        public static final short symbol_point = 691;

        /**
         * Polygon symbol
         */
        public static final short symbol_polygon = 692;

        /**
         * System clipboard
         */
        public static final short systemclipboard = 301;

        /**
         * Graph
         */
        public static final short tabGraphTitle = 302;

        /**
         * General informations
         */
        public static final short tabInfoTitle = 303;

        /**
         * Metadata
         */
        public static final short tabMetadataTitle = 304;

        /**
         * Speed  T x
         */
        public static final short temp_factor = 693;

        /**
         * Refresh rate (ms) =
         */
        public static final short temp_refresh = 694;

        /**
         * Temporal
         */
        public static final short temporal = 305;

        /**
         * Temporal attributs
         */
        public static final short temporal_configuration = 695;

        /**
         * Ending attribut
         */
        public static final short temporal_end = 696;

        /**
         * Starting attribut
         */
        public static final short temporal_start = 697;

        /**
         * Text color
         */
        public static final short textColor = 306;

        /**
         * Time trigger
         */
        public static final short timeTrigger = 307;

        /**
         * Title
         */
        public static final short title = 308;

        /**
         * Gap
         */
        public static final short tooltip_gap = 698;

        /**
         * Lenght
         */
        public static final short tooltip_lenght = 699;

        /**
         * Offset
         */
        public static final short tooltip_offset = 700;

        /**
         * Topologic
         */
        public static final short topologic = 309;

        /**
         * Type
         */
        public static final short type = 310;

        /**
         * undefined
         */
        public static final short undefined = 311;

        /**
         * Units
         */
        public static final short unit = 312;

        /**
         * more...
         */
        public static final short uom_more = 701;

        /**
         * Up
         */
        public static final short up = 313;

        /**
         * Update date
         */
        public static final short updateDate = 314;

        /**
         * URL
         */
        public static final short url = 315;

        /**
         * User
         */
        public static final short user = 316;

        /**
         * Validate
         */
        public static final short validate = 317;

        /**
         * Value
         */
        public static final short value = 318;

        /**
         * Version
         */
        public static final short version = 319;

        /**
         * waiting
         */
        public static final short waiting = 320;

        /**
         * Weight
         */
        public static final short weight = 321;

        /**
         * Well Knowed Form
         */
        public static final short wellknowned = 322;

        /**
         * Well Knowed Form
         */
        public static final short wellknownedform = 323;

        /**
         * Well known name
         */
        public static final short wellknownname = 324;

        /**
         * Width
         */
        public static final short width = 325;

        /**
         * Well-Known Text
         */
        public static final short wkt = 326;

        /**
         * WPS Chain
         */
        public static final short wpsChain = 327;

        /**
         * X
         */
        public static final short x = 328;

        /**
         * Graphic
         */
        public static final short xmlGraphic = 329;

        /**
         * XML
         */
        public static final short xmlview = 330;

        /**
         * Y
         */
        public static final short y = 331;

        /**
         * Yes
         */
        public static final short yes = 332;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public MessageBundle(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static MessageBundle getResources(Locale locale) throws MissingResourceException {
        return getBundle(MessageBundle.class, locale);
    }

    /**
     * The international string to be returned by {@link formatInternational}.
     */
    private static final class International extends ResourceInternationalString {
        private static final long serialVersionUID = -9199238559657784488L;

        International(final int key) {
            super(MessageBundle.class.getName(), String.valueOf(key));
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
    public static InternationalString formatInternational(final short key) {
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

    public static String format(String key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

}
