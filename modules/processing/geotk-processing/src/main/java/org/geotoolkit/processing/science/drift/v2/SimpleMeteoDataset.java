/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class SimpleMeteoDataset implements MeteoDataset {

    final UVSource wind;
    final UVSource current;

    public SimpleMeteoDataset(UVSource wind, UVSource current) {
        this.wind = wind;
        this.current = current;
    }

    @Override
    public UVSource getWind() {
        return wind;
    }

    @Override
    public UVSource getCurrent() {
        return current;
    }
}
