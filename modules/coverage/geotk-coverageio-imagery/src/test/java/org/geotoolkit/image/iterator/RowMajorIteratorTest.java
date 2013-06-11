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
 * Test RowMajorRenderedImageIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorIteratorTest extends RowMajorReadTest {

    /**
     * Table which contains expected tests results values.
     */
    private int[] tabRef;

    /**
     * Table which contains tests results values.
     */
    private int[] tabTest;

    public RowMajorIteratorTest() {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabTestValue(int index, double value) {
        tabTest[index] = (int) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected boolean compareTab() {
        return compareTab(tabRef, tabTest);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setMoveToRITabs(int indexCut, int length) {
        tabTest = new int[length];
        int[] tabTemp = new int[length];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, length);
        tabRef = tabTemp;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected int getDataBufferType() {
        return DataBuffer.TYPE_INT;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void createTable(int length) {
        tabRef = new int[length];
        tabTest = new int[length];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabRefValue(int index, double value) {
        tabRef[index] = (int) value;
    }
}
