package org.swtchart.ext.internal.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.swtchart.Chart;

/**
 * Abstract class for properties page with selector.
 */
abstract public class AbstractSelectorPage extends AbstractPage {

	private List list;

	private String selector;

	private boolean selectorEnabled;

	protected int selectedIndex;

	/**
	 * Constructor.
	 * 
	 * @param chart
	 *            the chart
	 * @param title
	 *            the title
	 * @param selector
	 *            the selector name
	 */
	public AbstractSelectorPage(Chart chart, String title, String selector) {
		super(chart, title);
		this.selector = selector;
		selectedIndex = 0;
		selectorEnabled = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		String[] items = getListItems();
		if (items.length < 2) {
			selectorEnabled = false;
		}

		Composite composite = new Composite(parent, SWT.NONE);
		if (selectorEnabled) {
			GridLayout layout = new GridLayout(3, true);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			composite.setLayout(layout);

			Label label = new Label(composite, SWT.NULL);
			label.setText(selector);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			label.setLayoutData(gridData);

			addLeftPanel(composite, items);
			addRightPanel(composite);
		} else {
			GridLayout layout = new GridLayout(1, true);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			composite.setLayout(layout);
			addRightPanelContents(composite);
		}
		
		selectInitialValues();
		updateControlSelections();
		
		return composite;
	}

	/**
	 * Gets the list items.
	 * 
	 * @return the list items
	 */
	abstract protected String[] getListItems();

	/**
	 * Selects value for each control.
	 */
	abstract protected void selectInitialValues();

	/**
	 * Adds the left panel.
	 * 
	 * @param parent
	 *            the parent to add the left panel
	 */
	private void addLeftPanel(Composite parent, String[] items) {
		Composite leftPanel = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		leftPanel.setLayoutData(gridData);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		leftPanel.setLayout(layout);

		list = new List(leftPanel, SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		gridData2.horizontalSpan = 1;
		list.setLayoutData(gridData2);
		for (String item : items) {
			list.add(item);
		}

		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedIndex = list.getSelectionIndex();
				updateControlSelections();
			}
		});
		list.select(0);
	}

	/**
	 * Adds the right panel.
	 * 
	 * @param parent
	 *            the parent to add the right panel
	 */
	private void addRightPanel(Composite parent) {
		Composite rightPanel = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		rightPanel.setLayoutData(gridData);
		rightPanel.setLayout(new GridLayout(1, false));

		addRightPanelContents(rightPanel);
	}

	/**
	 * Adds the contents on right panel.
	 * 
	 * @param parent
	 *            the parent to add the contents
	 */
	abstract protected void addRightPanelContents(Composite parent);

	/**
	 * Updates the selection on controls.
	 */
	abstract protected void updateControlSelections();
}