/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.style.bank;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StyleBanks {

    private static final List<StyleBank> BANKS = new ArrayList<StyleBank>();

    private StyleBanks(){}

    public static synchronized void registerBank(StyleBank bank){
        BANKS.add(bank);
    }

    public static synchronized void unregisterBank(StyleBank bank){
        BANKS.remove(bank);
    }

    public static synchronized List<StyleBank> getBanks(){
        return new ArrayList<StyleBank>(BANKS);
    }

    public static ElementNode createTree(ElementType type){
        return createTree(type,(StyleBank)null);
    }

    public static ElementNode createTree(ElementType type, StyleBank ... banks){
        if(banks == null || (banks.length == 1 && banks[0] == null)){
            banks = getBanks().toArray(new StyleBank[BANKS.size()]);
        }

        AbstractElementNode root = new DefaultGroupNode("root", null);

        for(StyleBank bank : banks){
            if(bank == null) continue;
            wrap(root, bank.getRoot(), type);
        }

        return root;
    }

    private static void wrap(AbstractElementNode root, ElementNode candidate, ElementType type){
        final ElementType candidateType = candidate.getType();

        if(!(candidateType.equals(ElementType.GROUP) || candidateType.equals(type))){
            //nothing to do
            return;
        }

        if(candidateType.equals(ElementType.GROUP)){
            //group node
            //check if a node of same name exist already
            AbstractElementNode futurParent = null;

            final String candidateName = candidate.toString();
            for(int i=0,n=root.getChildCount();i<n;i++){
                final ElementNode child = (ElementNode)root.getChildAt(i);
                final String str = child.toString();
                if(str.equalsIgnoreCase(candidateName) && child.getType().equals(ElementType.GROUP)){
                    //node exist
                    futurParent = (AbstractElementNode)child;
                }
            }

            if(futurParent == null){
                //no node of same name exist
                futurParent = new FilterNode(candidate);
                insertAlphabetic(root, futurParent);
            }

            for(int i=0,n=candidate.getChildCount();i<n;i++){
                wrap(futurParent, (ElementNode) candidate.getChildAt(i),type);
            }

        }else{
            //single node
            insertAlphabetic(root, new FilterNode(candidate));
        }

    }

    private static void insertAlphabetic(DefaultMutableTreeNode parent, MutableTreeNode child){
        final String childName = child.toString();
        for(int i=0,n=parent.getChildCount();i<n;i++){
            final String str = parent.getChildAt(i).toString();
            if(str.compareToIgnoreCase(childName) > 0){
                parent.insert(child, i);
            }
        }
        parent.add(child);
    }


}
