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
 * A compressor for line series data.
 */
public class CompressLineSeries extends Compress {

    /** the state indicating the relation between previous and current data points */
    enum STATE {
        /** stepping over x range */
        SteppingOverXRange,

        /** stepping over y range */
        SteppingOverYRange,

        /** out of range again */
        OutOfRangeAgain,

        /** stepping out of x range */
        SteppingOutOfXRange,

        /** stepping in x range */
        SteppingInXRange,

        /** stepping out of y range */
        SteppingOutOfYRange,

        /** stepping out of range */
        SteppingOutOfRange,

        /** in range again */
        InRangeAgain,

        /** stepping in range */
        SteppingInRange;
    }

    /** the flag indicating whether the previous point is out of range */
    private boolean isPrevOutOfRange;

    /*
     * @see Compress#addNecessaryPlots(ArrayList, ArrayList)
     */
    @Override
    protected void addNecessaryPlots(ArrayList<XYdata> list, ArrayList<Integer> indexList) {
        isPrevOutOfRange = true;

        int i = 0;
        XYdata pn1 = null; //series.get(0);
        for (XYdata p : series) {
            STATE state = getState(i, p, pn1);

            switch (state) {
            case SteppingOutOfYRange:
                addToList(list, indexList, p.x, p.y, i);
                break;
            case SteppingOverYRange:
            case SteppingInRange:
            case SteppingInXRange:
                addToList(list, indexList, pn1.x,
                        pn1.y, i - 1);
                addToList(list, indexList, p.x, p.y, i);
                break;
            case SteppingOverXRange:
            case SteppingOutOfXRange:
                addToList(list, indexList, pn1.x,
                        pn1.y, i - 1);
                addToList(list, indexList, p.x, p.y, i);
                i = series.size();
                break;
            case SteppingOutOfRange:
                addToList(list, indexList, p.x, p.y, i);
                i = series.size();
                break;
            case InRangeAgain:
                if (!isInSameGridAsPrevious(p.x, p.y)) {
                    addToList(list, indexList, p.x, p.y,
                            i);
                }
                break;
            case OutOfRangeAgain:
                break;
            default:
                break;
            }
            pn1 = p;
            i++;
        }
    }

    /**
     * Gets the state for each plot.
     * @param index
     *            the index for plot
     * @param p
     *            the series point at the current index
     * @param pn1
     *            the series point at the last index
     * @return the state of plot for the given index
     */
    private STATE getState(int index, XYdata p, XYdata pn1) {
//        XYdata p = series.get(index);
//        XYdata pn1;
//        if (index>0) {
//        	pn1 = series.get(index - 1);
//        } else {
//        	pn1 = null;
//        }

        STATE state;
        
        if (xLower <= p.x && p.x <= xUpper) {
            if (yLower <= p.y && p.y <= yUpper) {
                if (index > 0 && isPrevOutOfRange) {
                    state = STATE.SteppingInRange;
                } else {
                    state = STATE.InRangeAgain;
                }
            } else {
                if (isPrevOutOfRange) {
                    if (index > 0
                            && ((pn1.y < yLower && p.y > yUpper) || pn1.y > yUpper
                                    && p.y < yLower)) {
                        state = STATE.SteppingOverYRange;
                    } else if (index > 0 && pn1.x < xLower
                            && p.x > xLower) {
                        state = STATE.SteppingInXRange;
                    } else {
                        state = STATE.OutOfRangeAgain;
                    }
                } else {
                    state = STATE.SteppingOutOfYRange;
                }
            }
        } else {
            if (!isPrevOutOfRange) {
                state = STATE.SteppingOutOfRange;
            } else if (index > 0 && pn1.x < xUpper
                    && p.x > xUpper) {
                state = STATE.SteppingOutOfXRange;
            } else if (index > 0 && pn1.x < xLower
                    && p.x > xUpper) {
                state = STATE.SteppingOverXRange;
            } else {
                state = STATE.OutOfRangeAgain;
            }
        }

        // set flag
        if (xLower <= p.x && p.x <= xUpper
                && yLower <= p.y && p.y <= yUpper) {
            isPrevOutOfRange = false;
        } else {
            isPrevOutOfRange = true;
        }

        return state;
    }
}
