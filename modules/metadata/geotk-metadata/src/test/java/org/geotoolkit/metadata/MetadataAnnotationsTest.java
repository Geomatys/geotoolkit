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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import org.junit.*;
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
public final class MetadataAnnotationsTest {
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
     * Tests the annotations on metadata (not code list) objects.
     */
    @Test
    public void testMetadataAnnotations() {
        final MetadataStandard standard = MetadataStandard.ISO_19115;
        for (final Class<?> type : TYPES) {
            assertTrue(standard.isMetadata(type));
            if (CodeList.class.isAssignableFrom(type)) {
                // Skip code lists, as they are not the purpose of this test.
                continue;
            }
            assertTrue(type + " is not an interface", type.isInterface());
            final Class<?> impl = standard.getImplementation(type);
            assertNotSame("No implementation found for " + type, type, impl);
            /*
             * Get the @UML annotation, which is mandatory.
             */
            final UML classUML = type.getAnnotation(UML.class);
            assertNotNull("Missing @UML annotation for " + type, classUML);
            /*
             * Get the @XmlRootElement annotation and compare.
             */
            final XmlRootElement xmlRoot = impl.getAnnotation(XmlRootElement.class);
            assertNotNull("Missing @XmlRootElement annotation for " + impl, xmlRoot);
            assertEquals("Annotation mismatch for " + impl, classUML.identifier(), xmlRoot.name());
            /*
             * We do not expect a name attributes in @XmlType since the name
             * is already specified in the @XmlRootElement annotation.
             */
            final XmlType xmlType = impl.getAnnotation(XmlType.class);
            if (xmlType != null && false) { // TODO: this test is disabled for now.
                assertEquals("No @XmlType(name) value expected for " + impl, "##default", xmlType.name());
            }
            /*
             * Compare the method annotations.
             */
            for (final Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Deprecated.class)) {
                    // Skip deprecated methods.
                    continue;
                }
                final String name = method.getName();
                if (name.equals("equals") || name.equals("hashCode") || name.equals("doubleValue")) {
                    /*
                     * Do not verify annotations for those methods that we know they are
                     * intentionaly not annotated.
                     */
                    continue;
                }
                final UML methodUML = method.getAnnotation(UML.class);
                assertNotNull("Missing @UML annotation for " + method, methodUML);
                /*
                 * Get the annotation from the method. If the method is not annotated,
                 * get the annotation from the field instead.
                 */
                final Method methodImpl;
                try {
                    methodImpl = impl.getMethod(name, (Class<?>[]) null);
                } catch (NoSuchMethodException ex) {
                    fail(ex.getMessage());
                    continue;
                }
                XmlElement xmlElem = methodImpl.getAnnotation(XmlElement.class);
                if (xmlElem == null) try {
                    final Field field = impl.getDeclaredField(methodUML.identifier());
                    xmlElem = field.getAnnotation(XmlElement.class);
                } catch (NoSuchFieldException ex) {
                    // Ignore - we will consider that there is no annotation.
                }
                /*
                 * Just displays the missing @XmlElement annotation for the method, since it can be
                 * intentionaly done. Sometimes there is no implementation for the method return type,
                 * consequently we are unable to annotate the method with this annotation until the
                 * implementation is done.
                 */
                //assertNotNull("Missing @XmlElement annotation for method " + methodGeotk, xmlElem);
                if (xmlElem == null) {
                    System.out.println("Missing @XmlElement annotation for method " +
                            impl.getName() + '.' + name + "()");
                    continue;
                }
                assertEquals("Annotation mismatch for " + impl, methodUML.identifier(), xmlElem.name());
            }
        }
    }
}
