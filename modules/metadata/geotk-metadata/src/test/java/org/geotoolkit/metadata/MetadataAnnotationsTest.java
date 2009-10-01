/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.opengis.util.CodeList;
import org.geotoolkit.test.AnnotationsTest;

import static org.junit.Assert.*;


/**
 * Compares JAXB annotations with the UML ones.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public final class MetadataAnnotationsTest extends AnnotationsTest {
    /**
     * The list of Metadata code list or interfaces to test,
     * in alphabetical order.
     */
    private static final Class<?>[] TYPES = {
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
        org.opengis.metadata.ApplicationSchemaInformation.class,
        org.opengis.metadata.citation.Address.class,
        org.opengis.metadata.citation.Citation.class,
        org.opengis.metadata.citation.CitationDate.class,
        org.opengis.metadata.citation.Contact.class,
        org.opengis.metadata.citation.DateType.class,
        org.opengis.metadata.citation.OnLineFunction.class,
        org.opengis.metadata.citation.OnLineResource.class,
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
        org.opengis.metadata.Datatype.class,
        org.opengis.metadata.distribution.DataFile.class,
        org.opengis.metadata.distribution.DigitalTransferOptions.class,
        org.opengis.metadata.distribution.Distribution.class,
        org.opengis.metadata.distribution.Distributor.class,
        org.opengis.metadata.distribution.Format.class,
        org.opengis.metadata.distribution.Medium.class,
        org.opengis.metadata.distribution.MediumFormat.class,
        org.opengis.metadata.distribution.MediumName.class,
        org.opengis.metadata.distribution.StandardOrderProcess.class,
        org.opengis.metadata.ExtendedElementInformation.class,
        org.opengis.metadata.extent.BoundingPolygon.class,
        org.opengis.metadata.extent.Extent.class,
        org.opengis.metadata.extent.GeographicBoundingBox.class,
        org.opengis.metadata.extent.GeographicDescription.class,
        org.opengis.metadata.extent.GeographicExtent.class,
        org.opengis.metadata.extent.SpatialTemporalExtent.class,
        org.opengis.metadata.extent.TemporalExtent.class,
        org.opengis.metadata.extent.VerticalExtent.class,
        org.opengis.metadata.FeatureTypeList.class,
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
        org.opengis.metadata.Identifier.class,
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
        org.opengis.metadata.MetaData.class,
        org.opengis.metadata.MetadataExtensionInformation.class,
        org.opengis.metadata.Obligation.class,
        org.opengis.metadata.PortrayalCatalogueReference.class,
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
     * Returns the ISO 19139 wrapper for the given GeoAPI type.
     */
    @Override
    protected Class<?> getWrapper(final Class<?> type) {
        String classname = type.getSimpleName();
        if (classname.equals("MetaData")) {
            // Workaround an historical mispelling (TODO: fix in later GeoAPI version?)
            classname = "Metadata";
        }
        classname = "org.geotoolkit.internal.jaxb." +
              (CodeList.class.isAssignableFrom(type) ? "code" : "metadata") +
              '.' + classname + "Adapter";
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            /*
             * Do not consider missing wrapper as fatal errors for now, since we known
             * that some are missing. Instead just report the missing wrapper at build
             * time.
             */
            System.err.println("Missing adapter: " + classname);
            return null;
        }
    }
}
