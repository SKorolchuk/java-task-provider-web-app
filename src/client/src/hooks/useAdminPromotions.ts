import { useState, useEffect, startTransition } from 'react'
import { apiFetch } from '../utils/api'

export interface AdminPromotion {
    id: number;
    title: string;
    discountPercentage: number;
    endDate: string;
    description: string;
}

export interface ActiveTariff {
    id: number;
    name: string;
}

export const useAdminPromotions = (successCreateMsg: string, successUpdateMsg: string, deleteConfirmMsg: string) => {
    const [tariffs, setTariffs] = useState<ActiveTariff[]>([])
    const [promotions, setPromotions] = useState<AdminPromotion[]>([])

    const [title, setTitle] = useState('')
    const [discount, setDiscount] = useState('')
    const [endDate, setEndDate] = useState('')
    const [description, setDescription] = useState('')
    const [targetTariffId, setTargetTariffId] = useState('')

    const [editingId, setEditingId] = useState<number | null>(null)
    const [success, setSuccess] = useState('')
    const [error, setError] = useState('')

    const loadPromotionsData = async () => {
        try {
            const [tRes, pRes] = await Promise.all([
                apiFetch('/api/client/tariffs'),
                apiFetch('/api/admin/promotions')
            ])
            if (tRes.ok && pRes.ok) {
                const tData = await tRes.json()
                const pData = await pRes.json()
                startTransition(() => {
                    setTariffs(tData)
                    setPromotions(pData)
                })
            }
        } catch (err) {
            console.error('Ошибка загрузки данных акций:', err)
        }
    }

    useEffect(() => {
        loadPromotionsData()
    }, [])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setSuccess('')
        setError('')

        try {
            if (editingId) {
                const response = await apiFetch(`/api/admin/promotions/${editingId}`, {
                    method: 'PUT',
                    body: JSON.stringify({ title, discountPercentage: parseInt(discount), endDate, description })
                })
                if (response.ok) {
                    setSuccess(successUpdateMsg)
                    setEditingId(null)
                    loadPromotionsData()
                    clearForm()
                }
            } else {
                if (!targetTariffId) return
                const response = await apiFetch(`/api/admin/promotions?targetTariffId=${targetTariffId}`, {
                    method: 'POST',
                    body: JSON.stringify({ title, discountPercentage: parseInt(discount), endDate, description })
                })
                if (response.ok) {
                    setSuccess(successCreateMsg)
                    loadPromotionsData()
                    clearForm()
                } else {
                    const data = await response.json()
                    setError(data.error || 'Error')
                }
            }
        } catch {
            setError('Network error')
        }
    }

    const handleDelete = async (id: number) => {
        if (!confirm(deleteConfirmMsg)) return
        const res = await apiFetch(`/api/admin/promotions/${id}`, { method: 'DELETE' })
        if (res.ok) loadPromotionsData()
    }

    const startEdit = (p: AdminPromotion) => {
        setEditingId(p.id)
        setTitle(p.title)
        setDiscount(p.discountPercentage.toString())
        setEndDate(p.endDate)
        setDescription(p.description || '')
    }

    const cancelEdit = () => {
        setEditingId(null)
        clearForm()
    }

    const clearForm = () => {
        setTitle(''); setDiscount(''); setEndDate(''); setDescription(''); setTargetTariffId('')
    }

    return {
        tariffs, promotions, title, setTitle, discount, setDiscount, endDate, setEndDate,
        description, setDescription, targetTariffId, setTargetTariffId, editingId, success, error,
        handleSubmit, handleDelete, startEdit, cancelEdit
    }
}
