package colorSet;

import gmmlVision.GmmlVision;
import graphics.GmmlLegend;

import java.io.FileInputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import data.GmmlDb;
import data.GmmlGex;
import data.GmmlGex.Sample;

public class ColorSetWindow extends ApplicationWindow {
	public String[] cgColumnNames;
	public ArrayList<Integer> cgColumnIndex;

	public GmmlGex gmmlGex;
	
	public ColorSetWindow(Shell parent)
	{
		super(parent);
		setBlockOnOpen(true);
	}
	
	public void setColorSets(Vector colorSets)
	{
		gmmlGex.colorSets = colorSets;
	}
	
	public void setGmmlGex(GmmlGex gmmlGex)
	{
		this.gmmlGex = gmmlGex;
	}
	
	public Vector getColorSets()
	{
		return gmmlGex.colorSets;
	}
	
	public void run()
	{
		gmmlGex.loadColorSets();
		open();
		
		csColorGnf.dispose();
		csColorNc.dispose();
		cgColor1.dispose();
		cgColor2.dispose();
		
		gmmlGex.saveColorSets();
		
	}
	
	TableViewer coTableViewer;
	SashForm sashForm;
	GmmlLegend legend;
	SashForm topSash;
	Combo csCombo;
	Composite coTableComposite;
	
	protected Control createContents(Composite parent)
	{		
		Shell shell = parent.getShell();
		shell.setLocation(parent.getLocation());
		
		shell.setText("Color Set Builder");
		
		topSash = new SashForm(parent, SWT.HORIZONTAL);
		
		Composite coComposite = new Composite(topSash, SWT.NULL);
		coComposite.setLayout(new GridLayout(1, true));
		
		GridData tableGrid = new GridData(GridData.FILL_BOTH);
		tableGrid.horizontalSpan = 2;
		GridData comboGrid = new GridData(GridData.FILL_HORIZONTAL);
		comboGrid.horizontalSpan = 3;
		comboGrid.widthHint = 100;
		
		Group csComboGroup = new Group(coComposite, SWT.SHADOW_ETCHED_IN);
		csComboGroup.setText("Color sets");
		csComboGroup.setLayout(new GridLayout(3, false));
		csComboGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		csCombo = new Combo(csComboGroup, SWT.SINGLE | SWT.READ_ONLY);
		csCombo.setItems(gmmlGex.getColorSetNames());
		csCombo.setLayoutData(comboGrid);
		csCombo.addSelectionListener(new CsComboSelectionAdapter());
		
		Button newCsButton = new Button(csComboGroup, SWT.PUSH);
		newCsButton.setText("New");
		newCsButton.addSelectionListener(new NewCsButtonAdapter());
		newCsButton.pack();
		Button editCsButton = new Button(csComboGroup, SWT.PUSH);
		editCsButton.setText("Edit");
		editCsButton.addSelectionListener(new EditCsButtonAdapter());
		editCsButton.pack();
		Button deleteCsButton = new Button(csComboGroup, SWT.PUSH);
		deleteCsButton.setText("Delete");
		deleteCsButton.addSelectionListener(new DeleteCsButtonAdapter());
		deleteCsButton.pack();

		Group coTableGroup = new Group(coComposite, SWT.SHADOW_ETCHED_IN);
		coTableGroup.setText("Color criteria");
		coTableGroup.setLayout(new GridLayout(2, false));
		coTableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button newCoButton = new Button(coTableGroup, SWT.PUSH);
		newCoButton.setText("New");
		newCoButton.addSelectionListener(new NewCoButtonAdapter());
		newCoButton.pack();
		Button removeCoButton = new Button(coTableGroup, SWT.PUSH);
		removeCoButton.setText("Delete");
		removeCoButton.addSelectionListener(new DeleteCoButtonAdapter());
		removeCoButton.pack();
		
		coTableComposite = new Composite(coTableGroup, SWT.NONE);
		coTableComposite.setLayout(new FillLayout());
		coTableComposite.setLayoutData(tableGrid);
		
		Table table = new Table(coTableComposite, SWT.BORDER | SWT.SINGLE);
		table.addControlListener(new TableGrowListener(table));
		TableColumn coCol = new TableColumn(table, SWT.LEFT);

		coTableViewer = new TableViewer(table);
		coTableViewer.setContentProvider(new CoTableContentProvider());
		coTableViewer.setLabelProvider(new CoTableLabelProvider());
		coTableViewer.addSelectionChangedListener(new CoTableSelectionChangedListener());
		
		sashForm = new SashForm(topSash, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());
		initiateSashForm();
		
		legend = new GmmlLegend(topSash, SWT.NONE, false);
		legend.setGmmlGex(gmmlGex);
		
		topSash.setWeights(new int[] {30, 45, 25} );
		
		DragSource ds = new DragSource(coTableViewer.getTable(), DND.DROP_MOVE);
		ds.addDragListener(new CoTableDragAdapter());
		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		DropTarget dt = new DropTarget(coTableViewer.getTable(), DND.DROP_MOVE);
		dt.addDropListener(new CoTableDropAdapter());
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		
		csComboGroup.pack();
		coTableGroup.pack();
		legend.pack();

		shell.setSize(topSash.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, 500);
		
		csCombo.select(0);
		if(csCombo.getSelectionIndex() > -1)
		{
			GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
			coTableViewer.setInput(cs);
			setRightCompositeContents(cs);
		}
		return parent;
	}

