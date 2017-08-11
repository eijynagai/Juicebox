/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.gui;

import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideToggleButton;
import juicebox.Context;
import juicebox.HiC;
import juicebox.HiCGlobals;
import juicebox.data.ChromosomeHandler;
import juicebox.data.MatrixZoomData;
import juicebox.mapcolorui.HeatmapPanel;
import juicebox.mapcolorui.JColorRangePanel;
import juicebox.mapcolorui.ResolutionControl;
import juicebox.mapcolorui.ThumbnailPanel;
import juicebox.track.TrackLabelPanel;
import juicebox.track.TrackPanel;
import juicebox.track.feature.AnnotationLayerHandler;
import juicebox.windowui.*;
import org.broad.igv.Globals;
import org.broad.igv.feature.Chromosome;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammadsaadshamim on 8/4/15.
 */
public class MainViewPanel {

    public static final List<Color> preDefMapColorGradient = HiCGlobals.createNewPreDefMapColorGradient();
    public static final List<Float> preDefMapColorFractions = new ArrayList<>();
    public static boolean preDefMapColor = false;
    private static JComboBox<Chromosome> chrBox1;
    private static JComboBox<Chromosome> chrBox2;
    private static JideButton refreshButton;
    private static JComboBox<String> normalizationComboBox;
    private static JComboBox<MatrixType> displayOptionComboBox;
    private static JColorRangePanel colorRangePanel;
    private static ResolutionControl resolutionSlider;
    private static TrackPanel trackPanelX;
    private static TrackPanel trackPanelY;
    private static TrackLabelPanel trackLabelPanel;
    private static HiCRulerPanel rulerPanelX;
    private static HeatmapPanel heatmapPanel;
    private static HiCRulerPanel rulerPanelY;
    private static ThumbnailPanel thumbnailPanel;
    private static JEditorPane mouseHoverTextPanel;
    private static GoToPanel goPanel;
    private static JPanel hiCPanel;
    private static HiCChromosomeFigPanel chromosomePanelX;
    private static HiCChromosomeFigPanel chromosomePanelY;
    private static JPanel bottomChromosomeFigPanel;
    private static JPanel chrSidePanel;
    private static JPanel chrSidePanel3;
    private final JToggleButton annotationsPanelToggleButton = new JToggleButton("Show Annotation Panel");
    private JPanel annotationsLayerPanel;
    private JPanel tooltipPanel;
    private boolean tooltipAllowedToUpdated = true;
    private boolean ignoreUpdateThumbnail = false;
    private int miniButtonSize = 22;
    private JideButton btnMenu;
    private JPanel menuTabPanel;
    private JideToggleButton btnRightPnl;
    private boolean menuTabOpen = false;
    private boolean rightPnlOpen = false;
    private JPanel sliderPanel;
    private final JideLabel sliderLbl = new JideLabel();


    public void setIgnoreUpdateThumbnail(boolean flag) {
        ignoreUpdateThumbnail = true;
    }

    public JComboBox<Chromosome> getChrBox2() {
        return chrBox2;
    }

    public JComboBox<Chromosome> getChrBox1() {
        return chrBox1;
    }

    public void initializeMainView(final SuperAdapter superAdapter, Container contentPane,
                                   Dimension bigPanelDim, Dimension panelDim) {
        contentPane.setLayout(new BorderLayout());

        final JPanel mainPanel = new JPanel(); // The main content panel.
        mainPanel.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBackground(Color.white);

//        final JPanel toolbarPanel = new JPanel(); // Chromosomes, Show, Normalization, Resolution, ColorRange, Goto
//        toolbarPanel.setBorder(null);
//
//        toolbarPanel.setLayout(new GridBagLayout());
////        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        final JLayeredPane bigPanel = new JLayeredPane(); //Hi-C Map
//        bigPanel.setLayout(new BorderLayout());
        bigPanel.setBackground(Color.white);
        SpringLayout sl_bigPanel = new SpringLayout();
        bigPanel.setLayout(sl_bigPanel);

        bigPanel.setPreferredSize(new Dimension(bigPanelDim));
        bigPanel.setMaximumSize(new Dimension(bigPanelDim));
        bigPanel.setMinimumSize(new Dimension(bigPanelDim));

//        JPanel bottomPanel = new JPanel();
//        bottomPanel.setBackground(Color.white);


        JMenuBar menuBar = null;
        try {
            menuBar = superAdapter.createMenuBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert menuBar != null;
        contentPane.add(menuBar, BorderLayout.NORTH);

        GridBagConstraints toolbarConstraints = new GridBagConstraints();
        toolbarConstraints.anchor = GridBagConstraints.LINE_START;
        toolbarConstraints.fill = GridBagConstraints.HORIZONTAL;
        toolbarConstraints.gridx = 0;
        toolbarConstraints.gridy = 0;
        toolbarConstraints.weightx = 0.1;

        // --- Chromosome panel ---
        JPanel chrSelectionPanel = new JPanel();
//        toolbarPanel.add(chrSelectionPanel, toolbarConstraints);

        chrSelectionPanel.setBorder(LineBorder.createGrayLineBorder());

        chrSelectionPanel.setLayout(new BorderLayout());

        JPanel chrLabelPanel = new JPanel();
        JLabel chrLabel = new JLabel("Chromosomes");
        chrLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        chrLabelPanel.setBackground(HiCGlobals.backgroundColor);
        chrLabelPanel.setBackground(Color.WHITE);
        chrLabelPanel.setLayout(new BorderLayout());
        chrLabelPanel.add(chrLabel, BorderLayout.CENTER);
        chrSelectionPanel.add(chrLabelPanel, BorderLayout.PAGE_START);

        JPanel chrButtonPanel = new JPanel();
//        chrButtonPanel.setBackground(new Color(238, 238, 238));
        chrButtonPanel.setBackground(Color.WHITE);
        chrButtonPanel.setLayout(new BoxLayout(chrButtonPanel, BoxLayout.X_AXIS));

        //---- chrBox1 ----
        chrBox1 = new JComboBox<>(new Chromosome[]{new Chromosome(0, Globals.CHR_ALL, 0)});
        chrBox1.addPopupMenuListener(new BoundsPopupMenuListener<Chromosome>(true, false));
        chrBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chrBox1ActionPerformed(e);
            }
        });
        chrBox1.setPreferredSize(new Dimension(95, 70));
        chrButtonPanel.add(chrBox1);

        //---- chrBox2 ----
        chrBox2 = new JComboBox<>(new Chromosome[]{new Chromosome(0, Globals.CHR_ALL, 0)});
        chrBox2.addPopupMenuListener(new BoundsPopupMenuListener<Chromosome>(true, false));
        chrBox2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chrBox2ActionPerformed(e);
            }
        });
        chrBox2.setPreferredSize(new Dimension(95, 70));
        chrButtonPanel.add(chrBox2);


        //---- refreshButton ----
        refreshButton = new JideButton();
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh24.gif")));
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                superAdapter.safeRefreshButtonActionPerformed();
            }
        });
        refreshButton.setPreferredSize(new Dimension(24, 24));
        chrButtonPanel.add(refreshButton);

        chrBox1.setEnabled(false);
        chrBox2.setEnabled(false);
        refreshButton.setEnabled(false);
        chrSelectionPanel.add(chrButtonPanel, BorderLayout.CENTER);

        chrSelectionPanel.setMinimumSize(new Dimension(200, 70));
        chrSelectionPanel.setPreferredSize(new Dimension(210, 70));

        //======== Display Option Panel ========
        JPanel displayOptionPanel = new JPanel();
