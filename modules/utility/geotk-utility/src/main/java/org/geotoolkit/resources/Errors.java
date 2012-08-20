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
 * Locale-dependent resources for error messages.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.05
 *
 * @since 2.2
 * @module
 */
public final class Errors extends IndexedResourceBundle {
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
         * Ambiguous axis length.
         */
        public static final int AMBIGIOUS_AXIS_LENGTH = 0;

        /**
         * Value “{0}” is ambiguous since it associated to the following possibilities:
         */
        public static final int AMBIGUOUS_VALUE_$1 = 1;

        /**
         * Angle {0} is too high.
         */
        public static final int ANGLE_OVERFLOW_$1 = 2;

        /**
         * Azimuth {0} is out of range (±180°).
         */
        public static final int AZIMUTH_OUT_OF_RANGE_$1 = 4;

        /**
         * Bursa-Wolf parameters required.
         */
        public static final int BURSA_WOLF_PARAMETERS_REQUIRED = 19;

        /**
         * The operation has been canceled.
         */
        public static final int CANCELED_OPERATION = 20;

        /**
         * Can’t compute derivative.
         */
        public static final int CANT_COMPUTE_DERIVATIVE = 21;

        /**
         * Can’t concatenate transforms “{0}” and “{1}”.
         */
        public static final int CANT_CONCATENATE_TRANSFORMS_$2 = 22;

        /**
         * Failed to connect to the {0} database.
         */
        public static final int CANT_CONNECT_DATABASE_$1 = 23;

        /**
         * Can’t convert value from type ‘{0}’.
         */
        public static final int CANT_CONVERT_FROM_TYPE_$1 = 24;

        /**
         * Can’t convert from type ‘{0}’ to type ‘{1}’.
         */
        public static final int CANT_CONVERT_FROM_TYPE_$2 = 257;

        /**
         * Can’t create directory “{0}”.
         */
        public static final int CANT_CREATE_DIRECTORY_$1 = 25;

        /**
         * Can’t create a factory of type ‘{0}’.
         */
        public static final int CANT_CREATE_FACTORY_FOR_TYPE_$1 = 26;

        /**
         * Can’t create object of type ‘{0}’ from a text.
         */
        public static final int CANT_CREATE_OBJECT_FROM_TEXT_$1 = 27;

        /**
         * Can’t delete file “{0}”.
         */
        public static final int CANT_DELETE_FILE_$1 = 269;

        /**
         * Can’t evaluate a value for coordinate ({0}).
         */
        public static final int CANT_EVALUATE_FOR_COORDINATE_$1 = 28;

        /**
         * Failed to get the data source for name “{0}”.
         */
        public static final int CANT_GET_DATASOURCE_$1 = 29;

        /**
         * Can not process the “{0}={1}” property.
         */
        public static final int CANT_PROCESS_PROPERTY_$2 = 30;

        /**
         * Can’t read the “{1}” record in the “{0}” table.
         */
        public static final int CANT_READ_DATABASE_RECORD_$2 = 32;

        /**
         * Can’t read the “{1}” column for the “{2}” record in the “{0}” table.
         */
        public static final int CANT_READ_DATABASE_RECORD_$3 = 33;

        /**
         * Can’t read the “{0}” table.
         */
        public static final int CANT_READ_DATABASE_TABLE_$1 = 34;

        /**
         * Can’t read the “{1}” column in the “{0}” table.
         */
        public static final int CANT_READ_DATABASE_TABLE_$2 = 35;

        /**
         * Can’t read file “{0}”.
         */
        public static final int CANT_READ_FILE_$1 = 31;

        /**
         * Can’t reduce “{0}” to a two-dimensional coordinate system.
         */
        public static final int CANT_REDUCE_TO_TWO_DIMENSIONS_$1 = 36;

        /**
         * Can’t reproject grid coverage “{0}”.
         */
        public static final int CANT_REPROJECT_COVERAGE_$1 = 37;

        /**
         * Can’t separate CRS “{0}”.
         */
        public static final int CANT_SEPARATE_CRS_$1 = 38;

        /**
         * Can’t set a value for attribute “{0}”.
         */
        public static final int CANT_SET_ATTRIBUTE_VALUE_$1 = 190;

        /**
         * Can’t set a value for parameter “{0}”.
         */
        public static final int CANT_SET_PARAMETER_VALUE_$1 = 39;

        /**
         * Can’t transform envelope.
         */
        public static final int CANT_TRANSFORM_ENVELOPE = 40;

        /**
         * Can’t transform some points that should be valid.
         */
        public static final int CANT_TRANSFORM_VALID_POINTS = 41;

        /**
         * Can’t write file “{0}”.
         */
        public static final int CANT_WRITE_FILE_$1 = 42;

        /**
         * Graphic “{0}” is owned by an other canvas.
         */
        public static final int CANVAS_NOT_OWNER_$1 = 43;

        /**
         * Axis {0} and {1} are colinear.
         */
        public static final int COLINEAR_AXIS_$2 = 44;

