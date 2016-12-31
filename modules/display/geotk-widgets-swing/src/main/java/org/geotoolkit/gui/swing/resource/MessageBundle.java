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
 * @module
 */
public final class MessageBundle extends IndexedResourceBundle {

    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Accreditation
         */
        public static final short Accreditation = 1;

        /**
         * Abstract
         */
        public static final short abstrac = 2;

        /**
         * Add symbol
         */
        public static final short addSymbol = 3;

        /**
         * Add all values
         */
        public static final short add_all_values = 4;

        /**
         * Add dimension
         */
        public static final short add_dimension = 5;

        /**
         * Add value
         */
        public static final short add_value = 6;

        /**
         * Add vector file data ...
         */
        public static final short add_vector_file = 7;

        /**
         * Advanced
         */
        public static final short advanced = 8;

        /**
         * Aligned
         */
        public static final short aligned = 9;

        /**
         * All
         */
        public static final short all = 10;

        /**
         * All Geometries
         */
        public static final short allGeom = 11;

        /**
         * all status
         */
        public static final short allStatus = 12;

        /**
         * Analyze
         */
        public static final short analyze = 13;

        /**
         * Raster analyze
         */
        public static final short analyze_raster = 14;

        /**
         * Vector analyze
         */
        public static final short analyze_vector = 15;

        /**
         * Anchor
         */
        public static final short anchor = 16;

        /**
         * Animation
         */
        public static final short animation = 17;

        /**
         * Anti-Aliasing
         */
        public static final short antialiasing = 18;

        /**
         * Application clipboard
         */
        public static final short applicationclipboard = 19;

        /**
         * Apply
         */
        public static final short apply = 20;

        /**
         * Validity area
         */
        public static final short area_validity = 21;

        /**
         * Background
         */
        public static final short background = 22;

        /**
         * Backward
         */
        public static final short backward = 23;

        /**
         * Begin
         */
        public static final short begin = 24;

        /**
         * Blue channel
         */
        public static final short blue = 25;

        /**
         * Border
         */
        public static final short border = 26;

        /**
         * Brightness only
         */
        public static final short brightnessonly = 27;

        /**
         * Cancel
         */
        public static final short cancel = 28;

        /**
         * Category
         */
        public static final short category = 29;

        /**
         * Chain
         */
        public static final short chain = 30;

        /**
         * Chain editor
         */
        public static final short chainEditor = 31;

        /**
         * Input parameters
         */
        public static final short chainInputs = 32;

        /**
         * Output parameters
         */
        public static final short chainOutputs = 33;

        /**
         * Chains table
         */
        public static final short chainTable = 34;

        /**
         * Change
         */
        public static final short change = 35;

        /**
         * Channels
         */
        public static final short channels = 36;

        /**
         * Choose image :
         */
        public static final short chooseImage = 37;

        /**
         * Connect
         */
        public static final short choosercoveragestore_connect = 38;

        /**
         * Open coverage store
         */
        public static final short choosercoveragestore_label = 39;

        /**
         * New
         */
        public static final short choosercoveragestore_new = 40;

        /**
         * OK
         */
        public static final short choosercoveragestore_ok = 41;

        /**
         * Connect
         */
        public static final short chooserfeaturestore_connect = 42;

        /**
         * Open feature store
         */
        public static final short chooserfeaturestore_label = 43;

        /**
         * New
         */
        public static final short chooserfeaturestore_new = 44;

        /**
         * OK
         */
        public static final short chooserfeaturestore_ok = 45;

        /**
         * Connect
         */
        public static final short chooserserver_connect = 46;

        /**
         * Open serveur connection
         */
        public static final short chooserserver_label = 47;

        /**
         * OK
         */
        public static final short chooserserver_ok = 48;

        /**
         * Classes
         */
        public static final short classes = 49;

        /**
         * Classify
         */
        public static final short classify = 50;

        /**
         * Clear selection
         */
        public static final short clear_selection = 51;

        /**
         * Clipboard
         */
        public static final short clipboard = 52;

        /**
         * Code
         */
        public static final short code = 53;

        /**
         * Crs
         */
        public static final short colCrs = 54;

        /**
         * Delete
         */
        public static final short colDelete = 55;

        /**
         * Duplicate
         */
        public static final short colDuplicate = 56;

        /**
         * Edit
         */
        public static final short colEdit = 57;

        /**
         * Job
         */
        public static final short colJob = 58;

        /**
         * Log
         */
        public static final short colLog = 59;

        /**
         * Lower
         */
        public static final short colLower = 60;

        /**
         * Metadata
         */
        public static final short colMetadata = 61;

        /**
         * Pause
         */
        public static final short colPause = 62;

        /**
         * Play
         */
        public static final short colPlay = 63;

        /**
         * Priority
         */
        public static final short colPriority = 64;

        /**
         * Progress
         */
        public static final short colProgress = 65;

        /**
         * Remaining time
         */
        public static final short colRemain = 66;

        /**
         * Responsible
         */
        public static final short colResponsible = 67;

        /**
         * Start date
         */
        public static final short colStartDate = 68;

        /**
         * Status
         */
        public static final short colStatus = 69;

        /**
         * Stop
         */
        public static final short colStop = 70;

        /**
         * Upper
         */
        public static final short colUpper = 71;

        /**
         * Version
         */
        public static final short colVersion = 72;

        /**
         * View
         */
        public static final short colView = 73;

        /**
         * Color
         */
        public static final short color = 74;

        /**
         * Colors
         */
        public static final short colors = 75;

        /**
         * Commit
         */
        public static final short commit = 76;

        /**
         * Compare
         */
        public static final short compare = 77;

        /**
         * completed
         */
        public static final short completed = 78;

        /**
         * False
         */
        public static final short conditionalFailed = 79;

        /**
         * True
         */
        public static final short conditionalSuccess = 80;

        /**
         * Conditional
         */
        public static final short conditionalTitle = 81;

        /**
         * Configuration of process
         */
        public static final short configProcessTitle = 82;

        /**
         * Confirm deletion ?
         */
        public static final short confirm_delete = 83;

        /**
         * Constant
         */
        public static final short constantTitle = 84;

        /**
         * copy
         */
        public static final short contexttreetable_a_copy = 85;

        /**
         * Activated
         */
        public static final short contexttreetable_activated = 86;

        /**
         * Contexts
         */
        public static final short contexttreetable_contexts = 87;

        /**
         * Copy
         */
        public static final short contexttreetable_copy = 88;

        /**
         * Cut
         */
        public static final short contexttreetable_cut = 89;

        /**
         * Delete
         */
        public static final short contexttreetable_delete = 90;

