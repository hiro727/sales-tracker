package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class Search_Data extends JPanel implements ActionListener, ItemListener{
	private static final long serialVersionUID = 1L;
	
	private MainFrame m;
	
	private GridBagConstraints c = new GridBagConstraints();
	private Color  color  = new Color(190, 200, 255);
	private Border border = BorderFactory.createEtchedBorder(color, Color.black);
	private Font f_small  = new Font(null, Font.BOLD, 8);
	
	private JPanel north;		//panel for periods
	private JPanel west;		//panel for products
	private JPanel east;		//panel for dealers
	private JPanel center;		//panel for search result
	
	//components on north
	private JComboBox period_s, period_e;	//start and end periods
	private JButton custom;					//custom periods search button
	private JFrame frame;					//frame to show custom search methods
	//components on north frame
	private JCheckBox[][] periods;			//combo boxes for custom search
	
	//components on west
	private JTabbedPane[] westPanel;		//different types of selection for each product type
	//components on westPanel[0]-[3]
	private JCheckBox[][]   pAll = new JCheckBox[Products.getTypes().length][];			//check box to select all products of the product type
	private JCheckBox[][][] products = new JCheckBox[Products.getTypes().length][][];	//check boxes to select individual products
	
	//components on east
	private JComboBox dSearch;							//type of selection of dealers (names or locations)
	private JCheckBox[] dAll = new JCheckBox[2];		//check box to select all dealers
	private JPanel[]  eastPanel = new JPanel[2];		//panels containing information of names and dealers of all dealers
	private JPanel    southEastPanel;					//panel containing buttons (OK and CANCEL)
	//components on eastPanel[0] and eastPanel[1]
	private JCheckBox[][] dealers = new JCheckBox[2][];	//check boxes to select dealers individually
	//components on southEastPanel
	private JButton ok;									//reset the precious search
	private JButton cancel;								//start searching
	
	//components on center
	private JComboBox tableType;						//type of the displaying table
	private JButton[] tableOptions = new JButton[2];	//to display in full screen or to save in a MS Excel file
	
	private JPanel result;								//panel containing the result table
	private JToggleButton[] btns = new JToggleButton[Products.getTypes().length];	//button to determine which result is being shown
	private JScrollPane[] scr_south = new JScrollPane[Products.getTypes().length];	//scroll pane containing the result tables
	
	private Search search;								//search class containing the result of the search and all the result tables
	
	public Search_Data(MainFrame m) {
		//initialize GUI of this panel
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Search", 
				TitledBorder.LEFT, TitledBorder.TOP, MainFrame.f1, Color.red));
		
		this.m = m;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateUI();
				super.componentResized(e);
			}
		});
		
		c.fill = GridBagConstraints.BOTH;
		createNorthPanel();
		createWestPanels();
		createEastPanels();
		createCenterPanel();
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth  = 5;
		c.gridheight = 1;
		for(int i=0;i<12;i++){
			c.gridy=i;
			add(new JLabel(""+i, JTextField.CENTER), c);
		}
		c.gridy = 2;
		c.gridwidth  = 1;
		c.gridheight = 12;
		for(int i=0;i<5;i++){
			c.gridx = i;
			add(new JLabel(""+i), c);
		}
		
		north.setMinimumSize(new Dimension(600, MainFrame.height/12));
		west.setMinimumSize(new Dimension(200, MainFrame.height));
		east.setMinimumSize(new Dimension(200, MainFrame.height));
		center.setMinimumSize(new Dimension(600, MainFrame.height/12*11));
	}
	
	private void createNorthPanel() {
		north = new JPanel(new GridLayout(1, 0));
		north.setPreferredSize(new Dimension(600, MainFrame.height/12));
		north.setBorder(BorderFactory.createTitledBorder(border, "Choose Periods"));
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth  = 3;
		c.gridheight = 1;
		
		String[] periods = Sales.getPeriods();
		period_s = new JComboBox(periods);
		period_e = new JComboBox(periods);
		custom  = new JButton("Customize");
		custom.addActionListener(this);
		
		north.add(new JLabel("From: ", JTextField.CENTER));
		north.add(period_s);
		north.add(new JLabel(" To: ", JTextField.CENTER));
		north.add(period_e);
		north.add(new JLabel("", JTextField.CENTER));
		north.add(custom);
		
		add(north, c);
	}
	
	/*
	 * 		OVERVIEW OF WEST PANEL (PRODUCT)
	 * 
	 * 			CAR
	 * 			:
	 * 			:
	 * |---MOTORCYCLE-----------------------|
	 * |  _____ _____________ _______		|
	 * | |Names|Displacements|Strokes|		|
	 * | |--------------------------------| |
	 * | |	 _							  |	|
	 * | |	|_| ALL						  |	|
	 * | |	 _							  |	|
	 * | |	|_| Name 1					  |	|
	 * | |	|_| Name 2					  |	|
	 * | |	|_| Name 3					  |	|
	 * | |		 :						  |	|
	 * | |		 :						  |	|
	 * | |		 :						  |	|
	 * | |--------------------------------| |
	 * |------------------------------------|
	 * 			:
	 * 			:
	 * 			ATV
	 * 
	 */
	
	private void createWestPanels() {
		//initialize the GUI of the west panels
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth  = 1;
		
		west = new JPanel(new GridLayout(4, 1));
		west.setBorder(BorderFactory.createTitledBorder(border, "Choose Product(s)"));
		
		westPanel = new JTabbedPane[Products.getTypes().length];
		for(int i=0;i<westPanel.length;i++){
			westPanel[i] = new JTabbedPane();
			westPanel[i].setPreferredSize(new Dimension(200, MainFrame.height/4));
			westPanel[i].setFont(f_small);
			pAll[i] = new JCheckBox[Products.getDetailNum()[i].length+1];
			products[i] = new JCheckBox[Products.getDetailNum()[i].length+1][];
			west.add(westPanel[i]);
		}
		c.gridheight = 12;
		add(west, c);
		for(int i=0;i<westPanel.length;i++){
			for(int j=0;j<Products.getDetailNum()[i].length+1;j++){
				JPanel p = createEachWestPanel(i, j);
				JScrollPane s = new JScrollPane(p);
				westPanel[i].add(s);
				if(j == 0)
					westPanel[i].setTitleAt(0, "Name");
				else if(Products.getDetailNum()[i][j-1] == 0)
					westPanel[i].setTitleAt(j, "Displacement");
				else if(Products.getDetailNum()[i][j-1] == 1)
					westPanel[i].setTitleAt(j, "Strokes");
				else if(Products.getDetailNum()[i][j-1] == 2)
					westPanel[i].setTitleAt(j, "Horsepower");
			}
		}
	}
	
	
	
	
	private JPanel createEachWestPanel(final int n1, final int n2) {
		//create panels for each tab
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		pAll[n1][n2] = new JCheckBox("ALL", true);
		p.add(pAll[n1][n2]);
		
		//show the corresponding names (either names, displacements, numbers of strokes, or horsepowers)
		String[] names = null;
		
		if(n2 == 0){//names
			names = Products.getAllProducts(n1);
		}else if(Products.getDetailNum()[n1][n2-1] == 0){//displacement
			names = Products.getDisplacement();
		}else if(Products.getDetailNum()[n1][n2-1] == 1){//strokes
			names = Products.getStrokes();
		}else if(Products.getDetailNum()[n1][n2-1] == 2){//horsepower
			names = Products.getHorsepower();
		}
		
		products[n1][n2] = new JCheckBox[names.length];
		//create check boxes for each name
		for(int i=0;i<names.length;i++){
			products[n1][n2][i] = new JCheckBox(names[i]);
			products[n1][n2][i].setEnabled(false);
			p.add(products[n1][n2][i]);
		}
		//add action listener for 'all'
		pAll[n1][n2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pAll[n1][n2].isSelected()){
					//disable selection of all other individual combo boxes
					for(int i=0;i<products[n1][n2].length;i++)
						products[n1][n2][i].setEnabled(false);
				}else{
					//enable selection of all other individual combo boxes
					for(int i=0;i<products[n1][n2].length;i++)
						products[n1][n2][i].setEnabled(true);
				}
			}
		});
		return p;
	}
	

	/*
	 * 	OVERVIEW OF EAST PANEL (DEALERS)
	 * 
	 * |--------------------------------|
	 * |	 -----------------------	|
	 * |	|	 Search by Names	|	|	<--	search_d - combo box
	 * |	 -----------------------	|
	 * |	 _							|
	 * |	|_| ALL						|	<-- selectAll - check box
	 * |--------------------------------|
	 * |	 _							|
	 * |	|_| Name 1					|
	 * |	|_| Name 2					|	<-- dealerNames[] - check boxes
	 * |	|_| Name 3					|	 	   and/or
	 * |	|_| Name 4					|	<-- dealerLocs[] - check boxes
	 * |		 :						|
	 * |		 :						|
	 * |--------------------------------|
	 * |	---------	 ------------	|	<-- OK button
	 * |	|  OK   |	 |  CANCEL  |	|
	 * |	---------	 ------------	|	<-- CANCEL button
	 * |--------------------------------|
	 * 
	 */
	private void createEastPanels() {
		//initialize GUI of the east panel
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth  = 1;
		c.gridheight = 12;
		east = new JPanel(new BorderLayout());
		east.setPreferredSize(new Dimension(200, MainFrame.height));
		east.setBorder(BorderFactory.createTitledBorder(border, "Choose Dealer(s)"));
		
		dSearch = new JComboBox(new String[]{"Search by Names", "Search by Locations"});
		
		for(int i=0;i<eastPanel.length;i++)
			eastPanel[i] = createEachEastPanel(i);
		
		east.add(eastPanel[0], BorderLayout.CENTER);
		//add item listener to the combo box
		dSearch.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					int n = dSearch.getSelectedIndex();
					if(n == 0){
						//show names of dealers when check is removed
						east.remove(eastPanel[1]);
					}else{
						//show names of locations (states) when checked
						east.remove(eastPanel[0]);
					}
					east.validate();
					
					east.add(eastPanel[n], BorderLayout.CENTER);
					east.updateUI();
				}
			}
		});
		
		southEastPanel = new JPanel(new GridLayout(1, 2));
		southEastPanel.setPreferredSize(new Dimension(200, MainFrame.height/12));
		
		ok = new JButton("OK");
		cancel = new JButton("CANCEL");
		
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		southEastPanel.add(ok);
		southEastPanel.add(cancel);
		
		east.add(dSearch, BorderLayout.NORTH);
		east.add(southEastPanel, BorderLayout.SOUTH);
		
		add(east, c);
	}
	
	private JPanel createEachEastPanel(final int n) {
		//create panels of names of dealers and of locations (states)
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		dAll[n] = new JCheckBox("ALL", true);
		p.add(dAll[n]);
		
		String[] data = null;
		//create check boxes of names of dealers if n is 1
		//create check boxes of names of locations if n is 2
		if(n == 0)
			data = Dealers.getAllDealers();
		else// if(n == 1)
			data = Dealers.getStates();
		
		dealers[n] = new JCheckBox[data.length];
		for(int i=0;i<data.length;i++){
			dealers[n][i] = new JCheckBox(data[i]);
			dealers[n][i].setEnabled(false);
			p.add(dealers[n][i]);
		}
		//add action listener to 'all'
		dAll[n].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dAll[n].isSelected()){
					//disable selection of all other individual combo boxes
					for(int i=0;i<dealers[n].length;i++)
						dealers[n][i].setEnabled(false);
				}else{
					//enable selection of all other individual combo boxes
					for(int i=0;i<dealers[n].length;i++)
						dealers[n][i].setEnabled(true);
				}
			}
		});
		return p;
	}
	
	/*
	 * |--------------------------------|
	 * | JCOMBOCOX TO CHOOSE TABLE TYPE	|	<-- tableType - combo box
	 * |--------------------------------|
	 * |  ----------------------------	|
	 * |  |			CARS 			 |	|	<-- 4 panels containing the result tables
	 * |  ----------------------------	|	<-- only 1 of 4 can be expanded at a time
	 * |  |		MOTORCYCLES 		 |	|	<-- result table of the product type is shown when expanded
	 * |  ----------------------------	|
	 * |  |			ATVs 			 |	|
	 * |  ----------------------------	|
	 * |  |							 |	|
	 * |  |		RESULT				 |	|
	 * |  |			TABLES			 |	|
	 * |  |							 |	|
	 * |  |		(WHEN EXPANDED)		 |	|
	 * |  |							 |	|
	 * |  |--------------------------|	|
	 * |  ----------------------------	|
	 * |  |	  OUTBOARD MOTORS 		 |	|
	 * |  ----------------------------	|
	 * |--------------------------------|
	 * | DISPLAY IN 	| SAVE IN		|	<-- tableOptions - buttons
	 * |	FULL SCREEN	|  	EXCEL FILE	|
	 * |--------------------------------|
	 * 
	 */
	
	
	private void createCenterPanel() {
		//initialize the GUI of the center panel
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth  = 3;
		c.gridheight = 11;
		
		center = new JPanel(new BorderLayout());
		center.setPreferredSize(new Dimension(600, MainFrame.height/12*11));
		center.setBorder(BorderFactory.createTitledBorder(border, "Result Tables"));
		JPanel p2 = new JPanel(new GridLayout(1, 0));
		
		//JComboBox to allow user to view different types of result tables
		tableType = new JComboBox(new String[]{
				"total sales for dealers",
				"total sales of products",
				"sales of each product",
				"sales for each dealer"});
		tableType.setEnabled(false);
		tableType.addItemListener(this);
		center.add(tableType, BorderLayout.NORTH);
		
		result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.PAGE_AXIS));
		result.setBackground(Color.white);
		Image down = null;
		Image up   = null;
		try {
			//read images from the files and make the backgroung transparent
			down = ImageIO.read(MainFrame.classLoader.getResource("downward arrow.bmp"));
			up   = ImageIO.read(MainFrame.classLoader.getResource("upward arrow.bmp"));
			down = Images.makeColorTransparent(down, Color.white);
			up   = Images.makeColorTransparent(  up, Color.white);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0;i<btns.length;i++){
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			//JButtons used to expand/collapse searching results
			//set icon the downward arrow when unselected
			btns[i] = new JToggleButton(Products.getTypes()[i], new ImageIcon(down));
			btns[i].setMinimumSize(new Dimension(Short.MAX_VALUE, btns[i].getMaximumSize().height));
			btns[i].setMaximumSize(new Dimension(Short.MAX_VALUE, btns[i].getMaximumSize().height));
			btns[i].setHorizontalTextPosition(SwingConstants.LEFT);
			//set icon when selected to upward arrow
			btns[i].setSelectedIcon(new ImageIcon(up));
			btns[i].addActionListener(this);
			btns[i].setActionCommand("expand/collapse");
			p.add(btns[i]);
			
			//JScrollPanes containing results tables that appear under each expand/collapse JButtons
			scr_south[i] = new JScrollPane();
			p.add(scr_south[i]);
			result.add(p);
			scr_south[i].setVisible(false);
		}
		
		center.add(result, BorderLayout.CENTER);
		
		tableOptions[0] = new JButton("Display Table in Full Screen");
		tableOptions[1] = new JButton("Save in Excel File");
		
		tableOptions[0].setEnabled(false);
		tableOptions[1].setEnabled(false);

		tableOptions[0].addActionListener(this);
		tableOptions[1].addActionListener(this);
		
		p2.add(tableOptions[0]);
		p2.add(tableOptions[1]);
		
		center.add(p2, BorderLayout.SOUTH);
		
		add(center, c);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ok){
			getEntries();
		}else if(e.getSource() == custom){
			createCustomFrame();
		}else if(e.getSource() == cancel){
			deleteAll();
		}else if(e.getSource() == tableOptions[0]){
			displayInFullScreen();
		}else if(e.getSource() == tableOptions[1]){
			showJFileChooser();
		}else if(e.getActionCommand().equals("expand/collapse")){
			for(int i=0;i<btns.length;i++){
				if(e.getSource() == btns[i]){
					expand_collapseResultPanels(i);
					break;
				}
			}
		}
	}
	
	
	
	
	private void expand_collapseResultPanels(int n) {
		if(btns[n].isSelected()){//expand
			for(int i=0;i<scr_south.length;i++){
				//unselect all btns and collapse all scr_south
				btns[i].setSelected(false);
				scr_south[i].setVisible(false);
			}
			//select the right btns and expand the right scr_south
			btns[n].setSelected(true);
			scr_south[n].setVisible(true);
			int x = tableType.getSelectedIndex();
			if(x == 0){
				//check if component is not null
				if(scr_south[n].getViewport().getComponentCount() != 0)
					//check if the component is not JLabel
					if(scr_south[n].getViewport().getComponent(0).getClass() != JLabel.class){
						//enable tableOptions (JButtons)
						for(int i=0;i<tableOptions.length;i++)
							tableOptions[i].setEnabled(true);
						//set row header for the table to dealer names
						scr_south[n].setRowHeaderView(search.getList(0));
					}
			}else if(x == 1){
				//check if component is not null
				if(scr_south[n].getViewport().getComponentCount() != 0)
					//check if the component is not JLabel
					if(scr_south[n].getViewport().getComponent(0).getClass() != JLabel.class){
						//enable tableOptions (JButtons)
						for(int i=0;i<tableOptions.length;i++)
							tableOptions[i].setEnabled(true);
						//set row header for the table to dealer names
						scr_south[n].setRowHeaderView(search.getList(n+1));
					}
			}else if(x == 2){
				try{
					//add row header to table in JTabbedPane
					search.addRowHeaderToTable2(n);
				} catch(NullPointerException e){
					//if JTabbedPane is null display a message
					noAvailableData(n);
				}
			}else{//if(x == 3){
				
				//enable tableOptions (JButtons)
				for(int i=0;i<tableOptions.length;i++)
					tableOptions[i].setEnabled(true);
				try{
					//add row header to table in JTabbedPane
					search.addRowHeaderToTable3(n);
				} catch(NullPointerException e){
					//if JTabbedPane is null display a message
					noAvailableData(n);
				}
			}
		}else{//collapse
			scr_south[n].setVisible(false);
			for(int i=0;i<tableOptions.length;i++)
				tableOptions[i].setEnabled(false);
		}
		result.updateUI();
	}
	
	
	private void getEntries() {
		int[] period = null;
		int s = 0;
		int e = 0;
		//if periods are selected by JComboBoxes
		if(period_s.isEnabled()){
			s = period_s.getSelectedIndex();
			e = period_e.getSelectedIndex();
			//if start is greater than end, flip
			if(s > e){
				int temp = e;
				e = s;
				s = temp;
				period_s.setSelectedIndex(s);
				period_e.setSelectedIndex(e);
			}
		}else{//if periods are customized
			period = getPeriods();
		}
		String[][] product = new String[Products.getTypes().length][];
		for(int i=0;i<product.length;i++){
			int n = westPanel[i].getSelectedIndex();
			//get products names if 'All' is not selected
			if(!pAll[i][n].isSelected()){
				product[i] = getProducts(i, n);
			}//else leave product[i] null
		}
		String[] dealer = null;
		int n = dSearch.getSelectedIndex();
		//get selected dealer names if 'All' is not selected
		if(!dAll[n].isSelected()){
			dealer = getDealers(n);
		}
		//send in retrieved values to search class
		//to different constructors depending on entries
		if(period == null){
			if(dealer == null){
				search = new Search(s, e, product);
			}else{//(dealer != null{
				search = new Search(s, e, dealer, product);
			}
		}else{//if(period != null){
			if(dealer == null){
				search = new Search(period, product);
			}else{//(dealer != null){
				search = new Search(period, dealer, product);
			}
		}
		createTables();
	}
	
	private void createCustomFrame(){
		//allow focus only on custom frame
		m.disableFrame();
		//define custom frame if null
		if(frame == null){
			frame = new JFrame("Custom Periods Selection");
			frame.setSize(400, 400);
			frame.setResizable(false);
			//re-enable main frame when custom frame is closed
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					m.enableFrame();
					super.windowClosing(e);
				}
			});
			
			frame.setLayout(new BorderLayout());
			JPanel p = new JPanel(new GridLayout(13, Sales.getYears().length));
			JScrollPane s = new JScrollPane(p);
			frame.add(s, BorderLayout.CENTER);
			
			periods = new JCheckBox[Sales.getYears().length][12];
			//add column header (years)
			for(int i=0;i<Sales.getYears().length;i++){
				p.add(new JLabel(Sales.getYears()[i]));
			}
			//add JCheckBoxes under column headings
			for(int i=0;i<12;i++){
				for(int j=0;j<Sales.getYears().length;j++){
					periods[j][i] = new JCheckBox(Sales.getMonths()[i].substring(0, 3));
					p.add(periods[j][i]);
				}
			}
			//JPanel containing 2 JButtons (OK & CANCEL)
			JPanel action = new JPanel();
			action.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			action.setLayout(new BoxLayout(action, BoxLayout.LINE_AXIS));
			JButton ok = new JButton("OK");
			JButton cancel = new JButton("CANCEL");
			action.add(cancel);
			action.add(ok);
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//re-enable main frame and disable 2 JComboBoxes for periods
					m.enableFrame();
					frame.setVisible(false);
					period_s.setEnabled(false);
					period_e.setEnabled(false);
				}
			});
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//re-enable 2 JComboBoxes for periods on main frame
					//and reset selected JChekBoxes
					period_s.setEnabled(true);
					period_e.setEnabled(true);
					for(int i=0;i<periods.length;i++){
						for(int j=0;j<periods[i].length;j++)
							periods[i][j].setSelected(false);
					}
				}
			});
			frame.add(action, BorderLayout.SOUTH);
		}
		frame.setVisible(true);
		frame.setLocationRelativeTo(this);
	}
	
	private int[] getPeriods(){
		int counter = 0;
		//count how many JCheckBoxes are selected
		for(int i=0;i<Sales.getYears().length;i++){
			for(int j=0;j<12;j++){
				if(periods[i][j].isSelected())
					counter++;
			}
		}
		//create an array based on the number selected
		int[] data = new int[counter];
		counter = 0;
		//gets the order number of the selected dates
		for(int i=0;i<Sales.getYears().length;i++){
			for(int j=0;j<12;j++){
				if(periods[i][j].isSelected())
					data[counter++] = i*13+j;
				}
		}
		return data;
	}
	
	private String[] getProducts(int i, int n){
		int counter = 0;
		//count how many products are selected
		for(int j=0;j<products[i][n].length;j++){
			if(products[i][n][j].isSelected())
				counter++;
		}
		//if n is 0 (selected by names) return texts of selected JCheckBoxes
		if(n == 0){
			String[] data = new String[counter];
			counter = 0;
			for(int j=0;j<products[i][n].length;j++){
				if(products[i][n][j].isSelected())
					data[counter++] = products[i][n][j].getText();
			}
			return data;
		}else{
			//if selected by non-name determinants send the numbers of selected JCheckBoxes
			//into Products class and return names obtained from it
			int[] data = new int[counter];
			counter = 0;
			for(int j=0;j<products[i][n].length;j++){
				if(products[i][n][j].isSelected())
					data[counter++] = j;
			}
			return Products.getNamesFromDetails(i, n-1, data);
		}
	}
	
	private String[] getDealers(int n){
		int counter = 0;
		//count how many dealers are selected
		for(int i=0;i<dealers[n].length;i++){
			if(dealers[n][i].isSelected())
				counter++;
		}
		
		if(n == 0){
			//if user is searching by names, return texts
			String[] data = new String[counter];
			counter = 0;
			for(int i=0;i<dealers[n].length;i++){
				if(dealers[n][i].isSelected())
					data[counter++] = dealers[n][i].getText();
			}
			return data;
		}else{//if(n == 1){
			//if user is searching by locations (states),
			//get dealers names by sending the location numbers into Dealers class
			int[] data = new int[counter];
			counter = 0;
			for(int i=0;i<dealers[n].length;i++){
				if(dealers[n][i].isSelected())
					data[counter++] = i;
			}
			return Dealers.getNamesFromStates(data);
		}
	}
	
	private void createTables(){
		tableType.setEnabled(true);
		int n = tableType.getSelectedIndex();
		int x = -1;
		for(int i=0;i<btns.length;i++)
			if(btns[i].isSelected()){
				x = i;
				break;
			}
		if(n == 0){
			//add table0s on each JScrollPanes
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable0(i) == null || search.getTable0(i).getRowCount() == 0 || search.getTable0(i).getColumnCount() == 0)
					//when there is no data
					noAvailableData(i);
				else{
					scr_south[i].setViewportView(search.getTable0(i));
				}
			}
			//add row header to JScrollPane if it is displayed
			if(x != -1)		scr_south[x].setRowHeaderView(search.getList(0));
		}else if(n == 1){
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable1(i) == null || search.getTable1(i).getRowCount() == 0 || search.getTable1(i).getColumnCount() == 0)
					//when there is no data
					noAvailableData(i);
				else{
					scr_south[i].setViewportView(search.getTable1(i));
				}
			}
			//add row header to JScrollPane if it is displayed
			if(x != -1)		scr_south[x].setRowHeaderView(search.getList(x+1));
		}else if(n == 2){
			scr_south[x].setViewportView(search.getTable2(x));
			//add row header to JScrollPane if it is displayed
			if(x != -1)		search.addRowHeaderToTable2(x+1);
		}else{// if(n == 3){
			//add row header to JScrollPane if it is displayed
			scr_south[x].setViewportView(search.getTable2(x));
			//if(x != -1)		search.addRowHeaderToTable3(x+1);
		}
		if(btns[0].isSelected())
			btns[0].doClick();
		btns[0].doClick();
		
	}
	private void changeDisplayingTables(){
		int n = tableType.getSelectedIndex();
		for(int i=0;i<tableOptions.length;i++)
			tableOptions[i].setEnabled(true);
		int x = 0;
		for(int i=0;i<btns.length;i++)
			if(btns[i].isSelected()){
				x = i;
				break;
			}
		if(n == 0){
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable0(i) == null || search.getTable0(i).getRowCount() == 0 || search.getTable0(i).getColumnCount() == 0)
					noAvailableData(i);
				else{
					//add table0[i] on JScrollPane
					scr_south[i].setViewportView(search.getTable0(i));
					scr_south[i].setRowHeaderView(search.getList(0));
				}
			}
		}else if(n == 1){
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable1(i) == null || search.getTable1(i).getRowCount() == 0 || search.getTable1(i).getColumnCount() == 0)
					noAvailableData(i);
				else{
					scr_south[i].setViewportView(search.getTable1(i));
					scr_south[i].setRowHeaderView(search.getList(x+1));
				}
			}
		}else if(n == 2){
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable2(i) == null){
					noAvailableData(i);
				}else{
					scr_south[i].setViewportView(search.getTable2(i));
					scr_south[i].setRowHeaderView(search.getList(2));
				}
			}
		}else{//if(n == 3){
			for(int i=0;i<scr_south.length;i++){
				if(search.getTable3(i) == null)
					noAvailableData(i);
				else{
					scr_south[i].setViewportView(search.getTable3(i));
					scr_south[i].setRowHeaderView(search.getList(0));
				}
			}
		}
	}
		
	private void noAvailableData(int n){
		JLabel label = new JLabel("NO DATA IS AVAILABLE", JTextField.CENTER);
		label.setOpaque(true);
		//reset row header
		scr_south[n].setRowHeaderView(null);
		scr_south[n].setViewportView(label);
		//disable JButtons
		for(int i=0;i<tableOptions.length;i++)
			tableOptions[i].setEnabled(false);
	}
	
	private void deleteAll() {
		//reset  result tables and search class
		for(int i=0;i<scr_south.length;i++){
		scr_south[i].setRowHeaderView(null);
		scr_south[i].updateUI();
		}
		for(int i=0;i<tableOptions.length;i++)
			tableOptions[i].setEnabled(false);
		search = null;
		System.gc();
	}
	
	/**display currently displaying JTable in new screen sized JFrame*/
	private void displayInFullScreen() {
		JFrame frame = new JFrame("Search Reseult Table");
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		int n1 = -1;
		for(int i=0;i<btns.length;i++)
			if(btns[i].isSelected()){
				n1 = i;
				break;
			}
		int n = tableType.getSelectedIndex();
		//create a clone of currently showing JTable
		JScrollPane s = null;
		if(n == 0){
			s = new JScrollPane(search.getCloneOfTable0(n1));
			s.setRowHeaderView(search.getCloneOfList(0));
		}else if(n == 1){
			s = new JScrollPane(search.getCloneOfTable1(n1));
			s.setRowHeaderView(search.getCloneOfList(n1+1));
		}else if(n == 2){
			s = new JScrollPane(search.getCloneOfTable2(n1));
			s.setRowHeaderView(search.getCloneOfList(n1+1));
		}else if(n == 3){
			s = new JScrollPane(search.getCloneOfTable3(n1));
			s.setRowHeaderView(search.getCloneOfList(0));
		}
		//create a clone of currently used row header
		frame.add(s);
		frame.setVisible(true);
	}
	
	private void showJFileChooser() {
		//show JOptoinPane to check if user wants to save the table
		String[] options = new String[]{"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(m, "Would you like to save "+
		(tableType.getSelectedIndex()<2?"this Table":"this set of Tables")+"?", "Confirmation", 
		JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(result!=JOptionPane.OK_OPTION)
			return;
		int n1 = -1;
		for(int i=0;i<btns.length;i++)
			if(btns[i].isSelected()){
				n1 = i;
				break;
			}
		if(n1 == -1)
			return;
		//show custom JFileChooser to get destination path
		String path = chooseFile();
		Excel.save(path, "Table", scr_south[n1]);
	}
	
	BasicFileChooserUI ui;
	/**
	 * uses JFileChooser to have the file path determined by the user
	 * For convenience, LookAndFeel is adjusted until fileChooser is closed
	 * @return absolute path of file
	 */
	@SuppressWarnings("deprecation")
	private String chooseFile(){
		//changing LookAndFeel may take some time and change the cursor to WAIT_CURSOR until JFileChooser is displayed
		m.setCursor(Cursor.WAIT_CURSOR);
		//save default lookAndFeel
		LookAndFeel initial = UIManager.getLookAndFeel();
		try {//change LookAndFeel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		//create custom JFileChooser that allows user to enter the file name 
		//and browse to the saving destination
		//saving into excel file cannot be initiated if entered name is empty or invalid
		final JFileChooser fc = new JFileChooser(){
			private static final long serialVersionUID = 1L;
			@Override 
			public void approveSelection(){
				String name = getSelectedFile().getAbsolutePath();
				//super method can only be called if valid name has been entered
				if(name.equals(getSelectedFile().getParent())){
					JOptionPane.showMessageDialog(this, "Enter a file name");
				}else{
					if(!fileNameIsValid(name))
						JOptionPane.showMessageDialog(this, "Invalid Character");
					else{
						super.approveSelection();
					}
				}
			}
			private boolean fileNameIsValid(String name) {
				try {
					if(name.equals(new File(name).getCanonicalFile().getAbsolutePath()))
						return true;
					return false;
				} catch (Exception e) {
					return false;
				}
			}
		};
		//only shows directories
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//clear the text when level is changed or displaying panel is changed
		//as the absolute path would normally be entered in the textField
		fc.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				ui.setFileName("");
			}
		});
		ui = (BasicFileChooserUI)fc.getUI();
		ui.setFileName("");
		//set back the cursor to the original cursor
		m.setCursor(Cursor.DEFAULT_CURSOR);
		String pathName = "";
		int result = fc.showSaveDialog(m);
		if(result == JFileChooser.APPROVE_OPTION){
			pathName = fc.getSelectedFile().getAbsolutePath();
		}
		try {
			UIManager.setLookAndFeel(initial);	//reset lookAndFeel
		} catch (UnsupportedLookAndFeelException e) {}
		//extension code for Excel files
		if(pathName.endsWith(".xls"))
			return pathName;
		return pathName+".xls";
	}
	
	public JPanel getPanel() {
		return this;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			if(e.getSource() == tableType){
				for(int i=0;i<scr_south.length;i++)
					scr_south[i].setRowHeaderView(null);
				changeDisplayingTables();
			}
		}
	}
}