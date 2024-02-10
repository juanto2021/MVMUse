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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.tzi.use.uml.mm.MClassInvariant;

public class MVMObjCheckState extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;

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

	private JTextArea taExprInv = null;
	private JScrollPane scrollpaneExpr;

	private JTableHeader header;

	private JLabel lbObjects;
	private JLabel lbAttrs;
	private JLabel lbInvs;
	private JLabel lbExprInv;
	private JLabel lbIndicator;
	private JPanel pIndicator = new JPanel();

	private JTable tabObjects;
	private JScrollPane paneTabObjects;

	private JTable tabInvs;
	private JScrollPane panetabInvs;

	private JTable tabAttrs;
	private JScrollPane paneTabAttrs;

	private JButton btnExit;
	private JButton btnCompareExpr;

	private Map<MVMObject, Map<MClassInvariant, Boolean>> mapObjects;
	private boolean checkAllObjs=true;
	private boolean allObjsOk=true;

	String directoryName="";
	String strExtension="mva";

	private enum StatesObj {
		ALL, OK, KO
	}
	private StatesObj stateObj=StatesObj.ALL;

	private enum StatesInv {
		ALL, OK, KO
	}
	private StatesInv stateInv=StatesInv.ALL;

	public MVMObjCheckState(JFrame fParent, Map<MVMObject, Map<MClassInvariant, Boolean>> pMapObjects ) {
		super(fParent, "MVM Check Objects State",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("MVM Check Objects State");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);
		mapObjects = pMapObjects;

		// Objs options
		rbObjAll = new JRadioButton("All");
		rbObjAll.setSelected(true);
		rbObjOk = new JRadioButton("Ok");
		rbObjKo = new JRadioButton("Ko");

		ItemListener itemListenerObj = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
//					System.out.println("Seleccionado (obj): " + ((JRadioButton) e.getItem()).getText());
					String strCmp=((JRadioButton) e.getItem()).getText();
					switch (strCmp) {
					case "All":
						stateObj=StatesObj.ALL;
						break;
					case "Ok":
						stateObj=StatesObj.OK;
						break;
					case "Ko":
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
		TitledBorder titledBorderObj = new TitledBorder("Options Objects");
		panelGroupObj.setBorder(titledBorderObj);

		// Invs options
		rbInvAll = new JRadioButton("All");
		rbInvAll.setSelected(true);
		rbInvOk = new JRadioButton("Ok");
		rbInvKo = new JRadioButton("Ko");

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
		TitledBorder titledBorderInv = new TitledBorder("Options Objects");
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

		int col1 = 10 + insets.left;

		int filGroupTab1=20;
		int filGroupTab2=120;

		panelGroupObj.setBounds(col1 ,filGroupTab1, 150, 60);
		panelGroupInv.setBounds(col1+320 ,filGroupTab1, 150, 60);

		panel.add(panelGroupObj);
		panel.add(panelGroupInv);

		lbObjects = new JLabel("Objects");
		lbObjects.setBounds(col1 ,filGroupTab2-30, 130, 25);
		Font newFont = new Font(lbObjects.getFont().getName(), Font.BOLD, 18);
		lbObjects.setFont(newFont);
		panel.add(lbObjects);	

		lbInvs = new JLabel("Invs");
		lbInvs.setBounds(col1+320, filGroupTab2-30, 150, 25);
		lbInvs.setFont(newFont);
		panel.add(lbInvs);	

		lbAttrs = new JLabel("Attributes");
		lbAttrs.setBounds(col1+760 ,filGroupTab2-30, 150, 25);
		lbAttrs.setFont(newFont);
		panel.add(lbAttrs);	

		lbIndicator = new JLabel("");
		newFont = new Font(lbIndicator.getFont().getName(), Font.BOLD, 24);
		lbIndicator.setFont(newFont);
		lbIndicator.setForeground(Color.WHITE);
		lbIndicator.setBackground(Color.GREEN);

		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
		pIndicator = new JPanel();
		pIndicator = new JPanel(new GridBagLayout());
		pIndicator.setBounds(col1+160 ,filGroupTab1+7, 51, 51);
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

		loadTabObjects();

		tabObjects = new JTable(modelTabObjects);
		tabObjects.setBounds(col1, filGroupTab2, 320, 140);
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
				}
				loadListAttrs(row); // nObject
				putColorInvs();
			}
		});

		putColorObjs();

		paneTabObjects = new JScrollPane(tabObjects);
		paneTabObjects.setBounds(col1, filGroupTab2, 313, 140);
		panel.add(paneTabObjects);
		if(tabObjects.getModel().getRowCount()>0) {
			tabObjects.setRowSelectionInterval(0, 0);
		}


		loadListInvs(0);

		tabInvs = new JTable(modeltabInvs);
		tabInvs.setBounds(col1+330, filGroupTab2, 503, 140);
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
			}
		});

		putColorInvs();

		panetabInvs = new JScrollPane(tabInvs);
		panetabInvs.setBounds(col1+320, filGroupTab2, 433, 140);
		panel.add(panetabInvs);
		//		if(tabObjects.getModel().getRowCount()>0) {
		loadListAttrs(0); // nObject
		//		}


		tabAttrs = new JTable(modelTabAttrs);
		tabAttrs.setBounds(col1+760, filGroupTab2, 200, 140);
		header = tabAttrs.getTableHeader();
		header.setBackground(Color.lightGray);
		header.setVisible(true);

		tabAttrs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabAttrs.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabAttrs.getColumnModel().getColumn(1).setPreferredWidth(120);

		tabAttrs.setDefaultEditor(Object.class, null);

		paneTabAttrs = new JScrollPane(tabAttrs);
		paneTabAttrs.setBounds(col1+760, filGroupTab2, 243, 140);
		panel.add(paneTabAttrs);


		lbExprInv = new JLabel("Expr Inv.");
		lbExprInv.setBounds(col1, 270 + insets.top, 150, 25);
		lbExprInv.setFont(newFont);
		panel.add(lbExprInv);	

		taExprInv = new JTextArea();
		taExprInv.setBounds(col1, 300 + insets.top,650, 80 );

		scrollpaneExpr=new JScrollPane(taExprInv);    
		scrollpaneExpr.setBounds(col1, 300 + insets.top,650, 80);
		add(scrollpaneExpr);
		if(tabInvs.getModel().getRowCount()>0) {
			tabInvs.setRowSelectionInterval(0, 0);
			showExprInv(0);

		}

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnExit.setBounds(900, 355, 110, 25);
		panel.add(btnExit);
		
		btnCompareExpr = new JButton("Compare Expr");
		btnCompareExpr.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				compareExprInvs();
			}
		});
		btnCompareExpr.setBounds(560, filGroupTab1+60, 150, 25);
		panel.add(btnCompareExpr);

		getContentPane().add(panel);

	}
	private void compareExprInvs() {
//		System.out.println("Compara expresiones");
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
//		System.out.println("nInv ["+nInv+"]");
		MClassInvariant strInv = (MClassInvariant) tabInvs.getModel().getValueAt(nInv, 0);
		String texto = (String) strInv.bodyExpression().toString();
		//		taExprInv.setText(strInv.name());
		taExprInv.setText(texto);

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

	/**
	 * Load object table
	 */

	private void loadTabObjects() {
//		System.out.println("   stateObj [" + stateObj+"]");
		//		boolean allObjsOk=true;

		modelTabObjects = new DefaultTableModel();

		String[] columns;
		columns = new String[] {
				//Aqui
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
//					System.out.println("   " + inv + ": " + value);
					if (value==false) {
						allOk=false;
						if (checkAllObjs) {
							allObjsOk=false;
						}

						break;
					}
				}
//				System.out.println("Object ["+oObjKey.getName()+"] ["+allOk+"]");
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
		if (stateObj==stateObj.ALL) {
			if (allObjsOk) {
				pIndicator.setBackground(Color.GREEN);
				lbIndicator.setText("OK");
			}else {
				pIndicator.setBackground(Color.RED);
				lbIndicator.setText("KO");
			}

		}
		checkAllObjs=false;
		return;
	}

	/**
	 * Load invs table
	 */
	private void loadListInvs(int nObject) {
//		System.out.println("   stateInv [" + stateInv+"]");

		//		String oCompareName = (String) tabObjects.getValueAt(nObject, 0);
		//		String oCompareClass = (String) tabObjects.getValueAt(nObject, 1);
		modeltabInvs = new DefaultTableModel();
		String[] columns;
		//Aqui
		columns = new String[] {
				"Inv", "Satisfied "
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
							data[nInv][1]=value;
							nInv++;	
						}
//						System.out.println("   " + inv + ": " + value);
					}

					// Resize data result
					Object[][] newData = new Object[nInv][2];

					for (int i = 0; i < nInv; i++) {
						for (int j = 0; j < 2; j++) {
							newData[i][j] = data[i][j];
						}
					}

					//					modeltabInvs = new DefaultTableModel(data,columns);
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

		//		String oCompareName = (String) tabObjects.getValueAt(nObj, 0);
		//		String oCompareClass = (String) tabObjects.getValueAt(nObj, 1);

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
