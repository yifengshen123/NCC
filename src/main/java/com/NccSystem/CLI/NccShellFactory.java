package com.NccSystem.CLI;

import jline.console.ConsoleReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NccShellFactory extends ProcessShellFactory {
    private static Logger logger = Logger.getLogger("CLILogger");
    public static boolean exitFlag = false;

    private static class NccCommand {
        String fullName;
        String desc;
        boolean hasArgs;
        String autoComplete;
        String execMethod;
        ArrayList<NccCommand> subCommands;
        Class[] argTypes;

        public NccCommand(String fullName, String desc, boolean hasArgs, ArrayList<NccCommand> subCommands, String execMethod) {
            this.fullName = fullName;
            this.desc = desc;
            this.hasArgs = hasArgs;
            this.subCommands = subCommands;
            this.execMethod = execMethod;
        }

        public NccCommand(String fullName, String desc, boolean hasArgs, Class[] argTypes, ArrayList<NccCommand> subCommands, String execMethod) {
            this.fullName = fullName;
            this.desc = desc;
            this.hasArgs = hasArgs;
            this.subCommands = subCommands;
            this.execMethod = execMethod;
            this.argTypes = argTypes;
        }
    }

    private static class NccShell implements Command, Runnable {

        public static final boolean IS_MAC_OSX = System.getProperty("os.name").startsWith("Mac OS X");

        private static final String SHELL_THREAD_NAME = "NccShell";
        private static final String SHELL_PROMPT = "ncc#";

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
            ArrayList<NccCommand> transSubs = new ArrayList<>();
            transSubs.add(new NccCommand("restart", "Restart specified transponder", true, new Class[]{Integer.class}, null, "restartIptvTransponder"));
            transSubs.add(new NccCommand("run", "Run specified transponder", true, new Class[]{Integer.class}, null, "runIptvTransponder"));
            ArrayList<NccCommand> camSubs = new ArrayList<>();
            //subs.add(new NccCommand("cam", "Cam commands", true, camSubs, null));
            subs.add(new NccCommand("transponder", "Transponder commands", true, transSubs, null));
            nccCommands.add(new NccCommand("iptv", "IPTV commands", false, subs, null));

            subs = new ArrayList<>();
            subs.add(new NccCommand("dhcp", "Clear dhcp leases", true, null, "clearDhcpLeases"));
            subs.add(new NccCommand("session", "Clear sessions", true, null, "clearDhcpSessions"));
            nccCommands.add(new NccCommand("clear", "Clear commands", false, subs, null));

            nccCommands.add(new NccCommand("exit", "Exit from CLI", false, null, "exitCLI"));

            nccCommands.add(new NccCommand("quit", "Same as exit", false, null, "exitCLI"));

            subs = new ArrayList<>();
            ArrayList<NccCommand> dhcpSubs = new ArrayList<>();
            dhcpSubs.add(new NccCommand("binding", "Show active leases", true, null, "showDhcpLeases"));
            dhcpSubs.add(new NccCommand("bindings", "Show all active leases", false, null, "showDhcpLeases"));
            dhcpSubs.add(new NccCommand("unbinded", "Show unbinded users", false, null, "showDhcpUnbinded"));
            subs.add(new NccCommand("dhcp", "Show dhcp-related information", true, dhcpSubs, null));
            subs.add(new NccCommand("radius", "Show radius-related information", true, null, null));

            ArrayList<NccCommand> iptvSubs = new ArrayList<>();
            iptvSubs.add(new NccCommand("activechannels", "Show active channel list at specified transponder", true, new Class[]{Integer.class}, null, "showIptvActiveChannels"));
            iptvSubs.add(new NccCommand("cams", "Show CAM list", false, null, "showIptvCams"));
            iptvSubs.add(new NccCommand("channels", "Show channel list", false, null, "showIptvChannels"));
            iptvSubs.add(new NccCommand("transponders", "Show transponder list", false, null, "showIptvTransponders"));
            subs.add(new NccCommand("iptv", "IptvManager related info", true, iptvSubs, null));
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
                    if (token.trim().length() > cmd.fullName.length()) continue;
                    if (cmd.fullName.equals(token)) {
                        foundCommands.add(cmd);
                        break;
                    }
                }

                if (foundCommands.size() != 1) {
                    writer.println("Error in command\r");
                    writer.flush();
                    return null;
                }

                NccCommand cmd = foundCommands.get(0);

                if (st.hasMoreElements() && cmd.subCommands != null) {
                    commands = cmd.subCommands;
                    continue;
                }

                Class[] ptypes = new Class[]{};
                Object[] args = new Object[]{};

                if (cmd.subCommands == null) {
                    NccCLICommands cliCommands = new NccCLICommandsImpl(writer);

                    if (cmd.execMethod == null) continue;

                    if (cmd.hasArgs) {
                        if (!st.hasMoreElements()) {
                            writer.println("Argument missing\r");
                            writer.flush();
                            return null;
                        }

                        token = st.nextToken();

                        ptypes = cmd.argTypes;
                        ArrayList<Object> tmp = new ArrayList<Object>(Arrays.asList(args));
                        for (Class c : cmd.argTypes) {
                            String type = c.getName();
                            if (type.equals("java.lang.Integer")) {
                                tmp.add(Integer.valueOf(token));
                            } else if (type.equals("java.lang.String")) {
                                tmp.add(token);
                            } else {
                                logger.error("Unknown param type: " + type);
                                return null;
                            }
                        }
                        args = tmp.toArray();
                    } else {
                        ptypes = new Class[]{};
                        args = new Object[]{};
                    }

                    try {
                        Method m = cliCommands.getClass().getMethod(cmd.execMethod, ptypes);
                        try {
                            m.invoke(cliCommands, args);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
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

                    foundCommands = new ArrayList<>();
                    for (NccCommand cmd : commands) {
                        if (token.trim().length() > cmd.fullName.length()) continue;
                        if (cmd.fullName.substring(0, token.trim().length()).equals(token)) {
                            foundCommands.add(cmd);
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
                        if (cmd.subCommands != null) {
                            commands = cmd.subCommands;
                            for (NccCommand c : commands) {
                            }
                            if (st.hasMoreElements()) {
                                continue;
                            }
                        } else {
                            commands = new ArrayList<>();
                        }
                    }
                }

                foundCommands = new ArrayList<>();
                for (NccCommand cmd : commands) {
                    if (token.trim().length() > cmd.fullName.length()) continue;
                    if (cmd.fullName.substring(0, token.trim().length()).equals(token)) {
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

                    writer.print(longestMatch.substring(token.trim().length()));
                    writer.flush();

                    return longestMatch.substring(token.trim().length());

                } else if (foundCommands.size() == 1) {

                    NccCommand cmd = foundCommands.get(0);

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

                ArrayList<String> history = new ArrayList<>();
                Integer historyIndex = 0;

                exitFlag = false;

                while (true) {
                    Integer ch = reader.readCharacter();

                    if (ch == 27) { // escape seq
                        ch = reader.readCharacter();

                        if (ch == 91) {
                            ch = reader.readCharacter();

                            switch (ch) {
                                case 65:    // up
                                    if (!history.isEmpty()) {
                                        historyIndex--;
                                        if (historyIndex < 0) historyIndex = 0;
                                        line = history.get(historyIndex);
                                        for (int i = 0; i < 80; i++) {
                                            writer.print(" ");
                                            writer.flush();
                                        }
                                        writer.print("\r#" + line.trim());
                                        writer.flush();
                                    }
                                    break;
                                case 66:    // down
                                    if (!history.isEmpty()) {
                                        historyIndex++;
                                        if (historyIndex >= history.size()) historyIndex = history.size() - 1;
                                        line = history.get(historyIndex);
                                        for (int i = 0; i < 80; i++) {
                                            writer.print(" ");
                                            writer.flush();
                                        }
                                        writer.print("\r#" + line.trim());
                                        writer.flush();
                                    }
                                    break;
                                case 67:    // right
                                    break;
                                case 68:    // left
                                    break;
                                default:
                                    break;
                            }
                        }

                        continue;
                    }

                    if (ch == 13) {     // enter
                        writer.println("\r");
                        writer.println("command: " + line + "\r");
                        writer.flush();

                        if (!line.equals("")) {
                            history.add(line);
                            historyIndex = history.size();
                        }

                        executeCommand(line);

                        if (exitFlag) break;

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

