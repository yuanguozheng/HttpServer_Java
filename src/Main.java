import model.SettingInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import server.HttpServer;
import util.DOMParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        SettingInfo settingInfo = parseXML();
        int port = settingInfo.getPort();
        File homePath = new File(settingInfo.getHomePath());
        String defaultDoc = settingInfo.getDefault();
        HttpServer server = new HttpServer(port, homePath, defaultDoc);
        server.start();
    }

    private static SettingInfo parseXML() {
        DOMParser parser = new DOMParser();
        Document document = parser.parse("settings.xml");
        Element rootElement = document.getDocumentElement();
        String home = rootElement.getAttribute("HomePath");
        String port = rootElement.getAttribute("Port");
        String defaultDoc = rootElement.getAttribute("Default");
        SettingInfo settingInfo = new SettingInfo();
        settingInfo.setHomePath(home);
        settingInfo.setPort(Integer.parseInt(port));
        settingInfo.setDefault(defaultDoc);
        return settingInfo;
    }

}
