package sg.com.adsc;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * --------------------
 * Demo.java
 * --------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: Demo.java,v 1.12 2004/05/07 16:09:03 mungady Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 *
 */

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class Demo extends ApplicationFrame implements ActionListener {


    static Color backgroundColor = Color.WHITE;

    static Dimension plotDimension = new Dimension(300, 300);

    Double inputRate = 100000.0;
    Boolean dynamicBalancing = false;

    Double selectivity = 0.05;

    Double dispatchBenefit = 1.0;


    public class ThroughputPlot {

        /** The time series data. */
        private TimeSeries ourSeries;

        private TimeSeries HBaseSeries;

        /** The most recent value added. */
        private Double ourValue = 800000.0;

        private Double HBaseValue = 160000.0;

        public ChartPanel getChart() {
            this.ourSeries = new TimeSeries("DITIR", Millisecond.class);
            this.HBaseSeries = new TimeSeries("HBase", Millisecond.class);
            final TimeSeriesCollection ourDataset = new TimeSeriesCollection(this.ourSeries);

            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                    "Insertion Throughput Comparison",
                    "",
                    "Time",
                    "Throughput\n(tuples / second)",
                    ourDataset,
                    true,
                    true,
                    false
            );

            chart.getLegend().setWidth(40);
            chart.getLegend().setPosition(RectangleEdge.TOP);
            chart.getLegend().setHorizontalAlignment(HorizontalAlignment.RIGHT);

            final XYPlot plot = chart.getXYPlot();
            ValueAxis axis = plot.getDomainAxis();
            axis.setAutoRange(true);
            axis.setFixedAutoRange(60000.0);  // 60 seconds
            axis = plot.getRangeAxis();
            axis.setRange(0.0, 1000000);





            chart.getXYPlot().setDataset(1, new TimeSeriesCollection(HBaseSeries));
            chart.getXYPlot().setRenderer(1, new StandardXYItemRenderer());
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(plotDimension);
//            chartPanel.setMaximumSize(new Dimension(30,30));
//            chartPanel.setSize(new Dimension(30,30));
            startDataGenerator();
            chart.getTitle().setPosition(RectangleEdge.TOP);
            chart.getTitle().setFont(new Font("SansSerif", java.awt.Font.BOLD, 16));


            // set line size
            int seriesCount = plot.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
            }

            return chartPanel;
        }

        public void startDataGenerator() {

            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        double performanceFactor = 0.7;
                        if (dynamicBalancing)
                            performanceFactor += 0.24;

                        final double value = inputRate * performanceFactor * (1 + (Math.random() - 0.5) * 0.1);
                        final double value1 = Math.min(170000, inputRate) * 0.68 *  (1 + (Math.random() - 0.5) * 0.3);
//                        ourValue = ourValue + 50000 * (Math.random() - 0.5);
//                        HBaseValue = HBaseValue + 30000 * (Math.random() - 0.5);


                        final Millisecond now = new Millisecond();
                        ourSeries.add(now, value);
                        HBaseSeries.add(now, value1);
                    }
                }
            }).start();


        }



    }

     public class ResponseTimePlot {

        /** The time series data. */
        private TimeSeries ourSeries;

        private TimeSeries HBaseSeries;

        /** The most recent value added. */
        private Double outLastValue = 245.0;

        private Double HBaseLastValue = 1200.0;

        private int numberOfHistoricalRecords = 10;

        public ChartPanel getHistogramChart() {
            final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//            dataset.addValue(300.0, "HBase", "C1");
//            dataset.addValue(500.0, "DITIR", "C1");
            for (Integer i = 0; i < numberOfHistoricalRecords; i++) {
                dataset.addValue(0.0, "HBase", i);
                dataset.addValue(0.0, "DITIR", i);
            }

            final JFreeChart chart = ChartFactory.createBarChart(
//                    "Query Latency Comparison",       // chart title
                    "",
                    "Query ID",               // domain axis label
                    "Query Latency (ms)",                  // range axis label
                    dataset,                  // data
                    PlotOrientation.VERTICAL, // the plot orientation
                    true,                    // include legend
                    true,
                    false
            );

            chart.setBackgroundPaint(Color.WHITE);

            final CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            final ValueAxis rangeAxis = plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            plot.getDomainAxis().setVisible(true);

//            rangeAxis.setLowerMargin(0.15);
//            rangeAxis.setUpperMargin(0.15);

//            final ChartPanel chartPanel = new ChartPanel(chart);
//            chartPanel.setPreferredSize(plotDimension);
//            chartPanel.setMaximumSize(new Dimension(30,30));
//            chartPanel.setSize(new Dimension(30,30));
//            startDataGenerator();
//
//            chart.getTitle().setPosition(RectangleEdge.TOP);
//            chart.getTitle().setFont(new Font("SansSerif", java.awt.Font.BOLD, 16));


            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setDrawBarOutline(false);
            renderer.setGradientPaintTransformer(null);
            renderer.setMaximumBarWidth(0.15);
            renderer.setItemMargin(0.1);

            final GradientPaint gp0 = new GradientPaint(
                    0.0f, 0.0f, Color.blue,
                    0.0f, 0.0f, Color.lightGray
            );
            final GradientPaint gp1 = new GradientPaint(
                    0.0f, 0.0f, Color.green,
                    0.0f, 0.0f, Color.lightGray
            );
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.RED);
            final CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );

            plot.getRangeAxis().setRange(0, 4000.0);

            chart.getLegend().setWidth(40);
            chart.getLegend().setPosition(RectangleEdge.TOP);
            chart.getLegend().setHorizontalAlignment(HorizontalAlignment.RIGHT);


            final ChartPanel chartPanel = new ChartPanel(chart);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Integer queryId = 0;
                    while(true) {
                        final double responseTimeOurs = (200 + 80 * (selectivity / 0.05)) * (1 + (Math.random() - 0.5) * 0.05) * dispatchBenefit;
                        final double responseTimeHBase = (300 + 633 * (selectivity / 0.05)) * (1 + (Math.random() - 0.5) * 0.05);
                        dataset.setValue(responseTimeOurs, "DITIR", queryId);
                        dataset.setValue(responseTimeHBase, "HBase", queryId);

                        dataset.removeValue("DITIR", queryId - numberOfHistoricalRecords);
                        dataset.removeValue("HBase", queryId - numberOfHistoricalRecords);


                        queryId ++;

                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(true) {
//                        try {
//
//                            final double responseTimeOurs = (300 + 200 * (selectivity / 0.05)) * (1 + (Math.random() - 0.5) * 0.05 ) * dispatchBenefit;
//                            Thread.sleep((long)responseTimeOurs);
//
//                            for (Integer i = 0; i < numberOfHistoricalRecords - 1; i++) {
//                                double newValue = dataset.getValue("DITIR", i + 1).doubleValue();
//                                dataset.setValue(newValue, "DITIR", i);
//                            }
//
//                            dataset.setValue(responseTimeOurs, "DITIR", (Integer)(numberOfHistoricalRecords - 1));
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(true) {
//                        try {
//                            Thread.sleep(1000);
//                            final double responseTimeHBase = (500 + 800 * (selectivity / 0.05)) * (1 + (Math.random() - 0.5) * 0.05);
//                            Thread.sleep((long)responseTimeHBase);
//
//                            for (Integer i = 0; i < numberOfHistoricalRecords - 1; i++) {
//                                double newValue = dataset.getValue("HBase", i + 1).doubleValue();
//                                dataset.setValue(newValue, "HBase", i);
//                            }
//
//
//                            dataset.setValue(responseTimeHBase, "HBase", (Integer)(numberOfHistoricalRecords - 1));
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();

            return chartPanel;
        }

        public ChartPanel getChart() {
            this.ourSeries = new TimeSeries("DITIR", Millisecond.class);
            this.HBaseSeries = new TimeSeries("HBase", Millisecond.class);
            final TimeSeriesCollection ourDataset = new TimeSeriesCollection(this.ourSeries);

            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                    "Query Latency Comparison",
                    "",
                    "Time",
                    "Latency\n(ms)",
                    ourDataset,
                    true,
                    true,
                    false
            );

            chart.getLegend().setWidth(40);
            chart.getLegend().setPosition(RectangleEdge.TOP);
            chart.getLegend().setHorizontalAlignment(HorizontalAlignment.RIGHT);
            final XYPlot plot = chart.getXYPlot();
            ValueAxis axis = plot.getDomainAxis();
            axis.setAutoRange(true);
            axis.setFixedAutoRange(30000.0);  // 60 seconds
            axis = plot.getRangeAxis();
            axis.setRange(0.0, 2000);


            chart.getXYPlot().setDataset(1, new TimeSeriesCollection(HBaseSeries));
            chart.getXYPlot().setRenderer(1, new StandardXYItemRenderer());
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(plotDimension);
//            chartPanel.setMaximumSize(new Dimension(30,30));
//            chartPanel.setSize(new Dimension(30,30));
            startDataGenerator();
            chart.getTitle().setPosition(RectangleEdge.TOP);
            chart.getTitle().setFont(new Font("SansSerif", java.awt.Font.BOLD, 16));

            return chartPanel;
        }

        public void startDataGenerator() {

            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        outLastValue = outLastValue + 200 * (Math.random() - 0.5);
                        HBaseLastValue = HBaseLastValue + 400 * (Math.random() - 0.5);


                        final Millisecond now = new Millisecond();
                        ourSeries.add(now, outLastValue);
                        HBaseSeries.add(now, HBaseLastValue);
                    }
                }
            }).start();


        }



    }

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public Demo(final String title) {

        super(title);

        final JPanel insertPanel = new JPanel(new GridBagLayout());
        final JPanel queryPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.8;

        insertPanel.add(new ThroughputPlot().getChart(), constraints);



        constraints.gridx = 1;
        constraints.gridy = 1;
//        queryPanel.add(new ResponseTimePlot().getChart(), constraints);
        queryPanel.add(new ResponseTimePlot().getHistogramChart(), constraints);

        queryPanel.setBackground(backgroundColor);
//        content.setSize(new Dimension(1000,1000));

// LEFT ///////

        final JPanel leftConctrolJPanel = new JPanel(new GridBagLayout());
        leftConctrolJPanel.setBackground(backgroundColor);

        final JLabel insertionThroughputHeader = new JLabel();
        insertionThroughputHeader.setText("Input Rate (Tuples/s):");
        insertionThroughputHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0;
//        constraints.weighty = 0.4;
        leftConctrolJPanel.add(insertionThroughputHeader, constraints);


        final JSpinner throughputSpinner = new JSpinner(new SpinnerNumberModel(500000,50000,1000000,50000));
        throughputSpinner.setValue(100000.0);
        throughputSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        inputRate = (double)(int)throughputSpinner.getValue();
                    }
                }).start();

            }
        });
