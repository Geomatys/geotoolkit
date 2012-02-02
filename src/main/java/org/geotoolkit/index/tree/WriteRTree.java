/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.hilbert.HilbertRTree;
import org.geotoolkit.index.tree.star.StarRTree;

/**
 *
 * @author rmarech
 */
public class WriteRTree {
    
    private int inc = 0;
    private final Map<Node, Integer> index = new HashMap<Node, Integer>();
    String file_Url;
    public WriteRTree(final Tree tree, String file_Url) {
        this.file_Url = file_Url;
        createIndex(tree.getRoot());
        serializeTreeInBinary(tree);
    }
    
    private void serializeTreeInBinary(final Tree tree){
        
        try {
                FileOutputStream fout = new FileOutputStream(file_Url);
                DataOutputStream dops = new DataOutputStream(fout);
                
                if(tree instanceof BasicRTree){
                    dops.writeInt(0);
                    dops.writeInt(tree.getMaxElements());
                    switch (((BasicRTree)tree).getSplitCase()) {
                        case LINEAR : dops.writeInt(0);break;
                        case QUADRATIC : dops.writeInt(1);break;
                        default: throw new IllegalStateException("split made not conform");
                    } 
                }else if(tree instanceof StarRTree){
                    dops.writeInt(1);
                    dops.writeInt(tree.getMaxElements());
                }else if(tree instanceof HilbertRTree){
                    dops.writeInt(2);
                    dops.writeInt(tree.getMaxElements());
                    dops.writeInt(((HilbertRTree)tree).getHilbertOrder());
                }else{
                    throw new IllegalArgumentException("not implemented yet");
                }
                
                serializeNode(tree.getRoot(), dops);
                dops.close();
                fout.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
    }
    
    private void serializeNode(final Node2D root, final DataOutputStream dops) throws IOException{
        nodeToBinary(root, dops);
        for(Node2D child : root.getChildren()){
            serializeNode(child, dops);
        }
    }
    
    private void nodeToBinary(final Node2D node, final DataOutputStream dops) throws IOException{
        
        final List<Node2D> listChild = node.getChildren();
        final List<Shape> listEntries = node.getEntries();
        
        int nbrSubNode = listChild.size();
        dops.writeInt(index.get(node));
        dops.writeInt(nbrSubNode);
        
        for(Node2D child : listChild){
            dops.writeInt(index.get(child));//sur 4 octets
        }
        
        dops.writeInt(listEntries.size());
        for(Shape shape : listEntries){
            final ByteArrayOutputStream temp = new ByteArrayOutputStream(); //tableau de byte dans lequel on peut ecrire         
            final ObjectOutputStream     ost = new ObjectOutputStream(temp);//on ouvre un flux sur un tableau de byte 
            ost.writeObject(shape); //on ecrit le shape dans le tableau de byte
            temp.flush();//on vide le buffer
            final byte[] array = temp.toByteArray();
            dops.writeInt(array.length);
            dops.write(array);
        }
    }
    
    private void createIndex(final Node2D node){
        index.put(node, inc);
        for(Node2D child : node.getChildren()){
            inc++;
            createIndex(child);
        }
    }
    
}
