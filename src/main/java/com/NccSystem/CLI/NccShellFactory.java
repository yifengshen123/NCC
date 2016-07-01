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
                reader.setPrompt(SHELL_PROMPT);
                reader.addCompleter(new StringsCompleter(SHELL_CMD_QUIT,
                        SHELL_CMD_EXIT, SHELL_CMD_VERSION, SHELL_CMD_HELP,
                        SHELL_CMD_SHOW, SHELL_CMD_CLEAR, SHELL_CMD_SET));
                writer = new PrintWriter(reader.getOutput(), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    handleUserInput(line.trim());
                }

            } catch (InterruptedIOException e) {
                // Ignore
            } catch (Exception e) {
                //log.error("Error executing InAppShell...", e);
            } finally {
                callback.onExit(0);
            }
        }

        private void handleUserInput(String line) throws InterruptedIOException {

            if (line.equalsIgnoreCase(SHELL_CMD_QUIT)
                    || line.equalsIgnoreCase(SHELL_CMD_EXIT)) {
                writer.println("\rExitting\r");
                throw new InterruptedIOException();
            }

            String response;
            if (line.equalsIgnoreCase(SHELL_CMD_VERSION))
                response = "\rNCC kernel version 1.0\r\n";
            else if (line.equalsIgnoreCase(SHELL_CMD_HELP))
                response = "\r" +
                        "Console commands: \r\n" +
                        "exit\t\texit CLI\r\n" +
                        "help\t\tthis help\r\n" +
                        "quit\t\texit CLI\r\n";
            else if (line.equalsIgnoreCase(SHELL_CMD_SHOW)) {
                response = "\r";
            } else if (line.equalsIgnoreCase(SHELL_CMD_CLEAR)) {
                response = "\r";
            } else if (line.equalsIgnoreCase(""))
                response = "\r";
            else
                response = "\rUnknown command\r\n";

            writer.print(response);
        }
    }

    public Command create() {
        return new NccShell();
    }
}

