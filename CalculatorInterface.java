/*	
 *  Author: Lance Baker
 *  Student No: 3128034
 *  Date: 25-05-2011
 *  Description: 
 *  The static class is a Command Line Interface for the application. It handles all data entries, 
 *  validation checking (with error handling), and uses the relating Calculator instance for the 
 *  storage, management, and retrieval of Client objects. This is the only class that displays any
 *  output to the user. It is the singular starting and ending point for the application.
 */
import java.util.*;
public class CalculatorInterface {

	// The following constants are used for input prompts, and error messages relating to input given.
	private static final String SPACE = " ";
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String ERROR_PREFIX = "Error: ";
	private static final String INPUT_NAME_MSG = "Please enter their full name: ";
	private static final String INPUT_NAME_REGEX = "([a-zA-Z]+\\s[a-zA-Z]+)";
	private static final String INPUT_NAME_ERR = ERROR_PREFIX + "Please enter their first and last name (separated by a space)";
	private static final String INPUT_BOOLEAN_CRITERIA = "[Yes or No]: ";
	private static final String INPUT_BOOLEAN_YES = "Yes";
	private static final String INPUT_BOOLEAN_NO = "No";
	private static final String INPUT_BOOLEAN_ERR = ERROR_PREFIX + "Must input either Yes or No.";
	private static final String INPUT_INCOME_MSG = "Please enter their annual income: ";
	private static final String INPUT_RESIDENT_MSG = "Are they currently a resident? " + INPUT_BOOLEAN_CRITERIA;
	private static final String INPUT_LIVING_EXPENDITURE_MSG = "The amount (per week) they've used on living expenditures: ";
	private static final String INPUT_LIVING_EXPENDITURE_WARNING = "Warning: The amount given is more than their earnings. You will need to enter \n a new amount less than $%.2f, otherwise the client cannot be added.";
	private static final String INPUT_LIVING_EXPENDITURE_REENTER_MSG = "Would you like to enter a new living expenditure amount? " + INPUT_BOOLEAN_CRITERIA;
	private static final String INPUT_INVESTMENT_VALUE_MSG = "Please enter the investment amount per week: ";
	private static final String INPUT_INVESTMENT_VALUE_ERR1 = ERROR_PREFIX + "The client currently has $%.2f invested in other accounts. \nTherefore you cannot exceed $%.2f.";
	private static final String INPUT_INVESTMENT_VALUE_ERR2 = ERROR_PREFIX + "Your investment exceeds your available funds of $%.2f.";
	private static final String INPUT_INTEREST_RATE_MSG = "Please enter the interest rate percentage (between 1-100): ";
	private static final String INPUT_INTEREST_RATE_ERR = ERROR_PREFIX + "The interest rate must be between 1 and 100.";
	private static final String INPUT_INVESTMENT_LENGTH_MSG = "Please enter the investment length (# of weeks): ";
	private static final String INPUT_INVESTMENT_LENGTH_ERR = ERROR_PREFIX + "The investment length must be between 1 and 48.";
	private static final String ERROR_POSITIVE_AMOUNT_REQUIRED = ERROR_PREFIX + "The amount must be greater than zero.";
	private static final String ERROR_NOT_NUMERIC = ERROR_PREFIX + "Must only enter a numeric value.";
	private static final String ERROR_NOT_INTEGER = ERROR_PREFIX + "Must only enter a integer value.";
	private static final int MAX_INVESTMENT_LENGTH = 48;
	
	// The data for the menus used throughout the application are stored in static string arrays.
	private static final String[] MAIN_MENU = {"Add Client", "Delete Client", "Display Client", "Display All Clients", "Open", "Save", "Exit"};
	private static final String[] CLIENT_MENU = {"Add Account", "Delete Account", "Display Account", "Exit"};
	private static final String[] NAME_MENU = {"Accept & rename existing client", "Enter a new name"};
	
