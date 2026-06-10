import React from 'react'
import { useLang } from '../context/useLang'
import { Pagination } from '../components/Pagination'
import { Layout } from '../components/Layout'
import { Card } from '../components/Card'
import { useDashboard } from '../hooks/useDashboard'
import { theme } from '../styles/theme'

import { BalanceCard } from '../components/dashboard/BalanceCard'
import { TariffCard } from '../components/dashboard/TariffCard'
import { PromotionsCard } from '../components/dashboard/PromotionsCard'
import { PaymentsCard } from '../components/dashboard/PaymentsCard'

export const Dashboard: React.FC = () => {
    const { t, lang } = useLang()
    const isRu = lang === 'ru'

    const {
        dbState, page, setPage, depositAmount, setDepositAmount, handleDeposit, handleTariffChange, handleTariffChangeByPromo
    } = useDashboard()

    if (!dbState.info) {
        return <Layout><div style={{ padding: '20px', fontWeight: 500, color: theme.colors.textMuted }}>{t.loadingText}</div></Layout>
    }

    return (
        <Layout>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
                <h2 style={{ fontSize: '28px', fontWeight: 700, margin: 0, letterSpacing: '-0.5px' }}>
                    {t.clientDashboardTitle.replace('{user}', dbState.info.username)}
                </h2>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '24px' }}>

                    {/* 1. Блок счета */}
                    <BalanceCard
                        balance={dbState.info.balance} email={dbState.info.email}
                        depositAmount={depositAmount} setDepositAmount={setDepositAmount}
                        handleDeposit={handleDeposit} balanceLabel={t.balanceLabel} depositBtn={t.depositBtn}
                    />

                    {/* 2. Блок интернет-тарифа */}
                    <TariffCard
                        tariffId={dbState.info.tariffId} tariffName={dbState.info.tariffName}
                        speedMbps={dbState.info.speedMbps} tariffPrice={dbState.info.tariffPrice}
                        availableTariffs={dbState.availableTariffs} isRu={isRu}
                        handleTariffChange={(id) => handleTariffChange(id, isRu ? 'Тариф изменен!' : 'Plan updated!')}
                    />

                    {/* 3. Блок маркетинговых акций с обработчиком клика */}
                    <PromotionsCard
                        promotions={dbState.promotions}
                        isRu={isRu}
                        onApplyPromo={(id) => handleTariffChangeByPromo(id, isRu ? 'Тариф успешно активирован по акции!' : 'Promo plan activated!')}
                    />


                    {/* 4. Блок финансовых операций */}
                    <PaymentsCard
                        paymentsHistory={dbState.paymentsHistory} title={t.clientCardPaymentsTitle}
                        noPaymentsText={t.noPayments} locale={isRu ? 'ru-RU' : 'en-US'}
                    />

                    {/* 5. Таблица истории трафика */}
                    <div style={{ gridColumn: '1 / -1' }}>
                        <Card title={t.clientCardTrafficTitle}>
                            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '15px' }}>
                                <thead>
                                    <tr style={{ borderBottom: `2px solid ${theme.colors.border}`, textAlign: 'left', color: theme.colors.textMuted }}>
                                        <th style={{ padding: '12px 16px', fontWeight: 600 }}>{t.tableThMonth}</th>
                                        <th style={{ padding: '12px 16px', fontWeight: 600 }}>{t.tableThTraffic}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dbState.trafficHistory.map((tRec, idx) => (
                                        <tr key={idx} style={{ borderBottom: `1px solid ${theme.colors.border}`, color: '#334155' }}>
                                            <td style={{ padding: '16px' }}>
                                                {new Date(tRec.billing_period).toLocaleDateString(isRu ? 'ru-RU' : 'en-US', { month: 'long', year: 'numeric' })}
                                            </td>
                                            <td style={{ padding: '16px', fontWeight: 600, color: theme.colors.text }}>
                                                {tRec.traffic_consumed_gb.toFixed(2)} GB
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                            <Pagination currentPage={page} totalPages={dbState.trafficTotalPages} onPageChange={setPage} />
                        </Card>
                    </div>

                </div>
            </div>
        </Layout>
    )
}
