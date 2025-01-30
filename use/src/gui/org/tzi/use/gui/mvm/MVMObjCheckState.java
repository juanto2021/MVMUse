package org.tzi.use.gui.mvm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.tzi.use.gui.mvm.MVMInterpreteParser.InvDeclarationContext;
import org.tzi.use.gui.mvm.MVMInterpreteParser.LineInvContext;
import org.tzi.use.gui.views.WizardMVMView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;

public class MVMObjCheckState extends JDialog {

	/**
	 * Object State verification
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;
	private Session fSession;

	private DefaultTableModel modelTabObjects;
	private DefaultTableModel modelTabAttrs;
	private DefaultTableModel modeltabInvs;

	private JRadioButton rbObjAll;
	private JRadioButton rbObjOk;
	private JRadioButton rbObjKo;
	private ButtonGroup groupObjs;
	private JPanel panelGroupObj;

	private JRadioButton rbInvAll;
	private JRadioButton rbInvOk;
	private JRadioButton rbInvKo;
	private ButtonGroup groupInvs;
	private JPanel panelGroupInv;

	private JRadioButton rbAltAll;
	private JRadioButton rbAltOk;
	private JRadioButton rbAltKo;
	private ButtonGroup groupAlt;
	private JPanel panelGroupAlt;

	private JTextArea taExprInvCurrent = null;
	private JTextArea taExprInvNew = null;
	private JScrollPane scrollpaneExprCurrent;
	private JScrollPane scrollpaneExprNew;

	private JTableHeader header;

	private JLabel lbObjects;
	private JLabel lbAttrs;
	private JLabel lbInvs;
	private JLabel lbExprInvCurrent;
	private JLabel lbExprInvNew;
	private JLabel lbAlt;
	private JLabel lbIndicator;
	private JLabel lbIndicatorAlt;
	private JLabel lbext;
	private JLabel lbViability;

	private JPanel pIndicator = new JPanel();
	private JPanel pIndicatorAlt = new JPanel();

	private JTable tabObjects;
	private JScrollPane paneTabObjects;

	private JTable tabInvs;
	private JScrollPane panetabInvs;

	private JTable tabAttrs;
	private JScrollPane paneTabAttrs;

	private JLabel lbFileName;
	private JLabel lbDIRWRK;
	private JLabel lbDIRWRKValue;
	private JTextField txFileName;

	private JButton btnExit;

	private JButton btnSaveFile;
	private JButton btnTest;
	private JButton btnShowSource;

	private Map<MVMObject, Map<MClassInvariant, Boolean>> mapObjects;
	private boolean checkAllObjs=true;
	private boolean allObjsOk=true;

	private JTable tableAlt;
	private TableColumn colOpAlt;
	private TableColumn colBodyInv;
	private TableColumn colStateInv;	
	private JScrollPane scrollPaneTableAlt;

	private String strExtension="use";
	private String strExtensionProperties="properties";
	private String directoryName="";

	private String directory = "";
	private String dirWrkReplaceBodyInv="wrkReplaceBodyInv";
	private String dirWkr;
	private String fileNameWork = "WRKReplaceInv";

	private Border etchedTitledBorder;
	private Border etchedBorder;
	private int HEIGHT_TABLE = 140;
	private int filGroupTab1=20;
	private int filGroupTab2=120;
	private int filGroupTab3=HEIGHT_TABLE+130;
	private int BLOCK_WIDTH=850;
	private int col1=10;

	private Font fontNormal; // Normal Font
	private Font fontTitles; // Block captions
	private Font fontTitles2;// Indicator Alt
	private Font fontTitles3;// Viability

	private enum StatesObj {
		ALL, OK, KO
	}
	private StatesObj stateObj=StatesObj.ALL;

	private enum StatesInv {
		ALL, OK, KO
	}
	private StatesInv stateInv=StatesInv.ALL;

	private enum StatesAlt {
		ALL, OK, KO
	}
	private StatesAlt stateAlt=StatesAlt.ALL;

	private WizardMVMView thisMVMView;

	private List<MVMAction> lActions;

	private PrintWriter writer;
	private String expression="";
	private String particula = "";
	private String contentFile="";
	private String contentNew="";
	private int iniCONTEXT=0;
	private int finCONTEXT=0;
	private int iniINV=0;
	private int finINV=0;
	private String nameClassInv = "";
	private int numINV=0;
	private List<MVMDefInv> listInv;

	private MModel fModel;
	private String fileName;
	private String fileNameModelInicial;

	public MVMObjCheckState(WizardMVMView pThisMVMView, 
			Map<MVMObject,Map<MClassInvariant,Boolean>> pMapObjects,
			Session pSession,
			List<MVMAction> pLactions) {	

		super(pThisMVMView.frame, "MVM Check Objects Satisfiability",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("MVM Check Objects Satisfiability");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		mapObjects = pMapObjects;
		fSession=pSession;
		thisMVMView=pThisMVMView;
		lActions=pLactions;
		listInv= new ArrayList<MVMDefInv>();

		fModel = fSession.system().model();
		fileName =fModel.filename();
		fileNameModelInicial=fModel.filename();

		Path path = Paths.get("");
		directoryName = path.toAbsolutePath().toString();
		dirWkr=(directoryName+"/"+dirWrkReplaceBodyInv).replace("\\", "/");

		// Create a File object for the directory
		File directory = new File(dirWkr);

		// Check if the directory exists
		if (!directory.exists()) {
			// Si no existe, crearlo
			if (directory.mkdirs()) {
				JOptionPane.showMessageDialog(null, "Directory ["+dirWkr+"] created successfully.\r\n(Only first time)", "Successful", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Error creating directory ["+dirWkr+"].", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}


		analyze_Model();

		// Objs options
		rbObjAll = new JRadioButton("All");
		rbObjAll.setSelected(true);
		rbObjOk = new JRadioButton("Correct");
		rbObjKo = new JRadioButton("Incorrect");

		ItemListener itemListenerObj = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String strCmp=((JRadioButton) e.getItem()).getText();
					switch (strCmp) {
					case "All":
						stateObj=StatesObj.ALL;
						break;
					case "Correct":
						stateObj=StatesObj.OK;
						break;
					case "Incorrect":
						stateObj=StatesObj.KO;
						break;
					default:
						stateObj=StatesObj.ALL;
					}
				}
				reloadComponentsModel();

				int nInv = tabInvs.getSelectedRow();
				if (nInv>-1) {
					showInfoLinkFromRow(nInv);
				}else {
					showPanelTableAlt("", nInv);
					taExprInvNew.setText("");
					lbIndicatorAlt.setText("");
					pIndicatorAlt.setBackground(Color.YELLOW);
				}

			}
		};

		rbObjAll.addItemListener(itemListenerObj);
		rbObjOk.addItemListener(itemListenerObj);
		rbObjKo.addItemListener(itemListenerObj);

		groupObjs = new ButtonGroup();

		groupObjs.add(rbObjAll);
		groupObjs.add(rbObjOk);
		groupObjs.add(rbObjKo);

		panelGroupObj = new JPanel(new FlowLayout());

		panelGroupObj.add(rbObjAll);
		panelGroupObj.add(rbObjOk);
		panelGroupObj.add(rbObjKo);
		TitledBorder titledBorderObj = new TitledBorder("Filter Objects");
		panelGroupObj.setBorder(titledBorderObj);

		// Invs options
		rbInvAll = new JRadioButton("All");
		rbInvAll.setSelected(true);
		rbInvOk = new JRadioButton("Correct");
		rbInvKo = new JRadioButton("Incorrect");

		ItemListener itemListenerInv = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String strCmp=((JRadioButton) e.getItem()).getText();
					switch (strCmp) {
					case "All":
						stateInv=StatesInv.ALL;
						break;
					case "Correct":
						stateInv=StatesInv.OK;
						break;
					case "Incorrect":
						stateInv=StatesInv.KO;
						break;
					default:
						stateInv=StatesInv.ALL;
					}
				}
				int row = tabObjects.getSelectedRow();
				loadListInvs(row);
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
					showInfoLinkFromRow(0);
				}else {
					showPanelTableAlt("", row);
					taExprInvNew.setText("");
					lbIndicatorAlt.setText("");
					pIndicatorAlt.setBackground(Color.YELLOW);
				}
			}
		};

		rbInvAll.addItemListener(itemListenerInv);
		rbInvOk.addItemListener(itemListenerInv);
		rbInvKo.addItemListener(itemListenerInv);

		groupInvs = new ButtonGroup();

		groupInvs.add(rbInvAll);
		groupInvs.add(rbInvOk);
		groupInvs.add(rbInvKo);

		panelGroupInv = new JPanel(new FlowLayout());

		panelGroupInv.add(rbInvAll);
		panelGroupInv.add(rbInvOk);
		panelGroupInv.add(rbInvKo);
		TitledBorder titledBorderInv = new TitledBorder("Filter Invariants");
		panelGroupInv.setBorder(titledBorderInv);

		// Alternatives options
		rbAltAll = new JRadioButton("All");
		rbAltAll.setSelected(true);
		rbAltOk = new JRadioButton("Correct");
		rbAltKo = new JRadioButton("Incorrect");

		ItemListener itemListenerAlt = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String strCmp=((JRadioButton) e.getItem()).getText();
					switch (strCmp) {
					case "All":
						stateAlt=StatesAlt.ALL;
						break;
					case "Correct":
						stateAlt=StatesAlt.OK;
						break;
					case "Incorrect":
						stateAlt=StatesAlt.KO;
						break;
					default:
						stateAlt=StatesAlt.ALL;
					}
				}
				// Aqui1
				int nInv = tabInvs.getSelectedRow();
				int nObj = tabObjects.getSelectedRow();
				int nInvAnt=nInv;
				int nObjAnt=nObj;

				showInfoLinkFromRow(nInv);

				nInv=nInvAnt;
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(nInv, nInv);
					loadListAttrs(nObjAnt);
				}
				showExprInv(nInv);
			}
		};

		rbAltAll.addItemListener(itemListenerAlt);
		rbAltOk.addItemListener(itemListenerAlt);
		rbAltKo.addItemListener(itemListenerAlt);

		groupAlt = new ButtonGroup();

		groupAlt.add(rbAltAll);
		groupAlt.add(rbAltOk);
		groupAlt.add(rbAltKo);

		rbAltAll.setAlignmentX(Component.LEFT_ALIGNMENT);
		rbAltOk.setAlignmentX(Component.LEFT_ALIGNMENT);
		rbAltKo.setAlignmentX(Component.LEFT_ALIGNMENT);

		panelGroupAlt = new JPanel();
		panelGroupAlt.setLayout(new BoxLayout(panelGroupAlt, BoxLayout.Y_AXIS));

		panelGroupAlt.add(rbAltAll);
		panelGroupAlt.add(rbAltOk);
		panelGroupAlt.add(rbAltKo);
		panelGroupAlt.setBorder(titledBorderInv);

		modelTabObjects = new DefaultTableModel();
		tabObjects = new JTable(modelTabObjects);

		modelTabAttrs = new DefaultTableModel();
		tabAttrs = new JTable(modelTabAttrs);

		modeltabInvs = new DefaultTableModel();
		tabInvs = new JTable(modeltabInvs);

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		Insets insets = panel.getInsets();

		col1 = 10 + insets.left;

		panelGroupObj.setBounds(col1 ,filGroupTab1, 200, 60);
		panelGroupInv.setBounds(col1+320 ,filGroupTab1, 200, 60);
		panelGroupAlt.setBounds(900 ,filGroupTab3+170, 110, 100);

		panel.add(panelGroupObj);
		panel.add(panelGroupInv);
		panel.add(panelGroupAlt);

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(col1 ,filGroupTab2-30, 130, 25);
		fontNormal = lbObjects.getFont();
		fontTitles = new Font(lbObjects.getFont().getName(), Font.BOLD, 18);
		fontTitles2 = new Font(lbObjects.getFont().getName(), Font.BOLD, 16);
		fontTitles3 = new Font(lbObjects.getFont().getName(), Font.BOLD, 16);

		lbObjects.setFont(fontTitles);
		panel.add(lbObjects);	

		lbInvs = new JLabel("Invariants");
		lbInvs.setBounds(col1+320, filGroupTab2-30, 150, 25);
		lbInvs.setFont(fontTitles);
		panel.add(lbInvs);	

		lbAttrs = new JLabel("Attributes");
		lbAttrs.setBounds(col1+760 ,filGroupTab2-30, 150, 25);
		lbAttrs.setFont(fontTitles);
		panel.add(lbAttrs);	

		lbIndicator = new JLabel("");
		lbIndicator.setFont(fontTitles);
		lbIndicator.setForeground(Color.WHITE);
		lbIndicator.setBackground(Color.GREEN);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		pIndicator = new JPanel();
		pIndicator = new JPanel(new GridBagLayout());

		pIndicator.setBounds(col1+210 ,filGroupTab1+7, 100, 51);
		pIndicator.setBorder(border);

		pIndicator.setBackground(Color.BLUE);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.CENTER;

		pIndicator.add(lbIndicator, constraints);

		panel.add(pIndicator);

		lbIndicatorAlt = new JLabel("");
		lbIndicatorAlt.setFont(fontTitles2);
		lbIndicatorAlt.setForeground(Color.WHITE);
		lbIndicatorAlt.setBackground(Color.GREEN);

		pIndicatorAlt = new JPanel();
		pIndicatorAlt = new JPanel(new GridBagLayout());
		pIndicatorAlt.setBounds(col1+210 ,filGroupTab3+320, 100, 25);
		pIndicatorAlt.setBorder(border);

		pIndicatorAlt.setBackground(Color.YELLOW);

		GridBagConstraints constraintsAlt = new GridBagConstraints();
		constraintsAlt.gridx = 0;
		constraintsAlt.gridy = 0;
		constraintsAlt.weightx = 1.0;
		constraintsAlt.weighty = 1.0;
		constraintsAlt.anchor = GridBagConstraints.CENTER;

		pIndicatorAlt.add(lbIndicatorAlt, constraintsAlt);

		panel.add(pIndicatorAlt);

		loadTabObjects();

		tabObjects = new JTable(modelTabObjects);
		tabObjects.setBounds(col1, filGroupTab2, 320, HEIGHT_TABLE);
		header = tabObjects.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabObjects.getColumnModel().getColumn(0).setPreferredWidth(130);
		tabObjects.getColumnModel().getColumn(1).setPreferredWidth(130);
		tabObjects.getColumnModel().getColumn(2).setPreferredWidth(50);

		tabObjects.setDefaultEditor(Object.class, null);

		tabObjects.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int nObj = tabObjects.rowAtPoint(evt.getPoint());
				int nObjAnt=nObj;
				loadListInvs(nObj);
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
					int nInv = tabInvs.getSelectedRow();

					showExprInv(nInv);
					showInfoLinkFromRow(nInv);
				}
				putColorInvs();
				nObj=nObjAnt;
				loadListAttrs(nObj);
				if(tabObjects.getModel().getRowCount()>0) {
					tabObjects.setRowSelectionInterval(nObj, nObj);
				}
			}
		});

		putColorObjs();

		paneTabObjects = new JScrollPane(tabObjects);
		paneTabObjects.setBounds(col1, filGroupTab2, 313, HEIGHT_TABLE);
		panel.add(paneTabObjects);
		if(tabObjects.getModel().getRowCount()>0) {
			tabObjects.setRowSelectionInterval(0, 0);
		}

		loadListInvs(0);

		tabInvs = new JTable(modeltabInvs);
		tabInvs.setBounds(col1+330, filGroupTab2, 503, HEIGHT_TABLE);
		header = tabInvs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabInvs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(360);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(50);

		tabInvs.setDefaultEditor(Object.class, null);

		tabInvs.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int nInv = tabInvs.getSelectedRow();
				int nObj = tabObjects.getSelectedRow();
				int nInvAnt=nInv;
				int nObjAnt=nObj;

				showInfoLinkFromRow(nInv);

				nInv=nInvAnt;
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(nInv, nInv);
					loadListAttrs(nObjAnt);
				}
				showExprInv(nInv);
			}
		});

		putColorInvs();

		panetabInvs = new JScrollPane(tabInvs);
		panetabInvs.setBounds(col1+320, filGroupTab2, 433, HEIGHT_TABLE);
		panel.add(panetabInvs);

		loadListAttrs(0);

		tabAttrs = new JTable(modelTabAttrs);
		tabAttrs.setBounds(col1+760, filGroupTab2, 200, HEIGHT_TABLE);
		header = tabAttrs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabAttrs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabAttrs.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabAttrs.getColumnModel().getColumn(1).setPreferredWidth(120);

		tabAttrs.setDefaultEditor(Object.class, null);

		paneTabAttrs = new JScrollPane(tabAttrs);
		paneTabAttrs.setBounds(col1+760, filGroupTab2, 243, HEIGHT_TABLE);
		panel.add(paneTabAttrs);

		if (tabAttrs.getModel().getRowCount()>0) {
			tabAttrs.setRowSelectionInterval(0, 0);
		}

		lbExprInvCurrent = new JLabel("Current Invariant body");
		lbExprInvCurrent.setBounds(col1, filGroupTab3, 250, 25);
		lbExprInvCurrent.setFont(fontTitles);
		panel.add(lbExprInvCurrent);	

		taExprInvCurrent = new JTextArea();
		taExprInvCurrent.setBounds(col1, filGroupTab3+30,BLOCK_WIDTH, 80 );
		taExprInvCurrent.setEditable(false);

		scrollpaneExprCurrent=new JScrollPane(taExprInvCurrent);    
		scrollpaneExprCurrent.setBounds(col1, filGroupTab3+30,BLOCK_WIDTH, 80);
		panel.add(scrollpaneExprCurrent);

		if(tabInvs.getModel().getRowCount()>0) {
			tabInvs.setRowSelectionInterval(0, 0);
			showExprInv(0);
		}
		lbAlt = new JLabel("Body alternatives");
		lbAlt.setBounds(col1, filGroupTab3+120, 250, 25);
		lbAlt.setFont(fontTitles);
		panel.add(lbAlt);

		String borderTitle = "";
		etchedBorder = BorderFactory.createEtchedBorder();
		etchedTitledBorder = BorderFactory.createTitledBorder(etchedBorder, borderTitle);

		tableAlt = new JTable();
		tableAlt.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		scrollPaneTableAlt = new JScrollPane(tableAlt);
		scrollPaneTableAlt.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		scrollPaneTableAlt.setBorder(etchedTitledBorder);
		panel.add(scrollPaneTableAlt, BorderLayout.CENTER);

		lbExprInvNew = new JLabel("New Invariant body");
		lbExprInvNew.setBounds(col1, filGroupTab3+320, 250, 20);
		lbExprInvNew.setFont(fontTitles);
		panel.add(lbExprInvNew);	

		taExprInvNew = new JTextArea();
		taExprInvNew.setBounds(15, filGroupTab3+350, BLOCK_WIDTH,50 );

		taExprInvNew.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// Method called when text is inserted into the JTextArea
				//				System.out.println("Texto insertado: " + taExprInvNew.getText());
				//				prepareContentNew();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Method called when text is removed from the JTextArea
				//				System.out.println("Texto eliminado: " + taExprInvNew.getText());
				//				prepareContentNew();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Method called when the text in the JTextArea is changed
				//				System.out.println("Estilo de texto cambiado: " + taExprInvNew.getText());
				//				prepareContentNew();
			}
		});
		taExprInvNew.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				lbIndicatorAlt.setText("???");
				lbIndicatorAlt.setForeground(Color.RED);	
				pIndicatorAlt.setBackground(Color.WHITE);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (contentNew!="") {
					testNewBodyInv();
				}
			}
		});


		panel.add(taExprInvNew);

		scrollpaneExprNew=new JScrollPane(taExprInvNew);    
		scrollpaneExprNew.setBounds(col1, filGroupTab3+350,BLOCK_WIDTH, 80);
		panel.add(scrollpaneExprNew);

		lbFileName = new JLabel("File Name");
		lbFileName.setBounds(col1, filGroupTab3+440,150, 20);
		panel.add(lbFileName);	

		lbViability = new JLabel("viability");
		lbViability.setBounds(col1+440, filGroupTab3+320,500, 20);
		panel.add(lbViability);	

		lbDIRWRK = new JLabel("Work Directory: ");
		lbDIRWRK.setBounds(col1+60, filGroupTab3+440,450, 20);
		panel.add(lbDIRWRK);	

		lbDIRWRKValue = new JLabel(dirWkr);
		lbDIRWRKValue.setBounds(col1+140, filGroupTab3+442,450, 20);
		lbDIRWRKValue.setForeground(new Color(0, 0, 140));
		Font courierNewFont = new Font("Courier New", Font.BOLD, 12);
		lbDIRWRKValue.setFont(courierNewFont);
		panel.add(lbDIRWRKValue);	


		txFileName = new JTextField();
		txFileName.setBounds(col1, filGroupTab3+465, 450,20 );
		txFileName.setEditable(false);
		panel.add(txFileName);

		lbext = new JLabel("."+strExtension);
		lbext.setBounds(col1+460, filGroupTab3+465	,150, 20);
		panel.add(lbext);

		btnShowSource = new JButton("Show Source");
		btnShowSource.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showSource();
			}
		});
		btnShowSource.setBounds(900, filGroupTab3+350, 110, 80);
		panel.add(btnShowSource);

		btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (contentNew!="") {
					testNewBodyInv();
				}
			}
		});

		btnTest.setBounds(col1+320, filGroupTab3+320, 110, 25);
		panel.add(btnTest);

		btnSaveFile = new JButton("Save file");
		btnSaveFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				prepareContentNew();
				saveFile();
			}
		});
		btnSaveFile.setBounds(520, filGroupTab3+465, 110, 25);
		panel.add(btnSaveFile);

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnExit.setBounds(900, filGroupTab3+465, 110, 25);
		panel.add(btnExit);

		getContentPane().add(panel);

		String fileAct=fileName;
		String nameProposal ="";
		try {
			nameProposal = findNewNameModel(fileAct);
			txFileName.setText(nameProposal);
		} catch (Exception e1) {
			nameProposal="????";
			txFileName.setText(nameProposal);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		if(tabInvs.getModel().getRowCount()>0) {
			tabInvs.setRowSelectionInterval(0, 0);
			showInfoLinkFromRow(0);
			prepareContentNew();
			saveWorkFile(contentNew);

		}
		testNewBodyInv();
	}

	private void reloadComponentsModel() {
		int nObj = tabObjects.getSelectedRow();
		int nInv = tabInvs.getSelectedRow();

		int nObjAnt=nObj;
		int nInvAnt=nInv;

		loadTabObjects();
		if (nObjAnt>=tabObjects.getModel().getRowCount()) {
			nObjAnt=tabObjects.getModel().getRowCount()-1;
		}
		putColorObjs();

		if(nObjAnt>-1) {
			loadListInvs(nObjAnt);
			loadListAttrs(nObjAnt);
		}else {
			loadListInvs(0);
			loadListAttrs(0);
		}

		putColorInvs();

		if(tabObjects.getModel().getRowCount()>0) {
			if(nObjAnt>-1) {
				tabObjects.setRowSelectionInterval(nObjAnt, nObjAnt);	
			}else {
				tabObjects.setRowSelectionInterval(0, 0);
			}
		}

		if(tabInvs.getModel().getRowCount()>0) {
			if (nInvAnt>-1) {
				tabInvs.setRowSelectionInterval(nInvAnt, nInvAnt);
				showExprInv(nInvAnt);	
			}else {
				tabInvs.setRowSelectionInterval(0, 0);
				showExprInv(0);
			}
		}
	}
	private void showViability(String msgError) {
		lbViability.setForeground(Color.BLACK);
		if(!msgError.equals("")) {
			lbViability.setFont(fontNormal);
			lbViability.setForeground(Color.RED);
			lbViability.setText(msgError);
		}else {
			String srtViable="Model viable";
			Color darkBlue = new Color(0, 0, 140);
			lbViability.setFont(fontTitles3);
			lbViability.setForeground(darkBlue);
			lbViability.setText(srtViable);
		}

	}
	private boolean testNewBodyInv() {
		boolean bResTest=false;

		int nInv = tabInvs.getSelectedRow();
		int nInvAnt=nInv;
		int nObj = tabObjects.getSelectedRow();
		int nObjAnt=nObj;
		prepareContentNew();
		saveWorkFile(contentNew);
		//----------------
		String newBody=taExprInvNew.getText();

		String workFile = dirWkr+"/"+fileNameWork+"."+strExtension;
		String msgError=verifyContentModel(workFile);
		showViability(msgError);
		if (msgError.equals("")) {
			boolean bRes=ChangeContextSession(workFile);
			// Restore nInv
			nInv=nInvAnt;

			if (bRes) {
				// Stores result of the desired invariant
				if (nInv>-1) {
					//Provis
					int nFilasInv=tabInvs.getModel().getRowCount();

					if (nFilasInv>0) {
						MClassInvariant oInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
						String texto = (String) oInv.bodyExpression().toString();
						boolean stateInv = (boolean) tabInvs.getModel().getValueAt(nInv, 1);
						showIndicatorAlt(stateInv);
						bResTest=stateInv;
					}
				}
			}else {
				showIndicatorAlt(false);

			}

			// Restore original file 
			bRes=ChangeContextSession(fileNameModelInicial);	
			// Restore nInv
			nInv=nInvAnt;
			if (tabInvs.getModel().getRowCount()>0) {
				tabInvs.setRowSelectionInterval(nInv, nInv);
				loadListAttrs(nObjAnt);
			}
		}else {
			// Restore nInv
			showIndicatorAlt(false);
			nInv=nInvAnt;
			if (tabInvs.getModel().getRowCount()>0) {
				tabInvs.setRowSelectionInterval(nInv, nInv);
				loadListAttrs(nObjAnt);
			}
		}
		return bResTest;
	}

	private void prepareContentNew() {
		String newBody=taExprInvNew.getText();
		String sourceNew=contentFile;
		int nInv = tabInvs.getSelectedRow();
		if (tabInvs.getModel().getRowCount()<=0) {
			return;
		}
		MClassInvariant oInvTab = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
		if (oInvTab==null) {
			return;
		}
		String nameClassTabInv=oInvTab.cls().name();
		String nameInvTabInv=oInvTab.name();
		if(listInv.size()>0 && nInv>-1) {
			// You have to search for oInv by comparing inv name and not based on listInv
			for (int i=0;i<listInv.size();i++) {
				MVMDefInv oInv = listInv.get(i);
				String nameClassoInv=oInv.getNameClass();
				String nameInvoInv=oInv.getNameInv();
				if(nameClassoInv.equals(nameClassTabInv)&&
						nameInvoInv.equals(nameInvTabInv)) {

					int iniBodyExpr=oInv.getIniBodyExpression();
					int finBodyExpr=oInv.getFinBodyExpression();
					String strLeft=contentFile.substring(0,iniBodyExpr);
					String strRight=contentFile.substring(finBodyExpr, contentFile.length());
					String strChange=contentFile.substring(iniBodyExpr, finBodyExpr);
					String strChanged = strChange.replace("\r\n","").replace("\n", "").trim();
					String strModified="--< Modify by MVM ["+strChanged+"]\r\n" +newBody+"\r\n"; // Simple
					sourceNew=strLeft + strModified.trim() +"\r\n" + strRight;
					break;
				}
			}
		}
		contentNew=sourceNew;
	}

	private void showSource() {

		prepareContentNew();
		saveWorkFile(contentNew);
		String sourceAct=contentFile;

		MVMShowSourceModelInvs w = new MVMShowSourceModelInvs(frame, this, sourceAct, contentNew);

		w.setSize(1055, 625);
		w.setLocationRelativeTo(null);
		w.setResizable(false);
		w.setVisible(true);
		// Get the contents of the new screen
		contentNew=w.getContentNew();
	}

	private String findNewNameModel(String nameFileModel)throws Exception {
		String fileSinExtBarras=nameFileModel.replace("."+strExtension, "").replace("\\", "/");
		String fileSinExt=fileSinExtBarras.replace("\\", "/");
		String fileProposal=fileSinExtBarras; 
		String nomFile="";
		String directory="";

		if (fileSinExt.contains("/")) {
			String[] partes = fileSinExt.split("/");
			int nPartes = partes.length;
			nomFile=partes[nPartes-1];
			for (int n=0;n<(nPartes-1);n++) {
				if (directory!="") {
					directory+="/";
				}
				directory+=partes[n];
			}

		}else {
			nomFile=fileSinExt;
			directory=directoryName;
		}

		String base=dirWkr+"/"+nomFile;

		int contador=0;
		boolean exist=false;
		do {
			contador++; // Increments the counter by 1

			fileProposal=base+"_v"+contador+"."+strExtension;
			File archivo = new File(fileProposal);

			// Check if the file exists
			if (archivo.exists()) {
				exist=true;
			} else {
				exist=false;
			}
		} while (exist && contador < 5);

		if (exist) {
			throw new Exception("It is not possible to propose a name for a new file. Too many versions");
		}

		if (fileProposal.contains("/")) {
			String[] partes = fileProposal.split("/");
			int nPartes = partes.length;
			nomFile=partes[nPartes-1];
			for (int n=0;n<(nPartes-1);n++) {
				if (directory!="") {
					directory+="/";
				}
				directory+=partes[n];
			}
		}else {
			nomFile=fileProposal;
			directory=directoryName;
		}

		String res=nomFile.replace("."+strExtension, "");
		return res;
	}

	/**
	 * Save file
	 */
	private void saveFile() {
		String nomFile=txFileName.getText();
		String nomFileFull = dirWkr+"/"+nomFile+"."+strExtension;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("File to save");   

		File directorioInicial = new File(dirWkr);
		fileChooser.setCurrentDirectory(directorioInicial);
		fileChooser.setSelectedFile(new File(nomFileFull));

		int result = fileChooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();

			// Check if file already exists
			if (fileToSave.exists()) {
				int overwriteResult = JOptionPane.showConfirmDialog(frame,
						"El archivo ya existe. Â¿Deseas sobrescribirlo?",
						"Confirmar Sobrescritura",
						JOptionPane.YES_NO_OPTION);

				if (overwriteResult != JOptionPane.YES_OPTION) {
					// User chose not to overwrite, exit without saving
					return;
				}
			}
			try {

				String nomFileSave = fileToSave.getAbsolutePath().replace("\\", "/");

				if (nomFileSave.contains("/")) {
					directory="";
					String[] partes = nomFileSave.split("/");
					int nPartes = partes.length;
					nomFile=partes[nPartes-1];
					for (int n=0;n<(nPartes-1);n++) {
						if (directory!="") {
							directory+="/";
						}
						directory+=partes[n];
					}

				}else {
					nomFile=nomFileSave;
					directory=directoryName;
				}

				String res=nomFile.replace("."+strExtension, "");
				txFileName.setText(res);

				String content = contentNew;
				writeFileOnDisk(nomFileSave, content);

				lbDIRWRKValue.setText(directory);

				// Save new .properties 
				copyProperties(nomFileSave);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	private boolean copyProperties(String nomFileSave) {
		boolean bRes=true;
		// Read initial .properties
		String filePropertiesInicial=fileNameModelInicial.replace("."+strExtension, "."+strExtensionProperties);
		String filePropertiesFinal=nomFileSave.replace("."+strExtension, "."+strExtensionProperties);

		// Create File objects for the start and end files
		File fileInicial = new File(filePropertiesInicial);
		File fileFinal = new File(filePropertiesFinal);

		// Check if the initial file exists
		if (fileInicial.exists()) {
			try {
				// Copy the initial file to the final file
				Path source = fileInicial.toPath();
				Path destination = fileFinal.toPath();
				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				bRes=false;
			}
		} else {
			bRes=false;
		}			
		return bRes;
	}

	private boolean saveWorkFile(String content) {
		boolean bRes=false;
		String workFile = dirWkr+"/"+fileNameWork+"."+strExtension;
		bRes=writeFileOnDisk(workFile, content);

		copyProperties(workFile);

		//------
		// txt provis for preview on windows for checking
		String workFileTXT = workFile.replace(("."+strExtension), ".txt");
		bRes=writeFileOnDisk(workFileTXT, content);
		//------

		return bRes;
	}

	private boolean writeFileOnDisk(String nomFile, String content) {
		boolean bRes=false;
		File fileToSave = new File(nomFile);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
			content = contentNew;
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRes;
	}

	private void showInfoLinkFromRow(int row) {
		String strInv = "";
		MClassInvariant inv = (MClassInvariant) tabInvs.getModel().getValueAt(row, 0);
		if (inv!=null) {
			strInv = inv.name();
		}

		showPanelTableAlt(strInv, row);
	}

	private Map<String, String> doAlternativesOriginal(MClassInvariant oInv) {
		Map<String, String> mapAlternatives = new HashMap<String, String>();
		try {
			Expression exp = oInv.bodyExpression();
			List<Expression> ct = computeClassifyingTerms2(exp);
			int nExpr=0;
			for(Expression item: ct) {
				String strNExpr = Integer.toString(nExpr);
				mapAlternatives.put(strNExpr, item.toString());
				nExpr+=1;
			}
		}catch (Exception e) {
			// Do nothing
		}


		TreeMap<String, String> mapSorted = new TreeMap<>(mapAlternatives);
		return mapSorted;
	}

	private Map<String, String> doAlternatives(MClassInvariant oInv) {
		Map<String, String> mapAlternatives = new HashMap<String, String>();
		try {
			Expression exp = oInv.bodyExpression();
			List<Expression> ct = computeClassifyingTerms2(exp);
			// AQUI 
			// Comprobación de las alternativas sin optimizar
			System.out.println("====================================================================================================");			
			System.out.println("Alternativas sin optimizar");
			System.out.println("====================================================================================================");
			int nExprC1=0;
			for(Expression item: ct) {
				String strNExpr = Integer.toString(nExprC1);
				//				mapAlternatives.put(strNExpr, item.toString());
				System.out.println("Num["+strNExpr+"] - ["+item.toString()+"]");
				nExprC1+=1;
			}
			System.out.println("====================================================================================================");
			// Optimizamos y simplificamos
			List<Expression> uniqueExpList = new ArrayList<>();
			Set<String> seenExpressions = new HashSet<>();

			for(Expression item: ct) {
				Expression expOpt = OptimizationVisitor.optimize(item);
				String expString = expOpt.toString(); // Convertir a cadena para comparación

				if (!seenExpressions.contains(expString)) {
					seenExpressions.add(expString);
					uniqueExpList.add(expOpt);
				}				
			}
			System.out.println("====================================================================================================");			
			System.out.println("Alternativas optimizadas");
			System.out.println("====================================================================================================");
			int nExprC2=0;
			for(Expression item: uniqueExpList) {
				String strNExpr = Integer.toString(nExprC2);
				System.out.println("Num["+strNExpr+"] - ["+item.toString()+"]");
				nExprC2+=1;
			}
			System.out.println("====================================================================================================");			

			int nExpr=0;
			//			for(Expression item: ct) { // Antes
			for(Expression item: uniqueExpList) {
				String strNExpr = Integer.toString(nExpr);
				mapAlternatives.put(strNExpr, item.toString());
				nExpr+=1;
			}
		}catch (Exception e) {
			// Do nothing
		}


		TreeMap<String, String> mapSorted = new TreeMap<>(mapAlternatives);
		return mapSorted;
	}
	private static List<Expression> computeClassifyingTerms2(Expression exp) {
		Map<String, List<VarDecl>> mapVarsByType;
		mapVarsByType= new HashMap<>(); 
		WeakenVisitor visitor = new WeakenVisitor(mapVarsByType);
		exp.processWithVisitor(visitor);
		return visitor.getMutatedExpr();
	}

	private void showAlternative(String strAlt) {
		taExprInvNew.setText(strAlt);
		if (contentNew!="") {
			testNewBodyInv();
		}
	}

	private void showPanelTableAlt(String strInv2, int nInv2) {

		Map<String, String> mapSorted = new TreeMap<>();
		String strInv ="";
		if (nInv2>-1) {
			//-- nuevo
			if (tabInvs.getModel().getRowCount()>0) {
				MClassInvariant inv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv2, 0);
				mapSorted = doAlternatives(inv);
				if (inv!=null) {
					strInv = inv.name();
				}
			}
			//--
			//			MClassInvariant inv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv2, 0);
			//			mapSorted = doAlternatives(inv);
			//			if (inv!=null) {
			//				strInv = inv.name();
			//			}

		}else {
			strInv = strInv2;
		}

		scrollPaneTableAlt.remove(tableAlt);
		tableAlt = new JTable(new CustomTableModel(mapSorted));
		tableAlt.setRowSelectionAllowed(true);  // Disable row selection

		// Setting up the renderer and editor for the radio buttons column
		tableAlt.getColumnModel().getColumn(0).setCellRenderer(new RadioButtonRenderer());
		tableAlt.getColumnModel().getColumn(0).setCellEditor(new RadioButtonEditor(new JCheckBox()));

		// Setting up the renderer and editor for JTextField columns
		tableAlt.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		tableAlt.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));

