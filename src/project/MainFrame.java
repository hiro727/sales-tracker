package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	static Toolkit toolkit = Toolkit.getDefaultToolkit();
	static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	static int width = 1000, height = 700;
	
	static int index = 0;
	
	static boolean administrator;
	static String userName = "";
	
	static int left_p = 0;		//what to display on the left panel
	static int right_p = 0;		//what to display on the right panel
	
	static Font f1 = new Font(null, Font.PLAIN, 12);
	static Font f2 = new Font(null, Font.BOLD, 12);
	
	LoginPanel lp;
	
	JPanel options;			//panel for some basic options
	JButton north;			//show the options panel
	JCheckBox[] check;		//Checkbox for products types
	JCheckBox[] left_pnl;	//Checkbox for which data to show
	JCheckBox[] right_pnl;	//Checkbox for what to view
	String[] inst = new String[]{"Add and/or View","Search"};
	JButton account;		//when user wants to access to the user's account data
	
	//components for login
	JPanel pPanel  = new JPanel();
	JLabel pLabel  = new JLabel(" Enter a password", JTextField.CENTER);
	JLabel pInst   = new JLabel(" ***Password Field is empty***", JTextField.CENTER);
	JLabel pChange = new JLabel("Change Password", JTextField.CENTER);
	JButton logout;			//button to log out
	JButton[] btns1, btns2, btns3;	//confirmation buttons on JOptionPane
	
	JPanel change = new JPanel(new GridLayout(0, 2));	//panel to change password
	JPasswordField pass = new JPasswordField();			//password field
	JPasswordField text1 = new JPasswordField();		//current password
	JPasswordField text2 = new JPasswordField();		//new password
	JPasswordField text3 = new JPasswordField();		//re-enter new password
	JLabel warning = new JLabel();						//error label
	
	View_Data view;				//graphical user interface to view data
	JPanel panel1;				//panel containing data to view
	
	Search_Data panel_search;	//graphical user interface to search data
	
	JScrollPane[] west1;		//scroll pane containing products data
	JScrollPane   west2;		//scroll pane containing dealers data
	
	JSplitPane split;			//split pane to display both 'panel1' and 'panel_search'
	
	public static void main(String[] args) {
		try{
			//initialize the main system
			new MainFrame();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
			//halt the system when there is an error loading system
		}
	}
	
	public MainFrame() {
		super("Sales Database - Sales");
		setSize(width, height);
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		try {//change LookAndFeel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get (key);
				//set default font as it may differ on platforms
				if (value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put (key, f1);
			}
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) { }
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//log out from the current user account if a user closed the window without logging out
				if(LoginPanel.getUser() != null){
					LoginPanel.getUser().logout();
					LoginPanel.setUser(null);
				}
				super.windowClosing(e);
			}
		});
		
		
		//initialize users data
		new Users();
		//create the first panel for login
		lp = new LoginPanel(this);
		add(lp);
		
		setVisible(true);
	}
	public void initialize(int admin){
		System.out.println("initializing main frame");
		if(admin == 1)
			administrator = true;
		else
			administrator = false;
		
		System.out.println("initializing option frame");
		option_Panel();
		OptionFrame();
		
		System.out.println("initializing data from database");
		//load all data from database
		System.out.println("\tsales data");
		new Sales();
		System.out.println("\tproducts data");
		new Products();
		System.out.println("\tdealers data");
		new Dealers();
		new Excel();
		
		System.out.println("initializing panels for view and search");
		//create panels for viewing and for searching
		view = new View_Data();
		panel_search = new Search_Data(this);
		
		panel1 = view.getSales();
		
		System.out.println("initializing panels for products and dealers");
		//create scroll pane that will contain either data of products or dealers on left side of view_panel
		west1 = new JScrollPane[Products.getTypes().length];
		for(int i=0;i<west1.length;i++){
			west1[i]  = new JScrollPane(view.getProduct(i));
			west1[i].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		//create scroll pane that will contain the sales data on right side of the view_panel
		west2  = new JScrollPane(view.getDealer());
		west2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		System.out.println("initializing split pane");
		//create split pane to allow two panels appear on the window
		split  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT){
			private static final long serialVersionUID = 1L;
			@Override
			public void setDividerLocation(int location) {
				
				if(location>309)
					location = 309;
				else if(location<view.getWestSize())
					location = view.getWestSize();
				if(location == getDividerLocation())
					return;
				
				super.setDividerLocation(location);
				if(west2.isShowing()){
					view.changeWidth(location);
				}else{
					view.changeWidth(index, location);
				}
			}
		};
		split.setBottomComponent(panel1);
		split.setContinuousLayout(true);
		add(split, BorderLayout.CENTER);
		panel_search.setVisible(false);
		west1[index].setVisible(false);
		west2.setVisible(false);
		
		System.out.println("initializing password frame");
		//create frames to validate password and to show various options
		PasswordFrame();
		
	}
	/**
	 * mouse listener to be called when user selects other check box.
	 * remove check from the current one and allows only one select for each set of JCheckBox
	 */
	MouseListener mouse_listener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			for (int i = 0; i < check.length; i++) {
				if (e.getSource() == check[i]) {
					//do nothing if the selected box was already 'selected'
					if (check[i].isSelected())
						return;
					
					//check the selected box and remove check from others
					for (int j = 0; j < check.length; j++) {
						check[j].setSelected((j == i ? true : false));
					}
					return;
				}
			}
			for (int i = 0; i < left_pnl.length; i++) {
				if (e.getSource() == left_pnl[i]) {
					//do nothing if the selected box was already 'selected'
					if (left_pnl[i].isSelected())
						return;
					
					//check the selected box and remove check from others
					for (int j = 0; j < left_pnl.length; j++) {
						left_pnl[j].setSelected((j == i ? true : false));
					}
					return;
				}
			}
			for (int i = 0; i < right_pnl.length; i++) {
				if (e.getSource() == right_pnl[i]) {
					//do nothing if the selected box was already 'selected'
					if (right_pnl[i].isSelected())
						return;
					
					//check the selected box and remove check from others
					for (int j = 0; j < right_pnl.length; j++) {
						right_pnl[j].setSelected((j == i ? true : false));
					}
					return;
				}
			}
			
		};
	};
	
	
	private void option_Panel() {
		//panel to show all the detailed options
		options = new JPanel(new GridLayout(0, 1));
		options.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Parameters", 
				TitledBorder.LEFT, TitledBorder.TOP, f1, Color.red));
		options.setPreferredSize(new Dimension(300, 400));
		options.setMaximumSize(new Dimension(300, 400));
		
		//button to display the detailed options
		north = new JButton("Click to Show More Details");
		north.setVerticalAlignment(SwingConstants.TOP);
		north.setBackground(Color.blue);
		north.setForeground(Color.blue);
		north.setFont(f2);
		north.addActionListener(this);
		
		//First Row on the panel (Instruction 1)
		JLabel l1_1 = new JLabel("Choose the Product Type", JTextField.LEFT);
		l1_1.setFont(f1);
		options.add(l1_1);
		
		//Second Row on the panel (Check boxes 1)
		check = new JCheckBox[Products.getTypes().length];
		for(int i=0;i<check.length;i++){
			check[i] = new JCheckBox(Products.getTypes()[i], (i==0?true:false));
			MouseListener[] listeners = check[i].getMouseListeners();
			for (int j = 0; j < listeners.length; j++) {
				check[i].removeMouseListener(listeners[j]);
			}
			check[i].addMouseListener(mouse_listener);
			options.add(check[i]);
		}
		
		//Third Row on the panel (Instruction 2)
		options.add(new JLabel());
		JLabel l2_1 = new JLabel("Choose which data to show", JTextField.LEFT);
		l2_1.setFont(f1);
		options.add(l2_1);
		
		//Fourth Row on the panel (Check boxes 2)
		String[] texts = new String[]{"None", "Products Information", "Dealers Information"};
		left_pnl = new JCheckBox[texts.length];
		for(int i=0;i<left_pnl.length;i++){
			left_pnl[i] = new JCheckBox(texts[i], (i ==0 ? true : false));
			MouseListener[] listeners = left_pnl[i].getMouseListeners();
			for (int j = 0; j < listeners.length; j++) {
				left_pnl[i].removeMouseListener(listeners[j]);
			}
			left_pnl[i].addMouseListener(mouse_listener);
			options.add(left_pnl[i]);
		}
		
		//Fifth Row on the panel (Instruction 3)
		options.add(new JLabel());
		JLabel l3_1 = new JLabel("Choose what you'd like to do", JTextField.LEFT);
		l3_1.setFont(f1);
		options.add(l3_1);
		
		//Sixth Row on the panel (Check boxes #)
		right_pnl = new JCheckBox[inst.length];
		for(int i=0;i<right_pnl.length;i++){
			right_pnl[i] = new JCheckBox(inst[i], (i ==0 ? true : false));
			MouseListener[] listeners = right_pnl[i].getMouseListeners();
			for (int j = 0; j < listeners.length; j++) {
				right_pnl[i].removeMouseListener(listeners[j]);
			}
			right_pnl[i].addMouseListener(mouse_listener);
			right_pnl[i].setFont(f2);
			options.add(right_pnl[i]);
		}
		
		//Seventh Row on the panel (Additional Information)
		options.add(new JLabel());
		account = new JButton("Account Information");
		account.setBackground(Color.red);
		account.setForeground(Color.red);
		account.setFont(f2);
		account.addActionListener(this);
		options.add(account);
		
		
		add(north, BorderLayout.NORTH);
	}
	JLabel usersInfo;
	JPanel enter_pass;
	
	private void PasswordFrame() {
		//panel to verify the user
		pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.Y_AXIS));
		pPanel.add(pChange);
		pPanel.add(new JLabel(" "));
		
		//hyper-linked label to show log-in history
		JLabel history = new JLabel("View Log-in History");
		history.setForeground(Color.blue);
		history.setFont(f1);
		history.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		history.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				//close the JOptionPane to allow focus on other frames
				disposeFrame((Component)e.getSource());
				//panel to be shown
				JPanel p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
				
				//record of times and actions of what the user has done
				Vector<Date> record  = LoginPanel.getUser().getRecord();
				Vector<Integer> type = LoginPanel.getUser().getType();
				
				//display only the login and logout history on the screen
				for(int i=0;i<record.size();i++){
					switch(type.get(i)){
					case 0:
						p.add(new JLabel("log in at "+Users.getDateFormat().format(record.get(i))));
						if(i < record.size()-1 && type.get(i+1) == 0){
							p.add(new JLabel("         log out time: unknown"));
							i++;
						}
						break;
					case 1:
						p.add(new JLabel("         log out at "+Users.getDateFormat().format(record.get(i))));
						
						break;
					}
				}
				
				//scroll pane to allow scrolling of the panel when there is no enough space to show the history
				JScrollPane scroll = new JScrollPane(p);
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scroll.setPreferredSize(new Dimension(350, 350));
				scroll.setMaximumSize(new Dimension(350, 350));
				
				JPanel panel = new JPanel();
				panel.add(scroll);
				panel.setPreferredSize(new Dimension(400, 400));
				panel.setMaximumSize(new Dimension(400, 400));
				JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(pPanel), panel,
						"Login and Logout History", JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.INFORMATION_MESSAGE, null, new JButton[]{}, null);
			}
		});
		pPanel.add(history);
		pPanel.add(new JLabel(" "));
		
		//add hyper-link to display user information only for the administrator
		if(administrator){
			
			enter_pass = new JPanel();
			enter_pass.setLayout(new BoxLayout(enter_pass, BoxLayout.Y_AXIS));
			enter_pass.add(new JLabel("Enter a Password"));
			enter_pass.add(new JLabel(""));
			
			pass.addActionListener(this);
			pass.setActionCommand("enterP");
			enter_pass.add(pass);
			
			pInst.setForeground(Color.red);
			pInst.setVisible(false);
			enter_pass.add(pInst);
			
			usersInfo = new JLabel("View Users Information");
			usersInfo.setForeground(Color.blue);
			usersInfo.setFont(f1);
			usersInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			usersInfo.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					JOptionPane.showOptionDialog(MainFrame.this, enter_pass,
							"Enter a Password", JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE, null, btns1, JOptionPane.OK_OPTION);
					
				}
			});
			pPanel.add(usersInfo);
		}
		pPanel.add(new JLabel(" "));
		
		//button to log out
		logout = new JButton("Log out");
		logout.addActionListener(this);
		pPanel.add(logout);
		
		
		JLabel lbl = new JLabel("Enter Current Password:     ");
		change.add(lbl);
		change.add(text1);
		JLabel lbl2 = new JLabel("Enter New Password:");
		change.add(lbl2);
		change.add(text2);
		JLabel lbl3 = new JLabel("Re-Enter New Password:");
		change.add(lbl3);
		change.add(text3);
		warning = new JLabel();
		warning.setForeground(Color.red);
		change.add(warning);
		warning.setVisible(false);

		text1.addActionListener(this);
		text2.addActionListener(this);
		text3.addActionListener(this);
		text1.setActionCommand("changeP");
		text2.setActionCommand("changeP");
		text3.setActionCommand("changeP");
		
		//hyper-link to change password
		pChange.setForeground(Color.blue);
		pChange.setFont(f1);
		pChange.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pChange.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				text1.setText("");
				text2.setText("");
				text3.setText("");
				warning.setVisible(false);
				JOptionPane.showOptionDialog(null, change, "Update Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, btns2, JOptionPane.OK_OPTION);
			}
		});
		EnteringPasswordFrame();
		ChangingPasswordFrame();
		
	}
	
	//create components of JOptionPane that will be used to enter the password
	private void EnteringPasswordFrame() {
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("CANCEL");
		ok.addActionListener(this);
		ok.setActionCommand("enterP");
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");
		btns1 = new JButton[]{ok, cancel};
	}

	//create components of JOptionPane that will be used to change the password
	private void ChangingPasswordFrame() {
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("CANCEL");
		ok.addActionListener(this);
		ok.setActionCommand("changeP");
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");
		warning.setVisible(false);
		btns2 = new JButton[]{ok, cancel};
	}
	
	//display JOptionPane to enter the password
	@SuppressWarnings("deprecation")
	private void passwordScreen() {
		setCursor(Cursor.WAIT_CURSOR);
		JOptionPane.showOptionDialog(this, pPanel, "Account Information", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, new JButton[]{}, JOptionPane.OK_OPTION);
		//set back the cursor to the original cursor
		setCursor(Cursor.DEFAULT_CURSOR);

	}
	
	//run the password check when the administrator wants to see the users' data
	private void passwordCheck(Component c) {
		//do nothing if the user is not the administrator
		if(!administrator)
			return;
		//show error message if password has not been entered
		if(pass.getPassword().length == 0){
			pInst.setText(" ***Password Field is empty***");
			pInst.setVisible(true);
			return;
		}
		pInst.setVisible(false);
		try{
			//check the administrator's password
			if(Password.isValid(pass.getPassword())){
				pass.setText("");
				disposeFrame(c);
				showUsersInformation();
			}else{
				pass.setText("");
				pInst.setText("Invalid Password");
				pInst.setVisible(true);
				if(Password.getFailure()>=5)
					warningScreen();
			}
		}catch (NullPointerException npe){
			//disable logging in for certain minutes
			int x = Password.getFailure();
			int time = 0;
			if(x == 5)		time = 1;
			else if(x == 6)	time = 5;
			else if(x == 7)	time = 10;
			else if(x == 8)	time = 30;
			pInst.setText("try it again "+time+" minutes later");
			pInst.setVisible(true);
		}
		
	}
	//frame to show the list of users
	UsersListFrame ulf;
	private void showUsersInformation() {
		disposeFrame(enter_pass);
		disposeFrame(pPanel);
		disposeFrame(account);
		disableFrame();
		ulf = new UsersListFrame("Users Information", this);
		
	}

	private void warningScreen() {
		System.out.println("password failed "+Password.getFailure()+" times");
		if(Password.getFailure() == 8){
			//backupAll();
			//deleteAll();
		}
	}
	//run password check and then change password
	private void passwordChange(Component c) {
		warning.setVisible(true);
		if(text1.getPassword().length == 0){
			//when current password has not been entered
			warning.setText("Enter Current Password");
			return;
		}if(text2.getPassword().length == 0){
			//when new password has not been entered
			warning.setText("Enter New Password");
			return;
		}if(text3.getPassword().length == 0){
			//when new password has not been entered
			warning.setText("Re-Enter New Password");
			return;
		}
		if(text1.getPassword().equals(text2.getPassword())){
			//when new passwords do not match
			warning.setText("New Passwords do not match");
			return;
		}
		try{
			boolean success = false;
			//check password of the user from the binary tree or check the administrator's password
			if(lp.getWho() == 0)
				success = Password.changePassword(LoginPanel.getUser(), text1.getPassword(), text2.getPassword());
			else
				success = Password.changePassword(text1.getPassword(), text2.getPassword());
			
			if(success){
				//if the password is correct
				disposeFrame(c);
				warning.setVisible(false);
			}else{
				warning.setText("Invalid Current Password");
				if(Password.getFailure()>=5)
					warningScreen();
			}
		}catch (NullPointerException npe){
			int x = Password.getFailure();
			int time = 0;
			if(x == 5)		time = 1;
			else if(x == 6)	time = 5;
			else if(x == 7)	time = 10;
			else if(x == 8)	time = 30;
			warning.setText("try it again "+time+" minutes later");
			warning.setVisible(true);
		}
	}
	private void disposeFrame(Component source) {
		//following code to dispose only the top JOptionPane is taken from 
		//"http://stackoverflow.com/questions/2730044/closing-a-dialog-
		//created-by-joptionpane-showoptiondialog"
		Window w = SwingUtilities.getWindowAncestor(source);
		if (w != null) {
			w.setVisible(false);
		}
	}
	public void disableFrame(){
		setEnabled(false);
	}
	public void enableFrame(){
		setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == north)
			show_optionFrame();
		else if(e.getSource() == logout)
			logout((Component) e.getSource());
		else if(e.getSource() == account)
			passwordScreen();
		else if(e.getActionCommand().equals("finish_frame"))
			apply_changes_to_Frame((Component) e.getSource());
		else if(e.getActionCommand().equals("cancel_frame"))
			cancel_changes_to_Frame((Component) e.getSource());
		else if(e.getActionCommand().equals("enterP"))
			passwordCheck((Component) e.getSource());
		else if(e.getActionCommand().equals("changeP"))
			passwordChange((Component) e.getSource());
		else if(e.getActionCommand().equals("cancel") || e.getActionCommand().equals("close"))
			disposeFrame((Component) e.getSource());
		
	}
	
	private void OptionFrame() {
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("CANCEL");
		ok.addActionListener(this);
		ok.setActionCommand("finish_frame");
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel_frame");
		btns3 = new JButton[]{ok, cancel};
	}
	private void show_optionFrame() {
		JOptionPane.showOptionDialog(this, options, "Detail Information", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, btns3, JOptionPane.OK_OPTION);
	}
	
	

	private void apply_changes_to_Frame(Component c) {
		//change what to display on the frame when the options frame is closed
		
		//check if product type has been updated
		int 	product  = index;
		boolean p_change = false;
		for (int i = 0; i < check.length; i++) {
			if (check[i].isSelected()){
				System.out.println("type:\t\t"+index+"\t"+i);
				if(index != i)
					p_change = true;
				index = i;
				break;
			}
		}
		
		//check if the item to be shown on the left panel has been updated
//		int		item  = left_p;
		boolean left = false;
		for (int i = 0; i < left_pnl.length; i++) {
			if (left_pnl[i].isSelected()){
				System.out.println("left:\t\t"+left_p+"\t"+i);
				if(left_p != i)
					left = true;
				left_p = i;
				break;
			}
		}
		//check if what to view has been updated
		boolean right = false;
		for (int i = 0; i < right_pnl.length; i++) {
			if (right_pnl[i].isSelected()){
				System.out.println("right:\t\t"+right_p+"\t"+i);
				if(right_p != i)
					right = true;
				right_p = i;
				break;
			}
		}
		
		//change right panel only if the right has been updated
		if (right)
			changePanel();
		
		if (right_p == 0) {System.out.println("current product type: "+product+"\n\tnew product type: "+index);
			//when view panel is being shown
			if (west1[product].isShowing()) {System.out.println("west["+product+"]");
				//if products data has been shown
				if (left)
					//if the left panel is subject to change to dealers data
					show_hideWestPanel(left_p);
				else if (p_change) {System.out.println("product type changed");
					//if the displayed products type is to change to other products type
					switchWestPanel(index);
					view.changeTable(index);
					switchCenterPanel();
				}
			} else if (west2.isShowing()) {
				//if dealers data has been shown
				if (left)
					//if the left panel is to change to products data
					show_hideWestPanel(left_p);
				if (p_change) {
					//if the displayed products type is to change to other products type
					view.changeTable(index);
					switchCenterPanel();
				}
			} else {
				//if nothing has been shown on the left panel
				if (left)
					//if to show either data on the left panel
					show_hideWestPanel(left_p);
				
				if (p_change) {
					//if the displayed products type is to change to other products type
					view.changeTable(index);
					switchCenterPanel();
				}
			}
		}else{
			//when search panel is being shown, do nothing
		}
		//close the frame
		disposeFrame(c);
	}

	private void cancel_changes_to_Frame(Component c) {
		//close the frame
		disposeFrame(c);
	}

	private void logout(Component c) {
		//close the frame
		disposeFrame(c);
		disposeFrame(account);
		//log out from the user account
		LoginPanel.getUser().logout();
		LoginPanel.setUser(null);
		//recover the default settings and remove all components, variables and classes that may leak out some information
		if(administrator)
			ulf = null;
		index = 0;
		left_p = 0;
		right_p = 0;
		//only select the first check boxes
		left_pnl[0].setSelected(true);
		for (int i = 1; i < left_pnl.length; i++)
			left_pnl[i].setSelected(false);

		right_pnl[0].setSelected(true);
		for (int i = 1; i < right_pnl.length; i++)
			right_pnl[i].setSelected(false);
		
		check[0].setSelected(true);
		for (int i = 1; i < check.length; i++)
			check[i].setSelected(false);
		
		remove(options);
		remove(north);
		remove(panel1);
		remove(panel_search);
		remove(split);
		validate();
		
		//reset panels and components
		view   = null;
		panel1 = null;
		panel_search = null;
		options  = null;
		north = null;
		west1  = null;
		west2  = null;
		//reset all data
		reset();
		Products.reset();
		Dealers.reset();
		Sales.reset();
		lp.setVisible(true);
		lp.updateUI();
		lp.requestFocusInWindow();
		repaint();
		//run the garbage collector to delete everything from the bin
		System.gc();
	}

	private void reset() {
		//reset GUI on panels
		pPanel = new JPanel();
		change = new JPanel(new GridLayout(0, 2));
		pChange = new JLabel("Change Password", JTextField.CENTER);
	}

	private void changePanel() {
		//change the displaying panel on the right side
		if(split.isShowing()){
			//show searching panel
			split.setVisible(false);
			add(panel_search, BorderLayout.CENTER);System.out.println(panel_search);
			panel_search.setVisible(true);
			setTitle(getTitle().replaceAll(" - Sales", " - Search"));
		}else{
			//show the viewing panel
			panel_search.setVisible(false);
			split.setVisible(true);
			add(split, BorderLayout.CENTER);
			split.setDividerLocation(view.getWestSize());
			setTitle(getTitle().replaceAll(" - Search", " - Sales"));
		}
	}
	
	
	protected void switchCenterPanel() {
		//change which sales data to show
		panel1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),"Sales - "+Products.getTypes()[index], 
				TitledBorder.LEFT, TitledBorder.TOP, f1, Color.red));
		
	}
	
	
	protected void switchWestPanel(int n) {
		//change which products data to show
		west1[index].setVisible(false);
		west1[n].setVisible(true);
		split.setTopComponent(west1[n]);
		split.setDividerLocation(view.getWestSize());
		view.addTextArea_New(n);
	}
	/**
	 * 
	 * @param n 0: none, 1: products, 2: dealers
	 */
	private void show_hideWestPanel(int n) {
		//update left panel
		if (n == 0) {
			//hide all data
			if (west2.isShowing())
				west2.setVisible(false);
			if (west1[index].isShowing())
				west1[index].setVisible(false);
			split.setTopComponent(null);
		} else if (n == 1) {
			//show/hide products data
			
			//hide dealers data
			if(west2.isShowing()){
				west2.setVisible(false);
			}
			//hide the products data if it already has been shown
			if(west1[index].isShowing()){
				west1[index].setVisible(false);
				split.setTopComponent(null);
			}else{
				//if not, show on the panel
				split.setTopComponent(west1[index]);
				split.setDividerLocation(view.getWestSize());
				
				split.setVisible(true);
				west1[index].setVisible(true);
			}
		} else {
			//show/hide dealers data
			
			//hide any shown products data
			if(west1[index].isShowing()){
				west1[index].setVisible(false);
			}
			//hide the dealers data if it already has been shown
			if(west2.isShowing()){
				west2.setVisible(false);
				split.setTopComponent(null);
			}else{
				//if not, show on the panel
				split.setTopComponent(west2);
				add(split, BorderLayout.CENTER);
				split.setDividerLocation(view.getWestSize());
				//add(west2, BorderLayout.WEST);
				split.setVisible(true);
				west2.setVisible(true);
			}
		}
	}
}