	// The following are titles that are used for each section.
	private static final String TITLE_ADD_ACCOUNT = "Add Account";
	private static final String TITLE_DELETE_ACCOUNT = "Delete Account";
	private static final String TITLE_DISPLAY_ACCOUNT = "Display Account";
	private static final String TITLE_ADD_CLIENT = "Add Client";
	private static final String TITLE_DELETE_CLIENT = "Delete Client";
	private static final String TITLE_DISPLAY_CLIENT = "Display Client";
	private static final String TITLE_DISPLAY_ALL_CLIENTS = "Display All Clients";
	private static final String TITLE_OPEN_FILE = "Open";
	private static final String TITLE_SAVE = "Save";
	private static final String TITLE_CLIENT_MENU = "Client Menu";
	private static final String TITLE_MAIN_MENU = "Main Menu";
	private static final String TITLE_NAME_MENU = "Name Collision Menu";
	
	// The following constants are used for the menus
	private static final String SEPARATOR = "---------------------";
	private static final String OPTION_SEPARATOR = ") ";
	private static final String INPUT_MENU_OPTION = "Option #: ";
	private static final String INPUT_MENU_OPTION_ERR = ERROR_PREFIX + " Please enter a number corresponding to the options shown on the menu.";
	
	
	private static final String ERR_CLIENT_DOESNT_EXIST = ERROR_PREFIX + "The client does not exist.";
	private static final String INPUT_ACCOUNT_NUMBER = "Input Account ID (Between 1 - 3): ";
	private static final String ERR_CLIENT_NAME_ALREADY_EXISTS = ERROR_PREFIX + "There is a name collision with an existing client.";
	private static final String ERR_LIVING_EXPENDITURE_ABORTED = ERROR_PREFIX + "The client has not been added.";
	private static final String INPUT_FILE_MSG = "Please input filename: ";
	private static final String INPUT_FILE_REGEX = "(^.+(\\.(?i)(dat|txt))$)";
	private static final String INPUT_FILE_ERR = "Please ensure the file extension is .dat or .txt ";
	private static final String MSG_CLIENT_DELETED = "The client was deleted.";
	
	private static final String NUMBER_OF_ACCOUNTS = "Number of Accounts: ";
	private static final String MSG_NO_ACCOUNTS = "No Accounts";
	private static final String CLIENT_NUMBER = "Client: ";
	private static final String ERR_NO_CLIENTS = ERROR_PREFIX + " no clients";
	private static final String MSG_LOAD_SUCCESS = "The clients have been loaded";
	private static final String MSG_SAVE_SUCCESS = "The clients have been saved";
	private static final String ADD_CLIENT_MSG = "The client has been added";
	private static final String ADD_ACCOUNT_ERR1 = "No more funds to invest";
	private static final String ADD_ACCOUNT_ERR2 = "It is not possible to add more accounts";
	private static final String ADD_ACCOUNT_MSG = "Account has been added.";
	private static final String ADD_ACCOUNT_FUNDS = "Funds available for investment: $%.2f";
	private static final String DELETE_ACCOUNT_ERR = ERROR_PREFIX + "The account does not exist";
	private static final String DELETE_ACCOUNT_MSG = "The account was deleted";
	
	// The Calculator class is used for the management of the Client objects. It handles
	// the underlying structure in which the clients are stored in.
	private static Calculator calculator = new Calculator();
	
	/**
	* The inputNumber method is used for retrieving a numeric value from the user. It is used by both the inputDouble, and also the 
	* inputInteger methods. It is designed generically to avoid the repeated logic that would have occurred if a separate method
	* was indeed created for retrieving an integer. The method receives both a message String, and also a boolean value indicating whether a
	* Double or an Integer is required. It will disallow anything else other than the desired numeric type to be entered, and reprompt the user to enter a 
	* valid type with a friendly error message.
	* @param message String
	* @param isDouble boolean
	* @return double - The received value. In the circumstance that it is an Integer, than the value will have a zero decimal, 
	*				   which can be cutoff by simply casting it to an int.
	*/
	private static double inputNumber(String message, boolean isDouble) {
		double value = 0;
		do {
			try {
				String input = inputString(message);
				// converts the String to either a Double or an Integer (depending on the isDouble boolean paramater received).
				value = ((isDouble)? Double.valueOf(input).doubleValue() : Integer.valueOf(input).intValue());
				if (value <= 0) {
					System.out.println(ERROR_POSITIVE_AMOUNT_REQUIRED + NEW_LINE);
				}
			} catch (NumberFormatException ex) {
				System.out.println(((isDouble)? ERROR_NOT_NUMERIC : ERROR_NOT_INTEGER) + NEW_LINE);
			}
		} while (value <= 0);
		return value;
	}
	
