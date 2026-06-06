import React, { createContext, useState } from 'react'

const translations = {
    ru: {
        // Auth & Navigation
        loginTitle: 'Вход в систему',
        usernameLabel: 'Имя пользователя',
        passwordLabel: 'Пароль',
        loginBtn: 'Войти',
        logoutBtn: 'Выйти',
        registerLink: 'Зарегистрироваться',
        noAccount: 'Нет аккаунта?',
        alreadyHaveAccount: 'Уже есть аккаунт?',
        loginLink: 'Войти',
        backBtn: 'Назад',
        forwardBtn: 'Вперед',
        pageIndicator: 'Страница {curr} из {total}',
        loadingText: 'Загрузка личного кабинета...',

        // Register Page
        registerTitle: 'Регистрация нового клиента',
        emailLabel: 'Email',
        registerBtn: 'Зарегистрироваться',
        regSuccess: 'Регистрация завершена. Перенаправление на страницу входа...',
        feErrorUsername: 'Имя пользователя должно быть не менее 4 символов',
        feErrorPassword: 'Пароль должен быть не менее 6 символов',
        feErrorEmail: 'Некорректный формат email',
        feServerNetworkError: 'Не удалось связаться с сервером',

        // Dashboard (Client)
        clientDashboardTitle: 'Личный кабинет клиента: {user}',
        clientCardInfoTitle: 'Состояние счета и параметров',
        clientCardPaymentsTitle: 'История финансовых операций',
        clientCardTrafficTitle: 'Потребление интернет-трафика',
        balanceLabel: 'Текущий баланс',
        depositBtn: 'Пополнить счет',
        noPayments: 'Платежей отсутствуют',
        tableThMonth: 'Учетный период (месяц)',
        tableThTraffic: 'Расходовано трафика',
        tariffLabel: 'Текущий тариф (ID)',

        // Admin Panel
        adminTitle: 'Панель администратора — Управление клиентами',
        tableThId: 'ID',
        tableThLogin: 'Логин',
        tableThEmail: 'Email',
        tableThBalance: 'Баланс',
        tableThStatus: 'Статус',
        tableThAction: 'Действие',
        statusBlocked: 'Заблокирован',
        statusActive: 'Активен',
        actionBlock: 'Заблокировать',
        actionUnblock: 'Разблокировать',

        // Admin Tariffs
        adminTariffsTitle: 'Управление тарифными планами',
        tariffsCardTitle: 'Текущие тарифы провайдера',
        addTariffCardTitle: 'Добавить новый тариф',
        tariffNameLabel: 'Название тарифа',
        tariffPriceLabel: 'Цена (руб/мес)',
        tariffSpeedLabel: 'Скорость (Мбит/с)',
        tariffDescLabel: 'Описание тарифа',
        tariffStatusLabel: 'Статус тарифа',
        btnCreateTariff: 'Создать тариф',
        tariffCreatedSuccess: 'Тарифный план успешно создан.',

        // Admin Promotions
        adminPromotionsTitle: 'Объявление акций и скидок',
        addPromoCardTitle: 'Создать новую акцию',
        promoTitleLabel: 'Название акции',
        promoDiscountLabel: 'Процент скидки (%)',
        promoDateLabel: 'Дата окончания акции',
        promoDescLabel: 'Описание условий акции',
        promoSelectTariffLabel: 'Целевой тариф для скидки',
        btnAnnouncePromo: 'Объявить акцию',
        promoAnnouncedSuccess: 'Акция успешно добавлена и привязана к тарифу.',

        btnArchive: 'В архив',
        btnUnarchive: 'Вернуть из архива',
        btnDelete: 'Удалить',
        btnEdit: 'Редактировать',
        promoListTitle: 'Список всех акций провайдера'

    },
    en: {
        // Auth & Navigation
        loginTitle: 'Sign In',
        usernameLabel: 'Username',
        passwordLabel: 'Password',
        loginBtn: 'Login',
        logoutBtn: 'Logout',
        registerLink: 'Register',
        noAccount: "Don't have an account?",
        alreadyHaveAccount: 'Already have an account?',
        loginLink: 'Sign In',
        backBtn: 'Back',
        forwardBtn: 'Next',
        pageIndicator: 'Page {curr} of {total}',
        loadingText: 'Loading account dashboard...',

        // Register Page
        registerTitle: 'New Client Registration',
        emailLabel: 'Email',
        registerBtn: 'Sign Up',
        regSuccess: 'Registration successful. Redirecting to login page...',
        feErrorUsername: 'Username must be at least 4 characters long',
        feErrorPassword: 'Password must be at least 6 characters long',
        feErrorEmail: 'Invalid email format',
        feServerNetworkError: 'Failed to connect to the backend server',

        // Dashboard (Client)
        clientDashboardTitle: 'Client Account Dashboard: {user}',
        clientCardInfoTitle: 'Account & Parameters State',
        clientCardPaymentsTitle: 'Financial Transaction History',
        clientCardTrafficTitle: 'Internet Traffic Consumption (Paginated)',
        balanceLabel: 'Current Balance',
        depositBtn: 'Deposit Balance',
        noPayments: 'No payments recorded yet',
        tableThMonth: 'Billing Period (Month)',
        tableThTraffic: 'Traffic Consumed',
        tariffLabel: 'Current Tariff (ID)',

        // Admin Panel
        adminTitle: 'Admin Panel — Client Management',
        tableThId: 'ID',
        tableThLogin: 'Login',
        tableThEmail: 'Email',
        tableThBalance: 'Balance',
        tableThStatus: 'Status',
        tableThAction: 'Action',
        statusBlocked: 'Blocked',
        statusActive: 'Active',
        actionBlock: 'Block',
        actionUnblock: 'Unblock',

        // Admin Tariffs
        adminTariffsTitle: 'Tariff Plans Management',
        tariffsCardTitle: 'Current Provider Tariffs',
        addTariffCardTitle: 'Add New Tariff Plan',
        tariffNameLabel: 'Tariff Name',
        tariffPriceLabel: 'Price (rub/month)',
        tariffSpeedLabel: 'Speed (Mbps)',
        tariffDescLabel: 'Tariff Description',
        tariffStatusLabel: 'Tariff Status',
        btnCreateTariff: 'Create Tariff',
        tariffCreatedSuccess: 'Tariff plan successfully created.',

        // Admin Promotions
        adminPromotionsTitle: 'Announce Promotions & Discounts',
        addPromoCardTitle: 'Create New Promotion',
        promoTitleLabel: 'Promotion Title',
        promoDiscountLabel: 'Discount Percentage (%)',
        promoDateLabel: 'End Date',
        promoDescLabel: 'Promotion Description',
        promoSelectTariffLabel: 'Target Tariff for Discount',
        btnAnnouncePromo: 'Announce Promotion',
        promoAnnouncedSuccess: 'Promotion successfully announced and linked to tariff.',

        btnArchive: 'Archive',
        btnUnarchive: 'Restore',
        btnDelete: 'Delete',
        btnEdit: 'Edit',
        promoListTitle: 'All Provider Promotions'
    }
}

type LangType = 'ru' | 'en';

interface LangContextType {
    lang: LangType;
    t: typeof translations.ru;
    toggleLang: () => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const LangContext = createContext<LangContextType | undefined>(undefined)

export const LangProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [lang, setLang] = useState<LangType>(() => {
        return (localStorage.getItem('app_lang') as LangType) || 'ru'
    })

    const toggleLang = () => {
        setLang(prev => {
            const next = prev === 'ru' ? 'en' : 'ru'
            localStorage.setItem('app_lang', next)
            return next
        })
    }

    return (
        <LangContext.Provider value={{ lang, t: translations[lang], toggleLang }}>
            {children}
        </LangContext.Provider>
    )
}
