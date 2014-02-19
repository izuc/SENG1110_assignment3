import java.io.*;
import java.text.NumberFormat;

/*	
 *  Author: Lance Baker
 *  Student No: 3128034
 *  Date: 25-05-2011
 *  Description: 
 *  The Calculator class handles the underlying structure for storing the Clients. It enables for 
 *  the Client objects to be added from elsewhere without having to worry about the storing process.
 *  It dynamically resizes the Client array when there is insufficient room. It sorts the array when the
 *  Array getter is invoked; consistently returning an Array sorted based on the Client name. The class
 *  is also responsible for the handling of Files (saving, and opening).
 */
public class Calculator {
	private static final String EMPTY_SPACE = "";
	private static final String SPACE = " ";
	private static final String COMMA = ",";

	private static final String CLIENT_ALREADY_EXISTS = " already exists.";
	private static final String NEW_CLIENTS_ADDED = " new client(s) have been added.";
	private static final String FILE_DOESNT_EXIST = "The file does not exist";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final int MAX_CLIENTS = 3;
	
	private Client[] clients;
	private int noClients;
	
	public Calculator() {
		this.clients = new Client[MAX_CLIENTS];
		this.noClients = 0;
	}

	/**
	* The sort method is invoked by the getClients method, which will return a sorted Client array. It uses 
	* the BubbleSort algorithm, being a simple sorting algorithm repetitively stepping through each client and comparing
	* the client name & swapping the references based on the name compareTo evaluation. Its not the most ideal method for
	* sorting, but due to the small amount of clients that will be used within the system; this simple method should be fine.
	*/
	private void sort() {
		// Iterates through each index in the client array
		for(int i = 0; i < this.clients.length; i++) {
			// Iterates through each index, bringing the client with the smallest evaluated name to position i.
			for(int index = 0; index < (this.clients.length-1)-i; index++) {
				// Ensures that the based on the index, and the index afterwards is not a null reference.
				if ((this.clients[index] != null) && (this.clients[index+1] != null)) {
					// Compares the current client's name with the next client using the compareTo.
					if (this.clients[index].getName().compareTo(this.clients[index+1].getName()) > 0) {
						// Swaps the Clients.
						Client temp = this.clients[index]; // Sets the temp reference with the current client.
						this.clients[index] = this.clients[index+1]; // Assigns the next client to the current client.
						this.clients[index+1] = temp; // Assigns the temp reference to the next client.
					}
				}
			}
		}
	}
	
	/**
	* The resizeArray method will an additional space in the clients array. It transfers the client references on the 
	* previous clients array to the new one once created. 
	*/
	private void resizeArray() {
		// Creates a new array, adding an additional position.
		Client[] clients = new Client[this.clients.length + 1];
		// Transfers the existing clients to the new array.
		for (int i = 0; i < this.clients.length; i++) {
			clients[i] = this.clients[i]; // Copies the reference.
		}
		// Assigns the new array reference to the other reference.
		// The old array will be cleaned by the garbage collector.
		this.clients = clients; 
	}
	
	/**
	* The addClient method receives a Client object and is responsible for adding it to the Client array.
	* If there is no more room, it will resize the array using the resizeArray method and recursively invoke
	* the addClient method again passing the same Client instance. Once the array has enough space, it will 
	* add the client to the array based on the noClients position (which will be incremented after it has been added).
	* @param Client - The Client object that you desire to be added.
	*/
	public void addClient(Client client) {
		// Checks to ensure there is enough space.
		if (this.noClients < this.clients.length) {
			this.clients[this.noClients++] = client;
		} else {
			// If not it resizes the array
			this.resizeArray();
			// and then recursively invokes the addClient method again.
			this.addClient(client);
		}
	}

	/**
	* The findClient method receives the client name, and iterates through the clients stored evaluating the recieved
	* name against the client's. Once found, it will return the Client object. Otherwise, a null reference will be returned.
	* @param name String - The client's name that you are searching for.
	* @return Client - The client that has been found, or an null reference (indicating failure).
	*/
	public Client findClient(String name) {
		for (Client client : this.clients) { // Iterates for each stored client.
			if (client != null) {
				if (client.getName().equalsIgnoreCase(name)) { // Checks name
					return client; // Returns client if found.
				}
			}
		}
		return null; // Otherwise returns a null reference.
	}
	
