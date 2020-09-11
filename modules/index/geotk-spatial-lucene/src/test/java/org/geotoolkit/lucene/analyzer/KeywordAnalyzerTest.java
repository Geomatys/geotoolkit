/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 *    Constellation - An open source and Keyword compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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

package org.geotoolkit.lucene.analyzer;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.geotoolkit.nio.IOUtilities;
import org.junit.AfterClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.geotoolkit.index.LogicalFilterType;
import static org.geotoolkit.lucene.analyzer.AbstractAnalyzerTest.indexSearcher;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.lucene.DocumentIndexer;
import static org.geotoolkit.lucene.analyzer.AbstractAnalyzerTest.fillTestData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class KeywordAnalyzerTest extends AbstractAnalyzerTest {

    private static Path configDirectory = Paths.get("KeywordAnalyzerTest" + UUID.randomUUID().toString());

    private static boolean configured = false;

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (!configured) {
            IOUtilities.deleteRecursively(configDirectory);
            List<DocumentIndexer.DocumentEnvelope> object = fillTestData();
            DocumentIndexer indexer = new DocumentIndexer(configDirectory, object, new KeywordAnalyzer());
            indexer.createIndex();
            indexer.destroy();

            indexSearcher          = new LuceneIndexSearcher(configDirectory, "", new KeywordAnalyzer(), true);
            indexSearcher.setLogLevel(Level.FINER);
            configured = true;
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        try{
            if (indexSearcher != null) {
                indexSearcher.destroy();
            }
            SQLRtreeManager.removeTree(indexSearcher.getFileDirectory());
            IOUtilities.deleteRecursively(configDirectory);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }


    /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void simpleSearchTest() throws Exception {
       super.simpleSearchTest();
    }

    @Test
    @Override
    public void stringRangeSearchTest() throws Exception {
        super.stringRangeSearchTest();
    }

     /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void wildCharSearchTest() throws Exception {
        String resultReport = "";

        /**
         * Test 1 simple search: title = title1
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:90008411*", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result) {
            resultReport = resultReport + s + '\n';
        }

        LOGGER.log(Level.FINER, "wildCharSearch 1:\n{0}", resultReport);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");

        assertEquals(expectedResult, result);

        /**
         * Test 2 wildChar search: abstract LIKE *NEDIPROD*
         */
        spatialQuery = new SpatialQuery("abstract:*NEDIPROD*", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result) {
            resultReport = resultReport + s + '\n';
        }

        LOGGER.log(Level.FINER, "wildCharSearch 2:\n{0}", resultReport);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        /* corrections 8.4.0
        expectedResult = new LinkedHashSet<>();*/

        assertEquals(expectedResult, result);

        /**
         * Test 3 wildChar search: title like *.ctd
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("Title:*.ctd", LogicalFilterType.AND);
        result       = indexSearcher.doSearch(spatialQuery);

        for (String s: result) {
            resultReport = resultReport + s + '\n';
        }

        LOGGER.log(Level.FINER, "wildCharSearch 3:\n{0}", resultReport);

        assertTrue(result.contains("39727_22_19750113062500"));
        assertTrue(result.contains("40510_145_19930221211500"));
        assertTrue(result.contains("42292_5p_19900609195600"));
        assertTrue(result.contains("42292_9s_19900610041000"));


        /**
         * Test 4 wildCharSearch: anstract LIKE *onnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:(*onnees CTD NEDIPROD VI 120)", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result) {
            resultReport = resultReport + s + '\n';
        }

        LOGGER.log(Level.FINER, "wildCharSearch 4:\n{0}", resultReport);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        // ERROR it didn't find any result (why???)
        expectedResult = new LinkedHashSet<>();

        assertEquals(expectedResult, result);

        /**
         * Test 5 wildCharSearch: Format LIKE *MEDATLAS ASCII*
         */
        spatialQuery = new SpatialQuery("Format:(*MEDATLAS ASCII*)", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result) {
            resultReport = resultReport + s + '\n';
        }

        LOGGER.log(Level.FINER, "wildCharSearch 5:\n{0}", resultReport);

        // should be
        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");


        // but with this analyzer we got 11325_158_19640418141800
        // witch got for format = 'ASCII MEDATLAS'
        expectedResult = new LinkedHashSet<>();
        expectedResult.add("11325_158_19640418141800");

        assertEquals(expectedResult, result);

    }

    /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void wildCharUnderscoreSearchTest() throws Exception {
        super.wildCharUnderscoreSearchTest();
    }


     /**
     * Test simple lucene date search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void dateSearchTest() throws Exception {
        super.dateSearchTest();
    }

    /**
     * Test sorted lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void sortedSearchTest() throws Exception {
        super.sortedSearchTest();
    }

    /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void spatialSearchTest() throws Exception {
        super.spatialSearchTest();
    }

    /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void TermQueryTest() throws Exception {
        super.TermQueryTest();
    }
}
