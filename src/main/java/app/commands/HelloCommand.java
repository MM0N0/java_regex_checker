package app.commands;


import picocli.CommandLine;

@CommandLine.Command(name = "hello", description = "Greet World!")
public
class HelloCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello World!");
    }
}