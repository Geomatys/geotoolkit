/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.lang.reflect.Modifier;
import org.opengis.util.CodeList;
import org.opengis.annotation.UML;
import org.opengis.annotation.Specification;
import org.geotoolkit.test.xml.AnnotationsTestBase;
import org.geotoolkit.xml.Namespaces;

import static org.junit.Assert.*;


/**
 * Compares JAXB annotations with the UML ones.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.04
 */
public final strictfp class MetadataAnnotationsTest extends AnnotationsTestBase {
    /**
     * The list of Metadata code list or interfaces to test,
     * in alphabetical order.
     */
    private static final Class<?>[] TYPES = {
        org.opengis.metadata.ApplicationSchemaInformation.class,
        org.opengis.metadata.Datatype.class,
        org.opengis.metadata.ExtendedElementInformation.class,
        org.opengis.metadata.FeatureTypeList.class,
        org.opengis.metadata.Identifier.class,
        org.opengis.metadata.Metadata.class,
        org.opengis.metadata.MetadataExtensionInformation.class,
//      org.opengis.metadata.Obligation.class, // CodeList excluded because it doesn't use the usual kind of adapter.
        org.opengis.metadata.PortrayalCatalogueReference.class,
        org.opengis.metadata.acquisition.AcquisitionInformation.class,
        org.opengis.metadata.acquisition.Context.class,
        org.opengis.metadata.acquisition.EnvironmentalRecord.class,
        org.opengis.metadata.acquisition.Event.class,
        org.opengis.metadata.acquisition.GeometryType.class,
        org.opengis.metadata.acquisition.Instrument.class,
        org.opengis.metadata.acquisition.Objective.class,
        org.opengis.metadata.acquisition.ObjectiveType.class,
        org.opengis.metadata.acquisition.Operation.class,
        org.opengis.metadata.acquisition.OperationType.class,
        org.opengis.metadata.acquisition.Plan.class,
        org.opengis.metadata.acquisition.Platform.class,
        org.opengis.metadata.acquisition.PlatformPass.class,
        org.opengis.metadata.acquisition.Priority.class,
        org.opengis.metadata.acquisition.RequestedDate.class,
        org.opengis.metadata.acquisition.Requirement.class,
        org.opengis.metadata.acquisition.Sequence.class,
        org.opengis.metadata.acquisition.Trigger.class,
        org.opengis.metadata.citation.Address.class,
        org.opengis.metadata.citation.Citation.class,
        org.opengis.metadata.citation.CitationDate.class,
        org.opengis.metadata.citation.Contact.class,
        org.opengis.metadata.citation.DateType.class,
        org.opengis.metadata.citation.OnLineFunction.class,
        org.opengis.metadata.citation.OnlineResource.class,
        org.opengis.metadata.citation.PresentationForm.class,
        org.opengis.metadata.citation.ResponsibleParty.class,
        org.opengis.metadata.citation.Role.class,
        org.opengis.metadata.citation.Series.class,
        org.opengis.metadata.citation.Telephone.class,
        org.opengis.metadata.constraint.Classification.class,
        org.opengis.metadata.constraint.Constraints.class,
        org.opengis.metadata.constraint.LegalConstraints.class,
        org.opengis.metadata.constraint.Restriction.class,
        org.opengis.metadata.constraint.SecurityConstraints.class,
        org.opengis.metadata.content.Band.class,
        org.opengis.metadata.content.BandDefinition.class,
        org.opengis.metadata.content.ContentInformation.class,
        org.opengis.metadata.content.CoverageContentType.class,
        org.opengis.metadata.content.CoverageDescription.class,
        org.opengis.metadata.content.FeatureCatalogueDescription.class,
        org.opengis.metadata.content.ImageDescription.class,
        org.opengis.metadata.content.ImagingCondition.class,
        org.opengis.metadata.content.PolarizationOrientation.class,
        org.opengis.metadata.content.RangeDimension.class,
        org.opengis.metadata.content.RangeElementDescription.class,
        org.opengis.metadata.content.TransferFunctionType.class,
        org.opengis.metadata.distribution.DataFile.class,
        org.opengis.metadata.distribution.DigitalTransferOptions.class,
        org.opengis.metadata.distribution.Distribution.class,
        org.opengis.metadata.distribution.Distributor.class,
        org.opengis.metadata.distribution.Format.class,
        org.opengis.metadata.distribution.Medium.class,
        org.opengis.metadata.distribution.MediumFormat.class,
        org.opengis.metadata.distribution.MediumName.class,
        org.opengis.metadata.distribution.StandardOrderProcess.class,
        org.opengis.metadata.extent.BoundingPolygon.class,
        org.opengis.metadata.extent.Extent.class,
        org.opengis.metadata.extent.GeographicBoundingBox.class,
        org.opengis.metadata.extent.GeographicDescription.class,
        org.opengis.metadata.extent.GeographicExtent.class,
        org.opengis.metadata.extent.SpatialTemporalExtent.class,
        org.opengis.metadata.extent.TemporalExtent.class,
        org.opengis.metadata.extent.VerticalExtent.class,
        org.opengis.metadata.identification.AggregateInformation.class,
        org.opengis.metadata.identification.AssociationType.class,
        org.opengis.metadata.identification.BrowseGraphic.class,
        org.opengis.metadata.identification.CharacterSet.class,
        org.opengis.metadata.identification.DataIdentification.class,
        org.opengis.metadata.identification.Identification.class,
        org.opengis.metadata.identification.InitiativeType.class,
        org.opengis.metadata.identification.Keywords.class,
        org.opengis.metadata.identification.KeywordType.class,
        org.opengis.metadata.identification.Progress.class,
        org.opengis.metadata.identification.RepresentativeFraction.class,
        org.opengis.metadata.identification.Resolution.class,
        org.opengis.metadata.identification.ServiceIdentification.class,
        org.opengis.metadata.identification.TopicCategory.class,
        org.opengis.metadata.identification.Usage.class,
        org.opengis.metadata.lineage.Algorithm.class,
        org.opengis.metadata.lineage.Lineage.class,
        org.opengis.metadata.lineage.NominalResolution.class,
        org.opengis.metadata.lineage.Processing.class,
        org.opengis.metadata.lineage.ProcessStep.class,
        org.opengis.metadata.lineage.ProcessStepReport.class,
        org.opengis.metadata.lineage.Source.class,
        org.opengis.metadata.maintenance.MaintenanceFrequency.class,
        org.opengis.metadata.maintenance.MaintenanceInformation.class,
        org.opengis.metadata.maintenance.ScopeCode.class,
        org.opengis.metadata.maintenance.ScopeDescription.class,
        org.opengis.metadata.quality.AbsoluteExternalPositionalAccuracy.class,
        org.opengis.metadata.quality.AccuracyOfATimeMeasurement.class,
        org.opengis.metadata.quality.Completeness.class,
        org.opengis.metadata.quality.CompletenessCommission.class,
        org.opengis.metadata.quality.CompletenessOmission.class,
        org.opengis.metadata.quality.ConceptualConsistency.class,
        org.opengis.metadata.quality.ConformanceResult.class,
        org.opengis.metadata.quality.CoverageResult.class,
        org.opengis.metadata.quality.DataQuality.class,
        org.opengis.metadata.quality.DomainConsistency.class,
        org.opengis.metadata.quality.Element.class,
        org.opengis.metadata.quality.EvaluationMethodType.class,
        org.opengis.metadata.quality.FormatConsistency.class,
        org.opengis.metadata.quality.GriddedDataPositionalAccuracy.class,
        org.opengis.metadata.quality.LogicalConsistency.class,
        org.opengis.metadata.quality.NonQuantitativeAttributeAccuracy.class,
        org.opengis.metadata.quality.PositionalAccuracy.class,
        org.opengis.metadata.quality.QuantitativeAttributeAccuracy.class,
        org.opengis.metadata.quality.QuantitativeResult.class,
        org.opengis.metadata.quality.RelativeInternalPositionalAccuracy.class,
        org.opengis.metadata.quality.Result.class,
        org.opengis.metadata.quality.Scope.class,
        org.opengis.metadata.quality.TemporalAccuracy.class,
        org.opengis.metadata.quality.TemporalConsistency.class,
        org.opengis.metadata.quality.TemporalValidity.class,
        org.opengis.metadata.quality.ThematicAccuracy.class,
        org.opengis.metadata.quality.ThematicClassificationCorrectness.class,
        org.opengis.metadata.quality.TopologicalConsistency.class,
        org.opengis.metadata.quality.Usability.class,
        org.opengis.metadata.spatial.CellGeometry.class,
        org.opengis.metadata.spatial.Dimension.class,
        org.opengis.metadata.spatial.DimensionNameType.class,
        org.opengis.metadata.spatial.GCP.class,
        org.opengis.metadata.spatial.GCPCollection.class,
        org.opengis.metadata.spatial.GeolocationInformation.class,
        org.opengis.metadata.spatial.GeometricObjects.class,
        org.opengis.metadata.spatial.GeometricObjectType.class,
        org.opengis.metadata.spatial.Georectified.class,
        org.opengis.metadata.spatial.Georeferenceable.class,
        org.opengis.metadata.spatial.GridSpatialRepresentation.class,
        org.opengis.metadata.spatial.PixelOrientation.class,
        org.opengis.metadata.spatial.SpatialRepresentation.class,
        org.opengis.metadata.spatial.SpatialRepresentationType.class,
        org.opengis.metadata.spatial.TopologyLevel.class,
        org.opengis.metadata.spatial.VectorSpatialRepresentation.class
    };

    /**
     * Returns the list of Metadata code lists or interfaces to test.
     */
    @Override
    protected Class<?>[] getTestedTypes() {
        return TYPES;
    }

    /**
     * Returns the Geotk implementation for the given GeoAPI interface.
     */
    @Override
    protected Class<?> getImplementation(final Class<?> type) {
        assertTrue(MetadataStandard.ISO_19115.isMetadata(type));
        return MetadataStandard.ISO_19115.getImplementation(type);
    }

    /**
     * Returns the expected namespace for an element defined by the given specification.
     */
    @Override
    protected String getNamespace(final Specification specification) {
        switch (specification) {
            case ISO_19115:   return Namespaces.GMD;
            case ISO_19115_2: return Namespaces.GMI;
            case ISO_19139:   return Namespaces.GMX;
            case ISO_19108:   return Namespaces.GMD;
            default: throw new IllegalArgumentException(specification.toString());
        }
    }

    /**
     * Returns the prefix to use for the given namespace.
     */
    @Override
    protected String getPrefixForNamespace(final String namespace) {
        return Namespaces.getPreferredPrefix(namespace, null);
    }

    /**
     * Returns the type of the given element, or {@link #DEFAULT} if the type is not yet
     * determined (the later cases could change in a future version).
     *
     * @todo Use string switch with JDK 7.
     */
    @Override
    protected String getTypeForElement(final String rootName, final String implName) {
        // We don't know yet what is the type of this one.
        if (rootName.equals("MD_FeatureTypeList")) {
            return DEFAULT;
        }
        // Following prefix was changed in ISO 19115 corrigendum,
        // but ISO 19139 still use the old prefix.
        if (rootName.equals("SV_ServiceIdentification")) {
            return "MD_ServiceIdentification_Type";
        }
        final StringBuilder buffer = new StringBuilder(rootName.length() + 13);
        if (implName.startsWith("Abstract")) {
            buffer.append("Abstract");
        }
        return buffer.append(rootName).append("_Type").toString();
    }

    /**
     * Returns the ISO 19139 wrapper for the given GeoAPI type, or {@code null} if not found,
     * or {@link Void#TYPE} if no adapter is expected for the given type.
     */
    @Override
    protected Class<?> getWrapper(final Class<?> type) {
        if (type.equals(org.opengis.metadata.Metadata.class)) {
            /*
             * We don't have adapter for Metadata, since it is the root element.
             * We explicitly exclude it for avoiding confusion with PropertyType,
             * which is the base class of all other adapters.
             */
            return Void.TYPE;
        }
        final String classname = "org.geotoolkit.internal.jaxb." +
              (CodeList.class.isAssignableFrom(type) ? "code" : "metadata") +
              '.' + type.getAnnotation(UML.class).identifier();
        final Class<?> wrapper;
        try {
            wrapper = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            // A warning will be logged by the caller.
            return null;
        }
        assertTrue("Expected a final class for " + wrapper.getName(), Modifier.isFinal(wrapper.getModifiers()));
        return wrapper;
    }
}