        /**
         * Coverage returned by ‘{0}’ is already the view of coverage “{1}”.
         */
        public static final int COVERAGE_ALREADY_BOUND_$2 = 45;

        /**
         * Database failure while creating a ‘{0}’ object for code “{1}”.
         */
        public static final int DATABASE_FAILURE_$2 = 46;

        /**
         * Failed to update the database.
         */
        public static final int DATABASE_UPDATE_FAILURE = 47;

        /**
         * Date {0} is outside the range of available data.
         */
        public static final int DATE_OUTSIDE_COVERAGE_$1 = 48;

        /**
         * The destination has not been set.
         */
        public static final int DESTINATION_NOT_SET = 49;

        /**
         * The direction has not been set.
         */
        public static final int DIRECTION_NOT_SET = 50;

        /**
         * The factory has been disposed.
         */
        public static final int DISPOSED_FACTORY = 51;

        /**
         * The distance {0} is out of range ({1} to {2} {3})
         */
        public static final int DISTANCE_OUT_OF_RANGE_$4 = 52;

        /**
         * Dropped the “{0}” foreigner key constraint.
         */
        public static final int DROPPED_FOREIGNER_KEY_$1 = 267;

        /**
         * Name or alias for parameter “{0}” at index {1} conflict with name “{2}” at index {3}.
         */
        public static final int DUPLICATED_PARAMETER_NAME_$4 = 182;

        /**
         * The “{0}” record is defined more than once.
         */
        public static final int DUPLICATED_RECORD_$1 = 53;

        /**
         * Found {0} duplicated values.
         */
        public static final int DUPLICATED_VALUES_COUNT_$1 = 54;

        /**
         * Duplicated values for key “{0}”.
         */
        public static final int DUPLICATED_VALUES_FOR_KEY_$1 = 55;

        /**
         * The “{0}” value is specified more than once.
         */
        public static final int DUPLICATED_VALUE_$1 = 56;

        /**
         * Elliptical projection not supported.
         */
        public static final int ELLIPTICAL_NOT_SUPPORTED = 57;

        /**
         * The array must contains at least one element.
         */
        public static final int EMPTY_ARRAY = 58;

        /**
         * The dictionary must contains at least one entry.
         */
        public static final int EMPTY_DICTIONARY = 59;

        /**
         * Envelope must be at least two-dimensional and non-empty.
         */
        public static final int EMPTY_ENVELOPE_2D = 60;

        /**
         * Empty or invalid rectangle: {0}
         */
        public static final int EMPTY_RECTANGLE_$1 = 15;

        /**
         * Premature end of data file.
         */
        public static final int END_OF_DATA_FILE = 61;

        /**
         * Expected one of {0}.
         */
        public static final int EXPECTED_ONE_OF_$1 = 62;

        /**
         * No factory of kind “{0}” found.
         */
        public static final int FACTORY_NOT_FOUND_$1 = 63;

        /**
         * File “{0}” already exists.
         */
        public static final int FILE_ALREADY_EXISTS_$1 = 268;

        /**
         * File “{0}” does not exist or is unreadable.
         */
        public static final int FILE_DOES_NOT_EXIST_$1 = 64;

        /**
         * File has too few data.
         */
        public static final int FILE_HAS_TOO_FEW_DATA = 65;

        /**
         * File has too many data.
         */
        public static final int FILE_HAS_TOO_MANY_DATA = 66;

        /**
         * Attribute “{0}” is not allowed for an object of type ‘{1}’.
         */
        public static final int FORBIDDEN_ATTRIBUTE_$2 = 259;

        /**
         * Geotoolkit.org extension required for “{0}” operation.
         */
        public static final int GEOTOOLKIT_EXTENSION_REQUIRED_$1 = 67;

        /**
         * Latitude and Longitude grid locations are not equal
         */
        public static final int GRID_LOCATIONS_UNEQUAL = 68;

        /**
         * Hole is not inside polygon.
         */
        public static final int HOLE_NOT_INSIDE_POLYGON = 70;

        /**
         * Illegal angle pattern: {0}
         */
        public static final int ILLEGAL_ANGLE_PATTERN_$1 = 71;

        /**
         * Illegal value for argument ‘{0}’.
         */
        public static final int ILLEGAL_ARGUMENT_$1 = 72;

        /**
         * Illegal argument: ‘{0}’={1}
         */
        public static final int ILLEGAL_ARGUMENT_$2 = 73;

        /**
         * Argument ‘{0}’ can not be an instance of ‘{1}’. Expected an instance of ‘{2}’ or derived
         * type.
         */
        public static final int ILLEGAL_ARGUMENT_CLASS_$3 = 256;

        /**
         * Illegal array length for {0} dimensional points.
         */
        public static final int ILLEGAL_ARRAY_LENGTH_FOR_DIMENSION_$1 = 74;

        /**
         * Axis can’t be oriented toward {0} for coordinate system of class ‘{1}’.
         */
        public static final int ILLEGAL_AXIS_ORIENTATION_$2 = 75;

