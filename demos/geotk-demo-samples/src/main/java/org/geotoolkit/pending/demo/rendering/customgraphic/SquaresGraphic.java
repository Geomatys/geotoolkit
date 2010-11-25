
package org.geotoolkit.pending.demo.rendering.customgraphic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;

import org.opengis.display.primitive.Graphic;


public class SquaresGraphic extends AbstractGraphicJ2D{

    public SquaresGraphic(J2DCanvas canvas){
        super(canvas, canvas.getObjectiveCRS2D());
    }

    @Override
    public void paint(RenderingContext2D context) {
        final Graphics2D g = context.getGraphics();

        //our squares will always be in pixel units and display system
        context.switchToObjectiveCRS();

        g.setColor(Color.RED);
        g.fillRect(110, 10, 50, 50);

        g.setColor(Color.GREEN);
        g.fillRect(170, 10, 50, 50);

        g.setColor(Color.BLUE);
        g.fillRect(230, 10, 50, 50);
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

}
