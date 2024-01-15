/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundat	ion; either version 2 of the
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.mvm.AssocWizard;
import org.tzi.use.gui.mvm.LinkWizard;
import org.tzi.use.gui.mvm.MVMAction;
import org.tzi.use.gui.mvm.MVMAttribute;
import org.tzi.use.gui.mvm.MVMLink;
import org.tzi.use.gui.mvm.MVMObject;
import org.tzi.use.gui.mvm.MVMWizardActions;
import org.tzi.use.gui.mvm.MVMWizardAssoc;
import org.tzi.use.gui.util.ExtendedJTable;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagram;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagramView;
import org.tzi.use.gui.views.diagrams.objectdiagram.QualifierInputView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.MultiplicityViolationException;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkEnd;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.events.tags.SystemStateChangedEvent;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MLinkDeletionStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;

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

	private static final String NAMEFRAMEMVMDIAGRAM = "MVM";
	private static final String NAMEFRAMEMVMWIZARD = "MVMWizard";
	private MainWindow fMainWindow;
	//	private WizardMVMView thisWizard;
	private PrintWriter fLogWriter;
	private Session fSession;
	private MSystem fSystem;
	private MObject fObject;

	private JFrame frame;
	private JPanel panel;

	private DefaultListModel<String> modelObjects;
	private DefaultTableModel modelTabAttrs;

	private JLabel lbClass;
	private JLabel lbObjects;
	private JLabel lbAttrs;
	private JLabel lbAssoc;
	private JLabel lbObj;
	private JLabel lbFrom;
	private JLabel lbTo;
	private JLabel lbFromClass;
	private JLabel lbToClass;
	private JLabel lbAclass;	
	private JLabel lbAobject;	
	private JLabel lbAmultiplicity;	
	private JLabel lbArole;	
	private JLabel lbClassInvariants;
	private JLabel lbResClassInvariants;
	private JLabel lbCheckStructure;

	private JScrollPane scrollPaneClass;
	private JScrollPane scrollPaneObj;

	private JList<MClass> lClass;
	private JList<String> lObjects;
	private JList<MAssociation> lAssocs;
	private JList<String> lAttrs;

	private JTextField txNewObject;
	private JTextField txMultiOri;
	private JTextField txMultiDes;
	private JTextField txOriAssocRole;
	private JTextField txDesAssocRole;
	private String nomClass;
	private MClass oClass;
	private String nomObj;	
	private JComboBox<MClass> cmbClassOri;
	private JComboBox<MClass> cmbClassDes;
	private JComboBox<MObject> cmbObjectOri;
	private JComboBox<MObject> cmbObjectDes;
	private JComboBox<String> cmbMultiOri;
	private JComboBox<String> cmbMultiDes;
	private JCheckBox chkAutoLayout;	

	private JButton btnCreateObject;
	private JButton btnNewObjectAuto;
	private JButton btnDeleteObject;
	private JButton btnSaveObject;
	private JButton btnCancelObject;
	private JButton btnInsertLinkAssoc;
	private JButton btnDeleteLink;	
	private JButton btnShowClassInvariants;	
	private JButton btnShowCheckStructure;
	private JButton btnRefreshComponents;

	private JButton btnActions;
	private JButton btnReset;

	private WizardMVMView thisWizard;
	private boolean bNewObj;
	private JTable fTable;
	private JScrollPane fTablePane;

	private TableModel fTableModel;

	private List<MAttribute> fAttributes;
	private String[] fValues;
	private Map<MAttribute, Value> fAttributeValueMap;
	private NewObjectDiagram odvAssoc;
	private String aMulti[] = new String[] { 
			"0", "1", "*" };
	private List<Future<EvalResult>> futures;
	private List<AssocWizard> lAssocsWizard;
	private Color colorSoftGray;

	private JInternalFrame[] allframes;
	// To MVMWizardActions
	static List<MVMAction> lActions = new ArrayList<MVMAction>();
	private String strLastFile="";

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

	public WizardMVMView(MainWindow parent, Session session, PrintWriter logWriter) {
		super(new BorderLayout());
		thisWizard=this;
		fMainWindow = parent;
		fSession = session;
		fSystem = session.system();
		fSystem.registerRequiresAllDerivedValues();
		fSystem.getEventBus().register(this);
		fLogWriter=logWriter;
		colorSoftGray=new Color(218,224,224);

		searchObjDiagramAssociated();

		bNewObj=false;

		frame = new JFrame("Prototipo MVM Wizard");
		frame.setSize(630, 660);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		panel = new JPanel();

		lAttrs = new JList<String>();
		lObjects = new JList<String>();

		frame.add(panel);

		panel.setLayout(null);

		modelObjects = new DefaultListModel<>();
		modelTabAttrs = new DefaultTableModel();

		lbClass = new JLabel("Class");
		lbClass.setBounds(10, 15, 60, 25);
		panel.add(lbClass);

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(107, 15, 60, 25);
		panel.add(lbObjects);	   

		lbAttrs = new JLabel("Attributes");
		lbAttrs.setBounds(205, 15, 60, 25);
		panel.add(lbAttrs);	



		lClass = new JList<MClass>(loadListMClass());
		lClass.setBounds(10, 40, 90, 145);
		lClass.setBorder(BorderFactory.createLineBorder(Color.black));
		lClass.setSelectedIndex(0);
		oClass = lClass.getSelectedValue();
		nomClass = oClass.name();

		lClass.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(lClass.getModel().getSize()>0) {
					int index = lClass.locationToIndex(evt.getPoint());
					if (index != -1) {
						oClass = lClass.getModel().getElementAt(index);
						//						System.out.println("Clic en: " + oClass);
						lClass.setSelectedIndex(index);
						nomClass = oClass.name();
						lObjects.setModel(loadListObjects(nomClass));
						lObjects.setSelectedIndex(0);
						nomObj = (String) lObjects.getSelectedValue();
						selectObject( nomObj);
						txNewObject.setText(nomObj);
						txNewObject.setEnabled(false);
					}
				}
			}
		});

		scrollPaneClass = new JScrollPane();
		scrollPaneClass.setViewportView(lClass);
		scrollPaneClass.setBounds(10, 40, 90, 145);

		modelObjects=loadListObjects(nomClass);

		lObjects = new JList<String>( modelObjects );
		lObjects.setBounds(107, 40, 90, 110);
		lObjects.setBorder(BorderFactory.createLineBorder(Color.black));
		lObjects.setLayoutOrientation(JList.VERTICAL);
		lObjects.setSelectedIndex(0);
		nomObj = (String) lObjects.getSelectedValue();

		lObjects.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				oClass = lClass.getSelectedValue();
				nomClass = oClass.name();
				nomObj = (String) lObjects.getSelectedValue();
				selectObject( nomObj);
				txNewObject.setText(nomObj);
				txNewObject.setEnabled(false);
			}
		});
		scrollPaneObj = new JScrollPane();
		scrollPaneObj.setViewportView(lObjects);
		scrollPaneObj.setBounds(107, 40, 90, 110);
		// masmas
		btnNewObjectAuto = new JButton("+");
		btnNewObjectAuto.setBounds(107, 160, 90, 25);

		btnNewObjectAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newObjectAuto();
			}
		});
		panel.add(btnNewObjectAuto);

		fTableModel = new TableModel();
		fTable = new ExtendedJTable(fTableModel);
		fTable.setPreferredScrollableViewportSize(new Dimension(250, 70));

		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTablePane = new JScrollPane(fTable);
		fTablePane.setBounds(205, 40, 185, 80);

		chkAutoLayout=new JCheckBox("Auto Layout");
		chkAutoLayout.setBounds(205, 160, 160, 25);
		chkAutoLayout.setSelected(true);
		chkAutoLayout.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (odvAssoc!=null) {
					if (e.getStateChange()==1) {
						odvAssoc.forceStartLayoutThread();
					}else {
						odvAssoc.forceStopLayoutThread();
					}
				}
			}
		});
		panel.add(chkAutoLayout);	

		selectObject(nomObj);

		lbObj = new JLabel("Object");
		lbObj.setBounds(400, 15, 160, 25);
		panel.add(lbObj);	

		txNewObject = new JTextField(20);
		txNewObject.setBounds(400, 40, 95, 25);
		txNewObject.setEnabled(false);
		txNewObject.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) { //watch for key strokes
				if(txNewObject.getText().length() == 0 )
					btnSaveObject.setEnabled(false);
				else
				{
					btnSaveObject.setEnabled(true);
				}
			}
		});
		panel.add(txNewObject);

		nomObj = (String) lObjects.getSelectedValue();
		txNewObject.setText(nomObj);

		btnCreateObject = new JButton("New Object");
		btnCreateObject.setBounds(400, 70, 95, 25);
		btnCreateObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initNewObject();
			}
		});
		panel.add(btnCreateObject);

		btnSaveObject = new JButton("Save Obj");
		btnSaveObject.setBounds(205, 122, 90, 25);

		btnSaveObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nomObj = txNewObject.getText();
				oClass = lClass.getSelectedValue();
				saveObject(oClass, nomObj);
				setResClassInvariants();
				setResCheckStructure();
				bNewObj=false;
				txNewObject.setEnabled(false);
				selectObject(nomObj);
				cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
				cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
				lObjects.setModel(loadListObjects(oClass.name()));
				lObjects.setSelectedIndex(selectObject(nomObj));
			}
		});

		panel.add(btnSaveObject);

		btnCancelObject = new JButton("Cancel Obj");
		btnCancelObject.setBounds(301, 122, 90, 25);

		btnCancelObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelObject(nomObj);
			}
		});
		panel.add(btnCancelObject);

		btnDeleteObject = new JButton("Delete Object");
		btnDeleteObject.setBounds(400, 122, 95, 25);
		btnDeleteObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int resp =JOptionPane.showConfirmDialog(null, "Are you sure to delete object ["+nomObj+"]?",
						"Delete objects", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp==0) {
					deleteObject(nomObj);
					cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
					cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
				}
			}
		});
		panel.add(btnDeleteObject);

		lbAssoc = new JLabel("Association");
		lbAssoc.setBounds(10, 190, 160, 25);
		panel.add(lbAssoc);	

		lbFrom = new JLabel("From");
		lbFrom.setBounds(205, 190, 160, 25);
		panel.add(lbFrom);

		lbTo = new JLabel("To");
		lbTo.setBounds(335, 190, 160, 25);
		panel.add(lbTo);

		lAssocs = new JList<MAssociation>(loadListAssoc());

		lAssocs.setBounds(10, 215, 90, 85);
		lAssocs.setBorder(BorderFactory.createLineBorder(Color.black));
		lAssocs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				MAssociation oAssoc = lAssocs.getSelectedValue();
				setComposAssoc(oAssoc);
			}
		});

		panel.add(lAssocs);

		btnRefreshComponents = new JButton("Refresh");
		btnRefreshComponents.setBounds(10, 310, 90, 25);
		btnRefreshComponents.setVerticalAlignment(SwingConstants.CENTER);
		btnRefreshComponents.setHorizontalAlignment(SwingConstants.CENTER);
		btnRefreshComponents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshComponents();
			}
		});
		panel.add(btnRefreshComponents);

		btnActions = new JButton("Actions");
		btnActions.setBounds(10, 340, 90, 25);
		btnActions.setVerticalAlignment(SwingConstants.CENTER);
		btnActions.setHorizontalAlignment(SwingConstants.CENTER);
		btnActions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<MVMObject> lObjs = getMVMObjects();
				List<MVMLink> lLinks = getMVMLinks();	
				String sModel=fSystem.model().name();
				String sSourceUSE=fSystem.model().filename();
				MVMWizardActions w = new MVMWizardActions(frame,lActions,lObjs, lLinks, sModel,sSourceUSE, strLastFile, fSession );

				w.setSize(1008, 547);
				w.setLocationRelativeTo(null);
				w.setVisible(true);	

				strLastFile=w.getLastFile();

				List<MVMAction> lActionsRes=w.getListActions();
				if (lActionsRes!=null) {
					//					System.out.println(lActionsRes.size());
					doActions(lActionsRes);
				}
			}
		});
		panel.add(btnActions);

		btnReset = new JButton("Reset");
		btnReset.setBounds(10, 400, 90, 25);
		btnReset.setVerticalAlignment(SwingConstants.CENTER);
		btnReset.setHorizontalAlignment(SwingConstants.CENTER);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetObjLinks();
			}
		});
		panel.add(btnReset);

		lbAclass = new JLabel("Class");
		lbAclass.setBounds(135, 215, 100, 25);
		panel.add(lbAclass);

		lbAobject = new JLabel("Object");
		lbAobject.setBounds(135, 245, 100, 25);
		panel.add(lbAobject);	

		lbAmultiplicity = new JLabel("Multiplicity");
		lbAmultiplicity.setBounds(135, 275, 100, 25);
		panel.add(lbAmultiplicity);

		lbArole = new JLabel("Role");
		lbArole.setBounds(135, 305, 100, 25);
		panel.add(lbArole);

		cmbClassOri = new JComboBox<MClass>();

		cmbClassOri.setModel(loadComboClass());
		cmbClassOri.setBounds(205, 215, 120, 25);
		cmbClassOri.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
			}
		});
		cmbClassOri.setEnabled(false);
		cmbClassOri.setVisible(false);
		panel.add(cmbClassOri);

		Border blackline = BorderFactory.createLineBorder(Color.black);

		lbFromClass = new JLabel("");
		lbFromClass.setBounds(205, 215, 120, 25);
		lbFromClass.setBorder(blackline);
		lbFromClass.setBackground(colorSoftGray);
		lbFromClass.setHorizontalAlignment(SwingConstants.CENTER);
		lbFromClass.setOpaque(true);

		panel.add(lbFromClass);

		cmbClassDes = new JComboBox<MClass>();
		cmbClassDes.setModel(loadComboClass());
		cmbClassDes.setBounds(335, 215, 120, 25);
		cmbClassDes.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
			}
		});
		cmbClassDes.setEnabled(false);
		cmbClassDes.setVisible(false);
		panel.add(cmbClassDes);

		lbToClass = new JLabel("");
		lbToClass.setBounds(335, 215, 120, 25);
		lbToClass.setBorder(blackline);
		lbToClass.setBackground(colorSoftGray);
		lbToClass.setHorizontalAlignment(SwingConstants.CENTER);
		lbToClass.setOpaque(true);
		panel.add(lbToClass);

		cmbObjectOri = new JComboBox<MObject>();
		cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
		cmbObjectOri.setBounds(205, 245, 120, 25);

		cmbObjectOri.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MObject oSel = (MObject) cmbObjectOri.getSelectedItem();
				//				System.out.println("Busca extremo para ["+oSel.name()+"]");
				MObject oRel = findAssocEnd(oSel);
				if (oRel!=null) {
					//					System.out.println("Le corresponde ["+oRel.name()+"]");
					cmbObjectDes.setSelectedItem(oRel);
				}else {
					System.out.println("["+oSel.name()+"] No tiene extremo");
				}
			}
		});
		panel.add(cmbObjectOri);

		cmbObjectDes = new JComboBox<MObject>();
		cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
		cmbObjectDes.setBounds(335, 245, 120, 25);

		cmbObjectDes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MObject oSel = (MObject) cmbObjectDes.getSelectedItem();
				//				System.out.println("Busca extremo para ["+oSel.name()+"]");
				MObject oRel = findAssocEnd(oSel);
				if (oRel!=null) {
					//					System.out.println("Le corresponde ["+oRel.name()+"]");
					cmbObjectDes.setSelectedItem(oRel);
				}else {
					System.out.println("["+oSel.name()+"] No tiene extremo");
				}
			}
		});
		panel.add(cmbObjectDes);

		cmbMultiOri = new JComboBox<String>(aMulti);
		cmbMultiOri.setBounds(205, 275, 120, 25);
		cmbMultiOri.setEditable(false);
		cmbMultiOri.setVisible(false);

		panel.add(cmbMultiOri);		

		txMultiOri = new JTextField(20);
		txMultiOri.setBounds(205, 275, 120, 25);
		txMultiOri.setBorder(blackline);
		txMultiOri.setEditable(false);
		txMultiOri.setBackground(colorSoftGray);
		txMultiOri.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txMultiOri);	

		cmbMultiDes = new JComboBox<String>(aMulti);
		cmbMultiDes.setBounds(335, 275, 120, 25);
		cmbMultiDes.setEditable(false);
		cmbMultiDes.setVisible(false);
		panel.add(cmbMultiDes);		

		txMultiDes = new JTextField(20);
		txMultiDes.setBounds(335, 275, 120, 25);
		txMultiDes.setBorder(blackline);
		txMultiDes.setEditable(false);
		txMultiDes.setBackground(colorSoftGray);
		txMultiDes.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txMultiDes);	

		txOriAssocRole = new JTextField(20);
		txOriAssocRole.setBounds(205, 305, 120, 25);
		txOriAssocRole.setBorder(blackline);
		txOriAssocRole.setEditable(false);
		txOriAssocRole.setBackground(colorSoftGray);
		txOriAssocRole.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txOriAssocRole);	

		txDesAssocRole = new JTextField(20);
		txDesAssocRole.setBounds(335, 305, 120, 25);
		txDesAssocRole.setBorder(blackline);
		txDesAssocRole.setEditable(false);
		txDesAssocRole.setBackground(colorSoftGray);
		txDesAssocRole.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txDesAssocRole);	

		btnInsertLinkAssoc = new JButton("Insert link");
		btnInsertLinkAssoc.setBounds(205, 340, 120, 25);
		btnInsertLinkAssoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MAssociation oAssoc = lAssocs.getSelectedValue();
				insertLink(oAssoc) ;
			}
		});
		panel.add(btnInsertLinkAssoc);

		btnDeleteLink = new JButton("Delete link");
		btnDeleteLink.setBounds(335, 340, 120, 25);
		btnDeleteLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MAssociation oAssoc = lAssocs.getSelectedValue();
				int resp =JOptionPane.showConfirmDialog(null, "Are you sure to delete link ["+oAssoc.name()+"]?",
						"Delete Links", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);					

				deleteLink(oAssoc) ;
			}
		});

		panel.add(btnDeleteLink);

		lbClassInvariants = new JLabel("State invariants");
		lbClassInvariants.setBounds(205, 375, 120, 25);
		panel.add(lbClassInvariants);

		lbResClassInvariants = new JLabel("");
		lbResClassInvariants.setBounds(335, 375, 120, 25);
		lbResClassInvariants.setBorder(blackline);
		lbResClassInvariants.setVerticalAlignment(SwingConstants.CENTER);
		lbResClassInvariants.setHorizontalAlignment(SwingConstants.CENTER);
		lbResClassInvariants.setFont(new Font("Serif", Font.BOLD, 18));
		lbResClassInvariants.setVisible(false);
		panel.add(lbResClassInvariants);

		btnShowClassInvariants = new JButton("OK");
		btnShowClassInvariants.setBounds(335, 375, 120, 25);
		btnShowClassInvariants.setVerticalAlignment(SwingConstants.CENTER);
		btnShowClassInvariants.setHorizontalAlignment(SwingConstants.CENTER);
		btnShowClassInvariants.setFont(new Font("Serif", Font.BOLD, 18));
		btnShowClassInvariants.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showClassInvariantsState();
			}
		});
		panel.add(btnShowClassInvariants);

		lbCheckStructure = new JLabel("Check Structure");
		lbCheckStructure.setBounds(205, 410, 120, 25);
		panel.add(lbCheckStructure);

		btnShowCheckStructure = new JButton("OK");
		btnShowCheckStructure.setBounds(335, 410, 120, 25);
		btnShowCheckStructure.setVerticalAlignment(SwingConstants.CENTER);
		btnShowCheckStructure.setHorizontalAlignment(SwingConstants.CENTER);
		btnShowCheckStructure.setFont(new Font("Serif", Font.BOLD, 18));
		btnShowCheckStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean ok=checkStructure();
				if (!ok) {
					MVMWizardAssoc dW= new MVMWizardAssoc(frame,lAssocsWizard);
					dW.setSize(910,490);
					dW.setLocationRelativeTo(null);
					dW.setVisible(true);
					String commandWizard = dW.getCommandWizard();
					MAssociation oAssocPralWizard = dW.getAssocWizard();
					if (commandWizard!="") {
						doActionsWizardAssoc(oAssocPralWizard,commandWizard);
					}
				}
			}
		});
		panel.add(btnShowCheckStructure);
		panel.add(scrollPaneClass);
		panel.add(scrollPaneObj);

		panel.add(lAttrs);		
		panel.add(fTablePane);	

		lClass.setSelectedIndex(0);
		nomClass = ((MClass) lClass.getSelectedValue()).name();
		lObjects.setModel(loadListObjects(nomClass));
		lObjects.setSelectedIndex(0);
		lAssocs.setSelectedIndex(0);
		MAssociation oAssoc = lAssocs.getSelectedValue();
		if (oAssoc!=null) {
			setComposAssoc(oAssoc);
		}

		setResClassInvariants();
		setResCheckStructure();
		add(panel);

		setSize(new Dimension(400, 300));

	}

	/**
	 * Delete all existing objects and links
	 */
	private void resetObjLinks() {
		for (MLink oLink: fSession.system().state().allLinks()) {
			deleteLink(oLink);
		}
		Set<MObject> allObjects = fSession.system().state().allObjects();
		List<MObject> lObjs = new ArrayList<MObject>();
		for (MObject oObj : allObjects) {
			lObjs.add(oObj);
		}
		for (MObject oObj : lObjs) {
			deleteObject(oObj.name());
		}
		lClass.setModel(loadListMClass());
		lClass.setSelectedIndex(0);
		lAssocs.setModel(loadListAssoc());
		lAssocs.setSelectedIndex(0);
		DefaultComboBoxModel<MObject> cbmOri = new DefaultComboBoxModel<MObject>();
		DefaultComboBoxModel<MObject> cbmDes = new DefaultComboBoxModel<MObject>();
		cmbObjectOri.setModel(cbmOri);
		cmbObjectDes.setModel(cbmDes);

		// Clean Actions
		lActions.clear();
	}

	//	/**
	//	 * Another kind of reset (pending)
	//	 */
	//	private void TheoricReset() {
	//		Path f = Options.getRecentFile("use");
	//		if (f != null) {
	//
	//
	//			//					fMainWindow.fLogPanel.clear();
	//			//					showLogPanel();
	//
	//			fLogWriter.println("compiling specification " + f.toString() + "...");
	//
	//			MModel model = null;
	//			try (InputStream iStream = Files.newInputStream(f)) {
	//				model = USECompiler.compileSpecification(iStream, f.toAbsolutePath().toString(),
	//						fLogWriter, new ModelFactory());
	//				fLogWriter.println("done.");
	//			} catch (IOException ex) {
	//				fLogWriter.println("File `" + f.toAbsolutePath().toString() + "' not found.");
	//			}
	//
	//			final MSystem system;
	//			if (model != null) {
	//				fLogWriter.println(model.getStats());
	//				// create system
	//				system = new MSystem(model);
	//			} else {
	//				system = null;
	//			}
	//
	//			// set new system (may be null if compilation failed)
	//			//			SwingUtilities.invokeLater(new Runnable() {
	//			//				@Override
	//			//				public void run() {
	//			//					fSession.setSystem(system);
	//			//				}
	//			//			});
	//			fSession.reset();
	//			System.out.println(fSession.system().state().allObjects());
	//			//
	//			//			if (system != null) {
	//			//				Options.getRecentFiles().push(f.toString());
	//			//				Options.setLastDirectory(f.getParent());
	//			//				//						return true;
	//			//			} else {
	//			//				//						return false;
	//			//			}
	//			//				}
	//		}
	//	}

	/**
	 * Refresh components
	 */	
	public void refreshComponents() {
		lClass.setModel(loadListMClass());
		lClass.setSelectedIndex(0);
		oClass = lClass.getSelectedValue();
		nomClass = oClass.name();
		lObjects.setModel(loadListObjects(nomClass));
		lObjects.setSelectedIndex(0);
		nomObj = (String) lObjects.getSelectedValue();
		selectObject( nomObj);
		txNewObject.setText(nomObj);
		txNewObject.setEnabled(false);
		cmbClassOri.setModel(loadComboClass());
		cmbClassDes.setModel(loadComboClass());

		lAssocs.setModel(loadListAssoc());
		lAssocs.setSelectedIndex(0);

		MAssociation oAssoc = lAssocs.getSelectedValue();
		setComposAssoc(oAssoc);

		cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
		cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));

		setResClassInvariants();
		setResCheckStructure();

		// Reconstruir la lista de acciones
		// Array de objetos existentes antes
		int nFilasObj=0;
		int nColumnasObj=2;
		int nFilasLink=0;
		int nColumnasLink=2;
		int nActions = lActions.size();
		//--------
		//-- OBJ's
		//--------
		List<MVMObject> lMVMObjs = new ArrayList<MVMObject>();
		List<MVMLink> lMVMLinks = new ArrayList<MVMLink>();
		if (nActions>0) {
			MVMAction oAction = lActions.get(nActions-1);
			lMVMObjs = oAction.getlObjs();
			lMVMLinks = oAction.getlLinks();
			nFilasObj=lMVMObjs.size();	
			nFilasLink=lMVMLinks.size();
		}

		String[][] objsActual = new String[nFilasObj][nColumnasObj];
		// Inicializa objsActual con los objetos actuales
		int nMVMObj=0;
		for (MVMObject oMVMObj: lMVMObjs) {
			String strObj = oMVMObj.getClassName()+":"+oMVMObj.getName();
			objsActual[nMVMObj][0]=strObj;
			objsActual[nMVMObj][1]="0";
			nMVMObj++;
		}
		List<MObject> lObjsACrear = new ArrayList<MObject>();
		Set<MObject> allObjects = fSession.system().state().allObjects();
		for (MObject oObj : allObjects) {
			String strObjCmp=oObj.cls().name()+":"+oObj.name();
			// Ver si es un objeto ya existente
			boolean exist=false;
			for (int nf=0;nf<nFilasObj;nf++) {
				String strObjActual=objsActual[nf][0];
				//				if ((objsActual[nf][0]).equals(strObjCmp)) {
				if (strObjActual.equals(strObjCmp)) {
					objsActual[nf][1]="1";
					//-----------------------------------------------------------------
					// Ver si los valores de los atributos son iguales o han cambiado
					// Atributos del sistema
					MObjectState objState = oObj.state(fSystem.state());
					Map<MAttribute, Value> attrsMap = objState.attributeValueMap();
					Collection<MAttribute> attributes = ModelBrowserSorting.getInstance().sortAttributes( attrsMap.keySet() );
					List<MAttribute> attrs = Lists.newArrayList(attributes);
					String[]  valuesAttrs = new String[attrs.size()];
					for (int i = 0; i < valuesAttrs.length; i++) {
						valuesAttrs[i] = attrsMap.get(attrs.get(i)).toString();
					}
					// Atributos del objeto MVM
					List<MVMAttribute> lMVMAttrs = new ArrayList<MVMAttribute>();
					for (MVMObject oMVMObj: lMVMObjs) {
						if(oMVMObj.getName().equals(oObj.name())&&oMVMObj.getClassName().equals(oObj.cls().name())) {
							lMVMAttrs = oMVMObj.getAttributes();
						}
					}
					// Comparar atributos entre si y si hay diferencia, giardar en storeaction MA
					for (int na=0;na<attrs.size();na++) {
						MAttribute attr=attrs.get(na);
						for(MVMAttribute oMVMAttr:lMVMAttrs) {
							String attrNameSys=attr.name();
							String attrNameMVM=oMVMAttr.getName();
							if(attrNameSys.equals(attrNameMVM)) {

								String valueSYS=attrsMap.get(attr).toString();
								String valueMVM=oMVMAttr.getValue();

								if(!valueSYS.equals(valueMVM)) {
									storeAction("MA", "Modification object ["+oObj.name()+"] of ["+oObj.cls().name()+"]");
									//									System.out.println("attr ["+attrNameSys+"] sys["
									//											+valueSYS+"] MVM["+valueMVM+"]");	

								}
							}
						}
					}

					exist=true;
					break;
				}
			}
			if (!exist) {
				// Si no existe lo guardamos para crear posteriormente
				lObjsACrear.add(oObj);
			}
		}
		// Vemos si hay algun objeto que ya no exista y lo borramos
		for (int nf=0;nf<nFilasObj;nf++) {
			if ((objsActual[nf][1]).equals("0")) {
				String[] partes = objsActual[nf][0].split(":");
				String objClass=partes[0];
				String objName=partes[1];
				//				System.out.println("Borro ["+objsActual[nf][0]+"]");
				storeAction("DO", "Delete object ["+objName+"] of ["+objClass+"]");	
			}
		}
		// Vemos si hay algun objeto que se tenga que crear
		for (MObject oObj: lObjsACrear) {
			//			System.out.println("Creo ["+oObj.name()+"]");
			String objClass=oObj.cls().name();
			String objName=oObj.name();
			storeAction("CO", "Creation object ["+objName+"] of ["+objClass+"]");
		}

		//--------
		//-- Link's
		//--------

		String[][] linksActual = new String[nFilasLink][nColumnasLink];
		// Inicializa objsActual con los objetos actuales
		int nMVMLink=0;
		for (MVMLink oMVMLink: lMVMLinks) {
			String strLink = oMVMLink.getNomAssoc()+":"
					+oMVMLink.getEnd1Class()+":"+oMVMLink.getEnd1Object()
					+":"+oMVMLink.getEnd2Class()+":"+oMVMLink.getEnd2Object();
			linksActual[nMVMLink][0]=strLink;
			linksActual[nMVMLink][1]="0";
			nMVMLink++;
		}
		List<MLink> lLinksACrear = new ArrayList<MLink>();
		for (MLink oLink: fSession.system().state().allLinks()) {
			boolean exist=false;

			String cls1 = oLink.getLinkEnd(0).object().cls().name();
			String obj1 = oLink.getLinkEnd(0).object().name();
			String cls2 = oLink.getLinkEnd(1).object().cls().name();
			String obj2 = oLink.getLinkEnd(1).object().name();
			String strLinkCmp = oLink.association().name()+":"
					+cls1+":"+obj1+":"
					+cls2+":"+obj2;
			for (int nf=0;nf<nFilasLink;nf++) {
				String strActual = linksActual[nf][0];
				if ((strActual).equals(strLinkCmp)) {
					linksActual[nf][1]="1";
					exist=true;
					break;
				}
			}
			if (!exist) {
				// Si no existe lo guardamos para crear posteriormente
				lLinksACrear.add(oLink);
			}
		}
		// Vemos si hay algun link que ya no exista y lo borramos
		for (int nf=0;nf<nFilasLink;nf++) {
			if ((linksActual[nf][1]).equals("0")) {
				String strActual = linksActual[nf][0];
				String[] partes = strActual.split(":");
				String strAsocc=partes[0];
				String obj1=partes[2];
				String obj2=partes[4];
				//				System.out.println("Borro ["+linksActual[nf][0]+"]");
				storeAction("DL", "Delete link ["+strAsocc+"] - ["+obj1+"]/["+obj2+"]");
			}
		}
		// Vemos si hay algun objeto que se tenga que crear
		for (MLink oLink: lLinksACrear) {
			//			System.out.println("Creo en actions ["+oLink.association().name()+"]");
			String obj1 = oLink.getLinkEnd(0).object().name();
			String obj2 = oLink.getLinkEnd(1).object().name();
			MObject o1 = findObjectByName(obj1);
			MObject o2 = findObjectByName(obj2);
			MObject oOri=null;
			MObject oDes=null;

			// Determinar orden de oOri y oDes
			List<MAssociationEnd> oAsocEnds = oAssoc.associationEnds();

			int na=0;
			MAssociationEnd oAssocEnd = oAsocEnds.get(na);
			MClass oClassAssocEnd = oAssocEnd.cls();
			if (oClassAssocEnd.name().equals(o1.cls().name())) {
				oOri = o1;
				oDes =	o2;	
			}else {
				oOri = o2;
				oDes =	o1;	
			}
			storeAction("CL", "Creation link ["+oClassAssocEnd.name()+"] - ["+oOri.name()+"]/["+oDes.name()+"]");
		}
	}
	/**
	 * Searches for object corresponding to the end of an association
	 * @param oFindAssoc
	 * @return
	 */
	private MObject findAssocEnd(MObject oFindAssoc) {
		MObject objEnd=null;
		MSystemState state = fSystem.state();
		Set<MLink> oLinkSets=state.allLinks();
		for (MLink oLink: oLinkSets) {
			//			System.out.println("oLink ["+oLink.linkedObjects()+"]");
			MLinkEnd oL0 = oLink.getLinkEnd(0);
			MLinkEnd oL1 = oLink.getLinkEnd(1);
			if(oL0.object().name().equals(oFindAssoc.name())&&
					oL0.object().cls().name().equals(oFindAssoc.cls().name())) {
				return oL1.object();
			}
			if(oL1.object().name().equals(oFindAssoc.name())&&
					oL1.object().cls().name().equals(oFindAssoc.cls().name())) {
				return oL0.object();
			}				
		}
		return objEnd;
	}
	/**
	 * Get objects in format MVM to wizard Actions
	 */
	private List<MVMObject> getMVMObjects() {
		List<MVMObject> lObjs = new ArrayList<MVMObject>();
		Set<MObject> allObjects = fSession.system().state().allObjects();
		for (MObject oObj : allObjects) {
			MVMObject obj = new MVMObject();
			obj.setClassName(oObj.cls().name());
			obj.setName(oObj.name());

			MObjectState objState = oObj.state(fSystem.state());
			Map<MAttribute, Value> attrsMap = objState.attributeValueMap();
			Collection<MAttribute> attributes = ModelBrowserSorting.getInstance().sortAttributes( attrsMap.keySet() );

			attributes = Collections2.filter(attributes, new Predicate<MAttribute>() {
				@Override
				public boolean apply(MAttribute input) {
					return !input.isDerived();
				}
			});

			List<MAttribute> attrs = Lists.newArrayList(attributes);
			String[]  valuesAttrs = new String[attrs.size()];
			for (int i = 0; i < valuesAttrs.length; i++) {
				valuesAttrs[i] = attrsMap.get(attrs.get(i)).toString();
			}

			List<MVMAttribute> lMVMAttrs = new ArrayList<MVMAttribute>();

			for (int i = 0; i < valuesAttrs.length; i++) {
				MVMAttribute attr1 = new MVMAttribute();
				attr1.setName(((MAttribute)attrs.get(i)).name());
				attr1.setValue(valuesAttrs[i]);
				lMVMAttrs.add(attr1);
			}
			obj.setAttributes(lMVMAttrs);
			lObjs.add(obj);
		}
		return lObjs;
	}
	/**
	 * Get links in format MVM to wizard Actions
	 */
	private List<MVMLink> getMVMLinks() {
		List<MVMLink> lLinks = new ArrayList<MVMLink>();
		for (MLink oLink: fSession.system().state().allLinks()) {
			MLinkEnd oL0 = oLink.getLinkEnd(0);
			MLinkEnd oL1 = oLink.getLinkEnd(1);

			MVMLink link = new MVMLink();
			link.setCodeLink(String.valueOf(lLinks.size()+1));
			link.setNomAssoc(oLink.association().name());
			link.setEnd1Class(oL0.object().cls().name());
			link.setEnd1Object(oL0.object().name());
			link.setEnd1Role(oL0.associationEnd().name());
			link.setEnd2Class(oL1.object().cls().name());
			link.setEnd2Object(oL1.object().name());
			link.setEnd2Role(oL0.associationEnd().name());
			lLinks.add(link);
		}
		return lLinks;
	}

	/**
	 * store actions for WizardActions Load/Save
	 * @param strTypeAction
	 * @param strParam
	 */
	public void storeAction(String strTypeAction, String strParam) {
		MVMAction oAction = new MVMAction();
		oAction.setTypeAction(strTypeAction);
		oAction.setParameters(strParam);

		// Obtiene coleccion de objectos existentes tras la accion
		List<MVMObject> lObjs = getMVMObjects();
		oAction.setlObjs(lObjs);

		// Obtiene coleccion de links existentes tras la accion
		List<MVMLink> lLinks = getMVMLinks();
		oAction.setlLinks(lLinks);

		// Cuenta las acciones existentes en la lista e incrementa una unidad
		oAction.setOrden(lActions.size()+1);
		// Inserta acción en lista de acciones
		lActions.add(oAction);
	}

	/**
	 * Do Actions (create objects & links)
	 * @param lActionsRes
	 */

	private void doActions(List<MVMAction> lActionsRes) {
		resetObjLinks();
		int nActions = lActionsRes.size();
		// En realidad, las acciones a realizar son la creacion de objetos y de links
		if (nActions>0) {
			MVMAction oAction=lActionsRes.get(nActions-1);	
			List<MVMObject> lObjs=oAction.getlObjs();
			for (MVMObject oObj: lObjs) {
				String nomObj=oObj.getName();
				String ClassObj = oObj.getClassName();
				MClass oClassCreate = findMClassByName(ClassObj);

				if (!existObject(nomObj,ClassObj)) {
					createObject(oClassCreate, nomObj);
				}
				MSystemState state = fSystem.state();
				fObject = state.objectByName(nomObj);

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

				List<MVMAttribute> lAttrsMVM = oObj.getAttributes();

				boolean error = false;
				for (int i = 0; i < fAttributes.size(); ++i) {
					MAttribute attribute = fAttributes.get(i);
					MVMAttribute attrMVM = lAttrsMVM.get(i);
					String value = attrMVM.getValue();
					StringWriter errorOutput = new StringWriter();
					Expression valueAsExpression = 
							OCLCompiler.compileExpression(
									fSystem.model(),
									fSystem.state(),
									value, 
									"<input>", 
									new PrintWriter(errorOutput, true), 
									fSystem.varBindings());

					if (valueAsExpression == null) {
						JOptionPane.showMessageDialog(fMainWindow,errorOutput,"Error",JOptionPane.ERROR_MESSAGE);
						error = true;
						continue;
					}

					try {
						fSystem.execute(
								new MAttributeAssignmentStatement(fObject,attribute,valueAsExpression));

					} catch (MSystemException e) {
						JOptionPane.showMessageDialog(fMainWindow,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						error = true;
					}
				}					
				selectObject( nomObj);
				applyChanges();
				String strTypeAction="CO";
				String strParameters="Creation object ["+nomObj+"] of ["+ClassObj+"]";
				storeAction(strTypeAction, strParameters);
				cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
				cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));

			}
			List<MVMLink> lLinks = oAction.getlLinks();
			for (MVMLink oLinkMVM: lLinks) {
				String nomAssoc=oLinkMVM.getNomAssoc();
				String end1Object=oLinkMVM.getEnd1Object();
				String end2Object=oLinkMVM.getEnd2Object();

				MObject oOri=null;
				MObject oDes=null;

				MObject o1 = findObjectByName(end1Object);
				MObject o2 = findObjectByName(end2Object);
				MAssociation oAssoc = findAssocByName(nomAssoc);
				List<MAssociationEnd> oAsocEnds = oAssoc.associationEnds();
				int na=0;
				MAssociationEnd oAssocEnd = oAsocEnds.get(na);
				MClass oClassAssocEnd = oAssocEnd.cls();
				if (oClassAssocEnd.name().equals(o1.cls().name())) {
					oOri = o1;
					oDes =	o2;	
				}else {
					oOri = o2;
					oDes =	o1;	
				}			
				cmbClassOri.setSelectedItem(oOri.cls());
				cmbObjectOri.setSelectedItem(oOri);
				cmbClassDes.setSelectedItem(oDes.cls());
				cmbObjectDes.setSelectedItem(oDes);

				insertLink(oAssoc,o1,o2);
			}
			setResClassInvariants();
			setResCheckStructure();
			bNewObj=false;
			txNewObject.setEnabled(false);
			// Despues de cargar todo se ha de seleccionar la primera clase y el primer objeto de dicha clase

			if (lClass.getModel().getSize()>0) {
				oClass = lClass.getModel().getElementAt(0);
				//				System.out.println("Clic en: " + oClass);
				lClass.setSelectedIndex(0);
				nomClass = oClass.name();
				lObjects.setModel(loadListObjects(nomClass));
				lObjects.setSelectedIndex(0);
				nomObj = (String) lObjects.getSelectedValue();
				selectObject( nomObj);
				txNewObject.setText(nomObj);
				txNewObject.setEnabled(false);			
			}
		}
	}

	/**
	 * Realiza las acciones propuestas en wizard
	 * @param oAssocPralWizard
	 * @param commandWizard
	 */
	private void doActionsWizardAssoc(MAssociation oAssocPralWizard, String commandWizard) {
		//		System.out.println("getActionCommand " + commandWizard);
		ArrayList<String> lNewObjects = new ArrayList<String>();
		String[] partes = commandWizard.split("-");
		int nPartes = partes.length;
		for (int nParte=0;nParte<nPartes;nParte++) {
			String parte = partes[nParte];
			String[] subPartes = parte.split(" ");
			String action=subPartes[0];
			String paramAction=subPartes[1];
			switch(action) {
			case "C": // Create objects
				String[] subParamC = paramAction.split(":");
				int canCrear=Integer.valueOf(subParamC[0]);
				String classCrear=subParamC[1];
				for (int n=0;n<canCrear;n++) {
					String nomObjNew=findNomProposed(classCrear);
					bNewObj=true;
					MClass oClassCreate = findMClassByName(classCrear);
					// Inicializar objeto
					saveObject(oClassCreate, nomObjNew);
					cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
					cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
					//					System.out.println("Creo objeto ["+nomObjNew+"] [" + classCrear+"]");
					fLogWriter.println("Create object ["+nomObjNew+"] [" + classCrear+"]");
					lNewObjects.add(nomObjNew);
					cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
					cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
				}

				break;
			case "I": // Insert links
				String[] subParamI = paramAction.split(":");
				String objPral=subParamI[0];
				String restoParam=subParamI[1];
				if (restoParam.contains("(NEWS)")) {
					// Se han de recuperar los objetos creados y sustituir (NEWS) por dichos objetos
					// Los objetos estan en lNewObjects
					String agrupador="";
					for(String newObject: lNewObjects) {
						if (agrupador!="") {
							agrupador+=",";
						}
						agrupador+=newObject;
					}
					String strPaso=restoParam.replace("(NEWS)", agrupador);
					restoParam=strPaso;
				}
				String[] objsLinkar = restoParam.split(",");
				int nObjs = objsLinkar.length;
				for(int nObj=0;nObj<nObjs;nObj++) {
					String objLinkar = objsLinkar[nObj];

					//					System.out.println("Insert link entre ["+objPral+"] [" + objLinkar+"]");

					MObject oOri=null;
					MObject oDes=null;
					MObject o1 = findObjectByName(objPral);
					MObject o2 = findObjectByName(objLinkar);
					// Averiguar el orden correcto para oOri i oDes segun oAssocPralWizard
					List<MAssociationEnd> oAsocEnds = oAssocPralWizard.associationEnds();

					int na=0;
					MAssociationEnd oAssocEnd = oAsocEnds.get(na);
					MClass oClassAssocEnd = oAssocEnd.cls();
					if (oClassAssocEnd.name().equals(o1.cls().name())) {
						oOri = o1;
						oDes =	o2;	
					}else {
						oOri = o2;
						oDes =	o1;	
					}

					MObject[] fParticipants = new MObject[] {oOri,oDes};
					try {
						insertLink(oAssocPralWizard, fParticipants);
						fLogWriter.println("Inserto link entre ["+oOri.name()+"] y [" + oDes.name()+"]");
						storeAction("CL", "Creation link ["+oClassAssocEnd.name()+"] - ["+oOri.name()+"]/["+oDes.name()+"]");
					} catch (MSystemException e) {
						e.printStackTrace();
					}

				}
				break;
			case "D": // Delete object
				// Hay que borrar objeto indicado en paramAction 

				int idxActual = lObjects.getSelectedIndex();
				nomObj = paramAction;
				deleteObject(nomObj);
				int nObjects = lObjects.getModel().getSize();
				if (nObjects>0) {
					if (idxActual>nObjects) {
						idxActual=nObjects;
					}
					lObjects.setSelectedIndex(idxActual);
					nomObj = (String) lObjects.getSelectedValue();
					selectObject( nomObj);
				}else {
					fTableModel.update();
				}

				break;
			default:

			}
			setComposAssoc(oAssocPralWizard);
		}
		setResClassInvariants();
		setResCheckStructure();
	}
	/**
	 * Proposes a new object name of a class
	 * @param className
	 * @return
	 */
	public String findNomProposed(String className) {
		int numObj = 1;
		String nomProposed = className.toLowerCase() + numObj; 

		// Averiguar si existe objeto o no en base a una iteracion

		while(existObject(nomProposed,className )) {
			numObj+=1;
			nomProposed = className.toLowerCase() + numObj; 
		}
		//		System.out.println("nomProposed [" + nomProposed+"]");
		return nomProposed;
	}

	/**
	 * Create a new object automatically
	 */
	public void newObjectAuto() {
		oClass = lClass.getSelectedValue();
		nomClass = oClass.name();
		String nomProposed = findNomProposed(nomClass);

		bNewObj=true;
		saveObject(oClass, nomProposed);
		setResClassInvariants();
		setResCheckStructure();
		bNewObj=false;
		txNewObject.setEnabled(false);
		selectObject(nomProposed);
		nomObj = nomProposed;
		txNewObject.setText(nomProposed);
		cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
		cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
		selectObject( nomObj);
	}
	/**
	 * Checks the existence of an object
	 * @param name
	 * @return
	 */
	public boolean existObject(String name, String className) {
		boolean bRes=false;
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();

		for (MObject obj : allObjects) {
			if (obj.name().equals(name)&&obj.cls().name().equals(className)) {
				bRes=true;
				return bRes;
			}
		}		

		return bRes;
	}

	/**
	 * Color button Check Structure according to checkstructure function
	 */
	public void setResCheckStructure() {
		boolean bRes = checkStructure();
		String sRes="OK";
		if (!bRes) sRes="KO";
		btnShowCheckStructure.setText(sRes);
		if(!bRes) {
			btnShowCheckStructure.setForeground(Color.white);
			btnShowCheckStructure.setBackground(Color.red);
		}else {
			btnShowCheckStructure.setForeground(Color.black);
			btnShowCheckStructure.setBackground(Color.green);
		}
	}

	/**
	 * Color button State invariants according to check_inv_state() function
	 */
	public void setResClassInvariants() {
		boolean bRes = check_inv_state();
		String sRes="OK";
		if (!bRes) sRes="KO";
		lbResClassInvariants.setText(sRes);
		btnShowClassInvariants.setText(sRes);
		if(!bRes) {
			lbResClassInvariants.setForeground(Color.white);
			lbResClassInvariants.setBackground(Color.red);
			btnShowClassInvariants.setForeground(Color.white);
			btnShowClassInvariants.setBackground(Color.red);
		}else {
			lbResClassInvariants.setForeground(Color.black);
			lbResClassInvariants.setBackground(Color.green);
			btnShowClassInvariants.setForeground(Color.black);
			btnShowClassInvariants.setBackground(Color.green);
		}
		lbResClassInvariants.setOpaque(true);

	}
	/**
	 * Show view ClassInvariantView
	 */
	public void showClassInvariantsState() {
		ClassInvariantView civ = new ClassInvariantView(fMainWindow,
				fSession.system());
		ViewFrame f = new ViewFrame("Class invariants", civ,
				"InvariantView.gif");
		civ.setViewFrame(f);
		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(civ, BorderLayout.CENTER);
		fMainWindow.addNewViewFrame(f);
	}
	/**
	 * Check status of invariants
	 * @return
	 */
	public boolean check_inv_state() {
		boolean bRes = false;

		MModel fModel = fSystem.model();
		int n = fModel.classInvariants().size();
		MClassInvariant[] fClassInvariants = new MClassInvariant[0];
		fClassInvariants = new MClassInvariant[n];
		System.arraycopy(fModel.classInvariants().toArray(), 0,
				fClassInvariants, 0, n);
		Arrays.sort(fClassInvariants);
		EvalResult[] fValues;
		fValues = new EvalResult[n];
		for (int i = 0; i < fValues.length; i++) {
			fValues[i] = null;
		}
		ExecutorService executor = Executors.newFixedThreadPool(Options.EVAL_NUMTHREADS);
		futures = new ArrayList<Future<EvalResult>>();
		ExecutorCompletionService<EvalResult> ecs = new ExecutorCompletionService<EvalResult>(executor);
		boolean violationLabel = false; 
		int numFailures = 0;
		boolean structureOK = true;	
		for (int i = 0; i < fClassInvariants.length; i++) {
			if(!fClassInvariants[i].isActive()){
				continue;
			}
			MyEvaluatorCallable cb = new MyEvaluatorCallable(fSystem.state(), i, fClassInvariants[i]);
			futures.add(ecs.submit(cb));
		}

		for (int i = 0; i < fClassInvariants.length && !isCancelled(); i++) {
			if(!fClassInvariants[i].isActive()){
				continue;
			}
			try {
				EvalResult res;
				res = ecs.take().get();
				fValues[res.index] = res;

				boolean ok = false;
				// if v == null it is not considered as a failure, rather it is
				// a MultiplicityViolation and it is skipped as failure count
				boolean skip = false;
				if (res.result != null) {
					ok = res.result.isDefined() && ((BooleanValue)res.result).isTrue();
				} else {
					violationLabel = true;
					skip = true;
				}

				if (!skip && !ok)
					numFailures++;

			} catch (InterruptedException ex) {
				break;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		for (Future<EvalResult> f : futures) {
			f.cancel(true);
		}
		boolean todosOk=true;
		System.out.println("--------------------");
		for (EvalResult res : fValues) {
			Boolean boolRes=  ((BooleanValue)res.result).value();

			if (boolRes.equals(false)) todosOk=false;
			MClassInvariant inv = fClassInvariants[res.index];
			Value resultado = res.result;
			System.out.println("res.index ["+res.index+"] ["+inv.name()+"] result ["+resultado+"]");
			// Para inv

			Expression expr = inv.flaggedExpression();
			Expression eBodyExpr = inv.bodyExpression();
			Expression eExpandedExpr = inv.expandedExpression();
			MClass iClass= inv.cls();
			Expression eSatIns = inv.getExpressionForSatisfyingInstances();
			Expression eViolIns = inv.getExpressionForViolatingInstances();
			String iQualyName  = inv.qualifiedName();
			String iVar = inv.var();
			VarDeclList iVList = inv.vars();


			System.out.println("expr ["+expr+"]");

			//---
			int nesting=0;
			//			CollectionValue rangeVal=null;

			//			ExpQuery eq = new ExpQuery(Type resultType, VarDeclList elemVarDecls,
			//		            Expression rangeExp, Expression queryExp)
			ExpForAll exp = (ExpForAll) eExpandedExpr;

			Expression fRangeExp = exp.getRangeExpression();

			EvalContext ctx=null;
			MSystemState preState = fSystem.state();
			MSystemState postState = fSystem.state();
			VarBindings bindings = new VarBindings();+
			Value v = fRangeExp.eval(ctx);
			CollectionValue rangeValAll = (CollectionValue) v;
			
//			HashSet<Value> hv = rangeValAll.collection();
//			hv = (SetValue) v;
//			for(Value v2: rangeValAll.collection()) {
				for(Value v2: rangeValAll) {
//				SetValue hv = (SetValue) v2;
//				hv.
//				CollectionValue rangeVal = (CollectionValue) v2.toString();
				CollectionValue rangeVal=(CollectionValue) v2;
				
				rangeVal.collection().add(v2);
				
				boolean doExists=false;
				boolean res2 = evalExistsOrForAll0(nesting,  rangeVal, ctx, doExists, iVList, exp);
				System.out.println("res2 ["+res2+"]");
			}
//			CollectionValue rangeVal = (CollectionValue) v;
//			boolean doExists=false;
//			boolean res2 = evalExistsOrForAll0(nesting,  rangeVal, ctx, doExists, iVList, exp);
//			System.out.println("res2 ["+res2+"]");

			//---


		}
		System.out.println("todosOk ["+todosOk+"]");
		executor.shutdown();


		return todosOk;
	}
	//----------------
	private boolean evalExistsOrForAll0(int nesting,
			CollectionValue rangeVal, EvalContext ctx, boolean doExists, 
			VarDeclList fElemVarDecls, ExpForAll fQueryExp) {

		//    	VarDeclList fElemVarDecls;	
		//    	Expression fQueryExp;


		// loop over range elements
		boolean res = !doExists;

		for (Value elemVal : rangeVal) {
			
			//---Prueba
//			 res = !doExists;
			 res = true;
			
			//--
			
			boolean res2=false;
			// bind element variable to range element, if variable was
			// declared
			if (!fElemVarDecls.isEmpty())
				ctx.pushVarBinding(fElemVarDecls.varDecl(nesting).name(),
						elemVal);

			if (!fElemVarDecls.isEmpty() && nesting < fElemVarDecls.size() - 1) {
				// call recursively to iterate over range while
				// assigning each value to each element variable
				// eventually
				res2=evalExistsOrForAll0(nesting + 1, rangeVal, ctx,
						doExists, fElemVarDecls, fQueryExp);
				if (res != doExists)
					res = evalExistsOrForAll0(nesting + 1, rangeVal, ctx,
							doExists, fElemVarDecls, fQueryExp);

				else if (ctx.isEnableEvalTree())
					// don't change the result value when expression is true
					// (exists) or
					// false (forAll) and continue iteration
					evalExistsOrForAll0(nesting + 1, rangeVal, ctx, doExists, fElemVarDecls, fQueryExp);
				else {
					//                	if (!fElemVarDecls.isEmpty())
					//                        ctx.popVarBinding();
					break;
				}
			} else {
				// evaluate predicate expression
				Value queryVal = fQueryExp.eval(ctx);

				// undefined query values default to false
				if (queryVal.isUndefined())
					queryVal = BooleanValue.FALSE;

				// don't change the result value when expression is true
				// (exists) or
				// false (forAll) and continue iteration
				if (res != doExists && ((BooleanValue) queryVal).value() == doExists)
					res = doExists;
				else if (!ctx.isEnableEvalTree() &&  ((BooleanValue) queryVal).value() == doExists) {
					//                	if (!fElemVarDecls.isEmpty())
					//                        ctx.popVarBinding();
//					break;
				}
				//---
				if (res2 != doExists && ((BooleanValue) queryVal).value() == doExists)
					res2 = doExists;
				else if (!ctx.isEnableEvalTree() &&  ((BooleanValue) queryVal).value() == doExists) {
					//                	if (!fElemVarDecls.isEmpty())
					//                        ctx.popVarBinding();
//					break;
				}

				//---

			}

			System.out.println("Element ["+elemVal.toString()+"] res ["+res+"] res2 ["+res2+"]");
			//            if (!fElemVarDecls.isEmpty())
			//                ctx.popVarBinding();
		}	
		return res;
	}

	//----------------

	/**
	 * Gets information with structure check errors
	 */
	private void getErrorsEstructure() {
		lAssocsWizard = new ArrayList<AssocWizard>();
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		boolean reportAllErrors=true;
		lAssocsWizard = fSession.system().state().checkStructureErrors( out,reportAllErrors);
		return;
	}
	/**
	 * Check structure
	 * @return
	 */
	private boolean checkStructure() {
		Map<String, List<String>> mapObjects = new HashMap<String, List<String>>();
		getErrorsEstructure(); //Provis
		// En una pasada se han de ver los objetos que admiten una multiplicidad de * para que esten disponibles
		// Se han de ver todas las asociaciones finales que hay y ver que objetos siempre estan disponibles 

		for(AssocWizard oAssoc: lAssocsWizard) {
			MAssociation oAssocModel = oAssoc.getassocModel();
			List<MAssociationEnd> oAsoccEnds = oAssocModel.associationEnds();

			MAssociationEnd oAssocEnd1 = oAsoccEnds.get(0);
			MAssociationEnd oAssocEnd2 = oAsoccEnds.get(1);
			// Si la primera tiene multi *, los objetos de la segunda debe ser disponible			
			if (oAssocEnd1.multiplicity().getRanges().get(0).toString().equals("*")) {
				// Buscamos objetos de la segunda
				MClass oClassBuscar = oAssocEnd2.cls();
				mapObjects=addAndFindObjectsIntoMap(mapObjects, oClassBuscar);
			}
			// Si la segunda tiene multi *, los objetos de la primera debe ser disponible			
			if (oAssocEnd2.multiplicity().getRanges().get(0).toString().equals("*")) {
				// Buscamos objetos de la primera
				MClass oClassBuscar = oAssocEnd1.cls();
				mapObjects=addAndFindObjectsIntoMap(mapObjects, oClassBuscar);
			}
		}

		//		System.out.println("Ya");
		List<AssocWizard> lAssocsWizardPaso = new ArrayList<AssocWizard>();
		for(AssocWizard oAssoc: lAssocsWizard) {
			lAssocsWizardPaso.add(oAssoc);
		}
		lAssocsWizard.clear();
		for (AssocWizard aw: lAssocsWizardPaso) {
			//			System.out.println("aw ["+aw.getName()+"]");	
			for (LinkWizard lw: aw.getlLinks()) {
				int needed = lw.getNeeded();
				String objectName = lw.getObject();
				String nomClass = lw.getNomClass(); //Clase del objeto principal
				String classOfName = lw.getOfClass(); // Clase del objeto que necesita
				//				System.out.println("lw ["+objectName+"] cause ["+lw.getCause()+"] necesita ["+needed+"] de la clase ["+classOfName+"]");
				// Pasada para ver los objetos disponibles por clase
				if (needed>0) {
					List<String> lObjDisponibles = new ArrayList<String>();
					// Si tiene alguna necesidad es que puede linkarse con otros objetos segun relacion
					if (mapObjects.containsKey(nomClass)) {
						lObjDisponibles=mapObjects.get(nomClass);
						if (!lObjDisponibles.contains(objectName)) {
							lObjDisponibles.add(objectName);
						}
					}else{
						lObjDisponibles.add(objectName);
						mapObjects.put(nomClass, lObjDisponibles);
					}
				}
			}
			// Revision de clases disponibles y objetos disponibles de cada una de las mismas
			//			for (Map.Entry<String, List<String>> entry : mapObjects.entrySet()) {
			//				String className = (String) entry.getKey();
			//				List<String> lObjDisponibles = new ArrayList<String>();
			//				lObjDisponibles = entry.getValue();
			//				System.out.println("La clase ["+className+"] tiene disponibles:");
			//				for(String nameObject: lObjDisponibles) {
			//					System.out.println("   Object ["+nameObject+"] ");
			//				}
			//			}
			// Analisis de problemas a solucionar
			analyzeProposals(aw, mapObjects);
		}
		boolean ok = true;
		if (mapObjects.size()>0) ok=false;
		return ok;
	}

	/**
	 * Create map with objects available for each class
	 * @param mapObjects
	 * @param oClassBuscar
	 * @return
	 */
	private Map<String, List<String>> addAndFindObjectsIntoMap(Map<String, List<String>> mapObjects, 
			MClass oClassBuscar){
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();
		for (MObject obj : allObjects) {
			String objectName = obj.name();
			String strClassName = oClassBuscar.name();
			if (obj.cls().name().equals(strClassName)) {
				List<String> lObjDisponibles = new ArrayList<String>();

				if (mapObjects.containsKey(strClassName)) {
					lObjDisponibles=mapObjects.get(strClassName);
					if (!lObjDisponibles.contains(objectName)) {
						lObjDisponibles.add(objectName);
						mapObjects.replace(strClassName, lObjDisponibles);
					}
				}else{
					lObjDisponibles.add(objectName);
					mapObjects.put(strClassName, lObjDisponibles);
				}
			}
		}
		return mapObjects;
	}
	/** 
	 * Based on the structure of an association object, it proposes to create and/or link said object to others
	 * 
	 */
	public void analyzeProposals(AssocWizard aw,Map<String, List<String>> mapObjects) {

		AssocWizard awNew = aw;
		//		System.out.println("aw ["+aw.getName()+"]");	
		List<LinkWizard> oLinks = aw.getlLinks();
		List<LinkWizard> oNewLinks = new ArrayList<LinkWizard>();

		for (LinkWizard lw: oLinks) {
			List<String> lObjAsignar = new ArrayList<String>();
			int needed = lw.getNeeded();
			int cover = 0;
			int pending=0;
			int canAssig = 0;
			int mustCreate = 0;
			String objectNameToSolve = lw.getObject();// Object a solucionar
			String nomClass = lw.getNomClass(); //Clase del objeto principal
			String classNeeded = lw.getOfClass(); // Clase del objeto que se necesita
			//			System.out.println("lw ["+objectNameToSolve+"] cause ["+lw.getCause()+"] necesita ["+needed+"] de la clase ["+classNeeded+"]");
			// Buscar cuantos objetos necesarios estan disponibles

			String strAssig="";
			for (Map.Entry<String, List<String>> entry : mapObjects.entrySet()) {
				String className = (String) entry.getKey();
				if (className.equals(classNeeded)){
					List<String> lObjDisponibles = new ArrayList<String>();
					lObjDisponibles = entry.getValue();

					for(String nameObject: lObjDisponibles) {
						pending = needed - cover;
						if (pending > 0 ) {
							//							System.out.println("   Object ["+nameObject+"] ");
							lObjAsignar.add(nameObject);
							cover+=1;
							canAssig+=1;
							if (strAssig!="") {
								strAssig+=",";
							}
							strAssig+=nameObject;
						}
					}
				}
			}
			mustCreate = needed - cover;
			Map<String, String> mapActions = new HashMap<String, String>();
			String linkAction="";
			if (canAssig > 0) {
				//				System.out.println("Para ["+objectNameToSolve+"] asignamos ["+canAssig+"] ["+strAssig+"] y queda pendiente ["+mustCreate+"]");
				mapActions.put("A", strAssig);
				linkAction=objectNameToSolve+":"+strAssig;
			}

			// Si queda algun objeto pendiente de asignar hemos de crearlo
			if (mustCreate > 0 ) {
				//				System.out.println("Para ["+objectNameToSolve+"] hemos de crear ["+mustCreate+"] objects de tipo ["+classNeeded+"]");
				mapActions.put("C-"+mustCreate, classNeeded);
				// Considerar calcular los nombres de los nuevos objectos para cambiar NEWS por dichos nombres
				if (linkAction!="") {
					linkAction+=",(NEWS)";
				}else {
					linkAction=objectNameToSolve+":(NEWS)";
				}
			}
			if (linkAction!="") {
				mapActions.put("I", linkAction);
			}
			// Insertamos acciones en lw
			lw.setMapActions(mapActions);
			//			System.out.println();
			//			for (Map.Entry<String, String> entry : mapActions.entrySet()) {
			//				String actionW =  entry.getKey();
			//				String infoW = entry.getValue();
			//				System.out.println("ActionW ["+actionW+"]");
			//				System.out.println("InfoW   ["+infoW+"]");
			//			}
			oNewLinks.add(lw);
		}
		awNew.setlLinks(oNewLinks);
		lAssocsWizard.add(awNew);
	}

	/**
	 * Set frame name
	 * @param name
	 */
	public void setFrameName(String name) {
		frame.setName(name);
	}

	/**
	 * Prepare class list
	 * @return
	 */
	private DefaultListModel<MClass> loadListMClass() {
		DefaultListModel<MClass> ldefLModel = new DefaultListModel<MClass>();
		for (MClass oClass : fSystem.model().classes()) {
			ldefLModel.addElement(oClass);
		}
		ldefLModel=sortDefaultListModelMClass(ldefLModel);
		return ldefLModel;
	}

	/**
	 * Prepare list of objects
	 * @param nomClass
	 * @return
	 */
	private DefaultListModel<String> loadListObjects(String nomClass) {
		// Guardar lista de objetos antes de la carga para compararlos despues y si falta alguno crearlo 
		DefaultListModel<String> ldefLModel = new DefaultListModel<String>();
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();

		for (MObject obj : allObjects) {
			if (obj.cls().name().equals(nomClass)) {
				ldefLModel.addElement(obj.name());
			}
		}
		ldefLModel=sortDefaultListModelString(ldefLModel);
		return ldefLModel;
	}

	/**
	 * Sort list of strings
	 * @param ldefLModel
	 * @return
	 */
	private DefaultListModel<String> sortDefaultListModelString(DefaultListModel<String> ldefLModel) {
		// Obtener elementos del modelo y ordenarlos
		Vector<String> data = new Vector<>(ldefLModel.size());
		for (int i = 0; i < ldefLModel.size(); i++) {
			data.add(ldefLModel.getElementAt(i));
		}
		Collections.sort(data);

		// Limpiar el modelo y agregar elementos ordenados
		ldefLModel.clear();
		for (String element : data) {
			ldefLModel.addElement(element);
		}
		return ldefLModel;
	}

	/**
	 * Sort list of MClass
	 * @param ldefLModel
	 * @return
	 */
	private DefaultListModel<MClass> sortDefaultListModelMClass(DefaultListModel<MClass> ldefLModel) {
		// Obtener elementos del modelo y ordenarlos
		Vector<MClass> data = new Vector<>(ldefLModel.size());
		for (int i = 0; i < ldefLModel.size(); i++) {
			data.add(ldefLModel.getElementAt(i));
		}
		Collections.sort(data);

		// Limpiar el modelo y agregar elementos ordenados
		ldefLModel.clear();
		for (MClass element : data) {
			ldefLModel.addElement(element);
		}
		return ldefLModel;
	}

	/**
	 * Implements MObject comparator to classify
	 * @author utopi
	 *
	 */
	class MObjectComparator implements Comparator<MObject> {
		@Override
		public int compare(MObject o1, MObject o2) {
			Comparator<String> stringComparator = Comparator.naturalOrder();
			// Comparar por el valor
			return stringComparator.compare(o1.name(), o2.name());
		}
	}

	/**
	 * Load combo of classes
	 * @return
	 */
	private  DefaultComboBoxModel<MClass> loadComboClass() {
		DefaultComboBoxModel<MClass> cbm = new DefaultComboBoxModel<MClass>();

		for (MClass oClass : fSystem.model().classes()) {
			cbm.addElement(oClass);
		}

		List<MClass> data = new ArrayList<>();
		for (int i = 0; i < cbm.getSize(); i++) {
			data.add((MClass) cbm.getElementAt(i));
		}

		// Ordenar los elementos
		Collections.sort(data, new MClassComparator());

		// Limpiar el modelo y agregar elementos ordenados
		cbm.removeAllElements();
		for (MClass element : data) {
			cbm.addElement(element);
		}

		return cbm;
	}

	/**
	 * Implements MClass comparator to classify
	 * @author utopi
	 *
	 */
	class MClassComparator implements Comparator<MClass> {
		@Override
		public int compare(MClass o1, MClass o2) {
			Comparator<String> stringComparator = Comparator.naturalOrder();
			// Comparar por el valor
			return stringComparator.compare(o1.name(), o2.name());
		}
	}

	/**
	 * Load combo of objects
	 * @param cmbClass
	 * @return
	 */
	private  DefaultComboBoxModel<MObject> loadComboObjectMObject(JComboBox<MClass> cmbClass) {
		DefaultComboBoxModel<MObject> cbm = new DefaultComboBoxModel<MObject>();
		MClass oClass = (MClass) cmbClass.getItemAt(cmbClass.getSelectedIndex());
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();

		for (MObject obj : allObjects) {
			if (obj.cls().name().equals(oClass.name())) {
				cbm.addElement(obj);
			}
		}

		List<MObject> data = new ArrayList<>();
		for (int i = 0; i < cbm.getSize(); i++) {
			data.add((MObject) cbm.getElementAt(i));
		}

		// Ordenar los elementos
		Collections.sort(data, new MObjectComparator());

		// Limpiar el modelo y agregar elementos ordenados
		cbm.removeAllElements();
		for (MObject element : data) {
			cbm.addElement(element);
		}

		return cbm;
	}

	/**
	 * Load association list
	 * @return
	 */
	private DefaultListModel<MAssociation> loadListAssoc() {
		DefaultListModel<MAssociation> ldefLModel = new DefaultListModel<MAssociation>();
		for (MAssociation oAssoc : fSystem.model().associations()) {
			ldefLModel.addElement(oAssoc);
		}
		return ldefLModel;
	}

	/**
	 * Select the components of an indicated association
	 * @param oAssoc
	 */
	private void setComposAssoc(MAssociation oAssoc) {

		Set<MLink> links = fSystem.state().linksOfAssociation(oAssoc).links();
		// Si no hay links, se han de inicializar los combos en base a las AssociationEnds
		int nLink=0;
		if (links.size()==0) {
			for (MAssociationEnd ma:oAssoc.associationEnds()) {
				String className=ma.cls().name();
				switch(nLink){
				case 0:
					selectClassInCombo(cmbClassOri,className);
					lbFromClass.setText(className);
				case 1:
					selectClassInCombo(cmbClassDes,className);
					lbToClass.setText(className);
				default:
					// De momento no hacemos nada
					break;
				}
				nLink+=1;
			}
			return;
		}

		// Si hay links, se analizan los links y se muestra el primero
		// Inicialmente supondremos que solo hay un link

		for (MLink oLink:links) {
			//			System.out.println(oLink.linkedObjects());
			if (nLink==0) {
				MObject oOri=null;
				MObject oDes=null;

				int nLinkEnd=0;
				for(MLinkEnd oMlinkEnd: oLink.linkEnds()) {
					//					System.out.println(oMlinkEnd.object().name());
					MAssociationEnd oMAssociationEnd=oMlinkEnd.associationEnd();
					//					System.out.println(oMAssociationEnd.name());

					MMultiplicity oMMultiplicity=oMAssociationEnd.multiplicity();

					//					System.out.println(oMMultiplicity.toString());
					switch(nLinkEnd){
					case 0:
						oOri=oMlinkEnd.object();
						txOriAssocRole.setText(oMAssociationEnd.name());
						cmbClassOri.setSelectedItem(oOri.cls());
						cmbObjectOri.setSelectedItem(oOri);
						cmbMultiOri.setSelectedItem(oMMultiplicity.toString());
						txMultiOri.setText(oMMultiplicity.toString());
						lbFromClass.setText(oOri.cls().name());
						break;
					case 1:
						oDes=oMlinkEnd.object();
						txDesAssocRole.setText(oMAssociationEnd.name());
						cmbClassDes.setSelectedItem(oDes.cls());
						cmbObjectDes.setSelectedItem(oDes);
						cmbMultiDes.setSelectedItem(oMMultiplicity.toString());
						txMultiDes.setText(oMMultiplicity.toString());
						lbToClass.setText(oDes.cls().name());
						break;
					default:
						// De momento no hacemos nada
						break;
					}
					nLinkEnd+=1;
				}

			}
		}
		//		System.out.println(oAssoc.name());

	}
	/**
	 * Insert link in association
	 * @param oAssoc
	 */
	private void insertLink(MAssociation oAssoc,MObject o1, MObject o2) {
		MObject oOri=null;
		MObject oDes=null;

		// Determinar orden de oOri y oDes
		List<MAssociationEnd> oAsocEnds = oAssoc.associationEnds();

		int na=0;
		MAssociationEnd oAssocEnd = oAsocEnds.get(na);
		MClass oClassAssocEnd = oAssocEnd.cls();
		if (oClassAssocEnd.name().equals(o1.cls().name())) {
			oOri = o1;
			oDes =	o2;	
		}else {
			oOri = o2;
			oDes =	o1;	
		}

		MObject[] fParticipants = new MObject[] {oOri,oDes};
		try {
			insertLink(oAssoc, fParticipants);
			storeAction("CL", "Creation link ["+oClassAssocEnd.name()+"] - ["+oOri.name()+"]/["+oDes.name()+"]");
		} catch (MSystemException e) {
			JOptionPane.showMessageDialog(
					fMainWindow, 
					e.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
		}

	}
	/**
	 * Insert link in association
	 * @param oAssoc
	 */

	private void insertLink(MAssociation oAssoc) {
		MObject o1 = cmbObjectOri.getItemAt(cmbObjectOri.getSelectedIndex());
		MObject o2 = cmbObjectDes.getItemAt(cmbObjectDes.getSelectedIndex());
		insertLink( oAssoc, o1,  o2);
	}

	/**
	 * Insert link between selected objects
	 * @param association
	 * @param objects
	 * @throws MSystemException
	 */
	private void insertLink(MAssociation association, MObject[] objects) throws MSystemException {
		if (association.hasQualifiedEnds()) {
			QualifierInputView input = new QualifierInputView(fMainWindow, fSystem, association, objects);
			input.setLocationRelativeTo(this);
			input.setVisible(true);

		} else {
			try {
				fSystem.execute(new MLinkInsertionStatement(association, objects, Collections.<List<Value>>emptyList()));
			} catch (MSystemException e) {
				throw new MSystemException(e.getMessage());
			}
		}
		setResCheckStructure();
	}
	/**
	 * Remove link in association
	 * @param oAssoc
	 */
	private void deleteLink(MAssociation oAssoc) {
		// Averiguar el link del que se trata
		MObject oOri = cmbObjectOri.getItemAt(cmbObjectOri.getSelectedIndex());
		MObject oDes = cmbObjectDes.getItemAt(cmbObjectDes.getSelectedIndex());
		deleteLink(oAssoc, oOri, oDes);
	}

	/**
	 * Remove link in association
	 * @param oAssoc
	 */
	private void deleteLink(MAssociation oAssoc, MObject oOri, MObject oDes) {

		MLink oLinkTOdel=null;
		Set<MLink> links = fSystem.state().linksOfAssociation(oAssoc).links();
		// Inicialmente supondremos que solo hay un link

		for (MLink oLink:links) {
			//			System.out.println(oLink.linkedObjects());
			//Si se trata de los objetos seleccionados en la UI es el link buscado

			int nLinksObj = oLink.linkedObjects().size();
			MObject o1=null;
			MObject o2=null;
			for(int nLinkObj = 0; nLinkObj<nLinksObj;nLinkObj++) {
				switch(nLinkObj){
				case 0:
					o1=oLink.linkedObjects().get(nLinkObj);
					break;
				case 1:
					o2=oLink.linkedObjects().get(nLinkObj);
					break;
				default:
					break;
				}
			}
			if (o1!=null && o2!=null) {
				if (oOri.equals(o1) && oDes.equals(o2) || oOri.equals(o2) && oDes.equals(o1))
					oLinkTOdel = oLink;
				continue;
			}
		}

		if(oLinkTOdel!=null) {
			//			System.out.println("Borra ["+oLinkTOdel.toString()+"]");
			deleteLink(oLinkTOdel);
		}

	}
	/**
	 * Remove link 
	 * @param link
	 */
	private void deleteLink(MLink oLink) {
		try {
			fSystem.execute(
					new MLinkDeletionStatement(oLink));
			String obj1 = oLink.getLinkEnd(0).object().name();
			String obj2 = oLink.getLinkEnd(1).object().name();

			storeAction("DL", "Delete link ["+oLink.association().name()+"] - ["+obj1+"]/["+obj2+"]");
		} catch (MSystemException e) {
			JOptionPane.showMessageDialog(
					fMainWindow, 
					e.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		// Localizar diagrama y ver si se puede actualizar
		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
				odv.repaint();
			}
		}
		setResCheckStructure();
	}

	/**
	 * Search view of the associated diagram
	 */
	private void searchObjDiagramAssociated() {
		odvAssoc = null; 
		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			if (odv.getName()!=null) {
				if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
					odvAssoc = odv.getDiagram();
					return;
				}
			}
		}
	}

	/**
	 * Create object diagram
	 */
	private void createObjDiagram() {
		NewObjectDiagramView odv = new NewObjectDiagramView(fMainWindow, fSession.system());
		ViewFrame f = new ViewFrame("Object diagram", odv, "ObjectDiagram.gif");
		f.setName(NAMEFRAMEMVMDIAGRAM);
		odv.setName(NAMEFRAMEMVMDIAGRAM);

		int OBJECTS_LARGE_SYSTEM = 100;

		// Many objects. Ask user if all objects should be hidden
		if (fSession.system().state().allObjects().size() > OBJECTS_LARGE_SYSTEM) {

			int option = JOptionPane.showConfirmDialog(new JPanel(),
					"The current system state contains more then " + OBJECTS_LARGE_SYSTEM + " instances." +
							"This can slow down the object diagram.\r\nDo you want to start with an empty object diagram?",
							"Large system state", JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				odv.getDiagram().hideAll();
			}
		}

		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(odv, BorderLayout.CENTER);
		fMainWindow.addNewViewFrame(f);
		fMainWindow.getObjectDiagrams().add(odv);
		odvAssoc = odv.getDiagram();

		tile();
	}

	/**
	 * Init new object
	 */
	private void initNewObject() {
		txNewObject.setText("");
		txNewObject.setEnabled(true);
		bNewObj=true;
	}

	/**
	 * Cancel object modifications without save them
	 * @param nomObj
	 */
	private void cancelObject(String nomObj) {
		selectObject(nomObj);
	}

	/**
	 * Check existence of object diagram
	 * @return
	 */
	private boolean checkExistObjDiagram() {
		boolean existDiagram=false;
		// Ver frames
		JDesktopPane fDesk = fMainWindow.getFdesk();
		allframes = fDesk.getAllFrames();

		for (JInternalFrame ifr: allframes) {
			if (ifr.getName()!=null ) {
				if (ifr.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
					existDiagram=true;
					for (NewObjectDiagramView newObj: fMainWindow.getObjectDiagrams()) {
						if (newObj.getDiagram().getName()==null) {
							newObj.setName(NAMEFRAMEMVMDIAGRAM);
						}
						if (newObj.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
							odvAssoc = newObj.getDiagram();
							break;
						}
					}
					continue;
				}	
			}
		}

		if (!existDiagram) {
			createObjDiagram();
		}
		return existDiagram;
	}
	/**
	 * Create an object
	 * @param oClass
	 * @param nomObj
	 */
	private void createObject(MClass oClass, String nomObj) {

		checkExistObjDiagram();
		fMainWindow.createObject(oClass, nomObj);
		lObjects.setModel(loadListObjects(nomClass));

		if (chkAutoLayout.isSelected()) {
			odvAssoc.forceStartLayoutThread();
		}
	}

	/**
	 * Save an object
	 * @param oClass
	 * @param nomObj
	 */
	private void saveObject(MClass oClass, String nomObj) {
		// Verificamos existencia de ObjectDiagram MVM
		checkExistObjDiagram();

		if (bNewObj) {
			//			System.out.println("Creando obj ["+nomObj+"] of ["+oClass.name()+"]");
			// Hacer copia de fValues siempre que la clase sea la misma

			boolean sameClass=false;
			if (fAttributes.size()>0) {
				MAttribute attr1 = fAttributes.get(0);
				if (attr1.owner().name().equals(oClass.name())) {
					sameClass=true;
				}			
			}

			String[] fValuesAnt = new String[fAttributes.size()];
			if (sameClass) {
				for (int i = 0; i < fAttributes.size(); i++) {
					fValuesAnt[i] = fValues[i];
				}
			}
			if (!existObject(nomObj, oClass.name())) {
				createObject(oClass, nomObj);
			}

			selectObject( nomObj);
			if (fValues.length == fValuesAnt.length && sameClass) {
				for (int i = 0; i < fAttributes.size(); i++) {
					fValues[i] = fValuesAnt[i];
				}
			}else {
				for (int i = 0; i < fAttributes.size(); i++) {
					MAttribute attr = (MAttribute) fAttributes.get(i);
					attr.type();
					if (attr.type().isTypeOfInteger()) {
						fValues[i] = "1";
					}else if (attr.type().isTypeOfString()) {
						fValues[i] = "'x'";
					}
				}
			}
		}else {
			//			System.out.println("Modificando obj ["+nomObj+"] of ["+oClass.name()+"]");
		}
		applyChanges();// Provis

		if (!checkExistObjDiagram()) {
			odvAssoc.forceStartLayoutThread();
		}
		if (bNewObj) {
			storeAction("CO", "Creation object ["+nomObj+"] of ["+oClass.name()+"]");
		}else {
			storeAction("MA", "Modification object ["+nomObj+"] of ["+oClass.name()+"]");	
		}

	}

	/**
	 * Delete an object
	 * @param nomObjDel
	 */
	private void deleteObject(String nomObjDel) {
		int idx = selectObject(nomObjDel);
		MSystemState state = fSystem.state();
		// Localizar diagrama y ver si se puede actualizar
		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			// Si es nulo se le pone NAMEFRAMEMVMDIAGRAM
			//Aqui
			if (odv.getDiagram().getName()==null) {
				odv.setName(NAMEFRAMEMVMDIAGRAM);
			}
			if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
				odv.getDiagram().deleteObject(fObject);
				odv.repaint();
			}
		}
		state.deleteObject(fObject);
		storeAction("DO", "Delete object ["+nomObjDel+"] of ["+fObject.name()+"]");	

		// Ojo porque si el objeto esta linkado tendriamos que eliminar el link
		lObjects.setModel(loadListObjects(nomClass));

		int nObjects = lObjects.getModel().getSize();
		if (nObjects>0) {
			if (idx>nObjects-1) {
				idx=nObjects-1;
			}
			lObjects.setSelectedIndex(idx);
			nomObj = (String) lObjects.getSelectedValue();
			selectObject( nomObj);
		}else {
			fTableModel.update();
		}
		setResClassInvariants();
		setResCheckStructure();
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
		// No refresh? 
		// Don't refresh after first change...
		fSystem.getEventBus().unregister(this);// Provis
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

	/**
	 * Check existence of an object in the system
	 * @return
	 */
	private boolean haveObject() {
		return fObject != null && fObject.exists(fSystem.state());
	}

	/**
	 * An object has been selected from the list. Update the table of
	 * properties.
	 */
	public int selectObject(String objName) {
		int idx = -1;
		MSystemState state = fSystem.state();
		fObject = state.objectByName(objName);
		fTableModel.update();

		// Buscar en lista
		lObjects.setSelectedIndex(0);
		int nObjs= lObjects.getModel().getSize();
		for (int nObj=0;nObj<nObjs;nObj++) {
			if (lObjects.getModel().getElementAt(nObj).equals(objName)) {
				lObjects.setSelectedIndex(nObj);
				idx=nObj;
				return idx;
			}
		}
		return idx;
	}

	/**
	 * Search for an object by name
	 * @param nameObject
	 * @return
	 */
	private MObject findObjectByName(String nameObject) {
		MObject oRes=null;
		MSystemState state = fSystem.state();
		oRes=state.objectByName(nameObject);
		return oRes;
	}

	/**
	 * Search for an class by name
	 * @param nameClass
	 * @return
	 */
	private MClass findMClassByName(String nameClass) {
		MClass oRes=null;
		for (MClass oClass : fSystem.model().classes()) {
			if (oClass.name().equals(nameClass)) {
				oRes=oClass;
				return oRes;
			}
		}
		return oRes;
	}

	/**
	 * Search for an Association by name
	 * @param nameAssoc
	 * @return
	 */
	private MAssociation findAssocByName(String nameAssoc) {
		MAssociation oRes=null;
		for (MAssociation oAssoc : fSystem.model().associations()) {
			if (oAssoc.name().equals(nameAssoc)) {
				oRes=oAssoc;
				return oRes;
			}
		}		
		return oRes;
	}

	/**
	 * Select combo class
	 * @param cmb
	 * @param className
	 */
	public void selectClassInCombo(JComboBox<MClass> cmb, String className) {

		// Buscar en lista
		int nObjs= cmb.getModel().getSize();
		for (int nObj=0;nObj<nObjs;nObj++) {
			MClass cl = (MClass) cmb.getModel().getElementAt(nObj);
			if (cl.name().equals(className)) {
				cmb.setSelectedIndex(nObj);
				return;
			}
		}
		return;
	}

	/**
	 * Update table data model
	 */
	private void update() {
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

	/**
	 * Stack views
	 */
	private void tile() {
		JDesktopPane fDesk = fMainWindow.getFdesk();
		allframes = fMainWindow.sortInternalFrames(fDesk.getAllFrames());
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location
		Dimension size = fDesk.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and
		// then relocating & resizing each
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (f.isIcon() && !f.isClosed()) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException ex) {
						// ignored
					}
				}
				fDesk.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}

	}
	public final boolean isCancelled() {
		//        return future.isCancelled();
		return false;
	}

	private class EvalResult {
		public final int index;
		public final Value result;
		public final String message;
		public final long duration;

		public EvalResult(int index, Value result, String message, long duration) {
			this.index = index;
			this.result = result;
			this.message = message;
			this.duration = duration;
		}
	}

	private class MyEvaluatorCallable implements Callable<EvalResult> {
		final int index;
		final MSystemState state;
		final MClassInvariant inv;

		public MyEvaluatorCallable(MSystemState state, int index, MClassInvariant inv) {
			this.state = state;
			this.index = index;
			this.inv = inv;
		}

		@Override
		public EvalResult call() throws Exception {
			if (isCancelled()) return null;

			Evaluator eval = new Evaluator();
			Value v = null;
			String message = null;
			long start = System.currentTimeMillis();

			try {
				v = eval.eval(inv.flaggedExpression(), state);
			} catch (MultiplicityViolationException e) {
				message = e.getMessage();
			}

			return new EvalResult(index, v, message, System.currentTimeMillis() - start);
		}
	}
}

