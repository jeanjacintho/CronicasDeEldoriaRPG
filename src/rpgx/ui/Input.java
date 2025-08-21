package rpgx.ui;

import java.util.Scanner;

public class Input {
    private final Scanner sc = new Scanner(System.in);
    public int readInt(int min, int max) {
        while (true) {
            System.out.print("> ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v<min || v>max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida ("+min+"-"+max+").");
            }
        }
    }
}
