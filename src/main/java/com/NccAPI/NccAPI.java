package com.NccAPI;

import com.Ncc;
import com.NccAPI.IptvManager.IptvManagerImpl;
import com.NccAPI.IptvManager.IptvManagerService;
import com.NccAPI.DhcpBinding.DhcpBindingService;
import com.NccAPI.DhcpBinding.DhcpBindingServiceImpl;
import com.NccAPI.DhcpLeases.DhcpLeasesService;
import com.NccAPI.DhcpLeases.DhcpLeasesServiceImpl;
import com.NccAPI.Map.MapService;
import com.NccAPI.Map.MapServiceImpl;
import com.NccAPI.NAS.NasService;
import com.NccAPI.NAS.NasServiceImpl;
import com.NccAPI.Pools.PoolsService;
import com.NccAPI.Pools.PoolsServiceImpl;
import com.NccAPI.DhcpRelayAgents.DhcpRelayAgentService;
import com.NccAPI.DhcpRelayAgents.DhcpRelayAgentServiceImpl;
import com.NccAPI.Sessions.SessionsService;
import com.NccAPI.Sessions.SessionsServiceImpl;
import com.NccAPI.System.SystemService;
import com.NccAPI.System.SystemServiceImpl;
import com.NccAPI.UserAccounts.AccountsService;
import com.NccAPI.UserAccounts.AccountsServiceImpl;
import com.NccAPI.Users.UsersService;
import com.NccAPI.Users.UsersServiceImpl;
import com.NccAPI.Views.ViewsService;
import com.NccAPI.Views.ViewsServiceImpl;
import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;
import com.NccSystem.NccLogger;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NccAPI {

    private Server apiServer;
    private static NccLogger nccLogger = new NccLogger("APILogger");
    private static Logger logger = nccLogger.setFilename(Ncc.apiLogfile);

    public NccAPI() {
    }

    public NccAPI(Integer port) {

        class NccAPIHandler extends AbstractHandler {

            class CompositeServer extends JsonRpcServer {

                public CompositeServer(Object handler) {
                    super(handler);
                }

                public void handleFiles(HttpServletRequest request, HttpServletResponse response) {

                }

                public void handleCORS(HttpServletRequest request, HttpServletResponse response)
                        throws IOException {

                    // set response type
                    response.setContentType(JSONRPC_RESPONSE_CONTENT_TYPE);

                    // setup streams
                    InputStream input;
                    OutputStream output = response.getOutputStream();

                    // POST
                    if (request.getMethod().equals("POST")) {
                        input = request.getInputStream();
                        response.addHeader("Access-Control-Allow-Origin", "*");

                    } else if (request.getMethod().equals("OPTIONS")) {
                        response.addHeader("Access-Control-Allow-Headers", "Content-Type, POST");
                        response.addHeader("Access-Control-Allow-Origin", "*");

                        output.flush();
                        return;
                        // GET
                    } else if (request.getMethod().equals("GET")) {
                        input = createInputStream(
                                request.getParameter("method"),
                                request.getParameter("id"),
                                request.getParameter("params"));

                        // invalid request
                    } else {
                        throw new IOException(
                                "Invalid request method, only POST and GET is supported");
                    }

                    // service the request
                    //fix to set HTTP status correctly
                    int result = 0;
                    try {
                        result = handle(input, output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (result != 0) {
                        if (result == -32700 || result == -32602 || result == -32603
                                || (result <= -32000 && result >= -32099)) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        } else if (result == -32600) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        } else if (result == -32601) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }
                    //fix to not flush within handle() but outside so http status code can be set
                    output.flush();
                }
            }

            private CompositeServer compositeServer;

            private UsersService userService;
            private AccountsService accountsService;
            private PoolsService poolsService;
            private SessionsService sessionsService;
            private NasService nasService;
            private ViewsService viewsService;
            private IptvManagerService iptvManagerService;
            private DhcpRelayAgentService relayAgentService;
            private DhcpLeasesService dhcpLeasesService;
            private DhcpBindingService dhcpBindingService;
            private MapService mapService;
            private SystemService systemService;

            private Object compositeService;

            public NccAPIHandler() {
                userService = new UsersServiceImpl();
                accountsService = new AccountsServiceImpl();
                poolsService = new PoolsServiceImpl();
                sessionsService = new SessionsServiceImpl();
                nasService = new NasServiceImpl();
                viewsService = new ViewsServiceImpl();
                iptvManagerService = new IptvManagerImpl();
                relayAgentService = new DhcpRelayAgentServiceImpl();
                dhcpLeasesService = new DhcpLeasesServiceImpl();
                dhcpBindingService = new DhcpBindingServiceImpl();
                mapService = new MapServiceImpl();
                systemService = new SystemServiceImpl();

                compositeService = ProxyUtil.createCompositeServiceProxy(
                        this.getClass().getClassLoader(),
                        new Object[]{
                                userService,
                                accountsService,
                                poolsService,
                                sessionsService,
                                nasService,
                                viewsService,
                                iptvManagerService,
                                relayAgentService,
                                dhcpLeasesService,
                                dhcpBindingService,
                                mapService,
                                systemService
                        },
                        new Class<?>[]{
                                UsersService.class,
                                AccountsService.class,
                                PoolsService.class,
                                SessionsService.class,
                                NasService.class,
                                ViewsService.class,
                                IptvManagerService.class,
                                DhcpRelayAgentService.class,
                                DhcpLeasesService.class,
                                DhcpBindingService.class,
                                MapService.class,
                                SystemService.class
                        },
                        true);

                compositeServer = new CompositeServer(compositeService);
            }

            private void getFile(String filename, HttpServletResponse response) {

                String fname = filename.replaceAll("^\\/", "");

                System.out.println("Get file: " + fname);
                File file = new File(fname);

                try {
                    OutputStream outputStream = response.getOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    byte[] bytes = new byte[(int) file.length()];
                    bufferedInputStream.read(bytes, 0, bytes.length);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

                Pattern pattern = Pattern.compile("^\\/tiles\\/z\\d+\\/\\d+\\/\\d+\\.png$");
                Matcher matcher = pattern.matcher(target);

                if (matcher.matches()) {
                    getFile(target, response);
                    return;
                }

                pattern = Pattern.compile("^\\/files\\/.*");
                matcher = pattern.matcher(target);

                if (matcher.matches()) {
                    getFile(target, response);
                    return;
                }

                compositeServer.handleCORS(request, response);
            }
        }

        apiServer = new Server(port);
        apiServer.setHandler(new NccAPIHandler());
    }

    public void start() {
        logger.info("Starting API on port " + Ncc.apiPort);

        try {
            apiServer.start();
            apiServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            apiServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkKey(String apiKey) {
        if (apiKey.equals("CtrhtnT,fnmRfrjq")) return true;
        return false;
    }

    public AccountData checkKey(String login, String key) {
        AccountData accountData = new NccAccounts().getAccount(login);

        if (accountData != null) {

            String hash = DigestUtils.md5Hex(login.concat(accountData.accPassword));

            logger.info("Checking for login='" + login + "' key='" + key + "' with hash='" + hash + "'");

            if (hash.equals(key)) return accountData;
        }

        return null;
    }

    public boolean checkPermission(String login, String key, String permission) {

        logger.info("API request for: '" + login + "' permission=" + permission);

        AccountData accountData = checkKey(login, key);

        if (accountData != null) {
            return new NccAccounts().checkAccountPermission(accountData, permission);
        }

        return false;
    }

    public boolean checkPermission(String apiKey, String permission) {
        String login = "admin";
        String password = "CtrhtnysqGfhjkm";

        String hash = DigestUtils.md5Hex(DigestUtils.md5Hex(login).concat(password));

        System.out.println("Checking api key=" + apiKey + " with " + hash);

        if (apiKey.equals(hash)) return true;

        return false;
    }
}
