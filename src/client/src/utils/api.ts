/**
 * Универсальная обертка над стандартным fetch.
 * Автоматически подставляет текущий язык интерфейса, передает сессионные Cookie
 * и глобально перехватывает ошибки для сброса авторизации.
 */
export const apiFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
    const currentLang = localStorage.getItem('app_lang') || 'ru';

    const headers = {
        'Content-Type': 'application/json',
        'Accept-Language': currentLang,
        ...options.headers,
    };

    const response = await fetch(url, {
        credentials: 'include',
        ...options,
        headers,
    });

    if (response.status === 401 || response.status === 403 || response.status === 500 || response.status === 502) {
        console.warn(`Получен статус ${response.status}. Сессия недействительна. Сброс авторизации.`);
        sessionStorage.removeItem('auth_user');

        // Делаем мягкий редирект на страницу логина на уровне рантайма браузера
        // Это гарантирует сброс стейта и очистку экрана от защищенных данных
        if (!window.location.pathname.includes('/login')) {
            window.location.href = '/login';
        }
    }

    return response;
};
