package com.anhtt.miui.translation.helper;

import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.anhtt.miui.translation.helper.model.WrongApplication;
import com.anhtt.miui.translation.helper.model.WrongStringRes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Utils {

    public static void writeStringsToFile(String path, List<StringRes> stringRes) {
        if (stringRes.size() == 0) return;
        try {
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (StringRes string : stringRes) {
                Element stringNode = document.createElement("string");
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                stringNode.setTextContent(string.getValue());
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<resources>", "\n<resources>")
                    .replaceAll("</resources>", "\n</resources>");
            FileOutputStream outputXml = new FileOutputStream(path + "\\strings.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeWrongToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeWrongToFile(absolutePath, wrongApplication);
        }
    }
    public static void writeUnTranslatedStringToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeUnTranslatedStringToFile(absolutePath, wrongApplication);
        }
    }
    public static void writeUnTranslatedStringToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOrigins().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOrigins()) {
                    Element stringNode = document.createElement("string");
                    root.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    stringNode.setTextContent(string.getValue());
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\strings.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }
    public static void writeWrongToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOrigins().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);
            Element allDeviceRoot = document.createElement("allDevice");
            root.appendChild(allDeviceRoot);
            Element specificRoot = document.createElement("specificDevice");
            root.appendChild(specificRoot);

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOrigins()) {
                if (!string.getDevices().contains("all")) {
                    Element stringNode = document.createElement("string");
                    specificRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    stringNode.setTextContent(string.getValue());

                    Element childNode = document.createElement("device");
                    stringNode.appendChild(childNode);
                    childNode.setTextContent(string.getDevices().toString());
                } else {
                    Element stringNode = document.createElement("string");
                    allDeviceRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    stringNode.setTextContent(string.getValue());
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<specificDevice", "\n<specificDevice")
                    .replaceAll("</specificDevice", "\n</specificDevice");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\strings.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }



    public static void writeUnTranslatedArrayToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeUnTranslatedArrayToFile(absolutePath, wrongApplication);
        }
    }
    public static void writeUnTranslatedArrayToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOrigins().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOrigins()) {
                Element stringNode = document.createElement("string");
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                stringNode.setTextContent(string.getValue());
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\arrays.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }
}
