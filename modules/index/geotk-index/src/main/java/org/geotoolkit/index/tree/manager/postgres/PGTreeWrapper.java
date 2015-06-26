
package org.geotoolkit.index.tree.manager.postgres;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeIdentifierIterator;
import org.geotoolkit.index.tree.star.StarRTree;
import org.geotoolkit.internal.tree.TreeAccess;
import org.geotoolkit.internal.tree.TreeAccessSQLByteArray;
import org.geotoolkit.internal.tree.TreeUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class PGTreeWrapper implements Tree {

    private static final Logger LOGGER = Logging.getLogger(PGTreeWrapper.class);
    
    private long lastUpdate;
    
    private Tree rTree;
    
    private final DataSource source;
    
    private final File directory;
    
    private final TreeElementMapper mapper;
            
    public PGTreeWrapper(final File directory, final DataSource source, TreeElementMapper mapper, CoordinateReferenceSystem crs) throws IOException, SQLException, StoreIndexException {
        TreeAccess ta  = new TreeAccessSQLByteArray(directory, source, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER, 5, crs);
        rTree          = new StarRTree<>(ta, mapper);
        this.mapper    = mapper;
        this.source    = source;
        this.directory = directory;
        lastUpdate     = System.currentTimeMillis();
    }
    
    public PGTreeWrapper(final byte[] data, final File directory, final DataSource source, TreeElementMapper mapper) throws IOException, SQLException, ClassNotFoundException, StoreIndexException {
        TreeAccess ta  = new TreeAccessSQLByteArray(directory, source, data, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER);
        rTree          = new StarRTree<>(ta, mapper);
        this.mapper    = mapper;
        this.source    = source;
        this.directory = directory;
        lastUpdate     = System.currentTimeMillis();
    }

    @Override
    public int[] searchID(Envelope regionSearch) throws StoreIndexException {
        updateTree();
        return rTree.searchID(regionSearch);
    }

    @Override
    public TreeIdentifierIterator search(Envelope regionSearch) throws StoreIndexException {
        return rTree.search(regionSearch);
    }

    @Override
    public void insert(Object object) throws StoreIndexException {
        rTree.insert(object);
    }

    @Override
    public boolean remove(Object object) throws StoreIndexException {
        return rTree.remove(object);
    }

    @Override
    public boolean remove(int entry, Envelope entryEnvelope) throws StoreIndexException {
        return rTree.remove(entry, entryEnvelope);
    }

    @Override
    public void flush() throws StoreIndexException {
        rTree.flush();
    }

    @Override
    public TreeElementMapper getTreeElementMapper() {
        return rTree.getTreeElementMapper();
    }

    @Override
    public int getMaxElements() {
        return rTree.getMaxElements();
    }

    @Override
    public Node getRoot() {
        return rTree.getRoot();
    }

    @Override
    public void setRoot(Node root) throws StoreIndexException {
        rTree.setRoot(root);
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return rTree.getCrs();
    }

    @Override
    public void clear() throws StoreIndexException {
        rTree.clear();
    }

    @Override
    public int getElementsNumber() {
        return rTree.getElementsNumber();
    }

    @Override
    public double[] getExtent() throws StoreIndexException {
        return rTree.getExtent();
    }

    @Override
    public void close() throws IOException {
        rTree.close();
    }

    @Override
    public boolean isClosed() {
        return rTree.isClosed();
    }
    
    
    private void updateTree() throws StoreIndexException {
        try {
            if ((System.currentTimeMillis() - lastUpdate) > (5 * 60 * 1000)) {
                byte[] data    = TreeAccessSQLByteArray.getData(directory, source);
                TreeAccess ta  = new TreeAccessSQLByteArray(directory, source, data, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER);
                rTree          = new StarRTree<>(ta, mapper);
                lastUpdate     = System.currentTimeMillis();
                LOGGER.info("The R-tree has been updated");
            }
        } catch (SQLException | IOException | ClassNotFoundException | NoSuchAlgorithmException ex) {
            throw new StoreIndexException(ex);
        }
    }

    @Override
    public String toString() {
        return rTree.toString();
    }
   
}