        /**
         * Band number {0} is not valid.
         */
        public static final int ILLEGAL_BAND_NUMBER_$1 = 5;

        /**
         * Class ‘{0}’ is illegal. It must be ‘{1}’ or a derived class.
         */
        public static final int ILLEGAL_CLASS_$2 = 76;

        /**
         * ‘{0}’ can not be an instance of ‘{1}’. Expected an instance of ‘{2}’ or derived type.
         */
        public static final int ILLEGAL_CLASS_$3 = 265;

        /**
         * Illegal coordinate: {0}
         */
        public static final int ILLEGAL_COORDINATE_$1 = 7;

        /**
         * Illegal coordinate reference system.
         */
        public static final int ILLEGAL_COORDINATE_REFERENCE_SYSTEM = 77;

        /**
         * Coordinate system of type ‘{0}’ are incompatible with CRS of type ‘{1}’.
         */
        public static final int ILLEGAL_COORDINATE_SYSTEM_FOR_CRS_$2 = 78;

        /**
         * Coordinate system can’t have {0} dimensions.
         */
        public static final int ILLEGAL_CS_DIMENSION_$1 = 79;

        /**
         * Illegal descriptor for parameter “{0}”.
         */
        public static final int ILLEGAL_DESCRIPTOR_FOR_PARAMETER_$1 = 80;

        /**
         * Bad entry.
         */
        public static final int ILLEGAL_ENTRY = 8;

        /**
         * Illegal grid envelope [{1} … {2}] for dimension {0}.
         */
        public static final int ILLEGAL_GRID_ENVELOPE_$3 = 9;

        /**
         * “{0}” is not a valid identifier.
         */
        public static final int ILLEGAL_IDENTIFIER_$1 = 82;

        /**
         * Illegal instruction “{0}”.
         */
        public static final int ILLEGAL_INSTRUCTION_$1 = 83;

        /**
         * Illegal key: {0}
         */
        public static final int ILLEGAL_KEY_$1 = 84;

        /**
         * Local “{0}” is not recognized.
         */
        public static final int ILLEGAL_LANGUAGE_CODE_$1 = 11;

        /**
         * Illegal data at line {1} in file “{0}”.
         */
        public static final int ILLEGAL_LINE_IN_FILE_$2 = 10;

        /**
         * Illegal matrix size.
         */
        public static final int ILLEGAL_MATRIX_SIZE = 85;

        /**
         * Expected a source with a single image, or tiles having the same resolution.
         */
        public static final int ILLEGAL_MOSAIC_INPUT = 100;

        /**
         * Parameter “{0}” occurs {1} time, while the expected range of occurrences was [{2} … {3}].
         */
        public static final int ILLEGAL_OCCURS_FOR_PARAMETER_$4 = 86;

        /**
         * This operation can’t be applied to values of class ‘{0}’.
         */
        public static final int ILLEGAL_OPERATION_FOR_VALUE_CLASS_$1 = 87;

        /**
         * Bad ordinates at dimension {0}.
         */
        public static final int ILLEGAL_ORDINATE_AT_$1 = 81;

        /**
         * Parameter ‘{0}’ can’t be of type ‘{1}’.
         */
        public static final int ILLEGAL_PARAMETER_TYPE_$2 = 13;

        /**
         * Parameter ‘{0}’ can’t have value “{1}”.
         */
        public static final int ILLEGAL_PARAMETER_VALUE_$2 = 12;

        /**
         * Values for the ‘{0}’ property can not be of kind ‘{1}’.
         */
        public static final int ILLEGAL_PROPERTY_TYPE_$2 = 254;

        /**
         * Range [{0} … {1}] is not valid.
         */
        public static final int ILLEGAL_RANGE_$2 = 14;

        /**
         * Illegal transform of type ‘{0}’.
         */
        public static final int ILLEGAL_TRANSFORM_FOR_TYPE_$1 = 16;

        /**
         * Unit “{1}” can’t be raised to power {0}.
         */
        public static final int ILLEGAL_UNIT_POWER_$2 = 18;

        /**
         * Multiplication or division of “{0}” by “{1}” not allowed.
         */
        public static final int ILLEGAL_UNIT_PRODUCT_$2 = 17;

        /**
         * The “{0}” object is too complex for WKT syntax.
         */
        public static final int ILLEGAL_WKT_FORMAT_$1 = 101;

        /**
         * Incompatible coordinate system type.
         */
        public static final int INCOMPATIBLE_COORDINATE_SYSTEM_TYPE = 89;

        /**
         * Projection parameter “{0}” is incompatible with ellipsoid “{1}”.
         */
        public static final int INCOMPATIBLE_ELLIPSOID_$2 = 90;

        /**
         * Incompatible grid geometries.
         */
        public static final int INCOMPATIBLE_GRID_GEOMETRY = 91;

        /**
         * Incompatible unit: {0}
         */
        public static final int INCOMPATIBLE_UNIT_$1 = 92;

