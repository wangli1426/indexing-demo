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
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class Demo extends ApplicationFrame implements ActionListener {

    /** The time series data. */
    private TimeSeries ourSeries;

    private TimeSeries HBaseSeries;

    /** The most recent value added. */
    private double outLastValue = 800000;

    private double HBaseLastValue = 160000;

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public Demo(final String title) {

        super(title);
        this.ourSeries = new TimeSeries("DITIR", Millisecond.class);
        this.HBaseSeries = new TimeSeries("HBase", Millisecond.class);
        final TimeSeriesCollection ourDataset = new TimeSeriesCollection(this.ourSeries);
        final JFreeChart chart = createChart(ourDataset);
        chart.getXYPlot().setDataset(1, new TimeSeriesCollection(HBaseSeries));
        chart.getXYPlot().setRenderer(1, new StandardXYItemRenderer());

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumSize(new Dimension(30,30));
        chartPanel.setSize(new Dimension(30,30));
//        final JButton button = new JButton("Add New Data Item");
//        button.setActionCommand("ADD_DATA");
//        button.addActionListener(this);

        final JPanel content = new JPanel(new BorderLayout());
        content.add(chartPanel, BorderLayout.WEST);
        content.setSize(new Dimension(1000,1000));
        content.add(new ChartPanel(chart), BorderLayout.EAST);

//        content.add(button, BorderLayout.SOUTH);
//        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 270));
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
        axis.setFixedAutoRange(60000.0);  // 60 seconds
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
            this.outLastValue = this.outLastValue + 50000 * (Math.random() - 0.5);
            this.HBaseLastValue = this.HBaseLastValue + 30000 * (Math.random() - 0.5);


            final Millisecond now = new Millisecond();
            this.ourSeries.add(now, this.outLastValue);
            this.HBaseSeries.add(now, this.HBaseLastValue);
        }
    }

    public void startDataGenerator() {
        while(true) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.outLastValue = this.outLastValue + 50000 * (Math.random() - 0.5);
            this.HBaseLastValue = this.HBaseLastValue + 30000 * (Math.random() - 0.5);


            final Millisecond now = new Millisecond();
            this.ourSeries.add(now, this.outLastValue);
            this.HBaseSeries.add(now, this.HBaseLastValue);
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
        demo.startDataGenerator();
    }

}
