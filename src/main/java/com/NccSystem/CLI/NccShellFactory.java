package com.NccSystem.CLI;

import com.mysql.management.util.Str;
import jline.console.ConsoleReader;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.StringTokenizer;

public class NccShellFactory extends ProcessShellFactory {

    private static class NccCommand {
        String fullName;
        String desc;
        boolean hasArgs;
        String autoComplete;
        String execMethod;
        ArrayList<NccCommand> subCommands;

        public NccCommand(String fullName, String desc, boolean hasArgs, ArrayList<NccCommand> subCommands, String execMethod) {
            this.fullName = fullName;
            this.desc = desc;
            this.hasArgs = hasArgs;
            this.subCommands = subCommands;
            this.execMethod = execMethod;
        }
    }

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

        private ArrayList<NccCommand> nccCommands = new ArrayList<>();

        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private Environment environment;
        private Thread thread;
        private ConsoleReader reader;
        private PrintWriter writer;

        public NccShell() {
            super();

            ArrayList<NccCommand> subs;

            subs = new ArrayList<>();
            subs.add(new NccCommand("dhcp", "Clear dhcp leases", true, null, "clearDhcpLeases"));
            subs.add(new NccCommand("session", "Clear sessions", true, null, "clearDhcpSessions"));
            nccCommands.add(new NccCommand("clear", "Clear commands", false, subs, null));

            nccCommands.add(new NccCommand("exit", "Exit from CLI", false, null, null));

            nccCommands.add(new NccCommand("quit", "Same as exit", false, null, null));

            subs = new ArrayList<>();
            ArrayList<NccCommand> dhcpSubs = new ArrayList<>();
            dhcpSubs.add(new NccCommand("binding", "Show active leases", true, null, "showDhcpLeases"));
            dhcpSubs.add(new NccCommand("bindings", "Show all active leases", false, null, "showDhcpLeases"));
            dhcpSubs.add(new NccCommand("unbinded", "Show unbinded users", false, null, "showDhcpUnbinded"));
            subs.add(new NccCommand("dhcp", "Show dhcp-related information", true, dhcpSubs, null));
            subs.add(new NccCommand("radius", "Show radius-related information", true, null, null));
            nccCommands.add(new NccCommand("show", "Show various options", false, subs, null));

            nccCommands.add(new NccCommand("shutdown", "Gracefully shutdown NCC system", false, null, "sysShutdown"));
        }

