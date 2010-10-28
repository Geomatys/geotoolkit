/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.metadata.IIOMetadataFormat;

import org.opengis.util.CodeList;
import org.opengis.util.RecordType;

// We use a lot of different metadata interfaces in this class.
// It is a bit too tedious to declare all of them.
import org.opengis.metadata.*;
import org.opengis.metadata.extent.*;
import org.opengis.metadata.spatial.*;
import org.opengis.metadata.quality.*;
import org.opengis.metadata.lineage.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.constraint.*;
import org.opengis.metadata.acquisition.*;
import org.opengis.metadata.maintenance.*;
import org.opengis.metadata.distribution.*;
import org.opengis.metadata.identification.*;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.parameter.*;

import org.opengis.coverage.grid.GridCell;
import org.opengis.coverage.grid.GridPoint;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Point;
import org.opengis.util.InternationalString;
import org.opengis.util.GenericName;

import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.internal.image.io.DataTypes;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.datum.DefaultVerticalDatum;


/**
 * Provides constructor methods for predefined metadata formats. This class provides the methods
 * that created the <a href="SpatialMetadataFormat.html#default-formats">trees documented in the
 * super-class</a>. This class is public in order to allow users to create their own metadata
 * trees derived from the predefined Geotk trees.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07 (derived from 3.05)
 * @module
 */
public class PredefinedMetadataFormat extends SpatialMetadataFormat {
    /*
     * NOTE: It is better to not define any static constants (except String and primitive types)
     *       that may be used by the addTree(...) method, because they may not be initialized at
     *       the time STREAM and IMAGE constants are invoking addTree(...).
     */

    /**
     * Creates an initially empty format. Subclasses shall invoke the various
     * {@code addTree(...)} methods defined in this class or parent class for
     * adding new elements and attributes.
     *
     * @param rootName the name of the root element.
     */
    protected PredefinedMetadataFormat(final String rootName) {
        super(rootName);
    }

    /**
     * Adds the tree structure for <cite>stream</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Stream metadata</cite>" column of the
     * <a href="SpatialMetadataFormat.html#default-formats">class javadoc</a>.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#STREAM
     */
    protected void addTreeForStream(String addToElement) {
        if (addToElement == null) {
            addToElement = getRootName();
        }
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(48);
        /*
         * Metadata excluded because they are redundant with standard API.
         */
        substitution.put(Format.class,                    null);  // Redundant with ImageReaderWriterSpi.
        substitution.put(Locale.class,                    null);  // Specified in ImageReader.getLocale().
        substitution.put(CharacterSet.class,              null);  // Fixed to Unicode in java.lang.String.
        substitution.put(BrowseGraphic.class,             null);  // Redundant with Image I/O Thumbnails.
        substitution.put(SpatialRepresentationType.class, null);  // Fixed to "grid" for Image I/O.
        /*
         * Metadata excluded because we are not interested in (at this time). Their
         * inclusion introduce large sub-trees that would need to be simplified.  We
         * may revisit some of those exclusion in a future version, when we get more
         * experience about what are needed.
         */
        substitution.put(Usage.class,                  null);  // MD_DataIdentification.resourceSpecificUsage
        substitution.put(ResponsibleParty.class,       null);  // MD_DataIdentification.pointOfContact
        substitution.put(Constraints.class,            null);  // MD_DataIdentification.resourceConstraints
        substitution.put(MaintenanceInformation.class, null);  // MD_DataIdentification.resourceMaintenance
        substitution.put(AggregateInformation.class,   null);  // MD_DataIdentification.aggregationInfo
        substitution.put(Plan.class,                   null);  // MI_AcquisitionInformation.acquisitionPlan
        substitution.put(Objective.class,              null);  // MI_AcquisitionInformation.objective
        substitution.put(Operation.class,              null);  // MI_AcquisitionInformation.operation
        substitution.put(Requirement.class,            null);  // MI_AcquisitionInformation.acquisitionRequirement
        substitution.put(Scope.class,                  null);  // DQ_DataQuality.scope
        substitution.put(Lineage.class,                null);  // DQ_DataQuality.lineage
        substitution.put(Result.class,                 null);  // DQ_DataQuality.report.result
        /*
         * Metadata excluded because not yet implemented.
         */
        substitution.put(TemporalExtent.class, null);
        /*
         * Metadata simplification, where elements are replaced by attributes. The simplification
         * is especially important for Citation because they appear in many different places with
         * the same name ("citation"),  while Image I/O does not allow many element nodes to have
         * the same name (this is not strictly forbidden, but the getter methods return information
         * only about the first occurrence of the given name. Note however that having the same name
         * under different element node is not an issue for attributes). In addition, the Citation
         * sub-tree is very large and we don't want to allow the tree to growth that big.
         */
        substitution.put(Citation.class,   String.class);
        substitution.put(Citation[].class, String.class);
        substitution.put(Identifier.class, String.class);
        /*
         * Metadata excluded because they introduce circularity or because
         * they appear more than once (we shall not declare two nodes with
         * the same name in Image I/O). Some will be added by hand later.
         */
        substitution.put(Instrument.class, null);  // MI_AcquisitionInformation.instrument
        /*
         * Collections replaced by singletons, because only one
         * instance is enough for the purpose of stream metadata.
         */
        substitution.put(Extent[].class,           Extent.class);            // MD_DataIdentification.extent
        substitution.put(GeographicExtent[].class, GeographicExtent.class);  // MD_DataIdentification.extent.geographicElement
        substitution.put(VerticalExtent[].class,   VerticalExtent.class);    // MD_DataIdentification.extent.verticalElement
        substitution.put(Resolution[].class,       Resolution.class);        // MD_DataIdentification.spatialResolution
        substitution.put(Platform[].class,         Platform.class);          // MI_AcquisitionInformation.platform
        substitution.put(Element[].class,          Element.class);           // DQ_DataQuality.report
        substitution.put(Date[].class,             Date.class);              // DQ_DataQuality.report.dateTime
        /*
         * Since this set of metadata is about gridded data,
         * replace the generic interfaces by specialized ones.
         */
        substitution.put(Identification.class,        DataIdentification.class);
        substitution.put(SpatialRepresentation.class, GridSpatialRepresentation.class);
        substitution.put(GeographicExtent.class,      GeographicBoundingBox.class);
        /*
         * Build the tree.
         */
        final MetadataStandard standard = MetadataStandard.ISO_19115;
        addTree(standard, DataIdentification.class,     "DiscoveryMetadata",   addToElement, substitution);
        addTree(standard, AcquisitionInformation.class, "AcquisitionMetadata", addToElement, substitution);
        addTree(standard, DataQuality.class,            "QualityMetadata",     addToElement, substitution);
        removeAttribute("EquivalentScale", "doubleValue");
        /*
         * Add by hand a node in the place where it would have been added if we didn't
         * excluded it. We do this addition because Instruments appear in two places,
         * while we want only the occurrence that appear under the "Platform" node.
         */
        substitution.put(Platform.class, null);
        substitution.remove(Identifier.class); // Allow full expansion.
        addTree(standard, Instrument[].class, "Instruments", "Platform", substitution);
        mapName("Instruments", "getCitations", "citation");
    }