//        displayOptionPanel.setBackground(new Color(238, 238, 238));
        displayOptionPanel.setBackground(Color.WHITE);
        displayOptionPanel.setBorder(LineBorder.createGrayLineBorder());
        displayOptionPanel.setLayout(new BorderLayout());
        JPanel displayOptionLabelPanel = new JPanel();
//        displayOptionLabelPanel.setBackground(HiCGlobals.backgroundColor);
        displayOptionLabelPanel.setBackground(Color.WHITE);
        displayOptionLabelPanel.setLayout(new BorderLayout());


        JLabel displayOptionLabel = new JLabel("Show");
        displayOptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        displayOptionLabelPanel.add(displayOptionLabel, BorderLayout.CENTER);
        displayOptionPanel.add(displayOptionLabelPanel, BorderLayout.PAGE_START);
        JPanel displayOptionButtonPanel = new JPanel();
        displayOptionButtonPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        displayOptionButtonPanel.setLayout(new GridLayout(1, 0, 20, 0));
        displayOptionComboBox = new JComboBox<>(new MatrixType[]{MatrixType.OBSERVED});
        displayOptionComboBox.setPreferredSize(new Dimension(500, 30));
        displayOptionComboBox.addPopupMenuListener(new BoundsPopupMenuListener<MatrixType>(true, false));
        displayOptionComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                superAdapter.safeDisplayOptionComboBoxActionPerformed();
                normalizationComboBox.setEnabled(!isWholeGenome());
            }
        });
        displayOptionButtonPanel.add(displayOptionComboBox);
        displayOptionButtonPanel.setBackground(Color.WHITE);
        displayOptionPanel.add(displayOptionButtonPanel, BorderLayout.CENTER);
        displayOptionPanel.setMinimumSize(new Dimension(140, 70));
        displayOptionPanel.setPreferredSize(new Dimension(140, 70));
        displayOptionPanel.setMaximumSize(new Dimension(140, 70));

        toolbarConstraints.gridx = 1;
        toolbarConstraints.weightx = 0.1;
//        toolbarPanel.add(displayOptionPanel, toolbarConstraints);
        displayOptionComboBox.setEnabled(false);

        //======== Normalization Panel ========
        JPanel normalizationPanel = new JPanel();
//        normalizationPanel.setBackground(new Color(238, 238, 238));
        normalizationPanel.setBackground(Color.WHITE);
        normalizationPanel.setBorder(LineBorder.createGrayLineBorder());
        normalizationPanel.setLayout(new BorderLayout());

        JPanel normalizationLabelPanel = new JPanel();
//        normalizationLabelPanel.setBackground(HiCGlobals.backgroundColor);
        normalizationLabelPanel.setBackground(Color.WHITE);
        normalizationLabelPanel.setLayout(new BorderLayout());

        JLabel normalizationLabel = new JLabel("Normalization");
        normalizationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        normalizationLabelPanel.add(normalizationLabel, BorderLayout.CENTER);
        normalizationPanel.add(normalizationLabelPanel, BorderLayout.PAGE_START);

        JPanel normalizationButtonPanel = new JPanel();
        normalizationButtonPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        normalizationButtonPanel.setLayout(new GridLayout(1, 0, 20, 0));
        normalizationComboBox = new JComboBox<>(new String[]{NormalizationType.NONE.getLabel()});
        normalizationComboBox.addPopupMenuListener(new BoundsPopupMenuListener<String>(true, false));
        normalizationComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                superAdapter.safeNormalizationComboBoxActionPerformed(e);
            }
        });
        normalizationButtonPanel.add(normalizationComboBox);
        normalizationButtonPanel.setBackground(Color.WHITE);
        normalizationPanel.add(normalizationButtonPanel, BorderLayout.CENTER);
        normalizationPanel.setPreferredSize(new Dimension(180, 70));
        normalizationPanel.setMinimumSize(new Dimension(140, 70));


        toolbarConstraints.gridx = 2;
        toolbarConstraints.weightx = 0.1;
//        toolbarPanel.add(normalizationPanel, toolbarConstraints);
        normalizationComboBox.setEnabled(false);

        //======== Resolution Panel ========
        hiCPanel = new JPanel();
        hiCPanel.setBackground(Color.white);
        hiCPanel.setLayout(new HiCLayout());

        sl_bigPanel.putConstraint(SpringLayout.NORTH, hiCPanel, 10, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.WEST, hiCPanel, 33, SpringLayout.WEST, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.SOUTH, hiCPanel, -7, SpringLayout.SOUTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.EAST, hiCPanel, -30, SpringLayout.EAST, bigPanel);
//        bigPanel.add(hiCPanel, BorderLayout.CENTER);
        bigPanel.add(hiCPanel);

