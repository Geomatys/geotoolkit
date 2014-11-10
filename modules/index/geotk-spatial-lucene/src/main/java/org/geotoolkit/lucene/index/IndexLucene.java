/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Base class to manipulate Lucene index.
 *
 * @author Guilhem Legal
 * @module pending
 */
public abstract class IndexLucene {

    /**
     * for debug purpose.
     */
    protected static final Logger LOGGER = Logging.getLogger(IndexLucene.class);

    /**
     * A lucene analyser.
     */
    protected final Analyzer analyzer;

    /**
     * This is the RAM Directory if you would like to store the index in the RAM memory.
     */
    private RAMDirectory ramDirectory = new RAMDirectory();

    /**
     * This the File Directory if you would like to store the index in a File directory.
     */
    private File fileDirectory;

    /**
     * The global level of log.
     */
    protected Level logLevel = Level.INFO;

    /**
     * A flag indicating that the index is already closed.
     */
    private boolean closed = false;

    /**
     * A R-Tree to perform spatial query.
     */
    protected Tree<NamedEnvelope> rTree;

   /**
    * Creates a new Lucene Index.
    * Analyzer field is set to default value ClassicAnalyzer.
    */
    public IndexLucene() {
        analyzer = new ClassicAnalyzer(Version.LUCENE_4_9, new CharArraySet(Version.LUCENE_4_9, new HashSet<String>(), true));
    }

    /**
     * Creates a new Lucene Index with the specified Analyzer.
     * If the analyzer is null it will be set to default value {@link ClassicAnalyzer}.
     */
    public IndexLucene(final Analyzer analyzer) {
        if (analyzer == null) {
            this.analyzer = new ClassicAnalyzer(Version.LUCENE_4_9, new CharArraySet(Version.LUCENE_4_9, new HashSet<String>(), true));
        } else {
            this.analyzer = analyzer;
        }
    }

    /**
     * Returns a {@link RAMDirectory} of this Index Object.
     */
    public RAMDirectory getRAMdirectory() {
        return ramDirectory;
    }

    /**
     * The {@link RAMDirectory} setter for this index object.
     *
     * @param ramDirectory a RAMDirectory object.
     */
    public void setRAMdirectory(final RAMDirectory ramDirectory) {
        this.ramDirectory = ramDirectory;
    }

    /**
     * Returns a file directory of this index.
     */
    public File getFileDirectory() {
        return fileDirectory;
    }

    /**
     * The FileDirectory setter of this index.
     *
     * @param aFileDirectory a FileDirectory object.
     */
    public void setFileDirectory(final File aFileDirectory) {
        fileDirectory = aFileDirectory;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(final Level logLevel) {
        this.logLevel = logLevel;
    }

    public CoordinateReferenceSystem getTreeCrs() {
        return rTree.getCrs();
    }

    /**
     * Free all the resources.
     */
    public void destroy() {
        if (analyzer != null && !closed) {
            analyzer.close();
            closeTree();
            closed = true;
        }
    }

    protected void resetTree() throws StoreIndexException, IOException {
        rTree = SQLRtreeManager.resetTree(getFileDirectory(), rTree, this);
    }

    private void closeTree() {
        try {
            SQLRtreeManager.close(getFileDirectory(), rTree, this);
        } catch (StoreIndexException | IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }
    
    public Map<Integer, NamedEnvelope> getMapperContent() throws IOException {
        if (rTree != null) {
            TreeElementMapper mapper = rTree.getTreeElementMapper();
            return mapper.getFullMap();
        }
        return new HashMap<>();
    }
    
    public String getTreeRepresentation() {
        if (rTree != null) {
            return rTree.toString();
        }
        return null;
    }
}
