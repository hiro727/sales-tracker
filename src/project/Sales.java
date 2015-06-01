package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Sales implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static Sales[][] sales;
	private static Sales total;
	
	private static String[] months = {
		"January","February","March","April","May","June",
		"July","August","September","October","November","December"
	};
	private static String[] years;
	private static int currentYear = (int) (System.currentTimeMillis()/1000/60/60/24/365+1970);
	
	
	private int year;		//base year 2008 --> 0
	private int month;		//range of 0 ~ 12 (12: total)
	private int[][][] data;	//sales of data
	
	public Sales() {
		years = new String[currentYear-2007];
		int year = currentYear;
		for(int i=years.length-1;i>=0;i--)
			years[i] = year--+"";
		sales = new Sales[years.length][13];
		try {
			//load sales data from main storage
			loadSales();
		} catch (NullPointerException e) {
			//main storage does not exist
		} catch (IOException e) {
			System.out.println("\tIOException thrown");
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.out.println("\tClassNotFoundException thrown");
			e.printStackTrace();
			System.exit(0);
		}
	}
	public Sales(int year, int month){
		this.year  = year;
		this.month = month;
		data = new int[Products.getTypes().length][][];
	}
	
	/*
	 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 * ||||CHANGE EXISTING SALES VALUES RELATIVE TO USER INPUTS INTO TABLES||||
	 * ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 */
	/**
	 * when a focus on a cell is removed (editing is stopped), update the data stored in this class
	 * @param year
	 * @param month
	 * @param n
	 * @param value1 the original value
	 * @param value2 the new value
	 * @param row the row in which the cell editing is finished
	 * @param col the column in which the cell editing is finished
	 */
	public static void changeValueOf(int year, int month, int n, int value1, int value2, int row, int col){
		if(sales[year][month] == null){
			//instantiate new sales class if the sales of the date has not been instantiated
			sales[year][month] = new Sales(year, month);
			//initialize array of sales data in the class
			sales[year][month].data[n] = new int[Dealers.getDealersBefore(year, month).length]
					[Products.getProductsBefore(n, year, month).length];
			//set the value
			sales[year][month].data[n][row][col] = value2;
			
		}else if(sales[year][month].data[n] == null){
			//if the sales class of the date had been instantiated but not the array of sales data
			sales[year][month].data[n] = new int[Dealers.getDealersBefore(year, month).length]
					[Products.getProductsBefore(n, year, month).length];
			//set the value
			sales[year][month].data[n][row][col] = value2;
		}else{
			//if the array of the sale data exist, set the value
			sales[year][month].data[n][row][col] = value2;
		}
		//update total sales data of the year
		updateTotalSales(year, month, n, value1, value2, row, col);
	}
	
	public static void updateTotalSales(int year, int month, int n, int value1, int value2, int r, int c){
		
		//search where do the product and dealer of the updated value correspond to in the total sales data
		String[] names_old = Dealers.getDealersBefore(year, month);
		String[] names_new = Dealers.getDealersBefore(year, 12);
		int row = -1;
		int col = -1;
		//for dealer
		for (int i = 0; i < names_new.length; i++) {
			if(names_old[r].equals(names_new[i])){
				row = i;
				break;
			}
		}
		//for product
		names_old = Products.getProductsBefore(n, year, month);
		names_new = Products.getProductsBefore(n, year, 12);
		for (int i = 0; i < names_new.length; i++) {
			if(names_old[c].equals(names_new[i])){
				col = i;
				break;
			}
		}
		//if either is not found, throw an error
		if(row == -1 || col == -1)
			throw null;
		
		if(sales[year][12] == null){
			//instantiate new sales class if the total sales has not been instantiated
			sales[year][12] = new Sales(year, 12);
			//initialize array of sales data in the class
			sales[year][12].data[n] = new int[Dealers.getDealersBefore(year, 12).length]
					[Products.getProductsBefore(n, year, 12).length];
			//set the value
			sales[year][12].data[n][row][col] = value2;
		}else if(sales[year][12].data[n] == null){
			//initialize array of sales data in the class
			sales[year][12].data[n] = new int[Dealers.getDealersBefore(year, 12).length]
					[Products.getProductsBefore(n, year, 12).length];
			//set the value
			sales[year][12].data[n][row][col] = value2;
		}else{
			//if the array of the sale data exist, set the value
			sales[year][12].data[n][row][col] += (value2 - value1);
		}
		save();
	}
	
	/*
	 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 * ||||||CHANGE THE ORDER AND/OR THE AMOUNT OF SALE DATA||||||
	 * ||||||RELATIVE TO USER INPUTS OF DEALERS INFORMATION ||||||
	 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 */
	
	/**
	 * called when a new dealer information (i.e. initial released year and/or month)
	 * is added
	 */
	public static void updateDealerInfo(String name, int year, int month){
		System.out.println("dealer update: "+year+" "+month);
		for(int n=0;n<Products.getTypes().length;n++){
			if(sales == null || sales[year] == null || sales[year][month] == null){
				//do nothing if sales class of the date has not been instantiated
			}else if(sales[year][month].data[n] != null){
				String[] rowN = Dealers.getDealersBefore(year, month);
				int n1 = 0;
				for(int i=0;i<rowN.length;i++){
					if(name.equals(rowN[i])){
						n1 = i;
						break;
					}
				}
				int[][] temp = new int[sales[year][month].data[n].length + 1][];
				
				for(int i=0;i<temp.length-1;i++){
					temp[i] = new int[sales[year][month].data[n][i].length];
				}
				temp[temp.length-1] = new int[temp[temp.length-2].length];
				
				//copy the array
				for(int i=0;i<n1;i++){
					temp[i] = sales[year][month].data[n][i];
				}
				//move items after the new item backwards by 1
				for(int i=n1+1;i<rowN.length;i++){
					temp[i] = sales[year][month].data[n][i-1];
				}
				//update the array
				sales[year][month].data[n] = temp;
			}
		}
		//repeat this for the next month
		month++;
		if(month == 13){
			year++;
			month = 0;
		}
//		if(month == 0)
//			updateDealerInfo(name, year - 1);
		if(year+2008 < currentYear){
			updateDealerInfo(name, year, month);
		}
	}
	public static void updateDealerInfo(String name, int year){
		for(int n=0;n<Products.getTypes().length;n++){
			if(total == null){
				//do nothing if total sales class of has not been instantiated
			}else if(total.data[n] != null){
				String[] rowN = Dealers.getDealersBefore(currentYear, 12);
				int n1 = 0;
				
				//search for the position to be updated
				for(int i=0;i<rowN.length;i++){
					if(name.equals(rowN[i])){
						n1 = i;
						break;
					}
				}
				int[][] temp = new int[rowN.length][];
				
				for(int i=0;i<rowN.length-1;i++){
					temp[i] = new int[total.data[n][i].length];
				}
				temp[rowN.length-1] = new int[temp[rowN.length-2].length];
				
				//copy the original array
				for(int i=0;i<n1;i++){
					temp[i] = total.data[n][i];
				}
				//move others backwards by 1
				for(int i=n1+1;i<rowN.length;i++){
					temp[i] = total.data[n][i-1];
				}
				//update
				total.data[n] = temp;
			}System.out.println("dealer: "+n);
		}
	}
	public static void updateDealerInfo(String name){
		for(int n=0;n<Products.getTypes().length;n++){
			if(total == null){
				//do nothing if total sales class of has not been instantiated
			}else if(total.data[n] != null){
				String[] rowN = Dealers.getDealersBefore(currentYear, 12);
				int n1 = 0;
				
				//search for the position to be updated
				for(int i=0;i<rowN.length;i++){
					if(name.equals(rowN[i])){
						n1 = i;
						break;
					}
				}
				int[][] temp = new int[rowN.length][];
				
				for(int i=0;i<rowN.length-1;i++){
					temp[i] = new int[total.data[n][i].length];
				}
				temp[rowN.length-1] = new int[temp[rowN.length-2].length];
				
				//copy the original array
				for(int i=0;i<n1;i++){
					temp[i] = total.data[n][i];
				}
				//move others backwards by 1
				for(int i=n1+1;i<rowN.length;i++){
					temp[i] = total.data[n][i-1];
				}
				//update
				total.data[n] = temp;
			}System.out.println("dealer: "+n);
		}
	}
	/**
	 * called when a dealer information (i.e. initial released year and/or month)
	 * is changed by the administrator
	 */
	public static void updateDealerInfo(String[] colON, String name, int year, int month){
		for(int n=0;n<Products.getTypes().length;n++){
			if(sales[year][month] == null){
				//do nothing if sales class of the date has not been instantiated
			}else if(sales[year][month].data[n] != null){
				
				//search for the position of the product to be updated
				int[] New = new int[Products.getProductsBefore(n, year, month).length];
				int n1 = 0;
				for(int i=0;i<colON.length;i++){
					if(name.equals(colON[i])){
						n1 = i;
						break;
					}
				}
				//get original array
				for(int i=0;i<New.length;i++){
					New[i] = sales[year][month].data[n][i][n1];
				}
				
				//search for the position of the dealer to be updated
				String[] colNN = Dealers.getDealersBefore(year, month);
				int n2 = 0;
				for(int i=0;i<colNN.length;i++){
					if(name.equals(colNN[i])){
						n2 = i;
						break;
					}
				}
				if(n1 > n2){
					//if product comes before the dealer
					for(int i=n1;i>n2;i--){
						for(int j=0;j<colNN.length;j++){
							sales[year][month].data[n][j][i] = sales[year][month].data[n][j][i-1];
						}
					}
					sales[year][month].data[n][n2] = New;
					
				}else if(n2 > n1){
					//if dealer comes before the product
					for(int i=n1;i<n2;i++){
						for(int j=0;j<colNN.length;j++){
							sales[year][month].data[n][j][i] = sales[year][month].data[n][j][i+1];
						}
					}
					for(int i=0;i<sales[year][month].data[n].length;i++){
						sales[year][month].data[n][i][n2] = New[i];
					}
				}
			}
			//repeat this for the next month
			month++;
			if(month == 13){
				year++;
				month = 0;
			}
//			if(month == 0)
//				updateDealerInfo(name, year - 1);
			if(year + 2008 <= currentYear){
				updateDealerInfo(colON, name, year, month);
			}
		}
	}
	

	/*
	 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 * ||||||CHANGE THE ORDER AND/OR THE AMOUNT OF SALE DATA||||||
	 * ||||||RELATIVE TO USER INPUTS OF PRODUCTS INFORMATION||||||
	 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
	 */
	
	/**
	 * when a new product is added, update the sales data for each date
	 */
	public static void updateProductInfo(String name, int n, int year, int month){
		if(sales[year][month] == null){
			//do nothing if sales class of the date has not been instantiated
		}else if(sales[year][month].data[n] != null){
			
			//search for the position to be updated
			String[] colN = Products.getProductsBefore(n, year, month);
			int n1 = 0;
			for(int i=0;i<colN.length;i++){
				if(name.equals(colN[i])){
					n1 = i;
					break;
				}
			}
			int[][] temp = new int[sales[year][month].data[n].length][];
			for(int i=0;i<temp.length;i++){
				temp[i] = new int[sales[year][month].data[n][i].length+1];
			}
			
			for(int i=0;i<temp.length;i++){
				//copy the original array
				for(int j=0;j<n1;j++){
					temp[i][j] = sales[year][month].data[n][i][j];
				}
				//move others backward by 1
				for(int j=n1+1;j<temp[i].length;j++){
					temp[i][j] = sales[year][month].data[n][i][j-1];
				}
			}
			sales[year][month].data[n] = temp;
		}
		//repeat for the next month
		month++;
		if(month == 13){
			year++;
			month = 0;
		}
//		if(month == 0)
//			updateProductInfo(name, n);
		if(year + 2008 <= currentYear){
			updateProductInfo(name, n, year, month);
		} 
	}
	public static void updateProductInfo(String name, int n){
		if(total == null || total.data[n] == null){
			//do nothing if total sales class of has not been instantiated
		}else if(total != null){
			
			//search for the position to be updated
			String[] colN = Products.getProductsBefore(n, currentYear, 12);
			int n1 = 0;
			for(int i=0;i<colN.length;i++){
				if(name.equals(colN[i])){
					n1 = i;
					break;
				}
			}
			int[][] temp = new int[total.data[n].length][];
			for(int i=0;i<temp.length;i++){
				temp[i] = new int[total.data[n][i].length+1];
			}
			for(int i=0;i<temp.length;i++){
				//copy the original array
				for(int j=0;j<n1;j++){
					temp[i][j] = total.data[n][i][j];
				}
				//move others forward by 1
				for(int j=n1+1;j<temp[i].length;j++){
					temp[i][j] = total.data[n][i][j-1];
				}
			}
			total.data[n] = temp;
		}
	}
	/**
	 * when a new dealer is added, update the sales data for each date
	 */
	public static void updateProductInfo(String old, String name, int n, int year, int month){
		if(sales[year][month] == null){
			//do nothing if sales class of the date has not been instantiated
		}else if(sales[year][month].data[n] != null){
			
			//search for the poistion to be updated
			String[] colN = Products.getProductsBefore(n, year, month);
			int n1 = 0;
			for(int i=0;i<colN.length;i++){
				if(name.equals(colN[i])){
					n1 = i;
					break;
				}
			}
			int[][] new_data = new int[sales[year][month].data[n].length][];
			for(int i=0;i<new_data.length;i++){
				new_data[i] = new int[sales[year][month].data[n][i].length];
			}
			for(int i=0;i<new_data.length;i++){
				
				//copy the original array
				for(int j=0;j<n1;j++){
					new_data[i][j] = sales[year][month].data[n][i][j];
				}
				//move others backward by 1
				for(int j=n1+1;j<new_data[i].length;j++){
					new_data[i][j] = sales[year][month].data[n][i][j-1];
				}
			}
			sales[year][month].data[n] = new_data;
		}
		//repeat this for the next month
		month++;
		if(month == 13){
			year++;
			month = 0;
		}
//		if(month == 0)
//			updateProductInfo(old, name, n);
		if(year + 2008 <= currentYear){
			updateProductInfo(old, name, n, year, month);
		} 
	}
	public static void updateProductInfo(String old, String name, int n){
		if(total == null || total.data[n] == null){
			//do nothing if total sales class of has not been instantiated
		}else if(total != null){
			
			//search for the position to be updated
			String[] colN = Products.getProductsBefore(n, currentYear, 12);
			int n1 = 0;
			for(int i=0;i<colN.length;i++){
				if(name.equals(colN[i])){
					n1 = i;
					break;
				}
			}
			int[][] temp = new int[total.data[n].length][];
			for(int i=0;i<temp.length;i++){
				temp[i] = new int[total.data[n][i].length+1];
			}
			for(int i=0;i<temp.length;i++){
				//copy the original array
				for(int j=0;j<n1;j++){
					temp[i][j] = total.data[n][i][j];
				}
				//move others backward by 1
				for(int j=n1+1;j<temp[i].length;j++){
					temp[i][j] = total.data[n][i][j-1];
				}
			}
			total.data[n] = temp;
		}
	}
	/* ||||||||||||||||||||||||||||||||||||||||||||||
	 * |||||||||||||||||GET METHODS||||||||||||||||||
	 * ||||||||||||||||||||||||||||||||||||||||||||||
	 */
	public static String[] getYears() {
		return years;
	}
	public static String[] getMonths() {
		return months;
	}
	public static String[] getPeriods(){
		//get all the year and month since 2008 January
		String[] periods = new String[years.length*12];
		for(int i=0;i<years.length;i++){
			for(int j=0;j<12;j++){
				periods[i*12+j] = years[i]+" "+months[j].subSequence(0, 3);
			}
		}
		return periods;
	}
	public static Sales getSalesOf(int year, int month) {
		//get sales data of any given date
		if(sales[year][month] == null){
			sales[year][month] = new Sales(year, month);
		}
		return sales[year][month];
	}
	public static Sales getTotalSales() {
		return total;
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int[][] getData(int n) {
		return data[n];
	}
	public static Sales[] getAllSales(){
		//get all sales data being instantiated in an array
		int counter = 0;
		for(int i=0;i<years.length;i++){
			for(int j=0;j<13;j++){
				if(sales[i][j] != null){
					counter++;
				}
			}
		}
		Sales[] all = new Sales[counter+1];
		counter = 0;
		for(int i=0;i<years.length;i++){
			for(int j=0;j<13;j++){
				if(sales[i][j] != null)
					all[counter++] = sales[i][j];
			}
		}
		all[counter] = total;
		return all;
	}
	/* ||||||||||||||||||||||||||||||||||||||||||||||||
	 * ||||||||||SAVE AND LOAD FROM .SER FILE||||||||||
	 * ||||||||||||||||||||||||||||||||||||||||||||||||
	 */
	
	public static void save(){
		//save all the sales data stored in an array into a .ser file
		File f = new File("Res/sale.ser");
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(getAllSales());
			oos.close();
			System.out.println("S2.2\tSave Serialized Data into .ser file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadSales() throws NullPointerException, IOException, ClassNotFoundException{
		//load all instantiated sales data
		File file = new File("Res/sale.ser");
		if(!file.exists())
			throw null;
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Sales[] temp = (Sales[]) ois.readObject();
		//distribute the loaded sales data into two-dimensional array to be used
		addToTwoDimentionalArray(temp);
		ois.close();
	}
	private void addToTwoDimentionalArray(Sales[] temp) {
		//add sales data in one-dimensional array into a two-dimensional array
		for(int i=0;i<temp.length-1;i++){
			sales[temp[i].year][temp[i].month] = temp[i];
		}
		total = temp[temp.length-1];
	}
	public int[][] getDataAt_For(String[] dealers, String[] products, int n) {
		//get sales numerical data of any given dealers and products at the given date
		int[][] temp = new int[dealers.length][products.length];
		String[] pNames = Products.getProductsBefore(n, year, month);
		String[] dNames = Dealers.getDealersBefore(year, month);
		
		int[] pPos = new int[products.length];
		int[] dPos = new int[dealers.length];
		
		//get which dealers info required
		for(int i=0;i<dealers.length;i++){
			dPos[i] = -1;
			for(int j=0;j<dNames.length;j++){
				if(dealers[i].equals(dNames[j])){
					dPos[i] = j;
					break;
				}
			}
		}
		
		//get which products info required
		for(int i=0;i<products.length;i++){
			pPos[i] = -1;
			for(int j=0;j<pNames.length;j++){
				if(products[i].equals(pNames[j])){
					pPos[i] = j;
					break;
				}
			}
		}
		
		//put data into two-dimensional array
		for(int i=0;i<dealers.length;i++){
			for(int j=0;j<products.length;j++){
				if(dPos[i] != -1 && pPos[j] != -1)
				temp[i][j] = data[n][dPos[i]][pPos[j]];
			}
		}
		
		return temp;
	}
	public static void reset() {
		//reset sales information when the user logs off
		if(MainFrame.administrator)
			save();
		sales = null;
		total = null;
		
	}
}