import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;

/**
 * controller of all components<BR><BR>
 * BUG GENERATOR!!!
 * @author Jimmy801
 *
 */
public class CalculatorControl {
	/** main calculated Object */
	private CalculatorModel calModel;
	/** views of calculator */
	private CalculatorView calView;
	/** previous input operator */
	private String operator;
	/** previous input operand */
	private String operand;
	/** clip board for get & set */
	private Clipboard clipboard;
	/** index of insertion to calculator model (maybe useless?) */
	private int pos;
	/** enum for menuItem */
	enum MI { copy, paste }
	
	/**
	 * object constructor
	 * @param calModel main calculated Object
	 * @param calView views of calculator
	 */
	public CalculatorControl(CalculatorModel calModel, CalculatorView calView) {
		this.calModel = calModel;
		this.calView = calView;
		
		// get clip board
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		// register keyboard event
		calView.addKeyListener(keyEvent());
		calView.setFocusable(true);
		// register mouse event
		calView.getDisplayPanel().addMouseListener(mouseEvent());
		// register copy & paste menuItem of pm click event
		calView.getPM().getItem(MI.copy.ordinal()).addActionListener(copyEvent());
		calView.getPM().getItem(MI.paste.ordinal()).addActionListener(pasteEvent());
		
		initValue();
		
		// register buttons event
		for(JButton btn: calView.getBtns()) {
			if(CalUtils.isDigit(btn.getText()))
				btn.addActionListener(numBtnActionListener(btn.getText()));
			else
				btn.addActionListener(otherBtnActionListener(btn.getText()));
		}
	}
	
	/**
	 * initial calculator
	 */
	private void initValue() {
		// calModel also need to initial
		//callModel.clear();
		calView.setExpLblText("0");
		operator = "";
		operand = "0";
		pos = 0;
	}
	
