
package org.geotoolkit.pending.demo.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeFactory;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.calculator.DefaultCalculator;
import org.geotoolkit.index.tree.io.TreeReader;
import org.geotoolkit.index.tree.io.TreeWriter;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**R-tree uses.
 * Exist : R-Tree (BasicRTree), R*Tree (StarRTree), Hilbert R-Tree (HilbertRTree).
 * 
 * R-Tree         : fast indexing time, slow search query time.
 * R*Tree         : moderate indexing time, moderate search query time.
 * Hilbert R-Tree : slow indexing time, fast search query time.
 * 
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeDemo {
    
    public static void main(String[] args) throws TransformException, IOException, ClassNotFoundException {
        
        /**
         * Tree creation.
         */
        
        //Create crs which in what coordinate space tree is define--------------
        final CoordinateReferenceSystem crs = DefaultEngineeringCRS.CARTESIAN_2D;
        
        //Create Calculator. Be careful to choice calculator adapted from crs---
        final Calculator calculator = DefaultCalculator.CALCULATOR_2D;
        
        //In this demo basic r-tree requires split case. "LINEAR" or "QUADRATIC"
        final SplitCase splitcase = SplitCase.QUADRATIC;
        
        //creating tree (R-Tree)------------------------------------------------
        final Tree tree = TreeFactory.createBasicRTree(4, crs, splitcase, calculator);
        
        //Create an entry to add in tree----------------------------------------
        //NOTE : entry are GeneralEnvelope type---------------------------------
        final GeneralEnvelope entry = new GeneralEnvelope(crs);
        entry.setEnvelope(10, 20, 50, 150);
        
        //Insert entry in tree--------------------------------------------------
        tree.insert(entry);
        
        //Create empty GeneralEnvelope list to put search query results---------
        final List<GeneralEnvelope> resultList = new ArrayList<GeneralEnvelope>();
        
        //Create area search----------------------------------------------------
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        areaSearch.setEnvelope(-10, -20, 100, 200);
        
        //search----------------------------------------------------------------
        tree.search(areaSearch, resultList);
        
        //Delete an entry in tree-----------------------------------------------
        tree.delete(entry);
        
        
                                /*-------------------
                                  -------------------*/
        
        /**
         * For example another tree creation.
         */
        
        final CoordinateReferenceSystem anotherCrs = DefaultEngineeringCRS.CARTESIAN_3D;
        final Calculator anotherCalculator = DefaultCalculator.CALCULATOR_3D;
        
        //NOTE : no Splitcase required because split made is single in this tree case and contained in Tree body. 
        final Tree anotherTree = TreeFactory.createStarRTree(3, anotherCrs, anotherCalculator);
        
        //Same methods ---------------------------------------------------------
        anotherTree.insert(entry);
        anotherTree.search(areaSearch, resultList);
        anotherTree.delete(entry);
        
                                /*-------------------
                                  -------------------*/
        
        /**
         * Tree write and read.
         */
        
        //Create File to stock tree---------------------------------------------
        final File fil = new File("tree.bin");
        
        //Create tree writer----------------------------------------------------
        final TreeWriter treeWriter = new TreeWriter();
        
        //supported format : java.io.File, java.io.OutputStream-----------------
        treeWriter.setOutput(fil);
        
        //write-----------------------------------------------------------------
        treeWriter.write(tree);
        
        //Release potential locks or opened stream------------------------------
        treeWriter.dispose();
        
        //Close potential previous stream and cache if there are some-----------
        treeWriter.reset();
        
        final TreeReader treeReader = new TreeReader();
        treeReader.setInput(fil);
        
        //Create an R-Tree to re-build read result------------------------------
        final Tree resultTree = TreeFactory.createBasicRTree(4, crs, splitcase, calculator);
        
        //read (read result pushing in result tree)-----------------------------
        treeReader.read(resultTree);
        treeReader.dispose();
        treeReader.reset();
        
        //In another case, for a single write/read use exit static method-------
        //To write--------------------------------------------------------------
        TreeWriter.write(tree, fil);
        //to read---------------------------------------------------------------
        TreeReader.read(tree, fil);
        
                                /*-------------------
                                  -------------------*/
   }
}
