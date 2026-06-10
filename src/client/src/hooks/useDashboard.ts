import { useState, useEffect, startTransition } from 'react'
import { apiFetch } from '../utils/api'

export interface UserDashboardInfo {
  id: number; username: string; email: string; balance: number;
  tariffId: number | string | null; tariffName: string; tariffPrice: number; speedMbps: number;
}
export interface TrafficRecord { billing_period: string; traffic_consumed_gb: number; }
export interface PaymentRecord { amount: number; payment_date: string; }
export interface TariffPlan { id: number; name: string; price: number; speedMbps: number; description: string; active: boolean; }
export interface PromotionDto { id: number; title: string; discountPercentage: number; endDate: string; description: string; tariffName: string; }

interface DashboardState {
  info: UserDashboardInfo | null;
  trafficHistory: TrafficRecord[];
  paymentsHistory: PaymentRecord[];
  trafficTotalPages: number;
  availableTariffs: TariffPlan[];
  promotions: PromotionDto[];
}

export const useDashboard = () => {
  const [page, setPage] = useState<number>(0)
  const [depositAmount, setDepositAmount] = useState<string>('50.00')
  const [dbState, setDbState] = useState<DashboardState>({
    info: null, trafficHistory: [], paymentsHistory: [], trafficTotalPages: 0, availableTariffs: [], promotions: []
  })

  const initDashboard = async () => {
    try {
      const [dashRes, trafficRes] = await Promise.all([
        apiFetch('/api/client/dashboard'),
        apiFetch(`/api/client/traffic?page=0&size=3`)
      ])

      if (dashRes.ok && trafficRes.ok) {
        const dashData = await dashRes.json()    // Содержит info, availableTariffs, promotions
        const trafficData = await trafficRes.json() // Содержит trafficHistory, paymentsHistory, trafficTotalPages

        startTransition(() => {
          setDbState({
            info: dashData.info,
            availableTariffs: dashData.availableTariffs,
            promotions: dashData.promotions,
            trafficHistory: trafficData.trafficHistory,
            paymentsHistory: trafficData.paymentsHistory,
            trafficTotalPages: trafficData.trafficTotalPages
          })
        })
      }
    } catch (err) {
      console.error('Ошибка инициализации:', err)
    }
  }

  const fetchTrafficPage = async (targetPage: number) => {
    try {
      const res = await apiFetch(`/api/client/traffic?page=${targetPage}&size=3`)
      if (res.ok) {
        const trafficData = await res.json()
        startTransition(() => {
          setDbState(prev => ({
            ...prev,
            trafficHistory: trafficData.trafficHistory,
            paymentsHistory: trafficData.paymentsHistory,
            trafficTotalPages: trafficData.trafficTotalPages
          }))
        })
      }
    } catch (err) {
      console.error('Ошибка: ', err)
    }
  }

  useEffect(() => { initDashboard() }, [])
  useEffect(() => { if (dbState.info) fetchTrafficPage(page) }, [page])

  const handleDeposit = async () => {
    if (parseFloat(depositAmount) <= 0) return
    try {
      const response = await apiFetch(`/api/client/balance/deposit?amount=${depositAmount}`, { method: 'POST' })
      if (response.ok) {
        const profileRes = await apiFetch('/api/client/profile')
        if (profileRes.ok) {
          const freshProfile = await profileRes.json()
          setDbState(prev => ({ ...prev, info: freshProfile }))
          fetchTrafficPage(page)
        }
      }
    } catch (err) { console.error(err) }
  }

  const handleTariffChange = async (tariffId: number, successMsg: string) => {
    try {
      const response = await apiFetch(`/api/client/tariff/change?tariffId=${tariffId}`, { method: 'POST' })
      if (response.ok) {
        alert(successMsg)
        initDashboard()
      }
    } catch (err) { console.error(err) }
  }

  const handleTariffChangeByPromo = async (promotionId: number, successMsg: string) => {
    try {
      const response = await apiFetch(`/api/client/tariff/change-by-promo?promotionId=${promotionId}`, {
        method: 'POST'
      })

      if (response.ok) {
        alert(successMsg)
        initDashboard()
      } else {
        const data = await response.json()
        alert(`${data.error || 'Ошибка биллинга'}`)
      }
    } catch (err) {
      console.error(err)
    }
  }

  return {
    dbState,
    page,
    setPage,
    depositAmount,
    setDepositAmount,
    handleDeposit,
    handleTariffChange,
    handleTariffChangeByPromo
  }
}
