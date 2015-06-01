package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Users implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static Integer LOGIN = 0, LOGOUT = 1, LOGIN_FAILED = 2, CHANGE_PASS = 3, CHANGE_NAME = 4, FREEZE = 5, ACTIVATE = 6;
	private static Users admin;
	private static Users top;
	private Users left, right;
	
	private String userName;
	private String hashPass;
	private Date   registered;
	
//	private Vector<Date> in;
//	private Vector<Date> out;

	private Vector<Date> record;
	private Vector<Integer>	type;
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private boolean freeze = false;
	
	public Users() {
		try {
			data = new Vector<Users>();
			load();
		} catch (NullPointerException e) {
			System.out.println("No File Exists");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error during loading from the file");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error during converting loaded data into class data");
			e.printStackTrace();
		}
	}
	
	public Users(String user, String hash) throws NullPointerException{
		this.userName = user;
		this.hashPass = hash;
		this.record = new Vector<Date>();
		this.type	= new Vector<Integer>();
		add(this);
		this.registered = new Date();
		save();
	}
	public Users(int n, String user, String hash) throws NullPointerException{
		this.userName = user;
		this.hashPass = hash;
		this.record = new Vector<Date>();
		this.type	= new Vector<Integer>();
		this.registered = new Date();
	}
	

