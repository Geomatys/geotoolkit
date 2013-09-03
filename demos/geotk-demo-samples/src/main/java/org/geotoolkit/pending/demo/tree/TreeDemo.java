
package org.geotoolkit.pending.demo.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.FileTreeElementMapper;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeIdentifierIterator;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.index.tree.star.MemoryStarRTree;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * R-tree uses.
 * Exist : R-Tree (BasicRTree), R*Tree (StarRTree), Hilbert R-Tree (HilbertRTree).
 *
 * R-Tree         : fast indexing time, slow search query time.(Adapted for many update use).
 * R*Tree         : moderate indexing time, moderate search query time.
 * Hilbert R-Tree : slow indexing time, fast search query time.(Adapted for less update and many search query uses).
 *
 * @author Rémi Maréchal (Geomatys).
 * @see Tree
 */
public class TreeDemo {
    /*
     * CoordinateReferenceSystem used in this demo. 
     */
    private static CoordinateReferenceSystem DEMO_CRS = DefaultGeographicCRS.WGS84;
    
    public static void main(String[] args) throws StoreIndexException, IOException, ClassNotFoundException {
        
        /*
         * In these examples we'll show how to build and create a RTree according to our use case.
         * 2 RTree storage are made, the first where RTree is stored in computer memory 
         * and the second where RTree is stored in file on hard drive at a path stipulated by the user.
         * 
         * RTree which is stored in computer memory :
         *      - advantages    : all RTree action are faster than the others because do not require hard drive access.
         *      - inconvenients : computer RAM memory limits data number which should be inserted.
         * 
         * RTree which is stored on hard drive : 
         *      - advantages    : there is no data number limit of insertions.
         *      - inconvenients : all RTree action are slower than the others because require hard drive access.
         */
        
        /*
         * First example RTree stored in our computer memory.
         * In this example user has to know he can't insert too many elements, to not overflow memory.
         * For this example we choose StarRTree.
         */
        
        /*
         * In each RTree we need another object called TreeElementMapper.
         * This Object allows to link Integer identifier stored in RTree and datas.
         * (It is an internal operation). User should just override some methods from TreeElementMapper.
         * TreeElementMapper uses Generic template and user may specify the type.
         * In the present example we are working with Envelope type object. Moreover to store Envelope object we use an Envelope table.
         * User can choose List or Map or other object according to his needs.
         */
        
        final TreeElementMapper<Envelope> demoMemoryTreeEltMapper = new TreeElementMapper<Envelope>() {
            
            private int currentSize      = 100;
            private Envelope[] envelopes = new Envelope[currentSize];
            
            /**
             * {@inheritDoc }
             */
            @Override
            public int getTreeIdentifier(final Envelope object) throws IOException {
                int i = 0;
                for (Envelope env : envelopes) {
                    if (env.equals(object)) return i+1;
                    i++;
                }
                throw new IllegalStateException("getTreeIdentifier : impossible to find tree identifier from Envelope object.");
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public Envelope getEnvelope(final Envelope object) throws IOException {
                return object;
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public void setTreeIdentifier(final Envelope object, final int treeIdentifier) throws IOException {
                final int envID = treeIdentifier - 1;
                if (envID >= currentSize) {
                    currentSize = currentSize << 1;
                    Arrays.copyOf(envelopes, currentSize);
                }
                envelopes[envID] = object;
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public Envelope getObjectFromTreeIdentifier(final int treeIdentifier) throws IOException {
                final int envID = treeIdentifier - 1;
                if (envID >= currentSize) 
                    throw new IllegalStateException("getObjectFromTreeIdentifier : impossible to find object from identifier.");
                return envelopes[envID];
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public void clear() throws IOException {
                currentSize = 100;
                envelopes   = new Envelope[currentSize];
            }

            /**
             * {@inheritDoc }
             */
            @Override
            public void close() throws IOException {
                // do nothing no stream to close.
            }
        };
        
        /*
         * After creating TreeElementMapper object we create RTree.
         */
        final Tree demoMemoryRTree = new MemoryStarRTree<Envelope>(5, DEMO_CRS, demoMemoryTreeEltMapper);
        
        /*
         * get datas.
         */
        Envelope[] insertedEnvelope = createData(20);
        
        /*
         * Now insert data in RTree.
         */
        for (Envelope data : insertedEnvelope) {
            demoMemoryRTree.insert(data);
        }
        
        System.out.println("Tree Demo : "+demoMemoryRTree.toString());
        
        /*
         * Now we should search.
         */
        final GeneralEnvelope envelopeSearch = new GeneralEnvelope(DEMO_CRS);
        envelopeSearch.setEnvelope(-45, -50, 110, 75);
        
        /*
         * There is two way to search.
         * First, we can get a table of data identifiers which matches with the search area.
         */
        int[] treeIdentifierResult = demoMemoryRTree.searchID(envelopeSearch);
        
        System.out.println("identifiers which match : "+Arrays.toString(treeIdentifierResult));
        
        /*
         * To obtain data you have to ask data from TreeElementMapper as below.
         */
        final Envelope[] resultData = new Envelope[treeIdentifierResult.length];
        for (int id = 0; id < treeIdentifierResult.length; id++) {
            resultData[id] = demoMemoryTreeEltMapper.getObjectFromTreeIdentifier(treeIdentifierResult[id]);
        }
        
        /*
         * Secondly, we can get an iterator which iterates on each treeIdentifier search result.
         */
        TreeIdentifierIterator treeIterator = demoMemoryRTree.search(envelopeSearch);
        /*
         * And we can get data object result as below.
         */
        List<Envelope> listDataResult = new ArrayList<Envelope>();
        while (treeIterator.hasNext()) {
            listDataResult.add(demoMemoryTreeEltMapper.getObjectFromTreeIdentifier(treeIterator.nextInt()));
        }
        
        /*
         * Moreover we can also remove some elements in index RTree.
         */
        int idEnv = 1;
        for (Envelope env : insertedEnvelope) {
            demoMemoryRTree.remove(env);
            if (idEnv % 5 == 0) {
                System.out.println("RTree : during remove action after "+idEnv+" elements : "+demoMemoryRTree.toString());
            }
            idEnv++;
        }
        
                        /***********************************/
        /*
         * Second example: RTree stored on user computer hard disk.
         * If the user doesn't know if there is enought memory on his computer or 
         * if he knows that all the data overflow memory, he may choose to store RTree 
         * on his computer hard drive.
         */
        /*
         * Build the 2 files to store RTree.
         * One to store RTree architecture.
         * The other to store TreeElementMapper architecture.
         */
        final File treeFile          = File.createTempFile("tree", "bin");
        final File treeELTMapperFile = File.createTempFile("mapper", "bin");
        /*
         * First, like previously, we have to begin creating an appropriate
         * TreeElementMapper to link treeIdentifier and stored object.
         * In our case we would store elements on hard drive, then we would override 
         * a TreeElementMapper implementation called FileTreeElementMapper.
         * See DemoFileTreeElementMapper class.
         */
        TreeElementMapper<Envelope> demoFileTreeEltMapper = new DemoFileTreeElementMapper(treeELTMapperFile, DEMO_CRS);
        /*
         * Be carefull it exists 2 ways to open a FileRTree.
         * If tree has never been built previously, the user should open RTree in writing action.(see Javadoc)
         * Unlike if a tree has already been saved on a filled file, 
         * the user should open RTree in reading/writing and use constructor in accordance with it. 
         */
        /*
         * First, we make an RTree as if it has never been created before.  
         */
        Tree<Envelope> demoFileRTree = new FileStarRTree<Envelope>(treeFile, 4, DEMO_CRS, demoFileTreeEltMapper);
        
        /*
         * At this step the RTree is empty and we can fill it like previously.
         */
        /*
         * get datas.
         */
        insertedEnvelope = createData(20);
        
        /*
         * Now insert data in RTree.
         */
        for (Envelope data : insertedEnvelope) {
            demoFileRTree.insert(data);
        }
        
        /*
         * At this step we can use search or remove action freely.
         * But if we want to save the RTree to re-open it later we MUST call close method
         * on RTree and TreeElementMapper to finish to write Tree informations.
         * 
         * Be carefull not to open an RTree in reading if it has not been closed before.
         * 
         * In this example we close RTree and TreeElementMapper to re-open it after.
         */
        demoFileRTree.close();
        demoFileTreeEltMapper.close();
        
        /*
         * Re-open RTree.
         */
        /*
         * First we open again TreeElementMapper.
         */
        demoFileTreeEltMapper = new DemoFileTreeElementMapper(DEMO_CRS, treeELTMapperFile);
        /*
         * Then RTree.
         */
        demoFileRTree = new FileStarRTree<Envelope>(treeFile, demoFileTreeEltMapper);
        
        /*
         * At this step we can use search or remove action freely.
         * Moreover we can also add some data as below.
         */
        insertedEnvelope = createData(5);// five new datas.
        System.out.println("RTree : before re-inserting datas : "+demoFileRTree.toString());
        /*
         * Now insert data in RTree.
         */
        for (Envelope data : insertedEnvelope) {
            demoFileRTree.insert(data);
        }
        
        System.out.println("RTree : after re-inserting datas : "+demoFileRTree.toString());
        
        /*
         * Like previously if we want to save changes and re-open RTree afterwards we call close() methods.
         */
        demoFileRTree.close();
        demoFileTreeEltMapper.close();
   }
    
    /**
     * Build some data to insert in RTree.
     * 
     * @param dataNumber data number ask by user.
     * @return data table.
     */
    private static Envelope[] createData(final int dataNumber) {
        final Envelope[] result = new Envelope[dataNumber];
        for (int id = 0; id < dataNumber; id++) {
            final GeneralEnvelope genv = new GeneralEnvelope(DEMO_CRS);
            final double longCentroid = Math.random() * Math.random() * Math.random() * 360 - 180;
            final double latCentroid  = Math.random() * Math.random() * Math.random() * 180 - 90;
            genv.setEnvelope(longCentroid, latCentroid, longCentroid, latCentroid);
            result[id] = genv;
        }
        return result;
    }
}
