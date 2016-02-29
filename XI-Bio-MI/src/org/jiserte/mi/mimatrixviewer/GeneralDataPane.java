package org.jiserte.mi.mimatrixviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jiserte.bioformats.fastaIO.FastaMultipleReader;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.datastructures.Track;

import pair.Pair;

public class GeneralDataPane extends JPanel implements Observer{

	////////////////////////////////////////////////////////////////////////////
	// Constants
	private static final long serialVersionUID = 1052136447205136958L;
	private static final String ADD_DATA_FILE_COMMAND = "add";
	private static final String SET_ATTR_FILE_COMMAND = "set";
//	private static final String PICK_DATA_COMMAND = "pick";
//	private static final String SELECT_DATA_COMMAND = "pick";
	private static final String ADD_TRACK_COMMAND = "addTrack";
	private static final String ADD_MSA_COMMAND = "addMSA";
	private static final String ADD_POSITIVES_COMMAND = "addPositives";
	////////////////////////////////////////////////////////////////////////////

	
	////////////////////////////////////////////////////////////////////////////
	// Components
	private JButton addDataButton;
	private JButton removeData;
	private JButton setLengthsAndNamesButton;
	private JTextField protNamesTxt;
	private JTextField protLengthTxt;
	private JTextArea sequenceTxt;
	private Controller controller;
	private JList<CovariationData> dataList;
	private JPopupMenu popup;
	private JLabel infoLabel;


	//////////////////////////////////////////////////////////////////////////////

	public GeneralDataPane(Controller controller) {
		super();
		controller.registerModelObserver(this);
		this.setController(controller);
		this.createGUI();

	}
	////////////////////////////////////////////////////////////////////////////

