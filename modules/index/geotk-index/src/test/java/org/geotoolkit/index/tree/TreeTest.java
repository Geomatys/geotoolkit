/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.tree.TreeUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.junit.After;
import org.opengis.geometry.Envelope;

/**
 * Class which contains tree test utils methods.
 *
 * @author Rémi Maréchal (Géomatys).
 */
public abstract class TreeTest {

    protected Logger LOGGER = Logging.getLogger(TreeTest.class);

    /**
     * A temporary directory which will contains all files needed for Tree testing. Directory is deleted after each test
     * using {@linkplain #deleteTempFiles()} method.
     */
    protected final File tempDir;

    protected TreeTest() throws IOException {
        tempDir = Files.createTempDirectory("treetest").toFile();
    }

    @After
    public void deleteTempFiles() throws IOException {
        Files.walkFileTree(tempDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    /**
     * Compare 2 lists elements.
     *
     * <blockquote><font size=-1> <strong> NOTE: return {@code true} if listA
     * and listB are empty. </strong> </font></blockquote>
     *
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareList(final List listA, final List listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) return true;
        if (listA.size() != listB.size()) return false;

        boolean shapequals = false;
        for (Object objA : listA) {
            final Envelope shs = (Envelope) objA;
            for (Object objB : listB) {
                final Envelope shr = (Envelope) objB;
                if (new GeneralEnvelope(shs).equals(shr, 1E-9, false)) {
                    shapequals = true;
                    break;
                }
            }
            if (!shapequals) return false;
            shapequals = false;
        }
        return true;
    }
    
    protected boolean compareID (final int[] tabA, final int[] tabB) {
        if (tabA.length != tabB.length) return false;
        if (tabA.length == 0 && tabB.length == 0) return true;
        for (int intA : tabA) {
            boolean found = false;
            for (int intB : tabB) {
                if (intA == intB) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
    
    /**
     * Compare 2 lists elements.
     *
     * <blockquote><font size=-1> <strong> NOTE: return {@code true} if listA
     * and listB are empty. </strong> </font></blockquote>
     *
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareLists(final List<double[]> listA, final List<double[]> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) return true;
        if (listA.size() != listB.size()) return false;

        boolean shapequals = false;
        for (double[] objA : listA) {
            for (double[] objB : listB) {
                if (Arrays.equals(objA, objB)) {
                    shapequals = true;
                    break;
                }
            }
            if (!shapequals) return false;
            shapequals = false;
        }
        return true;
    }
    
    /**
     * Return boundary of all element union from list parameter.
     * 
     * @param list
     * @return boundary of all elements union from list parameter.
     */
    protected double[] getEnvelopeMin(final List<Envelope> list) {
        ArgumentChecks.ensureNonNull("compareList : listA", list);
        assert(!list.isEmpty()):"list to get envelope min should not be empty.";
        final double[] ge = TreeUtilities.getCoords(list.get(0));
        for (int i = 1; i < list.size();i++) {
            TreeUtilities.add(ge, TreeUtilities.getCoords(list.get(i)));
        }
        return ge;
    }
    
    /**
     * Return boundary of all element union from list parameter.
     * 
     * @param list
     * @return boundary of all elements union from list parameter.
     */
    protected double[] getExtent(final List<double[]> list) {
        ArgumentChecks.ensureNonNull("compareList : listA", list);
        assert(!list.isEmpty()):"list to get envelope min should not be empty.";
        final double[] ge = list.get(0).clone();
        for (int i = 1; i < list.size(); i++) {
            TreeUtilities.add(ge, list.get(i));
        }
        return ge;
    }
    
    /**
     * Create a default adapted test entry({@code GeneralEnvelope}).
     *
     * @param position the median of future entry.
     * @return {@code GeneralEnvelope} entry.
     */
    public static double[] createEntry(final double[] position) {
        final int length = position.length;
        final double[] envelope = new double[length << 1];
        for (int i = 0; i < length; i++) {
            envelope[i] = position[i] - (Math.random() * 5 + 5);
            envelope[i+length] = position[i] + (Math.random() * 5 + 5);
        }
        return envelope;
    }
}
