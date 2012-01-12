
package org.geotoolkit.pending.demo.rendering;


import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

import net.sf.jasperreports.engine.JasperReport;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericMappingFeatureIterator;
import org.geotoolkit.data.memory.mapping.FeatureMapper;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.report.FeatureCollectionDataSource;
import org.geotoolkit.report.JasperReportService;
import org.geotoolkit.report.graphic.chart.ChartDef;
import org.geotoolkit.report.graphic.legend.LegendDef;
import org.geotoolkit.report.graphic.map.MapDef;
import org.geotoolkit.report.graphic.northarrow.NorthArrowDef;
import org.geotoolkit.report.graphic.scalebar.ScaleBarDef;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.RandomStyleFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;

public class ReportDemo {

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
                 new Hints(Hints.FEATURE_FACTORY,LenientFeatureFactory.class));
    private static final MutableStyleFactory SF = (MutableStyleFactory)FactoryFinder.getStyleFactory(
                 new Hints(Hints.STYLE_FACTORY,MutableStyleFactory.class));
    private static final FilterFactory FIF = FactoryFinder.getFilterFactory(null);


    public static void main(String[] args) throws Exception {

        final InputStream template = ReportDemo.class.getResourceAsStream("/data/report/complexReport.jrxml");

        final Entry<JasperReport,FeatureType> entry = JasperReportService.prepareTemplate(template);
        final JasperReport report = entry.getKey();
        final FeatureType type = entry.getValue();
        System.out.println(type);

        
        //source to make an atlas ----------------------------------------------------
        final DataStore store = DataStoreFinder.getDataStore("url",ReportDemo.class.getResource("/data/world/Countries.shp"));
        final Name name = store.getNames().iterator().next();
        final FeatureCollection countries =  store.createSession(true).getFeatureCollection(QueryBuilder.all(name));


        //Iterator over all the countries --------------------------------------------
        final FeatureIterator ite = countries.iterator();


        //We map the feature type to the report type ---------------------------------
        final GenericMappingFeatureIterator mapped = new GenericMappingFeatureIterator(ite, new FeatureMapper(){
            
            @Override
            public FeatureType getSourceType() {
                return countries.getFeatureType();
            }

            @Override
            public FeatureType getTargetType() {
                return type;
            }
            
            @Override
            public Feature transform(Feature feature) {
                final Feature modified = FeatureUtilities.defaultFeature(type, "id");

                //create the main map with a single feature ------------------
                final FeatureCollection col = DataUtilities.collection(feature);
                final MapContext context = MapBuilder.createContext();
                final MutableStyle style = RandomStyleFactory.createRandomVectorStyle(col);
                context.layers().add(MapBuilder.createFeatureLayer(col, style));

                try{
                    modified.getProperty("map3").setValue(new MapDef(
                        new CanvasDef(new Dimension(1, 1), Color.WHITE,false),
                        new SceneDef(context,null),
                        new ViewDef(CRS.transform(context.getBounds(), CRS.decode("EPSG:3395")),0), //set this map in 3395
                        null));
                }catch(Exception ex){ex.printStackTrace();}


                //casual attributs -------------------
                modified.getProperty("CNTRY_NAME").setValue(feature.getProperty("CNTRY_NAME").getValue());
                modified.getProperty("POP_CNTRY").setValue(feature.getProperty("POP_CNTRY").getValue());

                //chart -------------------------
                final DefaultPieDataset pds = new DefaultPieDataset();
                pds.setValue((Comparable)feature.getProperty("SOVEREIGN").getValue(), Math.random());
                pds.setValue((Comparable)feature.getProperty("ISO_3DIGIT").getValue(), Math.random());
                final JFreeChart chart = ChartFactory.createPieChart("Info", pds, true, true, Locale.FRENCH);
                modified.getProperty("chart4").setValue(new ChartDef(chart));

                //legend --------------------------
                modified.getProperty("legend5").setValue(new LegendDef());

                //scale bar -------------------
                modified.getProperty("scalebar6").setValue(new ScaleBarDef());

                //north arow -------------------
                modified.getProperty("northarrow7").setValue(new NorthArrowDef());

                //subtable --------------
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.setName("subdata");
                ftb.add("men", Integer.class);
                ftb.add("women",Integer.class);
                ftb.add("desc", String.class);
                final FeatureType subType = ftb.buildFeatureType();
                final FeatureCollection subcol = DataUtilities.collection("sub", subType);
                try {
                    FeatureWriter fw = subcol.getSession().getDataStore().getFeatureWriterAppend(subType.getName());
                    for(int i=0,n=new Random().nextInt(20);i<n;i++){
                        Feature f =fw.next();
                        f.getProperty("men").setValue(new Random().nextInt());
                        f.getProperty("women").setValue(new Random().nextInt());
                        f.getProperty("desc").setValue("some text from attribut");
                        fw.write();
                    }
                    fw.close();

                } catch (DataStoreException ex) {
                    ex.printStackTrace();
                }
                modified.getProperty("table8").setValue(new FeatureCollectionDataSource(subcol));

                return modified;
            }
        });


        //Generate the report --------------------------------------------------------
        final OutputDef output = new OutputDef(JasperReportService.MIME_PDF, new File("atlas.pdf"));
        JasperReportService.generateReport(report, mapped, null, output);

    }


}
