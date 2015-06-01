package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class View_Data {
	
	JPanel[] product;					//panels to view products data
	JPanel dealer;						//panel to view dealers data
	JPanel sales;						//panel to view sales data
	
	JTextArea[][] pNms;					//with labels of products' names and details
	JTextArea[] dNms;					//with labels of dealers' names and details
	
	private int editingRow = 0;	
	JTextField	lbl_Name;				//empty field to adjust other text areas' locations
	JComboBox[] combo_PD;				//combo boxes containing all the detailed information
	JComboBox	combo_Year;				//combo box for all years since 2008
	JComboBox	combo_Month;			//combo box for all months
	JTextArea	new_dataP, new_dataD;	//text area for adding new product/dealer
	JComboBox	combo_State;			//combo box containing all the states information
	
	JTabbedPane   tabYear;				//tabbed pane for different years since 2008
	JTabbedPane[] tabMonth;				//tabbed pane for different months including total of the year
	JScrollPane   scroll;				//scroll pane containing any given sales data table
	JList		  list;					//list of dealers' names (row headers on the table)
	JTable[][]    total;				//tables for total sales data of all years
	
	/** table cell renderer to align text at right */
	private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	
	private int year = 0, month = 0;
	private int westSize = 200;
	
	public View_Data() {
		
		createProductPanels();
		createDealerPanel();
		createSalesPanel();
		
		
	}
	
	private void createProductPanels() {
		
		lbl_Name = new JTextField("");
		combo_PD = new JComboBox[Products.getDetailNames().length];
		
		//get all information of the details of products
		for(int i=0;i<combo_PD.length;i++){
			if(i == 0)
				combo_PD[i] = new JComboBox(Products.getDisplacement());
			else if(i == 1)
				combo_PD[i] = new JComboBox(Products.getStrokes());
			else if(i == 2)
				combo_PD[i] = new JComboBox(Products.getHorsepower());
			
			combo_PD[i].setMaximumSize(new Dimension(Short.MAX_VALUE, combo_PD[i].getPreferredSize().height));
		}
		//get information of years and months from the sales class
		combo_Year  = new JComboBox(Sales.getYears());
		combo_Month = new JComboBox(Sales.getMonths());
		
		//set maximum size of combo boxes not to become too large in height which happens on some platforms
		lbl_Name.setMaximumSize   (new Dimension(Short.MAX_VALUE, lbl_Name.getPreferredSize().height));
		combo_Year.setMaximumSize (new Dimension(Short.MAX_VALUE, combo_Year.getPreferredSize().height));
		combo_Month.setMaximumSize(new Dimension(Short.MAX_VALUE, combo_Month.getPreferredSize().height));
		
		
		product = new JPanel[Products.getTypes().length];
		pNms = new JTextArea[product.length][];
		
		//initialize GUI of the all products panels
		for(int i=0;i<product.length;i++){
			product[i] = new JPanel();
			product[i].setLayout(new BoxLayout(product[i], BoxLayout.Y_AXIS));
			product[i].setMinimumSize(new Dimension(westSize, 600));
			product[i].setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(),"Products Data - "+Products.getTypes()[i], 
					TitledBorder.LEFT, TitledBorder.TOP, MainFrame.f1, Color.red));
			//add mouse listener to the panel to enable editing only for the administrator
			if(MainFrame.administrator)
				product[i].addMouseListener(product_clicked);
			
			pNms[i] = new JTextArea[Products.getSize(i)];
			Object[][] data = Products.getAllData(i);
			//get data of the all products from the product class
			
			for(int j=0;j<pNms[i].length;j++){
				String text = data[j][0]+":   ";
				for(int k=1;k<data[j].length-1;k++)
					text += data[j][k]+",  ";
				text += data[j][data[j].length-1].toString().substring(0, 3);
				
				//set the text of each text area with all the information of one product and enable auto-line-break
				pNms[i][j] = new JTextArea(text);
				pNms[i][j].setOpaque(false);
				pNms[i][j].setLineWrap(true);
				pNms[i][j].setWrapStyleWord(true);
				pNms[i][j].setEditable(false);
				
				//add mouse listener to the text areas to enable editing only for the administrator
				if(MainFrame.administrator)
					pNms[i][j].addMouseListener(product_clicked);
				
				//set maximum size of combo boxes not to become too large in height which happens on some platforms
				pNms[i][j].setMinimumSize(new Dimension(westSize, 20));
				pNms[i][j].setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
				pNms[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
				product[i].add(pNms[i][j]);
				pNms[i][j].setSize(new Dimension(westSize, 20));
			}
			
		}
		new_dataP = new JTextArea("                      New Data ");
		new_dataP.setOpaque(true);
		new_dataP.setBackground(Color.blue);
		new_dataP.setForeground(Color.red);
		new_dataP.setMinimumSize(new Dimension(westSize, 20));
		new_dataP.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		if(MainFrame.administrator){
			new_dataP.addMouseListener(product_clicked);
			product[0].add(new_dataP);
		}
		new_dataP.setEditable(false);
		
	}

	private void createDealerPanel() {
		//initialize GUI of the dealers panel
		combo_State = new JComboBox(Dealers.getStates());
		combo_State.setMaximumSize (new Dimension(Short.MAX_VALUE, combo_State.getPreferredSize().height));
		dealer = new JPanel();
		dealer.setLayout(new BoxLayout(dealer, BoxLayout.Y_AXIS));
		dealer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Dealers", 
				TitledBorder.LEFT, TitledBorder.TOP, MainFrame.f1, Color.red));
		
		//add mouse listener to the panel to enable editing only for the administrator
		if(MainFrame.administrator)
			dealer.addMouseListener(dealer_clicked);
		
		Object[][] data = Dealers.getAllData();
		//get data of the all dealers from the dealer class
		
		dNms = new JTextArea[data.length];
		for(int i=0;i<data.length;i++){
			String text = data[i][0]+":     ";
			for(int j=1;j<data[i].length-1;j++){
				text += data[i][j]+",  ";
			}
			text += data[i][data[i].length-1].toString().substring(0, 3);
			
			//set the text of each text area with all the information of one product and enable auto-line-break
			dNms[i] = new JTextArea(text);
			dNms[i].setOpaque(false);
			dNms[i].setLineWrap(true);
			dNms[i].setWrapStyleWord(true);
			dNms[i].setEditable(false);
			
			//add mouse listener to the text areas to enable editing only for the administrator
			if(MainFrame.administrator)
				dNms[i].addMouseListener(dealer_clicked);
			
			//set maximum size of combo boxes not to become too large in height which happens on some platforms
			dNms[i].setMinimumSize(new Dimension(westSize, 20));
			dNms[i].setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
			dNms[i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
			
			dealer.add(dNms[i]);
		}
		new_dataD = new JTextArea("                      New Data ");
		new_dataD.setOpaque(true);
		new_dataD.setBackground(Color.blue);
		new_dataD.setForeground(Color.red);
		new_dataD.setMinimumSize(new Dimension(westSize, 20));
		new_dataD.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		if(MainFrame.administrator){
			new_dataD.addMouseListener(dealer_clicked);
			dealer.add(new_dataD);
		}
		new_dataD.setEditable(false);
	}

	private void createSalesPanel() {
		//initialize components and the GUI of the dealers panel
		sales = new JPanel();
		sales.setLayout(new BorderLayout());
		sales.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),"Sales - "+Products.getTypes()[0], 
				TitledBorder.LEFT, TitledBorder.TOP, MainFrame.f1, Color.red));
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
		scroll = new JScrollPane();
		list   = new JList();
		
		//add the list to the row header of the scroll pane
		scroll.setRowHeaderView(list);
		//set the default height and cell renderer of the list
		JTable temp = new JTable();
		list.setFixedCellHeight(temp.getRowHeight());
		list.setCellRenderer(new Renderer(temp));
		
		//create tabbedPane that will create new sales table when different tab is selected
		tabYear  = new JTabbedPane(){
			private static final long serialVersionUID = 1L;
			@Override
			public void setSelectedIndex(int index) {
				tabMonth[year].setComponentAt(month, null);
				super.setSelectedIndex(index);
				year = index;
				month = tabMonth[year].getSelectedIndex();
				createTable(MainFrame.index, year, month);
			}
		};
		//create tabbedPane that will create new sales table when different tab is selected
		tabMonth = new JTabbedPane[Sales.getYears().length];
		for(int i=0;i<tabMonth.length;i++){
			tabMonth[i] = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT){
				private static final long serialVersionUID = 1L;
				@Override
				public void setSelectedIndex(int index) {
					tabMonth[year].setComponentAt(month, null);
					super.setSelectedIndex(index);
					month = index;
					createTable(MainFrame.index, year, month);
				}
			};
			for(int j=0;j<Sales.getMonths().length;j++)
				tabMonth[i].add(Sales.getMonths()[j], null);
			
			tabMonth[i].add(" Total ", null);
			tabYear.add(" "+Sales.getYears()[i]+" ", tabMonth[i]);
		}
		sales.add(tabYear);
		
		//create total sales tables of each year from the database
		total = new JTable[Products.getTypes().length][];
		for(int i=0;i<total.length;i++){
			total[i] = new JTable[Sales.getYears().length+1];
			for(int j=0;j<total[i].length;j++){
				Sales sale = (j < total[i].length-1?Sales.getSalesOf(j, 12):Sales.getTotalSales());
				String[] pNms = Products.getProductsBefore(i, j, 12);
				int[][] data = null;
				if(sale == null || sale.getData(i) == null){
					data = new int[Dealers.getDealersBefore(j, 12).length][pNms.length];
				}else{
					data = sale.getData(i);
				}
				//create empty data if there is no sales data of the year
				if(data == null)
					data = new int[Dealers.getDealersBefore(j, 12).length][Products.getProductsBefore(i, j, 12).length];
				else if(data.length == 0){
					createTable(0, 0, 0);
					return;
				}
				//create total sales tables that will return 0 for null cell values or for empty cell values
				total[i][j] = new JTable(new Object[data.length][data[0].length], pNms){
					private static final long serialVersionUID = 1L;
					@Override
					public Object getValueAt(int row, int column) {
						Object o = super.getValueAt(row, column);
						if(o == null || o.toString().equals(""))
							return "0";
						return o;
					}
				};
				total[i][j].setRowSelectionAllowed(false);
			}
		}
		createTable(0, 0, 0);
	}
	/**
	 * 
	 * @param n Product Type
	 * @param year Year of View
	 * @param month Month of View
	 * @return Table showing sales data of the given year and month
	 */
	private void createTable(int n, int year, int month) {
		try{
			int[][] data = null;
			try{
				data = Sales.getSalesOf(year, month).getData(n);
				System.out.println(data.length);
			}catch (NullPointerException e){}
			
			if(data == null){
				//create empty array if there is predefined sales data
				data = new int[Dealers.getDealersBefore(year, month).length]
						[Products.getProductsBefore(n, year, month).length];
					
			}
			//show error message if either of products or dealers does not exist at the given date
			if(data.length == 0 || data[0].length == 0)
				throw null;
			
			DefaultTableModel model = new DefaultTableModel(
					new Object[data.length][data[0].length], Products.getProductsBefore(n, year, month));
			
			//create total row and total column and add them to the table model
			Integer[] totalC = new Integer[data.length];
			Integer[] totalR = new Integer[data[0].length+1];
			for(int i=0;i<totalR.length;i++)
				totalR[i] = Integer.valueOf(0);
			for(int i=0;i<totalC.length;i++){
				totalC[i] = Integer.valueOf(0);
				for(int j=0;j<totalR.length-1;j++){
					System.out.println("totalC["+i+"], totalR["+j+"]");
					totalC[i] += data[i][j];
					totalR[j] += data[i][j];
				}
			}
			for(int i=0;i<totalC.length;i++){
				totalR[totalR.length-1] += totalC[i];
			}
			model.addColumn("Total", totalC);
			model.addRow(totalR);
			JTable tbl = null;
			if(month != 12){
				//create a table that will not proceed any non-numeric key events
				tbl = new JTable(model){
					private static final long serialVersionUID = 1L;
					@Override
					protected void processKeyEvent(KeyEvent e) {
						char c = e.getKeyChar();
						if(c == KeyEvent.VK_0 || c == KeyEvent.VK_1 || c == KeyEvent.VK_2 || c == KeyEvent.VK_3 || c == KeyEvent.VK_4 || c == KeyEvent.VK_5 || c == KeyEvent.VK_6 || c == KeyEvent.VK_7 || c == KeyEvent.VK_8 || c == KeyEvent.VK_9){
							System.out.println(getEditingRow()+" "+getEditingColumn());
							super.processKeyEvent(e);
						}else if(c == KeyEvent.VK_TAB || c == KeyEvent.VK_RIGHT){
							if(getSelectedColumn() == getColumnCount() - 1){
								changeSelection((getSelectedRow() != getRowCount()-2?getSelectedRow()+1:0), 0, false, false);
							}else
								super.processKeyEvent(e);
						}else if(c == KeyEvent.VK_ENTER){
							if(getSelectedRow() == getRowCount() - 1){
								changeSelection(0, (getSelectedColumn() != getColumnCount()-2?getSelectedColumn()+1:0), false, false);
							}else
								super.processKeyEvent(e);
						}else{
							JOptionPane.showMessageDialog(null, "Invalid Character");
						}
					}
					@Override
					public void setValueAt(Object arg0, int arg1, int arg2) {
						//set an empty string if no data is sent in
						if((arg0 == null || arg0.toString().isEmpty() || arg0.toString().equals("0")) && 
								(arg1 != getRowCount()-1 && arg2 != getColumnCount()-1))
							super.setValueAt("", arg1, arg2);
						else
							super.setValueAt(arg0, arg1, arg2);
					}
					@Override
					public boolean isCellEditable(int row, int column) {
						//disable editing the total row and total column (last row and last column)
						if(row == getRowCount()-1)
							return false;
						if(column == getColumnCount()-1)
							return false;
						return true;
					}
					@Override
					public void editingStopped(ChangeEvent e) {
						//update total values when the editing of a cell is finished
						int row = getSelectedRow();
						int col = getSelectedColumn();
						int tab = tabYear.getSelectedIndex();
						try{
							
							//the original value
							int value1 = 0;
							//get the original value from the table
							if(getValueAt(row, col) == null || getValueAt(row, col).toString().isEmpty())
								value1 = 0;
							else
								value1 = Integer.parseInt(getValueAt(row, col).toString());
							
							//new value
							int value2 = 0;
							//get the new value from the cell editor
							if(!getCellEditor().getCellEditorValue().toString().isEmpty())
								value2 = Integer.parseInt(getCellEditor().getCellEditorValue().toString());
							else
								setValueAt("0", row, col);
							
							//do nothing if the value has not been changed
							if(value1 == value2){
								super.editingStopped(e);
								return;
							}
							//change value in the database
							Sales.changeValueOf(tab, tabMonth[tab].getSelectedIndex(), MainFrame.index, 
										value1, value2, row, col);
							//set total value of the row
							setValueAt(Integer.parseInt(getValueAt(getRowCount()-1, col).toString())
									-value1+value2, getRowCount()-1, col);
							//set total value of the column
							setValueAt(Integer.parseInt(getValueAt(row, getColumnCount()-1).toString())-
									value1+value2, row, getColumnCount()-1);
							//set total value of the month
							setValueAt(Integer.parseInt(getValueAt(getRowCount()-1, getColumnCount()-1).toString())-
									value1+value2, getRowCount()-1, getColumnCount()-1);
							//set total values of the year and of the all sales
							
							//all of the above are done before cell editing is stopped
							super.editingStopped(e);
						} catch(NumberFormatException nfe){
							//editing cannot be finished if invalid characters (e.g. String) are being entered
							JOptionPane.showMessageDialog(sales, "The number is too large\nPlease make sure that the number is correct");
							super.editingCanceled(e);
						}
					}
				};
			}else{
				//create the total sales table
				tbl = new JTable(model){
					private static final long serialVersionUID = 1L;
					@Override
					public Object getValueAt(int row, int column) {
						//return 0 for empty or null cell values
						Object o = super.getValueAt(row, column);
						if(o == null || o.toString().equals(""))
							return "0";
						return o;
					}
					@Override
					public boolean isCellEditable(int row, int column) {
						//all cells are not editable
						return false;
					}
				};
				tbl.setRowSelectionAllowed(false);
				tbl.setEnabled(false);
			}
			//set cell values
			for(int i=0;i<data.length;i++){
				for(int j=0;j<data[i].length;j++){
					tbl.setValueAt(data[i][j], i, j);
				}
			}
			//table can only be edited by the administrator
			if(!MainFrame.administrator)
				tbl.setEnabled(false);
			
			tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tbl.setRowSelectionAllowed(false);
			tbl.setCellSelectionEnabled(true);
			tbl.getTableHeader().setReorderingAllowed(false);
			
			//adjust the size of each column if the column name does not fit
			FontMetrics fm = tbl.getFontMetrics(tbl.getFont());
			for(int i=0;i<tbl.getColumnCount();i++){
				int w = fm.stringWidth(tbl.getColumnName(i));
				if(w < 100)
					tbl.getColumnModel().getColumn(i).setPreferredWidth(100);
				else
					tbl.getColumnModel().getColumn(i).setPreferredWidth(w+10);
				tbl.getColumnModel().getColumn(i).setCellRenderer(renderer);
			}
			
			//create new array of string with dealers' names and the string 'total' 
			String[] temp = Dealers.getDealersBefore(year, month);
			String[] rNms = new String[temp.length+1];
			for(int i=0;i<temp.length;i++)
				rNms[i] = temp[i];
			rNms[temp.length] = "Total";
			list.setListData(rNms);
			if(list.getMaximumSize().width>100)
				list.setFixedCellWidth(list.getMaximumSize().width);
			else
				list.setFixedCellWidth(100);
			
			
			if(month == 12){
				total[MainFrame.index][this.year] = tbl;
				scroll.setViewportView(total[MainFrame.index][this.year]);
			}else{
				scroll.setViewportView(tbl);
			}
			tabMonth[year].setComponentAt(month, scroll);
		}catch (NullPointerException e){
			//when null is thrown by the step above
			tabMonth[year].setComponentAt(month, new JLabel("No Data Available", JTextField.CENTER));
		}
	}

	public void changeTable(int n) {
		createTable(n, year, month);
	}
	public void changeWidth(int width){
		for(int i=0;i<dNms.length;i++){
			dNms[i].setSize(new Dimension(width/2, 20));
			dNms[i].updateUI();
		}
		editingRow = -1;
	}
	public void changeWidth(int n, int width){
		product[n].setSize(westSize, product[n].getSize().height);
		for(int j=0;j<pNms[n].length;j++){
			pNms[n][j].setSize(westSize/2, pNms[n][j].getSize().height);
			pNms[n][j].updateUI();
		}
		product[n].updateUI();
		editingRow = -1;
	}
	public JPanel getDealer() {
		return dealer;
	}
	public JPanel getProduct(int n) {
		return product[n];
	}
	public JPanel getSales() {
		return sales;
	}
	public int getWestSize() {
		return westSize;
	}
	
	
	
	/**
	 * mouseListener that responds to MouseEvent 'Clicked' on 
	 * JTextAreas pNms[][] and product panel to allow change in products' data
	 */
	public MouseListener product_clicked = new MouseAdapter() {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			//although mouseListener will not be added when the user is not the administrator,
			//check the user just in case and do nothing if not the administrator
			if(!MainFrame.administrator)
				return;
			
			int n  = MainFrame.index;
			
			if(lbl_Name.isShowing()){
				try{
				if(editingRow == -1)
					addNewProduct(n);
				else
					editProduct(n);
				} catch (NullPointerException npe){
					npe.printStackTrace();
					JOptionPane.showMessageDialog(null, "Duplicate Product Name");
					return;
				}
			}
			//if currently selected row is for any of the existing product, redisplay the text
			if(editingRow != -1 && e.getSource() != pNms[n][editingRow])
				resumeText(n, editingRow);
			
			//reset editing row
			editingRow = -1;
			
			//determine the currently editing row
			for(int i=0;i<pNms[n].length;i++){
				if(e.getSource() == pNms[n][i]){
					editingRow = i;
					break;
				}
			}
			//if non-product (empty) location is selected
			if(editingRow != -1){
				showProductsEntryFields(n, editingRow);
			}else if(e.getSource() == product[n]){
				hideProductsEntryFields(n);
				product[n].updateUI();
			}else if(e.getSource() == new_dataP){
				showProductsEntryFields(n);
			}
		}
		
		private void hideProductsEntryFields(int n) {
			try{
				//remove all components from the product panel
				product[n].remove(lbl_Name);
				for(int i=0;i<Products.getDetailNum()[n].length;i++)
					product[n].remove(combo_PD[Products.getDetailNum()[n][i]]);
				product[n].remove(combo_Year);
				product[n].remove(combo_Month);
			}catch (NullPointerException e){}
		}

		private void resumeText(int n, int hidden) {
			hideProductsEntryFields(n);
			pNms[n][hidden].setVisible(true);
		}
	};
	public void addTextArea_New(int n){
		//only for the administrator
		if(!MainFrame.administrator)
			return;
		//enable input of new product
		product[n].add(new_dataP);
		showProductsEntryFields(n);
		product[n].updateUI();
	}
	public void showProductsEntryFields(int n) {
		//only for the administrator
		if(!MainFrame.administrator)
			return;
		//show entry fields of any given products
		showProductsEntryFields(n, pNms[n].length+1);
		product[n].updateUI();
	}
	public void showProductsEntryFields(int n, int x) {
		//only for the administrator
		if(!MainFrame.administrator)
			return;
		int counter = 0;
		String[] temp = null;	//data of the selected product
		
		if(x < pNms[n].length){
			//to edit existing product
			pNms[n][x].setVisible(false);
			temp = Products.getDataOf(n, x);
		}else{
			//to add new product
			temp = new String[Products.getDetailNum()[n].length+3];
			//reset the product data
			temp[0] = "";
			for(int i=1;i<temp.length;i++)
				temp[i] = "0";
		}
		//set the label of the text area with the name of the product
		lbl_Name.setText(temp[counter++]);
		product[n].add(lbl_Name, x++);
		//set the selected items in the combo boxes for details with the details of the product
		for(int i=0;i<Products.getDetailNum()[n].length;i++){
			int index = Products.getDetailNum()[n][i];
			combo_PD[index].setSelectedIndex(Integer.parseInt(temp[counter++]));
			product[n].add(combo_PD[index], x++);
		}
		//set the year and the month of the release of the product with the product's
		combo_Year.setSelectedIndex(Integer.parseInt(temp[counter++]));
		combo_Month.setSelectedIndex(Integer.parseInt(temp[counter++]));
		
		product[n].add(combo_Year, x++);
		product[n].add(combo_Month, x++);
		
	}
	
	private void addNewProduct(int n) {
		String name = lbl_Name.getText();
		//do nothing if no name has been entered
		if(name.isEmpty())
			return;
		
		//the product name is converted to upper case letters
		name = name.toUpperCase();
		
		//get all the entries
		int[] details = new int[Products.getDetailNum()[n].length];
		for(int i=0;i<details.length;i++)
			details[i] = combo_PD[i].getSelectedIndex();
		
		int year  = combo_Year.getSelectedIndex();
		int month = combo_Month.getSelectedIndex();
		
		//instantiate new products class using the entries
		new Products(n, name, details, year, month);
		
		//update the table
		createTable(MainFrame.index, tabYear.getSelectedIndex(), tabMonth[tabYear.getSelectedIndex()].getSelectedIndex());
		
		//update the text areas
		recreateJTextAreas_Products(n);
	}
	private void editProduct(int n) {
		String name = lbl_Name.getText();
		//show error message if no name has been entered
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(null, "Enter a Name");
			return;
		}
		//the product name is converted to upper case letters
		name = name.toUpperCase();
		
		//get all the entries
		int[] details = new int[Products.getDetailNum()[n].length];
		for(int i=0;i<details.length;i++)
			details[i] = combo_PD[i].getSelectedIndex();
		
		int year  = combo_Year.getSelectedIndex();
		int month = combo_Month.getSelectedIndex();
		
		String[] original = Products.getDataOf(n, editingRow);
		if(name.equalsIgnoreCase(original[0])){
			//only edit the variables of the product if the name has not been changed
			Products.edit(editingRow, n, name, details, year, month);
			String text = name+":     ";
			for(int i=0;i<details.length;i++)
				text += Products.getDetailNames()[details[i]]+",  ";
			
			text += Sales.getYears()[year]+", "+Sales.getMonths()[month].toString().substring(0, 3);
			pNms[n][editingRow].setText(text);
		}else{
			//check if the new name is valid
			if(Products.search(n, name) != null){
				//product with the name already exists; show error message
				JOptionPane.showMessageDialog(null, "Duplicate Products");
				return;
			}
			//delete the original data
			Products.delete(n, editingRow);
			//instantiate new data with the updated variables
			new Products(n, name, details, year, month);
			
			//update the text areas
			recreateJTextAreas_Products(n);
		}
		//update the table
		createTable(MainFrame.index, tabYear.getSelectedIndex(), tabMonth[tabYear.getSelectedIndex()].getSelectedIndex());
	}
	
	private void recreateJTextAreas_Products(int n) {
		//remove and reset all the original text areas
		for(int i=0;i<pNms[n].length;i++){
			product[n].remove(pNms[n][i]);
			pNms[n][i].removeMouseListener(product_clicked);
			pNms[n][i] = null;
		}
		product[n].remove(new_dataP);
		
		//create them all again with the updated products data
		Object[][] data = Products.getAllData(n);
		pNms[n] = new JTextArea[data.length];
		for(int i=0;i<data.length;i++){
			String text = data[i][0]+":     ";
			for(int j=1;j<data[i].length-1;j++){
				text += data[i][j]+",  ";
			}
			text += data[i][data[i].length-1].toString().substring(0, 3);
			pNms[n][i] = new JTextArea(text);
			pNms[n][i].setOpaque(false);
			pNms[n][i].setLineWrap(true);
			pNms[n][i].setWrapStyleWord(true);
			pNms[n][i].setEditable(false);
			if(MainFrame.administrator)
				pNms[n][i].addMouseListener(product_clicked);
			pNms[n][i].setMinimumSize(new Dimension(westSize, 20));
			pNms[n][i].setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
			pNms[n][i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
			
			product[n].add(pNms[n][i]);
		}
		product[n].add(new_dataP);
		product[n].updateUI();
	}
	
		
	
	/**
	 * mouseListener that responds to MouseEvent 'Clicked' on 
	 * JTextAreas dNms[] and dealer panel to allow change in dealers' data
	 */
	public MouseListener dealer_clicked = new MouseAdapter() {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			//only for the administrator
			if(!MainFrame.administrator)
				return;
			if(lbl_Name.isShowing()){
				if(editingRow == -1)
					addNewDealer();
				else
					editDealer();
			}
			//if currently selected row is for any of the existing dealer, redisplay the text
			if(editingRow != -1 && e.getSource() != dNms[editingRow])
				resumeText();
			
			//reset editing row
			editingRow = -1;
			
			//determine the currently editing row
			for(int i=0;i<dNms.length;i++){
				if(e.getSource() == dNms[i]){
					editingRow = i;
					break;
				}
			}
			
			
			//if non-dealer (empty) location is selected
			if(editingRow != -1){
				showDealersEntryFields(editingRow);
			}else if(e.getSource() == dealer){
				hideDealersEntryFields();
				dealer.updateUI();
			}else if(e.getSource() == new_dataD){
				showDealersEntryFields();
			}
		};
		/**
		 * divisible all the entry fields for dealer's information
		 */
		private void hideDealersEntryFields() {
			try{
				//remove all components from the dealer panel
				dealer.remove(lbl_Name);
				dealer.remove(combo_State);
				dealer.remove(combo_Year);
				dealer.remove(combo_Month);
			} catch (NullPointerException e){e.printStackTrace();}
		}
		/**
		 * redisplays the JTextArea with a dealer's information when editing finished
		 */
		private void resumeText() {
			hideDealersEntryFields();
			dNms[editingRow].setVisible(true);
		}
	};
	/**
	 * display entry fields for dealer at the end (i.e. new dealer)
	 */
	public void showDealersEntryFields() {
		//only for the administrator
		if(!MainFrame.administrator)
			return;
		showDealersEntryFields(dNms.length+1);
		dealer.updateUI();
	}
	/**
	 * displays entry fields for any given location x
	 * @param x which dealer to be edited
	 */
	public void showDealersEntryFields(int x) {
		//only for the administrator
		if(!MainFrame.administrator)
			return;
		
		int counter = 0;
		String[] temp = null;
		if(x < dNms.length){
			//get the data of the selected dealer
			dNms[x].setVisible(false);
			temp = Dealers.getDataOf(x);
		}else{
			//create empty data for the new dealer
			temp = new String[4];
			temp[0] = "";
			for(int i=1;i<temp.length;i++)
				temp[i] = "0";
		}
		
		//set all the entry fields with the data of the dealer
		lbl_Name.setText(temp[counter++]);
		dealer.add(lbl_Name, x++);
			combo_State.setSelectedIndex(Integer.parseInt(temp[counter++]));
			dealer.add(combo_State, x++);
		
		combo_Year.setSelectedIndex(Integer.parseInt(temp[counter++]));
		combo_Month.setSelectedIndex(Integer.parseInt(temp[counter++]));
		
		dealer.add(combo_Year, x++);
		dealer.add(combo_Month, x++);
		
	}
	private void addNewDealer() {
		String name = lbl_Name.getText();
		//only for the administrator
		if(name.isEmpty())
			return;
		//dealer's name is always converted to the upper case letters
		name = name.toUpperCase();
		
		//get all the entries
		int state = combo_State.getSelectedIndex();
		int year  = combo_Year.getSelectedIndex();
		int month = combo_Month.getSelectedIndex();
		
		//instantiate new dealer class with the entries
		new Dealers(name, state, year, month);
		
		//update the text areas
		recreateJTextAreas_Dealers();
		
		//update the table
		createTable(MainFrame.index, tabYear.getSelectedIndex(), tabMonth[tabYear.getSelectedIndex()].getSelectedIndex());
	}
	private void editDealer() {
		String name = lbl_Name.getText();
		//show error message if no name has been entered
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(null, "Enter a Name");
			return;
		}
		//the product name is converted to upper case letters
		name = name.toUpperCase();
		
		//get all the entries
		int state = combo_State.getSelectedIndex();
		int year  = combo_Year.getSelectedIndex();
		int month = combo_Month.getSelectedIndex();
		
		String[] original = Dealers.getDataOf(editingRow);
		
		if(name.equalsIgnoreCase(original[0])){
		//only edit the variables of the product if the name has not been changed
			Dealers.changeDataOf(editingRow, name, state, year, month);
			dNms[editingRow].setText(name+": "+Dealers.getStates()[state]+", "+Sales.getYears()[year]+", "+Sales.getMonths()[month]);
		}else{
			//check if the new name is valid
			if(Dealers.search(name) != null){
				//dealer with the name already exists; show error message
				JOptionPane.showMessageDialog(null, "Duplicate Products");
				return;
			}
			//delete the original data
			Dealers.delete(editingRow);
			
			//instantiate new data with the updated variables
			new Dealers(name, state, year, month);
			
			//update the text areas
			recreateJTextAreas_Dealers();
		}
		
		//update the table
		createTable(MainFrame.index, tabYear.getSelectedIndex(), tabMonth[tabYear.getSelectedIndex()].getSelectedIndex());
	}

	private void recreateJTextAreas_Dealers() {
		//remove and reset all the original text areas
		for(int i=0;i<dNms.length;i++){
			dealer.remove(dNms[i]);
			dNms[i].removeMouseListener(dealer_clicked);
			dNms[i] = null;
		}
		dealer.remove(new_dataD);
		
		//create them all again with the updated products data
		Object[][] data = Dealers.getAllData();
		dNms = new JTextArea[data.length];
		for(int i=0;i<data.length;i++){
			String text = data[i][0]+":     ";
			for(int j=1;j<data[i].length-1;j++){
				text += data[i][j]+",  ";
			}
			text += data[i][data[i].length-1].toString().substring(0, 3);
			dNms[i] = new JTextArea(text);
			dNms[i].setOpaque(false);
			dNms[i].setLineWrap(true);
			dNms[i].setWrapStyleWord(true);
			dNms[i].setEditable(false);
			if(MainFrame.administrator)
				dNms[i].addMouseListener(dealer_clicked);
			dNms[i].setMinimumSize(new Dimension(westSize, 20));
			dNms[i].setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
			dNms[i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
			
			dealer.add(dNms[i]);
		}
		dealer.add(new_dataD);
		dealer.updateUI();
	}
}