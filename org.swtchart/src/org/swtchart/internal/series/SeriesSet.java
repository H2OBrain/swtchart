/*******************************************************************************
 * Copyright (c) 2008-2016 SWTChart project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.series;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Direction;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressConfig;
import org.swtchart.internal.compress.ICompress;

/**
 * A series container.
 */
public class SeriesSet implements ISeriesSet {

    /** the chart */
    private final Chart chart;

    /** the series */
    private LinkedHashMap<String, Series> seriesMap;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     */
    public SeriesSet(Chart chart) {
        this.chart = chart;

        seriesMap = new LinkedHashMap<>();
    }

    /*
     * @see ISeriesSet#createSeries(ISeries.SeriesType, String)
     */
    public ISeries createSeries(SeriesType type, String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
            return null; // to suppress warning...
        }

        String trimmedId = id.trim();

        if ("".equals(trimmedId)) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        Series series = null;
        if (type == SeriesType.BAR) {
            series = new BarSeries(chart, trimmedId);
        } else if (type == SeriesType.LINE) {
            series = new LineSeries(chart, trimmedId);
        } else {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            return null; // to suppress warning...
        }

        Series oldSeries = seriesMap.get(trimmedId);
        if (oldSeries != null) {
            oldSeries.dispose();
        }

        int[] xAxisIds = chart.getAxisSet().getXAxisIds();
        int[] yAxisIds = chart.getAxisSet().getYAxisIds();
        series.setXAxisId(xAxisIds[0]);
        series.setYAxisId(yAxisIds[0]);

        seriesMap.put(trimmedId, series);

        Axis axis = (Axis) chart.getAxisSet().getXAxis(xAxisIds[0]);
        if (axis != null) {
            updateStackAndRiserData();
        }

        // legend will be shown if there is previously no series.
        chart.updateLayout();

