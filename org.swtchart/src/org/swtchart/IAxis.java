package org.swtchart;

/**
 * An axis which is composed of title and tick. Grid is associated with axis.
 */
public interface IAxis {

	/** An axis direction */
	public enum Direction {

		/** the constant to represent X axis */
		X,

		/** the constant to represent Y axis */
		Y
	}

	/** An axis position */
	public enum Position {

		/** bottom or left side of chart */
		Primary,

		/** top or right side chart */
		Seconday
	}

	/**
	 * Gets the axis id.
	 * <p>
	 * An axis id is automatically assigned when axis is created.
	 * 
	 * @return the axis id
	 */
	int getId();

	/**
	 * Gets the axis direction.
	 * <p>
	 * The axis direction is set when axis is created, and won't be changed.
	 * 
	 * @return the axis direction
	 */
	Direction getDirection();

	/**
	 * Gets the axis position.
	 * 
	 * @return the axis position
	 */
	Position getPosition();

	/**
	 * Sets the axis position.
	 * 
	 * @param position
	 *            the axis position
	 */
	void setPosition(Position position);

	/**
	 * Sets the axis range.
	 * 
	 * @param range
	 *            the axis range
	 */
	void setRange(Range range);

	/**
	 * Gets the axis range.
	 * 
	 * @return the axis range
	 */
	Range getRange();

	/**
	 * Gets the axis title.
	 * 
	 * @return the axis title
	 */
	ITitle getTitle();

	/**
	 * Gets the axis tick.
	 * 
	 * @return the axis tick
	 */
	IAxisTick getTick();

	/**
	 * Enables the log scale. If enabling log scale, stacking trace and category
	 * axis will be disabled.
	 * 
	 * @param enabled
	 *            true if enabling log scales
	 * @throws IllegalStateException
	 *             if minimum value of series belonging to this axis is less
	 *             than zero.
	 */
	void enableLogScale(boolean enabled) throws IllegalStateException;

	/**
	 * Gets the state indicating if log scale is enabled.
	 * 
	 * @return true if log scale is enabled
	 */
	boolean isLogScaleEnabled();

	/**
	 * Gets the grid. The gird interval is identical with the position of axis
	 * tick marks. The horizontal grid is accessible from vertical axis, and the
	 * vertical grid is accessible from horizontal axis.
	 * 
	 * @return grid the grid
	 */
	IGrid getGrid();

	/**
	 * Auto-scales the axes. The axis range will be adjusted to the existing
	 * series on chart, so that all series are completely shown.
	 */
	void autoScale();

	/**
	 * Zooms in the axis.
	 */
	void zoomIn();

	/**
	 * Zooms out the axis.
	 */
	void zoomOut();

	/**
	 * Scrolls up the axis.
	 */
	void scrollUp();

	/**
	 * Scrolls up the axis.
	 */
	void scrollDown();

	/**
	 * Enables category. Category is applicable only for X axis. If category
	 * series are not yet set, category won't be enabled.
	 * 
	 * @param enabled
	 *            true if enabling category
	 */
	void enableCategory(boolean enabled);

	/**
	 * Gets the state indicating if category is enabled.
	 * 
	 * @return true if category is enabled
	 */
	boolean isCategoryEnabled();

	/**
	 * Sets the category series. In order to enable category series,
	 * <tt>enableCategoryAxis(true)</tt> has to be invoked.
	 * 
	 * @param series
	 *            the category series
	 */
	void setCategorySeries(String[] series);

	/**
	 * Gets the category series. If the category series haven't been set yet,
	 * <tt>null</tt> will be returned.
	 * 
	 * @return the category series
	 */
	String[] getCategorySeries();
}