	private void createGUI() {
		//////////////////////////
		// General Laout Settings
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		layout.columnWidths = new int[]{250,50};
		layout.columnWeights = new double[]{1,0};
		layout.rowHeights = new  int[]{0,25,25,25,25,0,25};
		layout.rowWeights = new  double[]{1,0,0,0,0,1,0};
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.insets= new Insets(3,3,3,3);
		this.setLayout(layout);
		///////////////////////////

		
		
		
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		dataList = new JList<CovariationData>();
		dataList.setCellRenderer(new CovariationDataCellRenderer());
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.addMouseListener( new PopupListener()) ;
		dataList.addListSelectionListener(new GeneralOptionDataSelectionListener());
		this.add(dataList,constraints);
		
	  //...where the GUI is constructed:
	  //Create the popup menu.
	  this.popup = new JPopupMenu();
	  
	  
		constraints.gridy = 1;
		 
		this.addDataButton = new JButton("Add data File");
		this.addDataButton.setActionCommand(ADD_DATA_FILE_COMMAND);
		GeneralOptionActionListener generalActionListener = new GeneralOptionActionListener();
		this.addDataButton.addActionListener(generalActionListener);
		this.add(this.addDataButton,constraints);
		
		
		constraints.gridy = 2;
		this.removeData = new JButton("Remove data file");
		this.add(removeData,constraints);

		
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		protNamesTxt = new JTextField("protein names");
		this.add(protNamesTxt,constraints);

		
		constraints.gridy = 4;
		protLengthTxt = new JTextField("protein lengths");
		this.add(protLengthTxt,constraints);
		
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		this.sequenceTxt = new JTextArea("Sequence");
		this.sequenceTxt.setWrapStyleWord(true);
		this.sequenceTxt.setLineWrap(true);
		this.sequenceTxt.setEditable(false);
		JScrollPane jsp1 = new JScrollPane(this.sequenceTxt);
		this.add(jsp1,constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridheight = 2;
		constraints.gridwidth = 1;
		this.setLengthsAndNamesButton = new JButton("Set");
		this.setLengthsAndNamesButton.setActionCommand(SET_ATTR_FILE_COMMAND);
		this.setLengthsAndNamesButton.addActionListener(generalActionListener);
		this.add(this.setLengthsAndNamesButton,constraints);
		
    constraints.gridx = 0;
    constraints.gridy = 6;
    constraints.gridheight = 1;
    constraints.gridwidth = 2;
    this.infoLabel = new JLabel("No info");
    this.add(this.infoLabel,constraints);
		
	}
	
	public void setInfo(String info) {
	  this.infoLabel.setText(info);
	}
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	class GeneralOptionActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			int keyModifiers = e.getModifiers();
//			Object src = e.getSource();
//			long time = e.getWhen();

			if (keyModifiers==16) {
			  
				////////////////////////////////////////////////////////////////////////
			  // Add a new file to the data
				if (actionCommand.equals(ADD_DATA_FILE_COMMAND)) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(true);
					int r = fileChooser.showOpenDialog(GeneralDataPane.this);
					if (r == JFileChooser.APPROVE_OPTION) {
						File[] selectedFiles = fileChooser.getSelectedFiles();
						GeneralDataPane.this.getController().addDataFiles(selectedFiles);
//						GeneralDataPane.this.sequenceTxt.setText(selectedFiles.toString());
					}
				}
        ////////////////////////////////////////////////////////////////////////
				

				if (actionCommand.equals(ADD_TRACK_COMMAND)) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setMultiSelectionEnabled(true);
          int r = fileChooser.showOpenDialog(GeneralDataPane.this);
          if (r == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File f : selectedFiles) {
              try {
                String description = (String)JOptionPane.showInputDialog(
                    GeneralDataPane.this,
                    "Enter a description for the Track in file: " + f.getName() + ".",
                    "Question",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    f.getName());
                Track track = Track.fromFile(f, description);
                dataList.getSelectedValue().addTrack(track);
              } catch (IOException e1) {
                e1.printStackTrace();
              }
            }

          }
				}
				
				
				
        if (actionCommand.equals(ADD_POSITIVES_COMMAND)) {
          
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setMultiSelectionEnabled(false);
          int r = fileChooser.showOpenDialog(GeneralDataPane.this);
          if (r == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
              FastaMultipleReader reader = new FastaMultipleReader();
              List<Pair<String, String>> msa;
              try {
                msa = reader.readFile(selectedFile);
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                
                Set<Pair<Integer,Integer>> pairs = new HashSet<>();
                String currentLine = null;
                while ( (currentLine = br.readLine()) != null) {
                  String[] fields = currentLine.split("\\s+");
                  if (fields.length==2) {
                    Pair<Integer, Integer> p;
                    Integer v1 = Integer.valueOf(fields[0]);
                    Integer v2 = Integer.valueOf(fields[1]);
                    p = (v1<v2) ? new Pair<>(v1,v2): new Pair<>(v2,v1) ;
                    pairs.add(p);
                  }
                }
                dataList.getSelectedValue().addPositives(pairs);
              } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }

            
            }

          }
				
				
				if (actionCommand.equals(ADD_MSA_COMMAND)) {
				  
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setMultiSelectionEnabled(false);
          int r = fileChooser.showOpenDialog(GeneralDataPane.this);
          if (r == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
              FastaMultipleReader reader = new FastaMultipleReader();
              List<Pair<String, String>> msa;
              try {
                msa = reader.readFile(selectedFile);
                dataList.getSelectedValue().addMsa(msa);
              } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }

            
            }

          }

				  


				if (actionCommand.equals(SET_ATTR_FILE_COMMAND)) {
					
				    try {
				    	
				    	List<String> names= GeneralDataPane.this.getListOfNames();
				        
				        List<Integer> lengths= GeneralDataPane.this.getListOfLengths();
				        
				        ////////////////////////////////////////////////////////
				        // Validate lengths:
				        // Conditions:
				        // 1) Sum of lengths == matrix size or zero
				        // 2) if size of names is 1 or 0, lengths size must be
				        //    1 or zero
				        // 3) else, size of names  == size of lengths 
				        ////////////////////////////////////////////////////////
				        
				        int sum = 0; 
				        for (Integer integer : lengths) {
							sum += integer;
						}
				        boolean cond1 = sum == GeneralDataPane.this.
				        		getController().
				        		getCurrentMatrixSize() 
				        		|| 
				        		sum == 0;
				        
				        boolean cond2 = (names.size()==0 || names.size()==1 &&
				        		        lengths.size() ==0 || names.size()==1)
				        		        ||
				        		        (names.size() == lengths.size());
				        
				        if (! (cond1 && cond2)) {
				        	
				        	JOptionPane.showMessageDialog(GeneralDataPane.this, "La cantidad de nombres y longitudes de proteínas no son correctos");
				        	
				        } else {
				        	
				        	GeneralDataPane.this.getController().setCurrentMatrixNames(names);
				        	GeneralDataPane.this.getController().setCurrentMatrixLengths(lengths);
				        	
				        }
				        
				    }
				    
				    catch (NumberFormatException e1) {
				    
				    	JOptionPane.showMessageDialog(GeneralDataPane.this, "Hubo un error al intentar parsear las longitudes");
				        
				    }
					
					
					
				}

			}

		}

	}
	
	class GeneralOptionDataSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
		  CovariationData value = GeneralDataPane.this.dataList.getSelectedValue();
			if (value!=null) {
				GeneralDataPane.this.sequenceTxt.setText(String.valueOf(value.getReferenceSequence()));
				GeneralDataPane.this.controller.setActiveData(value);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		List<CovariationData> l = ((Model)o).getData();
		
		if (((Model)o).hasDataChanged()) {
  		DefaultListModel<CovariationData> a = new DefaultListModel<CovariationData>();
  		for (CovariationData dataContainer : l) {
  			a.addElement(dataContainer);
  		}
  		this.dataList.setModel(a);
		}
		
	}
	
	public List<Integer> getListOfLengths() throws NumberFormatException {
		
		String[] numbersAsString = this.protLengthTxt.getText().split(", *");
		List<Integer> numbers = new ArrayList<Integer>();
		for (String string : numbersAsString) {

			numbers.add(Integer.valueOf(string));
			
		}
		
		return numbers;
		
	}
	public List<String> getListOfNames() {

		String[] namesArray = this.protNamesTxt.getText().split(", *");
		
		return Arrays.asList(namesArray);
	}

	class CovariationDataCellRenderer extends JPanel implements ListCellRenderer<CovariationData> {
	  /**
     * 
     */
    private static final long serialVersionUID = 2565584652245559927L;
    private final GridLayout layout = new GridLayout(3, 1, 0, 5);
	  private final Color sel = new Color(225,245,225);
	  private final Color noSel = new Color(225,225,225);
	  private JLabel comp = new JLabel();
	  private JLabel line1 = new JLabel();
	  private JLabel line2 = new JLabel();
	  private final Font focus = comp.getFont().deriveFont(Font.BOLD);
    private final Font plain = comp.getFont().deriveFont(Font.PLAIN);
	  
		@Override
		public Component getListCellRendererComponent(
				JList<? extends CovariationData> list, CovariationData value,
				int index, boolean isSelected, boolean cellHasFocus) {

			this.setLayout(layout);
			this.setOpaque(true);
			this.setBackground(isSelected?sel:noSel);
			comp.setText(value.getTitle());
			comp.setFont(cellHasFocus?focus:plain);
			this.add(comp);
			line1.setText(String.valueOf(value.getNumberOfElements() + " elements. / " + String.valueOf(value.getMatrixSize()) + " columns."));
			line2.setText(String.valueOf(value.getTrackCount() + " Tracks."));
			this.add(line1);
			this.add(line2);
			return this;
		}
	}
	
	class PopupListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
    public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popup.removeAll();
          JMenuItem menuItem = new JMenuItem("Load a new File");
          menuItem.setActionCommand(ADD_DATA_FILE_COMMAND);
          menuItem.addActionListener(new GeneralOptionActionListener());
          popup.add(menuItem);
          if (GeneralDataPane.this.dataList.getSelectedIndex() >= 0) {
            menuItem = new JMenuItem("Add Track.");
            menuItem.setActionCommand(ADD_TRACK_COMMAND);
            menuItem.addActionListener(new GeneralOptionActionListener());
            popup.add(menuItem);
            menuItem = new JMenuItem("Add MSA.");
            menuItem.setActionCommand(ADD_MSA_COMMAND);
            menuItem.addActionListener(new GeneralOptionActionListener());
            popup.add(menuItem);
            menuItem = new JMenuItem("Add Positives.");
            menuItem.setActionCommand(ADD_POSITIVES_COMMAND);
            menuItem.addActionListener(new GeneralOptionActionListener());
            popup.add(menuItem);
          }
            popup.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }
}
	
	
	
}