        return series;
    }

    /*
     * @see ISeriesSet#getSeries(String)
     */
    public ISeries getSeries(String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        String trimmedId = id.trim();
        return seriesMap.get(trimmedId);
    }

    /*
     * @see ISeriesSet#getSeries()
     */
    public ISeries[] getSeries() {
        Set<String> keys = seriesMap.keySet();
        ISeries[] series = new ISeries[keys.size()];
        int i = 0;
        for (String key : keys) {
            series[i++] = seriesMap.get(key);
        }
        return series;
    }

    /*
     * @see ISeriesSet#deleteSeries(String)
     */
    public void deleteSeries(String id) {
    	String trimmedId = validateSeriesId(id);

        seriesMap.get(trimmedId).dispose();
        seriesMap.remove(trimmedId);

        updateStackAndRiserData();

        // legend will be hidden if this is the last series
        chart.updateLayout();
    }

    /*
     * @see ISeriesSet#bringForward(String)
     */
    public void bringForward(String id) {
    	String trimmedId = validateSeriesId(id);

        String seriesId = null;
        LinkedHashMap<String, Series> newSeriesMap = new LinkedHashMap<>();
        for (Entry<String, Series> entry : seriesMap.entrySet()) {

            if (entry.getKey().equals(trimmedId)) {
                seriesId = trimmedId;
                continue;
            }

            newSeriesMap.put(entry.getKey(), entry.getValue());

            if (seriesId != null) {
                newSeriesMap.put(seriesId, seriesMap.get(seriesId));
                seriesId = null;
            }
        }
        if (seriesId != null) {
            newSeriesMap.put(seriesId, seriesMap.get(seriesId));
        }
        seriesMap = newSeriesMap;

        updateStackAndRiserData();
        chart.updateLayout();
    }

    /*
     * @see ISeriesSet#bringToFront(String)
     */
    public void bringToFront(String id) {
    	String trimmedId = validateSeriesId(id);

        Series series = seriesMap.get(trimmedId);
        seriesMap.remove(trimmedId);
        seriesMap.put(series.getId(), series);

        updateStackAndRiserData();
        chart.updateLayout();
    }

    /*
     * @see ISeriesSet#sendBackward(String)
     */
    public void sendBackward(String id) {
    	String trimmedId = validateSeriesId(id);

        String seriesId = null;
        LinkedHashMap<String, Series> newSeriesMap = new LinkedHashMap<>();
        for (Entry<String, Series> entry : seriesMap.entrySet()) {

            if (!entry.getKey().equals(trimmedId) || seriesId == null) {
                newSeriesMap.put(entry.getKey(), entry.getValue());
                seriesId = entry.getKey();
                continue;
            }

            newSeriesMap.remove(seriesId);
            newSeriesMap.put(entry.getKey(), entry.getValue());
            newSeriesMap.put(seriesId, seriesMap.get(seriesId));
        }
        seriesMap = newSeriesMap;

        updateStackAndRiserData();
        chart.updateLayout();
    }

    /*
     * @see ISeriesSet#sendToBack(String)
     */
    public void sendToBack(String id) {
    	String trimmedId = validateSeriesId(id);

        LinkedHashMap<String, Series> newSeriesMap = new LinkedHashMap<>();
        newSeriesMap.put(trimmedId, seriesMap.get(trimmedId));
        for (Entry<String, Series> entry : seriesMap.entrySet()) {
            if (!entry.getKey().equals(trimmedId)) {
                newSeriesMap.put(entry.getKey(), entry.getValue());
            }
        }
        seriesMap = newSeriesMap;

        updateStackAndRiserData();
        chart.updateLayout();
    }

    /**
     * Disposes the series.
     */
    public void dispose() {
        for (Entry<String, Series> entry : seriesMap.entrySet()) {
            entry.getValue().dispose();
        }
    }

    /**
     * Validates the given series id.
     * 
     * @param id
     *            the series id.
     * @return the valid series id
     */
    private String validateSeriesId(String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        String trimmedId = id.trim();
        if (seriesMap.get(trimmedId) == null) {
            throw new IllegalArgumentException("Given series id doesn't exist");
        }

        return trimmedId;
    }

    /**
     * Compresses all series data.
     */
    public void compressAllSeries() {
        if (!chart.isCompressEnabled()) {
            return;
        }

        CompressConfig config = new CompressConfig();

        final int PRECISION = 2;
        Point p = chart.getPlotArea().getSize();
        int width = p.x * PRECISION;
        int height = p.y * PRECISION;
        config.setSizeInPixel(width, height);

        for (ISeries series : getSeries()) {
            int xAxisId = series.getXAxisId();
            int yAxisId = series.getYAxisId();

            IAxis xAxis = chart.getAxisSet().getXAxis(xAxisId);
            IAxis yAxis = chart.getAxisSet().getYAxis(yAxisId);
            if (xAxis == null || yAxis == null) {
                continue;
            }
            Range xRange = xAxis.getRange();
            Range yRange = yAxis.getRange();

            if (xRange == null || yRange == null) {
                continue;
            }

            double xMin = xRange.lower;
            double xMax = xRange.upper;
            double yMin = yRange.lower;
            double yMax = yRange.upper;

            config.setXLogScale(xAxis.isLogScaleEnabled());
            config.setYLogScale(yAxis.isLogScaleEnabled());

            double lower = xMin - (xMax - xMin) * 0.015;
            double upper = xMax + (xMax - xMin) * 0.015;
            if (xAxis.isLogScaleEnabled()) {
                lower = ((Series) series).getXRange().lower;
            }
            config.setXRange(lower, upper);
            lower = yMin - (yMax - yMin) * 0.015;
            upper = yMax + (yMax - yMin) * 0.015;
            if (yAxis.isLogScaleEnabled()) {
                lower = ((Series) series).getYRange().lower;
            }
            config.setYRange(lower, upper);

            ICompress compressor = ((Series) series).getCompressor();
            compressor.compress(config);
        }
    }

    /**
     * Updates the compressor associated with the given axis.
     * <p>
     * In most cases, compressor is updated when series is changed. However,
     * there is a case that compressor has to be updated with the changes in
     * axis.
     * 
     * @param axis
     *            the axis
     */
    public void updateCompressor(Axis axis) {
        for (ISeries series : getSeries()) {
            int axisId = (axis.getDirection() == Direction.X) ? series
                    .getXAxisId() : series.getYAxisId();
            if (axisId != axis.getId()) {
                continue;
            }

            ICompress compressor = ((Series) series).getCompressor();
            if (axis.isValidCategoryAxis()) {
                String[] categorySeries = axis.getCategorySeries();
                if (categorySeries == null) {
                    continue;
                }
                ArrayList<XYdata> oseries = series.getSeries();
                ArrayList<XYdata> cseries = new ArrayList<>(categorySeries.length);
                for (int i = 0; i < categorySeries.length; i++) {
                	if (oseries.size()>i) {
                		cseries.add(new XYdata(i, oseries.get(i).y));
                	} else {
                		cseries.add(new XYdata(i, 0)); // TODO newly added hack!
                	}
                }
                compressor.setSeries(cseries);
            } else if (((Series) series).getSeries() != null) { // FIXME is never null!
                compressor.setSeries(((Series) series).getSeries());
            }
        }
        compressAllSeries();
    }

    /**
     * Updates the stack and riser data.
     */
    public void updateStackAndRiserData() {
        if (chart.isUpdateSuspended()) {
            return;
        }

        for (IAxis xAxis : chart.getAxisSet().getXAxes()) {
            ((Axis) xAxis).setNumRisers(0);
            for (IAxis yAxis : chart.getAxisSet().getYAxes()) {
                updateStackAndRiserData(xAxis, yAxis);
            }
        }
    }

    /**
     * Updates the stack and riser data for given axes.
     * 
     * @param xAxis
     *            the X axis
     * @param yAxis
     *            the Y axis
     */
    private void updateStackAndRiserData(IAxis xAxis, IAxis yAxis) {

        int riserCnt = 0;
        int stackRiserPosition = -1;
        double[] stackBarSeries = null;
        double[] stackLineSeries = null;

        if (((Axis) xAxis).isValidCategoryAxis()) {
            String[] categorySeries = xAxis.getCategorySeries();
            if (categorySeries != null) {
                int size = categorySeries.length;
                stackBarSeries = new double[size];
                stackLineSeries = new double[size];
            }
        }

        for (ISeries series : getSeries()) {
            if (series.getXAxisId() != xAxis.getId()
                    || series.getYAxisId() != yAxis.getId()
                    || !series.isVisible()) {
                continue;
            }

            if (series.isStackEnabled()
                    && !chart.getAxisSet().getYAxis(series.getYAxisId())
                            .isLogScaleEnabled()
                    && ((Axis) xAxis).isValidCategoryAxis()) {
                if (series.getType() == SeriesType.BAR) {
                    if (stackRiserPosition == -1) {
                        stackRiserPosition = riserCnt;
                        riserCnt++;
                    }
                    ((BarSeries) series).setRiserIndex(((Axis) xAxis)
                            .getNumRisers() + stackRiserPosition);
                    setStackSeries(stackBarSeries, series);
                } else if (series.getType() == SeriesType.LINE) {
                    setStackSeries(stackLineSeries, series);
                }
            } else {
                if (series.getType() == SeriesType.BAR) {
                    ((BarSeries) series).setRiserIndex(((Axis) xAxis)
                            .getNumRisers() + riserCnt++);
                }
            }
        }

        ((Axis) xAxis).setNumRisers(((Axis) xAxis).getNumRisers() + riserCnt);
    }

    /**
     * Sets the stack series.
     * 
     * @param stackSeries
     *            the stack series
     * @param series
     *            the series
     */
    private static void setStackSeries(double[] stackSeries, ISeries series) {
        ArrayList<XYdata> oseries = series.getSeries();
        if (oseries == null || stackSeries == null) {
            return;
        }

        // TODO optimize
        for (int i = 0; i < stackSeries.length; i++) {
            if (i >= oseries.size()) {
                break;
            }
            stackSeries[i] = BigDecimal.valueOf(stackSeries[i])
                    .add(BigDecimal.valueOf(oseries.get(i).y)).doubleValue();
        }
        double[] copiedStackSeries = new double[stackSeries.length];
        System.arraycopy(stackSeries, 0, copiedStackSeries, 0,
                stackSeries.length);
        ((Series) series).setStackSeries(copiedStackSeries);
    }
}
