package org.tzi.use.gui.mvm;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//---
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
//---
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.tzi.use.gui.mvm.MVMInterpreteParser.InvDeclarationContext;
import org.tzi.use.gui.mvm.MVMInterpreteParser.LineInvContext;
import org.tzi.use.gui.views.WizardMVMView;
import org.tzi.use.main.Session;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.sys.MSystem;

public class MVMObjCheckState extends JDialog {

	/**
	 * 
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

	private ButtonGroup group;// Para alternativas

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

	private JPanel pIndicator = new JPanel();
	private JPanel pIndicatorAlt = new JPanel();

	private JTable tabObjects;
	private JScrollPane paneTabObjects;

	private JTable tabInvs;
	private JScrollPane panetabInvs;

	private JTable tabAttrs;
	private JScrollPane paneTabAttrs;

	private JPanel pProposals;
	private JScrollPane pScrollProposals ;

	private JLabel lbFileName;
	private JLabel lbDIRWRK;	
	private JTextField txFileName;

	private JButton btnExit;
	private JButton btnCompareExpr;
	private JButton btnAnalyze;
	private JButton btnSaveFile;
	private JButton btnTest;
	private JButton btnShowSource;

	private Map<MVMObject, Map<MClassInvariant, Boolean>> mapObjects;
	private boolean checkAllObjs=true;
	private boolean allObjsOk=true;

	private String strExtension="use";
	private String strExtensionProperties="properties";
	private String directoryName="";

	private String directory = "";
	private String dirWrkReplaceBodyInv="wrkReplaceBodyInv";
	private String dirWkr;
	private String fileNameWork = "WRKReplaceInv";
	private String fileNameExt = "";

	private Border etchedTitledBorder;
	private Border etchedBorder;
	private int HEIGHT_TABLE = 140;
	private int filGroupTab1=20;
	private int filGroupTab2=120;
	private int filGroupTab3=HEIGHT_TABLE+130;
	private int BLOCK_WIDTH=850;
	private int col1=10;

	private Font fontTitles;
	private Font fontTitles2;

	private enum StatesObj {
		ALL, OK, KO
	}
	private StatesObj stateObj=StatesObj.ALL;

	private enum StatesInv {
		ALL, OK, KO
	}
	private StatesInv stateInv=StatesInv.ALL;

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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);
		mapObjects = pMapObjects;
		fSession=pSession;
		thisMVMView=pThisMVMView;
		lActions=pLactions;
		listInv= new ArrayList<MVMDefInv>();

		MSystem system=fSession.system();
		fModel = fSession.system().model();
		fileName =fModel.filename();
		fileNameModelInicial=fModel.filename();

		Path path = Paths.get("");
		directoryName = path.toAbsolutePath().toString();
		dirWkr=(directoryName+"/"+dirWrkReplaceBodyInv).replace("\\", "/");

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
				loadTabObjects();
				if (tabObjects.getModel().getRowCount()>0) {
					tabObjects.setRowSelectionInterval(0, 0);
				}

				int row = tabObjects.getSelectedRow();
				loadListInvs(row);
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
				}
				loadListAttrs(row);
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
					//					System.out.println("Seleccionado (inv): " + ((JRadioButton) e.getItem()).getText());
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

		panel.add(panelGroupObj);
		panel.add(panelGroupInv);

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(col1 ,filGroupTab2-30, 130, 25);
		fontTitles = new Font(lbObjects.getFont().getName(), Font.BOLD, 18);
		fontTitles2 = new Font(lbObjects.getFont().getName(), Font.BOLD, 16);

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

		//---
		lbIndicatorAlt = new JLabel("");
		lbIndicatorAlt.setFont(fontTitles2);
		lbIndicatorAlt.setForeground(Color.WHITE);
		lbIndicatorAlt.setBackground(Color.GREEN);

		//		Border border = BorderFactory.createLineBorder(Color.BLACK);
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

		//---

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
				int row = tabObjects.rowAtPoint(evt.getPoint());
				loadListInvs(row);
				if (tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
					int nInv = tabInvs.getSelectedRow();
					showExprInv(nInv);
					showInfoLinkFromRow(nInv);
				}
				loadListAttrs(row); // nObject
				putColorInvs();
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
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(380);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(50);

		tabInvs.setDefaultEditor(Object.class, null);

		tabInvs.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int nInv = tabInvs.getSelectedRow();
				showExprInv(nInv);
				showInfoLinkFromRow(nInv);
			}
		});

		putColorInvs();

		panetabInvs = new JScrollPane(tabInvs);
		panetabInvs.setBounds(col1+320, filGroupTab2, 433, HEIGHT_TABLE);
		panel.add(panetabInvs);

		loadListAttrs(0); // nObject

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

		//-- panel para alternativas
		String borderTitle = "Proposals";
		etchedBorder = BorderFactory.createEtchedBorder();
		etchedTitledBorder = BorderFactory.createTitledBorder(etchedBorder, borderTitle);
		//-----
		pProposals = new JPanel();
		BoxLayout layout = new BoxLayout(pProposals, BoxLayout.Y_AXIS);
		pProposals.setLayout(layout);
		//----
		pScrollProposals = new JScrollPane(pProposals);
		pScrollProposals.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		pScrollProposals.setBorder(etchedTitledBorder);
		panel.add(pScrollProposals);

		lbExprInvNew = new JLabel("New Invariant body");
		lbExprInvNew.setBounds(col1, filGroupTab3+320, 250, 20);
		lbExprInvNew.setFont(fontTitles);
		panel.add(lbExprInvNew);	

		taExprInvNew = new JTextArea();
		taExprInvNew.setBounds(15, filGroupTab3+350, BLOCK_WIDTH,50 );

		taExprInvNew.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// Método llamado cuando se inserta texto en el JTextArea
				System.out.println("Texto insertado: " + taExprInvNew.getText());
				prepareContentNew();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Método llamado cuando se elimina texto del JTextArea
				System.out.println("Texto eliminado: " + taExprInvNew.getText());
				prepareContentNew();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Método llamado cuando se cambia el estilo del texto en el JTextArea
				System.out.println("Estilo de texto cambiado: " + taExprInvNew.getText());
				prepareContentNew();
			}
		});
		taExprInvNew.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// Método llamado cuando el JTextArea obtiene el foco
				System.out.println("El JTextArea ha obtenido el foco.");
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Método llamado cuando el JTextArea pierde el foco
				System.out.println("El JTextArea ha perdido el foco.");
				saveWorkFile(contentNew);	
			}
		});

		// Establecer el borde en el JTextArea
		panel.add(taExprInvNew);

		scrollpaneExprNew=new JScrollPane(taExprInvNew);    
		scrollpaneExprNew.setBounds(col1, filGroupTab3+350,BLOCK_WIDTH, 80);
		panel.add(scrollpaneExprNew);

		lbFileName = new JLabel("File Name");
		lbFileName.setBounds(col1, filGroupTab3+440,150, 20);
		panel.add(lbFileName);	

		lbDIRWRK = new JLabel("Work Directory: "+dirWkr);
		lbDIRWRK.setBounds(col1+60, filGroupTab3+440,450, 20);
		panel.add(lbDIRWRK);	

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
				//				prepareContentNew();
				//				saveWorkFile(contentNew);
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

		btnAnalyze = new JButton("Analyze");
		btnAnalyze.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				allObjsOk=true;
				analyze(); //Provis

				loadTabObjects();
				putColorObjs();
				loadListInvs(0);
				putColorInvs();
				loadListAttrs(0);

				if(tabObjects.getModel().getRowCount()>0) {
					tabObjects.setRowSelectionInterval(0, 0);
				}

				if(tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
					showExprInv(0);
				}
				showIndicator();

			}
		});
		btnAnalyze.setBounds(900, 10, 110, 80);
		panel.add(btnAnalyze);

		getContentPane().add(panel);

		if(tabInvs.getModel().getRowCount()>0) {
			tabInvs.setRowSelectionInterval(0, 0);
			//		showExprInv(0);// NO SE
			showInfoLinkFromRow(0);

		}
		//--- AQUI

		analyze_Model();		

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
			prepareContentNew();
			saveWorkFile(contentNew);
		}

	}
	private void testNewBodyInv() {
		int nInv = tabInvs.getSelectedRow();
		prepareContentNew();
		saveWorkFile(contentNew);

		String workFile = dirWkr+"/"+fileNameWork+"."+strExtension;
		String msgError=verifyContentModel(workFile);
		if (msgError.equals("")) {
			boolean bRes=ChangeContextSession(workFile);
			if (bRes) {
				// Almacena resultado de la invariante deseada
				MClassInvariant oInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
				String texto = (String) oInv.bodyExpression().toString();
				boolean stateInv = (boolean) tabInvs.getModel().getValueAt(nInv, 1);
				showIndicatorAlt(stateInv);
			}else {
				JOptionPane.showMessageDialog(null, msgError, "Error", JOptionPane.ERROR_MESSAGE);
				// poner label con  error
			}
			// Restablece fichero original
			bRes=ChangeContextSession(fileNameModelInicial);		
		}else {
			JOptionPane.showMessageDialog(null, msgError, "Error", JOptionPane.ERROR_MESSAGE);
			// poner label con  error
		}


	}
	private void prepareContentNew() {
		String newBody=taExprInvNew.getText();
		String sourceNew=contentFile;
		int nInv = tabInvs.getSelectedRow();
		if(listInv.size()>0) {
			MVMDefInv oInv = listInv.get(nInv);
			int iniBodyExpr=oInv.getIniBodyExpression();
			int finBodyExpr=oInv.getFinBodyExpression();
			String strLeft=contentFile.substring(0,iniBodyExpr);
			String strRight=contentFile.substring(finBodyExpr, contentFile.length());
			sourceNew=strLeft + newBody+  strRight;
		}
		contentNew=sourceNew;

		if (tabObjects.getModel().getRowCount()>0) {
			String oCompareName = "";
			String oCompareClass = "";	
			int row = tabObjects.getSelectedRow();
			oCompareName = (String) tabObjects.getValueAt(row, 0);
			oCompareClass = (String) tabObjects.getValueAt(row, 1);
			String nomObj="("+oCompareClass+") - "+oCompareName;

			if (tabInvs.getModel().getRowCount()>0) {
				MClassInvariant oInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
				String nomInv = (String) oInv.bodyExpression().toString();
				calcStateInvObj(nomObj, nomInv);
			}
		}

	}

	private void showSource() {

		prepareContentNew();
		saveWorkFile(contentNew);
		String sourceAct=contentFile;

		MVMShowSourceModelInvs w = new MVMShowSourceModelInvs(frame, sourceAct, contentNew);

		w.setSize(1055, 625);
		w.setLocationRelativeTo(null);
		w.setResizable(false);
		w.setVisible(true);
		// Obtener el contenido de la pantalla new
		contentNew=w.getContentNew();
	}

	private String findNewNameModel(String nameFileModel)throws Exception {
		String fileSinExtBarras=nameFileModel.replace("."+strExtension, "").replace("\\", "/");
		String fileSinExt=fileSinExtBarras.replace("\\", "/");
		String fileProposal=fileSinExtBarras; 
		String nomFile="";
		String directory="";
		System.out.println("Nombre "+nameFileModel);
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
			contador++; // Incrementa el contador en 1
			System.out.println("El contador es: " + contador);

			fileProposal=base+"_v"+contador+"."+strExtension;
			File archivo = new File(fileProposal);

			// Verificar si el archivo existe
			if (archivo.exists()) {
				System.out.println("El archivo existe.");
				exist=true;
			} else {
				System.out.println("El archivo no existe.");
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

			// Comprobar si el archivo ya existe
			if (fileToSave.exists()) {
				int overwriteResult = JOptionPane.showConfirmDialog(frame,
						"El archivo ya existe. ¿Deseas sobrescribirlo?",
						"Confirmar Sobrescritura",
						JOptionPane.YES_NO_OPTION);

				if (overwriteResult != JOptionPane.YES_OPTION) {
					// El usuario eligio no sobrescribir, salir sin guardar
					return;
				}
			}
			try {

				String nomFileSave = fileToSave.getAbsolutePath().replace("\\", "/");

				if (nomFileSave.contains("/")) {
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

				// Salvar .properties nuevo
				copyProperties(nomFileSave);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	private boolean copyProperties(String nomFileSave) {
		boolean bRes=true;
		// Leer .properties inicial
		String filePropertiesInicial=fileNameModelInicial.replace("."+strExtension, "."+strExtensionProperties);
		String filePropertiesFinal=nomFileSave.replace("."+strExtension, "."+strExtensionProperties);

		// Crear objetos File para los archivos inicial y final
		File fileInicial = new File(filePropertiesInicial);
		File fileFinal = new File(filePropertiesFinal);

		// Verificar si el archivo inicial existe
		if (fileInicial.exists()) {
			try {
				// Copiar el archivo inicial al archivo final
				Path source = fileInicial.toPath();
				Path destination = fileFinal.toPath();
				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Archivo copiado correctamente.");
			} catch (IOException e) {
				System.out.println("Error al copiar el archivo: " + e.getMessage());
				bRes=false;
			}
		} else {
			System.out.println("El archivo inicial no existe.");
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
		// txt provis para vista previa en windows para comprobaciones
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
		MClassInvariant inv = (MClassInvariant) tabInvs.getModel().getValueAt(row, 0);
		String strInv = inv.name();
		showPanelActionsLink(strInv);
	}
	private Map<String, String> doAlternatives(String strInv) {
		Map<String, String> mapAlternatives = new HashMap<String, String>();
		for (int i=0;i<10;i++) {
			mapAlternatives.put("clave"+i, "valor"+i+" de "+strInv);
		}
		TreeMap<String, String> mapSorted = new TreeMap<>(mapAlternatives);
		return mapSorted;
	}
	private void showPanelActionsLink(String strInv) {

		Map<String, String> mapSorted = doAlternatives(strInv);
		pProposals = new JPanel();
		BoxLayout layout = new BoxLayout(pProposals, BoxLayout.Y_AXIS);

		pProposals.setLayout(layout);
		group = new ButtonGroup();
		boolean pVez = true;
		int sizeAlternatives=mapSorted.size();

		int nrb=0;
		for (String clave : mapSorted.keySet()) {

			String bodyAlt = mapSorted.get(clave);
			if (bodyAlt!="") {
				JRadioButton rb = new JRadioButton (bodyAlt);
				if (pVez) {
					rb.setSelected(true);
					pVez=false;
					showAlternative(bodyAlt);
				}
				rb.setActionCommand(bodyAlt);
				rb.setName("o"+nrb);
				pProposals.add(rb);
				group.add(rb);	
				rb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showAlternative(bodyAlt);
						saveWorkFile(contentNew);
					}
				});
				nrb+=1;
			}
		}

		String borderTitle = "Alternatives ("+ sizeAlternatives+")";
		borderTitle="";//provis
		panel.remove(pScrollProposals);
		pScrollProposals = new JScrollPane(pProposals);
		pScrollProposals.setBounds(col1, filGroupTab3+150, BLOCK_WIDTH, 155);
		etchedTitledBorder = BorderFactory.createTitledBorder(etchedBorder, borderTitle);
		pScrollProposals.setBorder(etchedTitledBorder);
		panel.add(pScrollProposals);
		panel.updateUI();

	}
	private void showAlternative(String strAlt) {
		taExprInvNew.setText(strAlt);
		if (contentNew!="") {
			testNewBodyInv();
		}
	}
	//Aqui
	//--------------------------

	private void analyze_Model() {
		System.out.println("START: " + fileName);

		System.out.println("Model name "+ fModel.name());
		System.out.println("Model filename "+ fileName);
		File fileF = new File(fileName);

		directory = fileF.getParent();
		fileNameExt = fileF.getName();

		System.out.println("Directorio: " + directory);
		System.out.println("Nombre de archivo: " + fileNameExt);

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

			System.out.println("FINISH: " + fileName);
			System.out.println("--------------------------------------------------------------------------------------------");
			System.out.println("Hay ["+listInv.size()+"] invariants in file ["+fileName+"]");
			System.out.println("--------------------------------------------------------------------------------------------");
			int nInvs=listInv.size();
			for (int nInv=0;nInv<nInvs;nInv++) {
				MVMDefInv oInv = listInv.get(nInv);
				String linea=String.format("%03d", nInv);
				linea+="  [";
				linea+=String.format("%5d", oInv.getIniBodyExpression());
				linea+="-";
				linea+=String.format("%5d", oInv.getFinBodyExpression());
				linea+="]";
				linea+="  ";
				linea+=String.format("%-40s","["+oInv.getNameClass()+"::"+oInv.getNameInv()+"]");

				linea+=String.format("%-70s","["+oInv.getBodyExpression()+"]");
				System.out.println(linea);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		contentFile=contentFile.replace("<EOF>", "");
	}
	private void doRecursively(org.antlr.v4.runtime.tree.ParseTree tree) {
		for (int i = 0; i < tree.getChildCount(); i++) {
			Object child = tree.getChild(i);
			String strChild = child.toString();

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
					LineInvContext childLineINV=(LineInvContext)child;
					iniINV=contentFile.length();
				}
				doRecursively((org.antlr.v4.runtime.tree.ParseTree) child);
				if (child instanceof InvDeclarationContext) {
					InvDeclarationContext childContext=(InvDeclarationContext)child;

					finCONTEXT=contentFile.length();
					String strCONTEXT = contentFile.substring(iniCONTEXT, finCONTEXT);
					//					System.out.println("["+iniCONTEXT+"]-["+finCONTEXT+"] nameClass ["+nameClassInv+"] ---------------- CONTEXT -----------------");
					//					System.out.println("["+strCONTEXT+"]");
					//					System.out.println("----------------------------------------------------------");
					//					System.out.println();
					nameClassInv="";
				}else if (child instanceof LineInvContext) {
					LineInvContext childLineINV=(LineInvContext)child;
					String nameInv = childLineINV.nameConstraint.getText();
					finINV=contentFile.length();
					String strINV = contentFile.substring(iniINV, finINV);
					String lineINV=strINV;
					String bodyExpression = cleaninvExpression(strINV);

					//					System.out.println("["+iniINV+"]-["+finINV+"] nameClass ["+nameClassInv+"] ["+nameInv+"] *** INV ************");
					//					System.out.println("["+lineINV+"]");
					//					System.out.println("***************************************************");
					//					System.out.println("["+bodyExpression+"]");
					//					System.out.println("***************************************************");
					//					System.out.println();

					int iniBody=iniINV+strINV.indexOf(bodyExpression);
					int finBody=iniBody+bodyExpression.length();
					String strCompare = contentFile.substring(iniBody, finBody);
					if (bodyExpression.equals(strCompare)) {
						//						System.out.println("SI");
					}else {

						//						System.out.println("bodyExpression ["+bodyExpression+"]");
						//						System.out.println("strCompare     ["+strCompare+"]");
						//						System.out.println("NO");
					}

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

	private String cleaninvExpression(String invExpression) {
		String body="";
		// Localiza primer ':'
		int posiCOLON = invExpression.indexOf(":");
		String resto="";
		if (posiCOLON>0) {
			resto=invExpression.substring(posiCOLON+1, invExpression.length());
		}

		int posiHYPHEN = resto.indexOf("--");
		if (posiHYPHEN>0) {
			resto=resto.substring(0, posiHYPHEN);
		}
		body=resto.replace("\r\n","").replace("\n", "").trim();
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

	//--------------------------
	private String verifyContentModel(String fileName){
		boolean bRes=true;
		String msgError="";

		try {
			FileInputStream specStreamNew;
			PrintWriter pw=new PrintWriter(System.err);
			specStreamNew = new FileInputStream(fileName);
			//			MModel newModel = USECompiler.compileSpecification(specStreamNew,
			//					fileName, new PrintWriter(System.err),
			//					new ModelFactory());
			MModel newModel = USECompiler.compileSpecification(specStreamNew,
					fileName, pw,
					new ModelFactory());
			if (newModel==null) {
				msgError="Model no viable";;
				bRes=false;
			}
		}catch (Exception e) {
			//			e.printStackTrace();
			//			JOptionPane.showMessageDialog(null, e.getMessage(), "Model no viable", JOptionPane.ERROR_MESSAGE);
			msgError=e.getMessage();
			bRes=false;
		}
		return msgError;
	}
	private boolean ChangeContextSession(String fileName){
		boolean bRes=true;
		try {
			allObjsOk=true;
			MSystem system=fSession.system();
			MModel model = fSession.system().model();
			FileInputStream specStreamNew;
			//			String newFilename="";
			//			if (filename.indexOf("Animals4_P2_v2")>0) {
			//				newFilename=filename;
			//			}else {
			//				newFilename=filename.replace("Animals4_P2", "Animals4_P2_v2");
			//			}
			String msgError=verifyContentModel(fileName);
			if(msgError.equals("")) {
				specStreamNew = new FileInputStream(fileName);
				MModel newModel = USECompiler.compileSpecification(specStreamNew,
						fileName, new PrintWriter(System.err),
						new ModelFactory());		
				System.out.println("new Model name "+ newModel.name());
				System.out.println("new Model filename "+ newModel.filename());
				system = new MSystem(newModel);

				fSession.setSystem(system);
				thisMVMView.putSession(fSession);

				List<MVMAction> lActionsCheck=new ArrayList<MVMAction>();
				int nActions = lActions.size();
				for (int nAction=0;nAction<nActions;nAction++) {
					MVMAction oAction=lActions.get(nActions-1);
					lActionsCheck.add(nAction, oAction);
				}
				// Hay que rehacer mapObjects de MVMView
				mapObjects = thisMVMView.getMapObjects(lActionsCheck);
				System.out.println("cambiado");
				loadTabObjects();
				putColorObjs();
				loadListInvs(0);
				putColorInvs();
				loadListAttrs(0);

				if(tabObjects.getModel().getRowCount()>0) {
					tabObjects.setRowSelectionInterval(0, 0);
				}

				if(tabInvs.getModel().getRowCount()>0) {
					tabInvs.setRowSelectionInterval(0, 0);
					showExprInv(0);
				}else {
					JOptionPane.showMessageDialog(null, msgError, "Model no viable", JOptionPane.ERROR_MESSAGE);
					bRes=false;
				}
			}

			specStreamNew = new FileInputStream(fileName);
			MModel newModel = USECompiler.compileSpecification(specStreamNew,
					fileName, new PrintWriter(System.err),
					new ModelFactory());		
			System.out.println("new Model name "+ newModel.name());
			System.out.println("new Model filename "+ newModel.filename());
			system = new MSystem(newModel);

			fSession.setSystem(system);
			thisMVMView.putSession(fSession);

			List<MVMAction> lActionsCheck=new ArrayList<MVMAction>();
			int nActions = lActions.size();
			for (int nAction=0;nAction<nActions;nAction++) {
				MVMAction oAction=lActions.get(nActions-1);
				lActionsCheck.add(nAction, oAction);
			}
			// Hay que rehacer mapObjects de MVMView
			mapObjects = thisMVMView.getMapObjects(lActionsCheck);
			System.out.println("cambiado");
			loadTabObjects();
			putColorObjs();
			loadListInvs(0);
			putColorInvs();
			loadListAttrs(0);

			if(tabObjects.getModel().getRowCount()>0) {
				tabObjects.setRowSelectionInterval(0, 0);
			}

			if(tabInvs.getModel().getRowCount()>0) {
				tabInvs.setRowSelectionInterval(0, 0);
				showExprInv(0);
			}
			//			showIndicator();

		} catch (Exception e) {
			//			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Model no viable", JOptionPane.ERROR_MESSAGE);
			bRes=false;
		}
		return bRes;
	}
	private void analyze() {

		// Ver contenido de modelo
		// Obtener contenido de otro fichero
		String dir = System.getProperty("user.dir");
		System.out.println("Directorio actual: " + dir);
		MSystem system=fSession.system();
		MModel model = fSession.system().model();
		String filename =model.filename();
		System.out.println("Model name "+ model.name());
		System.out.println("Model filename "+ filename);
		File file = new File(filename);

		String directory = file.getParent();
		String fileNameExt = file.getName();

		System.out.println("Directorio: " + directory);
		System.out.println("Nombre de archivo: " + fileNameExt);


		try {
			FileInputStream specStreamNew;
			String newFilename="";
			if (filename.indexOf("Animals4_P2_v2")>0) {
				newFilename=filename;
			}else {
				newFilename=filename.replace("Animals4_P2", "Animals4_P2_v2");
			}

			specStreamNew = new FileInputStream(newFilename);
			MModel newModel = USECompiler.compileSpecification(specStreamNew,
					newFilename, new PrintWriter(System.err),
					new ModelFactory());		
			System.out.println("new Model name "+ newModel.name());
			System.out.println("new Model filename "+ newModel.filename());
			system = new MSystem(newModel);

			fSession.setSystem(system);
			thisMVMView.putSession(fSession);

			List<MVMAction> lActionsCheck=new ArrayList<MVMAction>();
			int nActions = lActions.size();
			for (int nAction=0;nAction<nActions;nAction++) {
				MVMAction oAction=lActions.get(nActions-1);
				lActionsCheck.add(nAction, oAction);
			}
			// Hay que rehacer mapObjects de MVMView
			mapObjects = thisMVMView.getMapObjects(lActionsCheck);
			System.out.println("cambiado");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void compareExprInvs() {
		TreeMap<MVMObject, Map<MClassInvariant, Boolean>> mapaOrdenado = new TreeMap<>(mapObjects);
		int rowObject = tabObjects.getSelectedRow();
		String oCompareName = (String) tabObjects.getValueAt(rowObject, 0);
		String oCompareClass = (String) tabObjects.getValueAt(rowObject, 1);
		MVMCompareExprInvs w = new MVMCompareExprInvs(frame,mapaOrdenado, rowObject,oCompareName, oCompareClass);
		w.setSize(888, 340);
		w.setLocationRelativeTo(null);
		w.setResizable(false);
		w.setVisible(true);

	}
	private void showExprInv(int nInv) {

		MClassInvariant oInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
		String texto = (String) oInv.bodyExpression().toString();
		taExprInvCurrent.setText(texto);
	}

	/**
	 * Color objects lines according to state
	 */