//        JPanel wrapGapPanel = new JPanel();
//        wrapGapPanel.setBackground(Color.BLACK);
//        wrapGapPanel.setMaximumSize(new Dimension(5, 5));
//        wrapGapPanel.setMinimumSize(new Dimension(5, 5));
//        wrapGapPanel.setPreferredSize(new Dimension(5, 5));
//        wrapGapPanel.setBorder(LineBorder.createBlackLineBorder());
//        bigPanel.add(wrapGapPanel, BorderLayout.EAST);


        // splitPanel.insertPane(hiCPanel, 0);
        // splitPanel.setBackground(Color.white);


        //---- rulerPanel2 ----
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setLayout(new BorderLayout());
        hiCPanel.add(topPanel, BorderLayout.NORTH);
        trackLabelPanel = new TrackLabelPanel(superAdapter.getHiC());
        trackLabelPanel.setBackground(Color.white);
        hiCPanel.add(trackLabelPanel, HiCLayout.NORTH_WEST);

        JPanel topCenterPanel = new JPanel();
        topCenterPanel.setBackground(Color.BLUE);
        topCenterPanel.setLayout(new BorderLayout());
        topPanel.add(topCenterPanel, BorderLayout.CENTER);

        trackPanelX = new TrackPanel(superAdapter, superAdapter.getHiC(), TrackPanel.Orientation.X);
        trackPanelX.setMaximumSize(new Dimension(4000, 50));
        trackPanelX.setPreferredSize(new Dimension(1, 50));
        trackPanelX.setMinimumSize(new Dimension(1, 50));
        trackPanelX.setVisible(false);
        topCenterPanel.add(trackPanelX, BorderLayout.NORTH);

        rulerPanelX = new HiCRulerPanel(superAdapter.getHiC());
        rulerPanelX.setMaximumSize(new Dimension(4000, 50));
        rulerPanelX.setMinimumSize(new Dimension(1, 50));
        rulerPanelX.setPreferredSize(new Dimension(1, 50));
        rulerPanelX.setBorder(null);
        topCenterPanel.add(rulerPanelX, BorderLayout.SOUTH);

        //---- rulerPanel1 ----
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.white);
        leftPanel.setLayout(new BorderLayout());
        hiCPanel.add(leftPanel, BorderLayout.WEST);

        trackPanelY = new TrackPanel(superAdapter, superAdapter.getHiC(), TrackPanel.Orientation.Y);
        trackPanelY.setMaximumSize(new Dimension(50, 4000));
        trackPanelY.setPreferredSize(new Dimension(50, 1));
        trackPanelY.setMinimumSize(new Dimension(50, 1));
        trackPanelY.setVisible(false);
        leftPanel.add(trackPanelY, BorderLayout.WEST);

        rulerPanelY = new HiCRulerPanel(superAdapter.getHiC());
        rulerPanelY.setMaximumSize(new Dimension(50, 4000));
        rulerPanelY.setPreferredSize(new Dimension(50, 800));
        rulerPanelY.setBorder(null);
        rulerPanelY.setMinimumSize(new Dimension(50, 1));
        leftPanel.add(rulerPanelY, BorderLayout.EAST);


        //==== Chromosome Context Toggled ====
        //---- chromosomeSidePanel ----
        chrSidePanel = new JPanel();
        chrSidePanel.setBackground(Color.white);
        chrSidePanel.setLayout(new BorderLayout());
        chrSidePanel.setMaximumSize(new Dimension(4000, 50));
        chrSidePanel.setPreferredSize(new Dimension(50, 50));
        chrSidePanel.setMinimumSize(new Dimension(50, 50));
        chrSidePanel.setVisible(true);

        JPanel chrSidePanel2 = new JPanel();
        chrSidePanel2.setBackground(Color.white);
        chrSidePanel2.setLayout(new BorderLayout());
        chrSidePanel2.setMaximumSize(new Dimension(50, 50));
        chrSidePanel2.setPreferredSize(new Dimension(50, 50));
        chrSidePanel2.setMinimumSize(new Dimension(50, 50));

        chrSidePanel3 = new JPanel();
        chrSidePanel3.setBackground(Color.white);
        chrSidePanel3.setLayout(new BorderLayout());
        chrSidePanel3.setMaximumSize(new Dimension(50, 4000));
        chrSidePanel3.setPreferredSize(new Dimension(50, 50));
        chrSidePanel3.setMinimumSize(new Dimension(50, 50));
        chrSidePanel3.setVisible(true);

        //---- chromosomeFigPanel2 ----
        bottomChromosomeFigPanel = new JPanel();
        bottomChromosomeFigPanel.setBackground(Color.white);
        bottomChromosomeFigPanel.setLayout(new BorderLayout());
        //bottomChromosomeFigPanel.setVisible(true);

        chromosomePanelX = new HiCChromosomeFigPanel(superAdapter.getHiC());
        chromosomePanelX.setMaximumSize(new Dimension(4000, 50));
        chromosomePanelX.setPreferredSize(new Dimension(1, 50));
        chromosomePanelX.setMinimumSize(new Dimension(1, 50));
        bottomChromosomeFigPanel.add(chromosomePanelX, BorderLayout.CENTER);
        bottomChromosomeFigPanel.add(chrSidePanel2, BorderLayout.EAST);
        bottomChromosomeFigPanel.setVisible(true);

        leftPanel.add(chrSidePanel, BorderLayout.SOUTH);
        topPanel.add(chrSidePanel3, BorderLayout.EAST);

        //---- chromosomeFigPanel1 ----
        chromosomePanelY = new HiCChromosomeFigPanel(superAdapter.getHiC());
        chromosomePanelY.setMaximumSize(new Dimension(50, 4000));
        chromosomePanelY.setPreferredSize(new Dimension(50, 1));
        chromosomePanelY.setMinimumSize(new Dimension(50, 1));
        chromosomePanelY.setVisible(true);


        //---- heatmapPanel ----
        //Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        //int panelSize = screenDimension.height - 210;


        int panelWidth = (int) panelDim.getWidth();
        int panelHeight = (int) panelDim.getHeight();
        System.err.println("Window W: " + panelWidth + " H" + panelHeight);

        JPanel wrapHeatmapPanel = new JPanel(new BorderLayout());
        wrapHeatmapPanel.setMaximumSize(new Dimension(panelDim));
        wrapHeatmapPanel.setMinimumSize(new Dimension(panelDim));
        wrapHeatmapPanel.setPreferredSize(new Dimension(panelDim));
        wrapHeatmapPanel.setBackground(Color.white);
        wrapHeatmapPanel.setVisible(true);

        heatmapPanel = new HeatmapPanel(superAdapter);
        heatmapPanel.setMaximumSize(new Dimension(panelWidth - 5, panelHeight - 5));
        heatmapPanel.setMinimumSize(new Dimension(panelWidth - 5, panelHeight - 5));
        heatmapPanel.setPreferredSize(new Dimension(panelWidth - 5, panelHeight - 5));
        heatmapPanel.setBackground(Color.white);
        SpringLayout sl_heatmapPanel = new SpringLayout();
        heatmapPanel.setLayout(sl_heatmapPanel);

        wrapHeatmapPanel.add(heatmapPanel, BorderLayout.CENTER);

        //Chromosome Context Toggled
        wrapHeatmapPanel.add(bottomChromosomeFigPanel, BorderLayout.SOUTH);
        wrapHeatmapPanel.add(chromosomePanelY, BorderLayout.EAST);

        //hiCPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        //hiCPanel.setMinimumSize(new Dimension(panelWidth, panelHeight));
        //hiCPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        hiCPanel.add(wrapHeatmapPanel, BorderLayout.CENTER);

        //======== Resolution Slider Panel ========

        // Resolution  panel
        resolutionSlider = new ResolutionControl(superAdapter);
        resolutionSlider.setPreferredSize(new Dimension(200, 70));
        resolutionSlider.setMinimumSize(new Dimension(150, 70));

//        toolbarConstraints.gridx = 3;
//        toolbarConstraints.weightx = 0.1;
//        toolbarPanel.add(resolutionSlider, toolbarConstraints);
//        sl_bigPanel.putConstraint(SpringLayout.NORTH, resolutionSlider, -70, SpringLayout.SOUTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.WEST, resolutionSlider, 10, SpringLayout.EAST, hiCPanel);
//        sl_bigPanel.putConstraint(SpringLayout.SOUTH, resolutionSlider, -10, SpringLayout.SOUTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.EAST, resolutionSlider, -10, SpringLayout.EAST, bigPanel);
//        bigPanel.add(resolutionSlider);

        //======== Color Range Panel ========
        colorRangePanel = new JColorRangePanel(superAdapter, heatmapPanel, preDefMapColor);
//        sl_bigPanel.putConstraint(SpringLayout.NORTH, colorRangePanel, -150, SpringLayout.SOUTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.WEST, colorRangePanel, -170, SpringLayout.EAST, resolutionSlider);
//        sl_bigPanel.putConstraint(SpringLayout.SOUTH, colorRangePanel, -80, SpringLayout.SOUTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.EAST, colorRangePanel, 0, SpringLayout.EAST, resolutionSlider);
//        bigPanel.add(colorRangePanel);

        toolbarConstraints.gridx = 4;
        toolbarConstraints.weightx = 0.5;
//        toolbarPanel.add(colorRangePanel, toolbarConstraints);

        goPanel = new GoToPanel(superAdapter);
//        sl_bigPanel.putConstraint(SpringLayout.NORTH, goPanel, 10, SpringLayout.NORTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.WEST, goPanel, -170, SpringLayout.EAST, resolutionSlider);
//        sl_bigPanel.putConstraint(SpringLayout.SOUTH, goPanel, 70, SpringLayout.NORTH, bigPanel);
//        sl_bigPanel.putConstraint(SpringLayout.EAST, goPanel, 0, SpringLayout.EAST, resolutionSlider);
//        bigPanel.add(goPanel);
//        toolbarConstraints.gridx = 5;
        // not sure this is working
        //toolbarPanel.setPreferredSize(new Dimension(panelHeight,100));
