package com.NccIptvManager;

import com.Ncc;
import com.NccSystem.NccLogger;
import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class NccIptvManager {
    private static NccLogger nccLogger = new NccLogger("IptvManagerLogger");
    private static Logger logger = nccLogger.setFilename(Ncc.iptvLogfile);
    private NccQuery query;

    public static ArrayList<ActiveTransponder> Transponders = new ArrayList<>();
    public static ArrayList<ActiveChannel> Channels = new ArrayList<>();

    public NccIptvManager() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public ServerData getServerById(Integer id) {
        return new ServerData().getData("SELECT * FROM nccIptvServers WHERE id=" + id);
    }

    public ArrayList<ChannelData> getChannelsByTransponder(Integer id) {
        return new ChannelData().getDataList("SELECT * FROM nccViewAstraManagerChannel WHERE transponderId=" + id);
    }

    public ChannelData getChannelById(Integer id) {
        return new ChannelData().getData("SELECT * FROM nccViewAstraManagerChannel WHERE channelId=" + id);
    }

    public ArrayList<ChannelData> getChannels() {
        return new ChannelData().getDataList("SELECT * FROM nccViewAstraManagerChannel");
    }

    public ArrayList<ServerData> getServers() {
        return new ServerData().getDataList("SELECT * FROM nccIptvServers");
    }

    public ArrayList<AdapterData> getAdapters() {
        return new AdapterData().getDataList("SELECT * FROM nccIptvAdapters GROUP BY serverId, adapterDevice");
    }

    public ArrayList<AdapterData> getAdaptersByServerId(Integer id) {
        return new AdapterData().getDataList("SELECT * FROM nccIptvAdapters WHERE serverId=" + id);
    }

    public ArrayList<AdapterType> getAdapterTypes() {
        return new AdapterType().getDataList("SELECT * FROM nccIptvAdapterTypes");
    }

    public ArrayList<TransponderData> getTransponders() {
        return new TransponderData().getDataList("SELECT * FROM nccViewAstraTransponders");
    }

    public TransponderData getTransponderById(Integer id) {
        return new TransponderData().getData("SELECT * FROM nccViewAstraTransponders WHERE id=" + id);
    }

    public ArrayList<CamData> getCams() {
        return new CamData().getDataList("SELECT * FROM nccIptvCam");
    }

    public ArrayList<Integer> createServer(ServerData serverData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccIptvServers (" +
                    "serverIP," +
                    "serverSecret," +
                    "serverLocalAddress, " +
                    "serverComment, " +
                    "serverName" +
                    ") VALUES (" +
                    serverData.serverIP + ", " +
                    "'" + serverData.serverSecret + "', " +
                    serverData.serverLocalAddress + ", " +
                    "'" + serverData.serverComment + "', " +
                    "'" + serverData.serverName + "'" +
                    ")");

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> createAdapter(AdapterData adapterData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccIptvAdapters (" +
                    "adapterDevice," +
                    "adapterType," +
                    "serverId, " +
                    "adapterComment" +
                    ") VALUES (" +
                    adapterData.adapterDevice + ", " +
                    adapterData.adapterType + ", " +
                    adapterData.serverId + ", " +
                    "'" + adapterData.adapterComment + "'" +
                    ")");

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> createTransponder(TransponderData transponderData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccIptvTransponders (" +
                    "transName," +
                    "transFreq," +
                    "transPolarity," +
                    "transFEC," +
                    "transSymbolrate," +
                    "transType," +
                    "adapterId," +
                    "transLNB," +
                    "transSat," +
                    "transStatus" +
                    ") VALUES (" +
                    "'" + transponderData.transponderName + "', " +
                    transponderData.transponderFreq + ", " +
                    "'" + transponderData.transponderPolarity + "', " +
                    "'" + transponderData.transponderFEC + "', " +
                    transponderData.transponderSymbolrate + ", " +
                    "'" + transponderData.transponderType + "', " +
                    transponderData.adapterId + ", " +
                    "'" + transponderData.transponderLNB + "', " +
                    "'" + transponderData.transponderSat + "', " +
                    "1" +
                    ")");

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> createCam(CamData camData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccIptvCam (" +
                    "camServer," +
                    "camPort," +
                    "camUser," +
                    "camPassword," +
                    "camName," +
                    "camKey" +
                    ") VALUES (" +
                    "'" + camData.camServer + "', " +
                    camData.camPort + ", " +
                    "'" + camData.camUser + "', " +
                    "'" + camData.camPassword + "', " +
                    "'" + camData.camName + "', " +
                    "'" + camData.camKey + "'" +
                    ")");

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ChannelData createChannel(ChannelData channelData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccIptvChannels (" +
                    "channelName," +
                    "channelPnr," +
                    "transponderId," +
                    "camId," +
                    "channelIP" +
                    ") VALUES (" +
                    "'" + channelData.channelName + "', " +
                    channelData.channelPnr + ", " +
                    channelData.transponderId + ", " +
                    channelData.camId + ", " +
                    channelData.channelIP +
                    ")");

            return new NccIptvManager().getChannelById(ids.get(0));
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> deleteServer(Integer id) {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccIptvServers WHERE id=" + id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> deleteAdapter(Integer id) {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccIptvAdapters WHERE id=" + id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> deleteTransponder(Integer id) {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccIptvTransponders WHERE id=" + id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> deleteCam(Integer id) {

        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccIptvCam WHERE id=" + id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteChannel(Integer id) {

        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccIptvChannels WHERE id=" + id);

            return true;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Integer> updateServer(ServerData serverData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccIptvServers SET " +
                    "serverIP=" + serverData.serverIP + ", " +
                    "serverSecret='" + serverData.serverSecret + "', " +
                    "serverLocalAddress=" + serverData.serverLocalAddress + "," +
                    "serverComment='" + serverData.serverComment + "'," +
                    "serverName='" + serverData.serverName + "' " +
                    "WHERE id=" + serverData.id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> updateAdapter(AdapterData adapterData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccIptvAdapters SET " +
                    "adapterDevice=" + adapterData.adapterDevice + ", " +
                    "adapterType=" + adapterData.adapterType + ", " +
                    "serverId=" + adapterData.serverId + "," +
                    "adapterComment='" + adapterData.adapterComment + "' " +
                    "WHERE id=" + adapterData.id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> updateTransponder(TransponderData transponderData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccIptvTransponders SET " +
                    "transName='" + transponderData.transponderName + "', " +
                    "transFreq=" + transponderData.transponderFreq + ", " +
                    "transPolarity='" + transponderData.transponderPolarity + "', " +
                    "transFEC='" + transponderData.transponderFEC + "', " +
                    "transSymbolrate=" + transponderData.transponderSymbolrate + ", " +
                    "transType='" + transponderData.transponderType + "', " +
                    "adapterId=" + transponderData.adapterId + ", " +
                    "transLNB='" + transponderData.transponderLNB + "', " +
                    "transSat='" + transponderData.transponderSat + "' " +
                    "WHERE id=" + transponderData.id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> updateCam(CamData camData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccIptvCam SET " +
                    "camServer='" + camData.camServer + "', " +
                    "camPort=" + camData.camPort + ", " +
                    "camUser='" + camData.camUser + "', " +
                    "camPassword='" + camData.camPassword + "', " +
                    "camName='" + camData.camName + "', " +
                    "camKey='" + camData.camKey + "' " +
                    "WHERE id=" + camData.id);

            return ids;
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ChannelData updateChannel(ChannelData channelData) {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccIptvChannels SET " +
                    "channelName='" + channelData.channelName + "', " +
                    "channelPnr=" + channelData.channelPnr + ", " +
                    "transponderId=" + channelData.transponderId + ", " +
                    "channelIP=" + channelData.channelIP + ", " +
                    "camId=" + channelData.camId + " " +
                    "WHERE id=" + channelData.channelId);

            return new NccIptvManager().getChannelById(channelData.channelId);
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void runAnalyzer(final Integer channelId, Integer transponderId) {

        ChannelData channelData = getChannelById(channelId);

        logger.info("Running analyzer channelId=" + channelId + " transponderId=" + transponderId);

        Process p;
        try {
            logger.info("Starting Analyzer: udp://" + NccUtils.long2ip(channelData.serverLocalAddress) + "@" + NccUtils.long2ip(channelData.channelIP) + ":1234");
            p = Runtime.getRuntime().exec("/usr/bin/astra --analyze udp://" + NccUtils.long2ip(channelData.serverLocalAddress) + "@" + NccUtils.long2ip(channelData.channelIP) + ":1234");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            final ActiveChannel activeChannel = new ActiveChannel();
            activeChannel.id = channelId;
            activeChannel.transponderId = transponderId;
            activeChannel.process = p;
            activeChannel.reader = reader;
            activeChannel.channelData = channelData;
            activeChannel.bitrate = 0;
            activeChannel.scrambledCount = 0;
            activeChannel.ccCount = 0;
            Channels.add(activeChannel);

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        String line = "";

                        while ((line = reader.readLine()) != null) {
                            if (line.matches("(.*)Bitrate(.*)") || line.matches("(.*)ERROR(.*)")) break;
                        }

                        ActiveChannel channel = getActiveChannelById(channelId);
                        String[] parts = line.split("\\s");

                        if (parts[3].matches("(.*)INFO(.*)")) {
                            if (parts[4].matches("(.*)Bitrate(.*)")) {
                                channel.bitrate = Integer.parseInt(parts[5]);
                            }
                        }

                        if (parts[3].matches("(.*)ERROR(.*)")) {
                            if (parts[4].matches("(.*)Scrambled(.*)")) {
                                channel.scrambledCount++;
                            }
                            if (parts[4].matches("(.*)CC(.*)")) {
                                channel.ccCount++;
                                logger.debug("Channel [" + channel.channelData.channelName + "] CC ERROR, count=" + channel.ccCount + " bitrate=" + channel.bitrate);
                            }
                        }

                        updateActiveChannel(channel);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            timer.schedule(timerTask, 1000, 1000);

            activeChannel.timer = timer;
            activeChannel.timerTask = timerTask;

            logger.info("Channel analyzer started id=" + channelId);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateActiveChannel(ActiveChannel channel) {
        ArrayList<Integer> ids;
        try {
            NccQuery query = new NccQuery();

            ids = query.updateQuery("UPDATE nccIptvChannels SET currentState=1, currentBitrate=" + channel.bitrate + ", currentCC=" + channel.ccCount + ", currentPES=" + channel.scrambledCount + ", lastActive=UNIX_TIMESTAMP(NOW()) WHERE id=" + channel.id);
            ids = query.updateQuery("UPDATE nccIptvChannels SET currentState=0, currentBitrate=0, currentCC=0, currentPES=0 WHERE lastActive+60<UNIX_TIMESTAMP(NOW())");

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

    }

    public Process runTransponder(Integer id) {
        ArrayList<ChannelData> channelData = this.getChannelsByTransponder(id);
        TransponderData transponderData = this.getTransponderById(id);

        logger.info("Running transponder id=" + id);

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("tmp", ".lua", new File("/tmp"));
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            Writer writer = new BufferedWriter(outputStreamWriter);

            writer.write("log.set({ debug = false, stdout = true, filename = \"/var/log/astra/" + transponderData.transponderFreq + transponderData.transponderPolarity + ".log\" })\n");
            writer.write("pidfile(\"/etc/astra/run/" + transponderData.transponderFreq + transponderData.transponderPolarity + ".pid\")\n\n");
            writer.write("dvb1 = dvb_tune({ adapter = " + transponderData.adapterDevice + ", " +
                    "type =\"" + transponderData.transponderType + "\", " +
                    "lnb = \"" + transponderData.transponderLNB + "\", " +
                    "tp = \"" + transponderData.transponderFreq + ":" + transponderData.transponderPolarity + ":" + transponderData.transponderSymbolrate + "\"," +
                    "fec = \"" + transponderData.transponderFEC + "\" })\n\n");


            for (ChannelData ch : channelData) {

                if (ch.camId > 0) {
                    writer.write("cam_" + ch.channelPnr + " = newcamd({ name = \"cam_" + ch.channelPnr + "\", host = \"" + ch.camServer + "\", port = \"" + ch.camPort + "\", user = \"" + ch.camUser + "\", pass = \"" + ch.camPassword + "\", key = \"" + ch.camKey + "\", })");
                    writer.write("make_channel({ name = \"" + ch.channelName + "\", input = { \"dvb://dvb1#pnr=" + ch.channelPnr + "&cam=cam_" + ch.channelPnr + "\" }, output = { \"udp://" + NccUtils.long2ip(ch.channelIP) + ":1234#localaddr=" + NccUtils.long2ip(transponderData.serverLocalAddress) + "&ttl=7\" } })\n\n");
                } else {
                    writer.write("make_channel({ name = \"" + ch.channelName + "\", input = { \"dvb://dvb1#pnr=" + ch.channelPnr + "\" }, output = { \"udp://" + NccUtils.long2ip(ch.channelIP) + ":1234#localaddr=" + NccUtils.long2ip(transponderData.serverLocalAddress) + "&ttl=7\" } })\n\n");
                }

                runAnalyzer(ch.channelId, id);
            }

            writer.close();


//            System.out.println(tmpFile.getAbsoluteFile());

            Process p;
            p = Runtime.getRuntime().exec("/usr/bin/astra " + tmpFile.getAbsoluteFile());

            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            final ActiveTransponder activeTransponder = new ActiveTransponder();
            activeTransponder.id = id;
            activeTransponder.process = p;
            activeTransponder.tmpFile = tmpFile;
            activeTransponder.reader = reader;
            Transponders.add(activeTransponder);

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        String line = "";

                        while ((line = activeTransponder.reader.readLine()) != null) {
                            if (line.matches("(.*)SCVYL(.*)")) {
                                break;
                            }
                        }

                        if (line.matches("(.*)SCVYL(.*)")) {
                            line = line.replaceAll("signal:", "");
                            line = line.replaceAll("snr:", "");
                            line = line.replaceAll("ber:", "");
                            line = line.replaceAll("unc:", "");
                            line = line.replaceAll("%", "");
                            String[] parts = line.split("\\s");

                            ActiveTransponder transponder = getActiveTransponderById(activeTransponder.id);

                            transponder.signal = Integer.parseInt(parts[10]);
                            transponder.snr = Integer.parseInt(parts[11]);
                            transponder.ber = Integer.parseInt(parts[12]);
                            transponder.unc = Integer.parseInt(parts[13]);

                            logger.info("Transponder lock: signal=" + transponder.signal + " snr=" + transponder.snr + " ber=" + transponder.ber + " unc=" + transponder.unc);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            timer.schedule(timerTask, 1000, 1000);

            activeTransponder.timer = timer;
            activeTransponder.timerTask = timerTask;

            logger.info("Transponder started. Active transponders: " + Transponders.size());

            tmpFile.deleteOnExit();

            p.waitFor();

            logger.info("Astra process terminated");
            removeActiveTransponder(id);
        } catch (Exception e) {

        }
        return null;
    }

    public ActiveTransponder getActiveTransponderById(Integer id) {
        Iterator<ActiveTransponder> it = Transponders.iterator();

        while (it.hasNext()) {
            ActiveTransponder item = it.next();

            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    public ActiveChannel getActiveChannelById(Integer id) {
        Iterator<ActiveChannel> it = Channels.iterator();

        while (it.hasNext()) {
            ActiveChannel item = it.next();

            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    public ArrayList<ActiveChannel> getActiveChannelsByTransponderId(Integer id) {
        Iterator<ActiveChannel> it = Channels.iterator();
        ArrayList<ActiveChannel> activeChannels = new ArrayList<>();

        while (it.hasNext()) {
            ActiveChannel item = it.next();

            if (item.transponderId == id) {
                activeChannels.add(item);
            }
        }

        return activeChannels;
    }

    public ActiveTransponder getActiveTransponderByChannelId(Integer id) {
        Iterator<ActiveChannel> it = Channels.iterator();

        while (it.hasNext()) {
            ActiveChannel item = it.next();

            if (item.transponderId == id) {
                ActiveTransponder activeTransponder = getActiveTransponderById(id);
                return activeTransponder;
            }
        }

        return null;
    }

    public void removeActiveTransponder(Integer id) {
        ArrayList<Integer> stoppedTransponders = new ArrayList<>();

        Iterator<ActiveTransponder> it = Transponders.iterator();

        while (it.hasNext()) {
            ActiveTransponder item = it.next();

            if (item.id == id) {
                it.remove();
            }
        }
    }

    public void removeActiveChannel(Integer id) {

        Iterator<ActiveChannel> it = Channels.iterator();

        while (it.hasNext()) {
            ActiveChannel item = it.next();

            if (item.id == id) {
                it.remove();
            }
        }
    }

    public ArrayList<Integer> stopTransponder(Integer id) {

        ArrayList<Integer> stoppedTransponders = new ArrayList<>();

        logger.info("Stop transponder id=" + id);

        Iterator<ActiveChannel> ait = Channels.iterator();

        while (ait.hasNext()) {
            ActiveChannel item = ait.next();

            if (item.transponderId == id) {
                item.timerTask.cancel();
                item.timer.cancel();

                item.process.destroy();
                try {
                    item.process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ait.remove();
                logger.info("Stopped analyzer for channel: " + item.id);
            }
        }

        Iterator<ActiveTransponder> it = Transponders.iterator();

        while (it.hasNext()) {
            ActiveTransponder item = it.next();

            if (item.id == id) {

                item.timerTask.cancel();
                item.timer.cancel();

                item.process.destroy();
                try {
                    item.process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stoppedTransponders.add(item.id);
                it.remove();
                item.tmpFile.delete();
                logger.info("Stopped transponder: " + id + " Active transponders: " + Transponders.size());
            }
        }

        return stoppedTransponders;
    }

    public TransponderStatus getTransponderStatus(Integer id) {

        TransponderStatus transponderStatus = new TransponderStatus();
        ActiveTransponder activeTransponder = getActiveTransponderById(id);

        transponderStatus.id = id;
        transponderStatus.status = 0;

        if (activeTransponder != null) {
            transponderStatus.status = 1;
            transponderStatus.signal = activeTransponder.signal;
            transponderStatus.snr = activeTransponder.snr;
            transponderStatus.ber = activeTransponder.ber;
            transponderStatus.unc = activeTransponder.unc;
            return transponderStatus;
        }

        return transponderStatus;
    }
}