    /**
     * Adds the tree structure for <cite>image</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Image metadata</cite>" column of the
     * <a href="SpatialMetadataFormat.html#default-formats">class javadoc</a>.
     * <p>
     * The <cite>Coordinate Reference System</cite> branch is not included by this method.
     * For including CRS information, the {@link #addTreeForCRS(String)} method shall be
     * invoked explicitly.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#IMAGE
     */
    protected void addTreeForImage(String addToElement) {
        if (addToElement == null) {
            addToElement = getRootName();
        }
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(20);
        substitution.put(Citation.class,       String.class);   // MD_ImageDescription.xxxCode
        substitution.put(RecordType.class,     null);           // MD_CoverageDescription.attributeDescription
        substitution.put(RangeDimension.class, Band.class);     // MD_CoverageDescription.dimension
        /*
         * Adds the "ImageDescription" node derived from ISO 19115.
         * The 'fillSampleValues' attribute is a Geotk extension.
         */
        MetadataStandard standard = MetadataStandard.ISO_19115;
        addTree(standard, ImageDescription.class, "ImageDescription", addToElement, substitution);
        addAttribute("Dimension", "validSampleValues", DATATYPE_STRING, false, null);
        addAttribute("Dimension", "fillSampleValues",  DATATYPE_DOUBLE, false, 0, Integer.MAX_VALUE);
        addObjectValue("Dimension", SampleDimension.class, true, null); // Replace Band.class.
        /*
         * Adds the "SpatialRepresentation" node derived from ISO 19115.
         * We omit the information about spatial-temporal axis properties (the Dimension object)
         * because it is redundant with the information provided in the CRS and offset vectors.
         */
        substitution.put(Dimension.class,           null);  // GridSpatialRepresentation.axisDimensionProperties
        substitution.put(Point.class,     double[].class);  // MD_Georectified.centerPoint
        substitution.put(GCP.class,                 null);  // MD_Georectified.checkPoint
        substitution.put(Boolean.TYPE,              null);  // MD_Georectified.checkPointAvailability
        substitution.put(InternationalString.class, null);  // MD_Georectified.various descriptions...
        addTree(standard, Georectified.class, "SpatialRepresentation", addToElement, substitution);
        removeAttribute("SpatialRepresentation", "cornerPoints");
        /*
         * Adds the "RectifiedGridDomain" node derived from ISO 19123.
         */
        substitution.put(String.class,          null); // CV_Grid.axisNames
        substitution.put(GridCell.class,        null); // CV_Grid.cell
        substitution.put(GridPoint.class,       null); // CV_Grid.intersection
        substitution.put(GridEnvelope.class,    null); // CV_Grid.extent (will be added later)
        substitution.put(GridCoordinates.class, int[].class);    // CV_GridEnvelope.low/high
        substitution.put(DirectPosition.class,  double[].class); // CV_RectifiedGrid.origin
        standard = MetadataStandard.ISO_19123;
        addIncompleteTree(standard, RectifiedGrid.class, "RectifiedGridDomain", addToElement, substitution);
        /*
         * Following is part of ISO 19123 and "GML in JPEG 2000" specifications,
         * but under different names. We use the "GML in JPEG 2000" names.
         */
        addTree(standard, GridEnvelope.class, "Limits", "RectifiedGridDomain", substitution);
        removeAttribute("Limits",              "dimension"); // Redundant with the one in RectifiedGridDomain.
        removeAttribute("RectifiedGridDomain", "dimension"); // Redundant with the one in SpatialRepresentation.
        /*
         * There is no public API for this functionality at this time...
         */
        mapName("RectifiedGridDomain", "getExtent", "Limits");
    }

