package com.scc.companyitassets.dao;

import com.scc.companyitassets.Asset;
import com.scc.companyitassets.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScenarioDao {

    public boolean addAsset(Asset asset) throws SQLException {

        String sql = "INSERT INTO assets " +
                "(device_name, assigned_to, serial_no, \"condition\") " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, asset.getDeviceName());
            ps.setString(2, asset.getAssignedTo());
            ps.setString(3, asset.getSerialNo());
            ps.setString(4, asset.getCondition());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Asset> getAllAssets() throws SQLException {

        List<Asset> assets = new ArrayList<>();

        String sql = "SELECT id, device_name, assigned_to, " +
                "serial_no, \"condition\" FROM assets ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Asset asset = new Asset(
                        rs.getInt("id"),
                        rs.getString("device_name"),
                        rs.getString("assigned_to"),
                        rs.getString("serial_no"),
                        rs.getString("condition")
                );

                assets.add(asset);
            }
        }

        return assets;
    }
}