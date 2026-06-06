import React, { ButtonHTMLAttributes } from 'react'
import { theme } from '../styles/theme'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'danger' | 'success';
}

export const Button: React.FC<ButtonProps> = ({ variant = 'primary', children, ...props }) => {
    const styles = {
        primary: theme.colors.primary,
        danger: theme.colors.danger,
        success: theme.colors.success
    }

    return (
        <button
            style={{
                padding: '10px 16px', fontSize: '14px', fontWeight: 600, color: '#fff',
                border: 'none', borderRadius: theme.radius.sm, cursor: 'pointer',
                backgroundColor: styles[variant], transition: 'opacity 0.2s', width: '100%',
                opacity: props.disabled ? 0.6 : 1
            }}
            {...props}
        >
            {children}
        </button>
    )
}
