package wniemiec.util.io.parser.balance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Curly bracket balance, to which its balance is calculated as follows:
 * <code>Current balance - Amount of open curly brackets - Amount of closed curly brackets</code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class CurlyBracketBalance extends Balance {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses line by counting the number of curly brackets and performing the
	 * following calculation: 
	 * <code>Current balance - Amount of open curly brackets - Amount of closed curly brackets</code>
	 * 
	 * @param		text Text to be parsed
	 * 
	 * @return		This object to allows chained calls
	 */
	@Override
	public CurlyBracketBalance parse(String text) {
		String textWithoutString = removeStrings(text);
		
		int amountOpenCurlyBrackets = countOpenCurlyBrackets(textWithoutString);
		int amountClosedCurlyBrackets = countClosedCurlyBrackets(textWithoutString);
		
		currentBalance += amountOpenCurlyBrackets - amountClosedCurlyBrackets;
		alreadyIncreased = alreadyIncreased ? alreadyIncreased : amountOpenCurlyBrackets > 0;
		
		return this;
	}

	/**
	 * Counts how many open curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of open curly brackets in the text
	 */
	private int countOpenCurlyBrackets(String text) {
		final Pattern patternOpenCurlyBrackets = Pattern.compile("\\{");
		Matcher openCBMatcher = patternOpenCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++)
			;
		
		return size;
	}
	
	/**
	 * Counts how many closed curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of closed curly brackets in the text
	 */
	private int countClosedCurlyBrackets(String text) {
		final Pattern patternClosedCurlyBrackets = Pattern.compile("\\}");
		Matcher openCBMatcher = patternClosedCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++)
			;
		
		return size;
	}
	
	@Override
	public String toString() {
		return "CurlyBracketBalance ["
				+ "currentBalance=" + currentBalance
				+ ", alreadyIncreased="	+ alreadyIncreased 
			+ "]";
	}
}
