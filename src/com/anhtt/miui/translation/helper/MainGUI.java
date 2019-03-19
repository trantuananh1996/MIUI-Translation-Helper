/*
 * Created by JFormDesigner on Wed Mar 13 09:55:27 ICT 2019
 */

package com.anhtt.miui.translation.helper;

import com.anhtt.miui.translation.helper.model.*;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
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

/**
 * @author unknown
 */
public class MainGUI extends JFrame {
    TranslatedDevice transDevices;
    List<OriginDevice> originDevices;
    List<OriginDevice> specificOriginDevices;
    public static JLabel tvLogStatic;
    public static List<UnTranslateable> unTranslateables;
    public static List<UnTranslateable> autoIgnoredList;
    public static List<WrongStringRes> wrongTranslateGlobals = new ArrayList<>();
    public static List<WrongStringRes> wrongTranslateGlobalOrigin = new ArrayList<>();

    public List<WrongApplication> wrongApplications = new ArrayList<>();
    public List<WrongApplication> untranslatedApplications = new ArrayList<>();

    public MainGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
        setSize(500, 500);
        setLocationRelativeTo(getOwner());
        tvLogStatic = tvLog;
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

    public static void handleWrongString(OriginDevice originDevice, StringRes originString, StringRes translatedString) {

    }

    private void btnStartMouseClicked(MouseEvent e) {


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
//            try {
//                deleteDirectoryStream(filteredFolder.toPath());
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
            filteredFolder.mkdirs();
        }

        tvLog.setText("Đang chuẩn bị...");