//        ((JSpinner.DateEditor)throughputSpinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
//        ((JSpinner.DateEditor)throughputSpinner.getEditor()).getTextField().setEditable(false);

//        throughputSpinner.setSize(30,20);
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weighty = 0.0;
        constraints.weightx = 0.00;
        throughputSpinner.setPreferredSize(new Dimension(120,30));
        leftConctrolJPanel.add(throughputSpinner, constraints);


//        final JLabel insertionThroughputTail = new JLabel();
//        insertionThroughputTail.setText("    ");
//        constraints.gridx = 2;
//        constraints.gridy = 0;
//        constraints.weighty = 0.4;
//        leftConctrolJPanel.add(insertionThroughputTail, constraints);



        final JLabel loadBalanceCheckBotText = new JLabel();
        loadBalanceCheckBotText.setText("Dynamic Key Partitioning");
        loadBalanceCheckBotText.setHorizontalAlignment(SwingConstants.RIGHT);
        constraints.gridx = 1;
        constraints.gridy = 1;
        leftConctrolJPanel.add(loadBalanceCheckBotText, constraints);


        final JSpinner loadBalanceCheckBox = new JSpinner(new SpinnerListModel(new String[]{"Disabled", "Enabled"}));
        loadBalanceCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        if (loadBalanceCheckBox.getValue().equals("Enabled"))
                            dynamicBalancing = true;
                        else {
                            dynamicBalancing = false;
                        }
                    }
                }).start();

            }
        });
        loadBalanceCheckBox.setBackground(backgroundColor);
        loadBalanceCheckBox.setPreferredSize(new Dimension(120, 30));
        ((JSpinner.DefaultEditor)loadBalanceCheckBox.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        ((JSpinner.DefaultEditor)loadBalanceCheckBox.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)loadBalanceCheckBox.getEditor()).setBackground(backgroundColor);
        constraints.gridx = 2;
        constraints.gridy = 1;
