/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.test.feature;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Deque;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.opengis.util.GenericName;
import org.apache.sis.util.privy.CollectionsExt;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Deprecable;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

// Branch-dependent imports
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.PropertyType;
import org.opengis.feature.AttributeType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;


/**
 * Copy of assertion methods provided by Apache SIS.
 */
public class FeatureComparator {
    private final Feature expectedInstance;
    private final FeatureType expectedType;
    private final Feature actualInstance;
    private final FeatureType actualType;
    public final Set<String> ignoredProperties = new HashSet<>();
    public final Set<String> ignoredCharacteristics = new HashSet<>();
    public boolean ignoreDefinition;
    public boolean ignoreDesignation;
    public boolean ignoreDescription;
    private final Deque<String> path = new ArrayDeque<>();

    public FeatureComparator(final Feature expected, final Feature actual) {
        ArgumentChecks.ensureNonNull("expected", expected);
        ArgumentChecks.ensureNonNull("actual", actual);
        expectedInstance = expected;
        expectedType     = expected.getType();
        actualInstance   = actual;
        actualType       = actual.getType();
    }

    public FeatureComparator(final FeatureType expected, final FeatureType actual) {
        ArgumentChecks.ensureNonNull("expected", expected);
        ArgumentChecks.ensureNonNull("actual",   actual);
        expectedInstance = null;
        expectedType     = expected;
        actualInstance   = null;
        actualType       = actual;
    }

    public void compare() {
        if (expectedInstance != null) {
            compareFeature(expectedInstance, actualInstance);
        } else {
            compareFeatureType(expectedType, actualType);
        }
    }

    private void compareType(final IdentifiedType expected, final IdentifiedType actual) {
        boolean recognized = false;
        final Supplier<String> message = () -> path();
        if (expected instanceof FeatureType) {
            assertInstanceOf(FeatureType.class, actual, message);
            compareFeatureType((FeatureType) expected, (FeatureType) actual);
            recognized = true;
        }
        if (expected instanceof PropertyType) {
            assertInstanceOf(PropertyType.class, actual, message);
            comparePropertyType((PropertyType) expected, (PropertyType) actual);
            recognized = true;
        }
        if (!recognized) {
            fail(path() + "Unexpected type " + expected);
        }
    }

    private void compareFeatureType(final FeatureType expected, final FeatureType actual) {
        compareIdentifiedType(expected, actual);
        assertEquals(path() + "Abstract state differ", expected.isAbstract(), actual.isAbstract());
        assertEquals(path() + "Super types differ", expected.getSuperTypes(), actual.getSuperTypes());
        final List<PropertyType> actualProperties = new ArrayList<>(actual.getProperties(false));
        actualProperties.removeIf(this::isIgnored);
        for (final PropertyType pte : expected.getProperties(false)) {
            if (!isIgnored(pte)) {
                final String tip = push(pte.getName().toString());
                PropertyType pta = findAndRemove(actualProperties, pte.getName());
                comparePropertyType(pte, pta);
                pull(tip);
            }
        }
        if (!actualProperties.isEmpty()) {
            final StringBuilder b = new StringBuilder(path())
                    .append("Actual type contains a property not declared in expected type:")
                    .append(System.lineSeparator());
            for (final PropertyType pta : actualProperties) {
                b.append("  ").append(pta.getName()).append(System.lineSeparator());
            }
            fail(b.toString());
        }
    }

    private void compareFeature(final Feature expected, final Feature actual) {
        compareFeatureType(expected.getType(), actual.getType());
        for (final PropertyType p : expected.getType().getProperties(true)) {
            if (isIgnored(p)) {
                continue;
            }
            final String tip = push(p.getName().toString());
            final Collection<?> expectedValues = asCollection(expected.getPropertyValue(tip));
            final Collection<?> actualValues   = asCollection(actual.getPropertyValue(tip));
            assertEquals(path() + "Number of values differ", expectedValues.size(), actualValues.size());
            final Iterator<?> expectedIter = expectedValues.iterator();
            final Iterator<?> actualIter = actualValues.iterator();
            while (expectedIter.hasNext()) {
                final Object expectedElement = expectedIter.next();
                final Object actualElement = actualIter.next();
                if (expectedElement instanceof Feature) {
                    compareFeature((Feature) expectedElement, (Feature) actualElement);
                } else {
                    assertEquals(expectedElement, actualElement);
                }
            }
            pull(tip);
        }
    }

