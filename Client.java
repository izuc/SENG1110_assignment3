/*
 *  Author: Lance Baker
 *  Student No: 3128034
 *  Date: 25-05-2011
 *  Description: 
 *  The class is used to store client related income-based data, and calculate the
 *  tax rate in which applies to that client's income & residential status (using the public 
 *  methods available). It also has a overridden toString method which is used to retrieve 
 *  preformatted String output that can be optionally used for basic displaying purposes.
 *  The class also contains a Account[] array in which is used for storing their investment Account objects. 
 *  It does all the management/ handling of the Account objects, and therefore no other class should know about the Account class.
 */

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Client {
	private static final int[] RESIDENT_TAXABLE_INCOME = {6000, 37000, 80000, 180000};
	private static final double[] RESIDENT_TAXABLE_INCOME_RATE = {0, 0.15, 0.30, 0.37, 0.45};
	private static final int[] RESIDENT_TAXABLE_INCOME_TAX = {0, 0, 4650, 17550, 54550};
	private static final int[] NONRESIDENT_TAXABLE_INCOME = {37000, 80000, 180000};
	private static final double[] NONRESIDENT_TAXABLE_INCOME_RATE = {0.29, 0.30, 0.37, 0.45};
	private static final int[] NONRESIDENT_TAXABLE_INCOME_TAX = {0, 10730, 23630, 60630};
	private static final int MEDICARE_LEVY = 20000;
	private static final double MEDICARE_LEVY_RATE = 0.015;
	
	// Some miscellaneous constants.
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
	private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
	private static final int WEEKS_PER_YEAR = 52;
	
	// The following constants are used for formatting purposes by the toString method.
	private static final String LABEL_NAME = "Name: ";
	private static final String LABEL_RESIDENT = "Resident: ";
	private static final String LABEL_GROSS_SALARY = "Gross salary (per week): ";
	private static final String LABEL_NET_SALARY = "Net salary (per week): ";
	private static final String LABEL_TAX_PAID = "Tax (per week): ";
	private static final String LABEL_MEDICARE = "Medicare (per week): ";
	private static final String LABEL_EXPENSES = "Expenses (per week): ";
	private static final String RESIDENT_YES = "Yes";
	private static final String RESIDENT_NO = "No";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String COLON = ":";
	private static final String TAB = "\t";
	private static final String TOTAL_AMOUNT = "Total investment: ";
	private static final String ERR_ACCOUNT_DOESNT_EXIST = "The account doesn't exist";
	private static final String MSG_NO_ACCOUNTS = "No Accounts";
	private static final String ACCOUNT_NUMBER = "Account";

	public static final int MAX_ACCOUNTS = 3;
	public static final String FIELD_CLIENT_NAME = "name";
	public static final String FIELD_CLIENT_SALARY = "gross salary";
	public static final String FIELD_CLIENT_RESIDENT = "resident";
	public static final String FIELD_CLIENT_EXPENSES = "expenses";
	public static final String BOOLEAN_LETTER_Y = "y";
	public static final String BOOLEAN_LETTER_N = "n";
	public static final String FIELD_ACCOUNT_RATE = "rate";
	public static final String FIELD_ACCOUNT_WEEKS = "weeks";
	public static final String FIELD_ACCOUNT_AMOUNT = "amount";
	public static final String ACCOUNT = "account";
	public static final String NO_ACCOUNTS = "no accounts";

	// Instance attributes.
	private String name;
	private double grossSalary;
	private double netSalary;
	private boolean resident;
	private double tax;
	private double medicare;
	private double weeklyExpenses;
	
	private Account[] accounts;
	private int noAccounts;
	
	/**
	* The default constructor. It chains with initial values to the second constructor.
	*/
	public Client() {
		this(EMPTY_STRING, 0);
	}
	
	/**
	* The second constructor accepts the client name, and grossSalary. It chains to the main constructor
	* passing a default "resident" value of true.
	* @param name String - The full name of the client.
	* @param grossSalary double - The gross salary.
	*/
	public Client(String name, double grossSalary) {
		this(name, grossSalary, true);
	}
	
	/**
	* The main constructor for the Client. It receives the three parameters that are used for calculating the tax.
	* @param name String - The full name of the client.
	* @param grossSalary double - The gross salary.
	* @param resident boolean - A boolean value indicating whether the client is a resident or not.
	*/
	public Client(String name, double grossSalary, boolean resident) {
		this.setName(name);
		this.setGrossSalary(grossSalary);
		this.setResident(resident);
		this.accounts = new Account[MAX_ACCOUNTS];
		this.noAccounts = 0;
	}
	
	/**
	* The calcTaxFormula method is used by the calcTax method to calculate the income tax based on a set of parameters. It is a separate
	* method to avoid the repeated logic experienced throughout the conditions. It receives the taxableIncome (in being the last amount of 
	* the previous tax bracket) which is then subtracted from the gross salary, in order to get the range for the taxable income. It is then multiplied
	* by the taxableRate variable. The taxAmount is just an additional tax amount that is added to the other calculated amount.
	* @param taxableIncome double - The last amount of the previous tax bracket.
	* @param taxableRate double - The tax rate for each dollar over the taxableIncome bracket.
	* @param taxAmount double - An additional tax amount that is added to the calculated value.
	*/
	private double calcTaxFormula(double taxableIncome, double taxableRate, double taxAmount) {
		return (((this.getGrossSalary() - taxableIncome) * taxableRate) + taxAmount);
	}
	
	/**
	* The new and improved calcTax method receives the three arrays (which is used for residents and non residents), it iterates through
	* the taxableIncome array, getting the relating elements from the other arrays (using the index position). It partners with the calcTaxFormula method.
	* @param int[] taxableIncome - The array containing the tax brackets.
	* @param int[] taxableIncomeTax - The array containing any additonal tax added to that bracket.
	* @param double[] taxableIncomeRates - The array containing the tax rates relating to that bracket.
	* @return double - The calculated tax.
	*/
	private double calcTax(int[] taxableIncome, int[] taxableIncomeTax, double[] taxableIncomeRates) {
		for (int i = 0; i < taxableIncome.length; i++) {
			if (this.getGrossSalary() <= taxableIncome[i]) {
				return this.calcTaxFormula(((i > 0) ? taxableIncome[(i - 1)] : 0), taxableIncomeRates[i], taxableIncomeTax[i]);
			}
		}
		// Only executed if the client's income exceeds the last taxableIncome bracket.
		return this.calcTaxFormula(taxableIncome[taxableIncome.length-1], 
						taxableIncomeRates[taxableIncome.length], taxableIncomeTax[taxableIncome.length]);
	}
	
	public void setName(String name) {
		this.name = convertToUpper(name);	
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setGrossSalary(double grossSalary) {
		this.grossSalary = grossSalary;
	}
	
	public double getGrossSalary() {
		return this.grossSalary;
	}
	
	public void setNetSalary(double netSalary) {
		this.netSalary = netSalary;
	}
	
	public double getNetSalary() {
		return this.netSalary;
	}
	
	public double getWeeklyNetSalary() {
		return convertToWeekly(this.getNetSalary());
	}
	
	public void setResident(boolean resident) {
		this.resident = resident;
	}
	
	public boolean getResident() {
		return this.resident;
	}
	
	public void setTax(double tax) {
		this.tax = tax;
	}
	
	public double getTax() {
		return this.tax;
	}
	
	public void setMedicare(double medicare) {
		this.medicare = medicare;
	}
	
	public double getMedicare() {
		return this.medicare;
	}
	
	public void setWeeklyExpenses(double weeklyExpenses) {
		this.weeklyExpenses = weeklyExpenses;
	}
	
	public double getWeeklyExpenses() {
		return this.weeklyExpenses;
	}
	
	/**
	* The getAvailableFunds method is used to retrieve the funds that are left after the weekly expenses have been deducted.
	* It rounds the amount to two decimal places (which enables for the figure to be compared with other values easier).
	* @return double - The remaining funds rounded to two decimal places.
	*/
	public double getAvailableFunds() {
		return Double.parseDouble(DECIMAL_FORMAT.format((this.getWeeklyNetSalary() - this.getWeeklyExpenses())));
	}
	
	/**
	* The getTotalInvestments method is used to determine calculate the total
	* of the amounts invested in all the accounts.
	* @return - The total invested.
	*/
	public double getTotalInvestments() {
		double total = 0;
		for (Account account : this.accounts) {
			if (account != null) {
				total += account.getAmount(); 
			}
		}
		return total;
	}
	
	/**
	* The calcMedicare method will only calculate the medicare tax if the client is a resident, and also if the client earns more than the medicare
	* levy threshold. The calculation is done by multiplying the gross salary with the medicare levy rate. It sets the result to the medicare attribute
	* via its setter.
	*/
	public void calcMedicare() {
		if (this.getResident()) {
			this.setMedicare((this.getGrossSalary() >= MEDICARE_LEVY) ? 
				(this.getGrossSalary() * MEDICARE_LEVY_RATE) : 0);
		}
	}
	
	/**
	* The calcNetSalary method is used to calculate the net salary after the income and medicare tax have been calculated. It sets the result to
	* the netSalary attribute via its setter.
	*/
	public void calcNetSalary() {
		this.setNetSalary((this.getGrossSalary() - this.getTax()) - this.getMedicare());
	}
	
	/**
	* The addAcount method is used to add the account to the Client.
	* @param interestRate - The interest rate
	* @param investmentLength - The investment length
	* @param investmentAmount - The investment amount
	*/
	public void addAccount(double interestRate, int investmentLength, double investmentAmount) {
		Account account = new Account(interestRate, investmentLength, investmentAmount); 
		if (this.noAccounts < this.accounts.length) {
			this.accounts[this.noAccounts++] = account; // Adds the account to the array.
		}
	}
	
	/**
	* The deleteAccount method recieves the accountNo, and deletes the account by creating a new 
	* array which skips that account. Therefore shifting the elements once deleted
	* @return boolean - Whether the account was deleted.
	* @param accountNo int - The accountNo that you want deleted.
	*/
	public boolean deleteAccount(int accountNo) {
		boolean deleted = false;
		Account[] accounts = new Account[MAX_ACCOUNTS];
		int index = 0, accountNumber = 1;
		for (Account account : this.accounts) { // Iterates through all accounts.
			if (account != null) {
				if (accountNumber != accountNo) { // Adds everything but the account corresponding to that number.
					accounts[index++] = account;
				} else {
					deleted = true; // It was deleted.
				}
				accountNumber++;
			}
		}
		if (deleted) {
			this.noAccounts--; // Decrements counter
			this.accounts = accounts; // Assigns new accounts array reference.
		}
		return deleted; // returns boolean indicating success.
	}
	
	/**
	* The getAccount method is used for displaying account information based on the received accountNo parameter.
	* If the account exists, it will append the .toString information of the client (therefore showing their details)
	* and append the relating account via its .toString method. It will also output the calcInvestment table.
	* @param accountNo int - The accountNo that you want displayed.
	* @return String - The String of the account.
	* @throws Exception - If the account doesn't exist it throws an exception.
	*/
	public String getAccount(int accountNo) throws Exception {
		if ((accountNo > 0) && (accountNo <= this.noAccounts)) {
			StringBuilder builder = new StringBuilder();
			Account account = this.accounts[accountNo - 1];
			builder.append(NEW_LINE + this.toString() + NEW_LINE);
			builder.append(NEW_LINE + ACCOUNT_NUMBER + SPACE + accountNo + COLON + NEW_LINE);
			builder.append(account.toString() + NEW_LINE);
			builder.append(account.calcInvestment());
			return builder.toString();
		}
		throw new Exception(ERR_ACCOUNT_DOESNT_EXIST);
	}
	
	/**
	* The getAccounts method is used to return a String containing the account information
	* stored in the accounts array.  It uses the account .toString method for the retrieval of the formatted 
	* String of information to be displayed. It also adds a account number to each displayed account, and invokes
	* the calcTotalAmount method on the account instance that gets the total ptojected amount at the end of the peroid.
	* @return String - The account information.
	*/
	public String getAccounts() {
		StringBuilder builder = new StringBuilder();
		if (this.noAccounts > 0) {
			int accountNo = 1;
			for (Account account : this.accounts) { // Iterates for each account.
				if (account != null) {
					builder.append(NEW_LINE);
					builder.append(ACCOUNT_NUMBER + SPACE + (accountNo++) + COLON + NEW_LINE);
					builder.append(account.toString() + NEW_LINE); // Uses the account .toString() method.
					// Appends the calculated total projected amount (at the end of the investment period).
					builder.append(TAB + TOTAL_AMOUNT + CURRENCY_FORMAT.format(account.calcTotalAmount()));
				}
			}
		} else {
			builder.append(MSG_NO_ACCOUNTS);
		}
		return builder.toString();
	}
	
	public int getNumberOfAccounts() {
		return this.noAccounts;
	}
	
	/**
	* The calcTax method is used to calculate the income tax; it performs the tax calculation based on their residency and gross salary.
	* The result is then assigned to the tax instance attribute via the setter. It uses the partner method calcTaxFormula, passing in the
	* different arguments for each condition, which is done to avoid repeated logic.
	*/
	public void calcTax() {
		double tax = ((this.getResident()) ? this.calcTax(RESIDENT_TAXABLE_INCOME, RESIDENT_TAXABLE_INCOME_TAX, RESIDENT_TAXABLE_INCOME_RATE)
						: this.calcTax(NONRESIDENT_TAXABLE_INCOME, NONRESIDENT_TAXABLE_INCOME_TAX, NONRESIDENT_TAXABLE_INCOME_RATE));
		this.setTax(tax);
		this.calcMedicare();
		this.calcNetSalary();
	}
	
	private static double convertToWeekly(double value) {
		return Double.parseDouble(DECIMAL_FORMAT.format((value /WEEKS_PER_YEAR)));
	}
	
	/**
	* The convertToUpper method is designed to be reusable. 
	* It is used to capitalise each starting letter of each word in a String. Java (for some weird reason) doesn't contain 
	* a default method for doing such a thing, so therefore one had to be created in order to capitalise the received name. 
	* @param str String - The String that you desire to have the starting letter in each word to be capitalised.
	* @return String - The resulting capitalised String.
	*/
	private static String convertToUpper(String str) {
		StringBuilder builder = new StringBuilder();
		StringTokenizer tokens = new StringTokenizer(str.toLowerCase(), SPACE);
		while(tokens.hasMoreTokens()) {
			String text = tokens.nextToken();
			builder.append(text.substring(0, 1).toUpperCase() + text.substring(1) + SPACE);
		}
		return builder.toString().trim();
	}
	
	/**
	* The serialise method is used to output the current client instance data into the format for saving. It will
	* iterate through the stored accounts appending the account details. Therefore, this method will output a complete
	* record of the Object, which can be used for recreating the same instance.
	* @return String - The serialised client instance.
	*/
	public String serialise() {
		StringBuilder output = new StringBuilder();
		// Appends the attribute data to the StringBuilder in the specified saving format.
		output.append(FIELD_CLIENT_NAME + SPACE + this.getName() + NEW_LINE);
		output.append(FIELD_CLIENT_SALARY + SPACE + NUMBER_FORMAT.format(this.getGrossSalary()) + NEW_LINE);
		output.append(FIELD_CLIENT_RESIDENT + SPACE + (this.getResident() ? BOOLEAN_LETTER_Y : BOOLEAN_LETTER_N) + NEW_LINE);
		output.append(FIELD_CLIENT_EXPENSES + SPACE + NUMBER_FORMAT.format(this.getWeeklyExpenses()) + NEW_LINE);
		// If there are accounts
		if (this.getNumberOfAccounts() > 0) {
			int accountNo = 1;
			output.append(NEW_LINE);
			// It will iterate through them
			for (Account account : this.accounts) {
				if (account != null) {
					// Appending the account data
					output.append(ACCOUNT + SPACE + (accountNo++) + NEW_LINE);
					output.append(FIELD_ACCOUNT_RATE + SPACE + account.getRate() + NEW_LINE);
					output.append(FIELD_ACCOUNT_WEEKS + SPACE + account.getNumberOfWeeks() + NEW_LINE);
					output.append(FIELD_ACCOUNT_AMOUNT + SPACE + NUMBER_FORMAT.format(account.getAmount()) + NEW_LINE);
				}
			}
		} else {
			// If there aren't any accounts it appends 'no accounts'
			output.append(NEW_LINE + NO_ACCOUNTS + NEW_LINE);
		}
		output.append(NEW_LINE);
		return output.toString();
	}
	
	/**
	* The toString method is overridden from the super Object, and
	* enables a predefined way for (optionally) retrieving the contents of the Object's attributes for simple presentation purposes.
	* @return String - A text version containing the attribute data.
	*/
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(LABEL_NAME); 
		builder.append(this.getName());
		builder.append(NEW_LINE);  
		builder.append(LABEL_RESIDENT); 
		builder.append(this.getResident() ? RESIDENT_YES : RESIDENT_NO);
		builder.append(NEW_LINE);  
		builder.append(LABEL_GROSS_SALARY); 
		builder.append(CURRENCY_FORMAT.format(convertToWeekly(this.getGrossSalary())));
		builder.append(NEW_LINE);
		builder.append(LABEL_NET_SALARY); 
		builder.append(CURRENCY_FORMAT.format(this.getWeeklyNetSalary()));
		builder.append(NEW_LINE);
		builder.append(LABEL_TAX_PAID); 
		builder.append(CURRENCY_FORMAT.format(convertToWeekly(this.getTax())));
		builder.append(NEW_LINE); 
		builder.append(LABEL_MEDICARE); 
		builder.append(CURRENCY_FORMAT.format(convertToWeekly(this.getMedicare())));
		builder.append(NEW_LINE); 
		builder.append(LABEL_EXPENSES); 
		builder.append(CURRENCY_FORMAT.format(this.getWeeklyExpenses()));		
		return builder.toString();
	}
}