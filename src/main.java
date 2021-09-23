import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class main {
    public static void main(String args[]) {
        ArrayList<String> filePaths = new ArrayList<String>(Arrays.asList("cipher1.txt", "cipher2.txt", "cipher3.txt", "cipher4.txt", "cipher5.txt", "cipher6.txt", "cipher7.txt", "cipher8.txt", "cipher9.txt", "cipher10.txt",
                                                                          "cipher11.txt", "cipher12.txt", "cipher13.txt", "cipher14.txt", "cipher15.txt", "cipher16.txt", "cipher17.txt", "cipher18.txt", "cipher19.txt", "cipher20.txt",
                                                                          "cipher21.txt", "cipher22.txt", "cipher23.txt", "cipher24.txt", "cipher25.txt", "cipher26.txt", "cipher27.txt", "cipher28.txt", "cipher29.txt", "cipher30.txt", "cipher31.txt"));

        for (String filePath: filePaths){
            decipherText(filePath);
        }
    }

    public static void decipherText(String filePath){
        StringBuffer fileData = returnFileData(filePath);
        TextInfo textInfo = findTextInfo(fileData);
        String key = findKey(textInfo, fileData);
        if(textInfo.TextLanguage.equals(Languages.RANDOM)){
            System.out.println("The ciphertext is randomized. Cannot decipher it.");
        }else{
            System.out.println(key);
        }
    }

    public static StringBuffer returnFileData(String filePath){
        // Cria o Map e um StringBuffer
        StringBuffer fileData = new StringBuffer();

        // Lê o arquivo e armazena em um StringBuffer
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader buffReader = new BufferedReader(reader);

            String line = null;
            while ((line = buffReader.readLine()) != null) {
                fileData.append(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fileData;
    }

    public static TextInfo findTextInfo(StringBuffer fileData){
        int maxKeySize = 20;
        float ic = -1;
        int fileLenght = fileData.length();
        TextInfo textInfo;

        for(int keySize=1; keySize<=maxKeySize; keySize++){
            for(int i=0; i<keySize; i++){
                StringBuffer subFile = buildSubFile(fileData, i, keySize);
                LettersFrequency lf = calculateLettersFrequency(subFile);
                ic = getIC(lf.Frequency, fileLenght/keySize);
                //System.out.println("Key Size: " + keySize + "| IC: "+ ic);
                Languages textLanguage = getLanguage(ic);
                if(textLanguage != Languages.RANDOM) {
                    System.out.println("Key size: " + keySize + "| Language: "+ textLanguage);
                    return new TextInfo(keySize, textLanguage);
                }
            }
        }

        return new TextInfo(-1, Languages.RANDOM);
    }

    public static StringBuffer buildSubFile(StringBuffer originalFile, int start, int keySize){
        StringBuffer subFile = new StringBuffer();
        int fileLenght = originalFile.length();
        int i = start;

        while(i<fileLenght){
            subFile.append(originalFile.charAt(i));
            i += keySize;
        }
        return subFile;
    }

    public static LettersFrequency calculateLettersFrequency(StringBuffer fileData){
        ArrayList<Character> fileLetters = new ArrayList<>();
        ArrayList<Integer> lettersFrequency = new ArrayList<>();

        float fileLenght = fileData.length();

        //Insert frequency of each letter inside Array
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

        //System.out.println("Non-sorted values: \n");
        //printArrays(fileLetters, lettersFrequency);

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

        //System.out.println("\nSorted values: \n");
        //printArrays(fileLetters, lettersFrequency);

        return new LettersFrequency(fileLetters, lettersFrequency);
    }

    public static void printArrays(ArrayList<Character> fileLetters, ArrayList<Integer> lettersQuantity){
        for (int i = 0; i < fileLetters.size(); i++) {
            System.out.print(fileLetters.get(i) + " = " + lettersQuantity.get(i) + "\n");
        }
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

    public static Languages getLanguage(float ic){

        double icPT = 0.0745;
        double icPTmin = 0.0665;
        double icPTmax = 0.0825;

        double icENG = 0.0667;
        double icENGmin = 0.0600;
        double icENGmax = 0.0700;

        double i = ic - icENG;
        if(i >= -0.008 && i <= 0.008){
            return Languages.ENG;
        } else {
            i = ic - icPT;
            if (i >= -0.008 && i <= 0.008) {
                return Languages.ENG;
            }
        }
        return Languages.RANDOM;
    }

    public static String findKey(TextInfo textInfo, StringBuffer fileData){
        ArrayList<String> alphabet = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

        int keySize = textInfo.KeySize;
        Languages languages = textInfo.TextLanguage;
        String key = "";

        for (int i=0; i<keySize; i++){
            StringBuffer subFile = buildSubFile(fileData, i, keySize);
            LettersFrequency lf = calculateLettersFrequency(subFile);
            String textChar = Character.toString(lf.Letters.get(0));
            int indexTextChar = alphabet.indexOf(textChar);

            String languageChar = null;
            switch (languages){
                case ENG:
                    languageChar = Character.toString('e');
                    break;
                case PT:
                    languageChar = Character.toString('e');
                    break;
            }

            int indexLanguageChar = alphabet.indexOf(languageChar);
            int indexKey = Math.abs(indexTextChar - indexLanguageChar);

            key += alphabet.get(indexKey);
        }

        return key;
    }
}