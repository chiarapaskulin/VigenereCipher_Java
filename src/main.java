import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class main {

    // Alphabet array for consultation
    static ArrayList<String> Alphabet = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

    public static void main(String args[]) {
        ArrayList<String> filePaths = new ArrayList<String>(Arrays.asList("cipher1.txt", "cipher2.txt", "cipher3.txt", "cipher4.txt", "cipher5.txt", "cipher6.txt", "cipher7.txt", "cipher8.txt", "cipher9.txt", "cipher10.txt",
                                                                     "cipher11.txt", "cipher12.txt", "cipher13.txt", "cipher14.txt", "cipher15.txt", "cipher16.txt", "cipher17.txt", "cipher18.txt", "cipher19.txt", "cipher20.txt",
                                                                     "cipher21.txt", "cipher22.txt", "cipher23.txt", "cipher24.txt", "cipher25.txt", "cipher26.txt", "cipher27.txt", "cipher28.txt", "cipher29.txt", "cipher30.txt", "cipher31.txt"));
        //decipherText("PT.txt");
        for (String filePath: filePaths) {
            decipherText(filePath);
        }
    }

    public static void decipherText(String filePath){
        // Stores file text into a StringBuffer for easier manipulation
        StringBuffer fileData = returnFileData(filePath);
        System.out.println("File: " + filePath.replace(".txt", ""));

        // Gets key size and language of the text
        TextInfo textInfo = getKeySize_Language(fileData);

        // If text has no language - is random - inform the user
        if(textInfo.TextLanguage.equals(Languages.RANDOM)){
            System.out.println("\nThe ciphertext is randomized. Cannot decipher it.");
        }else{
            // If text has language - is not random
            String key = findKey(textInfo, fileData);
            System.out.println("Text Key: " + key);
            String decryptedText = decrypt(fileData, key);
            System.out.println("First 100 characters of the text: " + decryptedText + "\n\n");
        }
    }

    public static StringBuffer returnFileData(String filePath){
        // Creates StringBuffer to store file data
        StringBuffer fileData = new StringBuffer();

        // Reads the file and store it in fileData
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader buffReader = new BufferedReader(reader);

            String line;
            while ((line = buffReader.readLine()) != null) {
                fileData.append(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fileData;
    }

    public static TextInfo getKeySize_Language(StringBuffer fileData){
        int maxKeySize = 20;
        int fileLenght = fileData.length();
        double engIC = 0.0667;
        double randomIC = 0.06;

        // Map that stores valid found ICs and their respective key size
        Map<Float, Integer> ics = new HashMap<>();

        // Calculates ICs for key sizes from 1 to 20
        for(int keySize=1; keySize<=maxKeySize; keySize++) {
            // Creates StringBuffer to store the text characters corresponding to the key size [0, 0+keySize, 0+2*keySize,...]
            StringBuffer subFile = new StringBuffer();
            int i = 0;
            while (i < fileLenght) {
                subFile.append(fileData.charAt(i));
                i += keySize;
            }

            // Calculates letters frequency and IC of the subFile
            LettersFrequency lf = calculateLettersFrequency(subFile);
            float ic = getIC(lf.Frequency, fileLenght / keySize);

            // Calculates the distance between the found IC, the language IC and the considered random IC
            double distLanguageIC = Math.abs(engIC - ic);
            double distRandomIC = Math.abs(randomIC - ic);

            // If found IC is closer to language IC, store into the valid ICs map with its key size
            if (distLanguageIC < distRandomIC) {
                ics.put(ic, keySize);
            }
        }

        // Selects the best key size - the one that has the biggest IC
        float bestIC = 0;
        int bestKeySize = maxKeySize;

        for(float ic: ics.keySet()){
            if(bestIC<ic){
                bestIC = ic;
                bestKeySize = ics.get(ic);
            }
        }

        return new TextInfo(bestKeySize, Languages.ENG);
    }

    public static LettersFrequency calculateLettersFrequency(StringBuffer fileData){
        ArrayList<Character> fileLetters = new ArrayList<>();
        ArrayList<Integer> lettersFrequency = new ArrayList<>();

        float fileLenght = fileData.length();

        //Insert frequency of each letter inside array
        for (int i = 0; i < fileLenght; i++) {
            char letter = fileData.charAt(i);

            if(fileLetters.contains(letter)){
                int lettersIndex = fileLetters.indexOf(letter);
                int oldValue = lettersFrequency.get(lettersIndex);
                lettersFrequency.set(lettersIndex, oldValue+1);
            }else{
                fileLetters.add(letter);
                lettersFrequency.add(1);
            }
        }

        // Sorts letters and frequencies by frequency in descending order
        Character charAux;
        Integer intAux;
        boolean sorted = false;

        while (!sorted) {
            sorted = true;
            for (int i = 0; i < lettersFrequency.size()-1; i++) {
                if (lettersFrequency.get(i).compareTo(lettersFrequency.get(i + 1)) < 0) {
                    //Chance places in fileLetters array
                    charAux = fileLetters.get(i);
                    fileLetters.set(i, fileLetters.get(i + 1));
                    fileLetters.set(i + 1, charAux);

                    //Chance places in lettersQuantity array
                    intAux = lettersFrequency.get(i);
                    lettersFrequency.set(i, lettersFrequency.get(i + 1));
                    lettersFrequency.set(i + 1, intAux);

                    sorted = false;
                }
            }
        }

        return new LettersFrequency(fileLetters, lettersFrequency);
    }

    public static float getIC(ArrayList<Integer> lettersQuantity, float fileLenght){
        double sum = 0.0;
        for (int i = 0; i < lettersQuantity.size(); i++) {
            double frequency = (double) lettersQuantity.get(i);
            sum += frequency * (frequency - 1.0);
        }

        float ic = (float) (sum / (fileLenght * (fileLenght - 1.0)));

        return ic;
    }

    public static String findKey(TextInfo textInfo, StringBuffer fileData){
        int keySize = textInfo.KeySize;
        Languages languages = textInfo.TextLanguage;
        String key = "";

        for (int i=0; i<keySize; i++){
            // Calculates letters frequency of the text for every key size [1,20]
            StringBuffer subFile = buildSubFile(fileData, i, keySize);
            LettersFrequency lf = calculateLettersFrequency(subFile);

            // Gets the most frequent char of this subFile and its index
            String textChar = Character.toString(lf.Letters.get(0));
            int indexTextChar = Alphabet.indexOf(textChar);

            String languageChar = null;
            switch (languages){
                case ENG:
                    languageChar = Character.toString('e');
                    break;
                case PT:
                    languageChar = Character.toString('a');
                    break;
            }

            // Gets the index of the language most frequent char
            int indexLanguageChar = Alphabet.indexOf(languageChar);

            // Subtracts the two previous indexes to find the key index
            int indexKey = Math.abs(indexTextChar - indexLanguageChar);

            // Gets the letter corresponding to this index and adds it to the key
            key += Alphabet.get(indexKey);
        }

        return key;
    }

    public static StringBuffer buildSubFile(StringBuffer originalFile, int start, int keySize){
        // Builds a subFile of originalFile starting at i and incrementing keySize
        StringBuffer subFile = new StringBuffer();
        int fileLenght = originalFile.length();
        int i = start;

        while(i<fileLenght){
            subFile.append(originalFile.charAt(i));
            i += keySize;
        }
        return subFile;
    }

    public static String decrypt(StringBuffer fileData, String key) {
        String decryptedText = "";

        // Decrypt the first 100 chars of the fileData using the key parameter
        for (int i = 0, position=0; i < 100; i++, position++) {

            // position keeps the key position being used in the current iteration
            if (position == key.length()) {
                position = 0;
            }

            // Gets the index of the current text and key char
            Character textChar = fileData.charAt(i);
            int indexTextChar = Alphabet.indexOf(Character.toString(textChar));
            Character keyChar = key.charAt(position);
            int indexKeyChar = Alphabet.indexOf(Character.toString(keyChar));

            // Subtracts both values to find the index of the original char
            int indexDecryptedChar = indexTextChar - indexKeyChar;
            // If the result is less than 0, add 26 to make sure it's circular
            if (indexDecryptedChar < 0) {
                indexDecryptedChar = 26 + indexDecryptedChar;
            }

            // Gets the letter corresponding to this index and adds it to the decryptedText variable
            String decryptedChar = Alphabet.get(indexDecryptedChar);
            decryptedText += decryptedChar;
        }

        return decryptedText;
    }
}