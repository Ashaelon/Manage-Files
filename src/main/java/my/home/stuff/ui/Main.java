package my.home.stuff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import my.home.stuff.model.ComparisonResult;

public class Main extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList<String> lstFolders;

	
	private SimpleAttributeSet red;
	private SimpleAttributeSet black;
	
	{
		red = new SimpleAttributeSet();
		StyleConstants.setForeground(red, Color.RED);
		
		black = new SimpleAttributeSet();
		StyleConstants.setForeground(black, Color.BLACK);
	}
	
	/**
	 * Create the panel.
	 */
	public Main() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel versionPanel = new JPanel();
		buttonPanel.add(versionPanel, BorderLayout.WEST);
		
		JLabel lblVersion = new JLabel("Version1.0");
		versionPanel.add(lblVersion);
		
		JPanel actionButtonPanel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) actionButtonPanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		buttonPanel.add(actionButtonPanel);
		
		JButton btnCompare = new JButton("Compare");
		btnCompare.setAction(new CompareAction());
		actionButtonPanel.add(btnCompare);
		
		JPanel listManageButtonPanel = new JPanel();
		add(listManageButtonPanel, BorderLayout.EAST);
		listManageButtonPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		JButton btnAdd = new JButton("Add...");
		btnAdd.setAction(new AddAction());
		listManageButtonPanel.add(btnAdd, "2, 2, fill, fill");
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setAction(new RemoveAction());
		listManageButtonPanel.add(btnRemove, "2, 4, fill, fill");
		
		JButton btnRemoveAll = new JButton("Remove All");
		btnRemoveAll.setAction(new RemoveAllAction());
		listManageButtonPanel.add(btnRemoveAll, "2, 6, fill, fill");
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);
		
		JLabel lblFoldersToCompare = new JLabel("Folders to compare:");
		panel.add(lblFoldersToCompare);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"),}));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, "2, 1, fill, fill");
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		lstFolders = new JList<>();
		scrollPane.setViewportView(lstFolders);
		DefaultListModel<String> model = new DefaultListModel<>();
		lstFolders.setModel(model);

	}
	
	private DialogResult getFileDialog() {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setMultiSelectionEnabled(true);
			int ret = chooser.showOpenDialog(this);
			DialogResult result = new DialogResult();
			switch (ret) {
				case JFileChooser.CANCEL_OPTION:
					result.setCanceled(true);
					break;
				default:
					result.setCanceled(false);
					String[] paths = new String[chooser.getSelectedFiles().length];
					for (int i = 0; i < chooser.getSelectedFiles().length; i++) {
						paths[i] = chooser.getSelectedFiles()[i].getAbsolutePath();
					}
					result.setPaths(paths);
			}
			
			chooser = null;
			
			return result;
	}
	
	public void compareFolders() {
		List<List<String>> filesList = new ArrayList<>();
		DefaultListModel<String> model = (DefaultListModel<String>) lstFolders.getModel();
		Enumeration<String> elements = model.elements();
		while (elements.hasMoreElements()) {
			List<String> files = new ArrayList<>();
			File dir = new File(elements.nextElement());
			files = iterateFiles(dir);
			filesList.add(files);
		}
		
		LinkedHashMap<String, ComparisonResult> results = new LinkedHashMap<>();
		List<String> exts = new ArrayList<>();
		
		for (int i = 0; i < filesList.size(); i++) {
			List<String> files = filesList.get(i);
			ComparisonResult result = new ComparisonResult();
			
			for (String file : files) {
				if (file.contains(".")) {
					String ext = file.substring(file.lastIndexOf('.'));
					if (!exts.contains(ext)) exts.add(ext);
					
					if (!canProcessFile(ext)) {
						continue;
					}
				} else {
					continue;
				}
				
				if (results.containsKey(file)) {
					continue;
				}
				
				List<Boolean> exists = new ArrayList<>();
				for (int j = 0; j < filesList.size(); j++) {
					if (j == i) {
						exists.add(true);
						continue;
					}
					
					exists.add(filesList.get(j).contains(file));
				}
				
				if (exists.contains(false)) {
					result.setStyle(red);
				} else {
					result.setStyle(black);
				}
				
				result.setExists(exists);
				results.put(file, result);
			}
		}
		
		exts.stream().forEach(e -> System.out.println(e));
		
		Results resultsView = new Results();
		resultsView.setResults(results);
		resultsView.setVisible(true);
	}
	
	private boolean canProcessFile(String ext) {
		switch (ext.toLowerCase()) {
			case ".mp3":
			case ".m4a":
			case ".wma":
			case ".aif":
				return true;
		}
		
		return false;
	}

	private List<String> iterateFiles(File directory) {
		return iterateFiles("", directory);
	}
	
	private List<String> iterateFiles(String parentDir, File directory) {
		System.out.println(directory.getName());
		List<String> fileList = new ArrayList<>();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				fileList.addAll(iterateFiles(String.format("%s%s%s", parentDir, File.separator, f.getName()), f));
			} else {
				fileList.add(String.format("%s%s", parentDir, f.getAbsolutePath().replace(directory.getAbsolutePath(), "")));
			}
		}
		
		return fileList;
	}

	private class AddAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public AddAction() {
			this.putValue(NAME, "Add...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultListModel<String> model = (DefaultListModel<String>) lstFolders.getModel();
			DialogResult result = getFileDialog();
			if (!result.isCanceled()) {
				result.getPaths().stream().forEach(p -> {
					if (!model.contains(p)) {
						model.addElement(p);
					}
				});
			}
		}
		
	}
	
	private class RemoveAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public RemoveAction() {
			this.putValue(NAME, "Remove");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultListModel<String> model = (DefaultListModel<String>) lstFolders.getModel();
			while (lstFolders.getSelectedIndices().length > 0) {
				int[] indices = lstFolders.getSelectedIndices();
				model.remove(indices[indices.length - 1]);
			}
		}
		
	}
	
	private class RemoveAllAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
		public RemoveAllAction() {
			this.putValue(NAME, "Remove All");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultListModel<String> model = (DefaultListModel<String>) lstFolders.getModel();
			model.clear();
		}
		
	}
	
	private class CompareAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public CompareAction() {
			this.putValue(NAME, "Compare");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			compareFolders();
		}
		
	}
	
	private class DialogResult {
		private boolean canceled;
		private List<String> paths;
		
		{
			canceled = false;
			paths = new ArrayList<String>();
		}
		
		public DialogResult() {
		}
		
		public void setCanceled(boolean canceled) {
			
		}
		
		public boolean isCanceled() {
			return canceled;
		}
		
		public void setPaths(String[] paths) {
			setPaths(Arrays.asList(paths));
		}
		
		public void setPaths(List<String> paths) {
			this.paths = paths;
		}
		
		public List<String> getPaths() {
			return this.paths;
		}
		
	}
	
}
