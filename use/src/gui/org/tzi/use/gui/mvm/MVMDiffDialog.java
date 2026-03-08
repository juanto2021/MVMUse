package org.tzi.use.gui.mvm;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;

//import diff_match_patch;



public class MVMDiffDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MVMDiffDialog(Frame parent, String s1, String s2) {
		super(parent, "Comparador de textos", true); // true = modal

		setTitle("Comparador de textos");
//		setSize(1500, 800);
//		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		// --- CHECKBOX PARA ACTIVAR/DESACTIVAR SINCRONIZACIÓN ---
		JCheckBox syncCheck = new JCheckBox("Sincronizar scroll", true);

		JPanel topPanel = new JPanel();
		topPanel.add(syncCheck);
		add(topPanel, BorderLayout.NORTH);

		// --- SPLITPANE ---
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setResizeWeight(0.5);

		List<String> lines1 = Arrays.asList(s1.split("\n"));
		List<String> lines2 = Arrays.asList(s2.split("\n"));

		List<Pair> aligned = align(lines1, lines2);

		JEditorPane leftPane = new JEditorPane();
		leftPane.setContentType("text/html");
		leftPane.setEditable(false);
		leftPane.setText(renderLeft(aligned));

		JEditorPane rightPane = new JEditorPane();
		rightPane.setContentType("text/html");
		rightPane.setEditable(false);
		rightPane.setText(renderRight(aligned));

		JScrollPane leftScroll = new JScrollPane(leftPane);
		JScrollPane rightScroll = new JScrollPane(rightPane);

		// --- SINCRONIZACIÓN SUAVE SIN LAMBDAS ---
		final boolean[] adjusting = { false };

		AdjustmentListener smoothSync = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {

				if (!syncCheck.isSelected()) return;
				if (adjusting[0]) return;

				adjusting[0] = true;

				JScrollBar source = (JScrollBar) e.getSource();
				JScrollBar target = (source == leftScroll.getVerticalScrollBar())
						? rightScroll.getVerticalScrollBar()
								: leftScroll.getVerticalScrollBar();

				int start = target.getValue();
				int end = source.getValue();

				final int[] distance = { end - start };

				Timer timer = new Timer(5, null);

				timer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {

						int step = (int) (distance[0] * 0.2);
						if (Math.abs(step) < 1) step = distance[0];

						target.setValue(target.getValue() + step);
						distance[0] -= step;

						if (distance[0] == 0) {
							timer.stop();
							adjusting[0] = false;
						}
					}
				});

				timer.start();
			}
		};

		leftScroll.getVerticalScrollBar().addAdjustmentListener(smoothSync);
		rightScroll.getVerticalScrollBar().addAdjustmentListener(smoothSync);
		// --- FIN SINCRONIZACIÓN SUAVE ---
		// --- FIN SINCRONIZACIÓN SUAVE ---

		split.setLeftComponent(leftScroll);
		split.setRightComponent(rightScroll);

		add(split, BorderLayout.CENTER);

