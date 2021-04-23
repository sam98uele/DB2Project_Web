package it.polimi.db2.project.utils;

import java.util.regex.Pattern;

import it.polimi.db2.project.exceptions.EmailNotValidException;

import java.util.regex.Matcher;

public class EmailValidator {
	
	/**
	 * Check if the email is valid, instead throws an exception
	 * @param email
	 * @throws EmailNotValidException
	 */
	public static void emailValidator(String email) throws EmailNotValidException {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        if(!m.matches()) throw new EmailNotValidException("Error!! The email inserted is not valid!");
	}
	
}
