package com.anhtt.miui.translation.helper;

import com.anhtt.miui.translation.helper.model.WrongApplication;
import com.anhtt.miui.translation.helper.model.WrongArrayRes;
import com.anhtt.miui.translation.helper.model.WrongStringRes;
import com.anhtt.miui.translation.helper.model.res.Item;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static boolean isNumeric(String str) {
        String string = str.toLowerCase().replaceAll("\\.", "")
                .replaceAll(":", "")
                .replaceAll(",", "")
                .replaceAll("-", "")
                .replaceAll(" ", "")
                .replaceAll("\"", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("°", "")
                .replaceAll("%s", "")
                .replaceAll("%d", "")
                .replaceAll(";", "")
                .replaceAll("%", "")
                .replaceAll("\\$s", "")
                .replaceAll("\\$d", "")
                .replaceAll("\\+", "")
                .replaceAll("\\\\", "")
                .replaceAll("/", "")
                .replaceAll("m", "")
                .replaceAll("l", "")
                .replaceAll("z", "")
                .replaceAll("v", "")
                .replaceAll("c", "");
        if (string.length() == 0) return true;
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(string, pos);
        return string.length() == pos.getIndex();
    }

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
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
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

    public static void writeWronArray(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeWrongArrayToFile(absolutePath, wrongApplication);
        }
    }

    private static boolean isIgnoredDevice(String name) {
        List<String> strings = Arrays.asList("XiaomiEUTools.apk", "AutoDialer.apk", "Cit.apk", "cit_nikel.apk", "mediatek-res.apk", "SimContacts.apk"
                , "PrintRecommendationService.apk", "BSPTelephonyDevTool.apk", "BtTool.apk", "cit.apk", "EngineerMode.apk", "FactoryKitTest.apk", "FactoryMode.apk"
                , "imssettings.apk", "Mimoji.apk", "NQNfcNci.apk", "Traceur.apk", "Cit.apk");
        return strings.contains(name);
    }

    public static void writeUnTranslatedStringToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            if (!isIgnoredDevice(wrongApplication.getName()))
                writeUnTranslatedStringToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeUnTranslatedStringToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOriginStrings().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOriginStrings()) {
                Element stringNode = document.createElement("string");
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
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
        if (wrongApplication.getWrongTranslatedOriginStrings().size() == 0) return;
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

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOriginStrings()) {
                if (!string.getDevices().contains("all")) {
                    Element stringNode = document.createElement("string");
                    specificRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                    stringNode.setTextContent(string.getValue());

                    Element countNode = document.createElement("count");
                    stringNode.appendChild(countNode);
                    countNode.setTextContent(string.getDevices().size() + "");

                    Element childNode = document.createElement("device");
                    stringNode.appendChild(childNode);
                    childNode.setTextContent(string.getDevices().toString());


                } else {
                    Element stringNode = document.createElement("string");
                    allDeviceRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
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
                    .replaceAll("<count", "\n\t<count")
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

    public static void writeWrongArrayToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOriginArrays().size() == 0) return;
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

            for (WrongArrayRes string : wrongApplication.getWrongTranslatedOriginArrays()) {
                if (!string.getDevices().contains("all")) {
                    Element stringNode = document.createElement("string");
                    specificRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                    for (Item item : string.getItems()) {
                        Element itemNode = document.createElement("item");
                        stringNode.appendChild(itemNode);
                        itemNode.setTextContent(item.getValue());
                    }
                    Element countNode = document.createElement("count");
                    stringNode.appendChild(countNode);
                    countNode.setTextContent(string.getDevices().size() + "");

                    Element childNode = document.createElement("device");
                    stringNode.appendChild(childNode);
                    childNode.setTextContent(string.getDevices().toString());


                } else {
                    Element stringNode = document.createElement("string");
                    allDeviceRoot.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                    for (Item item : string.getItems()) {
                        Element itemNode = document.createElement("item");
                        stringNode.appendChild(itemNode);
                        itemNode.setTextContent(item.getValue());
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<specificDevice", "\n<specificDevice")
                    .replaceAll("</specificDevice", "\n</specificDevice");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\arrays.xml");
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
        if (wrongApplication.getWrongTranslatedOriginArrays().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongArrayRes string : wrongApplication.getWrongTranslatedOriginArrays()) {
                Element stringNode = document.createElement(string.getArrayType());
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                for (Item item : string.getItems()) {
                    Element itemNode = document.createElement("item");
                    stringNode.appendChild(itemNode);
                    itemNode.setTextContent(item.getValue());
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
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<item", "\n\t\t<item")
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

    public static void addIgnoredFile(String path, String filteredPath, List<WrongApplication> untranslatedApplications) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File sourceFile = new File(path + "\\MIUI10\\MIUI10_untranslateable.xml");
        File filteredFile = new File(filteredPath + "\\UnTranslated");
        if (!sourceFile.exists()) return;
        if (!filteredFile.exists() || filteredFile.listFiles() == null) return;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(sourceFile);
        Element root = doc.getDocumentElement();


        for (File file : Objects.requireNonNull(filteredFile.listFiles())) {
            File resFile = new File(file.getAbsolutePath() + "\\strings.xml");

        }


        for (WrongApplication wrongApplication : untranslatedApplications) {


            for (WrongStringRes string : wrongApplication.getWrongTranslatedOriginStrings()) {
                Element item = doc.createElement("item");
                root.appendChild(item);
                item.setAttribute("folder", "all");
                item.setAttribute("application", wrongApplication.getName());
                item.setAttribute("file", "strings.xml");
                item.setAttribute("name", string.getName());
            }
            for (WrongArrayRes string : wrongApplication.getWrongTranslatedOriginArrays()) {
                Element item = doc.createElement("item");
                root.appendChild(item);
                item.setAttribute("folder", "all");
                item.setAttribute("application", wrongApplication.getName());
                item.setAttribute("file", "arrays.xml");
                item.setAttribute("name", string.getName());
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(doc);
        StringWriter outputXmlStringWriter = new StringWriter();
        transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
        String outputXmlString = outputXmlStringWriter.toString()
                .replaceAll("<item", "\n\t<item")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(sourceFile.getAbsolutePath());
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
        System.out.println("Done");
    }

    public static void convertUntranslateableFile(String path, String appName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
        File valueFolder = new File(path + appName);
        File file = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
        Document inDoc = inDocBuilder.parse(file);
        NodeList inList = inDoc.getElementsByTagName("string");


        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElement("resources");
        document.appendChild(root);

        for (int i = 0; i < inList.getLength(); i++) {
            Element element = (Element) inList.item(i);
            String name = "";
            NamedNodeMap curAttr = element.getAttributes();
            for (int j = 0; j < curAttr.getLength(); j++) {
                Node attr = curAttr.item(j);
                if (attr.getNodeName().equals("name"))
                    name = attr.getNodeValue();
            }
            Element item = document.createElement("item");
            root.appendChild(item);
            item.setAttribute("folder", "all");
            item.setAttribute("application", appName);
            item.setAttribute("file", "strings.xml");
            item.setAttribute("name", name);

        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter outputXmlStringWriter = new StringWriter();
        transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
        String outputXmlString = outputXmlStringWriter.toString()
                .replaceAll("<item", "\n\t<item")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(path + "\\" + appName + "\\out.xml");
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
    }
}