	/**
	* The inputDouble method is used throughout the cli in order to receive a double value from the user. 
	* It uses the inputNumber method in order to receive the double value. The method exists purely as a wrapper, 
	* and to eliminate the overhead of passing a boolean each time.
	* @param message String - The input message that you wish to be prompted to the user.
	* @return double
	*/
	private static double inputDouble(String message) {
		return inputNumber(message, true);
	}
	
	/**
	* The inputInteger method is used throughout the cli in order to receive a integer value from the user.
	* It uses the inputNumber method in order to receive the integer value. Like the inputDouble method, it behaves as an
	* wrapper. It eliminates the need of passing the boolean, and also casting the retrieved double to an int.
	* @param message String - The input message that you wish to be prompted to the user.
	* @return int
	*/
	private static int inputInteger(String message) {
		return (int)inputNumber(message, false);
	}
	
	/**
	* The inputBoolean method is method for retrieving an boolean value from the user. It prompts the received input message paramater, 
	* and only enables the user to input a Yes or No response. If a value other than Yes or No is received, it will display a friendly 
	* error message stating the required input, and re-prompt for input until a valid value is given. Once a valid value is received, it will
	* return a boolean indicating the response.
	* @param message String - The input message that you wish to be prompted to the user.
	* @return boolean - Either being true (Yes) or false (No).
	*/
	private static boolean inputBoolean(String message) {
		String input;
		boolean value = false;
		do {
			input = inputString(message);
			// Checks whether the received String is either equal to Yes or No (ignoring case).
			if (input.equalsIgnoreCase(INPUT_BOOLEAN_YES) || input.equalsIgnoreCase(INPUT_BOOLEAN_NO)) {
				// If the input received is valid, it will assign the boolean response from the equals test to the value variable.
				value = input.equalsIgnoreCase(INPUT_BOOLEAN_YES);
			} else {
				// Otherwise, it will show an friendly error message.
				System.out.println(INPUT_BOOLEAN_ERR + NEW_LINE);
			}
		// It will iterate until a valid input is given.
		} while (!(input.equalsIgnoreCase(INPUT_BOOLEAN_YES) || input.equalsIgnoreCase(INPUT_BOOLEAN_NO)));
		// Returns the answer as a boolean.
		return value;
	}
	
	/**
	* The inputString method is the singular point in the application which prompts the user for input. The other 
	* methods use this method for the retrieval of a String that is then validated & converted to other primative data
	* types such as int and double. In the event that the user presses Control + C (or an equivalent in another OS) it will
	* recognise that no input has been received and throw an NoSuchElementException; which is then caught to end the application.
	* @param message String - The input message that you wish to be prompted to the user.
	*/
	private static String inputString(String message) {
		String input = EMPTY_STRING;
		try {
			System.out.print(message);
			input = new Scanner(System.in).nextLine().trim();
		// Catches the 'no such element' exception thrown by pressing CTRL + C.
		} catch (NoSuchElementException ex) {
			System.exit(0); // Terminates the program once caught.
		}
		return input;
	}
	
	/**
	* The inputString method is designed to be reusable, but is primarily intended to be used for retreiving the "full name" from the user.
	* It checks the input from the user against the received regular expression, and will display the received error message in the case that
	* the input doesn't match the criteria. It will iterate until a valid input has been given.
	* @param message String - The input message that you wish to be prompted to the user.
	* @param regex String - The regular expression will be used to validate the input.
	* @return String - The input that has been validated against the regular expression.
	*/
	private static String inputString(String message, String regex, String error) {
		String input;
		do {
			input = inputString(message);
			// If the input doesn't match the received regular expression, then it will show the received error message.
			if (!input.matches(regex)) {
				System.out.println(error + NEW_LINE);
			}
		// Iterates until the input matches the regular expression.
		} while(!(input.matches(regex)));
		// Returns the validated input.
		return input;
	}
	
