/*******************************************************************************
 * Copyright (c) 2008-2016 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.compress;

import java.util.ArrayList;

import org.swtchart.internal.series.XYdata;

/**
 * A Compressor.
 */
public interface ICompress {

    /**
     * Gets the compressed series
     * 
     * @return the compressed series
     */
    public abstract ArrayList<XYdata> getCompressedSeries();

    /**
     * Gets the compressed series indexes
     * 
     * @return the compressed series indexes
     */
    public abstract ArrayList<Integer> getCompressedIndexes();

    /**
     * Sets series which have to be sorted.
     * 
     * @param series
     *            the series
     */
    public abstract void setSeries(ArrayList<XYdata> series);

    /**
     * Ignores the points which are in the same grid as the previous point.
     * 
     * @param config
     *            the configuration for compression
     * @return true if the compression succeeds
     */
    public abstract boolean compress(CompressConfig config);

}