	/**
	* The deleteClient method recieves a client name String, which is then searched for using the findClient method.
	* If the client exists it will proceed to remove the client from the structure; it will create a new array
	* which will copy the existing client references across, skipping the client that matches the received client name.
	* It will then decrement the noClients instance variable, and copy the new array reference overwriting the existing one.
	* The old array is then collected by the garbage collector. It returns a boolean indicating whether it has been removed based
	* on if the client previously existed.
	* @param name String - The client's name (relating to the client that you want removed).
	* @return boolean - A boolean indicating whether the client was removed.
	*/
	public boolean deleteClient(String name) {
		Client rmClient = this.findClient(name); // First finds the client
		if (rmClient != null) { // If exists proceeds
			int index = 0;
			Client[] clients = new Client[this.clients.length]; // New array
			for (Client client : this.clients) { // Transfers old contents
				if (client != null) {
					if (!client.getName().equalsIgnoreCase(rmClient.getName())) { // Skipping the matched client
						clients[index++] = client; 
					}
				}
			}
			this.noClients--; // Decrements client count.
			this.clients = clients; // Assigns the new array reference.
		}
		return (rmClient != null);
	}
	
	/**
	* The getClients method returns the client Array. It first sorts the array before returning.
	* @return Client[] - The sorted array of clients.
	*/
	public Client[] getClients() {
		this.sort(); // Invokes the sort method.
		return this.clients;
	}
	
	public int getNumberOfClients() {
		return this.noClients;
	}
	
	/**
	* The addClient method is used by the open method in order to parse the details of the client.
	* It uses the BufferedReader to iterate through the Client data lines until the start of Account information.
	* @param BufferedReader br - The BufferedReader containing the FileInputStream. 
	* @param String name - The client name.
	* @return Client - The new Client object.
	*/
	private Client addClient(BufferedReader br, String name) throws Exception {
		Client client = new Client();
		client.setName(name);
		// Iterates until the line starts with the account information.
		for (String line = br.readLine(); (!(line.startsWith(Client.ACCOUNT) || line.startsWith(Client.NO_ACCOUNTS)));) {
			if (line.startsWith(Client.FIELD_CLIENT_SALARY)) {
				client.setGrossSalary(Double.parseDouble(line.replaceFirst(Client.FIELD_CLIENT_SALARY, EMPTY_SPACE).replaceAll(COMMA, EMPTY_SPACE).trim()));					
			} else if (line.startsWith(Client.FIELD_CLIENT_RESIDENT)) {
				client.setResident(line.replaceFirst(Client.FIELD_CLIENT_RESIDENT, EMPTY_SPACE).trim().equals(Client.BOOLEAN_LETTER_Y));									
			} else if (line.startsWith(Client.FIELD_CLIENT_EXPENSES)) {
				client.setWeeklyExpenses(Double.parseDouble(line.replaceFirst(Client.FIELD_CLIENT_EXPENSES, EMPTY_SPACE).replaceAll(COMMA, EMPTY_SPACE).trim()));																
			}
			// Fetches a new line if the account information hasn't been discovered.
			if (!(line.startsWith(Client.ACCOUNT) || line.startsWith(Client.NO_ACCOUNTS))) {
				line = br.readLine();
			}
		}
		client.calcTax(); // Calculates Tax
		this.addClient(client); // Adds the client to the internal structure.
		return client;
	}
	
