import React from 'react'
import { Card } from '../Card'
import { theme } from '../../styles/theme'
import { PaymentRecord } from '../../hooks/useDashboard'

interface PaymentsCardProps {
    paymentsHistory: PaymentRecord[];
    title: string;
    noPaymentsText: string;
    locale: string;
}

export const PaymentsCard: React.FC<PaymentsCardProps> = ({ paymentsHistory, title, noPaymentsText, locale }) => (
    <Card title={title}>
        <div style={{ maxHeight: '200px', overflowY: 'auto', fontSize: '15px' }}>
            {paymentsHistory.length === 0 ? <p style={{ color: theme.colors.textMuted, margin: 0 }}>{noPaymentsText}</p> : (
                <ul style={{ paddingLeft: '20px', margin: 0, color: '#334155' }}>
                    {paymentsHistory.map((p, idx) => {
                        // является ли транзакция пополнением или списанием
                        const isDeposit = p.amount >= 0;

                        return (
                            <li key={idx} style={{ marginBottom: '10px' }}>
                                <span style={{
                                    color: isDeposit ? theme.colors.success : theme.colors.danger,
                                    fontWeight: 600
                                }}>
                                    {isDeposit ? `+${p.amount.toFixed(2)}` : `${p.amount.toFixed(2)}`} руб.
                                </span>
                                <small style={{ color: theme.colors.textMuted, marginLeft: '12px' }}>
                                    ({new Date(p.payment_date).toLocaleDateString(locale)})
                                </small>
                            </li>
                        );
                    })}
                </ul>
            )}
        </div>
    </Card>
)