//		setVisible(true);
	}

	private static class Pair {
		String left;
		String right;
		Pair(String l, String r) { left = l; right = r; }
	}

	// ---------------------------------------------------------
	// SIMILITUD REAL usando diff-match-patch
	// ---------------------------------------------------------
	private boolean similar(String a, String b) {
		if (a == null || b == null) return false;
		if (a.equals(b)) return true;

		diff_match_patch dmp = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(a, b);
		dmp.diff_cleanupSemantic(diffs);

		int equalChars = 0;
		int total = Math.max(a.length(), b.length());

		for (diff_match_patch.Diff d : diffs) {
			if (d.operation == diff_match_patch.Operation.EQUAL) {
				equalChars += d.text.length();
			}
		}

		double ratio = (double) equalChars / total;
		return ratio > 0.6;
	}

	// ---------------------------------------------------------
	// ALINEACIÓN LCS usando similar()
	// ---------------------------------------------------------
	private List<Pair> align(List<String> a, List<String> b) {

		int n = a.size(), m = b.size();
		int[][] dp = new int[n+1][m+1];

		for (int i = n-1; i >= 0; i--) {
			for (int j = m-1; j >= 0; j--) {
				if (similar(a.get(i), b.get(j)))
					dp[i][j] = 1 + dp[i+1][j+1];
				else
					dp[i][j] = Math.max(dp[i+1][j], dp[i][j+1]);
			}
		}

		List<Pair> res = new ArrayList<>();
		int i = 0, j = 0;

		while (i < n && j < m) {
			if (similar(a.get(i), b.get(j))) {
				res.add(new Pair(a.get(i), b.get(j)));
				i++; j++;
			} else if (dp[i+1][j] >= dp[i][j+1]) {
				res.add(new Pair(a.get(i), null));
				i++;
			} else {
				res.add(new Pair(null, b.get(j)));
				j++;
			}
		}

		while (i < n) res.add(new Pair(a.get(i++), null));
		while (j < m) res.add(new Pair(null, b.get(j++)));

		return res;
	}

	// ---------------------------------------------------------
	// PANEL IZQUIERDO (v1)
	// ---------------------------------------------------------
	private String renderLeft(List<Pair> aligned) {

		StringBuilder sb = new StringBuilder("<html><pre style='font-size:14px;'>");

		for (Pair p : aligned) {

			if (p.left != null && p.right == null) {
				sb.append("<span style='display:block;color:red;background-color:#ffdddd;'>")
				.append(escapeHtml(p.left))
				.append("</span>\n");
				continue;
			}

			if (p.left == null && p.right != null) {
				sb.append("<span style='display:block;'>&nbsp;</span>\n");
				continue;
			}

			if (p.left.equals(p.right)) {
				sb.append("<span style='display:block;'>")
				.append(escapeHtml(p.left))
				.append("</span>\n");
				continue;
			}

			sb.append("<span style='display:block;background-color:#cce0ff;'>")
			.append(diffDeleteOnly(p.left, p.right))
			.append("</span>\n");
		}

		sb.append("</pre></html>");
		return sb.toString();
	}

	// ---------------------------------------------------------
	// PANEL DERECHO (v2)
	// ---------------------------------------------------------
	private String renderRight(List<Pair> aligned) {

		StringBuilder sb = new StringBuilder("<html><pre style='font-size:14px;'>");

		for (Pair p : aligned) {

			if (p.left != null && p.right == null) {
				sb.append("<span style='display:block;color:red;text-decoration:line-through;background-color:#ffdddd;'>")
				.append(escapeHtml(p.left))
				.append("</span>\n");
				continue;
			}

			if (p.left == null && p.right != null) {
				sb.append("<span style='display:block;color:green;background-color:#ddffdd;'>")
				.append(renderNewLine(p.right))
				.append("</span>\n");
				continue;
			}

			if (p.left.equals(p.right)) {
				sb.append("<span style='display:block;'>")
				.append(escapeHtml(p.right))
				.append("</span>\n");
				continue;
			}

			// MODIFICADA → fondo amarillo
			sb.append("<span style='display:block;background-color:#fff2cc;'>")
			.append(diffInsertModified(p.left, p.right))
			.append("</span>\n");
		}

		sb.append("</pre></html>");
		return sb.toString();
	}

	// ---------------------------------------------------------
	// SOLO DELETE (v1)
	// ---------------------------------------------------------
	private String diffDeleteOnly(String oldLine, String newLine) {

		diff_match_patch dmp = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(oldLine, newLine);
		dmp.diff_cleanupSemantic(diffs);

		StringBuilder sb = new StringBuilder();

		for (diff_match_patch.Diff diff : diffs) {
			switch (diff.operation) {
			case EQUAL:
				sb.append(escapeHtml(diff.text));
				break;
			case DELETE:
				sb.append("<span style='background-color:#ffcccc;'>")
				.append(escapeHtml(diff.text))
				.append("</span>");
				break;
			case INSERT:
				break;
			}
		}

		return sb.toString();
	}

	// ---------------------------------------------------------
	// INSERT DETALLADO (solo diferencias en azul + fondo verde oscuro)
	// ---------------------------------------------------------
	private String diffInsertModified(String oldLine, String newLine) {

		diff_match_patch dmp = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(oldLine, newLine);
		dmp.diff_cleanupSemantic(diffs);

		StringBuilder sb = new StringBuilder();

		for (diff_match_patch.Diff diff : diffs) {
			switch (diff.operation) {

			case EQUAL:
				sb.append(escapeHtml(diff.text));
				break;

			case INSERT:
				sb.append("<span style='color:blue;font-weight:bold;background-color:#99dd99;'>")
				.append(escapeHtml(diff.text))
				.append("</span>");
				break;

			case DELETE:
				break;
			}
		}

		return sb.toString();
	}

	private String renderNewLine(String newLine) {
		return escapeHtml(newLine);
	}

	private String escapeHtml(String s) {
		return s.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}
}