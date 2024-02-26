package org.tzi.use.gui.mvm;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.tzi.use.uml.mm.MClassInvariant;

public class MVMCompareExprInvs extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;
	private DefaultTableModel modeltabInvs;


	private JRadioButton rbInvAll;
	private JRadioButton rbInvOk;
	private JRadioButton rbInvKo;
	private ButtonGroup groupInvs;
	private JPanel panelGroupInv;
	private JTableHeader header;

	private JLabel lbInvs;

	private JTable tabInvs;
	private JScrollPane panetabInvs;

	private JButton btnExit;

	private Map<MVMObject, Map<MClassInvariant, Boolean>> mapObjects;
	private int rowObject;
	private String compareName;
	private String compareClass;

	private enum StatesInv {
		ALL, OK, KO
	}
	private StatesInv stateInv=StatesInv.ALL;

	public MVMCompareExprInvs(JFrame fParent, Map<MVMObject, 
			Map<MClassInvariant, Boolean>> pMapObjects, 
			int pRowObject, 
			String pCompareName, 
			String pCompareClass) {
		super(fParent, "MVM Show invs expressions",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("MVM Show invs expressions");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);
		mapObjects = pMapObjects;
		rowObject=pRowObject;
		compareName=pCompareName;
		compareClass=pCompareClass;

		// Invs options
		rbInvAll = new JRadioButton("All");
		rbInvAll.setSelected(true);
		rbInvOk = new JRadioButton("Ok");
		rbInvKo = new JRadioButton("Ko");

		ItemListener itemListenerInv = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					System.out.println("Seleccionado (inv): " + ((JRadioButton) e.getItem()).getText());
					String strCmp=((JRadioButton) e.getItem()).getText();
					switch (strCmp) {
					case "All":
						stateInv=StatesInv.ALL;
						break;
					case "Ok":
						stateInv=StatesInv.OK;
						break;
					case "Ko":
						stateInv=StatesInv.KO;
						break;
					default:
						stateInv=StatesInv.ALL;
					}
				}
				int row = rowObject;
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
		TitledBorder titledBorderInv = new TitledBorder("Options Invs");
		panelGroupInv.setBorder(titledBorderInv);

		modeltabInvs = new DefaultTableModel();
		tabInvs = new JTable(modeltabInvs);

		panel = new JPanel();
		frame.add(panel);

		panel.setLayout(null);

		Insets insets = panel.getInsets();

		int col1 = 10 + insets.left;

		int filGroupTab1=20;
		int filGroupTab2=120;

		panelGroupInv.setBounds(col1 ,filGroupTab1, 160, 60);
		panel.add(panelGroupInv);

		lbInvs = new JLabel("Invs");
		lbInvs.setBounds(col1, filGroupTab2-30, 300, 25);
		Font newFont = new Font(lbInvs.getFont().getName(), Font.BOLD, 18);
		lbInvs.setFont(newFont);
		panel.add(lbInvs);	

		header = tabInvs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		int row = rowObject;
		loadListInvs(row);
		if (tabInvs.getModel().getRowCount()>0) {
			tabInvs.setRowSelectionInterval(0, 0);
		}

		tabInvs = new JTable(modeltabInvs);
		tabInvs.setBounds(col1, filGroupTab2, 750, 140);
		header = tabInvs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabInvs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(360);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(440);
		tabInvs.getColumnModel().getColumn(2).setPreferredWidth(50);

		tabInvs.setDefaultEditor(Object.class, null);

		tabInvs.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int nInv = tabInvs.getSelectedRow();
			}
		});

		putColorInvs();

		panetabInvs = new JScrollPane(tabInvs);
		panetabInvs.setBounds(col1, filGroupTab2, 853, 140);
		panel.add(panetabInvs);

		tabInvs.setRowSelectionInterval(0, 0);

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//				lActionsRes=null;
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnExit.setBounds(752, 265, 110, 25);
		panel.add(btnExit);

		getContentPane().add(panel);

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
				boolean allOk=(boolean) table.getValueAt(row, 2);

				if (allOk == false) {
					c.setForeground(Color.RED);
				} else {
					c.setForeground(Color.BLACK);
				}

				return c;
			}
		};

		tabInvs.getColumnModel().getColumn(1).setCellRenderer(renderInv);
		tabInvs.getColumnModel().getColumn(2).setCellRenderer(renderInv);
	}

	/**
	 * Load object table
	 */

	/**
	 * Load invs table
	 */
	private void loadListInvs(int nObject) {
		System.out.println("   stateInv [" + stateInv+"]");

		modeltabInvs = new DefaultTableModel();
		String[] columns;
		columns = new String[] {
				"Inv", "Expr", "Satisfied"
		};

		if (mapObjects.size()>0) {
			int nInv=0;
			boolean hallaObj=false;
			for (Map.Entry<MVMObject, Map<MClassInvariant, Boolean>> entry : mapObjects.entrySet()) {
				MVMObject oObjKey = entry.getKey();
				if (oObjKey.getClassName().equals(compareClass) && oObjKey.getName().equals(compareName) ) {
					hallaObj=true;
					Map<MClassInvariant, Boolean> innerMap = entry.getValue();
					TreeMap<MClassInvariant, Boolean> mapaOrdenado = new TreeMap<>(innerMap);
					int nInvs = innerMap.size();
					Object[][] data = new Object[nInvs][3];
					// Determina si todos los invs son ok o no
					boolean allOk=true;
					//					for (Map.Entry<String, Boolean> innerEntry : innerMap.entrySet()) {
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
							String strExpr = (String) inv.bodyExpression().toString();
							data[nInv][1]=strExpr;
							data[nInv][2]=value;
							nInv++;	
						}
						System.out.println("   " + inv + ": " + value);
					}

					// Resize data result
					Object[][] newData = new Object[nInv][3];

					for (int i = 0; i < nInv; i++) {
						for (int j = 0; j < 3; j++) {
							newData[i][j] = data[i][j];
						}
					}

					modeltabInvs = new DefaultTableModel(newData,columns);
					break;
				}
			}
			// Si no halla objeto
			if(!hallaObj) {
				Object[][] data = new Object[0][3];
				modeltabInvs = new DefaultTableModel(data,columns);
			}else {
				//				modeltabInvs = new DefaultTableModel(data,columns);
			}

		}else {
			Object[][] data = new Object[0][3];
			modeltabInvs = new DefaultTableModel(data,columns);
		}

		tabInvs.setModel(modeltabInvs);
		tabInvs.getColumnModel().getColumn(0).setPreferredWidth(360);
		tabInvs.getColumnModel().getColumn(1).setPreferredWidth(440);
		tabInvs.getColumnModel().getColumn(2).setPreferredWidth(50);

		putColorInvs();

		tabInvs.repaint();
		return;
	}

}
