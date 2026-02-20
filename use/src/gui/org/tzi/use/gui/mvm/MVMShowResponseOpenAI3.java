package org.tzi.use.gui.mvm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MVMShowResponseOpenAI3 extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// NEW INI --------------------------
	private static JLabel lblModel;
	private static JLabel lblObjects;
	private static JLabel lblLinks;
	private static JLabel lblProperties;
	private static JLabel lblComments;

	private static JTextField txtModel;
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

	private static String mensaje;
	private static String jsonPretty;
	private static String jsonResult;

	// NEW FIN -----------------------------------------------------------

	private String contentNew="";

	public MVMShowResponseOpenAI3(JFrame pParent, String pStrNameModel, String pObjects, 
			String pLinks, String pProperties,String pComments,
			String pMensaje, String pJsonPretty, String pJsonResult) {
		super((JFrame) pParent, "Request to OpenAI for '"+pStrNameModel+"'", ModalityType.APPLICATION_MODAL);

		strNameModel=pStrNameModel;	
		parent=pParent;

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
			// Obtener claves usando keys() (compatible con todas las versiones)
			List<String> keys = new ArrayList<>();
			Iterator<String> it = obj.keys();
			while (it.hasNext()) {
				keys.add(it.next());
			}

			// Ordenar alfabéticamente (opcional)
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
			// Si ya es JSONArray, usarlo directamente
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

	public static String buildLinksTree(String jsonString) {

		JSONArray arr;
		StringBuilder sb = new StringBuilder();

		try {
			Object parsed = new JSONTokener(jsonString).nextValue();

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
			// Si ya es JSONArray, usarlo directamente
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



	public String getContentNew() {
		return contentNew;
	}

	private void initUI() {

		panel = new JPanel(null);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		int labelWidth = 130;
		int fieldWidth = 400;
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
				showDialogCreateObjects(panel.getParent(), rObjects);
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
		scrollObjects.setBounds(15, startY + 30, fieldWidth+50, fieldHeight+40);

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
				showDialogCreateObjectsAndLinks(panel.getParent(), rLinks);
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

		scrollLinks.setBounds(15+fieldWidth+60, startY + 30, fieldWidth-50, fieldHeight+40);

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
		btnCopyComment.setBounds(10, startY + fieldHeight*4 +90, 150, 30);
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
		btnCopyTXTRequest.setBounds(170, startY + fieldHeight*4 +90, 150, 30);
		btnCopyTXTRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(mensaje); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			}

		});
		panel.add(btnCopyTXTRequest);

		btnCopyJSONRequest = new JButton("JSON Request");
		btnCopyJSONRequest.setBounds(330, startY + fieldHeight*4 +90, 150, 30);
		btnCopyJSONRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(jsonPretty); 
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			}

		});
		panel.add(btnCopyJSONRequest);

		btnCopyJSONResult = new JButton("JSON Result");
		btnCopyJSONResult.setBounds(490, startY + fieldHeight*4 +90, 150, 30);
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
				System.exit(0); //Provis
				//				dispose();
			}
		});
		panel.add(btnExit);

		getRootPane().setDefaultButton(btnExit);

		this.add(panel);
		this.setSize(853, 925);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	}

	public static StringBuilder createTextObjects(String jsonArrayText) {
		StringBuilder sb = new StringBuilder();

		try {
			JSONArray arr = new JSONArray(jsonArrayText);

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				String name = obj.optString("name", "unknown");
				String className = obj.optString("class", "unknown");

				StringBuilder attrs = new StringBuilder();

				JSONArray keys = obj.names();
				for (int j = 0; j < keys.length(); j++) {
					String key = keys.getString(j);

					if (!key.equals("name") && !key.equals("class")) {
						attrs.append(key)
						.append("=")
						.append(obj.get(key))
						.append(", ");
					}
				}

				if (attrs.length() > 0) {
					attrs.setLength(attrs.length() - 2);
				}

				sb.append("Object name=[")
				.append(name)
				.append("] class=[")
				.append(className)
				.append("] attributes=[")
				.append(attrs)
				.append("]\n");
			}

		} catch (JSONException e) {
			sb.append("Error parsing JSON: ").append(e.getMessage());
		}
		return sb;
	}
	public static void showDialogCreateObjects(Component parent, String jsonArrayText) {
		StringBuilder sb = createTextObjects( jsonArrayText);

		// Crear el dialogo
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Objects to Create");
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(parent);
		dialog.setLayout(new BorderLayout());

		// Área de texto con scroll
		JTextArea textArea = new JTextArea(sb.toString());
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);

		// Añadir componentes
		dialog.add(scroll, BorderLayout.CENTER);

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
		JPanel panelButton = new JPanel();
		panelButton.add(btnExit);

		dialog.add(panelButton, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public static StringBuilder createTextLinks(String jsonArrayText) {
		StringBuilder sb = new StringBuilder();

		try {
			JSONArray arr = new JSONArray(jsonArrayText);

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				String association = obj.optString("association", "unknown");
				String from = obj.optString("from", "unknown");
				String to = obj.optString("to", "unknown");

				int id = i + 1;

				sb.append("Link id=[")
				.append(id)
				.append("]|association=[")
				.append(association)
				.append("] from=[")
				.append(from)
				.append("] to=[")
				.append(to)
				.append("]\n");
			}

		} catch (JSONException e) {
			sb.append("Error parsing JSON: ").append(e.getMessage());
		}


		return sb;
	}

	public static void showDialogCreateObjectsAndLinks(Component parent, String jsonArrayText) {

		StringBuilder sbObjects = createTextObjects( jsonArrayText);
		StringBuilder sbLinks = createTextLinks( jsonArrayText);

		// Crear el diálogo
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Objects & Links to create");
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(parent);
		dialog.setLayout(new BorderLayout());

		StringBuilder sbFinal = new StringBuilder();

		sbFinal.append("OBJECTS\n");
		sbFinal.append("=======\n");
		sbFinal.append(sbObjects.toString());
		sbFinal.append("\n");
		sbFinal.append("LINKS\n");
		sbFinal.append("=====\n");
		sbFinal.append(sbLinks.toString());

		// Área de texto con scroll
		JTextArea textArea = new JTextArea(sbFinal.toString());
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);

		// Añadir componentes
		dialog.add(scroll, BorderLayout.CENTER);

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
		JPanel panelButton = new JPanel();
		panelButton.add(btnExit);

		dialog.add(panelButton, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}


}
