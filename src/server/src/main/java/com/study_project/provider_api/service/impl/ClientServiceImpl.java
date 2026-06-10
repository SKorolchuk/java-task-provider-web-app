package com.study_project.provider_api.service.impl;

import com.study_project.provider_api.dao.PromotionDao;
import com.study_project.provider_api.dao.TariffDao;
import com.study_project.provider_api.dao.UserDao;
import com.study_project.provider_api.dto.ActivePromotionDto;
import com.study_project.provider_api.dto.ClientDashboardDto;
import com.study_project.provider_api.dto.ClientTariffDetailsDto;
import com.study_project.provider_api.dto.ClientTrafficPageDto;
import com.study_project.provider_api.dto.RegisterRequest;
import com.study_project.provider_api.dto.UpdateProfileRequest;
import com.study_project.provider_api.model.Tariff;
import com.study_project.provider_api.model.User;
import com.study_project.provider_api.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ClientServiceImpl implements ClientService {

        private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

        private final UserDao userDao;
        private final TariffDao tariffDao;
        private final BCryptPasswordEncoder passwordEncoder;
        private final PromotionDao promotionDao;

        // BCrypt для безопасного хранения паролей
        public ClientServiceImpl(UserDao userDao, PromotionDao promotionDao, TariffDao tariffDao) {
                this.userDao = userDao;
                this.promotionDao = promotionDao;
                this.tariffDao = tariffDao;
                this.passwordEncoder = new BCryptPasswordEncoder();
        }

        @Override
        @Transactional
        public void registerClient(RegisterRequest request) {
                log.info("Попытка регистрации нового клиента: {}", request.getUsername());

                if (userDao.findByUsername(request.getUsername()).isPresent()) {
                        log.error("Регистрация отклонена: логин {} уже занят", request.getUsername());
                        throw new IllegalArgumentException("user.exists");
                }

                User user = new User.Builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .email(request.getEmail())
                                .build();
                user.setRole("CLIENT");
                user.setBalance(BigDecimal.ZERO);
                user.setBlocked(false);
                user.setTariffId(null); // Изначально без тарифа

                userDao.save(user);
                log.info("Клиент {} успешно зарегистрирован", request.getUsername());
        }

        @Override
        @Transactional(readOnly = true)
        public ClientDashboardDto getClientDashboard(String username) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                if (user.isBlocked()) {
                        log.warn("Заблокированный пользователь {} пытается зайти в ЛК", username);
                        throw new IllegalStateException("Ваш аккаунт заблокирован");
                }

                // профиль с деталями LEFT JOIN
                ClientTariffDetailsDto clientDetails = userDao.findClientWithTariffDetails(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Параметры профиля не найдены"));

                // данные для инициализации ЛК
                return new ClientDashboardDto(
                                clientDetails,
                                tariffDao.findAllActive(),
                                promotionDao.findAllActiveWithTariffName());
        }

        @Override
        @Transactional(readOnly = true)
        public ClientTrafficPageDto getClientTrafficPage(String username, int page, int size) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                int offset = page * size;
                int totalRecords = userDao.countTrafficStatsByUserId(user.getId());
                int totalPages = (int) Math.ceil((double) totalRecords / size);

                return new ClientTrafficPageDto(
                                userDao.findTrafficStatsByUserId(user.getId(), size, offset),
                                userDao.findPaymentsByUserId(user.getId()),
                                totalPages);
        }

        @Override
        @Transactional
        public void depositBalance(String username, BigDecimal amount) {
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Сумма пополнения должна быть больше нуля");
                }

                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                userDao.updateBalance(user.getId(), amount);
                userDao.savePayment(user.getId(), amount);
                log.info("Пользователь {} пополнил счет на {} руб.", username, amount);
        }

        @Override
        @Transactional
        public void changeTariff(String username, Long tariffId) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                Tariff newTariff = tariffDao.findById(tariffId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Выбранный тарифный план не существует"));

                if (!newTariff.isActive()) {
                        throw new IllegalArgumentException("Нельзя подключить архивный тарифный план");
                }

                if (user.getBalance().compareTo(newTariff.getPrice()) < 0) {
                        throw new IllegalArgumentException(
                                        "На вашем счете недостаточно средств для перехода на этот тариф. Стоимость: "
                                                        + newTariff.getPrice()
                                                        + " руб.");
                }

                userDao.updateBalance(user.getId(), newTariff.getPrice().negate());

                userDao.savePayment(user.getId(), newTariff.getPrice().negate());

                userDao.updateTariff(user.getId(), tariffId);

                log.info("Клиент {} успешно сменил тариф на ID {}. Списана абонентская плата: {} руб.",
                                username, tariffId, newTariff.getPrice());
        }

        @Override
        public void updateProfile(String username, UpdateProfileRequest request) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                String hashedPwd = passwordEncoder.encode(request.getPassword());
                userDao.updatePersonalParams(user.getId(), request.getEmail(), hashedPwd);
                log.info("Пользователь {} обновил личные параметры (email/пароль)", username);
        }

        @Override
        public java.util.List<ActivePromotionDto> getActivePromotions() {
                log.info("Запрос списка действующих маркетинговых акций для клиентов");
                return promotionDao.findAllActiveWithTariffName();
        }

        @Override
        public ClientTariffDetailsDto getClientProfile(String username) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                if (user.isBlocked()) {
                        throw new IllegalStateException("Ваш аккаунт заблокирован");
                }

                return userDao.findClientWithTariffDetails(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Параметры профиля не найдены"));
        }

        @Override
        @Transactional
        public void changeTariffWithPromotion(String username, Long promotionId) {
                User user = userDao.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

                ActivePromotionDto promo = promotionDao.findActivePromotionById(promotionId)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Выбранная акция не существует или срок её действия истёк"));

                BigDecimal discountAmount = promo.getOriginalPrice()
                                .multiply(BigDecimal.valueOf(promo.getDiscountPercentage()))
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                BigDecimal finalPrice = promo.getOriginalPrice().subtract(discountAmount);

                if (user.getBalance().compareTo(finalPrice) < 0) {
                        throw new IllegalArgumentException(
                                        "Недостаточно средств для подключения по акции. Цена со скидкой "
                                                        + promo.getDiscountPercentage() + "% составляет: " + finalPrice
                                                        + " руб.");
                }

                userDao.updateBalance(user.getId(), finalPrice.negate());
                userDao.savePayment(user.getId(), finalPrice.negate());

                userDao.updateTariff(user.getId(), promo.getTariffId());

                log.info("Клиент {} успешно подключил тариф по акции ID {}. Списано со скидкой: {} руб. (Старая цена: {})",
                                username, promotionId, finalPrice, promo.getOriginalPrice());
        }
}