/*
|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
|||||||||||||||||||||||||||||||||||||||||||||||||||ADD|||||||||||||||||||||||||||||||||||||||||||||||||||
|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	
	public static void add(Users New) throws NullPointerException{
		if(top == null){
			//if no item exists in tree
			top = New;
			//add the new item at the top (beginning of tree)
			return;		//break
		}
		Users temp = top;
		//loop until break or return
		while(true){
			int n = New.userName.compareToIgnoreCase(temp.userName);
			//determines the largeness of the name of the new item
			//compared to the name of the pointing item
			if(n < 0){
				//if the name of the new item is smaller
				if(temp.left == null){
					//if the left node does not exist, insert new item there
					temp.left = New;
					return;
				}
				//move the pointer to the left
				temp = temp.left;
			}else if(n > 0){
				//if the name of the new item is larger
				if(temp.right == null){
					//if the right node does not exist, insert new item there
					temp.right = New;
					return;
				}
				//move the pointer to the right
				temp = temp.right;
			}else{
				//if the name of the new item already exists in the tree
				throw null;		//throw an error
			}
		}	//end of while loop
	}
	
/*
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||DELETE||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	
	public static void delete(String name) throws NullPointerException{
		if(top == null)	//this is unlikely to happen, but just in case to avoid the system to fail
			throw null;
		if(top.userName.equals(name)){	//when the parameter matches to the user name of top
			adjustTreeNodes();
			return;
		}
		Users parent = null;
		Users delete = null;
		Users temp1 = null;
		Users temp2 = top;
		while(temp2 != null){
			int n = name.compareToIgnoreCase(temp2.userName);
			if(n < 0){	//if the parameter is smaller than the pointer's user name
				if(temp2.left == null)
					throw null;
				temp1 = temp2;
				temp2 = temp2.left;
			}else if(n > 0){//if the parameter is greater than the pointer's user name
				if(temp2.right == null)
					throw null;
				temp1 = temp2;
				temp2 = temp2.right;
			}else{	//if the parameter is equal to the pointer's user name
				parent = temp1;
				delete = temp2;
				break;
			}
		}
		if(delete == null)	//no item found
			throw null;
		getLargerItemNode(delete);
		if(delete == parent.left)
			adjustTreeNodes(0, parent, delete);
		else
			adjustTreeNodes(1, parent, delete);
	}
	
	private static String original_name;
	private static int counter_left = 0, counter_right = 0;
	/**
	 * returns a number representing which node below the parameter has more items
	 * @param temp the User 
	 * @return 0: if the left node has more items.		1: if the right node has more items
	 */
	private static int getLargerItemNode(Users temp) {
		original_name = temp.userName;
		counter_left  = 0;
		counter_right = 0;
		
		countNumbers(temp);
		if(counter_left >= counter_right)
			return 0;
		else
			return 1;
	}
	private static void countNumbers(Users temp) {
		if(temp.left != null)
			countNumbers(temp.left);
		int n = temp.userName.compareTo(original_name);
		if(n < 0)
			counter_left++;
		else if(n > 0)
			counter_right++;
		if(temp.right != null)
			countNumbers(temp.right);
	}
	private static void adjustTreeNodes() {
		// when top is deleted
		int x = getLargerItemNode(top);
		if(x == 0)
			adjust_Left_Node();
		else
			adjust_Right_Node();
	}
	private static void adjustTreeNodes(int n, Users parent, Users delete) {
		//		when top is deleted
		//n - 0: delete = parent.left
		//n - 1: delete = parent.right
		int x = getLargerItemNode(delete);
		if(x == 0)
			adjust_Left_Node(n, parent, delete);
		else
			adjust_Right_Node(n, parent, delete);
	}

	private static void adjust_Left_Node() {
		if(top.left == null){
			//although should not happen...
			top = top.right;
		}else if(top.left.right == null){
			//when its left node does not have right node
			top.left.right = top.right;
			top = top.left;
		}else{
			Users parent = top.left;
			Users temp = top.left.right;
			//get the maximum of left nodes
			while(temp.right != null){
				parent = parent.right;
				temp   = temp.right;
			}
			if(temp.left == null){
				//if the maximum node does not contain any other nodes
				parent.right = null;
				temp.left  = top.left;
				temp.right = top.right;
				top = temp;
			}else{ //if (temp.left != null){
				parent.right = temp.left;
				temp.left  = top.left;
				temp.right = top.right;
				top = temp;
			}
		}
	}
	private static void adjust_Right_Node() {
		if(top.right == null){
			//although should not happen...
			top = top.left;
		}else if(top.right.left == null){
			//when its right node does not have left node
			top.right.left = top.left;
			top = top.right;
		}else{
			Users parent = top.right;
			Users temp = top.right.left;
			//get the minimum of right nodes
			while(temp.left != null){
				parent = parent.left;
				temp   = temp.left;
			}
			if(temp.right == null){
				//if the minimum node does not contain any other nodes
				parent.left = null;
				temp.right  = top.right;
				temp.left   = top.left;
				top = temp;
			}else{ //if (temp.left != null){
				parent.right = temp.left;
				temp.left 	 = top.left;
				temp.right	 = top.right;
				top = temp;
			}
		}
	}
	
	/** 
	 * @param n	determines where delete locates at.
	 * 			0: delete is left node of parent
	 * 			1: delete is right node of parent
	 * @param parent user that locates above in binary tree.
	 * @param delete user to be deleted.
	 */
	private static void adjust_Left_Node(int n, Users parent, Users delete) {
		/* #1	step 1:	delete.left == null
		 * 		step 2:	parent.(n==0?left:right) = delete.right
		 * 
		 * 				  parent
		 * 					/ \
		 * 				   /   \
		 * 				  /		\
		 * 			   delete 
		 * 				/ \
		 * 			   /   \
		 * 			  /		\
		 *step 1--> null	 R <-- step 2
		 * 					/ \
		 * 				   /   \
		 * 				  /		\
		 *				 K		 T 								 */
		/*
		 * #2	step 1:	check if left node of delete has right node
		 * 				--> delete.left.right == null
		 * 		step 2:	replace delete with delete.left
		 * 				--> parent.(n==0?left:right) = delete.left
		 * 
		 * 				  parent
		 * 					/ \
		 * 				 0 /   \ 1 (n value)
		 * 				  /		\
		 * 			   delete 
		 * 				/ \
		 * 			   /   \
		 * 			  /		\
		 *step 2 --> R		 V
		 * 			/ \	
		 *		   /   \
		 *	 	  /		\
		 *		 K	   null <-- step 1							 */
		/*
		 * #3	step 1:	delete.left.right != null 
		 * 	 	step 2:	loop until no more right
		 * 		step 3:	check if the last node has left node
		 * 		step 4:	replace delete with node found in step 2
		 * 
		 *	 			  parent
		 *	 				/ \
		 *	 			   /   \
		 *	 			  /		\
		 *	 		   delete
		 *	 			/ \
		 *	 		   /   \
		 *	 		  /		\
		 *  		 H		 R
		 *	 		/ \		  
		 *	 	   /   \       
		 *	 	  /		\ 		
		 *		 D		 K <-- step 1	 
		 *		/ \		  \
		 *	   /   \	   \
		 *	  /		\		\
		 *	null	 F	  	 Q <-- step 2, step 4
		 *					/ \
		 *				   /   \
		 *				  /		\
		 *	  step 3 --> M	   null 
		 * 
		 */
		
		if(delete.left == null){	//	#1
			//although should not happen
			if(n == 0)			parent.left = delete.right;
			else if (n == 1)	parent.right = delete.right;
			delete = null;
		}else if(delete.left.right == null){//	#2
			//when its left node does not have right node
			delete.left.right = delete.right;
			if(n == 0)			parent.left  = delete.left;
			else if (n == 1)	parent.right = delete.left;			
		}else{	//	#3
			Users temp1 = delete.left;
			Users temp2 = delete.left.right;
			//step 2: get maximum of left nodes
			while(temp2.right != null){
				temp1 = temp1.right;
				temp2 = temp2.right;
			}
			//	step 3 & step 4
			if(temp2.left == null){
				temp1.right = null;
				temp2.right = delete.right;
				temp2.left  = delete.left;
				if(n == 0)			parent.left  = temp2;
				else if (n == 1)	parent.right = temp2;
				delete = null;
			}else{ //if (temp2.left != null){
				temp1.right = temp2.left;
				temp2.right = delete.right;
				temp2.left  = delete.left;
				if(n == 0)			parent.left  = temp2;
				else if (n == 1)	parent.right = temp2;
				delete = null;
			}
		}
	}
	
	
	
	private static void adjust_Right_Node(int n, Users parent, Users delete) {
		/* #1	step 1:	delete.right == null
		 * 		step 2:	parent.(n==0?left:right) = delete.left
		 * 
		 * 				  parent
		 * 					/ \
		 * 				   /   \
		 * 				  /		\
		 * 					  delete 
		 * 						/ \
		 * 					   /   \
		 * 					  /		\
		 *		  step 2-->  R	   null	 <-- step 1
		 * 					/ \
		 * 				   /   \
		 * 				  /		\
		 *				 K		 T 								 */
		/*
		 * #2	step 1:	check if right node of delete has left node
		 * 				--> delete.right.left == null
		 * 		step 2:	replace delete with delete.right
		 * 				--> parent.(n==0?left:right) = delete.right
		 * 
		 * 				  parent
		 * 					/ \
		 * 				 0 /   \ 1 (n value)
		 * 				  /		\
		 * 					   delete 
		 * 						/ \
		 * 					   /   \
		 * 					  /		\
		 * 					 K		 R <-- step 2
		 * 							/ \	
		 *						   /   \
		 *	 					  /		\
		 *			 step 1 --> null	 V						 */
		/*
		 * #3	step 1:	delete.right.left != null 
		 * 	 	step 2:	loop until no more left
		 * 		step 3:	check if the last node has right node
		 * 		step 4:	replace delete with node found in step 2
		 * 
		 *	 			  parent
		 *	 				/ \
		 *	 			   /   \
		 *	 			  /		\
		 *		 		      delete
		 *	 					/ \
		 *	 				   /   \
		 *	 				  /		\
		 *  				 D		 R
		 *	 						/ \		  
		 *	 					   /   \       
		 *	 					  /		\ 		
		 *			step 1 -->	 K		 V 	 
		 *						/ \		  \
		 *					   /   \	   \
		 *					  /		\		\
		 *step 2, step 4 --> H	  	 M 		null
		 *					/ \
		 *				   /   \
		 *				  /		\
		 *  			null	 J <-- step 3
		 * 
		 */
		if(delete.right == null){//	#1
			//although should not happen
			if(n == 0)				parent.left  = delete.left;
			else if (n == 1)		parent.right = delete.left;
			delete = null;
		}else if(delete.right.left == null){//	#2
			//when its right node does not have left node
			delete.right.left = delete.left;
			if(n == 0)				parent.left  = delete.right;
			else if (n == 1)		parent.right = delete.right;
		}else{// #3
			Users temp1 = delete.right;
			Users temp2 = delete.right.left;
			//step 2: get maximum of right nodes
			while(temp2.left != null){
				temp1 = temp1.left;
				temp2 = temp2.left;
			}
			//	step 3 & step 4
			if(temp2.right == null){
				temp1.left = null;
				temp2.left = delete.left;
				temp2.right  = delete.right;
				if(n == 0)					parent.left  = temp2;
				else if (n == 1)			parent.right = temp2;
				delete = null;
			}else{ //if (temp2.right != null){
				temp1.left = temp2.right;
				temp2.left = delete.left;
				temp2.right  = delete.right;
				if(n == 0)					parent.left  = temp2;
				else if (n == 1)			parent.right = temp2;
				delete = null;
			}
		}
	}

