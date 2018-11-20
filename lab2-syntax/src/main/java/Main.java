import LR.*;
import grammer.*;
import parsing.ParserGenerator;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Grammar.constructFromFile(new File("./src/grammar_input1.txt"));
        LR1.constructAutomation();
        ParserGenerator.generateParser();
    }
}