//        toolbarPanel.setEnabled(false);


        //======== Right side panel ========

        final JPanel rightSidePanel = new JPanel(new BorderLayout());//(new BorderLayout());
        rightSidePanel.setBackground(Color.white);
        rightSidePanel.setPreferredSize(new Dimension(210, 1000));
        rightSidePanel.setMaximumSize(new Dimension(10000, 10000));

        //======== Bird's view mini map ========

        JPanel thumbPanel = new JPanel();
        thumbPanel.setLayout(new BorderLayout());

        //---- thumbnailPanel ----
//        thumbnailPanel = new ThumbnailPanel(superAdapter);
//        thumbnailPanel.setBackground(Color.white);
//        thumbnailPanel.setMaximumSize(new Dimension(210, 210));
//        thumbnailPanel.setMinimumSize(new Dimension(210, 210));
//        thumbnailPanel.setPreferredSize(new Dimension(210, 210));
//
////        JPanel gapPanel = new JPanel();
////        gapPanel.setMaximumSize(new Dimension(1, 1));
////        rightSidePanel.add(gapPanel,BorderLayout.WEST);
//        thumbPanel.add(thumbnailPanel, BorderLayout.CENTER);
//        thumbPanel.setBackground(Color.white);
//        rightSidePanel.add(thumbPanel, BorderLayout.NORTH);

        //========= mouse hover text ======
        tooltipPanel = new JPanel(new BorderLayout());
        tooltipPanel.setBackground(Color.white);
        tooltipPanel.setPreferredSize(new Dimension(210, 700));
        mouseHoverTextPanel = new JEditorPane();
        mouseHoverTextPanel.setEditable(false);
        mouseHoverTextPanel.setContentType("text/html");
        mouseHoverTextPanel.setFont(new Font("sans-serif", 0, 20));
        mouseHoverTextPanel.setBackground(Color.white);
        mouseHoverTextPanel.setBorder(null);
        int mouseTextY = rightSidePanel.getBounds().y + rightSidePanel.getBounds().height;

        //*Dimension prefSize = new Dimension(210, 490);
        Dimension prefSize = new Dimension(210, 210);
        mouseHoverTextPanel.setPreferredSize(prefSize);

        JScrollPane tooltipScroller = new JScrollPane(mouseHoverTextPanel);
        tooltipScroller.setBackground(Color.white);
        tooltipScroller.setBorder(null);

        annotationsLayerPanel = generate2DAnnotationsLayerSelectionPanel(superAdapter);
        annotationsLayerPanel.setBackground(Color.gray);
        annotationsLayerPanel.setPreferredSize(new Dimension(210, 160));
//        annotationsLayerPanel.setToolTipText("Hello!");

//        tooltipPanel.setPreferredSize(new Dimension(210, 500));
        tooltipPanel.add(tooltipScroller, BorderLayout.NORTH);
        tooltipPanel.add(annotationsLayerPanel, BorderLayout.SOUTH);
        tooltipPanel.setBounds(new Rectangle(new Point(0, mouseTextY), prefSize));
        tooltipPanel.setBackground(Color.white);
        tooltipPanel.setBorder(null);

        rightSidePanel.add(tooltipPanel, BorderLayout.CENTER);

        annotationsPanelToggleButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (annotationsPanelToggleButton.isSelected()) {
                    annotationsPanelToggleButton.setText("Hide Annotation Panel");
                } else {
                    annotationsPanelToggleButton.setText("Show Annotation Panel");
                }
            }
        });
        annotationsPanelToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (annotationsPanelToggleButton.isSelected()) {
                    superAdapter.setLayersPanelVisible(true);
                    annotationsPanelToggleButton.setText("Hide Annotation Panel");
                } else {
                    superAdapter.setLayersPanelVisible(false);
                    annotationsPanelToggleButton.setText("Show Annotation Panel");
                }
            }
        });
        annotationsPanelToggleButton.setSelected(false);
        annotationsPanelToggleButton.setEnabled(false);
        rightSidePanel.add(annotationsPanelToggleButton, BorderLayout.SOUTH);


        //======= Menu Tab =======
        menuTabPanel = new JPanel();
        SpringLayout sl_menuTabPanel = new SpringLayout();
        menuTabPanel.setLayout(sl_menuTabPanel);
        menuTabPanel.setBackground(Color.WHITE);
        menuTabPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

        sl_bigPanel.putConstraint(SpringLayout.NORTH, menuTabPanel, 0, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.WEST, menuTabPanel, 0, SpringLayout.WEST, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.SOUTH, menuTabPanel, 50 + 70 * 4, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.EAST, menuTabPanel, 215, SpringLayout.WEST, bigPanel);
        bigPanel.setLayer(menuTabPanel, 1);
        bigPanel.add(menuTabPanel);

        menuTabPanel.setVisible(false);

        //======= Menu Tab Button ========
        btnMenu = new JideButton();
        ImageIcon menuIcon = new ImageIcon(getClass().getResource("/images/menu.png"));
        btnMenu.setIcon(menuIcon);
        btnMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuTabOpen = !menuTabOpen;
                menuTabPanel.setVisible(menuTabOpen);
                btnMenu.setEnabled(false);
            }
        });
        sl_bigPanel.putConstraint(SpringLayout.NORTH, btnMenu, 10, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.WEST, btnMenu, 10, SpringLayout.WEST, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.SOUTH, btnMenu, 33, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.EAST, btnMenu, 33, SpringLayout.WEST, bigPanel);
        bigPanel.add(btnMenu);

        //====== Menu Tab Components ========
        JideButton btnClose = new JideButton();
        ImageIcon closeIcon = new ImageIcon(getClass().getResource("/images/close.png"));
        btnClose.setIcon(closeIcon);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuTabOpen = !menuTabOpen;
                menuTabPanel.setVisible(menuTabOpen);
                btnMenu.setEnabled(true);
            }
        });
        sl_menuTabPanel.putConstraint(SpringLayout.NORTH, btnClose, 10, SpringLayout.NORTH, menuTabPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.WEST, menuTabPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.SOUTH, btnClose, 33, SpringLayout.NORTH, menuTabPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.EAST, btnClose, 33, SpringLayout.WEST, menuTabPanel);
        menuTabPanel.add(btnClose);

        sl_menuTabPanel.putConstraint(SpringLayout.NORTH, chrSelectionPanel, 13, SpringLayout.SOUTH, btnClose);
        sl_menuTabPanel.putConstraint(SpringLayout.WEST, chrSelectionPanel, 0, SpringLayout.WEST, menuTabPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.SOUTH, chrSelectionPanel, 83, SpringLayout.SOUTH, btnClose);
        sl_menuTabPanel.putConstraint(SpringLayout.EAST, chrSelectionPanel, 210, SpringLayout.WEST, menuTabPanel);
        menuTabPanel.add(chrSelectionPanel);

        sl_menuTabPanel.putConstraint(SpringLayout.NORTH, displayOptionPanel, 0, SpringLayout.SOUTH, chrSelectionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.WEST, displayOptionPanel, 0, SpringLayout.WEST, chrSelectionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.SOUTH, displayOptionPanel, 70, SpringLayout.SOUTH, chrSelectionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.EAST, displayOptionPanel, 0, SpringLayout.EAST, chrSelectionPanel);
        menuTabPanel.add(displayOptionPanel);

        sl_menuTabPanel.putConstraint(SpringLayout.NORTH, normalizationPanel, 0, SpringLayout.SOUTH, displayOptionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.WEST, normalizationPanel, 0, SpringLayout.WEST, displayOptionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.SOUTH, normalizationPanel, 70, SpringLayout.SOUTH, displayOptionPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.EAST, normalizationPanel, 0, SpringLayout.EAST, displayOptionPanel);
        menuTabPanel.add(normalizationPanel);

        sl_menuTabPanel.putConstraint(SpringLayout.NORTH, goPanel, 0, SpringLayout.SOUTH, normalizationPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.WEST, goPanel, 0, SpringLayout.WEST, normalizationPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.SOUTH, goPanel, 70, SpringLayout.SOUTH, normalizationPanel);
        sl_menuTabPanel.putConstraint(SpringLayout.EAST, goPanel, 0, SpringLayout.EAST, normalizationPanel);
        menuTabPanel.add(goPanel);

        //======= Slider Tab Button =======
        ImageIcon sliderIcon = new ImageIcon(getClass().getResource("/images/sliders.png"));
        sliderLbl.setIcon(sliderIcon);
        sl_heatmapPanel.putConstraint(SpringLayout.NORTH, sliderLbl, -33, SpringLayout.SOUTH, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.WEST, sliderLbl, -28, SpringLayout.EAST, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.SOUTH, sliderLbl, -10, SpringLayout.SOUTH, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.EAST, sliderLbl, -5, SpringLayout.EAST, heatmapPanel);
        heatmapPanel.add(sliderLbl);
        sliderLbl.setVisible(false);

        sliderLbl.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //no-op
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //no-op
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //no-op
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sliderPanel.setVisible(true);
                sliderLbl.setVisible(false);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //no-op
            }
        });

        //=======Slider Panel=====
        sliderPanel = new JPanel();
        SpringLayout sl_sliderPanel = new SpringLayout();
        sliderPanel.setLayout(sl_sliderPanel);
        sliderPanel.setBackground(Color.WHITE);
        sliderPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        sliderPanel.setVisible(false);

        sl_heatmapPanel.putConstraint(SpringLayout.NORTH, sliderPanel, -70 * 2 - 30, SpringLayout.SOUTH, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.WEST, sliderPanel, -215, SpringLayout.EAST, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.SOUTH, sliderPanel, 0, SpringLayout.SOUTH, heatmapPanel);
        sl_heatmapPanel.putConstraint(SpringLayout.EAST, sliderPanel, 0, SpringLayout.EAST, heatmapPanel);
