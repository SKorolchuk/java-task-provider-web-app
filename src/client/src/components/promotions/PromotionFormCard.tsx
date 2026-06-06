import React, { FormEvent } from 'react'
import { Card } from '../Card'
import { Input } from '../Input'
import { Button } from '../Button'
import { theme } from '../../styles/theme'
import { ActiveTariff } from '../../hooks/useAdminPromotions'

interface PromotionFormCardProps {
    editingId: number | null;
    formTitle: string;
    title: string;
    setTitle: (val: string) => void;
    discount: string;
    setDiscount: (val: string) => void;
    endDate: string;
    setEndDate: (val: string) => void;
    description: string;
    setDescription: (val: string) => void;
    targetTariffId: string;
    setTargetTariffId: (val: string) => void;
    tariffs: ActiveTariff[];
    success: string;
    error: string;
    btnSubmitLabel: string;
    btnCancelLabel: string;
    selectTariffLabel: string;
    titleLabel: string;
    discountLabel: string;
    dateLabel: string;
    descLabel: string;
    onSubmit: (e: FormEvent) => void;
    onCancel: () => void;
}

export const PromotionFormCard: React.FC<PromotionFormCardProps> = ({
    editingId, formTitle, title, setTitle, discount, setDiscount, endDate, setEndDate,
    description, setDescription, targetTariffId, setTargetTariffId, tariffs, success, error,
    btnSubmitLabel, btnCancelLabel, selectTariffLabel, titleLabel, discountLabel, dateLabel, descLabel,
    onSubmit, onCancel
}) => (
    <Card title={formTitle}>
        <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Input label={titleLabel} value={title} onChange={e => setTitle(e.target.value)} required />
            <Input label={discountLabel} type="number" min="1" max="100" value={discount} onChange={e => setDiscount(e.target.value)} required />
            <Input label={dateLabel} type="date" value={endDate} onChange={e => setEndDate(e.target.value)} required />
            <Input label={descLabel} value={description} onChange={e => setDescription(e.target.value)} />

            {!editingId && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                    <label style={{ fontSize: '13px', fontWeight: 600, color: theme.colors.textMuted }}>{selectTariffLabel}</label>
                    <select value={targetTariffId} onChange={e => setTargetTariffId(e.target.value)} style={{ padding: '10px 14px', fontSize: '15px', borderRadius: theme.radius.sm, border: `1px solid ${theme.colors.border}`, backgroundColor: '#fff', outline: 'none', color: theme.colors.text }} required>
                        <option value="" disabled>-- Select Tariff --</option>
                        {tariffs.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                    </select>
                </div>
            )}

            {error && <p style={{ color: theme.colors.danger, margin: 0, fontSize: '14px', fontWeight: 500 }}>{error}</p>}
            {success && <p style={{ color: theme.colors.success, margin: 0, fontSize: '14px', fontWeight: 500 }}>{success}</p>}

            <Button type="submit">{btnSubmitLabel}</Button>
            {editingId && <Button variant="danger" type="button" onClick={onCancel}>{btnCancelLabel}</Button>}
        </form>
    </Card>
)
