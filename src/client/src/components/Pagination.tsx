import React from 'react'
import { Button } from './Button'
import { useLang } from '../context/useLang'
import { theme } from '../styles/theme'

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
}

export const Pagination: React.FC<PaginationProps> = ({ currentPage, totalPages, onPageChange }) => {
    const { t } = useLang()
    if (totalPages <= 1) return null;

    const text = t.pageIndicator
        .replace('{curr}', (currentPage + 1).toString())
        .replace('{total}', totalPages.toString())

    return (
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginTop: '24px', justifyContent: 'center' }}>
            <div style={{ width: 'auto' }}>
                <Button disabled={currentPage === 0} onClick={() => onPageChange(currentPage - 1)}>
                    {t.backBtn}
                </Button>
            </div>
            <span style={{ fontSize: '14px', fontWeight: 600, color: theme.colors.textMuted }}>
                {text}
            </span>
            <div style={{ width: 'auto' }}>
                <Button disabled={currentPage === totalPages - 1} onClick={() => onPageChange(currentPage + 1)}>
                    {t.forwardBtn}
                </Button>
            </div>
        </div>
    )
}