        StringWorker worker = new StringWorker(originFolder, translationFolder, filteredFolder);
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
            transDevices = TranslatedDevice.create(transDevicesFolder.getAbsolutePath());


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
                    }

                    if (cbFindUntranslated.isSelected()) {
                        groupString(application.getName(), application.getUnTranslatedStrings(), originDevice, untranslatedApplications);
                        groupArray(application.getName(), application.getUnTranslatedArrays(), originDevice, untranslatedApplications);
                    }
                    if (cbFindCanRemove.isSelected())
                        Utils.writeStringsToFile(filteredFolder.getAbsolutePath() + "\\Can Remove\\" + originDevice.getName() + "\\" + application.getName(), application.getOriginEqualTranslatedStrings());

                });
            });

            if (cbFindFormatedString.isSelected()) {
                Utils.writeWrongToFile(filteredFolder.getAbsolutePath() + "\\Wrongs", wrongApplications);
                Utils.writeWronArray(filteredFolder.getAbsolutePath() + "\\WrongsArray", wrongApplications);
            }
            if (cbFindUntranslated.isSelected()) {
                Utils.writeUnTranslatedStringToFile(filteredFolder.getAbsolutePath() + "\\UnTranslated", untranslatedApplications);
                Utils.writeUnTranslatedArrayToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedArray", untranslatedApplications);

            }
            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            for (String string : chunks) tvLogStatic.setText(string);
        }

        @Override
        protected void done() {
            tvLogStatic.setText("Đã xong. Kiểm tra thư mục kết quả");
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Trần Tuấn Anh
        panel1 = new JPanel();
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
        label4 = new JLabel();
        label5 = new JLabel();
        cbString = new JCheckBox();
        cbFindFormatedString = new JCheckBox();
        cbArray = new JCheckBox();
        cbFindUntranslated = new JCheckBox();
        cbPlural = new JCheckBox();
        checkBox2 = new JCheckBox();
        cbFindCanRemove = new JCheckBox();
        checkBox1 = new JCheckBox();
        btnStart = new JButton();
        btnMoveToIgnored = new JButton();
        tvLog = new JLabel();

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
                    "2*(default, $lcgap), $lcgap, 22dlu, 85dlu, 3*($lcgap, default), $lcgap, 71dlu, $lcgap, default, $lcgap, 22dlu",
                    "13*(default, $lgap), default"));

            //---- label2 ----
            label2.setText("Th\u01b0 m\u1ee5c g\u1ed1c");
            panel1.add(label2, CC.xy(1, 3));
            panel1.add(edtOriginFolder, CC.xywh(3, 3, 15, 1));

            //---- btnPickOrigin ----
            btnPickOrigin.setText("...");
            btnPickOrigin.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnPickOriginMouseClicked(e);
                }
            });
            panel1.add(btnPickOrigin, CC.xy(19, 3));

            //---- label3 ----
            label3.setText("Th\u01b0 m\u1ee5c \u0111\u00e3 d\u1ecbch");
            panel1.add(label3, CC.xy(1, 5));
            panel1.add(edtTranslatedFolder, CC.xywh(3, 5, 15, 1));

            //---- btnPickTranslated ----
            btnPickTranslated.setText("...");
            btnPickTranslated.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnPickTranslatedMouseClicked(e);
                }
            });
            panel1.add(btnPickTranslated, CC.xy(19, 5));

            //---- label6 ----
            label6.setText("L\u01b0u file \u0111\u00e3 l\u1ecdc");
            panel1.add(label6, CC.xy(1, 7));
            panel1.add(edtFilteredFolder, CC.xywh(3, 7, 15, 1));

            //---- btnPickFiltered ----
            btnPickFiltered.setText("...");
            btnPickFiltered.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnPickFilteredMouseClicked(e);
                }
            });
            panel1.add(btnPickFiltered, CC.xy(19, 7));

            //---- label1 ----
            label1.setText("Resources check");
            panel1.add(label1, CC.xy(1, 9));
            panel1.add(edtResCheckFolder, CC.xywh(3, 9, 15, 1));

            //---- btnPickResCheckFolder ----
            btnPickResCheckFolder.setText("...");
            btnPickResCheckFolder.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnPickResCheckFolderMouseClicked(e);
                }
            });
            panel1.add(btnPickResCheckFolder, CC.xy(19, 9));

            //---- label4 ----
            label4.setText("T\u00f9y ch\u1ecdn d\u1ecbch");
            panel1.add(label4, CC.xy(1, 13));

            //---- label5 ----
            label5.setText("T\u00f9y ch\u1ecdn");
            panel1.add(label5, CC.xywh(6, 13, 2, 1));

            //---- cbString ----
            cbString.setText("string");
            panel1.add(cbString, CC.xy(1, 15));

            //---- cbFindFormatedString ----
            cbFindFormatedString.setText("T\u00ecm formatted text d\u1ecbch sai ");
            cbFindFormatedString.setSelected(true);
            cbFindFormatedString.setToolTipText("T\u00ecm nh\u1eefng string c\u00f3 format b\u1ecb d\u1ecbch sai do ch\u01b0a update theo ng\u00f4n ng\u1eef m\u1edbi");
            panel1.add(cbFindFormatedString, CC.xywh(6, 15, 2, 1));

            //---- cbArray ----
            cbArray.setText("array");
            panel1.add(cbArray, CC.xy(1, 17));

            //---- cbFindUntranslated ----
            cbFindUntranslated.setText("T\u00ecm text ch\u01b0a d\u1ecbch");
            cbFindUntranslated.setToolTipText("T\u00ecm text ch\u01b0a d\u1ecbch trong to\u00e0n b\u1ed9 g\u00f3i ng\u00f4n ng\u1eef, \u1edf t\u1ea5t c\u1ea3 c\u00e1c thi\u1ebft b\u1ecb");
            cbFindUntranslated.setSelected(true);
            panel1.add(cbFindUntranslated, CC.xywh(6, 17, 2, 1));

            //---- cbPlural ----
            cbPlural.setText("plural");
            panel1.add(cbPlural, CC.xy(1, 19));

            //---- checkBox2 ----
            checkBox2.setText("Ch\u1ec9 t\u00ecm text ch\u01b0a c\u00f3 \u1edf b\u1ea5t k\u1ef3 app n\u00e0o");
            panel1.add(checkBox2, CC.xywh(7, 19, 7, 1));

            //---- cbFindCanRemove ----
            cbFindCanRemove.setText("T\u00ecm text c\u00f3 th\u1ec3 b\u1ecf");
            cbFindCanRemove.setSelected(true);
            cbFindCanRemove.setToolTipText("Nh\u1eefng text m\u00e0 d\u1ecbch gi\u1ed1ng h\u1ec7t g\u1ed1c c\u00f3 th\u1ec3 b\u1ecf kh\u1ecfi g\u00f3i ng\u00f4n ng\u1eef");
            panel1.add(cbFindCanRemove, CC.xywh(6, 21, 2, 1));

            //---- checkBox1 ----
            checkBox1.setText("So s\u00e1nh v\u1edbi string b\u1ecb b\u1ecf qua");
            checkBox1.setToolTipText("B\u1ecf qua text trong danh s\u00e1ch khi qu\u00e9t");
            panel1.add(checkBox1, CC.xywh(6, 23, 2, 1));

            //---- btnStart ----
            btnStart.setText("B\u1eaft \u0111\u1ea7u");
            btnStart.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnStartMouseClicked(e);
                }
            });
            panel1.add(btnStart, CC.xy(15, 23));

            //---- btnMoveToIgnored ----
            btnMoveToIgnored.setText("Chuy\u1ec3n string ch\u01b0a d\u1ecbch v\u00e0o ignore");
            btnMoveToIgnored.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnMoveToIgnoredMouseClicked(e);
                }
            });
            panel1.add(btnMoveToIgnored, CC.xywh(11, 25, 9, 1));

            //---- tvLog ----
            tvLog.setText("Log");
            panel1.add(tvLog, CC.xywh(1, 27, 13, 1));
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
    private JLabel label4;
    private JLabel label5;
    private JCheckBox cbString;
    private JCheckBox cbFindFormatedString;
    private JCheckBox cbArray;
    private JCheckBox cbFindUntranslated;
    private JCheckBox cbPlural;
    private JCheckBox checkBox2;
    private JCheckBox cbFindCanRemove;
    private JCheckBox checkBox1;
    private JButton btnStart;
    private JButton btnMoveToIgnored;
    private JLabel tvLog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
