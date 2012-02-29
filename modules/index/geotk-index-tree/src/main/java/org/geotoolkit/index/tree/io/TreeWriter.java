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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.Envelope;

/**Create TreeWriter object.
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

    /**Set the output for this writer.<br/>
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
        for (Node child : root.getChildren()) {
            serializeNode(child, dops);
        }
    }

    /**Write node in binary.
     * 
     * @param node
     * @param dops
     * @throws IOException 
     */
    private void nodeToBinary(final Node node, final DataOutputStream dops) throws IOException {
        final List<Node> listChild = node.getChildren();
        final List<Envelope> listEntries = new ArrayList<Envelope>(node.getEntries());
        final int nbrSubNode = listChild.size();
        dops.writeInt(index.get(node));
        final Envelope bound = node.getBoundary();
        final int dim = bound.getDimension();
        dops.writeInt(dim);
        
        for(int i = 0; i<dim; i++){
            dops.writeDouble(bound.getLowerCorner().getOrdinate(i));
        }
        for(int i = 0; i<dim; i++){
            dops.writeDouble(bound.getUpperCorner().getOrdinate(i));
        }
        if(node.isLeaf()){
            dops.writeInt(0);
            for(Node no : node.getChildren()){
                listEntries.addAll(no.getEntries());
            }
            dops.writeInt(listEntries.size());
            for (Envelope gEnv : listEntries) {
                final ByteArrayOutputStream temp = new ByteArrayOutputStream();
                final ObjectOutputStream ost = new ObjectOutputStream(temp);
                ost.writeObject(gEnv);
                temp.flush();
                final byte[] array = temp.toByteArray();
                dops.writeInt(array.length);
                dops.write(array);
            }
        }else{
            dops.writeInt(nbrSubNode);
            for (Node child : listChild) {
                dops.writeInt(index.get(child));
            }
            dops.writeInt(0);
        }
        

            
            
    }

    /**Find all tree node and affect an id for each them.
     * 
     * @param node tree root node.
     */
    private void createIndex(final Node node) {
        ArgumentChecks.ensureNonNull("createIndex : tree", node);
        index.put(node, inc);
        for (Node child : node.getChildren()) {
            inc++;
            createIndex(child);
        }
    }

    /**Release potential locks or opened stream.
     * Must be called when the writer is not needed anymore.
     * It should not be used after this method has been called.
     */
    public void dispose() throws IOException {
        if (closeOnDispose) {
            sourceStream.close();
        }
        dataOPStream.close();
    }

    /**Close potential previous stream and cache if there are some.
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

    /**To write one time without TreeWriter re-utilization.
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