	private void putColorObjs() {

		DefaultTableCellRenderer renderObj = new DefaultTableCellRenderer() {
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
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean allOk=(boolean) table.getValueAt(row, 1);

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

	private void calcStateInvObj(String nomObj, String nomInv) {
		System.out.println("Calculo state para ["+nomObj+"] - inv ["+nomInv+"]");
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

				int nInv=0;
				MVMObject oObjKey = entry.getKey();
				boolean allOk=true;

				Map<MClassInvariant, Boolean> innerMap = entry.getValue();
				// Determina si todos los invs son ok o no

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
	//showIndicator();
	private void showIndicatorAlt(boolean stateInv) {
		if (stateInv) {
			pIndicatorAlt.setBackground(Color.GREEN);
			lbIndicatorAlt.setText("Correct");
		}else {
			pIndicatorAlt.setBackground(Color.RED);
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

		if (mapObjects.size()>0) {
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
					// Determina si todos los invs son ok o no
					boolean allOk=true;

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

					modeltabInvs = new DefaultTableModel(newData,columns);
					break;
				}
			}
			// Si no halla objeto
			if(!hallaObj) {
				Object[][] data = new Object[0][2];
				modeltabInvs = new DefaultTableModel(data,columns);
			}else {
				//				modeltabInvs = new DefaultTableModel(data,columns);
			}

		}else {
			Object[][] data = new Object[0][2];
			modeltabInvs = new DefaultTableModel(data,columns);
		}

		tabInvs.setModel(modeltabInvs);
		tabInvs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(380);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(50);

		putColorInvs();

		tabInvs.repaint();
		return;
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
		if(tabObjects.getModel().getRowCount()>0) {
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
		// Si no halla objeto
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