	/**
	 * registered key event
	 * @return {@link KeyAdapter} keyAdapter
	 */
	private KeyAdapter keyEvent() {
		return new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				// ctrl key combination
				if(e.isControlDown()) {
					switch(e.getKeyCode()){
						case KeyEvent.VK_C: 
							doCopy();
							break;
						case KeyEvent.VK_V:
							doPaste();
							break;
					}
					return;
				}
				// shift key combination
				if(e.isShiftDown()) {
					switch(e.getKeyCode()) {
					// shift + '=' is the sign '+'
					case KeyEvent.VK_EQUALS:
						opAction(CalUtils.addStr);
						break;
					// shift + '8' is the sign '*'
					case KeyEvent.VK_8:
						opAction(CalUtils.mulStr); 
						break;
					}
					return;
				}
				if(Character.isDigit(e.getKeyChar())){
					numAction(String.valueOf(e.getKeyChar()));
					return;
				}
				switch(e.getKeyCode()){
					case KeyEvent.VK_BACK_SPACE:
						backAction();
						break;
					case KeyEvent.VK_ENTER:
					case KeyEvent.VK_EQUALS:
						eqAction();
						break;
					case KeyEvent.VK_ESCAPE:
						clearAction();
						break;
					case KeyEvent.VK_ADD:
						opAction(CalUtils.addStr);
						break;
					case KeyEvent.VK_SUBTRACT:
					case KeyEvent.VK_MINUS:
						opAction(CalUtils.subStr);
						break;
					case KeyEvent.VK_MULTIPLY:
						opAction(CalUtils.mulStr);
						break;
					case KeyEvent.VK_DIVIDE:
					case KeyEvent.VK_SLASH:
						opAction(CalUtils.divStr);
						break;
					case KeyEvent.VK_PERIOD:
					case KeyEvent.VK_DECIMAL:
						dotAction();
						break;
					default:
						break;
				}
			}
		};
	}
	
	/**
	 * registered mouse event
	 * @return {@link MouseAdapter} mouseAdapter
	 */
	private MouseAdapter mouseEvent() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// only right click will trigger
				if((e.getModifiers()&InputEvent.BUTTON3_MASK) != 0){
					calView.getPM().show(calView.getDisplayPanel(), e.getX(), e.getY());
				}
			}
		};
	}
	
	/**
	 *  copy action
	 */
	private void doCopy() {
		StringSelection data = new StringSelection(calView.getExpLblText());
		clipboard.setContents(data, data);
	}
	
	/**
	 *  paste action
	 */
	private void doPaste() {
		Transferable pasteData = clipboard.getContents(clipboard);
		if (pasteData == null) return;
		try {
			if (pasteData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String s = (String)(pasteData.getTransferData(DataFlavor.stringFlavor));
				// parse pasted string is valid equation or not
				//calModel.parse(s);
			}
		} catch (UnsupportedFlavorException ex) {
			ex.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
	}
	
	/**
	 * registered copy event
	 * @return {@link ActionListener} copyActionListener
	 */
	private ActionListener copyEvent() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doCopy();
			}
		};
	}
	
	/**
	 * registered paste event
	 * @return {@link ActionListener} pasteActionListener
	 */
	private ActionListener pasteEvent() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doPaste();
			}
		};
	}
	
	/**
	 * number action
	 * @param digit input number 
	 */
	private void numAction(String digit) {
		calView.setFocusable(true);
		// previous input is operator, then clear it and set operand to input
		if(!CalUtils.isNullOrEmpty(operator)) {
			operator = "";
			operand = digit;
			// previous operator is '=', and input new number without clear,
			// most clear by ourselves
			//if(operator.equals(CalUtils.eqStr))
			//	calModel.clear();
			pos++;
		} else {
			 if(operand.equals("0"))
				 operand = digit;
			 else
				 operand += digit;
		}
		//if(calModel.isEmpty()) calView.setPreLblText("");
		calView.setExpLblText(operand);
	}
	
	/**
	 * registered number button event
	 * @param digit number of numBtn
	 * @return {@link ActionListener} numBtnActionListener
	 */
	private ActionListener numBtnActionListener(String digit) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numAction(digit);
			}
		};
	}
	
	/**
	 * registered non-number button event
	 * @param op operation of button
	 * @return {@link ActionListener} non_numBtnActionListener
	 */
	private ActionListener otherBtnActionListener(String op) {
		if(op.equals(CalUtils.signStr)) return signBtnActionListener();
		else if(op.equals(CalUtils.dotStr)) return dotBtnActionListener();
		else if(op.equals(CalUtils.backStr)) return backBtnActionListener();
		else if(op.equals(CalUtils.eqStr)) return eqBtnActionListener();
		else if(op.equals(CalUtils.clearStr)) return clearBtnActionListener();
		else if(op.equals(CalUtils.currentEmptyStr)) return ceBtnActionListener();
		else if(CalUtils.isOperator(op)) return opBtnActionListener(op);
		return null;
	}

	/**
	 * sign action (กำ)
	 */
	private void signAction() {
		calView.setFocusable(true);
		String num = calView.getExpLblText();
		if(CalUtils.isZeroOrEmpty(num)) return;
		if(num.startsWith("-")) num = num.substring(1);
		else num = "-" + num;
		calView.setExpLblText(num);
	}
	
	/**
	 * registered sign button event
	 * @return {@link ActionListener} signBtnActionListener
	 */
	private ActionListener signBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signAction();
			}
		};
	}
	
	/**
	 * dot action (.)
	 */
	private void dotAction() {
		calView.setFocusable(true);
		if((operand.contains("."))) return;
		operator = "";
		if(CalUtils.isNullOrEmpty(operand)) operand = "0"; 
		operand += ".";
		calView.setExpLblText(operand);		
	}
	
	/**
	 * registered dot button event
	 * @return {@link ActionListener} dotBtnActionListener
	 */
	private ActionListener dotBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dotAction();
			}
		};
	}
	
	/**
	 * equal action
	 */
	private void eqAction() {
		calView.setFocusable(true);
		if(CalUtils.isZeroOrEmpty(operand)) return;
		//calModel.push(operand);
		// use calModel to calculate the answer of equation
		//String answer = calModel.cal();
		initValue();
		operator = CalUtils.eqStr;
		//operand = answer;
		// because input would be 1+2=*3, use previous answer and continue calculate,
		// answer must push again after clear calModel
		//calModel.push(answer);
		//calView.setExpLblText(answer);;
		//calView.setPreLblText(calModel.getInfix());		
	}
	
	/**
	 * registered equal button event
	 * @return {@link ActionListener} eqBtnActionListener
	 */
	private ActionListener eqBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eqAction();
			}
		};
	}
	
	/**
	 * operator action<BR>
	 * put numbers into model to calculate only trigger by this function
	 * @param op the trigger operator
	 */
	private void opAction(String op) {
		calView.setFocusable(true);
		// previous operator is not empty, replace it
		if(!CalUtils.isNullOrEmpty(operator)) {
			//calModel.pop();
			//calModel.push(op);
		} else {
			// put number and operator into calModel to calculate
			//calModel.push(operand);
			//calModel.push(operator);
		}
		operand = "0";
		operator = op;
		
		// for test
		calView.setPreLblText(op);
		
		// set the equation so far
		//calView.setPreLblText(calModel.getInfix());
		// evaluate answer, if operator is * or / will calculate later 
		//calView.setExpLblText(calModel.eval());
	}
	
	/**
	 * registered operator button event
	 * @param op the trigger operator
	 * @return {@link ActionListener} opBtnActionListener
	 */
	private ActionListener opBtnActionListener(String op) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opAction(op);
			}
		};
	}

	/**
	 * back action
	 */
	private void backAction() {
		calView.setFocusable(true);
		if(CalUtils.isNullOrEmpty(operand)) return;
		operand = operand.substring(0, operand.length() - 1);
		if(CalUtils.isNullOrEmpty(operand) || operand.equals("-"))
			operand = "0";
		
		calView.setExpLblText(operand);
	}
	
	/**
	 * registered back button event
	 * @return {@link ActionListener} backBtnActionListener
	 */	
	private ActionListener backBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backAction();
			}
		};
	}
	
	/**
	 * clear action
	 */
	private void clearAction() {
		calView.setFocusable(true);
		initValue();
		calView.setExpLblText("0");
		calView.setPreLblText("");
	}
	
	/**
	 * registered clear button event
	 * @return {@link ActionListener} backBtnActionListener
	 */	
	private ActionListener clearBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearAction();
			}
		};
	}
	
	/**
	 * current empty(CE) action
	 */
	private void ceAction() {
		calView.setFocusable(true);
		if(operand.equals("")) return;
		operand = "0";
		calView.setExpLblText(operand);
	}
	
	/**
	 * registered current empty(CE) button event
	 * @return {@link ActionListener} backBtnActionListener
	 */	
	private ActionListener ceBtnActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ceAction();
			}
		};
	}
}
