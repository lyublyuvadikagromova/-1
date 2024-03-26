package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.ui.RectangleInsets;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

class JFreeChartMainFrame extends JFrame {
    private final TreeMap<Double, Point> PointTreeMap = new TreeMap<>();
    private final ChartPanel chartPanel;
    private String userFunction;
    private double a;
    private double start;
    private double stop;
    private double step;
    private XYSeriesCollection dataset;
    private JFreeChart chart;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JFreeChartMainFrame frame = new JFreeChartMainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private final JTextField textFieldA;
    private final JTextField textFieldStart;
    private final JTextField textFieldStop;
    private final JTextField textFieldStep;
    private final JTextField textFieldFunction;

    /**
     * Create the frame.
     */
    public JFreeChartMainFrame() {
        setResizable(false);
        setTitle("fFreeChart Test Plot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panelButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panelButtons.getLayout();
        flowLayout.setHgap(15);
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        JButton btnNewButtonPlot = new JButton("Plot");
        btnNewButtonPlot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chartPanel.setChart(createChart());
            }
        });
        panelButtons.add(btnNewButtonPlot);

        JButton btnNewButtonWriteInFile = new JButton("Write in file");
        btnNewButtonWriteInFile.addActionListener(e -> writeInFile());
        panelButtons.add(btnNewButtonWriteInFile);

        JButton btnNewButtonOpenFile = new JButton("Open");
        btnNewButtonOpenFile.addActionListener(e -> readFromFile());
        panelButtons.add(btnNewButtonOpenFile);

        JButton btnNewButtonExit = new JButton("Exit");
        btnNewButtonExit.addActionListener(e -> System.exit(0));
        panelButtons.add(btnNewButtonExit);

        JPanel panelData = new JPanel();
        contentPane.add(panelData, BorderLayout.NORTH);

        JLabel parametrLabel = new JLabel("Parametr:");
        textFieldA = new JTextField("1.0");
        panelData.add(parametrLabel);
        panelData.add(textFieldA);
        textFieldA.setColumns(5);

        JLabel functionLabel = new JLabel("Function:");
        textFieldFunction = new JTextField();
        panelData.add(functionLabel);
        panelData.add(textFieldFunction);
        textFieldFunction.setColumns(10);

        JLabel startLabel = new JLabel("Start");
        textFieldStart = new JTextField();
        panelData.add(startLabel);
        panelData.add(textFieldStart);
        textFieldStart.setColumns(5);
        JLabel stopLabel = new JLabel("Stop");
        textFieldStop = new JTextField();
        panelData.add(stopLabel);
        panelData.add(textFieldStop);
        textFieldStop.setColumns(5);

        JLabel stepLabel = new JLabel("Step");
        textFieldStep = new JTextField();
        panelData.add(stepLabel);
        panelData.add(textFieldStep);
        textFieldStep.setColumns(5);
        XYSeries series = new XYSeries("Function");
        XYSeriesCollection xyDataset = new XYSeriesCollection(series);
        String userFunction = "";
        JFreeChart chart = ChartFactory.createXYLineChart(userFunction, "x", "y",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        chartPanel = new ChartPanel(chart);
        contentPane.add(chartPanel, BorderLayout.CENTER);
    }


    private JFreeChart createChart() {
        XYSeries series = new XYSeries("Function");
        XYSeries series2 = new XYSeries("Derivative");
        userFunction = textFieldFunction.getText();
        start = Double.parseDouble(textFieldStart.getText());
        stop = Double.parseDouble(textFieldStop.getText());
        step = Double.parseDouble(textFieldStep.getText());
        a = Double.parseDouble(textFieldA.getText());
        double y;
        double dirY;

        for (double x = start; x < stop; x += step) {
            y = f(a, x, userFunction);
            dirY = fDir(a, x, userFunction);
            series.add(x, y);
            series2.add(x, dirY);
            PointTreeMap.put(x, new Point(y, dirY));
        }

        dataset = new XYSeriesCollection(series);
        dataset.addSeries(series2);
        chart = ChartFactory.createXYLineChart("y = " + userFunction, "X", "Y", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        return chart;
    }

    private double fDir(double a, double x, String userFunction) {
        Expression expression = new ExpressionBuilder(userFunction)
                .variable("a")
                .variable("x")
                .build();
        expression.setVariable("a", a);
        expression.setVariable("x", x);
        return (expression.setVariable("x", x + 0.0001).evaluate()
                - expression.setVariable("x", x - 0.0001).evaluate()) / 0.0002;
    }


    public double f(double a, double x, String function) {
        Expression expression = new ExpressionBuilder(function)
                .variable("a")
                .variable("x")
                .build();
        expression.setVariable("a", a);
        expression.setVariable("x", x);
        return expression.evaluate();
    }


    public void writeInFile() {
        File selectedFile = new File("Table.csv");
        try (FileWriter fileWriter = new FileWriter(selectedFile);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            for (Map.Entry<Double, Point> item : PointTreeMap.entrySet()) {
                String[] row = new String[]{Double.toString(item.getKey()), Double.toString(item.getValue().getY()),
                        Double.toString(item.getValue().getyDir())};
                csvWriter.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile() {
        File file = new File ("Table.csv");
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] nextRecord;
            csvReader.readNext();
            PointTreeMap.clear();
            while ((nextRecord = csvReader.readNext()) != null) {
                double x = Double.parseDouble(nextRecord[0]);
                double y = Double.parseDouble(nextRecord[1]);
                double yDir = Double.parseDouble(nextRecord[2]);
                PointTreeMap.put(x, new Point(y, yDir));
            }
            createChartFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private void createChartFromFile() {
        XYSeries series = new XYSeries("Function");
        XYSeries seriesDef = new XYSeries("Derivative");
        for(Map.Entry<Double, Point> item : PointTreeMap.entrySet()){
            series.add((double) item.getKey(), item.getValue().getY());
            seriesDef.add((double)item.getKey(), item.getValue().getyDir());
        }
        dataset = new XYSeriesCollection(series);
        dataset.addSeries(seriesDef);
        chart = ChartFactory.createXYLineChart(userFunction, "x", "y",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        chartPanel.setChart(chart);
    }
}