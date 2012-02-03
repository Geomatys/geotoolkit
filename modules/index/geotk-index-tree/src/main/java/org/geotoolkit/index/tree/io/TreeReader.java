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
package org.geotoolkit.index.tree.io;

import java.awt.Shape;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.index.tree.AbstractNode;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.util.ArgumentChecks;

/**Create TreeReader object.
 *
 * <br/>
 * Example : <br/>
 * <pre>
 * {@code
 *      final TreeReader reader = new TreeReader();
 *      reader.setInput(input);
 *      reader.read(tree);
 *      reader.dispose();
 *      reader.reset(); 
 * 
 *      reader.setInput(input2);...//for another input
 * }
 * </pre>
 * And should be used like :<br/>
 * <pre>
 * {@code
 *      TreeReader.read(tree, File);
 * }
 * <blockquote><font size=-1>
 * <strong>NOTE: tree root node will be exchange with read node from file</strong> 
 * </font></blockquote>
 * </pre>
 * 
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class TreeReader {

    private boolean closeOnDispose = false;
    private InputStream sourceStream = null;
    private DataInputStream dataIPStream = null;

    public TreeReader() {
    }

    /**
     * Set the input for this reader.<br/>
     * Handle types are :<br/>
     * - java.io.File<br/>
     * - java.io.InputStream<br/>
     * - java.net.URL<br/>
     * - java.net.URI<br/>
     * 
     * @param input
     * @throws IOException
     */
    public void setInput(final Object input) throws IOException {
        ArgumentChecks.ensureNonNull("static read : input", input);
        if (input instanceof InputStream) {
            sourceStream = (InputStream) input;
            dataIPStream = new DataInputStream(sourceStream);
            closeOnDispose = false;
            return;
        }

        closeOnDispose = true;
        if (input instanceof File) {
            sourceStream = new FileInputStream((File) input);
        } else if (input instanceof URL) {
            sourceStream = ((URL) input).openStream();
        } else if (input instanceof URI) {
            sourceStream = ((URI) input).toURL().openStream();
        } else {
            throw new IOException("Unsuported input : " + input.getClass());
        }
        dataIPStream = new DataInputStream(sourceStream);
    }

    /**
     * close potential previous stream and cache if there are some.
     * This way the reader can be reused for a different input later.
     * The underlying stax reader will be closed.
     */
    public void reset() {
        closeOnDispose = false;
        sourceStream = null;
        dataIPStream = null;
    }

    /**
     * Read and re-create R-Tree.
     * 
     * @param tree  where result is affect.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void read(final Tree tree) throws IOException, ClassNotFoundException {
        ArgumentChecks.ensureNonNull("read : tree", tree);
        final List<Node2D> listNodes = new ArrayList<Node2D>();
        final Map<Integer, AbstractNode> index = new HashMap<Integer, AbstractNode>();
        readNode(tree, dataIPStream, listNodes, index);

        for (Node2D node : listNodes) {
            int[] tabC = (int[]) node.getUserProperty("tabidchildren");
            final List<Node2D> children = node.getChildren();
            for (int i = 0; i < tabC.length; i++) {
                Node2D child = (Node2D) index.get(tabC[i]);
                child.setParent(node);
                children.add(child);
            }
        }
        tree.setRoot((Node2D) index.get(0));
    }

    /**
     * Read and create one appropriate tree {@code Node}.
     * 
     * @param tree
     * @param dips 
     * @param listNodes to stock all node.
     * @param index to stock all node with them children id.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private void readNode(final Tree tree, final DataInputStream dips, final List<Node2D> listNodes, final Map<Integer, AbstractNode> index) throws IOException, ClassNotFoundException {
        ArgumentChecks.ensureNonNull("readNode : tree", tree);
        ArgumentChecks.ensureNonNull("readNode : dips", dips);
        ArgumentChecks.ensureNonNull("readNode : listNodes", listNodes);
        ArgumentChecks.ensureNonNull("readNode : index", index);
        if (dips.available() > 0) {
            int id = dips.readInt();
            int nbrChildren = dips.readInt();
            int[] tabChild = new int[nbrChildren];
            for (int i = 0; i < nbrChildren; i++) {
                tabChild[i] = dips.readInt();
            }
            int nbrEntries = dips.readInt();
            final List<Shape> listEntries = new ArrayList<Shape>();
            for (int i = 0; i < nbrEntries; i++) {
                int arrayLength = dips.readInt();
                byte[] tabB = new byte[arrayLength];
                dips.read(tabB, 0, arrayLength);
                ByteArrayInputStream bis = new ByteArrayInputStream(tabB);
                ObjectInputStream oins = new ObjectInputStream(bis);
                listEntries.add((Shape) oins.readObject());
            }
            final Node2D result = tree.createNode(tree, null, null, listEntries);
            result.setUserProperty("tabidchildren", tabChild);
            index.put(id, result);
            listNodes.add(result);
            readNode(tree, dips, listNodes, index);
        }
    }

    /**
     * Release potential locks or opened stream.
     * Must be called when the reader is not needed anymore.
     * It should not be used after this method has been called.
     */
    public void dispose() throws IOException {
        if (closeOnDispose) {
            sourceStream.close();
        }
        dataIPStream.close();
    }

    /**
     * To read one time without TreeReader re-utilization.
     * 
     * @param tree
     * @param input
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public static void read(final Tree tree, final Object input) throws IOException, ClassNotFoundException {
        ArgumentChecks.ensureNonNull("static read : tree", tree);
        ArgumentChecks.ensureNonNull("static read : input", input);
        final TreeReader reader = new TreeReader();
        reader.setInput(input);
        reader.read(tree);
        reader.dispose();
    }
}