	/**
	* The livingExpenditure method uses the inputDouble for retreiving the weekly expenses from the user. In the circumstance that the value given is
	* greater than the weekly netsalary, then a warning message will be shown to the user. It will then prompt whether the user wants to reenter the value
	* by using the inputBoolean method. In the case that they wish to terminate the program, it will throw an Exception (that will be later caught) to 
	* abort adding a Client. It will iterate until the value given is less than the weekly expenses.
	* @throws Exception - An exception that will be caught to abort the addition of the Client
	*/
	private static double inputLivingExpenditure(double weeklyNetSalary) throws Exception {
		double expenses = 0;
		do {
			expenses = inputDouble(INPUT_LIVING_EXPENDITURE_MSG);
			if (expenses > weeklyNetSalary) {
				System.out.println(String.format(INPUT_LIVING_EXPENDITURE_WARNING, weeklyNetSalary) + NEW_LINE);
				if (!inputBoolean(INPUT_LIVING_EXPENDITURE_REENTER_MSG)) {
					throw new Exception(ERR_LIVING_EXPENDITURE_ABORTED);
				}
			}
		} while(expenses > weeklyNetSalary);
		return expenses;
	}
	
	/**
	* The inputClientName is a recursive method that is used for the retrieval of a unique Client Name.
	* It uses the inputString method for the retrieval of a String matched against a regular expression
	* to ensure it is in the format of the Client's name. It uses the findClient method of the Calculator 
	* instance to check whether there is already a Client with the same name. If there is, it will prompt
	* a menu with options to either change the existing client's name, or to enter a new name; with both 
	* options reinvoking the inputClientName method to ensure it is a valid entry. Once a unique name has
	* been given, it will return the String.
	* @return String - A unique client name.
	*/
	private static String inputClientName() {
		String name = inputString(INPUT_NAME_MSG, INPUT_NAME_REGEX, INPUT_NAME_ERR);
		Client client = calculator.findClient(name); // Checks whether a Client already exists with the same name.
		if (client != null) { 
			// If the client exists it will prompt a name change menu.
			System.out.println(ERR_CLIENT_NAME_ALREADY_EXISTS);
			int selection = 0;			
			do {
				selection = displayMenu(TITLE_NAME_MENU, NAME_MENU);
				switch(selection) {
					case 1:
						client.setName(inputClientName());
						break;
					case 2:
						name = inputClientName();
						break;
				}
			} while(selection > 2); // Iterates until a valid selection has been made.
		}
		return name;
	}
	
	/**
	* The addClient method is used to prompt for input (using other methods) & instantiates a new Client object, passing the 
	* object onto the addClient method in the Calculator class which adds it to the underlying structure. Once added, it will
	* display a friendly message indicating success. The exception thrown by the inputLivingExpenditure method is handled here
	* which was thrown to abort the client add; it outputs the message added to the exception. 
	*/
	private static void addClient() {
		try { 
			Client client = new Client();
			// Prompts for input using the other methods, setting it to the new client instance.
			client.setName(inputClientName());
			client.setGrossSalary(inputDouble(INPUT_INCOME_MSG));
			client.setResident(inputBoolean(INPUT_RESIDENT_MSG));
			client.calcTax(); // Once the gross salary & resident status have been gathered the tax can be calculated.
			// The living expenditure requires the tax to be calculated first to retrieve the weekly net salary.
			client.setWeeklyExpenses(inputLivingExpenditure(client.getWeeklyNetSalary()));
			// Once complete, it will add the client to the Calculator instance.
			calculator.addClient(client);
			// Displays a message indicating it was successful.
			System.out.println(NEW_LINE + ADD_CLIENT_MSG);
		// TryCatch is used catch the exception raised from the inputLivingExpenditure method which will abort the client add.
		} catch (Exception ex) { 
			System.out.println(NEW_LINE + ex.getMessage());
		}
	}
	