//        heatmapPanel.setLayer(sliderPanel, 1);
        heatmapPanel.add(sliderPanel);

        JideLabel sliderCloseLbl = new JideLabel();
        ImageIcon sliderCloseIcon = new ImageIcon(getClass().getResource("/images/close.png"));
        sliderCloseLbl.setIcon(sliderCloseIcon);
        sl_sliderPanel.putConstraint(SpringLayout.NORTH, sliderCloseLbl, -28, SpringLayout.SOUTH, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.WEST, sliderCloseLbl, 5, SpringLayout.WEST, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.SOUTH, sliderCloseLbl, -5, SpringLayout.SOUTH, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.EAST, sliderCloseLbl, 28, SpringLayout.WEST, sliderPanel);
        sliderPanel.add(sliderCloseLbl);

        sliderCloseLbl.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                sliderPanel.setVisible(false);
                sliderLbl.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        sl_sliderPanel.putConstraint(SpringLayout.NORTH, resolutionSlider, 0, SpringLayout.NORTH, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.WEST, resolutionSlider, 0, SpringLayout.WEST, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.SOUTH, resolutionSlider, 70, SpringLayout.NORTH, sliderPanel);
        sl_sliderPanel.putConstraint(SpringLayout.EAST, resolutionSlider, 0, SpringLayout.EAST, sliderPanel);
        sliderPanel.add(resolutionSlider);

        sl_sliderPanel.putConstraint(SpringLayout.NORTH, colorRangePanel, 0, SpringLayout.SOUTH, resolutionSlider);
        sl_sliderPanel.putConstraint(SpringLayout.WEST, colorRangePanel, 0, SpringLayout.WEST, resolutionSlider);
        sl_sliderPanel.putConstraint(SpringLayout.SOUTH, colorRangePanel, 70, SpringLayout.SOUTH, resolutionSlider);
        sl_sliderPanel.putConstraint(SpringLayout.EAST, colorRangePanel, 0, SpringLayout.EAST, resolutionSlider);
        sliderPanel.add(colorRangePanel);

        //TODO cleaner GUI for chrSelectionPanel/displayOptionPanel/normalizationPanel

        //==== Right Panel open/close tab ====
        final JFrame rightPnlFrame = new JFrame();
        rightPnlFrame.setSize(290, 700);
        rightPnlFrame.setLayout(new BorderLayout());
        rightPnlFrame.add(rightSidePanel, BorderLayout.CENTER);
        rightPnlFrame.setPreferredSize(rightSidePanel.getPreferredSize());
        rightPnlFrame.setMaximumSize(rightSidePanel.getMaximumSize());
        rightPnlFrame.setVisible(false);
        rightPnlFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rightPnlOpen = !rightPnlOpen;
//                btnRightPnl.setEnabled(true);
                btnRightPnl.setSelected(false);
            }
        });