        /**
         * Duplicate
         */
        public static final short contexttreetable_duplicate = 91;

        /**
         * Feature table
         */
        public static final short contexttreetable_feature_table = 92;

        /**
         * Layers
         */
        public static final short contexttreetable_layers = 93;

        /**
         * New layer group
         */
        public static final short contexttreetable_newgroup = 94;

        /**
         * Paste
         */
        public static final short contexttreetable_paste = 95;

        /**
         * Properties
         */
        public static final short contexttreetable_properties = 96;

        /**
         * Visible
         */
        public static final short contexttreetable_visible = 97;

        /**
         * Contrast
         */
        public static final short contrast = 98;

        /**
         * Coordinates :
         */
        public static final short coordinates_title = 99;

        /**
         * Copy
         */
        public static final short copy = 100;

        /**
         * Copy selection
         */
        public static final short copyselection = 101;

        /**
         * Copy selection (append)
         */
        public static final short copyselectionappend = 102;

        /**
         * Copy to application clipboard
         */
        public static final short copytoappclipboard = 103;

        /**
         * Copy to system clipboard
         */
        public static final short copytosysclipboard = 104;

        /**
         * Count
         */
        public static final short count = 105;

        /**
         * Advanced
         */
        public static final short cql_advanced = 106;

        /**
         * Simple
         */
        public static final short cql_simple = 107;

        /**
         * CQL filter (Ex : Attribute = 'text' or attribute > number)
         */
        public static final short cql_text_help = 108;

        /**
         * Create
         */
        public static final short create = 109;

        /**
         * Create hole
         */
        public static final short createHole = 110;

        /**
         * Create linestring
         */
        public static final short createLineString = 111;

        /**
         * Create multi-linestring
         */
        public static final short createMultiLineString = 112;

        /**
         * Create multi-point
         */
        public static final short createMultiPoint = 113;

        /**
         * Create multi-polygon
         */
        public static final short createMultiPolygon = 114;

        /**
         * Create new
         */
        public static final short createNew = 115;

        /**
         * Create geometry piece
         */
        public static final short createPart = 116;

        /**
         * Create point
         */
        public static final short createPoint = 117;

        /**
         * Create polygon
         */
        public static final short createPolygon = 118;

        /**
         * Creation date
         */
        public static final short creationDate = 119;

        /**
         * CRS
         */
        public static final short crs = 120;

        /**
         * Coordinate system :
         */
        public static final short crs_title = 121;

        /**
         * Apply
         */
        public static final short crschooser_apply = 122;

        /**
         * Cancel
         */
        public static final short crschooser_cancel = 123;

        /**
         * Coordinate Reference Systems
         */
        public static final short crschooser_crs = 124;

        /**
         * List
         */
        public static final short crschooser_list = 125;

        /**
         * CRS Chooser
         */
        public static final short crschooser_title = 126;

        /**
         * WKT
         */
        public static final short crschooser_wkt = 127;

        /**
         * Dashes
         */
        public static final short dashes = 128;

        /**
         * Data
         */
        public static final short data = 129;

        /**
         * Data structure
         */
        public static final short dataStructure = 130;

        /**
         * Datas
         */
        public static final short datas = 131;

        /**
         * Default value
         */
        public static final short defaultValue = 132;

        /**
         * Default style
         */
        public static final short defaut = 133;

        /**
         * Delete
         */
        public static final short delete = 134;

        /**
         * Deleting this chain will delete related jobs. Are you sure to delete it ?
         */
        public static final short deleteChainAndJobs = 135;

        /**
         * Delete chain
         */
        public static final short deleteChainTitle = 136;

        /**
         * Do you really want to delete this job?
         */
        public static final short deleteJob = 137;

        /**
         * Delete job
         */
        public static final short deleteJobTitle = 138;

        /**
         * Description
         */
        public static final short description = 139;

        /**
         * Deviation
         */
        public static final short deviation = 140;

        /**
         * Displacement
         */
        public static final short displacement = 141;

        /**
         * Displacement X
         */
        public static final short displacementX = 142;

        /**
         * Displacement Y
         */
        public static final short displacementY = 143;

        /**
         * Division
         */
        public static final short division = 144;

        /**
         * Douglas Peuker
         */
        public static final short douglaspeuker = 145;

        /**
         * Down
         */
        public static final short down = 146;

        /**
         * Failed to download metadata document.
         */
        public static final short downloadMetadataFail = 147;

        /**
         * Downloading chains list
         */
        public static final short downloadingChains = 148;

        /**
         * Downloading jobs list
         */
        public static final short downloadingJobs = 149;

        /**
         * Downloading status list
         */
        public static final short downloadingStatus = 150;

        /**
         * Edit
         */
        public static final short edit = 151;

        /**
         * Edit nodes
         */
        public static final short editNode = 152;

        /**
         * Edit symbol
         */
        public static final short editSymbol = 153;

        /**
         * Edition tool
         */
        public static final short editTool = 154;

        /**
         * All dimension of layer need to be selected before editing.style_twostate_simple=Simple
         */
        public static final short edition_coverage_warrningdimension = 155;

        /**
         * Groovy script
         */
        public static final short editor = 156;

        /**
         * Default value
         */
        public static final short editorHelpDefaultLabel = 157;

        /**
         * Description
         */
        public static final short editorHelpDescriptionLabel = 158;

        /**
         * mandatory
         */
        public static final short editorHelpMandatory = 159;

        /**
         * Mandatory
         */
        public static final short editorHelpMandatoryLabel = 160;

        /**
         * optional
         */
        public static final short editorHelpOptional = 161;

        /**
         * Data type
         */
        public static final short editorHelpTypeLabel = 162;

        /**
         * Unit
         */
        public static final short editorHelpUnitLabel = 163;

        /**
         * Valid values
         */
        public static final short editorHelpValidLabel = 164;

        /**
         * El
         */
        public static final short el = 165;

        /**
         * Elevation attributs
         */
        public static final short elevation_configuration = 166;

        /**
         * Is else filter
         */
        public static final short else_filter = 167;

        /**
         * Active
         */
        public static final short enabled = 168;

        /**
         * End
         */
        public static final short end = 169;

        /**
         * Error while publishing chain
         */
        public static final short errorPublish = 170;

        /**
         * Unable to reach the server
         */
        public static final short errorURL = 171;

        /**
         * Estimated time (min)
         */
        public static final short estimatedTime = 172;

        /**
         * Execute
         */
        public static final short execute = 173;

        /**
         * Test execution
         */
        public static final short executeTest = 174;

        /**
         * Export XML
         */
        public static final short exportXML = 175;

