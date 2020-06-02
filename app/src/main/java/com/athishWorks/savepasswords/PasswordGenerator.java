package com.athishWorks.savepasswords;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PasswordGenerator {

    final static public String PASSWORD_LENGTH = "length";
    final static public String SPECIAL_LENGTH = "specialLength";
    final static public String NUMBER_LENGTH = "numberLength";

    final private int DEFAULT_SPECIAL_CHARACTERS = 2;
    final private int DEFAULT_NUMERIC_CHARACTERS = 2;
    
    private int passwordLength;
    private int specialCharactersLength = DEFAULT_SPECIAL_CHARACTERS;
    private int numericCharactersLength = DEFAULT_NUMERIC_CHARACTERS;
    private char[] specialCharacters = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};
    
    // {!, @, #, $, %, ^, &, *, (, )} Special Characters
    // 48-57 Numerics
    // 65-90 Capital Chars
    // 97-122 Small Chars
    
    public PasswordGenerator(int passwordLength) {
        this.passwordLength = passwordLength;
    }
    
    public PasswordGenerator() {
        this.passwordLength = 8;
    }
    
    public boolean setSpecialCharacters(int len) {
        if (len<0 || len>passwordLength) {
            return false;
        }
        this.specialCharactersLength = len;
        return true;
    }

    public boolean setNumericCharacters(int len) {
        if (len<0 || len>passwordLength) {
            return false;
        }
        this.numericCharactersLength = len;
        return true;
    }

    public String createPassword() {
        if (numericCharactersLength + specialCharactersLength > passwordLength) {
            Log.i("Pass", "Exceeded length");
            return null;
        }

        Random random = new Random();
        StringBuilder password = new StringBuilder(passwordLength);

        // add special characters
        for (int i=0; i<specialCharactersLength; i++) {
            password.append(specialCharacters[random.nextInt(specialCharacters.length)]);
        }

        // add number characters
        for (int i=0; i<numericCharactersLength; i++) {
            password.append(random.nextInt(10));
        }

        // add alpha characters
        int remainingCharactersLength = passwordLength - specialCharactersLength - numericCharactersLength;
        for (int i=0; i<remainingCharactersLength; i++) {
            int someRandom = random.nextInt(2);
            char generatedChar;

            if (someRandom==0) {
                // Capital char
                generatedChar = (char) (random.nextInt(26) + 65);
            } else {
                // Small char
                generatedChar = (char) (random.nextInt(26) + 97);
            }

            password.append(generatedChar);
        }

        // shuffle the generated string
        ArrayList<Integer> passwordPointer = new ArrayList<>(passwordLength);
        for (int i=0; i<passwordLength; i++) {
            passwordPointer.add(i);
        }
        Collections.shuffle(passwordPointer);

        Log.i("Pass", "Initial Password " + password.toString());
        StringBuilder result = new StringBuilder(passwordLength);
        for (int i=0; i<passwordLength; i++) {
            result.append(password.charAt(passwordPointer.get(i)));
        }

        Log.i("Pass", "Final Password " + result.toString());
        return result.toString();
    }
    
}
