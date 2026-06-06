import React from 'react'
import { Card } from '../Card'
import { theme } from '../../styles/theme'
import { TariffPlan } from '../../hooks/useDashboard';

interface TariffCardProps {
    tariffId: number | string | null;
    tariffName: string;
    speedMbps: number;
    tariffPrice: number;
    availableTariffs: TariffPlan[];
    isRu: boolean;
    handleTariffChange: (id: number) => void;
}

export const TariffCard: React.FC<TariffCardProps> = ({ tariffId, tariffName, speedMbps, tariffPrice, availableTariffs, isRu, handleTariffChange }) => {
    const hasActiveTariff = tariffId !== null && tariffId !== "Не подключен" && tariffId !== 0;

    return (
        <Card title={isRu ? 'Мой интернет-тариф' : 'My Internet Plan'}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '15px' }}>
                {hasActiveTariff ? (
                    <>
                        <p style={{ margin: 0 }}><strong>{isRu ? 'Тариф:' : 'Plan:'}</strong> {tariffName}</p>
                        <p style={{ margin: 0 }}><strong>{isRu ? 'Скорость:' : 'Speed:'}</strong> {speedMbps} Mbps</p>
                        <p style={{ margin: 0 }}><strong>{isRu ? 'Абонентская плата:' : 'Price:'}</strong> {tariffPrice.toFixed(2)} руб/мес</p>
                    </>
                ) : (
                    <p style={{ margin: 0, color: theme.colors.textMuted }}>
                        <strong>{isRu ? 'Тариф:' : 'Plan:'}</strong> {isRu ? 'Не подключен' : 'Not active'}
                    </p>
                )}
                <div style={{ marginTop: '12px' }}>
                    <select onChange={(e) => handleTariffChange(Number(e.target.value))} defaultValue="" style={{ padding: '10px', width: '100%', borderRadius: theme.radius.sm, border: `1px solid ${theme.colors.border}`, color: theme.colors.text, outline: 'none', backgroundColor: '#fff' }}>
                        <option value="" disabled>{hasActiveTariff ? (isRu ? '-- Сменить тариф --' : '-- Switch Plan --') : (isRu ? '-- Подключить тариф --' : '-- Connect Plan --')}</option>
                        {availableTariffs.map(tPlan => (
                            <option key={tPlan.id} value={tPlan.id}>{tPlan.name} ({tPlan.speedMbps} Mbps) — {tPlan.price.toFixed(2)} руб.</option>
                        ))}
                    </select>
                </div>
            </div>
        </Card>
    )
}
