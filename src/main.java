import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class main {
    public static void main(String args[]) throws IOException {
        // Cria o Map e um StringBuffer
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        StringBuffer string = new StringBuffer();

        // Lê o arquivo e armazena em um StringBuffer
        try {
            FileReader reader = new FileReader("TextoClaro.txt");
            BufferedReader leitor = new BufferedReader(reader);

            String linha = null;
            while ((linha = leitor.readLine()) != null) {
                string.append(linha);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Armazena a quantidade de cada letra no Map
        float qtTotal = string.length();

        for (int i = 0; i < qtTotal; i++) {
            if (map.get(string.charAt(i)) == null) {
                map.put(string.charAt(i), 1);
            } else {
                map.put(string.charAt(i), map.get(string.charAt(i)) + 1);
            }
        }

        // Imprime a quantidade de cada letra
        Object[] a = map.keySet().toArray();
        Iterator i = map.values().iterator();
        System.out.println("Quantidade de letras: ");
        for (int j = 0; j < a.length; j++) {
            System.out.print(a[j] + " = ");
            System.out.print(i.next() + "\n");
        }

        double somatorio = 0.0;
        for (int j = 0; j < 26; j++) {
            Object wantedChar = a[j];
            double frequency = (double) map.get(wantedChar);
            somatorio += frequency * (frequency - 1.0);
        }

        float ic = (float) (somatorio / (qtTotal * (qtTotal - 1.0)));
        System.out.println(ic);
    }

}