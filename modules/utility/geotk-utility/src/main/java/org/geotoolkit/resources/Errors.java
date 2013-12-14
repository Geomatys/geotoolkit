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
        public static final short AMBIGIOUS_AXIS_LENGTH = 0;

        /**
         * Value “{0}” is ambiguous since it associated to the following possibilities:
         */
        public static final short AMBIGUOUS_VALUE_1 = 1;

        /**
         * Angle {0} is too high.
         */
        public static final short ANGLE_OVERFLOW_1 = 2;

        /**
         * Azimuth {0} is out of range (±180°).
         */
        public static final short AZIMUTH_OUT_OF_RANGE_1 = 3;

        /**
         * Bursa-Wolf parameters required.
         */
        public static final short BURSA_WOLF_PARAMETERS_REQUIRED = 4;

        /**
         * The operation has been canceled.
         */
        public static final short CANCELED_OPERATION = 5;

        /**
         * Can’t compute derivative.
         */
        public static final short CANT_COMPUTE_DERIVATIVE = 6;

        /**
         * Can’t concatenate transforms “{0}” and “{1}”.
         */
        public static final short CANT_CONCATENATE_TRANSFORMS_2 = 7;

        /**
         * Failed to connect to the {0} database.
         */
        public static final short CANT_CONNECT_DATABASE_1 = 8;

        /**
         * Can’t convert value from type ‘{0}’.
         */
        public static final short CANT_CONVERT_FROM_TYPE_1 = 9;

        /**
         * Can’t convert from type ‘{0}’ to type ‘{1}’.
         */
        public static final short CANT_CONVERT_FROM_TYPE_2 = 10;

        /**
         * Can’t create directory “{0}”.
         */
        public static final short CANT_CREATE_DIRECTORY_1 = 11;

        /**
         * Can’t create a factory of type ‘{0}’.
         */
        public static final short CANT_CREATE_FACTORY_FOR_TYPE_1 = 12;

        /**
         * Can’t create object of type ‘{0}’ from a text.
         */
        public static final short CANT_CREATE_OBJECT_FROM_TEXT_1 = 13;

        /**
         * Can’t delete file “{0}”.
         */
        public static final short CANT_DELETE_FILE_1 = 14;

        /**
         * Can’t evaluate a value for coordinate ({0}).
         */
        public static final short CANT_EVALUATE_FOR_COORDINATE_1 = 15;

        /**
         * Failed to get the data source for name “{0}”.
         */
        public static final short CANT_GET_DATASOURCE_1 = 16;

        /**
         * Can not process the “{0}={1}” property.
         */
        public static final short CANT_PROCESS_PROPERTY_2 = 17;

        /**
         * Can’t read the “{1}” record in the “{0}” table.
         */
        public static final short CANT_READ_DATABASE_RECORD_2 = 18;

        /**
         * Can’t read the “{1}” column for the “{2}” record in the “{0}” table.
         */
        public static final short CANT_READ_DATABASE_RECORD_3 = 19;

        /**
         * Can’t read the “{0}” table.
         */
        public static final short CANT_READ_DATABASE_TABLE_1 = 20;

        /**
         * Can’t read the “{1}” column in the “{0}” table.
         */
        public static final short CANT_READ_DATABASE_TABLE_2 = 21;

        /**
         * Can’t read file “{0}”.
         */
        public static final short CANT_READ_FILE_1 = 22;

        /**
         * Can’t reduce “{0}” to a two-dimensional coordinate system.
         */
        public static final short CANT_REDUCE_TO_TWO_DIMENSIONS_1 = 23;

        /**
         * Can’t reproject grid coverage “{0}”.
         */
        public static final short CANT_REPROJECT_COVERAGE_1 = 24;

        /**
         * Can’t separate CRS “{0}”.
         */
        public static final short CANT_SEPARATE_CRS_1 = 25;

        /**
         * Can’t set a value for attribute “{0}”.
         */
        public static final short CANT_SET_ATTRIBUTE_VALUE_1 = 26;

        /**
         * Can’t set a value for parameter “{0}”.
         */
        public static final short CANT_SET_PARAMETER_VALUE_1 = 27;

        /**
         * Can’t transform envelope.
         */
        public static final short CANT_TRANSFORM_ENVELOPE = 28;

        /**
         * Can’t transform some points that should be valid.
         */
        public static final short CANT_TRANSFORM_VALID_POINTS = 29;

        /**
         * Can’t write file “{0}”.
         */
        public static final short CANT_WRITE_FILE_1 = 30;

        /**
         * Graphic “{0}” is owned by an other canvas.
         */
        public static final short CANVAS_NOT_OWNER_1 = 31;

        /**
         * Axis {0} and {1} are colinear.
         */
        public static final short COLINEAR_AXIS_2 = 32;

        /**
         * Coverage returned by ‘{0}’ is already the view of coverage “{1}”.
         */
        public static final short COVERAGE_ALREADY_BOUND_2 = 33;

        /**
         * Database failure while creating a ‘{0}’ object for code “{1}”.
         */
        public static final short DATABASE_FAILURE_2 = 34;

        /**
         * Failed to update the database.
         */
        public static final short DATABASE_UPDATE_FAILURE = 35;

        /**
         * Date {0} is outside the range of available data.
         */
        public static final short DATE_OUTSIDE_COVERAGE_1 = 36;

        /**
         * The destination has not been set.
         */
        public static final short DESTINATION_NOT_SET = 37;

        /**
         * The direction has not been set.
         */
        public static final short DIRECTION_NOT_SET = 38;

        /**
         * The factory has been disposed.
         */
        public static final short DISPOSED_FACTORY = 39;

        /**
         * The distance {0} is out of range ({1} to {2} {3})
         */
        public static final short DISTANCE_OUT_OF_RANGE_4 = 40;

        /**
         * Dropped the “{0}” foreigner key constraint.
         */
        public static final short DROPPED_FOREIGNER_KEY_1 = 41;

        /**
         * Name or alias for parameter “{0}” at index {1} conflict with name “{2}” at index {3}.
         */
        public static final short DUPLICATED_PARAMETER_NAME_4 = 42;

        /**
         * The “{0}” record is defined more than once.
         */
        public static final short DUPLICATED_RECORD_1 = 43;

        /**
         * Found {0} duplicated values.
         */
        public static final short DUPLICATED_VALUES_COUNT_1 = 44;

        /**
         * Duplicated values for key “{0}”.
         */
        public static final short DUPLICATED_VALUES_FOR_KEY_1 = 45;

        /**
         * The “{0}” value is specified more than once.
         */
        public static final short DUPLICATED_VALUE_1 = 46;

        /**
         * Elliptical projection not supported.
         */
        public static final short ELLIPTICAL_NOT_SUPPORTED = 47;

        /**
         * The array must contains at least one element.
         */
        public static final short EMPTY_ARRAY = 48;

        /**
         * The dictionary must contains at least one entry.
         */
        public static final short EMPTY_DICTIONARY = 49;

        /**
         * Envelope must be at least two-dimensional and non-empty.
         */
        public static final short EMPTY_ENVELOPE_2D = 50;

        /**
         * Empty or invalid rectangle: {0}
         */
        public static final short EMPTY_RECTANGLE_1 = 51;

        /**
         * Premature end of data file.
         */
        public static final short END_OF_DATA_FILE = 52;

        /**
         * Expected one of {0}.
         */
        public static final short EXPECTED_ONE_OF_1 = 53;

        /**
         * No factory of kind “{0}” found.
         */
        public static final short FACTORY_NOT_FOUND_1 = 54;

        /**
         * File “{0}” already exists.
         */
        public static final short FILE_ALREADY_EXISTS_1 = 55;

        /**
         * File “{0}” does not exist or is unreadable.
         */
        public static final short FILE_DOES_NOT_EXIST_1 = 56;

        /**
         * File has too few data.
         */
        public static final short FILE_HAS_TOO_FEW_DATA = 57;

        /**
         * File has too many data.
         */
        public static final short FILE_HAS_TOO_MANY_DATA = 58;

        /**
         * Attribute “{0}” is not allowed for an object of type ‘{1}’.
         */
        public static final short FORBIDDEN_ATTRIBUTE_2 = 59;

        /**
         * Geotoolkit.org extension required for “{0}” operation.
         */
        public static final short GEOTOOLKIT_EXTENSION_REQUIRED_1 = 60;

        /**
         * Latitude and Longitude grid locations are not equal
         */
        public static final short GRID_LOCATIONS_UNEQUAL = 61;

        /**
         * Hole is not inside polygon.
         */
        public static final short HOLE_NOT_INSIDE_POLYGON = 62;

        /**
         * Illegal angle pattern: {0}
         */
        public static final short ILLEGAL_ANGLE_PATTERN_1 = 63;

        /**
         * Illegal value for argument ‘{0}’.
         */
        public static final short ILLEGAL_ARGUMENT_1 = 64;

        /**
         * Illegal argument: ‘{0}’={1}
         */
        public static final short ILLEGAL_ARGUMENT_2 = 65;

        /**
         * Argument ‘{0}’ can not be an instance of ‘{1}’. Expected an instance of ‘{2}’ or derived
         * type.
         */
        public static final short ILLEGAL_ARGUMENT_CLASS_3 = 66;

        /**
         * Illegal array length for {0} dimensional points.
         */
        public static final short ILLEGAL_ARRAY_LENGTH_FOR_DIMENSION_1 = 67;

        /**
         * Axis can’t be oriented toward {0} for coordinate system of class ‘{1}’.
         */
        public static final short ILLEGAL_AXIS_ORIENTATION_2 = 68;

        /**
         * Band number {0} is not valid.
         */
        public static final short ILLEGAL_BAND_NUMBER_1 = 69;

        /**
         * Class ‘{0}’ is illegal. It must be ‘{1}’ or a derived class.
         */
        public static final short ILLEGAL_CLASS_2 = 70;

        /**
         * ‘{0}’ can not be an instance of ‘{1}’. Expected an instance of ‘{2}’ or derived type.
         */
        public static final short ILLEGAL_CLASS_3 = 71;

        /**
         * Illegal coordinate: {0}
         */
        public static final short ILLEGAL_COORDINATE_1 = 72;

        /**
         * Illegal coordinate reference system.
         */
        public static final short ILLEGAL_COORDINATE_REFERENCE_SYSTEM = 73;

        /**
         * Coordinate system of type ‘{0}’ are incompatible with CRS of type ‘{1}’.
         */
        public static final short ILLEGAL_COORDINATE_SYSTEM_FOR_CRS_2 = 74;

        /**
         * Coordinate system can’t have {0} dimensions.
         */
        public static final short ILLEGAL_CS_DIMENSION_1 = 75;

        /**
         * Illegal descriptor for parameter “{0}”.
         */
        public static final short ILLEGAL_DESCRIPTOR_FOR_PARAMETER_1 = 76;

        /**
         * Bad entry.
         */
        public static final short ILLEGAL_ENTRY = 77;

        /**
         * Illegal grid envelope [{1} … {2}] for dimension {0}.
         */
        public static final short ILLEGAL_GRID_ENVELOPE_3 = 78;

        /**
         * “{0}” is not a valid identifier.
         */
        public static final short ILLEGAL_IDENTIFIER_1 = 79;

        /**
         * Illegal instruction “{0}”.
         */
        public static final short ILLEGAL_INSTRUCTION_1 = 80;

        /**
         * Illegal key: {0}
         */
        public static final short ILLEGAL_KEY_1 = 81;

        /**
         * Local “{0}” is not recognized.
         */
        public static final short ILLEGAL_LANGUAGE_CODE_1 = 82;

        /**
         * Illegal data at line {1} in file “{0}”.
         */
        public static final short ILLEGAL_LINE_IN_FILE_2 = 83;

        /**
         * Illegal matrix size.
         */
        public static final short ILLEGAL_MATRIX_SIZE = 84;

        /**
         * Expected a source with a single image, or tiles having the same resolution.
         */
        public static final short ILLEGAL_MOSAIC_INPUT = 85;

        /**
         * Parameter “{0}” occurs {1} time, while the expected range of occurrences was [{2} … {3}].
         */
        public static final short ILLEGAL_OCCURS_FOR_PARAMETER_4 = 86;

        /**
         * This operation can’t be applied to values of class ‘{0}’.
         */
        public static final short ILLEGAL_OPERATION_FOR_VALUE_CLASS_1 = 87;

        /**
         * Bad ordinates at dimension {0}.
         */
        public static final short ILLEGAL_ORDINATE_AT_1 = 88;

        /**
         * Parameter ‘{0}’ can’t be of type ‘{1}’.
         */
        public static final short ILLEGAL_PARAMETER_TYPE_2 = 89;

        /**
         * Parameter ‘{0}’ can’t have value “{1}”.
         */
        public static final short ILLEGAL_PARAMETER_VALUE_2 = 90;

        /**
         * Values for the ‘{0}’ property can not be of kind ‘{1}’.
         */
        public static final short ILLEGAL_PROPERTY_TYPE_2 = 91;

        /**
         * Range [{0} … {1}] is not valid.
         */
        public static final short ILLEGAL_RANGE_2 = 92;

        /**
         * Illegal transform of type ‘{0}’.
         */
        public static final short ILLEGAL_TRANSFORM_FOR_TYPE_1 = 93;

        /**
         * Unit “{1}” can’t be raised to power {0}.
         */
        public static final short ILLEGAL_UNIT_POWER_2 = 94;

        /**
         * Multiplication or division of “{0}” by “{1}” not allowed.
         */
        public static final short ILLEGAL_UNIT_PRODUCT_2 = 95;

        /**
         * The “{0}” object is too complex for WKT syntax.
         */
        public static final short ILLEGAL_WKT_FORMAT_1 = 96;

        /**
         * Incompatible coordinate system type.
         */
        public static final short INCOMPATIBLE_COORDINATE_SYSTEM_TYPE = 97;

        /**
         * Projection parameter “{0}” is incompatible with ellipsoid “{1}”.
         */
        public static final short INCOMPATIBLE_ELLIPSOID_2 = 98;

        /**
         * Incompatible grid geometries.
         */
        public static final short INCOMPATIBLE_GRID_GEOMETRY = 99;

        /**
         * Incompatible unit: {0}
         */
        public static final short INCOMPATIBLE_UNIT_1 = 100;

        /**
         * Direction “{1}” is inconsistent with axis “{0}”.
         */
        public static final short INCONSISTENT_AXIS_ORIENTATION_2 = 101;

        /**
         * Inconsistent domain between “{0}” and “{1}”.
         */
        public static final short INCONSISTENT_DOMAIN_2 = 102;

        /**
         * Property “{0}” has a value inconsistent with other properties.
         */
        public static final short INCONSISTENT_PROPERTY_1 = 103;

        /**
         * Inconsistent value.
         */
        public static final short INCONSISTENT_VALUE = 104;

        /**
         * Index {0} is out of bounds.
         */
        public static final short INDEX_OUT_OF_BOUNDS_1 = 105;

        /**
         * Coefficient {0}={1} can’t be NaN or infinity.
         */
        public static final short INFINITE_COEFFICIENT_2 = 106;

        /**
         * {0} value is infinite.
         */
        public static final short INFINITE_VALUE_1 = 107;

        /**
         * Inseparable transform.
         */
        public static final short INSEPARABLE_TRANSFORM = 108;

        /**
         * {0} points were specified, while {1} are required.
         */
        public static final short INSUFFICIENT_POINTS_2 = 109;

        /**
         * Error in “{0}”:
         */
        public static final short IN_1 = 110;

        /**
         * Latitudes {0} and {1} are opposite.
         */
        public static final short LATITUDES_ARE_OPPOSITE_2 = 111;

        /**
         * Latitude {0} is out of range (±90°).
         */
        public static final short LATITUDE_OUT_OF_RANGE_1 = 112;

        /**
         * The line contains {0} columns while only {1} was expected. Characters “{2}” seem to be
         * extra.
         */
        public static final short LINE_TOO_LONG_3 = 113;

        /**
         * The line contains only {0} columns while {1} was expected.
         */
        public static final short LINE_TOO_SHORT_2 = 114;

        /**
         * Longitude {0} is out of range (±180°).
         */
        public static final short LONGITUDE_OUT_OF_RANGE_1 = 115;

        /**
         * Malformed envelope
         */
        public static final short MALFORMED_ENVELOPE = 116;

        /**
         * Attribute “{0}” is mandatory for an object of type ‘{1}’.
         */
        public static final short MANDATORY_ATTRIBUTE_2 = 117;

        /**
         * All rows doesn’t have the same length.
         */
        public static final short MATRIX_NOT_REGULAR = 118;

        /**
         * Mismatched array length.
         */
        public static final short MISMATCHED_ARRAY_LENGTH = 119;

        /**
         * Array length of parameters ‘{0}’ and ‘{1}’ do not match.
         */
        public static final short MISMATCHED_ARRAY_LENGTH_2 = 120;

        /**
         * The coordinate reference system must be the same for all objects.
         */
        public static final short MISMATCHED_COORDINATE_REFERENCE_SYSTEM = 121;

        /**
         * Mismatched object dimension: {0}D and {1}D.
         */
        public static final short MISMATCHED_DIMENSION_2 = 122;

        /**
         * Argument ‘{0}’ has {1} dimensions, while {2} was expected.
         */
        public static final short MISMATCHED_DIMENSION_3 = 123;

        /**
         * The envelope uses an incompatible CRS: was “{1}” while we expected “{0}”.
         */
        public static final short MISMATCHED_ENVELOPE_CRS_2 = 124;

        /**
         * The dimension of the “{0}” image is ({1}×{2}) pixels, while the expected dimension was
         * ({3}×{4}) pixels.
         */
        public static final short MISMATCHED_IMAGE_SIZE_5 = 125;

        /**
         * The number of image bands ({0}) differs from the number of supplied ‘{2}’ objects ({1}).
         */
        public static final short MISMATCHED_NUMBER_OF_BANDS_3 = 126;

        /**
         * Character ‘{0}’ was expected.
         */
        public static final short MISSING_CHARACTER_1 = 127;

        /**
         * This operation requires the “{0}” module.
         */
        public static final short MISSING_MODULE_1 = 128;

        /**
         * Geophysics categories mixed with non-geophysics ones.
         */
        public static final short MIXED_CATEGORIES = 129;

        /**
         * Column number for “{0}” ({1}) can’t be negative.
         */
        public static final short NEGATIVE_COLUMN_2 = 130;

        /**
         * Node “{0}” has no parent.
         */
        public static final short NODE_HAS_NO_PARENT_1 = 131;

        /**
         * Scaling affine transform is not invertible.
         */
        public static final short NONINVERTIBLE_SCALING_TRANSFORM = 132;

        /**
         * Transform is not invertible.
         */
        public static final short NONINVERTIBLE_TRANSFORM = 133;

        /**
         * The grid to coordinate system transform must be affine.
         */
        public static final short NON_AFFINE_TRANSFORM = 134;

        /**
         * Not an angular unit: “{0}”.
         */
        public static final short NON_ANGULAR_UNIT_1 = 135;

        /**
         * Coordinate system “{0}” is not Cartesian.
         */
        public static final short NON_CARTESIAN_COORDINATE_SYSTEM_1 = 136;

        /**
         * Bands {0} and {1} are not consecutive.
         */
        public static final short NON_CONSECUTIVE_BANDS_2 = 137;

        /**
         * Can’t convert value from units “{1}” to “{0}”.
         */
        public static final short NON_CONVERTIBLE_UNITS_2 = 138;

        /**
         * Unmatched parenthesis in “{0}”: missing ‘{1}’.
         */
        public static final short NON_EQUILIBRATED_PARENTHESIS_2 = 139;

        /**
         * Some categories use non-integer sample values.
         */
        public static final short NON_INTEGER_CATEGORY = 140;

        /**
         * Relation is not linear.
         */
        public static final short NON_LINEAR_RELATION = 141;

        /**
         * “{0}” is not a linear unit.
         */
        public static final short NON_LINEAR_UNIT_1 = 142;

        /**
         * Unit conversion from “{0}” to “{1}” is non-linear.
         */
        public static final short NON_LINEAR_UNIT_CONVERSION_2 = 143;

        /**
         * Axis directions {0} and {1} are not perpendicular.
         */
        public static final short NON_PERPENDICULAR_AXIS_2 = 144;

        /**
         * “{0}” is not a scale unit.
         */
        public static final short NON_SCALE_UNIT_1 = 145;

        /**
         * “{0}” is not a time unit.
         */
        public static final short NON_TEMPORAL_UNIT_1 = 146;

        /**
         * Transform is not affine.
         */
        public static final short NOT_AN_AFFINE_TRANSFORM = 147;

        /**
         * Can’t format object of class “{0}” as an angle.
         */
        public static final short NOT_AN_ANGLE_OBJECT_1 = 148;

        /**
         * Value “{0}” is not a valid integer.
         */
        public static final short NOT_AN_INTEGER_1 = 149;

        /**
         * Not a directory: {0}
         */
        public static final short NOT_A_DIRECTORY_1 = 150;

        /**
         * Points dont seem to be distributed on a regular grid.
         */
        public static final short NOT_A_GRID = 151;

        /**
         * Value “{0}” is not a valid real number.
         */
        public static final short NOT_A_NUMBER_1 = 152;

        /**
         * {0} is not a comparable class.
         */
        public static final short NOT_COMPARABLE_CLASS_1 = 153;

        /**
         * Value {0} is not a real, non-null number.
         */
        public static final short NOT_DIFFERENT_THAN_ZERO_1 = 154;

        /**
         * Value ‘{0}’={1} is invalid. Expected a number greater than 0.
         */
        public static final short NOT_GREATER_THAN_ZERO_2 = 155;

        /**
         * Not a 3D coordinate system.
         */
        public static final short NOT_THREE_DIMENSIONAL_CS = 156;

        /**
         * Can’t wrap a {0} dimensional object into a 2 dimensional one.
         */
        public static final short NOT_TWO_DIMENSIONAL_1 = 157;

        /**
         * No authority was defined for code “{0}”. Did you forget "AUTHORITY:NUMBER"?
         */
        public static final short NO_AUTHORITY_1 = 158;

        /**
         * No category for value {0}.
         */
        public static final short NO_CATEGORY_FOR_VALUE_1 = 159;

        /**
         * Transformation doesn’t convergence.
         */
        public static final short NO_CONVERGENCE = 160;

        /**
         * No convergence for points {0} and {1}.
         */
        public static final short NO_CONVERGENCE_2 = 161;

        /**
         * No data source found.
         */
        public static final short NO_DATA_SOURCE = 162;

        /**
         * No foreigner key found in table “{0}”.
         */
        public static final short NO_FOREIGNER_KEY_1 = 163;

        /**
         * No image codec found for the “{0}” format. Available formats are {1}.
         */
        public static final short NO_IMAGE_FORMAT_2 = 164;

        /**
         * No input set.
         */
        public static final short NO_IMAGE_INPUT = 165;

        /**
         * No output set.
         */
        public static final short NO_IMAGE_OUTPUT = 166;

        /**
         * No suitable image reader for this input.
         */
        public static final short NO_IMAGE_READER = 167;

        /**
         * No suitable image writer for this output.
         */
        public static final short NO_IMAGE_WRITER = 168;

        /**
         * No layer has been specified.
         */
        public static final short NO_LAYER_SPECIFIED = 169;

        /**
         * Parameter “{0}” is missing.
         */
        public static final short NO_PARAMETER_1 = 170;

        /**
         * Missing value for parameter “{0}”.
         */
        public static final short NO_PARAMETER_VALUE_1 = 171;

        /**
         * No data series has been specified.
         */
        public static final short NO_SERIES_SPECIFIED = 172;

        /**
         * No source axis match {0}.
         */
        public static final short NO_SOURCE_AXIS_1 = 173;

        /**
         * No attribute named “{0}” has been found.
         */
        public static final short NO_SUCH_ATTRIBUTE_1 = 174;

        /**
         * No attribute named “{0}” has been found in “{1}”.
         */
        public static final short NO_SUCH_ATTRIBUTE_2 = 175;

        /**
         * No object of type ‘{0}’ has been found for code “{1}”.
         */
        public static final short NO_SUCH_AUTHORITY_CODE_2 = 176;

        /**
         * No code “{0}” from authority “{1}” found for object of type ‘{2}’.
         */
        public static final short NO_SUCH_AUTHORITY_CODE_3 = 177;

        /**
         * No ‘{0}’ element.
         */
        public static final short NO_SUCH_ELEMENT_1 = 178;

        /**
         * No element named “{0}” has been found.
         */
        public static final short NO_SUCH_ELEMENT_NAME_1 = 179;

        /**
         * No such “{0}” operation for this processor.
         */
        public static final short NO_SUCH_OPERATION_1 = 180;

        /**
         * No record found in table “{0}” for key “{1}”.
         */
        public static final short NO_SUCH_RECORD_2 = 181;

        /**
         * No “{1}” record found in the “{0}” table.
         */
        public static final short NO_SUCH_RECORD_IN_TABLE_2 = 182;

        /**
         * No two-dimensional transform available for this geometry.
         */
        public static final short NO_TRANSFORM2D_AVAILABLE = 183;

        /**
         * No transformation available from system “{0}” to “{1}”.
         */
        public static final short NO_TRANSFORMATION_PATH_2 = 184;

        /**
         * No transform for classification “{0}”.
         */
        public static final short NO_TRANSFORM_FOR_CLASSIFICATION_1 = 185;

        /**
         * Unit must be specified.
         */
        public static final short NO_UNIT = 186;

        /**
         * Missing WKT definition.
         */
        public static final short NO_WKT_DEFINITION = 187;

        /**
         * Argument ‘{0}’ should not be null.
         */
        public static final short NULL_ARGUMENT_1 = 188;

        /**
         * Attribute “{0}” should not be null.
         */
        public static final short NULL_ATTRIBUTE_1 = 189;

        /**
         * Format #{0} (on {1}) is not defined.
         */
        public static final short NULL_FORMAT_2 = 190;

        /**
         * “{0}” parameter should be not null and of type ‘{1}’.
         */
        public static final short NULL_PARAMETER_2 = 191;

        /**
         * Unexpected null value in record “{0}” for the column “{1}” in table “{2}”.
         */
        public static final short NULL_VALUE_IN_TABLE_3 = 192;

        /**
         * Bad array length: {0}. An even array length was expected.
         */
        public static final short ODD_ARRAY_LENGTH_1 = 193;

        /**
         * Operation “{0}” is already bounds.
         */
        public static final short OPERATION_ALREADY_BOUNDS_1 = 194;

        /**
         * Possible use of “{0}” projection outside its valid area.
         */
        public static final short OUT_OF_PROJECTION_VALID_AREA_1 = 195;

        /**
         * Unparsable string: “{0}”. Please check characters “{1}”.
         */
        public static final short PARSE_EXCEPTION_2 = 196;

        /**
         * Pixels must be square with no flip and no rotation.
         */
        public static final short PIXELS_NOT_SQUARE_OR_ROTATED_IMAGE = 197;

        /**
         * Coordinate ({0}) is outside coverage.
         */
        public static final short POINT_OUTSIDE_COVERAGE_1 = 198;

        /**
         * Point is outside grid
         */
        public static final short POINT_OUTSIDE_GRID = 199;

        /**
         * Point outside hemisphere of projection.
         */
        public static final short POINT_OUTSIDE_HEMISPHERE = 200;

        /**
         * Latitude {0} is too close to a pole.
         */
        public static final short POLE_PROJECTION_1 = 201;

        /**
         * Can’t add point to a closed polygon.
         */
        public static final short POLYGON_CLOSED = 202;

        /**
         * Ranges [{0} … {1}] and [{2} … {3}] overlap.
         */
        public static final short RANGE_OVERLAP_4 = 203;

        /**
         * Recursive call while creating a ‘{0}’ object.
         */
        public static final short RECURSIVE_CALL_1 = 204;

        /**
         * Recursive call while creating a ‘{0}’ object for code “{1}”.
         */
        public static final short RECURSIVE_CALL_2 = 205;

        /**
         * The requested envelope does not intersect the data envelope.
         */
        public static final short REQUESTED_ENVELOPE_DO_NOT_INTERSECT = 206;

        /**
         * RGB value {0} is out of range.
         */
        public static final short RGB_OUT_OF_RANGE_1 = 207;

        /**
         * Execution on a remote machine failed.
         */
        public static final short RMI_FAILURE = 208;

        /**
         * Expected {0}={1} but got {2}.
         */
        public static final short TEST_FAILURE_3 = 209;

        /**
         * The thread doesn’t hold the lock.
         */
        public static final short THREAD_DOESNT_HOLD_LOCK = 210;

        /**
         * Timeout.
         */
        public static final short TIMEOUT = 211;

        /**
         * Tolerance error.
         */
        public static final short TOLERANCE_ERROR = 212;

        /**
         * Expected at least {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final short TOO_FEW_ARGUMENTS_2 = 213;

        /**
         * Expected at most {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final short TOO_MANY_ARGUMENTS_2 = 214;

        /**
         * Too many occurrences of “{0}”. There is already {1} of them.
         */
        public static final short TOO_MANY_OCCURRENCES_2 = 215;

        /**
         * Undefined format.
         */
        public static final short UNDEFINED_FORMAT = 216;

        /**
         * Format “{0}” is undefined.
         */
        public static final short UNDEFINED_FORMAT_1 = 217;

        /**
         * Undefined property.
         */
        public static final short UNDEFINED_PROPERTY = 218;

        /**
         * Property “{0}” is not defined.
         */
        public static final short UNDEFINED_PROPERTY_1 = 219;

        /**
         * Unexpected argument for operation “{0}”.
         */
        public static final short UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_1 = 220;

        /**
         * Unexpected domain for the “{0}” axis. Expected one of {1}.
         */
        public static final short UNEXPECTED_AXIS_DOMAIN_2 = 221;

        /**
         * Unexpected dimension for a “{0}” coordinate system.
         */
        public static final short UNEXPECTED_DIMENSION_FOR_CS_1 = 222;

        /**
         * Unexpected end of string.
         */
        public static final short UNEXPECTED_END_OF_STRING = 223;

        /**
         * Grid header has unexpected length: {0}
         */
        public static final short UNEXPECTED_HEADER_LENGTH_1 = 224;

        /**
         * Image doesn’t have the expected size.
         */
        public static final short UNEXPECTED_IMAGE_SIZE = 225;

        /**
         * Parameter “{0}” was not expected.
         */
        public static final short UNEXPECTED_PARAMETER_1 = 226;

        /**
         * Matrix row {0} has a length of {1}, while {2} was expected.
         */
        public static final short UNEXPECTED_ROW_LENGTH_3 = 227;

        /**
         * {4,choice,0#Forward|1#Inverse} transformation doesn’t produce the expected values. Expected
         * {0} but got {1} (a difference of {2}) at ordinate {3}.
         */
        public static final short UNEXPECTED_TRANSFORM_RESULT_5 = 228;

        /**
         * {0} records have been updated while only one was expected.
         */
        public static final short UNEXPECTED_UPDATES_1 = 229;

        /**
         * Parameter “{0}” has no unit.
         */
        public static final short UNITLESS_PARAMETER_1 = 230;

        /**
         * Authority “{0}” is unknown or doesn’t match the supplied hints. Maybe it is defined in an
         * unreachable JAR file?
         */
        public static final short UNKNOWN_AUTHORITY_1 = 231;

        /**
         * Authority “{0}” is not available. The cause is: {1}
         */
        public static final short UNKNOWN_AUTHORITY_2 = 232;

        /**
         * Unknown axis direction: “{0}”.
         */
        public static final short UNKNOWN_AXIS_DIRECTION_1 = 233;

        /**
         * Unknown command: {0}
         */
        public static final short UNKNOWN_COMMAND_1 = 234;

        /**
         * Unknown enumeration value: {0}.
         */
        public static final short UNKNOWN_ENUM_1 = 235;

        /**
         * File suffix “{0}” is unknown.
         */
        public static final short UNKNOWN_FILE_SUFFIX_1 = 236;

        /**
         * Image format “{0}” is unknown.
         */
        public static final short UNKNOWN_IMAGE_FORMAT_1 = 237;

        /**
         * Interpolation “{0}” is unknown.
         */
        public static final short UNKNOWN_INTERPOLATION_1 = 238;

        /**
         * MIME type “{0}” is unknown.
         */
        public static final short UNKNOWN_MIME_TYPE_1 = 239;

        /**
         * Unknown parameter: {0}
         */
        public static final short UNKNOWN_PARAMETER_1 = 240;

        /**
         * Unknown parameter name: {0}
         */
        public static final short UNKNOWN_PARAMETER_NAME_1 = 241;

        /**
         * Unknown projection type.
         */
        public static final short UNKNOWN_PROJECTION_TYPE = 242;

        /**
         * Type “{0}” is unknown in this context.
         */
        public static final short UNKNOWN_TYPE_1 = 243;

        /**
         * Unit “{0}” is not recognized.
         */
        public static final short UNKNOWN_UNIT_1 = 244;

        /**
         * This affine transform is unmodifiable.
         */
        public static final short UNMODIFIABLE_AFFINE_TRANSFORM = 245;

        /**
         * Unmodifiable geometry.
         */
        public static final short UNMODIFIABLE_GEOMETRY = 246;

        /**
         * Unmodifiable metadata.
         */
        public static final short UNMODIFIABLE_METADATA = 247;

        /**
         * Unmodifiable {0} object.
         */
        public static final short UNMODIFIABLE_OBJECT_1 = 248;

        /**
         * Can’t parse value “{1}” in attribute ‘{0}’.
         */
        public static final short UNPARSABLE_ATTRIBUTE_2 = 249;

        /**
         * Can’t parse “{0}” as a number.
         */
        public static final short UNPARSABLE_NUMBER_1 = 250;

        /**
         * Can’t parse “{0}” because “{1}” is unrecognized.
         */
        public static final short UNPARSABLE_STRING_2 = 251;

        /**
         * Coordinate reference system is unspecified.
         */
        public static final short UNSPECIFIED_CRS = 252;

        /**
         * Unspecified image’s size.
         */
        public static final short UNSPECIFIED_IMAGE_SIZE = 253;

        /**
         * Unspecified coordinates transform.
         */
        public static final short UNSPECIFIED_TRANSFORM = 254;

        /**
         * Coordinate system “{0}” is unsupported.
         */
        public static final short UNSUPPORTED_COORDINATE_SYSTEM_1 = 255;

        /**
         * Coordinate reference system “{0}” is unsupported.
         */
        public static final short UNSUPPORTED_CRS_1 = 256;

        /**
         * Unsupported data type.
         */
        public static final short UNSUPPORTED_DATA_TYPE = 257;

        /**
         * Data type “{0}” is not supported.
         */
        public static final short UNSUPPORTED_DATA_TYPE_1 = 258;

        /**
         * Datum “{0}” is unsupported.
         */
        public static final short UNSUPPORTED_DATUM_1 = 259;

        /**
         * Unsupported file type: {0}
         */
        public static final short UNSUPPORTED_FILE_TYPE_1 = 260;

        /**
         * Unsupported image type.
         */
        public static final short UNSUPPORTED_IMAGE_TYPE = 261;

        /**
         * At most one instance of ‘{0}’ is supported.
         */
        public static final short UNSUPPORTED_MULTI_OCCURRENCE_1 = 262;

        /**
         * Unsupported operation: {0}
         */
        public static final short UNSUPPORTED_OPERATION_1 = 263;

        /**
         * Unsupported transform.
         */
        public static final short UNSUPPORTED_TRANSFORM = 264;

        /**
         * A value is already defined for {0}.
         */
        public static final short VALUE_ALREADY_DEFINED_1 = 265;

        /**
         * Value {0} is out of range [{1} … {2}].
         */
        public static final short VALUE_OUT_OF_BOUNDS_3 = 266;

        /**
         * Value ‘{0}’={1} is out of range [{2} … {3}].
         */
        public static final short VALUE_OUT_OF_BOUNDS_4 = 267;

        /**
         * Numerical value tend toward infinity.
         */
        public static final short VALUE_TEND_TOWARD_INFINITY = 268;

        /**
         * No variable “{0}” found in file “{1}”.
         */
        public static final short VARIABLE_NOT_FOUND_IN_FILE_2 = 269;

        /**
         * A veto has been ignored because it has been applied too late.
         */
        public static final short VETO_TOO_LATE = 270;

        /**
         * Value {1} is outside the domain of coverage “{0}”.
         */
        public static final short ZVALUE_OUTSIDE_COVERAGE_2 = 271;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Errors(final java.net.URL filename) {
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
     *
     * @since 3.05
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
    public static String format(final short  key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2,
                                final Object arg3) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2, arg3);
    }
}