	/**
	* The displayAllClients method is used to output the currently stored clients. It first checks whether there are clients before proceeding, and
	* if not it will show an message indicating that there aren't any clients. If there are, it will retrieve the Client Array from the Calculator instance;
	* it will then iterate each Client object in the array displaying the client sequence number, the client details, and whether they have any accounts. 
	*/
	private static void displayAllClients() {
		if (hasClients()) { // Checks to ensure that clients exist.
			int clientNumber = 1;
			StringBuilder builder = new StringBuilder();
			// Iterates for each client in the stored structure. The Calculator instance
			// also ensures that the clients are sorted based on their name.
			for (Client client : calculator.getClients()) {
				if (client != null) {
					builder.append(NEW_LINE + CLIENT_NUMBER + (clientNumber++) + NEW_LINE);
					builder.append(client + NEW_LINE);
					builder.append(((client.getNumberOfAccounts() > 0) ? NUMBER_OF_ACCOUNTS + client.getNumberOfAccounts() :  MSG_NO_ACCOUNTS) + NEW_LINE);
				}
			}
			System.out.println(builder.toString().trim());
		}
	}
		
	/**
	* The interestRate method by the addAccount method. It prompts the user for the interest rate percentage amount by using the
	* inputDouble method. If the input is outside of the percentage range, it will then show a friendly error message. It will iterate until
	* a value is received within the stated range.
	* @return double - The given interest rate.
	*/
	private static double inputInterestRate() {
		double interest = 0;
		do {
			interest = inputDouble(INPUT_INTEREST_RATE_MSG); 
			// If the value given is outside of the range 1 to 100, then it will display an error message
			// stating to only input a value within the said range.
			if (!(interest >= 1 && interest <= 100)) {
				System.out.println(INPUT_INTEREST_RATE_ERR + NEW_LINE);
			}
		// Iterates until the value given is within the correct range.
		} while(!(interest >= 1 && interest <= 100));
		return (interest / 100);
	}
	
	/**
	* The inputInvestmentLength method is used by the addAccount method. It uses the inputInteger method to fetch an integer value from the user,
	* and checks to ensure that it is between a particular range. If not, it will keep on reprompting for input until a valid number has been given.
	* @return int - A valid investment length between 1 and 48.
	*/
	private static int inputInvestmentLength() {
		int investmentLength = 0;
		do {
			investmentLength = inputInteger(INPUT_INVESTMENT_LENGTH_MSG);
			// Prompts an error message if its not between the stated range.
			if (!(investmentLength >= 1 && investmentLength <= MAX_INVESTMENT_LENGTH)) {
				System.out.println(INPUT_INVESTMENT_LENGTH_ERR + NEW_LINE);
			}
		// Iterates until a valid number has been given.
		} while((!(investmentLength >= 1 && investmentLength <= MAX_INVESTMENT_LENGTH)));
		return investmentLength;
	}
	
	/**
	* The investmentAmount method is used by the addAccount method. It prompts the user for the amount of money that they desire to 
	* invest (on a weekly basis) by using the inputDouble method. It receives, which is used to calculate the total investment. If the total investment is greater than the amount of weekly funds available
	* then it will show a error message stating that the desired investment has exceeded the available funds. If other accounts exist, then it 
	* will also mention the funds invested in the other accounts. It will iterate until a valid investment has been given.
	* @param availableFunds double - The amount of funds available after the net salary subtracts the expenditure.
	* @param otherInvestment double - The total amount invested in other acounts.
	* @return double - The amount of money the client desires to invest.
	*/
	private static double inputInvestmentAmount(double availableFunds, double otherInvestment) {
		double investment = 0, totalInvestment = 0;
		do {
			// Fetches the desired investment from the user using the inputDouble method.
			investment = inputDouble(INPUT_INVESTMENT_VALUE_MSG);
			totalInvestment = investment + otherInvestment;
			// If the total investment is greater than the available funds (being the net salary - the living expenses).
			if (totalInvestment > availableFunds) {
				// If the other investment exists, then it will show a note stating the funds invested in that account, and the remaining funds available.
				if (otherInvestment > 0) {
					System.out.println(String.format(INPUT_INVESTMENT_VALUE_ERR1, otherInvestment, availableFunds - otherInvestment));
				} else {
					// It will then show a error message stating that the investment has exceeded the funds available.
					System.out.println(String.format(INPUT_INVESTMENT_VALUE_ERR2, availableFunds));
				}
				System.out.println(SPACE);
			}
		// It will iterate until the total investment is less than the available funds.
		} while (totalInvestment > availableFunds);
		// Returns the desired investment amount.
		return investment;
	}
	
