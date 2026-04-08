package org.tzi.use.gui.mvm;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

	private JButton btnReloadNewContent;
	private JButton btnExit;

	private JLabel lbModelOri = new JLabel("Current");
	private JLabel lbModelNew = new JLabel("New");
	private JTextArea taModelOri = new JTextArea();
	private JTextArea taModelNew = new JTextArea();
	private JScrollPane scrollPaneOri;
	private JScrollPane scrollPaneNew;
	private JCheckBox cbSyncScroll;
	
	private boolean syncing = false;
	private int HEIGHT_TA = 500;
	private int WIDTH_TA = 500;
	private int ROW_TA = 20;

	private String contentNew="";
	private String contentNewOri="";

	public MVMShowSourceModelInvs(JFrame fParent, MVMObjCheckState oParent, String sourceAct, String sourceNew) {
		super(fParent, "MVM Show source model",ModalityType.APPLICATION_MODAL);
		frame = new JFrame("MVM Show source model");
		frame.setAlwaysOnTop(true);
		frame.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		contentNewOri=sourceNew;

		panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);

		Border border = BorderFactory.createLineBorder(Color.BLACK);

		lbModelOri.setBounds(15, ROW_TA,150, 20);
		panel.add(lbModelOri);	

		taModelOri.setBounds(15, ROW_TA+20, WIDTH_TA, HEIGHT_TA );
		taModelOri.setBorder(border);
		taModelOri.setText(sourceAct);
		taModelOri.setEditable(false);

		scrollPaneOri = new JScrollPane(taModelOri);
		scrollPaneOri.setBounds(15, ROW_TA + 20, WIDTH_TA, HEIGHT_TA);

		panel.add(scrollPaneOri);

		lbModelNew.setBounds(15+HEIGHT_TA+10, ROW_TA,150, 20);
		panel.add(lbModelNew);	

		taModelNew.setBounds(15+WIDTH_TA+10, ROW_TA+20, WIDTH_TA, HEIGHT_TA );
		taModelNew.setBorder(border);
		taModelNew.setText(sourceNew);
		taModelNew.setEditable(false);

		scrollPaneNew = new JScrollPane(taModelNew);
		scrollPaneNew.setBounds(15+WIDTH_TA+10, ROW_TA+20, WIDTH_TA, HEIGHT_TA );

		panel.add(scrollPaneNew);

		cbSyncScroll = new JCheckBox("Sync scroll");
		cbSyncScroll.setBounds(170, ROW_TA + HEIGHT_TA + 30, 120, 25);
		panel.add(cbSyncScroll);

		scrollPaneOri.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (cbSyncScroll.isSelected() && !syncing) {
					syncing = true;
					scrollPaneNew.getVerticalScrollBar().setValue(e.getValue());
					syncing = false;
				}
			}
		});

		scrollPaneNew.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (cbSyncScroll.isSelected() && !syncing) {
					syncing = true;
					scrollPaneOri.getVerticalScrollBar().setValue(e.getValue());
					syncing = false;
				}
			}
		});

		btnReloadNewContent = new JButton("Reload newContent");
		btnReloadNewContent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				taModelNew.setText(contentNewOri);
			}
		});

		btnReloadNewContent.setBounds(15, ROW_TA+HEIGHT_TA+30, 140, 25);
		panel.add(btnReloadNewContent);


		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				contentNew=taModelNew.getText();
				dispose(); 
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