    /**
     * Adds the tree structure for a <cite>Coordinate Reference System</cite> object.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     */
    protected void addTreeForCRS(String addToElement) {
        if (addToElement == null) {
            addToElement = getRootName();
        }
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(20);
        /*
         * Metadata excluded in order to keep the CRS node relatively simple.
         */
        substitution.put(ReferenceIdentifier.class, null);  // IO_IdentifiedObject.identifiers
        substitution.put(GenericName.class,         null);  // IO_IdentifiedObject.alias
        substitution.put(String.class,              null);  // IO_IdentifiedObject.toWKT
        substitution.put(Extent.class,              null);  // RS_ReferenceSystem.domainOfValidity
        substitution.put(InternationalString.class, null);  // SC_CRS.scope
        substitution.put(Date.class,                null);  // CD_Datum.realizationEpoch
        substitution.put(Boolean.TYPE,              null);  // CD_Ellipsoid.isIvfDefinitive
        /*
         * Assume that the CRS will be geodetic CRS.
         * After the tree has been added, we will generalize the declared types.
         */
        substitution.put(Datum.class, GeodeticDatum.class);
        MetadataStandard standard = MetadataStandard.ISO_19111;
        final Set<Class<?>> incomplete = new HashSet<Class<?>>(4);
        incomplete.add(CoordinateReferenceSystem.class);
        incomplete.add(CoordinateSystem.class);
        incomplete.add(GeodeticDatum.class);
        addTree(standard, SingleCRS.class, "CoordinateReferenceSystem", addToElement, false, substitution, incomplete);
        addObjectValue("CoordinateReferenceSystem", CoordinateReferenceSystem.class, true, null);
        addObjectValue("Datum", Datum.class, true, null);
        /*
         * We need to add the axes explicitly, because the method signature is
         * CoordinateSystem.getAxis(int) which is not recognized by our reflection API.
         */
        addTree(standard, CoordinateSystemAxis[].class, "Axes", "CoordinateSystem", true, substitution);
        /*
         * Add conversion parameters. Note that the operation method will be replaced by a String
         * later. We can not replace it by a String now since Strings are excluded because of the
         * toWKT() method.
         */
        substitution.put(MathTransform.class,             null);
        substitution.put(OperationMethod.class,           null);
        substitution.put(PositionalAccuracy.class,        null);
        substitution.put(CoordinateReferenceSystem.class, null);
        substitution.put(ParameterValueGroup.class,       null);
        addIncompleteTree(standard, Conversion.class, "Conversion", "CoordinateReferenceSystem", substitution);
        addAttribute("Conversion", "method", DATATYPE_STRING, true, null);
        /*
         * Adds the parameter manually, because they are not in the ISO 19111 package
         * (so the MetadataStandard.ISO_19111 doesn't known them) and because we want
         * a simple (name, value) pair, instead than the parameter descriptor.
         */
        addElement    ("Parameters",     "Conversion", 0, Integer.MAX_VALUE);
        addElement    ("ParameterValue", "Parameters", CHILD_POLICY_EMPTY);
        addAttribute  ("ParameterValue", "name",       DATATYPE_STRING, true, null);
        addAttribute  ("ParameterValue", "value",      DATATYPE_DOUBLE, true, null);
        addObjectValue("Parameters", ParameterValueGroup.class, false, null);
        addObjectValue("ParameterValue",  ParameterValue.class, false, null);
    }

