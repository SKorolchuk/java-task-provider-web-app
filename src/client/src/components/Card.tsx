import React from 'react'
import { theme } from '../styles/theme'

interface CardProps {
    title?: string;
    children: React.ReactNode;
    style?: React.CSSProperties;
}

export const Card: React.FC<CardProps> = ({ title, children, style }) => {
    return (
        <div style={{
            backgroundColor: theme.colors.surface, border: `1px solid ${theme.colors.border}`,
            borderRadius: theme.radius.md, padding: '24px', boxShadow: theme.shadows.md,
            width: '100%', boxSizing: 'border-box', ...style
        }}>
            {title && <h3 style={{ margin: '0 0 20px 0', fontSize: '18px', fontWeight: 600, color: theme.colors.text, letterSpacing: '-0.3px' }}>{title}</h3>}
            {children}
        </div>
    )
}
