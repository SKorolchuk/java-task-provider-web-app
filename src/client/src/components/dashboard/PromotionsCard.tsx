import React from 'react'
import { Card } from '../Card'
import { Button } from '../Button'
import { theme } from '../../styles/theme'
import { PromotionDto } from '../../hooks/useDashboard'

interface PromotionsCardProps {
    promotions: PromotionDto[];
    isRu: boolean;
    onApplyPromo: (promoId: number) => void; // Добавлено прокидывание функции
}

export const PromotionsCard: React.FC<PromotionsCardProps> = ({ promotions, isRu, onApplyPromo }) => (
    <Card title={isRu ? 'Действующие акции' : 'Active Promotions'}>
        <div style={{ maxHeight: '250px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {promotions.length === 0 ? (
                <p style={{ color: theme.colors.textMuted, margin: 0, fontSize: '15px' }}>
                    {isRu ? 'Акций пока нет' : 'No active promotions'}
                </p>
            ) : promotions.map((p) => (
                <div key={p.id} style={{ padding: '14px', borderRadius: '8px', backgroundColor: '#fffbeb', border: '1px solid #fef3c7', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <div>
                        <h5 style={{ margin: '0 0 4px 0', color: '#b45309', fontSize: '14px', fontWeight: 600 }}>
                            {p.title} (-{p.discountPercentage}%)
                        </h5>
                        <p style={{ margin: '0 0 4px 0', fontSize: '13px', color: '#78350f', lineHeight: '1.4' }}>{p.description}</p>
                        <small style={{ color: theme.colors.textMuted }}>
                            {isRu ? 'Для тарифа:' : 'For tariff:'} <strong>{p.tariffName}</strong>
                        </small>
                    </div>

                    <div style={{ width: '100%', marginTop: '4px' }}>
                        <Button variant="success" onClick={() => onApplyPromo(p.id)}>
                            {isRu ? 'Подключить по акции' : 'Activate Offer'}
                        </Button>
                    </div>
                </div>
            ))}
        </div>
    </Card>
)
