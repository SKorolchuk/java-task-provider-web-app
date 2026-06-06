import React, { useState, FormEvent } from 'react'
import { useAuth } from '../context/useAuth'
import { useLang } from '../context/useLang'
import { useNavigate, Link } from 'react-router'
import { Input } from '../components/Input'
import { Button } from '../components/Button'
import { Card } from '../components/Card'
import { apiFetch } from '../utils/api'
import { theme } from '../styles/theme'

export const Login: React.FC = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const { login } = useAuth()
    const { t, lang, toggleLang } = useLang()
    const navigate = useNavigate()

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setError('')

        if (username.trim().length < 4) {
            setError(t.feErrorUsername)
            return
        }

        try {
            const response = await apiFetch('/api/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            })
            const data = await response.json()

            if (response.ok) {
                login({ username: data.username, roles: data.roles })
                if (data.roles.includes('ROLE_ADMIN')) {
                    navigate('/admin/clients')
                } else {
                    navigate('/dashboard')
                }
            } else {
                if (data.details && typeof data.details === 'object') {
                    setError(Object.values(data.details)[0] as string)
                } else {
                    setError(data.error || 'Auth Error')
                }
            }
        } catch {
            setError(lang === 'ru' ? 'Ошибка сервера' : 'Server error')
        }
    }

    return (
        <div style={{ minHeight: '100vh', backgroundColor: theme.colors.background, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px', fontFamily: 'system-ui, sans-serif' }}>
            <div style={{ width: '100%', maxWidth: '400px', position: 'relative' }}>
                <button onClick={toggleLang} style={{
                    position: 'absolute',
                    top: '-45px',
                    right: 0,
                    background: 'none',
                    border: `1px solid ${theme.colors.border}`,
                    padding: '6px 12px',
                    borderRadius: theme.radius.sm,
                    cursor: 'pointer',
                    fontSize: '14px',
                    fontWeight: 600,
                    color: theme.colors.textMuted,
                    backgroundColor: theme.colors.surface
                }}>
                    🌐 {lang.toUpperCase()}
                </button>

                <Card title={t.loginTitle}>
                    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                        <Input label={t.usernameLabel} value={username} onChange={e => setUsername(e.target.value)} required />
                        <Input label={t.passwordLabel} type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                        {error && <p style={{ color: theme.colors.danger, fontSize: '14px', margin: 0, fontWeight: 500 }}>{error}</p>}
                        <Button type="submit">{t.loginBtn}</Button>
                    </form>
                    <p style={{ marginTop: '20px', fontSize: '14px', textAlign: 'center', color: theme.colors.textMuted, margin: '20px 0 0 0' }}>
                        {t.noAccount} <Link to="/register" style={{ color: theme.colors.primary, fontWeight: 600, textDecoration: 'none' }}>{t.registerLink}</Link>
                    </p>
                </Card>
            </div>
        </div>
    )
}
