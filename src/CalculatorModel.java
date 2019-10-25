import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 
 * model of calculator
 * 
 * @author Jimmy801
 *
 */
public class CalculatorModel {

	/** save all digits & operators */
	private Stack<String> expression;

	/**
	 * objector constructor
	 */
	public CalculatorModel() {
		initialize();
	}

	/**
	 * initialize function
	 */
	private void initialize() {
		expression = new Stack<String>();
	}

	/**
	 * check expression is empty or not
	 * 
	 * @return True/False
	 */
	public boolean isEmpty() {
		return expression.isEmpty();
	}

	/**
	 * clear all digits & operators in expression
	 */
	public void clear() {
		expression.clear();
	}

	/**
	 * put an element to expression
	 * 
	 * @param element - {@link String} digit or operator
	 */
	public void push(String element) {
		expression.add(element);
	}

	/**
	 * return top of expression and remove it form expression
	 * 
	 * @return {@link String} top of expression
	 */
	public String pop() {
		return expression.pop();
	}

	/**
	 * return top of expression
	 * 
	 * @return {@link String} top of expression
	 */
	public String top() {
		return expression.peek();
	}

	/**
	 * get infix of expression
	 * 
	 * @return {@link String} infix of expression
	 */
	public String getInfix() {
		StringBuilder res = new StringBuilder();
		for (String op : expression) {
			res.append(op.contains(CalUtils.minusStr) ? String.format("(%s)", op) : op);
		}
		return res.toString();
	}

	/**
	 * get postfix of specific stack
	 * 
	 * @param expre - {@link Stack}<{@link String}>specific stack of expression
	 * @return {@link String} postfix of specific stack
	 */
	private ArrayList<String> getPostfix(Stack<String> expre) {
		System.out.println(expre);
		Stack<String> tmp = new Stack<String>();
		ArrayList<String> postfix = new ArrayList<String>();
		for (int i = 0; i < expre.size(); ++i) {
			if (i % 2 == 0) {
				postfix.add(expre.get(i));
			} else {
				String operator = expre.get(i);
				while (!tmp.isEmpty() && priority(tmp.peek()) >= priority(operator)) {
					postfix.add(tmp.pop());
				}
				tmp.add(operator);
			}
		}
		while (!tmp.isEmpty()) {
			postfix.add(tmp.pop());
		}
		System.out.println(postfix);
		return postfix;
	}

	/**
	 * calculate the answer of specific stack
	 * 
	 * @param expre - {@link Stack}<{@link String}>specific stack of expression
	 * @return {@link String} the answer of specific stack
	 */
	public String calculate(Stack<String> expre) {
		ArrayList<String> postfix = getPostfix(expre);
		Stack<BigDecimal> values = new Stack<BigDecimal>();
		for (String element : postfix) {
			if (CalUtils.isOperator(element)) {
				BigDecimal y = values.pop();
				BigDecimal x = values.pop();
				values.push(evaluate(x, y, element));
			} else {
				values.push(new BigDecimal(element));
			}
		}
		return CalUtils.trimPointZero(String.valueOf(values.peek()));
	}

	/**
	 * calculate the answer of expression
	 * 
	 * @return {@link String} the answer of expression
	 */
	public String calculate() {
		return calculate(expression);
	}

	/**
	 * parse a formula string, and split the numbers and operators
	 * 
	 * @param exp - formula string
	 * @return {@link ArrayList}<{@link String}> all numbers and operators of
	 *         formula string
	 */
	public ArrayList<String> parseAndSplitString(String exp) {
		ArrayList<String> strs = new ArrayList<>();
		String num = "";
		for (String e : exp.split("")) {
			if ((num.isEmpty() && e.equals(CalUtils.minusStr)) || e.matches("[\\d\\.]")) {
				num += e;
			} else {
				strs.add(num);
				if (e.equals("-"))
					strs.add(CalUtils.subStr);
				else if (e.equals("*"))
					strs.add(CalUtils.mulStr);
				else if (e.equals("/"))
					strs.add(CalUtils.divStr);
				else
					strs.add(e);
				num = "";
			}
		}
		if (!CalUtils.isNullOrEmpty(num))
			strs.add(num);

		return strs;
	}

	/**
	 * add formula to current expression
	 * 
	 * @param input - {@link String} parsed formula string
	 */
	public void parse(String input) {
		String exp = input.replaceAll("[\\s\\u3000\t,]", "");
		ArrayList<String> parseStr = parseAndSplitString(exp);
		expression.addAll(parseStr);
	}

	/**
	 * check the input string is valid formula string or not
	 * 
	 * @param input - {@link String} formula string
	 * @return True/False
	 */
	public boolean tryParse(String input) {
		String exp = input.replaceAll("[\\s\\u3000\t,]", "");
		String regex = String.format("[\\d%s]", "\\" + CalUtils.addStr + CalUtils.subStr + "\\" + CalUtils.minusStr
				+ "\\*" + CalUtils.mulStr + CalUtils.divStr + "\\/" + "\\" + CalUtils.dotStr + CalUtils.eqStr);
		if (!exp.replaceAll(regex, "").isEmpty())
			return false;

		Stack<String> stk = new Stack<String>();
		ArrayList<String> parseStr = parseAndSplitString(exp);
		stk.addAll(expression);
		if (!stk.isEmpty() && stk.peek().replaceAll("[\\d\\.]", "").isEmpty()
				&& parseStr.get(0).replaceAll("[\\d\\.]", "").isEmpty()) {
			stk.clear();
		}
		stk.addAll(parseStr);

		try {
			if (stk.peek().equals(CalUtils.eqStr))
				stk.pop();
			calculate(stk);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * calculate the result of x operator y
	 * 
	 * @param x        - number1
	 * @param y        - number2
	 * @param operator - operator of 2 parameters
	 * @return the result of x operator y
	 */
	private static BigDecimal evaluate(BigDecimal x, BigDecimal y, String operator) {
		if (y.equals(BigDecimal.ZERO) && operator.equals(CalUtils.divStr)) {
			throw new ArithmeticException();
		} else if (operator.equals(CalUtils.addStr)) {
			return x.add(y);
		} else if (operator.equals(CalUtils.subStr)) {
			return x.subtract(y);
		} else if (operator.equals(CalUtils.mulStr)) {
			return x.multiply(y);
		} else if (operator.equals(CalUtils.divStr)) {
			return x.divide(y);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * return priority of operator
	 * 
	 * @param operator - {@link String}the operator
	 * @return 0 if operator is addition or subtraction, other is 1
	 */
	private static int priority(String operator) {
		return operator.equals(CalUtils.addStr) || operator.equals(CalUtils.subStr) ? 0 : 1;
	}
}