        /**
         * Direction “{1}” is inconsistent with axis “{0}”.
         */
        public static final int INCONSISTENT_AXIS_ORIENTATION_$2 = 93;

        /**
         * Inconsistent domain between “{0}” and “{1}”.
         */
        public static final int INCONSISTENT_DOMAIN_$2 = 270;

        /**
         * Property “{0}” has a value inconsistent with other properties.
         */
        public static final int INCONSISTENT_PROPERTY_$1 = 94;

        /**
         * Inconsistent value.
         */
        public static final int INCONSISTENT_VALUE = 95;

        /**
         * Index {0} is out of bounds.
         */
        public static final int INDEX_OUT_OF_BOUNDS_$1 = 96;

        /**
         * Coefficient {0}={1} can’t be NaN or infinity.
         */
        public static final int INFINITE_COEFFICIENT_$2 = 6;

        /**
         * {0} value is infinite.
         */
        public static final int INFINITE_VALUE_$1 = 97;

        /**
         * Inseparable transform.
         */
        public static final int INSEPARABLE_TRANSFORM = 98;

        /**
         * {0} points were specified, while {1} are required.
         */
        public static final int INSUFFICIENT_POINTS_$2 = 99;

        /**
         * Error in “{0}”:
         */
        public static final int IN_$1 = 102;

        /**
         * Latitudes {0} and {1} are opposite.
         */
        public static final int LATITUDES_ARE_OPPOSITE_$2 = 3;

        /**
         * Latitude {0} is out of range (±90°).
         */
        public static final int LATITUDE_OUT_OF_RANGE_$1 = 103;

        /**
         * The line contains {0} columns while only {1} was expected. Characters “{2}” seem to be
         * extra.
         */
        public static final int LINE_TOO_LONG_$3 = 104;

        /**
         * The line contains only {0} columns while {1} was expected.
         */
        public static final int LINE_TOO_SHORT_$2 = 105;

        /**
         * Longitude {0} is out of range (±180°).
         */
        public static final int LONGITUDE_OUT_OF_RANGE_$1 = 106;

        /**
         * Malformed envelope
         */
        public static final int MALFORMED_ENVELOPE = 107;

        /**
         * Attribute “{0}” is mandatory for an object of type ‘{1}’.
         */
        public static final int MANDATORY_ATTRIBUTE_$2 = 258;

        /**
         * All rows doesn’t have the same length.
         */
        public static final int MATRIX_NOT_REGULAR = 108;

        /**
         * Mismatched array length.
         */
        public static final int MISMATCHED_ARRAY_LENGTH = 109;

        /**
         * Array length of parameters ‘{0}’ and ‘{1}’ do not match.
         */
        public static final int MISMATCHED_ARRAY_LENGTH_$2 = 110;

        /**
         * The coordinate reference system must be the same for all objects.
         */
        public static final int MISMATCHED_COORDINATE_REFERENCE_SYSTEM = 111;

        /**
         * Mismatched object dimension: {0}D and {1}D.
         */
        public static final int MISMATCHED_DIMENSION_$2 = 112;

        /**
         * Argument ‘{0}’ has {1} dimensions, while {2} was expected.
         */
        public static final int MISMATCHED_DIMENSION_$3 = 113;

        /**
         * The envelope uses an incompatible CRS: was “{1}” while we expected “{0}”.
         */
        public static final int MISMATCHED_ENVELOPE_CRS_$2 = 114;

        /**
         * The dimension of the “{0}” image is ({1}×{2}) pixels, while the expected dimension was
         * ({3}×{4}) pixels.
         */
        public static final int MISMATCHED_IMAGE_SIZE_$5 = 88;

        /**
         * The number of image bands ({0}) differs from the number of supplied ‘{2}’ objects ({1}).
         */
        public static final int MISMATCHED_NUMBER_OF_BANDS_$3 = 177;

        /**
         * Character ‘{0}’ was expected.
         */
        public static final int MISSING_CHARACTER_$1 = 116;

        /**
         * This operation requires the “{0}” module.
         */
        public static final int MISSING_MODULE_$1 = 118;

        /**
         * Geophysics categories mixed with non-geophysics ones.
         */
        public static final int MIXED_CATEGORIES = 122;

        /**
         * Column number for “{0}” ({1}) can’t be negative.
         */
        public static final int NEGATIVE_COLUMN_$2 = 123;

        /**
         * Node “{0}” has no parent.
         */
        public static final int NODE_HAS_NO_PARENT_$1 = 124;

        /**
         * Scaling affine transform is not invertible.
         */
        public static final int NONINVERTIBLE_SCALING_TRANSFORM = 125;

        /**
         * Transform is not invertible.
         */
        public static final int NONINVERTIBLE_TRANSFORM = 126;

        /**
         * The grid to coordinate system transform must be affine.
         */
        public static final int NON_AFFINE_TRANSFORM = 127;

        /**
         * Not an angular unit: “{0}”.
         */
        public static final int NON_ANGULAR_UNIT_$1 = 128;

