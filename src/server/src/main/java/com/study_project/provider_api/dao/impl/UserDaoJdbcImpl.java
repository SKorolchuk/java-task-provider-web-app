package com.study_project.provider_api.dao.impl;

import com.study_project.provider_api.dao.UserDao;
import com.study_project.provider_api.model.User;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDaoJdbcImpl implements UserDao {

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setTariffId(rs.getObject("tariff_id") != null ? rs.getLong("tariff_id") : null);
        return user;
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDaoJdbcImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), USER_ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = :username";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("username", username), USER_ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<User> findAllClientsWithPagination(int limit, int offset) {
        String sql = "SELECT u.*, t.name AS tariff_name FROM users u " +
                "LEFT JOIN tariffs t ON u.tariff_id = t.id " +
                "WHERE u.role = 'CLIENT' ORDER BY u.id ASC LIMIT :limit OFFSET :offset";

        return jdbcTemplate.query(sql, new MapSqlParameterSource().addValue("limit", limit).addValue("offset", offset),
                (rs, rowNum) -> {
                    User user = mapRowToUser(rs);
                    try {
                        user.setEmail(rs.getString("tariff_name") != null ? rs.getString("tariff_name") : "-");
                    } catch (Exception e) {
                    }
                    return user;
                });
    }

    @Override
    public int countAllClients() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'CLIENT'";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public void updateBlockedStatus(Long userId, boolean isBlocked) {
        String sql = "UPDATE users SET is_blocked = :isBlocked WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("isBlocked", isBlocked)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateBalance(Long userId, java.math.BigDecimal amount) {
        String sql = "UPDATE users SET balance = balance + :amount WHERE id = :userId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amount", amount)
                .addValue("userId", userId);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, password, email, role, balance, is_blocked, tariff_id) " +
                "VALUES (:username, :password, :email, :role, :balance, :isBlocked, :tariffId)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("password", user.getPassword()) // Сюда будет передаваться уже хэшированный пароль
                .addValue("email", user.getEmail())
                .addValue("role", user.getRole())
                .addValue("balance", user.getBalance())
                .addValue("isBlocked", user.isBlocked())
                .addValue("tariffId", user.getTariffId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updatePersonalParams(Long userId, String email, String password) {
        String sql = "UPDATE users SET email = :email, password = :password WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("password", password)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateTariff(Long userId, Long tariffId) {
        String sql = "UPDATE users SET tariff_id = :tariffId WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tariffId", tariffId)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<Map<String, Object>> findTrafficStatsByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT billing_period, traffic_consumed_gb FROM traffic_stats " +
                "WHERE user_id = :userId ORDER BY billing_period DESC LIMIT :limit OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", limit)
                .addValue("offset", offset);
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public int countTrafficStatsByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM traffic_stats WHERE user_id = :userId";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId), Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public List<Map<String, Object>> findPaymentsByUserId(Long userId) {
        String sql = "SELECT amount, payment_date FROM payments WHERE user_id = :userId ORDER BY payment_date DESC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("userId", userId));
    }

    @Override
    public void savePayment(Long userId, java.math.BigDecimal amount) {
        String sql = "INSERT INTO payments (user_id, amount) VALUES (:userId, :amount)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("amount", amount);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<com.study_project.provider_api.dto.ClientTariffDetailsDto> findClientWithTariffDetails(
            Long userId) {
        String sql = "SELECT u.id, u.username, u.email, u.balance, u.is_blocked, u.tariff_id, " +
                "t.name AS tariff_name, t.price AS tariff_price, t.speed_mbps " +
                "FROM users u LEFT JOIN tariffs t ON u.tariff_id = t.id WHERE u.id = :userId";

        org.springframework.jdbc.core.namedparam.MapSqlParameterSource params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource(
                "userId", userId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            com.study_project.provider_api.dto.ClientTariffDetailsDto dto = new com.study_project.provider_api.dto.ClientTariffDetailsDto();

            dto.setId(rs.getLong("id"));
            dto.setUsername(rs.getString("username"));
            dto.setEmail(rs.getString("email"));
            dto.setBalance(rs.getBigDecimal("balance"));
            dto.setBlocked(rs.getBoolean("is_blocked"));

            Long tId = rs.getObject("tariff_id") != null ? rs.getLong("tariff_id") : null;
            dto.setTariffId(tId);
            dto.setTariffName(rs.getString("tariff_name") != null ? rs.getString("tariff_name") : "-");
            dto.setTariffPrice(rs.getBigDecimal("tariff_price") != null ? rs.getBigDecimal("tariff_price")
                    : java.math.BigDecimal.ZERO);
            dto.setSpeedMbps(rs.getObject("speed_mbps") != null ? rs.getInt("speed_mbps") : 0);

            return dto;
        }).stream().findFirst();
    }

    @Override
    @Transactional
    public void recordTrafficAndCharge(Long userId, java.math.BigDecimal trafficGb, java.math.BigDecimal cost) {
        String updateBalanceSql = "UPDATE users SET balance = balance - :cost WHERE id = :userId";

        java.util.Map<String, Object> balanceParams = java.util.Map.of(
                "cost", cost,
                "userId", userId);
        jdbcTemplate.update(updateBalanceSql, balanceParams);

        String insertTrafficSql = "INSERT INTO traffic_stats (user_id, billing_period, traffic_consumed_gb) " +
                "VALUES (:userId, DATE_FORMAT(NOW(), '%Y-%m-01'), :traffic) " +
                "ON DUPLICATE KEY UPDATE traffic_consumed_gb = traffic_consumed_gb + :traffic";

        java.util.Map<String, Object> trafficParams = java.util.Map.of(
                "userId", userId,
                "traffic", trafficGb);
        jdbcTemplate.update(insertTrafficSql, trafficParams);

        savePayment(userId, cost.negate());
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setTariffId(rs.getObject("tariff_id") != null ? rs.getLong("tariff_id") : null);
        return user;
    }
}
