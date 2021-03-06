package com.anhtt.miui.translation.helper;

import com.anhtt.miui.translation.helper.model.*;
import com.anhtt.miui.translation.helper.model.res.Item;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.anhtt.miui.translation.helper.model.TargetDevice.distinctByKey;

public class Utils {
    public static void delete(File file) {
        if (file == null || !file.exists()) return;
        File[] listFile = file.listFiles();
        if (listFile == null) return;
        for (File childFile : listFile) {

            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                Path fp = childFile.toPath();
                try {
                    Files.delete(fp);
                } catch (NoSuchFileException x) {
                    System.err.format("%s: no such" + " file or directory%n", fp);
                } catch (DirectoryNotEmptyException x) {
                    System.err.format("%s not empty%n", fp);
                } catch (IOException x) {
                    // File permission problems are caught here.
                    x.printStackTrace();
                }
            }
        }
        try {

            if (!file.delete()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String nodeToString(Node node) {
        if (node.getChildNodes().getLength() == 1) return node.getTextContent();
        StringWriter sw = new StringWriter();
        try {
            return nodeListToString(node.getChildNodes());
        } catch (TransformerException te) {
        }
        return sw.toString();
    }

    public static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

    public static String nodeListToString(NodeList nodes) throws TransformerException {
        DOMSource source = new DOMSource();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        for (int i = 0; i < nodes.getLength(); ++i) {
            source.setNode(nodes.item(i));
            transformer.transform(source, result);
        }

        return writer.toString();
    }

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

    public static void writeStringsToFile(String path, Map<String, StringRes> stringRes) {
        if (stringRes.size() == 0) return;
        try {
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (StringRes string : stringRes.values().stream().filter(distinctByKey(StringRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
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

    public static void writeWrongPluralToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeWrongPluralToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeWrongArrayToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeWrongArrayToFile(absolutePath, wrongApplication);
        }
    }

    private static boolean isIgnoredApplication(String name) {
        List<String> strings = Arrays.asList("XiaomiEUTools.apk", "AutoDialer.apk", "Cit.apk", "cit_nikel.apk", "mediatek-res.apk", "SimContacts.apk"
                , "PrintRecommendationService.apk", "BSPTelephonyDevTool.apk", "BtTool.apk", "cit.apk", "EngineerMode.apk", "FactoryKitTest.apk", "FactoryMode.apk"
                , "imssettings.apk", "Mimoji.apk", "NQNfcNci.apk", "Traceur.apk", "Cit.apk");
        return strings.contains(name);
    }

    public static void writeUnTranslatedFromAllStringToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
//            if (!isIgnoredApplication(wrongApplication.getName()))
            writeUnTranslatedFromAllStringToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeUnTranslatedFromAllStringToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginStrings().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongStringRes string : wrongApplication.getWrongTranslatedFromAllOriginStrings().stream().filter(distinctByKey(WrongStringRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\stringsFromAll.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeUnTranslatedStringToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
//            if (!isIgnoredApplication(wrongApplication.getName()))
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

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOriginStrings().stream().filter(distinctByKey(WrongStringRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
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
        if (wrongApplication.getWrongTranslatedOriginStrings().size() == 0
                || wrongApplication.getWrongTranslatedOriginStrings().stream().anyMatch(wrongStringRes -> !wrongStringRes.getDevices().contains("all"))
        ) return;
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

            for (WrongStringRes string : wrongApplication.getWrongTranslatedOriginStrings().stream().filter(distinctByKey(WrongStringRes::getName)).collect(Collectors.toList())) {
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
        if (wrongApplication.getMapWrongTranslatedOriginArrays().size() == 0
                || wrongApplication.getMapWrongTranslatedOriginArrays().values().stream().anyMatch(wrongStringRes -> !wrongStringRes.getDevices().contains("all"))
        ) return;
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

            for (WrongArrayRes string : wrongApplication.getMapWrongTranslatedOriginArrays().values().stream().filter(distinctByKey(WrongArrayRes::getName)).collect(Collectors.toList())) {
                if (!string.getDevices().contains("all")) {
                    Element stringNode = document.createElement("string-array");
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
                    Element stringNode = document.createElement("string-array");
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
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
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

    public static void writeWrongPluralToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOriginPlurals().size() == 0
                || wrongApplication.getWrongTranslatedOriginPlurals().stream().anyMatch(wrongStringRes -> !wrongStringRes.getDevices().contains("all"))
        ) return;
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

            for (WrongPluralRes string : wrongApplication.getWrongTranslatedOriginPlurals().stream().filter(distinctByKey(WrongPluralRes::getName)).collect(Collectors.toList())) {
                if (!string.getDevices().contains("all")) {
                    Element stringNode = document.createElement(string.getArrayType());
                    root.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                    for (Item item : string.getItems()) {
                        Element itemNode = document.createElement("item");
                        stringNode.appendChild(itemNode);
                        itemNode.setTextContent(item.getValue());
                        itemNode.setAttribute("quantity", item.getQuantity());
                    }
                    Element countNode = document.createElement("count");
                    stringNode.appendChild(countNode);
                    countNode.setTextContent(string.getDevices().size() + "");

                    Element childNode = document.createElement("device");
                    stringNode.appendChild(childNode);
                    childNode.setTextContent(string.getDevices().toString());


                } else {
                    Element stringNode = document.createElement(string.getArrayType());
                    root.appendChild(stringNode);
                    stringNode.setAttribute("name", string.getName());
                    if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                    for (Item item : string.getItems()) {
                        Element itemNode = document.createElement("item");
                        stringNode.appendChild(itemNode);
                        itemNode.setTextContent(item.getValue());
                        itemNode.setAttribute("quantity", item.getQuantity());
                    }
                }

            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\plurals.xml");
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
        if (wrongApplication.getMapWrongTranslatedOriginArrays().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongArrayRes string : wrongApplication.getMapWrongTranslatedOriginArrays().values().stream().filter(distinctByKey(WrongArrayRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
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

    public static void writeUnTranslatedFromAllArrayToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeUnTranslatedFromAllArrayToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeUnTranslatedFromAllArrayToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginArrays().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongArrayRes string : wrongApplication.getWrongTranslatedFromAllOriginArrays().stream().filter(distinctByKey(WrongArrayRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\arraysFromAll.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeUnTranslatedPluralToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeUnTranslatedPluralToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeUnTranslatedPluralToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedOriginPlurals().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);

            for (WrongPluralRes string : wrongApplication.getWrongTranslatedOriginPlurals().stream().filter(distinctByKey(WrongPluralRes::getName)).collect(Collectors.toList())) {
                Element stringNode = document.createElement(string.getArrayType());
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                for (Item item : string.getItems()) {
                    Element itemNode = document.createElement("item");
                    stringNode.appendChild(itemNode);
                    itemNode.setTextContent(item.getValue());
                    itemNode.setAttribute("quantity", item.getQuantity());
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\plurals.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeUnTranslatedFromAllPluralToFile(String absolutePath, List<WrongApplication> wrongApplications) {
        for (WrongApplication wrongApplication : wrongApplications) {
            writeUnTranslatedFromAllPluralToFile(absolutePath, wrongApplication);
        }
    }

    public static void writeUnTranslatedFromAllPluralToFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginPlurals().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) dir.mkdirs();

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);


            for (WrongPluralRes string : wrongApplication.getWrongTranslatedFromAllOriginPlurals().stream().filter(distinctByKey(WrongPluralRes::getName)).collect(Collectors.toList())) {
                Element stringNode = document.createElement(string.getArrayType());
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                for (Item item : string.getItems()) {
                    Element itemNode = document.createElement("item");
                    stringNode.appendChild(itemNode);
                    itemNode.setTextContent(item.getValue());
                    itemNode.setAttribute("quantity", item.getQuantity());
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "\n<resources")
                    .replaceAll("</resources", "\n</resources");

            FileOutputStream outputXml = new FileOutputStream(path + "\\" + wrongApplication.getName() + "\\pluralsFromAll.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void addIgnoredFile(String path, String filteredPath, List<WrongApplication> untranslatedApplications) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File sourceFile = new File(path + "\\MIUI12\\MIUI12_untranslateable.xml");
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
            for (WrongArrayRes string : wrongApplication.getMapWrongTranslatedOriginArrays().values()) {
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
                .replaceAll("<count", "\n\t<count")
                .replaceAll("<item", "\n\t<item")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(sourceFile.getAbsolutePath());
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
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

        File oldOut = new File(path + "\\out.xml");
        if (oldOut.exists()) {
            Document oldOutDoc = inDocBuilder.parse(oldOut);
            NodeList oldOutList = oldOutDoc.getElementsByTagName("item");
            for (int i = 0; i < oldOutList.getLength(); i++) {
                Node node = oldOutList.item(i);
                Node firstDocImportedNode = document.importNode(node, true);
                root.appendChild(firstDocImportedNode);
            }
        }

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
                .replaceAll("<count", "\n\t<count")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(path + "\\out.xml");
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
    }

    public static void convertUntranslateableArrayFile(String path, String appName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
        File valueFolder = new File(path + appName);
        File file = new File(valueFolder.getAbsolutePath() + "\\arrays.xml");
        Document inDoc = inDocBuilder.parse(file);
        NodeList inList = inDoc.getElementsByTagName("string-array");


        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElement("resources");
        document.appendChild(root);
        File oldOut = new File(path + "\\out.xml");
        if (oldOut.exists()) {
            Document oldOutDoc = inDocBuilder.parse(oldOut);
            NodeList oldOutList = oldOutDoc.getElementsByTagName("item");
            for (int i = 0; i < oldOutList.getLength(); i++) {
                Node node = oldOutList.item(i);
                Node firstDocImportedNode = document.importNode(node, true);
                root.appendChild(firstDocImportedNode);
            }
        }
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
            item.setAttribute("file", "arrays.xml");
            item.setAttribute("name", name);

        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter outputXmlStringWriter = new StringWriter();
        transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
        String outputXmlString = outputXmlStringWriter.toString()
                .replaceAll("<item", "\n\t<item")
                .replaceAll("<count", "\n\t<count")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(path + "\\out.xml");
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
    }

    public static void convertUntranslateablePluralFile(String path, String appName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
        File valueFolder = new File(path + appName);
        File file = new File(valueFolder.getAbsolutePath() + "\\plurals.xml");
        Document inDoc = inDocBuilder.parse(file);
        NodeList inList = inDoc.getElementsByTagName("string-array");


        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElement("resources");
        document.appendChild(root);
        File oldOut = new File(path + "\\out.xml");
        if (oldOut.exists()) {
            Document oldOutDoc = inDocBuilder.parse(oldOut);
            NodeList oldOutList = oldOutDoc.getElementsByTagName("item");
            for (int i = 0; i < oldOutList.getLength(); i++) {
                Node node = oldOutList.item(i);
                Node firstDocImportedNode = document.importNode(node, true);
                root.appendChild(firstDocImportedNode);
            }
        }
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
            item.setAttribute("file", "arrays.xml");
            item.setAttribute("name", name);

        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter outputXmlStringWriter = new StringWriter();
        transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
        String outputXmlString = outputXmlStringWriter.toString()
                .replaceAll("<item", "\n\t<item")
                .replaceAll("<count", "\n\t<count")
                .replaceAll("<device", "\n\t\t<device")
                .replaceAll("<allDevice", "\n<allDevice")
                .replaceAll("</allDevice", "\n</allDevice")
                .replaceAll("<resources", "\n<resources")
                .replaceAll("</resources", "\n</resources");

        FileOutputStream outputXml = new FileOutputStream(path + "\\out.xml");
        outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
        outputXml.close();
    }

    public static void writeUnTranslatedFromAllStringToExistFile(String pathToWrite, String absolutePath, List<WrongApplication> untranslatedFromAllApplications) {
        for (WrongApplication wrongApplication : untranslatedFromAllApplications) {
//            if (!isIgnoredApplication(wrongApplication.getName()))
            writeUnTranslatedFromAllStringToExistFile(pathToWrite, wrongApplication);
        }
    }

    public static void writeUnTranslatedFromAllArrayToExistFile(String pathToWrite, String absolutePath, List<WrongApplication> untranslatedFromAllApplications) {
        for (WrongApplication wrongApplication : untranslatedFromAllApplications) {
//            if (!isIgnoredApplication(wrongApplication.getName()))
            writeUnTranslatedFromAllArrayToExistFile(pathToWrite, wrongApplication);
        }
    }

    public static void writeUnTranslatedFromAllPluralToExistFile(String pathToWrite, String absolutePath, List<WrongApplication> untranslatedFromAllApplications) {
        for (WrongApplication wrongApplication : untranslatedFromAllApplications) {
//            if (!isIgnoredApplication(wrongApplication.getName()))
            writeUnTranslatedFromAllPluralToExistFile(pathToWrite, wrongApplication);
        }
    }
    //Read file content into string with - Files.lines(Path path, Charset cs)

    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    //Read file content into string with - Files.lines(Path path, Charset cs)

    private static String readLineByLineJava8IgnoreFirst(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        AtomicBoolean ignoredFirst = new AtomicBoolean(false);
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(string -> {
                if (ignoredFirst.get()) {
                    contentBuilder.append(string).append("\n");
                }
                ignoredFirst.set(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString().trim();
    }

    private static String readLineByLineJava8IgnoreLast(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        AtomicBoolean ignoredFirst = new AtomicBoolean(false);
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(string -> {
                if (ignoredFirst.get()) {
                    contentBuilder.append(string).append("\n");
                }
                ignoredFirst.set(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static void writeUnTranslatedFromAllStringToExistFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginStrings().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) return;


            DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
            File valueFolder = new File(path + "\\" + wrongApplication.getName() + "\\res\\values-vi");
            File file = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
            if (!file.exists()) file.createNewFile();


            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);
            String oldContent = "";
            File oldOut = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
            if (oldOut.exists()) {
                oldContent = readLineByLineJava8(oldOut.getAbsolutePath());
            }

            for (WrongStringRes string : wrongApplication.getWrongTranslatedFromAllOriginStrings().stream().filter(distinctByKey(WrongStringRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "")
                    .replaceAll("</resources", "")
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            FileOutputStream outputXml = new FileOutputStream(path + "\\strings.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();

            String out = readLineByLineJava8IgnoreFirst(path + "\\strings.xml");
            out = out.replaceAll("</string>>", "</string>");
            if (!TextUtils.isEmpty(oldContent.trim())) {
                String toWrite = oldContent.replace("</resources>", "\t" + out);
                toWrite = toWrite + "</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\strings.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();

            } else {
                String toWrite = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
                toWrite = toWrite + "\n<resources>\n";
                toWrite = toWrite + "\t" + out;
                toWrite = toWrite + "\n</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\strings.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();
            }
            new File(path + "\\strings.xml").delete();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeUnTranslatedFromAllArrayToExistFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginArrays().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) return;
            if (wrongApplication.getWrongTranslatedFromAllOriginArrays().size() == 0) return;

            DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
            File valueFolder = new File(path + "\\" + wrongApplication.getName() + "\\res\\values-vi");
            File file = new File(valueFolder.getAbsolutePath() + "\\arrays.xml");
            if (!file.exists()) file.createNewFile();


            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);
            String oldContent = "";
            File oldOut = new File(valueFolder.getAbsolutePath() + "\\arrays.xml");
            if (oldOut.exists()) {
                oldContent = readLineByLineJava8(oldOut.getAbsolutePath());
            }

            for (WrongArrayRes string : wrongApplication.getWrongTranslatedFromAllOriginArrays().stream().filter(distinctByKey(WrongArrayRes::getName)).collect(Collectors.toList())) {
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
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "")
                    .replaceAll("</resources", "")
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            FileOutputStream outputXml = new FileOutputStream(path + "\\arrays.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();

            String out = readLineByLineJava8IgnoreFirst(path + "\\arrays.xml");
            out = out.replaceAll("</string-array>>", "</string-array>");
            if (!TextUtils.isEmpty(oldContent.trim())) {
                String toWrite = oldContent.replace("</resources>", "\t" + out);
                toWrite = toWrite + "</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\arrays.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();

            } else {
                String toWrite = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
                toWrite = toWrite + "\n<resources>\n";
                toWrite = toWrite + "\t" + out;
                toWrite = toWrite + "\n</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\arrays.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();
            }
            new File(path + "\\arrays.xml").delete();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }

    public static void writeUnTranslatedFromAllPluralToExistFile(String path, WrongApplication wrongApplication) {
        if (wrongApplication.getWrongTranslatedFromAllOriginPlurals().size() == 0) return;
        try {
            File dir = new File(path + "\\" + wrongApplication.getName());
            if (!dir.exists()) return;


            DocumentBuilderFactory inFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder inDocBuilder = inFactory.newDocumentBuilder();
            File valueFolder = new File(path + "\\" + wrongApplication.getName() + "\\res\\values-vi");
            File file = new File(valueFolder.getAbsolutePath() + "\\plurals.xml");
            if (!file.exists()) file.createNewFile();


            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("resources");
            document.appendChild(root);
            String oldContent = "";
            File oldOut = new File(valueFolder.getAbsolutePath() + "\\plurals.xml");
            if (oldOut.exists()) {
                oldContent = readLineByLineJava8(oldOut.getAbsolutePath());
            }

            for (WrongPluralRes string : wrongApplication.getWrongTranslatedFromAllOriginPlurals().stream().filter(distinctByKey(WrongPluralRes::getName)).collect(Collectors.toList())) {
                Element stringNode = document.createElement(string.getArrayType());
                root.appendChild(stringNode);
                stringNode.setAttribute("name", string.getName());
                if (!string.isFormatted()) stringNode.setAttribute("formatted", string.isFormatted() + "");
                for (Item item : string.getItems()) {
                    Element itemNode = document.createElement("item");
                    stringNode.appendChild(itemNode);
                    itemNode.setTextContent(item.getValue());
                    itemNode.setAttribute("quantity", item.getQuantity());
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StringWriter outputXmlStringWriter = new StringWriter();
            transformer.transform(domSource, new StreamResult(outputXmlStringWriter));
            String outputXmlString = outputXmlStringWriter.toString()
                    .replaceAll("<count", "\n\t<count")
                    .replaceAll("<string", "\n\t<string")
                    .replaceAll("</string", "\n\t</string")
                    .replaceAll("<array", "\n\t<array")
                    .replaceAll("</array", "\n\t</array")
                    .replaceAll("<plural", "\n\t<plural")
                    .replaceAll("</plural", "\n\t</plural")
                    .replaceAll("<item", "\n\t\t<item")
                    .replaceAll("<device", "\n\t\t<device")
                    .replaceAll("<allDevice", "\n<allDevice")
                    .replaceAll("</allDevice", "\n</allDevice")
                    .replaceAll("<resources", "")
                    .replaceAll("</resources", "")
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            FileOutputStream outputXml = new FileOutputStream(path + "\\plurals.xml");
            outputXml.write(outputXmlString.getBytes(StandardCharsets.UTF_8));
            outputXml.close();

            String out = readLineByLineJava8IgnoreFirst(path + "\\plurals.xml");
            out = out.replaceAll("</plurals>>", "</plurals>");
            if (!TextUtils.isEmpty(oldContent)) {
                String toWrite = oldContent.replace("</resources>", "\t" + out);
                toWrite = toWrite + "</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\plurals.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();

            } else {
                String toWrite = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
                toWrite = toWrite + "\n<resources>\n";
                toWrite = toWrite + "\t" + out;
                toWrite = toWrite + "\n</resources>";
                outputXml = new FileOutputStream(valueFolder.getAbsolutePath() + "\\plurals.xml");
                outputXml.write(toWrite.getBytes(StandardCharsets.UTF_8));
                outputXml.close();
            }
            new File(path + "\\plurals.xml").delete();
        } catch (ParserConfigurationException | TransformerException | IOException pce) {
            pce.printStackTrace();
        }
    }
}