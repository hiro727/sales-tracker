package project;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class Search {
	private Sales[] sales;
	private String[] year_month;
	private int[][][] data = new int[Products.getTypes().length][][];
	private String[][] proNames = new String[Products.getTypes().length][];
	private String[] deaNames;
	private int[]  proLengths = new int[Products.getTypes().length];
	
	private JTable[] all_P_P_D = new JTable[Products.getTypes().length];
	private JTable[] all_P_D_P = new JTable[Products.getTypes().length];
	private JTabbedPane[] total2 = new JTabbedPane[Products.getTypes().length];
	private JTabbedPane[] total3 = new JTabbedPane[Products.getTypes().length];
	private JTable[][] total_P_D = new JTable[Products.getTypes().length][];
	private JTable[][] total_P_P = new JTable[Products.getTypes().length][];
	
	private JList[] list = new JList[5];
	//period != null && dealer == null
	public Search(int start, int end, String[][] product) {
//		System.out.println("period != customized && dealer == null");
		getSalesAddresses(start, end);
		getProductNames(product);
		getDealerNames();
		createTables();
		
	}
	//period != null && dealer != null
	public Search(int start, int end, String[] dealer, String[][] product) {
//		System.out.println("period != customized && dealer != null");
		getSalesAddresses(start, end);
		getProductNames(product);
		getDealerNames(dealer);
		createTables();
		
	}
	//period == null && dealer == null
	public Search(int[] periods, String[][] product) {
//		System.out.println("period == customized && dealer == null");
		getSalesAddresses(periods);
		getProductNames(product);
		getDealerNames();
		createTables();
		
	}
	//period == null && dealer != null
	public Search(int[] periods, String[] dealer, String[][] product) {
//		System.out.println("period == customized && dealer != null");
		getSalesAddresses(periods);
		getProductNames(product);
		getDealerNames(dealer);
		createTables();
		
	}
	
	/*
	 * ||||||||||||||||||||||||||||||||||||||||||||
	 * |||||||||||||||||GET METHODS||||||||||||||||
	 * ||||||||||||||||||||||||||||||||||||||||||||
	 */
	/**
	 * Gets all sales between 'start' and 'end'
	 */
	private void getSalesAddresses(int start, int end) {
		int year = start/12;
		int month = start%12;
		
		//create an array that holds all the given sales data
		sales = new Sales[end - start + 1];
		year_month = new String[end - start + 1];
		
		int counter = 0;
		while(counter < sales.length){
			//get sales data
			sales[counter] = Sales.getSalesOf(year, month);
			//get year and month
			if(sales[counter] == null)
				year_month[counter] = (year+2008)+" "+Sales.getMonths()[month].substring(0, 3);
			else
				year_month[counter] = (sales[counter].getYear()+2008)+" "+Sales.getMonths()[sales[counter].getMonth()].substring(0, 3);
			counter++;
			//repeat for the next month
			month++;
			if(month == 12){
				month = 0;
				year++;
			}
		}
	}
	/**
	 * gets all sales of dates defined by the parameter
	 * @param period the dates which the user wants to search
	 */
	private void getSalesAddresses(int[] period) {
		sales = new Sales[period.length];
		year_month = new String[sales.length];
		
		//get sales data and, years and months
		for(int i=0;i<period.length;i++){
			sales[i] = Sales.getSalesOf(period[i]/13, period[i]%13);
			if(sales[i] == null)
				year_month[i] = (period[i]/13+2008)+" "+Sales.getMonths()[period[i]%13].substring(0, 3);
			else
				year_month[i] = (sales[i].getYear()+2008)+" "+Sales.getMonths()[sales[i].getMonth()].substring(0, 3);
		}
	}
	/**gets all dealers names */
	private void getDealerNames() {
		deaNames = Dealers.getAllDealers();
	}
	/**define local variable using the parameter */
	private void getDealerNames(String[] dealer) {
		deaNames = dealer;		
	}
	/**
	 * define local variable for products using the parameter
	 * @param product all selected products by the user
	 */
	private void getProductNames(String[][] product) {
		for(int i=0;i<product.length;i++){
			if(product[i] == null)
				proLengths[i] = Products.getAllProducts(i).length;
			else
				proLengths[i] = product[i].length;
		}
		for(int i=0;i<proLengths.length;i++)
			proNames[i] = new String[proLengths[i]];
		
		for(int i=0;i<product.length;i++){
			if(product[i] == null){
				//when all products are selected, get all names
				String[] names = Products.getAllProducts(i);
				for(int j=0;j<names.length;j++){
					proNames[i][j] = names[j];
				}
			}else{
				//if specific products are selected
				for(int j=0;j<product[i].length;j++){
					proNames[i][j] = product[i][j];
				}
			}
		}
	}
	
	/*
	 * ||||||||||||||||||||||||||||||||||||||||||||
	 * ||||||||||||||||TABLE MAKING||||||||||||||||
	 * ||||||||||||||||||||||||||||||||||||||||||||
	 */
	
	
	
	
	
	/**creates 4 tables based on the variables previously defined	*/
	private void createTables() {
		createListsForRowHeaders();
		createPeriod_Product_DealerTable();
		createPeriod_Dealer_ProductTable();
		createPeriod_ProductTotalTables();
		createPeriod_DealerTotalTables();
	}
	/**creates lists that will be used as row headers of tables
	 * (of JScrollPane to be more precise)	*/
	private void createListsForRowHeaders() {
		JTable table = new JTable();
		//list[0]: dealer names
		//list[1]: car names
		//list[2]: motorcycle names
		//list[3]: ATV names
		//list[4]: outboard motor
		list[0] = new JList(deaNames);
		for(int i=1;i<list.length;i++)
			list[i] = new JList(proNames[i-1]);
		
		//adjust the size to make sure all labels fit in the size
		for(int i=0;i<list.length;i++){
			if(list[i].getMaximumSize().width>70)
				list[i].setFixedCellWidth(list[i].getMaximumSize().width);
			else
				list[i].setFixedCellWidth(70);
			list[i].setFixedCellHeight(table.getRowHeight());
			
			//set the lists for tables
			list[i].setCellRenderer(new Renderer(table));
		}
		table = null;
	}
	
	
	/*
	 * all_P_P_D
	 * 	TABLE SHOWING ALL SALES OVER A PERIOD OF TIME
	 * 		PERIODS_PRODUCTS_DEALERS
	 * 
	 * |--------------------------------------------------------|
	 * |		|		2010 Jan		|		2010 Feb		|
	 * |--------------------------------------------------------|
	 * |		| CAR 1	| CAR 2 | CAR 3	| CAR 1	| CAR 2 | CAR 3	|
	 * |--------|-------|-------|-------|-------|-------|-------|
	 * |   DF	|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|
	 * | PUEBLA	|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|
	 * | OAXACA	|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|
	 * | CANCUN	|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|
	 * |   :	|		|		|		|		|		|		|
	 * |--------------------------------------------------------|
	 * 
	 */
	
	private void createPeriod_Product_DealerTable() {

		for(int i=0;i<all_P_P_D.length;i++){
			data[i] = new int[deaNames.length][sales.length*proLengths[i]];
				
			for(int j=0;j<sales.length;j++){
				int[][] temp = null;
				if(sales[j] == null || sales[j].getData(i) == null)
					temp = new int[deaNames.length][proLengths[i]];
				else
					temp = sales[j].getDataAt_For(deaNames, proNames[i], i);
				
				int offset = j*proLengths[i] ;
				for(int l=0;l<deaNames.length;l++){
					for(int m=0;m<proLengths[i];m++){
						data[i][l][m+offset] = temp[l][m];
					}
				}
			}
				
			
			int counter = 0;
			String[] colNames = new String[sales.length*proLengths[i]];
			for(int j=0;j<sales.length;j++){
				for(int l=0;l<proLengths[i];l++)
					colNames[counter++] = proNames[i][l];
				
			}
			all_P_P_D[i] = new JTable(new Object[deaNames.length][sales.length*proLengths[i]], colNames){
				private static final long serialVersionUID = 1L;
				@Override
				protected JTableHeader createDefaultTableHeader() {
					return new TableHeaders(columnModel);
				}
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
			for(int j=0;j<deaNames.length;j++){
				for(int k=0;k<all_P_P_D[i].getColumnCount();k++){
					all_P_P_D[i].setValueAt(data[i][j][k]+"", j, k);
				}
			}
			all_P_P_D[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			all_P_P_D[i].getTableHeader().setReorderingAllowed(false);
			
			
			createMultipleHeaderForTable0(all_P_P_D[i], i);
		}
	}
	
	
	
	private void createMultipleHeaderForTable0(JTable table, int n) {		
		
		TableColumnModel cm = table.getColumnModel();
		TableHeaders header = (TableHeaders)table.getTableHeader();
		ColumnGroup[] cg = new ColumnGroup[sales.length];
		for(int i=0;i<sales.length;i++){
			if(sales[i] != null)
				cg[i] = new ColumnGroup((sales[i].getYear()+2008)+" "+Sales.getMonths()[sales[i].getMonth()].substring(0, 3));
			else
				cg[i] = new ColumnGroup(year_month[i]);
			for(int j=0;j<proLengths[n];j++){
				cg[i].add(cm.getColumn(i*proLengths[n]+j));
			}
		}
		for(int i=0;i<cg.length;i++){
			header.addColumnGroup(cg[i]);
		}
		
	}

	/*
	 * all_P_D_P
	 * 	TABLE SHOWING ALL SALES OVER A PERIOD OF TIME
	 * 		PERIODS_DEALERS_PRODUCTS
	 * 
	 * |------------------------------------------------------------------------|
	 * |		|			2010 Jan			|			2010 Feb			|
	 * |------------------------------------------------------------------------|
	 * |		|   DF	|PEUBLA	|OAXACA	|CANCUN	|   DF	|PEUBLA	|OAXACA	|CANCUN	|
	 * |--------|-------|-------|-------|-------|-------|-------|-------|-------|
	 * |  CAR 1	|		|		|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|-------|-------|
	 * |  CAR 2 |		|		|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|-------|-------|
	 * |  CAR 3	|		|		|		|		|		|		|		|		|
	 * |--------|-------|-------|-------|-------|-------|-------|-------|-------|
	 * |   :	|		|		|		|		|		|		|		|		|
	 * |------------------------------------------------------------------------|
	 * 
	 */
	
	
	private void createPeriod_Dealer_ProductTable() {
		for(int i=0;i<all_P_D_P.length;i++){
			String[] colNames = new String[sales.length*deaNames.length];
			int counter = 0;
			for(int j=0;j<sales.length;j++){
				for(int k=0;k<deaNames.length;k++){
					colNames[counter++] = deaNames[k];
				}
			}
			all_P_D_P[i] = new JTable(new Object[proLengths[i]][colNames.length], colNames){
				private static final long serialVersionUID = 1L;
				@Override
				public JTableHeader createDefaultTableHeader() {
					return new TableHeaders(columnModel);
				}
				@Override
				public boolean isCellEditable(int arg0, int arg1) {
					return false;
				}
			};
			all_P_D_P[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			all_P_D_P[i].getTableHeader().setReorderingAllowed(false);
			for(int j=0;j<sales.length;j++){
				
				for(int k=0;k<deaNames.length;k++){
					
					for(int l=0;l<proLengths[i];l++){
						
						all_P_D_P[i].setValueAt(data[i][k][l+j*proLengths[i]]+"", l, k+j*deaNames.length);
					}
				}
			}
			createMultipleHeaderForTable1(all_P_D_P[i]);
		}
		
	}

	private void createMultipleHeaderForTable1(JTable table) {
		TableColumnModel cm = table.getColumnModel();
		TableHeaders header = (TableHeaders)table.getTableHeader();
		ColumnGroup[] cg = new ColumnGroup[sales.length];
		for(int i=0;i<sales.length;i++){
			if(sales[i] != null){
				cg[i] = new ColumnGroup((sales[i].getYear()+2008)+" "+Sales.getMonths()[sales[i].getMonth()].substring(0, 3));
			}else{
				cg[i] = new ColumnGroup(year_month[i]);
			}for(int j=0;j<deaNames.length;j++){
				cg[i].add(cm.getColumn(i*deaNames.length+j));
			}
		}
		for(int i=0;i<cg.length;i++){
			header.addColumnGroup(cg[i]);
		}
		
	}
	

	/*
	 * total_P_P 
	 * 	TABLES SHOWING SALES AT EACH DEALER OVER A PERIOD OF TIME
	 * 		PERIODS_PRODUCTS
	 *  _______ ________ ________ ________
	 * |   DF  | PEUBLA | OAXACA | CANCUN |
	 * |--------------------------------------------------------|
	 * |--------------------------------------------------------|
	 * |		|  2010 Jan	|  2010 Feb	|  2010 Mar	|  2010 Apr	|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |  CAR 1	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |  CAR 2|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |  CAR 3	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |   :	|			|			|			|			|
	 * |--------------------------------------------------------|
	 * 
	 */
	
	private void createPeriod_ProductTotalTables() {
		for(int i=0;i<total_P_P.length;i++){
			if(deaNames.length != 0 && proLengths[i] != 0){
				total_P_P[i] = new JTable[deaNames.length];
				
				final int x = i;
				total2[i] = new JTabbedPane(){
					private static final long serialVersionUID = 1L;
					@Override
					public void setSelectedIndex(int index) {
						super.setSelectedIndex(index);
						((JScrollPane) getComponentAt(index)).setRowHeaderView(list[x+1]);
					}
				};
				for(int j=0;j<total_P_P[i].length;j++){
					total_P_P[i][j] = new JTable(new Object[proLengths[i]][sales.length], year_month){
						private static final long serialVersionUID = 1L;
						@Override
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};
					int counter = 0;
					for(int k=0;k<sales.length;k++){
						for(int l=0;l<proLengths[i];l++){
							total_P_P[i][j].setValueAt(data[i][j][k], l, counter++/proLengths[i]);
						}
					}
					total_P_P[i][j].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					total_P_P[i][j].getTableHeader().setReorderingAllowed(false);
					JScrollPane sp = new JScrollPane(total_P_P[i][j]);
					total2[i].add(sp);
				}
			}else
				total2[i] = null;
		}
	}
	

	public void addRowHeaderToTable2(int n) {
		((JScrollPane) total2[n].getComponentAt(total2[n].getSelectedIndex())).setRowHeaderView(list[n+1]);
		
	}
	

	/*
	 * total_P_D 
	 * 	TABLES SHOWING SALES OF EACH PRODUCT (MODEL) OVER A PERIOD OF TIME
	 * 		PERIODS_DEALERS
	 *  _______ _______ _______
	 * | CAR 1 | CAR 2 | CAR 3 |
	 * |--------------------------------------------------------|
	 * |--------------------------------------------------------|
	 * |		|  2010 Jan	|  2010 Feb	|  2010 Mar	|  2010 Apr	|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |   DF	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * | PUEBLA	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * | OAXACA	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * | CANCUN	|			|			|			|			|
	 * |--------|-----------|-----------|-----------|-----------|
	 * |   :	|			|			|			|			|
	 * |--------------------------------------------------------|
	 * 
	 */
	
	private void createPeriod_DealerTotalTables() {
		for(int i=0;i<total_P_D.length;i++){

			if(deaNames.length != 0 && proLengths[i] != 0){
				total_P_D[i] = new JTable[proLengths[i]];
				
				total3[i] = new JTabbedPane(){
					private static final long serialVersionUID = 1L;
					@Override
					public void setSelectedIndex(int index) {
						super.setSelectedIndex(index);
						((JScrollPane) getComponentAt(index)).setRowHeaderView(list[0]);
					}
				};

				for(int j=0;j<total_P_D[i].length;j++){
					total_P_D[i][j] = new JTable(new Object[deaNames.length][sales.length], year_month){
						private static final long serialVersionUID = 1L;
						@Override
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};
					for(int k=0;k<deaNames.length;k++){
						for(int l=0;l<sales.length;l++){
							total_P_D[i][j].setValueAt(data[i][k][l*proLengths[i]+j], k, l);
						}
					}
					total_P_D[i][j].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					total_P_D[i][j].getTableHeader().setReorderingAllowed(false);
					JScrollPane sp = new JScrollPane(total_P_D[i][j]);
					total3[i].add(sp);
				}
			}else
				total3[i] = null;
		}
		
	}

	public void addRowHeaderToTable3(int n) {
		((JScrollPane) total3[n].getComponentAt(total3[n].getSelectedIndex())).setRowHeaderView(list[0]);
	}
	
	
	
	public JTable getTable0(int n) {
		return all_P_P_D[n];
	}
	public JTable getTable1(int n) {
		return all_P_D_P[n];
	}
	public JTabbedPane getTable2(int n) {
		return total2[n];
	}
	public JTabbedPane getTable3(int n) {
		return total3[n];
	}
	public JList getList(int n) {
		return list[n];
	}
	
	/*
	 * |||||||||||||||||||||||||||||||||||||||||||
	 * |||||||||METHODS FOR MAKING CLONES|||||||||
	 * |||||||||||||||||||||||||||||||||||||||||||
	 * 
	 */
	
	public JTable getCloneOfTable0(int n){
		JTable table = new JTable(all_P_P_D[n].getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new TableHeaders(columnModel);
			}
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		createMultipleHeaderForTable0(table, n);
		return table;
	}
	public JTable getCloneOfTable1(int n) {
		JTable table = new JTable(all_P_D_P[n].getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new TableHeaders(columnModel);
			}
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		createMultipleHeaderForTable1(table);
		return table;
	}
	public JTable getCloneOfTable2(int n) {
		JTable table = new JTable(total_P_P[n][total2[n].getSelectedIndex()].getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return table;
	}
	public JTable getCloneOfTable3(int n) {
		JTable table = new JTable(total_P_D[n][total3[n].getSelectedIndex()].getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return table;
	}
	public JList getCloneOfList(int n) {
		JList jlist = new JList(list[n].getModel());
		if(jlist.getMaximumSize().width>70)
			jlist.setFixedCellWidth(jlist.getMaximumSize().width);
		else
			jlist.setFixedCellWidth(70);
		if(n == 0){
			jlist.setFixedCellHeight(all_P_P_D[n].getRowHeight());
			jlist.setCellRenderer(new Renderer(all_P_P_D[n]));
		}else{//if(n == 1){
			jlist.setFixedCellHeight(all_P_D_P[n].getRowHeight());
			jlist.setCellRenderer(new Renderer(all_P_D_P[n]));
		}
		return jlist;
	}

	public JList getCloneOfList(int n, int n1) {
		JList jlist = new JList(list[n].getModel());
		if(jlist.getMaximumSize().width>70)
			jlist.setFixedCellWidth(jlist.getMaximumSize().width);
		else
			jlist.setFixedCellWidth(70);
		if(n == 2){
			jlist.setFixedCellHeight(total_P_D[n][n1].getRowHeight());
			jlist.setCellRenderer(new Renderer(total_P_D[n][n1]));
		}else{//if(n == 3){
			jlist.setFixedCellHeight(total_P_P[n][n1].getRowHeight());
			jlist.setCellRenderer(new Renderer(total_P_P[n][n1]));
		}
		return jlist;
	}
}