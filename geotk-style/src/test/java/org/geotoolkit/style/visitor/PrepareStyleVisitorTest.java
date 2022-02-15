package org.geotoolkit.style.visitor;

import java.util.Arrays;
import org.apache.sis.filter.DefaultFilterFactory;
import org.geotoolkit.style.DefaultStroke;
import org.geotoolkit.style.function.DefaultInterpolate;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.Expression;
import org.opengis.filter.FilterFactory;
import org.opengis.style.Stroke;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PrepareStyleVisitorTest {

    private static final FilterFactory FF = DefaultFilterFactory.forFeatures();

    /**
     * Temporary test to ensure interpolation expression is not broken by Style override visitor.
     * In the future, a rewrite of style/expression visitors might make it obsolete.
     */
    @Test
    public void interpolationExpressionShouldNotCauseError() {
        final PrepareStyleVisitor visitor = new PrepareStyleVisitor(Feature.class, null);

        final DefaultInterpolate interpol = new DefaultInterpolate(
                FF.property("random"),
                Arrays.asList(
                        new DefaultInterpolationPoint(0, FF.literal(0)),
                        new DefaultInterpolationPoint(1, FF.literal(1))
                ),
                Method.NUMERIC,
                Mode.LINEAR,
                FF.literal(-1)
        );

        final Object result = visitor.visit(interpol);
        assertNotNull("Visit result", result);
        assertTrue("Result should be an expression", result instanceof Expression);
        assertFalse(((Expression<?, ?>) result).getParameters().isEmpty());

        final Object stroke = visitor.visit(new DefaultStroke(interpol, null, null, null, null, null, null), null);
        assertNotNull(stroke);
        assertTrue(stroke instanceof Stroke);
        final Expression color = ((Stroke) stroke).getColor();
        assertNotNull("Stroke color", color);
        assertFalse(((Expression<?, ?>) result).getParameters().isEmpty());
    }
}