	/**
	* The addAccounts method is used parsing of the Account data based on the BufferedReader contents, which is iterated
	* line by line until a Client name has been discovered.
	* @param BufferedReader br - The BufferedReader containing the FileInputStream. 
	* @param Client client - The client that will have the accounts added to.
	* @return String - The line containing the client name.
	*/
	private String addAccounts(BufferedReader br, Client client) throws Exception {
		// variables used for storing the account data found.
		int weeks = 0;
		double rate = 0, amount = 0;
		String line = br.readLine(); // The next line.
		// Iterates until the line starts with the client name details.
		while ((line != null) && (!line.startsWith(Client.FIELD_CLIENT_NAME))) {
			// Parses the details
			if (line.startsWith(Client.FIELD_ACCOUNT_RATE)) {
				rate = Double.parseDouble(line.replaceFirst(Client.FIELD_ACCOUNT_RATE, EMPTY_SPACE).trim());
			} else if (line.startsWith(Client.FIELD_ACCOUNT_WEEKS)) {
				weeks = Integer.parseInt(line.replaceFirst(Client.FIELD_ACCOUNT_WEEKS, EMPTY_SPACE).trim());
			} else if (line.startsWith(Client.FIELD_ACCOUNT_AMOUNT)) {
				amount = Double.parseDouble(line.replaceFirst(Client.FIELD_ACCOUNT_AMOUNT, EMPTY_SPACE).replaceAll(COMMA, EMPTY_SPACE).trim());
				// Once the amount has been encountered, the account data is then added to the client.
				client.addAccount(rate, weeks, amount);
			}
			// Fetches a new line until it starts with the client name field.
			if (!line.startsWith(Client.FIELD_CLIENT_NAME)) {
				line = br.readLine();
			}
		}
		return line;
	}
	
	/**
	* The open method receives a filename String which is first checked to determine if it exists, otherwise it will throw an exception with
	* the error message stating it doesn't exist. If it does, then it will open the file in a InputStream passing it to a bufferedReader, which
	* is then looped line by line to determine the contents. It loops until a Client name has been found on a line, only enabling the ability to
	* add a client to the Structure if the client doesn't already exist. It uses the private addClient method and the private addAccounts method
	* for the parsing of the data inbetween client names.
	* @boolean - An indication whether it was successful.
	* @throws - An exception containing an error message.
	*/
	public boolean open(String filename) throws Exception { 
		File file = new File(filename); 
		if (file.exists() && file.isFile()) { // Checks whether exists & is file
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder errors = new StringBuilder();
			int clientCount = 0;
			for (String line = br.readLine(); line != null;) { // Iterates line by line
				if (line.startsWith(Client.FIELD_CLIENT_NAME)) { // If its a new client (base on name)
					// fetches the name from line
					String name = line.replaceFirst(Client.FIELD_CLIENT_NAME, EMPTY_SPACE).trim();
					// Searches for client in system
					if (this.findClient(name) == null) { // If null, means the client doesn't exist
						// Proceed to parse further lines relating to the Client. Once the client has
						// been processed it will continue with the accounts passing the client reference 
						// into the addAccounts.
						line = this.addAccounts(br, this.addClient(br, name));
						clientCount++; // increments client count (which will be later used)
					} else {
						// Appends any clients that already exist to the StringBuilder of errors.
						errors.append(((errors.toString().length() > 0) ? COMMA + SPACE: EMPTY_SPACE) + name);
						line = br.readLine(); // Reads a line to continue iterating
					}
				} else {
					line = br.readLine(); // Reads a line until the line indicates a new client.
				}
			}
			// If there are errors, it will throw an exception outputing the clients that already exist, and
			// a count indicating the amount that were successfully added.
			if (errors.toString().length() > 0) {
				throw new Exception(errors.toString() + CLIENT_ALREADY_EXISTS + 
									NEW_LINE + clientCount + NEW_CLIENTS_ADDED);
			}
			in.close(); // Closes file stream.
		} else {
			// Can't find file
			throw new FileNotFoundException(FILE_DOESNT_EXIST);
		}
		return true; // If it made it here nothing went wrong.
	}
	
	/**
	* The save method is used to output the clients stored in memory to a text file base on the received filename.
	* @param file String - The file that you want the output to be saved as.
	* @return boolean - A boolean indicating success.
	* @throws Exception - Throws a error message if something went wrong.
	*/
	public boolean save(String file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file)); // Opens the file in a PrintWriter
		StringBuilder builder = new StringBuilder(); // Uses a StringBuilder for composing the client output.
		for (Client client : this.clients) { // Iterates for each client.
			if (client != null) {
				builder.append(client.serialise()); // Appends the client output using the created serialise method.
			}
		}
		// Writes the contents of the StringBuilder to the file triming any surrounding white spaces/new lines.
		out.print(builder.toString().trim());
		out.close(); // Closes the PrintWriter
		return true; // If it made it here nothing went wrong.
	}
}