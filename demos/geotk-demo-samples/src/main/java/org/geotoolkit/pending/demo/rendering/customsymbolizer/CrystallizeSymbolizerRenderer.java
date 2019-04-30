

package org.geotoolkit.pending.demo.rendering.customsymbolizer;

import com.jhlabs.image.CrystallizeFilter;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;


public class CrystallizeSymbolizerRenderer extends AbstractSymbolizerRenderer<CrystallizeCachedSymbolizer>{

    public CrystallizeSymbolizerRenderer(final SymbolizerRendererService service,CrystallizeCachedSymbolizer cache, RenderingContext2D context){
        super(service,cache, context);
    }

    @Override
    public boolean portray(ProjectedObject graphic) throws PortrayalException {
        //works only with coverage data, do nothing
        return false;
    }

    @Override
    public boolean portray(ProjectedCoverage graphic) throws PortrayalException {

        //read the coverage
        //this is a fast way to do it, don't use it in real code
        GridCoverage dataCoverage;
        try {
            dataCoverage = graphic.getCoverage(new GridCoverageReadParam());
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return false;
        }

        //reproject coverage
        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        if (!Utilities.equalsIgnoreMetadata(coverageCRS,renderingContext.getObjectiveCRS2D()) ) {
            try {
                dataCoverage = new ResampleProcess(dataCoverage, renderingContext.getObjectiveCRS2D(), null, InterpolationCase.NEIGHBOR, null).executeNow();
            } catch (ProcessException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return false;
            }
        }


        final RenderedImage img = dataCoverage.render(null);


        final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = buffer.createGraphics();
        g.drawRenderedImage(img, new AffineTransform());

        //we apply our crystal effect
        final CrystallizeFilter op = new CrystallizeFilter();
        op.filter(buffer, buffer);


        //we switch in objective CRS to render the coverage.
        renderingContext.switchToObjectiveCRS();

        final MathTransform2D trs2D = CoverageUtilities.toGeotk(dataCoverage).getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if(trs2D instanceof AffineTransform){
            g2d.drawImage(buffer, (AffineTransform)trs2D, null);
        }
        return true;
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
