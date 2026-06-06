import React from 'react'
import { Card } from '../Card'
import { Button } from '../Button'
import { theme } from '../../styles/theme'
import { AdminPromotion } from '../../hooks/useAdminPromotions'

interface PromotionsListCardProps {
    promotions: AdminPromotion[];
    cardTitle: string;
    btnEditLabel: string;
    btnDeleteLabel: string;
    untilLabel: string;
    onEdit: (p: AdminPromotion) => void;
    onDelete: (id: number) => void;
}

export const PromotionsListCard: React.FC<PromotionsListCardProps> = ({
    promotions, cardTitle, btnEditLabel, btnDeleteLabel, untilLabel, onEdit, onDelete
}) => (
    <Card title={cardTitle}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {promotions.length === 0 ? (
                <p style={{ color: theme.colors.textMuted, margin: 0 }}>No promotions found</p>
            ) : promotions.map(p => (
                <div key={p.id} style={{ padding: '16px', borderRadius: theme.radius.md, border: `1px solid ${theme.colors.border}`, backgroundColor: theme.colors.background }}>
                    <h4 style={{ margin: '0 0 6px 0', fontSize: '16px', fontWeight: 600 }}>
                        {p.title} — <span style={{ color: theme.colors.danger }}>-{p.discountPercentage}%</span>
                    </h4>
                    <p style={{ margin: '0 0 12px 0', fontSize: '14px', color: theme.colors.textMuted }}>{p.description}</p>
                    <small style={{ display: 'block', marginBottom: '12px', fontWeight: 500, color: theme.colors.textMuted }}>
                        {untilLabel} {p.endDate}
                    </small>

                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginTop: '16px',
                        width: '100%'
                    }}>
                        <div style={{ width: '135px' }}>
                            <Button onClick={() => onEdit(p)}>{btnEditLabel}</Button>
                        </div>
                        <div style={{ width: '90px' }}>
                            <Button variant="danger" onClick={() => onDelete(p.id)}>{btnDeleteLabel}</Button>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    </Card>
)