		// Setting up custom renderer for third column
		tableAlt.getColumnModel().getColumn(2).setCellRenderer(new CustomColumnRenderer());

		colOpAlt = tableAlt.getColumnModel().getColumn(0);
		colOpAlt.setPreferredWidth(60);
		colOpAlt.setMinWidth(40);
		colOpAlt.setMaxWidth(60);

		int widthBodyInv=705;
		colBodyInv = tableAlt.getColumnModel().getColumn(1);
		colBodyInv.setPreferredWidth(widthBodyInv);
		colBodyInv.setMinWidth(widthBodyInv);
		colBodyInv.setMaxWidth(widthBodyInv);

		colStateInv = tableAlt.getColumnModel().getColumn(2);
		colStateInv.setPreferredWidth(80);
		colStateInv.setMinWidth(80);
		colStateInv.setMaxWidth(80);

		tableAlt.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					int selectedRow = tableAlt.getSelectedRow();
					if (selectedRow != -1) {
						Object value = tableAlt.getValueAt(selectedRow, 1); // Columna 2 (ï¿½ndice 1)
						if (value!=null) {
							tableAlt.setValueAt(true, selectedRow, 0);
							int nInv = tabInvs.getSelectedRow();
							int nInvAnt=nInv;
							int nObj = tabObjects.getSelectedRow();
							int nObjAnt=nObj;
							String bodyAlt=value.toString();
							showAlternative(bodyAlt);
							saveWorkFile(contentNew);
							// Restore nInv
							nInv=nInvAnt;
							if (tabInvs.getModel().getRowCount()>0) {
								tabInvs.setRowSelectionInterval(nInv, nInv);
								loadListAttrs(nObjAnt);
							}
							showExprInv(nInv);
						}
					}
				}
			}
		});

		tableAlt.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		panel.remove(scrollPaneTableAlt);
		scrollPaneTableAlt = new JScrollPane(tableAlt);
		scrollPaneTableAlt.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		scrollPaneTableAlt.setBorder(etchedTitledBorder);
		panel.add(scrollPaneTableAlt, BorderLayout.CENTER);
		panel.updateUI();
		//---
		if(tableAlt.getModel().getRowCount()>0) {
			tableAlt.setRowSelectionInterval(0, 0);
			Object value = tableAlt.getValueAt(0, 1); // Columna 2 (ï¿½ndice 1)
			if (value!=null) {
				int nInv = tabInvs.getSelectedRow();
				int nInvAnt=nInv;
				int nObj = tabObjects.getSelectedRow();
				int nObjAnt=nObj;
				String bodyAlt=value.toString();
				showAlternative(bodyAlt);
				saveWorkFile(contentNew);
				// Restore nInv
				nInv=nInvAnt;
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(nInv, nInv);
					loadListAttrs(nObjAnt);
				}
				showExprInv(nInv);
				rbAltAll.setEnabled(true);
				rbAltOk.setEnabled(true);
				rbAltKo.setEnabled(true);
			}
		}else {
			rbAltAll.setEnabled(false);
			rbAltOk.setEnabled(false);
			rbAltKo.setEnabled(false);
		}

	}
	public class CustomTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Op", "Alternative", "State"};
		private Object[][] data = {};

		private int selectedRow = -1;
		private Map<String, String> mapAlt;

		public CustomTableModel(Map<String, String> pMapAlt) {
			super();
			mapAlt=pMapAlt;
			int nFilas = mapAlt.size();
			data = new Object[nFilas][3];
			int nFila=0;

			for (Map.Entry<String, String> entry : pMapAlt.entrySet()) {
				String valor = entry.getValue();
				// Calculate with the alternative to see the result

				int nInv = tabInvs.getSelectedRow();
				int nInvAnt=nInv;
				int nObj = tabObjects.getSelectedRow();
				int nObjAnt=nObj;
				String bodyAlt=valor.toString();
				showAlternative(bodyAlt);
				prepareContentNew();
				saveWorkFile(contentNew);
				// Restore nInv
				nInv=nInvAnt;
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(nInv, nInv);
					loadListAttrs(nObjAnt);
				}
				showExprInv(nInv);

				String textResult="Incorrect";
				boolean bRes=false;
				if (!contentNew.equals("")) {
					bRes=testNewBodyInv();
				}
				if (bRes) {
					textResult="Correct";
				}

				boolean saveData=false;
				switch (stateAlt) {
				case ALL:
					saveData=true;
					break;
				case OK:
					if (textResult.equals("Correct")) {
						saveData=true;
					}
					break;
				case KO:
					if (!textResult.equals("Correct")) {
						saveData=true;
					}
					break;
				default:
					saveData=true;
				}

				if (saveData) {
					data[nFila][0]=false;
					data[nFila][1]=valor;
					data[nFila][2]=textResult;
					nFila++;
				}

			}
			if (nFilas==0) {
				this.data=new Object[0][0];
				fireTableDataChanged();
			}
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return rowIndex == selectedRow;
			} else {
				return data[rowIndex][columnIndex];
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return true;
			} else {
				return rowIndex == selectedRow;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				selectedRow = rowIndex;
				fireTableDataChanged();
			} else {
				data[rowIndex][columnIndex] = aValue;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
	}

	public class RadioButtonRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JRadioButton radioButton = new JRadioButton();
			radioButton.setSelected((Boolean) value);
			radioButton.setHorizontalAlignment(SwingConstants.CENTER);
			return radioButton;
		}
	}

	public class RadioButtonEditor extends DefaultCellEditor implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JRadioButton radioButton;
		private JTable table;

		public RadioButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			radioButton = new JRadioButton();
			radioButton.setHorizontalAlignment(SwingConstants.CENTER);
			radioButton.addActionListener(this);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.table = table;
			radioButton.setSelected((Boolean) value);
			return radioButton;
		}

		@Override
		public Object getCellEditorValue() {
			return radioButton.isSelected();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int row = table.getEditingRow();
			table.getModel().setValueAt(true, row, 0);
			stopCellEditing();
		}
	}

	public class CustomColumnRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 2) {
				String cellValue = (String) value;
				if ("Correct".equals(cellValue)) {
					c.setBackground(Color.GREEN);
					c.setForeground(Color.WHITE);
				}else if ("Incorrect".equals(cellValue)) {
					c.setBackground(Color.RED);
					c.setForeground(Color.WHITE);
				}else {
					c.setBackground(Color.WHITE);
					c.setForeground(Color.BLACK);
				}
			}
			setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
			setFont(getFont().deriveFont(Font.BOLD));
			return c;
		}
	}

	private void analyze_Model() {

		File fileF = new File(fileName);

		directory = fileF.getParent();

		listInv = new ArrayList<MVMDefInv>();
		contentFile = "";
		String[] parts = fileName.replace("\\", "/").split("/");

		String filenameWithExtension = parts[parts.length - 1];
		String nameFile = filenameWithExtension.substring(0, filenameWithExtension.indexOf("."));


		org.antlr.v4.runtime.CharStream in;
		try {
			in = org.antlr.v4.runtime.CharStreams.fromFileName(fileName);

			MVMInterpreteLexer lexer = new MVMInterpreteLexer(in);
			org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);
			MVMInterpreteParser parser = new MVMInterpreteParser(tokens);
			MVMInterpreteParser.ModelContext tree = parser.model();
			MVMInterpreteCustomVisitor visitor = new MVMInterpreteCustomVisitor();
			visitor.visit(tree);

			expression="";
			particula = "";

			writer = new PrintWriter("C://temp//"+nameFile+".ran", "UTF-8");
			doRecursively(tree);
			// Cerrar el archivo
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		contentFile=contentFile.replace("<EOF>", "");
	}
	private void doRecursively(org.antlr.v4.runtime.tree.ParseTree tree) {
		for (int i = 0; i < tree.getChildCount(); i++) {
			Object child = tree.getChild(i);

			if (child instanceof org.antlr.v4.runtime.tree.TerminalNodeImpl) {
				String partNL=((org.antlr.v4.runtime.tree.TerminalNodeImpl)child).getText();
				doTerminalNodeImpl(child);
				if (partNL.equals("\r\n")||partNL.equals("\n")||partNL.equals("<EOF>")){
					expression=expression.replace("<EOF>", "");
					wit(expression); // Escribe el contexto en el archivo
					expression="";
				}
			}else {
				if (child instanceof InvDeclarationContext) {
					InvDeclarationContext childContext=(InvDeclarationContext)child;
					nameClassInv=childContext.nameClassInv.getText();
					iniCONTEXT=contentFile.length();
				}else if (child instanceof LineInvContext) {
					iniINV=contentFile.length();
				}
				doRecursively((org.antlr.v4.runtime.tree.ParseTree) child);
				if (child instanceof InvDeclarationContext) {
					finCONTEXT=contentFile.length();
					nameClassInv="";
				}else if (child instanceof LineInvContext) {
					LineInvContext childLineINV=(LineInvContext)child;
					String nameInv = childLineINV.nameConstraint.getText();
					finINV=contentFile.length();
					String strINV = contentFile.substring(iniINV, finINV);
					String bodyExpression = cleaninvExpression(strINV);

					int iniBody=iniINV+strINV.indexOf(bodyExpression);
					int finBody=iniBody+bodyExpression.length();

					numINV+=1;
					MVMDefInv oInv = new MVMDefInv();
					oInv.setOrden(numINV);
					oInv.setNameClass(nameClassInv);
					oInv.setNameInv(nameInv);
					oInv.setRawExpression(strINV);
					oInv.setBodyExpression(bodyExpression);
					oInv.setIniRawExpression(iniINV);
					oInv.setFinRawExpression(finINV);
					oInv.setIniBodyExpression(iniBody);
					oInv.setFinBodyExpression(finBody);					

					listInv.add(oInv);
				}
			}

		}
	}

	public static String cleanBlock(String block) {
		StringBuilder result = new StringBuilder();
		// Find first ':'
		int posiCOLON = block.indexOf(":");
		String resto="";
		if (posiCOLON>0) {
			resto=block.substring(posiCOLON+1, block.length());
		}
		String block1 = resto.replace("\r\n", "\n");
		// Split the block into individual lines
		String[] lines = block1.split("\n");

		for (String line : lines) {
			// Remove everything after '--' (inclusive) and any preceding spaces
			String cleanedLine = line.split("--")[0].trim();

			// Only add the line if it is not empty
			if (!cleanedLine.isEmpty()) {
				result.append(cleanedLine).append("\r\n");
			}
		}

		return result.toString();
	}

	//--- old
	//	private static String cleaninvExpression(String invExpression) {
	//		String body = cleanBlock(invExpression);
	//		return body.replace("\r\n","").replace("\n", "");
	//	}
	//--- old

	private static String cleaninvExpression(String invExpression) {
		String body="";
		// Find first ':'
		int posiCOLON = invExpression.indexOf(":");
		String resto="";
		if (posiCOLON>0) {
			resto=invExpression.substring(posiCOLON+1, invExpression.length());
		}

		int posiHYPHEN = resto.indexOf("--");
		if (posiHYPHEN>0) {
			resto=resto.substring(0, posiHYPHEN);
		}
		//		body=resto.replace("\r\n","").replace("\n", "").trim();//Old
		body=resto;
		return body;
	}

	private void doTerminalNodeImpl(Object child) {
		org.antlr.v4.runtime.tree.TerminalNodeImpl child2 = ((org.antlr.v4.runtime.tree.TerminalNodeImpl)child);
		particula = ((org.antlr.v4.runtime.tree.TerminalNodeImpl) child2).getText(); // Obtiene el texto del contexto
		expression+=particula;
		contentFile+= particula;
	}

	private void wit(String line) {
		//		System.out.print(line);			
		writer.print(line);
	}

	private String verifyContentModel(String fileName){
		String msgError="";

		try {
			FileInputStream specStreamNew;
			StringWriter stringWriter = new StringWriter();
			PrintWriter pw=new PrintWriter(stringWriter);
			specStreamNew = new FileInputStream(fileName);

			MModel newModel = USECompiler.compileSpecification(specStreamNew,
					fileName, pw,
					new ModelFactory());
			String contenido = stringWriter.toString();
			if (newModel==null) {
				String error = contenido.replace(fileName+":", "");
				msgError="Non-viable model !!! ["+error+"]";
			}
		}catch (Exception e) {
			msgError=e.getMessage();
		}
		return msgError;
	}
	private boolean ChangeContextSession(String fileName){
		boolean bRes=true;
		try {
			allObjsOk=true;
			MSystem system=fSession.system();
			FileInputStream specStreamNew;

			String msgError=verifyContentModel(fileName);
			showViability(msgError);
			if(msgError.equals("")) {
				specStreamNew = new FileInputStream(fileName);
				MModel newModel = USECompiler.compileSpecification(specStreamNew,
						fileName, new PrintWriter(System.err),
						new ModelFactory());		
				system = new MSystem(newModel);

				fSession.setSystem(system);
				thisMVMView.putSession(fSession);

				List<MVMAction> lActionsCheck=new ArrayList<MVMAction>();
				int nActions = lActions.size();
				for (int nAction=0;nAction<nActions;nAction++) {
					MVMAction oAction=lActions.get(nActions-1);
					lActionsCheck.add(nAction, oAction);
				}
				// MVMView mapObjects need to be redone
				mapObjects = thisMVMView.getMapObjects(lActionsCheck);

				reloadComponentsModel();
			}else {
				bRes=false;
			}

			specStreamNew = new FileInputStream(fileName);
			MModel newModel = USECompiler.compileSpecification(specStreamNew,
					fileName, new PrintWriter(System.err),
					new ModelFactory());		
			system = new MSystem(newModel);

			fSession.setSystem(system);
			thisMVMView.putSession(fSession);

			List<MVMAction> lActionsCheck=new ArrayList<MVMAction>();
			int nActions = lActions.size();
			for (int nAction=0;nAction<nActions;nAction++) {
				MVMAction oAction=lActions.get(nActions-1);
				lActionsCheck.add(nAction, oAction);
			}
			// MVMView mapObjects need to be redone
			mapObjects = thisMVMView.getMapObjects(lActionsCheck);

			reloadComponentsModel();
		}catch (FileNotFoundException e) {
			lbViability.setText("File not found: "+e.getMessage());
			bRes=false;
		}catch (Exception e) {
			bRes=false;
		}
		return bRes;
	}

	private void showExprInv(int nInv) {
		String texto = "";
		if (nInv>-1) {
			if (tabInvs.getModel().getRowCount()>0) {
				try {
					MClassInvariant oInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
					texto = (String) oInv.bodyExpression().toString();
				}catch (Exception e) {
					// Do nothing
				}
			}
		}
		taExprInvCurrent.setText(texto);
	}

	/**
	 * Color objects lines according to state
	 */

	private void putColorObjs() {

		DefaultTableCellRenderer renderObj = new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean allOk=(boolean) table.getValueAt(row, 2);

				if (allOk == false) {
					c.setForeground(Color.RED);
				} else {
					c.setForeground(Color.BLACK);
				}

				return c;
			}
		};

		tabObjects.getColumnModel().getColumn(2).setCellRenderer(renderObj);
	}

	/**
	 * Color invs lines according to state
	 */

	private void putColorInvs() {

		DefaultTableCellRenderer renderInv = new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean allOk=true;
				Object o = table.getValueAt(row, 1);
				if (o!=null) {
					allOk=(boolean) table.getValueAt(row, 1);
				}

				if (allOk == false) {
					c.setForeground(Color.RED);
				} else {
					c.setForeground(Color.BLACK);
				}

				return c;
			}
		};

		tabInvs.getColumnModel().getColumn(1).setCellRenderer(renderInv);

	}

	/**
	 * Load object table
	 */

	private void loadTabObjects() {

		modelTabObjects = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"Object", "Class", "Satisfied "
		};

		if (mapObjects.size()>0) {

			int nObjs = mapObjects.size();

			Object[][] data = new Object[nObjs][3];

			int nObj=0;
			for (Map.Entry<MVMObject, Map<MClassInvariant, Boolean>> entry : mapObjects.entrySet()) {

				MVMObject oObjKey = entry.getKey();
				boolean allOk=true;

				Map<MClassInvariant, Boolean> innerMap = entry.getValue();
				// Determines whether all invs are ok or not

				for (Map.Entry<MClassInvariant, Boolean> innerEntry : innerMap.entrySet()) {
					MClassInvariant inv = innerEntry.getKey();
					Boolean value = innerEntry.getValue();
					if (value==false) {
						allOk=false;
						if (checkAllObjs) {
							allObjsOk=false;
						}
						break;
					}
				}
				boolean saveData=false;
				switch (stateObj) {
				case ALL:
					saveData=true;
					break;
				case OK:
					if (allOk) {
						saveData=true;
					}
					break;
				case KO:
					if (!allOk) {
						saveData=true;
					}
					break;
				default:
					saveData=true;
				}

				if (saveData) {
					data[nObj][0]=oObjKey.getName();
					data[nObj][1]=oObjKey.getClassName();
					data[nObj][2]=allOk;	
					nObj++;
				}

			}
			// Resize data result
			Object[][] newData = new Object[nObj][3];

			for (int i = 0; i < nObj; i++) {
				for (int j = 0; j < 3; j++) {
					newData[i][j] = data[i][j];
				}
			}

			modelTabObjects = new DefaultTableModel(newData,columns);
		}else {
			Object[][] data = new Object[0][3];
			modelTabObjects = new DefaultTableModel(data,columns);
		}

		tabObjects.setModel(modelTabObjects);
		tabObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabObjects.getColumnModel().getColumn(0).setPreferredWidth(130);
		tabObjects.getColumnModel().getColumn(1).setPreferredWidth(130);
		tabObjects.getColumnModel().getColumn(2).setPreferredWidth(50);

		putColorObjs();

		tabObjects.repaint();
		showIndicator();
		return;
	}

	private void showIndicator() {
		if (stateObj==stateObj.ALL) {
			if (allObjsOk) {
				pIndicator.setBackground(Color.GREEN);
				lbIndicator.setText("Correct");
			}else {
				pIndicator.setBackground(Color.RED);
				lbIndicator.setText("Incorrect");
			}

		}
	}

	private void showIndicatorAlt(boolean stateInv) {
		if (stateInv) {
			pIndicatorAlt.setBackground(Color.GREEN);
			lbIndicatorAlt.setForeground(Color.WHITE);
			lbIndicatorAlt.setText("Correct");
		}else {
			pIndicatorAlt.setBackground(Color.RED);
			lbIndicatorAlt.setForeground(Color.WHITE);
			lbIndicatorAlt.setText("Incorrect");
		}
	}
	/**
	 * Load invs table
	 */

	private void loadListInvs(int nObject) {
		modeltabInvs = new DefaultTableModel();
		String[] columns;

		columns = new String[] {
				"Invariant", "Satisfied "
		};

		if (mapObjects.size()>0 && nObject>-1) {
			String oCompareName = "";
			String oCompareClass = "";
			if (tabObjects.getModel().getRowCount()>0) {
				oCompareName = (String) tabObjects.getValueAt(nObject, 0);
				oCompareClass = (String) tabObjects.getValueAt(nObject, 1);
			}
			int nInv=0;
			boolean hallaObj=false;
			for (Map.Entry<MVMObject, Map<MClassInvariant, Boolean>> entry : mapObjects.entrySet()) {
				MVMObject oObjKey = entry.getKey();
				if (oObjKey.getClassName().equals(oCompareClass) && oObjKey.getName().equals(oCompareName) ) {
					hallaObj=true;
					Map<MClassInvariant, Boolean> innerMap = entry.getValue();
					TreeMap<MClassInvariant, Boolean> mapaOrdenado = new TreeMap<>(innerMap);
					int nInvs = innerMap.size();
					Object[][] data = new Object[nInvs][2];
					// Determines whether all invs are ok or not

					for (Map.Entry<MClassInvariant, Boolean> innerEntry : mapaOrdenado.entrySet()) {
						MClassInvariant inv = innerEntry.getKey();
						Boolean value = innerEntry.getValue();
						boolean saveData=false;
						switch (stateInv) {
						case ALL:
							saveData=true;
							break;
						case OK:
							if (value) {
								saveData=true;
							}
							break;
						case KO:
							if (!value) {
								saveData=true;
							}
							break;
						default:
							saveData=true;
						}
						if (saveData) {
							data[nInv][0]=inv;
							data[nInv][1]=value;
							nInv++;	
						}
					}

					// Resize data result
					Object[][] newData = new Object[nInv][2];

					for (int i = 0; i < nInv; i++) {
						for (int j = 0; j < 2; j++) {
							newData[i][j] = data[i][j];
						}
					}

					modeltabInvs = new DefaultTableModel(newData,columns);//Provis
					break;
				}
			}
			// If there is no object...
			if(!hallaObj) {
				Object[][] data = new Object[0][2];
				modeltabInvs = new DefaultTableModel(data,columns);
				//			}else {
				//				//				modeltabInvs = new DefaultTableModel(data,columns);
			}

		}else {
			Object[][] data = new Object[0][2];
			modeltabInvs = new DefaultTableModel(data,columns);
		}

		if (modeltabInvs.getColumnCount()>0) {
			sortTableModel2(columns);
		}

		tabInvs.setModel(modeltabInvs);
		tabInvs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(365);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(50);

		putColorInvs();

		tabInvs.repaint();
		return;
	}

	private void sortTableModel2(String[] columns) {
		int nInvs = listInv.size();
		int nInvsModel = modeltabInvs.getRowCount();
		Object[][] data = new Object[nInvsModel][2];
		for (int nInv=0;nInv<nInvs;nInv++) {
			MVMDefInv oInv = listInv.get(nInv);
			String nameClassInv = oInv.getNameClass();
			String nameInv = oInv.getNameInv();
			// Find in model
			for (int nInvModel=0;nInvModel<nInvsModel;nInvModel++) {
				MClassInvariant oInvModel = (MClassInvariant) modeltabInvs.getValueAt(nInvModel, 0);
				String nameClassInvModel = oInvModel.cls().name();
				String nameInvModel = oInvModel.name();
				boolean value=(boolean) modeltabInvs.getValueAt(nInvModel, 1);
				if (nameClassInv.equals(nameClassInvModel) &&
						nameInv.equals(nameInvModel)) {
					data[nInvModel][0]=oInvModel;
					data[nInvModel][1]=value;
					break;
				}
			}

		}
		modeltabInvs.setRowCount(0);
		modeltabInvs = new DefaultTableModel(data,columns);//Provis
	}

	public static void sortTableModel(DefaultTableModel model, int colIndex) {
		// Extract data from the model
		ArrayList<Vector> tableData = new ArrayList<>();
		for (int row = 0; row < model.getRowCount(); row++) {
			Vector rowData = (Vector) model.getDataVector().elementAt(row);
			tableData.add(rowData);
		}

		// Sort data based on the specified column index
		Collections.sort(tableData, new Comparator<Vector>() {
			@Override
			public int compare(Vector row1, Vector row2) {
				Comparable col1 = (Comparable) row1.get(colIndex);
				Comparable col2 = (Comparable) row2.get(colIndex);
				return col1.compareTo(col2);
			}
		});

		// Remove all rows from the model
		model.setRowCount(0);

		// Add sorted data back to the model
		for (Vector rowData : tableData) {
			model.addRow(rowData);
		}
	}

	/**
	 * Load attrs table
	 */
	private void loadListAttrs(int nObj) {
		modelTabAttrs = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"Attribute", "Value"
		};

		boolean hallaObj=false;
		if(tabObjects.getModel().getRowCount()>0 && nObj>-1) {
			String oCompareName = (String) tabObjects.getValueAt(nObj, 0);
			String oCompareClass = (String) tabObjects.getValueAt(nObj, 1);
			for (Map.Entry<MVMObject, Map<MClassInvariant, Boolean>> entry : mapObjects.entrySet()) {
				MVMObject oObject = entry.getKey();
				if (oObject.getClassName().equals(oCompareClass) && oObject.getName().equals(oCompareName) ) {
					hallaObj=true;
					List<MVMAttribute> lAttrs = oObject.getAttributes();
					int nAttrs = lAttrs.size();
					Object[][] data = new Object[nAttrs][2];
					for(int nAttr=0;nAttr<nAttrs;nAttr++) {
						MVMAttribute oAttribute=lAttrs.get(nAttr);
						data[nAttr][0]=oAttribute.getName();
						data[nAttr][1]=oAttribute.getValue();
					}
					modelTabAttrs = new DefaultTableModel(data,columns);
					break;
				}
			}
		}
		// If there is no object...
		if(!hallaObj) {
			Object[][] data = new Object[0][2];
			modelTabAttrs = new DefaultTableModel(data,columns);
		}

		tabAttrs.setModel(modelTabAttrs);
		tabAttrs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabAttrs.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabAttrs.getColumnModel().getColumn(1).setPreferredWidth(120);
		tabAttrs.repaint();
		return;
	}
}
