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
package org.geotoolkit.image.classification;

/**
 * <p>Define all different possibilities to fractionate table.<br/>
 * Sequence builded contains all maximum index table.<br/><br/>
 *
 * for example : we will be able to find all possibilities to fractionate a <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;elements table of length 8 in 3 groups(classes).<br/><br/>
 *
 * {@code int[] sequence = new int[3];}(length = class number)<br/>
 * {@code JenkSequence JS = new JenkSequence(sequence, 8);}<br/>
 *
 * {@code JS.next();} -&gt;{@code sequence = {1, 2, 8};}<br/>
 * {@code JS.next();} -&gt;{@code sequence = {1, 3, 8};}<br/>
 * {@code JS.next();} -&gt;{@code sequence = {1, 4, 8};}<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
 * {@code JS.next();} -&gt;{@code sequence = {6, 7, 8};}</p>
 *
 * @author Martin Desruisseaux (Geomatys).
 * @author Rémi Marechal       (Geomatys).
 */
class JenkSequence {

    /**
     * Jenks sequence table.
     */
    private final int[] sequence;

    /**
     * Define number of classes.<br/>
     * Equal to Jenks sequence table length.
     */
    private final int classNumber;

    /**
     * Equal to {@link #classNumber}-2.
     */
    private final int cN_2;

    /**
     * Attribute use to define max value achieved.
     * @see #next(int) .
     */
    private final int max;

    /**
     * Jenks sequence table index which change.
     */
    private final int[] id;

    /**
     * Create an appropriate sequence to build Jenks classification.<br/>
     *
     * Note : sequence table length define classes number.
     *
     * @param sequence table which contains all fractionate possibilities.
     * @param elementsNumber total elements number to classifieds.
     * @param id table which contain index which change during iteration.
     */
    JenkSequence(int[] sequence, int elementsNumber, int[] id) {
        assert (sequence.length > 1) : "impossible to classify datas with"
                + " class number lesser 2";
        assert (elementsNumber >= sequence.length) :"impossible to classify datas"
                + " with class number larger than overall elements number";
        this.sequence = sequence;
        this.classNumber = sequence.length;
        this.cN_2     = classNumber - 2;
        for (int i = 0; i<classNumber-1; i++) {
            sequence[i] = i+1;
        }
        sequence[classNumber-2]--;
        sequence[classNumber-1] = elementsNumber;
        this.max = elementsNumber - classNumber + 2;
        this.id = id;
    }

    /**
     * Increment next Jenks sequence solution.
     *
     * @return true if next iterate exist else return false.
     */
    boolean next() {
        return next(cN_2);
    }

    /**
     * Increment appropriate Jenks table column.
     *
     * @param currentIndex current column to increment.
     * @return
     */
    private boolean next(int currentIndex) {
        if (currentIndex < 0) return false;
        id[0] = currentIndex;
        sequence[currentIndex]++;
        if (sequence[currentIndex] == max + currentIndex) {
            if (!next(currentIndex-1)) return false;
            sequence[currentIndex] = sequence[currentIndex - 1] + 1;
        }
        return true;
    }
}
