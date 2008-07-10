package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ITitle;

/**
 * The chart property page on properties dialog.
 */
public class ChartPage extends AbstractPage {

	private ColorSelector backgroundInPlotAreaButton;

	private ColorSelector backgroundButton;

	private Button orientationButton;

	private Button showTitleButton;

	private Label titleLabel;

	private Text titleText;

	private Label fontSizeLabel;

	private Spinner fontSizeSpinner;

	private Label titleColorLabel;

	private ColorSelector titleColorButton;

	/**
	 * Constructor.
	 * 
	 * @param chart
	 *            the chart
	 * @param title
	 *            the title
	 */
	public ChartPage(Chart chart, String title) {
		super(chart, title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		addChartPanel(composite);
		addTitleGroup(composite);

		selectValues();
		return composite;
	}

	/**
	 * Adds the chart panel.
	 * 
	 * @param parent
	 *            the parent to add the chart panel
	 */
	private void addChartPanel(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(2, false));

		createLabelControl(panel, "Background in plot area:");
		backgroundInPlotAreaButton = createColorButtonControl(panel);

		createLabelControl(panel, "Background:");
		backgroundButton = createColorButtonControl(panel);

		orientationButton = createCheckBoxControl(panel,
				"Vertical orientation:");
	}

	/**
	 * Adds the title group.
	 * 
	 * @param parent
	 *            the parent to add the title group
	 */
	private void addTitleGroup(Composite parent) {
		Group group = createGroupControl(parent, "Title:", false);

		showTitleButton = createCheckBoxControl(group, "Show title");
		showTitleButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setTitleControlsEnable(showTitleButton.getSelection());
			}
		});

		titleLabel = createLabelControl(group, "Text:");
		titleText = createTextControl(group);

		fontSizeLabel = createLabelControl(group, "Font size:");
		fontSizeSpinner = createSpinnerControl(group, 8, 30);

		titleColorLabel = createLabelControl(group, "Color:");
		titleColorButton = createColorButtonControl(group);
	}

	/**
	 * Selects the values for controls.
	 */
	private void selectValues() {
		backgroundInPlotAreaButton.setColorValue(chart
				.getBackgroundInPlotArea().getRGB());
		backgroundButton.setColorValue(chart.getBackground().getRGB());
		orientationButton.setSelection(chart.getOrientation() == SWT.VERTICAL);

		ITitle title = chart.getTitle();
		showTitleButton.setSelection(title.isVisible());
		setTitleControlsEnable(title.isVisible());
		titleText.setText(title.getText());
		fontSizeSpinner.setSelection(title.getFont().getFontData()[0]
				.getHeight());
		titleColorButton.setColorValue(title.getForeground().getRGB());
	}

	/**
	 * Sets the enable state of title controls.
	 * 
	 * @param enabled
	 *            true if title controls are enabled
	 */
	private void setTitleControlsEnable(boolean enabled) {
		titleLabel.setEnabled(enabled);
		titleText.setEnabled(enabled);
		fontSizeLabel.setEnabled(enabled);
		fontSizeSpinner.setEnabled(enabled);
		titleColorLabel.setEnabled(enabled);
		titleColorButton.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.preference.AbstractPreferencePage#apply()
	 */
	@Override
	public void apply() {
		chart.setBackgroundInPlotArea(new Color(Display.getDefault(),
				backgroundInPlotAreaButton.getColorValue()));
		chart.setBackground(new Color(Display.getDefault(), backgroundButton
				.getColorValue()));
		chart.setOrientation(orientationButton.getSelection() ? SWT.VERTICAL
				: SWT.HORIZONTAL);

		ITitle title = chart.getTitle();
		title.setVisible(showTitleButton.getSelection());
		title.setText(titleText.getText());
		FontData fontData = title.getFont().getFontData()[0];
		Font font = new Font(title.getFont().getDevice(), fontData.getName(),
				fontSizeSpinner.getSelection(), fontData.getStyle());
		title.setFont(font);
		title.setForeground(new Color(Display.getDefault(), titleColorButton
				.getColorValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		backgroundInPlotAreaButton.setColorValue(Constants.WHITE);
		backgroundButton.setColorValue(Display.getDefault().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND).getRGB());
		orientationButton.setSelection(false);

		showTitleButton.setSelection(true);
		setTitleControlsEnable(true);
		titleText.setText("Chart Title");
		fontSizeSpinner.setSelection(Constants.LARGE_FONT_SIZE);
		titleColorButton.setColorValue(Constants.BLUE);

		super.performDefaults();
	}
}