        /**
         * External
         */
        public static final short external = 176;

        /**
         * Factor
         */
        public static final short factor = 177;

        /**
         * failed
         */
        public static final short failed = 178;

        /**
         * Family
         */
        public static final short family = 179;

        /**
         * Feature type name
         */
        public static final short featuretypename = 180;

        /**
         * File trigger
         */
        public static final short fileTrigger = 181;

        /**
         * Fill
         */
        public static final short fill = 182;

        /**
         * Fill Graphic
         */
        public static final short fillgraphic = 183;

        /**
         * Filter
         */
        public static final short filter = 184;

        /**
         * Filter
         */
        public static final short filter2 = 185;

        /**
         * Apply filters
         */
        public static final short filterApply = 186;

        /**
         * Reset filters
         */
        public static final short filterReset = 187;

        /**
         * Access Database
         */
        public static final short filter_access = 188;

        /**
         * ESRI ASCII Grid
         */
        public static final short filter_asc = 189;

        /**
         * CSV
         */
        public static final short filter_csv = 190;

        /**
         * Dimap, Spot image
         */
        public static final short filter_dimap = 191;

        /**
         * AutoCAD DWG
         */
        public static final short filter_dwg = 192;

        /**
         * ERMapper Compressed Wavelets
         */
        public static final short filter_ecw = 193;

        /**
         * Erdas Imagine
         */
        public static final short filter_erdas_img = 194;

        /**
         * Folders
         */
        public static final short filter_folder = 195;

        /**
         * Georeferenced Tiff
         */
        public static final short filter_geotiff = 196;

        /**
         * GML
         */
        public static final short filter_gml = 197;

        /**
         * Joint Photographic Experts Group
         */
        public static final short filter_jpg = 198;

        /**
         * JPEG 2000
         */
        public static final short filter_jpg2 = 199;

        /**
         * Keyhole Markup Language - Google Earth/Map
         */
        public static final short filter_kml = 200;

        /**
         * Zipped Keyhole Markup Language - Google Earth/Map
         */
        public static final short filter_kmz = 201;

        /**
         * MicroStation
         */
        public static final short filter_microstation_dgn = 202;

        /**
         * MapInfo Exchange
         */
        public static final short filter_mif = 203;

        /**
         * NetCDF-CF, NcML, Grib 1-2
         */
        public static final short filter_netcdfgrib = 204;

        /**
         * GPS Measures (NMEA)
         */
        public static final short filter_nmea = 205;

        /**
         * Adobe PDF
         */
        public static final short filter_pdf = 206;

        /**
         * Portable Network Graphics
         */
        public static final short filter_png = 207;

        /**
         * ENC S-57
         */
        public static final short filter_s57 = 208;

        /**
         * ESRI Shapefile
         */
        public static final short filter_shapefile = 209;

        /**
         * Style Layer Descriptor
         */
        public static final short filter_sld = 210;

        /**
         * Scalable Vector Graphics
         */
        public static final short filter_svg = 211;

        /**
         * MapInfo
         */
        public static final short filter_tab = 212;

        /**
         * Tagged Image File Format
         */
        public static final short filter_tiff = 213;

        /**
         * VisualDEM
         */
        public static final short filter_vdem = 214;

        /**
         * Web Map Context
         */
        public static final short filter_wmc = 215;

        /**
         * World Image
         */
        public static final short filter_world_image = 216;

        /**
         * Filters
         */
        public static final short filters = 217;

        /**
         * Folder
         */
        public static final short folder = 218;

        /**
         * Fonts
         */
        public static final short fonts = 219;

        /**
         * Force longitude first
         */
        public static final short force_longitude_first = 220;

        /**
         * From file :
         */
        public static final short fromFile = 221;

        /**
         * Function
         */
        public static final short function = 222;

        /**
         * Gamma
         */
        public static final short gamma = 223;

        /**
         * Gap
         */
        public static final short gap = 224;

        /**
         * General
         */
        public static final short general = 225;

        /**
         * Generalized
         */
        public static final short generalize = 226;

        /**
         * Generate
         */
        public static final short generate = 227;

        /**
         * Geographic
         */
        public static final short geographic = 228;

        /**
         * Geometry
         */
        public static final short geometry = 229;

        /**
         * Drag view
         */
        public static final short gesture_drag = 230;

        /**
         * Add new geometry
         */
        public static final short gesture_geom_add = 231;

        /**
         * Delete geometry
         */
        public static final short gesture_geom_delete = 232;

        /**
         * Move geometry
         */
        public static final short gesture_geom_move = 233;

        /**
         * Select geometry
         */
        public static final short gesture_geom_select = 234;

        /**
         * Add node
         */
        public static final short gesture_node_add = 235;

        /**
         * Delete node
         */
        public static final short gesture_node_delete = 236;

        /**
         * Move node
         */
        public static final short gesture_node_move = 237;

        /**
         * Select node
         */
        public static final short gesture_node_select = 238;

        /**
         * Add sub-geometry
         */
        public static final short gesture_subgeom_add = 239;

        /**
         * Delete sub-geometry
         */
        public static final short gesture_subgeom_delete = 240;

        /**
         * Move sub-geometry
         */
        public static final short gesture_subgeom_move = 241;

        /**
         * Validate sub-geometry
         */
        public static final short gesture_subgeom_validate = 242;

        /**
         * Validate
         */
        public static final short gesture_validate = 243;

        /**
         * Zoom +/-
         */
        public static final short gesture_zoom = 244;

        /**
         * Graphics
         */
        public static final short graphic = 245;

        /**
         * Graphic color
         */
        public static final short graphic_color = 246;

        /**
         * Graphic fill
         */
        public static final short graphic_fill = 247;

        /**
         * Graphic stroke
         */
        public static final short graphic_stroke = 248;

        /**
         * Gray channel
         */
        public static final short gray = 249;

        /**
         * Green channel
         */
        public static final short green = 250;

        /**
         * Grid
         */
        public static final short grid = 251;

        /**
         * Groovy editor
         */
        public static final short groovyEditror = 252;

        /**
         * Groovy script
         */
        public static final short groovyScriptLbl = 253;

        /**
         * Advanced
         */
        public static final short guiToogleAdvancedView = 254;

        /**
         * Basic
         */
        public static final short guiToogleBasicView = 255;

        /**
         * Intermediate
         */
        public static final short guiToogleInterView = 256;

        /**
         * Halo
         */
        public static final short halo = 257;

        /**
         * Hide help
         */
        public static final short hideHelp = 258;

        /**
         * Identifier
         */
        public static final short identifier = 259;

        /**
         * Image
         */
        public static final short image = 260;

