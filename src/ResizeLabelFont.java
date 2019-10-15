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

	/*
	 * private boolean leftArrowShow; private boolean rightArrowShow; private int
	 * startPos; private Point cursorPos;
	 */

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
	 * Initialize function, register listeners.
	 */
	protected void init() {
		/*
		 * startPos = 0; leftArrowShow = rightArrowShow = false;
		 */
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
		/*
		 * this.addMouseMotionListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseMoved(MouseEvent e) { cursorPos = e.getPoint();
		 * repaint(); } })
		 */;
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

	/*
	 * private void drawArrows(boolean isLeft, Graphics g) { String text = isLeft ?
	 * "〈" : "〉"; Dimension arrowSize = getTextSize(text, this.getFont()); int x =
	 * isLeft ? -arrowSize.width / 2 + 10 : this.getWidth() - arrowSize.width / 2 -
	 * 10, y = this.getLocation().y + arrowSize.height * 2 / 3; Rectangle rec = new
	 * Rectangle(new Point(x, this.getLocation().y), arrowSize); boolean arrowShow =
	 * isLeft ? leftArrowShow : rightArrowShow; System.out.println(rec);
	 * System.out.println(cursorPos); System.out.println(rec.contains(cursorPos));
	 * // System.out.println(rec.contains(new Point(300, 61)));
	 * System.out.println(); if (arrowShow && rec.contains(cursorPos) ) {
	 * g.setColor(Color.cyan); g.drawRect(rec.x, rec.y, arrowSize.width,
	 * arrowSize.height); }
	 * 
	 * g.setColor(this.getForeground()); g.setFont(this.getFont());
	 * g.drawString(text, x, y); }
	 * 
	 * private void drawLeftArrow(Graphics g) { if (leftArrowShow) drawArrows(true,
	 * g); }
	 * 
	 * private void drawRightArrow(Graphics g) { if (rightArrowShow)
	 * drawArrows(false, g); }
	 */

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		/*
		 * Dimension textSize = getTextSize(this.getText(), this.getFont()); boolean
		 * outOfRange = textSize.width > this.getWidth(); leftArrowShow = startPos > 0
		 * && outOfRange; rightArrowShow = textSize.width - startPos > this.getWidth()
		 * && outOfRange; drawLeftArrow(g); drawRightArrow(g);
		 */
	}

}