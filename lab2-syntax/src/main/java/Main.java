import LR.*;
import grammer.*;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Grammar.constructFromFile(new File("./src/input2.txt"));
        LR.constructAutomation();

        for (int i = 0; i < LR.LR_automation.size(); i++) {
            System.out.print(i + ":  ");
            System.out.println(LR.LR_automation.get(i));
        }

        System.out.println();
        System.out.println();
        System.out.println();

        for (Symbol symbol : Grammar.all_symbols) {
            System.out.print(symbol + ":   ");
            System.out.println(Grammar.first(symbol));
        }
    }
}
