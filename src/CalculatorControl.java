import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

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
	enum MI { COPY, PASTE }
	
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
		
		// disable IME
		calView.enableInputMethods(false);
		// register keyboard event
		registerKeyEvent();
		// register mouse event
		calView.getDisplayPanel().addMouseListener(mouseEvent());
		// register copy & paste menuItem of pm click event
		calView.getPM().getItem(MI.COPY.ordinal()).addActionListener(copyActionListener());
		calView.getPM().getItem(MI.PASTE.ordinal()).addActionListener(pasteActionListener());
		
		initValue();
		
		// register buttons event
		for(JButton btn: calView.getBtns()) {
			if(CalUtils.isDigit(btn.getText()))
				btn.addActionListener(numBtnActionListener(btn.getText()));
			else
				btn.addActionListener(otherBtnActionListener(btn.getText()));
			// disable IME
			btn.enableInputMethods(false);
		}
	}
	
	private void setBtnsEnable(boolean enable) {
		String txt;
		for(JButton btn: calView.getBtns()) {
			txt = btn.getText();
			if(!CalUtils.isDigit(txt) && !txt.equals(CalUtils.clearStr) && !txt.equals(CalUtils.currentEmptyStr)
					 && !txt.equals(CalUtils.backStr) && !txt.equals(CalUtils.eqStr)) {
				btn.setEnabled(enable);
			}
		}
	}
	
	/**
	 * initial calculator
	 */
	private void initValue() {
		// calModel also need to initial
		calModel.clear();
		calView.setExpLblText("0");
		operator = "";
		operand = "0";
		setBtnsEnable(true);
		pos = 0;
	}
	
	/**
	 * add keybind to contentPane
	 * @param comp the component be binded
	 * @param KeyStrokes register KeyStrokes
	 * @param description action description
	 * @param ac binding action
	 * @see {@link JComponent#getInputMap()}
	 * @see {@link JComponent#getActionMap()}
	 */
	private void addKeyBind(JComponent comp, KeyStroke[] KeyStrokes, String description, Action ac) {
		InputMap iMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    ActionMap aMap = comp.getActionMap();
	    aMap.put(description, ac);
	    for(KeyStroke ks: KeyStrokes) 
	    	iMap.put(ks, description);
	}
	
	
	/**
	 * use keyCode return press KeyStroke
	 * @param keyCode
	 * @return {@link KeyStroke} press KeyStroke
	 * @see {@link KeyStroke#getKeyStroke(int, int, boolean)}
	 */
	private KeyStroke getPressKS(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, 0, false);
	}
	
	
	/**
	 * registered key event
	 */
	private void registerKeyEvent() {
		JRootPane rootPane = calView.getRootPane();
		
		// Ctrl + C -> copy event
		addKeyBind(rootPane, 
				new KeyStroke[]{ KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK) }, 
				CalUtils.copyStr, 
				copyAction()
				);
		
		// Ctrl + V -> paste event
		addKeyBind(rootPane, 
				new KeyStroke[]{ KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK) }, 
				CalUtils.pasteStr,
				pasteAction()
				);
		
		// add event
		// Shift + '=' is the sign '+' -> add event
		addKeyBind(rootPane, 
				new KeyStroke[]{ KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_DOWN_MASK), getPressKS(KeyEvent.VK_ADD) }, 
				CalUtils.addStr, 
				opAction(CalUtils.addStr)
				);
		
		// substrate event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_SUBTRACT), getPressKS(KeyEvent.VK_MINUS) }, 
				CalUtils.subStr,
				opAction(CalUtils.subStr)
				);
		
		// multiply event
		// Shift + '8' is the sign '*' -> multiply event
		addKeyBind(rootPane, 
				new KeyStroke[]{ KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.SHIFT_DOWN_MASK), getPressKS(KeyEvent.VK_MULTIPLY) }, 
				CalUtils.mulStr, 
				opAction(CalUtils.mulStr)
				);
		
		// divide event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_DIVIDE), getPressKS(KeyEvent.VK_SLASH) }, 
				CalUtils.divStr, 
				opAction(CalUtils.divStr)
				);
		
		// number event
		for(int i = 0; i < 10; ++i) {
			final String numStr = String.valueOf(i);
			addKeyBind(rootPane, 
					new KeyStroke[]{ KeyStroke.getKeyStroke(numStr), KeyStroke.getKeyStroke("NUMPAD" + i) }, 
					numStr, 
					numBtnAction(numStr)
					);
		}
		
		// dot event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_PERIOD), getPressKS(KeyEvent.VK_DECIMAL) }, 
				CalUtils.dotStr, 
				dotAction()
				);
		
		// back event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_BACK_SPACE) }, 
				CalUtils.backStr, 
				backAction()
				);
		
		// sign event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_F9) }, 
				CalUtils.signStr, 
				signAction()
				);

		// clear event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_ESCAPE) }, 
				CalUtils.clearStr, 
				clearAction()
				);
		
		// CE event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_DELETE) }, 
				CalUtils.currentEmptyStr, 
				ceAction()
				);
		
		// equal event
		addKeyBind(rootPane, 
				new KeyStroke[]{ getPressKS(KeyEvent.VK_ENTER), getPressKS(KeyEvent.VK_EQUALS) }, 
				CalUtils.eqStr, 
				eqAction()
				);
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
	private void copyEvent() {
		StringSelection data = new StringSelection(calView.getExpLblText());
		clipboard.setContents(data, data);
	}
	
	
	/**
	 *  paste action
	 */
	private void pasteEvent() {
		Transferable pasteData = clipboard.getContents(clipboard);
		if (pasteData == null) return;
		try {
			if (pasteData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String pasteStr = (String)(pasteData.getTransferData(DataFlavor.stringFlavor));
				// parse pasted string is valid equation or not
				if(!calModel.tryParse(pasteStr)) {
					setBtnsEnable(false);
					calView.setPreLblText("");
					calView.setExpLblText(CalUtils.invalidStr);
				} else {
					calModel.parse(pasteStr);
					if(calModel.top().equals(CalUtils.eqStr)) {
						calModel.pop();
						operand = calModel.pop();
						eqEvent();
						return;
					}
					operand = calModel.pop();
					calView.setExpLblText(operand);
					calView.setPreLblText(calModel.getInfix());
				}
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
	private ActionListener copyActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				copyEvent();
			}
		};
	}
	
	
	/**
	 * registered copy action
	 * @return {@link Action} copyAction
	 */
	private Action copyAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				copyEvent();
		    }
		 };
	}
	
	/**
	 * registered paste event
	 * @return {@link ActionListener} pasteActionListener
	 */
	private ActionListener pasteActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pasteEvent();
			}
		};
	}
	
	/**
	 * registered paste action
	 * @return {@link Action} pasteAction
	 */
	private Action pasteAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				pasteEvent();
		    }
		 };
	}
	
	/**
	 * number action
	 * @param digit input number 
	 */
	private void numAction(String digit) {
		String nowStr = calView.getExpLblText();
		if(nowStr.equals(CalUtils.invalidStr) || nowStr.equals(CalUtils.infStr)) {
			clearEvent();
		}
		// previous input is operator, then clear it and set operand to input
		if(!CalUtils.isNullOrEmpty(operator)) {
			// previous operator is '=', and input new number without clear,
			// most clear by ourselves
			if(operator.equals(CalUtils.eqStr))
				calModel.clear();
			operator = "";
			operand = digit;
			pos++;
		} else {
			 if(operand.equals("0"))
				 operand = digit;
			 else
				 operand += digit;
		}
		if(calModel.isEmpty()) calView.setPreLblText("");
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
	 * registered number button action
	 * @param digit
	 * @return {@link Action} numBtnAction
	 */
	private Action numBtnAction(String digit) {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				numAction(digit);
		    }
		 };
	}
	
	/**	 
	 * registered operator button action
	 * @param op the trigger operator
	 * @return {@link Action} opAction
	 */
	private Action opAction(String op) {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				opEvent(op);
		    }
		 };
	}
	
	/**
	 * registered non-number button event
	 * @param op operation of button
	 * @return {@link ActionListener} non_numBtnActionListener
	 */
	private ActionListener otherBtnActionListener(String op) {
		if(op.equals(CalUtils.signStr)) return signActionListener();
		else if(op.equals(CalUtils.dotStr)) return dotActionListener();
		else if(op.equals(CalUtils.backStr)) return backActionListener();
		else if(op.equals(CalUtils.eqStr)) return eqActionListener();
		else if(op.equals(CalUtils.clearStr)) return clearActionListener();
		else if(op.equals(CalUtils.currentEmptyStr)) return ceActionListener();
		else if(CalUtils.isOperator(op)) return opActionListener(op);
		return null;
	}

	/**
	 * sign action (กำ)
	 */
	private void signEvent() {
		operand = calView.getExpLblText();
		if(CalUtils.isZeroOrEmpty(operand)) return;
		if(operand.startsWith("-")) operand = operand.substring(1);
		else operand = "-" + operand;
		calView.setExpLblText(operand);
	}
	
	/**
	 * registered sign button event
	 * @return {@link ActionListener} signActionListener
	 */
	private ActionListener signActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signEvent();
			}
		};
	}
	
	/**
	 * registered sign action (กำ)
	 * @return {@link Action} dotAction
	 */
	private Action signAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				signEvent();
		    }
		};
	}
	
	/**
	 * dot action (.)
	 */
	private void dotEvent() {
		if((operand.contains("."))) return;
		operator = "";
		if(CalUtils.isNullOrEmpty(operand)) operand = "0"; 
		operand += ".";
		calView.setExpLblText(operand);		
	}
	
	/**
	 * registered dot button event
	 * @return {@link ActionListener} dotActionListener
	 */
	private ActionListener dotActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dotEvent();
			}
		};
	}
	
	/**
	 * registered dot button action
	 * @return {@link Action} dotAction
	 */
	private Action dotAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				dotEvent();
		    }
		 };
	}

	/**
	 * equal action
	 */
	private void eqEvent() {
		//if(CalUtils.isZeroOrEmpty(operand)) return;
		String nowStr = calView.getExpLblText();
		if(nowStr.equals(CalUtils.invalidStr) || nowStr.equals(CalUtils.infStr)) {
			clearEvent();
			return;
		}
		
		calModel.push(CalUtils.trimPointZero(operand));
		// use calModel to calculate the answer of equation
		String answer = calModel.calculate();
		// because input would be 1+2=*3, use previous answer and continue calculate,
		// answer must push again after clear calModel
		calView.setPreLblText(calModel.getInfix() + CalUtils.eqStr);	
		initValue();
		operand = answer;
		operator = CalUtils.eqStr;
		calView.setExpLblText(answer);
	}
	
	/**
	 * registered equal button event
	 * @return {@link ActionListener} eqActionListener
	 */
	private ActionListener eqActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eqEvent();
			}
		};
	}
	
	/**
	 * registered equal button action
	 * @return {@link Action} eqAction
	 */
	private Action eqAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				eqEvent();
		    }
		 };
	}
	
	/**
	 * operator action<BR>
	 * put numbers into model to calculate only trigger by this function
	 * @param op the trigger operator
	 */
	private void opEvent(String op) {
		// previous operator is not empty, replace it
		if(!CalUtils.isNullOrEmpty(operator) && !operator.equals(CalUtils.eqStr)) {
			calModel.pop();
		} else {
			// put number and operator into calModel to calculate
			calModel.push(CalUtils.trimPointZero(operand));
		}
		operand = "0";
		operator = op;
		calModel.push(op);
		
		// for test
		calView.setPreLblText(op);
		
		// set the equation so far
		calView.setPreLblText(calModel.getInfix());
		// evaluate answer, if operator is * or / will calculate later 
		//calView.setExpLblText(calModel.eval());
	}
	
	/**
	 * registered operator button event
	 * @param op the trigger operator
	 * @return {@link ActionListener} opActionListener
	 */
	private ActionListener opActionListener(String op) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opEvent(op);
			}
		};
	}

	/**
	 * back action
	 */
	private void backEvent() {
		if(CalUtils.isNullOrEmpty(operand)) return;
		String nowStr = calView.getExpLblText();
		if(nowStr.equals(CalUtils.invalidStr) || nowStr.equals(CalUtils.infStr)) {
			clearEvent();
			return;
		}
		
		operand = operand.substring(0, operand.length() - 1);
		if(CalUtils.isNullOrEmpty(operand) || operand.equals("-"))
			operand = "0";
		
		calView.setExpLblText(operand);
	}
	
	/**
	 * registered back button event
	 * @return {@link ActionListener} backActionListener
	 */	
	private ActionListener backActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backEvent();
			}
		};
	}
	
	/**
	 * registered back button action
	 * @return {@link Action} backAction
	 */	
	private Action backAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				backEvent();
		    }
		 };
	}
	
	/**
	 * clear action
	 */
	private void clearEvent() {
		initValue();
		calView.setExpLblText("0");
		calView.setPreLblText("");
	}
	
	/**
	 * registered clear button event
	 * @return {@link ActionListener} clearActionListener
	 */	
	private ActionListener clearActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearEvent();
			}
		};
	}

	/**
	 * registered clear button action
	 * @return {@link Action} backAction
	 */	
	private Action clearAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				clearEvent();
		    }
		 };
	}
	
	/**
	 * current empty(CE) action
	 */
	private void ceEvent() {
		if(operand.equals("")) return;
		String nowStr = calView.getExpLblText();
		if(nowStr.equals(CalUtils.invalidStr) || nowStr.equals(CalUtils.infStr)) {
			clearEvent();
			return;
		}
			
		operand = "0";
		calView.setExpLblText(operand);
	}
	

	/**
	 * registered CE button action
	 * @return {@link Action} backAction
	 */	
	private Action ceAction() {
		return new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
				ceEvent();
		    }
		 };
	}
	
	/**
	 * registered current empty(CE) button event
	 * @return {@link ActionListener} ceEventListener
	 */	
	private ActionListener ceActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ceEvent();
			}
		};
	}
}
