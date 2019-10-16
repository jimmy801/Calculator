import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 
 * Costume Label Component, it will auto adjust font size to fixed<br>
 * See the
 * <a href="http://java-sl.com/tip_adapt_label_font_size.html">Reference</a>
 * 
 * @author Jimmy801
 * @see {@link JLabel}
 * 
 */
public class ResizeLabelFont extends JLabel {
	/** custom minimize font size */
	private int MIN_FONT_SIZE = 3;
	/** custom maximum font size */
	private int MAX_FONT_SIZE = 240;

	/**
	 * Creates a ResizeLabelFont instance with the specified text and horizontal
	 * alignment.
	 * 
	 * @see {@link JLabel#JLabel(String, int)}
	 * @param text                - The text to be displayed by the label.
	 * @param horizontalAlignment - One of the following constants defined in
	 *                            SwingConstants: LEFT, CENTER, RIGHT, LEADING or
	 *                            TRAILING.
	 */
	public ResizeLabelFont(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		init();
	}

	/**
	 * Creates a ResizeLabelFont instance with the specified text.
	 * 
	 * @see {@link JLabel#JLabel()}
	 * @param text - The text to be displayed by the label.
	 */
	public ResizeLabelFont(String text) {
		super(text);
		init();
	}

	/**
	 * Set maximum size of ResizeLabelFont component font.
	 * 
	 * @param maxSize - The size will be set.
	 */
	public void setMaxFontSize(int maxSize) {
		MAX_FONT_SIZE = maxSize;
	}

	/**
	 * Set minimize size of ResizeLabelFont component font.
	 * 
	 * @param minSize - The size will be set.
	 */
	public void setMinFontSize(int minSize) {
		MIN_FONT_SIZE = minSize;
	}

	/**
	 * Get current label text size
	 * 
	 * @return label text size
	 */
	public Dimension getTextSize() {
		return getTextSize(this.getText(), this.getFont());
	}

	/**
	 * Initialize function, register listeners.
	 */
	protected void init() {
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				adaptLabelFont();
			}
		});
		this.addPropertyChangeListener("text", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				adaptLabelFont();
			}
		});
	}

	/**
	 * Adjust font size from MIN_FONT_SIZE to MAX_FONT_SIZE
	 */
	protected void adaptLabelFont() {
		if (this.getGraphics() == null) {
			return;
		}
		Rectangle r = this.getBounds();
		int fontSize = MIN_FONT_SIZE;
		Font f = this.getFont();
		String text = this.getText();

		Rectangle r1 = new Rectangle();
		Rectangle r2 = new Rectangle();
		while (fontSize < MAX_FONT_SIZE) {
			r1.setSize(getTextSize(text, f.deriveFont(f.getStyle(), fontSize)));
			r2.setSize(getTextSize(text, f.deriveFont(f.getStyle(), fontSize + 1)));
			if (r.contains(r1) && !r.contains(r2) || r2.getHeight() > r.getHeight() || r2.getWidth() > r.getWidth()) {
				break;
			}
			fontSize++;
		}

		setFont(f.deriveFont(f.getStyle(), fontSize));
		repaint();
	}

	/**
	 * Calculate the dimension of text.
	 * 
	 * @param text - The string of text
	 * @param f    - The font of text
	 * @return {@link Dimension} dimension of text
	 */
	private Dimension getTextSize(String text, Font f) {
		Dimension size = new Dimension();
		Graphics g = this.getGraphics();
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);
		size.width = fm.stringWidth(text);
		size.height = fm.getHeight();

		return size;
	}
}