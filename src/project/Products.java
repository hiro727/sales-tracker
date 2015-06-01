package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Products implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static String[]   types        = new String[]{"CAR", "MOTORCYCLE", "ATV", "OUTBOARD MOTOR"};
	private static Products[] start        = new Products[types.length];
	private static int[]      size         = new int[types.length];
	
	private static String[]   detailNames  = new String[]{"DISPLACEMENT", "STROKES", "HORSEPOWER"};
	private static int[][]    detailNum    = new int[][]{{},{0, 1},{1},{1, 2}};
	private static String[]   horsepower   = new String[]{"2.5 - 15 HP", "20 - 40 HP", "50 - 90 HP", "115 - 140 HP", "150 - 200 HP", "225 - 300 HP"};
	private static String[]   displacement = new String[]{"50 cc", "75 cc", "100 cc", "125 cc", "150 cc", "200 cc", "300 cc", "500 cc", "750 cc"};
	private static String[]   strokes      = new String[]{"2 Strokes", "4 Strokes"};
	
	private int pattern;	//type of a product
	private String name;	//name of the product
	private int[] details;	//detail of the product
	private int year;		//initial released year
	private int month;		//initial released month
	
	private Products ptr;	//pointer to the next item in liked list
	
	public Products() {
		try {
			//lord data from main storage in the form of Vector<Products>
			loadProducts();
		} catch (NullPointerException e){
			//check backups if the main storage does not exist
			
			//for test
			
//			new Products(0, "CAR 1", new int[]{}, 0, 0);
//			new Products(0, "CAR 2", new int[]{}, 0, 6);
//			new Products(0, "CAR 3", new int[]{}, 0, 8);
//			new Products(1, "BIKE 1", new int[]{5, 0}, 0, 4);
//			new Products(2, "ATV 1", new int[]{0}, 1, 8);
//			new Products(3, "OUTBOARD MOTOR 1 ", new int[]{1, 5}, 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Products(int pattern, String name, int[] details, int year, int month) throws NullPointerException{
		//instantiate new product
		this.pattern = pattern;
		this.name    = name;
		this.details = details;
		this.year    = year;
		this.month   = month;
		//add the product to the linked list
		add(this);
		Sales.updateProductInfo(name, pattern, year, month);
		Sales.save();
		save();
	}
	
	private static void add(Products New) throws NullPointerException{
		if(start[New.pattern] == null){
			//if there is no data in the linked list
			start[New.pattern] = New;
			size[New.pattern]++;
			return;
		}else if(New.name.compareTo(start[New.pattern].name)<0){
			//if the new data comes before the first item in the linked list
			New.ptr = start[New.pattern];
			start[New.pattern] = New;
			size[New.pattern]++;
			return;
		}else if(New.name.compareTo(start[New.pattern].name)==0){
			//if the name of the new data is the same as the first item's in the linked list
			throw null;
		}
		
		//loop through the linked list to find the position to be inserted
		Products temp1 = start[New.pattern];
		Products temp2 = start[New.pattern].ptr;
		while(temp2 != null){
			if(New.name.compareTo(temp2.name)<0){
				//insert the new data if its name is smaller than the pointing item's
				New.ptr = temp2;
				temp1.ptr = New;
				size[New.pattern]++;
				return;
			}else if(New.name.compareTo(temp2.name)==0){
				//if the name of the new data is the same as the pointing item's
				throw null;
			}else{
				//position still not found. Move forward
				temp1 = temp1.ptr;
				temp2 = temp2.ptr;
			}
		}
		//if position to be inserted was not found, insert it at the end
		temp1.ptr = New;
		size[New.pattern]++;
	}
	
	public static void edit(int n, int type, String name, int[] details, int year, int month){
		//to edit variables of any given existing products
		//loop through the linked list to nth product
		Products temp = start[type];
		for(int i=0;i<n;i++)
			temp = temp.ptr;
		
		//update variables
		String old = temp.name;
		temp.name = name;
		temp.details = details;
		temp.year = year;
		temp.month = month;
		
		//delete from the linked list
		delete(temp);
		//re-add it in alphabetical order
		add(temp);
		//update sales data
		Sales.updateProductInfo(old, name, type, year, month);
		save();
	}
	public static void delete(int n, int editingRow) {
		//delete nth item in the linked list
		Products temp = start[n];
		for(int i=0;i<editingRow;i++)
			temp = temp.ptr;
		delete(temp);
	}
	public static Products search(int n, String name){
		//search for a product's name
		Products temp = start[n];
		while(temp != null){
			int x = name.compareToIgnoreCase(temp.name);
			if(x > 0){
				temp = temp.ptr;
			}else if(x == 0){
				//if the names match
				return temp;
			}else{
				//if the parameter is greater than a name of a product, nothing found
				return null;
			}
		}
		return null;
	}
	public static void delete(Products old){
		//delete product from the linked list
		if(old == start[old.pattern]){
			//if the deleting product is the start
			start[old.pattern] = start[old.pattern].ptr;
			size[old.pattern]--;
			return;
		}
		//otherwise, loop through the linked list
		Products temp1 = start[old.pattern];
		Products temp2 = start[old.pattern].ptr;
		while(temp2 != old){
			temp1 = temp1.ptr;
			temp2 = temp2.ptr;
		}
		temp1.ptr = temp2.ptr;
		size[old.pattern]--;
	}
	
	
	private static void save(){
		//save serialized data of products in a .ser file
		for(int i=0;i<types.length;i++){
			Products temp = start[i];
			
			//get all products into a vector
			Vector<Products> vect = new Vector<Products>();
			while(temp != null){
				vect.add(temp);
				temp = temp.ptr;
			}
			
			//save into a file
			File f = new File("Res");
			if(!f.exists())
				f.mkdir();
			f = new File("Res/"+types[i]+" data.ser");
			try {
				FileOutputStream fos = new FileOutputStream(f);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(vect);
				oos.close();
			} catch (IOException e) {
				//error during saving products data");
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("unchecked")
	private static void loadProducts() throws NullPointerException, IOException, ClassNotFoundException {
		
		//load all products from .ser files
		for(int i=0;i<types.length;i++){
			File file = new File("Res/"+types[i]+" data.ser");
			if(!file.exists())
				throw null;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Vector<Products> v = (Vector<Products>) ois.readObject();
			if(v.size() != 0)
				add(v.get(0));
			size[i] = v.size();
			ois.close();
		}
	}
	
	
	
	public static Object[][] getAllData(int n){
		//get all data of all products
		Products temp = start[n];
		String[][] data = new String[size[n]][detailNum[n].length+3];
		for(int i=0;i<size[n];i++){
			int x = 0;
			data[i][x++] = temp.name;
			for(int j=0;j<detailNum[n].length;j++){
				if(detailNum[n][j] == 0)
					data[i][x++] = displacement[temp.details[x-2]];
				else if(detailNum[n][j] == 1)
					data[i][x++] = strokes[temp.details[x-2]];
				else //if(detailNum[n][j] == 2)
					data[i][x++] = horsepower[temp.details[x-2]];
			}
			data[i][x++] = (temp.year+2008)+"";
			data[i][x++] = Sales.getMonths()[temp.month]+"";
			temp = temp.ptr;
		}
		return data;
	}
	public static String[] getDataOf(int n, int n1) {
		//get data of any given product
		Products temp = start[n];
		for(int i=0;i<n1;i++)
			temp = temp.ptr;
		
		int x = 1;
		
		String[] data = new String[detailNum[n].length+3];
		data[0] = temp.name;
		for(int i=0;i<detailNum[n].length;i++)
			data[x++] = temp.details[i]+"";
		data[x++] = temp.year+"";
		data[x++] = temp.month+"";
		
		return data;
	}
	public static String[] getTypes() {
		return types;
	}
	public static int getSize(int n) {
		return size[n];
	}
	public static String[] getDetailNames() {
		return detailNames;
	}
	public static int[][] getDetailNum() {
		return detailNum;
	}
	public static String[] getHorsepower() {
		return horsepower;
	}
	public static String[] getDisplacement() {
		return displacement;
	}
	public static String[] getStrokes() {
		return strokes;
	}
	public static String[] getAllProducts(int n) {
		//get names of all products in the linked list
		Products temp = start[n];
		
		//count the number of products
		int counter = 0;
		while(temp != null){
			counter++;
			temp = temp.ptr;
		}
		String[] names = new String[counter];
		temp = start[n];
		
		//put the names in an array of String
		for(int i=0;i<counter;i++){
			names[i] = temp.name;
			temp = temp.ptr;
		}
		return names;
	}
	public static String[] getProductsBefore(int n, int year, int month){
		//get names of products available at any given date
		Products temp = start[n];
		
		//count the number of products available
		int counter = 0;
		while(temp != null){
			if(temp.year < year || temp.year == year && temp.month <= month){
				counter++;
			}
			temp = temp.ptr;
		}
		String[] names = new String[counter];
		temp = start[n];
		counter = 0;
		
		//put the names in an array of String
		while(temp != null){
			if(temp.year < year || temp.year == year && temp.month <= month){
				names[counter++] = temp.name;
			}
			temp = temp.ptr;
		}
		return names;
	}

	public static String[] getNamesFromDetails(int n, int x, int[] data) {
		//get names of dealers that satisfy any given conditions
		int counter = 0;
		Products temp = start[n];
		
		//count the number of products that satisfy given condition
		while(temp != null){
			for(int i=0;i<data.length;i++){
				if(temp.details[detailNum[n][x]] == data[i]){
					counter++;
					break;
				}
			}
			temp = temp.ptr;
		}
		
		String[] names = new String[counter];
		counter = 0;
		temp = start[n];
		
		//put the names in an array of String
		while(temp != null){
			for(int i=0;i<data.length;i++){
				if(temp.details[detailNum[n][x]] == data[i]){
					names[counter++] = temp.name;
					break;
				}
			}
			temp = temp.ptr;
		}
		return names;
	}

	public static void reset() {
		//reset products information when the user logs out
		start = new Products[types.length];
		size = new int[types.length];
		
	}
}