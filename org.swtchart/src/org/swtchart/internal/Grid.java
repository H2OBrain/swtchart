package org.swtchart.internal;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Constants;
import org.swtchart.IGrid;
import org.swtchart.LineStyle;
import org.swtchart.internal.axis.Axis;

/**
 * A grid.
 */
public class Grid implements IGrid {

	/** the axis */
	private Axis axis;

	/** the grid color */
	private Color color;

	/** the visibility state */
	private boolean isVisible;

	/** the line style */
	private LineStyle lineStyle;

	/** the line width */
	private final static int LINE_WIDTH = 1;

	/** the default style */
	private final static LineStyle DEFAULT_STYLE = LineStyle.DOT;

	/** the default color */
	private final static RGB DEFAULT_FOREGROUND = Constants.GRAY;

	/**
	 * Constructor.
	 * 
	 * @param axis
	 *            the axis
	 */
	public Grid(Axis axis) {
		this.axis = axis;

		color = new Color(Display.getDefault(), DEFAULT_FOREGROUND);
		lineStyle = DEFAULT_STYLE;
		isVisible = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IGrid#getForeground()
	 */
	public Color getForeground() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IGrid#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (color == null) {
			color = new Color(Display.getDefault(), DEFAULT_FOREGROUND);
		}

		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IGrid#getStyle()
	 */
	public LineStyle getStyle() {
		return lineStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IGrid#setStyle(org.swtchart.LineStyle)
	 */
	public void setStyle(LineStyle style) {
		if (style == null) {
			style = DEFAULT_STYLE;
		}

		this.lineStyle = style;
	}

	/**
	 * Draws grid.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw grid
	 * @param height
	 *            the height to draw grid
	 */
	protected void draw(GC gc, int width, int height) {
		if (!isVisible || lineStyle.equals(LineStyle.NONE)) {
			return;
		}

		int xWidth;
		if (axis.isHorizontalAxis()) {
			xWidth = width;
		} else {
			xWidth = height;
		}

		gc.setForeground(color);
		ArrayList<Integer> tickLabelPosition = axis.getTick()
				.getAxisTickLabels().getTickLabelPositions();

		gc.setLineStyle(Util.getIndexDefinedInSWT(lineStyle));
		if (axis.isValidCategoryAxis()) {
			int step = 0;
			if (tickLabelPosition.size() > 1) {
				step = tickLabelPosition.get(1).intValue()
						- tickLabelPosition.get(0).intValue();
			}
			int x = (int) (tickLabelPosition.get(0).intValue() - step / 2d);

			for (int i = 0; i < tickLabelPosition.size() + 1; i++) {
				x += step;
				if (x >= xWidth) {
					continue;
				}

				if (axis.isHorizontalAxis()) {
					gc.drawLine(x, LINE_WIDTH, x, height - LINE_WIDTH);
				} else {
					gc.drawLine(LINE_WIDTH, x, width - LINE_WIDTH, x);
				}
			}
		} else {
			for (int i = 0; i < tickLabelPosition.size(); i++) {
				int x = tickLabelPosition.get(i).intValue();
				if (x >= xWidth) {
					continue;
				}

				if (axis.isHorizontalAxis()) {
					gc.drawLine(x, LINE_WIDTH, x, height - LINE_WIDTH);
				} else {
					gc.drawLine(LINE_WIDTH, height - 1 - x, width - LINE_WIDTH,
							height - 1 - x);
				}
			}
		}
	}
}