        /**
         * Import XML
         */
        public static final short importXML = 261;

        /**
         * Import/Export
         */
        public static final short import_export = 262;

        /**
         * Initial gap
         */
        public static final short initial_gap = 263;

        /**
         * Not supported input
         */
        public static final short inputNotSupported = 264;

        /**
         * Inputs
         */
        public static final short inputs = 265;

        /**
         * Interpolation
         */
        public static final short interpolation = 266;

        /**
         * Bicubic
         */
        public static final short interpolation_bicubic = 267;

        /**
         * Linear
         */
        public static final short interpolation_linear = 268;

        /**
         * None
         */
        public static final short interpolation_none = 269;

        /**
         * Invert palette
         */
        public static final short invert = 270;

        /**
         * Invert colors
         */
        public static final short invert_palette = 271;

        /**
         * Color map
         */
        public static final short isolineEditor_colormap = 272;

        /**
         * Isolines
         */
        public static final short isolineEditor_displayName = 273;

        /**
         * Show isolines only
         */
        public static final short isolineEditor_isolineOnly = 274;

        /**
         * Line
         */
        public static final short isolineEditor_line = 275;

        /**
         * Show isoline value
         */
        public static final short isolineEditor_showLabel = 276;

        /**
         * Text
         */
        public static final short isolineEditor_text = 277;

        /**
         * Dynamic method, no value/color association.
         */
        public static final short jenks_notable = 278;

        /**
         * Job
         */
        public static final short job = 279;

        /**
         * Job editor
         */
        public static final short jobEditor = 280;

        /**
         * The job has been launch on the server.
         */
        public static final short jobExecuting = 281;

        /**
         * Input parameters
         */
        public static final short jobInputs = 282;

        /**
         * Output parameters
         */
        public static final short jobOutputs = 283;

        /**
         * Jobs table
         */
        public static final short jobTable = 284;

        /**
         * Label
         */
        public static final short label = 285;

        /**
         * Layers
         */
        public static final short layers = 286;

        /**
         * Legend
         */
        public static final short legend = 287;

        /**
         * Lenght
         */
        public static final short lenght = 288;

        /**
         * Font and Style
         */
        public static final short libPoliceStyle = 289;

        /**
         * Line
         */
        public static final short line = 290;

        /**
         * Line shape and color
         */
        public static final short lineShapeAndColor = 291;

        /**
         * Line cap
         */
        public static final short linecap = 292;

        /**
         * Line join
         */
        public static final short linejoin = 293;

        /**
         * Line placement
         */
        public static final short lineplacement = 294;

        /**
         * Loading
         */
        public static final short loading = 295;

        /**
         * Mandatory
         */
        public static final short mandatory = 296;

        /**
         * Manual
         */
        public static final short manual = 297;

        /**
         * Enter a description for the manual intervention.
         */
        public static final short manualDesc = 298;

        /**
         * Manual intervention
         */
        public static final short manualInt = 299;

        /**
         * Map
         */
        public static final short map = 300;

        /**
         * Rendering parameters
         */
        public static final short map_config = 301;

        /**
         * Activate
         */
        public static final short map_control_activate = 302;

        /**
         * cannot calculate coordinate
         */
        public static final short map_control_coord_error = 303;

        /**
         * Coordinate :
         */
        public static final short map_control_mouse_coord = 304;

        /**
         * Unit
         */
        public static final short map_control_unit = 305;

        /**
         * Map Coordinate Reference System
         */
        public static final short map_crs = 306;

        /**
         * Rendering informations
         */
        public static final short map_debug = 307;

        /**
         * Edit mode
         */
        public static final short map_edit = 308;

        /**
         * Elevation slider
         */
        public static final short map_elevation_slider = 309;

        /**
         * Grab informations
         */
        public static final short map_information = 310;

        /**
         * Measure area
         */
        public static final short map_measure_area = 311;

        /**
         * Measure lenght
         */
        public static final short map_measure_lenght = 312;

        /**
         * Move center here
         */
        public static final short map_move_elevation_center = 313;

        /**
         * Move maximum limit here
         */
        public static final short map_move_elevation_maximum = 314;

        /**
         * Move minimum limit here
         */
        public static final short map_move_elevation_minimum = 315;

        /**
         * Move center here
         */
        public static final short map_move_temporal_center = 316;

        /**
         * Move left limit here
         */
        public static final short map_move_temporal_left = 317;

        /**
         * Move right limit here
         */
        public static final short map_move_temporal_right = 318;

        /**
         * Go to coordinate
         */
        public static final short map_nav_to = 319;

        /**
         * Painting
         */
        public static final short map_painting = 320;

        /**
         * Free move
         */
        public static final short map_pan = 321;

        /**
         * Repaint
         */
        public static final short map_refresh = 322;

        /**
         * Remove elevation range
         */
        public static final short map_remove_elevation = 323;

        /**
         * Remove maximum limit
         */
        public static final short map_remove_elevation_maximum = 324;

        /**
         * Remove minimum limit
         */
        public static final short map_remove_elevation_minimum = 325;

        /**
         * Remove time range
         */
        public static final short map_remove_temporal = 326;

        /**
         * Remove left limit
         */
        public static final short map_remove_temporal_left = 327;

        /**
         * Remove right limit
         */
        public static final short map_remove_temporal_right = 328;

        /**
         * Selection mode
         */
        public static final short map_select = 329;

        /**
         * Selection properties
         */
        public static final short map_select_config = 330;

        /**
         * Statefull rendering mode
         */
        public static final short map_statefull = 331;

        /**
         * Render by symbol order
         */
        public static final short map_style_order = 332;

        /**
         * Temporal slider
         */
        public static final short map_temporal_slider = 333;

        /**
         * Preserve X-Y ratio
         */
        public static final short map_xy_ratio = 334;

        /**
         * Zoom to extent
         */
        public static final short map_zoom_all = 335;

        /**
         * Zoom in
         */
        public static final short map_zoom_in = 336;

        /**
         * Next
         */
        public static final short map_zoom_next = 337;

        /**
         * Zoom out
         */
        public static final short map_zoom_out = 338;

        /**
         * Previous
         */
        public static final short map_zoom_previous = 339;

        /**
         * Zoom on layer
         */
        public static final short map_zoom_to_layer = 340;

        /**
         * Mark
         */
        public static final short mark = 341;

        /**
         * Maximum
         */
        public static final short maximum = 342;

        /**
         * Max scale
         */
        public static final short maxscale = 343;

        /**
         * Mean
         */
        public static final short mean = 344;

        /**
         * Median
         */
        public static final short median = 345;

        /**
         * Method
         */
        public static final short method = 346;

