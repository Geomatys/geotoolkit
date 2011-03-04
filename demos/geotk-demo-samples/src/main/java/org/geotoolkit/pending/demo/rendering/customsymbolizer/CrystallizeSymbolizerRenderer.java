

package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import com.jhlabs.image.CrystallizeFilter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.logging.Level;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.referencing.CRS;

import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;


public class CrystallizeSymbolizerRenderer extends AbstractSymbolizerRenderer<CrystallizeCachedSymbolizer>{

    public CrystallizeSymbolizerRenderer(CrystallizeCachedSymbolizer cache, RenderingContext2D context){
        super(cache, context);
    }

    @Override
    public void portray(ProjectedObject graphic) throws PortrayalException {
        //works only with coverage data, do nothing
    }

    @Override
    public void portray(ProjectedCoverage graphic) throws PortrayalException {

        //read the coverage
        //this is a fast way to do it, don't use it in real code
        GridCoverage2D dataCoverage;
        try {
            dataCoverage = graphic.getCoverage(new GridCoverageReadParam());
        } catch (CoverageStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        //reproject coverage
        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        if(!CRS.equalsIgnoreMetadata(coverageCRS,renderingContext.getObjectiveCRS2D()) ){
            try{
                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(
                        dataCoverage.view(ViewType.NATIVE), renderingContext.getObjectiveCRS2D());
                dataCoverage = dataCoverage.view(ViewType.RENDERED);
            } catch (CoverageProcessingException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
        }




        final RenderedImage img = dataCoverage.getRenderedImage();


        final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        g.drawRenderedImage(img, new AffineTransform());

        //we apply our crystal effect
        final CrystallizeFilter op = new CrystallizeFilter();
        op.filter(buffer, buffer);


        //we switch in objective CRS to render the coverage.
        renderingContext.switchToObjectiveCRS();

        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if(trs2D instanceof AffineTransform){
            g2d.drawImage(buffer, (AffineTransform)trs2D, null);
        }

    }

    @Override
    public boolean hit(ProjectedObject graphic, SearchAreaJ2D mask, VisitFilter filter) {
        //works only with coverage data, do nothing
        return false;
    }

    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        //not a selectable symbol
        return false;
    }

}
