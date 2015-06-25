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
package org.geotoolkit.index.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.junit.Test;
import static org.geotoolkit.internal.tree.TreeUtilities.*;
import static org.geotoolkit.index.tree.TreeTest.createEntry;
import org.geotoolkit.internal.tree.TreeAccess;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 * Test suite adapted for all {@link Tree} implementation.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class AbstractTreeTest extends TreeTest {

    private final static double[][] arrayDebug = new double[][]{
new double[]{-980346.4430034263, -526162.668747636, -776264.8662152946, -980332.308594978, -526145.0591787294, -776250.1295863811},
new double[]{-526263.179797182, -803196.0523274908, 584298.7649260454, -526245.209595725, -803179.7170178088, 584316.1388724488},
new double[]{-595453.00252313, 330997.2627468758, -796826.8980975284, -595438.0556881789, 331012.51748896675, -796814.3804312411},
new double[]{-227755.59749817767, -561358.9570429884, 383514.3621946512, -227740.05199853244, -561345.3818993641, 383529.6051377441},
new double[]{-995453.6916074401, -804217.4348699134, -852645.5795981302, -995439.0245794562, -804200.1619034293, -852629.3588871068},
new double[]{-716311.4656436287, 627882.9287842467, -30680.590483743188, -716295.319816927, 627900.1679316461, -30667.65008264872},
new double[]{-173609.88614128428, -601320.7974512527, 497554.67303074687, -173597.3321008997, -601302.5040370716, 497566.8996422544},
new double[]{-850886.5745305788, -654059.0780844876, -797846.1434047627, -850867.4568674995, -654045.9006917694, -797831.8364075569},
new double[]{-907280.2210483468, -642889.5101330385, -710548.7872109037, -907265.0355924562, -642872.8484268659, -710530.1669301436},
new double[]{-295525.8248016188, -650403.7195092752, 195516.6705000603, -295508.57663582754, -650386.8543582336, 195529.09292964093},
new double[]{-766506.6954778621, -869300.9200436765, -762359.0267281348, -766492.6957921499, -869287.1356206608, -762343.9710434712},
new double[]{-359613.30743568414, -998540.1439461975, -350266.5612688045, -359596.94400161755, -998526.2731886194, -350248.07200263604},
new double[]{-971491.9413520083, 93448.233556127, -859691.3643556037, -971473.164978738, 93462.498798241, -859677.942133168},
new double[]{-354554.00078317954, -484301.47330339457, -874443.8361833647, -354541.6710558171, -484285.45586015395, -874428.978154598},
new double[]{123604.03645307697, -643549.6039161666, 463558.3346306425, 123619.64605165819, -643532.3415280822, 463574.9518157973},
new double[]{-927395.4279359913, -975753.2633008842, -955531.4101166882, -927380.1656097987, -975738.3444757913, -955515.6619180357},
new double[]{-564149.0290311729, -515723.6524135336, -655738.6512406804, -564133.6004643597, -515706.65532938036, -655723.4222590016},
new double[]{-271653.8538486167, 7035.172938541182, -650196.7324377263, -271640.6071269636, 7047.24067999257, -650180.0126039707},
new double[]{-722777.2360137524, -24787.096392963096, -207040.25672302427, -722765.6155430463, -24774.25370055921, -207020.82090488842},
new double[]{-647049.7972461687, -940252.2784184912, 127042.38826388985, -647034.4204248221, -940240.1203004941, 127053.83874708402},
new double[]{-27837.100976342495, -717093.3212158195, -266863.58472078486, -27819.290764367397, -717076.3969868678, -266847.66020105063},
new double[]{-967526.070169311, -799210.2652295001, -795596.5122267187, -967509.6553098608, -799191.4456744125, -795579.6401395245},
new double[]{-856439.5956165493, -997576.7658321987, -973216.3174369172, -856425.24394434, -997562.107887719, -973204.1832565955},
new double[]{-953835.8159461179, -757508.1949167021, -273216.97114063613, -953817.9382470429, -757488.7505947488, -273206.7485908548},
new double[]{-101278.98040247262, -540858.8981096268, -947159.9389568099, -101264.39518483185, -540842.7374561762, -947147.3096263538},
new double[]{-654126.1903951932, -72684.24709051351, -606475.1806549956, -654108.3361826921, -72670.96064567368, -606461.1139828806},
new double[]{-445486.8384708213, 142337.56709348824, -322987.50526066206, -445472.422342746, 142351.52559726132, -322972.59795295907},
new double[]{-70726.5854404038, 379795.6370919833, -439113.039325333, -70712.22701140227, 379815.1496695926, -439100.19819192484},
new double[]{135265.97038807062, -250510.6828814473, -621219.0591651837, 135282.23902257337, -250495.7580102428, -621204.6146452634},
new double[]{-359123.0639454564, -923908.6256253776, -468385.9400536135, -359108.5408086499, -923894.2412817238, -468373.9618462364},
new double[]{-942701.8501902189, -219181.0000417184, -627241.0303802935, -942685.8799334939, -219169.12297390937, -627229.7440934058},
new double[]{-315194.25626007706, -59484.19413145838, 538888.1691701991, -315174.817634731, -59469.942299340975, 538906.3320714358},
new double[]{-989036.7678360209, -726975.0163973301, -845043.2868478629, -989023.1467115427, -726957.2548463398, -845030.3018992818},
new double[]{-740800.7185408976, -603378.3360909522, -229174.54107506355, -740788.7772822541, -603364.7981489016, -229162.76286987524},
new double[]{-328076.4381718186, -995866.9784179382, -858796.9959351826, -328059.43519962643, -995854.1210962678, -858780.8050069036},
new double[]{-889611.8647443755, -577158.3056795199, -812119.1420867577, -889594.7081806868, -577143.9124238157, -812104.0489462247},
new double[]{-690075.2011028142, -516015.2364669381, -939183.3492343862, -690061.6186085981, -516002.30926917377, -939166.6477982638},
new double[]{-366915.10545854515, 274292.62514962937, -79253.76816038381, -366902.0039353741, 274304.826283992, -79236.5198689345},
new double[]{-348362.73115810665, -883214.0688655177, -954309.1760237141, -348348.0674105806, -883195.3222144463, -954293.904521432},
new double[]{-949984.6798661205, -795889.9844696225, -374127.62394401146, -949968.5291052449, -795876.3717505046, -374113.0304327581},
new double[]{-255892.11923003927, -709771.257198216, -433051.2032356186, -255879.37494010213, -709756.3173018729, -433037.2112078075},
new double[]{-996125.6048868102, -211301.48596765596, -30498.290971723374, -996111.9335821618, -211285.19443976055, -30485.858483544813},
new double[]{317996.7005240274, -953150.5220603021, -946595.651135171, 318012.3302653044, -953131.9691432299, -946585.1115981195},
new double[]{-866150.3602601975, -626952.0041214986, -834064.9229295708, -866136.7296430256, -626934.9567871606, -834048.1940703879}};
    
    
    /**
     * data number inserted in Tree.
     */
    private final int lSize = 300;
    
    /**
     * Data list which contain data use in this test series.
     */
    private final List<double[]> lData = new ArrayList<double[]>();
    
    /**
     * Tree CRS.
     */
    protected final CoordinateReferenceSystem crs;
    
    /**
     * Dimension of Tree CRS space.
     */
    private final int dimension;
    
    /**
     * double table which contain "extends" area of all data.
     */
    private final double[] minMax;
    
    /**
     * Contain Tree Node architecture.
     */
    protected TreeAccess tAF;
    
    /**
     * Tested Tree.
     */
    protected Tree<double[]> tree;
    
    /**
     * Do link between between TreeIdentifier and objects.
     */
    protected TreeElementMapper<double[]> tEM;
    
    /**
     * Create tests series from specified {@link CoordinateReferenceSystem}.
     * 
     * @param crs 
     */
    protected AbstractTreeTest(final CoordinateReferenceSystem crs) throws IOException {
        super();
        this.crs = crs;
        this.dimension = crs.getCoordinateSystem().getDimension();
        ArgumentChecks.ensurePositive("dimension", this.dimension);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        minMax = new double[2 * dimension];
        final double cartesianValue = 1E6;
        for (int i = 0; i < dimension; i++) {
            final CoordinateSystemAxis csa = cs.getAxis(i);
            final double minV = csa.getMinimumValue();
            minMax[i] = ( ! Double.isInfinite(minV)) ? minV : (minV < 0) ? -cartesianValue : cartesianValue;
            final double maxV = csa.getMaximumValue();
            minMax[i + dimension] = ( ! Double.isInfinite(maxV)) ? maxV : (maxV < 0) ? -cartesianValue : cartesianValue;
        }
        final double[] centerEntry = new double[dimension];
//        for (int i = 0; i < lSize; i++) {
//            for (int d = 0; d < dimension; d++) {
//                centerEntry[d] = (minMax[d+dimension]-minMax[d]) * Math.random() * Math.random() + minMax[d];
//            }
//            lData.add(createEntry(centerEntry));
//        }
        for (double[] entry : arrayDebug) {
            lData.add(entry);
        }
    } 
    
    /**
     * Create test series from specified {@link Tree}.
     * 
     * @param tree Tree which will be test.
     */
    protected AbstractTreeTest(final Tree tree) throws IOException {
        this(tree.getCrs());
        this.tree = tree;
        this.tEM  = tree.getTreeElementMapper();
    }

    /**
     * Insert appropriate elements in Tree.
     * 
     * @throws StoreIndexException if problem during insertion.
     * @throws IOException if problem during {@link TreeElementMapper#clear() } method.
     */
    protected void insert() throws StoreIndexException, IOException {
        tEM.clear();
        for (int i = 0, s = lData.size(); i < s; i++) {
            final double[] envData = lData.get(i).clone();
//            try{
//                tree.insert(envData);
//            } catch (Exception ex){
//                if (i < 65) {
//                    System.out.println("i = "+i);
//                for (int j = 0; j <= i; j++) {
//                    System.out.println(Arrays.toString(lData.get(j)));
//                }
//                } else {
//                    System.out.println("i = "+i);
//                }
//                
//            }
            if (i == 42) {
                System.out.println("bloup");
            }
            tree.insert(envData);
            if (i != 0) {
                try{
                    checkNode(tree.getRoot(), lData);
                }catch (Exception ex) {
                    System.out.println(" i = "+i);
                    checkNode(tree.getRoot(), lData);
                }
            }
                
            
            tree.flush(); //-- add persistence comportement 
        }
        assertTrue("after massive insertion root node should not be null", tree.getRoot() != null);
    }
    
    /**
     * Test if tree contain all elements inserted.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Ignore
    public void insertTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        insert();
        final double[] gr = tree.getRoot().getBoundary();
        final double[] envSearch = gr.clone();
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gr);
        
        int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(tabSearch.length == lData.size());
        assertTrue(tree.getElementsNumber() == lData.size());
        try {
            final double[] ge = new double[]{ Double.NaN, 10, 5, Double.NaN};
            tree.insert(ge);
            Assert.fail("test should have fail");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalArgumentException);
            //ok
        }
    }
    
    /**
     * Compare node properties from its children.<br/>
     * Compare Node boundary from its sub-Nodes boundary sum.<br/>
     * Moreover verify conformity of stored datas.
     */    
    protected void checkNode(final Node node, List<double[]> listRef) throws StoreIndexException, IOException {
        final double[] nodeBoundary = node.getBoundary();
        double[] subNodeBound = null;
        int sibl = node.getChildId();
        while (sibl != 0) {
            final Node currentChild = tAF.readNode(sibl);
            assertTrue("Node child should never be empty.", !currentChild.isEmpty());
            if (subNodeBound == null) {
                subNodeBound = currentChild.getBoundary().clone();
            } else {
                add(subNodeBound, currentChild.getBoundary());
            }
            if (node.isLeaf()) {
                assertTrue(currentChild.isData());
                final int currentValue = - currentChild.getChildId();
                final int listId = currentValue -1;
                assertTrue("bad ID = "+(currentValue)
                        +" expected : "+Arrays.toString(listRef.get(listId))
                        +" found : "+Arrays.toString(currentChild.getBoundary()), Arrays.equals(currentChild.getBoundary(), listRef.get(listId)));
            } else {
                checkNode(currentChild, listRef);
            }
            sibl = currentChild.getSiblingId();
        }
        assertTrue("Node should have a boundary equals from its sub-Nodes boundary sum : "
            +" \nNode boundary = "+Arrays.toString(nodeBoundary)
            +"\nsub-nodes sum = "+Arrays.toString(subNodeBound), Arrays.equals(nodeBoundary, subNodeBound));
    }
    
    /**
     * Compare all boundary node from their children boundary.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    public void checkNodeTest() throws StoreIndexException, IOException {
        tAF = ((AbstractTree)tree).getTreeAccess();
        if (tree.getRoot() == null) insert();
        checkNode(tree.getRoot(), lData);
    }
    
    /**
     * Test search query on tree border.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Ignore
    public void queryOnBorderTest() throws StoreIndexException, IOException {
        tree.setRoot(null);
        tEM.clear();
        final List<double[]> lGE = new ArrayList<double[]>();
        
        final List<double[]> lGERef = new ArrayList<double[]>();
        final double[] gR ;
        
        assertTrue(tree.getElementsNumber() == 0);
        
        if (dimension == 2) {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final double[] gE = new double[]{5 * i, 5 * j, 5 * i, 5 * j};
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR = new double[]{93, 18, 130, 87};
        } else {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    final double[] gE = new double[]{5 * i, 5 * j, 20, 5 * i, 5 * j, 20};
                    lGE.add(gE);
                    if (i == 19 && j > 3 && j < 18) {
                        lGERef.add(gE);
                    }
                }
            }
            gR = new double[]{93, 18, 19, 130, 87, 21};
        }
        for (int i = 0,  s = lGE.size(); i < s; i++) {
            tree.insert(lGE.get(i));
            tree.flush();
            tEM.flush();
        }
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(gR);
        
        final int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lGERef, Arrays.asList(getResult(tabSearch))));
    }
    
    /**
     * Test search query inside tree.
     */
    @Test
    @Ignore
    public void queryInsideTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        final List<double[]> lDataTemp = new ArrayList<double[]>();
        for (int i = 0; i < lSize; i++) {
            lDataTemp.add(lData.get(i));
        }
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(getExtent(lData));
        
        int[] tabSearch = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lDataTemp, Arrays.asList(getResult(tabSearch))));
    }
    
     /**
     * Test query outside of tree area.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Ignore
    public void queryOutsideTest() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        final double[] areaSearch = new double[dimension<<1];
        for (int i = 0; i < dimension; i++) {
            areaSearch[i] = minMax[i+1]+100;
            areaSearch[dimension+i] = minMax[i+1]+2000;
        }
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(areaSearch);
        
        int[] tabResult = tree.searchID(rG);
        final TreeIdentifierIterator triter = tree.search(rG);
        final int[] tabIterSearch = new int[tabResult.length];
        int tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabResult, tabIterSearch));
        assertTrue(tabResult.length == 0);
    }
    
    /**
     * Test insertion and deletion in tree.
     *
     * @throws TransformException if entry can't be transform into tree crs.
     */
    @Test
    @Ignore
    public void insertDelete() throws StoreIndexException, IOException {
        if (tree.getRoot() == null) insert();
        Collections.shuffle(lData);
        for (int i = 0, s = lData.size(); i < s; i++) {
            assertTrue(tree.remove(lData.get(i)));
        }
        
        final GeneralEnvelope rG = new GeneralEnvelope(crs);
        rG.setEnvelope(minMax.clone());
        
        int[] tabSearch = tree.searchID(rG);
        TreeIdentifierIterator triter = tree.search(rG);
        int[] tabIterSearch = new int[tabSearch.length];
        int tabID = 0;
        while (triter.hasNext()) {
            Assert.fail("test should not be pass here.");
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue(tabSearch.length == 0);
        assertTrue(tree.getElementsNumber() == 0);
        
        insert();
        tabSearch = tree.searchID(rG);
        triter    = tree.search(rG);
        tabIterSearch = new int[tabSearch.length];
        tabID = 0;
        while (triter.hasNext()) {
            tabIterSearch[tabID++] = triter.nextInt();
        }
        assertTrue("comparison between tabSearch from iterator not equals with tabSearch", compareID(tabSearch, tabIterSearch));
        assertTrue(compareLists(lData, Arrays.asList(getResult(tabSearch))));
    }
        
    /**
     * Return result given by {@link TreeElementMapper} from tree identifier table given in parameter.
     * 
     * @param tabID tree identifier table results.
     * @return all object result (in our case Object = double[]).
     * @throws IOException if problem during tree identifier "translate".
     */
    protected double[][] getResult(int[] tabID) throws IOException {
        final int l = tabID.length;
        double[][] tabResult = new double[l][];
        for (int i = 0; i < l; i++) {
            tabResult[i] = tEM.getObjectFromTreeIdentifier(tabID[i]);
        }
        return tabResult;
    }
}