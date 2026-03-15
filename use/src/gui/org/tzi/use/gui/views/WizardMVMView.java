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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.tzi.use.gui.mvm.MVMConfigManager;
import org.tzi.use.gui.mvm.MVMLink;
import org.tzi.use.gui.mvm.MVMObjCheckState;
import org.tzi.use.gui.mvm.MVMObject;
import org.tzi.use.gui.mvm.MVMShowResponseOpenAI;
import org.tzi.use.gui.mvm.MVMWizardActions;
import org.tzi.use.gui.mvm.MVMWizardAssoc;
import org.tzi.use.gui.util.ExtendedJTable;
import org.tzi.use.gui.views.diagrams.DiagramView.LayoutThread;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.elements.edges.LinkEdge;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagram;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagramView;
import org.tzi.use.gui.views.diagrams.objectdiagram.QualifierInputView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassImpl;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.ExpForAll;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

	public static List<String> listStrSatisfiables = new ArrayList<String>();
	public static List<String> listStrUnSatisfiables = new ArrayList<String>();

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
	//	private JLabel lbAssoc;
	private JLabel lbObj;
	private JLabel lbFrom;
	private JLabel lbTo;
	//	private JLabel lbFromClass;
	//	private JLabel lbToClass;
	private JLabel lbAclass;	
	private JLabel lbAobject;	
	private JLabel lbAmultiplicity;	
	private JLabel lbArole;	
	private JLabel lbClassInvariants;
	private JLabel lbResClassInvariants;
	private JLabel lbCheckStructure;

	private JScrollPane scrollPaneClass;
	private JScrollPane scrollPaneObj;
	private JScrollPane scrollPaneAssoc;	

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
	private JButton btnViewCmbs;

	private JButton btnActions;
	private JButton btnSuggestions;
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
	private String[] fOriginalValues;
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

	//	private boolean fEnableEvalTree = false;
	private EvalContext fEvalContext;
	protected Expression fRangeExp;
	protected VarDeclList fElemVarDecls;
	protected Expression fQueryExp;


	private LayoutThread fLayoutThread;

	private NewObjectDiagramView odvGral=null;

	private StringBuilder blockForAssocFailOpenAI;
	private StringBuilder blockForInvsFailOpenAI;

	private static String API_KEY;
	private static String API_URL;

	// --- INI New OPenAI
	private static String strDefModel;
	private static String strDefProperties;
	private static String strInvariants;
	private static String strMUS;
	private static String strMSS;
	private static String strObjects;
	private static String strLinks;
	private static String strOriginalInvariants;

	//	private static String LINEASEP = "-----------------------------------------------------------\n";
	private static String LINEASEP = repeat("-", 70)+"\n";
	private static String mensaje;
	private static String jsonPretty;
	private static String jsonResult;

	private static String strCreateObjectsAI="";
	private static String strCreateLinksAI="";
	private static WizardMVMView fWizardMVMView;

	private static String strNameModel= "Animals.use";
	private static String strNameProperties = strNameModel.replace(".use", ".properties");
	private static String urlModel="C:\\Temp";
	private static String pathModel = urlModel+"\\"+strNameModel;
	private static String pathProperties = urlModel+"\\"+strNameProperties;

	// --- FIN New OPenAI
	private boolean showTxtBlocks=true;
	public boolean showTrace=false;

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
			checkForChanges();   // ← NUEVO

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
			fOriginalValues = Arrays.copyOf(fValues, fValues.length);
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
		this.setFocusable(true);
		lActions = new ArrayList<MVMAction>();
		fMainWindow = parent;
		fWizardMVMView=this;
		listStrSatisfiables.clear();
		listStrUnSatisfiables.clear();

		fSession = session;
		fSystem = session.system();

		strNameModel= fSystem.model().name()+".use";
		strNameProperties = strNameModel.replace(".use", ".properties");
		urlModel=fSystem.model().getModelDirectory().getPath();
		pathModel = urlModel+"\\"+strNameModel;
		pathProperties = urlModel+"\\"+strNameProperties;

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

						nomObj="";

						if (oClass.isAbstract()) {
							btnCreateObject.setEnabled(false);
							btnNewObjectAuto.setEnabled(false);
						}else {
							btnCreateObject.setEnabled(true);
							int nObjects = lObjects.getModel().getSize();
							if (nObjects>0) {
								lObjects.setSelectedIndex(0);
								nomObj = (String) lObjects.getSelectedValue();
								selectObject( nomObj);
								btnCreateObject.setEnabled(true);
								btnNewObjectAuto.setEnabled(true);
							}
						}
						lObjects.setSelectedIndex(0);
						nomObj = (String) lObjects.getSelectedValue();
						selectObject( nomObj);
						txNewObject.setText(nomObj);
						txNewObject.setEnabled(false);
					}
					btnSaveObject.setEnabled(false);
					btnCancelObject.setEnabled(false);
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
				btnSaveObject.setEnabled(false);  
				btnCancelObject.setEnabled(false);  
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

		// Fill
		btnNewObjectSampleAuto = new JButton("Fill");
		Font defaultFont = btnNewObjectSampleAuto.getFont();
		Font smallerFont = defaultFont.deriveFont(defaultFont.getSize() * 0.75f);		
		btnNewObjectSampleAuto.setFont(smallerFont);
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

		btnCreateObject = new JButton("New Obj");
		btnCreateObject.setBounds(400, 70, 95, 25);
		btnCreateObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initNewObject();
				btnCancelObject.setEnabled(true);
				btnCreateObject.setEnabled(false);

				iniObjDiagramAssoc();
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
				txNewObject.requestFocusInWindow();
			}
		});
		panel.add(btnCreateObject);

		btnSaveObject = new JButton("Save Obj");
		btnSaveObject.setBounds(205, 122, 90, 25);

		btnSaveObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nomObj = txNewObject.getText();
				oClass = lClass.getSelectedValue();
				// Si bNewObj hay que ver si ya existe un objeto con el mismo nombre
				if (bNewObj) {
					if (existObject(nomObj, oClass.name())) {
						JOptionPane.showMessageDialog(frame, "Error: [" +nomObj+"] ya existe", "Error", JOptionPane.ERROR_MESSAGE);
						txNewObject.requestFocusInWindow();
						return;
					}
				}
				//				btnCreateObject.setEnabled(true);
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
				bNewObj=false;
				btnCreateObject.setEnabled(true);
				btnSaveObject.setEnabled(false);
				btnCancelObject.setEnabled(false);
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
				btnCreateObject.setEnabled(true);
				cancelObject(nomObj);
				if (nomObj!=null){
					if (!nomObj.equals("")){
						selectObject(nomObj);
						btnCreateObject.setEnabled(true); 
						btnSaveObject.setEnabled(false);  
						btnCancelObject.setEnabled(false);
						btnNewObjectAuto.setEnabled(true);
					}
				}
				bNewObj=false;
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
				btnSaveObject.setEnabled(false);   
				btnCancelObject.setEnabled(false);  
			}
		});
		panel.add(btnDeleteObject);

		txNewObject.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				actualizarBotones();
			}

			public void removeUpdate(DocumentEvent e) {
				actualizarBotones();
			}

			public void changedUpdate(DocumentEvent e) {
				// Este método solo se usa con campos de texto con estilo (no aplica aquí)
			}

			private void actualizarBotones() {
				boolean hayTexto = !txNewObject.getText().trim().isEmpty();
				btnSaveObject.setEnabled(hayTexto);
				btnCancelObject.setEnabled(hayTexto);
				btnDeleteObject.setEnabled(hayTexto);
			}
		});

		panel.add(txNewObject);
		nomObj = (String) lObjects.getSelectedValue();
		txNewObject.setText(nomObj);

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

		lAssocs.setBounds(10, 215, 120, 140);
		lAssocs.setBorder(BorderFactory.createLineBorder(Color.black));
		lAssocs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				MAssociation oAssoc = lAssocs.getSelectedValue();
				if (oAssoc!=null) {
					setComposAssoc(oAssoc);
				}
				int nObjOri = cmbObjectOri.getModel().getSize();
				int nObjDes = cmbObjectDes.getModel().getSize();
				btnInsertLinkAssoc.setEnabled(nObjOri>0 && nObjDes>0);
			}
		});

		scrollPaneAssoc = new JScrollPane();
		scrollPaneAssoc.setViewportView(lAssocs);
		scrollPaneAssoc.setBounds(10, 215, 120, 140);
		panel.add(scrollPaneAssoc);

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
		btnActions.setBounds(10, 384, 95, 60);
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

		btnSuggestions = new JButton("<html><center>Suggest<br>fixes</center></html>");
		btnSuggestions.setBounds(107, 384, 95, 60);
		btnSuggestions.setVerticalAlignment(SwingConstants.CENTER);
		btnSuggestions.setHorizontalAlignment(SwingConstants.CENTER);
		btnSuggestions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				// Ver que contiene listStrSatisfiables y listStrUnSatisfiables
				//				System.out.println("listStrSatisfiables ["+listStrSatisfiables+"]");
				//				System.out.println("listStrUnSatisfiables ["+listStrUnSatisfiables+"]");
				runAnalisis();				
			}
		});
		panel.add(btnSuggestions);

		btnViewCmbs = new JButton("MSS/MUS");
		btnViewCmbs.setBounds(10, 450, 95, 60);
		btnViewCmbs.setVerticalAlignment(SwingConstants.CENTER);
		btnViewCmbs.setHorizontalAlignment(SwingConstants.CENTER);
		btnViewCmbs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Window window : MainWindow.getWindows()) {
					if (window instanceof JDialog) {
						JDialog dialog = (JDialog) window;
						if (("ValidatorMVMDialogSimple".equals(dialog.getName()) && fMainWindow.getValidatorDialog() !=null)) {
							fMainWindow.getValidatorDialog().setVisible(true);
							break;
						}
					}
				}
			}
		});

		btnViewCmbs.setEnabled(fMainWindow.getValidatorDialog()!=null);

		panel.add(btnViewCmbs);		

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
					fMainWindow.showMVMWizard(NAMEFRAMEMVMWIZARD, listStrSatisfiables, listStrUnSatisfiables);
				}

				searchObjDiagramAssociated();
				//--
				if (!existDiagram) {
					createObjDiagram("");
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

		lClass.setSelectedIndex(0);
		lObjects.setSelectedIndex(0);
		lAssocs.setSelectedIndex(0);

		boolean hayTexto = !txNewObject.getText().trim().isEmpty();
		btnSaveObject.setEnabled(hayTexto);
		btnCancelObject.setEnabled(hayTexto);
		btnDeleteObject.setEnabled(hayTexto);

		//Causes display of objects when Wizard is invoked from other classes with UI
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				int index = 0;

				lClass.setSelectedIndex(index);
				Rectangle cellBounds = lClass.getCellBounds(index, index);

				MouseEvent me1 = new MouseEvent(
						lClass, 
						MouseEvent.MOUSE_CLICKED, 
						System.currentTimeMillis(),
						0, // modifiers
						cellBounds.x + 1, 
						cellBounds.y + 1, 
						1, // click count
						false
						);

				for (MouseListener ml : lClass.getMouseListeners()) {
					ml.mouseClicked(me1);
				}

				lAssocs.setSelectedIndex(0);
				cellBounds = lAssocs.getCellBounds(index, index);

				MouseEvent me2 = new MouseEvent(
						lClass, 
						MouseEvent.MOUSE_CLICKED, 
						System.currentTimeMillis(),
						0, // modifiers
						cellBounds.x + 1, 
						cellBounds.y + 1, 
						1, // click count
						false
						);

				for (MouseListener ml : lAssocs.getMouseListeners()) {
					ml.mouseClicked(me2);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Do nothing
			}
		});

	}
	private void checkForChanges() {
		boolean changed = false;

		if (fOriginalValues != null && fValues != null) {
			for (int i = 0; i < fValues.length; i++) {
				if (!fValues[i].equals(fOriginalValues[i])) {
					changed = true;
					break;
				}
			}
		}

		btnSaveObject.setEnabled(changed);
		btnCancelObject.setEnabled(changed);
	}
	public static String getStrDefModel() {
		String sDefModel="";

		try {
			sDefModel = new String(Files.readAllBytes(Paths.get(pathModel)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sDefModel;
	}

	public static String getStrDefProperties() {
		String sDefProperties="DefProperties";
		try {
			sDefProperties = new String(Files.readAllBytes(Paths.get(pathProperties)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sDefProperties;
	}
	public static String getStrInvariants() {

		List<String> invariants = new ArrayList<>();
		invariants = extractInvariants(strDefModel);

		return String.join("\r\n", invariants);
	}
	public static List<String> extractInvariants(String modelText) {
		List<String> invariants = new ArrayList<String>();

		Pattern p = Pattern.compile(
				"\\bcontext\\s+(\\w+)\\s+inv\\s+(\\w+)\\s*:",
				Pattern.CASE_INSENSITIVE
				);
		Matcher m = p.matcher(modelText);

		int counter = 1;
		while (m.find()) {
			String className = m.group(1);
			String invName   = m.group(2);
			String entry = counter + "-" + className + "::" + invName ;
			invariants.add(entry);
			counter++;
		}

		return invariants;
	}
	public String getStrMUS() {

		//		String gMUS = ""; // in format: 1-2,3-4-5

		String gMUS = fMainWindow.listStrUnSatisfiables.toString().replace("[", "").replace("]", "");
		List<String> groups = new ArrayList<>();
		groups = buildInvariantGroups(strInvariants, gMUS);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < groups.size(); i++) {
			sb.append(groups.get(i).trim());
			if (i < groups.size() - 1) {
				sb.append("\n"); // line break between groups
			}
		}
		System.out.println("MUS: ["+sb.toString()+"]");
		return sb.toString();
	}
	public static List<String> buildInvariantGroups(String strInvariants, String gMSS) {

		List<String> result = new ArrayList<>();

		// 1) Convert list of invariants into an id -> text map
		Map<String, String> mapInv = new LinkedHashMap<>();

		String[] lines = strInvariants.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.isEmpty()) continue;

			int idx = line.indexOf("-");
			if (idx > 0) {
				String id = line.substring(0, idx).trim();
				mapInv.put(id, line);
			}
		}

		// 2) Procesar los grupos MUS
		String[] groups = gMSS.split(",");
		if (groups.length<=0 || groups[0].toString().equals("")) {
			return result;
		}
		for (int i = 0; i < groups.length; i++) {

			String group = groups[i].trim();   // Example: "1-2"
			String[] ids = group.split("-");

			StringBuilder sb = new StringBuilder();
			sb.append("Group").append(i + 1).append(": ");

			for (int j = 0; j < ids.length; j++) {
				String id = ids[j].trim();
				String inv = mapInv.get(id);

				if (inv != null) {
					sb.append(inv);
					if (j < ids.length - 1) sb.append(",");
				}
			}

			result.add(sb.toString());
		}

		return result;
	}

	public String getStrMSS() {
		//		String gMSS = ""; // en fomato 1-2,3-4-5
		String gMSS = fMainWindow.listStrSatisfiables.toString().replace("[", "").replace("]", "");
		List<String> groups = new ArrayList<>();
		groups = buildInvariantGroups(strInvariants, gMSS);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < groups.size(); i++) {
			sb.append(groups.get(i).trim());
			if (i < groups.size() - 1) {
				sb.append("\n".toUpperCase()); // salto de línea entre grupos
			}
		}
		System.out.println("MSS: ["+sb.toString()+"]");
		return sb.toString();
	}	

	public String getStrObjects() {

		List<String> lObjects = new ArrayList<>();

		try {
			List<MVMObject> lObjs = getMVMObjects();

			int count=0;

			for (MVMObject obj : lObjs) {

				StringBuilder sb = new StringBuilder();
				count+=1;

				sb.append("Object id=[").append(count).append("]");
				sb.append("|name=[").append(obj.getName()).append("]");
				sb.append("|class=[").append(obj.getClassName()).append("]");

				// Recorrer atributos
				for (MVMAttribute att : obj.getAttributes()) {
					sb.append("|field=[").append(att.getName()).append("] ");
					sb.append("value=[").append(att.getValue()).append("]");
				}

				lObjects.add(sb.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.join("\r\n", lObjects);
	}

	public String getStrLinks() {

		List<String> lLinks = new ArrayList<>();
		List<MVMLink> lLinksMVM = getMVMLinks();

		for (MVMLink link : lLinksMVM) {

			StringBuilder sb = new StringBuilder();

			sb.append("codeLink=\"").append(link.getCodeLink()).append("\"");
			sb.append(" end1Class=\"").append(link.getEnd1Class()).append("\"");
			sb.append(" end1Object=\"").append(link.getEnd1Object()).append("\"");
			sb.append(" end1Role=\"").append(link.getEnd1Role()).append("\"");
			sb.append(" end2Class=\"").append(link.getEnd2Class()).append("\"");
			sb.append(" end2Object=\"").append(link.getEnd2Object()).append("\"");
			sb.append(" end2Role=\"").append(link.getEnd2Role()).append("\"");
			sb.append(" nomAssoc=\"").append(link.getNomAssoc()).append("\"");

			lLinks.add(sb.toString());
		}

		return String.join("\r\n", lLinks);
	}

	public String getStrLinksJSON() {

		List<MVMLink> lLinksMVM = getMVMLinks();
		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (int i = 0; i < lLinksMVM.size(); i++) {
			MVMLink link = lLinksMVM.get(i);

			if (i > 0) sb.append(",");

			sb.append("{");

			// Orden fijo de claves
			sb.append("\"codeLink\":\"").append(link.getCodeLink()).append("\",");
			sb.append("\"nomAssoc\":\"").append(link.getNomAssoc()).append("\",");

			sb.append("\"end1Class\":\"").append(link.getEnd1Class()).append("\",");
			sb.append("\"end1Object\":\"").append(link.getEnd1Object()).append("\","); 
			sb.append("\"end1Role\":\"").append(link.getEnd1Role()).append("\",");

			sb.append("\"end2Class\":\"").append(link.getEnd2Class()).append("\",");
			sb.append("\"end2Object\":\"").append(link.getEnd2Object()).append("\",");
			sb.append("\"end2Role\":\"").append(link.getEnd2Role()).append("\"");

			sb.append("}");
		}

		sb.append("]");

		return sb.toString();
	}

	public static String builJsonRequest3(String strNameModel, String strDefModel, 
			String strDefProperties, String strInvariants, String strMUS, String strMSS,
			String strObjects, String strLinks) {

		int count=0;
		StringBuilder sb = new StringBuilder();

		sb.append("*Profile*\n");
		sb.append("1. You are a software modeling assistant, expert on software design, development and debugging.\n"+
				"2. You provide concise, concrete and actionable feedback about software design errors.\n\n");

		// TASK ====================================================================================================
		sb.append("*Task*\n");
		sb.append("Given a UML class diagram annotated with OCL invariants that is inconsistent, you will:\n");
		sb.append("1. Identify the invariants that cause the inconsistency.\n");
		sb.append("2. Review the properties file to see if the specified ranges are correct, and if not, suggest values ​​to change.\n");
		sb.append("3. Modify ONLY the invariants that MUST be changed to make the model satisfiable.\n");
		sb.append("4. Calculate the MUS/MSS if this prompt does not provide it later.\n");
		sb.append("5. Produce a JSON output with Properties, Objects, Links, Comment and ChangedInvariants.\n");
		sb.append("This explanation should be usable by a software engineer to locate and correct the defects in the model.\n");
		sb.append("\n");

		// INPUTS ================================================================================================================
		sb.append("*Inputs*\n");
		count=1;
		sb.append((count)+". Model:\n");
		sb.append("\n");
		sb.append("The UML model definition provided in the format of the USE tool "+
				"(UML-based Specification Environament) developed by the University of Bremen.\n");
		sb.append("\n");

		// MODEL DEFINITION	------------------------------------------------------------------------------------------------------
		sb.append(LINEASEP);
		sb.append("Model definition:\n");
		sb.append("\"\"\"\n").append(strDefModel).append("\"\"\"\n");
		sb.append(LINEASEP);		
		sb.append("\n");

		// PROPERTIES ------------------------------------------------------------------------------------------------------------
		count+=1;
		sb.append((count)+". Properties:\n");
		sb.append("A textual description of the range of allowed values for each attribute in the model ");
		sb.append("and the range on the number of objects per class and the number of links per association.\n");
		sb.append("\n");

		sb.append("Properties:\n");
		sb.append("\"\"\"\n").append(strDefProperties).append("\"\"\"\n");
		sb.append(LINEASEP);
		sb.append("\n");

		// INVARIANTS ------------------------------------------------------------------------------------------------------------
		count+=1;
		sb.append((count)+". Invariants:\n");
		sb.append("A summary of the invariants in the model, extracted from the USE definition model, ");
		sb.append("and assigning an integer index to each invariant that will be used to refer to the invariant.");
		sb.append("\n\n");

		sb.append("List of invariants:\n");
		sb.append("\"\"\"\n").append(strInvariants).append("\"\"\"\n");
		sb.append(LINEASEP);
		sb.append("\n");

		// ORIGINALINVARIANTS -----------------------------------------------------------------------------------------------------
		count+=1;
		sb.append((count)+". OriginalInvariants:\n");
		sb.append("A JSON array containing the exact original text of each invariant.\n\n");
		sb.append("OriginalInvariants:\n");
		sb.append("\"\"\"\n").append(strOriginalInvariants).append("\n\"\"\"\n");
		sb.append(LINEASEP);
		sb.append("\n");


		// MUS/MSS ---------------------------------------------------------------------------------------------------------------		
		if (!strMUS.equals("")||!strMSS.equals("")) {
			count+=1;
			sb.append((count)+". MUS/MSS found so far:\n");
			sb.append("\n");
			sb.append("List of MUS:\n\n\"\"\"\n"+strMUS+"\n\"\"\"");
			sb.append("\n\n");
			sb.append(LINEASEP);
			sb.append("List of MSS:\n\n\"\"\"\n"+strMSS+"\n\"\"\"");
			sb.append("\n\n");
			sb.append(LINEASEP);
			sb.append("\n");
		}		

		// OBJECTS & LINKS -------------------------------------------------------------------------------------------------------
		if (!strObjects.equals("")) {
			count+=1;
			sb.append((count)+". Instance of the model:\n");
			sb.append("An instance (a set of objects and links among objects of the model, ) ");
			sb.append("that should be satisfying all the textual invariants and the graphical UML constraints ");
			sb.append("such as the multiplicities of association ends.\n");
			sb.append("\n");

			sb.append("List of objects:\n\"\"\"\n"+strObjects+"\n\"\"\"");
			sb.append("\n\n");
			sb.append(LINEASEP);

			if (!strLinks.equals("")) {
				sb.append("List of links:\n\"\"\"\n"+strLinks+"\n\"\"\"");
				sb.append("\n\n");
				sb.append(LINEASEP);
			}
		}	

		// OUTPUTS ================================================================================================================
		sb.append("*Outputs*\n\n");

		// COMMENTS ---------------------------------------------------------------------------------------------------------------
		sb.append("1. Explanation:\n");
		sb.append("   1.1. A brief discussion of the inconsistency problems in the model.\n");
		sb.append("   1.2. For each problem, the model should identify the invariants involved and outline "+
				"a potential way to solve the inconsistency.\n");
		sb.append("   1.3. Do not provide suggestions like \"this value should be valid\".\n");
		sb.append("   1.4. Instead, provide informative statements like \"this value should be larger\", "+
				"\"this values should be within the following range\", "+
				"\"the value of attribute X should be different to the value of attribute Y\", "+
				"\"this value should be unique\", etc.\n");
		sb.append("   1.5. Each sentence must be on its own line.\n");		
		sb.append("\n");

		//OBJECTS LIST & LINKS -----------------------------------------------------------------------------------------------------
		sb.append("2. Updated list of objects and links:\n");
		sb.append("   2.1. If the input included an instance of the model, "+
				"provide a modified list of objects and links, with minimal changes, "+
				"such that the corresponding instance satisfies all invariants and graphical UML constraints.\n");
		sb.append("   3.1. If any object has an attribute whose value violates an invariant, please suggest "+
				"a new value for that attribute and include it in the list of objects you provide at the end.\n");
		sb.append("\n");

		// CHANGEDINVARIANTS -------------------------------------------------------------------------------------------------------
		sb.append("3. ChangedInvariants:\n");
		sb.append("   A JSON array containing ONLY the invariants whose text is actually modified.\n");
		sb.append("   You MUST follow these rules:\n");
		sb.append("     3.1. You MUST compare each proposed invariant with its corresponding entry in OriginalInvariants.\n");
		sb.append("     3.2. If the proposal is the same as the original (both textually and semantically), "+
				"it MUST NOT include that invariant..\n");
		sb.append("     3.3. You MUST modify an invariant ONLY if it is impossible to satisfy the model without modifying it.\n");
		sb.append("     3.4. If the MUS contains invariants that CAN be satisfied by adjusting objects or properties, "+
				"you MUST NOT modify those invariants.\n");
		sb.append("     3.5. If the MUS contains invariants that CANNOT be satisfied without modifying them, "+
				"you MUST generate a corrected version.\n");
		sb.append("     3.6. The corrected version MUST be syntactically valid OCL.\n");
		sb.append("     3.7. ChangedInvariants may be empty IF AND ONLY IF no invariant requires modification.\n");
		sb.append("     3.8. If you mention in the Explanation that an invariant must be changed, you MUST include it in ChangedInvariants.\n");
		sb.append("     3.9. Retain the numbering indicated in each invariant and indicate it in the results table.\n");
		sb.append("\n");

		sb.append("   Example format:\n");
		sb.append("   \"ChangedInvariants\":[\n");
		sb.append("     {\n");
		sb.append("       \"name\":\"1-validAge\",\n");
		sb.append("       \"original\":\"self.age <= 0 and self.age > 99\",\n");
		sb.append("       \"proposal\":\"self.age >= 0 and self.age < 99\"\n");
		sb.append("     }\n");
		sb.append("   ]\n\n");

		// REMARKS =================================================================================================================
		sb.append("*Remarks*\n");
		// JSON --------------------------------------------------------------------------------------------------------------------
		sb.append("1. Please return only the JSON structure without any additional explanation, since the result must be delivered "+
				"to an application.\n"+
				"   Therefore, the JSON structure must contain the following parts:\n");

		// PROPERTIES --------------------------------------------------------------------------------------------------------------
		sb.append("   1.1 Properties: properties to be modified. The properties tag only refers to the properties "+
				"that exist in "+strNameProperties+" and they should review the range limits to see "+
				"if it is necessary to modify them to satisfy the invariants.\n");

		// OBJECTS -----------------------------------------------------------------------------------------------------------------		
		sb.append("   1.2 Objects: A list of objects with their corresponding values as if the modified properties had already been applied.\n"); 
		sb.append("       Please return the same field names indicated in the request corresponding to each class.\n");
		sb.append("       Please ensure that when defining objects, you normalize the output using the tags 'class', 'name', and 'attributes'.\n");
		sb.append("       If more objects are needed to satisfy the multiplicity of the association links,"+
				"especially those multiplicities that require an endpoint, please indicate that these objects "+
				"should be created and propose a sample in the list of proposed resulting objects.\n");
		sb.append("       If you don't need to change your values, don't change them.\n");
		sb.append("       However, if there is a value that violates an invariant, it suggests changing it.");

		// LINKS -------------------------------------------------------------------------------------------------------------------
		sb.append("   1.3 Links: For each link, specify the fields: "+
				"codeLink, end1Class, end1Object, end1Role, end2Class, end2Object, end2Role, nomAssoc.\n");
		sb.append("       Example: codeLink=\"1\" | end1Class=\"Person\" end1Object=\"person1\" "+
				"end1Role=\"person\" end2Class=\"Pet\" end2Object=\"pet1\" end2Role=\"person\" nomAssoc=\"BelongsTo\"\n");
		sb.append("       For each association, respect its multiplicity. "+
				"That is, in a multiplicity of 4, it verifies that there are 4 links.\n");

		// COMMENT -----------------------------------------------------------------------------------------------------------------
		sb.append("   1.4 Comment: Explanation of 'how to correct the model', taking into account the properties "+
				"that have been indicated for correction as if they were already corrected.\n");
		sb.append("       Focus only on modifying invariants (not objects or properties).\n");
		sb.append("       The explanation should not be in the past tense but in the present or future tense.\n");
		sb.append("       The only tags to return should be: Properties, Objects, Links, Comment and ChangedInvariants.\n");
		sb.append("       Do not omit the creation of objects or links.\n");
		sb.append("       If the ChangedInvariants tag contains modified invariants, don't forget to include "+
				"the invariant number provided in the initial list.\n");
		sb.append("       Return ONLY valid JSON.\n");
		sb.append("       Do not include markdown.\n");
		sb.append("       If MUS exist, then ChangedInvariants must always exist.\n");
		sb.append("       Return each sentence on a separate line, using a line break between sentences.\n");
		sb.append("       For example:\n");
		sb.append("         Input:  Sentence1. Sentence2. Sentence3.\n");
		sb.append("       Output:\n");
		sb.append("         Sentence1.\n");
		sb.append("         Sentence2.\n");
		sb.append("         Sentence3.\n");

		// MUSS/MSS ----------------------------------------------------------------------------------------------------------------
		if (strMUS.equals("")&&strMSS.equals("")) {
			sb.append("   1.5 Include MUS and MSS in the Comment tag using EXACTLY this format:\n\n");
			sb.append(LINEASEP);
			sb.append("ADDITIONAL INFORMATION ABOUT MUS/MSS\n");
			sb.append(LINEASEP);
			sb.append("Minimal Unsatisfiable Subset:\n");
			sb.append(" - Group1:\n");
			sb.append("   1.invariantX\n");
			sb.append(" - Group2:\n");
			sb.append("   2.invariantY\n");
			sb.append("   3.invariantZ\n");
			sb.append(LINEASEP);
			sb.append("Maximal Satisfiable Subset:\n");
			sb.append(" - Group1:\n");
			sb.append("   4.invariantA\n");
			sb.append("   5.invariantB\n");
			sb.append("   6.invariantC\n");
			sb.append(" - Group2:\n");
			sb.append("   7.invariantD\n");
			sb.append("   8.invariantE\n");
			sb.append(" - Group3:\n");
			sb.append("   9.invariantF\n");
			sb.append("  10.invariantG\n");
			sb.append(LINEASEP);
		}

		mensaje=sb.toString();

		String json = buildRequest3(mensaje);
		return json;
	}		

	public static String buildRequest3(String prompt) {

		JSONObject root = new JSONObject();

		try {
			root.put("model", "gpt-4o-mini");
			root.put("temperature", 0);

			JSONObject responseFormat = new JSONObject();
			responseFormat.put("type", "json_object");
			root.put("response_format", responseFormat);

			JSONArray messages = new JSONArray();

			JSONObject userMessage = new JSONObject();
			userMessage.put("role", "user");
			userMessage.put("content", prompt);

			messages.put(userMessage);

			root.put("messages", messages);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return root.toString();
	}

	/*
	 * Formatea un objeto JSON
	 */

	public static String formatJson(String json) {
		json = json.trim();

		try {
			// Si empieza por [, es un array
			if (json.startsWith("[")) {
				JSONArray arr = new JSONArray(json);
				return arr.toString(2); // indentación de 2 espacios
			} 
			// Si empieza por {, es un objeto
			else if (json.startsWith("{")) {
				JSONObject obj = new JSONObject(json);
				return obj.toString(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	public static String analysisJsonToString(String jsonContent) {
		StringBuilder sb = new StringBuilder();

		try {
			JSONObject root = new JSONObject(jsonContent);
			JSONArray choices = root.getJSONArray("choices");
			if (choices.length() > 0) {
				JSONObject firstChoice = choices.getJSONObject(0);
				JSONObject message = firstChoice.getJSONObject("message");
				sb.append(message.getString("content").trim());
			} else {
				sb.append("No choices found in the response.");
			}
		} catch (JSONException e) {
			sb.append("Error parsing OpenAI response: " + e.getMessage());
		}

		return sb.toString();
	}

	/*
	 * Analiza determinados tags dentro de un JSON
	 */
	public static Map<String, String> parseResult(String jsonString) {

		Map<String, String> out = new LinkedHashMap<>();

		JSONObject root;
		try {
			root = new JSONObject(jsonString);

			// --- Properties ---
			Object propsObj = root.get("Properties");

			if (propsObj instanceof JSONObject) {
				JSONObject props = (JSONObject) propsObj;
				out.put("rProperties", props.toString());
			}
			else if (propsObj instanceof JSONArray) {
				JSONArray props = (JSONArray) propsObj;
				out.put("rProperties", props.toString());
			}
			else {
				out.put("rProperties", "{}"); 
			}

			// --- Objects ---
			Object objsObj = root.get("Objects");

			if (objsObj instanceof JSONArray) {
				JSONArray objs = (JSONArray) objsObj;
				out.put("rObjects", objs.toString());
			}
			else if (objsObj instanceof JSONObject) {
				JSONObject objs = (JSONObject) objsObj;
				out.put("rObjects", objs.toString());
			}
			else {
				out.put("rObjects", "[]"); 
			}

			// --- Links ---
			Object linksObj = root.get("Links");

			if (linksObj instanceof JSONArray) {
				JSONArray links = (JSONArray) linksObj;
				out.put("rLinks", links.toString());
			} 
			else if (linksObj instanceof JSONObject) {
				JSONObject links = (JSONObject) linksObj;
				out.put("rLinks", links.toString());
			} 
			else {
				out.put("rLinks", "[]"); 
			}

			// --- ChangedInvariants (nuevo tag) ---
			Object invObj = root.opt("ChangedInvariants");
			if (invObj instanceof JSONArray) {
				out.put("rChangedInvariants", ((JSONArray) invObj).toString());
			} else if (invObj instanceof JSONObject) {
				out.put("rChangedInvariants", ((JSONObject) invObj).toString());
			} else {
				out.put("rChangedInvariants", "[]");
			}

			// --- Comment ---
			String comment = root.getString("Comment");
			out.put("rComments", comment);	

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * Ejecuta prompt AI y analiza resultados
	 */

	private void runAnalisis() {
		try {
			panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			strDefModel=getStrDefModel();
			strDefProperties=getStrDefProperties();
			strInvariants=getStrInvariants();
			strMUS=getStrMUS();
			strMSS=getStrMSS();
			strObjects=getStrObjects();
			strLinks=getStrLinks();

			JSONArray originalInvariants = extractOriginalInvariants(strDefModel);

			strOriginalInvariants = buildOriginalInvariantsString(originalInvariants);

			String json = builJsonRequest3(strNameModel, strDefModel, 
					strDefProperties, strInvariants, strMUS, strMSS,
					strObjects, strLinks);

			ObjectMapper mapper = new ObjectMapper();
			Object jsonWork = mapper.readValue(json, Object.class);

			// Luego lo convierto a JSON bonito
			jsonPretty = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(jsonWork);

			if (showTxtBlocks) {
				System.out.println("jsonPretty 1 =================================================================");
				System.out.println(jsonPretty);
				System.out.println("jsonPretty 2 =================================================================");
			}

			// Llamada API
			jsonResult=callAPIOpenAI3(jsonPretty);

			if (showTxtBlocks) {
				System.out.println("jsonContent 1 =================================================================");
				System.out.println(jsonResult);
				System.out.println("jsonContent 2 =================================================================");
			}

			// Analisis json
			String jsonAnalysisResult = analysisJsonToString(jsonResult);
			if (showTxtBlocks) {
				System.out.println("resultado 1 =================================================================");
				System.out.println(jsonAnalysisResult);
				System.out.println("resultado 2 =================================================================");
			}

			Map<String, String> out = parseResult(jsonAnalysisResult);

			String rObjectsAI = out.get("rObjects");
			String rLinksAI = out.get("rLinks");
			String rProperties = out.get("rProperties");
			String rComments = out.get("rComments");
			String rchangedInvariants = out.get("rChangedInvariants");

			// rComments contiene el texto original devuelto por OpenAI
			String original = rComments.trim();

			// Divide por frases terminadas en punto, conservando el punto
			String[] sentences = original.split("(?<=\\.)\\s+");

			// Une cada frase en una línea distinta con CRLF
			String formatted = String.join("\r\n", sentences);

			// Si quieres reemplazar el original:
			rComments = formatted;

			//---------------
			JSONArray arrChanges = new JSONArray(rchangedInvariants);	
			// Filtrar solo invariantes que realmente cambian
			JSONArray filtered = new JSONArray();

			for (int i = 0; i < arrChanges.length(); i++) {
				JSONObject obj = arrChanges.getJSONObject(i);

				String originalInvs = obj.getString("original").replaceAll("\\s+", "");
				String proposalInvs = obj.getString("proposal").replaceAll("\\s+", "");

				if (!originalInvs.equals(proposalInvs)) {
					filtered.put(obj);
				}
			}

			// Reemplazar arr por filtered
			arrChanges = filtered;
			rchangedInvariants = arrChanges.toString();
			//---------------

			if (!rchangedInvariants.equals("[]")) {
				// rChangedInvariants reformated
				String CI = jsonToAsciiTable(rchangedInvariants);
				rchangedInvariants=CI;

				if (!CI.equals("")) {
					rComments+="\n";
					rComments+=LINEASEP;
					rComments+="SUMMARY OF INVARIANTS TO BE CHANGED\n";
					rComments+="\n"+rchangedInvariants;	
				}

			}

			if (showTxtBlocks) {
				System.out.println("rObjects 1 =================================================================");
				System.out.println(formatJson(rObjectsAI));
				System.out.println("rObjects 2 =================================================================");

				System.out.println("rLinks 1 =================================================================");
				System.out.println(formatJson(rLinksAI));
				System.out.println("rLinks 2 =================================================================");

				System.out.println("rProperties 1 =================================================================");
				System.out.println(formatJson(rProperties));
				System.out.println("rProperties 2 =================================================================");

				System.out.println("rComments 1 =================================================================");
				System.out.println(formatJson(rComments));
				System.out.println("rComments 2 =================================================================");

				System.out.println("rchangedInvariants 1 =================================================================");
				System.out.println(formatJson(rchangedInvariants));
				System.out.println("rchangedInvariants 2 =================================================================");

				System.out.println("");
				System.out.println(out);
			}

			// We have to order in the same way: 
			// rObjects
			String strSwap=sortRObjectsByClassAndName(rObjectsAI);
			rObjectsAI=strSwap;

			// strObjects
			strSwap=sortSTRObjectsByClassAndName(strObjects);
			String strObjectsJSON=strSwap;

			// We need to convert strLinks to strLinksJSON   
			String strLinksJSON = getStrLinksJSON();
			//			System.out.println("strLinksJSON:\n"+strLinksJSON);
			//			// Tenemos que ordenar Links atendiendo este orden dentro de cada objeto
			//			//			strSwap=sortStrLinksJSONFields(strLinksJSON);
			//			//			strLinksJSON=strSwap;

			// To ensure the comparison takes into account the order of fields, we also order rLinks.
			strSwap=sortStrLinksJSONFields(rLinksAI);
			rLinksAI=strSwap;
			//			System.out.println("rLinks:\n"+rLinksAI);

			// OpenAI visualization callout
			panel.setCursor(Cursor.getDefaultCursor());
			showResponseOpenAI(rObjectsAI, rLinksAI, rProperties, rComments, mensaje, jsonPretty, jsonResult, strObjectsJSON, strLinksJSON);

		} catch (Exception ex) {
			panel.setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String sortStrLinksJSONFields(String json) {

		json = json.trim();

		// Remove outer brackets
		if (json.startsWith("[") && json.endsWith("]")) {
			json = json.substring(1, json.length() - 1).trim();
		}

		// Extract complete objects while respecting keys
		List<String> rawObjects = new ArrayList<String>();
		int depth = 0;
		int start = -1;

		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);

			if (c == '{') {
				if (depth == 0) start = i;
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0 && start != -1) {
					rawObjects.add(json.substring(start, i + 1));
					start = -1;
				}
			}
		}

		// Desired order
		String[] order = {
				"codeLink",
				"nomAssoc",
				"end1Class",
				"end1Object",
				"end1Role",
				"end2Class",
				"end2Object",
				"end2Role"
		};

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// Parse each object
		for (String obj : rawObjects) {

			Map<String, String> map = new HashMap<String, String>();

			for (int i = 0; i < order.length; i++) {
				String key = order[i];
				String value = extractJsonStringValue(obj, key);
				map.put(key, value);
			}

			list.add(map);
		}

		// Reconstruct sorted JSON
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);

			if (i > 0) sb.append(",");

			sb.append("{");

			for (int j = 0; j < order.length; j++) {
				String key = order[j];
				String value = map.get(key);

				if (j > 0) sb.append(",");

				sb.append("\"").append(key).append("\":");

				// Detect numbers
				if (value != null && value.matches("-?\\d+")) {
					sb.append(value);
				}
				// Detect Booleans
				else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
					sb.append(value.toLowerCase());
				}
				// Strings
				else {
					sb.append("\"").append(value).append("\"");
				}
			}

			sb.append("}");
		}

		sb.append("]");

		return sb.toString();
	}
	private static String extractJsonStringValue(String obj, String key) {
		String pattern = "\"" + key + "\":";
		int p = obj.indexOf(pattern);
		if (p == -1) return "";

		p += pattern.length();

		// If it starts with quotes, it's a String
		if (obj.charAt(p) == '\"') {
			int start = p + 1;
			int end = obj.indexOf("\"", start);
			return obj.substring(start, end);
		}

		// Otherwise, it's a number or a boolean
		int end = p;
		while (end < obj.length() && obj.charAt(end) != ',' && obj.charAt(end) != '}') {
			end++;
		}

		return obj.substring(p, end).trim();
	}

	/**
	 * Sorts a string of MVM objects by name and formats it as a JSON string. 
	 * @param input
	 * @return
	 */
	public static String sortSTRObjectsByClassAndName(String input) {

		String[] lines = input.split("\\r?\\n");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < lines.length; i++) {

			String line = lines[i].trim();
			if (line.length() == 0) continue;

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, String> attributes = new HashMap<String, String>();

			// Extract name
			String name = extractBracketValue(line, "name=");
			map.put("name", name);

			// Extract class
			String clazz = extractBracketValue(line, "class=");
			map.put("class", clazz);

			// Extract fields
			int pos = 0;
			while (true) {
				int f = line.indexOf("field=[", pos);
				if (f == -1) break;

				int fEnd = line.indexOf("]", f + 7);
				String fieldName = line.substring(f + 7, fEnd);

				int v = line.indexOf("value=[", fEnd);
				int vEnd = line.indexOf("]", v + 7);
				String fieldValue = line.substring(v + 7, vEnd);

				// clear single quotes
				if (fieldValue.startsWith("'") && fieldValue.endsWith("'")) {
					fieldValue = fieldValue.substring(1, fieldValue.length() - 1);
				}

				attributes.put(fieldName, fieldValue);

				pos = vEnd + 1;
			}

			map.put("attributes", attributes);
			list.add(map);
		}

		// ORDENAR
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> a, Map<String, Object> b) {

				String ca = (String) a.get("class");
				String cb = (String) b.get("class");

				int cmp = ca.compareTo(cb);
				if (cmp != 0) return cmp;

				String na = (String) a.get("name");
				String nb = (String) b.get("name");

				return na.compareTo(nb);
			}
		});

		// BUILD JSON
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> obj = list.get(i);
			if (i > 0) sb.append(",");

			sb.append("{");

			sb.append("\"class\":\"").append(obj.get("class")).append("\",");
			sb.append("\"name\":\"").append(obj.get("name")).append("\",");

			sb.append("\"attributes\":{");

			Map<String, String> attrs = (Map<String, String>) obj.get("attributes");
			int j = 0;
			for (Map.Entry<String, String> e : attrs.entrySet()) {
				if (j > 0) sb.append(",");
				sb.append("\"").append(e.getKey()).append("\":");
				String val = e.getValue();

				// Detect Booleans
				if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
					sb.append(val.toLowerCase());
				}
				// Detect signed integers
				else if (val.matches("-?\\d+")) {
					sb.append(val);
				}
				// Detect decimals (optional)
				else if (val.matches("-?\\d+(\\.\\d+)?")) {
					sb.append(val);
				}
				// Everything else is String
				else {
					sb.append("\"").append(val).append("\"");
				}

				j++;
			}

			sb.append("}");

			sb.append("}");
		}

		sb.append("]");

		return sb.toString();
	}

	/**
	 * Remove 'extra' brackets
	 * @param line
	 * @param key
	 * @return
	 */
	private static String extractBracketValue(String line, String key) {
		int p = line.indexOf(key);
		if (p == -1) return "";
		int start = line.indexOf("[", p);
		int end = line.indexOf("]", start);
		return line.substring(start + 1, end);
	}

	/**
	 * Sort rObjects (AI Response) by class name and object name
	 * @param json
	 * @return
	 */
	public static String sortRObjectsByClassAndName(String json) {

		json = json.trim();

		// Remove outer brackets
		if (json.startsWith("[") && json.endsWith("]")) {
			json = json.substring(1, json.length() - 1);
		}

		// Extract complete objects while respecting keys
		List<String> rawObjects = new ArrayList<String>();
		int depth = 0;
		int start = 0;

		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);

			if (c == '{') {
				if (depth == 0) start = i;
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0) {
					rawObjects.add(json.substring(start, i + 1));
				}
			}
		}

		// Parse each object
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (String obj : rawObjects) {

			Map<String, String> map = new HashMap<String, String>();

			// Extract "class"
			map.put("class", extractSimpleField(obj, "class"));

			// Extract "name"
			map.put("name", extractSimpleField(obj, "name"));

			// Extract "attributes" as a complete object
			map.put("attributes", extractObjectField(obj, "attributes"));

			list.add(map);
		}

		// Sort
		Collections.sort(list, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> a, Map<String, String> b) {

				int cmp = a.get("class").compareTo(b.get("class"));
				if (cmp != 0) return cmp;

				return a.get("name").compareTo(b.get("name"));
			}
		});

		// Rebuild JSON
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);

			if (i > 0) sb.append(",");

			sb.append("{");
			sb.append("\"class\":\"").append(map.get("class")).append("\",");
			sb.append("\"name\":\"").append(map.get("name")).append("\",");
			sb.append("\"attributes\":").append(map.get("attributes"));
			sb.append("}");
		}

		sb.append("]");

		return sb.toString();
	}

	/**
	 * Extract a field from an object
	 * @param obj
	 * @param field
	 * @return
	 */
	private static String extractSimpleField(String obj, String field) {
		String pattern = "\"" + field + "\":\"";
		int start = obj.indexOf(pattern);
		if (start == -1) return "";
		start += pattern.length();
		int end = obj.indexOf("\"", start);
		return obj.substring(start, end);
	}
	/*
	 * Extract a JSON object from an object
	 */
	private static String extractObjectField(String obj, String field) {
		String pattern = "\"" + field + "\":{";
		int start = obj.indexOf(pattern);
		if (start == -1) return "{}";
		start += pattern.length() - 1;

		int depth = 0;
		for (int i = start; i < obj.length(); i++) {
			char c = obj.charAt(i);
			if (c == '{') depth++;
			if (c == '}') depth--;
			if (depth == 0) {
				return obj.substring(start, i + 1);
			}
		}
		return "{}";
	}

	/**
	 * It obtains original invariants with its definition
	 * @param originalInvariants
	 * @return
	 */

	public static String buildOriginalInvariantsString(JSONArray originalInvariants) {
		String strRes="";
		try {
			strRes=originalInvariants.toString(2);
		} catch (JSONException e) {
			e.printStackTrace();
		} // indentación de 2 espacios
		return strRes;
	}

	/**
	 * Extract the invariants from the original model
	 * @param modelText
	 * @return
	 */

	public static JSONArray extractOriginalInvariants(String modelText) {
		JSONArray result = new JSONArray();

		Pattern headerPattern = Pattern.compile(
				"context\\s+(\\w+)\\s+inv\\s+(\\w+):",
				Pattern.CASE_INSENSITIVE
				);

		Matcher matcher = headerPattern.matcher(modelText);
		List<int[]> positions = new ArrayList<>();

		while (matcher.find()) {
			positions.add(new int[]{matcher.start(), matcher.end()});
		}

		for (int i = 0; i < positions.size(); i++) {
			int start = positions.get(i)[1];
			int end = (i + 1 < positions.size()) ? positions.get(i + 1)[0] : modelText.length();

			String header = modelText.substring(positions.get(i)[0], positions.get(i)[1]);
			Matcher h = headerPattern.matcher(header);
			h.find();

			String className = h.group(1).trim();
			String invName = h.group(2).trim();
			String fullName = className + "::" + invName;

			String body = modelText.substring(start, end)
					.trim()
					.replaceAll("--.*", "")
					.replaceAll("\\s+", " ");

			if (body.startsWith(":")) {
				body = body.substring(1).trim();
			}

			try {
				JSONObject inv = new JSONObject();
				inv.put("name", fullName);
				inv.put("text", body);
				result.put(inv);
			} catch (JSONException e) {
				e.printStackTrace(); // o log, o ignorar ese invariant
			}
		}

		return result;
	}

	/**
	 * Converts a string from a JSON invariant comparison to modify into a visual text table
	 * @param jsonString
	 * @return
	 */
	public static String jsonToAsciiTable(String jsonString) {

		JSONArray arr;
		StringBuilder sb = new StringBuilder();

		try {
			arr = new JSONArray(jsonString);

			String[] headers = { "Name", "Original", "Proposal" };
			int[] widths = { headers[0].length(), headers[1].length(), headers[2].length() };

			// Calculate max width
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				widths[0] = Math.max(widths[0], obj.getString("name").length());
				widths[1] = Math.max(widths[1], obj.getString("original").length());
				widths[2] = Math.max(widths[2], obj.getString("proposal").length());
			}

			// Separator
			String sep =
					"+" + repeat("-", widths[0] + 2) +
					"+" + repeat("-", widths[1] + 2) +
					"+" + repeat("-", widths[2] + 2) + "+\n";

			sb.append(sep);

			// Header
			sb.append("| ")
			.append(pad(headers[0], widths[0])).append(" | ")
			.append(pad(headers[1], widths[1])).append(" | ")
			.append(pad(headers[2], widths[2])).append(" |\n");

			sb.append(sep);

			// Rows
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				sb.append("| ")
				.append(pad(obj.getString("name"), widths[0])).append(" | ")
				.append(pad(obj.getString("original"), widths[1])).append(" | ")
				.append(pad(obj.getString("proposal"), widths[2])).append(" |\n");
			}

			sb.append(sep);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * Format a Strng to a specific width by padding it with spaces
	 * @param text
	 * @param width
	 * @return
	 */
	private static String pad(String text, int width) {
		return String.format("%-" + width + "s", text);
	}

	/**
	 * Repite un determinado caracter
	 * @param s
	 * @param count
	 * @return
	 */
	private static String repeat(String s, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public void setStrCreateObjectsAI(String objACrear) {
		strCreateObjectsAI=objACrear;
	}
	public void setStrCreateLinksAI(String linksACrear) {
		strCreateLinksAI=linksACrear;
	}

	/**
	 * Ejectua llamad a OpenAI
	 * @param json
	 * @return
	 */
	public static String callAPIOpenAI3(String json) {

		String responseBody="";
		MVMConfigManager config = new MVMConfigManager("config.properties");

		API_KEY = System.getenv("OPENAI_API_KEY");
		API_URL = config.get("endpoint");

		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(900, TimeUnit.SECONDS)
				.writeTimeout(180, TimeUnit.SECONDS)
				.build();

		RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

		Request request = new Request.Builder()
				.url(API_URL)
				.header("Authorization", "Bearer " + API_KEY)
				.post(body)
				.build();
		try (Response response = client.newCall(request).execute()) {

			if (!response.isSuccessful()) {
				throw new IOException("HTTP error code: " + response.code() + 
						" body: " + responseBody);
			}

			if (response.body() != null) {
				responseBody = response.body().string();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseBody;
	}

	/**
	 * Invoca dialogo para visualizar resultado OpenAI
	 * @param rObjects
	 * @param rLinks
	 * @param rProperties
	 * @param rComments
	 * @param mensaje
	 * @param jsonPretty
	 * @param jsonResult
	 * @param strObjectsJSON
	 */

	private void showResponseOpenAI(String rObjects, 
			String rLinks, String rProperties,String rComments, String mensaje, String jsonPretty, 
			String jsonResult, String strObjectsJSON, String strLinksJSON) {

		MVMShowResponseOpenAI dialog = new MVMShowResponseOpenAI(fWizardMVMView, frame, strNameModel, rObjects,
				rLinks, rProperties, rComments,mensaje, jsonPretty, jsonResult, strObjectsJSON, strLinksJSON);

		dialog.setSize(1254, 930);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		if (!strCreateObjectsAI.equals("")) {
			List<MVMObject> lObjs = new ArrayList<MVMObject>();
			List<MVMLink> lLinks = new ArrayList<MVMLink>();

			lObjs=parseMVMObjects(strCreateObjectsAI);
			if (!strCreateLinksAI.equals("")) {
				lLinks=parseMVMLinks(strCreateLinksAI);
			}		
			doCreationsAI(lObjs, lLinks);
		}
	}
	/**
	 * Creation de objs and links from OpenAI
	 * @param lObjs
	 * @param lLinks
	 */

	private void doCreationsAI(List<MVMObject> lObjs, List<MVMLink> lLinks) {
		resetObjLinks();
		createObjsList(lObjs);
		createLinksList(lLinks);

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

	public static List<MVMObject> parseMVMObjects(String jsonText) {
		List<MVMObject> list = new ArrayList<>();

		try {
			JSONArray arr = new JSONArray(jsonText);

			for (int i = 0; i < arr.length(); i++) {

				JSONObject obj = arr.getJSONObject(i);

				MVMObject mvmObj = new MVMObject();
				mvmObj.setName(obj.optString("name"));
				mvmObj.setClassName(obj.optString("class"));

				// Procesar atributos
				JSONObject attrs = obj.optJSONObject("attributes");
				List<MVMAttribute> attrList = new ArrayList<>();

				if (attrs != null) {
					Iterator<String> keys = attrs.keys();
					while (keys.hasNext()) {
						String key = keys.next();

						Object raw = attrs.get(key);
						String value;

						if (raw instanceof String) {
							value = "'" + raw + "'";   // envolver en comillas simples
						} else {
							value = String.valueOf(raw);
						}

						attrList.add(new MVMAttribute(key, value));
					}
				}

				mvmObj.setAttributes(attrList);

				list.add(mvmObj);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

	//	public static List<MVMObject> parseMVMObjectsOLD(String jsonText) {
	//		List<MVMObject> list = new ArrayList<>();
	//
	//		try {
	//			JSONArray arr = new JSONArray(jsonText);
	//
	//			for (int i = 0; i < arr.length(); i++) {
	//
	//				JSONObject obj = arr.getJSONObject(i);
	//
	//				MVMObject mvmObj = new MVMObject();
	//				mvmObj.setName(obj.optString("name"));
	//				mvmObj.setClassName(obj.optString("class"));
	//
	//				// Procesar atributos
	//				JSONObject attrs = obj.optJSONObject("attributes");
	//				List<MVMAttribute> attrList = new ArrayList<>();
	//
	//				if (attrs != null) {
	//					Iterator<String> keys = attrs.keys();
	//					while (keys.hasNext()) {
	//						String key = keys.next();
	//						String value = String.valueOf(attrs.get(key));
	//						attrList.add(new MVMAttribute(key, value));
	//					}
	//				}
	//
	//				mvmObj.setAttributes(attrList);
	//
	//				list.add(mvmObj);
	//			}
	//
	//		} catch (JSONException e) {
	//			e.printStackTrace();
	//		}
	//
	//		return list;
	//	}

	/**
	 * Parsea links propuestos por AI
	 * @param jsonText
	 * @return
	 */
	public static List<MVMLink> parseMVMLinks(String jsonText) {
		List<MVMLink> list = new ArrayList<>();

		try {
			JSONArray arr = new JSONArray(jsonText);

			for (int i = 0; i < arr.length(); i++) {

				JSONObject obj = arr.getJSONObject(i);

				MVMLink link = new MVMLink();

				link.setCodeLink(obj.optString("codeLink"));
				link.setNomAssoc(obj.optString("nomAssoc"));

				link.setEnd1Class(obj.optString("end1Class"));
				link.setEnd1Object(obj.optString("end1Object"));
				link.setEnd1Role(obj.optString("end1Role"));

				link.setEnd2Class(obj.optString("end2Class"));
				link.setEnd2Object(obj.optString("end2Object"));
				link.setEnd2Role(obj.optString("end2Role"));

				list.add(link);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
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
		if (oAssoc!=null) {
			setComposAssoc(oAssoc);
		}


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

		btnViewCmbs.setEnabled(fMainWindow.getValidatorDialog()!=null);

		lClass.requestFocus();
		lClass.setSelectedIndex(0);
		lObjects.setSelectedIndex(0);
		lAssocs.setSelectedIndex(0);		


	}

	public void enableBtnViewCmbs(){
		btnViewCmbs.setEnabled(fMainWindow.getValidatorDialog()!=null);
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
			link.setEnd2Role(oL1.associationEnd().name());
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

	/**
	 * Crea objetos atendiendo a la estructura MVMObject
	 * @param oObj
	 * @param verbose
	 */
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
			// hay que buscar attribute fName en lAttrsMVM

			String value="";

			for (MVMAttribute attrMVM : lAttrsMVM) {
				if (attrMVM.getName().equals(attribute.name())) {
					value= attrMVM.getValue();
					//AQUI
					if(showTrace) {
						System.out.println("attribute ["+attribute+"]");
						System.out.println("attrMVM ["+attrMVM+"]");
						System.out.println("value ["+value+"]");
					}
					break;
				}
			}

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
	 * Creo objetos indicados en una lista
	 */
	private void createObjsList(List<MVMObject> lObjs) {
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
	}

	/**
	 * Creo objetos indicados en una lista
	 */
	private void createLinksList(List<MVMLink> lLinks) {
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
			createObjsList(lObjs);

			List<MVMLink> lLinks = oAction.getlLinks();
			createLinksList(lLinks);

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

		lAssocs.setSelectedIndex(lAssocs.getSelectedIndex());
		int nObjOri = cmbObjectOri.getModel().getSize();
		int nObjDes = cmbObjectDes.getModel().getSize();
		btnInsertLinkAssoc.setEnabled(nObjOri>0 && nObjDes>0);

		btnSaveObject.setEnabled(false);   
		btnCancelObject.setEnabled(false);  

	}
	/**
	 * Crea objetos de muestra automaticamente
	 */
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
				//				btnSaveObject.setEnabled(false);
				//				btnCreateObject.setEnabled(false);
				btnNewObjectAuto.setEnabled(false);
			}else {
				lObjects.setSelectedIndex(0);
				nomObj = (String) lObjects.getSelectedValue();
				selectObject( nomObj);
				//				btnSaveObject.setEnabled(true);
				//				btnCreateObject.setEnabled(true);
				btnNewObjectAuto.setEnabled(true);
			}
		}
		btnSaveObject.setEnabled(false);
		btnCancelObject.setEnabled(false);
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
			// For it to behave the same as USE, there cannot be two objects with the same name even if
			// they are of different classes.

			//			if (obj.name().equals(name)&&obj.cls().name().equals(className)) {
			if (obj.name().equals(name)) {
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
		//		boolean bRes = false;
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

		blockForAssocFailOpenAI = new StringBuilder(buffer.toString());

		return;
	}

	/**
	 * Check structure
	 * @return
	 */
	private boolean checkStructure() {
		Map<String, List<String>> mapObjectsStr = new HashMap<String, List<String>>();
		getErrorsEstructure(); 
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

		txOriAssocRole.setText("");
		txMultiOri.setText("");
		txDesAssocRole.setText("");
		txMultiDes.setText("");		

		int nObjOri = cmbObjectOri.getModel().getSize();
		int nObjDes = cmbObjectDes.getModel().getSize();
		btnInsertLinkAssoc.setEnabled(nObjOri>0 && nObjDes>0);

		Set<MLink> links = fSystem.state().linksOfAssociation(oAssoc).links();

		btnDeleteLink.setEnabled(links.size()>0);	
		//--

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

		if (association!=null) {
			setComposAssoc(association);
		}

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

		if (oAssoc!=null) {
			setComposAssoc(oAssoc);
		}
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
			if (oAssoc!=null) {
				setComposAssoc(oAssoc);
			}
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
	public void createObjDiagram(String strTitle) {
		NewObjectDiagramView odv = new NewObjectDiagramView(fMainWindow, fSession.system());
		ViewFrame f = new ViewFrame("Object diagram", odv, "ObjectDiagram.gif");
		f.setName(NAMEFRAMEMVMDIAGRAM);
		odv.setName(NAMEFRAMEMVMDIAGRAM);
		if (strTitle.equals("")) {
			strTitle="Object diagram";
		}
		f.setTitle(strTitle);

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

		btnCancelObject.setEnabled(true);

		fAttributes = new ArrayList<>(oClass.allAttributes());

		if (fAttributes != null) {
			fValues = new String[fAttributes.size()];
			for (int i = 0; i < fValues.length; i++) {
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
			fTableModel.fireTableDataChanged();
		}

	}

	/**
	 * Cancel object modifications without save them
	 * @param nomObj
	 */
	private void cancelObject(String nomObj) {
		selectObject(nomObj);
		btnCancelObject.setEnabled(false);
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

		String strTite="";
		for (JInternalFrame ifr: allframes) {
			if (ifr.getName()!=null ) {
				if (ifr.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
					existDiagram=true;
					for (NewObjectDiagramView newObj: fMainWindow.getObjectDiagrams()) {
						if (newObj.getDiagram().getName()==null) {
							newObj.setName(NAMEFRAMEMVMDIAGRAM);
						}
						if (newObj.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
							strTite=ifr.getTitle();
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
			createObjDiagram(strTite);
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

			if (!nomObj.equals("")) {
				if (!existObject(nomObj, oClass.name())) {
					createObject(oClass, nomObj);
				}

				selectObject( nomObj);
			}


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

		lAssocs.setSelectedIndex(lAssocs.getSelectedIndex());
		int nObjOri = cmbObjectOri.getModel().getSize();
		int nObjDes = cmbObjectDes.getModel().getSize();
		btnInsertLinkAssoc.setEnabled(nObjOri>0 && nObjDes>0);

	}

	/**
	 * Delete an object
	 * @param nomObjDel
	 */
	private void deleteObject(String nomObjDel) {
		int idx = selectObject(nomObjDel);
		MSystemState state = fSystem.state();

		txNewObject.setText("");
		checkExistObjDiagram();
		boolean existDiagram=false;

		if(!existDiagram && allframes!=null) {
			for (JInternalFrame ifr: allframes) {
				if (ifr.getName()==null&&ifr.getTitle().equals("Object diagram")){
					ifr.setName(NAMEFRAMEMVMDIAGRAM);
				}
				if (ifr.getName()!=null){
					if (ifr.getName().equals(NAMEFRAMEMVMDIAGRAM)) {
						existDiagram=true;
						JDesktopPane fDesk = fMainWindow.getFdesk();
						String ifrName = ifr.getName();
						String strTitle = ifr.getTitle();
						fDesk.remove(ifr);
						createObjDiagram(strTitle);
					}
				}
			}
		}

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

		MAssociation oAssoc = lAssocs.getSelectedValue();
		if (oAssoc!=null) {
			setComposAssoc(oAssoc);
		}

		boolean hayTexto = !txNewObject.getText().trim().isEmpty();
		btnSaveObject.setEnabled(hayTexto);
		btnCancelObject.setEnabled(hayTexto);
		btnDeleteObject.setEnabled(hayTexto);

		int nObjOri = cmbObjectOri.getModel().getSize();
		int nObjDes = cmbObjectDes.getModel().getSize();
		btnInsertLinkAssoc.setEnabled(nObjOri>0 && nObjDes>0);

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
				nomObj=objName;
				idx=nObj;
				return idx;
			}
		}
		return idx;
	}

	public int selectClass(String className) {
		int idx = -1;

		// Search in list
		lClass.setSelectedIndex(0);
		int nClss= lClass.getModel().getSize();
		for (int nCls=0;nCls<nClss;nCls++) {
			if (lClass.getModel().getElementAt(nCls).name().equals(className)) {
				lClass.setSelectedIndex(nCls);
				nomClass=className;
				lObjects.setModel(loadListObjects(nomClass));
				idx=nCls;
				return idx;
			}
		}
		return idx;
	}

	public int selectAssoc(String assocName) {
		int idx = -1;

		// Search in list

		lAssocs.setSelectedIndex(0);
		int nClss= lAssocs.getModel().getSize();
		for (int nCls=0;nCls<nClss;nCls++) {
			if (lAssocs.getModel().getElementAt(nCls).name().equals(assocName)) {
				lAssocs.setSelectedIndex(nCls);
				MAssociation oAssoc = lAssocs.getSelectedValue();
				setComposAssoc(oAssoc);
				idx=nCls;
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
	 * Selecciona objeto y clase al hacer clic en dialogo
	 * @param nomObj
	 * @param nomClass
	 */
	public void selObjFromDia(String nomObj, String nomClass) {

		// Buscar clase dentro de lClass
		selectClass(nomClass);
		// Buscar obj dentro de lObjects
		selectObject(nomObj);
	}

	/**
	 * Selecciona Link al hacer clic en dialogo
	 * @param selectedEdge
	 */
	public void selLinkFromDia(EdgeBase selectedEdge) {

		LinkEdge aEdge = (LinkEdge) selectedEdge;
		MLink link = aEdge.getLink();
		String nomAssoc = selectedEdge.getName();

		selectAssoc(nomAssoc);

		for (MLinkEnd oLE : link.linkEnds()) {
			MClass oClass = oLE.associationEnd().cls();
			MObject obj = oLE.object();

			String className = oClass.name();
			String nomObj = obj.name();

			Object objRef = cmbObjectOri.getModel().getElementAt(0);

			MObject objeto = (MObject) objRef; // Cast al tipo real
			MClassImpl cls = (MClassImpl) objeto.cls(); // Acceder al campo fClass
			String nameClassRef = cls.name(); // Obtener el nombre

			if (className.equals(nameClassRef)) {
				selectObjInCombo(cmbObjectOri,nomObj);
			}else {
				selectObjInCombo(cmbObjectDes,nomObj);
			}
		}
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
	 * Selecciona objeto en combo
	 * @param cmb
	 * @param objName
	 */
	public void selectObjInCombo(JComboBox<MObject> cmb, String objName) {

		// Search in list
		int nObjs= cmb.getModel().getSize();
		for (int nObj=0;nObj<nObjs;nObj++) {
			MObject obj = (MObject) cmb.getModel().getElementAt(nObj);
			if (obj.name().equals(objName)) {
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
				if (f!=null) {
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

				fEvalContext = new SimpleEvalContext(preState, postState, bindings);
				EvalContext ctx=fEvalContext;

				Class<? extends Expression> cl = expr.getClass(); 

				VarDeclList elemVarDecls=null;
				Expression rangeExp=null;
				Expression queryExp=null;

				String simpleName = cl.getSimpleName();
				if (simpleName.equals("ExpForAll")) {
					ExpForAll exp2 = (ExpForAll) expr;
					elemVarDecls=exp2.getVariableDeclarations();
					fElemVarDecls=elemVarDecls;
					rangeExp=exp2.getRangeExpression();
					queryExp=exp2.getQueryExpression();
					fQueryExp=queryExp;
				}else {
					// Do nothing
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

