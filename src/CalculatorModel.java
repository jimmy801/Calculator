import java.util.ArrayList;
import java.util.Stack;

public class CalculatorModel {
	
	
	Stack<String> expression;
	
	public CalculatorModel(){
		expression = new Stack<String>();
	}
	
	public boolean isEmpty() {
		return expression.isEmpty();
	}
	
	
	public void push(String element) {
		expression.add(element);
	}
	
	public String pop() {
		return expression.pop();
	}
	
	public void clear() {
		expression.clear();
	}
	
	
	public String getInfix() {
		
		String res = "";
		String op = "" ;
		
		for(int i = 0 ; i < expression.size(); i++) {
		
			op = expression.get(i) ;
			//如果有負數 給他一個可愛的括號
			if(op.contains(CalUtils.minusStr))
				res = res + "(" + op + ")" ;
			else
				res += op ;	
		
		}
		return res;
	}
	
	
	private static int priority(String operator) {
		
		if( operator.equals(CalUtils.addStr) || operator.equals(CalUtils.subStr) )
			return 0 ; // + -
		
		else
			return 1 ; // * 或 除法
		
		
		
	}
	
	
	private ArrayList<String> getPostfix(){
		
		ArrayList<String> postfix = new ArrayList<String>();
		Stack<String> temp = new Stack<String>();
		
		
		
		
		for(int i = 0; i < expression.size(); i++) {
			
			if(i % 2 == 0) {
				postfix.add(expression.get(i));
			} 
			
			else {
				
				String operator = expression.get(i);
				
				while(!temp.isEmpty() && priority(temp.peek()) >= priority(operator)) 
				{
					postfix.add(temp.pop());
				}
				temp.add(operator);
			}
		}
		
		
		while(!temp.isEmpty()) 
		{
			postfix.add(temp.pop());
		}
		
		System.out.println("前序:");
		System.out.println(expression);
		System.out.println("後序:");
		System.out.println(postfix);
		return postfix;
		
	}
	
	public String calculate() 
	{
		ArrayList<String> postfix = getPostfix();
		Stack<Float> num = new Stack<Float>();
		String element = "" ;
		
		for(int i = 0 ; i < postfix.size(); i++) {
			
			element = postfix.get(i) ;
			
			if(CalUtils.isOperator(element)) {
				
				float y = num.pop();
				
				float x = num.pop();
				
				num.push(cal(x, y, element));
			} 
			
			else {
				
				num.push(Float.valueOf(element));
			}
		}
		
		return CalUtils.trimPointZero(String.valueOf(num.peek()));
	
	}
	
	
	private static float cal(float a, float b, String operator) {
		if(operator.equals(CalUtils.addStr)) 
		{
			
			return a + b;
			
		} 
		else if(operator.equals(CalUtils.subStr)) 
		{
			
			return a - b;
			
		} 
		else if(operator.equals(CalUtils.divStr)) 
		{
			return a / b;
		}
		
		else if(operator.equals(CalUtils.mulStr)) 
		{
			return a * b;
		} 
		
		
		return 0;
	}
	
	
	
}
