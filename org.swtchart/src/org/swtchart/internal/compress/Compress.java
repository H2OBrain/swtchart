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
 * A base class for compressor providing default implementations.
 */
public abstract class Compress implements ICompress {

    /** the previous X grid index */
    protected int previousXGridIndex;

    /** the previous Y grid index */
    protected int previousYGridIndex;

    /** the configuration for compressor */
    protected CompressConfig config;

    /** the previous configuration for compressor */
    protected CompressConfig prevConfig;

    /** the flag indicating whether the data is compressed */
    protected boolean compressed;

    /** the source series to be compressed */
    protected ArrayList<XYdata> series = null;

    /** the compressed series */
    protected transient ArrayList<XYdata> compressedSeries = null;

    /** the compressed series indexes */
    protected transient ArrayList<Integer> compressedIndexes = null;

    /** the lower value of x range */
    protected double xLower;

    /** the upper value of x range */
    protected double xUpper;

    /** the lower value of y range */
    protected double yLower;

    /** the upper value of y range */
    protected double yUpper;

    /** the state indicating if x axis is log scale */
    private boolean isXLogScale;

    /** the state indicating if y axis is log scale */
    private boolean isYLogScale;

    /** the plot area width in pixels */
    private long widthInPixel;

    /** the plot area height in pixels */
    private long heightInPixel;

    /*
     * @see ICompress#setSeries(double[])
     */
    public void setSeries(ArrayList<XYdata> series) {
        if (series == null) {
            return;
        }

        this.series = series;
        compressedSeries = series;
        compressedIndexes = new ArrayList<>(series.size()); // TODO optimize..
        for (int i = 0; i < series.size(); i++) {
            compressedIndexes.add(i);
        }

        compressed = false;
    }

    /*
     * @see ICompress#getCompressedSeries()
     */
    public ArrayList<XYdata> getCompressedSeries() {
        return compressedSeries;
    }

    /*
     * @see ICompress#getCompressedIndexes()
     */
    public ArrayList<Integer> getCompressedIndexes() {
        return compressedIndexes;
    }

    /*
     * @see ICompress#compress(CompressConfig)
     */
    final public boolean compress(CompressConfig compressConfig) {

        if ((compressConfig.equals(prevConfig) && compressed)
                || series == null || series.size() == 0) {
            return false;
        }

        // store the previous configuration
        prevConfig = new CompressConfig(compressConfig);

        this.config = compressConfig;

        // store into fields to improve performance
        xLower = config.getXLowerValue();
        xUpper = config.getXUpperValue();
        yLower = config.getYLowerValue();
        yUpper = config.getYUpperValue();
        isXLogScale = config.isXLogScale();
        isYLogScale = config.isYLogScale();
        widthInPixel = config.getWidthInPixel();
        heightInPixel = config.getHeightInPixel();

        previousXGridIndex = -1;
        previousYGridIndex = -1;

        compressedSeries = new ArrayList<>();
        compressedIndexes = new ArrayList<>();

        // add necessary plots to the array
        addNecessaryPlots(compressedSeries, compressedIndexes);

        compressed = true;

        return true;
    }

    /**
     * Adds the necessary plots.
     * 
     * @param list
     *            the list to store the XY coordinate
     * @param indexList
     *            the array in which series index for necessary plot is stored
     */
    abstract protected void addNecessaryPlots(
    		ArrayList<XYdata> cseries, ArrayList<Integer> indexList);

    /**
     * Adds the given coordinate to list.
     * 
     * @param list
     *            the list to store the XY coordinate
     * @param indexList
     *            the list to store the series index
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     * @param index
     *            the series index
     */
    protected void addToList(ArrayList<XYdata> list, ArrayList<Integer> indexList,
    		double x, double y, int index) {
        list.add(new XYdata(x, y));
        indexList.add(index);
    }

    /**
     * Checks if the given coordinate is in the same grid as previous.
     * 
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     * @return true if the given coordinate is in the same grid as previous
     */
    protected boolean isInSameGridAsPrevious(double x, double y) {
        int xGridIndex;
        int yGridIndex;

        // calculate the X grid index
        if (isXLogScale) {
            double lower = Math.log10(xLower);
            double upper = Math.log10(xUpper);
            xGridIndex = (int) ((Math.log10(x) - lower) / (upper - lower) * widthInPixel);
        } else {
            xGridIndex = (int) ((x - xLower) / (xUpper - xLower) * widthInPixel);
        }

        // calculate the Y grid index
        if (isYLogScale) {
            double lower = Math.log10(yLower);
            double upper = Math.log10(yUpper);
            yGridIndex = (int) ((Math.log10(y) - lower) / (upper - lower) * heightInPixel);
        } else {
            yGridIndex = (int) ((y - yLower) / (yUpper - yLower) * heightInPixel);
        }

        // check if the grid index is the same as previous
        boolean isInSameGridAsPrevious = (xGridIndex == previousXGridIndex && yGridIndex == previousYGridIndex);

        // store the previous grid index
        previousXGridIndex = xGridIndex;
        previousYGridIndex = yGridIndex;

        return isInSameGridAsPrevious;
    }
}
