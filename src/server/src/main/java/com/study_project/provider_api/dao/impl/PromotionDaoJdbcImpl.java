package com.study_project.provider_api.dao.impl;

import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.model.Promotion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PromotionDaoJdbcImpl implements PromotionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PromotionDaoJdbcImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Promotion promotion) {
        String sql = "INSERT INTO promotions (title, discount_percentage, end_date, description) " +
                "VALUES (:title, :discount, :endDate, :desc)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", promotion.getTitle())
                .addValue("discount", promotion.getDiscountPercentage())
                .addValue("endDate", promotion.getEndDate())
                .addValue("desc", promotion.getDescription());

        // KeyHolder для безопасного и потокобезопасного получения LAST_INSERT_ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void linkToTariff(Long tariffId, Long promotionId) {
        String sql = "INSERT INTO tariff_promotions (tariff_id, promotion_id) VALUES (:tariffId, :promoId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tariffId", tariffId)
                .addValue("promoId", promotionId);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<ActivePromotionDto> findAllActiveWithTariffName() {
        String sql = "SELECT p.id, p.title, p.discount_percentage, p.end_date, p.description, t.name AS tariff_name " +
                "FROM promotions p " +
                "JOIN tariff_promotions tp ON p.id = tp.promotion_id " +
                "JOIN tariffs t ON tp.tariff_id = t.id " +
                "WHERE p.end_date >= NOW()";

        return jdbcTemplate.query(sql, new org.springframework.jdbc.core.namedparam.MapSqlParameterSource(),
                (rs, rowNum) -> {
                    ActivePromotionDto dto = new ActivePromotionDto();
                    dto.setId(rs.getLong("id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setDiscountPercentage(rs.getInt("discount_percentage"));
                    dto.setEndDate(rs.getDate("end_date").toLocalDate());
                    dto.setDescription(rs.getString("description"));
                    dto.setTariffName(rs.getString("tariff_name"));
                    return dto;
                });
    }

    @Override
    public List<com.study_project.provider_api.model.Promotion> findAllForAdmin() {
        String sql = "SELECT * FROM promotions ORDER BY id DESC";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> {
            com.study_project.provider_api.model.Promotion p = new com.study_project.provider_api.model.Promotion();
            p.setId(rs.getLong("id"));
            p.setTitle(rs.getString("title"));
            p.setDiscountPercentage(rs.getInt("discount_percentage"));
            p.setEndDate(rs.getDate("end_date").toLocalDate());
            p.setDescription(rs.getString("description"));
            return p;
        });
    }

    @Override
    public void update(com.study_project.provider_api.model.Promotion promotion) {
        String sql = "UPDATE promotions SET title = :title, discount_percentage = :discount, " +
                "end_date = :endDate, description = :desc WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", promotion.getTitle())
                .addValue("discount", promotion.getDiscountPercentage())
                .addValue("endDate", promotion.getEndDate())
                .addValue("desc", promotion.getDescription())
                .addValue("id", promotion.getId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM promotions WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public Optional<com.study_project.provider_api.dto.ActivePromotionDto> findActivePromotionById(Long promotionId) {
        String sql = "SELECT p.id, p.title, p.discount_percentage, p.end_date, p.description, " +
                "t.id AS tariff_id, t.price AS tariff_price, t.name AS tariff_name " +
                "FROM promotions p " +
                "JOIN tariff_promotions tp ON p.id = tp.promotion_id " +
                "JOIN tariffs t ON tp.tariff_id = t.id " +
                "WHERE p.id = :promoId AND p.end_date >= NOW()";

        org.springframework.jdbc.core.namedparam.MapSqlParameterSource params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource(
                "promoId", promotionId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            com.study_project.provider_api.dto.ActivePromotionDto dto = new com.study_project.provider_api.dto.ActivePromotionDto();
            dto.setId(rs.getLong("id"));
            dto.setTitle(rs.getString("title"));
            dto.setDiscountPercentage(rs.getInt("discount_percentage"));
            dto.setEndDate(rs.getDate("end_date").toLocalDate());
            dto.setDescription(rs.getString("description"));
            dto.setTariffName(rs.getString("tariff_name"));

            dto.setTariffId(rs.getLong("tariff_id"));
            dto.setOriginalPrice(rs.getBigDecimal("tariff_price"));
            return dto;
        }).stream().findFirst();
    }

}