	Composite cnComposite;
	Composite csComposite;
	Composite cgComposite;
	Composite ccComposite;
	
	private void initiateSashForm() {
		cnComposite = new Composite(sashForm, SWT.NONE);
		csComposite = new Composite(sashForm, SWT.NONE);
		cgComposite = new Composite(sashForm, SWT.NONE);
		ccComposite = new Composite(sashForm, SWT.NONE);
		csComposite.setLayout(new GridLayout(1, true));
		cgComposite.setLayout(new GridLayout(1, true));
		ccComposite.setLayout(new GridLayout(1, true));
		
		setCsGroupComponents();
	    setCgGroupComponents();
	    
		sashForm.setMaximizedControl(cnComposite);
	}
	
	Text csNameText;
	CLabel csCLabelNc;
	Button csColorButtonNc;
	CLabel csCLabelGnf;
	Button csColorButtonGnf;
	Color csColorNc;
	Color csColorGnf;
	Table sampleTable;
	TableViewer sampleTableViewer;
	Group csTableGroup;
	List<String> colNames = Arrays.asList(
			new String[] {"Use", "Sample name", "Type"});
	public void setCsGroupComponents()
	{		
		GridData csNameTextGrid = new GridData(GridData.FILL_HORIZONTAL);
		csNameTextGrid.horizontalSpan = 2;
		csNameTextGrid.widthHint = 100;
		GridData csCLabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		csCLabelGrid.widthHint = csCLabelGrid.heightHint = 15;
		GridData colorButtonGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		colorButtonGrid.widthHint = colorButtonGrid.heightHint = 15;
		GridData tableGroupGrid = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		tableGroupGrid.heightHint = 200;
		tableGroupGrid.widthHint = 400;
		
		Group csGroup = new Group(csComposite, SWT.SHADOW_IN);

		csGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		csGroup.setLayout(new GridLayout(3, false));
	    csGroup.setText("Color set options");
	    
	    csTableGroup = new Group(csComposite, SWT.SHADOW_IN);
	    
	    csTableGroup.setLayoutData(tableGroupGrid);
	    csTableGroup.setLayout(new FillLayout());
	    csTableGroup.setText("Color set data");
	    
	    Button csButton = new Button(csComposite, SWT.PUSH);
	    	    
	    csButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    csButton.setText("Save");
	    csButton.addSelectionListener(new csButtonAdapter());
	    
	    // csGroup
	    Label csNameLabel = new Label(csGroup, SWT.CENTER);
	    csNameText = new Text(csGroup, SWT.SINGLE | SWT.BORDER);
	    
	    Label csColorLabelNc = new Label(csGroup, SWT.CENTER);
	    csCLabelNc = new CLabel(csGroup, SWT.SHADOW_IN);
	    csColorButtonNc = new Button(csGroup, SWT.PUSH);
	    csColorButtonNc.addSelectionListener(new ColorButtonAdapter());
	    Label csColorLabelGnf = new Label(csGroup, SWT.CENTER);
	    csCLabelGnf = new CLabel(csGroup, SWT.SHADOW_IN);
	    csColorButtonGnf = new Button(csGroup, SWT.PUSH);
	    csColorButtonGnf.addSelectionListener(new ColorButtonAdapter());
	    
	    csColorLabelNc.setText("No criteria met color:");
	    csColorLabelGnf.setText("Gene not found color:");
	    csColorButtonNc.setText("...");
	    csColorButtonGnf.setText("...");
	    
	    csColorNc = new Color(getShell().getDisplay(), GmmlColorSet.COLOR_NO_CRITERIA_MET);
	    csColorGnf = new Color(getShell().getDisplay(), GmmlColorSet.COLOR_NO_GENE_FOUND);
	    csCLabelNc.setLayoutData(csCLabelGrid);
	    csCLabelGnf.setLayoutData(csCLabelGrid);
	    csCLabelNc.setBackground(csColorNc);
	    csCLabelNc.setBackground(csColorGnf);
	    csColorButtonNc.setLayoutData(colorButtonGrid);
	    csColorButtonGnf.setLayoutData(colorButtonGrid);
	    
	    csNameLabel.setText("Name:");
	    csNameText.setLayoutData(csNameTextGrid);
	    
	    // csTableGroup	    
	    sampleTable = new Table(csTableGroup, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
	    
	    sampleTable.setHeaderVisible(true);
	    sampleTable.setLinesVisible(true);
	    sampleTable.addControlListener(new TableGrowListener(sampleTable));
	    
	    TableColumn useCol = new TableColumn(sampleTable, SWT.LEFT);
	    TableColumn nameCol = new TableColumn(sampleTable, SWT.LEFT);
	    TableColumn typeCol = new TableColumn(sampleTable, SWT.LEFT);
	    useCol.setText(colNames.get(0));
	    nameCol.setText(colNames.get(1));
	    typeCol.setText(colNames.get(2));
	    
	    useCol.setWidth(40);
	    typeCol.setWidth(70);
	    
	    sampleTableViewer = new TableViewer(sampleTable);
	    
	    sampleTableViewer.setLabelProvider(new CsTableLabelProvider());
	    sampleTableViewer.setContentProvider(new CsTableContentProvider());
	    sampleTableViewer.setColumnProperties(colNames.toArray(new String[colNames.size()]));
	    CellEditor[] cellEditors = new CellEditor[3];
	    cellEditors[0] = new CheckboxCellEditor(sampleTable);
	    cellEditors[1] = new TextCellEditor(sampleTable);
	    cellEditors[2] = new ComboBoxCellEditor(sampleTable, GmmlColorSet.SAMPLE_TYPES);
	    sampleTableViewer.setCellEditors(cellEditors);
	    sampleTableViewer.setCellModifier(new CsTableCellModifier());

	    csTableGroup.pack();
	    csGroup.pack();
	}
	
	Text cgNameText;
	Combo cgCombo;
	Text cgColorText1;
	Text cgColorText2;
	Button cgColorButton1;
	Button cgColorButton2;
	CLabel cgCLabel1;
	CLabel cgCLabel2;
	Color cgColor1;
	Color cgColor2;
	ColorDialog cgColorDialog;
	Button cgButton;
	
	public void setCgGroupComponents()
	{			    
	    //TODO: add validator to colortext
	    
		GridData span4ColsGrid = new GridData(GridData.FILL_HORIZONTAL);
		span4ColsGrid.horizontalSpan = 4;
		GridData cgCLabelGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		cgCLabelGrid.widthHint = cgCLabelGrid.heightHint = 15;
		GridData colorTextGrid = new GridData(GridData.FILL_HORIZONTAL);
		GridData colorButtonGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		colorButtonGrid.widthHint = colorButtonGrid.heightHint = 15;
		GridData cgButtonGrid = new GridData(GridData.HORIZONTAL_ALIGN_END);
		cgButtonGrid.horizontalSpan = 5;
		
		cgColor1 = new Color(getShell().getDisplay(), 255, 0, 0);
	    cgColor2 = new Color(getShell().getDisplay(), 0, 255, 0);
	    cgColorDialog = new ColorDialog(getShell(), SWT.NONE);
	    
	    Group cgGroup = new Group(cgComposite, SWT.SHADOW_IN);
	    Button cgButton = new Button(cgComposite, SWT.PUSH);
	    
	    Label cgNameLabel = new Label(cgGroup, SWT.CENTER);
	    cgNameText = new Text(cgGroup, SWT.SINGLE | SWT.BORDER);
	    Label cgComboLabel = new Label(cgGroup, SWT.CENTER);
	    cgCombo = new Combo(cgGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
	    Label cgColorLabel1 = new Label(cgGroup, SWT.CENTER);
	    cgCLabel1 = new CLabel(cgGroup, SWT.SHADOW_IN);
	    cgColorButton1 = new Button(cgGroup, SWT.PUSH);
	    cgColorButton1.addSelectionListener(new ColorButtonAdapter());
	    Label cgValueLabel1 = new Label(cgGroup, SWT.CENTER);
	    cgColorText1 = new Text(cgGroup, SWT.SINGLE | SWT.BORDER);
	    Label cgColorLabel2 = new Label(cgGroup, SWT.CENTER);
	    cgCLabel2 = new CLabel(cgGroup, SWT.SHADOW_IN);
	    cgColorButton2 = new Button(cgGroup, SWT.PUSH);
	    cgColorButton2.addSelectionListener(new ColorButtonAdapter());
	    Label cgValueLabel2 = new Label(cgGroup, SWT.CENTER);
	    cgColorText2 = new Text(cgGroup, SWT.SINGLE | SWT.BORDER);
	    
	    cgColorText1.setLayoutData(colorTextGrid);
	    cgColorText2.setLayoutData(colorTextGrid);
	    cgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cgGroup.setLayout(new GridLayout(5, false));
	    cgGroup.setText("Color by gradient settings");
	    
	    cgNameLabel.setText("Name:");
	    cgNameText.setLayoutData(span4ColsGrid);
	    
	    cgComboLabel.setText("Data column:");
	    cgCombo.setLayoutData(span4ColsGrid);
	    
	    cgColorLabel1.setText("Start color:");
	    cgColorLabel2.setText("End color:");
	    cgCLabel1.setBackground(cgColor1);
	    cgCLabel2.setBackground(cgColor2);
	    cgCLabel1.setLayoutData(cgCLabelGrid);
	    cgCLabel2.setLayoutData(cgCLabelGrid);
	    cgColorButton1.setText("...");
	    cgColorButton2.setText("...");
	    cgColorButton1.setLayoutData(colorButtonGrid);
	    cgColorButton2.setLayoutData(colorButtonGrid);
	
	    cgValueLabel1.setText("at value:");
	    cgValueLabel2.setText("at value:");
	    
	    cgButton.setLayoutData(cgButtonGrid);
	    cgButton.setText("Save");
	    cgButton.addSelectionListener(new CgButtonAdapter());
	    
	    cgGroup.pack();
	}

	public void setRightCompositeContents(Object element) {	
		if(element == null) {
			sashForm.setMaximizedControl(cnComposite);
			legend.setVisible(false);
			return;
		}
		if(element instanceof GmmlColorSet) {
			GmmlColorSet cs = (GmmlColorSet)element;
			csNameText.setText(cs.name);
			csColorGnf = new Color(getShell().getDisplay(), cs.color_gene_not_found);
			csColorNc = new Color(getShell().getDisplay(), cs.color_no_criteria_met);
			csCLabelGnf.setBackground(csColorGnf);
			csCLabelNc.setBackground(csColorNc);
			sampleTableViewer.setInput(cs);
			legend.colorSetIndex = gmmlGex.colorSets.indexOf(cs);
			legend.setVisible(true);
			legend.redraw();
			sashForm.setMaximizedControl(csComposite);
			topSash.layout();
			return;
		}
		if(element instanceof GmmlColorGradient) {
			GmmlColorGradient cg = (GmmlColorGradient)element;
			cgNameText.setText(cg.name);
			setCgComboItems(cg.parent);
			cgCombo.select(cgColumnIndex.indexOf(cg.getDataColumn()));
			cgColorText1.setText(Double.toString(cg.valueStart));
			cgColorText2.setText(Double.toString(cg.valueEnd));
			cgColor1 = new Color(getShell().getDisplay(), cg.colorStart);
			cgColor2 = new Color(getShell().getDisplay(), cg.colorEnd);
			cgCLabel1.setBackground(cgColor1);
			cgCLabel2.setBackground(cgColor2);
			sashForm.setMaximizedControl(cgComposite);
			legend.colorSetIndex = gmmlGex.colorSets.indexOf(cg.parent);
			legend.setVisible(true);
			legend.redraw();
			topSash.layout();
			return;
		}
	}

	private void setCgComboItems(GmmlColorSet cs)
	{
		cgColumnNames = new String[cs.useSamples.size() + 1];
		cgColumnIndex = new ArrayList<Integer>();
		cgColumnNames[0] = "All samples";
		cgColumnIndex.add(-1);
		
		for(int i = 0; i < cs.useSamples.size(); i++)
		{
			Sample s = cs.useSamples.get(i);
			if(s.dataType == Types.REAL)
			{
				cgColumnNames[i + 1] = s.name;
				cgColumnIndex.add(s.idSample);
			}
		}
		cgCombo.setItems(cgColumnNames);
	}
	
	class TableGrowListener extends ControlAdapter {
		Table table;
		public TableGrowListener(Table table)
		{
			super();
			this.table = table;
		}
		public void controlResized(ControlEvent e) {
			TableColumn[] cols = table.getColumns();
			Rectangle area = null;
			if(table == sampleTable)
			{
				area = csTableGroup.getClientArea();
			} else {
				area = coTableComposite.getClientArea();
			}
			Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int width = area.width - 2*table.getBorderWidth();
			if (preferredSize.y > area.height + table.getHeaderHeight()) {
				Point vBarSize = table.getVerticalBar().getSize();
				width -= vBarSize.x;
			}
			Point oldSize = table.getSize();
			if (oldSize.x > area.width) {
				if(table == sampleTable)
				{
					cols[1].setWidth(width - cols[0].getWidth() - cols[2].getWidth());
				} else {
					cols[0].setWidth(width);
				}
				table.setSize(area.width, area.height);
			} else {
				table.setSize(area.width, area.height);
				if(table == sampleTable)
				{
					cols[1].setWidth(width - cols[0].getWidth() - cols[2].getWidth());
				} else {
					cols[0].setWidth(width);
				}
			}
		}
	}
	
	class CsComboSelectionAdapter extends SelectionAdapter {
		public CsComboSelectionAdapter() {
			super();
		}
		public void widgetSelected(SelectionEvent e) {
			GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
			coTableViewer.setInput(cs);
			setRightCompositeContents(cs);
		}
	}
	
	class CgButtonAdapter extends SelectionAdapter {
		public CgButtonAdapter() {
			super();
		}
		public void widgetSelected(SelectionEvent e) {
			if(cgNameText.getText().equals("")) {
				MessageDialog.openError(getShell(), "Error", "Specify a name");
				return;
			}
			if(cgCombo.getText().equals("")) {
				MessageDialog.openError(getShell(), "Error", "Choose a data column");
				return;
			}
			GmmlColorGradient cg = (GmmlColorGradient)
    		((IStructuredSelection)coTableViewer.getSelection()).getFirstElement();
			cg.name = cgNameText.getText();
			cg.setDataColumn(cgColumnIndex.get(cgCombo.getSelectionIndex()));
			cg.colorStart = cgColor1.getRGB();
			cg.colorEnd = cgColor2.getRGB();
			cg.valueStart = Double.parseDouble(cgColorText1.getText());
			cg.valueEnd = Double.parseDouble(cgColorText2.getText());
			legend.setVisible(true);
			legend.redraw();
			coTableViewer.refresh();
		}
	}
	
	class ColorButtonAdapter extends SelectionAdapter {
		public ColorButtonAdapter() {
			super();
		}
    	public void widgetSelected(SelectionEvent e) {
    		RGB rgb = cgColorDialog.open();
    		if (rgb != null) {
    			if(e.widget == cgColorButton1) {
    				cgColor1 = new Color(getShell().getDisplay(), rgb);
    				cgCLabel1.setBackground(cgColor1);
    			}
    			if(e.widget == cgColorButton2) {
    				cgColor2 = new Color(getShell().getDisplay(), rgb);
    				cgCLabel2.setBackground(cgColor2);
    			}
    			if(e.widget == csColorButtonGnf) {
    				csColorGnf = new Color(getShell().getDisplay(), rgb);
    				csCLabelGnf.setBackground(csColorGnf);
    			}
    			if(e.widget == csColorButtonNc) {
    				csColorNc = new Color(getShell().getDisplay(), rgb);
    				csCLabelNc.setBackground(csColorNc);
    			}
    		}
    	}
    }
	
	class csButtonAdapter extends SelectionAdapter {
		public csButtonAdapter() {
			super();
		}
		public void widgetSelected(SelectionEvent e) {
			if(csNameText.getText().equals("")) {
				MessageDialog.openError(getShell(), "Error", "Specify a name");
				return;
			}
			GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
			cs.name = csNameText.getText();
			cs.color_gene_not_found = csColorGnf.getRGB();
			cs.color_no_criteria_met = csColorNc.getRGB();
			legend.setVisible(true);
			legend.redraw();
		}
	}
	
	class NewCsButtonAdapter extends SelectionAdapter {
		
		public NewCsButtonAdapter() {
			super();
		}
		
		public void widgetSelected(SelectionEvent e) {
			InputDialog d = new InputDialog(Display.getCurrent().getActiveShell(),
					  "New Color Set", "Name of new Color Set:", "", null);
			int rc = d.open();
			if(rc == Window.OK) {
				GmmlColorSet cs = new GmmlColorSet(d.getValue(), gmmlGex);
				gmmlGex.colorSets.add(cs);
				csCombo.setItems(gmmlGex.getColorSetNames());
				csCombo.select(gmmlGex.colorSets.indexOf(cs));
				coTableViewer.setInput(cs);
				setRightCompositeContents(cs);
			}
		}
	}
    
	class EditCsButtonAdapter extends SelectionAdapter {
		
		public EditCsButtonAdapter() {
			super();
		}
		
		public void widgetSelected(SelectionEvent e) {
			if(csCombo.getSelectionIndex() > -1) {
				GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
				setRightCompositeContents(cs);
			}
		}
	}
	
	class NewCoButtonAdapter extends SelectionAdapter {
		NewCoDialog dialog;
		
		public NewCoButtonAdapter() {
			super();
		}
		
		public void widgetSelected(SelectionEvent e) {
			dialog = new NewCoDialog(Display.getCurrent().getActiveShell());
			dialog.open();
		}
	}
	
	class DeleteCsButtonAdapter extends SelectionAdapter {
		public DeleteCsButtonAdapter() {
			super();
		}
		
		public void widgetSelected(SelectionEvent e) {
			if(csCombo.getSelectionIndex() > -1)
			{
				gmmlGex.colorSets.remove(csCombo.getSelectionIndex());
				csCombo.setItems(gmmlGex.getColorSetNames());
				csCombo.select(0);
				if(csCombo.getSelectionIndex() > -1)
				{
					GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
					coTableViewer.setInput(cs);
					setRightCompositeContents(cs);
				}
			}
		}
	}
	
	class DeleteCoButtonAdapter extends SelectionAdapter {
		public DeleteCoButtonAdapter() {
			super();
		}
		
		public void widgetSelected(SelectionEvent e) {
			GmmlColorSetObject co = (GmmlColorSetObject)
			((IStructuredSelection)coTableViewer.getSelection()).getFirstElement();
			co.parent.colorSetObjects.remove(co);
			coTableViewer.refresh();
		}
	}
	
	static final String TRANSFER_CSOBJECT = "CSOBJECT";
	static final String TRANSFER_SEP = ":";
	
    private class CoTableDragAdapter extends DragSourceAdapter {
    	public void dragStart(DragSourceEvent e) {
    		Object selected = ((IStructuredSelection)coTableViewer.getSelection()).getFirstElement();
    		if(selected == null)
    		{
    			e.doit = false;
    		}
    	}
    	
    	public void dragSetData(DragSourceEvent e) {
    		Object selected = ((IStructuredSelection)coTableViewer.getSelection()).getFirstElement();
    		e.data = "NONE";
    		if(selected instanceof GmmlColorSetObject)
    		{
    			GmmlColorSetObject cso = (GmmlColorSetObject)selected;
    			int csIndex = gmmlGex.colorSets.indexOf(cso.parent);
    			int csoIndex = cso.parent.colorSetObjects.indexOf(cso);
    			e.data = TRANSFER_CSOBJECT + TRANSFER_SEP + csIndex + TRANSFER_SEP + csoIndex;
    		}
    	}
    }
    
    private class CoTableDropAdapter extends DropTargetAdapter {
    	public void drop(DropTargetEvent e) {
    		TableItem item = (TableItem)e.item;
    		if(item != null)
    		{
    			Object selected = item.getData();
    			String[] data = ((String)e.data).split(":");
    			if(data[0].equals(TRANSFER_CSOBJECT))
    			{
    				int csIndex = Integer.parseInt(data[1]);
    				int csoIndex = Integer.parseInt(data[2]);
    				GmmlColorSet cs = (GmmlColorSet)gmmlGex.colorSets.get(csIndex);
    				GmmlColorSetObject cso = (GmmlColorSetObject)cs.colorSetObjects.get(csoIndex);
    				if(((GmmlColorSetObject)selected).parent == cs)
    				{
    					moveElement(cs.colorSetObjects, cso, cs.colorSetObjects.indexOf(selected));
    				}
    				else
    				{
    					GmmlColorSet csNew = ((GmmlColorSetObject)selected).parent;
    					csNew.colorSetObjects.add(csNew.colorSetObjects.indexOf(selected), cso);
    					cs.colorSetObjects.remove(cso);
    					cso.parent = csNew;
    				}
    			}
    		}
    		coTableViewer.refresh();
    	}
    }
    
    public void moveElement(Vector v, Object o, int newIndex)
    {
    	v.remove(o);
    	v.add(newIndex, o);
    }
    
//	private class TreeMouseListener extends MouseAdapter {
//		public TreeMouseListener() {
//			super();
//		}
//		public void mouseDown(MouseEvent e) {
//			if(e.button == 3)
//			{
//				org.eclipse.swt.widgets.List list = listViewer.getList();
//				ListItem item = list.getItem(new Point(e.x, e.y));
//				if(item != null)
//				{
//					MenuManager mgr = new MenuManager();
//					mgr.add(new DeleteAction(item.getData()));
//					Menu m = mgr.createContextMenu(list);
//					list.setMenu(m);
//				}
//			}
//		}
//	}
	
	
	private class NewCoDialog extends Dialog
	{		
		public NewCoDialog(Shell parent)
		{
			super(parent);
		}
		
		public void open()
		{
			Shell parent = getParent();
			final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
			
			shell.setLayout(new GridLayout(2, false));
			
			Group csGroup = new Group(shell, SWT.SHADOW_IN);
			csGroup.setText("New color criterion");
			csGroup.setLayout(new GridLayout(2, false));
			GridData csGroupGrid = new GridData(GridData.FILL_BOTH);
			csGroupGrid.horizontalSpan = 2;
			csGroup.setLayoutData(csGroupGrid);
			
			Label csTextLabel = new Label(csGroup, SWT.CENTER);
		    final Text csText = new Text(csGroup, SWT.SINGLE | SWT.BORDER);
		    Label csComboLabel = new Label(csGroup, SWT.CENTER);
		    final Combo coTypeCombo = new Combo(csGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		    
		    csTextLabel.setText("Name:");
		    csText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		    
			final String[] comboText = new String[] { "Color by gradient", "Color by expression" };
		    csComboLabel.setText("Type:");
		    coTypeCombo.setItems(comboText);
		    coTypeCombo.setText(comboText[0]);
		    
		    final Button buttonOk = new Button(shell, SWT.PUSH);
		    buttonOk.setText("Ok");
		    buttonOk.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
		    final Button buttonCancel = new Button(shell, SWT.PUSH);
		    buttonCancel.setText("Cancel");
		    buttonCancel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		    
			buttonOk.addSelectionListener(new SelectionAdapter() {
		    	public void widgetSelected(SelectionEvent e) {
		    		GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
		    		if(csText.getText().equals("")) {
		    			MessageDialog.openError(getShell(), "Error", "Specify a name for the Color Set");
		    			return;
		    		}
		    		if(comboText[0].equals(coTypeCombo.getText())) {
		    			GmmlColorGradient cg = new GmmlColorGradient(cs, csText.getText());
		    			cs.addObject(cg);
		    			coTableViewer.refresh();
		    			coTableViewer.setSelection(new StructuredSelection(cg));
		   
		    			shell.dispose();
		    		}
		    	}
		    });
			
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					shell.dispose();
				}
			});
			
			shell.setDefaultButton(buttonOk);
		    shell.pack();
		    shell.setLocation(parent.getLocation().x + parent.getSize().x / 2 - shell.getSize().x / 2,
		    				  parent.getLocation().y + parent.getSize().y / 2 - shell.getSize().y / 2);
		    shell.open();
		    
		    Display display = parent.getDisplay();
		    while (!shell.isDisposed()){
		    	if(!display.readAndDispatch())
		    		display.sleep();
		    }
		}
	}
	
