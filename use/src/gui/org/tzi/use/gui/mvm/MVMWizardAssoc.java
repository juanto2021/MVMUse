package org.tzi.use.gui.mvm;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.sys.MObject;

public class MVMWizardAssoc extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;
	private JPanel pProposals;
	private JScrollPane pScrollProposals ;

	private DefaultTableModel modelTabAssocs;
	private DefaultTableModel modelTabLinks;

	private JTableHeader header;

	private JLabel lbAssociations;
	private JLabel lbLinks;

	private JLabel lbCause;	
	private JLabel lbFullMessage;	

	private JTextField txCause;
	private JTextArea txFullMessage;
	private JScrollPane scrollBarFullMessage;

	private JTable tabAssocs;
	private JScrollPane paneTabAssocs;

	private JTable tabLinks;
	private JScrollPane paneTabLinks;

	private JButton btnApply;
	private JButton btnExit;

	private ButtonGroup group;

	private ArrayList<MVMAssocWizard> lAssocs;

	private MVMAssocWizard assocPral;
	private String commandWizard;
	private MAssociation assocSelectWizard;

	private List<MVMAssocWizard> lAssocsWizard;

	private Border etchedTitledBorder;
	private Border etchedBorder;

	private int HEIGHT_TABLE = 100;

	public MVMWizardAssoc(JFrame fParent, List<MVMAssocWizard> lAssocsParent) {
		super(fParent, "Wizard Association",ModalityType.APPLICATION_MODAL);

		lAssocsWizard = lAssocsParent;
		commandWizard="";
		frame = new JFrame("Wizard Association");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		modelTabAssocs = new DefaultTableModel();
		tabAssocs = new JTable(modelTabAssocs);

		modelTabLinks = new DefaultTableModel();
		tabLinks = new JTable(modelTabLinks);

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		lbAssociations = new JLabel("Associations");
		lbAssociations.setBounds(15, 15, 100, 25);
		panel.add(lbAssociations);	

		lbLinks = new JLabel("Links");
		lbLinks.setBounds(230, 15, 100, 25);
		panel.add(lbLinks);	

		tabAssocs = new JTable(modelTabAssocs);
		tabAssocs.setBounds(15, 40, 210, HEIGHT_TABLE);
		tabAssocs.setDefaultEditor(Object.class, null);// Para no poder editar

		header = tabAssocs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabAssocs.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = tabAssocs.rowAtPoint(evt.getPoint());
				showInfoAssocFromRow(row);
				tabLinks.setRowSelectionInterval(0, 0);
				showInfoLinkFromRow(0);
			}
		});

		tabAssocs.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP ){
					int row = tabAssocs.getSelectedRow();
					showInfoAssocFromRow(row);
					tabLinks.setRowSelectionInterval(0, 0);
					showInfoLinkFromRow(0);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP ){
					int row = tabAssocs.getSelectedRow();
					showInfoAssocFromRow(row);
					//					showInfoLinkFromRow(0);
					tabLinks.setRowSelectionInterval(0, 0);
					showInfoLinkFromRow(0);
				}
			}
		});

		paneTabAssocs = new JScrollPane(tabAssocs);
		paneTabAssocs.setBounds(15, 40, 210, HEIGHT_TABLE);
		panel.add(paneTabAssocs);

		loadTableAssocs();
		adjustWidthColumnsAssocs();

		tabLinks = new JTable(modelTabLinks);
		tabLinks.setBounds(230, 40, 655, HEIGHT_TABLE);
		tabLinks.setDefaultEditor(Object.class, null);// Para no poder editar

		header = tabLinks.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabLinks.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = tabLinks.rowAtPoint(evt.getPoint());
				showInfoLinkFromRow(row);
			}
		});

		tabLinks.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP ){

					int row = tabLinks.getSelectedRow();
					showInfoLinkFromRow(row);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP ){

					int row = tabLinks.getSelectedRow();
					showInfoLinkFromRow(row);
				}
			}
		});

		paneTabLinks = new JScrollPane(tabLinks);
		paneTabLinks.setBounds(230, 40, 655, HEIGHT_TABLE);
		panel.add(paneTabLinks);

		Object o = tabAssocs.getValueAt(0, 0);
		assocPral= new MVMAssocWizard();

		if (!o.toString().equals("")) {
			// buscar assoc en lista			
			assocPral=findInfoAssoc(o.toString());
		}

		lbCause = new JLabel("Cause");
		lbCause.setBounds(15, 45+HEIGHT_TABLE, 100, 25);
		panel.add(lbCause);	

		txCause = new JTextField();
		txCause.setBounds(15, 70+HEIGHT_TABLE, 870, 25);
		Color color = Color.WHITE;
		txCause.setBackground(color);

		Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
		txCause.setBorder(border);
		txCause.setEditable(false);

		panel.add(txCause);

		lbFullMessage = new JLabel("Full Message");
		lbFullMessage.setBounds(15, 100+HEIGHT_TABLE, 100, 25);
		panel.add(lbFullMessage);	

		txFullMessage = new JTextArea();
		txFullMessage.setEditable(false);
		txFullMessage.setLineWrap(true);

		scrollBarFullMessage = new JScrollPane(txFullMessage,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBarFullMessage.setBounds(15, 125+HEIGHT_TABLE, 870, 100);

		panel.add(scrollBarFullMessage);

		loadTableLinks(assocPral);
		assocPral = findInfoAssoc(assocPral.getName());
		tabAssocs.setRowSelectionInterval(0, 0);
		List<LinkWizard> lLinks = assocPral.getlLinks();
		LinkWizard oLink = findInfoLink(lLinks,0);
		showInfoLink( oLink);
		tabLinks.setRowSelectionInterval(0, 0);

		String borderTitle = "Proposals";
		etchedBorder = BorderFactory.createEtchedBorder();
		etchedTitledBorder = BorderFactory.createTitledBorder(etchedBorder, borderTitle);

		pScrollProposals = new JScrollPane(pProposals);
		pScrollProposals.setBounds(15, 235+HEIGHT_TABLE, 600, 100);
		pScrollProposals.setBorder(etchedTitledBorder);
		panel.add(pScrollProposals);

		showInfoAssocFromRow(0);
		showInfoLinkFromRow(0);

		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//	ver que option esta seleccionada
				for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
					AbstractButton button = buttons.nextElement();

					if (button.isSelected()) {
						commandWizard=button.getActionCommand();
					}
				}
				dispose();

			}
		});
		btnApply.setBounds(630, 310+HEIGHT_TABLE, 120, 25);
		panel.add(btnApply);

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				commandWizard="";
				dispose();
			}
		});
		btnExit.setBounds(760, 310+HEIGHT_TABLE, 120, 25);
		panel.add(btnExit);

		getContentPane().add(panel);
	}

	/**
	 * Returns commands to be executed in the main Wizard
	 * @return
	 */
	public String getCommandWizard() {
		return commandWizard;
	}

	/**
	 * Returns association
	 * @return
	 */
	public MAssociation getAssocWizard() {
		return assocSelectWizard;
	}
	/**
	 * Create a collection of actions and links to carry out
	 * @param mapActions
	 * @param objPral
	 * @return
	 */
	private String[][] createActionLinks(Map<String, String> mapActions, MObject objPral) {

		// [x][y]
		// [x]->Acciones
		// [y]->Comandos a devolver para su ejecucion
		String actionLinks[][] = new String[2][2];
		String strAction="";
		String textAction="";
		String strCommand="";
		String strObjPral=objPral.name();
		for (Map.Entry<String, String> entry : mapActions.entrySet()) {

			String actionW =  entry.getKey();
			String infoW = entry.getValue();

			String pCar = actionW.substring(0,1);

			switch(pCar) {
			case "A":
				textAction = "Assign";
				break;
			case "C":
				String sCar = actionW.substring(2,actionW.length());
				textAction = "Create ["+sCar+"] object/s";
				if (strCommand!="") {
					strCommand+="|";
				}
				strCommand=strCommand+"C "+sCar+":"+infoW;
				break;
			case "I":
				textAction = "Insert link";
				if (strCommand!="") {
					strCommand+="-";
				}
				strCommand=strCommand+"I "+infoW;
				break;			    
			default:

			}

			if (strAction!="") {
				strAction+=", ";
			}
			strAction+=textAction+":"+infoW;
		}

		actionLinks[0][0] = strAction;
		actionLinks[0][1] = strCommand;

		//strCommand
		actionLinks[1][0] = "Delete "+strObjPral;
		actionLinks[1][1] = "D "+strObjPral;
		return actionLinks;
	}

	/**
	 * Shows information about an association row
	 * @param row
	 */
	private void showInfoAssocFromRow(int row) {
		String nomAssoc= tabAssocs.getValueAt(row, 0).toString();

		assocPral = findInfoAssoc(nomAssoc);
		loadTableLinks(assocPral);
		assocSelectWizard=assocPral.getassocModel();

		List<LinkWizard> lLinks = assocPral.getlLinks();
		LinkWizard oLink = findInfoLink(lLinks,0);
		tabLinks.setRowSelectionInterval(0, 0);

		showInfoLink( oLink);
	}

	/**
	 * Shows information about a link row
	 * @param row
	 */
	private void showInfoLinkFromRow(int row) {
		List<LinkWizard> lLinks = assocPral.getlLinks();
		LinkWizard oLink = findInfoLink(lLinks,row);
		tabLinks.setRowSelectionInterval(row, row);

		showInfoLink( oLink);
		showPanelActionsLink(oLink);
	}

	/**
	 * Shows information about a link
	 * @param oLink
	 */
	private void showInfoLink(LinkWizard oLink) {
		txCause.setText(oLink.getCause());
		txFullMessage.setText(oLink.getFullMessage());
	}

	/**
	 * Shows actions and links panel
	 * @param oLink
	 */
	private void showPanelActionsLink(LinkWizard oLink) {
		Map<String, String> mapActions = new HashMap<String, String>();
		mapActions=oLink.getMapActions();
		MObject objPral = oLink.getoMObject();
		String actionLinks[][] = createActionLinks(mapActions,objPral);

		pProposals = new JPanel();
		BoxLayout layout = new BoxLayout(pProposals, BoxLayout.Y_AXIS);
		pProposals.setLayout(layout);
		group = new ButtonGroup();
		boolean pVez = true;
		int sizeActions=actionLinks.length;
		for (int r=0;r<sizeActions;r++) {
			String strAction=actionLinks[r][0];
			String strCommand=actionLinks[r][1];
			if (strAction!="") {
				JRadioButton rb = new JRadioButton (strAction);
				if (pVez) {
					rb.setSelected(true);
					pVez=false;
				}
				rb.setActionCommand(strCommand);
				pProposals.add(rb);
				group.add(rb);	
			}
		}

		String borderTitle = "Proposals ("+ actionLinks.length+")";
		panel.remove(pScrollProposals);
		pScrollProposals = new JScrollPane(pProposals);
		pScrollProposals.setBounds(15, 235+HEIGHT_TABLE, 600, 100);
		etchedTitledBorder = BorderFactory.createTitledBorder(etchedBorder, borderTitle);
		pScrollProposals.setBorder(etchedTitledBorder);
		panel.add(pScrollProposals);
		panel.updateUI();

	}

	/**
	 * Search information from a link
	 * @param lLinks
	 * @param row
	 * @return
	 */
	private LinkWizard findInfoLink(List<LinkWizard> lLinks,int row) {
		LinkWizard oLink = new LinkWizard();

		String nomObject = tabLinks.getValueAt(row, 0).toString();
		String nomRole = tabLinks.getValueAt(row, 4).toString();
		for(int i=0;i<lLinks.size();i++) {
			LinkWizard oLinkRef = lLinks.get(i);
			String nomObjectOref = oLinkRef.getObject().toString();
			String nomRoleOref = oLinkRef.getAssocEnd().toString();
			if (nomObjectOref.equals(nomObject) && nomRoleOref.equals(nomRole)) {
				return oLinkRef;
			}
		}

		return oLink;

	}

	/**
	 * Search information from a association
	 * @param assocRef
	 * @return
	 */
	private MVMAssocWizard findInfoAssoc(String assocRef) {
		MVMAssocWizard assoc = new MVMAssocWizard();
		// find row of Assoc
		for (int i=0;i<lAssocs.size();i++) {
			assoc = lAssocs.get(i);
			if (assoc.getName().equals(assocRef)) {
				return assoc;
			}
		}
		return assoc;

	}

	/**
	 * Load associations table
	 */
	private void loadTableAssocs() {
		modelTabAssocs = new DefaultTableModel();
		String[] columns;
		columns = new String[] {
				"Association", "State"
		};

		ArrayList<MVMAssocWizard>data = loadAssocs();

		Object[][] objects = new Object[data.size()][2];
		for (int i=0;i<data.size();i++) {
			objects[i][0]= data.get(i).getName();
			objects[i][1]= data.get(i).getState();
		}

		modelTabAssocs = new DefaultTableModel(objects , columns);

		tabAssocs.setModel(modelTabAssocs);
		tabAssocs.repaint();
		return;
	}

	/**
	 * Load associations
	 * @return
	 */
	private ArrayList<MVMAssocWizard> loadAssocs(){
		lAssocs = new ArrayList<MVMAssocWizard>(lAssocsWizard);
		return lAssocs;		
	}

	/**
	 * Load links
	 * @param assoc
	 * @return
	 */

	private ArrayList<LinkWizard> loadLinksAssoc(MVMAssocWizard assoc){
		ArrayList<LinkWizard> lLinks = new ArrayList<LinkWizard>(assoc.getlLinks());
		return lLinks;
	}

	/**
	 * Load links table
	 * @param assoc
	 */
	private void loadTableLinks(MVMAssocWizard assoc) {
		modelTabLinks = new DefaultTableModel();
		String[] columns;
		columns = new String[] {
				//				"Object", "Class","Connected To","Of Class", "Assoc End","Multiplicity","ObjPral"// Provis
				"Object", "Class","Connected To","Of Class", "Assoc End","Multiplicity"
		};
		//Aqui
		ArrayList<LinkWizard> data = loadLinksAssoc(assoc);

		Object[][] objects = new Object[data.size()][7];
		for (int i=0;i<data.size();i++) {
			objects[i][0]= data.get(i).getObject();
			objects[i][1]= data.get(i).getNomClass();
			objects[i][2]= data.get(i).getConnectedTo();
			objects[i][3]= data.get(i).getOfClass();
			objects[i][4]= data.get(i).getAssocEnd();
			objects[i][5]= data.get(i).getMultiSpecified();
			//			objects[i][6]= data.get(i).getoMObject();//Provis
		}

		modelTabLinks = new DefaultTableModel(objects,columns);
		tabLinks.setModel(modelTabLinks);
		adjustWidthColumnsLinks();
		tabLinks.repaint();
		return;
	}

	/**
	 * Adjust size columns on association table
	 */
	private void adjustWidthColumnsAssocs(){
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		tabAssocs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabAssocs.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
		tabAssocs.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
	}

	/**
	 * Adjust size columns on links table
	 */
	private void adjustWidthColumnsLinks(){
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		tabLinks.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabLinks.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
		tabLinks.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
		tabLinks.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
	}
}
