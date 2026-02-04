package com.gearshiftinteractive.gearshiftscript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Scanner;


public class GearshiftScript {

    static final Scanner scanner = new Scanner(System.in);
    static final Lexer lexer = new Lexer();
    static final Parser parser = new Parser();
    static final Interpreter interpreter = new Interpreter();

    private static void shell() {
        var scope = interpreter.getDefaultScope();
        System.out.println("Type quit to quit");
        do {
            System.out.print(">>> ");
            String line = scanner.nextLine();
            if (line.equals("quit"))
                break;

            try {
                var tokens = lexer.perform("stdin", line);
                //            for (var token : tokens) {
                //                System.out.println(token);
                //            }
                var nodes = parser.perform(tokens.toArray(Token[]::new), "stdin");
                //            nodes.printOut("", "");
                interpreter.perform(nodes, scope);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } while (true);
    }
    private static void script(Path path, boolean debug) throws IOException {
        var content = Files.readString(path);

        var tokens = lexer.perform(path.toString(), content);
        if (debug)
            for (var token : tokens) {
                System.out.println(token);
            }
        var nodes = parser.perform(tokens.toArray(Token[]::new), path.toString());
        if (debug)
            nodes.printOut("", "");
        interpreter.perform(nodes);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            shell();
        else
            if (args.length == 2)
                if (args[1].equals("--debug"))
                    script(Paths.get(args[0]), true);
                else
                    throw new IllegalArgumentException("Invalid argument: " + args[1]);
            else
                script(Paths.get(args[0]), false);
    }
}
