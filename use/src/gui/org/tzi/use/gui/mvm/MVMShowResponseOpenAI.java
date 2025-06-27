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

public class MVMShowResponseOpenAI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel panel;
	private JButton btnExit;
	private JLabel lbModelOri = new JLabel("Comments");
	private JTextArea taModelOri = new JTextArea();
	private JTextArea taModelNew = new JTextArea();
	private JScrollPane scrollPaneOri;

	private int HEIGHT_TA = 500;
	private int WIDTH_TA = 1000;
	private int ROW_TA = 20;

	private String contentNew="";

	public MVMShowResponseOpenAI(String sourceAct) {
		super((JFrame) null, "OpenAI result", ModalityType.APPLICATION_MODAL); 

		setResizable(false);
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Provis
		setLocationRelativeTo(null);

		panel = new JPanel();
		panel.setLayout(null);
		add(panel);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		lbModelOri.setBounds(15, ROW_TA,150, 20);
		panel.add(lbModelOri);	

		// Configuración del JTextArea para ajustar líneas automáticamente
		taModelOri.setLineWrap(true);
		taModelOri.setWrapStyleWord(true);
		taModelOri.setText(sourceAct);
		taModelOri.setCaretPosition(0);
		taModelOri.setEditable(false);
		taModelOri.setBorder(border);

		scrollPaneOri = new JScrollPane(taModelOri);
		scrollPaneOri.setBounds(15, ROW_TA + 20, WIDTH_TA, HEIGHT_TA);

		panel.add(scrollPaneOri);

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

		btnExit.setBounds(15, ROW_TA+HEIGHT_TA+30, 250, 40);
		panel.add(btnExit);

		getContentPane().add(panel);

	}
	public String getContentNew() {
		return contentNew;
	}

}