	/**
	* The addAccount method is used to add a new account to a given Client object. It prompts for input using the other 
	* (inputInterestRate, inputInvestmentLength, & inputInvestmentAmount) methods. Before prompting, it checks to ensure
	* that there is enough space for another account, and if not; it will throw an Exception (which is caught later) containing
	* a error message. Also before proceeding, it will check to ensure the available funds are greater than the total invested amount; 
	* otherwise the client won't be able to invest, so therefore another exception is thrown containing a explaining error message. 
	* If the client can add a account, it will pass the received input into the addAccount method belonging to the client instance. 
	* Once added, it will show a message indicating it was successfully added.
	* @param Client client - The desired client instance that you wish to add an Account for.
	* @throws Exception - Any error messages.
	*/
	private static void addAccount(Client client) throws Exception {
		// Checks to ensure that the client has enough room for another account.
		if (client.getNumberOfAccounts() < Client.MAX_ACCOUNTS) {
			// Checks to ensure the client has enough available funds to invest.
			if (client.getAvailableFunds() > client.getTotalInvestments()) {
				// Shows a brief message indicating available funds for investment.
				System.out.println(String.format(ADD_ACCOUNT_FUNDS, (client.getAvailableFunds() - client.getTotalInvestments())));
				// Prompts for input using the methods. It will invoke the methods in the same order as the parameters.
				// Once input has been gathered, it will pass the data onto the addAccount method belonging to the client instance.
				client.addAccount(inputInterestRate(), 
					inputInvestmentLength(), 
					inputInvestmentAmount(client.getAvailableFunds(), client.getTotalInvestments()));
				// So after adding the account, it will display an awesome message indicating it was successful.
				System.out.println(NEW_LINE + ADD_ACCOUNT_MSG);
			} else {
				// Throws an exception containing the error message.
				throw new Exception(ADD_ACCOUNT_ERR1);
			}
		} else {
			// Throws an exception containing the error message.
			throw new Exception(ADD_ACCOUNT_ERR2);
		}
	}
	
	/**
	* The hasClients method is used to determine whether there are any current Clients stored.
	* If there are no clients, it will display an error message. Otherwise, it will just return
	* a boolean indicating if there are.
	* @return boolean - If there are any clients.
	*/
	private static boolean hasClients() {
		if (calculator.getNumberOfClients() == 0) {
			System.out.println(ERR_NO_CLIENTS);
		}
		return (calculator.getNumberOfClients() > 0);
	}
	
	/**
	* The displayTitle method is used to display a nice & consistent title based on the received String. 
	* It is used to ensure consistency amongst all menu sections to keep the user well informed about
	* the operation that they are performing.
	*/
	private static void displayTitle(String title) {
		System.out.println(NEW_LINE + SEPARATOR);
		System.out.println(title);
		System.out.println(SEPARATOR);
	}
	
