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
 * A compressor for bar series data.
 */
public class CompressBarSeries extends Compress {

    /*
     * @see Compress#addNecessaryPlots(ArrayList, ArrayList)
     */
    @Override
    protected void addNecessaryPlots(ArrayList<XYdata> list, ArrayList<Integer> indexList) {

        double prevX = series.get(0).x;
        double maxY = Double.NaN;
        int prevIndex = 0;

        int i = 0;
        for (XYdata p : list) {
        //for (int i = 0; i < xSeries.length && i < ySeries.length; i++) {
            if (p.x >= config.getXLowerValue()) {
                if (isInSameGridXAsPrevious(p.x)) {
                    if (maxY < p.y) {
                        maxY = p.y;
                    }
                } else {
                    if (!Double.isNaN(maxY)) {
                        addToList(list, indexList, prevX, maxY, prevIndex);
                    }
                    prevX = p.x;
                    maxY = p.y;
                    prevIndex = i;
                }
            }

            if (p.x > config.getXUpperValue()) {
                break;
            }
            
            i++;
        }
        addToList(list, indexList, prevX, maxY, prevIndex);
    }

    /**
     * Checks if the given x coordinate is in the same grid as previous.
     * 
     * @param x
     *            the X coordinate
     * @return true if the given coordinate is in the same grid as previous
     */
    private boolean isInSameGridXAsPrevious(double x) {
        int xGridIndex = (int) ((x - config.getXLowerValue())
                / (config.getXUpperValue() - config.getXLowerValue()) * config
                .getWidthInPixel());

        boolean isInSameGridAsPrevious = (xGridIndex == previousXGridIndex);

        previousXGridIndex = xGridIndex;

        return isInSameGridAsPrevious;
    }
}