/*
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||SEARCH||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	
	public static Users search(String name) {
		if(top == null)
			//this is unlikely to happen, but just in case to avoid the system to fail
			return null;
		if(top.userName.equals(name))
			//when the parameter matches to the user name of top
			return top;
		
		Users temp = top;
		//loop until break or return
		while(true){
			int n = name.compareToIgnoreCase(temp.userName);
			//n: determines the largeness of the searching name to pointer's user name
			if(n < 0){
				//if the searching name is smaller than the pointer's user name
				if(temp.left == null)
					//if the left node does not exist, no item is found
					return null;
				//otherwise, move the pointer to the left node
				temp = temp.left;
			}else if(n > 0){
				//if the parameter is greater than the pointer's user name
				if(temp.right == null)
					//if the right node does not exist, no item is found
					return null;
				//otherwise, move the pointer to the right node
				temp = temp.right;
			}else{
				//if the parameter is equal to the pointer's user name
				return temp;	//return the user being pointed
			}
		}
	}

/*
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
|||||||||||||||||||||||||||||||||||||||||||||||||||LOAD|||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	/**load users data from a file, create the administrator account otherwise	*/
	@SuppressWarnings("unchecked")
	private static void load() throws NullPointerException, IOException, ClassNotFoundException{
		File f = new File("Res/config.ser");
		if(f.exists()){
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			//read objects as Users in Vector form
			Vector<Users> users = (Vector<Users>) ois.readObject();
			//first one represents the administrator
			admin = users.get(0);
			//second one, if exists, represents the first user in the tree
			top   = users.get(1);
			
			ois.close();
		}else{
			//if user file does not exist
			char[] pass = "Admin$passw0rd".toCharArray();
			//create new user for the administrator with the default user name and password
			admin = new Users(1, "administrator", Password.createHashPassword(pass));
			pass = null;
			//create new user with random user name and default password
			new Users("user 1", Password.createHashPassword("1234abcd".toCharArray()));
		}

	}

