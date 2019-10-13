import java.util.ArrayList;
import java.util.Stack;

public class CalculatorModel {
	Stack<String> expression;
	
	public CalculatorModel(){
		initialize();
	}
	
	private void initialize() {
		expression = new Stack<String>();
	}
	
	public boolean isEmpty() {
		return expression.isEmpty();
	}
	
	public void clear() {
		expression.clear();
	}
	
	public void set(String element, int index) {
		expression.set(index, element);
	}
	
	public void push(String element) {
		expression.add(element);
	}
	
	public String pop() {
		return expression.pop();
	}
	
	public String top() {
		return expression.peek();
	}
	
	public String getInfix() {
		StringBuilder res = new StringBuilder();
		for(String op : expression) {
			res.append(op.contains(CalUtils.minusStr) ? String.format("(%s)", op) : op);
		}
		return res.toString();
	}
	
	private ArrayList<String> getPostfix(Stack<String> expre){
		System.out.println(expre);
		Stack<String> tmp = new Stack<String>();
		ArrayList<String> postfix = new ArrayList<String>();
		for(int i = 0; i < expre.size(); ++i) {
			if(i % 2 == 0) {
				postfix.add(expre.get(i));
			} else {
				String operator = expre.get(i);
				while(!tmp.isEmpty() && priority(tmp.peek()) >= priority(operator)) {
					postfix.add(tmp.pop());
				}
				tmp.add(operator);
			}
		}
		while(!tmp.isEmpty()) {
			postfix.add(tmp.pop());
		}
		System.out.println(postfix);
		return postfix;
	}
	
	
	public String calculate(Stack<String> expre) {
		ArrayList<String> postfix = getPostfix(expre);
		Stack<Float> values = new Stack<Float>();
		for(String element : postfix) {
			if(CalUtils.isOperator(element)) {
				float y = values.pop();
				float x = values.pop();
				values.push(evaluate(x, y, element));
			} else {
				values.push(Float.valueOf(element));
			}
		}
		return CalUtils.trimPointZero(String.valueOf(values.peek()));
	}
	
	public String calculate() {
		return calculate(expression);
	}
	
	public boolean parse(String input) {
		String exp = input.replaceAll("[\\s\\u3000\t,]", "");
		String regex = String.format("[\\d%s]", "\\" +  CalUtils.addStr + CalUtils.subStr + "\\" + CalUtils.minusStr +
				"\\*" +  CalUtils.mulStr + CalUtils.divStr + "\\/" + "\\" + CalUtils.dotStr + CalUtils.eqStr);
		if(!exp.replaceAll(regex, "").isEmpty()) return false;
		
		Stack<String> stk = new Stack();
		stk.addAll(expression);
		
		String num = "";
		
		for(String e: exp.split("")) {
			if((num.isEmpty() && e.equals(CalUtils.minusStr)) || e.matches("[\\d\\.]")) {
				num += e;
			} else {
				stk.add(num);
				stk.add(e.equals(CalUtils.minusStr) ? CalUtils.subStr : e);
				num = "";
			}
		}
		if(!CalUtils.isNullOrEmpty(num)) stk.add(num);
		
		try {
			calculate(stk);
		} catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	private static float evaluate(float x, float y, String operator) {
		if(operator.equals(CalUtils.addStr)) {
			return x + y;
		} else if(operator.equals(CalUtils.subStr)) {
			return x - y;
		} else if(operator.equals(CalUtils.mulStr)) {
			return x * y;
		} else if(operator.equals(CalUtils.divStr)) {
			return x / y;
		}
		return 0;
	}
	
	private static int priority(String operator) {
		return operator.equals(CalUtils.addStr) || operator.equals(CalUtils.subStr) ? 0 : 1;
	}
}
