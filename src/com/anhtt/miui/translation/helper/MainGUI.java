/*
 * Created by JFormDesigner on Wed Mar 13 09:55:27 ICT 2019
 */

package com.anhtt.miui.translation.helper;

import com.anhtt.miui.translation.helper.model.*;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.istack.internal.Nullable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultCaret;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.anhtt.miui.translation.helper.Utils.delete;

/**
 * @author unknown
 */
public class MainGUI extends JFrame {

    TargetDevice targetDevice;
    HashMap<String, SourceDevice> sourceDevices;
    List<SourceDevice> targetSeparateSourceDevices;
    public static Map<String, UnTranslateable> unTranslateables;
    public static List<UnTranslateable> autoIgnoredList;
    StringWorker worker;
    public List<WrongApplication> wrongApplications = new ArrayList<>();
    public List<WrongApplication> untranslatedApplications = new ArrayList<>();
    public List<WrongApplication> untranslatedFromAllApplications = new ArrayList<>();
    Logger logger = Logger.getLogger("Log");

    public MainGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
        tvLog.setLineWrap(true);
        tvLog.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) tvLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        logger.setUseParentHandlers(false);
        OutputStream os = new TextAreaOutputStream(tvLog);
        logger.addHandler(new TextAreaHandler(os));

        pack();
        setLocationRelativeTo(getOwner());
        btnViewUntranslatedArray.setVisible(false);
        btnViewUnTranslatedString.setVisible(false);
        //TODO: For fast testing
        edtOriginFolder.setText("C:\\Users\\trant\\Documents\\Github\\Xiaomi.eu-MIUIv11-XML-Compare");
        edtTranslatedFolder.setText("C:\\Users\\trant\\Documents\\Github\\MIUI-11-XML-Vietnamese\\Vietnamese");
        edtFilteredFolder.setText("C:\\Users\\trant\\Documents\\Filtered");
        edtResCheckFolder.setText("C:\\Users\\trant\\Documents\\Github\\MA-XML-CHECK-RESOURCES");


        for (Language language : Language.values()) {
            langComboBox.addItem(new ComboItem(language.name(), language.getCode()));
        }
        langComboBox.addActionListener(e -> {
            Language language = Language.values()[langComboBox.getSelectedIndex()];
            Config.PRIMARY_RES_FOLDER = "values-" + language.getLocale();
            Config.SECONDARY_RES_FOLDER = "values-" + language.getLocale() + "-r" + language.getCode();
            Config.FILTERED_PATH = "Filtered-" + language.getCode();
            Config.TRANSLATED_REPO = language.getGitUrl();
        });
        langComboBox.setSelectedIndex(3);
