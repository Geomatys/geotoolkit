/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.text.ParseException;
import javax.swing.tree.TreeModel;
import javax.measure.unit.SI;

import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.util.InternationalString;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeNode;
import org.geotoolkit.gui.swing.tree.TreeFormat;
import org.geotoolkit.gui.swing.tree.DefaultTreeModel;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.content.DefaultBand;
import org.geotoolkit.metadata.iso.content.DefaultImageDescription;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessing;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link MetadataTreeFormat}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
@Depend(MetadataStandard.class)
public final strictfp class MetadataTreeFormatTest extends LocaleDependantTestBase {
    /**
     * Creates a tree from the given string representation. Each node must have at least one
     * {@code '─'} character (unicode 2500) in front of it. The number of spaces and drawing
     * characters ({@code '│'}, {@code '├'} or {@code '└'}) before the node determines its
     * indentation, and the indentation determines the parent of each node.
     */
    private static TreeNode parse(final String text) throws ParseException {
        return new TreeFormat().parseObject(text);
    }

    /**
     * Creates a band for the given minimum and maximum wavelengths, in centimetres.
     */
    private static DefaultBand createBand(final double min, final double max) {
        final DefaultBand band = new DefaultBand();
        band.setMinValue(min);
        band.setMaxValue(max);
        band.setUnits(SI.CENTIMETRE);
        return band;
    }

    /**
     * Creates the citation to use for testing purpose.
     */
    private static DefaultCitation createCitation() {
        final DefaultCitation citation = new DefaultCitation();
        final InternationalString title = new SimpleInternationalString("Undercurrent");
        citation.setTitle(title);
        citation.setISBN("9782505004509");
        citation.getPresentationForms().add(PresentationForm.DOCUMENT_HARDCOPY);
        citation.getPresentationForms().add(PresentationForm.IMAGE_HARDCOPY);
        citation.getAlternateTitles().add(new SimpleInternationalString("Alt A"));
        citation.getAlternateTitles().add(new SimpleInternationalString("Alt B"));

        final DefaultResponsibleParty author = new DefaultResponsibleParty();
        author.setIndividualName("Testsuya Toyoda");
        author.setRole(Role.AUTHOR);
        citation.getCitedResponsibleParties().add(author);
        final DefaultResponsibleParty duplicated = new DefaultResponsibleParty();
        duplicated.setIndividualName("A japanese author");
        citation.getCitedResponsibleParties().add(duplicated);
        return citation;
    }

    /**
     * Creates a tree table from a metadata object and verifies that the tree contains
     * the expected values.
     *
     * @since 3.19
     */
    @Test
    public void testTreeTable() {
        final DefaultCitation citation = createCitation();
        final MetadataTreeFormat pt = new MetadataTreeFormat(citation.getStandard());
        final String text = Trees.toString(pt.asTreeTable(citation));
        assertMultilinesEquals(
            "Citation\n" +
            "├───Title………………………………………………………………………………………………………… Undercurrent\n" +
            "├───Alternate Titles\n" +
            "│   ├───[1]…………………………………………………………………………………………………… Alt A\n" +
            "│   └───[2]…………………………………………………………………………………………………… Alt B\n" +
            "├───Cited Responsible Parties\n" +
            "│   ├───[1] Responsible party ‒ Testsuya Toyoda\n" +
            "│   │   ├───Role……………………………………………………………………………………… author\n" +
            "│   │   └───Individual Name………………………………………………………… Testsuya Toyoda\n" +
            "│   └───[2] Responsible party ‒ A japanese author\n" +
            "│       └───Individual Name………………………………………………………… A japanese author\n" +
            "├───ISBN…………………………………………………………………………………………………………… 9782505004509\n" +
            "└───Presentation Forms\n" +
            "    ├───[1]…………………………………………………………………………………………………… document hardcopy\n" +
            "    └───[2]…………………………………………………………………………………………………… image hardcopy\n", text);
    }

    /**
     * Creates a tree from a metadata object and verifies that the tree contains
     * the expected values. Then remove values from the map and test again.
     *
     * @throws ParseException Should not happen.
     */
    @Test
    public void testCitationTree() throws ParseException {
        final DefaultCitation citation = createCitation();
        final ResponsibleParty author, duplicated;
        if (true) {
            final Iterator<ResponsibleParty> it = citation.getCitedResponsibleParties().iterator();
            assertTrue (it.hasNext()); author     = it.next();
            assertTrue (it.hasNext()); duplicated = it.next();
            assertFalse(it.hasNext());
        }
        TreeModel tree = citation.asTree();
        assertMultilinesEquals(
            "Citation\n" +
            "├───Title\n" +
            "│   └───Undercurrent\n" +
            "├───Alternate Titles\n" +
            "│   ├───Alt A\n" +
            "│   └───Alt B\n" +
            "├───Cited Responsible Parties\n" +
            "│   ├───[1] Responsible party ‒ Testsuya Toyoda\n" +
            "│   │   ├───Role\n" +
            "│   │   │   └───author\n" +
            "│   │   └───Individual Name\n" +
            "│   │       └───Testsuya Toyoda\n" +
            "│   └───[2] Responsible party ‒ A japanese author\n" +
            "│       └───Individual Name\n" +
            "│           └───A japanese author\n" +
            "├───ISBN\n" +
            "│   └───9782505004509\n" +
            "└───Presentation Forms\n" +
            "    ├───document hardcopy\n" +
            "    └───image hardcopy\n", tree.toString());

        TreeNode root = (TreeNode) tree.getRoot();
        assertSame(citation, root.getUserObject());
        assertEquals("Citation", root.toString());
        /*
         * Tests the "Cited Responsible Parties" node.
         */
        assertEquals(5, root.getChildCount());
        TreeNode node = (TreeNode) root.getChildAt(2);
        assertEquals("Cited Responsible Parties", node.toString());
        final Collection<ResponsibleParty> authors = new ArrayList<>();
        authors.add(author);
        authors.add(duplicated);
        assertEquals(authors, node.getUserObject());
        /*
         * Parses the tree and compare with the original citation.
         */
        DefaultCitation newCitation = new DefaultCitation();
        newCitation.parse(tree);
        assertEquals(authors, newCitation.getCitedResponsibleParties());
        assertEquals(citation, newCitation);
        /*
         * Formats and parse again the tree. The values stored in TreeNode.getUserObject()
         * should be lost in this process, so we are testing the capability to parse code
         * list string here (the previous test took the code directly from the user object).
         */
        root = parse(tree.toString());
        tree = new DefaultTreeModel(root);
        newCitation = new DefaultCitation();
        newCitation.parse(tree);
        assertEquals(citation, newCitation);
        /*
         * Parses a somewhat malformed (but still understandable) tree
         * and compares with the original citation.
         */
        root = parse(
            "Citation\n" +
            "├───Title\n" +
            "│   └───Undercurrent\n" +
            "├───Alternate Titles\n" +
            "│   ├───Alt A\n" +
            "│   └───Alt B\n" +
            "├───Cited Responsible Parties\n" +
            "│   ├───Role\n" +
            "│   │   └───author\n" +
            "│   ├───Individual Name\n" +
            "│   │   └───Testsuya Toyoda\n" +
            "│   └───Individual Name\n" +
            "│       └───A japanese author\n" +
            "├───ISBN\n" +
            "│   └───9782505004509\n" +
            "└───Presentation Forms\n" +
            "    ├───document hardcopy\n" +
            "    └───image hardcopy\n");
        tree = new DefaultTreeModel(root);
        newCitation = new DefaultCitation();
        newCitation.parse(tree);
        assertEquals(citation, newCitation);
    }

    /**
     * Tests a tree of metadata in which no {@link CharSequence} can be found for
     * generating the node label. The {@link MetadataTreeFormat#getTitle} method should
     * fallback on the code list.
     *
     * @since 3.18
     */
    @Test
    public void testTreeWithUntitledElements() {
        final DefaultCitation   titled = new DefaultCitation("Some specification");
        final DefaultCitation    coded = new DefaultCitation();
        final DefaultCitation untitled = new DefaultCitation();
        titled  .getPresentationForms().add(PresentationForm.DOCUMENT_HARDCOPY);
        coded   .getPresentationForms().add(PresentationForm.IMAGE_HARDCOPY);
        untitled.getCitedResponsibleParties().add(new DefaultResponsibleParty(Role.AUTHOR));
        final DefaultProcessing processing = new DefaultProcessing();
        processing.getDocumentations().add(titled);
        processing.getDocumentations().add(coded);
        processing.getDocumentations().add(untitled);

        TreeModel tree = processing.asTree();
        assertMultilinesEquals(
            "Processing\n" +
            "└───Documentations\n" +
            "    ├───[1] Citation ‒ Some specification\n" +
            "    │   ├───Title\n" +
            "    │   │   └───Some specification\n" +
            "    │   └───Presentation Forms\n" +
            "    │       └───document hardcopy\n" +
            "    ├───[2] Citation ‒ Image hardcopy\n" +
            "    │   └───Presentation Forms\n" +
            "    │       └───image hardcopy\n" +
            "    └───[3] Citation\n" +
            "        └───Cited Responsible Parties\n" +
            "            └───Role\n" +
            "                └───author\n", tree.toString());
    }

    /**
     * Tests a band specified by a range of wavelength. The main purpose of this method is to test
     * the {@code MetadataTreeFormat.getTitleForSpecialCases(Object)} method. The result of that method
     * is the summary that appears on the right side of [1] and [2].
     *
     * @since 3.20
     */
    @Test
    public void testTitleForSpecialCases() {
        final DefaultImageDescription image = new DefaultImageDescription();
        image.getDimensions().add(createBand(0.25, 0.26));
        image.getDimensions().add(createBand(0.28, 0.29));
        assertMultilinesEquals(
                "Image Description\n" +
                "└───Dimensions\n" +
                "    ├───[1] Band ‒ [0.25 … 0.26] cm\n" +
                "    │   ├───Units……………………………………………… cm\n" +
                "    │   ├───Max Value…………………………………… 0,26\n" +
                "    │   └───Min Value…………………………………… 0,25\n" +
                "    └───[2] Band ‒ [0.28 … 0.29] cm\n" +
                "        ├───Units……………………………………………… cm\n" +
                "        ├───Max Value…………………………………… 0,29\n" +
                "        └───Min Value…………………………………… 0,28\n", image.toString());
    }

    /**
     * Tests a tree of metadata containing custom topic categories.
     * Note that adding enumeration values is normally not allowed,
     * so a future version of this test may need to change the code type.
     * <p>
     * Tests also a tree of metadata containing more than one keyword.
     *
     * @since 3.19
     */
    @Test
    public void testTreeWithCustomElements() {
        final DefaultKeywords keywords = new DefaultKeywords();
        keywords.setKeywords(Arrays.asList(
                new SimpleInternationalString("Apple"),
                new SimpleInternationalString("Orange"),
                new SimpleInternationalString("Kiwi")));

        final DefaultDataIdentification identification = new DefaultDataIdentification();
        identification.setDescriptiveKeywords(Collections.singleton(keywords));
        identification.setTopicCategories(Arrays.asList(
                TopicCategory.HEALTH,
                TopicCategory.valueOf("OCEANS"), // Existing category
                TopicCategory.valueOf("test"))); // Custom category

        TreeModel tree = identification.asTree();
        assertMultilinesEquals(
            "Data Identification\n" +
            "├───Topic Categories\n" +
            "│   ├───health\n" +
            "│   ├───oceans\n" +
            "│   └───test\n" +
            "└───Descriptive Keywords\n" +
            "    └───Keywords\n" +
            "        ├───Apple\n" +
            "        ├───Orange\n" +
            "        └───Kiwi\n", tree.toString());

        assertMultilinesEquals(
            "Data Identification\n" +
            "├───Topic Categories\n" +
            "│   ├───[1]………………………………… health\n" +
            "│   ├───[2]………………………………… oceans\n" +
            "│   └───[3]………………………………… test\n" +
            "└───Descriptive Keywords\n" +
            "    └───Keywords\n" +
            "        ├───[1]……………………… Apple\n" +
            "        ├───[2]……………………… Orange\n" +
            "        └───[3]……………………… Kiwi\n", identification.toString());
    }

    /**
     * Tests the methods from the {@link java.text.Format} class.
     *
     * @throws ParseException If the parsing failed.
     *
     * @since 3.20
     */
    @Test
    public void testFormatAndParse() throws ParseException {
        final DefaultCitation citation = createCitation();
        final MetadataTreeFormat    format   = new MetadataTreeFormat(citation.getStandard());
        final DefaultCitation parsed   = (DefaultCitation) format.parseObject(format.format(citation));
        assertNotSame(citation, parsed);
        assertEquals (citation, parsed);
    }
}
