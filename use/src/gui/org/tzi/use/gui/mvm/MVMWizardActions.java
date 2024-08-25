package org.tzi.use.gui.mvm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MLink;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MVMWizardActions extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;

	private JLabel lbFileName = new JLabel("File Name");
	private JLabel lbExtension = new JLabel(".mva");
	private JLabel lbCreationDate = new JLabel("Creation Date");
	private JLabel lbModificationDate = new JLabel("Mod. Date");
	private JLabel lbModel = new JLabel("Model");
	private JLabel lbSourceUSE = new JLabel("Source USE File");
	private JLabel lbDescription = new JLabel("Description");

	private JTextField txFileName = null;
	private JTextField txCreationDate = null;
	private JTextField txModificationDate = null;
	private JTextField txModel = null;
	private JTextField txSourceUSE = null;
	private JTextField txDescription = null;

	public String sFileName = null;
	public Date sCreationDate = null;
	public Date sModificationDate = null;
	public String sModel = null;
	public String sSourceUSE = null;
	public String sDescription = null;

	private DefaultTableModel modelTabActions;
	private DefaultTableModel modelTabObjects;
	private DefaultTableModel modelTabAttrs;
	private DefaultTableModel modelTabLinks;

	private JTableHeader header;

	private JLabel lbActions;
	private JLabel lbObjects;
	private JLabel lbAttrs;
	private JLabel lbLinks;

	private JTable tabActions;
	private JScrollPane paneTabActions;

	private JTable tabObjects;
	private JScrollPane paneTabObjects;

	private JTable tabAttrs;
	private JScrollPane paneTabAttrs;

	private JTable tabLinks;
	private JScrollPane paneTabLinks;

	private JButton btnFind;
	private JButton btnOpen;
	private JButton btnSave;
	private JButton btnLoad;
	private JButton btnExit;

	private String strLastFile="";
	private List<MVMAction> lActions = new ArrayList<MVMAction>();
	private List<MVMObject> lObjsPral = new ArrayList<MVMObject>();
	private List<MVMLink> lLinksPral = new ArrayList<MVMLink>();
	private MVMGroupActions grPral = new MVMGroupActions();
	private MVMGroupActions grPralAnt = new MVMGroupActions();
	private List<MVMAction> lActionsRes=null;
	private Session fSession;

	private String groupActionsFolder = "groupActions";
	String directoryName="";
	String strExtension="mva";

	public MVMWizardActions(JFrame fParent, List<MVMAction> pLActions, List<MVMObject> pLObjs, List<MVMLink> pLLinks,
			String pModel,String pSourceUSE, String pLastFile , Session pSession) {
		super(fParent, "Prototipo MVM Wizard Actions",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("Prototipo MVM Wizard Actions");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);

		lActions=pLActions;
		lObjsPral=pLObjs;
		lLinksPral=pLLinks;

		sModel = pModel;
		sSourceUSE = pSourceUSE;
		strLastFile = pLastFile;
		fSession=pSession;

		cargaDatos();

		Path path = Paths.get("");
		directoryName = path.toAbsolutePath().toString()+"\\"+groupActionsFolder;

		txFileName = new JTextField();

		txCreationDate = new JTextField();
		txCreationDate.setEditable(false);

		txModificationDate = new JTextField();
		txModificationDate.setEditable(false);

		txDescription = new JTextField();

		txModel = new JTextField();
		txModel.setEditable(false);

		txSourceUSE = new JTextField();
		txSourceUSE.setEditable(false);

		modelTabActions = new DefaultTableModel();
		tabActions = new JTable(modelTabActions);

		modelTabObjects = new DefaultTableModel();
		tabObjects = new JTable(modelTabObjects);

		modelTabAttrs = new DefaultTableModel();
		tabAttrs = new JTable(modelTabAttrs);

		modelTabLinks = new DefaultTableModel();
		tabLinks = new JTable(modelTabLinks);

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		panel.add(lbFileName);
		panel.add(lbExtension);
		panel.add(lbCreationDate);
		panel.add(lbModificationDate);
		panel.add(lbDescription);
		panel.add(lbModel);
		panel.add(lbSourceUSE);

		panel.add(txFileName);
		panel.add(txCreationDate);
		panel.add(txModificationDate);
		panel.add(txDescription);
		panel.add(txModel);
		panel.add(txSourceUSE);

		Insets insets = panel.getInsets();

		int col1 = 15 + insets.left;
		int lbHeight=20;
		int txHeight=20;

		lbFileName.setBounds(col1, 15 + insets.top,150, lbHeight);
		lbExtension.setBounds(col1+125, 40 + insets.top,40, lbHeight);
		lbCreationDate.setBounds(col1+185, 15 + insets.top,150, lbHeight);
		lbModificationDate.setBounds(col1+315, 15 + insets.top,150 + 50, lbHeight);
		lbModel.setBounds(col1+445, 15 + insets.top,100, lbHeight);
		lbSourceUSE.setBounds(col1+558, 15 + insets.top,200, lbHeight);
		lbDescription.setBounds(col1, 75 + insets.top,200, lbHeight);

		txFileName.setBounds(col1, 40 + insets.top,120, txHeight );
		txCreationDate.setBounds(col1+185, 40 + insets.top,120, txHeight );
		txModificationDate.setBounds(col1+315, 40 + insets.top,120, txHeight );
		txModel.setBounds(col1+445, 40 + insets.top,102, txHeight );
		txSourceUSE.setBounds(col1+558, 40 + insets.top,400, txHeight );
		txDescription.setBounds(col1+80, 75 + insets.top,489, txHeight );

		int filGroupTab1=140;
		int filGroupTab2=320;

		lbActions = new JLabel("Actions");
		lbActions.setBounds(col1, filGroupTab1-30, 150, 25);
		Font newFont = new Font(lbActions.getFont().getName(), Font.BOLD, 18);
		lbActions.setFont(newFont);
		panel.add(lbActions);		

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(col1+390 ,filGroupTab1-30, 150, 25);
		lbObjects.setFont(newFont);
		panel.add(lbObjects);	

		lbAttrs = new JLabel("Attributes");
		lbAttrs.setBounds(col1+600 ,filGroupTab1-30, 150, 25);
		lbAttrs.setFont(newFont);
		panel.add(lbAttrs);	

		lbLinks = new JLabel("Links");
		lbLinks.setBounds(col1, filGroupTab2-30, 150, 25);
		lbLinks.setFont(newFont);
		panel.add(lbLinks);	

		loadListActions(lActions);

		tabActions = new JTable(modelTabActions);
		tabActions.setBounds(col1, filGroupTab1, 302, 140);
		header = tabActions.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabActions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabActions.getColumnModel().getColumn(0).setPreferredWidth(40);
		tabActions.getColumnModel().getColumn(1).setPreferredWidth(40);
		tabActions.getColumnModel().getColumn(2).setPreferredWidth(300);

		tabActions.setDefaultEditor(Object.class, null);

		tabActions.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = tabActions.rowAtPoint(evt.getPoint());
				if (evt.getClickCount() == 1)
				{
					showDataAction(row);
				}
				if (evt.getClickCount() == 2)
				{
					prepareListActions();
					dispose();
				}
			}
		});

		paneTabActions = new JScrollPane(tabActions);
		paneTabActions.setBounds(col1, filGroupTab1, 383, 140);
		panel.add(paneTabActions);

		loadListObjects(0);

		tabObjects = new JTable(modelTabObjects);
		tabObjects.setBounds(col1+388, filGroupTab1, 200, 140);
		header = tabObjects.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabObjects.getColumnModel().getColumn(0).setPreferredWidth(100);
		tabObjects.getColumnModel().getColumn(1).setPreferredWidth(100);

		tabObjects.setDefaultEditor(Object.class, null);

		tabObjects.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = tabObjects.rowAtPoint(evt.getPoint());
				int nAction = tabActions.getSelectedRow();
				loadListAttrs(nAction,row);
			}
		});

		paneTabObjects = new JScrollPane(tabObjects);
		paneTabObjects.setBounds(col1+388, filGroupTab1, 203, 140);
		panel.add(paneTabObjects);

		loadListAttrs(0, 0); // nAction, nObject

		tabAttrs = new JTable(modelTabAttrs);
		tabAttrs.setBounds(col1+595, filGroupTab1, 200, 140);
		header = tabAttrs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabAttrs.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tabAttrs.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabAttrs.getColumnModel().getColumn(1).setPreferredWidth(120);

		tabAttrs.setDefaultEditor(Object.class, null);

		paneTabAttrs = new JScrollPane(tabAttrs);
		paneTabAttrs.setBounds(col1+595, filGroupTab1, 243, 140);
		panel.add(paneTabAttrs);

		loadListLinks(0);

		tabLinks = new JTable(modelTabLinks);
		tabLinks.setBounds(col1, filGroupTab2, 960, 140);
		header = tabLinks.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabLinks.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabLinks.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(1).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(2).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(3).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(4).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(5).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(6).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(7).setPreferredWidth(120);

		tabLinks.setDefaultEditor(Object.class, null);

		paneTabLinks = new JScrollPane(tabLinks);
		paneTabLinks.setBounds(col1, filGroupTab2, 963, 140);
		panel.add(paneTabLinks);

		btnOpen = new JButton("Open actions");
		btnOpen.setBounds(866, 75, 110, 25);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
				showData();
			}
		});
		panel.add(btnOpen);

		btnFind = new JButton("Find actions");
		btnFind.setBounds(866, 110, 110, 25);
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String modelRef=pModel;
				MVMFindActions w = new MVMFindActions(frame, strLastFile, modelRef);

				w.setSize(1068, 410);
				w.setLocationRelativeTo(null);
				w.setVisible(true);
				String nomFileRes=w.getNomFile();
				if (nomFileRes!="") {
					String nomFile = directoryName+"/"+nomFileRes;
					grPral = readMVMGroup(nomFile);
					copyGrPralToGrPralAnt();

					if (!debugsClassesAndAssociations(grPral)){
						copyGrPralAntToGrPral();
					}else {
						showData();
						int indicePunto = nomFileRes.indexOf('.');
						String parteIzquierda = indicePunto != -1 ? nomFileRes.substring(0, indicePunto) : nomFileRes;
						strLastFile=parteIzquierda;
					}
				}
			}
		});
		panel.add(btnFind);

		btnSave = new JButton("Save actions");
		btnSave.setBounds(866, 145, 110, 25);
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		panel.add(btnSave);

		txFileName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				String previousText = (String) e.getDocument().getProperty("previousText");
				try {
					validateFileName();
					txFileName.getDocument().putProperty("previousText", txFileName.getText());
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					SwingUtilities.invokeLater(() ->
					txFileName.setText(previousText));
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String previousText = (String) e.getDocument().getProperty("previousText");
				try {
					validateFileName();
					txFileName.getDocument().putProperty("previousText", txFileName.getText());
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					SwingUtilities.invokeLater(() ->
					txFileName.setText(previousText));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				String previousText = (String) e.getDocument().getProperty("previousText");
				try {
					validateFileName();
					txFileName.getDocument().putProperty("previousText", txFileName.getText());
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					SwingUtilities.invokeLater(() ->
					txFileName.setText(previousText));
				}
			}
		});

		btnLoad = new JButton("Load actions");
		btnLoad.setBounds(866, 200, 110, 25);

		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prepareListActions();
				dispose();
			}
		});

		panel.add(btnLoad);


		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lActionsRes=null;
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnExit.setBounds(866, 470, 110, 25);
		panel.add(btnExit);

		getContentPane().add(panel);
		showData();
	}

	private void validateFileName() throws Exception{
		String inputText = txFileName.getText();
		try {
			String charsNoValid="!�$%&/()=?�'�*+^�`[]�{}�:.;,<>\\\"";
			for(int c=0;c<charsNoValid.length();c++) {
				String car = String.valueOf(charsNoValid.charAt(c));
				if (inputText.contains(car)) {
					throw new Exception("character ["+car+"] no valid"); 
				}
			}

			txFileName.setForeground(java.awt.Color.BLACK);
			btnSave.setEnabled(!txFileName.getText().isEmpty());
		}catch(Exception e) {
			txFileName.setForeground(java.awt.Color.RED);
			btnSave.setEnabled(false);
			throw new Exception(e.getMessage());

		}
	}

	private void showData() {
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		lActions=grPral.getlActions();
		sFileName = grPral.getnameGroup();
		sCreationDate = grPral.getCreationDate();
		String strCreationDate = date.format(sCreationDate);

		sModificationDate = grPral.getLastModifiedDate();
		String strModificationDate = date.format(sModificationDate);

		sModel = grPral.getModelName();
		sSourceUSE = grPral.getSrcFileUSE();
		sDescription = grPral.getDescription();

		txFileName.setText(sFileName);
		txCreationDate.setText(strCreationDate);
		txModificationDate.setText(strModificationDate);
		txModel.setText(sModel);
		txSourceUSE.setText(sSourceUSE);
		txDescription.setText(sDescription);

		loadListActions(lActions);
		int nActions = lActions.size();
		if (nActions>0) {
			showDataAction(nActions-1);
		}
	}

	private void openFile() {
		copyGrPralToGrPralAnt();
		String strLastFileAnt = strLastFile;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("File to open"); 
		fileChooser.setCurrentDirectory(new File(directoryName));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String nomFileSelected = selectedFile.getAbsolutePath();
			grPral = readMVMGroup(nomFileSelected);
			String modelNew=grPral.getModelName();
			String nomFile = selectedFile.getName();
			int indicePunto = nomFile.indexOf('.');
			String parteIzquierda = indicePunto != -1 ? nomFile.substring(0, indicePunto) : nomFile;			
//			if (!modelNew.equals(strLastFileAnt)) {
				if (!modelNew.equals(sModel)) {
				int forceUse = JOptionPane.showConfirmDialog(frame,
						"File ["+parteIzquierda+"] contains model ["+modelNew+"] diferent a model ["+sModel+"], Continuous?",
						"The models do not match",
						JOptionPane.YES_NO_OPTION);

				if (forceUse != JOptionPane.YES_OPTION) {
					copyGrPralAntToGrPral();
					return;
				}

			}
			// Depuracion de existencia de clases en el modelo
			if (!debugsClassesAndAssociations(grPral)){
				copyGrPralAntToGrPral();
				return;			
			}

			strLastFile=parteIzquierda;
			txFileName.setText(parteIzquierda);
		}
	}

	private boolean debugsClassesAndAssociations(MVMGroupActions group) {
		boolean existAll=true;
		boolean ok=true;
		// Ver objetos y clases de cada objeto
		List<MVMAction> listAct=grPral.getlActions();
		int nActions = listAct.size();
		MVMAction oAction=listAct.get(nActions-1);	
		List<MVMObject> lObjs=oAction.getlObjs();
		for (MVMObject oObj: lObjs) {
			String className = oObj.getClassName();
			// Comprobar existencia de la clase

			boolean exist=false;
			for (MClass oClass: fSession.system().model().classes()) {
				if (oClass.name().equals(className)) {
					exist=true;
					break;
				}
			}
			if (!exist) {
				existAll=false;
				ok=false;
				String mensaje = "No class ["+className+"] exists in group ["+group.getnameGroup()+"]. Group cannot be loaded";
				JOptionPane.showMessageDialog(null, mensaje, "inconsistency between groups",JOptionPane.INFORMATION_MESSAGE);
				return ok;
			}
		}

		// Ver asociaciones
		List<MVMLink> lLinks = oAction.getlLinks();
		for (MVMLink oMVMLink: lLinks) {
			String linkName = oMVMLink.getNomAssoc();
			boolean exist=false;
			for (MAssociation oAssoc: fSession.system().model().associations()) {
				if (oAssoc.name().equals(linkName)) {
					exist=true;
					break;
				}
			}

			if (!exist) {
				existAll=false;
				ok=false;
				String mensaje = "No Association ["+linkName+"] exists in group ["+group.getnameGroup()+"]. Group cannot be loaded";
				JOptionPane.showMessageDialog(null, mensaje, "inconsistency between associations",JOptionPane.INFORMATION_MESSAGE);
				return ok;
			}
		}
		return ok;
	}

	/**
	 * Save file
	 */
	private void saveFile() {
		String nomGrupo=txFileName.getText();
		String nomFile = nomGrupo+"."+strExtension;

		grPral.setnameGroup(nomGrupo);
		grPral.setDescription(txDescription.getText());

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("File to save");   

		File directorioInicial = new File(directoryName);
		fileChooser.setCurrentDirectory(directorioInicial);
		fileChooser.setSelectedFile(new File(nomFile));

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

				String nomFileSave = fileToSave.getAbsolutePath();
				nomFile = fileToSave.getName();
				writeMVMGroup(grPral,nomFileSave);
				int indicePunto = nomFile.indexOf('.');
				String parteIzquierda = indicePunto != -1 ? nomFile.substring(0, indicePunto) : nomFile;
				strLastFile=parteIzquierda;
				txFileName.setText(parteIzquierda);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * Show data by action
	 * @param nAction
	 */
	private void showDataAction(int nAction) {
		loadListObjects(nAction);
		loadListAttrs(nAction,0);
		loadListLinks(nAction);
		tabActions.setRowSelectionInterval(nAction, nAction);
		if (tabObjects.getModel().getRowCount()>0) {
			tabObjects.setRowSelectionInterval(0, 0);
		}
		if (tabAttrs.getModel().getRowCount()>0) {
			tabAttrs.setRowSelectionInterval(0, 0);
		}
		if (tabLinks.getModel().getRowCount()>0) {
			tabLinks.setRowSelectionInterval(0, 0);
		}
	}

	/**
	 * Load actions into table
	 * @param lActions
	 */
	private void loadListActions(List<MVMAction> lActions) {
		modelTabActions = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"Order", "Type","Params"
		};

		int nActions=lActions.size();
		Object[][] data = new Object[nActions][3];
		for(int nAction=0;nAction<nActions;nAction++) {
			MVMAction oAction=lActions.get(nAction);
			data[nAction][0]=oAction.getOrden();
			data[nAction][1]=oAction.getTypeAction();
			data[nAction][2]=oAction.getParameters();
		}

		modelTabActions = new DefaultTableModel(data,columns);
		tabActions.setModel(modelTabActions);
		tabActions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabActions.getColumnModel().getColumn(0).setPreferredWidth(40);
		tabActions.getColumnModel().getColumn(1).setPreferredWidth(40);
		tabActions.getColumnModel().getColumn(2).setPreferredWidth(300);
		tabActions.repaint();
		return;
	}
	/**
	 * Load objects into table
	 * @param nAction
	 */
	private void loadListObjects(int nAction) {
		lObjsPral = new ArrayList<MVMObject>();

		modelTabObjects = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"Class", "Object"
		};

		if (lActions.size()>0) {
			MVMAction oAction=lActions.get(nAction);
			lObjsPral = oAction.getlObjs();
			int nObjs = lObjsPral.size();
			Object[][] data = new Object[nObjs][2];
			for(int nObj=0;nObj<nObjs;nObj++) {
				MVMObject oObject=lObjsPral.get(nObj);
				data[nObj][0]=oObject.getClassName();
				data[nObj][1]=oObject.getName();
			}
			modelTabObjects = new DefaultTableModel(data,columns);
		}else {
			Object[][] data = new Object[0][2];
			modelTabObjects = new DefaultTableModel(data,columns);
		}

		tabObjects.setModel(modelTabObjects);
		tabObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabObjects.getColumnModel().getColumn(0).setPreferredWidth(100);
		tabObjects.getColumnModel().getColumn(1).setPreferredWidth(100);
		tabObjects.repaint();
		return;
	}

	/**
	 * Load attributes by object and by action
	 * @param nAction
	 * @param nObj
	 */
	private void loadListAttrs(int nAction,int nObj) {
		modelTabAttrs = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"Attribute", "Value"
		};

		if (lObjsPral.size()>0) {
			MVMObject oObject=lObjsPral.get(nObj);
			List<MVMAttribute> lAttrs = oObject.getAttributes();
			int nAttrs = lAttrs.size();
			Object[][] data = new Object[nAttrs][2];
			for(int nAttr=0;nAttr<nAttrs;nAttr++) {
				MVMAttribute oAttribute=lAttrs.get(nAttr);
				data[nAttr][0]=oAttribute.getName();
				data[nAttr][1]=oAttribute.getValue();
			}
			modelTabAttrs = new DefaultTableModel(data,columns);
		}else {
			Object[][] data = new Object[0][2];
			modelTabAttrs = new DefaultTableModel(data,columns);
		}
		tabAttrs.setModel(modelTabAttrs);
		tabAttrs.repaint();
		return;
	}
	/** 
	 * Load links into table
	 * @param nAction
	 */
	private void loadListLinks(int nAction) {
		modelTabLinks = new DefaultTableModel();
		String[] columns;
		columns = new String[] {
				"Link", "Assoc", "Role1","Object End 1","Class End 1","Role2","Object End 2","Class End 2	"
		};

		if (lActions.size()>0) {
			MVMAction oAction=lActions.get(nAction);
			lLinksPral = oAction.getlLinks();
			int nLinks = lLinksPral.size();
			Object[][] data = new Object[nLinks][8];
			for(int nLink=0;nLink<nLinks;nLink++) {
				MVMLink oLink=lLinksPral.get(nLink);
				data[nLink][0]=oLink.getCodeLink();
				data[nLink][1]=oLink.getNomAssoc();
				data[nLink][2]=oLink.getEnd1Role();
				data[nLink][3]=oLink.getEnd1Class();
				data[nLink][4]=oLink.getEnd1Object();
				data[nLink][5]=oLink.getEnd2Role();
				data[nLink][6]=oLink.getEnd2Class();
				data[nLink][7]=oLink.getEnd2Object();
			}
			modelTabLinks = new DefaultTableModel(data,columns);
		}else {
			Object[][] data = new Object[0][8];
			modelTabLinks = new DefaultTableModel(data,columns);
		}

		tabLinks.setModel(modelTabLinks);
		tabLinks.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabLinks.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(1).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(2).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(3).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(4).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(5).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(6).setPreferredWidth(120);
		tabLinks.getColumnModel().getColumn(7).setPreferredWidth(120);
		tabLinks.repaint();
		return;
	}
	/**
	 * Prepares list of actions to return for WizardMVMView
	 */
	private void prepareListActions() {
		// Se ha de construir el grupo a devolver en base a la accion seleccionada
		int nActionSel = tabActions.getSelectedRow();
		//		MVMGroupActions group = new MVMGroupActions();

		lActionsRes = new ArrayList<MVMAction>();
		for (int nAction=0;nAction<=nActionSel;nAction++) {
			lActionsRes.add(lActions.get(nAction));
		}
		return ;
	}

	/**
	 * returns list of actions
	 * @return
	 */
	public List<MVMAction> getListActions(){
		return lActionsRes;
	}

	/**
	 * returns last file used (only name without extension)
	 * @return
	 */
	public String getLastFile() {
		return strLastFile;
	}

	/**
	 * Load data into group class
	 */
	private void cargaDatos() {
		Date date = new Date();
		sFileName = strLastFile;
		sCreationDate = date;
		sModificationDate = date;
		sDescription = "";

		grPral.setnameGroup(sFileName);
		grPral.setCreationDate(sCreationDate);
		grPral.setLastModifiedDate(sModificationDate);
		grPral.setDescription(sDescription);
		grPral.setModelName(sModel);
		grPral.setSrcFileUSE(sSourceUSE);
		grPral.setlActions(lActions);

		copyGrPralToGrPralAnt();
	}

	private void copyGrPralToGrPralAnt() {
		grPralAnt = new MVMGroupActions();
		grPralAnt.setnameGroup(grPral.getnameGroup());
		grPralAnt.setCreationDate(grPral.getCreationDate());
		grPralAnt.setLastModifiedDate(grPral.getLastModifiedDate());
		grPralAnt.setDescription(grPral.getDescription());
		grPralAnt.setModelName(grPral.getModelName());
		grPralAnt.setSrcFileUSE(grPral.getSrcFileUSE());
		grPralAnt.setlActions(grPral.getlActions());
	}

	private void copyGrPralAntToGrPral() {
		grPral = new MVMGroupActions();
		grPral.setnameGroup(grPralAnt.getnameGroup());
		grPral.setCreationDate(grPralAnt.getCreationDate());
		grPral.setLastModifiedDate(grPralAnt.getLastModifiedDate());
		grPral.setDescription(grPralAnt.getDescription());
		grPral.setModelName(grPralAnt.getModelName());
		grPral.setSrcFileUSE(grPralAnt.getSrcFileUSE());
		grPral.setlActions(grPralAnt.getlActions());
	}

	/**
	 * Write data in JSON format
	 * @param gr1
	 * @param nomFile
	 */
	private void writeMVMGroup(MVMGroupActions gr1, String nomFile) {

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(new File(nomFile), gr1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read data from JSON format
	 * @param nomFile
	 * @return
	 */
	private MVMGroupActions readMVMGroup(String nomFile) {
		MVMGroupActions group = new MVMGroupActions();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			group = objectMapper.readValue(new File(nomFile), MVMGroupActions.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}

	public String convertWithIteration(Map<String, String> map) {
		StringBuilder mapAsString = new StringBuilder("{");
		for (String key : map.keySet()) {
			mapAsString.append(key + "=" + map.get(key) + ", ");
		}
		mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
		return mapAsString.toString();
	}
}