        /**
         * Histogram
         */
        public static final short method_histogram = 347;

        /**
         * None
         */
        public static final short method_none = 348;

        /**
         * Normalize
         */
        public static final short method_normalize = 349;

        /**
         * Mime type
         */
        public static final short mime = 350;

        /**
         * Mime type
         */
        public static final short mimeType = 351;

        /**
         * Minimum
         */
        public static final short minimum = 352;

        /**
         * Min scale
         */
        public static final short minscale = 353;

        /**
         * Model
         */
        public static final short model = 354;

        /**
         * Move
         */
        public static final short move = 355;

        /**
         * Move to position
         */
        public static final short movetoposition = 356;

        /**
         * Multithread
         */
        public static final short multithread = 357;

        /**
         * Name
         */
        public static final short name = 358;

        /**
         * NaN value
         */
        public static final short nanValue = 359;

        /**
         * New chain
         */
        public static final short newChain = 360;

        /**
         * New Job
         */
        public static final short newJob = 361;

        /**
         * New Groovy process
         */
        public static final short newProcess = 362;

        /**
         * New trigger
         */
        public static final short newTrigger = 363;

        /**
         * External
         */
        public static final short new_external = 364;

        /**
         * New
         */
        public static final short new_file = 365;

        /**
         * Mark
         */
        public static final short new_mark = 366;

        /**
         * No
         */
        public static final short no = 367;

        /**
         * No data values
         */
        public static final short noData = 368;

        /**
         * no name
         */
        public static final short noname = 369;

        /**
         * None
         */
        public static final short none = 370;

        /**
         * Normalize
         */
        public static final short normalize = 371;

        /**
         * Not supported
         */
        public static final short notSupported = 372;

        /**
         * not started
         */
        public static final short not_started = 373;

        /**
         * Offset
         */
        public static final short offset = 374;

        /**
         * Offset
         */
        public static final short offset2 = 375;

        /**
         * Ok
         */
        public static final short ok = 376;

        /**
         * Opacity
         */
        public static final short opacity = 377;

        /**
         * Open
         */
        public static final short open = 378;

        /**
         * Open
         */
        public static final short open_file = 379;

        /**
         * Operand
         */
        public static final short operand = 380;

        /**
         * Azimuth
         */
        public static final short org_geotoolkit_gui_swing_render3d_azimut = 381;

        /**
         * Layers
         */
        public static final short org_geotoolkit_gui_swing_render3d_layer = 382;

        /**
         * DEM
         */
        public static final short org_geotoolkit_gui_swing_render3d_mnt = 383;

        /**
         * Rotation
         */
        public static final short org_geotoolkit_gui_swing_render3d_rotation = 384;

        /**
         * Show shadows
         */
        public static final short org_geotoolkit_gui_swing_render3d_show = 385;

        /**
         * Others
         */
        public static final short other = 386;

        /**
         * Other rule
         */
        public static final short otherRule = 387;

        /**
         * Outline
         */
        public static final short outline = 388;

        /**
         * Outputs
         */
        public static final short outputs = 389;

        /**
         * Overlap
         */
        public static final short overlap = 390;

        /**
         * Palette
         */
        public static final short palette = 391;

        /**
         * Activate parameter
         */
        public static final short parameters_activateParam = 392;

        /**
         * Add a parameter group
         */
        public static final short parameters_addNewGroupParameter = 393;

        /**
         * Add new parameter
         */
        public static final short parameters_addNewSimpleParameter = 394;

        /**
         * Code
         */
        public static final short parameters_code = 395;

        /**
         * Collapse group
         */
        public static final short parameters_collapse = 396;

        /**
         * Default value
         */
        public static final short parameters_defaultValue = 397;

        /**
         * Description
         */
        public static final short parameters_description = 398;

        /**
         * Default value
         */
        public static final short parameters_editorHelpDefaultLabel = 399;

        /**
         * Description
         */
        public static final short parameters_editorHelpDescriptionLabel = 400;

        /**
         * mandatory
         */
        public static final short parameters_editorHelpMandatory = 401;

        /**
         * Mandatory
         */
        public static final short parameters_editorHelpMandatoryLabel = 402;

        /**
         * optional
         */
        public static final short parameters_editorHelpOptional = 403;

        /**
         * Data type
         */
        public static final short parameters_editorHelpTypeLabel = 404;

        /**
         * Unit
         */
        public static final short parameters_editorHelpUnitLabel = 405;

        /**
         * Valid values
         */
        public static final short parameters_editorHelpValidLabel = 406;

        /**
         * Error
         */
        public static final short parameters_editorHelpValidationErrorLabel = 407;

        /**
         * Parameter can't be null or empty :
         */
        public static final short parameters_errorNullEmptyParameterValue = 408;

        /**
         * Expend group
         */
        public static final short parameters_expend = 409;

        /**
         * Mandatory
         */
        public static final short parameters_mandatory = 410;

        /**
         * Max
         */
        public static final short parameters_max = 411;

        /**
         * Max. occurences
         */
        public static final short parameters_maxOccurs = 412;

        /**
         * Min
         */
        public static final short parameters_min = 413;

        /**
         * Min. occurences
         */
        public static final short parameters_minOccurs = 414;

        /**
         * Occurences :
         */
        public static final short parameters_occurences = 415;

        /**
         * Remove group value
         */
        public static final short parameters_removeGroup = 416;

        /**
         * Remove parameter
         */
        public static final short parameters_removeParameter = 417;

        /**
         * Type
         */
        public static final short parameters_type = 418;

        /**
         * Unactivate parameter
         */
        public static final short parameters_unactivateParam = 419;

        /**
         * Password
         */
        public static final short password = 420;

        /**
         * Paste
         */
        public static final short paste = 421;

        /**
         * paused
         */
        public static final short paused = 422;

        /**
         * Placement
         */
        public static final short placement = 423;

        /**
         * Bottom left
         */
        public static final short placement_bottomleft = 424;

        /**
         * Bottom
         */
        public static final short placement_bottommiddle = 425;

        /**
         * Bottom right
         */
        public static final short placement_bottomright = 426;

        /**
         * Left
         */
        public static final short placement_centerleft = 427;

        /**
         * Center
         */
        public static final short placement_centermiddle = 428;

        /**
         * Right
         */
        public static final short placement_centerright = 429;

        /**
         * Top left
         */
        public static final short placement_topleft = 430;

        /**
         * Top
         */
        public static final short placement_topmiddle = 431;

        /**
         * Top right
         */
        public static final short placement_topright = 432;

        /**
         * Plain color
         */
        public static final short plainColor = 433;

        /**
         * Planning table
         */
        public static final short planningTable = 434;