    private void comparePropertyType(final PropertyType expected, final PropertyType actual) {
        if (expected instanceof AttributeType) {
            assertInstanceOf(AttributeType.class, actual, () -> path());
            compareAttribute((AttributeType) expected, (AttributeType) actual);
        }
        if (expected instanceof FeatureAssociationRole) {
            assertInstanceOf(FeatureAssociationRole.class, actual, () -> path());
            compareFeatureAssociationRole((FeatureAssociationRole) expected, (FeatureAssociationRole) actual);
        }
        if (expected instanceof Operation) {
            assertInstanceOf(Operation.class, actual, () -> path());
            compareOperation((Operation) expected, (Operation) actual);
        }
    }

    private void compareAttribute(final AttributeType<?> expected, final AttributeType<?> actual) {
        compareIdentifiedType(expected, actual);
        assertEquals(path() + "Value classe differ",  expected.getValueClass(),   expected.getValueClass());
        assertEquals(path() + "Default value differ", expected.getDefaultValue(), expected.getDefaultValue());

        final Map<String, AttributeType<?>> expectedChrs = expected.characteristics();
        final Map<String, AttributeType<?>> actualChrs = actual.characteristics();
        final List<String> actualChrNames = new ArrayList<>(actualChrs.keySet());
        actualChrNames.removeIf((p) -> ignoredCharacteristics.contains(p));

        for (final Map.Entry<String, AttributeType<?>> entry : expectedChrs.entrySet()) {
            final String p = entry.getKey();
            if (!ignoredCharacteristics.contains(p)) {
                final AttributeType<?> expectedChr = entry.getValue();
                final AttributeType<?> actualChr = actualChrs.get(p);
                final String tip = push("characteristic(" + p + ')');
                assertNotNull(path(), actualChr);
                assertTrue(actualChrNames.remove(p));
                comparePropertyType(expectedChr, actualChr);
                pull(tip);
            }
        }
        if (!actualChrNames.isEmpty()) {
            final StringBuilder b = new StringBuilder(path())
                    .append("Result type contains a characteristic not declared in expected type:")
                    .append(System.lineSeparator());
            for (final String c : actualChrNames) {
                b.append("  ").append(c).append(System.lineSeparator());
            }
            fail(b.toString());
        }
    }

    private void compareFeatureAssociationRole(final FeatureAssociationRole expected, final FeatureAssociationRole actual) {
        compareIdentifiedType(expected, actual);
        assertEquals(path() + "Minimum occurences differ", expected.getMinimumOccurs(), actual.getMinimumOccurs());
        assertEquals(path() + "Maximum occurences differ", expected.getMaximumOccurs(), actual.getMaximumOccurs());
        final String tip = push("association-valuetype");
        compareFeatureType(expected.getValueType(), actual.getValueType());
        pull(tip);
    }

    private void compareOperation(final Operation expected, final Operation actual) {
        compareIdentifiedType(expected, actual);
        assertEquals(expected.getParameters(), actual.getParameters());
        final String tip = push("operation-actual(" + expected.getResult().getName() + ')');
        compareType(expected.getResult(), actual.getResult());
        pull(tip);
    }

    private void compareIdentifiedType(final IdentifiedType expected, final IdentifiedType actual) {
        assertEquals(path() + "Name differ", expected.getName(), actual.getName());
        if (!ignoreDefinition) {
            assertEquals(path() + "Definition differ", expected.getDefinition(), actual.getDefinition());
        }
        if (!ignoreDesignation) {
            assertEquals(path() + "Designation differ", expected.getDesignation(), actual.getDesignation());
        }
        if (!ignoreDescription) {
            assertEquals(path() + "Description differ", expected.getDescription(), actual.getDescription());
        }
        if (expected instanceof Deprecable && actual instanceof Deprecable) {
            assertEquals(path() + "Deprecated state differ",
                    ((Deprecable) expected).isDeprecated(),
                    ((Deprecable) actual).isDeprecated());
        }
    }

    private String push(final String label) {
        path.addLast(label);
        return label;
    }

    private void pull(final String tip) {
        assertSame(tip, path.removeLast());
    }

    private String path() {
        return path.stream().collect(Collectors.joining(" > ", "[", "]: "));
    }

    private boolean isIgnored(final PropertyType property) {
        return ignoredProperties.contains(property.getName().toString());
    }

    private PropertyType findAndRemove(final Collection<PropertyType> properties, final GenericName name) {
        final Iterator<PropertyType> it = properties.iterator();
        while (it.hasNext()) {
            final PropertyType pt = it.next();
            if (pt.getName().equals(name)) {
                it.remove();
                return pt;
            }
        }
        fail(path() + "Property not found for name " + name);
        return null;
    }

    private static Collection<?> asCollection(final Object value) {
        if (value instanceof Collection<?>) {
            return (Collection<?>) value;
        } else {
            return CollectionsExt.singletonOrEmpty(value);
        }
    }
}
