/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.ArrayList;
import java.util.Collection;
import java.text.ParseException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;

import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.util.InternationalString;

import org.geotoolkit.test.Depend;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeNode;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link PropertyTree}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 */
@Depend(MetadataStandard.class)
public final class PropertyTreeTest {
    /**
     * Creates a tree from a metadata object and verifies that the tree contains
     * the expected values. Then remove values from this map.
     *
     * @throws ParseException Should not happen.
     */
    @Test
    public void testTree() throws ParseException {
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

        TreeModel tree = citation.asTree();
        assertMultilinesEquals(
            "Citation\n" +
            "├───Title\n" +
            "│   └───Undercurrent\n" +
            "├───Alternate Titles\n" +
            "│   ├───Alt A\n" +
            "│   └───Alt B\n" +
            "├───Cited Responsible Parties\n" +
            "│   ├───[1] Testsuya Toyoda\n" +
            "│   │   ├───Role\n" +
            "│   │   │   └───author\n" +
            "│   │   └───Individual Name\n" +
            "│   │       └───Testsuya Toyoda\n" +
            "│   └───[2] A japanese author\n" +
            "│       └───Individual Name\n" +
            "│           └───A japanese author\n" +
            "├───ISBN\n" +
            "│   └───9782505004509\n" +
            "└───Presentation Forms\n" +
            "    ├───document hardcopy\n" +
            "    └───image hardcopy\n", Trees.toString(tree));

        TreeNode root = (TreeNode) tree.getRoot();
        assertSame(citation, root.getUserObject());
        assertEquals("Citation", root.toString());
        /*
         * Tests the "Cited Responsible Parties" node.
         */
        assertEquals(5, root.getChildCount());
        TreeNode node = (TreeNode) root.getChildAt(2);
        assertEquals("Cited Responsible Parties", node.toString());
        final Collection<DefaultResponsibleParty> authors = new ArrayList<DefaultResponsibleParty>();
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
        root = Trees.parse(Trees.toString(tree));
        tree = new DefaultTreeModel(root);
        newCitation = new DefaultCitation();
        newCitation.parse(tree);
        assertEquals(citation, newCitation);
        /*
         * Parses a somewhat malformed (but still understandable) tree
         * and compares with the original citation.
         */
        root = Trees.parse(
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
}