        /**
         * Point :
         */
        public static final short point = 435;

        /**
         * Point placement
         */
        public static final short pointplacement = 436;

        /**
         * Polygon
         */
        public static final short polygon = 437;

        /**
         * Position
         */
        public static final short position = 438;

        /**
         * Predefined shapes
         */
        public static final short predefinedShape = 439;

        /**
         * Priority
         */
        public static final short priority = 440;

        /**
         * Groovy process
         */
        public static final short processGroovy = 441;

        /**
         * Name
         */
        public static final short processIdLbl = 442;

        /**
         * Input parameters of process
         */
        public static final short processInputParameters = 443;

        /**
         * Editor
         */
        public static final short processTabEditor = 444;

        /**
         * Groovy process list
         */
        public static final short processTable = 445;

        /**
         * Processes
         */
        public static final short processes = 446;

        /**
         * processing
         */
        public static final short processing = 447;

        /**
         * Properties
         */
        public static final short properties = 448;

        /**
         * Property
         */
        public static final short property = 449;

        /**
         * Action
         */
        public static final short property_action = 450;

        /**
         * All
         */
        public static final short property_all = 451;

        /**
         * Apply
         */
        public static final short property_apply = 452;

        /**
         * Close
         */
        public static final short property_close = 453;

        /**
         * Geometric
         */
        public static final short property_cql_advance = 454;

        /**
         * Commun
         */
        public static final short property_cql_basic = 455;

        /**
         * Error in CQL query
         */
        public static final short property_cql_error = 456;

        /**
         * Fields
         */
        public static final short property_cql_field = 457;

        /**
         * CQL
         */
        public static final short property_cql_filter = 458;

        /**
         * Edit
         */
        public static final short property_edit = 459;

        /**
         * Editor
         */
        public static final short property_editor = 460;

        /**
         * Feature table
         */
        public static final short property_feature_table = 461;

        /**
         * Filter
         */
        public static final short property_filter = 462;

        /**
         * General
         */
        public static final short property_general_title = 463;

        /**
         * Description :
         */
        public static final short property_info_description = 464;

        /**
         * Keywords :
         */
        public static final short property_info_keyword = 465;

        /**
         * Publisher :
         */
        public static final short property_info_publisher = 466;

        /**
         * Schema :
         */
        public static final short property_info_schema = 467;

        /**
         * Source :
         */
        public static final short property_info_source = 468;

        /**
         * Info title :
         */
        public static final short property_info_title = 469;

        /**
         * Properties
         */
        public static final short property_properties = 470;

        /**
         * Revert
         */
        public static final short property_revert = 471;

        /**
         * Style
         */
        public static final short property_style = 472;

        /**
         * Advanced
         */
        public static final short property_style_advanced = 473;

        /**
         * Classification : interval
         */
        public static final short property_style_classification_interval = 474;

        /**
         * Classification : unique symbol
         */
        public static final short property_style_classification_unique = 475;

        /**
         * Color map
         */
        public static final short property_style_colormap = 476;

        /**
         * Jenks classification
         */
        public static final short property_style_jenks = 477;

        /**
         * Label
         */
        public static final short property_style_label = 478;

        /**
         * Enable Label
         */
        public static final short property_style_label_enable = 479;

        /**
         * Simple
         */
        public static final short property_style_simple = 480;

        /**
         * Style
         */
        public static final short property_style_style = 481;

        /**
         * Unknown Layer Style
         */
        public static final short property_style_unknown_simplestyle = 482;

        /**
         * Title :
         */
        public static final short property_title = 483;

        /**
         * Publish
         */
        public static final short publish = 484;

        /**
         * Publish chain
         */
        public static final short publishChain = 485;

        /**
         * Qantile
         */
        public static final short qantile = 486;

        /**
         * Quantity
         */
        public static final short quantity = 487;

        /**
         * Radius
         */
        public static final short radius = 488;

        /**
         * Raw data
         */
        public static final short rawData = 489;

        /**
         * Red channel
         */
        public static final short red = 490;

        /**
         * Refresh
         */
        public static final short refresh = 491;

        /**
         * Refresh table
         */
        public static final short refresh_table = 492;

        /**
         * Related to
         */
        public static final short relatedTo = 493;

        /**
         * Relief
         */
        public static final short relief = 494;

        /**
         * Remove hole
         */
        public static final short removeHole = 495;

        /**
         * Remove geometry piece
         */
        public static final short removePart = 496;

        /**
         * Remove all values
         */
        public static final short remove_all_values = 497;

        /**
         * Repeat interval (min)
         */
        public static final short repeatTime = 498;

        /**
         * Repeated
         */
        public static final short repeated = 499;

        /**
         * Responsible
         */
        public static final short responsible = 500;

        /**
         * RGB
         */
        public static final short rgb = 501;

        /**
         * RollBack
         */
        public static final short rollback = 502;

        /**
         * Rotation
         */
        public static final short rotation = 503;

        /**
         * Run
         */
        public static final short run = 504;

        /**
         * Area table
         */
        public static final short s52_areatable = 505;

        /**
         * Attributes filter
         */
        public static final short s52_attributefilter = 506;

        /**
         * Background
         */
        public static final short s52_background = 507;

        /**
         * Base
         */
        public static final short s52_base = 508;

        /**
         * Category
         */
        public static final short s52_category = 509;

        /**
         * Chart
         */
        public static final short s52_chart = 510;

        /**
         * Object class
         */
        public static final short s52_class = 511;

        /**
         * Code
         */
        public static final short s52_code = 512;

        /**
         * Contour labels
         */
        public static final short s52_contourlabel = 513;

        /**
         * Deep contour
         */
        public static final short s52_deepcontour = 514;

        /**
         * Description
         */
        public static final short s52_description = 515;

        /**
         * Details
         */
        public static final short s52_detail = 516;

        /**
         * Distance tag
         */
        public static final short s52_distancetag = 517;

        /**
         * Filter
         */
        public static final short s52_filter = 518;

        /**
         * Full sector
         */
        public static final short s52_fullsector = 519;

        /**
         * Global
         */
        public static final short s52_global = 520;

        /**
         * Show isolated danger (shallow water)
         */
        public static final short s52_isolateDanger = 521;

        /**
         * Light description
         */
        public static final short s52_lightdescription = 522;

        /**
         * Line table
         */
        public static final short s52_linetable = 523;

        /**
         * Low accuracy symbols
         */
        public static final short s52_lowaccsymbol = 524;

        /**
         * Mariner
         */
        public static final short s52_mariner = 525;

        /**
         * No text
         */
        public static final short s52_notext = 526;

