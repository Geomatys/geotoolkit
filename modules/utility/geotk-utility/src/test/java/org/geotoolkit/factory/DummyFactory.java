/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import static org.junit.Assert.*;


/**
 * An internal dummy factory for testing factory dependencies.
 * It doesn't matter if this factory is registered or not. We
 * just need a {@code InternalFactory.class} value different
 * than {@code DummyFactory.class}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 */
interface InternalFactory {
}


/**
 * Dummy factory interface for {@link FactoryRegistryTest}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 */
public interface DummyFactory extends InternalFactory {
    /**
     * A hint key for a {@code DummyFactory} instance.
     */
    Hints.ClassKey DUMMY_FACTORY = new Hints.ClassKey(DummyFactory.class);

    /**
     * A hint key for a {@code DummyFactory2} instance.
     */
    Hints.Key INTERNAL_FACTORY = new Hints.ClassKey(InternalFactory.class);
}


/**
 * An empty concrete subclass of {@link Factory}. Needed in order to instantiate
 * objects since {@link Factory} is abstract.
 */
final class EmptyFactory extends Factory implements DummyFactory {
}


/**
 * Dummy factory implementation #1.
 * This factory doesn't use any other factory.
 */
final class Example1 extends Factory implements DummyFactory {
    public Example1() {
        hints.put(Hints.KEY_INTERPOLATION, Hints.VALUE_INTERPOLATION_BILINEAR);
    }

    @Override
    public String toString() {
        return "#1";
    }
}


/**
 * Dummy factory implementation #2.
 * This factory uses factory #1.
 */
final class Example2 extends Factory implements DummyFactory {
    public Example2() {
        hints.put(INTERNAL_FACTORY, new Example1());
    }

    @Override
    public String toString() {
        return "#2";
    }
}


/**
 * Dummy factory implementation #3.
 * This factory uses factory #2, which uses itself factory #1.
 */
final class Example3 extends Factory implements DummyFactory {
    public Example3() {
        hints.put(INTERNAL_FACTORY, new Example2());
    }

    @Override
    public String toString() {
        return "#3";
    }
}


/**
 * Dummy factory implementation #4.
 * {@link FactoryRegistryTest} will not register this factory in same time than other ones.
 */
final class Example4 extends Factory implements DummyFactory {
    public Example4() {
        hints.put(Hints.KEY_INTERPOLATION, Hints.VALUE_INTERPOLATION_BICUBIC);
    }

    @Override
    public String toString() {
        return "#4";
    }
}


/**
 * Dummy factory implementation #5.
 * {@link FactoryRegistryTest} will not register this factory in same time than other ones.
 * This factory is the only one to accept hints.
 */
final class Example5 extends Factory implements DummyFactory {
    private Object value = Hints.VALUE_INTERPOLATION_BILINEAR;

    public Example5() {
        fail("The constructor with Hints argument should have been used.");
    }

    public Example5(Hints userHints) {
        if (userHints!=null && userHints.containsKey(Hints.KEY_INTERPOLATION)) {
            value = userHints.get(Hints.KEY_INTERPOLATION);
        }
        hints.put(Hints.KEY_INTERPOLATION, value);
    }

    @Override
    public String toString() {
        return "#5";
    }
}
