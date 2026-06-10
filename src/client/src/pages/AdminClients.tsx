import React, { useEffect, useState, startTransition, useRef } from 'react'
import { useLang } from '../context/useLang'
import { Button } from '../components/Button'
import { Pagination } from '../components/Pagination'
import { Layout } from '../components/Layout'
import { Card } from '../components/Card'
import { apiFetch } from '../utils/api'
import { theme } from '../styles/theme'

interface ClientUser {
    id: number;
    username: string;
    email: string;
    balance: number;
    blocked: boolean;
}

interface ClientsState {
    data: ClientUser[];
    totalPages: number;
}

export const AdminClients: React.FC = () => {
    const { t, lang } = useLang()
    const [clientsState, setClientsState] = useState<ClientsState>({ data: [], totalPages: 0 })
    const [page, setPage] = useState<number>(0)

    const fetchClients = async (currentPage: number) => {
        try {
            const response = await apiFetch(`/api/admin/clients?page=${currentPage}&size=5`)
            if (response.ok) {
                const result = await response.json()
                startTransition(() => {
                    setClientsState({
                        data: result.clients,
                        totalPages: result.totalPages
                    })
                })
            }
        } catch (err) {
            console.error('Ошибка загрузки списка клиентов:', err)
        }
    }

    useEffect(() => {
        fetchClients(page)
    }, [page])

    const toggleBlock = async (id: number, currentBlockedStatus: boolean) => {
        try {
            const response = await apiFetch(`/api/admin/clients/${id}/block?block=${!currentBlockedStatus}`, {
                method: 'PATCH'
            })
            if (response.ok) {
                fetchClients(page)
            }
        } catch (err) {
            console.error('Ошибка изменения статуса блокировки:', err)
        }
    }

    const isPromptOpen = useRef<boolean>(false);

    const handleCharge = async (userId: number) => {
        if (isPromptOpen.current) return;

        // повторный автоматический вызов от StrictMode
        isPromptOpen.current = true;

        try {
            const traffic = prompt(
                lang === 'ru' ? 'Введите израсходованный трафик (GB):' : 'Enter consumed traffic (GB):',
                '10.5'
            );

            // Если "Отмена" на первом окне
            if (traffic === null) {
                isPromptOpen.current = false;
                return;
            }

            const cost = prompt(
                lang === 'ru' ? 'Введите сумму к списанию (руб):' : 'Enter amount to charge (rub):',
                '15.00'
            );

            // Если "Отмена" на втором окне или пустые строки
            if (cost === null || !traffic.trim() || !cost.trim()) {
                isPromptOpen.current = false;
                return;
            }

            const response = await apiFetch(`/api/admin/clients/${userId}/charge?trafficGb=${traffic}&cost=${cost}`, {
                method: 'POST'
            });

            if (response.ok) {
                alert(lang === 'ru' ? 'Расход успешно зафиксирован!' : 'Account charged successfully!');
                fetchClients(page);
            }
        } catch (err) {
            console.error('Ошибка начисления расхода трафика:', err);
        } finally {
            isPromptOpen.current = false;
        }
    };

    return (
        <Layout>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                <h2 style={{ fontSize: '24px', fontWeight: 700, margin: 0, letterSpacing: '-0.5px' }}>
                    {t.adminTitle}
                </h2>

                <Card>
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '15px' }}>
                            <thead>
                                <tr style={{ borderBottom: `2px solid ${theme.colors.border}`, textAlign: 'left', color: theme.colors.textMuted }}>
                                    <th style={thStyle}>{t.tableThId}</th>
                                    <th style={thStyle}>{t.tableThLogin}</th>
                                    <th style={thStyle}>{t.tableThEmail}</th>
                                    <th style={thStyle}>{t.tableThBalance}</th>
                                    <th style={thStyle}>{t.tableThStatus}</th>
                                    <th style={thStyle}>{t.tableThAction}</th>
                                </tr>
                            </thead>
                            <tbody>
                                {clientsState.data.map(client => (
                                    <tr key={client.id} style={{ borderBottom: `1px solid ${theme.colors.border}` }}>
                                        <td style={tdStyle}>{client.id}</td>
                                        <td style={{ ...tdStyle, fontWeight: 500 }}>{client.username}</td>
                                        <td style={tdStyle}>{client.email}</td>
                                        <td style={{ ...tdStyle, color: client.balance < 0 ? theme.colors.danger : theme.colors.success, fontWeight: 600 }}>
                                            {client.balance.toFixed(2)} руб.
                                        </td>
                                        <td style={tdStyle}>
                                            <span style={{
                                                padding: '4px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 600,
                                                backgroundColor: client.blocked ? '#ffe4e6' : '#d1fae5', color: client.blocked ? theme.colors.danger : theme.colors.success
                                            }}>
                                                {client.blocked ? t.statusBlocked : t.statusActive}
                                            </span>
                                        </td>
                                        <td style={tdStyle}>
                                            <div style={{ display: 'flex', gap: '8px', width: '280px' }}>
                                                <Button variant={client.blocked ? 'success' : 'danger'} onClick={() => toggleBlock(client.id, client.blocked)}>
                                                    {client.blocked ? t.actionUnblock : t.actionBlock}
                                                </Button>
                                                <Button variant="primary" onClick={() => handleCharge(client.id)}>
                                                    {lang === 'ru' ? 'Расход' : 'Charge'}
                                                </Button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                    <Pagination currentPage={page} totalPages={clientsState.totalPages} onPageChange={setPage} />
                </Card>
            </div>
        </Layout>
    )
}

const thStyle: React.CSSProperties = { padding: '12px 16px', fontWeight: 600 }
const tdStyle: React.CSSProperties = { padding: '16px', color: '#334155', verticalAlign: 'middle' }
