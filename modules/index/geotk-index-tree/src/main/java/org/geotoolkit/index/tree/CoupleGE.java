/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.tree;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.util.ArgumentChecks;

/**
 * Create a couple of two {@code GeneralEnvelope}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class CoupleGE implements Couple<GeneralEnvelope> {

    final private GeneralEnvelope gE1;
    final private GeneralEnvelope gE2;
    final private Calculator calculator;
    private Map<String, Object> userProperties;

    /**
     * Create a {@code GeneralEnvelope} couple.
     *
     * @param gE1
     * @param gE2
     * @param calculator to compute all {@code Couple} properties.
     * @throws IllegalArgumentException if gE1 or gE2 are null.
     */
    public CoupleGE(final GeneralEnvelope gE1, final GeneralEnvelope gE2, final Calculator calculator) {
        ArgumentChecks.ensureNonNull("coupleGE contructor : gE1", gE1);
        ArgumentChecks.ensureNonNull("coupleGE contructor : gE2", gE2);
        ArgumentChecks.ensureNonNull("coupleGE contructor : calculator", calculator);
        this.gE1 = gE1;
        this.gE2 = gE2;
        this.calculator = calculator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneralEnvelope getObject1() {
        return this.gE1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneralEnvelope getObject2() {
        return this.gE2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean intersect() {
        return getObject1().intersects(getObject2(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDistance() {
        return calculator.getDistance(getObject1(), getObject2());
    }

    /**
     * Remark : intersection edge between object1 and object2 is not subtract.
     *
     * @return two objects area som.
     */
    @Override
    public double getEdge() {
        return calculator.getEdge(getObject1()) + calculator.getEdge(getObject2());
    }

    /**
     * Remark : intersection space between object1 and object2 is not subtract.
     *
     * @return two objects space som.
     */
    @Override
    public double getSpace() {
        return calculator.getSpace(getObject1()) + calculator.getSpace(getObject2());
    }

    /**
     * {@inheritDoc}.
     *
     * @return Two objects intersection space.
     */
    @Override
    public double getOverlaps() {
        return calculator.getOverlaps(getObject1(), getObject2());
    }

    /**
     * @param key
     * @return user property for given key
     */
    public Object getUserProperty(final String key) {
        if (userProperties == null) {
            return null;
        }
        return userProperties.get(key);
    }

    /**
     * Add user property with key access.
     *
     * @param key
     * @param value Object will be stocked.
     */
    public void setUserProperty(final String key, final Object value) {
        if (userProperties == null) {
            userProperties = new HashMap<String, Object>();
        }
        userProperties.put(key, value);
    }
}
