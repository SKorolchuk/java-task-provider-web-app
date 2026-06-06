import React, { createContext, useState } from 'react';

export interface UserState {
    username: string;
    roles: string[];
}

export interface AuthContextType {
    user: UserState | null;
    login: (userData: UserState) => void;
    logout: () => Promise<void>;
    loading: boolean;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Провайдер контекста авторизации.
 */
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<UserState | null>(() => {
        const savedUser = sessionStorage.getItem('auth_user');
        if (savedUser) {
            try {
                return JSON.parse(savedUser) as UserState;
            } catch (e) {
                console.log(e);
                sessionStorage.removeItem('auth_user');
                return null;
            }
        }
        return null;
    });

    const [loading] = useState<boolean>(false);

    const login = (userData: UserState) => {
        setUser(userData);
        sessionStorage.setItem('auth_user', JSON.stringify(userData));
    };

    const logout = async () => {
        try {
            await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
        } finally {
            setUser(null);
            sessionStorage.removeItem('auth_user');
        }
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
