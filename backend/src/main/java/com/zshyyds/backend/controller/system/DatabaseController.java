package com.zshyyds.backend.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 数据库管理控制器
 * 提供数据库表、列、索引等信息的查询和管理功能
 */
@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private DataSource dataSource;

    /**
     * 测试数据库连接
     */
    @GetMapping("/connection/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            response.put("success", true);
            response.put("message", "数据库连接成功");
            response.put("databaseName", metaData.getDatabaseProductName());
            response.put("databaseVersion", metaData.getDatabaseProductVersion());
            response.put("driverName", metaData.getDriverName());
            response.put("url", metaData.getURL().replaceAll("password=[^&]*", "password=***"));
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "数据库连接失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取所有表列表
     */
    @GetMapping("/tables")
    public ResponseEntity<List<Map<String, Object>>> getTables() {
        List<Map<String, Object>> tables = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 获取表信息
            String query = "SELECT TABLE_NAME, TABLE_ROWS, ENGINE, TABLE_COLLATION, " +
                          "DATA_LENGTH, INDEX_LENGTH, CREATE_TIME, TABLE_COMMENT " +
                          "FROM INFORMATION_SCHEMA.TABLES " +
                          "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'";
            
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> table = new HashMap<>();
                    table.put("name", rs.getString("TABLE_NAME"));
                    table.put("rows", rs.getLong("TABLE_ROWS"));
                    table.put("engine", rs.getString("ENGINE"));
                    table.put("collation", rs.getString("TABLE_COLLATION"));
                    table.put("dataLength", rs.getLong("DATA_LENGTH"));
                    table.put("indexLength", rs.getLong("INDEX_LENGTH"));
                    table.put("createTime", rs.getTimestamp("CREATE_TIME"));
                    table.put("comment", rs.getString("TABLE_COMMENT"));
                    tables.add(table);
                }
            }
            
            return ResponseEntity.ok(tables);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取表的详细信息
     */
    @GetMapping("/tables/{tableName}/info")
    public ResponseEntity<Map<String, Object>> getTableInfo(@PathVariable String tableName) {
        Map<String, Object> tableInfo = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT TABLE_ROWS, ENGINE, TABLE_COLLATION, " +
                          "DATA_LENGTH, INDEX_LENGTH, AVG_ROW_LENGTH, CREATE_TIME, TABLE_COMMENT " +
                          "FROM INFORMATION_SCHEMA.TABLES " +
                          "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        tableInfo.put("rows", rs.getLong("TABLE_ROWS"));
                        tableInfo.put("engine", rs.getString("ENGINE"));
                        tableInfo.put("collation", rs.getString("TABLE_COLLATION"));
                        tableInfo.put("dataLength", rs.getLong("DATA_LENGTH"));
                        tableInfo.put("indexLength", rs.getLong("INDEX_LENGTH"));
                        tableInfo.put("avgRowLength", rs.getLong("AVG_ROW_LENGTH"));
                        tableInfo.put("createTime", rs.getTimestamp("CREATE_TIME"));
                        tableInfo.put("comment", rs.getString("TABLE_COMMENT"));
                    }
                }
            }
            
            return ResponseEntity.ok(tableInfo);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取表的列信息
     */
    @GetMapping("/tables/{tableName}/columns")
    public ResponseEntity<List<Map<String, Object>>> getTableColumns(@PathVariable String tableName) {
        List<Map<String, Object>> columns = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, IS_NULLABLE, " +
                          "COLUMN_KEY, COLUMN_DEFAULT, EXTRA, COLUMN_COMMENT " +
                          "FROM INFORMATION_SCHEMA.COLUMNS " +
                          "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                          "ORDER BY ORDINAL_POSITION";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> column = new HashMap<>();
                        column.put("name", rs.getString("COLUMN_NAME"));
                        column.put("dataType", rs.getString("DATA_TYPE"));
                        column.put("type", rs.getString("COLUMN_TYPE"));
                        column.put("nullable", "YES".equals(rs.getString("IS_NULLABLE")));
                        column.put("key", rs.getString("COLUMN_KEY"));
                        column.put("default", rs.getString("COLUMN_DEFAULT"));
                        column.put("extra", rs.getString("EXTRA"));
                        column.put("comment", rs.getString("COLUMN_COMMENT"));
                        columns.add(column);
                    }
                }
            }
            
            return ResponseEntity.ok(columns);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取表数据
     */
    @GetMapping("/tables/{tableName}/data")
    public ResponseEntity<Map<String, Object>> getTableData(
            @PathVariable String tableName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String sortColumn,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 构建查询SQL
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM `" + tableName + "`");
            List<Object> params = new ArrayList<>();
            
            // 添加搜索条件
            if (search != null && !search.trim().isEmpty()) {
                // 获取所有字符串类型的列
                List<String> stringColumns = getStringColumns(connection, tableName);
                if (!stringColumns.isEmpty()) {
                    queryBuilder.append(" WHERE (");
                    for (int i = 0; i < stringColumns.size(); i++) {
                        if (i > 0) queryBuilder.append(" OR ");
                        queryBuilder.append("`").append(stringColumns.get(i)).append("` LIKE ?");
                        params.add("%" + search + "%");
                    }
                    queryBuilder.append(")");
                }
            }
            
            // 添加筛选条件
            if (filterColumn != null && !filterColumn.trim().isEmpty() && 
                filterValue != null && !filterValue.trim().isEmpty()) {
                String whereClause = search != null && !search.trim().isEmpty() ? " AND " : " WHERE ";
                queryBuilder.append(whereClause).append("`").append(filterColumn).append("` LIKE ?");
                params.add("%" + filterValue + "%");
            }
            
            // 添加排序
            if (sortColumn != null && !sortColumn.trim().isEmpty()) {
                queryBuilder.append(" ORDER BY `").append(sortColumn).append("` ").append(sortDirection);
            }
            
            // 获取总记录数
            String countQuery = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ") AS count_table";
            long totalRecords = 0;
            try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
                for (int i = 0; i < params.size(); i++) {
                    countStmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        totalRecords = countRs.getLong(1);
                    }
                }
            }
            
            // 添加分页
            int offset = (page - 1) * pageSize;
            queryBuilder.append(" LIMIT ?, ?");
            params.add(offset);
            params.add(pageSize);
            
            // 执行查询
            try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        data.add(row);
                    }
                }
            }
            
            result.put("data", data);
            result.put("total", totalRecords);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 获取表的索引信息
     */
    @GetMapping("/tables/{tableName}/indexes")
    public ResponseEntity<List<Map<String, Object>>> getTableIndexes(@PathVariable String tableName) {
        List<Map<String, Object>> indexes = new ArrayList<>();
        Map<String, Map<String, Object>> indexMap = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT INDEX_NAME, NON_UNIQUE, COLUMN_NAME, INDEX_TYPE, CARDINALITY " +
                          "FROM INFORMATION_SCHEMA.STATISTICS " +
                          "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                          "ORDER BY INDEX_NAME, SEQ_IN_INDEX";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String indexName = rs.getString("INDEX_NAME");
                        String columnName = rs.getString("COLUMN_NAME");
                        int nonUnique = rs.getInt("NON_UNIQUE");
                        String indexType = rs.getString("INDEX_TYPE");
                        long cardinality = rs.getLong("CARDINALITY");
                        
                        Map<String, Object> index = indexMap.computeIfAbsent(indexName, k -> {
                            Map<String, Object> newIndex = new HashMap<>();
                            newIndex.put("keyName", indexName);
                            newIndex.put("nonUnique", nonUnique == 1);
                            newIndex.put("indexType", indexType);
                            newIndex.put("cardinality", cardinality);
                            newIndex.put("columns", new ArrayList<String>());
                            return newIndex;
                        });
                        
                        @SuppressWarnings("unchecked")
                        List<String> columns = (List<String>) index.get("columns");
                        columns.add(columnName);
                    }
                }
            }
            
            indexes.addAll(indexMap.values());
            return ResponseEntity.ok(indexes);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取表的外键信息
     */
    @GetMapping("/tables/{tableName}/foreign-keys")
    public ResponseEntity<List<Map<String, Object>>> getTableForeignKeys(@PathVariable String tableName) {
        List<Map<String, Object>> foreignKeys = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT CONSTRAINT_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, " +
                          "REFERENCED_COLUMN_NAME, DELETE_RULE, UPDATE_RULE " +
                          "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu " +
                          "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc " +
                          "ON kcu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME " +
                          "WHERE kcu.TABLE_SCHEMA = DATABASE() AND kcu.TABLE_NAME = ? " +
                          "AND kcu.REFERENCED_TABLE_NAME IS NOT NULL";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> fk = new HashMap<>();
                        fk.put("name", rs.getString("CONSTRAINT_NAME"));
                        fk.put("columnName", rs.getString("COLUMN_NAME"));
                        fk.put("referencedTableName", rs.getString("REFERENCED_TABLE_NAME"));
                        fk.put("referencedColumnName", rs.getString("REFERENCED_COLUMN_NAME"));
                        fk.put("deleteRule", rs.getString("DELETE_RULE"));
                        fk.put("updateRule", rs.getString("UPDATE_RULE"));
                        foreignKeys.add(fk);
                    }
                }
            }
            
            return ResponseEntity.ok(foreignKeys);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取表的DDL语句
     */
    @GetMapping("/tables/{tableName}/ddl")
    public ResponseEntity<Map<String, Object>> getTableDDL(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            String query = "SHOW CREATE TABLE `" + tableName + "`";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    result.put("ddl", rs.getString(2));
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 执行SQL查询
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        Map<String, Object> result = new HashMap<>();
        
        if (sql == null || sql.trim().isEmpty()) {
            result.put("success", false);
            result.put("error", "SQL语句不能为空");
            return ResponseEntity.badRequest().body(result);
        }
        
        try (Connection connection = dataSource.getConnection()) {
            // 检查SQL类型
            String trimmedSql = sql.trim().toUpperCase();
            boolean isSelectQuery = trimmedSql.startsWith("SELECT") || 
                                   trimmedSql.startsWith("SHOW") || 
                                   trimmedSql.startsWith("DESCRIBE") || 
                                   trimmedSql.startsWith("EXPLAIN");
            
            if (isSelectQuery) {
                // 查询语句
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    List<Map<String, Object>> data = new ArrayList<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        data.add(row);
                    }
                    
                    result.put("success", true);
                    result.put("data", data);
                    result.put("type", "SELECT");
                }
            } else {
                // 更新语句
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    int affectedRows = stmt.executeUpdate();
                    
                    result.put("success", true);
                    result.put("affectedRows", affectedRows);
                    result.put("message", "查询执行成功，影响 " + affectedRows + " 行");
                    result.put("type", "UPDATE");
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取字符串类型的列名
     */
    private List<String> getStringColumns(Connection connection, String tableName) throws SQLException {
        List<String> stringColumns = new ArrayList<>();
        
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                      "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                      "AND DATA_TYPE IN ('varchar', 'char', 'text', 'longtext', 'mediumtext', 'tinytext')";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stringColumns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        
        return stringColumns;
    }
}