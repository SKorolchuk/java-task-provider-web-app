import { useContext } from 'react';
import { AuthContext, AuthContextType } from './AuthContext';

/**
 * Кастомный хук для доступа к глобальному состоянию авторизации.
 */
export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth должен использоваться строго внутри AuthProvider');
    }
    return context;
};
