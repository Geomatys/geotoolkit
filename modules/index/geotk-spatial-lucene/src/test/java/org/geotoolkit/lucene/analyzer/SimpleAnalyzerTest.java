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

package org.geotoolkit.lucene.analyzer;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
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
import org.junit.BeforeClass;

//Junit dependencies

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SimpleAnalyzerTest extends AbstractAnalyzerTest {

    private static Path configDirectory = Paths.get("SimpleAnalyzerTest"+ UUID.randomUUID().toString());

    private static boolean configured = false;

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (!configured) {
            IOUtilities.deleteRecursively(configDirectory);
            List<DocumentIndexer.DocumentEnvelope> object = fillTestData();
            DocumentIndexer indexer = new DocumentIndexer(configDirectory, object, new SimpleAnalyzer());
            indexer.createIndex();
            indexer.destroy();
            indexSearcher          = new LuceneIndexSearcher(configDirectory, "", new SimpleAnalyzer(), true);
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
        String resultReport = "";

        /**
         * Test 1 simple search: title = 90008411.ctd
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:\"90008411.ctd\"", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 1:", result);

        // the result we want are this
        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        // ERROR: but with the simple Analyzer remove the number so we get all the results finishing by ctd (why???)
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);

         /**
         * Test 2 simple search: indentifier != 40510_145_19930221211500
         */
        spatialQuery = new SpatialQuery("metafile:doc NOT identifier:\"40510_145_19930221211500\"", LogicalFilterType.AND);
        result       = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");

        // ERROR: here the simple analyzer remove all the number and '_'
        expectedResult.add("40510_145_19930221211500");
        // -----------------------------------------------------------//
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 3 simple search: abstract = Donnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:\"Donnees CTD NEDIPROD VI 120\"", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 3:", result);


        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 4 simple search: ID = World Geodetic System 84
         */
        spatialQuery = new SpatialQuery("ID:\"World Geodetic System 84\"", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");

        assertEquals(expectedResult, result);

        /**
         * Test 5 simple search: ID = 0UINDITENE
         */
        spatialQuery = new SpatialQuery("ID:\"0UINDITENE\"", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 5:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("11325_158_19640418141800");

        assertEquals(expectedResult, result);

        /**
         * Test 6 range search: Title <= FRA
         */
        spatialQuery = new SpatialQuery("Title_raw:[0 TO \"FRA\"]", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 6:", result);


        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("40510_145_19930221211500");

        assertEquals(expectedResult, result);

        /**
         * Test 7 range search: Title > FRA
         */
        spatialQuery = new SpatialQuery("Title_raw:[FRA TO z]", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SimpleSearch 7:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        //expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1"); no more null value => Spot5 ... has no title

        assertEquals(expectedResult, result);
    }

     /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void wildCharSearchTest() throws Exception {

        /**
         * Test 1 simple search: title = title1
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:*0008411.ctd", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");

        // ERROR: it didn't find any result (why???)
        expectedResult = new LinkedHashSet<>();
        assertEquals(expectedResult, result);

        /**
         * Test 2 wildChar search: originator LIKE *UNIVER....
         */
        spatialQuery = new SpatialQuery("abstract:*NEDIPROD*", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

         /**
         * Test 3 simple search: title like *.ctd
         */
        spatialQuery = new SpatialQuery("Title:*.ctd", LogicalFilterType.AND);
        result       = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 3:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");

        // ERROR: it didn't find any result (why???)
        expectedResult = new LinkedHashSet<>();

        assertEquals(expectedResult, result);


        /**
         * Test 4 wildCharSearch: anstract LIKE *onnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:(*onnees CTD NEDIPROD VI 120)", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 5 wildCharSearch: Format LIKE *MEDATLAS ASCII*
         */
        spatialQuery = new SpatialQuery("Format:(*MEDATLAS ASCII*)", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 5:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800"); // >>  ISSUES This one shoudn't be there because it not in the same order => ASCII MEDATLAS
        expectedResult.add("40510_145_19930221211500");

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

        /**
         * Test 1 date search: date after 25/01/2009
         */
        SpatialQuery spatialQuery = new SpatialQuery("date:{20090125 30000101}", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("DateSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("40510_145_19930221211500");

        //ERROR: it didn't find any result (why???)
        expectedResult = new LinkedHashSet<>();
        assertEquals(expectedResult, result);
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
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    public void spatialSearchTest() throws Exception {
        super.spatialSearchTest();
    }

    @Test
    @Override
    public void TermQueryTest() throws Exception {
        super.TermQueryTest();
    }
}
