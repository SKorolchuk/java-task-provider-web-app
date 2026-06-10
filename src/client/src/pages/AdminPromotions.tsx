import React from 'react'
import { Layout } from '../components/Layout'
import { useLang } from '../context/useLang'
import { useAdminPromotions } from '../hooks/useAdminPromotions'
import { PromotionsListCard } from '../components/promotions/PromotionsListCard'
import { PromotionFormCard } from '../components/promotions/PromotionFormCard'

export const AdminPromotions: React.FC = () => {
    const { t, lang } = useLang()
    const isRu = lang === 'ru'

    const {
        tariffs, promotions, title, setTitle, discount, setDiscount, endDate, setEndDate,
        description, setDescription, targetTariffId, setTargetTariffId, editingId, success, error,
        handleSubmit, handleDelete, startEdit, cancelEdit
    } = useAdminPromotions(
        t.promoAnnouncedSuccess,
        isRu ? 'Акция успешно обновлена!' : 'Promotion updated!',
        isRu ? 'Удалить эту акцию?' : 'Delete this promotion?'
    )

    return (
        <Layout>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                <h2 style={{ fontSize: '24px', fontWeight: 700, margin: 0, letterSpacing: '-0.5px' }}>
                    {t.adminPromotionsTitle}
                </h2>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '30px' }}>
                    {/* Левая колонка: Модуль вывода списка */}
                    <PromotionsListCard
                        promotions={promotions}
                        cardTitle={t.promoListTitle}
                        btnEditLabel={t.btnEdit}
                        btnDeleteLabel={t.btnDelete}
                        untilLabel={isRu ? 'До:' : 'Until:'}
                        onEdit={startEdit}
                        onDelete={handleDelete}
                    />

                    {/* Правая колонка: Модуль формы */}
                    <PromotionFormCard
                        editingId={editingId}
                        formTitle={editingId ? (isRu ? 'Редактировать акцию' : 'Edit Promotion') : t.addPromoCardTitle}
                        title={title} setTitle={setTitle}
                        discount={discount} setDiscount={setDiscount}
                        endDate={endDate} setEndDate={setEndDate}
                        description={description} setDescription={setDescription}
                        targetTariffId={targetTariffId} setTargetTariffId={setTargetTariffId}
                        tariffs={tariffs} success={success} error={error}
                        btnSubmitLabel={editingId ? (isRu ? 'Сохранить изменения' : 'Save Changes') : t.btnAnnouncePromo}
                        btnCancelLabel={isRu ? 'Отмена' : 'Cancel'}
                        selectTariffLabel={t.promoSelectTariffLabel}
                        titleLabel={t.promoTitleLabel}
                        discountLabel={t.promoDiscountLabel}
                        dateLabel={t.promoDateLabel}
                        descLabel={t.promoDescLabel}
                        onSubmit={handleSubmit}
                        onCancel={cancelEdit}
                    />
                </div>
            </div>
        </Layout>
    )
}
