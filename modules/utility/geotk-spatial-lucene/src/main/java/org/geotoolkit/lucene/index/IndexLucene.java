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
import java.util.logging.Level;

import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
        
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
public abstract class IndexLucene {

    protected static final Logger LOGGER = Logger.getLogger("org.constellation.metadata.index");

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

    protected Level logLevel;
    
   /**
     * Creates a new Lucene Index.
     */
    public IndexLucene() {
        analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
    }

    /**
     * Creates a new Lucene Index with the specified Analyzer.
     */
    public IndexLucene(final Analyzer analyzer) {
        if (analyzer == null)
            this.analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        else
            this.analyzer = analyzer;
    }

    /**
     * Returns a RAMdirectory of this Index Object.
     */
    public RAMDirectory getRAMdirectory() {
        return ramDirectory;
    }

    /**
     * The RAMdirectory setter for this Index object.
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
    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

}
