import React from 'react'
import { Link, useNavigate } from 'react-router'
import { useAuth } from '../context/useAuth'
import { useLang } from '../context/useLang'
import { theme } from '../styles/theme'

interface LayoutProps {
    children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
    const { user, logout } = useAuth()
    const { t, lang, toggleLang } = useLang()
    const navigate = useNavigate()

    const handleLogout = async () => {
        await logout()
        navigate('/login')
    }

    return (
        <div style={{ minHeight: '100vh', backgroundColor: theme.colors.background, fontFamily: 'system-ui, sans-serif', color: theme.colors.text }}>
            {/* Navbar */}
            <nav style={{
                height: '64px', backgroundColor: theme.colors.surface, borderBottom: `1px solid ${theme.colors.border}`,
                boxShadow: theme.shadows.sm, position: 'sticky', top: 0, zIndex: 100, display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 24px'
            }}>
                {/* Логотип */}
                <div style={{ fontWeight: 800, fontSize: '20px', color: theme.colors.primary, letterSpacing: '-0.5px' }}>
                    Provider
                </div>

                {/* Динамическое меню навигации */}
                <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                    {user && user.roles.includes('ROLE_ADMIN') && (
                        <>
                            <Link to="/admin/clients" style={navLinkStyle}>{lang === 'ru' ? 'Клиенты' : 'Clients'}</Link>
                            <Link to="/admin/tariffs" style={navLinkStyle}>{lang === 'ru' ? 'Тарифы' : 'Tariffs'}</Link>
                            <Link to="/admin/promotions" style={navLinkStyle}>{lang === 'ru' ? 'Акции' : 'Promotions'}</Link>
                        </>
                    )}
                    {user && user.roles.includes('ROLE_CLIENT') && (
                        <Link to="/dashboard" style={navLinkStyle}>{lang === 'ru' ? 'Мой Кабинет' : 'Dashboard'}</Link>
                    )}
                </div>

                {/* Правый блок: Язык + Сведения о пользователе */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                    <button onClick={toggleLang} style={{
                        background: 'none', border: `1px solid ${theme.colors.border}`, padding: '6px 12px',
                        borderRadius: theme.radius.sm, cursor: 'pointer', fontSize: '14px', fontWeight: 600, color: theme.colors.textMuted
                    }}>
                        {lang.toUpperCase()}
                    </button>

                    {user ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <span style={{ fontSize: '14px', fontWeight: 500 }}>{user.username}</span>
                            <button onClick={handleLogout} style={{
                                backgroundColor: 'transparent', color: theme.colors.danger, border: `1px solid ${theme.colors.danger}`,
                                padding: '6px 12px', borderRadius: theme.radius.sm, cursor: 'pointer', fontSize: '14px', fontWeight: 600
                            }}>
                                {t.logoutBtn}
                            </button>
                        </div>
                    ) : (
                        <Link to="/login" style={{ ...navLinkStyle, color: theme.colors.primary, fontWeight: 600 }}>{t.loginLink}</Link>
                    )}
                </div>
            </nav>

            {/* Контейнер основного контента страниц */}
            <main style={{ maxWidth: '1200px', margin: '0 auto', padding: '32px 24px' }}>
                {children}
            </main>
        </div>
    )
}

const navLinkStyle: React.CSSProperties = {
    textDecoration: 'none', color: theme.colors.textMuted, fontSize: '14px', fontWeight: 500,
    padding: '8px 12px', borderRadius: theme.radius.sm, transition: 'all 0.2s'
}
