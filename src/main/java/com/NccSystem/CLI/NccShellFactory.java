package com.NccSystem.CLI;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.*;

public class NccShellFactory extends ProcessShellFactory {

    private static class NccShell implements Command, Runnable {

        public static final boolean IS_MAC_OSX = System.getProperty("os.name").startsWith("Mac OS X");

        private static final String SHELL_THREAD_NAME = "NccShell";
        private static final String SHELL_PROMPT = "ncc#";

        private static final String SHELL_CMD_QUIT = "quit";
        private static final String SHELL_CMD_EXIT = "exit";
        private static final String SHELL_CMD_VERSION = "version";
        private static final String SHELL_CMD_HELP = "help";
        private static final String SHELL_CMD_SET = "set";
        private static final String SHELL_CMD_SHOW = "show";
        private static final String SHELL_CMD_CLEAR = "clear";

        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private Environment environment;
        private Thread thread;
        private ConsoleReader reader;
        private PrintWriter writer;

        public InputStream getIn() {
            return in;
        }

        public OutputStream getOut() {
            return out;
        }

        public OutputStream getErr() {
            return err;
        }

        public Environment getEnvironment() {
            return environment;
        }

        public void setInputStream(InputStream in) {
            this.in = in;
        }

        public void setOutputStream(OutputStream out) {
            this.out = out;
        }

        public void setErrorStream(OutputStream err) {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        public void start(Environment env) throws IOException {
            environment = env;
            thread = new Thread(this, SHELL_THREAD_NAME);
            thread.start();
        }

        public void destroy() {
            if (reader != null)
                reader.shutdown();
            thread.interrupt();
        }

        @Override
        public void run() {
            try {

                reader = new ConsoleReader(in, new FilterOutputStream(out) {
                    @Override
                    public void write(final int i) throws IOException {
                        super.write(i);

                        // workaround for MacOSX!! reset line after CR..
                        if (IS_MAC_OSX && i == ConsoleReader.CR.toCharArray()[0]) {
                            super.write(ConsoleReader.RESET_LINE);
                        }
                    }
                });

                writer = new PrintWriter(reader.getOutput(), true);

                writer.println("NCC CLI v1.0 ready.\r");

                writer.print("#");
                writer.flush();
                String line = "";
                while (true) {
                    Integer ch = reader.readCharacter();
                    System.out.println("ch=" + ch);

                    if (ch == 13) {     // enter
                        writer.println("\r");
                        writer.println("command: " + line + "\r");
                        writer.print("#");
                        writer.flush();
                        line = "";
                        continue;
                    }

                    if (ch == 127) {    // backspace
                        if (line.length() >= 1) {
                            line = line.substring(0, line.length() - 1);
                            writer.print("\b \b");
                            writer.flush();
                        }
                        continue;
                    }

                    if (ch == 63) { // ?
                        continue;
                    }

                    if (ch == 9) {  // tab
                        continue;
                    }

                    if (ch == 27) { // escape
                        writer.println("\r");
                        writer.println("Exitting from CLI...\r");
                        writer.flush();
                        break;
                    }

                    line += String.valueOf(Character.toChars(ch));
                    writer.print(String.valueOf(Character.toChars(ch)));
                    writer.flush();
                }

            } catch (InterruptedIOException e) {
                // Ignore
            } catch (Exception e) {
                //log.error("Error executing InAppShell...", e);
            } finally {
                callback.onExit(0);
            }
        }

    }

    public Command create() {
        return new NccShell();
    }
}

