/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.util.ArgumentChecks;
import static org.junit.Assert.assertTrue;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**Test some R-Tree queries.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class TreeTest {
    protected final Tree tree;
    protected final List<GeneralEnvelope> lData = new ArrayList<GeneralEnvelope>();
    protected final CoordinateReferenceSystem crs;
    protected final int dimension;
    
    public TreeTest(Tree tree, CoordinateReferenceSystem crs) throws TransformException {
        ArgumentChecks.ensureNonNull("tree", tree);
        ArgumentChecks.ensureNonNull("crs", crs);
        this.dimension = crs.getCoordinateSystem().getDimension();
        ArgumentChecks.ensurePositive("dimension", this.dimension);
        this.tree = tree;
        this.crs = crs;
        
        final DirectPosition centerEntry = new GeneralDirectPosition(crs);
        for(int i = 0; i<3000; i++){
            for(int nbCoords = 0; nbCoords<this.dimension; nbCoords++){
                double value = (Math.random() < 0.5) ? -1 : 1;
                value *= 1500 * Math.random();
                centerEntry.setOrdinate(nbCoords, value);
            }
            lData.add(createEntry(centerEntry));
        }
        insert();
    }
    
    
    private void insert() throws TransformException {
        for (GeneralEnvelope gEnv : lData) {
            tree.insert(gEnv);
        }
    }
    
    /**
     * Test if tree contain all elements inserted.
     */
    protected void insertTest() throws TransformException {
        final GeneralEnvelope gr = ((DefaultNode)tree.getRoot()).getBoundary();
        final GeneralEnvelope gem = DefaultTreeUtils.getEnveloppeMin(lData);
        assertTrue(gr.equals(gem, 1E-9, false));
        final List<GeneralEnvelope> listSearch = new ArrayList<GeneralEnvelope>();
        tree.search(gr, listSearch);
        assertTrue(listSearch.size() == lData.size());
    }

    /**
     * Compare all boundary node from their children boundary.
     */
    protected void checkBoundaryTest() throws TransformException {
        checkNodeBoundaryTest((DefaultNode)tree.getRoot());
    }

    /**
     * Compare boundary node from his children boundary.
     */
    protected void checkNodeBoundaryTest(final DefaultNode node) {        
        assertTrue(checkBoundaryNode(node));
        for (DefaultNode no : node.getChildren()) {
            checkNodeBoundaryTest(no);
        }
    }

    public void queryOnBorderTest() throws TransformException {
        final List<GeneralEnvelope> lGE = new ArrayList<GeneralEnvelope>();
        for(GeneralEnvelope ge : lData){
            tree.delete(ge);
        }
        final List<GeneralEnvelope> lGERef = new ArrayList<GeneralEnvelope>();
        final GeneralEnvelope gR = new GeneralEnvelope(crs);
        
        if(dimension == 2){
            for(int i = 0; i<20; i++){
                for(int j = 0; j<20; j++){
                    final GeneralEnvelope gE = new GeneralEnvelope(crs);
                    gE.setEnvelope(5*i,5*j,5*i,5*j);
                    lGE.add(gE);
                    if(i == 19 && j>3 &&j<18){
                        lGERef.add(gE);
                    }
                }
            }
            gR.setEnvelope(93, 18, 130, 87);
        }else{
            for(int i = 0; i<20; i++){
                for(int j = 0; j<20; j++){
                    final GeneralEnvelope gE = new GeneralEnvelope(crs);
                    gE.setEnvelope(5*i,5*j,20,5*i,5*j,20);
                    lGE.add(gE);
                    if(i == 19 && j>3 &&j<18){
                        lGERef.add(gE);
                    }
                }
            }
            gR.setEnvelope(93, 18, 19, 130, 87, 21);
        }
        
        for(GeneralEnvelope ge : lGE){
            tree.insert(ge);
        }
        
        final List<GeneralEnvelope> lGES = new ArrayList<GeneralEnvelope>(); 
        tree.search(gR, lGES);
        
        assertTrue(compareList(lGERef, lGES));
    }
    
    /**
     * Compare boundary node from his children boundary.
     */
    protected boolean checkBoundaryNode(final DefaultNode node) {
        final List<GeneralEnvelope> lGE = new ArrayList<GeneralEnvelope>();
        if (node.isLeaf()) {
            for (GeneralEnvelope gEnv : node.getEntries()) {
                lGE.add(gEnv);
            }

        } else {
            for (DefaultNode no : node.getChildren()) {
                lGE.add(no.getBoundary());
            }
        }
        final GeneralEnvelope subBound = DefaultTreeUtils.getEnveloppeMin(lGE);
        return subBound.equals(node.getBoundary());
    }

    /**
     * Test search query inside tree.
     */
    protected void queryInsideTest() throws TransformException {
        final List<GeneralEnvelope> listSearch = new ArrayList<GeneralEnvelope>();
        tree.search(DefaultTreeUtils.getEnveloppeMin(lData), listSearch);
        assertTrue(compareList(lData, listSearch));
    }

    /**
     * Test query outside of tree area.
     */
    protected void queryOutsideTest() throws TransformException {
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        for(int i = 0; i<dimension; i++){
            areaSearch.setRange(i, 1600, 2000);
        }
        final List<GeneralEnvelope> listSearch = new ArrayList<GeneralEnvelope>();
        tree.search(areaSearch, listSearch);
        assertTrue(listSearch.isEmpty());
    }


    /**
     * Test insertion and deletion in tree.
     */
    protected void insertDelete() throws TransformException {
        Collections.shuffle(lData);
        for (GeneralEnvelope env : lData) {
            tree.delete(env);
        }
        final GeneralEnvelope areaSearch = new GeneralEnvelope(crs);
        for(int i = 0; i<dimension; i++){
            areaSearch.setRange(i, -1500, 1500);
        }
        final List<GeneralEnvelope> listSearch = new ArrayList<GeneralEnvelope>();
        tree.search(areaSearch, listSearch);
        assertTrue(listSearch.isEmpty());
        insert();
        tree.search(areaSearch, listSearch);
        assertTrue(compareList(listSearch, lData));
    }

    /**
     * Compare 2 lists elements.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: return {@code true} if listA and listB are empty.</strong> 
     * </font></blockquote>
     * 
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareList(final List<GeneralEnvelope> listA, final List<GeneralEnvelope> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) {
            return true;
        }

        if (listA.size() != listB.size()) {
            return false;
        }

        boolean shapequals = false;
        for (GeneralEnvelope shs : listA) {
            for (GeneralEnvelope shr : listB) {
                if (shs.equals(shr, 1E-9, false)) {
                    shapequals = true;
                }
            }
            if (!shapequals) {
                return false;
            }
            shapequals = false;
        }
        return true;
    }
    
    public static GeneralEnvelope createEntry(final DirectPosition position){
        final double[] coord = position.getCoordinate();
        int length = coord.length;
        double[] coordLow = new double[length];
        double[] coordUpp = new double[length];
        for(int i = 0; i< length; i++){
            coordLow[i] = coord[i]-(Math.random() * 5 + 5);
            coordUpp[i] = coord[i]+(Math.random() * 5 + 5);
        }
        final CoordinateReferenceSystem crs = position.getCoordinateReferenceSystem();
        if(crs == null){
            return new GeneralEnvelope(new GeneralDirectPosition(coordLow), new GeneralDirectPosition(coordUpp));
        }
        final GeneralDirectPosition dpLow = new GeneralDirectPosition(crs);
        final GeneralDirectPosition dpUpp = new GeneralDirectPosition(crs);
        for(int i = 0; i<length; i++){
            dpLow.setOrdinate(i, coordLow[i]);
            dpUpp.setOrdinate(i, coordUpp[i]);
        }
        return new GeneralEnvelope(dpLow, dpUpp);
    }
    
}
