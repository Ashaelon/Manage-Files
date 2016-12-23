package my.home.stuff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import my.home.stuff.model.ComparisonResult;

public class Results extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private LinkedHashMap<String, ComparisonResult> results;
	private JTextPane resultsPane;
	
	public void setResults(LinkedHashMap<String, ComparisonResult> results) {
		this.results = results;
		
		if (results != null && results.size() > 0) {
			processResults();
		}
	}
	
	private void processResults() {
		System.out.println("Results - Processing");
		resultsPane.setFont(new Font("monospaced", Font.PLAIN, 12));
		StyledDocument doc = resultsPane.getStyledDocument();
		int maxLen = 0;
		for (String key : results.keySet()) {
			if (key.trim().length() > maxLen) {
				maxLen = key.trim().length();
			}
		}
		
		boolean altRow = true;
		for (Map.Entry<String, ComparisonResult> e : results.entrySet()) {
			try {
				SimpleAttributeSet lineStyle = new SimpleAttributeSet(e.getValue().getStyle());
				if (altRow) {
					StyleConstants.setBackground(lineStyle, Color.LIGHT_GRAY);
				}
				
				altRow = !altRow;
				
				doc.insertString(doc.getLength(), String.format("%1$-" + maxLen + "s", e.getKey().trim()), lineStyle);
				for (Boolean b : e.getValue().getExists()) {
					doc.insertString(doc.getLength(), String.format("\t%s", String.valueOf(b)), lineStyle);
				}
				doc.insertString(doc.getLength(), "\n", lineStyle);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}

	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Results dialog = new Results();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Results() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setBounds(100, 100, 1214, 708);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				resultsPane = new JTextPane();
				scrollPane.setViewportView(resultsPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.setActionCommand("Cancel");
				buttonPane.add(closeButton);
			}
		}
	}

}
