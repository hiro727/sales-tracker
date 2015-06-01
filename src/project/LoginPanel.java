package project;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private MainFrame mf;
	
	private int who = 0;
	private int USER = 0, ADMINISTRATOR = 1;
	private static Users user = null;
	
	JLabel img;			//image on the login panel
	JLabel label_I;		//label for "Log in"
	JLabel label_U;		//label for "Enter User Name"
	JLabel label_A;		//label for "Log in Administrator"
	JLabel label_P;		//label for "Enter Password"
	JTextField userName;//textField to enter user name
	JPasswordField pass;//textField to enter password
	JButton btn;		//button to move forward
	
	JLabel warning;		//label for error messages
	
	Font title = new Font(null, Font.BOLD, 20);
	Font f1 = new Font(null, Font.BOLD, 15);
	
	public LoginPanel(MainFrame mf) {
		super();
		setLayout(new BoxLayout(LoginPanel.this, BoxLayout.Y_AXIS));
		
		this.mf = mf;
		//initialize password
		new Password();
		
		label_I = new JLabel("Log in", JTextField.CENTER);
		label_I.setMaximumSize(new Dimension(200, 80));
		label_I.setVerticalAlignment(SwingConstants.CENTER);
		label_I.setFont(title);
		
		try{
			//read image from file
			//Image image = ImageIO.read(MainFrame.classLoader.getResource("image.jpg"));//img.jpg"));
			Image image = ImageIO.read(new File("res/image.jpg"));
			image = Images.makeColorTransparent(image, Color.red);
			ImageIcon ii = new ImageIcon(image);
			img = new JLabel(ii);
			img.setMaximumSize(new Dimension(ii.getIconWidth(), ii.getIconHeight()));
		} catch (Exception e){
			//create default label of 600*400 if image is not found
			img = new JLabel(new ImageIcon());
			img.setMaximumSize(new Dimension(600, 400));
		}
		
		label_U = new JLabel("Enter User Name", JTextField.CENTER);
		label_U.setMaximumSize(new Dimension(300, 50));
		label_U.setVerticalAlignment(SwingConstants.BOTTOM);
		label_U.setFont(f1);
		
		label_A = new JLabel("Log in As Administrator", JTextField.CENTER);
		
		label_P = new JLabel("Enter Password", JTextField.CENTER);
		label_P.setMaximumSize(new Dimension(300, 30));
		label_P.setVerticalAlignment(SwingConstants.BOTTOM);
		label_P.setFont(f1);
		
		userName = new JTextField();
		userName.setMaximumSize(new Dimension(300, 20));
		userName.setFont(f1);
		userName.addActionListener(this);
		
		pass = new JPasswordField();
		pass.setMaximumSize(new Dimension(300, 20));
		pass.setFont(f1);
		pass.addActionListener(this);
		
		btn = new JButton("Log in");
		btn.setMaximumSize(new Dimension(300, 50));
		btn.setFont(f1);
		btn.addActionListener(this);
		
		warning   = new JLabel(" ***Password Field is empty***", JTextField.CENTER);
		warning.setForeground(Color.red);
		warning.setVisible(false);
		
		label_A.setForeground(Color.blue);
		label_A.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_A.addMouseListener(admin);
				
		label_I.setAlignmentX(Component.CENTER_ALIGNMENT);

		img.setAlignmentX	 (Component.CENTER_ALIGNMENT);

		label_U.setAlignmentX(Component.CENTER_ALIGNMENT);

		userName.setAlignmentX	 (Component.CENTER_ALIGNMENT);

		label_A.setAlignmentX(Component.CENTER_ALIGNMENT);

		label_P.setAlignmentX(Component.CENTER_ALIGNMENT);

		pass.setAlignmentX	 (Component.CENTER_ALIGNMENT);

		warning.setAlignmentX(Component.CENTER_ALIGNMENT);

		btn.setAlignmentX	 (Component.CENTER_ALIGNMENT);
		
		add(label_I);
		
		add(img);
		
		add(label_U);
		
		add(userName);
		
		add(label_A);
		
		add(label_P);
		
		add(pass);
		
		add(warning);
		
		JLabel l = new JLabel("");
		l.setMaximumSize(new Dimension(300, 30));
		add(l);
		
		add(btn);
		
	}
	
	public MouseListener admin = new MouseAdapter() {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if(who == USER){
				//to change the user to the administrator
				who = ADMINISTRATOR;
				label_U.setText("Enter Administrator's User Name");
				label_P.setText("Enter Administrator's Password");
				label_A.setText("Log in As User");
			}else{
				//to change the user to users
				who = USER;
				label_U.setText("Enter User Name");
				label_P.setText("Enter Password");
				label_A.setText("Log in As Administrator");
				
			}
		};
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pass || e.getSource() == btn || e.getSource() == userName){
			//check user name and password
			System.out.println("login attempted");
			checkPassword();
		}
		
	}
	
	private void checkPassword() {
		if(userName.getText().isEmpty()){
			//if no name has been entered
			warning.setText(" ***User Name Field is empty***");
			warning.setVisible(true);
			updateUI();
			return;
		}
		if(pass.getPassword().length == 0){
			//if no password has been entered
			warning.setText(" ***Password Field is empty***");
			warning.setVisible(true);
			updateUI();
			return;
		}
		warning.setVisible(false);
		try{
			char[] password = pass.getPassword();
			pass.setText(null);
			if(Password.isValid(who, userName.getText(), password)){
				//if combination of user name and password is valid
				password = null;
				if(user.isFreeze()){
					//show error message if the user is unable to log in
					System.out.println("login failed: unable account");
					warning.setText("<html>Your Account has been frozen<br>Ask Administrator to recover");
					warning.setVisible(true);
					return;
				}
				System.out.println("login succeeded");
				//if the user's status is okay, log in and save the record
				user.login();
				Users.save();
				userName.setText("");
				//start loading data from database
				mf.initialize(who);
				setVisible(false);
			}else{
				System.out.println("login failed: invalid combination");
				//if combination of user name and password is not valid
				password = null;
				if(user != null)
					user.loginFailed();
				user = null;
				userName.setText("");
				//show error message
				warning.setText("Invalid User Name or Password");
				warning.setVisible(true);
				pass.setText("");
			}
		}catch (NullPointerException npe){
			//if access to the user account is denied
			int x = Password.getFailure();
			if(user != Users.getAdmin() && x >= 5){
				if(!user.isFreeze())
				user.setFreeze(true);
				warning.setText("<html>Your Account has been frozen<br>Ask Administrator to recover");
				warning.setVisible(true);
				return;
			}
			//disable entering password for the following minutes
			int time = 0;
			if(x == 5)		time = 1;
			else if(x == 6)	time = 5;
			else if(x == 7)	time = 10;
			else if(x == 8)	time = 30;
			if(time != 0){
				warning.setText("try it again "+time+" minutes later");
				warning.setVisible(true);
			}
		}
		
	}
	public int getWho() {
		return who;
	}
	public static Users getUser() {
		return user;
	}
	public static void setUser(Users user) {
		LoginPanel.user = user;
	}

}