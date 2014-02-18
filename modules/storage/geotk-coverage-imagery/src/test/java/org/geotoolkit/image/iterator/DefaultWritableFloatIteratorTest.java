/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.iterator;

import java.awt.image.DataBuffer;

/**
 * Test DefaultWritableFloatIterator class.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class DefaultWritableFloatIteratorTest extends DefaultWritableTest{

    /**
     * Table which contains expected tests results values.
     */
    private float[] tabRef;

    /**
     * Table which contains tests results values.
     */
    private float[] tabTest;

    /**
     * {@inheritDoc }
     */
    @Override
    protected int getDataBufferType() {
        return DataBuffer.TYPE_FLOAT;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void setTabTestValue(int index, double value) {
        tabTest[index] = (float) value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean compareTab() {
        return compareTab(tabRef, tabTest);
    }

     /**
     * {@inheritDoc }
     */
    @Override
    protected void setTabRefValue(int index, double value) {
        tabRef[index] = (float) value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createTable(int length) {
        tabRef = new float[length];
        tabTest = new float[length];
    }
}
