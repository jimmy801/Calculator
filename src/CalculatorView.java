import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Components of this simple calculator
 * 
 * @author Jimmy801
 */
public class CalculatorView extends JFrame {
	/** the place of input value, answers, or temporary calculated value */
	private ResizeLabelFont expLbl;
	/** the place of equation so far */
	private ResizeLabelFont preLbl;
	/** all buttons of calculator */
	private ArrayList<JButton> btns;
	/** cut & copy menu */
	private PopupMenu pm;
	/** the place to put all buttons */
	private JPanel btnPanel;
	/** the place to put exLpl & preLbl */
	private JPanel displayPanel;
	/** font style of labels */
	private String fontName = "LucidaSans";

	/**
	 * object constructor
	 * 
	 * @param calModel - Main calculated Object
	 */
	public CalculatorView() {
		btns = new ArrayList<JButton>();
		initComponents();
	}

	/**
	 * getter method, return text of exLpl
	 */
	public String getExpLblText() {
		return expLbl.getText();
	}

	/**
	 * setter method, set text of exLpl
	 * 
	 * @param str - The text which will be set
	 */
	public void setExpLblText(String str) {
		expLbl.setText(str);
	}

	/**
	 * getter method, return text of preLpl
	 */
	public String getPreLblText() {
		return preLbl.getText();
	}

	/**
	 * setter method, set text of preLpl
	 * 
	 * @param str - The text which will be set
	 */
	public void setPreLblText(String str) {
		preLbl.setText(str);
	}

	/**
	 * getter method, return all buttons of this calculator
	 * 
	 * @return {@link ArrayList}<{@link JButton}> btns
	 */
	public ArrayList<JButton> getBtns() {
		return btns;
	}

	/**
	 * getter method, return buttons container panel
	 * 
	 * @return {@link JButton} btnPanel
	 */
	public JPanel getBtnPanel() {
		return btnPanel;
	}

	/**
	 * getter method, return exLpl & preLbl container panel
	 * 
	 * @return {@link JButton} displayPanel
	 */
	public JPanel getDisplayPanel() {
		return displayPanel;
	}

	/**
	 * getter method, return cut & copy menu container PopupMenu
	 * 
	 * @return {@link PopupMenu} pm
	 */
	public PopupMenu getPM() {
		return pm;
	}

	/**
	 * create a number button
	 * 
	 * @param num - Number string of created button
	 * @return {@link JButton} newNumBtn
	 */
	private JButton numBtn(String num) {
		JButton btn = new JButton(num);
		btn.setFont(new Font(fontName, Font.BOLD, 30));
		btn.setBackground(Color.WHITE);
		return btn;
	}

	/**
	 * Create a operation button
	 * 
	 * @param op - Operator string of created button
	 * @return {@link JButton} newOpBtn
	 */
	private JButton opBtn(String op) {
		JButton btn = new JButton(op);
		btn.setFont(new Font(fontName, Font.BOLD, 30));
		btn.setBackground(Color.LIGHT_GRAY);
		return btn;
	}

	/**
	 * initial button components & its container
	 */
	private void initBtnPanel() {
		btnPanel = new JPanel();
		int w = 4, h = 5;
		btnPanel.setLayout(new GridLayout(h, w));
		/**
		 * temporary container to save string of buttons by created order<BR>
		 * if <@link GridLayout> component add elements not by order, it would created
		 * out of our exception
		 */
		ArrayList<String> btnStr = new ArrayList<>();
		/*
		 * // some operator can be added, but it seems too troublesome
		 * btnStr.add(CalUtils.sqrtStr); btnStr.add(CalUtils.powStr);
		 */

		btnStr.add(CalUtils.currentEmptyStr);
		btnStr.add(CalUtils.clearStr);
		btnStr.add(CalUtils.backStr);

		int opStart = btnStr.size();
		// #region create number buttons
		int pos = -1;
		for (int i = 9; i > 0; --i) {
			if (i % 3 == 0)
				pos = btnStr.size();
			btnStr.add(pos, String.valueOf(i));
		}
		// #endregion

		// #region +-*/ buttons are next to number buttons
		btnStr.add(opStart, CalUtils.divStr);
		btnStr.add(opStart + w, CalUtils.mulStr);
		btnStr.add(opStart + w * 2, CalUtils.subStr);
		// #endregion

		btnStr.add(CalUtils.addStr);
		btnStr.add(CalUtils.signStr);
		btnStr.add("0");
		btnStr.add(CalUtils.dotStr);
		btnStr.add(CalUtils.eqStr);

		// '(' and ')' buttons, a little troublesome
		/*
		 * btnStr.add(CalUtils.leftParenthesisStr);
		 * btnStr.add(CalUtils.rightParenthesisStr);
		 */

		// create buttons
		for (String str : btnStr) {
			JButton btn;
			if (CalUtils.isDigit(str))
				btn = numBtn(str);
			else
				btn = opBtn(str);
			btns.add(btn);
			btnPanel.add(btn);
		}
	}

	private JButton transparentBtn(String text) {
		JButton btn = new JButton(text);
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		return btn;
	}

	/**
	 * initial display components & its container
	 */
	private void initDisplayPanel() {
		displayPanel = new JPanel();
		displayPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		gbc.gridx = 0;
		gbc.gridy = 1;

		int expLblMaxFont = 70, expLblMinFont = 20;
		expLbl = new ResizeLabelFont("0", SwingConstants.RIGHT);
		expLbl.setVerticalAlignment(SwingConstants.BOTTOM);
		expLbl.setMaxFontSize(expLblMaxFont);
		expLbl.setMinFontSize(expLblMinFont);
		expLbl.setFont(new Font(fontName, Font.BOLD, expLblMaxFont));
		displayPanel.add(expLbl, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		int preLblMaxFont = 30, preLblMinFont = 10;
		preLbl = new ResizeLabelFont("", SwingConstants.RIGHT);
		preLbl.setVerticalAlignment(SwingConstants.BOTTOM);
		preLbl.setMaxFontSize(preLblMaxFont);
		preLbl.setMinFontSize(preLblMinFont);
		preLbl.setFont(new Font(fontName, Font.BOLD, preLblMaxFont));
		preLbl.setForeground(Color.GRAY);
		displayPanel.add(preLbl, gbc);

		pm = new PopupMenu();
		MenuItem CopyMI = new MenuItem();
		MenuItem PateMI = new MenuItem();
		CopyMI.setLabel(CalUtils.copyStr);
		PateMI.setLabel(CalUtils.pasteStr);
		pm.add(CopyMI);
		pm.add(PateMI);
		displayPanel.add(pm);
	}

	/**
	 * initial all components
	 */
	private void initComponents() {
		this.setLayout(new GridBagLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(CalUtils.window_size);

		initDisplayPanel();

		initBtnPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridheight = 1;
		this.add(displayPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		this.add(btnPanel, gbc);
	}
}
