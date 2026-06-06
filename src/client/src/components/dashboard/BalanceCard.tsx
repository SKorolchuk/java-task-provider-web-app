import React from 'react'
import { Card } from '../Card'
import { Button } from '../Button'
import { theme } from '../../styles/theme'

interface BalanceCardProps {
    balance: number;
    email: string;
    depositAmount: string;
    balanceLabel: string;
    depositBtn: string;
    setDepositAmount: (val: string) => void;
    handleDeposit: () => void;
}

export const BalanceCard: React.FC<BalanceCardProps> = ({ balance, email, depositAmount, balanceLabel, depositBtn, setDepositAmount, handleDeposit }) => (
    <Card title={balanceLabel}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', fontSize: '15px' }}>
            <p style={{ margin: 0 }}><strong>Email:</strong> {email}</p>
            <p style={{ margin: '12px 0 0 0', fontSize: '18px' }}>
                <strong>{balanceLabel}: </strong>
                <span style={{ color: balance < 0 ? theme.colors.danger : theme.colors.success, fontWeight: 700 }}>
                    {balance.toFixed(2)} руб.
                </span>
            </p>
            <div style={{ display: 'flex', gap: '12px', marginTop: '12px', alignItems: 'center' }}>
                <input type="number" value={depositAmount} onChange={e => setDepositAmount(e.target.value)} style={{ padding: '10px 14px', width: '100px', borderRadius: theme.radius.sm, border: `1px solid ${theme.colors.border}`, fontSize: '15px', outline: 'none' }} />
                <div style={{ width: '160px' }}><Button variant="success" onClick={handleDeposit}>{depositBtn}</Button></div>
            </div>
        </div>
    </Card>
)
