/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.tzi.use.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.tzi.use.gui.util.ExtendedJTable;
import org.tzi.use.main.Session;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.events.tags.SystemStateChangedEvent;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

/** 
 * A view for showing and changing object properties (attributes).
 *  
 * @author  Mark Richters
 */
@SuppressWarnings("serial")
public class WizardMVMView extends JPanel implements View {
	private static final String NO_OBJECTS_AVAILABLE = "(No objects available.)";

	private MainWindow fMainWindow;
	private Session fSession;
	private MSystem fSystem;
	private MObject fObject;

	private JComboBox<String> fObjectComboBox;
	private JTable fTable;
	private JScrollPane fTablePane;
	private JPanel fListPanel;
	private JPanel fInputsPanel;	
	private JPanel pObjectPanel;
	private JPanel pAttrPanel;
	
	private JButton fBtnApply;
	private JButton fBtnReset;
	private JButton fBtnJuanto;
	private TableModel fTableModel;
	private ObjectComboBoxActionListener fObjectComboBoxActionListener;

	private List<MAttribute> fAttributes;
	private String[] fValues;
	private Map<MAttribute, Value> fAttributeValueMap;

	//---
	private JList<String> fListClass;
	private JList<String> fListObjects;
	private JList<String> fListAttrs;
	private JLabel lNomObj;
	private JLabel lValAttr;
	private JTextField fNomObj;
	private JTextField fValAttr;
	//---



	/**
	 * The table model.
	 */
	class TableModel extends AbstractTableModel implements SortChangeListener {
		final String[] columnNames = { "Attribute", "Value" };

		TableModel() {
			ModelBrowserSorting.getInstance().addSortChangeListener( this );
			update();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getColumnCount() { 
			return 2; 
		}
		public int getRowCount() { 
			return fAttributes.size();
		}
		public Object getValueAt(int row, int col) { 
			if (col == 0 )
				return fAttributes.get(row);
			else
				return fValues[row];
		}
		public boolean isCellEditable(int row, int col) {
			return col == 1 && !fAttributes.get(row).isDerived();
		}

		public void setValueAt(Object value, int row, int col) {
			fValues[row] = value.toString();
			fireTableCellUpdated(row, col);
		}

		private void update() {
			// initialize table model
			if ( haveObject() ) {
				MObjectState objState = fObject.state(fSystem.state());
				fAttributeValueMap = objState.attributeValueMap();

				Collection<MAttribute> attributes = ModelBrowserSorting.getInstance().sortAttributes( fAttributeValueMap.keySet() );

				attributes = Collections2.filter(attributes, new Predicate<MAttribute>() {
					@Override
					public boolean apply(MAttribute input) {
						return !input.isDerived();
					}
				});

				fAttributes = Lists.newArrayList(attributes);
				fValues = new String[fAttributes.size()];
				for (int i = 0; i < fValues.length; i++) {
					fValues[i] = fAttributeValueMap.get(fAttributes.get(i)).toString();
				}
			} else {
				fAttributes = Collections.emptyList();
				fValues = new String[0];
			}
			fireTableDataChanged();
		}

		/**
		 * After the occurrence of an event the attribute list is updated.
		 */
		public void stateChanged( SortChangeEvent e ) {
			fAttributes = ModelBrowserSorting.getInstance()
					.sortAttributes( fAttributes );
			update();
		}
	}

	class ObjectComboBoxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String objName = (String)fObjectComboBox.getSelectedItem();

