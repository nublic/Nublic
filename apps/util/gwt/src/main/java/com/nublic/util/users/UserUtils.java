package com.nublic.util.users;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.util.messages.SequenceHelper;


public class UserUtils {
	public static final int MIN_PASSWORD_LENGTH = 5;
	public static final int MAX_USERNAME_LENGTH = 30;
	
	// Methods
	public static boolean checkValidName(String newText) {
		return newText.matches("[a-zA-Z_][a-zA-Z0-9_\\-]*") && newText.length() < MAX_USERNAME_LENGTH;
	}

	public static void checkUserAvailability(String name, CheckUserHandler cuh) {
		CheckUserMessage cum = new CheckUserMessage(name, cuh);
		SequenceHelper.sendJustOne(cum, RequestBuilder.GET);
	}
	
	public static String getSystemName(String realName) {	
		String temp = realName.toLowerCase()
				.replaceAll("[^a-zA-Z0-9_\\-]", "")
				.replaceAll("^[0-9\\-_]*","");
		return temp.substring(0,
				temp.length() > MAX_USERNAME_LENGTH ? MAX_USERNAME_LENGTH : temp.length());

//		System.out.println("a:");
//		System.out.println(realName);
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")); // This seems to do nothing
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase());
//		System.out.println(realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().replaceAll("[^a-zA-Z0-9_]", ""));
//		return realName.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");
//		return Normalizer.normalize(realName, Form.NFKD).replaceAll("[^a-zA-Z0-9_]", "");
	}
	
	public static void addUser(String systemName, String shownName, String password, AddUserHandler auh) {
		AddUserMessage aum = new AddUserMessage(systemName, shownName, password, auh);
		SequenceHelper.sendJustOne(aum, RequestBuilder.PUT);
	}
	
	public static void getUserList(UserListHandler ulh) {
		UserListMessage ulm = new UserListMessage(ulh);
		SequenceHelper.sendJustOne(ulm, RequestBuilder.GET);
	}
}