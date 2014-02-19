/*	
 *  Author: Lance Baker
 *  Student No: 3128034
 *  Date: 25-05-2011
 *  Description: 
 *  The class is used to store investment related data (amount, interest rate, and investment term), and is used 
 *  to calculate the investment growth into monthly subsets using said data; which is formatted and returnable
 *  as a String from the method invocation. The class also contains a overridden toString method which is used
 *  for the retrieval of the attribute data as a preformatted String, to optionally be used elsewhere for 
 *  basic displaying purposes (regardless of interface). 
 */

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Account {
	
	// The following constants are used for formatting purposes by the calcInvestment method.
	private static final String HEADING_INVESTMENT = "Investment:";
	private static final String COLUMN_INVESTMENT_WEEKS = "Weeks";
	private static final String COLUMN_INVESTMENT_BALANCE = "Balance";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String SEPARATOR = "------------------" + NEW_LINE;
	private static final String FORMAT_INTEREST_TITLE = "%s %12s";
	private static final String FORMAT_INTEREST_BODY = "%02d %15s";
	
	// The following constants are used for formatting purposes by the toString method.
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
	private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	private static final String LABEL_WEEKLY_INVESTMENT = "Invested per week: ";
	private static final String LABEL_INTEREST_RATE = "Interest rate: ";
	private static final String LABEL_NUMBER_OF_WEEKS = "Number of weeks: ";
	private static final String PERCENTAGE = "%";
	private static final String TAB = "\t";
	
	// Instance attributes.
	private double rate;
	private int numberOfWeeks;
	private double amount;
	
	/**
	* The default constructor. It chains with initial values to the second constructor.
	*/
	public Account() {
		this(0);
	}
	
	/**
	* The second constructor receives the amount parameter, which is then chained to the main constructor.
	* @param amount double - The invesment amount.
	*/
	public Account(double amount) {
		this(0, 0, amount);
	}
	
	/**
	* The main constructor receives the three values to be assigned to the instance attributes.
	* @param rate double - The interest rate.
	* @param numberOfWeeks int - The investment length.
	* @param amount double - The investment amount.
	*/
	public Account(double rate, int numberOfWeeks, double amount) {
		this.setRate(rate);
		this.setNumberOfWeeks(numberOfWeeks);
		this.setAmount(amount);
	}
	
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	public double getRate() {
		return this.rate;
	}
	
	public void setNumberOfWeeks(int numberOfWeeks) {
		this.numberOfWeeks = numberOfWeeks;
	}
	
	public int getNumberOfWeeks() {
		return this.numberOfWeeks;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return this.amount;
	}
	
	/**
	* The calcInvestment method is used by the calcTotalAmount, and calcInvestment methods.
	* @param double total - The running total.
	* @param int week - The current iterated week in the investment length.
	* @return double - The calculated investment amount.
	*/
	private double calcInvestment(double total, int week) {
		// The following weeks variable will be for majority equal to four, but in the circumstance
		// that an unevenly divisable investment length was given, then on the last iteration it will be the remainder.
		int weeks = (((week % 4) == 0) ? 4 : (week % 4));
		// The total is calculated by adding the previous running total with the investment and multiplying it by the weeks variable.
		// This is then additionally multiplied by the interest rate (which is converted to a monthly rate), 
		// with 1 added to get it in the correct format. In the circumstance that the weeks do not equal to four, meaning it is the last
		// week on the uneven investment length, then it won't have the interest rate applied and instead be set to 1 (being the amount the 
		// total is multiplied, not the interest percentage).
		return (((total + this.getAmount() * weeks)) * ((weeks == 4) ? ((this.getRate() / 13) + 1) : 1));
	}
	
	/**
	* The calcTotalAmount is used to get the total projected amount invested at the end of the investment peroid.
	* @return double the total amount projected at the end of the investment peroid.
	*/
	public double calcTotalAmount() {
		double total = 0;
		// The following will iterate for each week for the investmentLength.
		for (int week = 1; week <= this.getNumberOfWeeks(); week++) {
			// In the event that the investment length is not evenly divisible by four, then it will still perform 
			// the following once the weeks variable is equal to the investment length and the division by four has a remainder.
			if (((week % 4) == 0) || ((week == this.getNumberOfWeeks()) && ((week % 4) != 0))) {
				total = this.calcInvestment(total, week);
			}
		}
		return total;
	}
	
	/**
	* The calcInvestment method is used to calculate the monthly investment projections (based on the interest rate)
	* for the duration of the investment length in weeks. 
	* @return String - It outputs a friendly String containing a text-based table showing the monthly groupings of the expected projection.
	*/
	public String calcInvestment() {
		StringBuilder builder = new StringBuilder();
		builder.append(TAB + HEADING_INVESTMENT + NEW_LINE);
		builder.append(TAB + String.format(FORMAT_INTEREST_TITLE, COLUMN_INVESTMENT_WEEKS, COLUMN_INVESTMENT_BALANCE) + NEW_LINE);
		builder.append(TAB + SEPARATOR);
		double total = 0;
		// The following will iterate for each week for the investmentLength.
		for (int week = 1; week <= this.getNumberOfWeeks(); week++) {
			// In the event that the investment length is not evenly divisible by four, then it will still perform 
			// the following once the weeks variable is equal to the investment length and the division by four has a remainder.
			if (((week % 4) == 0) || ((week == this.getNumberOfWeeks()) && ((week % 4) != 0))) {
				total = this.calcInvestment(total, week);
				builder.append(TAB + String.format(FORMAT_INTEREST_BODY, week, CURRENCY_FORMAT.format(total)) + NEW_LINE);
			}
		}
		return builder.toString();
	}
	
	/**
	* The toString method is overridden from the super Object, and
	* enables a predefined way for (optionally) retrieving the contents of the Object's attributes for simple presentation purposes.
	* @return String - A text version containing the attribute data.
	*/
	public String toString() {
		// Instantiates a StringBuilder which is used to concatenate the output.
		StringBuilder builder = new StringBuilder();
		// The interest rate.
		builder.append(TAB + LABEL_INTEREST_RATE); 
		builder.append(DECIMAL_FORMAT.format(this.getRate() * 100)); // The interest rate.
		builder.append(PERCENTAGE); // The percentage symbol.
		builder.append(NEW_LINE); 
		// The investment length.
		builder.append(TAB + LABEL_NUMBER_OF_WEEKS); 
		builder.append(this.getNumberOfWeeks()); // The investment length.
		builder.append(NEW_LINE);
		// The investment amount.
		builder.append(TAB + LABEL_WEEKLY_INVESTMENT); 
		builder.append(CURRENCY_FORMAT.format(this.getAmount())); // The amount formatted as a currency.

		return builder.toString(); // Returns a String.
	}
}