        /**
         * Coordinate system “{0}” is not Cartesian.
         */
        public static final int NON_CARTESIAN_COORDINATE_SYSTEM_$1 = 129;

        /**
         * Bands {0} and {1} are not consecutive.
         */
        public static final int NON_CONSECUTIVE_BANDS_$2 = 130;

        /**
         * Can’t convert value from units “{1}” to “{0}”.
         */
        public static final int NON_CONVERTIBLE_UNITS_$2 = 131;

        /**
         * Unmatched parenthesis in “{0}”: missing ‘{1}’.
         */
        public static final int NON_EQUILIBRATED_PARENTHESIS_$2 = 132;

        /**
         * Some categories use non-integer sample values.
         */
        public static final int NON_INTEGER_CATEGORY = 133;

        /**
         * Relation is not linear.
         */
        public static final int NON_LINEAR_RELATION = 134;

        /**
         * “{0}” is not a linear unit.
         */
        public static final int NON_LINEAR_UNIT_$1 = 135;

        /**
         * Unit conversion from “{0}” to “{1}” is non-linear.
         */
        public static final int NON_LINEAR_UNIT_CONVERSION_$2 = 136;

        /**
         * Axis directions {0} and {1} are not perpendicular.
         */
        public static final int NON_PERPENDICULAR_AXIS_$2 = 137;

        /**
         * “{0}” is not a scale unit.
         */
        public static final int NON_SCALE_UNIT_$1 = 138;

        /**
         * “{0}” is not a time unit.
         */
        public static final int NON_TEMPORAL_UNIT_$1 = 139;

        /**
         * Transform is not affine.
         */
        public static final int NOT_AN_AFFINE_TRANSFORM = 140;

        /**
         * Can’t format object of class “{0}” as an angle.
         */
        public static final int NOT_AN_ANGLE_OBJECT_$1 = 141;

        /**
         * Value “{0}” is not a valid integer.
         */
        public static final int NOT_AN_INTEGER_$1 = 142;

        /**
         * Not a directory: {0}
         */
        public static final int NOT_A_DIRECTORY_$1 = 143;

        /**
         * Points dont seem to be distributed on a regular grid.
         */
        public static final int NOT_A_GRID = 144;

        /**
         * Value “{0}” is not a valid real number.
         */
        public static final int NOT_A_NUMBER_$1 = 145;

        /**
         * {0} is not a comparable class.
         */
        public static final int NOT_COMPARABLE_CLASS_$1 = 146;

        /**
         * Value {0} is not a real, non-null number.
         */
        public static final int NOT_DIFFERENT_THAN_ZERO_$1 = 147;

        /**
         * Value ‘{0}’={1} is invalid. Expected a number greater than 0.
         */
        public static final int NOT_GREATER_THAN_ZERO_$2 = 148;

        /**
         * Not a 3D coordinate system.
         */
        public static final int NOT_THREE_DIMENSIONAL_CS = 149;

        /**
         * Can’t wrap a {0} dimensional object into a 2 dimensional one.
         */
        public static final int NOT_TWO_DIMENSIONAL_$1 = 150;

        /**
         * No authority was defined for code “{0}”. Did you forget "AUTHORITY:NUMBER"?
         */
        public static final int NO_AUTHORITY_$1 = 115;

        /**
         * No category for value {0}.
         */
        public static final int NO_CATEGORY_FOR_VALUE_$1 = 151;

        /**
         * Transformation doesn’t convergence.
         */
        public static final int NO_CONVERGENCE = 152;

        /**
         * No convergence for points {0} and {1}.
         */
        public static final int NO_CONVERGENCE_$2 = 153;

        /**
         * No data source found.
         */
        public static final int NO_DATA_SOURCE = 154;

        /**
         * No foreigner key found in table “{0}”.
         */
        public static final int NO_FOREIGNER_KEY_$1 = 117;

        /**
         * No image codec found for the “{0}” format. Available formats are {1}.
         */
        public static final int NO_IMAGE_FORMAT_$2 = 155;

        /**
         * No input set.
         */
        public static final int NO_IMAGE_INPUT = 156;

        /**
         * No output set.
         */
        public static final int NO_IMAGE_OUTPUT = 157;

        /**
         * No suitable image reader for this input.
         */
        public static final int NO_IMAGE_READER = 158;

        /**
         * No suitable image writer for this output.
         */
        public static final int NO_IMAGE_WRITER = 159;

        /**
         * No layer has been specified.
         */
        public static final int NO_LAYER_SPECIFIED = 160;

        /**
         * Parameter “{0}” is missing.
         */
        public static final int NO_PARAMETER_$1 = 119;

        /**
         * Missing value for parameter “{0}”.
         */
        public static final int NO_PARAMETER_VALUE_$1 = 120;

        /**
         * No data series has been specified.
         */
        public static final int NO_SERIES_SPECIFIED = 161;

        /**
         * No source axis match {0}.
         */
        public static final int NO_SOURCE_AXIS_$1 = 162;

