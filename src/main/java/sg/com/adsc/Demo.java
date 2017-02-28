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

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.ui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
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


    public class ThroughputPlot {

        /** The time series data. */
        private TimeSeries ourSeries;

        private TimeSeries HBaseSeries;

        /** The most recent value added. */
        private Double outLastValue = 800000.0;

        private Double HBaseLastValue = 160000.0;

        public ChartPanel getChart() {
            this.ourSeries = new TimeSeries("DITIR", Millisecond.class);
            this.HBaseSeries = new TimeSeries("HBase", Millisecond.class);
            final TimeSeriesCollection ourDataset = new TimeSeriesCollection(this.ourSeries);

            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Insertion Throughput Comparison",
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
//            chartPanel.setMaximumSize(new Dimension(30,30));
//            chartPanel.setSize(new Dimension(30,30));
            startDataGenerator();
            return chartPanel;
        }

        public void startDataGenerator() {

            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        outLastValue = outLastValue + 50000 * (Math.random() - 0.5);
                        HBaseLastValue = HBaseLastValue + 30000 * (Math.random() - 0.5);


                        final Millisecond now = new Millisecond();
                        ourSeries.add(now, outLastValue);
                        HBaseSeries.add(now, HBaseLastValue);
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

        public ChartPanel getChart() {
            this.ourSeries = new TimeSeries("DITIR", Millisecond.class);
            this.HBaseSeries = new TimeSeries("HBase", Millisecond.class);
            final TimeSeriesCollection ourDataset = new TimeSeriesCollection(this.ourSeries);

            final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Query Latency Comparison",
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
//            chartPanel.setMaximumSize(new Dimension(30,30));
//            chartPanel.setSize(new Dimension(30,30));
            startDataGenerator();
            return chartPanel;
        }

        public void startDataGenerator() {

            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(500);
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




        final JPanel content = new JPanel(new BorderLayout());
        content.add(new ThroughputPlot().getChart(), BorderLayout.WEST);

        final JSlider throughputSlider = new JSlider(100000,800000);
        throughputSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                 throughputSlider.getValue();
            }
        });
        throughputSlider.setSize(100,20);
        content.add(throughputSlider, BorderLayout.NORTH);


        final JSpinner throughputSpinner = new JSpinner(new SpinnerNumberModel(500000,50000,1000000,50000));
        throughputSpinner.setSize(100,20);
        content.add(throughputSpinner, BorderLayout.NORTH);

        content.add(new ResponseTimePlot().getChart(), BorderLayout.EAST);
        content.setBackground(Color.WHITE);
//        content.setSize(new Dimension(1000,1000));


        setContentPane(content);

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
                "Insertion Throughput Comparison",
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
//            this.outLastValue = this.outLastValue + 50000 * (Math.random() - 0.5);
//            this.HBaseLastValue = this.HBaseLastValue + 30000 * (Math.random() - 0.5);
//
//
//            final Millisecond now = new Millisecond();
//            this.ourSeries.add(now, this.outLastValue);
//            this.HBaseSeries.add(now, this.HBaseLastValue);
        }
    }



    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final Demo demo = new Demo("Dynamic Data Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
//        demo.startDataGenerator();
    }

}