	/**
	* The displayMenu is a generic method which displays a menu based on the received String[] menu parameter.
	* It is used by the mainMenu, clientMenu, and the inputClientName methods. It uses the displayTitle
	* method to print a title based on the received String title parameter. When displaying the menu, it outputs
	* a number (starting at 1) based on the iteration along with the String element. After, it prompts the user for
	* a menu option using the inputInteger method. If the menu option is outside of the menu size, it will show an 
	* error message. It then returns the selection made.
	* @param title String - The title to be displayed for the menu.
	* @param menu String[] - The menu String array, which contains the options.
	* @return int - The menu selection.
	*/
	private static int displayMenu(String title, String[] menu) {
		displayTitle(title); // Displays the title.
		// Iterates for each menu option
		for (int i = 0; i < menu.length; i++) {
			System.out.println((i + 1) + OPTION_SEPARATOR + menu[i]);
		}
		System.out.println(SPACE);
		// Prompts for menu option
		int selection = inputInteger(INPUT_MENU_OPTION);
		if (selection > menu.length) { // If greater than the length, it will show an error.
			System.out.println(INPUT_MENU_OPTION_ERR);
		}
		return selection;
	}
	
	/**
	* The client menu is used to find a client, and prompt options for interacting with the found client instance. It first checks
	* whether there are clients loaded in the system using the hasClients method. If it does, then it will attempt to find the client
	* based on a given name (which is prompted using the inputString method based on a regular expression). If the client is found, the 
	* findClient method will return a instance; so it checks to ensure its not null, otherwise if it is null it will show an error message
	* indicating that the client couldn't be found. If it has been found it will prompt a menu using the displayMenu method, which will iterate 
	* until the exit option has been selected. It allows the ability to iteract with the client, by; adding accounts; deleting accounts; and 
	* displaying accounts. 
	*/
	private static void clientMenu() {
		if (hasClients()) { // Checks to ensure clients exist. Otherwise, there is no point to search for a client.
			// It uses the findClient method of the Calculator instance. It passes the inputted client name string, and will return
			// a instance or a null reference (if it couldn't be found).
			Client client = calculator.findClient(inputString(INPUT_NAME_MSG, INPUT_NAME_REGEX, INPUT_NAME_ERR));
			if (client != null) { // If the client has been found
				System.out.println(NEW_LINE + client); // It will display the client details using the toString.
				System.out.println(client.getAccounts()); // It will then display their account information.
				// It will iterate a menu until the exitFlag has been flipped.
				for (boolean exitFlag = false; (!exitFlag); ) {
					try {
						// The displayMenu method is used to show the menu options, and receives input from the user for a selection.
						// The selection option is then evaluated in the switch statement.
						switch(displayMenu(TITLE_CLIENT_MENU, CLIENT_MENU)) {
							case 1: // Add Account
								displayTitle(TITLE_ADD_ACCOUNT);
								addAccount(client); // Invokes the addAccount method, passing the client instance.
								break;
							case 2: // Delete Account
								displayTitle(TITLE_DELETE_ACCOUNT);
								// If the client instance has accounts, then it will proceed to prompt for deletion.
								if (client.getNumberOfAccounts() > 0) {
									// Prompts for a number using the inputInteger method. The number is then passed to the
									// deleteAccount method belonging to the client instance. It will return a boolean indicating 
									// the success, which is then evaluated and displayed in the println statement.
									System.out.println(client.deleteAccount(inputInteger(INPUT_ACCOUNT_NUMBER)) ? 
														DELETE_ACCOUNT_MSG : DELETE_ACCOUNT_ERR);
								} else {
									// Otherwise, it will show an error message stating there aren't any accounts.
									System.out.println(ERROR_PREFIX + MSG_NO_ACCOUNTS);
								}
								break;
							case 3: // Display Account
								displayTitle(TITLE_DISPLAY_ACCOUNT);
								// If the client instance has accounts, then it will proceed to prompt for displaying
								if (client.getNumberOfAccounts() > 0) {
									// Prompts for a number using the inputInteger method. The number is then passed to the
									// getAccount method belonging to the client instance. It will output the contents of the account
									// along with the client information again. If the account doesn't exist it will throw an Exception
									// with a error message stating what went wrong.
									System.out.println(client.getAccount(inputInteger(INPUT_ACCOUNT_NUMBER)));
								} else {
									// Otherwise, it will show an error message stating there aren't any accounts.
									System.out.println(ERROR_PREFIX + MSG_NO_ACCOUNTS);
								}
								break;
							case 4: // Exit
								exitFlag = true;
								break;			
						}
					// The TryCatch is used to catch any errors that were purposely thrown by other methods.
					} catch (Exception ex) {
						// Outputs the error message with a 'Error:' prefix.
						System.out.println(ERROR_PREFIX + ex.getMessage());
					}
				}
			} else {
				// If the findClient method returned a null reference, then the client doesn't exist. 
				// Therefore a error message is shown.
				System.out.println(ERR_CLIENT_DOESNT_EXIST);
			}
		}
	}
	