	private class CoTableSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent e)
		{
			if(e.getSelection().isEmpty()) {
				setRightCompositeContents(null);
			} else {
				Object s = ((IStructuredSelection)e.getSelection()).getFirstElement();
				setRightCompositeContents(s);
			}
		}
	}
	
	private class CoTableContentProvider implements IStructuredContentProvider {
		
		public void dispose() {	}
		
		public Object[] getElements(Object inputElement) {
				return ((GmmlColorSet)inputElement).colorSetObjects.toArray();
		}
				
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//TODO: input changed
		}
		
	}
	
	private class CoTableLabelProvider implements ITableLabelProvider {
		private java.util.List listeners;
		private Image criterionImage;
		private Image gradientImage;
				
		public CoTableLabelProvider() {
			listeners = new ArrayList();
			try {
				criterionImage = new Image(null, new FileInputStream("icons/colorset.gif"));
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		
		public void addListener(ILabelProviderListener listener) {
			listeners.add(listener);
		}
		
		public void dispose() {
			if(criterionImage != null)
				criterionImage.dispose();
			if(gradientImage != null)
				gradientImage.dispose();
		}
		
		public Image getColumnImage(Object element, int columnIndex) { 
//			if(element instanceof GmmlColorCriterion) {
//				return criterionImage;
//			}
			if(element instanceof GmmlColorGradient) {
				gradientImage = new Image(null, createGradientImage((GmmlColorGradient)element));
				return gradientImage;
			}
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof GmmlColorSetObject)
				return ((GmmlColorSetObject)element).name;
			return "";
		}
		
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		
		public void removeListener(ILabelProviderListener listener) {
			listeners.remove(listener);
		}
	}
	
	private ImageData createGradientImage(GmmlColorGradient cg)
	{
		PaletteData colors = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
		ImageData data = new ImageData(16, 16, 24, colors);
		for(int i = 0; i < 16; i++)
		{
			RGB rgb = cg.getColor(cg.valueStart + (i * (cg.valueEnd - cg.valueStart)) / 16 );
			if(rgb == null)
				rgb = new RGB(255,255,255);
			for(int j = 0; j < 16; j++)
			{
				data.setPixel(j, i, colors.getPixel(rgb));
			}
		}
		return data;
	}
	
	private class CsTableContentProvider implements IStructuredContentProvider
	{		
		public CsTableContentProvider()
		{
			super();
		}
		
		public Object[] getElements(Object inputElement)
		{
			GmmlColorSet cs = (GmmlColorSet)inputElement;
			ArrayList<Sample> samples = new ArrayList<Sample>();
			samples.addAll(cs.useSamples);
			
			Vector<Sample> allSamples = new Vector<Sample>(gmmlGex.samples.values());
			Collections.sort(allSamples);
			for(Sample s : allSamples)
			{
				if(!samples.contains(s))
				{
					samples.add(s);
				}
			}
			return samples.toArray();
		}
		
		public void dispose() { }
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
	}
	
	private class CsTableLabelProvider implements ITableLabelProvider
	{
		Image checkImage;
		Image uncheckImage;
		
		public CsTableLabelProvider() {
			super();
			checkImage = new Image(null, "icons/sample_checked.gif");
			uncheckImage = new Image(null, "icons/sample_unchecked.gif");
		}
		public Image getColumnImage(Object element, int columnIndex) { 
			Sample s = (Sample)element;
			if(csCombo.getSelectionIndex() > -1)
			{
				GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
				switch(columnIndex) {
				case 0:
					if(cs.useSamples.contains(s))
					{
						return checkImage;
					} else {
						return uncheckImage;
					}
				}
			}
			return null;
		}
		public String getColumnText(Object element, int columnIndex)
		{
			Sample s = (Sample)element;
			if(csCombo.getSelectionIndex() > -1)
			{
				GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
				switch(columnIndex) {
				case 1: //Name
					return s.name;
				case 0: //Use
//					if(cs.useSamples.contains(s))
//					{
//						return "X";
//					} else {
//						return "";
//					}
					return "";
				case 2: //Type
					if(cs.useSamples.contains(s))
					{
						return GmmlColorSet.SAMPLE_TYPES[cs.sampleTypes.get(cs.useSamples.indexOf(s))];
					} else {
						return GmmlColorSet.SAMPLE_TYPES[0];
					}
				}
			}
			return "";
		}
		
		public void addListener(ILabelProviderListener listener) { }
		public void dispose() { 
			checkImage.dispose();
			uncheckImage.dispose();
		}
		public boolean isLabelProperty(Object element, String property) { return false; }
		public void removeListener(ILabelProviderListener listener) { }
		
		
	}
	
	private class CsTableCellModifier implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			if(!colNames.get(1).equals(property))
			{
				return true;
			}
			return false;
		}
		
		public Object getValue(Object element, String property)
		{
			Sample s = (Sample)element;
			GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());	
			switch(colNames.indexOf(property)) {
			case 1:
				return s.name;
			case 0:
				return cs.useSamples.contains(s);
			case 2:
				if(cs.useSamples.contains(s))
				{
					return cs.sampleTypes.get(cs.useSamples.indexOf(s));
				} else {
					return 0;
				}
			}
			return null;
		}
		
		public void modify(Object element, String property, Object value)
		{
			Sample s = null;
			if(element instanceof Item) {
				TableItem t = (TableItem)element;
				s = (Sample)t.getData();
			} else {
				s = (Sample)element;
			}

			GmmlColorSet cs = gmmlGex.colorSets.get(csCombo.getSelectionIndex());
			
			switch(colNames.indexOf(property)) {
			case 0:
				if((Boolean)value)
				{
					if(!cs.useSamples.contains(s.idSample))
						cs.useSamples.add(s);
						cs.sampleTypes.add(0);
				} else {
					if(cs.useSamples.contains(s))
					{
						cs.useSamples.remove(s);
					}
				}
				sampleTableViewer.refresh();
				break;
			case 2:
				if(cs.useSamples.contains(s))
				{
					cs.sampleTypes.set(cs.useSamples.indexOf(s), (Integer)value);
					sampleTableViewer.refresh();
				}
			}
		}
	}
}