			if (!NO_OBJECTS_AVAILABLE.equals(objName))
				selectObject(objName);
		}
	}

	//    public WizardMVMView(MainWindow parent, MSystem system) {
	public WizardMVMView(MainWindow parent, Session session) {
		super(new BorderLayout());
		fMainWindow = parent;
		fSession = session;
		fSystem = session.system();
		fSystem.registerRequiresAllDerivedValues();
		fSystem.getEventBus().register(this);

		// create combo box with available objects
		fObjectComboBox = new JComboBox<String>();
		fObjectComboBoxActionListener = new ObjectComboBoxActionListener();

		// create table of attribute/value pairs
		fTableModel = new TableModel();
		fTable = new ExtendedJTable(fTableModel);
		fTable.setPreferredScrollableViewportSize(new Dimension(250, 70));
		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTablePane = new JScrollPane(fTable);

		// create buttons
		fBtnApply = new JButton("Apply");
		fBtnApply.setMnemonic('A');
		fBtnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChanges();
			}
		});
		fBtnReset = new JButton("Reset");
		fBtnReset.setMnemonic('R');
		fBtnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});

		fBtnJuanto = new JButton("Create Object");
		fBtnJuanto.setPreferredSize(new Dimension(50, 20));
		fBtnJuanto.setMnemonic('J');
		fBtnJuanto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Crear objeto
				String nomClass = fListClass.getSelectedValue();
				String nomObj = fNomObj.getText();
				createObject(nomClass,  nomObj);
				fListObjects.setModel(loadListObjects(nomClass));
			}
		});

		//---

		fListClass = new JList<String>(loadListClass());
		fListClass.setPreferredSize(new Dimension(100, 200));
		fListClass.setFixedCellWidth(100);
		fListClass.setFixedCellHeight(20);
		fListClass.setBorder(BorderFactory.createLineBorder(Color.black));
		
		fListClass.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				String nomClass = fListClass.getSelectedValue();
				fListObjects.setModel(loadListObjects(nomClass));
				fListObjects.setSelectedIndex(0);
				String nomObj = (String) fListObjects.getSelectedValue();
				fListAttrs.setModel(loadListAttrs(nomClass,nomObj));
				fListAttrs.setSelectedIndex(0);
			}
		});
		
		fListClass.setSelectedIndex(0);
		String nomClass = fListClass.getSelectedValue();

		fListObjects = new JList<String>(loadListObjects(nomClass));	
		fListObjects.setPreferredSize(new Dimension(100, 140));
		fListObjects.setFixedCellWidth(100);
		fListObjects.setFixedCellHeight(20);
		fListObjects.setBorder(BorderFactory.createLineBorder(Color.black));
		fListObjects.setSelectedIndex(0);
		String nomObj = (String) fListObjects.getSelectedValue();
		
		fListObjects.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				String nomClass = fListClass.getSelectedValue();
				String nomObj = (String) fListObjects.getSelectedValue();
//				fListAttrs = new JList<String>(loadListAttrs(nomClass,nomObj));
				fListAttrs.setModel(loadListAttrs(nomClass,nomObj));
			}
		});
		
		pObjectPanel = new JPanel();
		pObjectPanel.setLayout(new BoxLayout(pObjectPanel, BoxLayout.Y_AXIS));
		pObjectPanel.add(fListObjects);

		fListAttrs = new JList<String>(loadListAttrs(nomClass,nomObj));
		fListAttrs.setPreferredSize(new Dimension(100, 160));
		fListAttrs.setFixedCellWidth(100);
		fListAttrs.setFixedCellHeight(20);		
		fListAttrs.setBorder(BorderFactory.createLineBorder(Color.black));
		
		pAttrPanel = new JPanel();
		pAttrPanel.setLayout(new BoxLayout(pAttrPanel, BoxLayout.Y_AXIS));
		pAttrPanel.add(fListAttrs);
		
		//---

		// layout the buttons centered from left to right
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(fBtnApply);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(fBtnReset);
		buttonPane.add(Box.createHorizontalGlue());
//		buttonPane.add(fBtnJuanto);
//		buttonPane.add(Box.createHorizontalGlue());      

		fListPanel = new JPanel();
		
		fListPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		fListPanel.add(fListClass);
		fListPanel.add(pObjectPanel);
		fListPanel.add(pAttrPanel);
		
		fInputsPanel = new JPanel();
		lNomObj = new JLabel("Nom. Object");
		lNomObj.setPreferredSize(new Dimension(50, 20));
		lValAttr = new JLabel("Val. attribute");
//		lValAttr.setAlignmentX(Component.LEFT_ALIGNMENT);
//		lValAttr.setHorizontalAlignment(JLabel.LEFT);
//		lValAttr.setHorizontalAlignment(JLabel.LEFT);
		lValAttr.setPreferredSize(new Dimension(50, 20));
		fNomObj= new JTextField();
		fNomObj.setPreferredSize(new Dimension(50, 20));
		fValAttr= new JTextField();
		fValAttr.setPreferredSize(new Dimension(50, 20));
		
		pObjectPanel.add(lNomObj);
		pObjectPanel.add(fNomObj);
		pObjectPanel.add(fBtnJuanto);
		
		pAttrPanel.add(lValAttr);
		pAttrPanel.add(fValAttr);		

		// layout panel
		add(fObjectComboBox, BorderLayout.NORTH);
		add(fListPanel, BorderLayout.CENTER);