        /**
         * Other
         */
        public static final short s52_other = 527;

        /**
         * Palette
         */
        public static final short s52_palette = 528;

        /**
         * Palettes
         */
        public static final short s52_palettes = 529;

        /**
         * Point table
         */
        public static final short s52_pointtable = 530;

        /**
         * Preview
         */
        public static final short s52_preview = 531;

        /**
         * Priority
         */
        public static final short s52_priority = 532;

        /**
         * Radar
         */
        public static final short s52_radar = 533;

        /**
         * null
         */
        public static final short s52_radarnull = 534;

        /**
         * Over
         */
        public static final short s52_radarover = 535;

        /**
         * Under
         */
        public static final short s52_radarunder = 536;

        /**
         * Rules
         */
        public static final short s52_rules = 537;

        /**
         * Safety contour
         */
        public static final short s52_safetycontour = 538;

        /**
         * Safety depth (meters)
         */
        public static final short s52_safetydepth = 539;

        /**
         * Scale filter (SCAMIN/SCAMAX)
         */
        public static final short s52_scalefilter = 540;

        /**
         * Shallow contour
         */
        public static final short s52_shallowcontour = 541;

        /**
         * Shallow pattern
         */
        public static final short s52_shallowpattern = 542;

        /**
         * Ship outline
         */
        public static final short s52_shipoutline = 543;

        /**
         * Standard
         */
        public static final short s52_standard = 544;

        /**
         * Symbol instructions
         */
        public static final short s52_symbolinstructions = 545;

        /**
         * Symbols
         */
        public static final short s52_symbols = 546;

        /**
         * Time tag
         */
        public static final short s52_timetag = 547;

        /**
         * Title
         */
        public static final short s52_title = 548;

        /**
         * Two shades
         */
        public static final short s52_twoshades = 549;

        /**
         * Viewing group
         */
        public static final short s52_viewinggroup = 550;

        /**
         * Visible
         */
        public static final short s52_visible = 551;

        /**
         * Save
         */
        public static final short save = 552;

        /**
         * Save as Draft
         */
        public static final short saveDraft = 553;

        /**
         * Save
         */
        public static final short save_file = 554;

        /**
         * Save as ...
         */
        public static final short saveas_file = 555;

        /**
         * Scale
         */
        public static final short scale = 556;

        /**
         * Scale :
         */
        public static final short scale_title = 557;

        /**
         * Edition area
         */
        public static final short select_area = 558;

        /**
         * Geographic
         */
        public static final short select_geographic = 559;

        /**
         * Intersect
         */
        public static final short select_intersect = 560;

        /**
         * Free form
         */
        public static final short select_lasso = 561;

        /**
         * Rectangle
         */
        public static final short select_square = 562;

        /**
         * Visual
         */
        public static final short select_visual = 563;

        /**
         * Within
         */
        public static final short select_within = 564;

        /**
         * Selected channels
         */
        public static final short selecteds = 565;

        /**
         * Any
         */
        public static final short semantic_any = 566;

        /**
         * Lines
         */
        public static final short semantic_line = 567;

        /**
         * Points
         */
        public static final short semantic_point = 568;

        /**
         * Polygons
         */
        public static final short semantic_polygon = 569;

        /**
         * Rasters
         */
        public static final short semantic_raster = 570;

        /**
         * Texts
         */
        public static final short semantic_text = 571;

        /**
         * server URL
         */
        public static final short serverURL = 572;

        /**
         * Commit
         */
        public static final short sessionCommit = 573;

        /**
         * Rollback
         */
        public static final short sessionRollback = 574;

        /**
         * Shape border
         */
        public static final short shapeBorder = 575;

        /**
         * Shape fill
         */
        public static final short shapeFill = 576;

        /**
         * Exp
         */
        public static final short shortexpression = 577;

        /**
         * Display help
         */
        public static final short showHelp = 578;

        /**
         * Show identifying properties
         */
        public static final short show_id = 579;

        /**
         * Shapefile creation
         */
        public static final short shp_Shapefile_creation = 580;

        /**
         * Add
         */
        public static final short shp_add = 581;

        /**
         * Attributs
         */
        public static final short shp_attributs = 582;

        /**
         * Create
         */
        public static final short shp_create = 583;

        /**
         * CRS
         */
        public static final short shp_crs = 584;

        /**
         * default
         */
        public static final short shp_default = 585;

        /**
         * Delete
         */
        public static final short shp_delete = 586;

        /**
         * Down
         */
        public static final short shp_down = 587;

        /**
         * File :
         */
        public static final short shp_file = 588;

        /**
         * Geometry
         */
        public static final short shp_geometry = 589;

        /**
         * List
         */
        public static final short shp_list = 590;

        /**
         * MultiLine
         */
        public static final short shp_multiline = 591;

        /**
         * MultiPoint
         */
        public static final short shp_multipoint = 592;

        /**
         * MultiPolygon
         */
        public static final short shp_multipolygon = 593;

        /**
         * Name
         */
        public static final short shp_name = 594;

        /**
         * Point
         */
        public static final short shp_point = 595;

        /**
         * ...
         */
        public static final short shp_ppp = 596;

        /**
         * Shapefile creation
         */
        public static final short shp_shapefile_creation = 597;

        /**
         * Type
         */
        public static final short shp_type = 598;

        /**
         * Up
         */
        public static final short shp_up = 599;

        /**
         * Simplification
         */
        public static final short simplification = 600;

        /**
         * Single
         */
        public static final short single = 601;

        /**
         * Size
         */
        public static final short size = 602;

        /**
         * SLD
         */
        public static final short sld = 603;

        /**
         * Editor
         */
        public static final short sldeditor = 604;

        /**
         * Spatial
         */
        public static final short spatial = 605;

        /**
         * Standard
         */
        public static final short standard = 606;

        /**
         * Start edition
         */
        public static final short start = 607;

        /**
         * Start time
         */
        public static final short startTime = 608;

        /**
         * started
         */
        public static final short started = 609;

        /**
         * Status table
         */
        public static final short statusTable = 610;

        /**
         * Stop
         */
        public static final short stop = 611;

        /**
         * Stroke
         */
        public static final short stroke = 612;

        /**
         * Stroke Graphic
         */
        public static final short strokegraphic = 613;

        /**
         * Style
         */
        public static final short style = 614;

        /**
         * Style bank
         */
        public static final short styleBank = 615;

        /**
         * X
         */
        public static final short style_anchorpoint_x = 616;

        /**
         * Y
         */
        public static final short style_anchorpoint_y = 617;

        /**
         * Cell size
         */
        public static final short style_cellsymbolizer_cellsize = 618;

