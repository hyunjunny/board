package kr.co.ultari.board.scheduler;

import kr.co.ultari.board.repository.mapper.AlarmMapper;
import kr.co.ultari.board.util.StringUtilCustomize;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class AlarmScheduler {

    @Value("${ultari.alarm.url:}")
    String URL;

    @Value("${ultari.alarm.max-count:5}")
    int maxCount;

    @Value("${ultari.alarm.interval-min:10}")
    int intervalMin;

    @Autowired
    AlarmMapper alarmMapper;

    @Scheduled(cron = "${ultari.scheduler.board-noti.select.cron:0 * * * * *}")
    public void AlarmLoad () throws InterruptedException {
        log.debug(new Date().toString());
        log.debug("alarmUrl : "+URL);

        ArrayList<JSONObject> list = new ArrayList<>();
        List<Map<String,String>> alarmList = alarmMapper.selectAlarm(maxCount, intervalMin);
        for(Map<String, String> alarm:alarmList) {
            JSONObject json = new JSONObject();

            json.put("boardId",alarm.get("boardId"));
            json.put("contentId",alarm.get("contentId"));
            json.put("userId",alarm.get("userId"));
            json.put("readUserId",alarm.get("readUserId"));
            json.put("count",alarm.get("count"));
            json.put("userName",alarm.get("userName"));
            json.put("notiTitle",alarm.get("notiTitle"));
            json.put("notiContents",alarm.get("notiContent"));
            json.put("linkUrl",alarm.get("boardId"));

            String uuid = "";
            Map<String, String> uuidMap = alarmMapper.selectUUID(alarm.get("userId"));
            if(uuidMap != null)
                uuid = uuidMap.get("userId");

            int count = json.getInt("count");
            if(count >= maxCount) {
                json.put("notiCode", "FULL_COUNT");
                json.put("count",999);
            }
            else json.put("notiCode", StringUtilCustomize.getUUID());
            json.put("senderName","");
            json.put("systemName","BOARD");
            json.put("sendDate",StringUtilCustomize.getNowDate("yyyyMMddHHmmss"));
            json.put("alertType","0");
            if(!uuid.isEmpty()) json.put("receivers",uuid);
            else json.put("receivers",alarm.get("userId"));
            json.put("persistence",true);

            log.debug(json.toString());

            if(count >= maxCount) alarmMapper.updateSendYN("Y",alarm.get("contentId"),alarm.get("userId"));
            else alarmMapper.updateCountAlarm(json.getString("contentId"), json.getString("userId"));

            JSONObject messageJson = parsePushMessage(json);
            list.add(messageJson);

            Thread.sleep(1);
        }

        if(!list.isEmpty()) sendAlarm(list);
    }

    public JSONObject parsePushMessage(JSONObject json){
        JSONObject pushJson = new JSONObject();
        JSONObject messageJson = new JSONObject();

        pushJson.put("userId",json.getString("receivers"));
        messageJson.put("id",json.getString("receivers"));
        messageJson.put("name",json.getString("senderName"));
        messageJson.put("message",json.getString("notiContents"));
        messageJson.put("key","MSG");
        messageJson.put("date",json.getString("sendDate"));
        messageJson.put("PCICON","N");
        messageJson.put("badgeCount",json.getInt("count"));
        messageJson.put("boardLink",json.getString("linkUrl"));
        pushJson.put("message",messageJson.toString());
        log.debug(pushJson.toString());

        return pushJson;
    }


    public boolean sendAlarm(ArrayList<JSONObject> list){
        try {
            URL url = new URL(URL);
            log.debug(list.toString());

            if(url.getProtocol().equals("http")) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Content-Type", "application/json");

                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = list.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                log.debug("Response Code : " + responseCode);
                if(responseCode == 200 || responseCode == 201) return true;
            }else{
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Content-Type", "application/json");

                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = list.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                log.debug("Response Code : " + responseCode);
                /*if(responseCode == 200 || responseCode == 201) */return true;
            }

            /*BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            log.debug(response.toString());*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Scheduled(cron = "${ultari.scheduler.board-noti.delete.cron:0 0 4 * * *}")
    public void deleteNoti()  throws InterruptedException {
        String date = StringUtilCustomize.castNowDate(LocalDateTime.now().minusWeeks(1));
        log.debug("deleteCron : "+date);
        alarmMapper.deleteCron(date);
    }

    static {
        // this part is needed cause Lebocoin has invalid SSL certificate, that cannot be normally processed by Java
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Not relevant.
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                }
        };

        HostnameVerifier trustAllHostnames = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // Just allow them all.
            }
        };

        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

