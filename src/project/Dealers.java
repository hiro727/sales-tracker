package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Dealers implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	private static Dealers start;	//first item in the linked list
	// list of states in Mexico
	private static String[] states = new String[]{
		"MEXICO",
		"D.F.",
		"AGUASCALIENTES",
		"BAJA CALIFORNIA",
		"BAJA CALIFORNIA SUR",
		"CAMPECHE",
		"CHIAPAS",
		"CHIHUAHUA",
		"COAHUILA DE ZARAGOZA",
		"COLIMA",
		"DURANGO",
		"GUANAJUATO",
		"GUERRERO",
		"HIDALGO",
		"JALISCO",
		"MICHOACÁN",
		"MORELOS",
		"NAYARIT",
		"NUEVO LEÓN",
		"OAXACA",
		"PUEBLA",
		"QUERÉTARO",
		"QUINTANA ROO",
		"SAN LUIS POTOSÍ",
		"SINALOA",
		"SONORA",
		"TABASCO",
		"TAMAULIPAS",
		"TLAXCALA",
		"VERACRUZ",
		"YUCATÁN",
		"ZACATECAS",
	};
	
	private static int size = 0;	//number of items in the linked list
	private static boolean[] actives;//if  dealers are still valid
	
	private Dealers ptr;	//pointer to the next item in the linked list
	private String name;	//name of dealer or city in which dealer is located
	private int state;		//state in which dealer is located
	private int year;		//initial released year
	private int month;		//initial released year
	private boolean active;	//if the dealer is still valid
	
	public Dealers() {
		try {
			//Lord data from main storage in the form of Vector<Dealers>
			loadDealers();
		} catch (NullPointerException e){
			//check for other backups if file does not exist
			System.out.println("\tD1.2\tCheck if backup storage exists");
			
			//for test
//			new Dealers("DEALER 1", 0, 0, 0);//DF
//			new Dealers("DEALER 2", 0, 3, 5);//POLANCO
//			new Dealers("DEALER 3", 1, 2, 9);//CANCUN
			
		} catch (IOException e) {
			//error occurred during loading process of data
		} catch (ClassNotFoundException e) {
			//error if format of this class does not match the format of the previously saved data 
			e.printStackTrace();
		}
		actives = areActive();
	}
	public Dealers(String name, int state, int year, int month) throws NullPointerException{
		//instantiate new dealer
		this.state  = state;
		this.name   = name;
		this.year   = year;
		this.month  = month;
		this.active = true;
		//save into the linked list
		add(this);
		//update sales data
		Sales.updateDealerInfo(name, year, month);
		Sales.save();
		actives = areActive();
		save();
	}
	
	private static void add(Dealers New) throws NullPointerException{
		if(start == null){	//if no item exists in the linked list
			start = New;
			size++;
		}else if(New.name.compareTo(start.name)<0){
			//if new item comes before the existing first one in alphabetical order
			New.ptr = start;
			start = New;	//insert the new item at the beginning of the linked list
			size++;
		}else if(New.name.equals(start.name)){
			//if new item's name is equal to the name of the existing first one
			throw null;			//throw an error
		}else{
			Dealers temp1 = start;
			Dealers temp2 = start.ptr;
			//check for the position to be inserted until no items are left in the linked list 
			while(temp2 != null){
				if(New.name.compareTo(temp2.name)<0){
					//if the name of new item is larger than the name of currently pointing item (temp2)
					New.ptr = temp2;
					temp1.ptr = New;	//insert the new item right before the currently pointing item (temp2)
					size++;
					return;				//break
				}else if(New.name.compareTo(temp2.name)==0){
					//if the name of new item already exists in the linked list
					throw null;			//throw an error
				}else{
					//if the name of new item is still smaller than the name of currently pointing item (temp2)
					temp1 = temp1.ptr;
					temp2 = temp2.ptr;	//move the pointers to the next
				}
			}
			//if the inserting place was not found
			temp1.ptr = New;		//insert the new item to the node
			size++;
		}
	}
	public static void changeDataOf(int n, String name, int state, int year, int month){
		Dealers temp = start;
		for(int i=0;i<n;i++){
			temp = temp.ptr;
		}
		//update variables to parameters
		temp.name  = name;
		temp.state = state;
		temp.year  = year;
		temp.month = month;
		delete(temp);	//first, delete from linkedList
		add(temp);		//then, add it again to relocate the position
		actives = areActive();
		save();			//save new LinkedList
	}
	

	public static Dealers search(String name){
		Dealers temp = start;
		//loop through the linked list until no items are left in the linked list
		while(temp != null){
			int x = name.compareToIgnoreCase(temp.name);
			//determines the largeness of the searching name compared to the name of the pointing item
			if(x > 0){
				//if the searching name is still smaller than the name of the pointing item
				temp = temp.ptr;	//move the pointer to the next
			}else if(x == 0){
				//if the name matches the name in the linked list
				return temp;		//return the item
			}else{
				//if the searching name becomes larger than the name of the pointing item
				return null;		//there is no name in the linked list
			}
		}
		return null;
	}
	public static void delete(String name) throws NullPointerException{
		if(start == null)
			//if no items exist in the linked list
			throw null;
		if(name.equals(start.name)){
			//if the deleting name is equal to the name of the first item
			start = start.ptr;
			return;
		}
		Dealers temp1 = start;		//keeps track of the previous pointer
		Dealers temp2 = start.ptr;	//current pointer
		//loop until no items are left in the linked list
		while(temp2 != null){
			int n = name.compareToIgnoreCase(temp2.name);
			//n: determines the largeness of deleting name to the pointer's name
			if(n < 0){
				//move pointers
				temp1 = temp1.ptr;
				temp2 = temp2.ptr;
			}else if(n == 0){
				//the name is found
				//delete temp2 by setting the pointer of temp1 to that of temp2
				temp1.ptr = temp2.ptr;
				return;
			}else{ //if(n > 0){
				//no item found
				throw null;
			}
		}
	}
	public static void delete(int editingRow) {
		//delete nth item in the linked list
		Dealers temp = start;
		for(int i=0;i<editingRow;i++)
			temp = temp.ptr;
		delete(temp);
		
	}
	public static void delete(Dealers old) {
		if(old == start){
			//if the first item is being deleted
			start = start.ptr;
			size--;
			actives = areActive();
			return;
		}
		//search through the linked list until the item is found
		Dealers temp1 = start;
		Dealers temp2 = start.ptr;
		while(temp2 != old){
			temp1 = temp1.ptr;
			temp2 = temp2.ptr;
		}
		temp1.ptr = temp2.ptr;
		size--;
		actives = areActive();
	}
	
	public static Object[][] getAllData(){
		//get data of all dealers
		Object[][] data = new Object[size][4];
		Dealers temp = start;
		for(int i=0;i<size;i++){
			data[i][0] = temp.name;
			data[i][1] = states[temp.state];
			data[i][2] = temp.year + 2008;
			data[i][3] = Sales.getMonths()[temp.month];
			temp = temp.ptr;
		}
		return data;
	}

	public static String[] getDataOf(int n){
		//get data of any given dealer
		Dealers temp = start;
		for(int i=0;i<n;i++)
			temp = temp.ptr;
		String[] data = new String[4];
		data[0] = temp.name;
		data[1] = temp.state+"";
		data[2] = temp.year+"";
		data[3] = temp.month+"";
		return data;
	}
	private static void save(){
		//get all dealers into a vector
		Dealers temp = start;
		Vector<Dealers> vect = new Vector<Dealers>();
		while(temp != null){
			vect.add(temp);
			temp = temp.ptr;
		}
		try {
			//save the serialized data of dealers into .ser file
			File file = new File("Res/Dealers.ser");
			FileOutputStream fis = new FileOutputStream(file);
        	ObjectOutputStream oos = new ObjectOutputStream(fis);
			oos.writeObject(vect);
			oos.close();
		} catch (IOException e) {
			//error during saving dealers data");
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private static void loadDealers() throws NullPointerException, IOException, ClassNotFoundException{
		//load serialized data of dealers
    	File file = new File("Res/Dealers.ser");
    	if(!file.exists())
    		throw null;
	    FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);//url.openStream());
		Vector<Dealers> v = (Vector<Dealers>) ois.readObject();
		if(v.size() != 0)
			add(v.get(0));
		size = v.size();
		ois.close();
		actives = areActive();
    }
	
	public static String[] getStates() {
		return states;
	}
	public static void disable(int n, int endYear, int endMonth) {
		//disable any given dealer
		Dealers temp = start;
		for(int i=0;i<n;i++)
			temp = temp.ptr;
		temp.active   = false;
		actives = areActive();
		save();
	}
	public static boolean[] areActive() {
		Dealers temp = start;
		boolean[] actives = new boolean[size];
		for(int i=0;i<size;i++){
			actives[i] = temp.active;
			temp = temp.ptr;
		}
		return actives;
	}
	public static boolean[] getActives() {
		return actives;
	}
	public static String[] getAllDealers() {
		//get names of all dealers
		int counter = 0;
		Dealers temp = start;
		while(temp != null){
			counter++;
			temp = temp.ptr;
		}
		String[] names = new String[counter];
		temp = start;
		for(int i=0;i<names.length;i++){
			names[i] = temp.name;
			temp = temp.ptr;
		}
		return names;
	}
	public static String[] getDealersBefore(int year, int month){
		//get names of dealers that were founded before any given dates
		int counter = 0;
		Dealers temp = start;
		
		//count the number of dealers that were available
		while(temp != null){
			if(temp.year < year || temp.year == year && temp.month <= month){
				counter++;
			}
			temp = temp.ptr;
		}
		String[] names = new String[counter];
		counter = 0;
		temp = start;
		
		//put the names into an array of string
		while(temp != null){
			if(temp.year < year || temp.year == year && temp.month <= month){
				names[counter++] = temp.name;
			}
			temp = temp.ptr;
		}
		return names;
	}
	public static String[] getNamesFromStates(int[] states) {
		//get names of dealers that locate in given state(s)
		int counter = 0;
		Dealers temp;
		
		//count the number of dealers
		for(int i=0;i<states.length;i++){
			temp = start;
			while(temp != null){
				if(states[i] == temp.state){
					counter++;
				}
				temp = temp.ptr;
			}
		}
		String[] names = new String[counter];
		counter = 0;
		
		//put the names into an array of String
		for(int i=0;i<states.length;i++){
			temp = start;
			while(temp != null){
				if(states[i] == temp.state){
					names[counter++] = temp.name;
				}
				temp = temp.ptr;
			}
		}
		
		return names;
	}
	public static void reset() {
		//reset information when the user logs out
		start = null;
		size  = 0;
		actives = null;
		
	}
}