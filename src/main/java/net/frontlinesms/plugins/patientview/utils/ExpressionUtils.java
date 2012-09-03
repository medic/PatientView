package net.frontlinesms.plugins.patientview.utils;

public class ExpressionUtils {

	private static final String NUMBER_RE = "(-?\\d+(\\.\\d+)?)";
	private static final String OPERATOR_RE = "(\\+|-|\\*|/)";
	private static final String EXPRESSION_RE = NUMBER_RE + "(" + OPERATOR_RE + NUMBER_RE + ")*";
	
	private static boolean containsChars(String str, String chars) {
		for (int i = 0; i < chars.length(); i++) {
			if (str.indexOf(chars.charAt(i)) != -1) {
				return true;
			}
		}
		return false;
	}
	
	private static Pair<Integer,Integer> getDeepestParentheses(String expression){
	    int start = 0;
	    int end = 0;
	    int depth = 0;
	    int maxDepth = 0;
	    for(int i = 0; i < expression.length(); i++){
	      char c = expression.charAt(i);
	      if(c == '('){
	        depth++;
	        if(depth >= maxDepth){
	          start = i;
	          depth = maxDepth;
	        }
	      }if(c == ')'){
	        if(depth == maxDepth){
	          end = i;
	        }
	        depth--;
	      }
	    }
	    if(!(start == 0 && end == 0)){
		    return new Pair<Integer,Integer> (start, end);
	    }
	    return null;
	}

	public static double evaluate(String expression){
		return eval(expression.replaceAll("\\s", ""));
	}
	
	private static double eval(String expression) {
		if(!containsChars(expression, "*-/+()")) return Double.valueOf(expression);
		if(containsChars(expression,"()")){
		      Pair<Integer,Integer> parens = getDeepestParentheses(expression);
		      String left = expression.substring(0,parens.one);
		      String parensExpression = expression.substring(parens.one+1,parens.two);
		      String right = expression.substring(parens.two+1);
		      double parensVal = eval(parensExpression);
		      return eval(left + parensVal + right);
		}else if (containsChars(expression, "+-")) {
			int index = Math.max(expression.lastIndexOf('+'), expression.lastIndexOf('-'));
			String lhs = expression.substring(0, index);
			String rhs = expression.substring(index + 1);
			if (expression.charAt(index) == '+') {
				return eval(lhs) + eval(rhs);
			} else {
				return eval(lhs) - eval(rhs);
			}
		} else if (containsChars(expression, "*/")) {
			int index = Math.max(expression.lastIndexOf('*'), expression.lastIndexOf('/'));
			String lhs = expression.substring(0, index);
			String rhs = expression.substring(index + 1);
			if (expression.charAt(index) == '*') {
				return eval(lhs) * eval(rhs);
			} else {
				return eval(lhs) / eval(rhs);
			}
		}
		return 0;
	}
	
	public static boolean isValidNumericExpression(String expression){
		expression = expression.replaceAll("\\s", "");
		return expression.matches(EXPRESSION_RE);
	}

	public static boolean isValidExpression(String expression){
		expression = expression.replaceAll("\\s", "");
		Pair<Integer, Integer> parens = getDeepestParentheses(expression);
		if(parens != null){
			if (isValidNumericExpression(expression.substring(parens.one + 1, parens.two))) {
				String lhs = expression.substring(0, parens.one);
				String rhs = expression.substring(parens.two + 1);
				return isValidExpression(lhs + 0 + rhs);
			} else {
				return false;
			}
		}else{
			return isValidNumericExpression(expression);
		}
	}
}