//        rightPnlFrame.setResizable(false);
        btnRightPnl = new JideToggleButton();
        btnRightPnl.setBorderPainted(true);
        final ImageIcon openRightPnl = new ImageIcon(getClass().getResource("/images/pen.png"));
        btnRightPnl.setIcon(openRightPnl);
        btnRightPnl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rightPnlOpen = !rightPnlOpen;
                rightPnlFrame.setVisible(rightPnlOpen);
            }
        });

        sl_bigPanel.putConstraint(SpringLayout.NORTH, btnRightPnl, 10, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.WEST, btnRightPnl, -28, SpringLayout.EAST, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.SOUTH, btnRightPnl, 33, SpringLayout.NORTH, bigPanel);
        sl_bigPanel.putConstraint(SpringLayout.EAST, btnRightPnl, -5, SpringLayout.EAST, bigPanel);
        bigPanel.add(btnRightPnl);



        // compute preferred size
        Dimension preferredSize = new Dimension();
        for (int i = 0; i < rightSidePanel.getComponentCount(); i++) {
            Rectangle bounds = rightSidePanel.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        Insets insets = rightSidePanel.getInsets();
        preferredSize.width += insets.right + 20;
        preferredSize.height += insets.bottom;
        rightSidePanel.setMinimumSize(preferredSize);
        rightSidePanel.setPreferredSize(preferredSize);
        mainPanel.add(bigPanel, BorderLayout.CENTER);
//        mainPanel.add(rightSidePanel, BorderLayout.EAST);
//        rightSidePanel.setVisible(false);
    }

    public JPanel generate2DAnnotationsLayerSelectionPanel(final SuperAdapter superAdapter) {
        JPanel twoDAnnotationsLayerSelectionPanel = new JPanel();

        int i = 0;
        for (AnnotationLayerHandler handler : superAdapter.getAllLayers()) {
            try {
                JPanel panel = createLayerPanel(handler, superAdapter, twoDAnnotationsLayerSelectionPanel);
                //layerPanels.add(panel);
                twoDAnnotationsLayerSelectionPanel.add(panel, 0);
            } catch (IOException e) {
                System.err.println("Unable to generate layer panel " + (i - 1));
                //e.printStackTrace();
            }
        }
        return twoDAnnotationsLayerSelectionPanel;
    }

    public JPanel createLayerPanel(final AnnotationLayerHandler handler, final SuperAdapter superAdapter,
                                   final JPanel twoDAnnotationsLayerSelectionPanel) throws IOException {
        final JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new FlowLayout());

        /* layer name */
        final JTextField nameField = new JTextField(handler.getLayerName(), 10);
        nameField.getDocument().addDocumentListener(anyTextChangeListener(handler, nameField));
        nameField.setToolTipText("Change the name for this layer: " + nameField.getText());
        nameField.setMaximumSize(new Dimension(20, 20));
        handler.setNameTextField(nameField);

        /* Sets Active Layer */
        final JToggleButton writeButton = createToggleIconButton("/images/layer/pencil.png", "/images/layer/pencil_gray.png", handler.isActiveLayer(superAdapter));
        handler.setActiveLayerButton(writeButton);
        writeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                superAdapter.setActiveLayerHandler(handler);
                updateLayers2DPanel(superAdapter);
                superAdapter.repaint();
                updateMiniAnnotationsLayerPanel(superAdapter);
            }
        });
        writeButton.setToolTipText("Changes Active Layer");

        /* show/hide annotations for this layer */
        final JToggleButton toggleVisibleButton = createToggleIconButton("/images/layer/eye_clicked_green.png",
                "/images/layer/eye_clicked.png", handler.getLayerVisibility());
        toggleVisibleButton.setSelected(handler.getLayerVisibility());
        toggleVisibleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.setLayerVisibility(toggleVisibleButton.isSelected());
                updateLayers2DPanel(superAdapter);
                superAdapter.repaint();
            }
        });
        toggleVisibleButton.setToolTipText("Toggle visibility of this layer");

        JButton upButton = createIconButton("/images/layer/up.png");
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                twoDAnnotationsLayerSelectionPanel.remove(parentPanel);
                int index = superAdapter.moveUpIndex(handler);
                twoDAnnotationsLayerSelectionPanel.add(parentPanel, index);
                twoDAnnotationsLayerSelectionPanel.revalidate();
                twoDAnnotationsLayerSelectionPanel.repaint();
                updateLayers2DPanel(superAdapter);
                superAdapter.repaint();
            }
        });
        upButton.setToolTipText("Move this layer up (drawing order)");

        JButton downButton = createIconButton("/images/layer/down.png");
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                twoDAnnotationsLayerSelectionPanel.remove(parentPanel);
                int index = superAdapter.moveDownIndex(handler);
                twoDAnnotationsLayerSelectionPanel.add(parentPanel, index);
                twoDAnnotationsLayerSelectionPanel.revalidate();
                twoDAnnotationsLayerSelectionPanel.repaint();
                updateLayers2DPanel(superAdapter);
                superAdapter.repaint();
            }
        });
        downButton.setToolTipText("Move this layer down (drawing order)");

        parentPanel.add(nameField);
        Component[] allComponents = new Component[]{writeButton, toggleVisibleButton, upButton, downButton};
        for (Component component : allComponents) {
            if (component instanceof AbstractButton) {
                component.setMaximumSize(new Dimension(miniButtonSize, miniButtonSize));
            }
            parentPanel.add(component);
        }
        return parentPanel;
    }

    private DocumentListener anyTextChangeListener(final AnnotationLayerHandler handler,
                                                   final JTextField nameField) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handler.setLayerName(nameField.getText());
                nameField.setToolTipText("Change the name for this layer: " + nameField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handler.setLayerName(nameField.getText());
                nameField.setToolTipText("Change the name for this layer: " + nameField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handler.setLayerName(nameField.getText());
                nameField.setToolTipText("Change the name for this layer: " + nameField.getText());
            }
        };
    }

    private JToggleButton createToggleIconButton(String url1, String url2, boolean activatedStatus) throws IOException {

        // image when button is active/selected (is the darkest shade/color)
        //BufferedImage imageActive = ImageIO.read(getClass().getResource(url1));
        ImageIcon iconActive = new ImageIcon(ImageIO.read(getClass().getResource(url1)));

        // image when button is inactive/transitioning (lighter shade/color)
        ImageIcon iconTransitionDown = new ImageIcon(translucentImage(ImageIO.read(getClass().getResource(url2)), 0.6f));
        ImageIcon iconTransitionUp = new ImageIcon(translucentImage(ImageIO.read(getClass().getResource(url1)), 0.6f));
        ImageIcon iconInactive = new ImageIcon(translucentImage(ImageIO.read(getClass().getResource(url2)), 0.2f));
        ImageIcon iconDisabled = new ImageIcon(translucentImage(ImageIO.read(getClass().getResource(url2)), 0.1f));

        JToggleButton toggleButton = new JToggleButton(iconInactive);
        toggleButton.setRolloverIcon(iconTransitionDown);
        toggleButton.setPressedIcon(iconDisabled);
        toggleButton.setSelectedIcon(iconActive);
        toggleButton.setRolloverSelectedIcon(iconTransitionUp);
        toggleButton.setDisabledIcon(iconDisabled);
        toggleButton.setDisabledSelectedIcon(iconDisabled);

        toggleButton.setBorderPainted(false);
        toggleButton.setSelected(activatedStatus);
        toggleButton.setPreferredSize(new Dimension(miniButtonSize, miniButtonSize));

        return toggleButton;
    }

    private Image translucentImage(BufferedImage originalImage, float alpha) {

        int width = originalImage.getWidth(), height = originalImage.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return newImage;
    }

    private JButton createIconButton(String url) throws IOException {
        BufferedImage imageActive = ImageIO.read(getClass().getResource(url));
        ImageIcon iconActive = new ImageIcon(imageActive);

        // image when button is inactive/transitioning (lighter shade/color)
        ImageIcon iconTransition = new ImageIcon(translucentImage(imageActive, 0.6f));
        ImageIcon iconInactive = new ImageIcon(translucentImage(imageActive, 0.2f));

        JButton button = new JButton(iconActive);
        button.setRolloverIcon(iconTransition);
        button.setPressedIcon(iconInactive);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(miniButtonSize, miniButtonSize));
        return button;
    }

    public JPanel getAnnotationsLayerPanel() {
        return this.annotationsLayerPanel;
    }

    public void setAnnotationsLayerPanel(JPanel annotationsLayerPanel) {
        tooltipPanel.remove(this.annotationsLayerPanel);
        this.annotationsLayerPanel = annotationsLayerPanel;
        this.annotationsLayerPanel.setBackground(Color.gray);
        this.annotationsLayerPanel.setPreferredSize(new Dimension(210, 160));
        tooltipPanel.add(this.annotationsLayerPanel, BorderLayout.SOUTH);
    }

    public JPanel getHiCPanel() {
        return hiCPanel;
    }

    public void updateToolTipText(String s) {
        if (tooltipAllowedToUpdated)
            mouseHoverTextPanel.setText(s);
        mouseHoverTextPanel.setCaretPosition(0);
    }

    public boolean isResolutionLocked() {
        return resolutionSlider.isResolutionLocked();
    }

    public HeatmapPanel getHeatmapPanel() {
        return heatmapPanel;
    }

    public void updateZoom(HiCZoom newZoom) {
        resolutionSlider.setZoom(newZoom);
    }

    public void updateAndResetZoom(HiCZoom newZoom) {
        resolutionSlider.setZoom(newZoom);
        //resolutionSlider.reset();
    }

    /*
     * Only accessed from within another unsafe method in Heatmap Panel class,
     * which in turn is encapsulated (i.e. made safe)
     */
    public void unsafeSetSelectedChromosomes(SuperAdapter superAdapter, Chromosome xChrom, Chromosome yChrom) {
        chrBox1.setSelectedIndex(yChrom.getIndex());
        chrBox2.setSelectedIndex(xChrom.getIndex());
        unsafeRefreshChromosomes(superAdapter);
    }

    public void unsafeRefreshChromosomes(SuperAdapter superAdapter) {

        if (chrBox1.getSelectedIndex() == 0 || chrBox2.getSelectedIndex() == 0) {
            chrBox1.setSelectedIndex(0);
            chrBox2.setSelectedIndex(0);
            MatrixType matrixType = (MatrixType) displayOptionComboBox.getSelectedItem();
            if (MatrixType.isPearsonType(matrixType)) {
                // can't do pearson's genomewide
                displayOptionComboBox.setSelectedIndex(0);
                superAdapter.unsafeDisplayOptionComboBoxActionPerformed();
            }
        }

        Chromosome chr1 = (Chromosome) chrBox1.getSelectedItem();
        Chromosome chr2 = (Chromosome) chrBox2.getSelectedItem();

        Chromosome chrX = chr1.getIndex() < chr2.getIndex() ? chr1 : chr2;
        Chromosome chrY = chr1.getIndex() < chr2.getIndex() ? chr2 : chr1;

        superAdapter.unsafeUpdateHiCChromosomes(chrX, chrY);
        setNormalizationDisplayState(superAdapter.getHiC());

        updateThumbnail(superAdapter.getHiC());
    }

    public void setSelectedChromosomesNoRefresh(Chromosome xChrom, Chromosome yChrom, Context xContext, Context yContext) {
        chrBox1.setSelectedIndex(yChrom.getIndex());
        chrBox2.setSelectedIndex(xChrom.getIndex());
        rulerPanelX.setContext(xContext, HiCRulerPanel.Orientation.HORIZONTAL);
        rulerPanelY.setContext(yContext, HiCRulerPanel.Orientation.VERTICAL);
        chromosomePanelX.setContext(xContext, HiCChromosomeFigPanel.Orientation.HORIZONTAL);
        chromosomePanelY.setContext(yContext, HiCChromosomeFigPanel.Orientation.VERTICAL);
        resolutionSlider.setEnabled(!ChromosomeHandler.isAllByAll(xChrom));
    }

    /**
     * Chromosome "0" is whole genome
     *
     * @param handler for list of chromosomes
     */
    void setChromosomes(ChromosomeHandler handler) {
        heatmapPanel.setChromosomeBoundaries(handler.getChromosomeBoundaries());
        chrBox1.setModel(new DefaultComboBoxModel<>(handler.getChromosomeArray()));
        chrBox2.setModel(new DefaultComboBoxModel<>(handler.getChromosomeArray()));
    }

    private boolean isInterChromosomal() {
        Chromosome chr1 = (Chromosome) chrBox1.getSelectedItem();
        Chromosome chr2 = (Chromosome) chrBox2.getSelectedItem();
        return chr1.getIndex() != chr2.getIndex();
    }

    /**
     * Note that both versions of isWholeGenome are needed otherwise we get
     * a bug when partial states have changed
     */
    private boolean isWholeGenome() {
        Chromosome chr1 = (Chromosome) chrBox1.getSelectedItem();
        Chromosome chr2 = (Chromosome) chrBox2.getSelectedItem();
        return ChromosomeHandler.isAllByAll(chr1) || ChromosomeHandler.isAllByAll(chr2);
    }

    private boolean isWholeGenome(HiC hic) {
        Chromosome chr1 = hic.getXContext().getChromosome();
        Chromosome chr2 = hic.getYContext().getChromosome();
        return ChromosomeHandler.isAllByAll(chr1) || ChromosomeHandler.isAllByAll(chr2);
    }

    public void setNormalizationDisplayState(HiC hic) {

        // Test for new dataset ("All"),  or change in chromosome
        if (isWholeGenome()) { // for now only allow observed
            hic.setDisplayOption(MatrixType.OBSERVED);
            displayOptionComboBox.setSelectedIndex(0);
            normalizationComboBox.setSelectedIndex(0);
        } else if (isInterChromosomal()) {
            if (MatrixType.isOnlyIntrachromosomalType(hic.getDisplayOption())) {
                hic.setDisplayOption(MatrixType.OBSERVED);
                displayOptionComboBox.setSelectedIndex(0);
            }
        }

        normalizationComboBox.setEnabled(!isWholeGenome(hic));
        displayOptionComboBox.setEnabled(true);
    }

    public void repaintTrackPanels() {
        trackPanelX.repaint();
        trackPanelY.repaint();
    }

    public void repaintGridRulerPanels() {
        rulerPanelX.repaint();
        rulerPanelY.repaint();
    }

    public String getTrackPanelPrintouts(int x, int y) {
        String trackToolTip = "";
        try {
            String text = trackPanelX.tooltipText(x, y, false);
            if (text != null) trackToolTip += "<span style='color:" + HiCGlobals.topChromosomeColor +
                    "; font-family: arial; font-size: 12pt; '>" + text + "</span>";
            text = trackPanelY.tooltipText(x, y, false);
            if (text != null) trackToolTip += "<span style='color:" + HiCGlobals.leftChromosomeColor +
                    "; font-family: arial; font-size: 12pt; '>" + text + "</span>";
        } catch (Exception e) {
        }
        return trackToolTip;
    }

    public void updateThumbnail(HiC hic) {
        if (ignoreUpdateThumbnail) return;
        //new Exception().printStackTrace();

        if (hic.getMatrix() != null) {

            //   MatrixZoomData zd0 = initialZoom == null ? hic.getMatrix().getFirstZoomData(hic.getZoom().getUnit()) :
            //           hic.getMatrix().getZoomData(initialZoom);
            MatrixZoomData zd0 = hic.getMatrix().getFirstZoomData(hic.getZoom().getUnit());
            MatrixZoomData zdControl = null;
            if (hic.getControlMatrix() != null)
                zdControl = hic.getControlMatrix().getFirstZoomData(hic.getZoom().getUnit());
            Image thumbnail = heatmapPanel.getThumbnailImage(zd0, zdControl,
                    thumbnailPanel.getWidth(), thumbnailPanel.getHeight(),
                    hic.getDisplayOption(), hic.getNormalizationType());
            if (thumbnail != null) {
                thumbnailPanel.setImage(thumbnail);
                thumbnailPanel.repaint();
            }
        } else {
            thumbnailPanel.setImage(null);
        }
    }

    private void chrBox1ActionPerformed(ActionEvent e) {
        if (chrBox1.getSelectedIndex() == 0) {
            chrBox2.setSelectedIndex(0);
        }
    }

    private void chrBox2ActionPerformed(ActionEvent e) {
        if (chrBox2.getSelectedIndex() == 0) {
            chrBox1.setSelectedIndex(0);
        }
    }

    public boolean setResolutionSliderVisible(boolean state, SuperAdapter superAdapter) {

        // Test for new dataset ("All"),  or change in chromosome
        boolean makeResVisible = state && !isWholeGenome();

        resolutionSlider.setEnabled(makeResVisible);
        if (makeResVisible) {
            resolutionSlider.setForeground(Color.BLUE);
        } else {
            resolutionSlider.setForeground(Color.BLACK);
        }
        return true;
        // why are we calling this?  why is this a boolean method?
        //return superAdapter.safeDisplayOptionComboBoxActionPerformed();
    }

    public void updateTrackPanel(boolean hasTracks) {

        trackLabelPanel.updateLabels();

        if (hasTracks) {
            if (!trackPanelX.isVisible()) {
                trackPanelX.setVisible(true);
                trackLabelPanel.setVisible(true);
            }
            if (!trackPanelY.isVisible()) {
                trackPanelY.setVisible(true);
            }
        } else {
            if (trackPanelX.isVisible()) {
                trackPanelX.setVisible(false);
                trackLabelPanel.setVisible(false);
            }
            if (trackPanelY.isVisible()) {
                trackPanelY.setVisible(false);
            }
        }

        trackPanelX.invalidate();
        trackLabelPanel.invalidate();
        trackPanelY.invalidate();
    }

    public void setShowChromosomeFig(boolean showFigure) {

        if (showFigure) {
            if (!bottomChromosomeFigPanel.isVisible()) {
                bottomChromosomeFigPanel.setVisible(true);
            }
            if (!chromosomePanelY.isVisible()) {
                chromosomePanelY.setVisible(true);
            }
            if (!chrSidePanel.isVisible()) {
                chrSidePanel.setVisible(true);
            }
            if (!chrSidePanel3.isVisible()) {
                chrSidePanel3.setVisible(true);
            }
        } else {
            if (bottomChromosomeFigPanel.isVisible()) {
                bottomChromosomeFigPanel.setVisible(false);
            }
            if (chromosomePanelY.isVisible()) {
                chromosomePanelY.setVisible(false);
            }
            if (chrSidePanel.isVisible()) {
                chrSidePanel.setVisible(false);
            }
            if (chrSidePanel3.isVisible()) {
                chrSidePanel3.setVisible(false);
            }
        }
        HiCRulerPanel.setShowChromosomeFigure(showFigure);
        chromosomePanelY.invalidate();
        bottomChromosomeFigPanel.invalidate();
        chrSidePanel.invalidate();
        chrSidePanel3.invalidate();
    }

    public boolean getShowGridLines() {
        if (heatmapPanel != null) {
            return heatmapPanel.getShowGridLines();
        }
        return true; // when starting from scratch, the gridlines option is set to true
    }

    public void setShowGridLines(boolean status) {
        if (heatmapPanel != null) {
            heatmapPanel.setShowGridLines(status);
        }
    }

    public String getToolTip() {
        return mouseHoverTextPanel.getText();
    }

    public void setDisplayBox(int indx) {
        displayOptionComboBox.setSelectedIndex(indx);
    }

    public void setNormalizationBox(int indx) {
        normalizationComboBox.setSelectedIndex(indx);
    }

    public void setNormalizationEnabledForReload() {
        //normalizationComboBox.setEnabled(true);
        normalizationComboBox.setEnabled(!isWholeGenome());
    }

    public void setPositionChrLeft(String newPositionDate) {
        goPanel.setPositionChrLeft(newPositionDate);
    }

    public void setPositionChrTop(String newPositionDate) {
        goPanel.setPositionChrTop(newPositionDate);
    }

    public void setEnableForAllElements(SuperAdapter superAdapter, boolean status) {
        chrBox1.setEnabled(status);
        chrBox2.setEnabled(status);
        refreshButton.setEnabled(status);
        colorRangePanel.setElementsVisible(status, superAdapter);
        if (setResolutionSliderVisible(status, superAdapter)) {
            // TODO succeeded
        } else {
            // TODO failed
        }
        goPanel.setEnabled(status);
        annotationsPanelToggleButton.setEnabled(status);
    }

    public String getColorRangeValues() {
        return colorRangePanel.getColorRangeValues();
    }

    public double getColorRangeScaleFactor() {
        return colorRangePanel.getColorRangeScaleFactor();
    }

    public void updateRatioColorSlider(HiC hic, double maxColor, double upColor) {
        colorRangePanel.updateRatioColorSlider(hic, maxColor, upColor);
    }

    public void updateColorSlider(HiC hic, double minColor, double lowColor, double upColor, double maxColor) {
        colorRangePanel.updateColorSlider(hic, minColor, lowColor, upColor, maxColor);
    }

    public void updateColorSlider(HiC hic, double minColor, double lowColor, double upColor, double maxColor, double scalefactor) {
        colorRangePanel.updateColorSlider(hic, minColor, lowColor, upColor, maxColor);//scalefactor);
    }

    public void setEnabledForNormalization(String[] normalizationOptions, boolean status) {
        if (normalizationOptions.length == 1) {
            normalizationComboBox.setEnabled(false);
        } else {
            normalizationComboBox.setModel(new DefaultComboBoxModel<>(normalizationOptions));
            normalizationComboBox.setSelectedIndex(0);
            normalizationComboBox.setEnabled(status && !isWholeGenome());
        }
    }

    public JComboBox<MatrixType> getDisplayOptionComboBox() {
        return displayOptionComboBox;
    }

    public void resetResolutionSlider() {
        resolutionSlider.unit = HiC.Unit.BP;
        resolutionSlider.reset();
    }

    public void setSelectedDisplayOption(MatrixType[] options, boolean control) {
        if (control) {
            MatrixType originalMatrixType = (MatrixType) displayOptionComboBox.getSelectedItem();
            displayOptionComboBox.setModel(new DefaultComboBoxModel<>(options));
            int indx = 0;
            for (int i = 0; i < displayOptionComboBox.getItemCount(); i++) {
                if (originalMatrixType.equals(displayOptionComboBox.getItemAt(i))) {
                    indx = i;
                    break;
                }
            }
            displayOptionComboBox.setSelectedIndex(indx);
        } else {
            displayOptionComboBox.setModel(new DefaultComboBoxModel<>(options));
            displayOptionComboBox.setSelectedIndex(0);
        }
    }

    public JEditorPane getMouseHoverTextPanel() {
        return mouseHoverTextPanel;
    }

    public ResolutionControl getResolutionSlider() {
        return resolutionSlider;
    }

    public JColorRangePanel getColorRangePanel() {
        return colorRangePanel;
    }

    public boolean isTooltipAllowedToUpdated() {
        return tooltipAllowedToUpdated;
    }

    public void toggleToolTipUpdates(boolean tooltipAllowedToUpdated) {
        this.tooltipAllowedToUpdated = tooltipAllowedToUpdated;
    }

    public JComboBox<String> getNormalizationComboBox() {
        return normalizationComboBox;
    }

    public HiCRulerPanel getRulerPanelY() {
        return rulerPanelY;
    }

    public HiCRulerPanel getRulerPanelX() {
        return rulerPanelX;
    }

    public HiCChromosomeFigPanel getChromosomeFigPanelY() {
        return chromosomePanelY;
    }

    public HiCChromosomeFigPanel getChromosomeFigPanelX() {
        return chromosomePanelX;
    }

    public void setAnnotationsPanelToggleButtonSelected(boolean status) {
        annotationsPanelToggleButton.setSelected(status);
    }

    private void updateLayers2DPanel(SuperAdapter superAdapter) {
        superAdapter.getLayersPanel().updateLayers2DPanel(superAdapter);
    }

    public void updateMiniAnnotationsLayerPanel(SuperAdapter superAdapter) {
        setAnnotationsLayerPanel(generate2DAnnotationsLayerSelectionPanel(superAdapter));
        annotationsLayerPanel.revalidate();
        annotationsLayerPanel.repaint();
    }

    public void showSliders() {
        sliderLbl.setVisible(true);
    }

    /*public boolean isPearsonDisplayed() {
        return displayOptionComboBox.getSelectedItem() == MatrixType.PEARSON;
    }*/
}
