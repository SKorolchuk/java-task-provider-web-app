import React, { InputHTMLAttributes } from 'react'
import { theme } from '../styles/theme'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
    error?: string;
}

export const Input: React.FC<InputProps> = ({ label, error, ...props }) => {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', width: '100%' }}>
            <label style={{ fontSize: '13px', fontWeight: 600, color: theme.colors.textMuted }}>{label}</label>
            <input
                style={{
                    padding: '10px 14px', fontSize: '15px', borderRadius: theme.radius.sm,
                    border: error ? `1px solid ${theme.colors.danger}` : `1px solid ${theme.colors.border}`,
                    outline: 'none', transition: 'border-color 0.2s', color: theme.colors.text
                }}
                {...props}
            />
            {error && <span style={{ color: theme.colors.danger, fontSize: '12px', fontWeight: 500 }}>{error}</span>}
        </div>
    )
}
