package com.ai.azure.monitor.loganalytics.repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AzureRepository {

	private static final String API_BASE_URL = "http://127.0.0.1:5000";

	// Fetch all logs
	public static ArrayList<String[]> fetchLogs() {
		ArrayList<String[]> logs = new ArrayList<>();
		try {
			URL url = new URL(API_BASE_URL + "/logs");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();

				JSONArray jsonArray = new JSONArray(response.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					logs.add(new String[] { String.valueOf(obj.getInt("id")), obj.getString("timestamp"),
							String.valueOf(obj.getDouble("cpu_usage")),
							String.valueOf(obj.getDouble("memory_usage")) });
				}
			} else {
				System.out.println("Failed to fetch logs. HTTP Code: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logs;
	}

	// Insert a new log
	public static boolean insertLog(String timestamp, double cpu, double memory) {
		try {
			URL url = new URL(API_BASE_URL + "/logs");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			JSONObject log = new JSONObject();
			log.put("timestamp", timestamp);
			log.put("cpu_usage", cpu);
			log.put("memory_usage", memory);

			OutputStream os = conn.getOutputStream();
			os.write(log.toString().getBytes());
			os.flush();
			os.close();

			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Update a log by ID
	public static boolean updateLog(int id, double cpu, double memory) {
		try {
			URL url = new URL(API_BASE_URL + "/logs/" + id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			JSONObject log = new JSONObject();
			log.put("cpu_usage", cpu);
			log.put("memory_usage", memory);

			OutputStream os = conn.getOutputStream();
			os.write(log.toString().getBytes());
			os.flush();
			os.close();

			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Delete a log by ID
	public static boolean deleteLog(int id) {
		try {
			URL url = new URL(API_BASE_URL + "/logs/" + id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("DELETE");
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Fetch AI-detected anomalies
	public static ArrayList<String[]> fetchAnomalies() {
		ArrayList<String[]> anomalies = new ArrayList<>();
		try {
			URL url = new URL(API_BASE_URL + "/logs/anomalies");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONArray jsonArray = new JSONArray(response.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				anomalies.add(new String[] { String.valueOf(obj.getInt("id")),
						String.valueOf(obj.getDouble("cpu_usage")), String.valueOf(obj.getDouble("memory_usage")) });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return anomalies;
	}
}
