package swinggui;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class TagExtractorFrame extends JFrame
{
    Scanner fileName;
    LinkedHashMap wordCount = new LinkedHashMap<String, Integer>();
    String word; //Words from file
    Integer count;
    JPanel pnlTop, pnlLeft, pnlRight, pnlBot;
    JTextArea prompt, tags;
    JButton btnQuit, btnExtract, btnOk;
    JScrollPane scroll;
    JLabel fileExtractLbl, tagsExtractLbl;
    JTextField fileField, tagsField;
    
    String stopWords = "StopWords.txt";
    
    List<String> ignoreAll = new ArrayList<String>();
    
    
    public TagExtractorFrame() throws FileNotFoundException
    {
        add(this.createTopPanel(), BorderLayout.NORTH);
        add(this.createCenterPanel(), BorderLayout.WEST);
        add(this.createRightPanel(), BorderLayout.EAST);
        add(this.createBottomPanel(), BorderLayout.SOUTH);
        
        this.setSize(800, 925);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true); 
    }
    private Component createTopPanel() 
    {
        pnlTop = new JPanel();
        fileExtractLbl = new JLabel("Extract File:");
        fileField = new JTextField(40);
        btnExtract = new JButton("Extract File");
        btnExtract.addActionListener(e -> { 
        readFile();
        });
        
        pnlTop.add(fileExtractLbl);
        pnlTop.add(fileField);
        pnlTop.add(btnExtract);
        return pnlTop;
    }

    private Component createCenterPanel()
    {
        pnlLeft = new JPanel();
        prompt = new JTextArea(50, 30);
        scroll = new JScrollPane(prompt);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       
        pnlLeft.add(scroll);
        return pnlLeft;
    }

    private Component createRightPanel() 
    {
        pnlRight = new JPanel();
        tags = new JTextArea(50, 30);
        scroll = new JScrollPane(tags);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tags.setEditable(false);
        pnlRight.add(scroll);
        
        return pnlRight;
    }
   
    private Component createBottomPanel() 
    {
        pnlBot = new JPanel();
        btnQuit = new JButton("Quit");
        btnQuit.addActionListener(e -> { 
        System.exit(0);
        });
        
        tagsExtractLbl = new JLabel("File Path:");
        tagsField = new JTextField(40);
        btnOk = new JButton("Extract Tags");
        btnOk.addActionListener(e -> { 
        extractTags();
        });
        
        
        pnlBot.add(tagsExtractLbl);
        pnlBot.add(tagsField);
        pnlBot.add(btnOk);
        pnlBot.add(btnQuit);
        return pnlBot;
    } 
    
    private void readFile()
    {
        try
        {
            FileReader fileReader = new FileReader(stopWords);
            
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String location = fileField.getText();
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                ignoreAll.add(line.replaceAll("\"", ""));
            }
            
            bufferedReader.close();

            
            prompt.append("Searching for file: " + location + "\n");
            fileName = new Scanner(new FileReader(location));
            fileName.useDelimiter("(\\n)|(\\s+)");
            prompt.append("File has been found. Extracting tags. \n");
            while (fileName.hasNext())
                {
                    word = fileName.next();
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if(ignoreAll.contains(word))
                    {
                        continue;
                    }
                    else
                    {
                        incrementCount(word);
                    } 
                    populateTags();
                }
            populateTags();
            prompt.append("Tags have been extracted. \n");
            prompt.append("If you would like the tags to be extracted to a text file \n");
            prompt.append("please type in the file location below. Otherwise please \n");
            prompt.append("press 'Quit'. \n");
        }
        catch (IOException e)
        {
            prompt.append("Unable to find file. \n");
            prompt.append("Please re-type file name and folder path. \n");
        }
    }
    
    private int incrementCount(String word)
    {
        if(!wordCount.containsKey(word)){
            wordCount.put(word, 0);
        }
        int newCount = (int)wordCount.get(word) + 1;
        wordCount.put(word, newCount);
        return newCount;
    }
    
    private void populateTags()
    {
        tags.setText("");
        wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue().reversed())
                .limit(11).forEach(entry -> 
                {
                    tags.append(((Map.Entry)entry).getKey() + " : " + ((Map.Entry)entry).getValue() + "\n");
                });
    }

    private void extractTags()
    {
        String location = tagsField.getText();
        try 
        {
            PrintWriter writer = new PrintWriter(location);
            for(String word : (Set<String>)wordCount.keySet())
                {
                    writer.append(word + " : " + wordCount.get(word) + " ");
                    writer.append("\r\n");
                }
            writer.close();
            prompt.append("Tags have been extracted. \n");
        } 
        catch (FileNotFoundException ex) 
        {
            prompt.append("Unable to find file. \n");
            prompt.append("Please re-type file name and folder path. \n");
        }
    }
}
