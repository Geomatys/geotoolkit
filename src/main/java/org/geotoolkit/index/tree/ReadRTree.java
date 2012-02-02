/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.index.tree.basic.SplitCase;

/**
 *
 * @author rmarech
 */
public class ReadRTree {
    private final Map<Integer, Node> index = new HashMap<Integer, Node>();
    final List<Node2D> lN = new ArrayList<Node2D>();
    Tree tree;
    String file_Url;
    public ReadRTree(String file_Url) {
        this.file_Url = file_Url;
    }
    
    public Tree readFile() throws ClassNotFoundException{
        try {
                FileInputStream fis = new FileInputStream(file_Url);
                DataInputStream dips = new DataInputStream(fis);
                int type = dips.readInt();
                int maxElements = dips.readInt();
                
                switch (type) {
                    case 0 : {
                        int splitcase = dips.readInt();
                        if(splitcase == 0){
                            tree = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, maxElements);
                        }else{
                            tree = TreeFactory.createBasicRTree2D(SplitCase.QUADRATIC, maxElements);
                        }
                    }break;
                    case 1 : tree = TreeFactory.createStarRTree2D(maxElements);break;
                    case 2 : {
                        int hilbertOrder = dips.readInt();
                        tree = TreeFactory.createHilbertRTree2D(maxElements, hilbertOrder);
                    }break;    
                }
                
                try{
                    readNode(dips);
                }catch(EOFException ex){
                    
                }
                tree.setRoot(getRoot());
                dips.close();
                fis.close();
                return tree;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        return null;
    }
    
    private Node2D getRoot(){
        for(Node2D node : lN){
            int[] tabC = (int[])node.getUserProperty("tabidchildren");
            final List<Node2D> children = node.getChildren();
            for(int i = 0; i<tabC.length; i++){
                Node2D child = (Node2D)index.get(tabC[i]);
                child.setParent(node);
                children.add(child);
            }
        }
        return (Node2D)index.get(0);
    }
    
    private void readNode(DataInputStream dips) throws IOException, ClassNotFoundException{
        
        int id = dips.readInt();
        int nbrChildren = dips.readInt();
        int[] tabChild = new int[nbrChildren];
        for(int i=0;i<nbrChildren;i++){
            tabChild[i] = dips.readInt();
        }
        int nbrEntries = dips.readInt();
        List<Shape> listEntries = new ArrayList<Shape>();
        for(int i = 0; i<nbrEntries;i++){
            int arrayLength = dips.readInt();
            byte[] tabB = new byte[arrayLength];
            dips.read(tabB, 0, arrayLength);
            ByteArrayInputStream bis = new ByteArrayInputStream(tabB);
            ObjectInputStream oins = new ObjectInputStream(bis);
            listEntries.add((Shape)oins.readObject());
        }
        Node2D result = new Node2D(tree, null, null, listEntries);
        result.setUserProperty("tabidchildren", tabChild);
        index.put(id, result);
        lN.add(result);  
        readNode(dips);
    }
    
}
