import React, { useEffect, useState, startTransition, FormEvent } from 'react'
import { Input } from '../components/Input'
import { Button } from '../components/Button'
import { Card } from '../components/Card'
import { Pagination } from '../components/Pagination'
import { Layout } from '../components/Layout'
import { useLang } from '../context/useLang'
import { apiFetch } from '../utils/api'
import { theme } from '../styles/theme'

interface Tariff { id: number; name: string; price: number; speedMbps: number; description: string; active: boolean; }
interface TariffsState { data: Tariff[]; totalPages: number; }

export const AdminTariffs: React.FC = () => {
    const { t } = useLang()
    const [tariffsState, setTariffsState] = useState<TariffsState>({ data: [], totalPages: 0 })
    const [page, setPage] = useState<number>(0)

    const [name, setName] = useState('')
    const [price, setPrice] = useState('')
    const [speed, setSpeed] = useState('')
    const [desc, setDesc] = useState('')
    const [success, setSuccess] = useState('')

    const fetchTariffs = async (currentPage: number) => {
        try {
            const res = await apiFetch(`/api/admin/tariffs?page=${currentPage}&size=4`)
            if (res.ok) {
                const result = await res.json()
                startTransition(() => { setTariffsState({ data: result.tariffs, totalPages: result.totalPages }) })
            }
        } catch (err) { console.error(err) }
    }

    useEffect(() => { fetchTariffs(page) }, [page])

    const handleCreate = async (e: FormEvent) => {
        e.preventDefault(); setSuccess('')
        try {
            const response = await apiFetch('/api/admin/tariffs', {
                method: 'POST',
                body: JSON.stringify({ name, price: parseFloat(price), speedMbps: parseInt(speed), description: desc, active: true })
            })
            if (response.ok) {
                setSuccess(t.tariffCreatedSuccess)
                fetchTariffs(page)
                setName(''); setPrice(''); setSpeed(''); setDesc('')
            }
        } catch (err) { console.error(err) }
    }

    const toggleTariff = async (tariffId: number, currentActiveStatus: boolean) => {
        try {
            const res = await apiFetch(`/api/admin/tariffs/${tariffId}/toggle?active=${!currentActiveStatus}`, {
                method: 'PATCH'
            })
            if (res.ok) {
                fetchTariffs(page)
            }
        } catch (err) { console.error(err) }
    }

    return (
        <Layout>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                <h2 style={{ fontSize: '24px', fontWeight: 700, margin: 0, letterSpacing: '-0.5px' }}>{t.adminTariffsTitle}</h2>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '30px' }}>
                    <Card title={t.tariffsCardTitle}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                            {tariffsState.data.map(tariff => (
                                <div key={tariff.id} style={{
                                    padding: '20px',
                                    borderRadius: theme.radius.md,
                                    border: `1px solid ${theme.colors.border}`,
                                    backgroundColor: theme.colors.background,
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '12px'
                                }}>
                                    {/* Верхний ряд: Название и Цена */}
                                    <h4 style={{ margin: 0, fontSize: '16px', fontWeight: 600, color: theme.colors.text }}>
                                        {tariff.name} — <span style={{ color: theme.colors.success }}>{tariff.price.toFixed(2)} руб.</span>
                                    </h4>

                                    {/* Средний ряд: Описание */}
                                    <p style={{ margin: 0, fontSize: '14px', color: theme.colors.textMuted, lineHeight: '1.5' }}>
                                        {tariff.description} ({tariff.speedMbps} Mbps)
                                    </p>

                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        marginTop: '8px',
                                        borderTop: `1px solid ${theme.colors.border}`,
                                        paddingTop: '12px'
                                    }}>
                                        <span style={{
                                            padding: '6px 10px',
                                            borderRadius: '6px',
                                            fontSize: '12px',
                                            fontWeight: 600,
                                            backgroundColor: tariff.active ? '#d1fae5' : '#ffe4e6',
                                            color: tariff.active ? theme.colors.success : theme.colors.danger,
                                            display: 'inline-block'
                                        }}>
                                            {tariff.active ? t.statusActive : t.statusBlocked}
                                        </span>
                                        <div style={{ width: '175px' }}>
                                            <Button
                                                variant={tariff.active ? 'danger' : 'success'}
                                                onClick={() => toggleTariff(tariff.id, tariff.active)}
                                            >
                                                {tariff.active ? t.btnArchive : t.btnUnarchive}
                                            </Button>
                                        </div>
                                    </div>

                                </div>
                            ))}
                        </div>
                        <Pagination currentPage={page} totalPages={tariffsState.totalPages} onPageChange={setPage} />
                    </Card>


                    <Card title={t.addTariffCardTitle}>
                        <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                            <Input label={t.tariffNameLabel} value={name} onChange={e => setName(e.target.value)} required />
                            <Input label={t.tariffPriceLabel} type="number" step="0.01" value={price} onChange={e => setPrice(e.target.value)} required />
                            <Input label={t.tariffSpeedLabel} type="number" value={speed} onChange={e => setSpeed(e.target.value)} required />
                            <Input label={t.tariffDescLabel} value={desc} onChange={e => setDesc(e.target.value)} />
                            {success && <p style={{ color: theme.colors.success, margin: 0, fontSize: '14px', fontWeight: 500 }}>{success}</p>}
                            <Button type="submit">{t.btnCreateTariff}</Button>
                        </form>
                    </Card>
                </div>
            </div>
        </Layout>
    )
}