//        constraints.gridwidth = 2;
        leftConctrolJPanel.add(loadBalanceCheckBox, constraints);


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        insertPanel.add(leftConctrolJPanel, constraints);


        // RIGHT control pannel

        final JPanel rightConctrolJPanel = new JPanel(new GridBagLayout());
        rightConctrolJPanel.setBackground(backgroundColor);

        String[] polices = {"Shuffle", "Hashing", "LATQM"};
        final JSpinner dispatcherPolicySpinner = new JSpinner(new SpinnerListModel(polices));
        dispatcherPolicySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (dispatcherPolicySpinner.getValue().equals("Shuffle"))
                    dispatchBenefit = 1.0;
                else if (dispatcherPolicySpinner.getValue().equals("Hashing"))
                    dispatchBenefit = 0.87;
                else
                    dispatchBenefit = 0.74;
            }
        });
        ((JSpinner.DefaultEditor) dispatcherPolicySpinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        ((JSpinner.DefaultEditor) dispatcherPolicySpinner.getEditor()).getTextField().setEditable(false);
        dispatcherPolicySpinner.setPreferredSize(new Dimension(120, 30));
        dispatcherPolicySpinner.setAlignmentX(SwingConstants.RIGHT);


        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        rightConctrolJPanel.add(dispatcherPolicySpinner, constraints);

        JLabel policyLabel = new JLabel();
        policyLabel.setText("Dispatch Policy:");
        constraints.gridx = 2;
        constraints.gridy = 0;
        policyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightConctrolJPanel.add(policyLabel, constraints);

        JLabel selectivityLabel = new JLabel();
        selectivityLabel.setText("Selectivity:");
        constraints.gridx = 2;
        constraints.gridy = 1;
        selectivityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightConctrolJPanel.add(selectivityLabel, constraints);

        final JSpinner selectivitySpinner = new JSpinner(new SpinnerNumberModel(0.1, 0.05, 0.5, 0.05));
        selectivitySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                selectivity = (double)selectivitySpinner.getValue();
            }
        });
        ((JSpinner.DefaultEditor) selectivitySpinner.getEditor()).getTextField().setEditable(false);
        selectivitySpinner.setPreferredSize(new Dimension(120, 30));
        selectivitySpinner.setAlignmentX(SwingConstants.RIGHT);
        constraints.gridx = 3;
        constraints.gridy = 1;
        rightConctrolJPanel.add(selectivitySpinner, constraints);


        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 0.1;
        queryPanel.add(rightConctrolJPanel, constraints);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Data Insertion", insertPanel);
        tabbedPane.addTab("Query Evaluation", queryPanel);
        tabbedPane.setBackground(backgroundColor);

//        setContentPane(content);
//        setContentPane(rightConctrolJPanel);
        setContentPane(tabbedPane);
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
//                "Insertion Throughput Comparison",
                "",
                "Time",
                "Throughput\n(tuples / second)",
                dataset,
                true,
                true,
                false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(30000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 1000000);
        result.getTitle().setPosition(RectangleEdge.BOTTOM);
        return result;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Handles a click on the button by adding new (random) data.
     *
     * @param e  the action event.
     */
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("ADD_DATA")) {
//            this.ourValue = this.ourValue + 50000 * (Math.random() - 0.5);
//            this.HBaseValue = this.HBaseValue + 30000 * (Math.random() - 0.5);
//
//
//            final Millisecond now = new Millisecond();
//            this.ourSeries.add(now, this.ourValue);
//            this.HBaseSeries.add(now, this.HBaseValue);
        }
    }



    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final Demo demo = new Demo("DITIR demonstration");
//        demo.setResizable(false);
        demo.pack();
        demo.setMinimumSize(demo.getPreferredSize());
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
//        demo.startDataGenerator();
    }

}
