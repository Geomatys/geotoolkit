/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import org.opengis.util.CodeList;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.CitationFactory;
import org.opengis.metadata.maintenance.ScopeDescription;
import org.opengis.metadata.identification.AggregateInformation;
import org.opengis.metadata.identification.RepresentativeFraction;

import org.apache.sis.util.Classes;
import org.geotoolkit.util.collection.CheckedContainer;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests every implementation in the {@link org.geotoolkit.metadata.iso} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 *
 * @todo Current implementation relies on {@link Metadata} dependencies. This is probably
 *       not enough; we should provide an explicit list of metadata interface.
 */
public final strictfp class ISOTest extends TestBase {
    /**
     * Root package for interfaces, with trailing dot.
     */
    private static final String INTERFACE_PACKAGE = "org.opengis.metadata.";

    /**
     * Root package for implementations, with trailing dot.
     */
    private static final String IMPLEMENTATION_PACKAGE = "org.geotoolkit.metadata.iso.";

    /**
     * Prefix for implementation classes.
     */
    private static final String[] IMPLEMENTATION_PREFIX = {"Default", "Abstract"};

    /**
     * List of GeoAPI interfaces to test. This list is not exclusive, since this test suite
     * will automatically scans for dependencies even if an interface do not appears in this
     * list. This list should not contains any {@link CodeList}.
     */
    private static final Class<?>[] TEST = new Class<?>[] {
        org.opengis.metadata.ApplicationSchemaInformation.class,
        org.opengis.metadata.ExtendedElementInformation.class,
        org.opengis.metadata.FeatureTypeList.class,
        org.opengis.metadata.Identifier.class,
        org.opengis.metadata.Metadata.class,
        org.opengis.metadata.MetadataExtensionInformation.class,
        org.opengis.metadata.PortrayalCatalogueReference.class,
        org.opengis.metadata.citation.Address.class,
        org.opengis.metadata.citation.Citation.class,
        org.opengis.metadata.citation.CitationDate.class,
        org.opengis.metadata.citation.CitationFactory.class,
        org.opengis.metadata.citation.Contact.class,
        org.opengis.metadata.citation.OnlineResource.class,
        org.opengis.metadata.citation.ResponsibleParty.class,
        org.opengis.metadata.citation.Series.class,
        org.opengis.metadata.citation.Telephone.class,
        org.opengis.metadata.constraint.Constraints.class,
        org.opengis.metadata.constraint.LegalConstraints.class,
        org.opengis.metadata.constraint.SecurityConstraints.class,
        org.opengis.metadata.content.Band.class,
        org.opengis.metadata.content.ContentInformation.class,
        org.opengis.metadata.content.CoverageDescription.class,
        org.opengis.metadata.content.FeatureCatalogueDescription.class,
        org.opengis.metadata.content.ImageDescription.class,
        org.opengis.metadata.content.RangeDimension.class,
        org.opengis.metadata.distribution.DigitalTransferOptions.class,
        org.opengis.metadata.distribution.Distribution.class,
        org.opengis.metadata.distribution.Distributor.class,
        org.opengis.metadata.distribution.Format.class,
        org.opengis.metadata.distribution.Medium.class,
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
        org.opengis.metadata.identification.BrowseGraphic.class,
        org.opengis.metadata.identification.DataIdentification.class,
        org.opengis.metadata.identification.Identification.class,
        org.opengis.metadata.identification.Keywords.class,
        org.opengis.metadata.identification.RepresentativeFraction.class,
        org.opengis.metadata.identification.Resolution.class,
        org.opengis.metadata.identification.ServiceIdentification.class,
        org.opengis.metadata.identification.Usage.class,
        org.opengis.metadata.lineage.Lineage.class,
        org.opengis.metadata.lineage.ProcessStep.class,
        org.opengis.metadata.lineage.Source.class,
        org.opengis.metadata.maintenance.MaintenanceInformation.class,
        org.opengis.metadata.maintenance.ScopeDescription.class,
        org.opengis.metadata.quality.AbsoluteExternalPositionalAccuracy.class,
        org.opengis.metadata.quality.AccuracyOfATimeMeasurement.class,
        org.opengis.metadata.quality.Completeness.class,
        org.opengis.metadata.quality.CompletenessCommission.class,
        org.opengis.metadata.quality.CompletenessOmission.class,
        org.opengis.metadata.quality.ConceptualConsistency.class,
        org.opengis.metadata.quality.ConformanceResult.class,
        org.opengis.metadata.quality.DataQuality.class,
        org.opengis.metadata.quality.DomainConsistency.class,
        org.opengis.metadata.quality.Element.class,
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
        org.opengis.metadata.spatial.Dimension.class,
        org.opengis.metadata.spatial.GeometricObjects.class,
        org.opengis.metadata.spatial.Georectified.class,
        org.opengis.metadata.spatial.Georeferenceable.class,
        org.opengis.metadata.spatial.GridSpatialRepresentation.class,
        org.opengis.metadata.spatial.SpatialRepresentation.class,
        org.opengis.metadata.spatial.VectorSpatialRepresentation.class
    };

    /**
     * GeoAPI interfaces that are know to be unimplemented at this stage.
     */
    private static final Class<?>[] UNIMPLEMENTED = new Class<?>[] {
        AggregateInformation.class,
        CitationFactory.class,          // SHOULD THIS INTERFACE REALLY EXISTS IN GEOAPI?
        RepresentativeFraction.class,   // Implemented on top of 'Number'.
        ScopeDescription.class,         // Only partially implemented (no references to Features).
        OnlineResource.class            // No 'setProtocol' method.
    };

    /**
     * Ensures that the {@link #TEST} array do not contains code list.
     */
    @Test
    public void testNoCodeList() {
        for (int i=0; i<TEST.length; i++) {
            final Class<?> type = TEST[i];
            assertFalse(type.getCanonicalName(), CodeList.class.isAssignableFrom(type));
        }
    }

    /**
     * Tests all dependencies starting from the {@link DefaultMetadata} class.
     */
    @Test
    public void testDependencies() {
        assertNull(getImplementation(Number.class));
        assertSame(DefaultMetadata.class, getImplementation(Metadata.class));
        final Set<Class<?>> done = new HashSet<>();
        for (int i=0; i<TEST.length; i++) {
            final Class<?> type = TEST[i];
            final Class<?> impl = getImplementation(type);
            if (impl == null) {
                if (isImplemented(type)) {
                    fail(type.getCanonicalName() + " is not implemented.");
                }
                continue;
            }
            assertSetters(new PropertyAccessor(impl, type), done);
        }
    }

    /**
     * Recursively ensures that the specified metadata implementation has
     * setters for every methods.
     */
    private static void assertSetters(final PropertyAccessor accessor, final Set<Class<?>> done) {
        if (done.add(accessor.type)) {
            /*
             * Tries to instantiate the implementation. Every implementation should have a
             * no-args constructor, and their instantiation should never fail.
             */
            final Object dummyInstance;
            final boolean isImplemented = isImplemented(accessor.type);
            if (isImplemented) try {
                dummyInstance = accessor.implementation.getConstructor((Class<?>[]) null).
                        newInstance((Object[]) null);
            } catch (ReflectiveOperationException e) {
                fail(e.toString());
                return;
            } else {
                dummyInstance = null;
            }
            /*
             * Iterates over all properties defined in the interface,
             * and checks for the existences of a setter method.
             */
            final String classname = Classes.getShortName(accessor.type) + '.';
            final int count = accessor.count();
            for (int i=0; i<count; i++) {
                final String name = accessor.name(i, KeyNamePolicy.JAVABEANS_PROPERTY);
                assertNotNull(String.valueOf(i), name);
                final String fullname = classname + name;
                assertEquals(fullname, i, accessor.indexOf(name));
                if (!isImplemented) {
                    continue;
                }
                // We can not continue below this point for
                // implementations that are only partial.
                assertTrue(fullname, accessor.isWritable(i));
                /*
                 * Get the property type. In the special case where the property type
                 * is a collection, this is the type of elements in that collection.
                 */
                final Class<?> type = accessor.type(i, TypeValuePolicy.ELEMENT_TYPE);
                final Class<?> impl = getImplementation(type);
                assertFalse(Collection.class.isAssignableFrom(type));
                final Object example = accessor.get(i, dummyInstance);
                if (example instanceof CheckedContainer<?>) {
                    assertTrue(type.isAssignableFrom(((CheckedContainer<?>) example).getElementType()));
                }
                if (impl != null) {
                    assertTrue(type.isAssignableFrom(impl));
                    assertSetters(new PropertyAccessor(impl, type), done);
                }
            }
        }
    }

    /**
     * Returns the implementation class for the specified interface class,
     * or {@code null} if none.
     */
    private static Class<?> getImplementation(final Class<?> type) {
        if (!CodeList.class.isAssignableFrom(type)) {
            String name = type.getCanonicalName();
            if (name.startsWith(INTERFACE_PACKAGE)) {
                ClassNotFoundException failure = null;
                final int nameStart = name.lastIndexOf('.') + 1;
                final String packageName = IMPLEMENTATION_PACKAGE + name.substring(INTERFACE_PACKAGE.length(), nameStart);
                final String className = name.substring(nameStart);
                for (int i=0; i<IMPLEMENTATION_PREFIX.length; i++) {
                    name = packageName + IMPLEMENTATION_PREFIX[i] + className;
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        failure = e;
                    }
                }
                /*
                 * Found a class which is not implemented. Before to report an error,
                 * check if it is part of the list of known unimplemented interfaces.
                 */
                if (isImplemented(type)) {
                    fail(failure.toString());
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified type is not in the list of
     * known unimplemented types.
     */
    private static boolean isImplemented(final Class<?> type) {
        for (int i=0; i<UNIMPLEMENTED.length; i++) {
            if (type.equals(UNIMPLEMENTED[i])) {
                return false;
            }
        }
        return true;
    }
}
