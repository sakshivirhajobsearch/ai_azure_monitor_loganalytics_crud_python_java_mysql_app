package com.ai.azure.monitor.loganalytics.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.ai.azure.monitor.loganalytics.repository.AzureRepository;

public class UnifiedGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTable logTable;
	private JTable anomalyTable;
	private DefaultTableModel logModel;
	private DefaultTableModel anomalyModel;

	private JTextField timestampField;
	private JTextField cpuField;
	private JTextField memoryField;
	private JTextField updateIdField;
	private JTextField deleteIdField;

	public UnifiedGUI() {
		setTitle("AI + Azure Monitor & Log Analytics CRUD");
		setSize(1100, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// === Logs Table ===
		logModel = new DefaultTableModel(new String[] { "ID", "Timestamp", "CPU Usage", "Memory Usage" }, 0);
		logTable = new JTable(logModel);
		JScrollPane scrollPane1 = new JScrollPane(logTable);
		scrollPane1.setBorder(BorderFactory.createTitledBorder("Azure Monitor Logs"));
		add(scrollPane1, BorderLayout.CENTER);

		// === AI Anomalies Table ===
		anomalyModel = new DefaultTableModel(new String[] { "ID", "CPU Usage", "Memory Usage" }, 0);
		anomalyTable = new JTable(anomalyModel);
		JScrollPane scrollPane2 = new JScrollPane(anomalyTable);
		scrollPane2.setBorder(BorderFactory.createTitledBorder("AI-Detected Anomalies"));
		scrollPane2.setPreferredSize(new Dimension(350, 0));
		add(scrollPane2, BorderLayout.EAST);

		// === Input Panel ===
		JPanel inputPanel = new JPanel(new GridLayout(7, 4, 5, 5));
		inputPanel.setBorder(BorderFactory.createTitledBorder("CRUD Controls"));

		timestampField = new JTextField();
		cpuField = new JTextField();
		memoryField = new JTextField();
		updateIdField = new JTextField();
		deleteIdField = new JTextField();

		inputPanel.add(new JLabel("Timestamp (YYYY-MM-DD HH:MM:SS):"));
		inputPanel.add(timestampField);
		inputPanel.add(new JLabel("CPU Usage (%):"));
		inputPanel.add(cpuField);
		inputPanel.add(new JLabel("Memory Usage (%):"));
		inputPanel.add(memoryField);
		inputPanel.add(new JLabel("Update Log ID:"));
		inputPanel.add(updateIdField);
		inputPanel.add(new JLabel("Delete Log ID:"));
		inputPanel.add(deleteIdField);

		JButton fetchBtn = new JButton("🔄 Fetch Logs");
		JButton insertBtn = new JButton("➕ Insert Log");
		JButton updateBtn = new JButton("✏️ Update Log");
		JButton deleteBtn = new JButton("🗑️ Delete Log");
		JButton anomalyBtn = new JButton("🤖 Detect Anomalies (AI)");
		JButton exportBtn = new JButton("📤 Export Anomalies to CSV");

		fetchBtn.addActionListener(e -> loadLogs());
		insertBtn.addActionListener(e -> insertLog());
		updateBtn.addActionListener(e -> updateLog());
		deleteBtn.addActionListener(e -> deleteLog());
		anomalyBtn.addActionListener(e -> loadAnomalies());
		exportBtn.addActionListener(e -> exportAnomalies());

		inputPanel.add(fetchBtn);
		inputPanel.add(insertBtn);
		inputPanel.add(updateBtn);
		inputPanel.add(deleteBtn);
		inputPanel.add(anomalyBtn);
		inputPanel.add(exportBtn);

		add(inputPanel, BorderLayout.SOUTH);
	}

	private void loadLogs() {
		logModel.setRowCount(0);
		ArrayList<String[]> logs = AzureRepository.fetchLogs();
		for (String[] log : logs) {
			logModel.addRow(log);
		}
	}

	private void insertLog() {
		String timestamp = timestampField.getText().trim();
		double cpu, memory;
		try {
			cpu = Double.parseDouble(cpuField.getText().trim());
			memory = Double.parseDouble(memoryField.getText().trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "❌ Invalid CPU or Memory input.");
			return;
		}

		boolean success = AzureRepository.insertLog(timestamp, cpu, memory);
		if (success) {
			JOptionPane.showMessageDialog(this, "✅ Log inserted successfully.");
			loadLogs();
		} else {
			JOptionPane.showMessageDialog(this, "❌ Failed to insert log.");
		}
	}

	private void updateLog() {
		int id;
		double cpu, memory;
		try {
			id = Integer.parseInt(updateIdField.getText().trim());
			cpu = Double.parseDouble(cpuField.getText().trim());
			memory = Double.parseDouble(memoryField.getText().trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "❌ Invalid input for update.");
			return;
		}

		boolean success = AzureRepository.updateLog(id, cpu, memory);
		if (success) {
			JOptionPane.showMessageDialog(this, "✅ Log updated successfully.");
			loadLogs();
		} else {
			JOptionPane.showMessageDialog(this, "❌ Failed to update log.");
		}
	}

	private void deleteLog() {
		int id;
		try {
			id = Integer.parseInt(deleteIdField.getText().trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "❌ Invalid ID for deletion.");
			return;
		}

		boolean success = AzureRepository.deleteLog(id);
		if (success) {
			JOptionPane.showMessageDialog(this, "✅ Log deleted successfully.");
			loadLogs();
		} else {
			JOptionPane.showMessageDialog(this, "❌ Failed to delete log.");
		}
	}

	private void loadAnomalies() {
		anomalyModel.setRowCount(0);
		ArrayList<String[]> anomalies = AzureRepository.fetchAnomalies();

		if (anomalies.isEmpty()) {
			JOptionPane.showMessageDialog(this, "✅ No anomalies detected by AI.");
			return;
		}

		for (String[] anomaly : anomalies) {
			anomalyModel.addRow(anomaly);
		}

		JOptionPane.showMessageDialog(this, "⚠️ AI detected " + anomalies.size() + " anomalies.");
	}

	private void exportAnomalies() {
		ArrayList<String[]> anomalies = AzureRepository.fetchAnomalies();

		if (anomalies.isEmpty()) {
			JOptionPane.showMessageDialog(this, "❌ No anomalies to export.");
			return;
		}

		try (PrintWriter writer = new PrintWriter("anomalies.csv")) {
			writer.println("ID,CPU Usage,Memory Usage");
			for (String[] row : anomalies) {
				writer.println(String.join(",", row));
			}
			JOptionPane.showMessageDialog(this, "📁 Anomalies exported to anomalies.csv");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "❌ Failed to export anomalies.");
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new UnifiedGUI().setVisible(true));
	}
}
