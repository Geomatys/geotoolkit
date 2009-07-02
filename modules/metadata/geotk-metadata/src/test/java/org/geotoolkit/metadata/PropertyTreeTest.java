/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Set;
import java.util.HashSet;
import java.text.ParseException;
import javax.swing.tree.TreeModel;

import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.util.InternationalString;

import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeNode;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link PropertyTree}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
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

        final TreeModel tree = citation.asTree();
        assertMultilinesEquals(
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
            "    └───image hardcopy\n", Trees.toString(tree));

        final TreeNode root = (TreeNode) tree.getRoot();
        assertSame(citation, root.getUserObject());
        assertEquals("Citation", root.toString());
        /*
         * Tests the "Cited Responsible Parties" node.
         */
        assertEquals(5, root.getChildCount());
        TreeNode node = (TreeNode) root.getChildAt(2);
        assertEquals("Cited Responsible Parties", node.toString());
        final Set<DefaultResponsibleParty> authors = new HashSet<DefaultResponsibleParty>();
        authors.add(author);
        authors.add(duplicated);
        assertEquals(authors, node.getUserObject());

        final DefaultCitation newCitation = new DefaultCitation();
        newCitation.parse(tree);
        assertEquals(authors, newCitation.getCitedResponsibleParties());

        // Following test fails until we fix the parsing of multi-occurences of CodeList.
//      System.out.println(Trees.toString(newCitation.asTree()));
//      assertEquals(citation, newCitation);
    }
}