        private ArrayList<String> executeCommand(String line) {
            StringTokenizer st = new StringTokenizer(line);
            ArrayList<NccCommand> commands = nccCommands;

            while (st.hasMoreElements()) {
                String token = st.nextToken();
                ArrayList<NccCommand> foundCommands = new ArrayList<>();

                for (NccCommand cmd : commands) {
                    System.out.println("Processing: '" + cmd.fullName + "'");
                    if (token.trim().length() > cmd.fullName.length()) continue;
                    if (cmd.fullName.equals(token)) {
                        foundCommands.add(cmd);
                    }
                }

                if (foundCommands.size() != 1) {
                    writer.println("Error in command\r");
                    writer.flush();
                    return null;
                }

                NccCommand cmd = foundCommands.get(0);

                if (st.hasMoreElements() && cmd.subCommands != null) {
                    System.out.println("Found next: '" + cmd.fullName + "'");
                    commands = cmd.subCommands;
                    continue;
                }

                if (cmd.subCommands == null) {
                    System.out.println("No subCommands in '" + cmd.fullName + "'");
                    if (!cmd.hasArgs) {
                        NccCLICommands cliCommands = new NccCLICommandsImpl();

                        if (cmd.execMethod == null) continue;
                        try {
                            Method m = cliCommands.getClass().getMethod(cmd.execMethod, new Class[]{});
                            try {
                                m.invoke(cliCommands, new Object[]{});
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(!st.hasMoreElements()){
                            writer.println("Argument missing\r");
                            writer.flush();
                            return null;
                        }
                    }
                } else {

                }
            }

            return null;
        }

        private String maxMatch(String str1, String str2) {
            String result = "";
            char[] c1 = str1.toCharArray();
            char[] c2 = str2.toCharArray();

            int len = (str1.length() > str2.length()) ? str2.length() : str1.length();

            for (int i = 0; i < len; i++) {
                if (c1[i] == c2[i]) result += c1[i];
            }

            return result;
        }

        private String autoComplete(String line) {
            StringTokenizer st = new StringTokenizer(line);
            String token = null, nextToken = null;
            ArrayList<NccCommand> commands = nccCommands;
            ArrayList<NccCommand> foundCommands = new ArrayList<>();

            while (st.hasMoreElements()) {
                if (nextToken != null) {
                    token = nextToken;
                } else {
                    token = st.nextToken();
                }

                if (st.hasMoreElements()) {
                    //nextToken = st.nextToken();

                    System.out.println("Has next");

                    foundCommands = new ArrayList<>();
                    for (NccCommand cmd : commands) {
                        if (token.trim().length() > cmd.fullName.length()) continue;
                        if (cmd.fullName.substring(0, token.trim().length()).equals(token)) {
                            foundCommands.add(cmd);
                            System.out.println("Found: '" + cmd.fullName + "'");
                        }
                    }

                    if (foundCommands.size() > 1) {
                        StringBuilder sb = new StringBuilder();

                        writer.println("\r");
                        writer.flush();
                        for (NccCommand cmd : foundCommands) {
                            Formatter formatter = new Formatter(sb, Locale.US);
                            writer.println(formatter.format("%-20s%-30s\r", cmd.fullName, cmd.desc));
                            writer.flush();
                        }

                    } else if (foundCommands.size() == 1) {
                        NccCommand cmd = foundCommands.get(0);
                        System.out.println("Found 1: '" + cmd.fullName + "' token '" + token + "' sub: " + cmd.subCommands.toString());
                        if (cmd.subCommands != null) {
                            System.out.println("Has subCommands");
                            commands = cmd.subCommands;
                            for (NccCommand c : commands) {
                                System.out.println("subCommand: '" + c.fullName + "'");
                            }
                            if (st.hasMoreElements()) {
                                System.out.println("Get next token");
                                continue;
                            }
                        } else {
                            commands = new ArrayList<>();
                        }
                    }

                    //System.out.println("Token '" + token + "' has next token: '" + nextToken + "'");
                    //if (st.hasMoreElements()) continue;
                    //token = nextToken;
                }

                System.out.println("Out from IF token: '" + token + "'");

                foundCommands = new ArrayList<>();
                for (NccCommand cmd : commands) {
                    System.out.println("Compare '" + token + "' with '" + cmd.fullName + "'");
                    if (token.trim().length() > cmd.fullName.length()) continue;
                    if (cmd.fullName.substring(0, token.trim().length()).equals(token)) {
                        System.out.println("Equals");
                        foundCommands.add(cmd);
                    }
                }

                if (foundCommands.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    String lastToken = foundCommands.get(0).fullName;
                    String longestMatch = "";

                    writer.println("\r");
                    writer.flush();
                    for (NccCommand cmd : foundCommands) {
                        Formatter formatter = new Formatter(sb, Locale.US);
                        writer.println(formatter.format("%-20s%-30s\r", cmd.fullName, cmd.desc));
                        writer.flush();

                        longestMatch = maxMatch(lastToken, cmd.fullName);
                        lastToken = cmd.fullName;
                    }

                    System.out.println("longestMatch='" + longestMatch + "'");
                    writer.print(longestMatch.substring(token.trim().length()));
                    writer.flush();

                    return longestMatch.substring(token.trim().length());

                } else if (foundCommands.size() == 1) {

                    NccCommand cmd = foundCommands.get(0);

                    System.out.println("Completing: '" + token.trim() + "' to '" + cmd.fullName + "'");

                    if (token.trim().equals(cmd.fullName) && (cmd.subCommands != null)) {
                        StringBuilder sb = new StringBuilder();

                        writer.println("\r");
                        writer.flush();
                        for (NccCommand sub : cmd.subCommands) {
                            Formatter formatter = new Formatter(sb, Locale.US);
                            writer.println(formatter.format("%-20s%-30s\r", sub.fullName, sub.desc));
                            writer.flush();
                        }
                    }

                    if (token.trim().equals(cmd.fullName)) return "";
                    cmd.autoComplete = cmd.fullName.substring(token.trim().length()) + " ";
                    writer.print(cmd.autoComplete);
                    writer.flush();

                    return cmd.autoComplete;
                }
            }

            return "";
        }

        private void help(String cmd) {
            writer.println("Command help on " + cmd + ":\r");
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);

            for (NccCommand command : nccCommands) {

                writer.println(formatter.format("%-20s%-50s\r", command.fullName, command.desc));
                writer.flush();
            }
        }

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

                    if (ch == 13) {     // enter
                        writer.println("\r");
                        writer.println("command: " + line + "\r");
                        writer.flush();

                        executeCommand(line);

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
                        writer.println("\r");
                        writer.flush();

                        StringTokenizer st = new StringTokenizer(line);

                        while (st.hasMoreElements()) {
                            String token = st.nextToken();
                            if (!st.hasMoreElements()) help(token);
                        }
                        writer.print("\r#" + line);
                        writer.flush();
                        continue;
                    }

                    if (ch == 9) {  // tab
                        line += autoComplete(line);
                        writer.print("\r#" + line);
                        writer.flush();
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