//		add(buttonPane, BorderLayout.SOUTH);
		add(fInputsPanel, BorderLayout.SOUTH);

		setSize(new Dimension(300, 300));

		updateGUIState();
	}

	// Aqui1
	private DefaultListModel<String> loadListClass() {
		DefaultListModel<String> ldefLModel = new DefaultListModel<String>();
		for (MClass oClass : fSystem.model().classes()) {
			ldefLModel.addElement(oClass.name());
		}
		return ldefLModel;
	}
	private DefaultListModel<String> loadListObjects(String nomClass) {
		DefaultListModel<String> ldefLModel = new DefaultListModel<String>();
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();
		ArrayList<String> livingObjects = new ArrayList<String>();

		for (MObject obj : allObjects) {
			if (obj.cls().name().equals(nomClass)) {
				ldefLModel.addElement(obj.name());
			}
		}
		return ldefLModel;
	}
	private DefaultListModel<String> loadListAttrs(String nomClass, String nomObj) {
		MSystemState state = fSystem.state();
		fObject = state.objectByName(nomObj);
		DefaultListModel<String> ldefLModel = new DefaultListModel<String>();
		MObjectState objState = fObject.state(fSystem.state());
		fAttributeValueMap = objState.attributeValueMap();

		Collection<MAttribute> attributes = ModelBrowserSorting.getInstance().sortAttributes( fAttributeValueMap.keySet() );
		fAttributes = Lists.newArrayList(attributes);
		for (MAttribute attr: fAttributes) {
			ldefLModel.addElement(attr.name());
		}
		
		return ldefLModel;
	}
	private void createObject(String nomClass, String nomObj) {
		Shell.createInstance(fSession, MainWindow.instance().getfPluginRuntime());
		Shell sh = Shell.getInstance();
		String line = "!create "+nomObj+" : "+nomClass;
		sh.processLineSafely(line);
	}

	/**
	 * Applies changes by setting new attribute values. Entries may be
	 * arbitrary OCL expressions. 
	 */
	private void applyChanges() {

		if (fTable.getCellEditor() != null) { 
			fTable.getCellEditor().stopCellEditing();
		}

		if (!haveObject()) { 
			return;
		}

		// Don't refresh after first change...
		fSystem.getEventBus().unregister(this);
		boolean error = false;

		for (int i = 0; i < fValues.length; ++i) {
			MAttribute attribute = fAttributes.get(i);
			String newValue = fValues[i];
			String oldValue = fAttributeValueMap.get(attribute).toString();

			if (!newValue.equals(oldValue)) {

				StringWriter errorOutput = new StringWriter();
				Expression valueAsExpression = 
						OCLCompiler.compileExpression(
								fSystem.model(),
								fSystem.state(),
								newValue, 
								"<input>", 
								new PrintWriter(errorOutput, true), 
								fSystem.varBindings());

				if (valueAsExpression == null) {
					JOptionPane.showMessageDialog(
							fMainWindow, 
							errorOutput, 
							"Error", 
							JOptionPane.ERROR_MESSAGE);
					error = true;
					continue;
				}

				try {
					fSystem.execute(
							new MAttributeAssignmentStatement(
									fObject, 
									attribute, 
									valueAsExpression));

				} catch (MSystemException e) {
					JOptionPane.showMessageDialog(
							fMainWindow, 
							e.getMessage(), 
							"Error", 
							JOptionPane.ERROR_MESSAGE);
					error = true;
				}
			}
		}

		if (!error) update();

		fSystem.getEventBus().register(this);
	}

	private boolean haveObject() {
		return fObject != null && fObject.exists(fSystem.state());
	}

	/**
	 * Initializes and updates the list of available objects.
	 */
	private void updateGUIState() {
		// temporarily turn off action listener, since setting the
		// model triggers a select action which cannot be
		// distinguished from a user initiated selection
		fObjectComboBox.removeActionListener(fObjectComboBoxActionListener);

		// build list of names of currently existing objects
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();
		ArrayList<String> livingObjects = new ArrayList<String>();

		for (MObject obj : allObjects) {
			if (obj.exists(state) )
				livingObjects.add(obj.name());
		}

		if (livingObjects.isEmpty() ) {
			livingObjects.add(NO_OBJECTS_AVAILABLE);
			fObjectComboBox.setEnabled(false);
			fObject = null;
		} else
			fObjectComboBox.setEnabled(true);

		String[] objNames = livingObjects.toArray(new String[]{});
		Arrays.sort(objNames);

		// create combo box with available objects
		fObjectComboBox.setModel(new DefaultComboBoxModel<String>(objNames));

		// try to keep selection
		if (haveObject() )
			fObjectComboBox.setSelectedItem(fObject.name());
		fObjectComboBox.addActionListener(fObjectComboBoxActionListener);


		//---
		//		DefaultListModel<String> ldefLModel = new DefaultListModel<String>();
		//		for (MClass oClass : fSystem.model().classes()) {
		//			ldefLModel.addElement(oClass.name());
		//		}
		//		fListClass = new JList<String>(ldefLModel);	

	}



	/**
	 * An object has been selected from the list. Update the table of
	 * properties.
	 */
	public void selectObject(String objName) {
		MSystemState state = fSystem.state();
		fObject = state.objectByName(objName);
		fTableModel.update();
		if (!fObjectComboBox.getSelectedItem().equals(objName)) {
			fObjectComboBox.setSelectedItem(objName);
		}
	}


	private void update() {
		updateGUIState();
		fTableModel.update();
	}

	@Subscribe
	public void onStateChanged(SystemStateChangedEvent e) {
		update();
	}

	/**
	 * Detaches the view from its model.
	 */
	public void detachModel() {
		fSystem.getEventBus().unregister(this);
		fSystem.unregisterRequiresAllDerivedValues();
	}
}