        /**
         * No attribute named “{0}” has been found.
         */
        public static final int NO_SUCH_ATTRIBUTE_$1 = 163;

        /**
         * No attribute named “{0}” has been found in “{1}”.
         */
        public static final int NO_SUCH_ATTRIBUTE_$2 = 264;

        /**
         * No object of type ‘{0}’ has been found for code “{1}”.
         */
        public static final int NO_SUCH_AUTHORITY_CODE_$2 = 164;

        /**
         * No code “{0}” from authority “{1}” found for object of type ‘{2}’.
         */
        public static final int NO_SUCH_AUTHORITY_CODE_$3 = 165;

        /**
         * No ‘{0}’ element.
         */
        public static final int NO_SUCH_ELEMENT_$1 = 261;

        /**
         * No element named “{0}” has been found.
         */
        public static final int NO_SUCH_ELEMENT_NAME_$1 = 166;

        /**
         * No such “{0}” operation for this processor.
         */
        public static final int NO_SUCH_OPERATION_$1 = 180;

        /**
         * No record found in table “{0}” for key “{1}”.
         */
        public static final int NO_SUCH_RECORD_$2 = 192;

        /**
         * No “{1}” record found in the “{0}” table.
         */
        public static final int NO_SUCH_RECORD_IN_TABLE_$2 = 167;

        /**
         * No two-dimensional transform available for this geometry.
         */
        public static final int NO_TRANSFORM2D_AVAILABLE = 168;

        /**
         * No transformation available from system “{0}” to “{1}”.
         */
        public static final int NO_TRANSFORMATION_PATH_$2 = 169;

        /**
         * No transform for classification “{0}”.
         */
        public static final int NO_TRANSFORM_FOR_CLASSIFICATION_$1 = 170;

        /**
         * Unit must be specified.
         */
        public static final int NO_UNIT = 171;

        /**
         * Missing WKT definition.
         */
        public static final int NO_WKT_DEFINITION = 121;

        /**
         * Argument ‘{0}’ should not be null.
         */
        public static final int NULL_ARGUMENT_$1 = 172;

        /**
         * Attribute “{0}” should not be null.
         */
        public static final int NULL_ATTRIBUTE_$1 = 173;

        /**
         * Format #{0} (on {1}) is not defined.
         */
        public static final int NULL_FORMAT_$2 = 174;

        /**
         * “{0}” parameter should be not null and of type ‘{1}’.
         */
        public static final int NULL_PARAMETER_$2 = 175;

        /**
         * Unexpected null value in record “{0}” for the column “{1}” in table “{2}”.
         */
        public static final int NULL_VALUE_IN_TABLE_$3 = 176;

        /**
         * Bad array length: {0}. An even array length was expected.
         */
        public static final int ODD_ARRAY_LENGTH_$1 = 178;

        /**
         * Operation “{0}” is already bounds.
         */
        public static final int OPERATION_ALREADY_BOUNDS_$1 = 179;

        /**
         * Possible use of “{0}” projection outside its valid area.
         */
        public static final int OUT_OF_PROJECTION_VALID_AREA_$1 = 181;

        /**
         * Unparsable string: “{0}”. Please check characters “{1}”.
         */
        public static final int PARSE_EXCEPTION_$2 = 183;

        /**
         * Pixels must be square with no flip and no rotation.
         */
        public static final int PIXELS_NOT_SQUARE_OR_ROTATED_IMAGE = 184;

        /**
         * Coordinate ({0}) is outside coverage.
         */
        public static final int POINT_OUTSIDE_COVERAGE_$1 = 185;

        /**
         * Point is outside grid
         */
        public static final int POINT_OUTSIDE_GRID = 186;

        /**
         * Point outside hemisphere of projection.
         */
        public static final int POINT_OUTSIDE_HEMISPHERE = 187;

        /**
         * Latitude {0} is too close to a pole.
         */
        public static final int POLE_PROJECTION_$1 = 188;

        /**
         * Can’t add point to a closed polygon.
         */
        public static final int POLYGON_CLOSED = 189;

        /**
         * Ranges [{0} … {1}] and [{2} … {3}] overlap.
         */
        public static final int RANGE_OVERLAP_$4 = 191;

        /**
         * Recursive call while creating a ‘{0}’ object.
         */
        public static final int RECURSIVE_CALL_$1 = 193;

        /**
         * Recursive call while creating a ‘{0}’ object for code “{1}”.
         */
        public static final int RECURSIVE_CALL_$2 = 194;

        /**
         * The requested envelope does not intersect the data envelope.
         */
        public static final int REQUESTED_ENVELOPE_DO_NOT_INTERSECT = 266;

        /**
         * RGB value {0} is out of range.
         */
        public static final int RGB_OUT_OF_RANGE_$1 = 195;

        /**
         * Execution on a remote machine failed.
         */
        public static final int RMI_FAILURE = 196;

