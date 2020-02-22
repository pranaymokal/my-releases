package com.pranay.codegen;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class CodeGenerator extends JFrame {

  private static final long serialVersionUID = -2994404537244424914L;
  private static final String NIMBUS_THEME = "Nimbus";
  private static final String INFORMATION = "Information";
  private static final String ERROR = "Error";
  private JTextField oldEntityName;
  private JTextField newEntityName;
  private JLabel oldEntityLabel;
  private JLabel newEntityLabel;
  private JButton browseSource;
  private JButton browseDestination;
  private JTextField sourceText;
  private JTextField destinationText;
  private JButton generateCode;
  private JButton resetCode;
  private Container container;
  private JFileChooser sourceChooser;
  private JFileChooser destinationChooser;

  public CodeGenerator(String title) {
    super(title);

    container = getContentPane();
    container.setLayout(new GridLayout(6, 6));
    this.setTheme();
    oldEntityLabel = new JLabel(" Old Entity Name");
    oldEntityName = new JTextField(100);
    newEntityLabel = new JLabel(" New Entity Name");
    newEntityName = new JTextField(100);
    browseSource = new JButton("Select Source folder");
    browseDestination = new JButton("Select Destination folder");
    sourceText = new JTextField(100);
    destinationText = new JTextField(100);
    generateCode = new JButton("Generate Code");
    resetCode = new JButton("Reset");
    JMenuBar menuBarLeft = new JMenuBar();

    JMenu help = new JMenu("Help");
    JMenu about = new JMenu("About");

    JMenuBar menuBarRight = new JMenuBar();

    JMenuItem appInfo = new JMenuItem("Using Code Generator");
    JMenuItem aboutDev = new JMenuItem("About Developer");
    JMenuItem aboutApp = new JMenuItem("About Code Generator");

    help.add(appInfo);
    about.add(aboutDev);
    about.add(aboutApp);

    menuBarLeft.add(help);
    menuBarLeft.add(about);

    container.add(menuBarLeft);
    container.add(menuBarRight);

    container.add(oldEntityLabel);
    container.add(oldEntityLabel);

    container.add(oldEntityLabel);
    container.add(oldEntityName);
    container.add(newEntityLabel);
    container.add(newEntityName);
    container.add(browseSource);
    container.add(sourceText);
    container.add(browseDestination);
    container.add(destinationText);
    container.add(generateCode);
    container.add(resetCode);

    browseSource.addActionListener(ae -> {
      sourceChooser = getFileChooser("Select Source Folder");
      if (sourceChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        sourceText.setText(sourceChooser.getSelectedFile().getPath());
      } else {
        JOptionPane.showMessageDialog(this, "No Folder Selected", INFORMATION,
            JOptionPane.INFORMATION_MESSAGE);
      }
    });

    browseDestination.addActionListener(ae -> {
      destinationChooser = getFileChooser("Select Destination Folder");
      if (destinationChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        destinationText.setText(destinationChooser.getSelectedFile().getPath());
      } else {
        JOptionPane.showMessageDialog(this, "No Folder Selected", INFORMATION,
            JOptionPane.INFORMATION_MESSAGE);
      }
    });

    generateCode.addActionListener(ae -> {
      if (destinationText.getText().isEmpty() || sourceText.getText().isEmpty()
          || newEntityName.getText().isEmpty() || oldEntityName.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill/select Valid Details before Proceed",
            INFORMATION, JOptionPane.INFORMATION_MESSAGE);
      } else {

        generateCode(sourceText.getText(), destinationText.getText(), oldEntityName.getText(),
            newEntityName.getText());
      }
    });
    resetCode.addActionListener(ae -> {
      destinationText.setText("");
      sourceText.setText("");
      newEntityName.setText("");
      oldEntityName.setText("");
    });

    appInfo.addActionListener(ae -> JOptionPane.showMessageDialog(this,
        " Create Generic Template of Code and select all in source folder, \n Provider old entity name in it along with your new entity name. \n choose desination folder and click on Generate",
        "How to Use Code Generator?", JOptionPane.INFORMATION_MESSAGE));

    aboutDev.addActionListener(ae -> JOptionPane.showMessageDialog(this,
        " Developer : Pranay Sanjay Mokal, Connect: pranay.mokal@gmail.com", "About Developer",
        JOptionPane.INFORMATION_MESSAGE));

    aboutApp.addActionListener(ae -> JOptionPane.showMessageDialog(this,
        " Code Generator version 1.0.0 , Java Version : 8 , Developement Date :15/02/2020",
        "About Code Generator", JOptionPane.INFORMATION_MESSAGE));
  }

  private void generateCode(String sourceDir, String destinationDir, String oldEntity,
      String newEntity) {
    File sourceDirectory = new File(sourceDir);
    if (sourceDirectory.exists() && sourceDirectory.isDirectory()) {
      new Thread(() -> processCodeGeneration(sourceDirectory, destinationDir, oldEntity, newEntity))
          .start();
    }
  }

  private void processCodeGeneration(File sourceDirectory, String destinationDir, String oldEntity,
      String newEntity) {
    String oldEntityLowerCase = oldEntity.toLowerCase();
    String oldEntityUpperCase = oldEntity.toUpperCase();
    String oldEntityCapitalCase = getCapital(oldEntity.toLowerCase());

    String newEntityLowerCase = newEntity.toLowerCase();
    String newEntityUpperCase = newEntity.toUpperCase();
    String newEntityCapitalCase = getCapital(newEntity.toLowerCase());

    File[] files = sourceDirectory.listFiles();
    for (File file : files) {
      String fileContent = readFileContents(file);
      fileContent = fileContent.replaceAll(oldEntityLowerCase, newEntityLowerCase);
      fileContent = fileContent.replaceAll(oldEntityUpperCase, newEntityUpperCase);
      fileContent = fileContent.replaceAll(oldEntityCapitalCase, newEntityCapitalCase);
      String fileName = file.getName().replaceAll(oldEntityLowerCase, newEntityLowerCase);
      fileName = fileName.replaceAll(oldEntityUpperCase, newEntityUpperCase);
      fileName = fileName.replaceAll(oldEntityCapitalCase, newEntityCapitalCase);
      
      String source=sourceDirectory.getName().replaceAll(oldEntityLowerCase, newEntityLowerCase);
      source=source.replaceAll(oldEntityUpperCase, newEntityUpperCase);
      source =source.replaceAll(oldEntityCapitalCase, newEntityCapitalCase);

      String destinationDirectory = destinationDir + File.separator + source;
      File destinationFolder = new File(destinationDirectory);
      if (!destinationFolder.exists()) {
        destinationFolder.mkdirs();
      }
      writeFileContents(fileContent, destinationDirectory + File.separator + fileName);
    }
    JOptionPane.showMessageDialog(this,
        "Code has been generated Sucessfully at location" + destinationDir, INFORMATION,
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void writeFileContents(String fileContent, String filePath) {
    try {
      Files.write(Paths.get(new File(filePath).toURI()), fileContent.getBytes());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Error Occured while writing:" + e.getMessage(), ERROR,
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private String getCapital(String lowerCase) {
    final StringBuilder builder = new StringBuilder();
    char[] chars = lowerCase.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (i == 0) {
        builder.append(String.valueOf(chars[i]).toUpperCase());
      } else {
        builder.append(chars[i]);
      }
    }
    return builder.toString();
  }

  private String readFileContents(File file) {
    final StringBuilder builder = new StringBuilder();
    try (Stream<String> lines = Files.lines(Paths.get(file.toURI()))) {
      lines.forEach(line -> builder.append(line + "\n"));
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Error Occured while reading:" + e.getMessage(), ERROR,
          JOptionPane.ERROR_MESSAGE);
    }
    return builder.toString();
  }

  private JFileChooser getFileChooser(String title) {
    JFileChooser folderSelector = new JFileChooser();
    folderSelector.setCurrentDirectory(new File("."));
    folderSelector.setDialogTitle(title);
    folderSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    folderSelector.setAcceptAllFileFilterUsed(false);
    return folderSelector;
  }

  private void setTheme() {
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if (NIMBUS_THEME.equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error Occured:" + e, ERROR, JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      CodeGenerator codeGenerator = new CodeGenerator("Code Generator 1.0.0");
      codeGenerator.setVisible(true);
      codeGenerator.setSize(400, 300);
      codeGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    });
  }
}
