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

	private JButton btnLoad;
	private JButton btnCancel;

	private JTable tabActions;
	private JScrollPane paneTabActions;
	private JTableHeader header;

	private DefaultTableModel modelTabActions;

	//	private List<MVMAction> lActions = new ArrayList<MVMAction>();
	List<Map.Entry<String, MVMGroupActions>> lActions;
	Map<String, MVMGroupActions> mActions = new HashMap<>();

	private String groupActionsFolder = "groupActions";
	private String directoryName="";
	//	private String strExtension="mva";
	private String nomFileRes="";


	public MVMFindActions(JFrame fParent, String strLastFile ) {
		super(fParent, "Find Actions",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("Find Actions");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);

		Path path = Paths.get("");
		directoryName = path.toAbsolutePath().toString()+"/"+groupActionsFolder;

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		//		panel.add(lbLastFile);
		//		panel.add(txLastFile);

		Insets insets = panel.getInsets();

		int col1 = 15 + insets.left;
		int lbHeight=20;
		//		int txHeight=20;


		lbLastFile.setBounds(col1, 332 + insets.top,150, lbHeight);

		txLastFile = new JTextField();
		if (strLastFile!="") {
			txLastFile.setText(strLastFile+".mva");
			//			txLastFile.setText("1234567890123456789012345678901234567890");

		}
		txLastFile.setBounds(col1+60, 332 + insets.top,150, lbHeight);
		txLastFile.setEditable(false);
		//		txLastFile.setText("1234567890");

		panel.add(lbLastFile);
		panel.add(txLastFile);

		modelTabActions = new DefaultTableModel();
		tabActions = new JTable(modelTabActions);


		cargaDatos();

		loadListActions();
		int filGroupTab1=10 + insets.top;

		tabActions = new JTable(modelTabActions);
		tabActions.setBounds(col1, filGroupTab1, 940, 300);
		header = tabActions.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		DefaultTableCellRenderer modelocentrar = new DefaultTableCellRenderer();
		modelocentrar.setHorizontalAlignment(SwingConstants.CENTER);

		tabActions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabActions.getColumnModel().getColumn(0).setPreferredWidth(100);//File
		tabActions.getColumnModel().getColumn(1).setPreferredWidth(150);//Desc
		tabActions.getColumnModel().getColumn(2).setPreferredWidth(120);//creation date
		tabActions.getColumnModel().getColumn(3).setPreferredWidth(120);// Modification date
		tabActions.getColumnModel().getColumn(4).setPreferredWidth(120);// Model
		tabActions.getColumnModel().getColumn(5).setPreferredWidth(150);// FIl2 USE
		tabActions.getColumnModel().getColumn(6).setPreferredWidth(60);// Num Actions
		tabActions.getColumnModel().getColumn(6).setCellRenderer(modelocentrar);

		tabActions.getColumnModel().getColumn(7).setPreferredWidth(60);// Num objs
		tabActions.getColumnModel().getColumn(7).setCellRenderer(modelocentrar);

		tabActions.getColumnModel().getColumn(8).setPreferredWidth(60);// Num links
		tabActions.getColumnModel().getColumn(8).setCellRenderer(modelocentrar);

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
		paneTabActions.setBounds(col1, filGroupTab1,943, 310);
		panel.add(paneTabActions);

		btnLoad = new JButton("Load action");
		btnLoad.setBounds(col1+833, 330 + insets.top, 110, 25);

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
		btnCancel.setBounds(col1+710, 330 + insets.top, 110, 25);
		panel.add(btnCancel);
		getContentPane().add(panel);

		tabActions.setRowSelectionInterval(0, 0);// Provisionalmente seleccionamos primera fila
		if (tabActions.getModel().getRowCount()>0) {
			String nomfile = (String) tabActions.getValueAt(0, 0);
			txLastFile.setText(nomfile);
		}


	}
	private void prepareFileRes(String nomFile) {
		nomFileRes=nomFile;
		return ;
	}
	public String getNomFile() {
		return nomFileRes;
	}
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


			String sFileName = group.getnameGroup();
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
			//			data[nAction][5]=group.getlActions(getl)
			//			data[nAction][5]=group.getDescription();
			nAction++;  
		}				

		modelTabActions = new DefaultTableModel(data,columns);
		tabActions.setModel(modelTabActions);
		tabActions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabActions.getColumnModel().getColumn(0).setPreferredWidth(20);
		tabActions.getColumnModel().getColumn(1).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(2).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(3).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(4).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(5).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(6).setPreferredWidth(100);
		tabActions.getColumnModel().getColumn(7).setPreferredWidth(100);
		tabActions.repaint();
		return;
	}
	private void cargaDatos() {

		mActions = new HashMap<>();

		File carpeta = new File(directoryName);
		String[] listado = carpeta.list();
		for (int i=0; i< listado.length; i++) {
			System.out.println(listado[i]);
			String nomFile = listado[i];
			String nomFileGroup = directoryName+"/"+nomFile;
			MVMGroupActions group = readMVMGroup(nomFileGroup);
			String descripcion=group.getDescription();
			mActions.put(nomFile, group);
			// Obtener la descripcion

		}

		lActions = new ArrayList<>(mActions.entrySet());

		// Ordenar la lista de entradas por clave
		Collections.sort(lActions, Map.Entry.comparingByKey());

	}
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