//        try {
//            callGit();
//        } catch (IOException | GitAPIException e) {
//            e.printStackTrace();
//        }
    }

    private static final String REMOTE_URL = "https://github.com/ingbrzy/Xiaomi.eu-MIUIv11-XML-Compare.git";


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

        if (!SearchOptions.searchPlural && !SearchOptions.searchArray && !SearchOptions.searchString) {
            JOptionPane.showMessageDialog(null, "You have to pick string, array or/and plural");
            return;
        }
        String originPath = edtOriginFolder.getText();
        String translationPath = edtTranslatedFolder.getText();
        String filteredPath = edtFilteredFolder.getText() + "/" + Config.FILTERED_PATH;

        File originFolder = checkOrginFolder(originPath);
        File translationFolder = checkTranslationFolder(translationPath);
        File filteredFolder = new File(filteredPath);
        if (originFolder == null) {
            JOptionPane.showMessageDialog(null, "Check english folder");
            return;
        }
        if (translationFolder == null) {
            JOptionPane.showMessageDialog(null, "Check translated folder");
            return;
        }

        if (!filteredFolder.exists()) filteredFolder.mkdirs();
        else {
            delete(filteredFolder);
            filteredFolder.mkdirs();
        }

        logger.info("Preparing...");

        worker = new StringWorker(originFolder, translationFolder, filteredFolder);
        worker.execute();
    }

    private File checkTranslationFolder(String translationPath) {
        System.out.println("checkTranslationFolder() called with: translationPath = [" + translationPath + "]");
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
        System.out.println("checkOrginFolder() called with: originPath = [" + originPath + "]");
        File file = new File(originPath);
        if (!file.exists()) return null;
        if (!file.isDirectory()) return null;
        if (file.listFiles() == null) return null;
        return file;
//        for (File child : Objects.requireNonNull(file.listFiles())) {
//            if (child.isDirectory())
//                if (child.getName().equals("Diff_v11-v10-9.9.24")) return file;
//        }
//        return null;
    }

    public final class StringWorker extends SwingWorker<String, String> {
        File sourceCompareFolder;
        File targetLangFolder;
        File filteredFolder;

        public StringWorker(File sourceCompareFolder, File targetLangFolder, File filteredFolder) {
            this.sourceCompareFolder = sourceCompareFolder;
            this.targetLangFolder = targetLangFolder;
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


            File targetDeviceFolder = new File(targetLangFolder.getAbsolutePath() + "\\main");
            if (!targetDeviceFolder.exists()) return null;
//            HashMap<String, SourceDevice> sourceDevicesWithoutCompare = createDevices(this, sourceCompareFolder, targetDevice, targetSeparateSourceDevices, false);
            targetDevice = TargetDevice.create(targetDeviceFolder.getAbsolutePath(), false, SourceDevice.create(sourceCompareFolder.getAbsolutePath() + "/sirius"));


            File specificDevicesFolder = new File(targetLangFolder.getAbsolutePath() + "\\device");
            targetSeparateSourceDevices = new ArrayList<>();
            for (File file : Objects.requireNonNull(specificDevicesFolder.listFiles())) {
                if (file.isDirectory()) {
                    SourceDevice sourceDevice = SourceDevice.create(file.getAbsolutePath());
                    if (sourceDevice != null) targetSeparateSourceDevices.add(sourceDevice);
                }
            }

            sourceDevices = createDevices(this, sourceCompareFolder, targetDevice, targetSeparateSourceDevices, true);


            sourceDevices.values().forEach(sourceDevice -> {
                sourceDevice.getApps().values().forEach(application -> {
                    if (cbFindFormatedString.isSelected()) {
                        groupString(application.getName(), application.getMapWrongTranslatedOriginStrings(), sourceDevice, wrongApplications);
                        groupArray(application.getName(), application.getMapWrongTranslatedOriginArrays(), sourceDevice, wrongApplications);
                        groupPlural(application.getName(), application.getMapWrongTranslatedOriginPlurals(), sourceDevice, wrongApplications);
                    }

                    if (cbFindUntranslated.isSelected()) {
                        groupStringFromAll(application.getName(), application.getMapTranslatedFromOtherString(), sourceDevice, untranslatedFromAllApplications);
                        groupString(application.getName(), application.getMapUnTranslatedStrings(), sourceDevice, untranslatedApplications);
                        groupArrayFromAll(application.getName(), application.getMapTranslatedFromOtherArray(), sourceDevice, untranslatedFromAllApplications);
                        groupArray(application.getName(), application.getMapUnTranslatedArrays(), sourceDevice, untranslatedApplications);
                        groupPluralFromAll(application.getName(), application.getMapTranslatedFromOtherPlural(), sourceDevice, untranslatedFromAllApplications);
                        groupPlural(application.getName(), application.getMapUnTranslatedPlurals(), sourceDevice, untranslatedApplications);
                    }
//                    if (cbFindCanRemove.isSelected())
//                        Utils.writeStringsToFile(filteredFolder.getAbsolutePath() + "\\Can Remove\\" + sourceDevice.getName() + "\\" + application.getName(), application.getMapOriginEqualTranslatedStrings());

                });
            });

            if (cbFindFormatedString.isSelected()) {
                Utils.writeWrongToFile(filteredFolder.getAbsolutePath() + "\\Wrongs", wrongApplications);
                Utils.writeWrongArrayToFile(filteredFolder.getAbsolutePath() + "\\WrongArray", wrongApplications);
                Utils.writeWrongPluralToFile(filteredFolder.getAbsolutePath() + "\\WrongPlural", wrongApplications);

            }
            if (cbFindUntranslated.isSelected()) {
                Utils.writeUnTranslatedStringToFile(filteredFolder.getAbsolutePath() + "\\UnTranslated", untranslatedApplications);
                Utils.writeUnTranslatedFromAllStringToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedFromOther", untranslatedFromAllApplications);
                Utils.writeUnTranslatedArrayToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedArray", untranslatedApplications);
                Utils.writeUnTranslatedFromAllArrayToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedArrayFromOther", untranslatedFromAllApplications);
                Utils.writeUnTranslatedPluralToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedPlural", untranslatedApplications);
                Utils.writeUnTranslatedFromAllPluralToFile(filteredFolder.getAbsolutePath() + "\\UnTranslatedPluralFromOther", untranslatedFromAllApplications);

            }
            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            for (String string : chunks) logger.info(string);
        }

        @Override
        protected void done() {
            logger.info("Done. Check working folder for result");
            btnViewUntranslatedArray.setVisible(true);
            btnViewUnTranslatedString.setVisible(true);
        }
    }

    private void groupString(String appName, Map<String, StringRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.forEach((s, origin) -> wrongApplication.addOrigin(sourceDevices.size(), sourceDevice, origin));
        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupStringFromAll(String appName, Map<String, StringRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.forEach((s, origin) -> wrongApplication.addOriginFromAll(sourceDevices.size(), sourceDevice, origin));

        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupArray(String appName, Map<String, ArrayRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.values().forEach(origin -> wrongApplication.addOrigin(sourceDevices.size(), sourceDevice, origin));

        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupArrayFromAll(String appName, Map<String, ArrayRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.values().forEach(origin -> wrongApplication.addOriginFromAll(sourceDevices.size(), sourceDevice, origin));

        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupPlural(String appName, Map<String, PluralRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.values().forEach(origin -> wrongApplication.addOrigin(sourceDevices.size(), sourceDevice, origin));
        if (!wrongApplication1.isPresent()) wrongApplications.add(wrongApplication);
    }

    private void groupPluralFromAll(String appName, Map<String, PluralRes> stringToGroups, SourceDevice sourceDevice, List<WrongApplication> wrongApplications) {
        WrongApplication wrongApplication;
        Optional<WrongApplication> wrongApplication1 = wrongApplications.stream().filter(wrong -> {
            return wrong.getName().equals(appName);
        }).findFirst();
        wrongApplication = wrongApplication1.orElseGet(() -> new WrongApplication(appName));
        stringToGroups.values().forEach(origin -> wrongApplication.addOriginFromAll(sourceDevices.size(), sourceDevice, origin));
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

    private Map<String, UnTranslateable> createUnTranslateable() throws IOException, SAXException, ParserConfigurationException {
        Map<String, UnTranslateable> unTranslateables = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();

        File sourceFile = new File(edtResCheckFolder.getText() + "\\MIUI11\\MIUI11_untranslateable.xml");
        if (!sourceFile.exists()) return new HashMap<>();
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

                unTranslateables.put(application + file + name, unTranslateable);
            }
        }

        return unTranslateables;
    }

    private HashMap<String, SourceDevice> createDevices(StringWorker swingWorker, File sourceCompareFolder, TargetDevice targetDevice, List<SourceDevice> targetSeparateDevices, boolean needCompare) {
        File sourceDevicesFolder = new File(sourceCompareFolder.getAbsolutePath());
        if (!sourceDevicesFolder.exists()) new ArrayList<>();
        HashMap<String, SourceDevice> sourceDevices = new HashMap<>();
        for (File file : Objects.requireNonNull(sourceDevicesFolder.listFiles())) {
            if (file.isDirectory()) {
                SourceDevice sourceDevice = SourceDevice.create(swingWorker, file.getAbsolutePath(), targetDevice, targetSeparateDevices, needCompare);
                if (sourceDevice != null) sourceDevices.put(sourceDevice.getName(), sourceDevice);
            }
        }
        return sourceDevices;
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


    private void btnStopMouseClicked(MouseEvent e) {
        worker.done();
        worker.cancel(true);
        logger.info("Cancelled");
    }

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void btnCheckDuplicateMouseClicked(MouseEvent e) {
        SearchOptions.searchArray = cbArray.isSelected();
        SearchOptions.searchPlural = cbPlural.isSelected();
        SearchOptions.searchString = cbString.isSelected();
        SearchOptions.filterCanRemove = cbFindCanRemove.isSelected();
        SearchOptions.filterFormatted = cbFindFormatedString.isSelected();
        SearchOptions.filterUnTranslated = cbFindUntranslated.isSelected();
        logger.info("Checking duplicates");
        File transDevicesFolder = new File(edtTranslatedFolder.getText() + "\\main");
        if (!transDevicesFolder.exists()) return;
        targetDevice = TargetDevice.create(transDevicesFolder.getAbsolutePath(), false, null);
        if (targetDevice == null) return;
        targetDevice.getApps().forEach(application -> {
            application.setDuplicateString(getDuplicates(application.getMapOriginStrings()));
            if (application.getDuplicateString() == null || application.getDuplicateString().size() == 0) {
//                logger.info("Không có trùng lặp");
            } else {
                logger.info(application.getName());
                logger.info("Have duplicated text");
                btnViewDuplicate.setVisible(true);
            }
        });
    }

    public static List<StringRes> getDuplicates(final Map<String, StringRes> personList) {
        return getDuplicatesMap(personList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Map<String, List<StringRes>> getDuplicatesMap(Map<String, StringRes> personList) {
        return personList.values().stream().collect(Collectors.groupingBy(StringRes::getName));
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
                    logger.info("Converting " + applications.get(index).getName());
                    try {
                        Utils.convertUntranslateableFile(edtFilteredFolder.getText() + "\\" + Config.FILTERED_PATH + "\\UnTranslated\\", applications.get(index).getName());
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
        List<WrongApplication> applications = untranslatedApplications.stream().filter(wrongApplication -> wrongApplication.getMapWrongTranslatedOriginArrays().values().size() > 0)
                .collect(Collectors.toList());
        JList<WrongApplication> list = new JList(applications.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    logger.info("Converting " + applications.get(index).getName());
                    try {
                        Utils.convertUntranslateableArrayFile(edtFilteredFolder.getText() + "\\" + Config.FILTERED_PATH + "\\UnTranslatedArray\\", applications.get(index).getName());
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

    private void btnViewDuplicateMouseClicked(MouseEvent e) {
        JFrame f = new JFrame("Duplicated");
        List<Application> applications = targetDevice.getApps().stream().filter(application -> application.getDuplicateString() != null && application.getDuplicateString().size() > 0).collect(Collectors.toList());
        JList<Application> list = new JList(applications.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    logger.info("Converting " + applications.get(index).getName());
                    try {
                        Desktop.getDesktop().open(new File(checkTranslationFolder(edtTranslatedFolder.getText()).getAbsolutePath() + "\\main\\" + applications.get(index).getName()));
                    } catch (IOException e1) {
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
        // TODO add your code here
    }

    private void btnPullEnglishMouseClicked(MouseEvent e) {
        try {
            logger.info("Fetching repository: " + edtOriginFolder.getText());
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(edtOriginFolder.getText() + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();


            FetchResult result = new Git(repository).fetch().call();
            System.out.println(result.getMessages());
            logger.info("Fetched from remote repository to local repository at " + repository.getDirectory());

        } catch (RepositoryNotFoundException ex) {
            logger.info("Repository not found");
        } catch (IOException | GitAPIException e1) {
            e1.printStackTrace();
        }
    }

    private void btnPullTranslatedMouseClicked(MouseEvent e) {
        try {
            logger.info("Fetching repository: " + edtTranslatedFolder.getText());
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(edtTranslatedFolder.getText() + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();


            FetchResult result = new Git(repository).fetch().call();
            System.out.println(result.getMessages());
            logger.info("Fetched from remote repository to local repository at " + repository.getDirectory());

        } catch (RepositoryNotFoundException ex) {
            logger.info("Repository not found");
        } catch (IOException | GitAPIException e1) {
            e1.printStackTrace();
        }
    }

    private void btnPullMAXMLMouseClicked(MouseEvent e) {
        try {
            logger.info("Fetching repository: " + edtResCheckFolder.getText());
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(edtResCheckFolder.getText() + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();


            FetchResult result = new Git(repository).fetch().call();
            System.out.println(result.getMessages());
            logger.info("Fetched from remote repository to local repository at " + repository.getDirectory());

        } catch (RepositoryNotFoundException ex) {
            logger.info("Repository not found");
        } catch (IOException | GitAPIException e1) {
            e1.printStackTrace();
        }
    }

    public void callGit() {
        GitWorker worker = new GitWorker(logger, edtOriginFolder.getText(), REMOTE_URL);
        worker.execute();
    }

    public void callMaXml() {
        GitWorker worker = new GitWorker(logger, edtResCheckFolder.getText(), "https://github.com/redmaner/MA-XML-CHECK-RESOURCES");
        worker.execute();
    }

    public void callTranslated() {
        GitWorker worker = new GitWorker(logger, edtTranslatedFolder.getText(), Config.TRANSLATED_REPO);
        worker.execute();
    }

    private void btnGenerateWorkingFolderMouseClicked(MouseEvent e) {
        edtTranslatedFolder.setText(edtFilteredFolder.getText() + "\\Translated");
        edtOriginFolder.setText(edtFilteredFolder.getText() + "\\Source");
        edtResCheckFolder.setText(edtFilteredFolder.getText() + "\\MA-XML-CHECK-RESOURCES");

        callMaXml();
        callGit();
        callTranslated();

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Trần Tuấn Anh
        panel1 = new JPanel();
        panel3 = new JPanel();
        label4 = new JLabel();
        langComboBox = new JComboBox();
        label6 = new JLabel();
        edtFilteredFolder = new JTextField();
        btnPickFiltered = new JButton();
        btnGenerateWorkingFolder = new JButton();
        label2 = new JLabel();
        edtOriginFolder = new JTextField();
        btnPickOrigin = new JButton();
        btnPullEnglish = new JButton();
        panel6 = new JPanel();
        label3 = new JLabel();
        edtTranslatedFolder = new JTextField();
        btnPickTranslated = new JButton();
        btnPullTranslated = new JButton();
        label1 = new JLabel();
        edtResCheckFolder = new JTextField();
        btnPickResCheckFolder = new JButton();
        btnPullMAXML = new JButton();
        panel4 = new JPanel();
        panel7 = new JPanel();
        cbString = new JCheckBox();
        cbArray = new JCheckBox();
        cbPlural = new JCheckBox();
        panel8 = new JPanel();
        cbFindFormatedString = new JCheckBox();
        cbFindCanRemove = new JCheckBox();
        cbFindUntranslated = new JCheckBox();
        panel5 = new JPanel();
        panel2 = new JPanel();
        btnStart = new JButton();
        btnStop = new JButton();
        btnCheckDuplicate = new JButton();
        btnViewUnTranslatedString = new JButton();
        btnViewUntranslatedArray = new JButton();
        button1 = new JButton();
        btnViewDuplicate = new JButton();
        panelLog = new JPanel();
        scrollPane1 = new JScrollPane();
        tvLog = new JTextArea();

        //======== this ========
        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax
                .swing.border.EmptyBorder(0, 0, 0, 0), "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax.swing
                .border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM, new java.awt.
                Font("Dia\u006cog", java.awt.Font.BOLD, 12), java.awt.Color.red
        ), getBorder()));
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                if ("\u0062ord\u0065r".equals(e.getPropertyName(
                ))) throw new RuntimeException();
            }
        });
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "5dlu, $lcgap, 454dlu, $lcgap",
                    "2*(default, $lgap), default"));

            //======== panel3 ========
            {
                panel3.setLayout(new FormLayout(
                        "default, $lcgap, 275dlu, $lcgap, 26dlu, $lcgap, default",
                        "4*(default, $lgap), default"));

                //---- label4 ----
                label4.setText("Working language");
                panel3.add(label4, CC.xy(1, 1));
                panel3.add(langComboBox, CC.xy(3, 1));

                //---- label6 ----
                label6.setText("Working folder");
                panel3.add(label6, CC.xy(1, 3));
                panel3.add(edtFilteredFolder, CC.xywh(3, 3, 2, 1));

                //---- btnPickFiltered ----
                btnPickFiltered.setText("...");
                btnPickFiltered.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickFilteredMouseClicked(e);
                    }
                });
                panel3.add(btnPickFiltered, CC.xy(5, 3));

                //---- btnGenerateWorkingFolder ----
                btnGenerateWorkingFolder.setText("Generate");
                btnGenerateWorkingFolder.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnGenerateWorkingFolderMouseClicked(e);
                    }
                });
                panel3.add(btnGenerateWorkingFolder, CC.xy(7, 3));

                //---- label2 ----
                label2.setText("English folder");
                panel3.add(label2, CC.xy(1, 5));
                panel3.add(edtOriginFolder, CC.xywh(3, 5, 2, 1));

                //---- btnPickOrigin ----
                btnPickOrigin.setText("...");
                btnPickOrigin.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickOriginMouseClicked(e);
                    }
                });
                panel3.add(btnPickOrigin, CC.xy(5, 5));

                //---- btnPullEnglish ----
                btnPullEnglish.setText("Pull");
                btnPullEnglish.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPullEnglishMouseClicked(e);
                    }
                });
                panel3.add(btnPullEnglish, CC.xy(7, 5));

                //======== panel6 ========
                {
                    panel6.setLayout(new FormLayout(
                            "36dlu, $lcgap, 53dlu",
                            "default"));

                    //---- label3 ----
                    label3.setText("Translated");
                    panel6.add(label3, CC.xy(1, 1));
                }
                panel3.add(panel6, CC.xy(1, 7));
                panel3.add(edtTranslatedFolder, CC.xywh(3, 7, 2, 1));

                //---- btnPickTranslated ----
                btnPickTranslated.setText("...");
                btnPickTranslated.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickTranslatedMouseClicked(e);
                    }
                });
                panel3.add(btnPickTranslated, CC.xy(5, 7));

                //---- btnPullTranslated ----
                btnPullTranslated.setText("Pull");
                btnPullTranslated.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPullTranslatedMouseClicked(e);
                    }
                });
                panel3.add(btnPullTranslated, CC.xy(7, 7));

                //---- label1 ----
                label1.setText("Resources check folder");
                panel3.add(label1, CC.xy(1, 9));
                panel3.add(edtResCheckFolder, CC.xywh(3, 9, 2, 1));

                //---- btnPickResCheckFolder ----
                btnPickResCheckFolder.setText("...");
                btnPickResCheckFolder.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPickResCheckFolderMouseClicked(e);
                    }
                });
                panel3.add(btnPickResCheckFolder, CC.xy(5, 9));

                //---- btnPullMAXML ----
                btnPullMAXML.setText("Pull");
                btnPullMAXML.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btnPullMAXMLMouseClicked(e);
                    }
                });
                panel3.add(btnPullMAXML, CC.xy(7, 9));
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
                            new TitledBorder("Filter"),
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
                    panel8.setBorder(new TitledBorder("Options"));
                    panel8.setLayout(new FormLayout(
                            "default",
                            "2*(default, $lgap), default"));

                    //---- cbFindFormatedString ----
                    cbFindFormatedString.setText("Find format mismatch");
                    cbFindFormatedString.setSelected(true);
                    cbFindFormatedString.setToolTipText("Find format missmatch text");
                    panel8.add(cbFindFormatedString, CC.xy(1, 1));

                    //---- cbFindCanRemove ----
                    cbFindCanRemove.setText("Find removeable");
                    cbFindCanRemove.setSelected(true);
                    cbFindCanRemove.setToolTipText("Find text which translated is equals with english. It can be safely remove from translated");
                    panel8.add(cbFindCanRemove, CC.xy(1, 3));

                    //---- cbFindUntranslated ----
                    cbFindUntranslated.setText("Find untranslated");
                    cbFindUntranslated.setToolTipText("Find untranslated text");
                    cbFindUntranslated.setSelected(true);
                    cbFindUntranslated.addChangeListener(e -> cbFindUntranslatedStateChanged(e));
                    panel8.add(cbFindUntranslated, CC.xy(1, 5));
                }
                panel4.add(panel8, CC.xy(3, 1));

                //======== panel5 ========
                {
                    panel5.setBorder(new TitledBorder("Action"));
                    panel5.setLayout(new FormLayout(
                            "101dlu, $lcgap, 84dlu",
                            "2*(default, $lgap), default"));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new FormLayout(
                                "51dlu, $lcgap, 46dlu",
                                "default"));

                        //---- btnStart ----
                        btnStart.setText("Start");
                        btnStart.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                btnStartMouseClicked(e);
                            }
                        });
                        panel2.add(btnStart, CC.xy(1, 1));

                        //---- btnStop ----
                        btnStop.setText("D\u1eebng");
                        btnStop.setVisible(false);
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
                    btnCheckDuplicate.setText("Check duplicate");
                    btnCheckDuplicate.setToolTipText("Ki\u1ec3m tra tr\u00f9ng l\u1eb7p trong th\u01b0 m\u1ee5c \u0111\u00e3 d\u1ecbch");
                    btnCheckDuplicate.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnCheckDuplicateMouseClicked(e);
                        }
                    });
                    panel5.add(btnCheckDuplicate, CC.xy(3, 1));

                    //---- btnViewUnTranslatedString ----
                    btnViewUnTranslatedString.setText("View untranslated string");
                    btnViewUnTranslatedString.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnViewUnTranslatedStringMouseClicked(e);
                        }
                    });
                    panel5.add(btnViewUnTranslatedString, CC.xy(1, 3));

                    //---- btnViewUntranslatedArray ----
                    btnViewUntranslatedArray.setText("View untranslated array");
                    btnViewUntranslatedArray.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnViewUntranslatedArrayMouseClicked(e);
                        }
                    });
                    panel5.add(btnViewUntranslatedArray, CC.xy(3, 3));

                    //---- button1 ----
                    button1.setText("Xu\u1ea5t ng\u00f4n ng\u1eef");
                    button1.setVisible(false);
                    panel5.add(button1, CC.xy(1, 5));

                    //---- btnViewDuplicate ----
                    btnViewDuplicate.setText("Xem tr\u00f9ng l\u1eb7p");
                    btnViewDuplicate.setVisible(false);
                    btnViewDuplicate.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            btnViewDuplicateMouseClicked(e);
                        }
                    });
                    panel5.add(btnViewDuplicate, CC.xy(3, 5));
                }
                panel4.add(panel5, CC.xy(5, 1));
            }
            panel1.add(panel4, CC.xywh(3, 3, 2, 1));

            //======== panelLog ========
            {
                panelLog.setBorder(new TitledBorder("Log"));
                panelLog.setPreferredSize(new Dimension(666, 200));
                panelLog.setLayout(new FormLayout(
                        "448dlu",
                        "106dlu"));

                //======== scrollPane1 ========
                {
                    scrollPane1.setPreferredSize(new Dimension(2, 200));
                    scrollPane1.setViewportView(tvLog);
                }
                panelLog.add(scrollPane1, CC.xy(1, 1));
            }
            panel1.add(panelLog, CC.xy(3, 5));
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
    private JLabel label4;
    private JComboBox langComboBox;
    private JLabel label6;
    private JTextField edtFilteredFolder;
    private JButton btnPickFiltered;
    private JButton btnGenerateWorkingFolder;
    private JLabel label2;
    private JTextField edtOriginFolder;
    private JButton btnPickOrigin;
    private JButton btnPullEnglish;
    private JPanel panel6;
    private JLabel label3;
    private JTextField edtTranslatedFolder;
    private JButton btnPickTranslated;
    private JButton btnPullTranslated;
    private JLabel label1;
    private JTextField edtResCheckFolder;
    private JButton btnPickResCheckFolder;
    private JButton btnPullMAXML;
    private JPanel panel4;
    private JPanel panel7;
    private JCheckBox cbString;
    private JCheckBox cbArray;
    private JCheckBox cbPlural;
    private JPanel panel8;
    private JCheckBox cbFindFormatedString;
    private JCheckBox cbFindCanRemove;
    private JCheckBox cbFindUntranslated;
    private JPanel panel5;
    private JPanel panel2;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnCheckDuplicate;
    private JButton btnViewUnTranslatedString;
    private JButton btnViewUntranslatedArray;
    private JButton button1;
    private JButton btnViewDuplicate;
    private JPanel panelLog;
    private JScrollPane scrollPane1;
    private JTextArea tvLog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
