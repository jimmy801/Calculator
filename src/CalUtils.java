import java.awt.Dimension;

/**
 * utils function and value
 * @author Jimmy801
 *
 */
public class CalUtils {
	/** window size */
	public static final Dimension window_size = new Dimension(330, 500);
	/** string of copy menuItem, "複製"  */
	public static final String copyStr = "複製";
	/** string of paste menuItem, "貼上" */
	public static final String pasteStr = "貼上";
	/** string of invalid paste input, "Invalid Input" */
	public static final String invalidStr = "Invalid Input";
	/** string of clear button, 'C' */
	public static final String clearStr = "C";
	/** string of current empty(CE) button, 'CE' */
	public static final String currentEmptyStr = "CE";
	/** string of sign button, '±' */
	public static final String signStr = "\u00B1";
	/*public static final String sqrtStr = "\u221a";
	public static final String powStr = "\u1d6a\u02b8";*/
	/** string of back button, '←' */
	public static final String backStr = "\u2190";
	/** string of add button, '+' */
	public static final String addStr = "\u002b";
	/** string of subtraction button, '–' */
	public static final String subStr = "\u2013";
	/** string of multiplication button, '×' */
	public static final String mulStr = "\u00D7";
	/** string of division button, '÷' */
	public static final String divStr = "\u00F7";
	/** string of equal button, '=' */
	public static final String eqStr = "=";
	/** string of dot button, '.' */
	public static final String dotStr = ".";
	/** string of minus symbol, '-' */
	public static final String minusStr = "-";
	/** string of leftParenthesis button, '(' */
	public static final String leftParenthesisStr = "(";
	/** string of rightParenthesisStr button, ')' */
	public static final String rightParenthesisStr = ")";
	/** infinite string */
	public static final String infStr = "Inf.";
	/** not a number string */
	public static final String nanStr = "NaN";
	
	/**
	 * distinguish string is null or empty 
	 * @param str string be distinguished
	 * @return True/False
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}
	
	/**
	 * distinguish string is "0", "0.00" or empty 
	 * @param str string be distinguished
	 * @return True/False
	 */
	public static boolean isZeroOrEmpty(String val) {
		return isNullOrEmpty(val) || trimPointZero(val).equals("0");
	}
	
	/**
	 * distinguish string is digit or not 
	 * @param str string be distinguished
	 * @return True/False
	 */
	public static boolean isDigit(String str) {
		if(isNullOrEmpty(str)) return false;
		return str.chars().allMatch(Character::isDigit);
	}
	
	/**
	 * distinguish is operator or not
	 * @param str string be distinguished
	 * @return True/False
	 */
	public static boolean isOperator(String str) {
		return !isNullOrEmpty(str) && getOperators().contains(str); 
	}
	
	public static String getOperators() {
		return CalUtils.addStr + CalUtils.subStr + CalUtils.mulStr + CalUtils.divStr;
	}
	
	/**
	 * trim end '0' from float number
	 * @param val value be distinguished
	 * @return trimmed value string
	 */
	public static String trimPointZero(String val) {
		if(!val.contains(".")) return val;
		int end = -1;
		for(int i = val.length() - 1; i >= 0; i--) {
			if(val.charAt(i) != '0') break;
			end = i;
		}
		String tmp = end > 0 ? val.substring(0, end) : val;
		if(tmp.endsWith(".")) tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;
	}
}
