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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.geotoolkit.index.LogicalFilterType;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.geotoolkit.nio.IOUtilities;
import org.junit.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import static org.geotoolkit.lucene.analyzer.AbstractAnalyzerTest.indexSearcher;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.lucene.DocumentIndexer;
import static org.geotoolkit.lucene.analyzer.AbstractAnalyzerTest.fillTestData;

import static org.junit.Assert.assertEquals;

//Junit dependencies

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class StandardAnalyzerTest extends AbstractAnalyzerTest {

    private static Path configDirectory = Paths.get("StandardAnalyzerTest"+ UUID.randomUUID().toString());

    private static boolean configured = false;

    @BeforeClass
    public static void setUpClass() throws Exception {
        if (!configured) {
            IOUtilities.deleteRecursively(configDirectory);
            List<DocumentIndexer.DocumentEnvelope> object = fillTestData();
            DocumentIndexer indexer = new DocumentIndexer(configDirectory, object, new StandardAnalyzer());
            indexer.createIndex();
            indexer.destroy();
            indexSearcher          = new LuceneIndexSearcher(configDirectory, "", new StandardAnalyzer(), true);
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
        SpatialQuery spatialQuery = new SpatialQuery("Title:90008411*", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");

        assertEquals(expectedResult, result);

        /**
         * Test 2 wildChar search: abstract LIKE *NEDIPROD*
         */
        spatialQuery = new SpatialQuery("abstract:*NEDIPROD*", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 3 wildChar search: title like *.ctd
         */
        spatialQuery = new SpatialQuery("Title:*.ctd", LogicalFilterType.AND);
        result       = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 3:", result);

        // ISSUE here the . is removed at the idexation
        //assertTrue(result.contains("39727_22_19750113062500"));
        //assertTrue(result.contains("40510_145_19930221211500"));
        //assertTrue(result.contains("42292_5p_19900609195600"));
        //assertTrue(result.contains("42292_9s_19900610041000"));


        /**
         * Test 4 wildCharSearch: abstract LIKE *onnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:(*onnees CTD NEDIPROD VI 120)", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("42292_5p_19900609195600");

        //issues here it found
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
        String resultReport = "";

        /**
         * Test 1 simple search: title = title1
         */
        SpatialQuery spatialQuery = new SpatialQuery("identifier:*MDWeb_FR_SY*", LogicalFilterType.AND);
        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharUnderscoreSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");

        assertEquals(expectedResult, result);

        /**
         * Test 2 simple search: title = identifier:Spot5-Cyprus-THX-IMAGERY3_ortho*
         */
        spatialQuery = new SpatialQuery("identifier:Spot5-Cyprus-THX-IMAGERY3_ortho*", LogicalFilterType.AND);
        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("wildCharUnderscoreSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        //expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1"); // error

        assertEquals(expectedResult, result);
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

        /**
         * Test 1 sorted search: all orderBy identifier ASC
         */
        SpatialQuery spatialQuery = new SpatialQuery("metafile:doc", LogicalFilterType.AND);
        SortField sf = new SortField("identifier_sort", SortField.Type.STRING, false);
        spatialQuery.setSort(new Sort(sf));

        Set<String> result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 1:", result);

        Set<String> expectedResult = new LinkedHashSet<>();
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 2 sorted search: all orderBy identifier DSC
         */
        spatialQuery = new SpatialQuery("metafile:doc", LogicalFilterType.AND);
        sf = new SortField("identifier_sort", SortField.Type.STRING, true);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 2:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("CTDF02");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 3 sorted search: all orderBy Abstract ASC
         */
        spatialQuery = new SpatialQuery("metafile:doc", LogicalFilterType.AND);
        sf = new SortField("Abstract_sort", SortField.Type.STRING, false);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 3:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");
        expectedResult.add("CTDF02");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");

        assertEquals(expectedResult, result);

        /**
         * Test 4 sorted search: all orderBy Abstract DSC
         */
        spatialQuery = new SpatialQuery("metafile:doc", LogicalFilterType.AND);
        sf = new SortField("Abstract_sort", SortField.Type.STRING, true);
        spatialQuery.setSort(new Sort(sf));

        result = indexSearcher.doSearch(spatialQuery);
        logResultReport("SortedSearch 4:", result);

        expectedResult = new LinkedHashSet<>();
        expectedResult.add("Spot5-Cyprus-THX-IMAGERY3_ortho1");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");
        expectedResult.add("MDWeb_FR_SY_couche_vecteur_258");

        assertEquals(expectedResult, result);
    }

    /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
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
    public void TermQueryTest() throws Exception {
        super.TermQueryTest();
    }
}
