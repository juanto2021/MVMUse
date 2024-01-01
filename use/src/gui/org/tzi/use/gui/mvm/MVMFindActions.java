package org.tzi.use.gui.mvm;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MVMFindActions extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel panel;

	private JLabel lbLastFile = new JLabel("File to load");
	private JTextField txLastFile;
	private JCheckBox chkFilterModel = new JCheckBox("Filter model");

	private JButton btnLoad;
	private JButton btnCancel;

	private JTable tabActions;
	private JScrollPane paneTabActions;
	private JTableHeader header;

	private DefaultTableModel modelTabActions;

	List<Map.Entry<String, MVMGroupActions>> lActions;
	Map<String, MVMGroupActions> mActions = new HashMap<>();

	private String groupActionsFolder = "groupActions";
	private String directoryName="";
	private String nomFileRes="";
	private boolean filterMode=true;
	private String modelRef="";


	public MVMFindActions(JFrame fParent, String strLastFile, String pModelRef ) {
		super(fParent, "Find Actions",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("Find Actions");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);

		Path path = Paths.get("");
		directoryName = path.toAbsolutePath().toString()+"/"+groupActionsFolder;
		modelRef=pModelRef;

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		Insets insets = panel.getInsets();

		int col1 = 15 + insets.left;
		int lbHeight=20;

		lbLastFile.setBounds(col1, 332 + insets.top,150, lbHeight);


		txLastFile = new JTextField();
		if (strLastFile!="") {
			txLastFile.setText(strLastFile+".mva");
		}
		txLastFile.setBounds(col1+60, 332 + insets.top,150, lbHeight);
		txLastFile.setEditable(false);

		chkFilterModel.setBounds(col1+400, 332 + insets.top,150, lbHeight);
		chkFilterModel.setText("Filter model '"+ modelRef+"'");
		chkFilterModel.setSelected(true);

		chkFilterModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterMode=chkFilterModel.isSelected();
				cargaDatos();
				loadListActions();
			}
		});


		panel.add(lbLastFile);
		panel.add(txLastFile);
		panel.add(chkFilterModel);

		modelTabActions = new DefaultTableModel();
		tabActions = new JTable(modelTabActions);


		cargaDatos();

		loadListActions();
		int filGroupTab1=10 + insets.top;

		tabActions = new JTable(modelTabActions);
		tabActions.setBounds(col1, filGroupTab1, 1020, 300);
		header = tabActions.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);
		columnAdjustment();

		tabActions.setToolTipText("Double-clic to select");

		tabActions.setDefaultEditor(Object.class, null);

		tabActions.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = tabActions.rowAtPoint(evt.getPoint());
				String nomfile = (String) tabActions.getValueAt(row, 0);
				if (evt.getClickCount() == 1)
				{
					txLastFile.setText(nomfile);
				}
				if (evt.getClickCount() == 2)
				{
					prepareFileRes(txLastFile.getText());
					dispose();
				}
			}
		});
		paneTabActions = new JScrollPane(tabActions);
		paneTabActions.setBounds(col1, filGroupTab1,1023, 310);
		panel.add(paneTabActions);

		btnLoad = new JButton("Load action");
		btnLoad.setBounds(col1+913, 330 + insets.top, 110, 25);

		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prepareFileRes(txLastFile.getText());
				dispose();
			}
		});

		panel.add(btnLoad);


		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnCancel.setBounds(col1+790, 330 + insets.top, 110, 25);
		panel.add(btnCancel);
		getContentPane().add(panel);

		tabActions.setRowSelectionInterval(0, 0);// Select first row
		if (tabActions.getModel().getRowCount()>0) {
			String nomfile = (String) tabActions.getValueAt(0, 0);
			txLastFile.setText(nomfile);
		}
	}

	/**
	 * Initialize variable nomFileRes to return to WizardActions
	 * @param nomFile
	 */
	private void prepareFileRes(String nomFile) {
		nomFileRes=nomFile;
		return ;
	}

	/**
	 * Return variable nomFileRes to WizardActions
	 * @return
	 */
	public String getNomFile() {
		return nomFileRes;
	}

	/**
	 * Load actions list
	 */
	private void loadListActions() {
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		modelTabActions = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				"File", "Description", "Creation date","Modification date","Model","File USE","N.Actions", "N. Objs","N.Links"
		};

		int nActions=mActions.size();
		Object[][] data = new Object[nActions][9];
		int nAction=0;
		for (Map.Entry<String, MVMGroupActions> entry : lActions) {

			MVMGroupActions group = (MVMGroupActions) entry.getValue();

			Date dCreationDate = group.getCreationDate();
			String strCreationDate = date.format(dCreationDate);
			Date dModificationDate = group.getLastModifiedDate();
			String strModificationDate = date.format(dModificationDate);

			String sModel = group.getModelName();
			String sSourceUSE = group.getSrcFileUSE();
			String sDescription = group.getDescription();

			List<MVMAction> lGroupActions = new ArrayList<MVMAction>();
			List<MVMObject> lGroupObjs = new ArrayList<MVMObject>();
			List<MVMLink> lGroupLinks = new ArrayList<MVMLink>();

			lGroupActions =group.getlActions();
			MVMAction oAction=lGroupActions.get(lGroupActions.size()-1);

			lGroupObjs = oAction.getlObjs();
			lGroupLinks = oAction.getlLinks();

			int nGroupActions=lGroupActions.size();
			int nGroupObjs=lGroupObjs.size();
			int nGroupLinks=lGroupLinks.size();	

			data[nAction][0]=entry.getKey();
			data[nAction][1]=sDescription;
			data[nAction][2]=strCreationDate;
			data[nAction][3]=strModificationDate;
			data[nAction][4]=sModel;
			data[nAction][5]=sSourceUSE;
			data[nAction][6]=String.valueOf(nGroupActions);
			data[nAction][7]=String.valueOf(nGroupObjs);
			data[nAction][8]=String.valueOf(nGroupLinks);
			nAction++;  
		}				

		modelTabActions = new DefaultTableModel(data,columns);
		tabActions.setModel(modelTabActions);

		columnAdjustment();

		return;
	}

	/**
	 * Adjust size columns
	 */
	private void columnAdjustment() {
		DefaultTableCellRenderer modelocentrar = new DefaultTableCellRenderer();
		modelocentrar.setHorizontalAlignment(SwingConstants.CENTER);

		tabActions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabActions.getColumnModel().getColumn(0).setPreferredWidth(60);//File
		tabActions.getColumnModel().getColumn(1).setPreferredWidth(140);//Desc
		tabActions.getColumnModel().getColumn(2).setPreferredWidth(105);//creation date
		tabActions.getColumnModel().getColumn(3).setPreferredWidth(105);// Modification date
		tabActions.getColumnModel().getColumn(4).setPreferredWidth(80);// Model
		tabActions.getColumnModel().getColumn(5).setPreferredWidth(380);// FIl2 USE
		tabActions.getColumnModel().getColumn(6).setPreferredWidth(50);// Num Actions
		tabActions.getColumnModel().getColumn(6).setCellRenderer(modelocentrar);

		tabActions.getColumnModel().getColumn(7).setPreferredWidth(50);// Num objs
		tabActions.getColumnModel().getColumn(7).setCellRenderer(modelocentrar);

		tabActions.getColumnModel().getColumn(8).setPreferredWidth(50);// Num links
		tabActions.getColumnModel().getColumn(8).setCellRenderer(modelocentrar);
		tabActions.repaint();
	}

	/**
	 * Load data into lActions
	 */
	private void cargaDatos() {

		mActions = new HashMap<>();

		File carpeta = new File(directoryName);
		String[] listado = carpeta.list();
		for (int i=0; i< listado.length; i++) {
			System.out.println(listado[i]);
			String nomFile = listado[i];
			String nomFileGroup = directoryName+"/"+nomFile;
			MVMGroupActions group = readMVMGroup(nomFileGroup);
			String model = group.getModelName();
			boolean guardar=true;
			if (filterMode && !model.equals(modelRef)) {
				guardar=false;
			}
			if (guardar) {
				mActions.put(nomFile, group);
			}
		}

		lActions = new ArrayList<>(mActions.entrySet());

		// Ordenar la lista de entradas por clave
		Collections.sort(lActions, Map.Entry.comparingByKey());
	}

	/**
	 * Read group of actions from file
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
}