/*
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
|||||||||||||||||||||||||||||||||||||||||||||||||||SAVE|||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	/**save the record of users in Vector form into .ser file	*/
	public static void save() {
		try{
			File f = new File("Res/config.ser");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(getNamesToSave());
			
			oos.close();
		} catch (NullPointerException e){
		} catch (IOException e) {
		} catch (Exception e) {
		}

	}

/*
|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
||||||||||||||||||||||||||||||||||||||||||||GETTERS & SETTERS||||||||||||||||||||||||||||||||||||||||||||
|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
*/
	
	private static Vector<Users> data;
	public static Vector<Users> getNamesToSave() {
		data.clear();
		data.add(admin);
		data.add(top);
		if(top != null)
			recursion(top);
		return data;
	}
	public static void recursion(Users temp) {
		if(temp.left != null)
			recursion(temp.left);
		if (temp != null) {
			System.out.println(temp.userName);
		}
		
		if(temp != top)
			data.add(temp);
		
		if(temp.right != null)
			recursion(temp.right);
	}
	public static Vector<Users> getAllNames(){
		data.clear();
		if(top == null)
			return data;
		getName(top);
		return data;
	}
	private static void getName(Users temp) {
		if(temp.left != null)
			getName(temp.left);
		data.add(temp);
		if(temp.right != null)
			getName(temp.right);
	}
	public static Users getAdmin() {
		return admin;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		record.add(new Date());
		type.add(CHANGE_NAME);
		this.userName = userName;
	}
	public String getHashPass() {
		return hashPass;
	}
	public void setHashPass(String hashPass) {
		record.add(new Date());
		type.add(CHANGE_PASS);
		this.hashPass = hashPass;
		save();
	}
	public Date getRegistered() {
		return registered;
	}
	public static DateFormat getDateFormat() {
		return dateFormat;
	}
	public boolean isFreeze() {
		return freeze;
	}
	public void setFreeze(boolean freeze) {
		record.add(new Date());
		if(freeze)
			type.add(FREEZE);
		else
			type.add(ACTIVATE);
		this.freeze = freeze;
		save();
	}
	public Vector<Date> getRecord() {
		return record;
	}
	public Vector<Integer> getType() {
		return type;
	}
	public void login() {
		record.add(new Date());
		type.add(LOGIN);
	}
	public void logout() {
		record.add(new Date());
		type.add(LOGOUT);
		save();
	}
	public void loginFailed() {
		record.add(new Date());
		type.add(LOGIN_FAILED);
		save();
	}
}