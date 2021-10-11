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
         * Value “{0}” is ambiguous since it associated to the following possibilities:
         */
        public static final short AmbiguousValue_1 = 1;

        /**
         * Azimuth {0} is out of range (±180°).
         */
        public static final short AzimuthOutOfRange_1 = 2;

        /**
         * Bursa-Wolf parameters required.
         */
        public static final short BursaWolfParametersRequired = 3;

        /**
         * The operation has been canceled.
         */
        public static final short CanceledOperation = 4;

        /**
         * Can’t compute derivative.
         */
        public static final short CantComputeDerivative = 5;

        /**
         * Failed to connect to the {0} database.
         */
        public static final short CantConnectDatabase_1 = 6;

        /**
         * Can’t convert value from type ‘{0}’.
         */
        public static final short CantConvertFromType_1 = 7;

        /**
         * Can’t convert from type ‘{0}’ to type ‘{1}’.
         */
        public static final short CantConvertFromType_2 = 8;

        /**
         * Can’t create directory “{0}”.
         */
        public static final short CantCreateDirectory_1 = 9;

        /**
         * Can’t create a factory of type ‘{0}’.
         */
        public static final short CantCreateFactoryForType_1 = 10;

        /**
         * Can’t delete file “{0}”.
         */
        public static final short CantDeleteFile_1 = 11;

        /**
         * Can’t evaluate a value for coordinate ({0}).
         */
        public static final short CantEvaluateForCoordinate_1 = 12;

        /**
         * Failed to get the data source for name “{0}”.
         */
        public static final short CantGetDatasource_1 = 13;

        /**
         * Can’t read the “{1}” record in the “{0}” table.
         */
        public static final short CantReadDatabaseRecord_2 = 14;

        /**
         * Can’t read the “{1}” column for the “{2}” record in the “{0}” table.
         */
        public static final short CantReadDatabaseRecord_3 = 15;

        /**
         * Can’t read the “{0}” table.
         */
        public static final short CantReadDatabaseTable_1 = 16;

        /**
         * Can’t read the “{1}” column in the “{0}” table.
         */
        public static final short CantReadDatabaseTable_2 = 17;

        /**
         * Can’t read file “{0}”.
         */
        public static final short CantReadFile_1 = 18;

        /**
         * Can’t reduce “{0}” to a two-dimensional coordinate system.
         */
        public static final short CantReduceToTwoDimensions_1 = 19;

        /**
         * Can’t reproject grid coverage “{0}”.
         */
        public static final short CantReprojectCoverage_1 = 20;

        /**
         * Can’t separate CRS “{0}”.
         */
        public static final short CantSeparateCrs_1 = 21;

        /**
         * Can’t set a value for parameter “{0}”.
         */
        public static final short CantSetParameterValue_1 = 22;

        /**
         * Can’t transform envelope.
         */
        public static final short CantTransformEnvelope = 23;

        /**
         * Can’t write file “{0}”.
         */
        public static final short CantWriteFile_1 = 24;

        /**
         * Axis {0} and {1} are colinear.
         */
        public static final short ColinearAxis_2 = 25;

        /**
         * Database failure while creating a ‘{0}’ object for code “{1}”.
         */
        public static final short DatabaseFailure_2 = 26;

        /**
         * Failed to update the database.
         */
        public static final short DatabaseUpdateFailure = 27;

        /**
         * Date {0} is outside the range of available data.
         */
        public static final short DateOutsideCoverage_1 = 28;

        /**
         * The destination has not been set.
         */
        public static final short DestinationNotSet = 29;

        /**
         * The direction has not been set.
         */
        public static final short DirectionNotSet = 30;

        /**
         * The factory has been disposed.
         */
        public static final short DisposedFactory = 31;

        /**
         * The distance {0} is out of range ({1} to {2} {3})
         */
        public static final short DistanceOutOfRange_4 = 32;

        /**
         * Dropped the “{0}” foreigner key constraint.
         */
        public static final short DroppedForeignerKey_1 = 33;

        /**
         * Name or alias for parameter “{0}” at index {1} conflict with name “{2}” at index {3}.
         */
        public static final short DuplicatedParameterName_4 = 34;

        /**
         * The “{0}” record is defined more than once.
         */
        public static final short DuplicatedRecord_1 = 35;

        /**
         * The “{0}” value is specified more than once.
         */
        public static final short DuplicatedValue_1 = 36;

        /**
         * Duplicated values for key “{0}”.
         */
        public static final short DuplicatedValuesForKey_1 = 37;

        /**
         * The array must contains at least one element.
         */
        public static final short EmptyArray = 38;

        /**
         * Envelope must be at least two-dimensional and non-empty.
         */
        public static final short EmptyEnvelope2d = 39;

        /**
         * Empty or invalid rectangle: {0}
         */
        public static final short EmptyRectangle_1 = 40;

        /**
         * Premature end of data file.
         */
        public static final short EndOfDataFile = 41;

        /**
         * Expected one of {0}.
         */
        public static final short ExpectedOneOf_1 = 42;

        /**
         * No factory of kind “{0}” found.
         */
        public static final short FactoryNotFound_1 = 43;

        /**
         * File “{0}” already exists.
         */
        public static final short FileAlreadyExists_1 = 44;

        /**
         * File “{0}” does not exist or is unreadable.
         */
        public static final short FileDoesNotExist_1 = 45;

        /**
         * File has too few data.
         */
        public static final short FileHasTooFewData = 46;

        /**
         * File has too many data.
         */
        public static final short FileHasTooManyData = 47;

        /**
         * Attribute “{0}” is not allowed for an object of type ‘{1}’.
         */
        public static final short ForbiddenAttribute_2 = 48;

        /**
         * Geotoolkit.org extension required for “{0}” operation.
         */
        public static final short GeotoolkitExtensionRequired_1 = 49;

        /**
         * Latitude and Longitude grid locations are not equal
         */
        public static final short GridLocationsUnequal = 50;

        /**
         * Argument ‘{0}’ can not be an instance of ‘{1}’. Expected an instance of ‘{2}’ or derived
         * type.
         */
        public static final short IllegalArgumentClass_3 = 51;

        /**
         * Illegal value for argument ‘{0}’.
         */
        public static final short IllegalArgument_1 = 52;

        /**
         * Illegal argument: ‘{0}’={1}
         */
        public static final short IllegalArgument_2 = 53;

        /**
         * Band number {0} is not valid.
         */
        public static final short IllegalBandNumber_1 = 54;

        /**
         * Class ‘{0}’ is illegal. It must be ‘{1}’ or a derived class.
         */
        public static final short IllegalClass_2 = 55;

        /**
         * Illegal coordinate reference system.
         */
        public static final short IllegalCoordinateReferenceSystem = 56;

        /**
         * Coordinate system of type ‘{0}’ are incompatible with CRS of type ‘{1}’.
         */
        public static final short IllegalCoordinateSystemForCrs_2 = 57;

        /**
         * Illegal coordinate: {0}
         */
        public static final short IllegalCoordinate_1 = 58;

        /**
         * Coordinate system can’t have {0} dimensions.
         */
        public static final short IllegalCsDimension_1 = 59;

        /**
         * Illegal descriptor for parameter “{0}”.
         */
        public static final short IllegalDescriptorForParameter_1 = 60;

        /**
         * Bad entry.
         */
        public static final short IllegalEntry = 61;

        /**
         * Illegal grid envelope [{1} … {2}] for dimension {0}.
         */
        public static final short IllegalGridEnvelope_3 = 62;

        /**
         * “{0}” is not a valid identifier.
         */
        public static final short IllegalIdentifier_1 = 63;

        /**
         * Illegal instruction “{0}”.
         */
        public static final short IllegalInstruction_1 = 64;

        /**
         * Illegal key: {0}
         */
        public static final short IllegalKey_1 = 65;

        /**
         * Illegal data at line {1} in file “{0}”.
         */
        public static final short IllegalLineInFile_2 = 66;

        /**
         * Expected a source with a single image, or tiles having the same resolution.
         */
        public static final short IllegalMosaicInput = 67;

        /**
         * Parameter “{0}” occurs {1} time, while the expected range of occurrences was [{2} … {3}].
         */
        public static final short IllegalOccursForParameter_4 = 68;

        /**
         * This operation can’t be applied to values of class ‘{0}’.
         */
        public static final short IllegalOperationForValueClass_1 = 69;

        /**
         * Parameter ‘{0}’ can’t be of type ‘{1}’.
         */
        public static final short IllegalParameterType_2 = 70;

        /**
         * Parameter ‘{0}’ can’t have value “{1}”.
         */
        public static final short IllegalParameterValue_2 = 71;

        /**
         * Range [{0} … {1}] is not valid.
         */
        public static final short IllegalRange_2 = 72;

        /**
         * Illegal transform of type ‘{0}’.
         */
        public static final short IllegalTransformForType_1 = 73;

        /**
         * Error in “{0}”:
         */
        public static final short In_1 = 74;

        /**
         * Incompatible coordinate system type.
         */
        public static final short IncompatibleCoordinateSystemType = 75;

        /**
         * Incompatible grid geometries.
         */
        public static final short IncompatibleGridGeometry = 76;

        /**
         * Incompatible unit: {0}
         */
        public static final short IncompatibleUnit_1 = 77;

        /**
         * Direction “{1}” is inconsistent with axis “{0}”.
         */
        public static final short InconsistentAxisOrientation_2 = 78;

        /**
         * Inconsistent domain between “{0}” and “{1}”.
         */
        public static final short InconsistentDomain_2 = 79;

        /**
         * Inconsistent value.
         */
        public static final short InconsistentValue = 80;

        /**
         * Index {0} is out of bounds.
         */
        public static final short IndexOutOfBounds_1 = 81;

        /**
         * Coefficient {0}={1} can’t be NaN or infinity.
         */
        public static final short InfiniteCoefficient_2 = 82;

        /**
         * {0} value is infinite.
         */
        public static final short InfiniteValue_1 = 83;

        /**
         * Inseparable transform.
         */
        public static final short InseparableTransform = 84;

        /**
         * Latitude {0} is out of range (±90°).
         */
        public static final short LatitudeOutOfRange_1 = 85;

        /**
         * Latitudes {0} and {1} are opposite.
         */
        public static final short LatitudesAreOpposite_2 = 86;

        /**
         * The line contains {0} columns while only {1} was expected. Characters “{2}” seem to be
         * extra.
         */
        public static final short LineTooLong_3 = 87;

        /**
         * The line contains only {0} columns while {1} was expected.
         */
        public static final short LineTooShort_2 = 88;

        /**
         * Longitude {0} is out of range (±180°).
         */
        public static final short LongitudeOutOfRange_1 = 89;

        /**
         * All rows doesn’t have the same length.
         */
        public static final short MatrixNotRegular = 90;

        /**
         * Mismatched array length.
         */
        public static final short MismatchedArrayLength = 91;

        /**
         * Array length of parameters ‘{0}’ and ‘{1}’ do not match.
         */
        public static final short MismatchedArrayLength_2 = 92;

        /**
         * The coordinate reference system must be the same for all objects.
         */
        public static final short MismatchedCoordinateReferenceSystem = 93;

        /**
         * Mismatched object dimension: {0}D and {1}D.
         */
        public static final short MismatchedDimension_2 = 94;

        /**
         * Argument ‘{0}’ has {1} dimensions, while {2} was expected.
         */
        public static final short MismatchedDimension_3 = 95;

        /**
         * The dimension of the “{0}” image is ({1}×{2}) pixels, while the expected dimension was
         * ({3}×{4}) pixels.
         */
        public static final short MismatchedImageSize_5 = 96;

        /**
         * The number of image bands ({0}) differs from the number of supplied ‘{2}’ objects ({1}).
         */
        public static final short MismatchedNumberOfBands_3 = 97;

        /**
         * Character ‘{0}’ was expected.
         */
        public static final short MissingCharacter_1 = 98;

        /**
         * This operation requires the “{0}” module.
         */
        public static final short MissingModule_1 = 99;

        /**
         * Geophysics categories mixed with non-geophysics ones.
         */
        public static final short MixedCategories = 100;

        /**
         * Column number for “{0}” ({1}) can’t be negative.
         */
        public static final short NegativeColumn_2 = 101;

        /**
         * No authority was defined for code “{0}”. Did you forget "AUTHORITY:NUMBER"?
         */
        public static final short NoAuthority_1 = 102;

        /**
         * No category for value {0}.
         */
        public static final short NoCategoryForValue_1 = 103;

        /**
         * Transformation doesn’t convergence.
         */
        public static final short NoConvergence = 104;

        /**
         * No convergence for points {0} and {1}.
         */
        public static final short NoConvergence_2 = 105;

        /**
         * No data source found.
         */
        public static final short NoDataSource = 106;

        /**
         * No foreigner key found in table “{0}”.
         */
        public static final short NoForeignerKey_1 = 107;

        /**
         * No image codec found for the “{0}” format. Available formats are {1}.
         */
        public static final short NoImageFormat_2 = 108;

        /**
         * No input set.
         */
        public static final short NoImageInput = 109;

        /**
         * No output set.
         */
        public static final short NoImageOutput = 110;

        /**
         * No suitable image reader for this input.
         */
        public static final short NoImageReader = 111;

        /**
         * No suitable image writer for this output.
         */
        public static final short NoImageWriter = 112;

        /**
         * No layer has been specified.
         */
        public static final short NoLayerSpecified = 113;

        /**
         * Missing value for parameter “{0}”.
         */
        public static final short NoParameterValue_1 = 114;

        /**
         * Parameter “{0}” is missing.
         */
        public static final short NoParameter_1 = 115;

        /**
         * No data series has been specified.
         */
        public static final short NoSeriesSpecified = 116;

        /**
         * No source axis match {0}.
         */
        public static final short NoSourceAxis_1 = 117;

        /**
         * No attribute named “{0}” has been found.
         */
        public static final short NoSuchAttribute_1 = 118;

        /**
         * No object of type ‘{0}’ has been found for code “{1}”.
         */
        public static final short NoSuchAuthorityCode_2 = 119;

        /**
         * No code “{0}” from authority “{1}” found for object of type ‘{2}’.
         */
        public static final short NoSuchAuthorityCode_3 = 120;

        /**
         * No element named “{0}” has been found.
         */
        public static final short NoSuchElementName_1 = 121;

        /**
         * No ‘{0}’ element.
         */
        public static final short NoSuchElement_1 = 122;

        /**
         * No such “{0}” operation for this processor.
         */
        public static final short NoSuchOperation_1 = 123;

        /**
         * No “{1}” record found in the “{0}” table.
         */
        public static final short NoSuchRecordInTable_2 = 124;

        /**
         * No record found in table “{0}” for key “{1}”.
         */
        public static final short NoSuchRecord_2 = 125;

        /**
         * No two-dimensional transform available for this geometry.
         */
        public static final short NoTransform2dAvailable = 126;

        /**
         * No transformation available from system “{0}” to “{1}”.
         */
        public static final short NoTransformationPath_2 = 127;

        /**
         * Unit must be specified.
         */
        public static final short NoUnit = 128;

        /**
         * Missing WKT definition.
         */
        public static final short NoWktDefinition = 129;

        /**
         * The grid to coordinate system transform must be affine.
         */
        public static final short NonAffineTransform = 130;

        /**
         * Not an angular unit: “{0}”.
         */
        public static final short NonAngularUnit_1 = 131;

        /**
         * Bands {0} and {1} are not consecutive.
         */
        public static final short NonConsecutiveBands_2 = 132;

        /**
         * Unmatched parenthesis in “{0}”: missing ‘{1}’.
         */
        public static final short NonEquilibratedParenthesis_2 = 133;

        /**
         * Some categories use non-integer sample values.
         */
        public static final short NonIntegerCategory = 134;

        /**
         * Relation is not linear.
         */
        public static final short NonLinearRelation = 135;

        /**
         * “{0}” is not a linear unit.
         */
        public static final short NonLinearUnit_1 = 136;

        /**
         * “{0}” is not a scale unit.
         */
        public static final short NonScaleUnit_1 = 137;

        /**
         * “{0}” is not a time unit.
         */
        public static final short NonTemporalUnit_1 = 138;

        /**
         * Transform is not invertible.
         */
        public static final short NoninvertibleTransform = 139;

        /**
         * Not a directory: {0}
         */
        public static final short NotADirectory_1 = 140;

        /**
         * Points dont seem to be distributed on a regular grid.
         */
        public static final short NotAGrid = 141;

        /**
         * Transform is not affine.
         */
        public static final short NotAnAffineTransform = 142;

        /**
         * Value “{0}” is not a valid integer.
         */
        public static final short NotAnInteger_1 = 143;

        /**
         * Can’t wrap a {0} dimensional object into a 2 dimensional one.
         */
        public static final short NotTwoDimensional_1 = 144;

        /**
         * Argument ‘{0}’ should not be null.
         */
        public static final short NullArgument_1 = 145;

        /**
         * Format #{0} (on {1}) is not defined.
         */
        public static final short NullFormat_2 = 146;

        /**
         * Unexpected null value in record “{0}” for the column “{1}” in table “{2}”.
         */
        public static final short NullValueInTable_3 = 147;

        /**
         * Bad array length: {0}. An even array length was expected.
         */
        public static final short OddArrayLength_1 = 148;

        /**
         * Operation “{0}” is already bounds.
         */
        public static final short OperationAlreadyBounds_1 = 149;

        /**
         * Unparsable string: “{0}”. Please check characters “{1}”.
         */
        public static final short ParseException_2 = 150;

        /**
         * Pixels must be square with no flip and no rotation.
         */
        public static final short PixelsNotSquareOrRotatedImage = 151;

        /**
         * Coordinate ({0}) is outside coverage.
         */
        public static final short PointOutsideCoverage_1 = 152;

        /**
         * Point outside hemisphere of projection.
         */
        public static final short PointOutsideHemisphere = 153;

        /**
         * Ranges [{0} … {1}] and [{2} … {3}] overlap.
         */
        public static final short RangeOverlap_4 = 154;

        /**
         * Recursive call while creating a ‘{0}’ object.
         */
        public static final short RecursiveCall_1 = 155;

        /**
         * Recursive call while creating a ‘{0}’ object for code “{1}”.
         */
        public static final short RecursiveCall_2 = 156;

        /**
         * The requested envelope does not intersect the data envelope.
         */
        public static final short RequestedEnvelopeDoNotIntersect = 157;

        /**
         * RGB value {0} is out of range.
         */
        public static final short RgbOutOfRange_1 = 158;

        /**
         * Expected {0}={1} but got {2}.
         */
        public static final short TestFailure_3 = 159;

        /**
         * The thread doesn’t hold the lock.
         */
        public static final short ThreadDoesntHoldLock = 160;

        /**
         * Timeout.
         */
        public static final short Timeout = 161;

        /**
         * Tolerance error.
         */
        public static final short ToleranceError = 162;

        /**
         * Expected at least {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final short TooFewArguments_2 = 163;

        /**
         * Expected at most {0} argument{0,choice,1#|2#s}, but got {1}.
         */
        public static final short TooManyArguments_2 = 164;

        /**
         * Too many occurrences of “{0}”. There is already {1} of them.
         */
        public static final short TooManyOccurrences_2 = 165;

        /**
         * Undefined format.
         */
        public static final short UndefinedFormat = 166;

        /**
         * Format “{0}” is undefined.
         */
        public static final short UndefinedFormat_1 = 167;

        /**
         * Undefined property.
         */
        public static final short UndefinedProperty = 168;

        /**
         * Property “{0}” is not defined.
         */
        public static final short UndefinedProperty_1 = 169;

        /**
         * Unexpected argument for operation “{0}”.
         */
        public static final short UnexpectedArgumentForInstruction_1 = 170;

        /**
         * Unexpected domain for the “{0}” axis. Expected one of {1}.
         */
        public static final short UnexpectedAxisDomain_2 = 171;

        /**
         * Unexpected dimension for a “{0}” coordinate system.
         */
        public static final short UnexpectedDimensionForCs_1 = 172;

        /**
         * Unexpected end of string.
         */
        public static final short UnexpectedEndOfString = 173;

        /**
         * Grid header has unexpected length: {0}
         */
        public static final short UnexpectedHeaderLength_1 = 174;

        /**
         * Image doesn’t have the expected size.
         */
        public static final short UnexpectedImageSize = 175;

        /**
         * Parameter “{0}” was not expected.
         */
        public static final short UnexpectedParameter_1 = 176;

        /**
         * Matrix row {0} has a length of {1}, while {2} was expected.
         */
        public static final short UnexpectedRowLength_3 = 177;

        /**
         * {4,choice,0#Forward|1#Inverse} transformation doesn’t produce the expected values. Expected
         * {0} but got {1} (a difference of {2}) at ordinate {3}.
         */
        public static final short UnexpectedTransformResult_5 = 178;

        /**
         * {0} records have been updated while only one was expected.
         */
        public static final short UnexpectedUpdates_1 = 179;

        /**
         * Parameter “{0}” has no unit.
         */
        public static final short UnitlessParameter_1 = 180;

        /**
         * Authority “{0}” is unknown or doesn’t match the supplied hints. Maybe it is defined in an
         * unreachable JAR file?
         */
        public static final short UnknownAuthority_1 = 181;

        /**
         * Authority “{0}” is not available. The cause is: {1}
         */
        public static final short UnknownAuthority_2 = 182;

        /**
         * Unknown command: {0}
         */
        public static final short UnknownCommand_1 = 183;

        /**
         * File suffix “{0}” is unknown.
         */
        public static final short UnknownFileSuffix_1 = 184;

        /**
         * Image format “{0}” is unknown.
         */
        public static final short UnknownImageFormat_1 = 185;

        /**
         * Interpolation “{0}” is unknown.
         */
        public static final short UnknownInterpolation_1 = 186;

        /**
         * MIME type “{0}” is unknown.
         */
        public static final short UnknownMimeType_1 = 187;

        /**
         * Unknown parameter name: {0}
         */
        public static final short UnknownParameterName_1 = 188;

        /**
         * Unknown parameter: {0}
         */
        public static final short UnknownParameter_1 = 189;

        /**
         * Unknown projection type.
         */
        public static final short UnknownProjectionType = 190;

        /**
         * Type “{0}” is unknown in this context.
         */
        public static final short UnknownType_1 = 191;

        /**
         * Unit “{0}” is not recognized.
         */
        public static final short UnknownUnit_1 = 192;

        /**
         * Unmodifiable metadata.
         */
        public static final short UnmodifiableMetadata = 193;

        /**
         * Unmodifiable {0} object.
         */
        public static final short UnmodifiableObject_1 = 194;

        /**
         * Can’t parse value “{1}” in attribute ‘{0}’.
         */
        public static final short UnparsableAttribute_2 = 195;

        /**
         * Can’t parse “{0}” as a number.
         */
        public static final short UnparsableNumber_1 = 196;

        /**
         * Can’t parse “{0}” because “{1}” is unrecognized.
         */
        public static final short UnparsableString_2 = 197;

        /**
         * Coordinate reference system is unspecified.
         */
        public static final short UnspecifiedCrs = 198;

        /**
         * Unspecified image’s size.
         */
        public static final short UnspecifiedImageSize = 199;

        /**
         * Unspecified coordinates transform.
         */
        public static final short UnspecifiedTransform = 200;

        /**
         * Coordinate system “{0}” is unsupported.
         */
        public static final short UnsupportedCoordinateSystem_1 = 201;

        /**
         * Coordinate reference system “{0}” is unsupported.
         */
        public static final short UnsupportedCrs_1 = 202;

        /**
         * Unsupported data type.
         */
        public static final short UnsupportedDataType = 203;

        /**
         * Data type “{0}” is not supported.
         */
        public static final short UnsupportedDataType_1 = 204;

        /**
         * Datum “{0}” is unsupported.
         */
        public static final short UnsupportedDatum_1 = 205;

        /**
         * Unsupported file type: {0}
         */
        public static final short UnsupportedFileType_1 = 206;

        /**
         * Unsupported image type.
         */
        public static final short UnsupportedImageType = 207;

        /**
         * At most one instance of ‘{0}’ is supported.
         */
        public static final short UnsupportedMultiOccurrence_1 = 208;

        /**
         * Unsupported operation: {0}
         */
        public static final short UnsupportedOperation_1 = 209;

        /**
         * Unsupported transform.
         */
        public static final short UnsupportedTransform = 210;

        /**
         * A value is already defined for {0}.
         */
        public static final short ValueAlreadyDefined_1 = 211;

        /**
         * Value {0} is out of range [{1} … {2}].
         */
        public static final short ValueOutOfBounds_3 = 212;

        /**
         * Value ‘{0}’={1} is out of range [{2} … {3}].
         */
        public static final short ValueOutOfBounds_4 = 213;

        /**
         * Numerical value tend toward infinity.
         */
        public static final short ValueTendTowardInfinity = 214;

        /**
         * No variable “{0}” found in file “{1}”.
         */
        public static final short VariableNotFoundInFile_2 = 215;

        /**
         * A veto has been ignored because it has been applied too late.
         */
        public static final short VetoTooLate = 216;

        /**
         * Value {1} is outside the domain of coverage “{0}”.
         */
        public static final short ZvalueOutsideCoverage_2 = 217;
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