    /**
     * Returns the code list identifiers. This is a hook for overriding by subclasses.
     */
    @Override
    final String[] getCodeList(final Class<CodeList<?>> codeType) {
        String[] identifiers = super.getCodeList(codeType);
        if (AxisDirection.class.equals(codeType)) {
            for (int i=0; i<identifiers.length; i++) {
                // Replace "CS_AxisOrientationEnum.CS_AO_Other" by something more readeable.
                if (identifiers[i].endsWith("Other")) {
                    identifiers[i] = "other";
                }
            }
        }
        return identifiers;
    }

    /**
     * Adds the {@code "name"} and {@code "type"} attributes to referencing objects.
     */
    @Override
    final void addCustomAttributes(final String elementName, final Class<?> type) {
        if (IdentifiedObject.class.isAssignableFrom(type)) {
            addAttribute(elementName, "name", DATATYPE_STRING, true, null);
        }
        if (CoordinateSystemAxis.class.isAssignableFrom(type)) {
            addAttribute(elementName, "axisAbbrev", DATATYPE_STRING, true, null);
        }
        final List<String> types;
        if (CoordinateReferenceSystem.class.isAssignableFrom(type)) {
            types = DataTypes.CRS_TYPES;
        } else if (CoordinateSystem.class.isAssignableFrom(type)) {
            types = DataTypes.CS_TYPES;
        } else if (Datum.class.isAssignableFrom(type)) {
            types = DataTypes.DATUM_TYPES;
        } else {
            return;
        }
        addAttribute(elementName, "type", DATATYPE_STRING, true, null, types);
    }

    /**
     * Returns the default value for an object reference of the given type. This method is
     * invoked automatically by the {@code addTree} methods for determining the value of the
     * {@code defaultValue} argument in the call to the {@link #addObjectValue(String, Class,
     * boolean, Object) addObjectValue} method.
     * <p>
     * This method is also invoked by {@link ReferencingBuilder#getDefault(Class)}, which does not
     * rely on {@link IIOMetadataFormat#getObjectDefaultValue(String)} because the default value of
     * some referencing objects depends on the type of the enclosing element. For example the default
     * coordinate system shall be ellipsoidal for a geographic CRS and cartesian for a projected
     * CRS.
     * <p>
     * The default implementation returns a value determined from the table below.
     * Subclasses can override this method for providing different default values.
     * <p>
     * <table border="1" cellspacing="0">
     * <tr bgcolor="lightblue">
     *   <th>Type</th>
     *   <th>Default value</th>
     * </tr><tr>
     *   <td>&nbsp;{@link PrimeMeridian}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultPrimeMeridian#GREENWICH}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link Ellipsoid}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoid#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeodeticDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeodeticDatum#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link VerticalDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultVerticalDatum#GEOIDAL}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EngineeringDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEngineeringDatum#UNKNOWN}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EllipsoidalCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoidalCS#GEODETIC_2D}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link CartesianCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultCartesianCS#GENERIC_2D}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeographicCRS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeographicCRS#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeocentricCRS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeocentricCRS#CARTESIAN}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;All other type&nbsp;</td>
     *   <td>&nbsp;{@code null}&nbsp;</td>
     * </tr>
     * </table>
     *
     * @param  <T> The compile-time type of {@code classType}.
     * @param  type The class type of the object for which to get a default value.
     * @return The default value for an object of the given type, or {@code null} if none.
     *
     * @see ReferencingBuilder#getDefault(Class)
     * @see #getObjectDefaultValue(String)
     *
     * @since 3.08
     */
    @Override
    protected <T> T getDefaultValue(final Class<T> type) {
        final IdentifiedObject object;
        if (PrimeMeridian.class.isAssignableFrom(type)) {
            object = DefaultPrimeMeridian.GREENWICH;
        } else if (Ellipsoid.class.isAssignableFrom(type)) {
            object = DefaultEllipsoid.WGS84;
        } else if (GeodeticDatum.class.isAssignableFrom(type)) {
            object = DefaultGeodeticDatum.WGS84;
        } else if (VerticalDatum.class.isAssignableFrom(type)) {
            object = DefaultVerticalDatum.GEOIDAL;
        } else if (EngineeringDatum.class.isAssignableFrom(type)) {
            object = DefaultEngineeringDatum.UNKNOWN;
        } else if (EllipsoidalCS.class.isAssignableFrom(type)) {
            object = DefaultEllipsoidalCS.GEODETIC_2D;
        } else if (CartesianCS.class.isAssignableFrom(type)) {
            object = DefaultCartesianCS.GENERIC_2D;
        } else if (GeographicCRS.class.isAssignableFrom(type)) {
            object = DefaultGeographicCRS.WGS84;
        } else if (GeocentricCRS.class.isAssignableFrom(type)) {
            object = DefaultGeocentricCRS.CARTESIAN;
        } else {
            object = null;
        }
        return type.cast(object);
    }
}
