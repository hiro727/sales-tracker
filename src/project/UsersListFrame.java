package project;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class UsersListFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private MainFrame mf;
	private JPanel panel;
	
	private JScrollPane sp;
	private JTable tbl;
	
	/**CellRenderer to align text to center**/
	private DefaultTableCellRenderer renderer_center;
	/**CellRenderer to align text to right**/
	private DefaultTableCellRenderer renderer_right;
	
	JLabel inst;
	
	JPanel btn_panel;
	JPanel add_panel;
	JPanel edit_panel;

	JLabel l_tf;
	JTextField tf;
	
	
	public UsersListFrame(String title, MainFrame mf) {
		super(title);
		this.mf = mf;
		
		setSize(600,600);
		setResizable(false);
		setLocationRelativeTo(mf);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				UsersListFrame.this.mf.enableFrame();
				super.windowClosing(e);
			}
		});
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		inst = new JLabel("");//<html><u>Underline</u>Tab<pre>a</pre></html>");
		inst.setAlignmentX(Component.CENTER_ALIGNMENT);
		inst.setMaximumSize(new Dimension(100, 100));
		panel.add(inst);
				
		createTable();
		panel.add(sp);
		
		createBottonPanel();
		createAddPanel();
		createEntryPanel();
		panel.add(btn_panel);
		
		add(panel);
		
		setVisible(true);
	}
	
	Vector<Users> users;
	private void createTable() {
		users = Users.getAllNames();
		System.out.println(users.size());
		tbl = new JTable(new DefaultTableModel((users.size()>11?new Object[users.size() + 4][3]:new Object[15][3]),new String[]{"User Name", "Registered Date", "Status"})){
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			@Override
			public void changeSelection(int rowIndex, int columnIndex,
					boolean toggle, boolean extend) {
				if(getSelectedRow() != rowIndex)
					changeSelectedData(rowIndex);
				super.changeSelection(rowIndex, columnIndex, toggle, extend);
			}
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if(getSelectedRow() == row)
					return c;
				if(row < 4 || row > users.size() + 3)
					c.setBackground(Color.white);
				else if(users.get(row-4).isFreeze())
					c.setBackground(Color.red);
				else
					c.setBackground(Color.white);
				return c;
			}
		};
		
		renderer_center = new DefaultTableCellRenderer();
		renderer_center.setHorizontalAlignment(SwingConstants.CENTER);
		renderer_right = new DefaultTableCellRenderer();
		renderer_right.setHorizontalAlignment(SwingConstants.RIGHT);
		
		updateTable();
		
		
		
		sp = new JScrollPane(tbl);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setMaximumSize(new Dimension(500, 349));
		sp.setAlignmentX(Component.CENTER_ALIGNMENT);
	}


	protected void changeSelectedData(int n) {
		if(n == 1){
			tf.setText(tbl.getValueAt(1, 0).toString());
			return;
		}else if(n < 4 || n > Users.getAllNames().size() + 3){
			tf.setText("");
			return;
		}
		editing = Users.search(tbl.getValueAt(n, 0).toString());
		if(editing == null){
			JOptionPane.showMessageDialog(this, "<html>Error!:<br>The User doesn't exist");
			return;
		}
		if(!edit_panel.isShowing()){
			showHistory();
			return;
		}
		status.setEnabled(true);
		reset_password.setEnabled(true);
		delete.setEnabled(true);
		edit_panel.remove(search);
		edit_panel.add(ok);
		edit_panel.updateUI();
		l_tf.setText("Enter a Name");
		tf.setText(tbl.getValueAt(n, 0).toString());
		status.setSelected(tbl.getValueAt(n, 2).toString().equals("Active"));
	}
	
	private void showHistory() {
		JFrame f = new JFrame("'"+editing.getUserName()+" 's History");
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setEnabled(true);
				super.windowClosing(e);
			}
		});
		f.setSize(300, 400);
		f.setLocationRelativeTo(this);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		Vector<Date> record  = editing.getRecord();
		Vector<Integer> type = editing.getType();
		
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
			case 2:
				JLabel l2 = new JLabel("log in failed at "+Users.getDateFormat().format(record.get(i)));
				l2.setOpaque(true);
				l2.setForeground(Color.red);
				p.add(l2);
				
				break;
			case 3:
				JLabel l3 = new JLabel("         password changed at "+Users.getDateFormat().format(record.get(i)));
				l3.setOpaque(true);
				l3.setForeground(Color.red);
				p.add(l3);
				break;
			case 4:
				JLabel l4 = new JLabel("         user name changed at "+Users.getDateFormat().format(record.get(i)));
				l4.setOpaque(true);
				l4.setForeground(Color.red);
				p.add(l4);
				break;
			case 5:
				JLabel l5 = new JLabel("         account frozen at "+Users.getDateFormat().format(record.get(i)));
				l5.setOpaque(true);
				l5.setForeground(Color.blue);
				p.add(l5);
				break;
			case 6:
				JLabel l6 = new JLabel("         account recovered at "+Users.getDateFormat().format(record.get(i)));
				l6.setOpaque(true);
				l6.setForeground(Color.blue);
				p.add(l6);
				break;
			}
		}
		JScrollPane scroll = new JScrollPane(p);
		f.add(scroll);
		
		f.setVisible(true);
		setEnabled(false);
	}
	
	JButton add, edit;
	private void createBottonPanel() {
		btn_panel = new JPanel(null);
		btn_panel.setMaximumSize(new Dimension(600, 100));
		
		add = new JButton("Add new user");
		add.setBounds(100, 25, 150, 50);
		btn_panel.add(add);
		
		edit = new JButton("Edit a user");
		edit.setBounds(350, 25, 150, 50);
		btn_panel.add(edit);
		
		add.addActionListener(this);
		edit.addActionListener(this);
		
	}
	
	
	private void createAddPanel() {
		add_panel = new JPanel(null);
		add_panel.setMaximumSize(new Dimension(600, 100));
		
	}
	JButton quit, ok;
	JButton search;
	JButton reset_password, delete;
	JCheckBox status;
	private void createEntryPanel() {
		edit_panel = new JPanel(null);
		edit_panel.setMaximumSize(new Dimension(600, 100));
		
		l_tf = new JLabel("Enter a User Name", JTextField.CENTER);
		l_tf.setVerticalTextPosition(SwingConstants.TOP);
		l_tf.setBounds(25, 25, 200, 25);
		
		tf = new JTextField();
		tf.setBounds(25, 55, 200, 30);
		tf.addActionListener(this);
		
		status = new JCheckBox("Activate");
		status.setBounds(230, 20, 100, 30);
		edit_panel.add(status);
		
		ok = new JButton("OK");
		ok.setBounds(230, 50, 100, 50);
		ok.addActionListener(this);
		
		search = new JButton("Search");
		search.setBounds(230, 50, 100, 50);
		search.addActionListener(this);
				
		reset_password = new JButton("Reset Password");
		reset_password.setBounds(450, 0, 135, 50);
		reset_password.addActionListener(this);
		edit_panel.add(reset_password);
		
		delete = new JButton("<html>Delete<br>Account");
		delete.setBounds(340, 50, 100, 50);
		delete.addActionListener(this);
		edit_panel.add(delete);
		
		quit = new JButton("Quit");
		quit.setBounds(450, 50, 135, 50);
		quit.addActionListener(this);
		
	}
	
	
	public void reset(){
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == add)
			showAddPanel();
		else if(e.getSource() == edit)
			showEntryPanel();
		else if(e.getSource() == quit)
			showBottonPanel();
		else if(e.getSource() == ok){
			if(add_panel.isShowing())
				addNewUser();
			else if(edit_panel.isShowing())
				editUser();
		}else if(e.getSource() == search)
			searchUser(tf.getText());
		else if(e.getSource() == tf)
			checkBehavior();
		else if(e.getSource() == reset_password)
			resetPassword();
		else if(e.getSource() == delete)
			deleteAccount();
	}
	
	
	private void showAddPanel() {
		if(btn_panel.isShowing())
			panel.remove(btn_panel);
		else if(edit_panel.isShowing())
			panel.remove(edit_panel);
		
		l_tf.setText("Enter a User Name");
		tf.setText("");
		add_panel.add(l_tf);
		add_panel.add(tf);
		add_panel.add(ok);
		add_panel.add(quit);
		panel.add(add_panel);
		panel.updateUI();
	}
	
	
	private void showEntryPanel() {
		if(btn_panel.isShowing())
			panel.remove(btn_panel);
		else if(add_panel.isShowing())
			panel.remove(add_panel);

		l_tf.setText("<html>Search for a User or<br>click a row in the table to edit");
		tf.setText("");
		delete.setEnabled(false);
		reset_password.setEnabled(false);
		status.setEnabled(false);
		edit_panel.add(l_tf);
		edit_panel.add(tf);
		edit_panel.add(quit);
		edit_panel.add(search);
		panel.add(edit_panel);
		panel.updateUI();
	}
	
	
	private void showBottonPanel() {
		if(add_panel.isShowing())
			panel.remove(add_panel);
		else if(edit_panel.isShowing())
			panel.remove(edit_panel);
		panel.add(btn_panel);
		panel.updateUI();
	}
	
	private Users editing;
	private void searchUser(String name) {
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(this, "<html>Enter a User Name or<br>Select a row");
			return;
		}
		Users user = Users.search(name);
		if(user == null){
			JOptionPane.showMessageDialog(this, "<html>Error!:<br>The User doesn't exist");
			return;
		}
		editing = user;
		l_tf.setText("Enter a Name");
		tf.setText(name);
		for(int i=0;i<Users.getAllNames().size();i++){
			if(tbl.getValueAt(i + 4, 0).toString().equals(name)){
				tbl.setEditingRow(i + 4);
				break;
			}
		}
		delete.setEnabled(true);
		reset_password.setEnabled(true);
		status.setEnabled(true);
		status.setSelected(!user.isFreeze());
		edit_panel.remove(search);
		edit_panel.add(ok);
		edit_panel.updateUI();
	}
	
	
	private void addNewUser() {
		String name = tf.getText();
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a Name");
			return;
		}
		try{
			new Users(name, Password.createHashPassword("1234abcd".toCharArray()));
			users = Users.getAllNames();
			((DefaultTableModel)tbl.getModel()).setDataVector((users.size()>11?new Object[users.size() + 4][3]:new Object[15][3]),new String[]{"User Name", "Registered Date", "Status"});
			updateTable();
			tf.setText("");
		} catch (NullPointerException e){
			JOptionPane.showMessageDialog(this, "The user already exists");
			return;
		}
	}
	
	
	private void editUser() {
		if(editing == null){
			JOptionPane.showMessageDialog(this, "<html>Error!:<br>No User has been Selected");
			return;
		}
		String name = tf.getText();
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a Name");
			return;
		}
		if(name.equals(editing.getUserName())){
			if(editing.isFreeze() == status.isSelected()){
				editing.setFreeze(!status.isSelected());
				for(int i=0;i<Users.getAllNames().size();i++){
					if(tbl.getValueAt(i + 4, 0).toString().equals(editing.getUserName())){
						tbl.setValueAt((status.isSelected()?"Active":"Freeze"), i + 4, 2);
						break;
					}
				}
			}
		}else{
			//when user name is changed
			try{
				Users temp = Users.search(name);
				if(temp != null)
					throw null;
				int n = Users.getAllNames().size();
				for(int i=0;i<n;i++){
					if(tbl.getValueAt(i + 4, 0).toString().equals(editing.getUserName())){
						tbl.setValueAt(name, i + 4, 0);
						tbl.setValueAt((status.isSelected()?"Active":"Freeze"), i + 4, 2);
						break;
					}
				}
				System.out.println("deleting");
				Users.delete(editing.getUserName());
				System.out.println("successfully deleted");
				editing.setUserName(name);
				if(editing.isFreeze() == status.isSelected())
					editing.setFreeze(!status.isSelected());
				System.out.println("adding");
				Users.add(editing);
				System.out.println("successfully added");
				Users.save();
				
			} catch (NullPointerException e){
				JOptionPane.showMessageDialog(this, "The User Name already exists");
				e.printStackTrace();
			}
		}
	}
	
	
	private void checkBehavior() {
		if(search.isShowing()){
			searchUser(tf.getText());
		}else if(ok.isShowing()){
			if(add_panel.isShowing())
				addNewUser();
			else if(edit_panel.isShowing())
				editUser();
		}
	}
	
	
	private void resetPassword() {
		String name = tf.getText();
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a Name");
			return;
		}
		if(editing == null){
			JOptionPane.showMessageDialog(this, "No User has been selected");
		}
		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset '"+editing.getUserName()+"'s Password?");
		if(result != JOptionPane.OK_OPTION)
			return;
		System.out.println(editing.getUserName());
		System.out.println(editing.getHashPass());
		editing.setHashPass(Password.createHashPassword("1234abcd".toCharArray()));
		JOptionPane.showMessageDialog(this, "Password has successfully been reseted");
	}
	
	
	private void deleteAccount() {
		String name = tf.getText();
		if(name.isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a Name");
			return;
		}
		if(editing == null){
			JOptionPane.showMessageDialog(this, "No User has been selected");
		}
		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '"+editing.getUserName()+"'s account?");
		if(result != JOptionPane.OK_OPTION)
			return;
		Users.delete(editing.getUserName());
		JOptionPane.showMessageDialog(this, "Account has successfully been deleted");
		users = Users.getAllNames();
		((DefaultTableModel)tbl.getModel()).setDataVector((users.size()>11?new Object[users.size() + 4][3]:new Object[15][3]),new String[]{"User Name", "Registered Date", "Status"});
		
		updateTable();
		
	}
	private void updateTable() {
		
		tbl.getTableHeader().setReorderingAllowed(false);
		tbl.setOpaque(false);
//		tbl.enableMultipleRowSelection(false);
		
		tbl.getColumnModel().getColumn(0).setCellRenderer(renderer_right);
		tbl.getColumnModel().getColumn(1).setCellRenderer(renderer_right);
		tbl.getColumnModel().getColumn(2).setCellRenderer(renderer_center);
		tbl.setValueAt("Administrator:     ", 0, 0);
		Users admin = Users.getAdmin();
		tbl.setValueAt(admin.getUserName(), 1, 0);
		tbl.setValueAt(Users.getDateFormat().format(admin.getRegistered()), 1, 1);
		tbl.setValueAt("---", 1, 2);
		
		tbl.setValueAt("Users:     ", 3, 0);
		
		for(int i=0;i<users.size();i++){
			tbl.setValueAt(users.get(i).getUserName(), i+4, 0);
			tbl.setValueAt(Users.getDateFormat().format(users.get(i).getRegistered()), i+4, 1);
			tbl.setValueAt((users.get(i).isFreeze()?"Freeze":"Active"), i+4, 2);
		}
	}
}