        /**
         * Edit
         */
        public static final short style_cellsymbolizer_edit = 619;

        /**
         * Filter
         */
        public static final short style_cellsymbolizer_filter = 620;

        /**
         * Cell patches
         */
        public static final short style_cellsymbolizer_tooltip = 621;

        /**
         * Type
         */
        public static final short style_cellsymbolizer_type = 622;

        /**
         * Blue channel
         */
        public static final short style_channelSelection_bluechannel = 623;

        /**
         * Override grayscale
         */
        public static final short style_channelSelection_gray = 624;

        /**
         * Gray channel
         */
        public static final short style_channelSelection_graychannel = 625;

        /**
         * Green channel
         */
        public static final short style_channelSelection_greenchannel = 626;

        /**
         * Red channel
         */
        public static final short style_channelSelection_redchannel = 627;

        /**
         * Override RGB
         */
        public static final short style_channelSelection_rgb = 628;

        /**
         * Native colors
         */
        public static final short style_channelselection_native = 629;

        /**
         * Band
         */
        public static final short style_rastercolormappane_band = 630;

        /**
         * Fit to data
         */
        public static final short style_rastercolormappane_fittodata = 631;

        /**
         * Interpolate
         */
        public static final short style_rastercolormappane_interpolate = 632;

        /**
         * Invert
         */
        public static final short style_rastercolormappane_invert = 633;

        /**
         * NaN
         */
        public static final short style_rastercolormappane_nan = 634;

        /**
         * Palette
         */
        public static final short style_rastercolormappane_palette = 635;

        /**
         * Channels
         */
        public static final short style_rastersymbolizer_channels = 636;

        /**
         * ColorMap
         */
        public static final short style_rastersymbolizer_cm_colormap = 637;

        /**
         * Grayscale/RGB
         */
        public static final short style_rastersymbolizer_cm_rgb = 638;

        /**
         * Color
         */
        public static final short style_rastersymbolizer_color = 639;

        /**
         * Color model
         */
        public static final short style_rastersymbolizer_colormodel = 640;

        /**
         * Divisions
         */
        public static final short style_rastersymbolizer_divisions = 641;

        /**
         * Edit
         */
        public static final short style_rastersymbolizer_edit = 642;

        /**
         * General
         */
        public static final short style_rastersymbolizer_general = 643;

        /**
         * Line
         */
        public static final short style_rastersymbolizer_line = 644;

        /**
         * Lower
         */
        public static final short style_rastersymbolizer_lower = 645;

        /**
         * None
         */
        public static final short style_rastersymbolizer_none = 646;

        /**
         * Opacity
         */
        public static final short style_rastersymbolizer_opacity = 647;

        /**
         * Outline
         */
        public static final short style_rastersymbolizer_outline = 648;

        /**
         * Overlap
         */
        public static final short style_rastersymbolizer_overlap = 649;

        /**
         * Polygon
         */
        public static final short style_rastersymbolizer_polygon = 650;

        /**
         * Upper
         */
        public static final short style_rastersymbolizer_upper = 651;

        /**
         * Value
         */
        public static final short style_rastersymbolizer_value = 652;

        /**
         * Advanced
         */
        public static final short style_twostate_advanced = 653;

        /**
         * Simple
         */
        public static final short style_twostate_simple = 654;

        /**
         * Sum
         */
        public static final short sum = 655;

        /**
         * Symbol
         */
        public static final short symbol = 656;

        /**
         * Symbols layer
         */
        public static final short symbolLayer = 657;

        /**
         * Symbol preview
         */
        public static final short symbolPreview = 658;

        /**
         * Line symbol
         */
        public static final short symbol_line = 659;

        /**
         * Point symbol
         */
        public static final short symbol_point = 660;

        /**
         * Polygon symbol
         */
        public static final short symbol_polygon = 661;

        /**
         * System clipboard
         */
        public static final short systemclipboard = 662;

        /**
         * Graph
         */
        public static final short tabGraphTitle = 663;

        /**
         * General informations
         */
        public static final short tabInfoTitle = 664;

        /**
         * Metadata
         */
        public static final short tabMetadataTitle = 665;

        /**
         * Speed  T x
         */
        public static final short temp_factor = 666;

        /**
         * Refresh rate (ms) =
         */
        public static final short temp_refresh = 667;

        /**
         * Temporal
         */
        public static final short temporal = 668;

        /**
         * Temporal attributs
         */
        public static final short temporal_configuration = 669;

        /**
         * Ending attribut
         */
        public static final short temporal_end = 670;

        /**
         * Starting attribut
         */
        public static final short temporal_start = 671;

        /**
         * Text color
         */
        public static final short textColor = 672;

        /**
         * Time trigger
         */
        public static final short timeTrigger = 673;

        /**
         * Title
         */
        public static final short title = 674;

        /**
         * Gap
         */
        public static final short tooltip_gap = 675;

        /**
         * Lenght
         */
        public static final short tooltip_lenght = 676;

        /**
         * Offset
         */
        public static final short tooltip_offset = 677;

        /**
         * Topologic
         */
        public static final short topologic = 678;

        /**
         * Type
         */
        public static final short type = 679;

        /**
         * undefined
         */
        public static final short undefined = 680;

        /**
         * Units
         */
        public static final short unit = 681;

        /**
         * more...
         */
        public static final short uom_more = 682;

        /**
         * Up
         */
        public static final short up = 683;

        /**
         * Update date
         */
        public static final short updateDate = 684;

        /**
         * URL
         */
        public static final short url = 685;

        /**
         * User
         */
        public static final short user = 686;

        /**
         * Validate
         */
        public static final short validate = 687;

        /**
         * Value
         */
        public static final short value = 688;

        /**
         * Version
         */
        public static final short version = 689;

        /**
         * waiting
         */
        public static final short waiting = 690;

        /**
         * Weight
         */
        public static final short weight = 691;

        /**
         * Well Knowed Form
         */
        public static final short wellknowned = 692;

        /**
         * Well Knowed Form
         */
        public static final short wellknownedform = 693;

        /**
         * Well known name
         */
        public static final short wellknownname = 694;

        /**
         * Width
         */
        public static final short width = 695;

        /**
         * Well-Known Text
         */
        public static final short wkt = 696;

        /**
         * WPS Chain
         */
        public static final short wpsChain = 697;

        /**
         * X
         */
        public static final short x = 698;

        /**
         * Graphic
         */
        public static final short xmlGraphic = 699;

        /**
         * XML
         */
        public static final short xmlview = 700;

        /**
         * Y
         */
        public static final short y = 701;

        /**
         * Yes
         */
        public static final short yes = 702;
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
