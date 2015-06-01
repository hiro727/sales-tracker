package project;

/* 
 * Password Hashing With PBKDF2 (http://crackstation.net/hashing-security.htm).
 * Copyright (c) 2013, Taylor Hornby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
//import java.util.Scanner;
/*
 * PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * www: http://crackstation.net/hashing-security.htm
 */
import java.util.Vector;
public class Password
{
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // The following constants may be changed without breaking existing hashes.
	private static final int SALT_BYTE_SIZE = 24;
	private static final int HASH_BYTE_SIZE = 24;
	private static final int PBKDF2_ITERATIONS = 1000;

	private static final int ITERATION_INDEX = 0;
	private static final int SALT_INDEX = 1;
	private static final int PBKDF2_INDEX = 2;
	/**
	 * Returns a salted PBKDF2 hash of the password.
	 *
	 * @param   password    the password to hash
	 * @return              a salted PBKDF2 hash of the password
	 */
	private static String createHash(char[] password)
		throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		// Generate a random salt
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_BYTE_SIZE];
		random.nextBytes(salt);
		
		// Hash the password
		byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
		// format iterations:salt:hash
		return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);
	}
		
	/**
	 * Validates a password using a hash.
	 *
	 * @param   password        the password to check
	 * @param   correctHash     the hash of the valid password
	 * @return                  true if the password is correct, false if not
	 */
	private static boolean validatePassword(char[] password, String correctHash)
		throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		// Decode the hash into its parameters
		String[] params = correctHash.split(":");
		int iterations = Integer.parseInt(params[ITERATION_INDEX]);
		byte[] salt = fromHex(params[SALT_INDEX]);
		byte[] hash = fromHex(params[PBKDF2_INDEX]);
		// Compute the hash of the provided password, using the same salt, 
		// iteration count, and hash length
		byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
		// Compare the hashes in constant time. The password is correct if
		// both hashes match.
		password = null;
		return slowEquals(hash, testHash);
	}
	
	/**
	 * Compares two byte arrays in length-constant time. This comparison method
	 * is used so that password hashes cannot be extracted from an on-line 
	 * system using a timing attack and then attacked off-line.
	 * 
	 * @param   a       the first byte array
	 * @param   b       the second byte array 
	 * @return          true if both byte arrays are the same, false if not
	 */
	private static boolean slowEquals(byte[] a, byte[] b)
	{
		int diff = a.length ^ b.length;
		for(int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}
	
	/**
	 *  Computes the PBKDF2 hash of a password.
	 *
	 * @param   password    the password to hash.
	 * @param   salt        the salt
	 * @param   iterations  the iteration count (slowness factor)
	 * @param   bytes       the length of the hash to compute in bytes
	 * @return              the PBDKF2 hash of the password
	 */
	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
		throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		return skf.generateSecret(spec).getEncoded();
	}
    
	/**
	 * Converts a string of hexadecimal characters into a byte array.
	 *
	 * @param   hex         the hex string
	 * @return              the hex string decoded into a byte array
	 */
	private static byte[] fromHex(String hex)
	{
		byte[] binary = new byte[hex.length() / 2];
		for(int i = 0; i < binary.length; i++)
		{
			binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
		}
		return binary;
	}
	
	/**
	 * Converts a byte array into a hexadecimal string.
	 *
	 * @param   array       the byte array to convert
	 * @return              a length*2 character string encoding the byte array
	 */
	private static String toHex(byte[] array)
	{
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if(paddingLength > 0)
			return String.format("%0" + paddingLength + "d", 0) + hex;
		else
			return hex;
	}
	
	
	
	private static int failure = 0;
	private static long enteredTime;
	private static Vector<String> names = new Vector<String>();
	
	public static boolean isValid(char[] pass) throws NullPointerException{
		//administrator
		try {
			if(validatePassword(pass, Users.getAdmin().getHashPass())){
				pass = null;
				LoginPanel.setUser(Users.getAdmin());
				return true;
			}else{
				pass = null;
				enteredTime = System.currentTimeMillis();
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isValid(int who, String name, char[] password) throws NullPointerException{
		if(who == 0)
			//user
			return isValid(name, password);
		else{
			//administrator
			Users admin = Users.getAdmin();
			if(admin.getUserName().equals(name)){
				return isValid(password);
			}
			return false;
		}
	}
	public static boolean isValid(String name, char[] password) throws NullPointerException{
		//users
		Users user = Users.search(name);
		LoginPanel.setUser(user);
		if(user == null)
			return false;
		checkAvailability(name);
		try {
			if(validatePassword(password, user.getHashPass())){
				password = null;
				deleteNameFromFailingList(name);
				return true;
			}else{
				names.add(name);
				password = null;
				enteredTime = System.currentTimeMillis();
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static void checkAvailability(String name) throws NullPointerException{
		failure = countFailedTime(name);
		if(failure >= 5){
			if(failure == 5 && System.currentTimeMillis() < enteredTime + 60000)
				throw null;
			else if(failure == 6 && System.currentTimeMillis() < enteredTime + 300000)
				throw null;
			else if(failure == 7 && System.currentTimeMillis() < enteredTime + 600000)
				throw null;
			else if(failure == 8 && System.currentTimeMillis() < enteredTime + 1800000)
				throw null;
		}
	}

	private static int countFailedTime(String name) {
		int n = 0;
		for(int i = 0; i < names.size(); i++)
			if(names.get(i).equals(name))
				n++;
		
		return n;
	}
	private static void deleteNameFromFailingList(String name) {
		for (int i = names.size()-1; i >=0; i--) {
			if(names.get(i).equals(name))
				names.remove(i);
		}
	}
	
	public static int getFailure() {
		return failure;
	}
	
	public static boolean changePassword(Users user, char[] password, char[] password2) throws NullPointerException{
		//for user
		if(user == null)
			return false;
		checkAvailability(user.getUserName());
		try {
			if(validatePassword(password, user.getHashPass())){
				user.setHashPass(createHash(password2));
				password  = null;
				password2 = null;
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean changePassword(char[] password, char[] password2) throws NullPointerException{
		checkAvailability(Users.getAdmin().getUserName());
		//for administrator
		Users admin = Users.getAdmin();
		if(isValid(password)){
			try {
				admin.setHashPass(createHash(password2));
				password  = null;
				password2 = null;
				return true;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String createHashPassword(char[] pass) {
		try {
			return createHash(pass);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

}