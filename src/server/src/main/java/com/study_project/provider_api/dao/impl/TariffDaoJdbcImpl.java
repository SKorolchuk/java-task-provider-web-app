package com.study_project.provider_api.dao.impl;

import com.study_project.provider_api.dao.TariffDao;
import com.study_project.provider_api.model.Tariff;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class TariffDaoJdbcImpl implements TariffDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TariffDaoJdbcImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Tariff> findById(Long id) {
        String sql = "SELECT * FROM tariffs WHERE id = :id";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", id),
                (rs, rowNum) -> mapRowToTariff(rs)).stream().findFirst();
    }

    @Override
    public List<Tariff> findAllActive() {
        String sql = "SELECT * FROM tariffs WHERE is_active = TRUE";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> mapRowToTariff(rs));
    }

    @Override
    public List<Tariff> findAllWithPagination(int limit, int offset) {
        String sql = "SELECT * FROM tariffs ORDER BY id ASC LIMIT :limit OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("limit", limit).addValue("offset", offset);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRowToTariff(rs));
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM tariffs";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public void save(Tariff tariff) {
        String sql = "INSERT INTO tariffs (name, price, speed_mbps, description, is_active) " +
                "VALUES (:name, :price, :speed, :description, :isActive)";
        jdbcTemplate.update(sql, getParamSource(tariff));
    }

    @Override
    public void update(Tariff tariff) {
        String sql = "UPDATE tariffs SET name = :name, price = :price, speed_mbps = :speed, " +
                "description = :description, is_active = :isActive WHERE id = :id";
        jdbcTemplate.update(sql, getParamSource(tariff).addValue("id", tariff.getId()));
    }

    @Override
    public void toggleActiveStatus(Long id, boolean isActive) {
        String sql = "UPDATE tariffs SET is_active = :isActive WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("isActive", isActive).addValue("id", id));
    }

    private MapSqlParameterSource getParamSource(Tariff tariff) {
        return new MapSqlParameterSource()
                .addValue("name", tariff.getName())
                .addValue("price", tariff.getPrice())
                .addValue("speed", tariff.getSpeedMbps())
                .addValue("description", tariff.getDescription())
                .addValue("isActive", tariff.isActive());
    }

    private Tariff mapRowToTariff(java.sql.ResultSet rs) throws java.sql.SQLException {
        Tariff tariff = new Tariff();
        tariff.setId(rs.getLong("id"));
        tariff.setName(rs.getString("name"));
        tariff.setPrice(rs.getBigDecimal("price"));
        tariff.setSpeedMbps(rs.getInt("speed_mbps"));
        tariff.setDescription(rs.getString("description"));
        tariff.setActive(rs.getBoolean("is_active"));
        return tariff;
    }
}