        /**
         * Expected {0}={1} but got {2}.
         */
        public static final int TEST_FAILURE_$3 = 197;

        /**
         * The thread doesn’t hold the lock.
         */
        public static final int THREAD_DOESNT_HOLD_LOCK = 198;

        /**
         * Timeout.
         */
        public static final int TIMEOUT = 252;

        /**
         * Tolerance error.
         */
        public static final int TOLERANCE_ERROR = 199;

        /**
         * Expected at least {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final int TOO_FEW_ARGUMENTS_$2 = 200;

        /**
         * Expected at most {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final int TOO_MANY_ARGUMENTS_$2 = 201;

        /**
         * Too many occurrences of “{0}”. There is already {1} of them.
         */
        public static final int TOO_MANY_OCCURRENCES_$2 = 202;

        /**
         * Undefined format.
         */
        public static final int UNDEFINED_FORMAT = 250;

        /**
         * Format “{0}” is undefined.
         */
        public static final int UNDEFINED_FORMAT_$1 = 203;

        /**
         * Undefined property.
         */
        public static final int UNDEFINED_PROPERTY = 204;

        /**
         * Property “{0}” is not defined.
         */
        public static final int UNDEFINED_PROPERTY_$1 = 205;

        /**
         * Unexpected argument for operation “{0}”.
         */
        public static final int UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_$1 = 206;

        /**
         * Unexpected domain for the “{0}” axis. Expected one of {1}.
         */
        public static final int UNEXPECTED_AXIS_DOMAIN_$2 = 271;

        /**
         * Unexpected dimension for a “{0}” coordinate system.
         */
        public static final int UNEXPECTED_DIMENSION_FOR_CS_$1 = 207;

        /**
         * Unexpected end of string.
         */
        public static final int UNEXPECTED_END_OF_STRING = 208;

        /**
         * Grid header has unexpected length: {0}
         */
        public static final int UNEXPECTED_HEADER_LENGTH_$1 = 69;

        /**
         * Image doesn’t have the expected size.
         */
        public static final int UNEXPECTED_IMAGE_SIZE = 209;

        /**
         * Parameter “{0}” was not expected.
         */
        public static final int UNEXPECTED_PARAMETER_$1 = 210;

        /**
         * Matrix row {0} has a length of {1}, while {2} was expected.
         */
        public static final int UNEXPECTED_ROW_LENGTH_$3 = 211;

        /**
         * {4,choice,0#Forward|1#Inverse} transformation doesn’t produce the expected values. Expected
         * {0} but got {1} (a difference of {2}) at ordinate {3}.
         */
        public static final int UNEXPECTED_TRANSFORM_RESULT_$5 = 212;

        /**
         * {0} records have been updated while only one was expected.
         */
        public static final int UNEXPECTED_UPDATES_$1 = 213;

        /**
         * Parameter “{0}” has no unit.
         */
        public static final int UNITLESS_PARAMETER_$1 = 214;

        /**
         * Authority “{0}” is unknown or doesn’t match the supplied hints. Maybe it is defined in an
         * unreachable JAR file?
         */
        public static final int UNKNOWN_AUTHORITY_$1 = 215;

        /**
         * Authority “{0}” is not available. The cause is: {1}
         */
        public static final int UNKNOWN_AUTHORITY_$2 = 216;

        /**
         * Unknown axis direction: “{0}”.
         */
        public static final int UNKNOWN_AXIS_DIRECTION_$1 = 217;

        /**
         * Unknown command: {0}
         */
        public static final int UNKNOWN_COMMAND_$1 = 218;

        /**
         * Unknown enumeration value: {0}.
         */
        public static final int UNKNOWN_ENUM_$1 = 260;

        /**
         * File suffix “{0}” is unknown.
         */
        public static final int UNKNOWN_FILE_SUFFIX_$1 = 219;

        /**
         * Image format “{0}” is unknown.
         */
        public static final int UNKNOWN_IMAGE_FORMAT_$1 = 220;

        /**
         * Interpolation “{0}” is unknown.
         */
        public static final int UNKNOWN_INTERPOLATION_$1 = 221;

        /**
         * MIME type “{0}” is unknown.
         */
        public static final int UNKNOWN_MIME_TYPE_$1 = 222;

        /**
         * Unknown parameter: {0}
         */
        public static final int UNKNOWN_PARAMETER_$1 = 223;

        /**
         * Unknown parameter name: {0}
         */
        public static final int UNKNOWN_PARAMETER_NAME_$1 = 224;

        /**
         * Unknown projection type.
         */
        public static final int UNKNOWN_PROJECTION_TYPE = 225;

        /**
         * Type “{0}” is unknown in this context.
         */
        public static final int UNKNOWN_TYPE_$1 = 226;

        /**
         * Unit “{0}” is not recognized.
         */
        public static final int UNKNOWN_UNIT_$1 = 227;

        /**
         * This affine transform is unmodifiable.
         */
        public static final int UNMODIFIABLE_AFFINE_TRANSFORM = 228;

