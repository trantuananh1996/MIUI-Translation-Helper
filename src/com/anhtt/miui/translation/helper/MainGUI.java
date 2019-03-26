/*
 * Created by JFormDesigner on Wed Mar 13 09:55:27 ICT 2019
 */

package com.anhtt.miui.translation.helper;

import javax.swing.border.*;
import javax.swing.event.*;

import com.anhtt.miui.translation.helper.model.*;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author unknown
 */
public class MainGUI extends JFrame {
    TranslatedDevice transDevices;
    List<OriginDevice> originDevices;
    List<OriginDevice> specificOriginDevices;
    public static JTextArea tvLogStatic;
    public static List<UnTranslateable> unTranslateables;
    public static List<UnTranslateable> autoIgnoredList;
    StringWorker worker;
    public List<WrongApplication> wrongApplications = new ArrayList<>();
    public List<WrongApplication> untranslatedApplications = new ArrayList<>();

    public MainGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
        setSize(500, 500);
        pack();
        setLocationRelativeTo(getOwner());
        tvLogStatic = tvLog;
        btnViewUntranslatedArray.setVisible(false);
        btnViewUnTranslatedString.setVisible(false);
        btnMoveToIgnored.setVisible(false);
        //TODO: For fast testing
        edtOriginFolder.setText("C:\\Users\\trant\\Documents\\Github\\Xiaomi.eu-MIUIv10-XML-Compare");
        edtTranslatedFolder.setText("C:\\Users\\trant\\Documents\\Github\\MIUI-10-XML-Vietnamese\\Vietnamese");
        edtFilteredFolder.setText("C:\\Users\\trant\\Documents\\Filtered");
        edtResCheckFolder.setText("C:\\Users\\trant\\Documents\\Github\\MA-XML-CHECK-RESOURCES");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);

    }

    private void btnStartMouseClicked(MouseEvent e) {
        SearchOptions.searchArray = cbArray.isSelected();
        SearchOptions.searchPlural = cbPlural.isSelected();
        SearchOptions.searchString = cbString.isSelected();
        SearchOptions.filterCanRemove = cbFindCanRemove.isSelected();
        SearchOptions.filterFormatted = cbFindFormatedString.isSelected();
        SearchOptions.filterUnTranslated = cbFindUntranslated.isSelected();
        SearchOptions.deepFilterForUnTranslated = cbFindInAllApp.isSelected();

        if (!SearchOptions.searchPlural && !SearchOptions.searchArray && !SearchOptions.searchString) {
            JOptionPane.showMessageDialog(null, "Chưa chọn mục cần lọc");
            return;
        }
        String originPath = edtOriginFolder.getText();
        String translationPath = edtTranslatedFolder.getText();
        String filteredPath = edtFilteredFolder.getText();

        File originFolder = checkOrginFolder(originPath);
        File translationFolder = checkTranslationFolder(translationPath);
        File filteredFolder = new File(filteredPath);
        if (originFolder == null) {
            JOptionPane.showMessageDialog(null, "Kiểm tra thư mục ngôn ngữ gốc");
            return;
        }
        if (translationFolder == null) {
            JOptionPane.showMessageDialog(null, "Kiểm tra thư mục ngôn ngữ đã dịch");
            return;
        }

        if (!filteredFolder.exists()) filteredFolder.mkdirs();
        else {
            try {
                deleteDirectoryStream(filteredFolder.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            filteredFolder.mkdirs();
        }

        tvLog.append("Đang chuẩn bị...");

        worker = new StringWorker(originFolder, translationFolder, filteredFolder);
        worker.execute();
    }

    private File checkTranslationFolder(String translationPath) {
        File file = new File(translationPath);
        if (!file.exists()) return null;
        if (!file.isDirectory()) return null;
        if (file.listFiles() == null) return null;
        int folderCount = 0;
        String lastFolderName = "";
        for (File child : Objects.requireNonNull(file.listFiles())) {
            if (child.isDirectory() && !child.getName().contains(".")) {
                folderCount++;
                lastFolderName = child.getName();
            }
        }
        if (!translationPath.endsWith("\\")) translationPath += "\\";
        String finalName = translationPath + lastFolderName;
        if (folderCount == 1) return new File(finalName);
        else if (folderCount > 1) return file;
        return null;
    }

    @Nullable
    private File checkOrginFolder(String originPath) {
        File file = new File(originPath);
        if (!file.exists()) return null;
        if (!file.isDirectory()) return null;
        if (file.listFiles() == null) return null;
        for (File child : Objects.requireNonNull(file.listFiles())) {
            if (child.isDirectory())
                if (child.getName().equals("Diff_v9-v10")) return file;
        }
        return null;
    }

    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public final class StringWorker extends SwingWorker<String, String> {
        File originFolder;
        File translationFolder;
        File filteredFolder;

        public StringWorker(File originFolder, File translationFolder, File filteredFolder) {
            this.originFolder = originFolder;
            this.translationFolder = translationFolder;
            this.filteredFolder = filteredFolder;
        }

        public void sendLog(String string) {
            publish(string);
        }

        @Override
        protected String doInBackground() throws Exception {
            try {
                unTranslateables = createUnTranslateable();

            } catch (IOException | SAXException | ParserConfigurationException e1) {
                e1.printStackTrace();
            }
            try {
                autoIgnoredList = createAutoIgnoredList();

            } catch (IOException | SAXException | ParserConfigurationException e1) {
                e1.printStackTrace();
            }


            File transDevicesFolder = new File(translationFolder.getAbsolutePath() + "\\main");
            if (!transDevicesFolder.exists()) return null;
            transDevices = TranslatedDevice.create(transDevicesFolder.getAbsolutePath(), false);


            File specificDevicesFolder = new File(translationFolder.getAbsolutePath() + "\\device");
            specificOriginDevices = new ArrayList<>();
            for (File file : Objects.requireNonNull(specificDevicesFolder.listFiles())) {
                if (file.isDirectory()) {
                    OriginDevice originDevice = OriginDevice.create(file.getAbsolutePath());
                    if (originDevice != null) specificOriginDevices.add(originDevice);
                }
            }

            originDevices = createOriginDevices(this, originFolder, transDevices, specificOriginDevices);


            originDevices.forEach(originDevice -> {
                originDevice.getApps().forEach(application -> {
                    if (cbFindFormatedString.isSelected()) {
                        groupString(application.getName(), application.getWrongTranslatedOriginStrings(), originDevice, wrongApplications);
                        groupArray(application.getName(), application.getWrongTranslatedOriginArrays(), originDevice, wrongApplications);
                        groupPlural(application.getName(), application.getWrongTranslatedOriginPlurals(), originDevice, wrongApplications);
                    }

                    if (cbFindUntranslated.isSelected()) {
                        groupString(application.getName(), application.getUnTranslatedStrings(), originDevice, untranslatedApplications);
                        groupArray(application.getName(), application.getUnTranslatedArrays(), originDevice, untranslatedApplications);
                        groupPlural(application.getName(), application.getWrongTranslatedOriginPlurals(), originDevice, wrongApplications);
                    }
                    if (cbFindCanRemove.isSelected())
                        Utils.writeStringsToFile(filteredFolder.getAbsolutePath() + "\\Can Remove\\" + originDevice.getName() + "\\" + application.getName(), application.getOriginEqualTranslatedStrings());

                });
            });

            if (cbFindFormatedString.isSelected()) {
                Utils.writeWrongToFile(filteredFolder.getAbsolutePath() + "\\Wrongs", wrongApplications);
                Utils.writeUnTranslatedArrayToFile(filteredFolder.getAbsolutePath() + "\\WrongArray", wrongApplications);

            }
            if (cbFindUntranslated.isSelected()) {
                Utils.writeUnTranslatedStringToFile(filteredFolder.getAbsolutePath() + "\\UnTranslated", untranslatedApplications);
                Utils.writeUnTranslatedArrayToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedArray", untranslatedApplications);
                Utils.writeUnTranslatedPluralToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedPlural", untranslatedApplications);

            }
            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            for (String string : chunks) tvLogStatic.append("\n" + string);
        }

        @Override
        protected void done() {
            tvLogStatic.append("\n" + "Đã xong. Kiểm tra thư mục kết quả");
            btnViewUntranslatedArray.setVisible(true);
            btnViewUnTranslatedString.setVisible(true);
        }
    }

    private void groupString(String appName, List<StringRes> stringToGroups, OriginDevice originDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        for (int i = 0; i < stringToGroups.size(); i++) {
            StringRes origin = stringToGroups.get(i);
            wrongApplication.addOrigin(originDevices.size(), originDevice, origin);
        }
        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupArray(String appName, List<ArrayRes> stringToGroups, OriginDevice originDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        for (int i = 0; i < stringToGroups.size(); i++) {
            ArrayRes origin = stringToGroups.get(i);
            wrongApplication.addOrigin(originDevices.size(), originDevice, origin);
        }
        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupPlural(String appName, List<PluralRes> stringToGroups, OriginDevice originDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        for (int i = 0; i < stringToGroups.size(); i++) {
            PluralRes origin = stringToGroups.get(i);
            wrongApplication.addOrigin(originDevices.size(), originDevice, origin);
        }
        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private List<UnTranslateable> createAutoIgnoredList() throws ParserConfigurationException, IOException, SAXException {
        List<UnTranslateable> unTranslateables = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();

        File sourceFile = new File(edtResCheckFolder.getText() + "\\MIUI10\\MIUI10_auto_ignorelist.xml");
        if (!sourceFile.exists()) return new ArrayList<>();
        Document doc = docBuilder.parse(sourceFile);
        NodeList list = doc.getElementsByTagName("item");
        for (int i = 0; i < list.getLength(); i++) {
            String name = "";
            String folder = "";
            String application = "";
            String file = "";
            Element cur = (Element) list.item(i);
            NamedNodeMap curAttr = cur.getAttributes();
            for (int j = 0; j < curAttr.getLength(); j++) {
                Node attr = curAttr.item(j);
                if (attr.getNodeName().equals("name"))
                    name = attr.getNodeValue();
                if (attr.getNodeName().equals("folder"))
                    folder = attr.getNodeValue();
                if (attr.getNodeName().equals("application"))
                    application = attr.getNodeValue();
                if (attr.getNodeName().equals("file"))
                    file = attr.getNodeValue();
            }
            if (name != null && name.length() > 0) {
                UnTranslateable unTranslateable = new UnTranslateable();
                unTranslateable.setName(name);
                unTranslateable.setFolder(folder);
                unTranslateable.setApplication(application);
                unTranslateable.setFile(file);

                unTranslateables.add(unTranslateable);
            }
        }

        unTranslateables.removeIf(unTranslateable ->
                unTranslateable.getName() == null
        );
        return unTranslateables;

    }

    private List<UnTranslateable> createUnTranslateable() throws IOException, SAXException, ParserConfigurationException {
        List<UnTranslateable> unTranslateables = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();

        File sourceFile = new File(edtResCheckFolder.getText() + "\\MIUI10\\MIUI10_untranslateable.xml");
        if (!sourceFile.exists()) return new ArrayList<>();
        Document doc = docBuilder.parse(sourceFile);
        NodeList list = doc.getElementsByTagName("item");
        for (int i = 0; i < list.getLength(); i++) {
            String name = "";
            String folder = "";
            String application = "";
            String file = "";
            Element cur = (Element) list.item(i);
            NamedNodeMap curAttr = cur.getAttributes();
            for (int j = 0; j < curAttr.getLength(); j++) {
                Node attr = curAttr.item(j);
                if (attr.getNodeName().equals("name"))
                    name = attr.getNodeValue();
                if (attr.getNodeName().equals("folder"))
                    folder = attr.getNodeValue();
                if (attr.getNodeName().equals("application"))
                    application = attr.getNodeValue();
                if (attr.getNodeName().equals("file"))
                    file = attr.getNodeValue();
            }
            if (name != null && name.length() > 0) {
                UnTranslateable unTranslateable = new UnTranslateable();
                unTranslateable.setName(name);
                unTranslateable.setFolder(folder);
                unTranslateable.setApplication(application);
                unTranslateable.setFile(file);

                unTranslateables.add(unTranslateable);
            }
        }

        unTranslateables.removeIf(unTranslateable ->
                unTranslateable.getName() == null
        );
        return unTranslateables;
    }

    private List<OriginDevice> createOriginDevices(StringWorker swingWorker, File originFolder, TranslatedDevice transDevices, List<OriginDevice> specificOriginDevices) {
        File originDevices = new File(originFolder.getAbsolutePath());
        if (!originDevices.exists()) new ArrayList<>();
        List<OriginDevice> devices = new ArrayList<>();
        for (File file : Objects.requireNonNull(originDevices.listFiles())) {
            if (file.isDirectory()) {
                OriginDevice originDevice = OriginDevice.create(swingWorker, file.getAbsolutePath(), transDevices, specificOriginDevices);
                if (originDevice != null) devices.add(originDevice);
            }
        }
        return devices;
    }


    private void btnPickOriginMouseClicked(MouseEvent e) {
        JFileChooser fileChooser = createFileChooser(this);
        edtOriginFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private void btnPickTranslatedMouseClicked(MouseEvent e) {
        JFileChooser fileChooser = createFileChooser(this);
        edtTranslatedFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private JFileChooser createFileChooser(final JFrame frame) {

        String filename = File.separator + "tmp";
        JFileChooser fileChooser = new JFileChooser(new File(filename));

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(frame);
        return fileChooser;
    }

    private void btnPickFilteredMouseClicked(MouseEvent e) {
        JFileChooser fileChooser = createFileChooser(this);
        edtFilteredFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private void btnPickResCheckFolderMouseClicked(MouseEvent e) {
        JFileChooser fileChooser = createFileChooser(this);
        edtResCheckFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private void btnMoveToIgnoredMouseClicked(MouseEvent e) {
        if (untranslatedApplications != null && untranslatedApplications.size() > 0) {
            try {
                Utils.addIgnoredFile(edtResCheckFolder.getText(), edtFilteredFolder.getText(), untranslatedApplications);
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException e1) {
                e1.printStackTrace();
            }
        } else JOptionPane.showMessageDialog(null, "Chưa lọc/không có file chưa dịch");

    }


    private void btnStopMouseClicked(MouseEvent e) {
        worker.done();
        worker.cancel(true);
        tvLog.append("\n" + "Đã hủy quá trình");
    }

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void btnCheckDuplicateMouseClicked(MouseEvent e) {

        tvLog.append("\nKiểm tra trùng lặp");
        File transDevicesFolder = new File(edtTranslatedFolder.getText() + "\\main");
        if (!transDevicesFolder.exists()) return;
        TranslatedDevice transDevices = TranslatedDevice.create(transDevicesFolder.getAbsolutePath(), false);
        if (transDevices == null) return;
        transDevices.getApps().forEach(application -> {
            tvLog.append("\n" + application.getName());
            application.setDuplicateString(getDuplicates(application.getOriginString()));
            if (application.getDuplicateString() == null || application.getDuplicateString().size() == 0) {
                tvLog.append("\nKhông có trùng lặp");
            } else tvLog.append("\n Có trùng lặp");
        });
    }

    public static List<StringRes> getDuplicates(final List<StringRes> personList) {
        return getDuplicatesMap(personList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Map<String, List<StringRes>> getDuplicatesMap(List<StringRes> personList) {
        return personList.stream().collect(Collectors.groupingBy(StringRes::getGroupValue));
    }

    private void btnViewUnTranslatedStringMouseClicked(MouseEvent e) {
        JFrame f = new JFrame("Test");
        List<WrongApplication> applications = untranslatedApplications.stream().filter(wrongApplication -> wrongApplication.getWrongTranslatedOriginStrings().size() > 0)
                .collect(Collectors.toList());
        JList<WrongApplication> list = new JList(applications.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    System.out.println("Converting " + applications.get(index).getName());
                    try {
                        Utils.convertUntranslateableFile(edtFilteredFolder.getText() + "\\UnTranslated\\", applications.get(index).getName());
                    } catch (ParserConfigurationException | IOException | SAXException | TransformerException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        f.add(list);
        f.pack();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void btnViewUntranslatedArrayMouseClicked(MouseEvent e) {
        JFrame f = new JFrame("Test");
        List<WrongApplication> applications = untranslatedApplications.stream().filter(wrongApplication -> wrongApplication.getWrongTranslatedOriginArrays().size() > 0)
                .collect(Collectors.toList());
        JList<WrongApplication> list = new JList(applications.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    System.out.println("Converting " + applications.get(index).getName());
                    try {
                        Utils.convertUntranslateableArrayFile(edtFilteredFolder.getText() + "\\UnTranslatedArray\\", applications.get(index).getName());
                    } catch (ParserConfigurationException | IOException | SAXException | TransformerException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        f.add(list);
        f.pack();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void cbFindUntranslatedStateChanged(ChangeEvent e) {
        cbFindInAllApp.setVisible(cbFindUntranslated.isSelected());
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Trần Tuấn Anh
        panel1 = new JPanel();
        panel3 = new JPanel();
        label2 = new JLabel();
        edtOriginFolder = new JTextField();
        btnPickOrigin = new JButton();
        label3 = new JLabel();
        edtTranslatedFolder = new JTextField();
        btnPickTranslated = new JButton();
        label6 = new JLabel();
        edtFilteredFolder = new JTextField();
        btnPickFiltered = new JButton();
        label1 = new JLabel();
        edtResCheckFolder = new JTextField();
        btnPickResCheckFolder = new JButton();
        panel4 = new JPanel();
        panel7 = new JPanel();
        cbString = new JCheckBox();
        cbArray = new JCheckBox();
        cbPlural = new JCheckBox();
        panel8 = new JPanel();
        cbFindFormatedString = new JCheckBox();
        cbFindCanRemove = new JCheckBox();
        checkBox1 = new JCheckBox();
        cbFindUntranslated = new JCheckBox();
        cbFindInAllApp = new JCheckBox();
        panel5 = new JPanel();
        panel2 = new JPanel();
        btnStart = new JButton();
        btnStop = new JButton();
        btnCheckDuplicate = new JButton();
        btnViewUnTranslatedString = new JButton();
        btnMoveToIgnored = new JButton();
        btnViewUntranslatedArray = new JButton();
        panel6 = new JPanel();
        label7 = new JLabel();
        scrollPane1 = new JScrollPane();
        tvLog = new JTextArea();

        //======== this ========

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), getBorder()));
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                if ("border".equals(e.getPropertyName())) throw new RuntimeException();
            }
        });

        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "5dlu, $lcgap, 402dlu, $lcgap",
                    "2*(default, $lgap), default"));

            //======== panel3 ========
            {
                panel3.setLayout(new FormLayout(
                        "default, $lcgap, 275dlu, $lcgap, 26dlu",
                        "3*(default, $lgap), default"));

                //---- label2 ----
                label2.setText("Th\u01b0 m\u1ee5c g\u1ed1c");
                panel3.add(label2, CC.xy(1, 1));
                panel3.add(edtOriginFolder, CC.xywh(3, 1, 2, 1));

                //---- btnPickOrigin ----
                btnPickOrigin.setText("...");
                btnPickOrigin.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickOriginMouseClicked(e);
                    }
                });
                panel3.add(btnPickOrigin, CC.xy(5, 1));

                //---- label3 ----
                label3.setText("Th\u01b0 m\u1ee5c \u0111\u00e3 d\u1ecbch");
                panel3.add(label3, CC.xy(1, 3));
                panel3.add(edtTranslatedFolder, CC.xywh(3, 3, 2, 1));

                //---- btnPickTranslated ----
                btnPickTranslated.setText("...");
                btnPickTranslated.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickTranslatedMouseClicked(e);
                    }
                });
                panel3.add(btnPickTranslated, CC.xy(5, 3));

                //---- label6 ----
                label6.setText("L\u01b0u file \u0111\u00e3 l\u1ecdc");
                panel3.add(label6, CC.xy(1, 5));
                panel3.add(edtFilteredFolder, CC.xywh(3, 5, 2, 1));

                //---- btnPickFiltered ----
                btnPickFiltered.setText("...");
                btnPickFiltered.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickFilteredMouseClicked(e);
                    }
                });
                panel3.add(btnPickFiltered, CC.xy(5, 5));

                //---- label1 ----
                label1.setText("Resources check");
                panel3.add(label1, CC.xy(1, 7));
                panel3.add(edtResCheckFolder, CC.xywh(3, 7, 2, 1));

                //---- btnPickResCheckFolder ----
                btnPickResCheckFolder.setText("...");
                btnPickResCheckFolder.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickResCheckFolderMouseClicked(e);
                    }
                });
                panel3.add(btnPickResCheckFolder, CC.xy(5, 7));
            }
            panel1.add(panel3, CC.xywh(3, 1, 2, 1));

            //======== panel4 ========
            {
                panel4.setLayout(new FormLayout(
                        "8*(default, $lcgap), default",
                        "default"));

                //======== panel7 ========
                {
                    panel7.setBorder(new CompoundBorder(
                            new TitledBorder("M\u1ee5c c\u1ea7n l\u1ecdc"),
                            Borders.DLU2));
                    panel7.setLayout(new FormLayout(
                            "42dlu",
                            "2*(default, $lgap), default"));

                    //---- cbString ----
                    cbString.setText("string");
                    cbString.setSelected(true);
                    panel7.add(cbString, CC.xy(1, 1));

                    //---- cbArray ----
                    cbArray.setText("array");
                    cbArray.setSelected(true);
                    panel7.add(cbArray, CC.xy(1, 3));

                    //---- cbPlural ----
                    cbPlural.setText("plural");
                    cbPlural.setSelected(true);
                    panel7.add(cbPlural, CC.xy(1, 5));
                }
                panel4.add(panel7, CC.xy(1, 1));

                //======== panel8 ========
                {
                    panel8.setBorder(new TitledBorder("T\u00f9y ch\u1ecdn"));
                    panel8.setLayout(new FormLayout(
                            "default",
                            "4*(default, $lgap), default"));

                    //---- cbFindFormatedString ----
                    cbFindFormatedString.setText("T\u00ecm formatted text d\u1ecbch sai ");
                    cbFindFormatedString.setSelected(true);
                    cbFindFormatedString.setToolTipText("T\u00ecm nh\u1eefng string c\u00f3 format b\u1ecb d\u1ecbch sai do ch\u01b0a update theo ng\u00f4n ng\u1eef m\u1edbi");
                    panel8.add(cbFindFormatedString, CC.xy(1, 1));

                    //---- cbFindCanRemove ----
                    cbFindCanRemove.setText("T\u00ecm text c\u00f3 th\u1ec3 b\u1ecf");
                    cbFindCanRemove.setSelected(true);
                    cbFindCanRemove.setToolTipText("Nh\u1eefng text m\u00e0 d\u1ecbch gi\u1ed1ng h\u1ec7t g\u1ed1c c\u00f3 th\u1ec3 b\u1ecf kh\u1ecfi g\u00f3i ng\u00f4n ng\u1eef");
                    panel8.add(cbFindCanRemove, CC.xy(1, 3));

                    //---- checkBox1 ----
                    checkBox1.setText("So s\u00e1nh v\u1edbi string b\u1ecb b\u1ecf qua");
                    checkBox1.setToolTipText("B\u1ecf qua text trong danh s\u00e1ch khi qu\u00e9t");
                    checkBox1.setSelected(true);
                    panel8.add(checkBox1, CC.xy(1, 5));

                    //---- cbFindUntranslated ----
                    cbFindUntranslated.setText("T\u00ecm text ch\u01b0a d\u1ecbch");
                    cbFindUntranslated.setToolTipText("T\u00ecm text ch\u01b0a d\u1ecbch trong to\u00e0n b\u1ed9 g\u00f3i ng\u00f4n ng\u1eef, \u1edf t\u1ea5t c\u1ea3 c\u00e1c thi\u1ebft b\u1ecb");
                    cbFindUntranslated.setSelected(true);
                    cbFindUntranslated.addChangeListener(e -> cbFindUntranslatedStateChanged(e));
                    panel8.add(cbFindUntranslated, CC.xy(1, 7));

                    //---- cbFindInAllApp ----
                    cbFindInAllApp.setText("T\u00ecm trong to\u00e0n b\u1ed9 (l\u00e2u h\u01a1n r\u1ea5t nhi\u1ec1u)");
                    panel8.add(cbFindInAllApp, CC.xy(1, 9));
                }
                panel4.add(panel8, CC.xy(3, 1));

                //======== panel5 ========
                {
                    panel5.setBorder(new TitledBorder("H\u00e0nh \u0111\u1ed9ng"));
                    panel5.setLayout(new FormLayout(
                            "88dlu, $lcgap, 84dlu",
                            "2*(default, $lgap), default"));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new FormLayout(
                                "46dlu, $lcgap, 41dlu",
                                "default"));

                        //---- btnStart ----
                        btnStart.setText("B\u1eaft \u0111\u1ea7u");
                        btnStart.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                btnStartMouseClicked(e);
                            }
                        });
                        panel2.add(btnStart, CC.xy(1, 1));

                        //---- btnStop ----
                        btnStop.setText("D\u1eebng");
                        btnStop.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                button1MouseClicked(e);
                                btnStopMouseClicked(e);
                            }
                        });
                        panel2.add(btnStop, CC.xy(3, 1));
                    }
                    panel5.add(panel2, CC.xy(1, 1));

                    //---- btnCheckDuplicate ----
                    btnCheckDuplicate.setText("Ki\u1ec3m tra tr\u00f9ng l\u1eb7p");
                    btnCheckDuplicate.setToolTipText("Ki\u1ec3m tra tr\u00f9ng l\u1eb7p trong th\u01b0 m\u1ee5c \u0111\u00e3 d\u1ecbch");
                    btnCheckDuplicate.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnCheckDuplicateMouseClicked(e);
                        }
                    });
                    panel5.add(btnCheckDuplicate, CC.xy(3, 1));

                    //---- btnViewUnTranslatedString ----
                    btnViewUnTranslatedString.setText("Xem string ch\u01b0a d\u1ecbch");
                    btnViewUnTranslatedString.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnViewUnTranslatedStringMouseClicked(e);
                        }
                    });
                    panel5.add(btnViewUnTranslatedString, CC.xy(1, 3));

                    //---- btnMoveToIgnored ----
                    btnMoveToIgnored.setText("Chuy\u1ec3n string ch\u01b0a d\u1ecbch v\u00e0o ignore");
                    btnMoveToIgnored.setVisible(false);
                    btnMoveToIgnored.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnMoveToIgnoredMouseClicked(e);
                        }
                    });
                    panel5.add(btnMoveToIgnored, CC.xy(3, 3));

                    //---- btnViewUntranslatedArray ----
                    btnViewUntranslatedArray.setText("Xem array ch\u01b0a d\u1ecbch");
                    btnViewUntranslatedArray.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnViewUntranslatedArrayMouseClicked(e);
                        }
                    });
                    panel5.add(btnViewUntranslatedArray, CC.xy(1, 5));
                }
                panel4.add(panel5, CC.xy(5, 1));
            }
            panel1.add(panel4, CC.xywh(3, 3, 2, 1));

            //======== panel6 ========
            {
                panel6.setLayout(new FormLayout(
                        "362dlu, 2*($lcgap, default)",
                        "default, 97dlu"));

                //---- label7 ----
                label7.setText("Log");
                panel6.add(label7, CC.xy(1, 1));

                //======== scrollPane1 ========
                {
                    scrollPane1.setMinimumSize(new Dimension(16, 170));

                    //---- tvLog ----
                    tvLog.setPreferredSize(new Dimension(40, 160));
                    tvLog.setMinimumSize(new Dimension(7, 170));
                    scrollPane1.setViewportView(tvLog);
                }
                panel6.add(scrollPane1, CC.xy(1, 2));
            }
            panel1.add(panel6, CC.xy(3, 5));
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void setBorder(CompoundBorder compoundBorder) {

    }

    private Border getBorder() {
        return null;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Trần Tuấn Anh
    private JPanel panel1;
    private JPanel panel3;
    private JLabel label2;
    private JTextField edtOriginFolder;
    private JButton btnPickOrigin;
    private JLabel label3;
    private JTextField edtTranslatedFolder;
    private JButton btnPickTranslated;
    private JLabel label6;
    private JTextField edtFilteredFolder;
    private JButton btnPickFiltered;
    private JLabel label1;
    private JTextField edtResCheckFolder;
    private JButton btnPickResCheckFolder;
    private JPanel panel4;
    private JPanel panel7;
    private JCheckBox cbString;
    private JCheckBox cbArray;
    private JCheckBox cbPlural;
    private JPanel panel8;
    private JCheckBox cbFindFormatedString;
    private JCheckBox cbFindCanRemove;
    private JCheckBox checkBox1;
    private JCheckBox cbFindUntranslated;
    private JCheckBox cbFindInAllApp;
    private JPanel panel5;
    private JPanel panel2;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnCheckDuplicate;
    private JButton btnViewUnTranslatedString;
    private JButton btnMoveToIgnored;
    private JButton btnViewUntranslatedArray;
    private JPanel panel6;
    private JLabel label7;
    private JScrollPane scrollPane1;
    private JTextArea tvLog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
