package org.tzi.use.gui.mvm;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class MVMShowSourceModelInvs extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private JPanel panel;
	private JPanel pProposals;
	private JScrollPane pScrollProposals ;	

	private JButton btnReloadNewContent;
	private JButton btnExit;

	private JLabel lbModelOri = new JLabel("Current");
	private JLabel lbModelNew = new JLabel("New");
	private JTextArea taModelOri = new JTextArea();
	private JTextArea taModelNew = new JTextArea();
	private JScrollPane scrollPaneOri;
	private JScrollPane scrollPaneNew;

	private Border etchedTitledBorder;
	private Border etchedBorder;
	private int HEIGHT_TA = 500;
	private int WIDTH_TA = 500;
	private int ROW_TA = 20;

	private String contentNew="";
	private String contentNewOri="";

	//	public MVMShowSourceModelInvs(JFrame fParent, Map<MVMObject, 
	//			Map<String, Boolean>> pMapObjects, 
	//			int pRowObject, 
	//			String pCompareName, 
	//			String pCompareClass) {
	public MVMShowSourceModelInvs(JFrame fParent, MVMObjCheckState oParent, String sourceAct, String sourceNew) {
		super(fParent, "MVM Show source model",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("MVM Show source model");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		frame.setLocationRelativeTo(null);

		contentNewOri=sourceNew;

		panel = new JPanel();
		frame.add(panel);
		//
		panel.setLayout(null);

		int filTa=20;

		//Aqui
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		lbModelOri.setBounds(15, ROW_TA,150, 20);
		panel.add(lbModelOri);	

		taModelOri.setBounds(15, ROW_TA+20, WIDTH_TA, HEIGHT_TA );
		taModelOri.setBorder(border);
		taModelOri.setText(sourceAct);

		scrollPaneOri = new JScrollPane(taModelOri);
		scrollPaneOri.setBounds(15, ROW_TA + 20, WIDTH_TA, HEIGHT_TA);

		//		panel.add(taModelOri);
		panel.add(scrollPaneOri);

		lbModelNew.setBounds(15+HEIGHT_TA+10, ROW_TA,150, 20);
		panel.add(lbModelNew);	

		taModelNew.setBounds(15+WIDTH_TA+10, ROW_TA+20, WIDTH_TA, HEIGHT_TA );
		taModelNew.setBorder(border);
		taModelNew.setText(sourceNew);
		
		scrollPaneNew = new JScrollPane(taModelNew);
		scrollPaneNew.setBounds(15+WIDTH_TA+10, ROW_TA+20, WIDTH_TA, HEIGHT_TA );
		
		panel.add(scrollPaneNew);

		btnReloadNewContent = new JButton("Reload newContent");
		btnReloadNewContent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				taModelNew.setText(contentNewOri);
				//				lActionsRes=null;
				//				contentNew=taModelNew.getText();
				//								dispose(); //Provis
				//				System.exit(0);
			}
		});
		//		btnReloadNewContent.setBounds(WIDTH_TA*2+10-270, ROW_TA+HEIGHT_TA+30, 140, 25);
		btnReloadNewContent.setBounds(15, ROW_TA+HEIGHT_TA+30, 140, 25);
		panel.add(btnReloadNewContent);


		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//				lActionsRes=null;
				contentNew=taModelNew.getText();
				dispose(); //Provis
				//				System.exit(0);
			}
		});
		btnExit.setBounds(WIDTH_TA*2+10-125, ROW_TA+HEIGHT_TA+30, 140, 25);
		panel.add(btnExit);

		getContentPane().add(panel);

	}
	public String getContentNew() {
		return contentNew;
	}

}
