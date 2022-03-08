package org.geotoolkit.geometry.jts.distance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;

/**
 * Test data created thanks to <a href="http://www.sciences.univ-nantes.fr/sites/genevieve_tulloue/Meca/RefTerre/Orthodromie1.php">Nantes university demonstration</a>
 * and <a href="http://ressources.univ-lemans.fr/AccesLibre/UM/Pedago/physique/02/divers/ortholoxo.html">Le Mans university demonstration</a>.
 *
 * @author Alexis Manin (Geomatys)
 */
class Segment {

    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.geometry.jts.distance.test");

    final String title;
    final Coordinate start;
    final Coordinate end;
    final double orthodromicDistance;
    final double loxodromicDistance;

    Segment(String title, Coordinate start, Coordinate end, double orthodromicDistance, double loxodromicDistance) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.orthodromicDistance = orthodromicDistance;
        this.loxodromicDistance = loxodromicDistance;
    }

    static List<Segment> loadTestData() throws IOException {
        try (final InputStream inPairs = Segment.class.getResourceAsStream("pairs.csv");
                final InputStreamReader tmpReader = new InputStreamReader(inPairs, StandardCharsets.US_ASCII);
                final BufferedReader br = new BufferedReader(tmpReader)) {
            return br.lines()
                    .map(Segment::removeComments)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .skip(1) // First non-comment/non-empty line should be header
                    .map(line -> line.split("\t"))
                    .filter(tokens -> {
                        if (tokens.length != 7) {
                            LOGGER.log(Level.WARNING, "A line of test file is ignored, because the number of columns is a mismatch. Expected: 7, but found: " + tokens.length);
                            return false;
                        }
                        return true;
                    })
                    .map(Segment::read)
                    .collect(Collectors.toList());
        }
    }

    private static Segment read(final String[] txtValues) {
        final Coordinate start = new Coordinate(
                Double.parseDouble(txtValues[1]),
                Double.parseDouble(txtValues[2])
        );


        final Coordinate end = new Coordinate(
                Double.parseDouble(txtValues[3]),
                Double.parseDouble(txtValues[4])
        );

        final double ortho = Double.parseDouble(txtValues[5]);
        final double loxo = Double.parseDouble(txtValues[6]);

        return new Segment(txtValues[0], start, end, ortho, loxo);
    }

    private static String removeComments(final String in) {
        int commentStart = in.indexOf('#');
        switch (commentStart) {
            case -1: return in;
            case 0: return "";
            default: return in.substring(0, commentStart);
        }
    }
}
