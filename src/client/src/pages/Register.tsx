import React, { useState, FormEvent } from 'react'
import { useNavigate, Link } from 'react-router'
import { Input } from '../components/Input'
import { Button } from '../components/Button'
import { Card } from '../components/Card'
import { useLang } from '../context/useLang'
import { apiFetch } from '../utils/api'
import { theme } from '../styles/theme'

export const Register: React.FC = () => {
    const { t, lang, toggleLang } = useLang()
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [email, setEmail] = useState('')
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')
    const navigate = useNavigate()

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setError('')
        setSuccess('')

        if (username.trim().length < 4) { setError(t.feErrorUsername); return; }
        if (password.length < 6) { setError(t.feErrorPassword); return; }
        if (!email.includes('@')) { setError(t.feErrorEmail); return; }

        try {
            const response = await apiFetch('/api/auth/signup', {
                method: 'POST',
                body: JSON.stringify({ username, password, email })
            })
            const data = await response.json()

            if (response.ok) {
                setSuccess(t.regSuccess)
                setTimeout(() => navigate('/login'), 2000)
            } else {
                if (data.details && typeof data.details === 'object') {
                    setError(Object.values(data.details)[0] as string)
                } else {
                    setError(data.error || 'Registration error')
                }
            }
        } catch {
            setError(t.feServerNetworkError)
        }
    }

    return (
        <div style={{ minHeight: '100vh', backgroundColor: theme.colors.background, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px', fontFamily: 'system-ui, sans-serif' }}>
            <div style={{ width: '100%', maxWidth: '400px', position: 'relative' }}>
                <button onClick={toggleLang} style={{ position: 'absolute', top: '-45px', right: 0, background: 'none', border: `1px solid ${theme.colors.border}`, padding: '6px 12px', borderRadius: theme.radius.sm, cursor: 'pointer', fontSize: '14px', fontWeight: 600, color: theme.colors.textMuted, backgroundColor: theme.colors.surface }}>
                    {lang.toUpperCase()}
                </button>

                <Card title={t.registerTitle}>
                    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                        <Input label={t.usernameLabel} value={username} onChange={e => setUsername(e.target.value)} required />
                        <Input label={t.emailLabel} type="email" value={email} onChange={e => setEmail(e.target.value)} required />
                        <Input label={t.passwordLabel} type="password" value={password} onChange={e => setPassword(e.target.value)} required />

                        {error && <p style={{ color: theme.colors.danger, fontSize: '14px', margin: 0, fontWeight: 500 }}>{error}</p>}
                        {success && <p style={{ color: theme.colors.success, fontSize: '14px', margin: 0, fontWeight: 500 }}>{success}</p>}

                        <Button type="submit">{t.registerBtn}</Button>
                    </form>
                    <p style={{ marginTop: '20px', fontSize: '14px', textAlign: 'center', color: theme.colors.textMuted, margin: '20px 0 0 0' }}>
                        {t.alreadyHaveAccount} <Link to="/login" style={{ color: theme.colors.primary, fontWeight: 600, textDecoration: 'none' }}>{t.loginLink}</Link>
                    </p>
                </Card>
            </div>
        </div>
    )
}
