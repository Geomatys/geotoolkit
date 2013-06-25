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
package org.geotoolkit.index.tree.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.apache.sis.util.ArgumentChecks;

/**
 * Create TreeWriter object.
 *
 * <br/>
 * Example : <br/>
 * <pre>
 * {@code
 * final TreeWriter writer = new TreeWriter();
 * writer.setOutput(output);
 * writer.write(tree);
 * writer.dispose();
 * writer.reset();
 *
 * writer.setOutput(input2);...//for another output
 * }
 * </pre>
 * And should be used like :<br/>
 * <pre>
 * {@code
 * TreeWriter.write(arbre, fil);
 * }
 * </pre>
 *
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class TreeWriter {

    int inc = 0;
    private boolean closeOnDispose = false;
    private OutputStream sourceStream = null;
    private DataOutputStream dataOPStream = null;
    private Map<Node, Integer> index = null;

    public TreeWriter() {
    }

    /**
     * Set the output for this writer.<br/>
     * Handle types are :<br/>
     * - java.io.File<br/>
     * - java.io.OutputStream<br/>
     *
     * @param output
     * @throws IOException
     */
    public void setOutput(final Object output) throws IOException {
        index = new HashMap<Node, Integer>();
        if (output instanceof OutputStream) {
            sourceStream = (OutputStream) output;
            dataOPStream = new DataOutputStream(sourceStream);
            closeOnDispose = false;
            return;
        }

        closeOnDispose = true;
        if (output instanceof File) {
            sourceStream = new FileOutputStream((File) output);
            dataOPStream = new DataOutputStream(sourceStream);
        } else {
            throw new IOException("Unsuported output : " + output.getClass());
        }

    }

    /**Write tree in binary.
     *
     * @param tree
     * @throws IOException
     */
    public void write(final Tree tree) throws IOException {
        final Node root = (Node)tree.getRoot();
        if(root == null)return;
        createIndex(root);
        serializeNode(root, dataOPStream);
    }

    /**Write all node to binary.
     *
     * @param root
     * @param dops
     * @throws IOException
     */
    private void serializeNode(final Node root, final DataOutputStream dops) throws IOException {
        nodeToBinary(root, dops);
        final int nbChild = root.getChildCount();
        for (int i = 0; i < nbChild; i++) {
            serializeNode(root.getChild(i), dops);
        }
    }

    /**
     * Write node in binary.
     *
     * @param node
     * @param dops
     * @throws IOException
     */
    private void nodeToBinary(final Node node, final DataOutputStream dops) throws IOException {
        final List<Object> listEntries = new ArrayList<Object>();
        final List<double[]> listCoordinates = new ArrayList<double[]>();
        dops.writeInt(index.get(node));
        final double[] bound = node.getBoundary();
        final int dim = bound.length;
        dops.writeInt(dim >> 1);
        
        for (int i = 0; i < dim; i++) {
            dops.writeDouble(bound[i]);
        }
        if (node.isLeaf()) {
            dops.writeInt(0);
            int nbCell = node.getChildCount();
            for (int i = 0; i < nbCell; i++) {
                final Node cuCell = node.getChild(i);
                listEntries.addAll(Arrays.asList(cuCell.getObjects()));
                listCoordinates.addAll(Arrays.asList(cuCell.getCoordinates()));
            }
            listEntries.addAll(Arrays.asList(node.getObjects()));
            listCoordinates.addAll(Arrays.asList(node.getCoordinates()));
            final int siz = listEntries.size();
            assert siz == listCoordinates.size() : "tree writer : node to binary : listEntries and listCoordinates should have same length.";
            dops.writeInt(listEntries.size());
            for (int i = 0; i < siz; i++) {
                final ByteArrayOutputStream temp = new ByteArrayOutputStream();
                final ObjectOutputStream ost = new ObjectOutputStream(temp);
                
                //write object boundary
                final double[] objBound = listCoordinates.get(i);
                for (int bi = 0; bi < dim; bi++) {
                    dops.writeDouble(objBound[bi]);
                }
                //write object
                ost.writeObject(listEntries.get(i));
                temp.flush();
                final byte[] array = temp.toByteArray();
                dops.writeInt(array.length);
                dops.write(array);
            }
        } else {
            final int nbChild = node.getChildCount();
            dops.writeInt(nbChild);
            for (int i = 0; i < nbChild; i++) {
                dops.writeInt(index.get(node.getChild(i)));
            }
            dops.writeInt(0);
        }
    }

    /**
     * Find all tree node and affect an id for each them.
     *
     * @param node tree root node.
     */
    private void createIndex(final Node node) {
        ArgumentChecks.ensureNonNull("createIndex : tree", node);
        index.put(node, inc);
        final int nbChild = node.getChildCount();
        for (int i = 0; i < nbChild; i++) {
            inc++;
            createIndex(node.getChild(i));
        }
    }

    /**
     * Release potential locks or opened stream.
     * Must be called when the writer is not needed anymore.
     * It should not be used after this method has been called.
     */
    public void dispose() throws IOException {
        if (closeOnDispose) {
            sourceStream.close();
        }
        dataOPStream.close();
    }

    /**
     * Close potential previous stream and cache if there are some.
     * This way the writer can be reused for a different output later.
     * The underlying stax writer will be closed.
     */
    public void reset() {
        closeOnDispose = false;
        sourceStream = null;
        dataOPStream = null;
        index = null;
        inc = 0;
    }

    /**
     * To write one time without TreeWriter re-utilization.
     *
     * @param tree
     * @param output
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void write(final Tree tree, final Object output) throws IOException {
        ArgumentChecks.ensureNonNull("static write : tree", tree);
        ArgumentChecks.ensureNonNull("static write : output", output);
        final TreeWriter tW = new TreeWriter();
        tW.setOutput(output);
        tW.write(tree);
        tW.dispose();
    }
}