	/**
	* The mainMenu method is the starting point to the application. It prompts a menu using the displayMenu method in a loop
	* that will continue iterating until the exit option has been selected. It enables for the user to Add Clients, Delete Clients,
	* Display Clients, Display All Clients, Open Data Files, and Save Clients.
	*/
	public static void mainMenu() {
		for (boolean exitFlag = false; (!exitFlag); ) {
			try {
				switch(displayMenu(TITLE_MAIN_MENU, MAIN_MENU)) {
					case 1: // Add Client
						displayTitle(TITLE_ADD_CLIENT);
						addClient(); // Invokes the addClient method.
						break;
					case 2: // Delete Client
						displayTitle(TITLE_DELETE_CLIENT);
						if (hasClients()) { // If the system has clients loaded
							// Then it will prompt for the client name, passing the name to the deleteClient method in the Calculator instance,
							// which then returns a boolean that is evaluated to determine success & message to output.
							System.out.println((calculator.deleteClient(inputString(INPUT_NAME_MSG, INPUT_NAME_REGEX, INPUT_NAME_ERR))) ? 
												MSG_CLIENT_DELETED : ERR_CLIENT_DOESNT_EXIST);
						}
						break;
					case 3: // Display Client
						displayTitle(TITLE_DISPLAY_CLIENT);
						clientMenu(); // Invokes the clientMenu which will prompt for a client.
						break;
					case 4: // Display All Clients	
						displayTitle(TITLE_DISPLAY_ALL_CLIENTS);
						displayAllClients(); // Displays all clients.
						break;
					case 5: // Open
						displayTitle(TITLE_OPEN_FILE);
						// Prompts for a filename/ or path, which uses a regular expression to ensure that the filename has a extension. The
						// extension can either be a .dat or a .txt (since the output is a text document). The filename is then passed
						// to the open method belonging to the Calculator instance, which will handle the file input and parsing of the text.
						// If an error occurs, it will throw an Exception which is caught in the surrounding TryCatch.
						if (calculator.open(inputString(INPUT_FILE_MSG, INPUT_FILE_REGEX, INPUT_FILE_ERR))) {
							// Once loaded it will show a message indicating success.
							System.out.println(MSG_LOAD_SUCCESS);
						}
						break;
					case 6: // Save
						displayTitle(TITLE_SAVE);
						// If the client have been loaded, then it will enable the ability to save the clients to a file. It will prompt the user
						// to input a file name (based on a regular expression) which ensures that the file has an extension. The filename is then
						// passed to the save method belonging to the Calculator instance which will handle the output. If an error occurs, it will
						// throw an Exception which is caught in the surrounding TryCatch.
						if (hasClients() && calculator.save(inputString(INPUT_FILE_MSG, INPUT_FILE_REGEX, INPUT_FILE_ERR))) {
							// Once saved, it will display a message indicating success.
							System.out.println(MSG_SAVE_SUCCESS);
						}
						break;
					case 7: // Exit
						exitFlag = true;
						break;
				}
			// The TryCatch is used to catch any errors that were purposely thrown by other methods.
			} catch (Exception ex) {
				// Outputs the error message with a 'Error:' prefix.
				System.out.println(ERROR_PREFIX + ex.getMessage());
			}
		}
	}
	
	/**
	* The main method launches the program.
	*/
	public static void main(String[] args) {
		CalculatorInterface.mainMenu(); // Starts the program by invoking the mainMenu() method.
	}
}