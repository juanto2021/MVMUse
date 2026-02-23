package org.tzi.use.gui.mvm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.tzi.use.gui.views.WizardMVMView;

public class MVMShowResponseOpenAI3 extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// NEW INI --------------------------
	private static JLabel lblObjects;
	private static JLabel lblLinks;
	private static JLabel lblProperties;
	private static JLabel lblComments;

	private static JTextArea txtObjects;
	private static JTextArea txtLinks;
	private static JTextArea txtProperties;
	private static JTextArea txtComments;

	private static Font font = new Font("Courier New", Font.PLAIN, 13);
	private static Font fontBold = new Font(UIManager.getFont("Label.font").getFamily(), Font.BOLD, 16);

	private static JButton btnExit;	
	private static JButton btnCreateObjects;
	private static JButton btnCreateLinks;
	private static JButton btnCopyComment;
	private static JButton btnCopyTXTRequest;
	private static JButton btnCopyJSONRequest;
	private static JButton btnCopyJSONResult;
	private static JToggleButton btnShowButtons;

	private static JScrollPane scrollObjects;
	private static JScrollPane scrollComments;
	private static JScrollPane scrollLinks;
	private static JScrollPane scrollProperties;

	public JFrame parent;
	private static JPanel panel;

	private static String strNameModel;
	private static String rObjects;
	private static String rLinks;
	private static String rProperties;
	private static String rComments;

	private static String strCreateObjectsAI;
	private static String strCreateLinksAI;
	private static WizardMVMView fWizardMVMView;

	private static String mensaje;
	private static String jsonPretty;
	private static String jsonResult;

	// NEW FIN -----------------------------------------------------------

	private String contentNew="";

	public MVMShowResponseOpenAI3(WizardMVMView pWizardMVMView, JFrame pParent, String pStrNameModel, String pObjects, 
			String pLinks, String pProperties,String pComments,
			String pMensaje, String pJsonPretty, String pJsonResult) {
		super((JFrame) pParent, "Request to OpenAI for '"+pStrNameModel+"'", ModalityType.APPLICATION_MODAL);

		setStrNameModel(pStrNameModel);	
		parent=pParent;
		fWizardMVMView=pWizardMVMView;


		rObjects=pObjects;
		rLinks=pLinks;
		rProperties=pProperties;
		rComments=pComments;

		mensaje=pMensaje;
		jsonPretty=pJsonPretty;
		jsonResult=pJsonResult;

		initUI();


		txtObjects.setText(buildObjectsTree(rObjects));
		txtObjects.setCaretPosition(0);

		txtLinks.setText(buildLinksTree(rLinks));
		txtLinks.setCaretPosition(0);

		txtProperties.setText(formatProperties(rProperties));
		txtProperties.setCaretPosition(0);

		txtComments.setText(rComments);
		txtComments.setCaretPosition(0);

		btnCreateObjects.setEnabled(!rObjects.trim().isEmpty());
		btnCreateLinks.setEnabled(!rLinks.trim().isEmpty());


	}
	public static String formatProperties(String json) {
		JSONObject obj;
		StringBuilder sb = new StringBuilder();
		try {
			obj = new JSONObject(json);
			List<String> keys = new ArrayList<>();
			Iterator<String> it = obj.keys();
			while (it.hasNext()) {
				keys.add(it.next());
			}

			Collections.sort(keys);
			for (String key : keys) {
				try {
					sb.append("    \"")
					.append(key)
					.append("\": ")
					.append(obj.get(key))
					.append(",\n");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public static String buildObjectsTree(String jsonString) {

		StringBuilder sb = new StringBuilder();

		try {
			Object parsed = new JSONTokener(jsonString).nextValue();
			JSONArray arr;

			// Si es JSONObject, convertirlo a JSONArray
			if (parsed instanceof JSONObject) {
				JSONObject obj = (JSONObject) parsed;
				arr = new JSONArray();

				Iterator<String> it = obj.keys();
				while (it.hasNext()) {
					String key = it.next();
					arr.put(obj.getJSONObject(key));
				}
			}
			else {
				arr = (JSONArray) parsed;
			}

			// Estructura: class, object, (attr, value)
			Map<String, Map<String, Map<String, String>>> data = new LinkedHashMap<>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);

				String clazz = o.getString("class");
				String name = o.getString("name");

				data.putIfAbsent(clazz, new LinkedHashMap<>());
				data.get(clazz).putIfAbsent(name, new LinkedHashMap<>());

				// Recorrer todos los atributos 
				Iterator<String> it = o.keys();
				while (it.hasNext()) {
					String key = it.next();

					// Ignorar campos reservados
					if (key.equals("class") || key.equals("name"))
						continue;

					String value = o.get(key).toString();
					data.get(clazz).get(name).put(key, value);
				}
			}

			// Construccion del arbol ASCII
			for (String clazz : data.keySet()) {
				sb.append(clazz).append("\n");

				Map<String, Map<String, String>> objects = data.get(clazz);
				int objCount = 0;
				int objTotal = objects.size();

				for (String objName : objects.keySet()) {
					objCount++;
					boolean isLastObj = (objCount == objTotal);

					sb.append(isLastObj ? "└── " : "├── ")
					.append(objName).append("\n");

					Map<String, String> attrs = objects.get(objName);
					int attrCount = 0;
					int attrTotal = attrs.size();

					for (String attr : attrs.keySet()) {
						attrCount++;
						boolean isLastAttr = (attrCount == attrTotal);

						sb.append(isLastObj ? "    " : "│   ");
						sb.append(isLastAttr ? "└── " : "├── ");
						sb.append(attr).append(": ").append(attrs.get(attr)).append("\n");
					}
				}

				sb.append("\n");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String buildLinksTreeOld(String jsonString) {

		JSONArray arr;
		StringBuilder sb = new StringBuilder();

		try {
			Object parsed = new JSONTokener(jsonString).nextValue();

			if (parsed instanceof JSONObject) {
				JSONObject obj = (JSONObject) parsed;
				arr = new JSONArray();

				Iterator<String> it = obj.keys();
				while (it.hasNext()) {
					String key = it.next();
					arr.put(obj.getJSONObject(key));
				}
			}
			else {
				arr = (JSONArray) parsed;
			}

			// --- Procesar los links ---
			Map<String, List<JSONObject>> data = new LinkedHashMap<>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject link = arr.getJSONObject(i);

				String assoc = link.getString("association");

				data.putIfAbsent(assoc, new ArrayList<>());
				data.get(assoc).add(link);
			}

			for (String assoc : data.keySet()) {
				sb.append(assoc).append("\n");

				List<JSONObject> links = data.get(assoc);
				int count = 0;
				int total = links.size();

				for (JSONObject link : links) {
					count++;
					boolean isLast = (count == total);

					String from = link.getString("from");
					String to = link.getString("to");

					sb.append(isLast ? "└── " : "├── ")
					.append("from ").append(from)
					.append(" to: ").append(to)
					.append("\n");
				}

				sb.append("\n");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String buildLinksTree(String jsonString) {

		StringBuilder sb = new StringBuilder();

		try {
			JSONArray arr = new JSONArray(jsonString);

			// Agrupar por nomAssoc
			Map<String, List<JSONObject>> data = new LinkedHashMap<>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject link = arr.getJSONObject(i);

				String assoc = link.getString("nomAssoc");

				data.putIfAbsent(assoc, new ArrayList<>());
				data.get(assoc).add(link);
			}

			// Construir arbol
			for (String assoc : data.keySet()) {

				sb.append(assoc).append("\n");

				List<JSONObject> links = data.get(assoc);
				int total = links.size();
				int count = 0;

				for (JSONObject link : links) {
					count++;
					boolean isLast = (count == total);

					String prefix = isLast ? "└── " : "├── ";

					String code = link.getString("codeLink");

					String e1Class = link.getString("end1Class");
					String e1Obj   = link.getString("end1Object");
					String e1Role  = link.getString("end1Role");

					String e2Class = link.getString("end2Class");
					String e2Obj   = link.getString("end2Object");
					String e2Role  = link.getString("end2Role");

					sb.append(prefix).append("codeLink ").append(code).append("\n");

					sb.append("    ├── from: ")
					.append(e1Class).append(" ").append(e1Obj)
					.append(" (role=").append(e1Role).append(")\n");

					sb.append("    └── to:   ")
					.append(e2Class).append(" ").append(e2Obj)
					.append(" (role=").append(e2Role).append(")\n");
				}

				sb.append("\n");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public String getContentNew() {
		return contentNew;
	}

	private void initUI() {

		panel = new JPanel(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		int labelWidth = 130;
		int fieldWidth = 600;
		int fieldHeight = 180;
		int startY = 10;

		lblObjects = new JLabel("Objects:");
		lblObjects.setBounds(15, startY , labelWidth, 25);
		lblObjects.setFont(fontBold);

		btnCreateObjects = new JButton("Create Objects");
		btnCreateObjects.setBounds(15 + fieldWidth - 150, startY + fieldHeight + 80 , 150, 30);
		btnCreateObjects.setEnabled(false);
		btnCreateObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				showDialogCreateObjects(panel.getParent());
				showDialogCreateObjects(panel);
			}
		});
		panel.add(btnCreateObjects);

		txtObjects = new JTextArea();
		txtObjects.setLineWrap(true);
		txtObjects.setWrapStyleWord(true);
		txtObjects.setEditable(false);
		txtObjects.setBackground(Color.WHITE);
		txtObjects.setForeground(Color.BLACK);
		txtObjects.setFont(font);

		scrollObjects = new JScrollPane(
				txtObjects,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);
		scrollObjects.setBounds(15, startY + 30, fieldWidth, fieldHeight+40);

		panel.add(lblObjects);
		panel.add(scrollObjects);

		lblLinks = new JLabel("Links:");
		lblLinks.setBounds(15+fieldWidth+60, startY , labelWidth, 25);
		lblLinks.setFont(fontBold);

		btnCreateLinks = new JButton("Create Objects + Links");
		btnCreateLinks.setBounds(15 + fieldWidth*2 - 140, startY + fieldHeight + 80 , 150, 30);
		btnCreateLinks.setEnabled(false);
		btnCreateLinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDialogCreateObjectsAndLinks(panel);
			}
		});
		panel.add(btnCreateLinks);

		txtLinks = new JTextArea();
		txtLinks.setLineWrap(true);
		txtLinks.setWrapStyleWord(true);
		txtLinks.setEditable(false);
		txtLinks.setBackground(Color.WHITE);
		txtLinks.setForeground(Color.BLACK);
		txtLinks.setFont(font);

		scrollLinks = new JScrollPane(
				txtLinks,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);

		scrollLinks.setBounds(15+fieldWidth+10, startY + 30, fieldWidth, fieldHeight+40);

		panel.add(lblLinks);
		panel.add(scrollLinks);

		lblProperties = new JLabel("Properties:");
		lblProperties.setBounds(15, startY + fieldHeight + 100, labelWidth, 25);
		lblProperties.setFont(fontBold);

		txtProperties = new JTextArea();
		txtProperties.setLineWrap(true);
		txtProperties.setWrapStyleWord(true);
		txtProperties.setEditable(false);
		txtProperties.setBackground(Color.WHITE);
		txtProperties.setForeground(Color.BLACK);
		txtProperties.setFont(font);

		scrollProperties = new JScrollPane(
				txtProperties,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);


		scrollProperties.setBounds(15, startY + fieldHeight + 130, fieldWidth*2+10, fieldHeight);

		panel.add(lblProperties);
		panel.add(scrollProperties);

		lblComments = new JLabel("Comments:");
		lblComments.setBounds(15, startY + fieldHeight*2 + 140, labelWidth, 25);
		lblComments.setFont(fontBold);

		txtComments = new JTextArea();
		txtComments.setLineWrap(true);
		txtComments.setWrapStyleWord(true);
		txtComments.setEditable(false);
		txtComments.setBackground(Color.WHITE);
		txtComments.setForeground(Color.BLACK);
		txtComments.setFont(font);

		scrollComments = new JScrollPane(txtComments);
		scrollComments.setBounds(15, startY + fieldHeight*2 + 170, fieldWidth*2+10, fieldHeight+90);

		panel.add(lblComments);
		panel.add(scrollComments);

		btnCopyComment = new JButton("Copy Comment");
		btnCopyComment.setBounds(175, startY + fieldHeight*4 +90, 150, 30);
		btnCopyComment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = txtComments.getText();
				StringSelection selection = new StringSelection(text);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, null);
			}

		});

		panel.add(btnCopyComment);

		btnCopyTXTRequest = new JButton("Request txt");
		btnCopyTXTRequest.setBounds(335, startY + fieldHeight*4 +90, 150, 30);
		btnCopyTXTRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(mensaje); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			}
		});

		panel.add(btnCopyTXTRequest);

		btnCopyJSONRequest = new JButton("JSON Request");
		btnCopyJSONRequest.setBounds(495, startY + fieldHeight*4 +90, 150, 30);
		btnCopyJSONRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(jsonPretty); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			}
		});
		panel.add(btnCopyJSONRequest);

		btnCopyJSONResult = new JButton("JSON Result");
		btnCopyJSONResult.setBounds(655, startY + fieldHeight*4 +90, 150, 30);
		btnCopyJSONResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(jsonResult); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			}
		});
		panel.add(btnCopyJSONResult);

		btnExit = new JButton("Exit");
		btnExit.setBounds(fieldWidth*2-130, startY + fieldHeight*4 +90, 150, 30);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				System.exit(0); //Provis
				fWizardMVMView.setStrCreateObjectsAI("");
				fWizardMVMView.setStrCreateLinksAI("");
				dispose();
			}
		});
		panel.add(btnExit);

		getRootPane().setDefaultButton(btnExit);

		//---

		btnCopyComment.setVisible(false);
		btnCopyTXTRequest.setVisible(false);
		btnCopyJSONRequest.setVisible(false);
		btnCopyJSONResult.setVisible(false);

		btnShowButtons = new JToggleButton("Show options");
		btnShowButtons.setBounds(15, startY + fieldHeight*4 +90, 150, 30);
		btnShowButtons.setSelected(false); // inicialmente ocultos

		btnShowButtons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean visible = btnShowButtons.isSelected();

				btnCopyComment.setVisible(visible);
				btnCopyTXTRequest.setVisible(visible);
				btnCopyJSONRequest.setVisible(visible);
				btnCopyJSONResult.setVisible(visible);

				btnShowButtons.setText(visible ? "Hide options" : "Show options");

				// refrescar layout si es necesario
				revalidate();
				repaint();
			}
		});
		panel.add(btnShowButtons);

		this.add(panel);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setResizable(false);

	}

	public static StringBuilder createTextObjects(String jsonArrayText) {
		StringBuilder sb = new StringBuilder();

		try {
			JSONArray arr = new JSONArray(jsonArrayText);

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				String className = obj.optString("class", "unknown");
				String name = obj.optString("name", "unknown");
				JSONObject attributes = obj.optJSONObject("attributes");

				sb.append("class:\"")
				.append(className)
				.append("\",name:\"")
				.append(name)
				.append("\",attributes:")
				.append(attributes != null ? attributes.toString() : "{}");

				if (i < arr.length() - 1) {
					sb.append("\n");
				}
			}

		} catch (JSONException e) {
			sb.append("Error parsing JSON: ").append(e.getMessage());
		}

		return sb;
	}

	public static void showDialogCreateObjects(JPanel parent) {
		StringBuilder sb = createTextObjects( rObjects);

		// Crear el dialogo
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Objects to Create");
		dialog.setLayout(new BorderLayout());

		// Área de texto con scroll
		JTextArea textArea = new JTextArea("\n"+sb.toString()+"\n");
		textArea.setFont(font);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);

		// Añadir componentes
		dialog.add(scroll, BorderLayout.CENTER);

		JPanel panelButton = new JPanel();
		//.--

		// Crear el botón Create
		JButton btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(150, 30));

		// Listener del botón Create (si lo necesitas)
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// acción del botón Create
				System.out.println("Create");


				fWizardMVMView.setStrCreateObjectsAI(rObjects);
				//		    	fWizardMVMView.setStrCreateLinksAI(rLinks);

				dialog.dispose();
				// cerrar el JFrame padre
				//aqui
				Window window = SwingUtilities.getWindowAncestor(parent.getParent());
				if (window instanceof MVMShowResponseOpenAI3) {
					((MVMShowResponseOpenAI3) window).dispose();
				}

			}
		});
		panelButton.add(btnCreate);

		//---



		// Botón Exit
		JButton btnExit = new JButton("Exit");
		btnExit.setPreferredSize(new Dimension(150, 30));

		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		// Panel para centrar el botón
		//		JPanel panelButton = new JPanel();
		panelButton.add(btnExit);

		dialog.add(panelButton, BorderLayout.SOUTH);

		dialog.pack();
		int width = dialog.getWidth();
		if (width > 1500) {
			width = 1500;
		}
		dialog.setSize(width, dialog.getHeight());
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	//	public static StringBuilder createTextLinksOLd(String jsonArrayText) {
	//		StringBuilder sb = new StringBuilder();
	//
	//		try {
	//			JSONArray arr = new JSONArray(jsonArrayText);
	//
	//			for (int i = 0; i < arr.length(); i++) {
	//				JSONObject obj = arr.getJSONObject(i);
	//
	//				String association = obj.optString("association", "unknown");
	//				String from = obj.optString("from", "unknown");
	//				String to = obj.optString("to", "unknown");
	//
	//				int id = i + 1;
	//
	//				sb.append("Link id=[")
	//				.append(id)
	//				.append("]|association=[")
	//				.append(association)
	//				.append("] from=[")
	//				.append(from)
	//				.append("] to=[")
	//				.append(to)
	//				.append("]\n");
	//			}
	//
	//		} catch (JSONException e) {
	//			sb.append("Error parsing JSON: ").append(e.getMessage());
	//		}
	//
	//		return sb;
	//	}

	public static StringBuilder createTextLinks(String jsonArrayText) {
		StringBuilder sb = new StringBuilder();

		try {
			JSONArray arr = new JSONArray(jsonArrayText);

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				String nomAssoc   = obj.optString("nomAssoc", "unknown");
				String codeLink   = obj.optString("codeLink", "unknown");

				String end1Class  = obj.optString("end1Class", "unknown");
				String end1Object = obj.optString("end1Object", "unknown");
				String end1Role   = obj.optString("end1Role", "unknown");

				String end2Class  = obj.optString("end2Class", "unknown");
				String end2Object = obj.optString("end2Object", "unknown");
				String end2Role   = obj.optString("end2Role", "unknown");

				sb.append("nomAssoc:\"").append(nomAssoc).append("\",")
				.append("codeLink:\"").append(codeLink).append("\",")
				.append("end1Class:\"").append(end1Class).append("\",")
				.append("end1Object:\"").append(end1Object).append("\",")
				.append("end1Role:\"").append(end1Role).append("\",")
				.append("end2Class:\"").append(end2Class).append("\",")
				.append("end2Object:\"").append(end2Object).append("\",")
				.append("end2Role:\"").append(end2Role).append("\"");

				// Si no es el último, añade salto de línea
				if (i < arr.length() - 1) {
					sb.append("\n");
				}
			}

		} catch (JSONException e) {
			sb.append("Error parsing JSON: ").append(e.getMessage());
		}

		return sb;
	}

	public static void showDialogCreateObjectsAndLinks(JPanel parent) {

		StringBuilder sbObjects = createTextObjects( rObjects);
		StringBuilder sbLinks = createTextLinks( rLinks);

		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Objects & Links to create");
		dialog.setLayout(new BorderLayout());

		StringBuilder sbFinal = new StringBuilder();

		sbFinal.append("\n");
		sbFinal.append("OBJECTS\n");
		sbFinal.append("=======\n");
		sbFinal.append(sbObjects.toString());
		sbFinal.append("\n\n");
		sbFinal.append("LINKS\n");
		sbFinal.append("=====\n");
		sbFinal.append(sbLinks.toString());
		sbFinal.append("\n");

		JTextArea textArea = new JTextArea(sbFinal.toString());
		textArea.setFont(font);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);

		dialog.add(scroll, BorderLayout.CENTER);

		JButton btnExit = new JButton("Exit");
		btnExit.setPreferredSize(new Dimension(150, 30));

		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		//		JPanel panelButton = new JPanel();


		JPanel panelButton = new JPanel();
		//.--

		// Crear el botón Create
		JButton btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(150, 30));

		// Listener del botón Create (si lo necesitas)
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// acción del botón Create
				System.out.println("Create");


				fWizardMVMView.setStrCreateObjectsAI(rObjects);
				fWizardMVMView.setStrCreateLinksAI(rLinks);

				dialog.dispose();

				// cerrar el JFrame padre
				//aqui
				Window window = SwingUtilities.getWindowAncestor(parent.getParent());
				if (window instanceof MVMShowResponseOpenAI3) {
					((MVMShowResponseOpenAI3) window).dispose();
				}

			}
		});
		panelButton.add(btnCreate);

		//---



		panelButton.add(btnExit);

		dialog.add(panelButton, BorderLayout.SOUTH);

		dialog.pack();
		int width = dialog.getWidth();
		if (width > 1500) {
			width = 1500;
		}
		dialog.setSize(width, dialog.getHeight());
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
	public static String getStrNameModel() {
		return strNameModel;
	}
	public static void setStrNameModel(String strNameModel) {
		MVMShowResponseOpenAI3.strNameModel = strNameModel;
	}
	public static String getStrCreateObjectsAI() {
		return strCreateObjectsAI;
	}
	public static void setStrCreateObjectsAI(String strCreateObjectsAI) {
		MVMShowResponseOpenAI3.strCreateObjectsAI = strCreateObjectsAI;
	}
	public static String getStrCreateLinksAI() {
		return strCreateLinksAI;
	}
	public static void setStrCreateLinksAI(String strCreateLinksAI) {
		MVMShowResponseOpenAI3.strCreateLinksAI = strCreateLinksAI;
	}

}
