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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.mvm.LinkWizard;
import org.tzi.use.gui.mvm.MVMAction;
import org.tzi.use.gui.mvm.MVMAssocWizard;
import org.tzi.use.gui.mvm.MVMAttribute;
import org.tzi.use.gui.mvm.MVMLink;
import org.tzi.use.gui.mvm.MVMObjCheckState;
import org.tzi.use.gui.mvm.MVMObject;
import org.tzi.use.gui.mvm.MVMWizardActions;
import org.tzi.use.gui.mvm.MVMWizardAssoc;
import org.tzi.use.gui.util.ExtendedJTable;
import org.tzi.use.gui.views.diagrams.DiagramView.LayoutThread;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagram;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagramView;
import org.tzi.use.gui.views.diagrams.objectdiagram.QualifierInputView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.MultiplicityViolationException;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
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

	public static final String NAMEFRAMEMVMDIAGRAM = "MVM";
	public static final String NAMEFRAMEMVMWIZARD = "MVMWizard";
	public MainWindow fMainWindow;
	private PrintWriter fLogWriter;
	private Session fSession;
	private MSystem fSystem;
	private MObject fObject;

	public JFrame frame;
	private JPanel panel;

	private DefaultListModel<String> modelObjects;

	// Titles block
	private JLabel lbTitleElements;
	private JLabel lbTitleAssociations;
	private JLabel lbTitleActions;

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
	private JButton btnNewObjectSampleAuto;
	private JButton btnDeleteObject;
	private JButton btnSaveObject;
	private JButton btnCancelObject;
	private JButton btnInsertLinkAssoc;
	private JButton btnDeleteLink;	
	private JButton btnShowClassInvariants;	
	private JButton btnShowIndividuals;
	private JButton btnShowCheckStructure;
	private JButton btnRefreshComponents;

	private JButton btnActions;
	private JButton btnReset;

	private JSeparator separator0 = new JSeparator();
	private JSeparator separator1 = new JSeparator();
	private JSeparator separator2 = new JSeparator();

	private boolean bNewObj;
	private JTable fTable;
	private JScrollPane fTablePane;

	private TableModel fTableModel;

	private List<MAttribute> fAttributes;
	private String[] fValues;
	private Map<MAttribute, Value> fAttributeValueMap;
	private NewObjectDiagram odvAssoc;
	private String aMulti[] = new String[] { 
			"0", "1", "*", "0..*", "1..*" };
	private ExecutorService executor;
	private List<Future<EvalResult>> futures;
	private ExecutorCompletionService<EvalResult> ecs;	
	private MyEvaluatorCallable cb;

	private List<MVMAssocWizard> lAssocsWizard;
	private Color colorSoftGray;

	private JInternalFrame[] allframes;

	static List<MVMAction> lActions = new ArrayList<MVMAction>();
	private String strLastFile="";

	public Map<MVMObject, Map<MClassInvariant, Boolean>> mapObjects;

	public WizardMVMView thisMVMView=this;

	private boolean fEnableEvalTree = false;
	private EvalContext fEvalContext;
	protected Expression fRangeExp;
	protected VarDeclList fElemVarDecls;
	protected Expression fQueryExp;

	private LayoutThread fLayoutThread;

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

	public void putSession(Session session) {
		fSession = session;
		fSystem = session.system();
		fSystem.registerRequiresAllDerivedValues();
		fSystem.getEventBus().register(this);
	}
	public WizardMVMView(MainWindow parent, Session session, PrintWriter logWriter) {
		super(new BorderLayout());
		lActions = new ArrayList<MVMAction>();
		fMainWindow = parent;
		fSession = session;
		fSystem = session.system();
		fSystem.registerRequiresAllDerivedValues();
		fSystem.getEventBus().register(this);
		fLogWriter=logWriter;
		colorSoftGray=new Color(218,224,224);
		executor = Executors.newFixedThreadPool(Options.EVAL_NUMTHREADS);

		futures = new ArrayList<Future<EvalResult>>();
		ecs = new ExecutorCompletionService<EvalResult>(executor);

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

		separator0.setOrientation(SwingConstants.HORIZONTAL);
		separator0.setBounds(100, 12, 486, 10);
		panel.add(separator0);

		lbTitleElements = new JLabel("Elements");
		lbTitleElements.setBounds(10, 0, 100, 25);
		Font titleFont = new Font(lbTitleElements.getFont().getName(), Font.BOLD, 18);
		lbTitleElements.setFont(titleFont);
		panel.add(lbTitleElements);

		lbClass = new JLabel("Classes");
		Font boldFont = new Font(lbClass.getFont().getName(), Font.BOLD, 11);
		lbClass.setBounds(10, 18, 60, 25);
		lbClass.setFont(boldFont);
		panel.add(lbClass);

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(107, 18, 60, 25);
		lbObjects.setFont(boldFont);
		panel.add(lbObjects);	   

		lbAttrs = new JLabel("Attributes");
		lbAttrs.setBounds(205, 18, 60, 25);
		lbAttrs.setFont(boldFont);
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
						lClass.setSelectedIndex(index);
						nomClass = oClass.name();
						lObjects.setModel(loadListObjects(nomClass));
						if (oClass.isAbstract()) {
							nomObj="";
							btnSaveObject.setEnabled(false);
							btnCreateObject.setEnabled(false);
							btnNewObjectAuto.setEnabled(false);
						}else {
							lObjects.setSelectedIndex(0);
							nomObj = (String) lObjects.getSelectedValue();
							selectObject( nomObj);
							btnSaveObject.setEnabled(true);
							btnCreateObject.setEnabled(true);
							btnNewObjectAuto.setEnabled(true);
						}
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
		Font newFont = new Font(btnNewObjectAuto.getFont().getName(), Font.BOLD, 10);
		btnNewObjectAuto.setFont(newFont);
		btnNewObjectAuto.setBounds(107, 160, 40, 25);
		btnNewObjectAuto.setToolTipText("add an object of selected class");

		btnNewObjectAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newObjectAuto();
				iniObjDiagramAssoc();
			}
		});
		panel.add(btnNewObjectAuto);
		// masmas
		btnNewObjectSampleAuto = new JButton("Fill");
		btnNewObjectSampleAuto.setBounds(150, 160, 46, 25);
		btnNewObjectSampleAuto.setToolTipText("add an object of each class");

		btnNewObjectSampleAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newObjectSampleAuto();
				iniObjDiagramAssoc();
			}
		});
		panel.add(btnNewObjectSampleAuto);

		fTableModel = new TableModel();
		fTable = new ExtendedJTable(fTableModel);
		fTable.setPreferredScrollableViewportSize(new Dimension(250, 70));

		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTablePane = new JScrollPane(fTable);
		fTablePane.setBounds(205, 40, 185, 80);

		chkAutoLayout=new JCheckBox("Auto Layout");
		chkAutoLayout.setBounds(205, 160, 90, 25);
		chkAutoLayout.setSelected(true);
		chkAutoLayout.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				iniObjDiagramAssoc();

				if (odvAssoc!=null) {
					if (e.getStateChange()==1) {
						if (fLayoutThread!=null) {
							boolean isDoLayout = fLayoutThread.isAlive();
							if (!isDoLayout) {
								fLayoutThread= odvAssoc.forceStartLayoutThread();
							}
						}else {
							fLayoutThread= odvAssoc.forceStartLayoutThread();
						}


					}else {
						odvAssoc.forceStopLayoutThread(fLayoutThread);
					}
				}
			}
		});
		panel.add(chkAutoLayout);	

		selectObject(nomObj);

		lbObj = new JLabel("Object");
		lbObj.setBounds(400, 18, 160, 25);
		lbObj.setFont(boldFont);
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

		btnCreateObject = new JButton("New Obj");
		btnCreateObject.setBounds(400, 70, 95, 25);
		btnCreateObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initNewObject();
				iniObjDiagramAssoc();//aqui
				if (chkAutoLayout.isSelected()) {
					if (odvAssoc!=null) {
						if (fLayoutThread!=null) {
							boolean isDoLayout = fLayoutThread.isAlive();
							if (!isDoLayout) {
								fLayoutThread= odvAssoc.forceStartLayoutThread();
							}
						}else {
							fLayoutThread= odvAssoc.forceStartLayoutThread();
						}
					}
				}
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

		if (oClass.isAbstract()) {
			btnSaveObject.setEnabled(false);
			btnCreateObject.setEnabled(false);
			btnNewObjectAuto.setEnabled(false);
		}else {
			btnSaveObject.setEnabled(true);
			btnCreateObject.setEnabled(true);
			btnNewObjectAuto.setEnabled(true);
		}

		btnCancelObject = new JButton("Cancel Obj");
		btnCancelObject.setBounds(301, 122, 90, 25);

		btnCancelObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelObject(nomObj);
			}
		});
		panel.add(btnCancelObject);

		btnDeleteObject = new JButton("Delete Obj");
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

		separator1.setOrientation(SwingConstants.HORIZONTAL);
		separator1.setBounds(127, 195, 459, 10);
		panel.add(separator1);

		lbTitleAssociations = new JLabel("Associations");
		lbTitleAssociations.setBounds(10, 183, 200, 25);
		lbTitleAssociations.setFont(titleFont);
		panel.add(lbTitleAssociations);

		lbFrom = new JLabel("From");

		lbFrom.setBounds(205, 192, 160, 25);
		lbFrom.setFont(boldFont);
		panel.add(lbFrom);

		lbTo = new JLabel("To");
		lbTo.setBounds(335, 192, 160, 25);
		lbTo.setFont(boldFont);
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
		btnRefreshComponents.setBounds(301, 160, 90, 25);
		btnRefreshComponents.setVerticalAlignment(SwingConstants.CENTER);
		btnRefreshComponents.setHorizontalAlignment(SwingConstants.CENTER);
		btnRefreshComponents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshComponents();
			}
		});
		panel.add(btnRefreshComponents);

		separator2.setOrientation(SwingConstants.HORIZONTAL);
		separator2.setBounds(84, 375, 502, 10);
		panel.add(separator2);

		lbTitleActions = new JLabel("Actions");
		lbTitleActions.setBounds(10, 360, 400, 25);
		lbTitleActions.setFont(titleFont);
		panel.add(lbTitleActions);

		btnActions = new JButton("Actions");
		btnActions.setBounds(10, 384, 90, 60);
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
				w.setResizable(false);
				w.setVisible(true);	

				strLastFile=w.getLastFile();

				List<MVMAction> lActionsRes=w.getListActions();
				if (lActionsRes!=null) {
					doActions(lActionsRes);
				}
			}
		});
		panel.add(btnActions);

		btnReset = new JButton("Reset");

		btnReset.setBounds(400, 160, 95, 25);		
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
		lbAclass.setFont(boldFont);
		panel.add(lbAclass);

		lbAobject = new JLabel("Object");
		lbAobject.setBounds(135, 245, 100, 25);
		lbAobject.setFont(boldFont);
		panel.add(lbAobject);	

		lbAmultiplicity = new JLabel("Multiplicity");
		lbAmultiplicity.setBounds(135, 275, 100, 25);
		lbAmultiplicity.setFont(boldFont);
		panel.add(lbAmultiplicity);

		lbArole = new JLabel("Role");
		lbArole.setBounds(135, 305, 100, 25);
		lbArole.setFont(boldFont);
		panel.add(lbArole);

		cmbClassOri = new JComboBox<MClass>();

		cmbClassOri.setModel(loadComboClass());
		cmbClassOri.setBounds(205, 215, 120, 25);
		cmbClassOri.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
			}
		});

		cmbClassOri.setEnabled(true);
		cmbClassOri.setVisible(true);
		panel.add(cmbClassOri);

		Border blackline = BorderFactory.createLineBorder(Color.black);

		cmbClassDes = new JComboBox<MClass>();
		cmbClassDes.setModel(loadComboClass());
		cmbClassDes.setBounds(335, 215, 120, 25);
		cmbClassDes.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));
			}
		});

		cmbClassDes.setEnabled(true);
		cmbClassDes.setVisible(true);
		panel.add(cmbClassDes);

		cmbObjectOri = new JComboBox<MObject>();
		cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
		cmbObjectOri.setBounds(205, 245, 120, 25);

		cmbObjectOri.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MObject oSel = (MObject) cmbObjectOri.getSelectedItem();
				MObject oRel = findAssocEnd(oSel);
				if (oRel!=null) {
					cmbObjectDes.setSelectedItem(oRel);
					//				}else {
					//					//					System.out.println("["+oSel.name()+"] No tiene extremo");
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
				MObject oRel = findAssocEnd(oSel);
				if (oRel!=null) {
					cmbObjectDes.setSelectedItem(oRel);
					//				}else {
					//					//					System.out.println("["+oSel.name()+"] No tiene extremo");
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
				if (resp==0) {
					deleteLink(oAssoc) ;
				}

			}
		});

		panel.add(btnDeleteLink);

		lbClassInvariants = new JLabel("State invariants");
		lbClassInvariants.setBounds(205, 385, 120, 25);
		panel.add(lbClassInvariants);

		lbResClassInvariants = new JLabel("");
		lbResClassInvariants.setBounds(325, 385, 120, 25);
		lbResClassInvariants.setBorder(blackline);
		lbResClassInvariants.setVerticalAlignment(SwingConstants.CENTER);
		lbResClassInvariants.setHorizontalAlignment(SwingConstants.CENTER);
		lbResClassInvariants.setFont(new Font("Serif", Font.BOLD, 18));
		lbResClassInvariants.setVisible(false);
		panel.add(lbResClassInvariants);

		btnShowClassInvariants = new JButton("");// Before empty 'OK'
		btnShowClassInvariants.setBounds(295, 385, 90, 25);//65
		btnShowClassInvariants.setVerticalAlignment(SwingConstants.CENTER);
		btnShowClassInvariants.setHorizontalAlignment(SwingConstants.CENTER);
		btnShowClassInvariants.setFont(new Font("Serif", Font.BOLD, 13));//18
		btnShowClassInvariants.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showClassInvariantsState();
			}
		});
		panel.add(btnShowClassInvariants);

		btnShowIndividuals = new JButton("OBJs");
		btnShowIndividuals.setBounds(385, 385, 70, 25);
		btnShowIndividuals.setVerticalAlignment(SwingConstants.CENTER);
		btnShowIndividuals.setHorizontalAlignment(SwingConstants.CENTER);
		btnShowIndividuals.setFont(new Font("Serif", Font.BOLD, 13));
		btnShowIndividuals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_inv_state_individual();

				boolean existDiagram=false;
				boolean existWizard=false; 
				//		Ver frames
				JDesktopPane fDesk = fMainWindow.getFdesk();
				JInternalFrame[] allframes = fDesk.getAllFrames();
				for (JInternalFrame ifr: allframes) {
					if (ifr.getName()==null&&ifr.getTitle().equals("Object diagram")){
						ifr.setName(NAMEFRAMEMVMDIAGRAM);
					}
					if (ifr.getName()!=null){
						if (ifr.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
							existDiagram=true;
						}
						if (ifr.getName().equals(NAMEFRAMEMVMWIZARD)) {
							existWizard=true;
						}	
					}
				}
				if (!existWizard) {
					fMainWindow.showMVMWizard(NAMEFRAMEMVMWIZARD);
				}
				//--- aqui0
				searchObjDiagramAssociated();
				//--
				if (!existDiagram) {
					createObjDiagram();
				}
			}

		});
		panel.add(btnShowIndividuals);

		lbCheckStructure = new JLabel("Multiplicities");
		lbCheckStructure.setBounds(205, 420, 120, 25);
		panel.add(lbCheckStructure);

		btnShowCheckStructure = new JButton("");// Before empty 'OK'
		btnShowCheckStructure.setBounds(300, 420, 155, 25);
		btnShowCheckStructure.setVerticalAlignment(SwingConstants.CENTER);
		btnShowCheckStructure.setHorizontalAlignment(SwingConstants.CENTER);
		btnShowCheckStructure.setFont(new Font("Serif", Font.BOLD, 13));
		btnShowCheckStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean ok=checkStructure();
				if (!ok) {
					MVMWizardAssoc dW= new MVMWizardAssoc(frame,lAssocsWizard);
					dW.setSize(910,490);
					dW.setLocationRelativeTo(null);
					dW.setResizable(false);
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

		add(panel);

		setSize(new Dimension(400, 300));

		// Reload existing components and actions to recreate them
		refreshComponents();
		setResClassInvariants();
		setResCheckStructure();

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

		lActions.clear();
	}

	public Map<MVMObject, Map<MClassInvariant, Boolean>> getMapObjects(List<MVMAction> pLactions) {
		if (pLactions!=null) {
			doActions(pLactions);
			lActions=pLactions;
		}
		TreeMap<MVMObject, Map<MClassInvariant, Boolean>> mapaOrdenado = new TreeMap<>(mapObjects);
		return mapaOrdenado;
	}
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

		// Rebuild the action list
		// Array of previously existing objects
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
		// Initializes current objs with the current objects
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
			// Check if it is an existing object
			boolean exist=false;
			for (int nf=0;nf<nFilasObj;nf++) {
				String strObjActual=objsActual[nf][0];
				//				if ((objsActual[nf][0]).equals(strObjCmp)) {
				if (strObjActual.equals(strObjCmp)) {
					objsActual[nf][1]="1";
					// See if the attribute values are the same or have changed
					// System attributes
					MObjectState objState = oObj.state(fSystem.state());
					Map<MAttribute, Value> attrsMap = objState.attributeValueMap();
					Collection<MAttribute> attributes = ModelBrowserSorting.getInstance().sortAttributes( attrsMap.keySet() );
					List<MAttribute> attrs = Lists.newArrayList(attributes);
					String[]  valuesAttrs = new String[attrs.size()];
					for (int i = 0; i < valuesAttrs.length; i++) {
						valuesAttrs[i] = attrsMap.get(attrs.get(i)).toString();
					}
					// Attributes of the MVM object
					List<MVMAttribute> lMVMAttrs = new ArrayList<MVMAttribute>();
					for (MVMObject oMVMObj: lMVMObjs) {
						if(oMVMObj.getName().equals(oObj.name())&&oMVMObj.getClassName().equals(oObj.cls().name())) {
							lMVMAttrs = oMVMObj.getAttributes();
						}
					}
					// Compare attributes with each other and if there is a difference, save in storeaction MA
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
								}
							}
						}
					}

					exist=true;
					break;
				}
			}
			if (!exist) {
				// If it does not exist, we save it to create later
				lObjsACrear.add(oObj);
			}
		}
		// We see if there is any object that no longer exists and we delete it
		for (int nf=0;nf<nFilasObj;nf++) {
			if ((objsActual[nf][1]).equals("0")) {
				String[] partes = objsActual[nf][0].split(":");
				String objClass=partes[0];
				String objName=partes[1];
				storeAction("DO", "Delete object ["+objName+"] of ["+objClass+"]");	
			}
		}
		// We see if there is any object that needs to be created
		for (MObject oObj: lObjsACrear) {
			String objClass=oObj.cls().name();
			String objName=oObj.name();
			storeAction("CO", "Creation object ["+objName+"] of ["+objClass+"]");
		}

		//-- Link's

		String[][] linksActual = new String[nFilasLink][nColumnasLink];
		// Initializes currents objs with the current objects
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
				// If it does not exist, we save it to create later
				lLinksACrear.add(oLink);
			}
		}
		// We see if there is any link that no longer exists and we delete it
		for (int nf=0;nf<nFilasLink;nf++) {
			if ((linksActual[nf][1]).equals("0")) {
				String strActual = linksActual[nf][0];
				String[] partes = strActual.split(":");
				String strAsocc=partes[0];
				String obj1=partes[2];
				String obj2=partes[4];
				storeAction("DL", "Delete link ["+strAsocc+"] - ["+obj1+"]/["+obj2+"]");
			}
		}
		// We see if there is any object that needs to be created
		for (MLink oLink: lLinksACrear) {
			String obj1 = oLink.getLinkEnd(0).object().name();
			String obj2 = oLink.getLinkEnd(1).object().name();
			MObject o1 = findObjectByName(obj1);
			MObject o2 = findObjectByName(obj2);
			MObject oOri=null;
			MObject oDes=null;

			// Determine order of oOri and oDes
			List<MAssociationEnd> oAsocEnds = oAssoc.associationEnds();

			int na=0;
			MAssociationEnd oAssocEnd = oAsocEnds.get(na);
			MClass oClassAssocEnd = oAssocEnd.cls();
			if (oClassAssocEnd.name().equals(o1.cls().name())) {
				oOri = o1;
				oDes = o2;	
			}else {
				oOri = o2;
				oDes = o1;	
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

		// Obtains collection of existing objects after the action
		List<MVMObject> lObjs = getMVMObjects();
		oAction.setlObjs(lObjs);

		// Obtains collection of existing links after the action
		List<MVMLink> lLinks = getMVMLinks();
		oAction.setlLinks(lLinks);

		// Counts the existing actions in the list and increments one unit
		oAction.setOrden(lActions.size()+1);
		// Insert action into action list
		lActions.add(oAction);
	}

	private void createObjectAccordingMVMObject(MVMObject oObj, boolean verbose) {
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
				if (verbose) {
					JOptionPane.showMessageDialog(fMainWindow,errorOutput,"Error",JOptionPane.ERROR_MESSAGE);
				}

				continue;
			}

			try {
				fSystem.execute(
						new MAttributeAssignmentStatement(fObject,attribute,valueAsExpression));

			} catch (MSystemException e) {
				if (verbose) {
					JOptionPane.showMessageDialog(fMainWindow,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}

			}
		}					
	}

	/**
	 * Do Actions (create objects & links)
	 * @param lActionsRes
	 */

	private void doActions(List<MVMAction> lActionsRes) {
		resetObjLinks();
		int nActions = lActionsRes.size();
		// In reality, the actions to be performed are the creation of objects and links
		if (nActions>0) {
			MVMAction oAction=lActionsRes.get(nActions-1);	
			List<MVMObject> lObjs=oAction.getlObjs();
			for (MVMObject oObj: lObjs) {
				String nomObj=oObj.getName();
				String ClassObj = oObj.getClassName();
				createObjectAccordingMVMObject(oObj, true);

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
					oDes = o2;	
				}else {
					oOri = o2;
					oDes = o1;	
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
			// After loading everything, the first class and the first object of said class must be selected

			if (lClass.getModel().getSize()>0) {
				oClass = lClass.getModel().getElementAt(0);
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
	 * Perform the actions proposed in the wizard
	 * @param oAssocPralWizard
	 * @param commandWizard
	 */
	private void doActionsWizardAssoc(MAssociation oAssocPralWizard, String commandWizard) {
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
					// Initialize object
					saveObject(oClassCreate, nomObjNew);
					cmbObjectOri.setModel(loadComboObjectMObject(cmbClassOri));
					cmbObjectDes.setModel(loadComboObjectMObject(cmbClassDes));

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
					// The created objects must be recovered and replaced (NEWS) by said objects
					// The objects are in lNewObjects
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

					MObject oOri=null;
					MObject oDes=null;
					MObject o1 = findObjectByName(objPral);
					MObject o2 = findObjectByName(objLinkar);
					// Find out the correct order for oOri i oDes according to oAssocPralWizard
					List<MAssociationEnd> oAsocEnds = oAssocPralWizard.associationEnds();

					int na=0;
					MAssociationEnd oAssocEnd = oAsocEnds.get(na);
					MClass oClassAssocEnd = oAssocEnd.cls();

					if (oClassAssocEnd.name().equals(o1.cls().name())||o1.cls().isSubClassOf(oClassAssocEnd)) {
						oOri = o1;
						oDes = o2;	
					}else {
						oOri = o2;
						oDes = o1;	
					}

					MObject[] fParticipants = new MObject[] {oOri,oDes};
					try {
						insertLink(oAssocPralWizard, fParticipants);
						fLogWriter.println("Insert link between ["+oOri.name()+"] y [" + oDes.name()+"]");
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

		// Find out if object exists or not based on an iteration

		while(existObject(nomProposed,className )) {
			numObj+=1;
			nomProposed = className.toLowerCase() + numObj; 
		}

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
	public void newObjectSampleAuto() {
		int nElems=lClass.getModel().getSize();
		for(int nElem=0;nElem<nElems;nElem++) {
			lClass.setSelectedIndex(nElem);
			newObjectAuto();
		}
		oClass = lClass.getSelectedValue();
		if (oClass!=null) {
			if (oClass.isAbstract()) {
				nomObj="";
				btnSaveObject.setEnabled(false);
				btnCreateObject.setEnabled(false);
				btnNewObjectAuto.setEnabled(false);
			}else {
				lObjects.setSelectedIndex(0);
				nomObj = (String) lObjects.getSelectedValue();
				selectObject( nomObj);
				btnSaveObject.setEnabled(true);
				btnCreateObject.setEnabled(true);
				btnNewObjectAuto.setEnabled(true);
			}
		}
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
		String sRes="Correct";
		if (!bRes) sRes="Incorrect";
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
		String sRes="Correct";
		if (!bRes) sRes="Incorrect";
		lbResClassInvariants.setText(sRes);
		btnShowClassInvariants.setText(sRes);
		if(!bRes) {
			lbResClassInvariants.setForeground(Color.white);
			lbResClassInvariants.setBackground(Color.red);
			btnShowClassInvariants.setForeground(Color.white);
			btnShowClassInvariants.setBackground(Color.red);
			btnShowIndividuals.setForeground(Color.white);
			btnShowIndividuals.setBackground(Color.red);
		}else {
			lbResClassInvariants.setForeground(Color.black);
			lbResClassInvariants.setBackground(Color.green);
			btnShowClassInvariants.setForeground(Color.black);
			btnShowClassInvariants.setBackground(Color.green);
			btnShowIndividuals.setForeground(Color.black);
			btnShowIndividuals.setBackground(Color.green);
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
		mapObjects = new HashMap<>();

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

		executor = Executors.newFixedThreadPool(Options.EVAL_NUMTHREADS);
		ecs = new ExecutorCompletionService<EvalResult>(executor);

		futures.clear();

		for (int i = 0; i < fClassInvariants.length; i++) {
			if(!fClassInvariants[i].isActive()){
				continue;
			}
			cb = new MyEvaluatorCallable(fSystem.state(), i, fClassInvariants[i]);
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

		for (EvalResult res : fValues) {

			Boolean boolRes= false;
			try {
				if(!res.result.equals(null)) {
					boolRes=  ((BooleanValue)res.result).value();
				}
			}catch (Exception e) {
				boolRes= false;
			}

			if (boolRes.equals(false)) todosOk=false;

		}

		executor.shutdown();

		return todosOk;
	}
	// Analyze every object in individual way

	public void check_inv_state_individual() {

		TreeMap<MVMObject, Map<MClassInvariant, Boolean>> mapaOrdenado = new TreeMap<>(mapObjects);

		thisMVMView=this;
		List<MVMAction> lActionsCheck=lActions;

		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			if (odv.getName()!=null) {
				if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
					fMainWindow.getObjectDiagrams().remove(odv);
					iniObjDiagramAssoc();
					break;
				}
			}
		}
		//		
		MVMObjCheckState w = new MVMObjCheckState(thisMVMView,mapaOrdenado, fSession, lActionsCheck);
		w.setSize(1038, 820);
		w.setLocationRelativeTo(null);
		w.setResizable(false);
		w.setVisible(true);
		refreshComponents();

	}

	/**
	 * Gets information with structure check errors
	 */
	private void getErrorsEstructure() {
		lAssocsWizard = new ArrayList<MVMAssocWizard>();
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
		Map<String, List<String>> mapObjectsStr = new HashMap<String, List<String>>();
		getErrorsEstructure(); //Provis
		// In one pass the objects that admit a multiplicity of * must be seen so that they are available
		// You have to see all the final associations that there are and see which objects are always available

		for(MVMAssocWizard oAssoc: lAssocsWizard) {
			MAssociation oAssocModel = oAssoc.getassocModel();
			List<MAssociationEnd> oAsoccEnds = oAssocModel.associationEnds();

			MAssociationEnd oAssocEnd1 = oAsoccEnds.get(0);
			MAssociationEnd oAssocEnd2 = oAsoccEnds.get(1);
			// If the first has multi *, the objects of the second must be available		
			if (oAssocEnd1.multiplicity().getRanges().get(0).toString().equals("*")) {
				// We look for objects of the second
				MClass oClassBuscar = oAssocEnd2.cls();
				mapObjectsStr=addAndFindObjectsIntoMap(mapObjectsStr, oClassBuscar);
			}
			// If the second has multi *, the objects of the first must be available			
			if (oAssocEnd2.multiplicity().getRanges().get(0).toString().equals("*")) {
				// We look for objects of the first
				MClass oClassBuscar = oAssocEnd1.cls();
				mapObjectsStr=addAndFindObjectsIntoMap(mapObjectsStr, oClassBuscar);
			}
		}

		List<MVMAssocWizard> lAssocsWizardPaso = new ArrayList<MVMAssocWizard>();
		for(MVMAssocWizard oAssoc: lAssocsWizard) {
			lAssocsWizardPaso.add(oAssoc);
		}
		lAssocsWizard.clear();
		for (MVMAssocWizard aw: lAssocsWizardPaso) {
			for (LinkWizard lw: aw.getlLinks()) {
				int needed = lw.getNeeded();
				int disponibility = lw.getDisponibility();
				String objectName = lw.getObject();
				String nomClass = lw.getNomClass(); //Main object class
				// Swipe to view available items by class

				if (needed>0 || disponibility>0) {
					List<String> lObjDisponibles = new ArrayList<String>();
					// If it have any need or availability, it is that it can link with other objects according to the relationship.
					if (mapObjectsStr.containsKey(nomClass)) {
						lObjDisponibles=mapObjectsStr.get(nomClass);
						if (!lObjDisponibles.contains(objectName)) {
							lObjDisponibles.add(objectName);
						}
					}else{
						lObjDisponibles.add(objectName);
						mapObjectsStr.put(nomClass, lObjDisponibles);
					}
				}
			}

			// Analysis of problems to solve
			analyzeProposals(aw, mapObjectsStr);
		}
		boolean ok = true;
		if (mapObjectsStr.size()>0) ok=false;
		return ok;
	}

	/**
	 * Create map with objects available for each class
	 * @param mapObjects
	 * @param oClassBuscar
	 * @return
	 */
	private Map<String, List<String>> addAndFindObjectsIntoMap(Map<String, List<String>> mapObjectsStr, 
			MClass oClassBuscar){
		MSystemState state = fSystem.state();
		Set<MObject> allObjects = state.allObjects();
		for (MObject obj : allObjects) {
			String objectName = obj.name();
			String strClassName = oClassBuscar.name();
			if (obj.cls().name().equals(strClassName)) {
				List<String> lObjDisponibles = new ArrayList<String>();

				if (mapObjectsStr.containsKey(strClassName)) {
					lObjDisponibles=mapObjectsStr.get(strClassName);
					if (!lObjDisponibles.contains(objectName)) {
						lObjDisponibles.add(objectName);
						mapObjectsStr.replace(strClassName, lObjDisponibles);
					}
				}else{
					lObjDisponibles.add(objectName);
					mapObjectsStr.put(strClassName, lObjDisponibles);
				}
			}
		}
		return mapObjectsStr;
	}
	/** 
	 * Based on the structure of an association object, it proposes to create and/or link said object to others
	 * 
	 */
	public void analyzeProposals(MVMAssocWizard aw,Map<String, List<String>> mapObjects) {

		MVMAssocWizard awNew = aw;
		List<LinkWizard> oLinks = aw.getlLinks();
		List<LinkWizard> oNewLinks = new ArrayList<LinkWizard>();

		for (LinkWizard lw: oLinks) {
			List<String> lObjAsignar = new ArrayList<String>();
			int needed = lw.getNeeded();
			int cover = 0;
			int pending=0;
			int canAssig = 0;
			int mustCreate = 0;
			String objectNameToSolve = lw.getObject();//Object to solve
			String nomClass = lw.getNomClass(); //Main object class
			String classNeeded = lw.getOfClass(); // Class of the object needed
			// Find how many necessary objects are available

			String strAssig="";
			for (Map.Entry<String, List<String>> entry : mapObjects.entrySet()) {
				String className = (String) entry.getKey();
				if (className.equals(classNeeded)){
					List<String> lObjDisponibles = new ArrayList<String>();
					lObjDisponibles = entry.getValue();

					for(String nameObject: lObjDisponibles) {
						pending = needed - cover;
						if (pending > 0 ) {
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
				mapActions.put("A", strAssig);
				linkAction=objectNameToSolve+":"+strAssig;
			}

			// If there is any object pending to be assigned, we must create it
			if (mustCreate > 0 ) {
				mapActions.put("C-"+mustCreate, classNeeded);
				// Consider calculating the names of new objects to change NEWS to those names
				if (linkAction!="") {
					linkAction+=",(NEWS)";
				}else {
					linkAction=objectNameToSolve+":(NEWS)";
				}
			}
			if (linkAction!="") {
				mapActions.put("I", linkAction);
			}
			//We order mapActions so that the (C)reation occurs before the (I)nsertion

			Map<String, String> sortedMap = new TreeMap<>(mapActions);
			lw.setMapActions(sortedMap);			
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
		// Save list of objects before loading to compare them later and if any are missing, create them
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
		// Get model elements and sort them
		Vector<String> data = new Vector<>(ldefLModel.size());
		for (int i = 0; i < ldefLModel.size(); i++) {
			data.add(ldefLModel.getElementAt(i));
		}
		Collections.sort(data);

		// Clean the model and add ordered elements
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
		// Get model elements and sort them
		Vector<MClass> data = new Vector<>(ldefLModel.size());
		for (int i = 0; i < ldefLModel.size(); i++) {
			data.add(ldefLModel.getElementAt(i));
		}
		Collections.sort(data);

		// Clean the model and add ordered elements
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
			// Compare by value
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
			if (!oClass.isAbstract()) {
				cbm.addElement(oClass);
			}
		}

		List<MClass> data = new ArrayList<>();
		for (int i = 0; i < cbm.getSize(); i++) {
			data.add((MClass) cbm.getElementAt(i));
		}

		// Sort the elements
		Collections.sort(data, new MClassComparator());

		// Clean the ComboBoxModel and add ordered elements
		cbm.removeAllElements();
		for (MClass element : data) {
			cbm.addElement(element);
		}

		return cbm;
	}

	/**
	 * Implements MClass comparator to classify
	 * @author utopick
	 *
	 */
	class MClassComparator implements Comparator<MClass> {
		@Override
		public int compare(MClass o1, MClass o2) {
			Comparator<String> stringComparator = Comparator.naturalOrder();
			// Compare by value
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

		// Sort the elements
		Collections.sort(data, new MObjectComparator());

		// Clear the model and add sorted elements
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
		// If there are no links, the combos must be initialized based on the AssociationEnds
		int nLink=0;
		if (links.size()==0) {
			for (MAssociationEnd ma:oAssoc.associationEnds()) {
				String className=ma.cls().name();
				switch(nLink){
				case 0:
					selectClassInCombo(cmbClassOri,className);
				case 1:
					selectClassInCombo(cmbClassDes,className);
				default:
					// Do nothing
					break;
				}
				nLink+=1;
			}
			return;
		}

		// If there are links, the links are analyzed and the first one is displayed
		// Initially we will assume that there is only one link

		for (MLink oLink:links) {
			if (nLink==0) {
				MObject oOri=null;
				MObject oDes=null;

				int nLinkEnd=0;
				for(MLinkEnd oMlinkEnd: oLink.linkEnds()) {
					MAssociationEnd oMAssociationEnd=oMlinkEnd.associationEnd();
					MMultiplicity oMMultiplicity=oMAssociationEnd.multiplicity();

					switch(nLinkEnd){
					case 0:
						oOri=oMlinkEnd.object();
						txOriAssocRole.setText(oMAssociationEnd.name());
						cmbClassOri.setSelectedItem(oOri.cls());
						cmbObjectOri.setSelectedItem(oOri);
						cmbMultiOri.setSelectedItem(oMMultiplicity.toString());
						txMultiOri.setText(oMMultiplicity.toString());
						break;
					case 1:
						oDes=oMlinkEnd.object();
						txDesAssocRole.setText(oMAssociationEnd.name());
						cmbClassDes.setSelectedItem(oDes.cls());
						cmbObjectDes.setSelectedItem(oDes);
						cmbMultiDes.setSelectedItem(oMMultiplicity.toString());
						txMultiDes.setText(oMMultiplicity.toString());
						break;
					default:
						// Do nothing
						break;
					}
					nLinkEnd+=1;
				}

			}
		}
	}
	/**
	 * Insert link in association
	 * @param oAssoc
	 */
	private void insertLink(MAssociation oAssoc,MObject o1, MObject o2) {
		MObject oOri=null;
		MObject oDes=null;

		// Determine order of oOri and oDes
		List<MAssociationEnd> oAsocEnds = oAssoc.associationEnds();

		int na=0;
		MAssociationEnd oAssocEnd = oAsocEnds.get(na);
		MClass oClassAssocEnd = oAssocEnd.cls();

		if (oClassAssocEnd.name().equals(o1.cls().name())||o1.cls().isSubClassOf(oClassAssocEnd)) {
			oOri = o1;
			oDes = o2;	
		}else {
			oOri = o2;
			oDes = o1;	
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
		// Find out the link it is about
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
		// Initially we will assume that there is only one link

		for (MLink oLink:links) {
			//If it is the selected objects in the UI it is the link searched

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
		// Locate diagram and see if it can be updated
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
					if (fLayoutThread==null) {
						fMainWindow.getObjectDiagrams().remove(odv);	
					}
					iniObjDiagramAssoc();
					return;
				}
			}
		}
	}

	/**
	 * Create object diagram
	 */
	public void createObjDiagram() {
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
		iniObjDiagramAssoc();
		fLayoutThread=odvAssoc.forceStartLayoutThread();
		fLayoutThread=odvAssoc.getFDoAutoLayout();

		chkAutoLayout.setSelected(true);

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
							fMainWindow.getObjectDiagrams().remove(newObj);
							iniObjDiagramAssoc();
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
	private void iniObjDiagramAssoc() {
		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {

			if (odv.getName()!=null) {
				if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
					odvAssoc = odv.getDiagram();
					fLayoutThread=odvAssoc.getFDoAutoLayout();
					break;
				}
			}
		}
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
	}

	/**
	 * Save an object
	 * @param oClass
	 * @param nomObj
	 */
	private void saveObject(MClass oClass, String nomObj) {
		// If the class is abstract we do nothing
		if (oClass.isAbstract()) return;
		// We verify existence of ObjectDiagram MVM
		checkExistObjDiagram();

		if (bNewObj) {
			// Make copy of fValues as long as the class is the same

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
					}if (attr.type().isTypeOfBoolean()) {
						fValues[i] = "true";
					}if (attr.type().isTypeOfReal()) {
						fValues[i] = "1.0";
					}
				}
			}
		}else {
			// Do nothing
		}
		applyChanges();

		if (!checkExistObjDiagram()) {
			fLayoutThread=odvAssoc.forceStartLayoutThread();
		}else {
			iniObjDiagramAssoc();
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

		boolean existDiagram=false;

		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			// If it is null, it is given NAMEFRAMEMVMDIAGRAM

			if (odv.getDiagram().getName()==null) {
				odv.setName(NAMEFRAMEMVMDIAGRAM);
			}
			if (odv.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
				existDiagram=true;

				break;
			}
		}

		if(!existDiagram && allframes!=null) {
			for (JInternalFrame ifr: allframes) {
				if (ifr.getName()==null&&ifr.getTitle().equals("Object diagram")){
					ifr.setName(NAMEFRAMEMVMDIAGRAM);
				}
				if (ifr.getName()!=null){
					if (ifr.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
						existDiagram=true;

						ifr.dispose();
						createObjDiagram();
					}
				}
			}
		}

		// Locate diagram and see if it can be updated
		for (NewObjectDiagramView odv: fMainWindow.getObjectDiagrams()) {
			// If it is null, it is given NAMEFRAMEMVMDIAGRAM

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

		lObjects.setModel(loadListObjects(nomClass));

		int nObjects = lObjects.getModel().getSize();
		if (nObjects>0) {
			if (idx>nObjects-1) {
				idx=nObjects-1;
			}
			lObjects.setSelectedIndex(idx);
			nomObj = (String) lObjects.getSelectedValue();
			selectObject( nomObj);
			txNewObject.setText(nomObj);
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

		// Search in list
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

		// Search in list
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
				if (f!=null) {//Provis
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
			}
			y += h; // start the next row
			x = 0;
		}

	}
	public final boolean isCancelled() {
		//		return future.isCancelled();
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

		int index;
		MSystemState state;
		MClassInvariant inv;

		public MyEvaluatorCallable(MSystemState state, int index, MClassInvariant inv) {
			this.state = state;
			this.index = index;
			this.inv = inv;
		}

		@Override
		public EvalResult call() throws Exception {
			if (isCancelled()) return null;

			Value v = null;
			String message = null;
			long start = System.currentTimeMillis();

			try {
				Expression expr = inv.flaggedExpression();

				MSystemState preState=state;
				MSystemState postState=state;
				VarBindings bindings=new VarBindings();

				fEnableEvalTree = true;
				fEvalContext = new SimpleEvalContext(preState, postState, bindings);
				EvalContext ctx=fEvalContext;

				Class<? extends Expression> cl = expr.getClass(); 

				VarDeclList elemVarDecls=null;
				Expression rangeExp=null;
				Expression queryExp=null;
				Expression exp;
				String simpleName = cl.getSimpleName();
				if (simpleName.equals("ExpForAll")) {
					ExpForAll exp2 = (ExpForAll) expr;
					elemVarDecls=exp2.getVariableDeclarations();
					fElemVarDecls=elemVarDecls;
					rangeExp=exp2.getRangeExpression();
					queryExp=exp2.getQueryExpression();
					fQueryExp=queryExp;
					exp = (ExpForAll) expr;
				}else {
					exp = (ExpStdOp) expr;
				}

				fRangeExp=rangeExp;

				v=evalExistsOrForAllMVM(ctx, false,inv);;

			} catch (MultiplicityViolationException e) {
				message = e.getMessage();
			}

			return new EvalResult(index, v, message, System.currentTimeMillis() - start);
		}
	}

	protected Value evalExistsOrForAllMVM(EvalContext ctx, boolean doExists, MClassInvariant inv) {
		Value v = fRangeExp.eval(ctx);
		if (v.isUndefined())
			return UndefinedValue.instance;
		CollectionValue rangeVal = (CollectionValue) v;

		// we need recursion for the permutation of assignments of
		// range values to all element variables.
		boolean res = evalExistsOrForAll0MVM(0, rangeVal, ctx, doExists, inv);
		return BooleanValue.get(res);
	}
	private boolean evalExistsOrForAll0MVM(int nesting,
			CollectionValue rangeVal, EvalContext ctx, boolean doExists, MClassInvariant inv) {
		// loop over range elements
		boolean res = !doExists;

		for (Value elemVal : rangeVal) {
			MVMObject oMVMObj = new MVMObject();
			int nActions = lActions.size();

			if (nActions<1) break;

			MVMAction oAction=lActions.get(nActions-1);	
			List<MVMObject> lObjs=oAction.getlObjs();
			for (MVMObject oObj: lObjs) {
				String nomElemVal=elemVal.toString();
				if (oObj.getName().equals(nomElemVal)) {
					oMVMObj=oObj;
					break;
				}
			}

			// bind element variable to range element, if variable was
			// declared
			if (!fElemVarDecls.isEmpty())
				ctx.pushVarBinding(fElemVarDecls.varDecl(nesting).name(),
						elemVal);

			if (!fElemVarDecls.isEmpty() && nesting < fElemVarDecls.size() - 1) {
				// call recursively to iterate over range while
				// assigning each value to each element variable
				// eventually
				if (res != doExists) {
					res = evalExistsOrForAll0MVM(nesting + 1, rangeVal, ctx,
							doExists, inv);
				}else if (ctx.isEnableEvalTree()) {
					// don't change the result value when expression is true
					// (exists) or
					// false (forAll) and continue iteration
					evalExistsOrForAll0MVM(nesting + 1, rangeVal, ctx, doExists, inv);

				}else {
					if (!fElemVarDecls.isEmpty())
						ctx.popVarBinding();
					break;
				}
			} else {
				// evaluate predicate expression
				Value queryVal = fQueryExp.eval(ctx);
				//				Boolean boolRes = ((BooleanValue)queryVal).value();
				Boolean boolRes = false;
				if (!queryVal.isUndefined()) {
					boolRes = ((BooleanValue)queryVal).value();
				}else {
					boolRes = false;
				}
				// Locate map objects in mapObjects
				Map<MClassInvariant, Boolean> mapInvsObj = new HashMap<>();
				if(mapObjects.containsKey(oMVMObj)) {
					mapInvsObj=mapObjects.get(oMVMObj);
					if(mapInvsObj.containsKey(inv)) {
						mapInvsObj.replace(inv, boolRes);
					}else {
						mapInvsObj.put(inv, boolRes);
					}

					mapObjects.replace(oMVMObj, mapInvsObj);
				}else {
					mapInvsObj.put(inv, boolRes);
					mapObjects.put(oMVMObj, mapInvsObj);
				}

				// undefined query values default to false
				if (queryVal.isUndefined())
					queryVal = BooleanValue.FALSE;

				// don't change the result value when expression is true
				// (exists) or
				// false (forAll) and continue iteration
				if (res != doExists && ((BooleanValue) queryVal).value() == doExists)
					res = doExists;
				else if (!ctx.isEnableEvalTree() &&  ((BooleanValue) queryVal).value() == doExists) {
					if (!fElemVarDecls.isEmpty()) {
						if  (ctx.varBindings().iterator().hasNext()) {
							ctx.popVarBinding();
						}
					}
				}

			}

			if (!fElemVarDecls.isEmpty()) {
				if  (ctx.varBindings().iterator().hasNext()) {
					ctx.popVarBinding();
				}
			}
		}

		return res;
	}

}

