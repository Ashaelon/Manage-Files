package my.home.stuff.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;

public class ComparisonResult {
	private List<Boolean> exists;
	private SimpleAttributeSet style;
	
	public ComparisonResult() {
		exists = new ArrayList<>();
	}

	public List<Boolean> getExists() {
		return exists;
	}

	public void setExists(List<Boolean> exists) {
		this.exists = exists;
	}

	public SimpleAttributeSet getStyle() {
		return style;
	}

	public void setStyle(SimpleAttributeSet style) {
		this.style = style;
	}
	
}