        /**
         * Unmodifiable geometry.
         */
        public static final int UNMODIFIABLE_GEOMETRY = 229;

        /**
         * Unmodifiable metadata.
         */
        public static final int UNMODIFIABLE_METADATA = 230;

        /**
         * Unmodifiable {0} object.
         */
        public static final int UNMODIFIABLE_OBJECT_$1 = 231;

        /**
         * Can’t parse value “{1}” in attribute ‘{0}’.
         */
        public static final int UNPARSABLE_ATTRIBUTE_$2 = 263;

        /**
         * Can’t parse “{0}” as a number.
         */
        public static final int UNPARSABLE_NUMBER_$1 = 232;

        /**
         * Can’t parse “{0}” because “{1}” is unrecognized.
         */
        public static final int UNPARSABLE_STRING_$2 = 233;

        /**
         * Coordinate reference system is unspecified.
         */
        public static final int UNSPECIFIED_CRS = 234;

        /**
         * Unspecified image’s size.
         */
        public static final int UNSPECIFIED_IMAGE_SIZE = 235;

        /**
         * Unspecified coordinates transform.
         */
        public static final int UNSPECIFIED_TRANSFORM = 236;

        /**
         * Coordinate system “{0}” is unsupported.
         */
        public static final int UNSUPPORTED_COORDINATE_SYSTEM_$1 = 237;

        /**
         * Coordinate reference system “{0}” is unsupported.
         */
        public static final int UNSUPPORTED_CRS_$1 = 238;

        /**
         * Unsupported data type.
         */
        public static final int UNSUPPORTED_DATA_TYPE = 239;

        /**
         * Data type “{0}” is not supported.
         */
        public static final int UNSUPPORTED_DATA_TYPE_$1 = 240;

        /**
         * Datum “{0}” is unsupported.
         */
        public static final int UNSUPPORTED_DATUM_$1 = 241;

        /**
         * Unsupported file type: {0}
         */
        public static final int UNSUPPORTED_FILE_TYPE_$1 = 242;

        /**
         * Unsupported image type.
         */
        public static final int UNSUPPORTED_IMAGE_TYPE = 253;

        /**
         * At most one instance of ‘{0}’ is supported.
         */
        public static final int UNSUPPORTED_MULTI_OCCURRENCE_$1 = 262;

        /**
         * Unsupported operation: {0}
         */
        public static final int UNSUPPORTED_OPERATION_$1 = 243;

        /**
         * Unsupported transform.
         */
        public static final int UNSUPPORTED_TRANSFORM = 244;

        /**
         * A value is already defined for {0}.
         */
        public static final int VALUE_ALREADY_DEFINED_$1 = 245;

        /**
         * Value {0} is out of range [{1} … {2}].
         */
        public static final int VALUE_OUT_OF_BOUNDS_$3 = 246;

        /**
         * Value ‘{0}’={1} is out of range [{2} … {3}].
         */
        public static final int VALUE_OUT_OF_BOUNDS_$4 = 255;

        /**
         * Numerical value tend toward infinity.
         */
        public static final int VALUE_TEND_TOWARD_INFINITY = 247;

        /**
         * No variable “{0}” found in file “{1}”.
         */
        public static final int VARIABLE_NOT_FOUND_IN_FILE_$2 = 248;

        /**
         * A veto has been ignored because it has been applied too late.
         */
        public static final int VETO_TOO_LATE = 251;

        /**
         * Value {1} is outside the domain of coverage “{0}”.
         */
        public static final int ZVALUE_OUTSIDE_COVERAGE_$2 = 249;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    Errors(final String filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Errors getResources(Locale locale) throws MissingResourceException {
        return getBundle(Errors.class, locale);
    }

    /**
     * The international string to be returned by {@link formatInternational}.
     *
     * @since 3.05
     */
    private static final class International extends ResourceInternationalString {
        private static final long serialVersionUID = -229348959712294902L;

        International(final int key) {
            super(Errors.class.getName(), String.valueOf(key));
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
     *
     * @since 3.05
     */
    public static InternationalString formatInternational(final int key) {
        return new International(key);
    }

    /**
     * Gets an international string for the given key. This method does not check for the key
     * validity. If the key is invalid, then a {@link MissingResourceException} may be thrown
     * when a {@link InternationalString#toString} method is invoked.
     *
     * {@note This method is redundant with the one expecting <code>Object...</code>, but avoid
     *        the creation of a temporary array. There is no risk of confusion since the two
     *        methods delegate their work to the same <code>format</code> method anyway.}
     *
     * @param  key The key for the desired string.
     * @param  arg Values to substitute to "{0}".
     * @return An international string for the given key.
     *
     * @todo Current implementation just invokes {@link #format}. Need to format only when
     *       {@code toString(Locale)} is invoked.
     *
     * @since 3.05
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
     *
     * @since 3.05
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

    /**
     * Gets a string for the given key are replace all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @param  arg3 Value to substitute to "{3}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2,
                                final Object arg3) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2, arg3);
    }
}
